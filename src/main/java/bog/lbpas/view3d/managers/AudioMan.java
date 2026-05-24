package bog.lbpas.view3d.managers;

import bog.lbpas.view3d.utils.print;
import cwlib.util.Audio;
import org.joml.Vector3f;
import org.lwjgl.openal.*;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.util.*;

public class AudioMan {
    public long device;
    public long context;

    private String lastKnownDefault = "placeholder";

    public void init() {
        device = ALC10.alcOpenDevice((java.nio.ByteBuffer) null);
        if(device == 0)
            device = ALC10.alcOpenDevice("placeholder");

        if (device != 0)
        {
            context = ALC10.alcCreateContext(device, (int[]) null);

            ALC10.alcMakeContextCurrent(context);
            AL.createCapabilities(ALC.createCapabilities(device));
        }
        else
            print.error("Error occurred during audio device initialization.");
    }

    public void secondaryThread()
    {
        int[] connected = {0};
        ALC10.alcGetIntegerv(device, EXTDisconnect.ALC_CONNECTED, connected);

        if (connected[0] == ALC10.ALC_FALSE)
        {
            SOFTReopenDevice.alcReopenDeviceSOFT(device, "placeholder", (int[]) null);
            lastKnownDefault = "placeholder";
        }

        String currentDefault = ALC10.alcGetString(0, ALC10.ALC_DEFAULT_DEVICE_SPECIFIER);

        if (!currentDefault.equals(lastKnownDefault)) {
            if(SOFTReopenDevice.alcReopenDeviceSOFT(device, (java.nio.ByteBuffer) null, (int[]) null))
                lastKnownDefault = currentDefault;
        }
    }

    public void cleanup() {
        ALC10.alcMakeContextCurrent(0);
        ALC10.alcDestroyContext(context);
        ALC10.alcCloseDevice(device);
    }

    public static class AudioInstance {
        private final int bufferId;
        private final Set<AudioSource> activeSources;
        public Audio.PCMAudio audioData;
        public long duration;

        public AudioInstance(Audio.PCMAudio audioData) {
            this.bufferId = AL10.alGenBuffers();
            this.activeSources = Collections.synchronizedSet(new HashSet<>());

            int format = AL10.AL_FORMAT_MONO8;

            if (audioData.channels == 1)
            {
                if (audioData.bitsPerSample == 8)  format = AL10.AL_FORMAT_MONO8;
                if (audioData.bitsPerSample == 16) format = AL10.AL_FORMAT_MONO16;
            }
            else if (audioData.channels == 2) {
                if (audioData.bitsPerSample == 8) format = AL10.AL_FORMAT_STEREO8;
                if (audioData.bitsPerSample == 16) format = AL10.AL_FORMAT_STEREO16;
            }

            ByteBuffer data = MemoryUtil.memAlloc(audioData.pcmData.length);
            data.put(audioData.pcmData).flip();

            AL10.alBufferData(bufferId, format, data, audioData.sampleRate);

            MemoryUtil.memFree(data);

            this.audioData = audioData;
            this.duration = audioData.calculateDuration();
        }

        public int getBuffer(AudioSource source)
        {
            activeSources.add(source);
            return bufferId;
        }

        public void done(AudioSource source) {
            activeSources.remove(source);
        }

        public void cleanup() {

            for (AudioSource source : activeSources)
            {
                source.stop();
                source.setAudio(null);
            }

            activeSources.clear();
            AL10.alDeleteBuffers(bufferId);
        }
    }

    public static class AudioSource {

        public final int id;
        private AudioInstance audio;
        private float gain;
        private float pitch;
        private Vector3f position;
        private boolean looping;

        public AudioSource() {
            this.id = AL10.alGenSources();
        }

        public AudioSource(AudioInstance audio, float gain, float pitch, Vector3f position, boolean looping) {
            this.id = AL10.alGenSources();

            this.setAudio(audio);
            this.setGain(gain);
            this.setPitch(pitch);
            this.setPosition(position);
            this.setLooping(looping);
        }

        public void start()
        {
            AL10.alSourceStop(id);
            AL10.alSourcePlay(id);
        }

        public void play() {
            AL10.alSourcePlay(id);
        }

        public void pause() {
            AL10.alSourcePause(id);
        }

        public void stop() {
            AL10.alSourceStop(id);
            this.audio.done(this);
        }

        public boolean isPlaying() {
            return AL10.alGetSourcei(id, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
        }
        public boolean isPaused() {
            return AL10.alGetSourcei(id, AL10.AL_SOURCE_STATE) == AL10.AL_PAUSED;
        }

        public void setAudio(AudioInstance audio)
        {
            this.audio = audio;
            AL10.alSourcei(id, AL10.AL_BUFFER, audio == null ? 0 : audio.getBuffer(this));
        }

        public AudioInstance getAudio() {
            return audio;
        }

        public void setGain(float gain) {
            this.gain = gain;
            AL10.alSourcef(id, AL10.AL_GAIN, gain);
        }

        public float getGain() {
            return gain;
        }

        public void setPitch(float pitch) {
            this.pitch = pitch;
            AL10.alSourcef(id, AL10.AL_PITCH, pitch);
        }

        public float getPitch() {
            return pitch;
        }

        public void setPosition(float x, float y, float z) {
            this.position = new Vector3f(x, y, z);
            AL10.alSource3f(id, AL10.AL_POSITION, x, y, z);
        }

        public void setPosition(Vector3f position) {
            this.position = new Vector3f(position);
            AL10.alSource3f(id, AL10.AL_POSITION, this.position.x, this.position.y, this.position.z);
        }

        public Vector3f getPosition() {
            return position;
        }

        public void setLooping(boolean looping)
        {
            this.looping = looping;
            AL10.alSourcei(id, AL10.AL_LOOPING, looping ? AL10.AL_TRUE : AL10.AL_FALSE);
        }

        public boolean isLooping() {
            return looping;
        }

        public float getPlaybackSeconds() {
            return AL10.alGetSourcef(id, AL11.AL_SEC_OFFSET);
        }

        public void setPlaybackSeconds(float seconds) {
            AL10.alSourcef(id, AL11.AL_SEC_OFFSET, seconds);
        }

        public void cleanup() {
            stop();
            AL10.alDeleteSources(id);
        }
    }
}
