package bog.lbpas.view3d.utils;

import bog.lbpas.view3d.renderer.Camera;
import bog.lbpas.view3d.managers.MouseInput;
import bog.lbpas.view3d.managers.WindowMan;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * @author Bog
 */
public class MousePicker {

    public Vector3f currentRay;
    public Matrix4f viewMatrix;
    private MouseInput mouse;
    private WindowMan window;
    private Camera camera;

    public MousePicker(MouseInput mouseInput, WindowMan window) {
        this.mouse = mouseInput;
        this.window = window;
    }

    public void update(Camera camera)
    {
        if (this.camera == null || this.camera != camera)
            this.camera = camera;

        viewMatrix = Transformation.getViewMatrix(camera);
        currentRay = calculateMouseRay();
    }

    public void update(Camera camera, float mouseX, float mouseY)
    {
        if (this.camera == null || this.camera != camera)
            this.camera = camera;

        viewMatrix = Transformation.getViewMatrix(camera);
        currentRay = calculateMouseRay(mouseX, mouseY);
    }

    private Vector3f calculateMouseRay()
    {
        return calculateMouseRay((float)mouse.currentPos.x, (float)mouse.currentPos.y);
    }

    private Vector3f calculateMouseRay(float mouseX, float mouseY)
    {
        Vector2f normalizedCoords = getNormalizedDeviceCoords(mouseX, mouseY);

        Vector4f clipCoords = new Vector4f(normalizedCoords.x, normalizedCoords.y, -1f, 1f);
        Vector4f eyeCoords = toEyeCoords(clipCoords);
        Vector3f worldRay = toWorldCoords(eyeCoords);

        return worldRay;
    }
    private Vector3f toWorldCoords(Vector4f eyeCoords)
    {
        Matrix4f invertedView = viewMatrix.invert();
        Vector4f rayWorld = invertedView.transform(eyeCoords);
        Vector3f mouseRay = new Vector3f(rayWorld.x, rayWorld.y, rayWorld.z);
        mouseRay.normalize();
        return mouseRay;
    }
    private Vector4f toEyeCoords(Vector4f clipCoords)
    {
        Matrix4f invertedProjection = new Matrix4f(window.projectionMatrix).invert();
        Vector4f eyeCoords = invertedProjection.transform(clipCoords);
        return new Vector4f(eyeCoords.x, eyeCoords.y, -1f, 0f);
    }
    private Vector2f getNormalizedDeviceCoords(float mouseX, float mouseY)
    {
        float zoom = Config.FRONT_VIEW ? 1 : Math.max(camera.getPos().z / 1000f, 0.0f);
        float orthoWidth = Config.FRONT_VIEW ? 1 : window.width == 0 ? zoom : (float) window.width * zoom;
        float orthoHeight = Config.FRONT_VIEW ? 1 : window.height == 0 ? zoom : (float) window.height * zoom;

        return new Vector2f(((2f * mouseX) / window.width - 1f) * (Config.FRONT_VIEW ? orthoWidth : 1), -((2f * mouseY) / window.height - 1f) * (Config.FRONT_VIEW ? orthoHeight : 1));
    }
    public Vector3f getPointOnRay(Vector3f ray, float distance) {
        Vector3f camPos = camera.getPos();
        Vector3f start = new Vector3f(camPos.x, camPos.y, camPos.z);
        Vector3f scaledRay = new Vector3f(ray.x * distance, ray.y * distance, ray.z * distance);
        return start.add(scaledRay);
    }

    public Vector3f getPointOnPlaneX(float xPos)
    {
        Vector3f intersectionPoint = new Vector3f();

        if (currentRay.x == 0)
            return null;

        float t = (xPos - camera.getPos().x) / currentRay.x;

        if (t < 0)
            return null;

        intersectionPoint.set(currentRay)
                .mul(t)
                .add(camera.getPos());

        return intersectionPoint;
    }

    public Vector3f getPointOnPlaneY(float yPos)
    {
        Vector3f intersectionPoint = new Vector3f();

        if (currentRay.y == 0)
            return null;

        float t = (yPos - camera.getPos().y) / currentRay.y;

        if (t < 0)
            return null;

        intersectionPoint.set(currentRay)
                .mul(t)
                .add(camera.getPos());

        return intersectionPoint;
    }

    public Vector3f getPointOnPlaneZ(float zPos)
    {
        if(Config.FRONT_VIEW)
        {
            float zoom = Math.max(camera.getPos().z / 1000f, 0.0f);
            Vector3f point = new Vector3f(camera.getPos()).add((float) ((mouse.currentPos.x - window.width/2) * zoom), (float) ((1 - mouse.currentPos.y + window.height/2)  * zoom),0);

            return new Vector3f(point.x, point.y, zPos);
        }
        else
        {
            Vector3f intersectionPoint = new Vector3f();

            if (currentRay.z == 0)
                return null;

            float t = (zPos - camera.getPos().z) / currentRay.z;

            if (t < 0)
                return null;

            intersectionPoint.set(currentRay)
                    .mul(t)
                    .add(camera.getPos());

            return intersectionPoint;
        }
    }

    public Vector3f getPointOnPlaneAbstract(Vector3f pos, Vector3f normal) {
        float denom = new Vector3f(currentRay).dot(normal);

        if (Math.abs(denom) > 1e-6) {
            Vector3f diff = new Vector3f(pos).sub(camera.getPos());
            float t = diff.dot(normal) / denom;

            if (t >= 0)
                return new Vector3f().set(camera.getPos()).add(new Vector3f(currentRay).mul(t));
        }

        return null;
    }

}
