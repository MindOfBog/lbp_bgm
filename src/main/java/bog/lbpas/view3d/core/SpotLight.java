package bog.lbpas.view3d.core;

import org.joml.Vector3f;

/**
 * @author Bog
 */
public class SpotLight {

    public PointLight pointLight;
    public Vector3f coneDirection;
    public float cutoff;

    public SpotLight(PointLight pointLight, Vector3f coneDirection, float cutoff) {
        this.pointLight = pointLight;
        this.coneDirection = coneDirection;
        this.cutoff = cutoff;
    }

    public SpotLight(SpotLight spotLight)
    {
        this.pointLight = spotLight.pointLight;
        this.coneDirection = spotLight.coneDirection;
        this.cutoff = spotLight.cutoff;
    }
}
