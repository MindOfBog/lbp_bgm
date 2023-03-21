package bog.bgmaker.view3d.utils;

import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * @author Bog
 */
public class Const {

    public static String TITLE = "LBP Background Maker";
    public static float FOV = (float) Math.toRadians(63);
    public static float Z_NEAR = 10f;
    public static float Z_FAR = 500000f;
    public static float MOUSE_SENS = 0.2f;
    public static float CAMERA_MOVE_SPEED = 800f;
    public static Vector4f DEFAULT_COLOR = new Vector4f(1f, 1f, 1f, 1f);
    public static Vector3f AMBIENT_LIGHT = new Vector3f(1f, 1f, 1f);
    public static float SPECULAR_POWER = 10f;
    public static long NANOSECOND = 1000000000L;
    public static float FRAMERATE = 120;
    public static float NaNf = Float.intBitsToFloat(0x7fc00000);
    public static double NaNd = Double.longBitsToDouble(0x7ff8000000000000L);

}
