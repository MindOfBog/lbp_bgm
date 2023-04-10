package bog.bgmaker.view3d.core.types;

import bog.bgmaker.view3d.ObjectLoader;
import bog.bgmaker.view3d.core.Model;
import cwlib.types.data.GUID;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * @author Bog
 */
public class WorldAudio extends Entity{

    public String soundName;
    public float initialVolume;
    public float initialPitch;
    public boolean isLocal;
    public GUID soundNames;
    public WorldAudio(Vector3f position, String entityName, String soundName, float initialVolume, float initialPitch, boolean isLocal, GUID soundNames, ObjectLoader loader)
    {
        super(new Matrix4f().identity().translate(position), entityName, loader);
        this.soundName = soundName;
        this.initialVolume = initialVolume;
        this.initialPitch = initialPitch;
        this.isLocal = isLocal;
        this.soundNames = soundNames;
    }

    @Override
    public int getType() {
        return 3;
    }


    @Override
    public void setModel(Model model) {}

}
