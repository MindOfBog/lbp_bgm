package cwlib.resources;

import bog.lbpas.view3d.utils.print;
import cwlib.enums.ResourceType;
import cwlib.enums.SerializationType;
import cwlib.io.Resource;
import cwlib.io.serializer.SerializationData;
import cwlib.io.serializer.Serializer;
import cwlib.types.data.Revision;
import cwlib.util.Audio;

public class RVoip implements Resource {

    public static final int BASE_ALLOCATION_SIZE = 0x10;

    public byte[] rawSpeexData;

    public RVoip(){}

    public RVoip(byte[] rawSpeexData) {
        this.rawSpeexData = rawSpeexData;
    }

    public RVoip(Audio.PCMAudio audio)
    {
        this(audio, 8, 10, false);
    }

    public RVoip(Audio.PCMAudio audio, int quality, int complexity, boolean vad)
    {
        this.rawSpeexData = Audio.Speex.encode(audio, quality, complexity, vad);
    }

    @Override
    public void serialize(Serializer serializer) {
        rawSpeexData = serializer.bytearray(rawSpeexData);
    }

    @Override
    public int getAllocatedSize() {
        int size = BASE_ALLOCATION_SIZE;
        if (this.rawSpeexData != null)
            size += this.rawSpeexData.length;
        return size;
    }

    @Override
    public SerializationData build(Revision revision, byte compressionFlags) {
        Serializer serializer = new Serializer(this.getAllocatedSize(), revision,
                compressionFlags);
        serializer.struct(this, RVoip.class);
        return new SerializationData(
                serializer.getBuffer(),
                revision,
                compressionFlags,
                ResourceType.VOIP_RECORDING,
                SerializationType.BINARY,
                serializer.getDependencies()
        );
    }

    private transient Audio.PCMAudio decodedPCM;

    public Audio.PCMAudio getDecodedPCM()
    {
        if(decodedPCM == null)
            decode();

        return decodedPCM;
    }

    public void decode()
    {
        try
        {
            decodedPCM = Audio.Speex.decode(this.rawSpeexData);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void replaceAudio(Audio.PCMAudio audio)
    {
        this.replaceAudio(audio, 8, 10, false);
    }

    public void replaceAudio(Audio.PCMAudio audio, int quality, int complexity, boolean vad)
    {
        this.rawSpeexData = Audio.Speex.encode(audio, quality, complexity, vad);
        decode();
    }
}