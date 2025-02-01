package bog.lbpas.view3d.core.types;

import bog.lbpas.view3d.managers.assetLoading.ObjectLoader;
import bog.lbpas.view3d.core.PointLight;
import bog.lbpas.view3d.core.SpotLight;
import org.joml.Matrix4f;

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
}
