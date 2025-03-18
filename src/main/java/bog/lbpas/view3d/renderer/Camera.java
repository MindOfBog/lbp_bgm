package bog.lbpas.view3d.renderer;

import bog.lbpas.view3d.managers.WindowMan;
import bog.lbpas.view3d.utils.Config;
import bog.lbpas.view3d.utils.Transformation;
import org.joml.Math;
import org.joml.*;

/**
 * @author Bog
 */
public class Camera {

    private Vector3f pos, rotation;

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
            pos.x += (float) Math.sin(Math.toRadians(getRotation().y)) * -1f * z;
            pos.z += (float) Math.cos(Math.toRadians(getRotation().y)) * z;
        }
        if(x != 0)
        {
            pos.x += (float) Math.sin(Math.toRadians(getRotation().y - 90)) * -1f * x;
            pos.z += (float) Math.cos(Math.toRadians(getRotation().y - 90)) * x;
        }
        pos.y += y;
    }

    public void setPos(float x, float y, float z)
    {
        pos.x = x;
        pos.y = y;
        pos.z = z;
    }

    public void setPos(Vector3f pos)
    {
        this.pos = pos;
    }

    public void setRot(float x, float y, float z)
    {
        rotation.x = x;
        rotation.y = y;
        rotation.z = z;
    }

    public void setRot(Vector3f rotation)
    {
        this.rotation = rotation;
    }
    public void setRotX(float x)
    {
        rotation.x = x;
    }
    public void setRotY(float y)
    {
        rotation.y = y;
    }
    public void setRotZ(float z)
    {
        rotation.z = z;
    }
    public void setPosX(float x)
    {
        pos.x = x;
    }
    public void setPosY(float y)
    {
        pos.y = y;
    }
    public void setPosZ(float z)
    {
        pos.z = z;
    }

    public void moveRot(float x, float y, float z)
    {
        rotation.x += x;
        rotation.y += y;
        rotation.z += z;
    }

    public Vector3f getWrappedRotation()
    {
        float x =  getRotation().x % 360f;

        x = (x + 360f) % 360f;

        if (x > 180f)
            x -= 360f;

        float y =  getRotation().y % 360f;

        y = (y + 360f) % 360f;

        if (y > 180f)
            y -= 360f;

        float z =  getRotation().z % 360f;

        z = (z + 360f) % 360f;

        if (z > 180f)
            z -= 360f;

        return new Vector3f(x, y, z);
    }

    public Vector3f worldToScreen(Vector3f worldPosition, WindowMan window) {
        Vector4f clipSpacePos;
        Vector4f cameraSpacePos = new Vector4f(worldPosition, 1.0f).mul(Transformation.getViewMatrix(this));
        clipSpacePos = new Vector4f(cameraSpacePos).mul(window.projectionMatrix);

        if (!Config.FRONT_VIEW && clipSpacePos.w != 0.0f) {
            clipSpacePos.div(clipSpacePos.w);
        }

        float x = ((clipSpacePos.x + 1.0f) / 2.0f) * window.width;
        float y = ((1.0f - clipSpacePos.y) / 2.0f) * window.height;
        float z = Config.FRONT_VIEW || (clipSpacePos.z >= 0 && clipSpacePos.z <= 1) ? 0 : -1;

        return new Vector3f(x, y, z);
    }

    public Vector3f getPos() {
        return pos;
    }

    public Vector3f getRotation() {
        return Config.FRONT_VIEW ? new Vector3f(0f) : rotation;
    }
}