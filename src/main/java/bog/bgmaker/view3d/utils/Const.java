package bog.bgmaker.view3d.utils;

import org.joml.Vector3f;
import org.joml.Vector4f;

import java.awt.*;

/**
 * @author Bog
 */
public class Const {

    public static String TITLE = "LBP Background Maker";
    public static float FOV = (float) Math.toRadians(63);
    public static float Z_NEAR = 10f;
    public static float Z_FAR = 500000f;
    public static boolean NO_CULLING;
    public static float MOUSE_SENS = 0.2f;
    public static float CAMERA_MOVE_SPEED = 1500f;
    public static Vector4f DEFAULT_COLOR = new Vector4f(1f, 1f, 1f, 1f);
    public static Vector3f AMBIENT_LIGHT = new Vector3f(1f, 1f, 1f);
    public static float SPECULAR_POWER = 10f;
    public static long NANOSECOND = 1000000000L;
    public static float FRAMERATE = 120;
    public static float NaNf = Float.intBitsToFloat(0x7fc00000);
    public static double NaNd = Double.longBitsToDouble(0x7ff8000000000000L);
    public static float OUTLINE_DISTANCE = 0.6f;
    public static Color OUTLINE_COLOR = new Color(252, 173, 3);
    public static Color BORDER_COLOR_1 = new Color(156, 0, 0, 166);
    public static Color BORDER_COLOR_2 = new Color(230, 0, 0, 166);
    public static Color BORDER_COLOR_3 = new Color(1, 41, 143, 166);
    public static Color BORDER_COLOR_4 = new Color(0, 62, 219, 166);
    public static Color POD_COLOR = new Color(154, 236, 93, 166);
    public static Color EARTH_COLOR = new Color(236, 93, 154, 166);
    public static Color FONT_COLOR = new Color(255, 255, 255, 255);
    public static Color PRIMARY_COLOR = new Color(0, 0, 0, 127);
    public static Color SECONDARY_COLOR = new Color(0, 0, 0, 127);
    public static Color INTERFACE_PRIMARY_COLOR = new Color(0, 0, 0, 157);
    public static Color INTERFACE_PRIMARY_COLOR2 = new Color(0, 0, 0, 157);
    public static Color INTERFACE_SECONDARY_COLOR = new Color(50, 50, 50, 200);
    public static Color INTERFACE_SECONDARY_COLOR2 = new Color(50, 50, 50, 200);
    public static Color INTERFACE_TERTIARY_COLOR = new Color(100, 100, 100, 210);
    public static Color INTERFACE_TERTIARY_COLOR2 = new Color(100, 100, 100, 210);

}
