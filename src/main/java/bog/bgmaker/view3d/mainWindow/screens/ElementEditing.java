package bog.bgmaker.view3d.mainWindow.screens;

import bog.bgmaker.view3d.core.Transformation3D;
import bog.bgmaker.view3d.core.types.Entity;
import bog.bgmaker.view3d.core.types.MaterialPrimitive;
import bog.bgmaker.view3d.core.types.Mesh;
import bog.bgmaker.view3d.core.types.WorldAudio;
import bog.bgmaker.view3d.mainWindow.LoadedData;
import bog.bgmaker.view3d.mainWindow.View3D;
import bog.bgmaker.view3d.managers.MouseInput;
import bog.bgmaker.view3d.renderer.gui.GuiScreen;
import bog.bgmaker.view3d.renderer.gui.elements.Button;
import bog.bgmaker.view3d.renderer.gui.elements.*;
import bog.bgmaker.view3d.utils.Config;
import bog.bgmaker.view3d.utils.Utils;
import common.FileChooser;
import cwlib.enums.Part;
import cwlib.enums.ResourceType;
import cwlib.ex.SerializationException;
import cwlib.resources.RLevel;
import cwlib.resources.RPlan;
import cwlib.structs.things.Thing;
import cwlib.structs.things.parts.PShape;
import cwlib.structs.things.parts.PWorld;
import cwlib.types.Resource;
import cwlib.types.archives.FileArchive;
import cwlib.types.data.ResourceDescriptor;
import cwlib.types.databases.FileDB;
import cwlib.types.databases.FileDBRow;
import cwlib.types.databases.FileEntry;
import cwlib.types.save.BigSave;
import org.joml.Math;
import org.joml.*;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * @author Bog
 */
public class ElementEditing extends GuiScreen {

    public Transformation3D.Tool elementTool;
    View3D mainView;

    public ElementEditing(View3D mainView)
    {
        super(mainView.renderer, mainView.loader, mainView.window);
        this.mainView = mainView;
        this.init();
    }
    public DropDownTab camPos;
    public DropDownTab currentSelection;
    public DropDownTab fileLoading;
    public DropDownTab helpers;
    public DropDownTab availableAssets;
    public ButtonList assetList;
    public Element loadedEntitiesHitbox;
    public ButtonList loadedEntities;
    public Button loadPlanElements;
    public DropDownTab.LabeledTextbox loadedEntitiesSearch;
    public Button clearAllEntites;
    public Button deleteSelectedEntities;
    public Button sortEntityList;
    public ButtonImage move;
    public ButtonImage rotate;
    public ButtonImage scale;
    public ButtonImage materialEdit;

    public void init() {
        elementTool = new Transformation3D.Tool(mainView.loader);

        camPos = new DropDownTab("camPosition", "Camera position", new Vector2f(7, 39), new Vector2f(200, getFontHeight(10) + 4), 10, mainView.renderer, mainView.loader, mainView.window) {
            @Override
            public void secondThread() {
                super.secondThread();

                Vector2f camX = setTextboxValueFloat(((DropDownTab.LabeledTextbox) this.tabElements.get(0)).textbox, mainView.camera.pos.x);
                if (camX.y == 1)
                    mainView.camera.pos.x = camX.x;
                Vector2f camY = setTextboxValueFloat(((DropDownTab.LabeledTextbox) this.tabElements.get(1)).textbox, mainView.camera.pos.y);
                if (camY.y == 1)
                    mainView.camera.pos.y = camY.x;
                Vector2f camZ = setTextboxValueFloat(((DropDownTab.LabeledTextbox) this.tabElements.get(2)).textbox, mainView.camera.pos.z);
                if (camZ.y == 1)
                    mainView.camera.pos.z = camZ.x;
            }
        };
        camPos.addLabeledTextbox("x", "X:  ", true, false, false);
        camPos.addLabeledTextbox("y", "Y:  ", true, false, false);
        camPos.addLabeledTextbox("z", "Z:  ", true, false, false);

        currentSelection = new DropDownTab("currentSelection", "Current Selection", new Vector2f(521, 39), new Vector2f(200, getFontHeight(10) + 4), 10, mainView.renderer, mainView.loader, mainView.window) {
            @Override
            public void secondThread() {
                super.secondThread();

                boolean hasSelection = false;
                ArrayList<Integer> selected = new ArrayList<>();
                boolean hasMesh = false;
                boolean hasMaterial = false;
                boolean hasAudio = false;

                for (int i = 0; i < mainView.entities.size(); i++)
                    if (mainView.entities.get(i).selected) {
                        hasSelection = true;
                        selected.add(i);
                        switch (mainView.entities.get(i).getType())
                        {
                            case 0:
                                hasMesh = true;
                                break;
                            case 1:
                                hasMaterial = true;
                                break;
                            case 3:
                                hasAudio = true;
                                break;
                        }
                    }

                try {
                    String name = setTextboxValueString(((Textbox) this.tabElements.get(1)), mainView.getSelectedName());
                    if (name != null && selected.size() >= 1)
                        for (int i : selected)
                            mainView.entities.get(i).entityName = name;
                } catch (Exception e) {e.printStackTrace();}

                if(hasMesh && !hasMaterial && !hasAudio)
                {
                    this.addString("meshDescrLabel", "Mesh descriptor:");
                    this.addTextbox("meshDescriptor");

                    String mesh = setTextboxValueString(((Textbox) this.tabElements.get(7)), mainView.getSelectedMeshDescriptor());
                    if (mesh != null && selected.size() >= 1)
                        for (int i : selected) {
                            Entity entity = mainView.entities.get(i);

                            if (mesh.startsWith("h"))
                                mesh = mesh.substring(1);

                                try
                                {
                                    ResourceDescriptor desc = new ResourceDescriptor(mesh, ResourceType.MESH);
                                    if (!(desc.isGUID() ? desc.getGUID().toString() : desc.getSHA1().toString()).equalsIgnoreCase(((Mesh)entity).meshDescriptor.isGUID() ? ((Mesh)entity).meshDescriptor.getGUID().toString() : ((Mesh)entity).meshDescriptor.getSHA1().toString())) {
                                        ((Mesh)entity).meshDescriptor = desc;
                                        entity.reloadModel();
                                    }
                                }catch (Exception e){}
                        }
                }
                else
                {
                    this.removeElementByID("meshDescrLabel");
                    this.removeElementByID("meshDescriptor");
                }
                if(hasMaterial && !hasMesh && !hasAudio)
                {
                    this.addString("materialGfxMatDescrLabel", "Gfx descriptor:");
                    this.addTextbox("materialGfxMatDescriptor");
                    this.addString("materialMaterialDescrLabel", "Material descriptor:");
                    this.addTextbox("materialMaterialDescriptor");
                    this.addString("materialBevelDescrLabel", "Bevel descriptor:");
                    this.addTextbox("materialBevelDescriptor");
                    this.addString("materialThickLabel", "Thickness:");
                    this.addTextbox("materialThickness");
                    this.addString("materialBevelSizeLabel", "Bevel size:");
                    this.addTextbox("materialBevelSize");

                    String gmat = setTextboxValueString(((Textbox) this.tabElements.get(7)), mainView.getSelectedGfxMaterialDescriptor());
                    String mat = setTextboxValueString(((Textbox) this.tabElements.get(9)), mainView.getSelectedMaterialDescriptor());
                    String bev = setTextboxValueString(((Textbox) this.tabElements.get(11)), mainView.getSelectedBevelDescriptor());
                    Vector2f thickness = setTextboxValueFloat(((Textbox) this.tabElements.get(13)), mainView.getSelectedMaterialThickness());
                    Vector2f bevelSize = setTextboxValueFloat(((Textbox) this.tabElements.get(15)), mainView.getSelectedBevelSize());

                    if (selected.size() >= 1)
                        for (int i : selected) {
                            Entity entity = mainView.entities.get(i);

                            if(gmat != null)
                            {
                                if (gmat.startsWith("h"))
                                    gmat = gmat.substring(1);

                                try{
                                    ResourceDescriptor desc = new ResourceDescriptor(gmat, ResourceType.GFX_MATERIAL);
                                    if (desc != null && !(desc.isGUID() ? desc.getGUID().toString() : desc.getSHA1().toString()).equalsIgnoreCase(((MaterialPrimitive)entity).gmat.isGUID() ? ((MaterialPrimitive)entity).gmat.getGUID().toString() : ((MaterialPrimitive)entity).gmat.getSHA1().toString())) {
                                        ((MaterialPrimitive)entity).gmat = desc;
                                        entity.reloadModel();
                                    }
                                }catch (Exception e){}
                            }

                            if(mat != null)
                            {
                                if (mat.startsWith("h"))
                                    mat = mat.substring(1);

                                try{
                                    ResourceDescriptor desc = new ResourceDescriptor(mat, ResourceType.GFX_MATERIAL);
                                    if (desc != null && !(desc.isGUID() ? desc.getGUID().toString() : desc.getSHA1().toString()).equalsIgnoreCase(((MaterialPrimitive)entity).gmat.isGUID() ? ((MaterialPrimitive)entity).gmat.getGUID().toString() : ((MaterialPrimitive)entity).gmat.getSHA1().toString()))
                                        ((MaterialPrimitive)entity).mat = desc;
                                }catch (Exception e){}
                            }

                            if(bev != null)
                            {
                                if (bev.startsWith("h"))
                                    bev = bev.substring(1);

                                try{
                                    ResourceDescriptor desc = new ResourceDescriptor(bev, ResourceType.GFX_MATERIAL);
                                    if (desc != null && !(desc.isGUID() ? desc.getGUID().toString() : desc.getSHA1().toString()).equalsIgnoreCase(((MaterialPrimitive)entity).gmat.isGUID() ? ((MaterialPrimitive)entity).gmat.getGUID().toString() : ((MaterialPrimitive)entity).gmat.getSHA1().toString()))
                                        ((MaterialPrimitive)entity).bev = desc;
                                }catch (Exception e){}
                            }

                            if(thickness.y == 1)
                            {
                                ((MaterialPrimitive)entity).shape.thickness = thickness.x;
                                entity.reloadModel();
                            }

                            if(bevelSize.y == 1)
                                ((MaterialPrimitive)entity).shape.bevelSize = bevelSize.x;
                        }
                }
                else
                {
                    this.removeElementByID("materialGfxMatDescrLabel");
                    this.removeElementByID("materialGfxMatDescriptor");
                    this.removeElementByID("materialMaterialDescrLabel");
                    this.removeElementByID("materialMaterialDescriptor");
                    this.removeElementByID("materialBevelDescrLabel");
                    this.removeElementByID("materialBevelDescriptor");
                    this.removeElementByID("materialThickLabel");
                    this.removeElementByID("materialThickness");
                    this.removeElementByID("materialBevelSizeLabel");
                    this.removeElementByID("materialBevelSize");
                }
                if(hasAudio && !hasMesh && !hasMaterial)
                {
                    this.addString("audioLabel", "Audio:");
                    this.addTextbox("audio");
                    this.addString("audioVolumeLabel", "Volume:");
                    this.addSlider("audioVolume", 1, 0, 1);
                    this.addString("audioPitchLabel", "Pitch:");
                    this.addSlider("audioPitch", 0, -1, 1);

                    String soundName = setTextboxValueString(((Textbox) this.tabElements.get(7)), mainView.getSelectedSoundName());

                    StringElement audioVolumeLabel = (StringElement) this.tabElements.get(8);
                    StringElement audioPitchLabel = (StringElement) this.tabElements.get(10);
                    Slider audioVolume = (Slider) this.tabElements.get(9);
                    Slider audioPitch = (Slider) this.tabElements.get(11);

                    audioVolumeLabel.string = "Volume: " + Utils.round(audioVolume.getCurrentValue(), 2);
                    audioPitchLabel.string = "Pitch: " + Utils.round(audioPitch.getCurrentValue(), 2);

                    Vector2f volume = setSliderValue(audioVolume, mainView.getSelectedSoundVolume());
                    Vector2f pitch = setSliderValue(audioPitch, mainView.getSelectedSoundPitch());

                    if (selected.size() >= 1)
                        for (int i : selected)
                        {
                            Entity entity = mainView.entities.get(i);

                            if(volume.y == 1)
                                ((WorldAudio)entity).initialVolume = volume.x;

                            if(pitch.y == 1)
                                ((WorldAudio)entity).initialPitch = pitch.x;

                            if(soundName != null)
                                ((WorldAudio)entity).soundName = soundName;
                        }
                }
                else
                {
                    this.removeElementByID("audioLabel");
                    this.removeElementByID("audio");
                    this.removeElementByID("audioVolumeLabel");
                    this.removeElementByID("audioVolume");
                    this.removeElementByID("audioPitchLabel");
                    this.removeElementByID("audioPitch");
                }

                Vector3f selectedPos = mainView.getSelectedPosition();
                Vector2f posX = setTextboxValueFloat(((DropDownTab.LabeledTextbox) this.tabElements.get(3)).textbox, selectedPos.x);
                Vector2f posY = setTextboxValueFloat(((DropDownTab.LabeledTextbox) this.tabElements.get(4)).textbox, selectedPos.y);
                Vector2f posZ = setTextboxValueFloat(((DropDownTab.LabeledTextbox) this.tabElements.get(5)).textbox, selectedPos.z);
                if (posX.y == 1 || posY.y == 1 || posZ.y == 1)
                    mainView.setSelectedPosition(new Vector3f(posX.y == 1 ? posX.x : selectedPos.x, posY.y == 1 ? posY.x : selectedPos.y, posZ.y == 1 ? posZ.x : selectedPos.z));
            }
        };
        currentSelection.addString("", "Name:");
        currentSelection.addTextbox("name");
        currentSelection.addLabeledButton("pos", "Position:  ", "Go To", new bog.bgmaker.view3d.renderer.gui.elements.Button() {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if (button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS) {
                    Vector3f cpos = new Vector3f(mainView.getSelectedPosition());
                    if (!(Float.isNaN(cpos.x) || Float.isNaN(cpos.y) || Float.isNaN(cpos.z)))
                        mainView.camera.pos = cpos;
                }
            }
        });
        currentSelection.addLabeledTextbox("posX", "X:  ", true, false, false);
        currentSelection.addLabeledTextbox("posY", "Y:  ", true, false, false);
        currentSelection.addLabeledTextbox("posZ", "Z:  ", true, false, false);

        fileLoading = new DropDownTab("fileLoading", "File Loading", new Vector2f(7, 39 + camPos.getFullHeight() + 7), new Vector2f(200, getFontHeight(10) + 4), 10, mainView.renderer, mainView.loader, mainView.window);
        fileLoading.addCheckbox("legacyFileLoading", "Legacy file dialogue");
        fileLoading.addButton("map", "Load .MAP", new bog.bgmaker.view3d.renderer.gui.elements.Button() {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if (button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS) {
                    LoadedData.MAP = null;
                    try {
                        File map = FileChooser.openFile(null, "map", false, false)[0];
                        LoadedData.MAP = new FileDB(map);
                    } catch (Exception ex) {ex.printStackTrace();}

                    mainView.setupList();
                }
            }
        });
        fileLoading.addButton("farc", "Load .FARCs", new bog.bgmaker.view3d.renderer.gui.elements.Button() {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if (button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS) {
                    LoadedData.FARCs.clear();
                    File[] farcs = FileChooser.openFiles("farc");
                    if (farcs != null) {
                        if (farcs.length != 0)
                            for (File farc : farcs)
                            {
                                FileArchive archive = null;

                                try {
                                    archive = new FileArchive(farc);
                                } catch (SerializationException ex) {ex.printStackTrace();}

                                if (archive != null)
                                    LoadedData.FARCs.add(archive);
                            }
                    } else return;

                    mainView.setupList();
                }
            }
        });
        fileLoading.addButton("bigfart", "Load BIG Profile", new bog.bgmaker.view3d.renderer.gui.elements.Button() {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if (button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS) {
                    LoadedData.BIGFART = null;
                    try {
                        File fart = FileChooser.openFile(null, null, false, false)[0];
                        LoadedData.BIGFART = new BigSave(fart);
                    } catch (Exception ex) {ex.printStackTrace();}

                    mainView.setupList();
                }
            }
        });

        helpers = new DropDownTab("helpers", "Helpers", new Vector2f(7, 39 + camPos.getFullHeight() + 7 + fileLoading.getFullHeight() + 3), new Vector2f(200, getFontHeight(10) + 4), 10, mainView.renderer, mainView.loader, mainView.window).closed();
        helpers.addCheckbox("levelBorders", "Level borders", true);
        helpers.addCheckbox("podHelper", "Pod helper");
        helpers.addButton("podCam", "Pod cam", new bog.bgmaker.view3d.renderer.gui.elements.Button() {
            @Override
            public void clickedButton(int button, int action, int mods) {
                mainView.camera.pos = new Vector3f(4.673651f, 1565.6465f, 13882.88f);
                mainView.camera.rotation = new Vector3f(5.800001f, 0.20008372f, 0.0f);
            }
        });

        availableAssets = new DropDownTab("availableModels", "Available assets", new Vector2f(214, 39), new Vector2f(300, getFontHeight(10) + 4), 10, mainView.renderer, mainView.loader, mainView.window);
        availableAssets.addLabeledTextbox("availableEntitiesSearch", "Search:  ");

        assetList = new ButtonList("availableModelsList", mainView.entries, new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window) {

            @Override
            public void onClick(Vector2d pos, int button, int action, int mods, boolean overElement) {
                super.onClick(pos, button, action, mods, overElement);

                if (button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_RELEASE)
                    clicked = -1;
            }

            @Override
            public void clickedButton(Object object, int index, int button, int action, int mods) {
                FileEntry entry = (FileEntry) object;

                if (button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS) {
                    clicked = index;

                    if(entry.getPath().endsWith("mol"))
                    {
                        ResourceDescriptor descriptor = new ResourceDescriptor(entry.getSHA1(), ResourceType.MESH);

                        if(entry instanceof FileDBRow && ((FileDBRow)entry).getGUID() != null)
                            descriptor = new ResourceDescriptor(((FileDBRow)entry).getGUID(), ResourceType.MESH);

                        Vector3f pos = mainView.centerPicker.getPointOnRay(mainView.centerPicker.currentRay, 1000);
                        String name = entry.getPath().substring(entry.getPath().lastIndexOf("/") + 1);

                        if (descriptor.isHash())
                            name = name.substring(name.length() - 12);

                        for (Entity entity : mainView.entities)
                            entity.selected = false;

                        mainView.addMesh(
                                name.substring(0, name.lastIndexOf(".")),
                                descriptor,
                                new Matrix4f().identity().translate(pos)
                        );
                    }
                    else if(entry.getPath().endsWith("gmat") || entry.getPath().endsWith("gmt"))
                    {
                        ResourceDescriptor descriptor = new ResourceDescriptor(entry.getSHA1(), ResourceType.MESH);

                        if(entry instanceof FileDBRow && ((FileDBRow)entry).getGUID() != null)
                            descriptor = new ResourceDescriptor(((FileDBRow)entry).getGUID(), ResourceType.MESH);

                        Vector3f pos = mainView.centerPicker.getPointOnRay(mainView.centerPicker.currentRay, 1000);
                        String name = entry.getPath().substring(entry.getPath().lastIndexOf("/") + 1);

                        if (descriptor.isHash())
                            name = "material_" + name.substring(name.length() - 12);

                        for (Entity entity : mainView.entities)
                            entity.selected = false;

                        mainView.addMaterial(
                                name.substring(0, name.lastIndexOf(".")),
                                new PShape(),
                                descriptor,
                                new ResourceDescriptor(10790l, ResourceType.BEVEL),
                                new ResourceDescriptor(10716l, ResourceType.MATERIAL),
                                new Matrix4f().identity().translate(pos)
                        );
                    }
                }
            }

            @Override
            public void hoveringButton(Object object, int index) {
                hovering = index;
            }

            int hovering = -1;
            int clicked = -1;

            @Override
            public void draw(MouseInput mouseInput, boolean overElement) {
                hovering = -1;
                super.draw(mouseInput, overElement);
            }

            @Override
            public boolean isHighlighted(Object object, int index) {
                if (hovering == index) {
                    return true;
                }
                if (clicked == index)
                    clicked = -1;
                return false;
            }

            @Override
            public boolean isSelected(Object object, int index) {
                if (clicked == index)
                    return true;
                return false;
            }

            @Override
            public String buttonText(Object object, int index) {
                FileEntry entry = (FileEntry) object;

                String name = entry.getPath().substring(entry.getPath().lastIndexOf("/") + 1);

                if(!(entry instanceof FileDBRow))
                    return name.substring(name.length() - 12);

                return entry.getName();
            }

            Textbox searchTextbox;

            @Override
            public boolean searchFilter(Object object, int index) {
                FileEntry entry = (FileEntry) object;

                try {
                    if (searchTextbox == null)
                        searchTextbox = ((DropDownTab.LabeledTextbox) availableAssets.tabElements.get(0)).textbox;

                    if (entry.getPath().endsWith("mol") || entry.getPath().endsWith("gmat") || entry.getPath().endsWith("gmt")) {
                        String text = searchTextbox.getText();

                        if (text.isBlank() || text.isEmpty() || text.equalsIgnoreCase("") || text.equalsIgnoreCase(" "))
                            return true;

                        String descriptor = "h" + entry.getSHA1().toString();

                        if(entry instanceof FileDBRow && ((FileDBRow)entry).getGUID() != null)
                            descriptor = ((FileDBRow)entry).getGUID().toString();

                        for (String search : text.split(" "))
                            if (!(entry.getName().toLowerCase().contains(search.toLowerCase()) || descriptor.toLowerCase().contains(search.toLowerCase())))
                                return false;

                        return true;
                    }
                } catch (Exception e) {e.printStackTrace();}

                return false;
            }

            @Override
            public Color buttonColorSelected(Object object, int index) {
                return new Color(0.38f, 0.38f, 0.38f, 0.5f);
            }

            @Override
            public int buttonHeight() {
                return getFontHeight(fontSize) + 8;
            }
        };

        availableAssets.addList("availableModelsList", assetList, 500);

        loadedEntitiesHitbox = new Element() {
            @Override
            public void secondThread() {
                super.secondThread();

                this.pos = new Vector2f(mainView.window.width - 305, 0);
                this.size = new Vector2f(305, mainView.window.height);
            }
        };
        loadedEntitiesHitbox.pos = new Vector2f(mainView.window.width - 305, 0);
        loadedEntitiesHitbox.size = new Vector2f(305, mainView.window.height);
        loadedEntitiesHitbox.id = "loadedEntitiesHitbox";
        loadedEntities = new ButtonList("loadedEntities", mainView.entities, new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window) {
            int lastSelected = 0;
            @Override
            public void clickedButton(Object object, int index, int button, int action, int mods) {
                Entity entity = ((Entity) object);

                boolean ctrl = mods == GLFW.GLFW_MOD_CONTROL;
                boolean shift = mods == GLFW.GLFW_MOD_SHIFT;
                boolean ctrlshift = mods == GLFW.GLFW_MOD_SHIFT + GLFW.GLFW_MOD_CONTROL;
                boolean hasSelection = false;
                ArrayList<Integer> selected = new ArrayList<>();

                for (int i = 0; i < mainView.entities.size(); i++)
                    if (mainView.entities.get(i).selected) {
                        hasSelection = true;
                        selected.add(i);
                    }

                if (button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS) {
                    if (!hasSelection || ctrl)
                        entity.selected = !entity.selected;
                    else if(ctrlshift)
                    {
                        int min = lastSelected;
                        int max = index;
                        if(lastSelected > index)
                        {
                            min = index;
                            max = lastSelected;
                        }

                        for(int i = min; i <= max; i++)
                            if(searchFilter(mainView.entities.get(i), i))
                                mainView.entities.get(i).selected = true;
                    }else if(shift)
                    {
                        int min = lastSelected;
                        int max = index;
                        if(lastSelected > index)
                        {
                            min = index;
                            max = lastSelected;
                        }

                        for(int i : selected)
                            mainView.entities.get(i).selected = false;

                        for(int i = min; i <= max; i++)
                            if(searchFilter(mainView.entities.get(i), i))
                                mainView.entities.get(i).selected = true;
                    }
                    else {
                        for (Entity ent : mainView.entities)
                            if (ent != entity)
                                ent.selected = false;

                        entity.selected = !entity.selected;
                    }
                }

                lastSelected = index;
            }

            @Override
            public void hoveringButton(Object object, int index) {
                ((Entity) object).highlighted = true;
            }

            @Override
            public boolean isHighlighted(Object object, int index) {
                return ((Entity) object).highlighted;
            }

            @Override
            public boolean isSelected(Object object, int index) {
                return ((Entity) object).selected;
            }

            @Override
            public String buttonText(Object object, int index) {
                return ((Entity) object).entityName;
            }

            Textbox searchTextbox;

            @Override
            public boolean searchFilter(Object object, int index) {

                try {
                    if (searchTextbox == null)
                        searchTextbox = loadedEntitiesSearch.textbox;

                    String text = searchTextbox.getText();

                    if (text.isBlank() || text.isEmpty() || text.equalsIgnoreCase("") || text.equalsIgnoreCase(" "))
                        return true;

                    String descriptor = ((Mesh) object).meshDescriptor.isGUID() ? ((Mesh) object).meshDescriptor.getGUID().toString() : "h" + ((Mesh) object).meshDescriptor.getSHA1().toString();
                    for (String search : text.split(" "))
                        if (!(((Entity) object).entityName.toLowerCase().contains(search.toLowerCase()) || descriptor.toLowerCase().contains(search.toLowerCase())))
                            return false;

                    return true;
                } catch (Exception e) {}

                return false;
            }

            @Override
            public void secondThread() {
                super.secondThread();

                this.pos = new Vector2f(mainView.window.width - (302 - getFontHeight(10) - 8), 2 + Math.floor(getFontHeight(10) * 1.25f + 2) * 2);
                this.size = new Vector2f(302 - getFontHeight(10) - 8, mainView.window.height - (2 + Math.floor(getFontHeight(10) * 1.25f + 2) * 2 + (Math.floor(getFontHeight(10) * 1.25f) + 4)));
            }

            @Override
            public void drawButton(int posY, float scrollY, float scrollHeight, int height, Object object, int i) {
                super.drawButton(posY, scrollY, scrollHeight, height, object, i);
                endScissor();
                startScissor((int) pos.x - height, (int) scrollY, (int) size.x + 2 + height, (int) java.lang.Math.ceil(scrollHeight));
                Entity entity = (Entity) object;
                drawRect((int)pos.x - height + 2, posY, height, height, buttonColor(object, i));
                drawImageStatic(entity.getType() == 0 ? mainView.modelIcon : entity.getType() == 1 ? mainView.materialIcon : entity.getType() == 2 ? mainView.lightIcon : mainView.audioIcon, (int)pos.x - height + 2, posY, height, height);
                drawRectOutline((int)pos.x - height + 2, posY, height, height, buttonColor2(object, i), false);
            }

            @Override
            public int buttonHeight() {
                return getFontHeight(fontSize) + 8;
            }
        };
        loadPlanElements = new Button("loadPlanElements", "Load elements from .PLAN/.BIN", new Vector2f(mainView.window.width - 303, 2), new Vector2f(301, getFontHeight(10) * 1.25f), 10, mainView.renderer, mainView.loader, mainView.window) {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if (button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS) {
                    File file = null;
                    try {
                        file = FileChooser.openFile(null, "plan,bin", false, false)[0];
                    } catch (Exception e) {
                    }

                    if (file == null || !file.exists()) return;

                    for (Entity e : mainView.entities)
                        e.selected = false;

                    String ext = file.getAbsolutePath().toString();
                    ext = ext.substring(ext.lastIndexOf(".") + 1);

                    switch(ext)
                    {
                        case "plan":
                        case "pln":
                            try {
                                RPlan plan = new Resource(file.getAbsolutePath()).loadResource(RPlan.class);
                                if (plan == null) return;
                                Thing[] things = plan.getThings();

                                mainView.addThings(things);
                            } catch (Exception ex) {ex.printStackTrace();}
                            break;
                        case "bin":
                            try
                            {
                                RLevel level = new Resource(file.getAbsolutePath()).loadResource(RLevel.class);
                                if (level == null)
                                    return;
                                ArrayList<Thing> things = ((PWorld)level.world.getPart(Part.WORLD)).things;

                                mainView.addThings(things);
                            }catch (Exception ex) {ex.printStackTrace();}
                            break;
                        default:
                            System.err.println("Unknown background file type.");
                            break;
                    }
                }
            }

            @Override
            public void secondThread() {
                super.secondThread();

                this.pos.x = mainView.window.width - 303;
            }
        };
        loadedEntitiesSearch = new DropDownTab.LabeledTextbox("Search:  ", "loadedEntitiesSearch", new Vector2f(mainView.window.width - 303, (2 + getFontHeight(10) * 1.25f) + 2), new Vector2f(301, getFontHeight(10) * 1.25f), 10, mainView.renderer, mainView.loader, mainView.window) {
            @Override
            public void secondThread() {
                super.secondThread();

                this.pos.x = mainView.window.width - 303;
            }
        };
        clearAllEntites = new Button("clearAllEntities", "Clear all", new Vector2f(), new Vector2f(99f, getFontHeight(10) * 1.25f), 10, mainView.renderer, mainView.loader, mainView.window) {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if (button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS) {
                    mainView.entities.clear();
                }
            }

            @Override
            public void secondThread() {
                super.secondThread();

                this.pos = new Vector2f(mainView.window.width - 303, Math.ceil(mainView.window.height - getFontHeight(10) * 1.25f) - 2);
            }
        };
        deleteSelectedEntities = new Button("deleteSelectedEntities", "Delete", new Vector2f(), new Vector2f(99f, getFontHeight(10) * 1.25f), 10, mainView.renderer, mainView.loader, mainView.window) {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if (button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS) {
                    for (int i = mainView.entities.size() - 1; i >= 0; i--)
                        if (mainView.entities.get(i).selected)
                            mainView.entities.remove(i);
                }
            }

            @Override
            public void secondThread() {
                super.secondThread();

                this.pos = new Vector2f(mainView.window.width - 303 + 99f + 2, Math.ceil(mainView.window.height - getFontHeight(10) * 1.25f) - 2);
            }
        };
        sortEntityList = new Button("sortEntityList", "Sort list", new Vector2f(), new Vector2f(99f, getFontHeight(10) * 1.25f), 10, mainView.renderer, mainView.loader, mainView.window) {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if (button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS) {
                    Collections.sort(mainView.entities,
                            new Comparator<Entity>() {
                                @Override
                                public int compare(Entity e1, Entity e2) {
                                    return Integer.compare(e2.getType(), e1.getType());
                                }
                            }
                            .thenComparing(new Comparator<Entity>() {
                                @Override
                                public int compare(Entity e1, Entity e2) {
                                    return e1.entityName.compareToIgnoreCase(e2.entityName);
                                }
                            }));
                }
            }

            @Override
            public void secondThread() {
                super.secondThread();

                this.pos = new Vector2f(mainView.window.width - 303 + (99f + 2) * 2, Math.ceil(mainView.window.height - getFontHeight(10) * 1.25f) - 2);
            }
        };
        move = (new ButtonImage("move", "/textures/move.png", new Vector2f(mainView.window.width - 342, 7), new Vector2f(30, 30), mainView.renderer, mainView.loader, mainView.window) {
            @Override
            public void clickedButton(int button, int action, int mods) {
            }

            @Override
            public void onClick(Vector2d pos, int button, int action, int mods, boolean overOther) {
                if (button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos) && !overOther) {
                    isClicked = true;
                    rotate.isClicked = false;
                    scale.isClicked = false;
                }
            }

            @Override
            public void setClicked(boolean clicked) {
            }

            @Override
            public void secondThread() {
                super.secondThread();

                this.pos.x = mainView.window.width - 342;
            }
        }).clicked();
        rotate = new ButtonImage("rotate", "/textures/rotate.png", new Vector2f(mainView.window.width - 342, 44), new Vector2f(30, 30), mainView.renderer, mainView.loader, mainView.window) {
            @Override
            public void clickedButton(int button, int action, int mods) {
            }

            @Override
            public void onClick(Vector2d pos, int button, int action, int mods, boolean overOther) {
                if (button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos) && !overOther) {
                    isClicked = true;
                    move.isClicked = false;
                    scale.isClicked = false;
                }
            }

            @Override
            public void setClicked(boolean clicked) {
            }

            @Override
            public void secondThread() {
                super.secondThread();

                this.pos.x = mainView.window.width - 342;
            }
        };
        scale = new ButtonImage("scale", "/textures/scale.png", new Vector2f(mainView.window.width - 342, 81), new Vector2f(30, 30), mainView.renderer, mainView.loader, mainView.window) {
            @Override
            public void clickedButton(int button, int action, int mods) {
            }

            @Override
            public void setClicked(boolean clicked) {
            }

            @Override
            public void onClick(Vector2d pos, int button, int action, int mods, boolean overOther) {
                if (button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos) && !overOther) {
                    isClicked = true;
                    move.isClicked = false;
                    rotate.isClicked = false;
                }
            }

            @Override
            public void secondThread() {
                super.secondThread();

                this.pos.x = mainView.window.width - 342;
            }
        };
        materialEdit = new ButtonImage("materialEdit", "/textures/material edit.png", new Vector2f(mainView.window.width - 342, 118), new Vector2f(30, 30), mainView.renderer, mainView.loader, mainView.window) {
            @Override
            public void clickedButton(int button, int action, int mods) {
                mainView.setCurrentScreen(mainView.MaterialEditing);
            }

            @Override
            public void onClick(Vector2d pos, int button, int action, int mods, boolean overOther) {
                super.onClick(pos, button, action, mods, overOther);
            }

            @Override
            public void draw(MouseInput mouseInput, boolean overOther) {

                boolean hasSelection = false;
                ArrayList<Integer> selected = new ArrayList<>();

                for(int i = 0; i < mainView.entities.size(); i++)
                    if(mainView.entities.get(i).selected)
                    {
                        hasSelection = true;
                        selected.add(i);
                    }

                if(selected.size() == 1 && mainView.entities.get(selected.get(0)).getType() == 1)
                {
                    this.pos.x = mainView.window.width - 342;
                    super.draw(mouseInput, overOther);
                }
            }
        };

        this.guiElements.add(loadedEntitiesHitbox);
        this.guiElements.add(loadedEntities);
        this.guiElements.add(loadPlanElements);
        this.guiElements.add(loadedEntitiesSearch);
        this.guiElements.add(clearAllEntites);
        this.guiElements.add(deleteSelectedEntities);
        this.guiElements.add(sortEntityList);
        this.guiElements.add(move);
        this.guiElements.add(rotate);
        this.guiElements.add(scale);
        this.guiElements.add(materialEdit);

        this.guiElements.add(camPos);
        this.guiElements.add(fileLoading);
        this.guiElements.add(currentSelection);
        this.guiElements.add(helpers);
        this.guiElements.add(availableAssets);
    }

    @Override
    public void draw(MouseInput mouseInput) {

        Vector3f screenPos = mainView.camera.worldToScreenPointF(mainView.getSelectedPosition(), mainView.window);
        boolean overElement = false;
        boolean hasSelection = false;
        int selectedAmount = 0;

        for(Element element : guiElements)
            if (element.isMouseOverElement(mouseInput))
                overElement = true;

        for(Entity entity : mainView.entities)
            if(entity.selected)
            {
                hasSelection = true;
                selectedAmount++;
            }

        if(hasSelection)
            elementTool.updateModels(mainView.camera, screenPos, mainView.window);


        if(mouseInput.inWindow && elementTool.selected == -1)
        {
            for(Entity entity : mainView.entities)
                entity.highlighted = false;

            elementTool.testForMouse(hasSelection, mainView.camera, mainView.mousePicker,
                    move.isClicked,
                    rotate.isClicked,
                    scale.isClicked);
        }

        if(!mouseInput.inWindow || overElement)
            for(int i = 0; i < mainView.entities.size(); i++)
                mainView.entities.get(i).highlighted = false;

        if(elementTool.selected != -1)
        {
            switch (elementTool.selected)
            {
                //0,1,2 pos
                //3,4,5 rot
                //7,8,9 scale
                case 0:
                    //x pos
                {
                    float mousediff = (float) (mouseInput.currentPos.x - elementTool.initPos.x);
                    boolean h = false;

                    if (mainView.camera.getWrappedRotation().y > 60f) {
                        mousediff = -(float) (mouseInput.currentPos.y - elementTool.initPos.y);
                        h = true;
                    }

                    if (mainView.camera.getWrappedRotation().y < -60f) {
                        mousediff = (float) (mouseInput.currentPos.y - elementTool.initPos.y);
                        h = true;
                    }

                    if (mainView.camera.getWrappedRotation().y > 120f || mainView.camera.getWrappedRotation().y < -120f)
                    {
                        mousediff = -(float) (mouseInput.currentPos.x - elementTool.initPos.x);
                        h = false;
                    }

                    Vector3f ppos = mainView.getSelectedPosition();
                    mainView.setSelectedPosition(new Vector3f(lastPos.x + (mousediff * (mainView.camera.pos.distance(new Vector3f(lastPos)))) / (h ? 100f : 400f), ppos.y, ppos.z));
                }
                break;
                case 1:
                    //y pos
                {
                    float mousediff = -(float) (mouseInput.currentPos.y - elementTool.initPos.y);

                    boolean h = false;

                    if (mainView.camera.getWrappedRotation().x > 20f)
                        h = true;

                    if (mainView.camera.getWrappedRotation().x < -20f)
                        h = true;

                    Vector3f ppos = mainView.getSelectedPosition();
                    mainView.setSelectedPosition(new Vector3f(ppos.x, lastPos.y + (mousediff * (mainView.camera.pos.distance(new Vector3f(lastPos)))) / (h ? 100f : 400f), ppos.z));
                }
                break;
                case 2:
                    //z pos
                {
                    float mousediff = (float) (mouseInput.currentPos.x - elementTool.initPos.x);
                    boolean h = false;

                    if (mainView.camera.getWrappedRotation().y > 60f + 90f) {
                        mousediff = -(float) (mouseInput.currentPos.y - elementTool.initPos.y);
                        h = true;
                    }

                    if (mainView.camera.getWrappedRotation().y < -60f + 90f) {
                        mousediff = (float) (mouseInput.currentPos.y - elementTool.initPos.y);
                        h = true;
                    }

                    if (mainView.camera.getWrappedRotation().y > 120f + 90f || mainView.camera.getWrappedRotation().y < -120f + 90f)
                    {
                        mousediff = -(float) (mouseInput.currentPos.x - elementTool.initPos.x);
                        h = false;
                    }

                    Vector3f ppos = mainView.getSelectedPosition();
                    mainView.setSelectedPosition(new Vector3f(ppos.x, ppos.y, lastPos.z + (mousediff * (mainView.camera.pos.distance(new Vector3f(lastPos)))) / (h ? 100f : 400f)));
                }
                break;
                case 3:
                    //x rot
                {
                    double y = mouseInput.currentPos.y - screenPos.y;
                    double x = mouseInput.currentPos.x - screenPos.x;
                    float ang = (float) Math.atan2(y, x);
                    float diff = ang - elementTool.lastAng;
                    if(mainView.camera.getWrappedRotation().y < 0)
                        diff *= -1;
                    elementTool.lastAng = ang;
                    Vector3f centerPoint = new Vector3f(mainView.getSelectedPosition());

                    for (Entity ent : mainView.entities)
                        if (ent.selected)
                        {
                            ent.transformation.rotateAroundLocal(new Quaternionf().rotateLocalX(diff), centerPoint.x, centerPoint.y, centerPoint.z, ent.transformation);
                            if(ent.getType() >= 1)
                            {
                                Vector3f curTrans = ent.transformation.getTranslation(new Vector3f());
                                ent.transformation.setTranslation(new Vector3f()).rotateLocalX(-diff).setTranslation(curTrans);
                            }
                        }
                }
                break;
                case 4:
                    //y rot
                {
                    double y = mouseInput.currentPos.y - screenPos.y;
                    double x = mouseInput.currentPos.x - screenPos.x;
                    float ang = (float) Math.atan2(y, x);
                    float diff = ang - elementTool.lastAng;
                    if(mainView.camera.getWrappedRotation().x > 0)
                        diff *= -1;
                    elementTool.lastAng = ang;
                    Vector3f centerPoint = mainView.getSelectedPosition();

                    for (Entity ent : mainView.entities)
                        if (ent.selected) {
                            ent.transformation.rotateAroundLocal(new Quaternionf().rotateLocalY(diff), centerPoint.x, centerPoint.y, centerPoint.z, ent.transformation);
                            if (ent.getType() >= 1) {
                                Vector3f curTrans = ent.transformation.getTranslation(new Vector3f());
                                ent.transformation.setTranslation(new Vector3f()).rotateLocalY(-diff).setTranslation(curTrans);
                            }
                        }
                }
                break;
                case 5:
                    //z rot
                {
                    double y = mouseInput.currentPos.y - screenPos.y;
                    double x = mouseInput.currentPos.x - screenPos.x;
                    float ang = (float) Math.atan2(y, x);
                    float diff = ang - elementTool.lastAng;
                    if(mainView.camera.getWrappedRotation().y < 90 && mainView.camera.getWrappedRotation().y > -90)
                        diff *= -1;
                    elementTool.lastAng = ang;
                    Vector3f centerPoint = mainView.getSelectedPosition();

                    for (Entity ent : mainView.entities)
                        if (ent.selected)
                        {
                            ent.transformation.rotateAroundLocal(new Quaternionf().rotateLocalZ(diff), centerPoint.x, centerPoint.y, centerPoint.z, ent.transformation);
                            if (ent.getType() >= 2) {
                                Vector3f curTrans = ent.transformation.getTranslation(new Vector3f());
                                ent.transformation.setTranslation(new Vector3f()).rotateLocalZ(-diff).setTranslation(curTrans);
                            }
                        }
                }
                break;
                case 6:
                    //x scale
                {
                    float mousediff = (float) (mouseInput.currentPos.x - elementTool.initPos.x);
                    boolean h = false;

                    if (mainView.camera.getWrappedRotation().y > 60f) {
                        mousediff = -(float) (mouseInput.currentPos.y - elementTool.initPos.y);
                        h = true;
                    }

                    if (mainView.camera.getWrappedRotation().y < -60f) {
                        mousediff = (float) (mouseInput.currentPos.y - elementTool.initPos.y);
                        h = true;
                    }

                    if (mainView.camera.getWrappedRotation().y > 120f || mainView.camera.getWrappedRotation().y < -120f)
                    {
                        mousediff = -(float) (mouseInput.currentPos.x - elementTool.initPos.x);
                        h = false;
                    }

                    elementTool.initPos = new Vector2d(mouseInput.currentPos);
                    Vector3f centerPoint = mainView.getSelectedPosition();

                    for (Entity ent : mainView.entities)
                        if (ent.selected)
                            ent.transformation.scaleAroundLocal(1 + (mousediff * (mainView.camera.pos.distance(centerPoint))) / (h ? 1000000f : 4000000f), 1, 1, centerPoint.x, centerPoint.y, centerPoint.z);
                }
                break;
                case 7:
                    //y scale
                {
                    float mousediff = -(float) (mouseInput.currentPos.y - elementTool.initPos.y);

                    boolean h = false;

                    if (mainView.camera.getWrappedRotation().x > 20f)
                        h = true;

                    if (mainView.camera.getWrappedRotation().x < -20f)
                        h = true;

                    elementTool.initPos = new Vector2d(mouseInput.currentPos);
                    Vector3f centerPoint = mainView.getSelectedPosition();

                    for (Entity ent : mainView.entities)
                        if (ent.selected)
                            ent.transformation.scaleAroundLocal(1, 1 + (mousediff * (mainView.camera.pos.distance(centerPoint))) / (h ? 1000000f : 4000000f), 1, centerPoint.x, centerPoint.y, centerPoint.z);
                }
                break;
                case 8:
                    //z scale
                {
                    float mousediff = (float) (mouseInput.currentPos.x - elementTool.initPos.x);
                    boolean h = false;

                    if (mainView.camera.getWrappedRotation().y > 60f + 90f) {
                        mousediff = -(float) (mouseInput.currentPos.y - elementTool.initPos.y);
                        h = true;
                    }

                    if (mainView.camera.getWrappedRotation().y < -60f + 90f) {
                        mousediff = (float) (mouseInput.currentPos.y - elementTool.initPos.y);
                        h = true;
                    }

                    if (mainView.camera.getWrappedRotation().y > 120f + 90f || mainView.camera.getWrappedRotation().y < -120f + 90f)
                    {
                        mousediff = -(float) (mouseInput.currentPos.x - elementTool.initPos.x);
                        h = false;
                    }

                    elementTool.initPos = new Vector2d(mouseInput.currentPos);
                    Vector3f centerPoint = mainView.getSelectedPosition();

                    for (Entity ent : mainView.entities)
                        if (ent.selected)
                        {
                            ent.transformation.scaleAroundLocal(1, 1, 1 + (mousediff * (mainView.camera.pos.distance(centerPoint))) / (h ? 1000000f : 4000000f),  centerPoint.x, centerPoint.y, centerPoint.z);
                            if(ent.getType() == 1)
                            {
                                Vector3f curTrans = ent.transformation.getTranslation(new Vector3f());
                                ent.transformation.setTranslation(new Vector3f()).scaleLocal(1, 1, 1 / ent.transformation.getScale(new Vector3f()).z).setTranslation(curTrans);
                            }
                        }
                }
                break;
                case 9:
                    //u scale
                {
                    float mousediff = (float) (mouseInput.currentPos.x - elementTool.initPos.x);

                    elementTool.initPos = new Vector2d(mouseInput.currentPos);
                    Vector3f centerPoint = mainView.getSelectedPosition();

                    for (Entity ent : mainView.entities)
                        if (ent.selected)
                        {
                            ent.transformation.scaleAroundLocal(1 + (mousediff * (mainView.camera.pos.distance(centerPoint))) / 1000000f,  centerPoint.x, centerPoint.y, centerPoint.z);
                            if(ent.getType() == 1)
                            {
                                Vector3f curTrans = ent.transformation.getTranslation(new Vector3f());
                                ent.transformation.setTranslation(new Vector3f()).scaleLocal(1, 1, 1 / ent.transformation.getScale(new Vector3f()).z).setTranslation(curTrans);
                            }
                        }
                }
                break;
            }
        }

        elementTool.render(hasSelection,
                move.isClicked,
                rotate.isClicked,
                scale.isClicked,
                mainView.crosshair, screenPos, mainView.window, mainView.loader, mainView.renderer, mouseInput);

        drawRect(mainView.window.width - 305, 0, 305, mainView.window.height, Config.PRIMARY_COLOR);
        drawLine(new Vector2i(mainView.window.width - 304, 0), new Vector2i(mainView.window.width - 304, mainView.window.height), Config.SECONDARY_COLOR, false);

        super.draw(mouseInput);
    }

    public Vector3f lastPos = new Vector3f();

    @Override
    public boolean onClick(Vector2d pos, int button, int action, int mods) {
        boolean onclick = super.onClick(pos, button, action, mods);

        for (int i = 0; i < mainView.entities.size(); i++)
            if(mainView.entities.get(i).getType() == 0 || mainView.entities.get(i).getType() == 1)
                mainView.entities.get(i).testForMouse = true;

        boolean overElement = false;
        boolean hasSelection = false;
        int selectedAmount = 0;
        int entityHit = -1;

        for(Element element : guiElements)
            if (element.isMouseOverElement(pos))
                overElement = true;

        for(int i = 0; i < mainView.entities.size(); i++)
        {
            Entity entity = mainView.entities.get(i);
            if(entity.selected)
            {
                hasSelection = true;
                selectedAmount++;
            }
            if(entity.highlighted)
                entityHit = i;
        }

        if(elementTool.onClick(pos, button, action, mods, mainView.window, mainView.camera))
            lastPos = mainView.getSelectedPosition();

        if (button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && !overElement && !(elementTool.hit != -1 || elementTool.selected != -1)) {
            if (entityHit == -1) {
                if (mods != GLFW.GLFW_MOD_CONTROL)
                    for(Entity entity : mainView.entities)
                        entity.selected = false;
            } else {
                Entity entity = mainView.entities.get(entityHit);

                if (mods != GLFW.GLFW_MOD_CONTROL)
                    if(!(selectedAmount == 1 && entity.selected))
                        for(Entity e1 : mainView.entities)
                            e1.selected = false;

                entity.selected = !entity.selected;
            }
        }

        return onclick;
    }

}
