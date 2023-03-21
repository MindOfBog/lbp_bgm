package bog.bgmaker.view3d.core.types;

import bog.bgmaker.view3d.ObjectLoader;
import bog.bgmaker.view3d.core.Model;
import bog.bgmaker.view3d.core.PointLight;
import bog.bgmaker.view3d.core.SpotLight;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * @author Bog
 */
public class Light extends Entity{

    public PointLight pointLight;
    public SpotLight spotLight;

    public Light(PointLight pointLight, String entityName, ObjectLoader loader)
    {
        super(new Matrix4f().identity().translate(pointLight.position), entityName, loader);
        this.pointLight = pointLight;
    }

    @Override
    public int getType() {
        return 2;
    }

    @Override
    public void updateModel() {}

    @Override
    public void setModel(Model model) {}

    @Override
    public Vector3f rayIntersectModel(Vector3f ray, Vector3f origin) {
        return new Vector3f();
    }
}
