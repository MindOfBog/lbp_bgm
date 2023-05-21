package bog.bgmaker;

import bog.bgmaker.view3d.mainWindow.LoadedData;
import bog.bgmaker.view3d.mainWindow.View3D;
import bog.bgmaker.view3d.managers.EngineMan;
import bog.bgmaker.view3d.managers.WindowMan;
import bog.bgmaker.view3d.utils.Config;
import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.DarculaTheme;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Bog
 */

public class Main {

    public static WindowMan window;
    public static View3D view;
    public static EngineMan engine;
    public static boolean debug;
    public static Thread secondaryThread;

    public static void main(String args[]) {

        Config.init();

        LafManager.install(new DarculaTheme());
        window = new WindowMan(Config.TITLE, 1280, 720);
        view = new View3D(window);
        engine = new EngineMan();
        debug = args.length > 0 && args[0].equalsIgnoreCase("debug");

        LoadedData.loadedModels = new HashMap<>();
        LoadedData.loadedGfxMaterials = new HashMap<>();
        LoadedData.loadedTextures = new HashMap<>();
        LoadedData.FARCs = new ArrayList<>();
        secondaryThread = new Thread() {
            long lastMillis = 0;
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
                    }
                } catch(Exception v) {
                    v.printStackTrace();
                    this.stop();
                    System.exit(-1);
                }
            }
        };

        secondaryThread.start();

        try
        {
            engine.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(-1);
        }

    }


}
