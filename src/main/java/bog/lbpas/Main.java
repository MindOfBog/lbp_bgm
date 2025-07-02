package bog.lbpas;

import bog.lbpas.view3d.mainWindow.LoadedData;
import bog.lbpas.view3d.mainWindow.View3D;
import bog.lbpas.view3d.mainWindow.screens.ProjectManager;
import bog.lbpas.view3d.managers.EngineMan;
import bog.lbpas.view3d.managers.WindowMan;
import bog.lbpas.view3d.renderer.gui.elements.FileTree;
import bog.lbpas.view3d.utils.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import cwlib.enums.ResourceType;
import cwlib.resources.RLevel;
import cwlib.resources.RPlan;
import cwlib.resources.RTranslationTable;
import cwlib.structs.things.Thing;
import cwlib.types.Resource;
import cwlib.types.archives.FileArchive;
import cwlib.types.archives.SaveArchive;
import cwlib.types.data.GUID;
import cwlib.types.data.ResourceDescriptor;
import cwlib.types.databases.FileDB;
import cwlib.types.databases.FileDBRow;
import cwlib.types.mods.Mod;
import cwlib.types.save.BigSave;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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

    public static void main(String args[]){

        Config.init();
        window = new WindowMan(Consts.TITLE, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT, 1050,650);
        Cursors.loadCursors();
        window.init();
        if(Config.WINDOW_MAXIMIZED)
            window.maximize();

        view = new View3D(window);
        engine = new EngineMan();

        try
        {
            engine.init(window, view);
        }catch (Exception e)
        {
            print.stackTrace(e);
        }

        secondaryThread = new Thread() {

            public void run() {
                try
                {
                    while (true)
                    {
                        view.secondaryThread();
                        if(Config.hasConfigChanged())
                            Config.updateFile();
                        Thread.sleep(Config.SECONDARY_THREAD);
                    }
                } catch(Exception v) {
                    print.stackTrace(v);
                    System.exit(-1);
                }
            }
        };

        secondaryThread.start();

        loaderThread = new Thread() {

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
                        FilePicker.loadingThread((View3D) engine.viewLogic);
                        Thread.sleep(Config.LOADER_THREAD);
                    } catch(Exception v) {
                        print.stackTrace(v);
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
                        Thread.sleep(Config.ENTRY_DIGEST_THREAD);
                    } catch(Exception v) {
                        print.stackTrace(v);
                        System.exit(-1);
                    }
                }
            }
        };

        entryDigesionThread.start();

        if(args.length > 0)
            for(String arg : args)
            {
                switch (arg.toLowerCase())
                {
                    case "debug":
                        debug = true;
                        view.Settings.initDebug();
                        break;
                }

                if(arg.contains("="))
                {
                    String[] splitArg = arg.split("=");
                    String name = splitArg[0];
                    String value = splitArg[1];

                    JsonObject jsonData;

                    switch (name)
                    {
                        case "projectPath":
                        {
                            File file = Paths.get(value).toFile();
                            String projectFilePath = file.getPath();

                            view.ProjectManager.projectSavePath.setText(projectFilePath);

                            try (InputStream fileInputStream = Files.newInputStream(Paths.get(projectFilePath));
                                 ZipInputStream zipIn = new ZipInputStream(fileInputStream)) {

                                byte[] mapData = null;
                                byte[] farcData = null;

                                ZipEntry entry;

                                ArrayList<Number> selected = new ArrayList<>();

                                while ((entry = zipIn.getNextEntry()) != null) {
                                    switch (entry.getName())
                                    {
                                        case "data.json":
                                        {
                                            Gson gson = new Gson();
                                            InputStreamReader reader = new InputStreamReader(zipIn);
                                            jsonData = gson.fromJson(reader, JsonObject.class);

                                            if(jsonData.has("cameraPosRot"));
                                            {
                                                JsonArray cameraPosRot = jsonData.get("cameraPosRot").getAsJsonArray();
                                                view.camera.setPos(
                                                        cameraPosRot.get(0).getAsFloat(),
                                                        cameraPosRot.get(1).getAsFloat(),
                                                        cameraPosRot.get(2).getAsFloat());
                                                view.camera.setRot(
                                                        cameraPosRot.get(3).getAsFloat(),
                                                        cameraPosRot.get(4).getAsFloat(),
                                                        cameraPosRot.get(5).getAsFloat());
                                            }
                                            if(jsonData.has("scene"))
                                            {
                                                JsonObject scene = jsonData.get("scene").getAsJsonObject();

                                                if(scene.has("loadedEntitiesSearch"));
                                                    view.ElementEditing.loadedEntitiesSearch.setText(scene.get("loadedEntitiesSearch").getAsString());

                                                if(scene.has("tabCamPos"));
                                                {
                                                    JsonObject tabCamPos = scene.get("tabCamPos").getAsJsonObject();

                                                    if(tabCamPos.has("tabPos"))
                                                    {
                                                        JsonArray tabPos = tabCamPos.get("tabPos").getAsJsonArray();
                                                        view.ElementEditing.camPos.pos.x = tabPos.get(0).getAsFloat();
                                                        view.ElementEditing.camPos.pos.y = tabPos.get(1).getAsFloat();
                                                    }

                                                    if(tabCamPos.has("tabExtended"))
                                                        view.ElementEditing.camPos.extended = tabCamPos.get("tabExtended").getAsBoolean();
                                                }

                                                if(scene.has("tabHelpers"));
                                                {
                                                    JsonObject tabHelpers = scene.get("tabHelpers").getAsJsonObject();

                                                    if(tabHelpers.has("tabPos"))
                                                    {
                                                        JsonArray tabPos = tabHelpers.get("tabPos").getAsJsonArray();
                                                        view.ElementEditing.helpers.pos.x = tabPos.get(0).getAsFloat();
                                                        view.ElementEditing.helpers.pos.y = tabPos.get(1).getAsFloat();
                                                    }

                                                    if(tabHelpers.has("tabExtended"))
                                                        view.ElementEditing.helpers.extended = tabHelpers.get("tabExtended").getAsBoolean();
                                                }

                                                if(scene.has("tabAssets"));
                                                {
                                                    JsonObject tabAssets = scene.get("tabAssets").getAsJsonObject();

                                                    if(tabAssets.has("tabPos"))
                                                    {
                                                        JsonArray tabPos = tabAssets.get("tabPos").getAsJsonArray();
                                                        view.ElementEditing.availableAssets.pos.x = tabPos.get(0).getAsFloat();
                                                        view.ElementEditing.availableAssets.pos.y = tabPos.get(1).getAsFloat();
                                                    }

                                                    if(tabAssets.has("tabExtended"))
                                                        view.ElementEditing.availableAssets.extended = tabAssets.get("tabExtended").getAsBoolean();

                                                    if(tabAssets.has("assetSearch"))
                                                        view.ElementEditing.availableAssetsSearch.setText(tabAssets.get("assetSearch").getAsString());
                                                    if(tabAssets.has("assetFilter"))
                                                    {
                                                        JsonArray filters = tabAssets.get("assetFilter").getAsJsonArray();
                                                        view.ElementEditing.filterPlans.isChecked = filters.get(0).getAsBoolean();
                                                        view.ElementEditing.filterLevels.isChecked = filters.get(1).getAsBoolean();
                                                        view.ElementEditing.filterMeshes.isChecked = filters.get(2).getAsBoolean();
                                                        view.ElementEditing.filterMaterials.isChecked = filters.get(3).getAsBoolean();
                                                    }
                                                }

                                                if(scene.has("tabCurrentSelection"));
                                                {
                                                    JsonObject tabCurrentSelection = scene.get("tabCurrentSelection").getAsJsonObject();

                                                    if(tabCurrentSelection.has("tabPos"))
                                                    {
                                                        JsonArray tabPos = tabCurrentSelection.get("tabPos").getAsJsonArray();
                                                        view.ElementEditing.currentSelection.pos.x = tabPos.get(0).getAsFloat();
                                                        view.ElementEditing.currentSelection.pos.y = tabPos.get(1).getAsFloat();
                                                    }

                                                    if(tabCurrentSelection.has("tabExtended"))
                                                        view.ElementEditing.currentSelection.extended = tabCurrentSelection.get("tabExtended").getAsBoolean();
                                                }
                                            }
                                            if(jsonData.has("archive"))
                                            {
                                                JsonObject archive = jsonData.get("archive").getAsJsonObject();

                                                if(archive.has("archivePaths"))
                                                {
                                                    JsonObject archivePaths = archive.get("archivePaths").getAsJsonObject();

                                                    if(archivePaths.has("map"))
                                                    {
                                                        JsonArray map = archivePaths.get("map").getAsJsonArray();

                                                        for(int i = 0; i < map.size(); i++)
                                                        {
                                                            String path = map.get(i).getAsString();
                                                            LoadedData.MAPs.add(new FileDB(new File(path)));
                                                        }
                                                    }
                                                    if(archivePaths.has("farc"))
                                                    {
                                                        JsonArray farc = archivePaths.get("farc").getAsJsonArray();

                                                        for(int i = 0; i < farc.size(); i++)
                                                        {
                                                            String path = farc.get(i).getAsString();

                                                            FileArchive arc = null;
                                                            arc = new FileArchive(path);

                                                            if (arc != null)
                                                                LoadedData.FARCs.add(arc);
                                                        }
                                                    }
                                                    if(archivePaths.has("fart"))
                                                    {
                                                        JsonArray fart = archivePaths.get("fart").getAsJsonArray();

                                                        for(int i = 0; i < fart.size(); i++)
                                                        {
                                                            String path = fart.get(i).getAsString();

                                                            LoadedData.BIGFARTs.add(new BigSave(Paths.get(path).toFile()));
                                                        }
                                                    }
                                                    LoadedData.shouldSetupList = true;

                                                    if(archivePaths.has("translation"))
                                                    {
                                                        while (LoadedData.shouldSetupList)
                                                            Thread.sleep(100);

                                                        JsonObject translation = archivePaths.get("translation").getAsJsonObject();
                                                        long main = translation.get("main").getAsLong();
                                                        long patch = translation.get("patch").getAsLong();

                                                        if(main != -1)
                                                        {
                                                            byte[] data = LoadedData.extract(new ResourceDescriptor(new GUID(main), ResourceType.TRANSLATION));
                                                            if (data == null)
                                                            {
                                                                print.error("Failed loading Translation: extracted data null");
                                                            }
                                                            else {
                                                                RTranslationTable trans = new RTranslationTable(data);

                                                                if(trans != null)
                                                                {
                                                                    LoadedData.loadedTranslationTable = trans;
                                                                    LoadedData.loadedTranslation = main;
                                                                }

                                                                if(patch != -1)
                                                                {
                                                                    byte[] dataP = LoadedData.extract(new ResourceDescriptor(new GUID(patch), ResourceType.TRANSLATION));
                                                                    if (dataP == null)
                                                                    {
                                                                        print.error("Failed loading Translation Patch: extracted data null");
                                                                        return;
                                                                    }

                                                                    RTranslationTable translationP = new RTranslationTable(dataP);

                                                                    if(translationP != null)
                                                                    {
                                                                        LoadedData.loadedPatchTranslationTable = translationP;
                                                                        LoadedData.loadedPatchTranslation = patch;
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }

                                                if(archive.has("tabMaps"));
                                                {
                                                    JsonObject tabMaps = archive.get("tabMaps").getAsJsonObject();

                                                    if(tabMaps.has("tabPos"))
                                                    {
                                                        JsonArray tabPos = tabMaps.get("tabPos").getAsJsonArray();
                                                        view.Archive.mapList.pos.x = tabPos.get(0).getAsFloat();
                                                        view.Archive.mapList.pos.y = tabPos.get(1).getAsFloat();
                                                    }

                                                    if(tabMaps.has("tabExtended"))
                                                        view.Archive.mapList.extended = tabMaps.get("tabExtended").getAsBoolean();
                                                }

                                                if(archive.has("tabFarcs"));
                                                {
                                                    JsonObject tabFarcs = archive.get("tabFarcs").getAsJsonObject();

                                                    if(tabFarcs.has("tabPos"))
                                                    {
                                                        JsonArray tabPos = tabFarcs.get("tabPos").getAsJsonArray();
                                                        view.Archive.farcList.pos.x = tabPos.get(0).getAsFloat();
                                                        view.Archive.farcList.pos.y = tabPos.get(1).getAsFloat();
                                                    }

                                                    if(tabFarcs.has("tabExtended"))
                                                        view.Archive.farcList.extended = tabFarcs.get("tabExtended").getAsBoolean();
                                                }

                                                if(archive.has("tabFarts"));
                                                {
                                                    JsonObject tabFarts = archive.get("tabFarts").getAsJsonObject();

                                                    if(tabFarts.has("tabPos"))
                                                    {
                                                        JsonArray tabPos = tabFarts.get("tabPos").getAsJsonArray();
                                                        view.Archive.fartList.pos.x = tabPos.get(0).getAsFloat();
                                                        view.Archive.fartList.pos.y = tabPos.get(1).getAsFloat();
                                                    }

                                                    if(tabFarts.has("tabExtended"))
                                                        view.Archive.fartList.extended = tabFarts.get("tabExtended").getAsBoolean();
                                                }

                                                if(archive.has("tabTranslations"));
                                                {
                                                    JsonObject tabTranslations = archive.get("tabTranslations").getAsJsonObject();

                                                    if(tabTranslations.has("tabPos"))
                                                    {
                                                        JsonArray tabPos = tabTranslations.get("tabPos").getAsJsonArray();
                                                        view.Archive.translations.pos.x = tabPos.get(0).getAsFloat();
                                                        view.Archive.translations.pos.y = tabPos.get(1).getAsFloat();
                                                    }

                                                    if(tabTranslations.has("tabExtended"))
                                                        view.Archive.translations.extended = tabTranslations.get("tabExtended").getAsBoolean();

                                                    if(tabTranslations.has("search"))
                                                        view.Archive.searchTransl.setText(tabTranslations.get("search").getAsString());
                                                }
                                            }
                                            if(jsonData.has("project"))
                                            {
                                                JsonObject project = jsonData.get("project").getAsJsonObject();

                                                if(project.has("tabProject"));
                                                {
                                                    JsonObject tabBuilder = project.get("tabProject").getAsJsonObject();

                                                    if(tabBuilder.has("tabPos"))
                                                    {
                                                        JsonArray tabPos = tabBuilder.get("tabPos").getAsJsonArray();
                                                        view.ProjectManager.project.pos.x = tabPos.get(0).getAsFloat();
                                                        view.ProjectManager.project.pos.y = tabPos.get(1).getAsFloat();
                                                    }

                                                    if(tabBuilder.has("tabExtended"))
                                                        view.ProjectManager.project.extended = tabBuilder.get("tabExtended").getAsBoolean();
                                                }

                                                if(project.has("plan"))
                                                {
                                                    JsonObject plan = project.get("plan").getAsJsonObject();

                                                    if(plan.has("extended"))
                                                        view.ProjectManager.planExport.extended = plan.get("extended").getAsBoolean();

                                                    if(plan.has("selection"))
                                                        view.ProjectManager.selectionOnlyExportPlan.isChecked = plan.get("selection").getAsBoolean();
                                                    if(plan.has("ints"))
                                                        view.ProjectManager.compressionFlagsIntPlan.isChecked = plan.get("ints").getAsBoolean();
                                                    if(plan.has("vecs"))
                                                        view.ProjectManager.compressionFlagsVecPlan.isChecked = plan.get("vecs").getAsBoolean();
                                                    if(plan.has("mats"))
                                                        view.ProjectManager.compressionFlagsMatPlan.isChecked = plan.get("mats").getAsBoolean();
                                                    if(plan.has("head"))
                                                        view.ProjectManager.customRevisionPlanHead.setText(plan.get("head").getAsString());
                                                    if(plan.has("id"))
                                                        view.ProjectManager.customRevisionPlanID.setText(plan.get("id").getAsString());
                                                    if(plan.has("rev"))
                                                        view.ProjectManager.customRevisionPlanRev.setText(plan.get("rev").getAsString());
                                                }

                                                if(project.has("bin"))
                                                {
                                                    JsonObject bin = project.get("bin").getAsJsonObject();

                                                    if(bin.has("extended"))
                                                        view.ProjectManager.binExport.extended = bin.get("extended").getAsBoolean();

                                                    if(bin.has("selection"))
                                                        view.ProjectManager.selectionOnlyExportBin.isChecked = bin.get("selection").getAsBoolean();
                                                    if(bin.has("ints"))
                                                        view.ProjectManager.compressionFlagsIntBin.isChecked = bin.get("ints").getAsBoolean();
                                                    if(bin.has("vecs"))
                                                        view.ProjectManager.compressionFlagsVecBin.isChecked = bin.get("vecs").getAsBoolean();
                                                    if(bin.has("mats"))
                                                        view.ProjectManager.compressionFlagsMatBin.isChecked = bin.get("mats").getAsBoolean();
                                                    if(bin.has("head"))
                                                        view.ProjectManager.customRevisionBinHead.setText(bin.get("head").getAsString());
                                                    if(bin.has("id"))
                                                        view.ProjectManager.customRevisionBinID.setText(bin.get("id").getAsString());
                                                    if(bin.has("rev"))
                                                        view.ProjectManager.customRevisionBinRev.setText(bin.get("rev").getAsString());
                                                }
                                            }
                                            if(jsonData.has("selectedThings")) {
                                                JsonArray selectedThings = jsonData.get("selectedThings").getAsJsonArray();
                                                selected = new Gson().fromJson(selectedThings, selected.getClass());
                                            }
                                        }
                                        break;
                                        case "sceneBuilder.plan":
                                        {
                                            byte[] data = null;

                                            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

                                                byte[] buffer = new byte[1024];
                                                int bytesRead;
                                                while ((bytesRead = zipIn.read(buffer)) != -1) {
                                                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                                                }

                                                data = byteArrayOutputStream.toByteArray();
                                            }

                                            if(data != null)
                                            {
                                                RPlan plan = new Resource(data).loadResource(RPlan.class);
                                                if (plan != null)
                                                {
                                                    view.ProjectManager.importMetadataPlan(plan);
                                                }
                                            }
                                        }
                                            break;
                                        case "sceneBuilder.bin":
                                        {
                                            byte[] data = null;

                                            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

                                                byte[] buffer = new byte[1024];
                                                int bytesRead;
                                                while ((bytesRead = zipIn.read(buffer)) != -1) {
                                                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                                                }

                                                data = byteArrayOutputStream.toByteArray();
                                            }

                                            if(data != null)
                                            {
                                                RLevel lvl = new Resource(data).loadResource(RLevel.class);
                                                if (lvl != null)
                                                {
                                                    view.ProjectManager.importMetadataBin(lvl);
                                                }
                                            }
                                        }
                                            break;
                                        case "data.map":
                                        {
                                            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

                                                byte[] buffer = new byte[1024];
                                                int bytesRead;
                                                while ((bytesRead = zipIn.read(buffer)) != -1) {
                                                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                                                }

                                                mapData = byteArrayOutputStream.toByteArray();
                                            }
                                        }
                                        break;
                                        case "data.farc":
                                        {
                                            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

                                                byte[] buffer = new byte[1024];
                                                int bytesRead;
                                                while ((bytesRead = zipIn.read(buffer)) != -1) {
                                                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                                                }

                                                farcData = byteArrayOutputStream.toByteArray();
                                            }
                                        }
                                        break;
                                        case "world.plan":
                                        {
                                            while(LoadedData.shouldSetupList)
                                                Thread.sleep(100);

                                            byte[] data = null;

                                            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

                                                byte[] buffer = new byte[1024];
                                                int bytesRead;
                                                while ((bytesRead = zipIn.read(buffer)) != -1) {
                                                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                                                }

                                                data = byteArrayOutputStream.toByteArray();
                                            }

                                            if(data != null)
                                            {
                                                RPlan plan = new Resource(data).loadResource(RPlan.class);
                                                if (plan != null)
                                                {
                                                    Thing[] things = plan.getThings();

                                                    view.addThings(things, selected);
                                                }
                                            }
                                        }
                                        break;
                                    }

                                    zipIn.closeEntry();
                                }

                                if(mapData != null && farcData != null)
                                {
                                    LoadedData.PROJECT_DATA = new Mod(mapData, farcData);

                                    FileTree fileTree = view.ProjectManager.modFileTree;

                                    for(FileDBRow row : LoadedData.PROJECT_DATA.entries)
                                    {
                                        String rowName = row.getName();
                                        String rowPath = row.getPath().replaceAll(rowName, "");

                                        String[] folders = rowPath.split("/");

                                        print.neutral(folders);

                                        if(row.getSHA1().toString().equalsIgnoreCase("0000000000000000000000000000000000000000") && row.getSize() == 0)
                                        {
                                            if(folders.length <= 1)
                                                fileTree.root.addFolder(String.valueOf(fileTree.root.children.size()), row, rowName, fileTree.itemHeight);
                                            else
                                            {
                                                FileTree.TreeFolder parentFolder = fileTree.root;

                                                String path = "";

                                                for(String parent : folders)
                                                {
                                                    FileTree.TreeFolder childFolder = null;
                                                    for(FileTree.TreeItem parentChild : parentFolder.children)
                                                        if(parentChild instanceof FileTree.TreeFolder && parentChild.itemName.getText().equalsIgnoreCase(parent))
                                                            childFolder = (FileTree.TreeFolder) parentChild;

                                                    if(childFolder == null)
                                                    {
                                                        FileDBRow newRow = LoadedData.PROJECT_DATA.newFileDBRow(((path == null || path.isEmpty() || path.isBlank()) ? "" : path + "/"));
                                                        childFolder = parentFolder.addFolder(String.valueOf(parentFolder.children.size()), newRow, parent, fileTree.itemHeight);
                                                    }

                                                    parentFolder = childFolder;
                                                    path += parent + "/";
                                                }

                                                parentFolder.addFolder(String.valueOf(parentFolder.children.size()), row, rowName, fileTree.itemHeight);
                                            }
                                        }
                                        else
                                        {
                                            if(folders.length == 0)
                                                fileTree.root.addItem(String.valueOf(fileTree.root.children.size()), row, rowName, fileTree.itemHeight);
                                            else
                                            {
                                                FileTree.TreeFolder parentFolder = fileTree.root;

                                                String path = "";

                                                for(String parent : folders)
                                                {
                                                    FileTree.TreeFolder childFolder = null;
                                                    for(FileTree.TreeItem parentChild : parentFolder.children)
                                                        if(parentChild instanceof FileTree.TreeFolder && parentChild.itemName.getText().equalsIgnoreCase(parent))
                                                            childFolder = (FileTree.TreeFolder) parentChild;

                                                    if(childFolder == null)
                                                    {
                                                        FileDBRow newRow = LoadedData.PROJECT_DATA.newFileDBRow(((path == null || path.isEmpty() || path.isBlank()) ? "" : path + "/"));
                                                        childFolder = parentFolder.addFolder(String.valueOf(parentFolder.children.size()), newRow, parent, fileTree.itemHeight);
                                                    }

                                                    parentFolder = childFolder;
                                                    path += parent + "/";
                                                }

                                                parentFolder.addItem(String.valueOf(parentFolder.children.size()), row, rowName, fileTree.itemHeight);
                                            }
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                print.stackTrace(e);
                            }
                        }
                            break;
                    }
                }
            }

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

        try
        {
            view.initMillis = System.currentTimeMillis();
            engine.start(window, view);
        }
        catch (Exception e)
        {
            print.stackTrace(e);
            System.exit(-1);
        }
    }
}
