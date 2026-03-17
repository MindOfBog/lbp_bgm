package cwlib.util;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.joml.Math;
import org.xiph.speex.Bits;
import org.xiph.speex.NbDecoder;
import org.xiph.speex.NbEncoder;

import javax.sound.sampled.AudioFormat;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Audio {

    public static class PCMAudio {
        public final byte[] pcmData;
        public final int sampleRate;
        public final int channels;
        public final int bitsPerSample;

        public PCMAudio(byte[] pcmData, int sampleRate, int channels, int bitsPerSample) {
            this.pcmData = pcmData;
            this.sampleRate = sampleRate;
            this.channels = channels;
            this.bitsPerSample = bitsPerSample;
        }

        public long calculateDuration() {
            if (this.sampleRate > 0)
                return ((long) this.pcmData.length * 1000L / ((long) this.sampleRate * (long) this.channels * ((long) this.bitsPerSample / 8L)));

            return 0;
        }

        public static PCMAudio loadAndConvertForSpeex(File inputFile) throws Exception {

            AudioFormat targetFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    Speex.SAMPLE_RATE,
                    Speex.BITS_PER_SAMPLE,
                    Speex.CHANNEL_COUNT,
                    2,     //16-bit Mono = 2 bytes
                    Speex.SAMPLE_RATE,
                    false  //little endian
            );

            try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFile)) {
                grabber.setSampleRate((int) targetFormat.getSampleRate());
                grabber.setAudioChannels(targetFormat.getChannels());
                grabber.setAudioBitrate(Speex.BITS_PER_SAMPLE);

                grabber.start();

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                Frame frame;

                while ((frame = grabber.grabFrame()) != null) {
                    if (frame.samples == null) continue;

                    java.nio.ShortBuffer sb = (java.nio.ShortBuffer) frame.samples[0];
                    byte[] b = new byte[sb.limit() * 2];

                    ByteBuffer.wrap(b).order(targetFormat.isBigEndian() ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(sb);

                    out.write(b);
                }

                grabber.stop();
                grabber.release();
                grabber.close();

                return new PCMAudio(out.toByteArray(), Speex.SAMPLE_RATE, Speex.CHANNEL_COUNT, Speex.BITS_PER_SAMPLE);
            }
        }
    }

    public static class Speex
    {
        //default values for LBP vop audio
        public static final int SAMPLE_COUNT = 160;
        public static final int SAMPLE_RATE = 8000; //always 8Khz Narrowband
        public static final int CHANNEL_COUNT = 1; //always single channel
        public static final int BITS_PER_SAMPLE = 16;

        public static PCMAudio decode(byte[] speexData) {
            NbDecoder decoder = new NbDecoder();
            decoder.nbinit();

            ByteArrayOutputStream pcmBuffer = new ByteArrayOutputStream();

            int offset = 0;
            while (offset < speexData.length) {
                float[] frame = new float[SAMPLE_COUNT];

                byte flags = speexData[offset];
                int submode = flags & Flags.SUBMODE;

                int bitsPerFrame = Submode.fromIndex(submode).id();
                int bytesPerFrame = ((bitsPerFrame + 7) >> 3);

                Bits bits = new Bits();
                bits.init();

                try
                {
                    byte[] frameBytes = new byte[bytesPerFrame];
                    System.arraycopy(speexData, offset + 1, frameBytes, 0, bytesPerFrame);
                    bits.read_from(frameBytes, 0, bytesPerFrame);

                    int result = decoder.decode(bits, frame);
                    if (result != 0)
                    {
                        System.err.println("Error decoding offset: " + offset);
                        System.err.println("Result: " + result);
                        return null;
                    }
                }catch (Exception e)
                {
                    System.err.println("Error decoding offset: " + offset);
                    e.printStackTrace();
                    return null;
                }

                for (float value : frame) {
                    short pcmSample = (short) Math.clamp(-32768, 32767, value);
                    pcmBuffer.write((byte) (pcmSample & 0xFF));
                    pcmBuffer.write((byte) ((pcmSample >> 8) & 0xFF));
                }

                offset += 1;
                offset += bytesPerFrame;
            }

            if (offset != speexData.length)
            {
                System.err.println("Error, final offset not at end of array: " + offset + " != " + speexData.length);
                return null;
            }

            byte[] pcmData = pcmBuffer.toByteArray();
            return new PCMAudio(pcmData, SAMPLE_RATE, CHANNEL_COUNT, BITS_PER_SAMPLE);
        }

        public static byte[] encode(PCMAudio input, int quality, int complexity, boolean vad) {
            NbEncoder encoder = new NbEncoder();
            encoder.nbinit();
            encoder.setQuality(Math.min(quality, 8)); //anything over 8 crashes the game
            encoder.setComplexity(complexity);
            encoder.setVad(vad);

            ByteArrayOutputStream speexOutputStream = new ByteArrayOutputStream();

            float[] allSamples = new float[input.pcmData.length / 2];
            for (int i = 0; i < allSamples.length; i++)
            {
                int low = input.pcmData[i * 2] & 0xFF;
                int high = input.pcmData[i * 2 + 1] << 8;
                allSamples[i] = (float) ((short) (high | low));
            }

            for (int i = 0; i < allSamples.length; i += SAMPLE_COUNT)
            {
                float[] frame = new float[SAMPLE_COUNT];
                int lengthToCopy = Math.min(SAMPLE_COUNT, allSamples.length - i);
                System.arraycopy(allSamples, i, frame, 0, lengthToCopy);

                if (lengthToCopy < SAMPLE_COUNT)
                    for (int j = lengthToCopy; j < SAMPLE_COUNT; j++) frame[j] = 0.0f;

                Bits bits = new Bits();
                bits.init();

                encoder.encode(bits, frame);

                bits.pack(0, 1);

                int submodeIndex = encoder.getMode();
                Submode submode = Submode.fromIndex(submodeIndex);

                int bitsPerFrame = submode.id();
                int bytesPerFrame = (bitsPerFrame + 7) >> 3;

                byte flags = (byte) (submodeIndex & 0x3F);
                if (vad)
                {
                    if (submodeIndex == 7)
                        flags |= 0x80;
                } else
                    flags |= 0x80;

                speexOutputStream.write(flags);

                byte[] encodedBuffer = bits.getBuffer();
                int actualBytes = bits.getBufferSize();

                byte[] finalFrame = new byte[bytesPerFrame];
                System.arraycopy(encodedBuffer, 0, finalFrame, 0, Math.min(actualBytes, bytesPerFrame));

                try
                {
                    speexOutputStream.write(finalFrame);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            return speexOutputStream.toByteArray();
        }


        public static class Flags
        {
            /**
             * DTX (Discontinuous Transmission): boolean mask
             *
             * optimizes bandwidth when the encoder detects silence or stationary background noise
             */
            public static final int DTX = 0x40;
            /**
             * VAD (Voice Activation Detection): boolean mask
             *
             * if set, the frame contains speech
             */
            public static final int VAD = 0x80;
            /**
             * Submode: integer filter
             *
             * returns an int enum that maps to {@link Submode}
             */
            public static final int SUBMODE = 0x3F;
        }

        public enum Submode
        {
            INVALID(0, 5),
            VOCODER_LIKE(1, 43),
            EXTREME_LOW(8, 79),
            VERY_LOW(2, 119),
            LOW(3, 160),
            MEDIUM(4, 220),
            HIGH(5, 300),
            VERY_HIGH(6, 364),
            EXTREME_HIGH(7, 492);

            private final int index;
            private final int modeId;

            Submode(int index, int modeId)
            {
                this.index = index;
                this.modeId = modeId;
            }

            public int id()
            {
                return this.modeId;
            }

            public static Submode fromIndex(int index)
            {
                index = Math.clamp(1, 8, index);

                for (Submode submode : Submode.values())
                {
                    if (submode.index == index)
                        return submode;
                }
                return INVALID;
            }
        }
    }
}
