package bog.bgmaker.view3d.utils;

import bog.bgmaker.view3d.Camera;
import bog.bgmaker.view3d.core.types.Entity;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * @author Bog
 */
public class Transformation {

    public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale) {
        Matrix4f matrix = new Matrix4f();
        matrix.identity().translate(new Vector3f(translation.x, translation.y, 0f))
                .scale(new Vector3f(scale.x, scale.y, 1f));
        return matrix;
    }

    public static Matrix4f createTransformationMatrix(Entity entity)
    {
        Matrix4f matrix = new Matrix4f(entity.transformation);
        return matrix;
    }

    public static Matrix4f createTransformationMatrix(Vector3f pos)
    {
        Matrix4f matrix = new Matrix4f();
        matrix.identity().translate(pos)
                .rotateX((float) 0)
                .rotateY((float) 0)
                .rotateZ((float) 0)
                .scale(1);
        return matrix;
    }

    public static Matrix4f getViewMatrix(Camera camera)
    {
        Vector3f pos = new Vector3f(camera.pos);
        Vector3f rot = new Vector3f(camera.rotation);
        Matrix4f matrix = new Matrix4f();
        matrix.identity()
                .rotate((float)Math.toRadians(rot.x), new Vector3f(1, 0, 0))
                .rotate((float)Math.toRadians(rot.y), new Vector3f(0, 1, 0))
                .rotate((float)Math.toRadians(rot.z), new Vector3f(0, 0, 1))
                .translate(-pos.x, -pos.y, -pos.z);
        return matrix;
    }

    public static boolean isMatrixParseable(Matrix4f matrix) {
        if (!matrix.isAffine()) {
            return false;
        }

        float determinant = matrix.m00() * (matrix.m11() * matrix.m22() - matrix.m12() * matrix.m21())
                - matrix.m01() * (matrix.m10() * matrix.m22() - matrix.m12() * matrix.m20())
                + matrix.m02() * (matrix.m10() * matrix.m21() - matrix.m11() * matrix.m20());

        return Math.abs(determinant - 1.0f) < 1e-6;
    }
}
