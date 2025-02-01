package bog.lbpas.view3d.renderer;

import bog.lbpas.view3d.managers.WindowMan;
import bog.lbpas.view3d.utils.Transformation;
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

    public Vector3f worldToScreen(Vector3f worldPosition, WindowMan window) {
        Vector4f cameraSpacePos = new Vector4f(worldPosition, 1.0f).mul(Transformation.getViewMatrix(this));
        Vector4f clipSpacePos = new Vector4f(cameraSpacePos).mul(window.projectionMatrix);
        if (clipSpacePos.w != 0.0f)
            clipSpacePos.div(clipSpacePos.w);
        float x = ((clipSpacePos.x + 1.0f) / 2.0f) * window.width;
        float y = ((1.0f - clipSpacePos.y) / 2.0f) * window.height;
        float z = (clipSpacePos.z < 0 || clipSpacePos.z > 1) ? -1 : 0;

        return new Vector3f(x, y, z);
    }
}