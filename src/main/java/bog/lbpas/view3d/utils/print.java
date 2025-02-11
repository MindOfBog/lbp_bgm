package bog.lbpas.view3d.utils;

public class print {

    public static void error(Object message)
    {
        System.out.print(Consts.ANSI_RED);
        System.out.print(message);
        System.out.println(Consts.ANSI_RESET);
    }

    public static void warning(Object message)
    {
        System.out.print(Consts.ANSI_YELLOW);
        System.out.print(message);
        System.out.println(Consts.ANSI_RESET);
    }

    public static void line(Object message)
    {
        System.out.println(message);
    }

    public static void success(Object message)
    {
        System.out.print(Consts.ANSI_GREEN);
        System.out.print(message);
        System.out.println(Consts.ANSI_RESET);
    }

    public static void neutral(Object message)
    {
        System.out.print(Consts.ANSI_CYAN);
        System.out.print(message);
        System.out.println(Consts.ANSI_RESET);
    }

    public static void stackTrace(Exception e)
    {
        // Print our stack trace
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
            error += "\nat " + traceElement;
        return error;
    }
}