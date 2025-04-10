package bog.lbpas;

import bog.lbpas.view3d.mainWindow.LoadedData;
import bog.lbpas.view3d.mainWindow.View3D;
import bog.lbpas.view3d.managers.EngineMan;
import bog.lbpas.view3d.managers.WindowMan;
import bog.lbpas.view3d.utils.*;
import cwlib.enums.Part;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryUtil;

/**
 * @author Bog
 */

public class Main {

    public static WindowMan window;
    public static View3D view;
    public static EngineMan engine;
    public static boolean debug;
    public static Thread secondaryThread;
    public static Thread loaderThread;
    public static Thread entryDigesionThread;

    public static void main(String args[]) {

        Config.init();
        window = new WindowMan(Consts.TITLE, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT, 1050,650);
        Cursors.loadCursors();
        window.init();
        if(Config.WINDOW_MAXIMIZED)
            window.maximize();

        view = new View3D(window);
        engine = new EngineMan();
        debug = args.length > 0 && args[0].equalsIgnoreCase("debug");

        if(debug)
        {
            print.neutral("GL_VENDOR [" + GL11.glGetString(GL11.GL_VENDOR) + "]");
            print.neutral("GL_VERSION [" + GL11.glGetString(GL11.GL_VERSION) + "]");

            int[] minorVer = new int[1];
            GL30.glGetIntegerv(GL30.GL_MINOR_VERSION, minorVer);
            int[] majorVer = new int[1];
            GL30.glGetIntegerv(GL30.GL_MAJOR_VERSION, majorVer);

            print.neutral("GL_MINOR_VERSION [" + minorVer[0] + "]");
            print.neutral("GL_MAJOR_VERSION [" + majorVer[0] + "]");
        }

        secondaryThread = new Thread() {
            long lastMillis = 0;
            long lastMillis2 = 0;
            public void run() {
                try
                {
                    while (true)
                    {
                        if(System.currentTimeMillis() - lastMillis > 100)
                        {
                            view.secondaryThread();
                            lastMillis = System.currentTimeMillis();
                        }
                        if(System.currentTimeMillis() - lastMillis2 > 7500)
                        {
                            if(Config.hasConfigChanged())
                                Config.updateFile();
                            lastMillis2 = System.currentTimeMillis();
                        }
                    }
                } catch(Exception v) {
                    print.stackTrace(v);
                    System.exit(-1);
                }
            }
        };

        secondaryThread.start();

        loaderThread = new Thread() {
            long lastMillis = 0;
            public void run() {

                GLFW.glfwMakeContextCurrent(window.sharedContext);
                GL.createCapabilities();
                Utils.checkOpenGLErrors("binding shared context");

                if(Main.debug)
                    GLUtil.setupDebugMessageCallback(System.out);

                while (true)
                {
                    try
                    {
                        view.loaderThread();
                        if(System.currentTimeMillis() - lastMillis > 50)
                        {
                            FilePicker.loadingThread((View3D) engine.viewLogic);
                            lastMillis = System.currentTimeMillis();
                        }
                    } catch(Exception v) {
                        print.stackTrace(v);
                        System.exit(-1);
                    }
                }
            }
        };

        loaderThread.start();

        entryDigesionThread = new Thread() {

            long lastMillis = 0;

            public void run() {
                while (true)
                {
                    try
                    {
                        if(System.currentTimeMillis() - lastMillis > 500)
                        {
                            LoadedData.setupList();
                            lastMillis = System.currentTimeMillis();
                        }
                    } catch(Exception v) {
                        print.stackTrace(v);
                        System.exit(-1);
                    }
                }
            }
        };

        entryDigesionThread.start();

        try
        {
            engine.start(window, view);
        }
        catch (Exception e)
        {
            print.stackTrace(e);
            System.exit(-1);
        }
    }
}
