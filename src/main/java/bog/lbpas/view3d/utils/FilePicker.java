package bog.lbpas.view3d.utils;

import bog.lbpas.view3d.mainWindow.LoadedData;
import bog.lbpas.view3d.mainWindow.View3D;
import common.FileChooser;
import cwlib.types.archives.FileArchive;
import cwlib.types.databases.FileDB;
import cwlib.types.save.BigSave;

import java.io.File;

public class FilePicker {

    private static boolean loadMap = false;
    private static boolean loadFarc = false;
    private static boolean loadFart = false;

    public static void loadingThread(View3D mainView)
    {
        if(mainView == null)
            return;

        if(loadMap)
        {
            try {
                File maps[] = FileChooser.openFiles("map");

                if(maps != null && maps.length != 0)
                    for(File file : maps)
                        LoadedData.MAPs.add(new FileDB(file));

                LoadedData.shouldSetupList = true;
            } catch (Exception ex) {
                mainView.pushWarning("File Loading", ex.getMessage());
            }
            loadMap = false;
        }

        if(loadFarc)
        {
            try {
                File[] farcs = FileChooser.openFiles("farc");
                if (farcs != null && farcs.length != 0)
                        for (File farc : farcs)
                        {
                            FileArchive archive = null;
                                archive = new FileArchive(farc);

                            if (archive != null)
                                LoadedData.FARCs.add(archive);
                        }

                LoadedData.shouldSetupList = true;
            } catch (Exception ex) {
                mainView.pushWarning("File Loading", ex.getMessage());
            }

            loadFarc = false;
        }

        if(loadFart)
        {
            try
            {
                File[] farts = FileChooser.openFiles(null);

                if(farts != null && farts.length != 0)
                    for(File file : farts)
                        LoadedData.BIGFARTs.add(new BigSave(file));

                LoadedData.shouldSetupList = true;
            } catch(Exception ex) {
                mainView.pushWarning("File Loading", ex.getMessage());
            }

            loadFart = false;
        }
    }

    public static void loadMap()
    {
        loadMap = true;
    }

    public static void loadFarc()
    {
        loadFarc = true;
    }

    public static void loadFart()
    {
        loadFart = true;
    }

}
