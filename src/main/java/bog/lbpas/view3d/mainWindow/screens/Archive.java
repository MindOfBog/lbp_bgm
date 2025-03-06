package bog.lbpas.view3d.mainWindow.screens;

import bog.lbpas.view3d.mainWindow.LoadedData;
import bog.lbpas.view3d.mainWindow.View3D;
import bog.lbpas.view3d.managers.MouseInput;
import bog.lbpas.view3d.renderer.gui.GuiScreen;
import bog.lbpas.view3d.renderer.gui.elements.*;
import bog.lbpas.view3d.utils.FilePicker;
import bog.lbpas.view3d.utils.print;
import cwlib.enums.ResourceType;
import cwlib.resources.RPlan;
import cwlib.resources.RTranslationTable;
import cwlib.types.Resource;
import cwlib.types.archives.FileArchive;
import cwlib.types.data.ResourceDescriptor;
import cwlib.types.databases.FileDB;
import cwlib.types.databases.FileDBRow;
import cwlib.types.databases.FileEntry;
import cwlib.types.save.BigSave;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;

/**
 * @author Bog
 */
public class Archive extends GuiScreen {

    View3D mainView;
    DropDownTab mapList;
    ButtonList maps;
    Button loadMAP;
    DropDownTab farcList;
    ButtonList farcs;
    Button loadFARC;
    DropDownTab fartList;
    ButtonList farts;
    Button loadFART;
    DropDownTab translations;

    public Archive(View3D mainView)
    {
        super(mainView.renderer, mainView.loader, mainView.window);
        this.mainView = mainView;
        init();
    }

    public void init()
    {
        mapList = new DropDownTab("mapList", "MAPs", new Vector2f(10, 21 + 10), new Vector2f(200, getFontHeight(10) + 4), 10, renderer, loader, window)
        {

        };
        loadMAP = mapList.addButton("Load", new Button() {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if (button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)
                    FilePicker.loadMap();
            }
        });
        maps = mapList.addList("maps", new ButtonList(LoadedData.MAPs, 10, renderer, loader, window) {
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
                return ((FileDB)object).getName();
            }

            @Override
            public boolean searchFilter(Object object, int index) {
                return true;
            }

            @Override
            public int buttonHeight() {
                return getFontHeight(10) + 4;
            }
        }.deletable().draggable(), 150);

        farcList = new DropDownTab("farcList", "FARCs", new Vector2f(7 * 2 + 3 + 200, 21 + 10), new Vector2f(200, getFontHeight(10) + 4), 10, renderer, loader, window)
        {

        };
        loadFARC = farcList.addButton("Load", new Button() {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if (button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)
                    FilePicker.loadFarc();
            }
        });
        farcs = farcList.addList("farcs", new ButtonList(LoadedData.FARCs, 10, renderer, loader, window) {
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
                return ((FileArchive)object).getFile().getName();
            }

            @Override
            public boolean searchFilter(Object object, int index) {
                return true;
            }

            @Override
            public int buttonHeight() {
                return getFontHeight(10) + 4;
            }
        }.deletable().draggable(), 150);

        fartList = new DropDownTab("fartList", "BIG Profiles", new Vector2f(7 * 3 + 3 + 400, 21 + 10), new Vector2f(200, getFontHeight(10) + 4), 10, renderer, loader, window)
        {

        };
        loadFART = fartList.addButton("Load", new Button() {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if (button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)
                    FilePicker.loadFart();
            }
        });
        farts = fartList.addList("farts", new ButtonList(LoadedData.BIGFARTs, 10, renderer, loader, window) {
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
                return ((BigSave)object).getFile().getName();
            }

            @Override
            public boolean searchFilter(Object object, int index) {
                return true;
            }

            @Override
            public int buttonHeight() {
                return getFontHeight(10) + 4;
            }
        }.deletable().draggable(), 150);

        translations = new DropDownTab("translations", "Translations", new Vector2f(10, 21 + 10 + mapList.getFullHeight() + 7), new Vector2f(325, getFontHeight(10) + 4), 10, renderer, loader, window);
        translations.addButton("None", new Button() {
            @Override
            public void clickedButton(int button, int action, int mods) {
                LoadedData.loadedTranslationTable = null;
                LoadedData.loadedPatchTranslationTable = null;
                LoadedData.loadedTranslation = -1;
            }
        });
        Panel searchTranslationsPanel = translations.addPanel("Panel");
        searchTranslationsPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("searchTranslStr", "Search:", 10, renderer), 0.225f));
        Textbox searchTransl = new Textbox("searchTransl", new Vector2f(), new Vector2f(), 10, renderer, loader, window);
        searchTranslationsPanel.elements.add(new Panel.PanelElement(searchTransl, 0.775f));
        farts = translations.addList("transl", new ButtonList(LoadedData.digestedEntries, 10, renderer, loader, window) {
            @Override
            public void clickedButton(Object object, int index, int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)
                {
                    FileEntry entry = (FileEntry) object;
                    ResourceDescriptor descriptor = LoadedData.digestedEntriesDescriptors.get(LoadedData.digestedEntries.indexOf(entry));

                    ResourceDescriptor descriptorPatch = null;
                    for(FileEntry e : LoadedData.digestedEntries)
                        if(e.getPath().equalsIgnoreCase("gamedata/languages/patch/" + entry.getName()))
                            descriptorPatch = LoadedData.digestedEntriesDescriptors.get(LoadedData.digestedEntries.indexOf(e));

                    byte[] data = LoadedData.extract(descriptor);
                    if (data == null)
                    {
                        print.error("Failed loading Translation: extracted data null");
                        return;
                    }

                    RTranslationTable translation = new RTranslationTable(data);

                    if(translation != null)
                    {
                        LoadedData.loadedTranslationTable = translation;
                        LoadedData.loadedTranslation = descriptor.getGUID().getValue();
                    }

                    if(descriptorPatch != null)
                    {
                        byte[] dataP = LoadedData.extract(descriptorPatch);
                        if (dataP == null)
                        {
                            print.error("Failed loading Translation Patch: extracted data null");
                            return;
                        }

                        RTranslationTable translationP = new RTranslationTable(dataP);

                        if(translationP != null)
                            LoadedData.loadedPatchTranslationTable = translationP;
                    }
                }
            }

            int hovering = -1;

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
                int ind = LoadedData.digestedEntries.indexOf(object);
                if(ind < 0)
                    return false;
                return LoadedData.loadedTranslation == LoadedData.digestedEntriesDescriptors.get(ind).getGUID().getValue();
            }

            @Override
            public String buttonText(Object object, int index) {
                FileEntry entry = (FileEntry) object;

                String name = entry.getPath().substring(entry.getPath().lastIndexOf("/") + 1);

                int extInd = name.lastIndexOf(".");
                boolean nameIsHash = name.substring(0, extInd != -1 ? extInd : name.length()).equalsIgnoreCase(entry.getSHA1().toString());

                if(!(entry instanceof FileDBRow) && nameIsHash)
                    return LoadedData.digestedEntriesDescriptors.get(index).getType().getHeader().toLowerCase() + "_" + name.substring(name.length() - 12);

                return entry.getPath().substring(19);
            }

            @Override
            public boolean searchFilter(Object object, int index) {
                FileEntry entry = (FileEntry) object;
                return !entry.getPath().startsWith("gamedata/languages/patch/") && entry.getPath().endsWith(".trans") && entry.getPath().contains(searchTransl.getText());
            }

            @Override
            public int buttonHeight() {
                return getFontHeight(10) + 4;
            }

            @Override
            public void draw(MouseInput mouseInput, boolean overElement) {
                hovering = -1;
                super.draw(mouseInput, overElement);
            }
        }, 150);

        this.guiElements.add(mapList);
        this.guiElements.add(farcList);
        this.guiElements.add(fartList);
        this.guiElements.add(translations);
    }
}