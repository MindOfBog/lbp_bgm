package bog.lbpas.view3d.utils;

import bog.lbpas.view3d.renderer.Camera;
import bog.lbpas.view3d.core.types.Entity;
import org.joml.*;
import org.joml.Math;

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
        Matrix4f matrix = new Matrix4f(entity.getTransformation());
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
        Vector3f pos = new Vector3f(camera.getPos());
        Vector3f rot = new Vector3f(camera.getRotation());
        Matrix4f matrix = new Matrix4f();
        matrix.identity()
                .rotateAffineXYZ(Math.toRadians(rot.x), Math.toRadians(rot.y), Math.toRadians(rot.z))
                .translate(-pos.x, -pos.y, -(Config.FRONT_VIEW ? 20000f : pos.z));
        return matrix;
    }

    public static boolean isOrthogonal(Matrix3f matrix) {
        Matrix3f result = new Matrix3f();
        Matrix3f transpose = new Matrix3f();

        matrix.transpose(transpose);

        matrix.mul(transpose, result);

        return isIdentityMatrix(result);
    }

    public static boolean isIdentityMatrix(Matrix3f matrix) {
        return matrix.m00() == 1.0f && matrix.m11() == 1.0f && matrix.m22() == 1.0f &&
                matrix.m01() == 0.0f && matrix.m02() == 0.0f &&
                matrix.m10() == 0.0f && matrix.m12() == 0.0f &&
                matrix.m20() == 0.0f && matrix.m21() == 0.0f;
    }
}
