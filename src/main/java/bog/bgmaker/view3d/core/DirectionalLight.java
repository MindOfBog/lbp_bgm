package bog.bgmaker.view3d.core;

import org.joml.Vector3f;

/**
 * @author Bog
 */
public class DirectionalLight {

    public Vector3f color, direction;
    public float intensity;

    public DirectionalLight(Vector3f color, Vector3f direction, float intensity) {
        this.color = color;
        this.direction = direction;
        this.intensity = intensity;
    }
}
