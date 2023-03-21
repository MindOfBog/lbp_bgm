package bog.bgmaker.view3d;

import bog.bgmaker.view3d.managers.WindowMan;
import bog.bgmaker.view3d.utils.Transformation;
import org.joml.Math;
import org.joml.*;

/**
 * @author Bog
 */
public class Camera {

    public Vector3f pos, rotation;

    public Camera()
    {
        pos = new Vector3f(0, 0, 0);
        rotation = new Vector3f(0, 0, 0);
    }

    public Camera(Vector3f pos, Vector3f rotation)
    {
        this.pos = pos;
        this.rotation = rotation;
    }

    public void movePos(float x, float y, float z)
    {
        if(z != 0)
        {
            pos.x += (float) Math.sin(Math.toRadians(rotation.y)) * -1f * z;
            pos.z += (float) Math.cos(Math.toRadians(rotation.y)) * z;
        }
        if(x != 0)
        {
            pos.x += (float) Math.sin(Math.toRadians(rotation.y - 90)) * -1f * x;
            pos.z += (float) Math.cos(Math.toRadians(rotation.y - 90)) * x;
        }
        pos.y += y;
    }

    public void setPos(float x, float y, float z)
    {
        pos.x = x;
        pos.y = y;
        pos.z = z;
    }

    public void setRot(float x, float y, float z)
    {
        rotation.x = x;
        rotation.y = y;
        rotation.z = z;
    }

    public void moveRot(float x, float y, float z)
    {
        rotation.x += x;
        rotation.y += y;
        rotation.z += z;
    }

    public Vector3f getWrappedRotation()
    {
        float x =  rotation.x % 360f;

        x = (x + 360f) % 360f;

        if (x > 180f)
            x -= 360f;

        float y =  rotation.y % 360f;

        y = (y + 360f) % 360f;

        if (y > 180f)
            y -= 360f;

        float z =  rotation.z % 360f;

        z = (z + 360f) % 360f;

        if (z > 180f)
            z -= 360f;

        return new Vector3f(x, y, z);
    }

    public Vector3f worldToScreenPointF(Vector3f pos, WindowMan window)
    {
        if(pos == null)
            return null;

        Vector3f vec3 = applyMatricesF(pos, window.projectionMatrix);

        Vector3f vec2 = new Vector3f(vec3.x, vec3.y, 0f);
        vec2.x = (window.width * (vec2.x + 1.0f) / 2.0f);
        vec2.y = (window.height * (1.0f - ((vec2.y + 1.0f) / 2.0f)));

        if(vec3.z >= 1)
            vec2.sub(new Vector3f(window.width/2f, window.height/2f, 0f)).mul(-1).add((new Vector3f(window.width/2f, window.height/2f, 1f)));

        return vec2;
    }

    public Vector3d worldToScreenPointD(Vector3f pos, WindowMan window)
    {
        if(pos == null)
            return null;

        Vector3d vec3 = applyMatricesD(pos, window.projectionMatrix);

        Vector3d vec2 = new Vector3d(vec3.x, vec3.y/vec3.z, 0d);
        vec2.x = (window.width * (vec2.x + 1.0d) / 2d);
        vec2.y = (window.height * (1.0d - ((vec2.y + 1.0d) / 2d)));

        if(vec3.z >= 1)
            vec2.sub(new Vector3d(window.width/2d, window.height/2d, 0d)).mul(-1).add((new Vector3d(window.width/2d, window.height/2d, 1d)));

        return vec2;
    }

    private Vector3f applyMatricesF(Vector3f pos, Matrix4f projectionMatrix)
    {
        Vector3f vec3 = new Vector3f(pos);
        vec3.mulProject(new Matrix4f(projectionMatrix).mul(Transformation.getViewMatrix(this)));
        return vec3;
    }

    private Vector3d applyMatricesD(Vector3f pos, Matrix4f projectionMatrix)
    {
        Vector3d vec3 = new Vector3d(pos);
        vec3.mulProject(new Matrix4d(projectionMatrix).mul(new Matrix4d(Transformation.getViewMatrix(this))));
        return vec3;
    }

}
