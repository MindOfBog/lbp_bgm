package bog.lbpas;

import bog.lbpas.view3d.mainWindow.LoadedData;
import bog.lbpas.view3d.mainWindow.View3D;
import bog.lbpas.view3d.managers.EngineMan;
import bog.lbpas.view3d.managers.WindowMan;
import bog.lbpas.view3d.utils.Config;
import bog.lbpas.view3d.utils.Cursors;
import bog.lbpas.view3d.utils.FilePicker;
import bog.lbpas.view3d.utils.print;

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

        window = new WindowMan(Config.TITLE, 1280, 720);
        Cursors.loadCursors();
        window.init();
        view = new View3D(window);
        engine = new EngineMan();
        debug = args.length > 0 && args[0].equalsIgnoreCase("debug");

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
                    v.printStackTrace();
                    System.exit(-1);
                }
            }
        };

        secondaryThread.start();

        loaderThread = new Thread() {
            long lastMillis = 0;

            public void run() {
                while (true)
                {
                    try
                    {
                        if(System.currentTimeMillis() - lastMillis > 500)
                        {
                            view.loaderThread();
                            FilePicker.loadingThread((View3D) engine.viewLogic);
                            lastMillis = System.currentTimeMillis();
                        }
                    } catch(Exception v) {
                        v.printStackTrace();
                        System.exit(-1);
                    }
                }
            }
        };

        loaderThread.start();

        entryDigesionThread = new Thread() {

            public void run() {
                while (true)
                {
                    try
                    {
                        LoadedData.setupList();
                    } catch(Exception v) {
                        v.printStackTrace();
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
