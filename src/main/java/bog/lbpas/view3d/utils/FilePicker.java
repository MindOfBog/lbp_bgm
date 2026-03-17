package bog.lbpas.view3d.utils;

import bog.lbpas.Main;
import bog.lbpas.view3d.core.Texture;
import bog.lbpas.view3d.mainWindow.ConstantTextures;
import bog.lbpas.view3d.mainWindow.LoadedData;
import bog.lbpas.view3d.mainWindow.View3D;
import bog.lbpas.view3d.renderer.gui.elements.*;
import bog.lbpas.view3d.renderer.gui.elements.Button;
import bog.lbpas.view3d.renderer.gui.elements.Image;
import bog.lbpas.view3d.renderer.gui.elements.Panel;
import com.googlecode.jfilechooserbookmarks.*;
import cwlib.enums.ResourceType;
import cwlib.types.archives.FileArchive;
import cwlib.types.databases.FileDB;
import cwlib.types.databases.FileDBRow;
import cwlib.types.mods.Mod;
import cwlib.types.save.BigSave;
import kotlin.collections.builders.SetBuilder;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.prefs.Preferences;

public class FilePicker {

    private static boolean loadMap = false;
    private static boolean loadFarc = false;
    private static boolean loadFart = false;
    private static boolean saveMod = false;
    private static boolean selectProjectLocation = false;
    private static String loadProjectAssetsPath = null;
    private static FileTree.TreeFolder loadProjectAssetsFolder = null;
    private static FileTree.TreeFolder loadProjectAssetsMod = null;
    private static FileTree.TreeItem replaceProjectItem = null;
    private static boolean loadProjectTextures = false;
    private static FileDBRow extractFile = null;

    private static final Preferences prefs = Preferences.userNodeForPackage(FilePicker.class);
    public static boolean dialogOpen = false;
    private static JFrame dummyFrame;
    private static JFileChooser currentChooser;

    public static FileNameExtensionFilter[] ALL_LBP_EXTENSIONS;
    public static FileNameExtensionFilter[] IMAGE_EXTENSIONS;
    public static FileNameExtensionFilter MOD_EXTENSION;
    public static FileNameExtensionFilter FARC_EXTENSION;
    public static FileNameExtensionFilter MAP_EXTENSION;
    public static FileNameExtensionFilter PROJECT_EXTENSION;
    public static FileNameExtensionFilter JSON_EXTENSION;
    public static FileNameExtensionFilter[] AUDIO_EXTENSIONS;

    public static void init()
    {
        dummyFrame = new JFrame();
        dummyFrame.setIconImages(Main.iconList);
        dummyFrame.setVisible(false);

        ALL_LBP_EXTENSIONS = setupLBPExtensionFilter("All LBP File Formats", ResourceType.values());
        IMAGE_EXTENSIONS = new FileNameExtensionFilter[]
                {
                        new FileNameExtensionFilter("All Image Formats (*.png, *.jpg, *.jpeg, *.dds)", "png", "jpg", "jpeg", "dds"),
                        new FileNameExtensionFilter("PNG (*.png)", "png"),
                        new FileNameExtensionFilter("JPEG (*.jpg, *.jpeg)", "jpg", "jpeg"),
                        new FileNameExtensionFilter("DDS (*.dds)", "dds"),
                };
        MOD_EXTENSION = new FileNameExtensionFilter("Mod (*.mod)", "mod");
        FARC_EXTENSION = new FileNameExtensionFilter("FileArchive (*.farc)", "farc");
        MAP_EXTENSION = new FileNameExtensionFilter("FileDB (*.map)", "map");
        PROJECT_EXTENSION = new FileNameExtensionFilter("LBP AS Project (*.jar)", "jar");
        JSON_EXTENSION = new FileNameExtensionFilter("JSON (*.json)", "json");
        AUDIO_EXTENSIONS = new FileNameExtensionFilter[]
                {
                        new FileNameExtensionFilter("All Audio Formats (*.wav, *.mp3, *.ogg, *.au, *.aiff)", "wav", "mp3", "ogg", "au", "aiff"),
                        new FileNameExtensionFilter("WAV (*.wav)", "wav"),
                        new FileNameExtensionFilter("MP3 (*.mp3)", "mp3"),
                        new FileNameExtensionFilter("Vorbis (*.ogg)", "ogg"),
                        new FileNameExtensionFilter("SUN/NEXT AUDIO (*.au)", "au"),
                        new FileNameExtensionFilter("APPLE (*.aiff)", "aiff"),
                };
    }

    public static FileNameExtensionFilter[] setupLBPExtensionFilter(String allDescription, ResourceType[] types)
    {
        boolean filterAll = types.length > 1 && allDescription != null;
        ArrayList<FileNameExtensionFilter> lbpExtensions = new ArrayList<>();

        Set<String> extensions = new SetBuilder<>();

        if(filterAll)
            lbpExtensions.add(null);

        for(ResourceType type : types)
        {
            if(type.getValue() == 0)
                continue;

            String description = "";

            for(String split : type.name().split("_"))
                description += split.substring(0, 1).toUpperCase() + split.substring(1).toLowerCase() + " ";
            description.trim();

            String extension = type.getExtension().substring(1);

            String secondaryExtension = null;
            if(type.getHeader() != null && !type.getHeader().isEmpty())
                secondaryExtension = type.getHeader().toLowerCase();

            if(secondaryExtension != null && !extension.equalsIgnoreCase(secondaryExtension))
            {
                lbpExtensions.add(new FileNameExtensionFilter(description + " (*." + extension + ", *." + secondaryExtension + ")", extension, secondaryExtension));

                if(filterAll)
                {
                    extensions.add(extension);
                    extensions.add(secondaryExtension);
                }
            }
            else
            {
                lbpExtensions.add(new FileNameExtensionFilter(description + " (*." + extension + ")", extension));

                if(filterAll)
                    extensions.add(extension);
            }
        }

        if(filterAll)
        {
            String allFilterDescription = allDescription + " (";

            for(String e : extensions)
            {
                allFilterDescription += "*." + e + ", ";

                if(allFilterDescription.length() > allDescription.length() + 20)
                {
                    allFilterDescription = allFilterDescription.substring(0, allFilterDescription.indexOf(",", allDescription.length() + 19));
                    allFilterDescription += ", ...";
                    break;
                }
            }

            if (allFilterDescription.endsWith(","))
                allFilterDescription = allFilterDescription.substring(0, allFilterDescription.length() - 1);

            allFilterDescription += ")";

            lbpExtensions.set(0, new FileNameExtensionFilter(allFilterDescription, extensions.toArray(new String[0])));
        }

        return lbpExtensions.toArray(new FileNameExtensionFilter[0]);
    }

    public static void onWindowFocus(boolean focused)
    {
        if (currentChooser == null)
            return;

        if(focused)
            forceFocus();
    }

    private static void forceFocus()
    {
        if(dummyFrame == null)
            return;

        dummyFrame.toFront();
        dummyFrame.setAlwaysOnTop(true);

        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            while(!currentChooser.isShowing()){
                try {Thread.sleep(50l);} catch (InterruptedException e) {print.stackTrace(e);}
            }
            dummyFrame.setAlwaysOnTop(false);
        });
    }

    public static Path getLastVisitedPath()
    {
        String lastPath = prefs.get("lastVisitedPath", System.getProperty("user.home"));
        return Paths.get(lastPath);
    }

    public static void setLastVisitedPath(Path path) {
        prefs.put("lastVisitedPath", path.toString());
    }

    public static File[] openFiles(String fileName, FileNameExtensionFilter[] extensions, boolean saveFile, boolean multiple)
    {
        dialogOpen = true;
        JFileChooser fileChooser = new JFileChooser(getLastVisitedPath().toFile());

        FileChooserBookmarksPanel panel = new FileChooserBookmarksPanel();
        panel.setOwner(fileChooser);
        fileChooser.setAccessory(panel);

        if (fileName != null && !fileName.isEmpty())
            fileChooser.setSelectedFile(new File(fileName));

        if (extensions != null) {
            FileNameExtensionFilter defaultFilter = null;

            for (FileNameExtensionFilter ext : extensions) {

                ArrayList<String> newExtensions = new ArrayList<>(Arrays.asList(ext.getExtensions()));
                newExtensions.add("lnk");

                FileNameExtensionFilter filter = new FileNameExtensionFilter(ext.getDescription(), newExtensions.toArray(new String[0]));

                if(defaultFilter == null)
                    defaultFilter = filter;

                fileChooser.addChoosableFileFilter(filter);
            }

            if(defaultFilter != null)
                fileChooser.setFileFilter(defaultFilter);
        }

        fileChooser.setMultiSelectionEnabled(multiple);

        File[] selectedFiles = null;
        try {
            currentChooser = fileChooser;

            //todo
            forceFocus();

            int dialogResult = saveFile ? fileChooser.showSaveDialog(dummyFrame) : fileChooser.showOpenDialog(dummyFrame);

            setLastVisitedPath(fileChooser.getCurrentDirectory().toPath());

            if (dialogResult == JFileChooser.APPROVE_OPTION) {
                if (multiple)
                {
                    selectedFiles = fileChooser.getSelectedFiles();

                    for(int i = 0; i < selectedFiles.length; i++)
                    {
                        File selectedFile = selectedFiles[i];

                        if (selectedFile != null && selectedFile.getName().endsWith(".lnk"))
                            selectedFiles[i] = ShortcutResolver.resolveShortcut(selectedFile);
                    }
                }
                else
                {
                    File selectedFile = fileChooser.getSelectedFile();

                    if (selectedFile != null && selectedFile.getName().endsWith(".lnk"))
                        selectedFiles = new File[]{ShortcutResolver.resolveShortcut(selectedFile)};
                    else
                        selectedFiles = new File[]{selectedFile};
                }
            }

        }catch (Exception e){print.stackTrace(e);}

        dialogOpen = false;
        currentChooser = null;
        return selectedFiles;
    }

    public static File[] openFiles(FileNameExtensionFilter[] extensions, boolean saveFile, boolean multiple)
    {
        return openFiles(null, extensions, saveFile, multiple);
    }
    public static File[] openFiles(FileNameExtensionFilter[] extensions)
    {
        return openFiles(null, extensions, false, true);
    }

    public static File saveFile(String fileName, FileNameExtensionFilter[] extensions) {
        dialogOpen = true;
        JFileChooser fileChooser = new JFileChooser(getLastVisitedPath().toFile());

        FileChooserBookmarksPanel panel = new FileChooserBookmarksPanel();
        panel.setOwner(fileChooser);
        fileChooser.setAccessory(panel);

        if (fileName != null && !fileName.isEmpty()) {
            fileChooser.setSelectedFile(new File(fileName));
        }

        if (extensions != null) {
            FileNameExtensionFilter defaultFilter = null;

            for (FileNameExtensionFilter ext : extensions) {

                ArrayList<String> newExtensions = new ArrayList<>(Arrays.asList(ext.getExtensions()));
                newExtensions.add("lnk");

                FileNameExtensionFilter filter = new FileNameExtensionFilter(ext.getDescription(), newExtensions.toArray(new String[0]));

                if(defaultFilter == null)
                    defaultFilter = filter;

                fileChooser.addChoosableFileFilter(filter);
            }

            if(defaultFilter != null)
                fileChooser.setFileFilter(defaultFilter);
        }

        currentChooser = fileChooser;

        forceFocus();

        File file = null;

        int dialogResult = fileChooser.showSaveDialog(dummyFrame);

        setLastVisitedPath(fileChooser.getCurrentDirectory().toPath());

        if (dialogResult == JFileChooser.APPROVE_OPTION) {

            File selectedFile = fileChooser.getSelectedFile();

            if (selectedFile != null && selectedFile.getName().endsWith(".lnk"))
                file = ShortcutResolver.resolveShortcut(selectedFile);
            else
                file = selectedFile;
        }

        dialogOpen = false;
        currentChooser = null;
        return file;
    }

    public static void loadingThread(View3D mainView)
    {
        if(mainView == null)
            return;

        if(loadMap)
        {
            try {
                File[] maps = openFiles(new FileNameExtensionFilter[]{MAP_EXTENSION});

                if(maps != null && maps.length != 0)
                {
                    for(File file : maps)
                        LoadedData.MAPs.add(new FileDB(file));
                    LoadedData.shouldSetupList = true;
                }
            } catch (Exception ex) {
//                mainView.pushWarning("File Loading", ex.getMessage());
            }
            loadMap = false;
        }

        if(loadFarc)
        {
            try {
                File[] farcs = openFiles(new FileNameExtensionFilter[]{FARC_EXTENSION});
                if (farcs != null && farcs.length != 0)
                {
                    for (File farc : farcs)
                    {
                        FileArchive archive = null;
                        archive = new FileArchive(farc);

                        if (archive != null)
                            LoadedData.FARCs.add(archive);
                    }

                    LoadedData.shouldSetupList = true;
                }
            } catch (Exception ex) {
//                mainView.pushWarning("File Loading", ex.getMessage());
            }

            loadFarc = false;
        }

        if(loadFart)
        {
            try
            {
                File[] farts = openFiles(null);

                if(farts != null && farts.length != 0)
                {
                    for(File file : farts)
                        LoadedData.BIGFARTs.add(new BigSave(file));

                    LoadedData.shouldSetupList = true;
                }
            } catch(Exception ex) {
//                mainView.pushWarning("File Loading", ex.getMessage());
            }

            loadFart = false;
        }

        if(saveMod)
        {
            try {
                File mod = saveFile(mainView.ProjectManager.modFileTree.root.itemName.getText(), new FileNameExtensionFilter[]{MOD_EXTENSION});
                LoadedData.PROJECT_DATA.save(mod);
            } catch (Exception ex) {
//                mainView.pushWarning("File Loading", ex.getMessage());
            }

            saveMod = false;
        }

        if(selectProjectLocation)
        {
            try
            {
                File file = saveFile(mainView.ProjectManager.projectSavePath.getText(), new FileNameExtensionFilter[]{PROJECT_EXTENSION});
                mainView.ProjectManager.projectSavePath.setText(file.getPath());
            } catch(Exception ex) {
//                mainView.pushWarning("File Loading", ex.getMessage());
            }

            selectProjectLocation = false;
        }

        if(loadProjectAssetsPath != null)
        {
            try
            {
                File[] assets = openFiles(ALL_LBP_EXTENSIONS);
                if(assets != null && assets.length != 0)
                {
                    for(File file : assets)
                    {
                        FileDBRow row = LoadedData.PROJECT_DATA.add(loadProjectAssetsPath + file.getName(), Files.readAllBytes(file.toPath()));
                        FileTree.TreeItem newItem = loadProjectAssetsFolder.addItem(String.valueOf(loadProjectAssetsFolder.children.size()), row, file.getName(), loadProjectAssetsFolder.size.y);
                    }

                    LoadedData.shouldSetupList = true;
                }
            } catch(Exception ex) {
//                mainView.pushWarning("File Loading", ex.getMessage());
            }

            loadProjectAssetsPath = null;
            loadProjectAssetsFolder = null;
        }

        if(loadProjectAssetsMod != null)
        {
            try
            {
                File[] assets = openFiles(new FileNameExtensionFilter[]{MOD_EXTENSION});
                if(assets != null && assets.length != 0)
                {
                    for(File file : assets)
                    {
                        byte[] firstTwoBytes = new byte[2];

                        try (FileInputStream fis = new FileInputStream(file))
                        {
                            int bytesRead = fis.read(firstTwoBytes);
                        }
                        catch (IOException e) {print.stackTrace(e);}

                        Mod mod = firstTwoBytes == new byte[] {0x50, 0x4B} ? Mod.fromLegacyMod(file) : new Mod(file);
                        if (mod == null) continue;

                        for (FileDBRow row : mod) {
                            byte[] data = mod.extract(row.getSHA1());
                            if (data == null) continue;

                            String rowName = row.getName();
                            String rowPath = row.getPath().replaceAll(rowName, "");

                            String[] folders = rowPath.split("/");

                            FileDBRow nrow = LoadedData.PROJECT_DATA.add(row.getPath(), data);
//                            FileTree.TreeItem newItem = loadProjectAssetsMod.addItem(String.valueOf(loadProjectAssetsMod.children.size()), nrow, file.getName(), loadProjectAssetsMod.size.y);

                            if(nrow.getSHA1().toString().equalsIgnoreCase("0000000000000000000000000000000000000000") && nrow.getSize() == 0)
                            {
                                if(folders.length <= 1)
                                    loadProjectAssetsMod.addFolder(String.valueOf(loadProjectAssetsMod.children.size()), nrow, rowName, loadProjectAssetsMod.size.y);
                                else
                                {
                                    FileTree.TreeFolder parentFolder = loadProjectAssetsMod;

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
                                            childFolder = parentFolder.addFolder(String.valueOf(parentFolder.children.size()), newRow, parent, loadProjectAssetsMod.size.y);
                                        }

                                        parentFolder = childFolder;
                                        path += parent + "/";
                                    }

                                    parentFolder.addFolder(String.valueOf(parentFolder.children.size()), nrow, rowName, loadProjectAssetsMod.size.y);
                                }
                            }
                            else
                            {
                                if(folders.length == 0)
                                    loadProjectAssetsMod.addItem(String.valueOf(loadProjectAssetsMod.children.size()), nrow, rowName, loadProjectAssetsMod.size.y);
                                else
                                {
                                    FileTree.TreeFolder parentFolder = loadProjectAssetsMod;

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
                                            childFolder = parentFolder.addFolder(String.valueOf(parentFolder.children.size()), newRow, parent, loadProjectAssetsMod.size.y);
                                        }

                                        parentFolder = childFolder;
                                        path += parent + "/";
                                    }

                                    parentFolder.addItem(String.valueOf(parentFolder.children.size()), nrow, rowName, loadProjectAssetsMod.size.y);
                                }
                            }
                        }
                    }

                    LoadedData.shouldSetupList = true;
                }
            } catch(Exception ex) {
//                mainView.pushWarning("File Loading", ex.getMessage());
            }
            loadProjectAssetsMod = null;
        }

        if(replaceProjectItem != null)
        {
            try
            {
                String ext = (replaceProjectItem.itemName.getText().contains(".") ? replaceProjectItem.itemName.getText().substring(replaceProjectItem.itemName.getText().lastIndexOf(".") + 1) : null);

                ArrayList<FileNameExtensionFilter> extensions = new ArrayList<>();

                for(FileNameExtensionFilter filter : ALL_LBP_EXTENSIONS)
                    if(filter.getExtensions().length == 1)
                        for(String extension : filter.getExtensions())
                            if(ext.equalsIgnoreCase(extension))
                                extensions.add(filter);

                File item = openFiles(null, extensions.isEmpty() ? null : extensions.toArray(new FileNameExtensionFilter[0]), false, false)[0];
                if(item != null)
                {
                    FileDBRow row = ((FileDBRow)replaceProjectItem.item);
                    byte[] bytes = Files.readAllBytes(item.toPath());
                    row.setDetails(bytes);
                    LoadedData.PROJECT_DATA.add(bytes);

                    LoadedData.shouldSetupList = true;
                }
            } catch(Exception ex) {
//                mainView.pushWarning("File Loading", ex.getMessage());
            }

            replaceProjectItem = null;
        }

        if(loadProjectTextures)
        {
            try {
                File[] texs = openFiles(IMAGE_EXTENSIONS);

                if(texs != null && texs.length != 0)
                    for(File file : texs)
                    {
                        int ind = mainView.ProjectManager.textureListTexImport.elements.size();
                        BufferedImage img = ImageIO.read(file);
                        Texture texture = new Texture(img);
                        mainView.ProjectManager.listTexImport.add(texture);

                        ElementList list = mainView.ProjectManager.textureListTexImport;
                        Panel panel = list.addPanel(String.valueOf(ind));

                        float gap = 2 / list.size.x;

                        panel.elements.add(new Panel.PanelElement(new Image(new Vector2f(), null, mainView.renderer, mainView.loader, mainView.window) {
                            @Override
                            public Texture getImage() {
                                return texture;
                            }
                        }, 1f - 0.2f - gap * 2));

                        panel.elements.add(new Panel.PanelElement(null, gap));

                        ComboBoxImage optionsCombo = new ComboBoxImage("optionsCombo", new Vector2f(23, 23), mainView.renderer, mainView.loader, mainView.window) {
                            @Override
                            public Texture getImage() {
                                return ConstantTextures.getTexture(ConstantTextures.OPTIONS, 23, 23, loader);
                            }

                            @Override
                            public int[] getParentTransform() {
                                return list.getParentTransform();
                            }

                            @Override
                            public int tabWidth() {
                                return Math.round(200f * (getFontHeight() / 12f));
                            }
                        };

                        String fileName = file.getName();
                        if(fileName.contains("."))
                            fileName = fileName.substring(0, fileName.lastIndexOf("."));
                        fileName += ".tex";
                        optionsCombo.addTextbox("name", fileName);
                        optionsCombo.addButton("Normal2Bump", new Button() {
                            @Override
                            public void clickedButton(int button, int action, int mods) {

                                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS) {
                                    BufferedImage bump = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
                                    for (int x = 0; x < img.getWidth(); x++)
                                        for (int y = 0; y < img.getHeight(); y++) {
                                            int r = new Color(img.getRGB(x, y)).getRed();
                                            int g = new Color(img.getRGB(x, y)).getGreen();
                                            bump.setRGB(x, y, new Color(g, g, g, r).getRGB());
                                        }
                                    texture.image = bump;
                                    texture.cleanup();
                                }
                            }
                        });

                        panel.elements.add(new Panel.PanelElement(optionsCombo, 0.1f));

                        panel.elements.add(new Panel.PanelElement(null, gap));

                        panel.elements.add(new Panel.PanelElement(new ButtonImage("closeButton", new Vector2f(0), new Vector2f(22, 22), new Vector2f(23, 23), mainView.renderer, mainView.loader, mainView.window) {
                            @Override
                            public void clickedButton(int button, int action, int mods) {
                                mainView.ProjectManager.listTexImport.remove(texture);
                                texture.cleanup();
                                list.elements.remove(panel);
                            }

                            @Override
                            public Texture getImage() {
                                return ConstantTextures.getTexture(ConstantTextures.WINDOW_CLOSE, 23, 23, loader);
                            }
                        }, 0.1f));
                        panel.size.y = 60;
                    }

            } catch (Exception ex) {
                mainView.pushWarning("File Loading", ex.getMessage());
                print.stackTrace(ex);
            }
            loadProjectTextures = false;
        }

        if(extractFile != null)
        {
            byte[] data = LoadedData.PROJECT_DATA.extract(extractFile.getSHA1());

            if(data != null)
            {
                String ext = (extractFile.getName().contains(".") ? extractFile.getName().substring(extractFile.getName().lastIndexOf(".") + 1) : null);

                ArrayList<FileNameExtensionFilter> extensions = new ArrayList<>();

                if(ext != null)
                    for(FileNameExtensionFilter filter : ALL_LBP_EXTENSIONS)
                        if(filter.getExtensions().length == 1)
                            for(String extension : filter.getExtensions())
                                if(ext.equalsIgnoreCase(extension))
                                    extensions.add(filter);

                File file = saveFile(extractFile.getName(),  extensions.isEmpty() ? null : extensions.toArray(new FileNameExtensionFilter[0]));

                if(file != null)
                    try
                    {
                        Files.write(file.toPath(), data);
                    }catch (Exception e)
                    {
                        print.stackTrace(e);
                    }
            }

            extractFile = null;
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
    public static void saveMod()
    {
        saveMod = true;
    }

    public static void loadFart()
    {
        loadFart = true;
    }

    public static void selectProjectLocation()
    {
        selectProjectLocation = true;
    }
    public static void loadProjectAssets(String path, FileTree.TreeFolder treeFolder)
    {
        loadProjectAssetsPath = path;
        loadProjectAssetsFolder = treeFolder;
    }

    public static void loadProjectAssetsFromMod(FileTree.TreeFolder treeFolder)
    {
        loadProjectAssetsMod = treeFolder;
    }

    public static void replaceProjectItem(FileTree.TreeItem item)
    {
        replaceProjectItem = item;
    }

    public static void loadProjectTextures()
    {
        loadProjectTextures = true;
    }

    public static void extractItem(FileDBRow row)
    {
        extractFile = row;
    }

    static class PropertiesHandler
            extends AbstractPropertiesHandler {

        protected String getFilename() {
            return System.getProperty("user.home") + File.separator + "lbp_as_bookmarks.props";
        }
    }

    static class Factory
            extends DefaultFactory {

        public AbstractPropertiesHandler newPropertiesHandler() {
            return new PropertiesHandler();
        }
    }

    static class FileChooserBookmarksPanel
            extends AbstractBookmarksPanel {

        protected AbstractFactory newFactory() {
            return new Factory();
        }
    }

    static class ShortcutResolver {

        /**
         * shortcut parser written by Sam Brightman - https://stackoverflow.com/a/352738
         */

        public static File resolveShortcut(File f){

            try {
                FileInputStream fin = new FileInputStream(f);
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                byte[] buff = new byte[256];
                while (true) {
                    int n = fin.read(buff);
                    if (n == -1) {
                        break;
                    }
                    bout.write(buff, 0, n);
                }
                fin.close();
                byte[] link = bout.toByteArray();

                byte flags = link[0x14];

                final int file_atts_offset = 0x18;
                byte file_atts = link[file_atts_offset];
                byte is_dir_mask = (byte) 0x10;
                if ((file_atts & is_dir_mask) > 0) {
                    print.error("Shortcut links to a folder.");
                    return null;
                }

                final int shell_offset = 0x4c;
                final byte has_shell_mask = (byte) 0x01;
                int shell_len = 0;
                if ((flags & has_shell_mask) > 0)
                    shell_len = bytes2short(link, shell_offset) + 2;

                int file_start = 0x4c + shell_len;

                final int basename_offset_offset = 0x10;
                final int finalname_offset_offset = 0x18;
                int basename_offset = link[file_start + basename_offset_offset]
                        + file_start;
                int finalname_offset = link[file_start + finalname_offset_offset]
                        + file_start;
                String basename = getNullDelimitedString(link, basename_offset);
                String finalname = getNullDelimitedString(link, finalname_offset);
                return new File(basename + finalname);
            }
            catch (Exception e)
            {
                print.stackTrace(e);
            }
            return null;
        }

        private static String getNullDelimitedString(byte[] bytes, int off) {
            int len = 0;
            while (true) {
                if (bytes[off + len] == 0) {
                    break;
                }
                len++;
            }
            return new String(bytes, off, len);
        }

        private static int bytes2short(byte[] bytes, int off) {
            return ((bytes[off + 1] & 0xff) << 8) | (bytes[off] & 0xff);
        }
    }
}
