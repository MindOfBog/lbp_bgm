package bog.lbpas.view3d.utils;

/**
 * @author Bog
 */
public class Consts {
    public static long NANOSECOND = 1000000000L;
    public static float NaNf = Float.intBitsToFloat(0x7fc00000);
    public static double NaNd = Double.longBitsToDouble(0x7ff8000000000000L);
    public static long startMillis = 0;
    public static String ANSI_RESET = "\u001B[0m";
    public static String ANSI_BLACK = "\u001B[30m";
    public static String ANSI_RED = "\u001B[31m";
    public static String ANSI_GREEN = "\u001B[32m";
    public static String ANSI_YELLOW = "\u001B[33m";
    public static String ANSI_BLUE = "\u001B[34m";
    public static String ANSI_PURPLE = "\u001B[35m";
    public static String ANSI_CYAN = "\u001B[36m";
    public static String ANSI_WHITE = "\u001B[37m";

    public static char LEFT_ARROW_WITH_SMALL_CIRCLE = (char)0x2b30;
}
