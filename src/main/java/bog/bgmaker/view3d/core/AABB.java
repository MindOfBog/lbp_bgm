package bog.bgmaker.view3d.core;

import org.joml.Vector3f;

/**
 * @author Bog
 */
public class AABB {

    public Vector3f min = new Vector3f(0f, 0f, 0f);
    public Vector3f max = new Vector3f(0f, 0f, 0f);
    public Model model;

    public AABB(Vector3f min, Vector3f max, Model model) {
        this.min = min;
        this.max = max;
        this.model = model;
    }

    public AABB() {}

    public boolean isPointInsideBB(Vector3f point)
    {
        return (point.x < max.x && point.x > min.x &&
                point.y < max.y && point.y > min.y &&
                point.z < max.z && point.z > min.z);
    }

    public boolean doesRayIntersectBB(Vector3f ray, Vector3f origin)
    {
        Vector3f dirfrac = new Vector3f(1.0f / ray.x, 1.0f / ray.y, 1.0f / ray.z);

        float t1 = (min.x - origin.x) * dirfrac.x;
        float t2 = (max.x - origin.x) * dirfrac.x;
        float t3 = (min.y - origin.y) * dirfrac.y;
        float t4 = (max.y - origin.y) * dirfrac.y;
        float t5 = (min.z - origin.z) * dirfrac.z;
        float t6 = (max.z - origin.z) * dirfrac.z;

        float tmin = Math.max(Math.max(Math.min(t1, t2), Math.min(t3, t4)), Math.min(t5, t6));
        float tmax = Math.min(Math.min(Math.max(t1, t2), Math.max(t3, t4)), Math.max(t5, t6));

        if (tmax < 0 || tmin > tmax)
            return false;

        return true;
    }
}
