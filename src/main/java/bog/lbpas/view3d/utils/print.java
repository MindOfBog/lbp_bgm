package bog.lbpas.view3d.utils;

import org.joml.*;

import java.util.List;

public class print {

    public static void print(Object message, boolean line)
    {
        String messageString = getString(message);

        if(line)
            System.out.println(messageString);
        else
            System.out.print(messageString);
    }

    private static String getString(Object message)
    {
        if(message instanceof int[])
        {
            int[] intArray = (int[]) message;
            String build = "int[]{";
            for(int i = 0; i < intArray.length; i++)
                build += intArray[i] + (i == intArray.length - 1 ? "" : ", ");
            build += "}";
            return build;
        }
        if(message instanceof byte[])
        {
            byte[] byteArray = (byte[]) message;
            String build = "byte[]{";
            for(int i = 0; i < byteArray.length; i++)
                build += byteArray[i] + (i == byteArray.length - 1 ? "" : ", ");
            build += "}";
            return build;
        }
        else if(message instanceof float[])
        {
            float[] floatArray = (float[]) message;
            String build = "float[]{";
            for(int i = 0; i < floatArray.length; i++)
                build += floatArray[i] + (i == floatArray.length - 1 ? "f" : "f, ");
            build += "}";
            return build;
        }
        else if(message instanceof String[])
        {
            String[] stringArray = (String[]) message;
            String build = "String[]{";
            for(int i = 0; i < stringArray.length; i++)
                build += "\"" + stringArray[i] + (i == stringArray.length - 1 ? "\"" : "\", ");
            build += "}";
            return build;
        }
        else if(message instanceof Vector3f)
        {
            Vector3f vector = (Vector3f) message;
            return "Vector3f(" + vector.x + "f, " + vector.y + "f, " + vector.z + "f)";
        }
        else if(message instanceof Vector2f)
        {
            Vector2f vector = (Vector2f) message;
            return "Vector2f(" + vector.x + "f, " + vector.y + "f)";
        }
        else if(message instanceof Vector3d)
        {
            Vector3d vector = (Vector3d) message;
            return "Vector3d(" + vector.x + "d, " + vector.y + "d, " + vector.z + "d)";
        }
        else if(message instanceof Vector2d)
        {
            Vector2d vector = (Vector2d) message;
            return "Vector2d(" + vector.x + "d, " + vector.y + "d)";
        }
        else if(message instanceof Vector3i)
        {
            Vector3i vector = (Vector3i) message;
            return "Vector3i(" + vector.x + ", " + vector.y + ", " + vector.z + ")";
        }
        else if(message instanceof Vector2i)
        {
            Vector2i vector = (Vector2i) message;
            return "Vector2i(" + vector.x + ", " + vector.y + ")";
        }

        return String.valueOf(message);
    }

    public static void error(Object message)
    {
        print(Consts.ANSI_RED, false);
        print(message, false);
        print(Consts.ANSI_RESET, true);
    }

    public static void warning(Object message)
    {
        print(Consts.ANSI_YELLOW, false);
        print(message, false);
        print(Consts.ANSI_RESET, true);
    }

    public static void line(Object message)
    {
        print(message, true);
    }

    public static void success(Object message)
    {
        print(Consts.ANSI_GREEN, false);
        print(message, false);
        print(Consts.ANSI_RESET, true);
    }

    public static void neutral(Object message)
    {
        print(Consts.ANSI_CYAN,false);
        print(message, false);
        print(Consts.ANSI_RESET, true);
    }

    public static void stackTrace(Exception e)
    {
        error(e);
        StackTraceElement[] trace = e.getStackTrace();
        for (StackTraceElement traceElement : trace)
            error("\tat " + traceElement);

//        // Print suppressed exceptions, if any
//        for (Throwable se : e.getSuppressed())
//            se.printEnclosedStackTrace(s, trace, SUPPRESSED_CAPTION, "\t", dejaVu);
//
//        // Print cause, if any
//        Throwable ourCause = e.getCause();
//        if (ourCause != null)
//            ourCause.printEnclosedStackTrace(s, trace, CAUSE_CAPTION, "", dejaVu);
    }

    public static String stackTraceAsString(Exception e)
    {
        String error = e.toString();
        StackTraceElement[] trace = e.getStackTrace();
        for (StackTraceElement traceElement : trace)
            error += "\n  at " + traceElement;
        return error;
    }
}