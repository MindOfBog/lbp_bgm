package bog.lbpas.view3d.mainWindow.screens;

import bog.lbpas.view3d.core.Texture;
import bog.lbpas.view3d.mainWindow.ConstantTextures;
import bog.lbpas.view3d.mainWindow.LoadedData;
import bog.lbpas.view3d.mainWindow.View3D;
import bog.lbpas.view3d.mainWindow.screens.thingPart.ThingPart;
import bog.lbpas.view3d.managers.MouseInput;
import bog.lbpas.view3d.renderer.gui.GuiScreen;
import bog.lbpas.view3d.renderer.gui.elements.Button;
import bog.lbpas.view3d.renderer.gui.elements.*;
import bog.lbpas.view3d.renderer.gui.elements.Checkbox;
import bog.lbpas.view3d.renderer.gui.elements.Panel;
import bog.lbpas.view3d.utils.Consts;
import bog.lbpas.view3d.utils.FilePicker;
import bog.lbpas.view3d.utils.Utils;
import bog.lbpas.view3d.utils.print;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import cwlib.enums.*;
import cwlib.io.serializer.SerializationData;
import cwlib.resources.RLevel;
import cwlib.resources.RPlan;
import cwlib.structs.inventory.CreationHistory;
import cwlib.structs.inventory.InventoryItemDetails;
import cwlib.structs.inventory.UserCreatedDetails;
import cwlib.structs.things.Thing;
import cwlib.structs.things.components.script.FieldLayoutDetails;
import cwlib.structs.things.components.script.InstanceLayout;
import cwlib.structs.things.components.script.ScriptInstance;
import cwlib.structs.things.components.script.ScriptObject;
import cwlib.structs.things.parts.*;
import cwlib.types.Resource;
import cwlib.types.archives.FileArchive;
import cwlib.types.data.*;
import cwlib.types.databases.FileDB;
import cwlib.types.databases.FileDBRow;
import cwlib.types.save.BigSave;
import cwlib.util.Colors;
import cwlib.util.GsonUtils;
import cwlib.util.Images;
import gr.zdimensions.jsquish.Squish;
import org.joml.*;
import org.lwjgl.glfw.GLFW;
import toolkit.utilities.FileChooser;

import javax.activation.DataHandler;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.Math;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author Bog
 */
public class ProjectManager extends GuiScreen {

    View3D mainView;

    public ProjectManager(View3D mainView)
    {
        super(mainView.renderer, mainView.loader, mainView.window);
        this.mainView = mainView;
        init();
    }

    public DropDownTab project;
    public FileTree modFileTree;
    public Textbox projectSavePath;
    public ComboBox buildScene;
    public ComboBox planExport;
    Textbox userCreatedNamePlan;
    Textarea userCreatedDescriptionPlan;
    Textbox titleKeyPlan;
    Textbox descriptionKeyPlan;
    Textbox creatorPlan;
    ComboBox creatorHistoryPlan;
    Textbox creatorHistoryAddPlan;
    ArrayList<String> creatorHistoryListPlan;
    Textbox iconPlan;
    Checkbox allowEmitPlan;
    Checkbox shareablePlan;
    Checkbox copyrightPlan;
    Textbox categoryPlan;
    Textbox categoryTagPlan;
    Textbox categoryIndexPlan;
    Textbox locationPlan;
    Textbox locationTagPlan;
    Textbox locationIndexPlan;
    ComboBox toolTypePlan;
    ToolType toolTypePln = ToolType.NONE;
    Checkbox used;
    Checkbox hiddenItem;
    Checkbox restrictedLevel;
    Checkbox restrictedPod;
    Checkbox disableLoopPreview;
    ArrayList<Checkbox> typePlan;
    Radiobutton madeByAnyRBPlan;
    Radiobutton madeByOthersRBPlan;
    Radiobutton madeByMeRBPlan;

    Radiobutton earthRBPlan;
    Radiobutton moonRBPlan;
    Radiobutton adventureRBPlan;
    Radiobutton externalRBPlan;

    Radiobutton nonSackboyRBPlan;
    Radiobutton giantRBPlan;
    Radiobutton dwarfRBPlan;
    Radiobutton birdRBPlan;
    Radiobutton quadRBPlan;
    ElementList subTypeList;
    Textbox numUsesPlan;
    Textbox lastUsedPlan;
    Textbox fluffCostPlan;
    Checkbox makeSizeProportionalPlan;
    Textbox primaryIndexPlan;
    Textbox translationTagPlan;
    Textbox rColorPlan;
    Textbox gColorPlan;
    Textbox bColorPlan;
    Textbox aColorPlan;
    Textbox highlightSoundPlan;
    Textbox dateAddedDayPlan;
    Textbox dateAddedMonthPlan;
    Textbox dateAddedYearPlan;
    Textbox dateAddedHourPlan;
    Textbox dateAddedMinutePlan;
    Textbox dateAddedSecondPlan;

    public Checkbox compressionFlagsIntPlan;
    public Checkbox compressionFlagsVecPlan;
    public Checkbox compressionFlagsMatPlan;
    public Checkbox selectionOnlyExportPlan;

    public Textbox customRevisionPlanHead;
    public Textbox customRevisionPlanID;
    public Textbox customRevisionPlanRev;
    Button customRevisionPlanExport;

    public ComboBox binExport;
    public Checkbox compressionFlagsIntBin;
    public Checkbox compressionFlagsVecBin;
    public Checkbox compressionFlagsMatBin;

    public Textbox customRevisionBinHead;
    public Textbox customRevisionBinID;
    public Textbox customRevisionBinRev;
    Button customRevisionBinExport;
    public Checkbox selectionOnlyExportBin;

    Thing worldThing;
    ArrayList<bog.lbpas.view3d.core.types.Thing> worldThingA;
    ThingPart thingPart;

    Checkbox isBumpTexImport;
    Checkbox mipmapsTexImport;
    Checkbox gtfTexImport;
    int formatTexImport = 2;
    public ArrayList<Texture> listTexImport;
    public ElementList textureListTexImport;

    public void init()
    {
        worldThing = new Thing();
        worldThing.UID = -1;

        presetLevel();

        worldThingA = new ArrayList<>();
        worldThingA.add(new bog.lbpas.view3d.core.types.Thing(worldThing, null));
        worldThingA.get(0).selected = true;

        typePlan = new ArrayList<>();
        creatorHistoryListPlan = new ArrayList<>();

        project = new DropDownTab("project", "Project", new Vector2f(10, 21 + 10), new Vector2f(400, getFontHeight(10) + 4), 10, mainView.renderer, mainView.loader, mainView.window);

        ElementList projectFileTree = project.addElementList("projectFileTree", 300);

        buildScene = new ComboBox("buildScene", "Build Scene", new Vector2f(), null, 10, 100, renderer, loader, window);

        ComboBox importTextures = new ComboBox("texture", "Texture(s)", new Vector2f(), null, 10, 250, renderer, loader, window);

        importTextures.addButton("open", "Open", new Button() {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_RELEASE)
                    FilePicker.loadProjectTextures();
            }
        });

        textureListTexImport = importTextures.addElementList("textureListTexImport", 200);
        listTexImport = new ArrayList<>();

        Panel formatPanel = importTextures.addPanel("formatPanel");
        formatPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("formatStr", "Format:", 10, renderer), 0.4f));
        ComboBox imgFormat = new ComboBox("formatCombo", "DXT5", 10, 100, renderer, loader, window);
        formatPanel.elements.add(new Panel.PanelElement(imgFormat, 0.6f));
        imgFormat.addButton("DXT1", new Button() {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)
                {
                    formatTexImport = 0;
                    imgFormat.tabTitle = "DXT1";
                }
            }
        });
        imgFormat.addButton("DXT3", new Button() {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)
                {
                    formatTexImport = 1;
                    imgFormat.tabTitle = "DXT3";
                }
            }
        });
        imgFormat.addButton("DXT5", new Button() {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)
                {
                    formatTexImport = 2;
                    imgFormat.tabTitle = "DXT5";
                }
            }
        });

        isBumpTexImport = importTextures.addCheckbox("isBump", "Is Bump (noSRGB)");
        mipmapsTexImport= importTextures.addCheckbox("mipmaps", "Mipmaps").checked();

        importTextures.addSeparator("sep2");

        gtfTexImport = importTextures.addCheckbox("gtf", "GTF Texture (LBP 2/3)");

        importTextures.addButton("buildTex", "Build", new Button() {
            @Override
            public void clickedButton(int button, int action, int mods) {

                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)
                {
                    for(int i = 0; i < listTexImport.size(); i++)
                    {
                        Texture texture = listTexImport.get(i);
                        Panel texPanel = (Panel) textureListTexImport.elements.get(i);
                        ComboBoxImage optionsCombo = (ComboBoxImage) texPanel.elements.get(2).element;
                        Textbox fileName = (Textbox) optionsCombo.comboElements.get(0);

                        if(texture.image == null)
                            continue;

                        boolean noSRGB = isBumpTexImport.isChecked;
                        boolean genMips = mipmapsTexImport.isChecked;
                        boolean useGTF = gtfTexImport.isChecked;

                        Squish.CompressionType type = null;
                        switch (formatTexImport) {
                            case 0: type = Squish.CompressionType.DXT1; break;
                            case 1: type = Squish.CompressionType.DXT3; break;
                            case 2: type = Squish.CompressionType.DXT5; break;
                        }

                        if (useGTF)
                        {
                            byte[] imageData = Images.toGTF(texture.image, type, noSRGB, genMips);

                            FileDBRow row = LoadedData.PROJECT_DATA.add(modFileTree.getPath(modFileTree.getSelectedItem()) + "/" + fileName.getText(), imageData);
                            FileTree.TreeItem newItem = ((FileTree.TreeFolder)modFileTree.getSelectedItem()).addItem(String.valueOf(((FileTree.TreeFolder)modFileTree.getSelectedItem()).children.size()), row, fileName.getText(), ((FileTree.TreeFolder)modFileTree.getSelectedItem()).size.y);
                        }
                        else
                        {
                            byte[] imageData = Images.toTEX(texture.image, type, noSRGB, genMips);

                            FileDBRow row = LoadedData.PROJECT_DATA.add(modFileTree.getPath(modFileTree.getSelectedItem()) + "/" + fileName.getText(), imageData);
                            FileTree.TreeItem newItem = ((FileTree.TreeFolder)modFileTree.getSelectedItem()).addItem(String.valueOf(((FileTree.TreeFolder)modFileTree.getSelectedItem()).children.size()), row, fileName.getText(), ((FileTree.TreeFolder)modFileTree.getSelectedItem()).size.y);
                        }
                    }

                    listTexImport.clear();
                    textureListTexImport.elements.clear();
                }

            }
        });

        modFileTree = projectFileTree.addFileTree(new FileTree("modFileTree", "Some kind of mod", getFontHeight(10) * 1.25f + 2, new Vector2f(), new Vector2f(), 10, renderer, loader, window) {
            @Override
            public int[] getParentTransform() {
                return new int[]{(int) Math.round(project.pos.x), (int) Math.round(project.pos.y), (int) Math.round(project.size.x)};
            }

            @Override
            public void rename(TreeItem treeItem) {

                if(treeItem.item == null)
                    return;

                String path = getPath(treeItem);
                ((FileDBRow) treeItem.item).setPath(path);

                if(treeItem instanceof TreeFolder)
                    renameChildren(treeItem);

                LoadedData.shouldSetupList = true;
            }

            private void renameChildren(TreeItem treeItem)
            {
                TreeFolder folderItem = (TreeFolder) treeItem;
                for(TreeItem child : folderItem.children)
                {
                    String path = getPath(child);
                    ((FileDBRow) child.item).setPath(path);

                    if(child instanceof TreeFolder)
                        renameChildren(child);
                }
            }

            @Override
            public void delete(TreeItem treeItem) {
                if(treeItem instanceof TreeFolder)
                    deleteChildren(treeItem);

                treeItem.parent.children.remove(treeItem);
                LoadedData.PROJECT_DATA.entries.remove(treeItem.item);

                LoadedData.shouldSetupList = true;
            }

            private void deleteChildren(TreeItem treeItem)
            {
                TreeFolder folderItem = (TreeFolder) treeItem;
                for(TreeItem child : folderItem.children)
                {
                    if(child instanceof TreeFolder)
                        deleteChildren(child);

                    LoadedData.PROJECT_DATA.remove((FileDBRow) child.item);
                }
            }

            @Override
            public void copy(TreeItem treeItem) {
                FileDBRow row = (FileDBRow) treeItem.item;

                if(row == null || row.getSHA1() == null || !LoadedData.PROJECT_DATA.archive.exists(row.getSHA1()))
                    return;

                byte[] bytes = LoadedData.PROJECT_DATA.archive.extract(row.getSHA1());

                if(bytes == null)
                    return;

                try {
                    FileClipboard dataPackage = new FileClipboard(treeItem.itemName.getText(), bytes);
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(new DataPackageTransferable(dataPackage), null);
                }catch (Exception e){print.stackTrace(e);}
            }

            @Override
            public void replace(TreeItem treeItem) {
                FilePicker.replaceProjectItem(treeItem);
            }

            @Override
            public void paste(TreeFolder treeFolder) {

                String name = "";
                byte[] bytes = new byte[]{};
                try
                {
                    Clipboard clipbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
                    Transferable transferable = clipbrd.getContents(null);
                    if (transferable.isDataFlavorSupported(DataPackageTransferable.DATA_FLAVOR)) {
                        FileClipboard retrieved = (FileClipboard) transferable.getTransferData(DataPackageTransferable.DATA_FLAVOR);
                        name = retrieved.name;
                        bytes = retrieved.bytes;
                    }
                }catch (Exception e){}

                if(name == null || name.isEmpty() || name.isBlank())
                    return;

                String path = getPath(treeFolder);
                FileDBRow row = LoadedData.PROJECT_DATA.add(((path == null || path.isEmpty() || path.isBlank()) ? "" : path + "/") + name, bytes);
                FileTree.TreeItem newItem = treeFolder.addItem(String.valueOf(treeFolder.children.size()), row, name, this.itemHeight);

            }

            @Override
            public void newFolder(TreeFolder treeFolder) {
                String path = getPath(treeFolder);
                FileDBRow row = LoadedData.PROJECT_DATA.newFileDBRow(((path == null || path.isEmpty() || path.isBlank()) ? "" : path + "/"));

                TreeFolder newFolder = treeFolder.addFolder(String.valueOf(treeFolder.children.size()), row, "", this.itemHeight);
                newFolder.itemName.setFocused(true);
                treeFolder.selected = false;
                newFolder.selected = true;
            }

            @Override
            public void addOptionsItem(ComboBox comboBox, TreeItem treeItem) {
                comboBox.addComboBox("mod", "Modify", 10);
                comboBox.addSeparator("sep");
                super.addOptionsItem(comboBox, treeItem);
            }

            @Override
            public void addOptionsFolder(ComboBox comboBox, TreeFolder folder) {
                comboBox.addButton("load", "Load Asset(s)", new Button() {
                    @Override
                    public void clickedButton(int button, int action, int mods) {
                        if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)
                        {
                            String path = getPath(folder);
                            FilePicker.loadProjectAssets(((path == null || path.isEmpty() || path.isBlank()) ? "" : path + "/"), folder);
                        }
                    }
                });
                comboBox.comboElements.add(buildScene);
                ComboBox importCombo = comboBox.addComboBox("import", "Import", 200);

                importCombo.comboElements.add(importTextures);

                importCombo.addButton("model", "Model", new Button() {
                    @Override
                    public void clickedButton(int button, int action, int mods) {
                        if(action == GLFW.GLFW_RELEASE)
                            mainView.pushWarning("Trolled", "we not there yet twin...\nI'm lazy");
                    }
                });

                comboBox.addSeparator("sep");
                super.addOptionsFolder(comboBox, folder);
            }

            @Override
            public Texture getRootIcon(String itemName, boolean extended, Vector2f size)
            {
                int s = org.joml.Math.round(size.y);
                return ConstantTextures.getTexture(extended ? ConstantTextures.ICON_GROUP_GEAR_OPEN : ConstantTextures.ICON_GROUP_GEAR, s, s, loader);
            }
            @Override
            public Texture getItemIcon(String itemName, Vector2f size) {
                int s = org.joml.Math.round(size.y);

                int ind = itemName.lastIndexOf(".");

                if(ind != -1)
                switch(itemName.toLowerCase().substring(ind + 1))
                {
                    case "bev":
                        return ConstantTextures.getTexture(ConstantTextures.FILE_BEVEL, s, s, loader);
                    case "gmat":
                    case "gmt":
                        return ConstantTextures.getTexture(ConstantTextures.FILE_GFX_MATERIAL, s, s, loader);
                    case "mat":
                        return ConstantTextures.getTexture(ConstantTextures.FILE_MATERIAL, s, s, loader);
                    case "mol":
                    case "smh":
                        return ConstantTextures.getTexture(ConstantTextures.FILE_MODEL, s, s, loader);
                    case "plan":
                    case "pln":
                        return ConstantTextures.getTexture(ConstantTextures.FILE_PLAN, s, s, loader);
                    case "ff":
                        return ConstantTextures.getTexture(ConstantTextures.FILE_SCRIPT, s, s, loader);
                    case "sph":
                        return ConstantTextures.getTexture(ConstantTextures.FILE_SOFTBODY, s, s, loader);
                    case "tex":
                        return ConstantTextures.getTexture(ConstantTextures.FILE_TEXTURE, s, s, loader);
                }

                return ConstantTextures.getTexture(ConstantTextures.FILE_UNKNOWN, s, s, loader);
            }
        });

        Button exportMod = project.addButton("Export .MOD", new Button() {
            @Override
            public void clickedButton(int button, int action, int mods) {

                for(int i = LoadedData.PROJECT_DATA.entries.size() - 1; i >= 0; i--)
                    if(!LoadedData.PROJECT_DATA.archive.exists(LoadedData.PROJECT_DATA.entries.get(i).getSHA1()))
                        LoadedData.PROJECT_DATA.entries.remove(i);

                FilePicker.saveMod();
            }
        });
        exportMod.size.y = getFontHeight(10) * 2f;

        project.addSeparator("sep");
        Panel projectSavePathPanel = project.addPanel("projectSavePathPanel");
        projectSavePathPanel.elements.add(new Panel.PanelElement(
                new DropDownTab.StringElement("projectSavePathStr", "Project Path:", 10, mainView.renderer),
                0.3f));
        projectSavePath = new Textbox("projectSavePath", new Vector2f(), new Vector2f(getFontHeight(10)), 10, mainView.renderer, mainView.loader, mainView.window);

        try {
            projectSavePath.setText(Paths.get(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).getParent().toString() + File.separator + "Some kind of project.jar");
        } catch (URISyntaxException e) {print.stackTrace(e);}

        float panelWidth = org.joml.Math.floor(project.size.x - 5f);
        float fileBrowseButton = projectSavePathPanel.size.y / panelWidth;
        float spacer = 1f / panelWidth;
        float leftSpace = 0.7f - (fileBrowseButton + spacer);

        projectSavePathPanel.elements.add(new Panel.PanelElement(projectSavePath, leftSpace));
        projectSavePathPanel.elements.add(new Panel.PanelElement(null, spacer));
        ButtonImage browseProjectSave = new ButtonImage("", mainView.renderer, mainView.loader, mainView.window) {
            @Override
            public void clickedButton(int button, int action, int mods) {
                FilePicker.selectProjectLocation();
            }

            @Override
            public Texture getImage() {
                return ConstantTextures.getTexture(ConstantTextures.ICON_GROUP, Math.round(this.imageSize.x), Math.round(this.imageSize.y), loader);
            }
        };
        projectSavePathPanel.elements.add(new Panel.PanelElement(browseProjectSave, fileBrowseButton));

        Button saveProject = project.addButton("Save Project", new Button() {
            @Override
            public void clickedButton(int button, int action, int mods) {

                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS) {

                    String projectPath = projectSavePath.getText();
                    if (!projectPath.endsWith(".jar"))
                        projectPath += ".jar";

                    JsonObject jsonFile = new JsonObject();

                    JsonArray camPosRot = new JsonArray();
                    camPosRot.add(mainView.camera.getPos().x);
                    camPosRot.add(mainView.camera.getPos().y);
                    camPosRot.add(mainView.camera.getPos().z);
                    camPosRot.add(mainView.camera.getRotation().x);
                    camPosRot.add(mainView.camera.getRotation().y);
                    camPosRot.add(mainView.camera.getRotation().z);

                    jsonFile.add("cameraPosRot", camPosRot);

                    JsonObject scene = new JsonObject();

                    {
                        scene.addProperty("loadedEntitiesSearch", mainView.ElementEditing.loadedEntitiesSearch.getText());
                    }
                    {
                        DropDownTab tab = mainView.ElementEditing.camPos;
                        JsonObject tabData = new JsonObject();
                        JsonArray tabPos = new JsonArray();
                        tabPos.add(tab.pos.x);
                        tabPos.add(tab.pos.y);
                        tabData.add("tabPos", tabPos);
                        tabData.addProperty("tabExtended", tab.extended);
                        scene.add("tabCamPos", tabData);
                    }
                    {
                        DropDownTab tab = mainView.ElementEditing.helpers;
                        JsonObject tabData = new JsonObject();
                        JsonArray tabPos = new JsonArray();
                        tabPos.add(tab.pos.x);
                        tabPos.add(tab.pos.y);
                        tabData.add("tabPos", tabPos);
                        tabData.addProperty("tabExtended", tab.extended);
                        scene.add("tabHelpers", tabData);
                    }
                    {
                        DropDownTab tab = mainView.ElementEditing.availableAssets;
                        JsonObject tabData = new JsonObject();
                        JsonArray tabPos = new JsonArray();
                        tabPos.add(tab.pos.x);
                        tabPos.add(tab.pos.y);
                        tabData.add("tabPos", tabPos);
                        tabData.addProperty("tabExtended", tab.extended);

                        tabData.addProperty("assetSearch", mainView.ElementEditing.availableAssetsSearch.getText());

                        JsonArray filters = new JsonArray();
                        filters.add(mainView.ElementEditing.filterPlans.isChecked);
                        filters.add(mainView.ElementEditing.filterLevels.isChecked);
                        filters.add(mainView.ElementEditing.filterMeshes.isChecked);
                        filters.add(mainView.ElementEditing.filterMaterials.isChecked);
                        tabData.add("assetFilter", filters);

                        scene.add("tabAssets", tabData);
                    }
                    {
                        DropDownTab tab = mainView.ElementEditing.currentSelection;
                        JsonObject tabData = new JsonObject();
                        JsonArray tabPos = new JsonArray();
                        tabPos.add(tab.pos.x);
                        tabPos.add(tab.pos.y);
                        tabData.add("tabPos", tabPos);
                        tabData.addProperty("tabExtended", tab.extended);
                        scene.add("tabCurrentSelection", tabData);
                    }

                    jsonFile.add("scene", scene);

                    JsonObject archive = new JsonObject();

                    {
                        JsonObject archivePaths = new JsonObject();

                        JsonArray map = new JsonArray();
                        for (FileDB m : LoadedData.MAPs)
                            map.add(m.getFile().toPath().toString());
                        archivePaths.add("map", map);

                        JsonArray farc = new JsonArray();
                        for (FileArchive f : LoadedData.FARCs)
                            farc.add(f.getFile().toPath().toString());
                        archivePaths.add("farc", farc);

                        JsonArray fart = new JsonArray();
                        for (BigSave f : LoadedData.BIGFARTs)
                            fart.add(f.getFile().toPath().toString());
                        archivePaths.add("fart", fart);

                        JsonObject translation = new JsonObject();
                        translation.addProperty("main", LoadedData.loadedTranslation);
                        translation.addProperty("patch", LoadedData.loadedPatchTranslation);
                        archivePaths.add("translation", translation);

                        archive.add("archivePaths", archivePaths);
                    }
                    {
                        DropDownTab tab = mainView.Archive.mapList;
                        JsonObject tabData = new JsonObject();
                        JsonArray tabPos = new JsonArray();
                        tabPos.add(tab.pos.x);
                        tabPos.add(tab.pos.y);
                        tabData.add("tabPos", tabPos);
                        tabData.addProperty("tabExtended", tab.extended);
                        archive.add("tabMaps", tabData);
                    }
                    {
                        DropDownTab tab = mainView.Archive.farcList;
                        JsonObject tabData = new JsonObject();
                        JsonArray tabPos = new JsonArray();
                        tabPos.add(tab.pos.x);
                        tabPos.add(tab.pos.y);
                        tabData.add("tabPos", tabPos);
                        tabData.addProperty("tabExtended", tab.extended);
                        archive.add("tabFarcs", tabData);
                    }
                    {
                        DropDownTab tab = mainView.Archive.fartList;
                        JsonObject tabData = new JsonObject();
                        JsonArray tabPos = new JsonArray();
                        tabPos.add(tab.pos.x);
                        tabPos.add(tab.pos.y);
                        tabData.add("tabPos", tabPos);
                        tabData.addProperty("tabExtended", tab.extended);
                        archive.add("tabFarts", tabData);
                    }
                    {
                        DropDownTab tab = mainView.Archive.translations;
                        JsonObject tabData = new JsonObject();
                        JsonArray tabPos = new JsonArray();
                        tabPos.add(tab.pos.x);
                        tabPos.add(tab.pos.y);
                        tabData.add("tabPos", tabPos);
                        tabData.addProperty("tabExtended", tab.extended);
                        tabData.addProperty("search", mainView.Archive.searchTransl.getText());
                        archive.add("tabTranslations", tabData);
                    }

                    jsonFile.add("archive", archive);

                    JsonObject project = new JsonObject();

                    {
                        DropDownTab tab = mainView.ProjectManager.project;
                        JsonObject tabData = new JsonObject();
                        JsonArray tabPos = new JsonArray();
                        tabPos.add(tab.pos.x);
                        tabPos.add(tab.pos.y);
                        tabData.add("tabPos", tabPos);
                        tabData.addProperty("tabExtended", tab.extended);
                        project.add("tabProject", tabData);
                    }
                    {
                        JsonObject plan = new JsonObject();
                        plan.addProperty("extended", mainView.ProjectManager.planExport.extended);
                        plan.addProperty("selection", mainView.ProjectManager.selectionOnlyExportPlan.isChecked);
                        plan.addProperty("ints", mainView.ProjectManager.compressionFlagsIntPlan.isChecked);
                        plan.addProperty("vecs", mainView.ProjectManager.compressionFlagsVecPlan.isChecked);
                        plan.addProperty("mats", mainView.ProjectManager.compressionFlagsMatPlan.isChecked);
                        plan.addProperty("head", mainView.ProjectManager.customRevisionPlanHead.getText());
                        plan.addProperty("id", mainView.ProjectManager.customRevisionPlanID.getText());
                        plan.addProperty("rev", mainView.ProjectManager.customRevisionPlanRev.getText());

                        project.add("plan", plan);
                    }
                    {
                        JsonObject bin = new JsonObject();
                        bin.addProperty("extended", mainView.ProjectManager.binExport.extended);
                        bin.addProperty("selection", mainView.ProjectManager.selectionOnlyExportBin.isChecked);
                        bin.addProperty("ints", mainView.ProjectManager.compressionFlagsIntBin.isChecked);
                        bin.addProperty("vecs", mainView.ProjectManager.compressionFlagsVecBin.isChecked);
                        bin.addProperty("mats", mainView.ProjectManager.compressionFlagsMatBin.isChecked);
                        bin.addProperty("head", mainView.ProjectManager.customRevisionBinHead.getText());
                        bin.addProperty("id", mainView.ProjectManager.customRevisionBinID.getText());
                        bin.addProperty("rev", mainView.ProjectManager.customRevisionBinRev.getText());

                        project.add("bin", bin);
                    }

                    jsonFile.add("project", project);

                    byte[] sceneBuilderPlan = Resource.compress(buildPlan(new Revision(Branch.MIZUKI.getHead(), Branch.MIZUKI.getID(), Branch.MIZUKI.getRevision()), false).build());
                    byte[] sceneBuilderBin = Resource.compress(buildBin(new Revision(Branch.MIZUKI.getHead(), Branch.MIZUKI.getID(), Branch.MIZUKI.getRevision()), false));
                    byte[] dataMap = LoadedData.PROJECT_DATA.build();
                    byte[] dataFarc = LoadedData.PROJECT_DATA.archive.build(false);

                    byte[] worldPlan = Resource.compress(buildPlan(new Revision(Branch.MIZUKI.getHead(), Branch.MIZUKI.getID(), Branch.MIZUKI.getRevision()), true).build());
                    JsonElement element = GsonUtils.GSON.toJsonTree(mainView.selectedThingsBuiltThingArray);
                    jsonFile.add("selectedThings", element.getAsJsonArray());

                    byte[] dataJson = jsonFile.toString().getBytes();

                    try (InputStream resourceStream = this.getClass().getResourceAsStream("/projectLoader/project_loader.jar");
                         ZipInputStream zipInputStream = new ZipInputStream(resourceStream);
                         ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(Path.of(projectPath)))) {

                        ZipEntry entry;
                        while ((entry = zipInputStream.getNextEntry()) != null) {
                            zipOutputStream.putNextEntry(new ZipEntry(entry.getName()));
                            byte[] buffer = new byte[1024];
                            int bytesRead;
                            while ((bytesRead = zipInputStream.read(buffer)) != -1) {
                                zipOutputStream.write(buffer, 0, bytesRead);
                            }
                            zipOutputStream.closeEntry();
                        }

                        JsonObject launcherJson = new JsonObject();
                        launcherJson.addProperty("programJar", Paths.get(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).toString());

                        zipOutputStream.putNextEntry(new ZipEntry("launcher.json"));
                        zipOutputStream.write(launcherJson.toString().getBytes());
                        zipOutputStream.closeEntry();

                        zipOutputStream.putNextEntry(new ZipEntry("data.json"));
                        zipOutputStream.write(dataJson);
                        zipOutputStream.closeEntry();

                        zipOutputStream.putNextEntry(new ZipEntry("sceneBuilder.plan"));
                        zipOutputStream.write(sceneBuilderPlan);
                        zipOutputStream.closeEntry();

                        zipOutputStream.putNextEntry(new ZipEntry("sceneBuilder.bin"));
                        zipOutputStream.write(sceneBuilderBin);
                        zipOutputStream.closeEntry();

                        zipOutputStream.putNextEntry(new ZipEntry("data.map"));
                        zipOutputStream.write(dataMap);
                        zipOutputStream.closeEntry();

                        zipOutputStream.putNextEntry(new ZipEntry("data.farc"));
                        zipOutputStream.write(dataFarc);
                        zipOutputStream.closeEntry();

                        zipOutputStream.putNextEntry(new ZipEntry("world.plan"));
                        zipOutputStream.write(worldPlan);
                        zipOutputStream.closeEntry();

                    } catch (Exception e) {
                        print.stackTrace(e);
                        mainView.pushError("Saving Project Error", e.getLocalizedMessage());
                    }

                    mainView.pushSuccess("Project saved!", "Successfully saved \"" + Path.of(projectPath).getFileName() + "\"");
                }
            }
        });
        saveProject.size.y = getFontHeight(10) * 2f;
        planExport = buildScene.addComboBox("planExport", "Plan", 450);

        Panel userCreatedNamePlanPanel = planExport.addPanel("userCreatedNamePlanPanel");
        userCreatedNamePlanPanel.elements.add(new Panel.PanelElement(
                new DropDownTab.StringElement("userCreatedNamePlanStr", "User Created Name:", 10, mainView.renderer),
                0.4f));
        userCreatedNamePlan = new Textbox("userCreatedNamePlan", new Vector2f(), new Vector2f(getFontHeight(10)), 10, mainView.renderer, mainView.loader, mainView.window);
        userCreatedNamePlan.setText("Some kind of object");
        userCreatedNamePlanPanel.elements.add(new Panel.PanelElement(userCreatedNamePlan, 0.6f));

        Panel userCreatedDescriptionPlanPanel = planExport.addPanel("userCreatedDescriptionPlanPanel");
        userCreatedDescriptionPlanPanel.size.y = 100;
        userCreatedDescriptionPlanPanel.elements.add(new Panel.PanelElement(
                new DropDownTab.StringElement("userCreatedDescriptionPlanStr", "User Created Desc.:", 10, mainView.renderer),
                0.4f));
        userCreatedDescriptionPlan = new Textarea("userCreatedDescriptionPlan", new Vector2f(), new Vector2f(getFontHeight(10)), 10, mainView.renderer, mainView.loader, mainView.window);
        userCreatedDescriptionPlanPanel.elements.add(new Panel.PanelElement(userCreatedDescriptionPlan, 0.6f));

        Panel titleKeyPlanPanel = planExport.addPanel("titleKeyPlanPanel");
        titleKeyPlanPanel.elements.add(new Panel.PanelElement(
                new DropDownTab.StringElement("titleKeyPlanStr", "Title Key:", 10, mainView.renderer),
                0.4f));
        titleKeyPlan = new Textbox("titleKeyPlan", new Vector2f(), new Vector2f(getFontHeight(10)), 10, mainView.renderer, mainView.loader, mainView.window).noLetters().noOthers();
        titleKeyPlanPanel.elements.add(new Panel.PanelElement(titleKeyPlan, 0.6f));

        Panel descriptionKeyPlanPanel = planExport.addPanel("descriptionKeyPlanPanel");
        descriptionKeyPlanPanel.elements.add(new Panel.PanelElement(
                new DropDownTab.StringElement("descriptionKeyPlanStr", "Description Key:", 10, mainView.renderer),
                0.4f));
        descriptionKeyPlan = new Textbox("descriptionKeyPlan", new Vector2f(), new Vector2f(getFontHeight(10)), 10, mainView.renderer, mainView.loader, mainView.window).noLetters().noOthers();
        descriptionKeyPlanPanel.elements.add(new Panel.PanelElement(descriptionKeyPlan, 0.6f));

        Panel creatorPlanPanel = planExport.addPanel("creatorPlanPanel");
        creatorPlanPanel.elements.add(new Panel.PanelElement(
                new DropDownTab.StringElement("creatorPlanStr", "Creator:", 10, mainView.renderer),
                0.4f));
        creatorPlan = new Textbox("creatorPlan", new Vector2f(), new Vector2f(getFontHeight(10)), 10, mainView.renderer, mainView.loader, mainView.window);
        creatorPlan.setText("MM_Studio");
        creatorPlanPanel.elements.add(new Panel.PanelElement(creatorPlan, 0.4f));
        creatorHistoryPlan = new ComboBox("creatorHistoryPlan", "History", new Vector2f(), new Vector2f(), 10, 250, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public int[] getParentTransform() {
                return planExport.getTabPosWidth();
            }
        };
        creatorPlanPanel.elements.add(new Panel.PanelElement(null, 0.002f));
        creatorPlanPanel.elements.add(new Panel.PanelElement(creatorHistoryPlan, 0.198f));

        Panel addCreatorPanel = creatorHistoryPlan.addPanel("addCreatorPanel");
        creatorHistoryAddPlan = new Textbox("creatorHistoryAddPlan", new Vector2f(), new Vector2f(getFontHeight(10)), 10, mainView.renderer, mainView.loader, mainView.window);
        addCreatorPanel.elements.add(new Panel.PanelElement(creatorHistoryAddPlan, 0.8f));
        addCreatorPanel.elements.add(new Panel.PanelElement(null, 0.01f));
        addCreatorPanel.elements.add(new Panel.PanelElement(new Button("creatorHistoryAddButtonPlan", "Add", new Vector2f(), new Vector2f(getFontHeight(10)), 10, mainView.renderer, mainView.loader, mainView.window) {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && creatorHistoryAddPlan.getText() != null)
                    creatorHistoryListPlan.add(creatorHistoryAddPlan.getText());
                print.warning(creatorHistoryListPlan.size());
            }
        }, 0.19f));

        creatorHistoryPlan.addList("creatorHistoryList", new ButtonList("creatorHistoryList", creatorHistoryListPlan, new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window) {
            @Override
            public void clickedButton(Object object, int index, int button, int action, int mods) {
            }

            @Override
            public void hoveringButton(Object object, int index) {

            }

            @Override
            public boolean isHighlighted(Object object, int index) {
                return false;
            }

            @Override
            public boolean isSelected(Object object, int index) {
                return false;
            }

            @Override
            public String buttonText(Object object, int index) {
                return (String)object;
            }

            @Override
            public boolean searchFilter(Object object, int index) {
                return true;
            }
        }.deletable().draggable(), 150);

        Panel iconPlanPanel = planExport.addPanel("iconPlanPanel");
        iconPlanPanel.elements.add(new Panel.PanelElement(
                new DropDownTab.StringElement("iconPlanStr", "Icon:", 10, mainView.renderer),
                0.4f));
        iconPlan = new Textbox("iconPlan", new Vector2f(), new Vector2f(getFontHeight(10)), 10, mainView.renderer, mainView.loader, mainView.window);
        iconPlan.setText("2551");
        iconPlanPanel.elements.add(new Panel.PanelElement(iconPlan, 0.6f));

        Panel shareablePlanPanel = planExport.addPanel("shareablePlanPanel");
        allowEmitPlan = new Checkbox("allowEmitPlan", "Allow Emit", new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
        allowEmitPlan.isChecked = true;
        shareablePlan = new Checkbox("shareablePlan", "Shareable", new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
        shareablePlan.isChecked = true;
        copyrightPlan = new Checkbox("copyrightPlan", "Copyright", new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
        shareablePlanPanel.elements.add(new Panel.PanelElement(allowEmitPlan, 1f/3f));
        shareablePlanPanel.elements.add(new Panel.PanelElement(shareablePlan, 1f/3f));
        shareablePlanPanel.elements.add(new Panel.PanelElement(copyrightPlan, 1f/3f));

        Panel categoryPanel = planExport.addPanel("categoryPanel");
        categoryPanel.elements.add(new Panel.PanelElement(
                new DropDownTab.StringElement("categoryStr", "Category:", 10, mainView.renderer),
                0.4f));
        categoryPlan = new Textbox("category", new Vector2f(), new Vector2f(getFontHeight(10)), 10, mainView.renderer, mainView.loader, mainView.window).noLetters().noOthers();
        categoryPanel.elements.add(new Panel.PanelElement(categoryPlan, 0.123f));
        categoryPanel.elements.add(new Panel.PanelElement(null, 0.002f));
        categoryPanel.elements.add(new Panel.PanelElement(
                new DropDownTab.StringElement("categoryTagStr", "Tag:", 10, mainView.renderer),
                0.098f));
        categoryTagPlan = new Textbox("categoryTag", new Vector2f(), new Vector2f(getFontHeight(10)), 10, mainView.renderer, mainView.loader, mainView.window);
        categoryPanel.elements.add(new Panel.PanelElement(categoryTagPlan, 0.123f));
        categoryPanel.elements.add(new Panel.PanelElement(null, 0.002f));
        categoryPanel.elements.add(new Panel.PanelElement(
                new DropDownTab.StringElement("categoryIndexStr", "Index:", 10, mainView.renderer),
                0.128f));
        categoryIndexPlan = new Textbox("categoryIndex", new Vector2f(), new Vector2f(getFontHeight(10)), 10, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public void setFocused(boolean focused) {
                super.setFocused(focused);

                if(focused)
                    return;

                String text = this.getText();

                if(text.isEmpty() || text.isBlank() || text.equalsIgnoreCase(" "))
                    return;

                int number = Utils.parseInt(text);

                if(number < -32768)
                    this.setText("-32768");
                if(number > 32767)
                    this.setText("32767");
            }
        }.noLetters().noOthers();
        categoryPanel.elements.add(new Panel.PanelElement(categoryIndexPlan, 0.123f));
        categoryIndexPlan.setText("-1");

        Panel locationPanel = planExport.addPanel("locationPanel");
        locationPanel.elements.add(new Panel.PanelElement(
                new DropDownTab.StringElement("locationStr", "Location:", 10, mainView.renderer),
                0.4f));
        locationPlan = new Textbox("location", new Vector2f(), new Vector2f(getFontHeight(10)), 10, mainView.renderer, mainView.loader, mainView.window).noLetters().noOthers();
        locationPanel.elements.add(new Panel.PanelElement(locationPlan, 0.123f));
        locationPanel.elements.add(new Panel.PanelElement(null, 0.002f));
        locationPanel.elements.add(new Panel.PanelElement(
                new DropDownTab.StringElement("locationTagStr", "Tag:", 10, mainView.renderer),
                0.098f));
        locationTagPlan = new Textbox("locationTag", new Vector2f(), new Vector2f(getFontHeight(10)), 10, mainView.renderer, mainView.loader, mainView.window);
        locationPanel.elements.add(new Panel.PanelElement(locationTagPlan, 0.123f));
        locationPanel.elements.add(new Panel.PanelElement(null, 0.002f));
        locationPanel.elements.add(new Panel.PanelElement(
                new DropDownTab.StringElement("locationIndexStr", "Index:", 10, mainView.renderer),
                0.128f));
        locationIndexPlan = new Textbox("locationIndex", new Vector2f(), new Vector2f(getFontHeight(10)), 10, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public void setFocused(boolean focused) {
                super.setFocused(focused);

                if(focused)
                    return;

                String text = this.getText();

                if(text.isEmpty() || text.isBlank() || text.equalsIgnoreCase(" "))
                    return;

                int number = Utils.parseInt(text);

                if(number < -32768)
                    this.setText("-32768");
                if(number > 32767)
                    this.setText("32767");
            }
        }.noLetters().noOthers();
        locationIndexPlan.setText("-1");
        locationPanel.elements.add(new Panel.PanelElement(locationIndexPlan, 0.123f));

        Panel typePlanPanel = planExport.addPanel("typePlanPanel");
        ComboBox planType = new ComboBox("planType", "Type", new Vector2f(), new Vector2f(), 10, 250, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public int[] getParentTransform() {
                return planExport.getTabPosWidth();
            }
        };

        ElementList typeList = planType.addElementList("types", 250);

        List<InventoryObjectType> typeArray = Arrays.asList(InventoryObjectType.values());

        Collections.sort(typeArray, new Comparator<InventoryObjectType>() {
            @Override
            public int compare(InventoryObjectType o1, InventoryObjectType o2) {
                return Integer.compare(o1.getGameVersion(), o2.getGameVersion());
            }
        });

        byte lbpType = 1;
        for(InventoryObjectType type : typeArray)
        {
            String name = type.name().replaceAll("_", " ");
            name = name.substring(0, 1) + name.substring(1).toLowerCase();

            if(lbpType == 1 && type.getGameVersion() == 2)
            {
                typeList.addString("lbp2Types", "LBP2:");
                lbpType = 2;
            }
            if(lbpType == 2 && type.getGameVersion() == 3)
            {
                typeList.addString("lbp3Types", "LBP3:");
                lbpType = 3;
            }

            Checkbox cb = typeList.addCheckbox(type.name(), name);
            typePlan.add(cb);
        }

        typePlanPanel.elements.add(new Panel.PanelElement(planType, 0.695f));
        typePlanPanel.elements.add(new Panel.PanelElement(null, 0.005f));
        ComboBox planSubType = new ComboBox("planSubType", "Sub Type", new Vector2f(), new Vector2f(), 10, 250, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public int[] getParentTransform() {
                return planExport.getTabPosWidth();
            }
        };
        typePlanPanel.elements.add(new Panel.PanelElement(planSubType, 0.3f));

        subTypeList = planSubType.addElementList("subTypeList", 250);

        earthRBPlan = new Radiobutton()
        {
            @Override
            public void onCheck() {
                moonRBPlan.isChecked = false;
                adventureRBPlan.isChecked = false;
                externalRBPlan.isChecked = false;
            }
        };
        moonRBPlan = new Radiobutton()
        {
            @Override
            public void onCheck() {
                earthRBPlan.isChecked = false;
                adventureRBPlan.isChecked = false;
                externalRBPlan.isChecked = false;
            }
        };
        adventureRBPlan = new Radiobutton()
        {
            @Override
            public void onCheck() {
                moonRBPlan.isChecked = false;
                earthRBPlan.isChecked = false;
                externalRBPlan.isChecked = false;
            }
        };
        externalRBPlan = new Radiobutton()
        {
            @Override
            public void onCheck() {
                moonRBPlan.isChecked = false;
                adventureRBPlan.isChecked = false;
                earthRBPlan.isChecked = false;
            }
        };

        subTypeList.addString("planetText", "Planets:");
        subTypeList.addCheckbox("EARTH", "Earth", earthRBPlan);
        subTypeList.addCheckbox("MOON", "Moon", moonRBPlan);
        subTypeList.addCheckbox("ADVENTURE", "Adventure", adventureRBPlan);
        subTypeList.addCheckbox("EXTERNAL", "External", externalRBPlan);

        nonSackboyRBPlan = new Radiobutton()
        {
            @Override
            public void onCheck() {
                giantRBPlan.isChecked = false;
                dwarfRBPlan.isChecked = false;
                birdRBPlan.isChecked = false;
                quadRBPlan.isChecked = false;
            }
        };
        giantRBPlan = new Radiobutton()
        {
            @Override
            public void onCheck() {
                nonSackboyRBPlan.isChecked = false;
                dwarfRBPlan.isChecked = false;
                birdRBPlan.isChecked = false;
                quadRBPlan.isChecked = false;
            }
        };
        dwarfRBPlan = new Radiobutton()
        {
            @Override
            public void onCheck() {
                giantRBPlan.isChecked = false;
                nonSackboyRBPlan.isChecked = false;
                birdRBPlan.isChecked = false;
                quadRBPlan.isChecked = false;
            }
        };
        birdRBPlan = new Radiobutton()
        {
            @Override
            public void onCheck() {
                giantRBPlan.isChecked = false;
                dwarfRBPlan.isChecked = false;
                nonSackboyRBPlan.isChecked = false;
                quadRBPlan.isChecked = false;
            }
        };
        quadRBPlan = new Radiobutton()
        {
            @Override
            public void onCheck() {
                giantRBPlan.isChecked = false;
                dwarfRBPlan.isChecked = false;
                birdRBPlan.isChecked = false;
                nonSackboyRBPlan.isChecked = false;
            }
        };
        subTypeList.addString("costumeText", "Costumes:");
        subTypeList.addCheckbox("CREATURE_MASK", "Non Sackboy", nonSackboyRBPlan);
        subTypeList.addCheckbox("CREATURE_MASK_GIANT", "Big Toggle", giantRBPlan);
        subTypeList.addCheckbox("CREATURE_MASK_DWARF", "Small Toggle", dwarfRBPlan);
        subTypeList.addCheckbox("CREATURE_MASK_BIRD", "Swoop", birdRBPlan);
        subTypeList.addCheckbox("CREATURE_MASK_QUAD", "Oddsock", quadRBPlan);

        madeByMeRBPlan = new Radiobutton()
        {
            @Override
            public void onCheck() {
                madeByAnyRBPlan.isChecked = false;
                madeByOthersRBPlan.isChecked = false;
            }
        };
        madeByOthersRBPlan = new Radiobutton()
        {
            @Override
            public void onCheck() {
                madeByMeRBPlan.isChecked = false;
                madeByAnyRBPlan.isChecked = false;
            }
        };
        madeByAnyRBPlan = new Radiobutton()
        {
            @Override
            public void onCheck() {
                madeByMeRBPlan.isChecked = false;
                madeByOthersRBPlan.isChecked = false;
            }
        };

        subTypeList.addSeparator("cosSep");
        subTypeList.addCheckbox("MADE_BY_ME", "Made by me", madeByMeRBPlan);
        subTypeList.addCheckbox("MADE_BY_OTHERS", "Made by others", madeByOthersRBPlan);
        subTypeList.addCheckbox("MADE_BY_ANYONE", "Made by anyone", madeByAnyRBPlan);

        subTypeList.addSeparator("cosSep2");
        subTypeList.addCheckbox("SPECIAL_COSTUME", "Special Costume");
        subTypeList.addCheckbox("FULL_COSTUME", "Full costume");
        subTypeList.addCheckbox("PLAYER_AVATAR", "Player avatar");

        subTypeList.addString("stickersText", "Stickers & Decorations:");
        subTypeList.addCheckbox("PAINTING", "Painting");
        subTypeList.addCheckbox("EARTH_DECORATION", "Earth decoration");

        ComboBox flagsPlan = planExport.addComboBox("flagsPlan", "Flags", 250);
        used = flagsPlan.addCheckbox("used", "Used");
        hiddenItem = flagsPlan.addCheckbox("hiddenItem", "Hidden item");
        restrictedLevel = flagsPlan.addCheckbox("restrictedLevel", "Restricted Level");
        restrictedPod = flagsPlan.addCheckbox("restrictedPod", "Restricted Pod");
        disableLoopPreview = flagsPlan.addCheckbox("disableLoopPreview", "Disable loop preview");

        ComboBox extrasPlan = planExport.addComboBox("extrasPlan", "More stuff", 320);

        float firstSegWidth = 0.4f;
        float secondSegWidth = 0.6f;

        Panel toolTypePlanPanel = extrasPlan.addPanel("toolTypePlanPanel");
        toolTypePlanPanel.elements.add(new Panel.PanelElement(
                new DropDownTab.StringElement("toolTypePlanStr", "Tool Type:", 10, mainView.renderer),
                firstSegWidth));
        toolTypePlan = new ComboBox("toolTypePlan", "None", new Vector2f(), new Vector2f(), 10, 250, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public int[] getParentTransform() {
                return extrasPlan.getTabPosWidth();
            }
        };
        toolTypePlan.addList("toolTypeList", new ButtonList(Arrays.asList(ToolType.values()), 10, mainView.renderer, mainView.loader, mainView.window) {
            @Override
            public void clickedButton(Object object, int index, int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)
                {
                    if(toolTypePln != object)
                        toolTypePln = (ToolType) object;
                    else
                        toolTypePln = ToolType.NONE;

                    toolTypePlan.tabTitle = toolTypePln.name().replaceAll("_", " ");
                    toolTypePlan.tabTitle = toolTypePlan.tabTitle.substring(0, 1) + toolTypePlan.tabTitle.substring(1).toLowerCase();
                }
            }

            int hovering = -1;

            @Override
            public void draw(MouseInput mouseInput, boolean overElement) {
                hovering = -1;
                super.draw(mouseInput, overElement);
            }

            @Override
            public void hoveringButton(Object object, int index) {
                hovering = index;
            }

            @Override
            public boolean isHighlighted(Object object, int index) {
                return hovering == index;
            }

            @Override
            public boolean isSelected(Object object, int index) {
                return toolTypePln == object;
            }

            @Override
            public String buttonText(Object object, int index) {
                String name = ((ToolType)object).name().replaceAll("_", " ");
                name = name.substring(0, 1) + name.substring(1).toLowerCase();
                return name;
            }

            @Override
            public boolean searchFilter(Object object, int index) {
                return true;
            }
        }, 300);
        toolTypePlanPanel.elements.add(new Panel.PanelElement(toolTypePlan, secondSegWidth));

        Panel uses = extrasPlan.addPanel("usesPlanPanel");
        uses.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("title", "Uses:", 10, renderer), firstSegWidth));
        numUsesPlan = new Textbox("numUsesPlan", new Vector2f(), new Vector2f(getFontHeight(10)), 10, mainView.renderer, mainView.loader, mainView.window).noLetters().noOthers();
        uses.elements.add(new Panel.PanelElement(numUsesPlan, secondSegWidth));

        Panel lastUsed = extrasPlan.addPanel("lastUsedPlanPanel");
        lastUsed.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("title", "Last Used:", 10, renderer), firstSegWidth));
        lastUsedPlan = new Textbox("lastUsedPlan", new Vector2f(), new Vector2f(getFontHeight(10)), 10, mainView.renderer, mainView.loader, mainView.window).noLetters().noOthers();
        lastUsed.elements.add(new Panel.PanelElement(lastUsedPlan, secondSegWidth));

        Panel fluffCost = extrasPlan.addPanel("fluffCostPlanPanel");
        fluffCost.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("title", "Fluff Cost:", 10, renderer), firstSegWidth));
        fluffCostPlan = new Textbox("fluffCostPlan", new Vector2f(), new Vector2f(getFontHeight(10)), 10, mainView.renderer, mainView.loader, mainView.window).noLetters().noOthers();
        fluffCost.elements.add(new Panel.PanelElement(fluffCostPlan, secondSegWidth));

        makeSizeProportionalPlan = extrasPlan.addCheckbox("makeSizeProportional", "Make Size Proportional");

        Panel primaryIndex = extrasPlan.addPanel("primaryIndex");
        primaryIndex.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("title", "Prim. Index:", 10, renderer), firstSegWidth));
        primaryIndexPlan = new Textbox("primaryIndexPlan", new Vector2f(), new Vector2f(getFontHeight(10)), 10, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public void setFocused(boolean focused) {
                super.setFocused(focused);

                if(focused)
                    return;

                String text = this.getText();

                if(text.isEmpty() || text.isBlank() || text.equalsIgnoreCase(" "))
                    return;

                int number = Utils.parseInt(text);

                if(number < -32768)
                    this.setText("-32768");
                if(number > 32767)
                    this.setText("32767");
            }
        }.noLetters().noOthers();
        primaryIndex.elements.add(new Panel.PanelElement(primaryIndexPlan, secondSegWidth));

        Panel translationTag = extrasPlan.addPanel("translationTag");
        translationTag.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("title", "Transl. Tag:", 10, renderer), firstSegWidth));
        translationTagPlan = new Textbox("translationTagPlan", new Vector2f(), new Vector2f(getFontHeight(10)), 10, mainView.renderer, mainView.loader, mainView.window);
        translationTag.elements.add(new Panel.PanelElement(translationTagPlan, secondSegWidth));

        extrasPlan.addString("colorPlanStr", "Color:");
        Panel colorPlanPanel = extrasPlan.addPanel("colorPlanPanel");

        float gap = 0.02f;
        float boxes = 1f - gap;

        rColorPlan = new Textbox("rColorPlan", new Vector2f(), new Vector2f(getFontHeight(10)), 10, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public void setFocused(boolean focused) {
                super.setFocused(focused);

                if(focused)
                    return;

                String text = this.getText();

                if(text.isEmpty() || text.isBlank() || text.equalsIgnoreCase(" "))
                    return;

                int number = Utils.parseInt(text);

                if(number < 0)
                    this.setText("0");
                if(number > 255)
                    this.setText("255");
            }
        }.noLetters().noOthers();
        colorPlanPanel.elements.add(new Panel.PanelElement(rColorPlan, boxes/4f));
        colorPlanPanel.elements.add(new Panel.PanelElement(null, gap/3f));
        gColorPlan = new Textbox("gColorPlan", new Vector2f(), new Vector2f(getFontHeight(10)), 10, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public void setFocused(boolean focused) {
                super.setFocused(focused);

                if(focused)
                    return;

                String text = this.getText();

                if(text.isEmpty() || text.isBlank() || text.equalsIgnoreCase(" "))
                    return;

                int number = Utils.parseInt(text);

                if(number < 0)
                    this.setText("0");
                if(number > 255)
                    this.setText("255");
            }
        }.noLetters().noOthers();
        colorPlanPanel.elements.add(new Panel.PanelElement(gColorPlan, boxes/4f));
        colorPlanPanel.elements.add(new Panel.PanelElement(null, gap/3f));
        bColorPlan = new Textbox("bColorPlan", new Vector2f(), new Vector2f(getFontHeight(10)), 10, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public void setFocused(boolean focused) {
                super.setFocused(focused);

                if(focused)
                    return;

                String text = this.getText();

                if(text.isEmpty() || text.isBlank() || text.equalsIgnoreCase(" "))
                    return;

                int number = Utils.parseInt(text);

                if(number < 0)
                    this.setText("0");
                if(number > 255)
                    this.setText("255");
            }
        }.noLetters().noOthers();
        colorPlanPanel.elements.add(new Panel.PanelElement(bColorPlan, boxes/4f));
        colorPlanPanel.elements.add(new Panel.PanelElement(null, gap/3f));
        aColorPlan = new Textbox("aColorPlan", new Vector2f(), new Vector2f(getFontHeight(10)), 10, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public void setFocused(boolean focused) {
                super.setFocused(focused);

                if(focused)
                    return;

                String text = this.getText();

                if(text.isEmpty() || text.isBlank() || text.equalsIgnoreCase(" "))
                    return;

                int number = Utils.parseInt(text);

                if(number < 0)
                    this.setText("0");
                if(number > 255)
                    this.setText("255");
            }
        }.noLetters().noOthers();
        colorPlanPanel.elements.add(new Panel.PanelElement(aColorPlan, boxes/4f));

        Panel highlightSound = extrasPlan.addPanel("highlightSound");
        highlightSound.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("title", "Highl. Sound (GUID):", 10, renderer), 0.575f));
        highlightSoundPlan = new Textbox("highlightSoundPlan", new Vector2f(), new Vector2f(getFontHeight(10)), 10, mainView.renderer, mainView.loader, mainView.window).noLetters().noOthers();
        highlightSound.elements.add(new Panel.PanelElement(highlightSoundPlan, 0.425f));

        extrasPlan.addString("dateAddedPlanStr", "Date Added:");
        Panel dateAddedPlanPanel1 = extrasPlan.addPanel("dateAddedPlanPanel1");

        gap = 0.6f;
        boxes = 1f - gap;

        dateAddedPlanPanel1.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("day", "Day:", 10, renderer), 0.16f));
        dateAddedDayPlan = new Textbox("dateAddedDayPlan", new Vector2f(), new Vector2f(getFontHeight(10)), 10, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public void setFocused(boolean focused) {
                super.setFocused(focused);

                if(focused)
                    return;

                String text = this.getText();

                if(text.isEmpty() || text.isBlank() || text.equalsIgnoreCase(" "))
                    return;

                int number = Utils.parseInt(text);

                if(number < 1)
                    this.setText("1");
                if(number > 31)
                    this.setText("31");
            }
        }.noLetters().noOthers();
        dateAddedPlanPanel1.elements.add(new Panel.PanelElement(dateAddedDayPlan, (boxes + 0.02f)/3f));
        dateAddedPlanPanel1.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("month", "Month:", 10, renderer), gap/3f));
        dateAddedMonthPlan = new Textbox("dateAddedMonthPlan", new Vector2f(), new Vector2f(getFontHeight(10)), 10, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public void setFocused(boolean focused) {
                super.setFocused(focused);

                if(focused)
                    return;

                String text = this.getText();

                if(text.isEmpty() || text.isBlank() || text.equalsIgnoreCase(" "))
                    return;

                int number = Utils.parseInt(text);

                if(number < 1)
                    this.setText("1");
                if(number > 12)
                    this.setText("12");
            }
        }.noLetters().noOthers();
        dateAddedPlanPanel1.elements.add(new Panel.PanelElement(dateAddedMonthPlan, (boxes + 0.02f)/3f));
        dateAddedPlanPanel1.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("year", "Year:", 10, renderer), gap/3f + 0.02f));
        dateAddedYearPlan = new Textbox("dateAddedYearPlan", new Vector2f(), new Vector2f(getFontHeight(10)), 10, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public void setFocused(boolean focused) {
                super.setFocused(focused);

                if(focused)
                    return;

                String text = this.getText();

                if(text.isEmpty() || text.isBlank() || text.equalsIgnoreCase(" "))
                    return;

                int number = Utils.parseInt(text);

                if(number < 1900)
                    this.setText("1900");
            }
        }.noLetters().noOthers();
        dateAddedPlanPanel1.elements.add(new Panel.PanelElement(dateAddedYearPlan, (boxes + 0.02f)/3f));

        Panel dateAddedPlanPanel2 = extrasPlan.addPanel("dateAddedPlanPanel2");

        dateAddedPlanPanel2.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("hour", "Hour:", 10, renderer), 0.16f));
        dateAddedHourPlan = new Textbox("dateAddedHourPlan", new Vector2f(), new Vector2f(getFontHeight(10)), 10, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public void setFocused(boolean focused) {
                super.setFocused(focused);

                if(focused)
                    return;

                String text = this.getText();

                if(text.isEmpty() || text.isBlank() || text.equalsIgnoreCase(" "))
                    return;

                int number = Utils.parseInt(text);

                if(number < 0)
                    this.setText("0");
                if(number > 23)
                    this.setText("23");
            }
        }.noLetters().noOthers();
        dateAddedPlanPanel2.elements.add(new Panel.PanelElement(dateAddedHourPlan, (boxes + 0.02f)/3f));
        dateAddedPlanPanel2.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("minute", "Minute:", 10, renderer), gap/3f));
        dateAddedMinutePlan = new Textbox("dateAddedMinutePlan", new Vector2f(), new Vector2f(getFontHeight(10)), 10, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public void setFocused(boolean focused) {
                super.setFocused(focused);

                if(focused)
                    return;

                String text = this.getText();

                if(text.isEmpty() || text.isBlank() || text.equalsIgnoreCase(" "))
                    return;

                int number = Utils.parseInt(text);

                if(number < 0)
                    this.setText("0");
                if(number > 59)
                    this.setText("59");
            }
        }.noLetters().noOthers();
        dateAddedPlanPanel2.elements.add(new Panel.PanelElement(dateAddedMinutePlan, (boxes + 0.02f)/3f));
        dateAddedPlanPanel2.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("second", "Second:", 10, renderer), gap/3f + 0.02f));
        dateAddedSecondPlan = new Textbox("dateAddedSecondPlan", new Vector2f(), new Vector2f(getFontHeight(10)), 10, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public void setFocused(boolean focused) {
                super.setFocused(focused);

                if(focused)
                    return;

                String text = this.getText();

                if(text.isEmpty() || text.isBlank() || text.equalsIgnoreCase(" "))
                    return;

                int number = Utils.parseInt(text);

                if(number < 0)
                    this.setText("0");
                if(number > 59)
                    this.setText("59");
            }
        }.noLetters().noOthers();
        dateAddedPlanPanel2.elements.add(new Panel.PanelElement(dateAddedSecondPlan, (boxes + 0.02f)/3f));

        planExport.addSeparator("exportSeparator").size.y = 11;

        selectionOnlyExportPlan = planExport.addCheckbox("selectionOnlyExportPlan", "Selection Only");

        planExport.addString("compressionFlagsStr", "Compression Flags:");
        Panel compressionFlagsPanel = planExport.addPanel("compressionFlagsPanel");

        compressionFlagsIntPlan = new Checkbox("compressionFlagsIntPlan", "Integers", 10, renderer, loader, window).checked();
        compressionFlagsPanel.elements.add(new Panel.PanelElement(compressionFlagsIntPlan, 1f/3f));
        compressionFlagsVecPlan = new Checkbox("compressionFlagsVecPlan", "Vectors", 10, renderer, loader, window).checked();
        compressionFlagsPanel.elements.add(new Panel.PanelElement(compressionFlagsVecPlan, 1f/3f));
        compressionFlagsMatPlan = new Checkbox("compressionFlagsMatPlan", "Matrices", 10, renderer, loader, window).checked();
        compressionFlagsPanel.elements.add(new Panel.PanelElement(compressionFlagsMatPlan, 1f/3f));

        Panel metadataPanel = planExport.addPanel("metadataPanel");
        metadataPanel.size.y = 35;

        Button importMetadata = new Button("importMetadataPlan", "Import Metadata", new Vector2f(), new Vector2f(), 10, renderer, loader, window) {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)
                    importPlanMeta = true;
            }
        };

        ComboBox templateMetadata = new ComboBox("templateMetadata", "Templates", new Vector2f(), new Vector2f(), 10, 200, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public int[] getParentTransform() {
                return planExport.getTabPosWidth();
            }
        };

        templateMetadata.addButton("obj", "Object", new Button() {
            @Override
            public void clickedButton(int button, int action, int mods) {
                presetObjectPlan();
            }
        });
        templateMetadata.addButton("mat", "Material", new Button() {
            @Override
            public void clickedButton(int button, int action, int mods) {
                presetMaterialPlan();
            }
        });
        templateMetadata.addButton("bg", "Background", new Button() {
            @Override
            public void clickedButton(int button, int action, int mods) {
                presetBGPlan();
            }
        });
        templateMetadata.addButton("bgPod", "BG (Pod|LBP2+)", new Button() {
            @Override
            public void clickedButton(int button, int action, int mods) {
                presetBGPlan();
                restrictedPod.isChecked = true;
            }
        });
        templateMetadata.addButton("bgLevel", "BG (Level|LBP2+)", new Button() {
            @Override
            public void clickedButton(int button, int action, int mods) {
                presetBGPlan();
                restrictedLevel.isChecked = true;
            }
        });

        gap = 0.005f;
        metadataPanel.elements.add(new Panel.PanelElement(importMetadata, (1f-gap)/2f));
        metadataPanel.elements.add(new Panel.PanelElement(null, gap));
        metadataPanel.elements.add(new Panel.PanelElement(templateMetadata, (1f-gap)/2f));

        ComboBox exportCombo = planExport.addComboBox("exportCombo", "Build", 200);
        exportCombo.size.y = 35;

        exportCombo.addButton("lbp1", "LBP1", new Button() {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_RELEASE) {

                    FileTree.TreeFolder folder = (FileTree.TreeFolder) modFileTree.getSelectedItem();

                    if(folder == null)
                        return;

                    byte[] data = Resource.compress(buildPlan(new Revision(Branch.LEERDAMMER.getHead(), Branch.LEERDAMMER.getID(), Branch.LEERDAMMER.getRevision()), true).build());

                    String path = modFileTree.getPath(folder);

                    FileDBRow row = LoadedData.PROJECT_DATA.add(path + "/.plan", data);

                    String nameText = userCreatedNamePlan.getText();

                    FileTree.TreeItem planItem = folder.addItem(String.valueOf(folder.children.size()), row, nameText + ".plan", modFileTree.itemHeight);
                    planItem.itemName.setFocused(true);
                    planItem.itemName.setSelection(nameText.length());
                    folder.selected = false;
                    planItem.selected = true;
                    folder.optionsCombo.extended = false;

                    LoadedData.shouldSetupList = true;
                }
            }
        });
        exportCombo.addButton("lbp2", "LBP2", new Button() {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if (button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_RELEASE) {

                    FileTree.TreeFolder folder = (FileTree.TreeFolder) modFileTree.getSelectedItem();

                    if (folder == null)
                        return;

                    byte[] data = Resource.compress(buildPlan(new Revision(Revisions.LBP2_MAX), true).build());

                    String path = modFileTree.getPath(folder);

                    FileDBRow row = LoadedData.PROJECT_DATA.add(path + "/.plan", data);

                    String nameText = userCreatedNamePlan.getText();

                    FileTree.TreeItem planItem = folder.addItem(String.valueOf(folder.children.size()), row, nameText + ".plan", modFileTree.itemHeight);
                    planItem.itemName.setFocused(true);
                    planItem.itemName.setSelection(nameText.length());
                    folder.selected = false;
                    planItem.selected = true;
                    folder.optionsCombo.extended = false;

                    LoadedData.shouldSetupList = true;
                }
            }
        });
        exportCombo.addButton("lbpv", "LBP Vita", new Button() {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_RELEASE) {

                    FileTree.TreeFolder folder = (FileTree.TreeFolder) modFileTree.getSelectedItem();

                    if(folder == null)
                        return;

                    byte[] data = Resource.compress(buildPlan(new Revision(Branch.DOUBLE11.getHead(), Branch.DOUBLE11.getID(), Branch.DOUBLE11.getRevision()), true).build());

                    String path = modFileTree.getPath(folder);

                    FileDBRow row = LoadedData.PROJECT_DATA.add(path + "/.plan", data);

                    String nameText = userCreatedNamePlan.getText();

                    FileTree.TreeItem planItem = folder.addItem(String.valueOf(folder.children.size()), row, nameText + ".plan", modFileTree.itemHeight);
                    planItem.itemName.setFocused(true);
                    planItem.itemName.setSelection(nameText.length());
                    folder.selected = false;
                    planItem.selected = true;
                    folder.optionsCombo.extended = false;

                    LoadedData.shouldSetupList = true;
                }
            }
        });
        exportCombo.addButton("lbp3", "LBP3", new Button() {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_RELEASE) {
                    FileTree.TreeFolder folder = (FileTree.TreeFolder) modFileTree.getSelectedItem();

                    if(folder == null)
                        return;

                    byte[] data = Resource.compress(buildPlan(new Revision(Revisions.LBP3_MAX), true).build());

                    String path = modFileTree.getPath(folder);

                    FileDBRow row = LoadedData.PROJECT_DATA.add(path + "/.plan", data);

                    String nameText = userCreatedNamePlan.getText();

                    FileTree.TreeItem planItem = folder.addItem(String.valueOf(folder.children.size()), row, nameText + ".plan", modFileTree.itemHeight);
                    planItem.itemName.setFocused(true);
                    planItem.itemName.setSelection(nameText.length());
                    folder.selected = false;
                    planItem.selected = true;
                    folder.optionsCombo.extended = false;

                    LoadedData.shouldSetupList = true;
                }
            }
        });

        exportCombo.addSeparator("customRevSep").size.y = 5;
        exportCombo.addString("customRevStr", "Custom Revision:");

        Panel customRevPanelHead = exportCombo.addPanel("customRevPanel");
        customRevPanelHead.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("head", "Head:", 10, renderer), 0.5f));
        customRevisionPlanHead = new Textbox("customRevisionPlanHead", new Vector2f(), new Vector2f(), 10, renderer, loader, window);
        customRevPanelHead.elements.add(new Panel.PanelElement(customRevisionPlanHead, 0.5f));

        Panel customRevPanelID = exportCombo.addPanel("customRevPanelID");
        customRevPanelID.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("id", "ID:", 10, renderer), 0.5f));
        customRevisionPlanID = new Textbox("customRevisionPlanID", new Vector2f(), new Vector2f(), 10, renderer, loader, window);
        customRevPanelID.elements.add(new Panel.PanelElement(customRevisionPlanID, 0.5f));

        Panel customRevPanelRev = exportCombo.addPanel("customRevPanelRev");
        customRevPanelRev.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("rev", "Revision:", 10, renderer), 0.5f));
        customRevisionPlanRev = new Textbox("customRevisionPlanRev", new Vector2f(), new Vector2f(), 10, renderer, loader, window);
        customRevPanelRev.elements.add(new Panel.PanelElement(customRevisionPlanRev, 0.5f));

        customRevisionPlanExport = exportCombo.addButton("customRevisionPlanExport", "Build", new Button() {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_RELEASE) {

                    FileTree.TreeFolder folder = (FileTree.TreeFolder) modFileTree.getSelectedItem();

                    if(folder == null)
                        return;

                    RPlan plan;

                    String head = customRevisionPlanHead.getText();
                    String id = customRevisionPlanID.getText();
                    String revision = customRevisionPlanRev.getText();

                    if(!((head.isEmpty() || head.isBlank() || head.equalsIgnoreCase(" ")) && (id.isEmpty() || id.isBlank() || id.equalsIgnoreCase(" "))))
                        plan = buildPlan(new Revision(Utils.parseIntA(head), Utils.parseIntA(id), Utils.parseIntA(revision)), true);
                    else
                        plan = buildPlan(new Revision(Utils.parseIntA(revision)), true);

                    byte[] data = Resource.compress(plan.build());

                    String path = modFileTree.getPath(folder);

                    FileDBRow row = LoadedData.PROJECT_DATA.add(path + "/.plan", data);

                    String nameText = userCreatedNamePlan.getText();

                    FileTree.TreeItem planItem = folder.addItem(String.valueOf(folder.children.size()), row, nameText + ".plan", modFileTree.itemHeight);
                    planItem.itemName.setFocused(true);
                    planItem.itemName.setSelection(nameText.length());
                    folder.selected = false;
                    planItem.selected = true;
                    folder.optionsCombo.extended = false;

                    LoadedData.shouldSetupList = true;
                }
            }
        });

        binExport = buildScene.addComboBox("binExport", "Bin", 450);// = new DropDownTab("binExport", "Export Bin", new Vector2f(7 * 2 + 3 + 450, 21 + 10), new Vector2f(450, getFontHeight(10) + 4), 10, mainView.renderer, mainView.loader, mainView.window);

        Panel addPartsPanel = binExport.addPanel("addPartsPanel");
        addPartsPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("addpartsstr", "Parts:", 10, mainView.renderer), 0.5f));

        ComboBox addPartCombo = new ComboBox("addPartCombo", "Add", new Vector2f(), new Vector2f(), 10, 215, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public int[] getParentTransform() {
                return binExport.getTabPosWidth();
            }
        };
        ArrayList<Part> pList = new ArrayList<>();

        for(Part part : Part.values())
            if(part.getSerializable() != null)
                pList.add(part);

        Collections.sort(pList, new Comparator<Part>() {
            @Override
            public int compare(Part o1, Part o2) {
                return o1.name().compareTo(o2.name());
            }
        });

        Textbox searchParts = new Textbox("searchParts", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
        Panel searchPartsPanel = addPartCombo.addPanel("searchPartsPanel");
        searchPartsPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("srchPrt", "Search:", 10, mainView.renderer), 0.4f));
        searchPartsPanel.elements.add(new Panel.PanelElement(searchParts, 0.6f));

        addPartCombo.addList("addpPartList", new ButtonList(pList, 10, mainView.renderer, mainView.loader, mainView.window) {

            int hovering = -1;
            int clicked = -1;

            @Override
            public void draw(MouseInput mouseInput, boolean overElement) {
                hovering = -1;
                super.draw(mouseInput, overElement);

                if(clicked != hovering)
                    clicked = -1;
            }

            @Override
            public void clickedButton(Object object, int index, int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1)
                {
                    if(action == GLFW.GLFW_PRESS)
                    {
                        clicked = index;
                        try {
                            thingPart.addPart((Part) object, worldThingA);
                        } catch (Exception e) {
                            print.stackTrace(e);
                        }
                    }
                }
            }

            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement, boolean focusedOther) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_RELEASE)
                    clicked = -1;
                super.onClick(mouseInput, pos, button, action, mods, overElement, focusedOther);
            }

            @Override
            public void hoveringButton(Object object, int index) {
                hovering = index;
            }

            @Override
            public boolean isHighlighted(Object object, int index) {
                return hovering == index;
            }

            @Override
            public boolean isSelected(Object object, int index) {
                return clicked == index;
            }

            @Override
            public String buttonText(Object object, int index) {

                String name = "";
                for(String part : ((Part)object).name().split("_"))
                    name += part.substring(0, 1).toUpperCase() + part.substring(1).toLowerCase() + " ";

                return name;
            }

            @Override
            public boolean searchFilter(Object object, int index) {
                return buttonText(object, index).toLowerCase().contains(searchParts.getText().toLowerCase());
            }

            @Override
            public int buttonHeight() {
                return super.buttonHeight() + 4;
            }
        }, 295);

        addPartsPanel.elements.add(new Panel.PanelElement(addPartCombo, 0.5f));

        ElementList binParts = binExport.addElementList(new ElementList("binParts", new Vector2f(), new Vector2f(150), 10, renderer, loader, window)
        {
            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overOther, boolean focusedOther) {

                if(isMouseOverElement(pos) && (button == GLFW.GLFW_MOUSE_BUTTON_2 || button == GLFW.GLFW_MOUSE_BUTTON_1) && action == GLFW.GLFW_PRESS)
                    for(Element element : this.elements)
                    {
                        Panel panel = (Panel) element;
                        Element e = panel.elements.get(0).element;
                        if(e instanceof ComboBox && !((ComboBox)e).isMouseOverTab(pos) && !((ComboBox)e).isMouseOverElement(pos))
                            ((ComboBox)e).collapsed(true);
                    }

                super.onClick(mouseInput, pos, button, action, mods, overOther, focusedOther);
            }

            @Override
            public int[] getParentTransform() {
                return binExport.getTabPosWidth();
            }
        });

        thingPart = new ThingPart(mainView, binParts, binExport, worldThingA);

        binExport.addSeparator("exportSeparator").size.y = 11;

        selectionOnlyExportBin = binExport.addCheckbox("selectionOnlyExportBin", "Selection Only");

        binExport.addString("compressionFlagsStr", "Compression Flags:");
        Panel compressionFlagsBinPanel = binExport.addPanel("compressionFlagsBinPanel");

        compressionFlagsIntBin = new Checkbox("compressionFlagsIntBin", "Integers", 10, renderer, loader, window).checked();
        compressionFlagsBinPanel.elements.add(new Panel.PanelElement(compressionFlagsIntBin, 1f/3f));
        compressionFlagsVecBin = new Checkbox("compressionFlagsVecBin", "Vectors", 10, renderer, loader, window).checked();
        compressionFlagsBinPanel.elements.add(new Panel.PanelElement(compressionFlagsVecBin, 1f/3f));
        compressionFlagsMatBin = new Checkbox("compressionFlagsMatBin", "Matrices", 10, renderer, loader, window).checked();
        compressionFlagsBinPanel.elements.add(new Panel.PanelElement(compressionFlagsMatBin, 1f/3f));

        Panel metadataPanelBin = binExport.addPanel("metadataPanelBin");
        metadataPanelBin.size.y = 35;

        Button importMetadataBin = new Button("importMetadataBin", "Import Metadata", new Vector2f(), new Vector2f(), 10, renderer, loader, window) {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)
                    importBinMeta = true;
            }
        };

        ComboBox templateMetadataBin = new ComboBox("templateMetadataBin", "Templates", new Vector2f(), new Vector2f(), 10, 200, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public int[] getParentTransform() {
                return binExport.getTabPosWidth();
            }
        };

        templateMetadataBin.addButton("lbp1 pod bg", "LBP1 Pod Background", new Button() {
            @Override
            public void clickedButton(int button, int action, int mods) {
                presetLBP1PodBG();
            }
        });

        templateMetadataBin.addButton("level", "Level", new Button() {
            @Override
            public void clickedButton(int button, int action, int mods) {
                presetLevel();
            }
        });

        gap = 0.005f;
        metadataPanelBin.elements.add(new Panel.PanelElement(importMetadataBin, (1f-gap)/2f));
        metadataPanelBin.elements.add(new Panel.PanelElement(null, gap));
        metadataPanelBin.elements.add(new Panel.PanelElement(templateMetadataBin, (1f-gap)/2f));

        ComboBox exportComboBin = binExport.addComboBox("exportComboBin", "Build", 200);

        exportComboBin.size.y = 35;

        exportComboBin.addButton("lbp1", "LBP1", new Button() {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_RELEASE) {

                    FileTree.TreeFolder folder = (FileTree.TreeFolder) modFileTree.getSelectedItem();

                    if(folder == null)
                        return;

                    byte[] data = Resource.compress(buildBin(new Revision(Branch.LEERDAMMER.getHead(), Branch.LEERDAMMER.getID(), Branch.LEERDAMMER.getRevision()), true));

                    String path = modFileTree.getPath(folder);

                    FileDBRow row = LoadedData.PROJECT_DATA.add(path + "/.bin", data);

                    FileTree.TreeItem binItem = folder.addItem(String.valueOf(folder.children.size()), row, ".bin", modFileTree.itemHeight);
                    binItem.itemName.setFocused(true);
                    binItem.itemName.setSelection(0);
                    folder.selected = false;
                    binItem.selected = true;
                    folder.optionsCombo.extended = false;

                    LoadedData.shouldSetupList = true;
                }
            }
        });
        exportComboBin.addButton("lbp2", "LBP2", new Button() {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_RELEASE) {

                    FileTree.TreeFolder folder = (FileTree.TreeFolder) modFileTree.getSelectedItem();

                    if(folder == null)
                        return;

                    byte[] data = Resource.compress(buildBin(new Revision(Revisions.LBP2_MAX), true));

                    String path = modFileTree.getPath(folder);

                    FileDBRow row = LoadedData.PROJECT_DATA.add(path + "/.bin", data);

                    FileTree.TreeItem binItem = folder.addItem(String.valueOf(folder.children.size()), row, ".bin", modFileTree.itemHeight);
                    binItem.itemName.setFocused(true);
                    binItem.itemName.setSelection(0);
                    folder.selected = false;
                    binItem.selected = true;
                    folder.optionsCombo.extended = false;

                    LoadedData.shouldSetupList = true;
                }
            }
        });
        exportComboBin.addButton("lbpv", "LBP Vita", new Button() {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_RELEASE) {

                    FileTree.TreeFolder folder = (FileTree.TreeFolder) modFileTree.getSelectedItem();

                    if(folder == null)
                        return;

                    byte[] data = Resource.compress(buildBin(new Revision(Branch.DOUBLE11.getHead(), Branch.DOUBLE11.getID(), Branch.DOUBLE11.getRevision()), true));

                    String path = modFileTree.getPath(folder);

                    FileDBRow row = LoadedData.PROJECT_DATA.add(path + "/.bin", data);

                    FileTree.TreeItem binItem = folder.addItem(String.valueOf(folder.children.size()), row, ".bin", modFileTree.itemHeight);
                    binItem.itemName.setFocused(true);
                    binItem.itemName.setSelection(0);
                    folder.selected = false;
                    binItem.selected = true;
                    folder.optionsCombo.extended = false;

                    LoadedData.shouldSetupList = true;
                }
            }
        });
        exportComboBin.addButton("lbp3", "LBP3", new Button() {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_RELEASE) {

                    FileTree.TreeFolder folder = (FileTree.TreeFolder) modFileTree.getSelectedItem();

                    if(folder == null)
                        return;

                    byte[] data = Resource.compress(buildBin(new Revision(Revisions.LBP3_MAX), true), true);

                    String path = modFileTree.getPath(folder);

                    FileDBRow row = LoadedData.PROJECT_DATA.add(path + "/.bin", data);

                    FileTree.TreeItem binItem = folder.addItem(String.valueOf(folder.children.size()), row, ".bin", modFileTree.itemHeight);
                    binItem.itemName.setFocused(true);
                    binItem.itemName.setSelection(0);
                    folder.selected = false;
                    binItem.selected = true;
                    folder.optionsCombo.extended = false;

                    LoadedData.shouldSetupList = true;
                }
            }
        });

        exportComboBin.addSeparator("customRevSep").size.y = 5;
        exportComboBin.addString("customRevStr", "Custom Revision:");

        Panel customRevPanelHeadBin = exportComboBin.addPanel("customRevPanelHeadBin");
        customRevPanelHeadBin.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("head", "Head:", 10, renderer), 0.5f));
        customRevisionBinHead = new Textbox("customRevisionBinHead", new Vector2f(), new Vector2f(), 10, renderer, loader, window);
        customRevPanelHeadBin.elements.add(new Panel.PanelElement(customRevisionBinHead, 0.5f));

        Panel customRevPanelIDBin = exportComboBin.addPanel("customRevPanelIDBin");
        customRevPanelIDBin.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("id", "ID:", 10, renderer), 0.5f));
        customRevisionBinID = new Textbox("customRevisionBinID", new Vector2f(), new Vector2f(), 10, renderer, loader, window);
        customRevPanelIDBin.elements.add(new Panel.PanelElement(customRevisionBinID, 0.5f));

        Panel customRevPanelRevBin = exportComboBin.addPanel("customRevPanelRevBin");
        customRevPanelRevBin.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("rev", "Revision:", 10, renderer), 0.5f));
        customRevisionBinRev = new Textbox("customRevisionBinRev", new Vector2f(), new Vector2f(), 10, renderer, loader, window);
        customRevPanelRevBin.elements.add(new Panel.PanelElement(customRevisionBinRev, 0.5f));

        customRevisionBinExport = exportComboBin.addButton("customRevisionBinExport", "Build", new Button() {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_RELEASE) {

                    FileTree.TreeFolder folder = (FileTree.TreeFolder) modFileTree.getSelectedItem();

                    if(folder == null)
                        return;

                    SerializationData bin;

                    String head = customRevisionBinHead.getText();
                    String id = customRevisionBinID.getText();
                    String revision = customRevisionBinRev.getText();

                    if(!((head.isEmpty() || head.isBlank() || head.equalsIgnoreCase(" ")) && (id.isEmpty() || id.isBlank() || id.equalsIgnoreCase(" "))))
                        bin = buildBin(new Revision(Utils.parseIntA(head), Utils.parseIntA(id), Utils.parseIntA(revision)), true);
                    else
                        bin = buildBin(new Revision(Utils.parseIntA(revision)), true);

                    byte[] data = Resource.compress(bin);

                    String path = modFileTree.getPath(folder);

                    FileDBRow row = LoadedData.PROJECT_DATA.add(path + "/.bin", data);

                    FileTree.TreeItem binItem = folder.addItem(String.valueOf(folder.children.size()), row, ".bin", modFileTree.itemHeight);
                    binItem.itemName.setFocused(true);
                    binItem.itemName.setSelection(0);
                    folder.selected = false;
                    binItem.selected = true;
                    folder.optionsCombo.extended = false;

                    LoadedData.shouldSetupList = true;
                }
            }
        });

        this.guiElements.add(project);
    }

    public RPlan buildPlan(Revision revision, boolean includeThings)
    {
        RPlan plan = new RPlan();
        plan.revision = revision;

        plan.compressionFlags = CompressionFlags.USE_NO_COMPRESSION;
        Utils.setBitwiseBool(plan.compressionFlags, CompressionFlags.USE_COMPRESSED_INTEGERS, compressionFlagsIntPlan.isChecked);
        Utils.setBitwiseBool(plan.compressionFlags, CompressionFlags.USE_COMPRESSED_VECTORS, compressionFlagsVecPlan.isChecked);
        Utils.setBitwiseBool(plan.compressionFlags, CompressionFlags.USE_COMPRESSED_MATRICES, compressionFlagsMatPlan.isChecked);

        plan.setThings(includeThings ? mainView.buildThingArray(selectionOnlyExportPlan.isChecked) : new Thing[0]);

        ResourceDescriptor icon = new ResourceDescriptor(2551, ResourceType.TEXTURE);
        String planIcon = iconPlan.getText();
        if(planIcon.length() == 40)
            icon = new ResourceDescriptor(new SHA1(planIcon), ResourceType.TEXTURE);
        else if(planIcon.length() == 41)
            icon = new ResourceDescriptor(new SHA1(planIcon.substring(1)), ResourceType.TEXTURE);
        else if(planIcon.length() > 0 && planIcon.substring(0, 1).equalsIgnoreCase("g"))
            icon = new ResourceDescriptor(Utils.parseLong(planIcon.substring(1)), ResourceType.TEXTURE);
        else
            icon = new ResourceDescriptor(Utils.parseLong(planIcon), ResourceType.TEXTURE);

        Date currentDate = new Date();

        String dateDay = dateAddedDayPlan.getText();
        int day = dateDay.isBlank() || dateDay.isEmpty() || dateDay.equalsIgnoreCase(" ") ? currentDate.getDate() : Utils.parseInt(dateDay);
        String dateMonth = dateAddedMonthPlan.getText();
        int month = dateMonth.isBlank() || dateMonth.isEmpty() || dateMonth.equalsIgnoreCase(" ") ? currentDate.getMonth() + 1 : Utils.parseInt(dateMonth);
        String dateYear = dateAddedYearPlan.getText();
        int year = dateYear.isBlank() || dateYear.isEmpty() || dateYear.equalsIgnoreCase(" ") ? currentDate.getYear() + 1900 : Utils.parseInt(dateYear);

        String dateHour = dateAddedHourPlan.getText();
        int hour = dateHour.isBlank() || dateHour.isEmpty() || dateHour.equalsIgnoreCase(" ") ? currentDate.getHours() : Utils.parseInt(dateHour);
        String dateMinute = dateAddedMinutePlan.getText();
        int minute = dateMinute.isBlank() || dateMinute.isEmpty() || dateMinute.equalsIgnoreCase(" ") ? currentDate.getMinutes() : Utils.parseInt(dateMinute);
        String dateSecond = dateAddedSecondPlan.getText();
        int second = dateSecond.isBlank() || dateSecond.isEmpty() || dateSecond.equalsIgnoreCase(" ") ? currentDate.getSeconds() : Utils.parseInt(dateSecond);

        plan.inventoryData = new InventoryItemDetails();
        plan.inventoryData.translationTag = translationTagPlan.getText();
        plan.inventoryData.dateAdded = new Date(year - 1900, month - 1, day, hour, minute, second).getTime() / 1000;
        long highlGuid = Utils.parseLong(highlightSoundPlan.getText());
        plan.inventoryData.highlightSound = highlGuid < 1 ? null : new GUID(highlGuid);
        plan.inventoryData.colour = Colors.RGBA32.fromVector(new Vector4f(
                Utils.parseInt(rColorPlan.getText()) / 255f,
                Utils.parseInt(gColorPlan.getText()) / 255f,
                Utils.parseInt(bColorPlan.getText()) / 255f,
                Utils.parseInt(aColorPlan.getText()) / 255f));
        EnumSet<InventoryObjectType> types = EnumSet.noneOf(InventoryObjectType.class);

        for(Checkbox typeCB : typePlan)
            if(typeCB.isChecked)
            {
                InventoryObjectType type = InventoryObjectType.valueOf(typeCB.id);
                types.add(type);
            }

        plan.inventoryData.type = types;

        try {
            for(Element e : subTypeList.elements)
                if(e instanceof Checkbox)
                    Utils.setBitwiseBool(plan.inventoryData.subType, InventoryObjectSubType.class.getDeclaredField(e.id).getInt(new InventoryObjectSubType()), ((Checkbox)e).isChecked);
        } catch (Exception ex) {
            print.error(ex);
            mainView.pushError("Build Plan Error", ex.getLocalizedMessage());
        }

        plan.inventoryData.titleKey = Utils.parseLong(titleKeyPlan.getText());
        plan.inventoryData.descriptionKey = Utils.parseLong(descriptionKeyPlan.getText());

        plan.inventoryData.userCreatedDetails = new UserCreatedDetails();
        plan.inventoryData.userCreatedDetails.name = userCreatedNamePlan.getText();
        plan.inventoryData.userCreatedDetails.description = userCreatedDescriptionPlan.getText();

        plan.inventoryData.creationHistory = new CreationHistory();
        plan.inventoryData.creationHistory.creators = creatorHistoryListPlan.toArray(new String[]{});

        plan.inventoryData.icon = icon;
        plan.inventoryData.locationIndex = Utils.parseShort(locationIndexPlan.getText());
        plan.inventoryData.categoryIndex = Utils.parseShort(categoryIndexPlan.getText());
        plan.inventoryData.primaryIndex = Utils.parseShort(primaryIndexPlan.getText());
        plan.inventoryData.lastUsed = Utils.parseInt(lastUsedPlan.getText());
        plan.inventoryData.numUses = Utils.parseInt(numUsesPlan.getText());
        plan.inventoryData.fluffCost = Utils.parseInt(fluffCostPlan.getText());
        plan.inventoryData.allowEmit = allowEmitPlan.isChecked;
        plan.inventoryData.shareable = shareablePlan.isChecked;
        plan.inventoryData.copyright = copyrightPlan.isChecked;
        String creator = creatorPlan.getText();
        plan.inventoryData.creator = creator.isEmpty() || creator.isBlank() || creator.equalsIgnoreCase(" ") ? null : new NetworkPlayerID(creator);
        plan.inventoryData.toolType = toolTypePln;
        plan.inventoryData.flags = InventoryItemFlags.NONE;

        Utils.setBitwiseBool(plan.inventoryData.flags, InventoryItemFlags.ALLOW_EMIT, allowEmitPlan.isChecked);
        Utils.setBitwiseBool(plan.inventoryData.flags, InventoryItemFlags.COPYRIGHT, copyrightPlan.isChecked);
        Utils.setBitwiseBool(plan.inventoryData.flags, InventoryItemFlags.USED, used.isChecked);
        Utils.setBitwiseBool(plan.inventoryData.flags, InventoryItemFlags.HIDDEN_ITEM, hiddenItem.isChecked);
        Utils.setBitwiseBool(plan.inventoryData.flags, InventoryItemFlags.RESTRICTED_LEVEL, restrictedLevel.isChecked);
        Utils.setBitwiseBool(plan.inventoryData.flags, InventoryItemFlags.RESTRICTED_POD, restrictedPod.isChecked);
        Utils.setBitwiseBool(plan.inventoryData.flags, InventoryItemFlags.DISABLE_LOOP_PREVIEW, disableLoopPreview.isChecked);

        plan.inventoryData.makeSizeProportional = makeSizeProportionalPlan.isChecked;
        plan.inventoryData.location = Utils.parseLong(locationPlan.getText());
        plan.inventoryData.category = Utils.parseLong(categoryPlan.getText());
        plan.inventoryData.locationTag = locationTagPlan.getText();
        plan.inventoryData.categoryTag = categoryTagPlan.getText();

        return plan;
    }

    public SerializationData buildBin(Revision revision, boolean includeThings)
    {
        RLevel level = new RLevel();

        level.world = this.worldThing;

        if(!level.world.hasPart(Part.WORLD))
            level.world.setPart(Part.WORLD, new PWorld());

        PWorld world = ((PWorld)level.world.getPart(Part.WORLD));

        world.things = includeThings ? mainView.buildThingArrayList(selectionOnlyExportBin.isChecked) : new ArrayList<>();
        world.things.add(0, worldThing);
        world.thingUIDCounter = world.things.get(world.things.size() - 1).UID;

        byte compressionFlags = CompressionFlags.USE_NO_COMPRESSION;
        Utils.setBitwiseBool(compressionFlags, CompressionFlags.USE_COMPRESSED_INTEGERS, compressionFlagsIntBin.isChecked);
        Utils.setBitwiseBool(compressionFlags, CompressionFlags.USE_COMPRESSED_VECTORS, compressionFlagsVecBin.isChecked);
        Utils.setBitwiseBool(compressionFlags, CompressionFlags.USE_COMPRESSED_MATRICES, compressionFlagsMatBin.isChecked);

        return level.build(revision, compressionFlags);
    }

    @Override
    public void secondaryThread() {
        thingPart.addParts(worldThingA);
        super.secondaryThread();
    }

    private void presetLevel()
    {
        worldThing.setPart(Part.BODY, new PBody());
        worldThing.setPart(Part.POS, new PPos());
        worldThing.setPart(Part.EFFECTOR, new PEffector());
        worldThing.setPart(Part.GAMEPLAY_DATA, new PGameplayData());
        worldThing.setPart(Part.WORLD, new PWorld());

        PMetadata metadata = new PMetadata();
        metadata.type = EnumSet.of(InventoryObjectType.BACKGROUND);
        worldThing.setPart(Part.METADATA, metadata);

        PScript script = new PScript();
        script.instance = new ScriptInstance();
        script.instance.script = new ResourceDescriptor(19744l, ResourceType.SCRIPT);
        script.instance.instanceLayout = new InstanceLayout();
        script.instance.instanceLayout.fields = new ArrayList<>();

        {
            FieldLayoutDetails field = new FieldLayoutDetails();
            field.name = "PlayerIndicatorList";
            field.modifiers = EnumSet.of(ModifierType.PRIVATE, ModifierType.DIVERGENT);
            field.machineType = MachineType.OBJECT_REF;
            field.fishType = BuiltinType.VOID;
            field.instanceOffset = 0;
            field.arrayBaseMachineType = MachineType.OBJECT_REF;
            script.instance.instanceLayout.fields.add(field);
        }
        {
            FieldLayoutDetails field = new FieldLayoutDetails();
            field.name = "DisplayNameFrame";
            field.modifiers = EnumSet.of(ModifierType.PRIVATE, ModifierType.DIVERGENT);
            field.machineType = MachineType.S32;
            field.fishType = BuiltinType.S32;
            field.instanceOffset = 4;
            field.arrayBaseMachineType = MachineType.VOID;
            script.instance.instanceLayout.fields.add(field);
        }
        {
            FieldLayoutDetails field = new FieldLayoutDetails();
            field.name = "DisplayNameTimer";
            field.modifiers = EnumSet.of(ModifierType.PRIVATE, ModifierType.DIVERGENT);
            field.machineType = MachineType.S32;
            field.fishType = BuiltinType.S32;
            field.instanceOffset = 8;
            field.arrayBaseMachineType = MachineType.VOID;
            script.instance.instanceLayout.fields.add(field);
        }
        {
            FieldLayoutDetails field = new FieldLayoutDetails();
            field.name = "OmnesBlack";
            field.modifiers = EnumSet.of(ModifierType.PRIVATE, ModifierType.DIVERGENT);
            field.machineType = MachineType.OBJECT_REF;
            field.fishType = BuiltinType.VOID;
            field.instanceOffset = 12;
            field.arrayBaseMachineType = MachineType.VOID;
            script.instance.instanceLayout.fields.add(field);
        }
        {
            FieldLayoutDetails field = new FieldLayoutDetails();
            field.name = "CurrentChallenge";
            field.modifiers = EnumSet.of(ModifierType.PRIVATE);
            field.machineType = MachineType.SAFE_PTR;
            field.fishType = BuiltinType.VOID;
            field.instanceOffset = 16;
            field.arrayBaseMachineType = MachineType.VOID;
            script.instance.instanceLayout.fields.add(field);
        }
        {
            FieldLayoutDetails field = new FieldLayoutDetails();
            field.name = "ScoreMultiplier";
            field.modifiers = EnumSet.of(ModifierType.PRIVATE);
            field.machineType = MachineType.S32;
            field.fishType = BuiltinType.S32;
            field.instanceOffset = 20;
            field.arrayBaseMachineType = MachineType.VOID;
            field.value = 0;
            script.instance.instanceLayout.fields.add(field);
        }
        {
            FieldLayoutDetails field = new FieldLayoutDetails();
            field.name = "LastScoreTimer";
            field.modifiers = EnumSet.of(ModifierType.PRIVATE);
            field.machineType = MachineType.S32;
            field.fishType = BuiltinType.S32;
            field.instanceOffset = 24;
            field.arrayBaseMachineType = MachineType.VOID;
            field.value = 0;
            script.instance.instanceLayout.fields.add(field);
        }
        {
            FieldLayoutDetails field = new FieldLayoutDetails();
            field.name = "LastScoreFrame";
            field.modifiers = EnumSet.of(ModifierType.PRIVATE);
            field.machineType = MachineType.S32;
            field.fishType = BuiltinType.S32;
            field.instanceOffset = 28;
            field.arrayBaseMachineType = MachineType.VOID;
            field.value = -1;
            script.instance.instanceLayout.fields.add(field);
        }
        {
            FieldLayoutDetails field = new FieldLayoutDetails();
            field.name = "Bonuses";
            field.modifiers = EnumSet.of(ModifierType.PRIVATE, ModifierType.DIVERGENT);
            field.machineType = MachineType.OBJECT_REF;
            field.fishType = BuiltinType.S32;
            field.instanceOffset = 32;
            field.arrayBaseMachineType = MachineType.OBJECT_REF;
            script.instance.instanceLayout.fields.add(field);
        }
        {
            FieldLayoutDetails field = new FieldLayoutDetails();
            field.name = "VCR";
            field.modifiers = EnumSet.of(ModifierType.PRIVATE);
            field.machineType = MachineType.OBJECT_REF;
            field.fishType = BuiltinType.VOID;
            field.instanceOffset = 36;
            field.arrayBaseMachineType = MachineType.VOID;
            field.type = ScriptObjectType.NULL;
            field.value = new ScriptObject();
            ((ScriptObject) field.value).type = ScriptObjectType.INSTANCE;
            ((ScriptObject) field.value).value = new ScriptInstance();
            ((ScriptInstance)((ScriptObject) field.value).value).script = new ResourceDescriptor(60292l, ResourceType.SCRIPT);
            script.instance.instanceLayout.fields.add(field);
        }
        {
            FieldLayoutDetails field = new FieldLayoutDetails();
            field.name = "ActiveDirectControlPromptList";
            field.modifiers = EnumSet.of(ModifierType.PRIVATE);
            field.machineType = MachineType.OBJECT_REF;
            field.fishType = BuiltinType.VOID;
            field.instanceOffset = 40;
            field.arrayBaseMachineType = MachineType.SAFE_PTR;
            field.type = ScriptObjectType.NULL;
            field.value = new ScriptObject();
            ((ScriptObject)field.value).type = ScriptObjectType.ARRAY_SAFE_PTR;
            ((ScriptObject)field.value).value = new Thing[]{};

            script.instance.instanceLayout.fields.add(field);
        }
        {
            FieldLayoutDetails field = new FieldLayoutDetails();
            field.name = "Finish";
            field.modifiers = EnumSet.of(ModifierType.PRIVATE);
            field.machineType = MachineType.BOOL;
            field.fishType = BuiltinType.BOOL;
            field.instanceOffset = 44;
            field.arrayBaseMachineType = MachineType.VOID;
            field.value = false;
            script.instance.instanceLayout.fields.add(field);
        }
        script.instance.instanceLayout.instanceSize = 45;

        worldThing.setPart(Part.SCRIPT, script);
    }
    private void presetLBP1PodBG()
    {
        worldThing.setPart(Part.BODY, new PBody());
        worldThing.setPart(Part.POS, new PPos());
        worldThing.setPart(Part.EFFECTOR, new PEffector());
        worldThing.setPart(Part.GAMEPLAY_DATA, new PGameplayData());
        worldThing.setPart(Part.WORLD, new PWorld());

        PScript script = new PScript();
        script.instance = new ScriptInstance();
        script.instance.script = new ResourceDescriptor(20316l, ResourceType.SCRIPT);

        PMetadata metadata = new PMetadata();
        metadata.type = EnumSet.of(InventoryObjectType.BACKGROUND);
        worldThing.setPart(Part.METADATA, metadata);

        worldThing.setPart(Part.SCRIPT, script);
    }

    private void presetBGPlan()
    {
        used.isChecked = false;
        hiddenItem.isChecked = false;
        restrictedLevel.isChecked = false;
        restrictedPod.isChecked = false;
        disableLoopPreview.isChecked = false;

        for(Checkbox c : typePlan)
            if(c.id.equalsIgnoreCase(InventoryObjectType.BACKGROUND.name()))
                c.isChecked = true;
            else
                c.isChecked = false;

        madeByAnyRBPlan.isChecked = false;
        madeByOthersRBPlan.isChecked = false;
        madeByMeRBPlan.isChecked = false;

        earthRBPlan.isChecked = false;
        moonRBPlan.isChecked = false;
        adventureRBPlan.isChecked = false;
        externalRBPlan.isChecked = false;

        nonSackboyRBPlan.isChecked = false;
        giantRBPlan.isChecked = false;
        dwarfRBPlan.isChecked = false;
        birdRBPlan.isChecked = false;
        quadRBPlan.isChecked = false;
    }

    private void presetObjectPlan()
    {
        used.isChecked = false;
        hiddenItem.isChecked = false;
        restrictedLevel.isChecked = false;
        restrictedPod.isChecked = false;
        disableLoopPreview.isChecked = false;

        for(Checkbox c : typePlan)
            if(c.id.equalsIgnoreCase(InventoryObjectType.READYMADE.name()))
                c.isChecked = true;
            else
                c.isChecked = false;

        madeByAnyRBPlan.isChecked = false;
        madeByOthersRBPlan.isChecked = false;
        madeByMeRBPlan.isChecked = false;

        earthRBPlan.isChecked = false;
        moonRBPlan.isChecked = false;
        adventureRBPlan.isChecked = false;
        externalRBPlan.isChecked = false;

        nonSackboyRBPlan.isChecked = false;
        giantRBPlan.isChecked = false;
        dwarfRBPlan.isChecked = false;
        birdRBPlan.isChecked = false;
        quadRBPlan.isChecked = false;
    }

    private void presetMaterialPlan()
    {
        used.isChecked = false;
        hiddenItem.isChecked = false;
        restrictedLevel.isChecked = false;
        restrictedPod.isChecked = false;
        disableLoopPreview.isChecked = false;

        for(Checkbox c : typePlan)
            if(c.id.equalsIgnoreCase(InventoryObjectType.PRIMITIVE_MATERIAL.name()))
                c.isChecked = true;
            else
                c.isChecked = false;

        madeByAnyRBPlan.isChecked = false;
        madeByOthersRBPlan.isChecked = false;
        madeByMeRBPlan.isChecked = false;

        earthRBPlan.isChecked = false;
        moonRBPlan.isChecked = false;
        adventureRBPlan.isChecked = false;
        externalRBPlan.isChecked = false;

        nonSackboyRBPlan.isChecked = false;
        giantRBPlan.isChecked = false;
        dwarfRBPlan.isChecked = false;
        birdRBPlan.isChecked = false;
        quadRBPlan.isChecked = false;
    }

    private void importMetadataBin()
    {
        RLevel lvl = null;
        String name = "level.bin";

        try
        {
            File file = FileChooser.openFile(null, "bin,lvl", false);

            if(file != null)
            {
                name = file.getName();
                lvl = new Resource(file.getAbsolutePath()).loadResource(RLevel.class);
            }
        }catch (Exception e)
        {
            print.stackTrace(e);
            mainView.pushError("File/Resource Error", print.stackTraceAsString(e));
        }

        if(lvl != null)
            if(lvl.world != null)
                importMetadataBin(lvl);
    }

    public void importMetadataBin(RLevel lvl)
    {
        for(Part part : Part.values())
            worldThing.setPart(part, lvl.world.getPart(part));
    }

    private void importMetadataPlan()
    {
        RPlan pln = null;
        String name = "some_kind_of_.plan";

        try
        {
            File file = FileChooser.openFile(null, "plan,pln", false);

            if(file != null)
            {
                name = file.getName();
                pln = new Resource(file.getAbsolutePath()).loadResource(RPlan.class);
            }
        }catch (Exception e)
        {
            print.stackTrace(e);
            mainView.pushError("File/Resource Error", print.stackTraceAsString(e));
        }

        if(pln != null)
            importMetadataPlan(pln);
    }

    public void importMetadataPlan(RPlan pln)
    {
        if(pln.inventoryData.userCreatedDetails != null)
        {
            userCreatedNamePlan.setText(pln.inventoryData.userCreatedDetails.name);
            userCreatedDescriptionPlan.setText(pln.inventoryData.userCreatedDetails.description);
        }
        titleKeyPlan.setText(String.valueOf(pln.inventoryData.titleKey));
        descriptionKeyPlan.setText(String.valueOf(pln.inventoryData.descriptionKey));
        creatorPlan.setText(pln.inventoryData.creator.toString());
        creatorHistoryListPlan.clear();
        creatorHistoryListPlan.addAll(List.of(pln.inventoryData.creationHistory.creators));
        iconPlan.setText(pln.inventoryData.icon.toString());
        allowEmitPlan.isChecked = pln.inventoryData.allowEmit || Utils.isBitwiseBool(pln.inventoryData.flags, InventoryItemFlags.ALLOW_EMIT);
        shareablePlan.isChecked = pln.inventoryData.shareable;
        copyrightPlan.isChecked = pln.inventoryData.copyright || Utils.isBitwiseBool(pln.inventoryData.flags, InventoryItemFlags.COPYRIGHT);
        categoryPlan.setText(String.valueOf(pln.inventoryData.category));
        categoryTagPlan.setText(pln.inventoryData.categoryTag);
        categoryIndexPlan.setText(String.valueOf(pln.inventoryData.categoryIndex));
        locationPlan.setText(String.valueOf(pln.inventoryData.location));
        locationTagPlan.setText(pln.inventoryData.locationTag);
        locationIndexPlan.setText(String.valueOf(pln.inventoryData.locationIndex));

        for(Checkbox c : typePlan)
            c.isChecked = pln.inventoryData.type.contains(InventoryObjectType.valueOf(c.id));

        try {
            for(Element e : subTypeList.elements)
                if(e instanceof Checkbox)
                    ((Checkbox)e).isChecked = Utils.isBitwiseBool(pln.inventoryData.subType, InventoryObjectSubType.class.getDeclaredField(e.id).getInt(new InventoryObjectSubType()));
        } catch (Exception ex) {print.error(ex);}

        used.isChecked = Utils.isBitwiseBool(pln.inventoryData.flags, InventoryItemFlags.USED);
        hiddenItem.isChecked = Utils.isBitwiseBool(pln.inventoryData.flags, InventoryItemFlags.HIDDEN_ITEM);
        restrictedLevel.isChecked = Utils.isBitwiseBool(pln.inventoryData.flags, InventoryItemFlags.RESTRICTED_LEVEL);
        restrictedPod.isChecked = Utils.isBitwiseBool(pln.inventoryData.flags, InventoryItemFlags.RESTRICTED_POD);
        disableLoopPreview.isChecked = Utils.isBitwiseBool(pln.inventoryData.flags, InventoryItemFlags.DISABLE_LOOP_PREVIEW);

        toolTypePln = pln.inventoryData.toolType;

        numUsesPlan.setText(String.valueOf(pln.inventoryData.numUses));
        lastUsedPlan.setText(String.valueOf(pln.inventoryData.lastUsed));
        fluffCostPlan.setText(String.valueOf(pln.inventoryData.fluffCost));
        makeSizeProportionalPlan.isChecked = pln.inventoryData.makeSizeProportional;
        primaryIndexPlan.setText(String.valueOf(pln.inventoryData.primaryIndex));
        translationTagPlan.setText(pln.inventoryData.translationTag);

        Vector4f color = Colors.RGBA32.toVector(pln.inventoryData.colour);
        rColorPlan.setText(String.valueOf(Math.round(color.x * 255f)));
        gColorPlan.setText(String.valueOf(Math.round(color.y * 255f)));
        bColorPlan.setText(String.valueOf(Math.round(color.z * 255f)));
        aColorPlan.setText(String.valueOf(Math.round(color.w * 255f)));

        if(pln.inventoryData.highlightSound != null)
            highlightSoundPlan.setText(pln.inventoryData.highlightSound.toString());

        if(pln.inventoryData.dateAdded != 0)
        {
            Date date = new Date(pln.inventoryData.dateAdded / 1000);
            dateAddedDayPlan.setText(String.valueOf(date.getDate()));
            dateAddedMonthPlan.setText(String.valueOf(date.getMonth()));
            dateAddedYearPlan.setText(String.valueOf(date.getYear()));
            dateAddedHourPlan.setText(String.valueOf(date.getHours()));
            dateAddedMinutePlan.setText(String.valueOf(date.getMinutes()));
            dateAddedSecondPlan.setText(String.valueOf(date.getSeconds()));
        }
    }

    boolean importPlanMeta = false;
    boolean importBinMeta = false;

    public void loaderThread()
    {
        if(importPlanMeta)
        {
            importMetadataPlan();
            importPlanMeta = false;
        }
        if(importBinMeta)
        {
            importMetadataBin();
            importBinMeta = false;
        }
    }

    static class FileClipboard implements Serializable {
        private static final long serialVersionUID = 1L;
        String name;
        byte[] bytes;

        public FileClipboard(String name, byte[] bytes) {
            this.name = name;
            this.bytes = bytes;
        }
    }

    static class DataPackageTransferable implements Transferable {
        private final FileClipboard dataPackage;

        private static final DataFlavor DATA_FLAVOR = new DataFlavor(FileClipboard.class, "FileClipboard");

        public DataPackageTransferable(FileClipboard dataPackage) {
            this.dataPackage = dataPackage;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{DATA_FLAVOR};
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return DATA_FLAVOR.equals(flavor);
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
            if (!DATA_FLAVOR.equals(flavor)) {
                throw new UnsupportedFlavorException(flavor);
            }
            return dataPackage;
        }
    }
}