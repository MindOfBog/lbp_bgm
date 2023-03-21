package bog.bgmaker.view3d.utils;

import bog.bgmaker.view3d.Camera;
import bog.bgmaker.view3d.managers.MouseInput;
import bog.bgmaker.view3d.managers.WindowMan;
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
        float mouseX = (float)mouse.currentPos.x;
        float mouseY = (float)mouse.currentPos.y;
        Vector2f normalizedCoords = getNormalizedDeviceCoords(mouseX, mouseY);
        Vector4f clipCoords = new Vector4f(normalizedCoords.x, normalizedCoords.y, -1f, 1f);
        Vector4f eyeCoords = toEyeCoords(clipCoords);
        Vector3f worldRay = toWorldCoords(eyeCoords);
        return worldRay;
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
        return new Vector2f((2f * mouseX) / window.width - 1f, -((2f * mouseY) / window.height - 1f));
    }
    public Vector3f getPointOnRay(Vector3f ray, float distance) {
        Vector3f camPos = camera.pos;
        Vector3f start = new Vector3f(camPos.x, camPos.y, camPos.z);
        Vector3f scaledRay = new Vector3f(ray.x * distance, ray.y * distance, ray.z * distance);
        return start.add(scaledRay);
    }

}
