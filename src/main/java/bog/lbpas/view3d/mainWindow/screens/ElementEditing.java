package bog.lbpas.view3d.mainWindow.screens;

import bog.lbpas.view3d.core.Transformation3D;
import bog.lbpas.view3d.core.types.Entity;
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
import bog.lbpas.view3d.renderer.gui.ingredients.Line;
import bog.lbpas.view3d.renderer.gui.ingredients.LineStrip;
import bog.lbpas.view3d.utils.CWLibUtils.SkeletonUtils;
import bog.lbpas.view3d.utils.Config;
import bog.lbpas.view3d.utils.Consts;
import bog.lbpas.view3d.utils.print;
import common.FileChooser;
import cwlib.enums.Part;
import cwlib.enums.ResourceType;
import cwlib.resources.*;
import cwlib.structs.slot.Pack;
import cwlib.structs.slot.Slot;
import cwlib.structs.things.Thing;
import cwlib.structs.things.parts.*;
import cwlib.types.Resource;
import cwlib.types.data.ResourceDescriptor;
import cwlib.types.databases.FileDBRow;
import cwlib.types.databases.FileEntry;
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
    public Textbox positionX;
    public Textbox positionY;
    public Textbox positionZ;


    public DropDownTab helpers;
    public Checkbox levelBorders;
    public Checkbox podHelper;

    public DropDownTab availableAssets;
    public ButtonList assetList;
    public Textbox availableAssetsSearch;
    public Checkbox filterMeshes;
    public Checkbox filterMaterials;
    public Checkbox filterPlans;
    public Checkbox filterLevels;

    public Element loadedEntitiesHitbox;
    public ButtonList loadedEntities;
    public Button loadPlanElements;
    public Textbox loadedEntitiesSearch;
    public Button clearAllEntites;
    public Button sortEntityList;
    public Button newThing;

    public ButtonImage move;
    public ButtonImage rotate;
    public ButtonImage scale;

    public ButtonImage preview;

    public DropDownTab currentSelection;
    public Textbox selectionName;
    public ComboBox addParentCombo;
    public ComboBox addGroupCombo;
    public ElementList partsList;

    public ThingPart currentSelectionParts;

    public void init() {
        elementTool = new Transformation3D.Tool(loader);

        camPos = new DropDownTab("camPosition", "Camera position", new Vector2f(10, 21 + 10), new Vector2f(200, getFontHeight(10) + 4), 10, renderer, loader, window) {
            @Override
            public void secondThread() {
                super.secondThread();

                Vector2f camX = positionX.setTextboxValueFloat(mainView.camera.pos.x);
                if (camX.y == 1)
                    mainView.camera.pos.x = camX.x;
                Vector2f camY = positionY.setTextboxValueFloat(mainView.camera.pos.y);
                if (camY.y == 1)
                    mainView.camera.pos.y = camY.x;
                Vector2f camZ = positionZ.setTextboxValueFloat(mainView.camera.pos.z);
                if (camZ.y == 1)
                    mainView.camera.pos.z = camZ.x;
            }
        };
        Panel xPos = camPos.addPanel("x");
        xPos.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("xstr", "X:", 10, renderer), 0.1f));
        positionX = new Textbox("xtex", new Vector2f(), new Vector2f(), 10, renderer, loader, window);
        positionX.noLetters().noOthers();
        xPos.elements.add(new Panel.PanelElement(positionX, 0.9f));
        Panel yPos = camPos.addPanel("y");
        yPos.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("ystr", "Y:", 10, renderer), 0.1f));
        positionY = new Textbox("ytex", new Vector2f(), new Vector2f(), 10, renderer, loader, window);
        positionY.noLetters().noOthers();
        yPos.elements.add(new Panel.PanelElement(positionY, 0.9f));
        Panel zPos = camPos.addPanel("z");
        zPos.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("zstr", "Z:", 10, renderer), 0.1f));
        positionZ = new Textbox("ztex", new Vector2f(), new Vector2f(), 10, renderer, loader, window);
        positionZ.noLetters().noOthers();
        zPos.elements.add(new Panel.PanelElement(positionZ, 0.9f));
        currentSelection = new DropDownTab("currentSelection", "Current Selection", new Vector2f(524, 21 + 10), new Vector2f(200, getFontHeight(10) + 4), 10, renderer, loader, window) {

            @Override
            public void draw(MouseInput mouseInput, boolean overOther) {
                super.draw(mouseInput, overOther);
                if(shouldResize)
                {
                    resize();
                    shouldResize = false;
                }
            }

            @Override
            public void secondThread() {
                super.secondThread();

                boolean hasSelection = false;
                ArrayList<Integer> selected = new ArrayList<>();
                boolean hasMesh = false;
                boolean hasMaterial = false;
                boolean hasAudio = false;

                for (int i = 0; i < mainView.things.size(); i++)
                    if(mainView.things.get(i) != null && mainView.things.get(i).selected) {
                        hasSelection = true;
                        selected.add(i);
                    }

                try {
                    String name = selectionName.setTextboxValueString(mainView.getSelectedName());
                    if (name != null && selected.size() >= 1)
                        for (int i : selected)
                            mainView.things.get(i).thing.name = name;
                } catch (Exception e) {e.printStackTrace();}

                Thing parent = mainView.getSelectedParent();
                String parentName = parent == null ? "None" : parent.name;
                addParentCombo.tabTitle = parentName == null ? "null" : parentName;
                Thing group = mainView.getSelectedGroup();
                String groupName = group == null ? "None" : group.name;
                addGroupCombo.tabTitle = groupName == null ? "null" : groupName;

                shouldResize = true;
            }

            boolean shouldResize = false;
        };
        currentSelection.addString("namestr", "Name:");
        selectionName = currentSelection.addTextbox("name");

        Panel parentPanel = currentSelection.addPanel("parentPanel");
        parentPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("parentstr", "Parent:", 10, mainView.renderer), 0.35f));
        addParentCombo = new ComboBox("addParentCombo", "None", new Vector2f(), new Vector2f(), 10, 215, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public int[] getParentTransform() {
                return new int[]{(int) java.lang.Math.round(currentSelection.pos.x), (int) java.lang.Math.round(currentSelection.pos.y), (int) java.lang.Math.round(currentSelection.size.x)};
            }
        };
        Button clearParent = addParentCombo.addButton("clearParent", "None", new Button() {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)
                {
                    mainView.setSelectedParent(null);
                    addParentCombo.tabTitle = "None";
                }
            }
        });

        Textbox searchParents = new Textbox("searchParents", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
        Panel searchParentsPanel = addParentCombo.addPanel("searchParentsPanel");
        searchParentsPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("srchPrnt", "Search:", 10, mainView.renderer), 0.4f));
        searchParentsPanel.elements.add(new Panel.PanelElement(searchParents, 0.6f));

        addParentCombo.addList("parentList", new ButtonList(mainView.things, 10, mainView.renderer, mainView.loader, mainView.window) {

            int hovering = -1;

            @Override
            public void draw(MouseInput mouseInput, boolean overElement) {
                hovering = -1;
                super.draw(mouseInput, overElement);
            }

            @Override
            public void clickedButton(Object object, int index, int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1)
                {
                    if(action == GLFW.GLFW_PRESS)
                    {
                        mainView.setSelectedParent(((bog.lbpas.view3d.core.types.Thing)object).thing);
                        addParentCombo.tabTitle = ((bog.lbpas.view3d.core.types.Thing)object).thing.name;
                    }
                }
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
                return ((bog.lbpas.view3d.core.types.Thing)object).thing == mainView.getSelectedParent();
            }

            @Override
            public String buttonText(Object object, int index) {
                String name = ((bog.lbpas.view3d.core.types.Thing)object).thing.name;
                return name == null ? "null" : name;
            }

            @Override
            public boolean searchFilter(Object object, int index) {
                return buttonText(object, index).toLowerCase().contains(searchParents.getText().toLowerCase());
            }

            @Override
            public int buttonHeight() {
                return super.buttonHeight() + 4;
            }
        }, 295);
        parentPanel.elements.add(new Panel.PanelElement(addParentCombo, 0.65f));

        Panel groupPanel = currentSelection.addPanel("groupPanel");
        groupPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("groupstr", "Group:", 10, mainView.renderer), 0.35f));
        addGroupCombo = new ComboBox("addGroupCombo", "None", new Vector2f(), new Vector2f(), 10, 215, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public int[] getParentTransform() {
                return new int[]{(int) java.lang.Math.round(currentSelection.pos.x), (int) java.lang.Math.round(currentSelection.pos.y), (int) java.lang.Math.round(currentSelection.size.x)};
            }
        };
        Button clearGroup = addGroupCombo.addButton("clearGroup", "None", new Button() {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)
                {
                    mainView.setSelectedGroup(null);
                    addGroupCombo.tabTitle = "None";
                }
            }
        });

        Textbox searchGroup = new Textbox("searchGroup", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
        Panel searchGroupPanel = addGroupCombo.addPanel("searchGroupPanel");
        searchGroupPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("srchGrp", "Search:", 10, mainView.renderer), 0.4f));
        searchGroupPanel.elements.add(new Panel.PanelElement(searchGroup, 0.6f));

        addGroupCombo.addList("addGroupCombo", new ButtonList(mainView.things, 10, mainView.renderer, mainView.loader, mainView.window) {

            int hovering = -1;

            @Override
            public void draw(MouseInput mouseInput, boolean overElement) {
                hovering = -1;
                super.draw(mouseInput, overElement);

            }

            @Override
            public void clickedButton(Object object, int index, int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1)
                {
                    if(action == GLFW.GLFW_PRESS)
                    {
                        mainView.setSelectedGroup(((bog.lbpas.view3d.core.types.Thing)object).thing);
                        addGroupCombo.tabTitle = ((bog.lbpas.view3d.core.types.Thing)object).thing.name;
                    }
                }
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
                return ((bog.lbpas.view3d.core.types.Thing)object).thing == mainView.getSelectedGroup();
            }

            @Override
            public String buttonText(Object object, int index) {
                String name = ((bog.lbpas.view3d.core.types.Thing)object).thing.name;
                return name == null ? "null" : name;
            }

            @Override
            public boolean searchFilter(Object object, int index) {
                return buttonText(object, index).toLowerCase().contains(searchGroup.getText().toLowerCase());
            }

            @Override
            public int buttonHeight() {
                return super.buttonHeight() + 4;
            }
        }, 295);
        groupPanel.elements.add(new Panel.PanelElement(addGroupCombo, 0.65f));

        Panel partsLabelAdd = currentSelection.addPanel("partsLabelAdd");
        partsLabelAdd.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("partsstr", "Parts:", 10, mainView.renderer), 0.6f));
        ComboBox addPartCombo = new ComboBox("addPartCombo", "Add", new Vector2f(), new Vector2f(), 10, 215, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public int[] getParentTransform() {
                return new int[]{(int) java.lang.Math.round(currentSelection.pos.x), (int) java.lang.Math.round(currentSelection.pos.y), (int) java.lang.Math.round(currentSelection.size.x)};
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
                            currentSelectionParts.addPart((Part) object, mainView.things);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_RELEASE)
                    clicked = -1;
                super.onClick(mouseInput, pos, button, action, mods, overElement);
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
        partsLabelAdd.elements.add(new Panel.PanelElement(addPartCombo, 0.4f));

        partsList = new ElementList("partsList", new Vector2f(), new Vector2f(150), 10, renderer, loader, window)
        {
            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overOther) {

                if(isMouseOverElement(pos) && (button == GLFW.GLFW_MOUSE_BUTTON_2 || button == GLFW.GLFW_MOUSE_BUTTON_1) && action == GLFW.GLFW_PRESS)
                    for(Element element : this.elements)
                    {
                        Panel panel = (Panel) element;
                        Element e = panel.elements.get(0).element;
                        if(e instanceof ComboBox && !((ComboBox)e).isMouseOverTab(pos) && !((ComboBox)e).isMouseOverElement(pos))
                            ((ComboBox)e).collapsed(true);
                    }

                super.onClick(mouseInput, pos, button, action, mods, overOther);
            }

            @Override
            public int[] getParentTransform() {
                return new int[]{(int) java.lang.Math.round(this.pos.x), (int) java.lang.Math.round(this.pos.y), (int) java.lang.Math.round(this.size.x)};
            }
        };
        currentSelection.addElementList(partsList);

        currentSelectionParts = new ThingPart(mainView, partsList, currentSelection, mainView.things);

        helpers = new DropDownTab("helpers", "Helpers", new Vector2f(10, 21 + 10 + camPos.getFullHeight() + 3), new Vector2f(200, getFontHeight(10) + 4), 10, renderer, loader, window).closed();
        levelBorders = helpers.addCheckbox("levelBorders", "Level borders", Config.LEVEL_BORDERS);
        podHelper = helpers.addCheckbox("podHelper", "Pod helper", Config.POD_HELPER);
        helpers.addButton("Pod cam", new bog.lbpas.view3d.renderer.gui.elements.Button("podCam") {
            @Override
            public void clickedButton(int button, int action, int mods) {
                mainView.camera.pos = new Vector3f(4.673651f, 1565.6465f, 13882.88f);
                mainView.camera.rotation = new Vector3f(5.800001f, 0.20008372f, 0.0f);
            }
        });

        availableAssets = new DropDownTab("availableModels", "Available assets", new Vector2f(217, 21 + 10), new Vector2f(300, getFontHeight(10) + 4), 10, renderer, loader, window);
        Panel assetsPanel = availableAssets.addPanel("assetsPanel");
        assetsPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("", "Search:", 10, renderer), 0.25f));
        availableAssetsSearch = new Textbox("availableAssetsSearch", new Vector2f(), new Vector2f(), 10, renderer, loader, window);
        assetsPanel.elements.add(new Panel.PanelElement(availableAssetsSearch, 0.4425f));
        assetsPanel.elements.add(new Panel.PanelElement(null, 0.0075f));
        ComboBox filters = new ComboBox("filters", "Filters", new Vector2f(), new Vector2f(), 10, 100, renderer, loader, window)
        {
            @Override
            public int[] getParentTransform() {
                return new int[]{(int) java.lang.Math.round(availableAssets.pos.x), (int) java.lang.Math.round(availableAssets.pos.y), (int) java.lang.Math.round(availableAssets.size.x)};
            }
        };
        filterPlans = filters.addCheckbox("filterPlans", "Plans", true);
        filterLevels = filters.addCheckbox("filterLevels", "Levels", true);
        filterMeshes = filters.addCheckbox("filterMeshes", "Meshes", true);
        filterMaterials = filters.addCheckbox("filterMaterials", "Materials", true);
        assetsPanel.elements.add(new Panel.PanelElement(filters, 0.3f));

        assetList = new ButtonList("availableModelsList", LoadedData.digestedEntries, new Vector2f(), new Vector2f(), 10, renderer, loader, window) {

            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement) {
                super.onClick(mouseInput, pos, button, action, mods, overElement);

                if (button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_RELEASE)
                    clicked = -1;
            }

            @Override
            public void clickedButton(Object object, int index, int button, int action, int mods) {
                FileEntry entry = (FileEntry) object;

                if (button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS) {
                    clicked = index;

                    ResourceType type = entry.getInfo().getType();

                    switch(type)
                    {
                        case MESH:
                        {
                            ResourceDescriptor descriptor = new ResourceDescriptor(entry.getSHA1(), ResourceType.MESH);

                            if (entry instanceof FileDBRow && ((FileDBRow) entry).getGUID() != null)
                                descriptor = new ResourceDescriptor(((FileDBRow) entry).getGUID(), ResourceType.MESH);

                            Vector3f pos = mainView.centerPicker.getPointOnRay(mainView.centerPicker.currentRay, 1000);
                            String name = entry.getPath().substring(entry.getPath().lastIndexOf("/") + 1);

                            int extInd = name.lastIndexOf(".");
                            boolean nameIsHash = name.substring(0, extInd != -1 ? extInd : name.length()).equalsIgnoreCase(entry.getSHA1().toString());

                            if(!(entry instanceof FileDBRow) && nameIsHash)
                                name = LoadedData.digestedEntriesDescriptors.get(index).getType().getHeader().toLowerCase() + "_" + name.substring(name.length() - 12);

                            for (Entity entity : mainView.things)
                            {
                                entity.selected = false;
                                currentSelectionParts.selectionChange();
                            }

                            Thing mesh = new Thing();
                            mesh.name = name.substring(0, name.lastIndexOf("."));
                            Matrix4f trans = new Matrix4f().identity().translate(pos).rotate(Math.toRadians(-90), 1, 0 , 0);
                            mesh.setPart(Part.POS, new PPos(trans));
                            PRenderMesh rmesh = new PRenderMesh();
                            rmesh.mesh = descriptor;

                            RMesh msh = LoadedData.loadMesh(descriptor);

                            Thing[] boneThings = new Thing[msh.getBones().length];
                            for(int i = 0; i < boneThings.length; i++)
                                boneThings[i] = new Thing();
                            boneThings = SkeletonUtils.computeBoneThings(boneThings, mesh, trans, msh.getBones());

                            for(Thing boneThing : boneThings)
                                if(!boneThing.equals(mesh))
                                    mainView.things.add(new bog.lbpas.view3d.core.types.Thing(boneThing, loader));

                            rmesh.boneThings = boneThings;

                            mesh.setPart(Part.RENDER_MESH, rmesh);
                            mainView.things.add(new bog.lbpas.view3d.core.types.Thing(mesh, loader));
                        }
                        break;
                        case GFX_MATERIAL:
                        {
                            ResourceDescriptor descriptor = new ResourceDescriptor(entry.getSHA1(), ResourceType.MESH);

                            if(entry instanceof FileDBRow && ((FileDBRow)entry).getGUID() != null)
                                descriptor = new ResourceDescriptor(((FileDBRow)entry).getGUID(), ResourceType.MESH);

                            Vector3f pos = mainView.centerPicker.getPointOnRay(mainView.centerPicker.currentRay, 1000);
                            String name = entry.getPath().substring(entry.getPath().lastIndexOf("/") + 1);

                            int extInd = name.lastIndexOf(".");
                            boolean nameIsHash = name.substring(0, extInd != -1 ? extInd : name.length()).equalsIgnoreCase(entry.getSHA1().toString());

                            if(!(entry instanceof FileDBRow) && nameIsHash)
                                name = LoadedData.digestedEntriesDescriptors.get(index).getType().getHeader().toLowerCase() + "_" + name.substring(name.length() - 12);

                            for (Entity entity : mainView.things)
                            {
                                entity.selected = false;
                                currentSelectionParts.selectionChange();
                            }

                            Thing material = new Thing();
                            material.name = name.substring(0, name.lastIndexOf("."));
                            material.setPart(Part.SHAPE, new PShape(new Vector3f[]{
                                    new Vector3f(-100f, -100f, 0f),
                                    new Vector3f(-100f, 100f, 0f),
                                    new Vector3f(100f, 100f, 0f),
                                    new Vector3f(100f, -100f, 0f)
                            }));
                            PGeneratedMesh gmesh = new PGeneratedMesh();
                            gmesh.gfxMaterial = descriptor;
                            material.setPart(Part.GENERATED_MESH, gmesh);
                            material.setPart(Part.POS, new PPos(new Matrix4f().identity().translate(pos)));
                            mainView.things.add(new bog.lbpas.view3d.core.types.Thing(material, loader));
                        }
                        break;
                        case PLAN:
                        {
                            ResourceDescriptor descriptor = new ResourceDescriptor(entry.getSHA1(), ResourceType.MESH);

                            RPlan plan = LoadedData.loadPlan(descriptor);
                            if (plan == null) return;
                            Thing[] things = plan.getThings();

                            mainView.addThings(things);
                        }
                        break;
                        case LEVEL:
                        {
                            ResourceDescriptor descriptor = new ResourceDescriptor(entry.getSHA1(), ResourceType.MESH);
                            RLevel level = LoadedData.loadLevel(descriptor);
                            if (level == null)
                                return;
                            ArrayList<Thing> things = ((PWorld)level.world.getPart(Part.WORLD)).things;

                            mainView.addThings(things);
                        }
                        break;
                        default:
                            mainView.pushError("Failed loading asset.", "Resource type " + type.name() + " cannot be loaded.");
                            print.error("Resource type " + type.name() + " cannot be loaded.");
                            break;
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
                return clicked == index;
            }

            @Override
            public String buttonText(Object object, int index) {
                FileEntry entry = (FileEntry) object;

                if(LoadedData.loadedTranslation != -1)
                {
                    ResourceDescriptor descriptor = LoadedData.digestedEntriesDescriptors.get(index);

                    if (descriptor.getType() == ResourceType.PLAN) {
                        if(entry.translatedFor != LoadedData.loadedTranslation)
                        {
                            long titleKey = LoadedData.loadPlan(descriptor).inventoryData.titleKey;
                            String translatedTitle = LoadedData.loadedTranslationTable.translate(titleKey);
                            if((translatedTitle == null || translatedTitle.isEmpty() || translatedTitle.isBlank()) && LoadedData.loadedPatchTranslationTable != null)
                                translatedTitle = LoadedData.loadedPatchTranslationTable.translate(titleKey);

                            if (translatedTitle != null && !translatedTitle.isEmpty() && !translatedTitle.isBlank())
                                entry.translation = translatedTitle + ".plan";
                            else
                                entry.translation = entry.getPath().substring(entry.getPath().lastIndexOf("/") + 1);
                            entry.translatedFor = LoadedData.loadedTranslation;
                        }
                        return entry.translation;
                    }
                    else if(descriptor.getType() == ResourceType.LEVEL)
                    {
                        if(entry.translatedFor != LoadedData.loadedTranslation)
                        {
                            String translation = null;
                            l : for(RSlotList slt : LoadedData.slotLists)
                                    for(Slot s : slt.getSlots())
                                        if(s.root != null && ((s.root.isGUID() && descriptor.isGUID() && s.root.getGUID().getValue() == descriptor.getGUID().getValue()) || (s.root.isHash() && descriptor.isHash() && s.root.getSHA1().toString().equalsIgnoreCase(descriptor.getSHA1().toString()))))
                                        {
                                            translation = s.translationTag + "_NAME";
                                            break l;
                                        }
                            if(translation == null)
                                l : for(RPacks pck : LoadedData.packs)
                                        for(Pack p : pck.getPacks())
                                            if(p.slot.root != null && ((p.slot.root.isGUID() && descriptor.isGUID() && p.slot.root.getGUID().getValue() == descriptor.getGUID().getValue()) || (p.slot.root.isHash() && descriptor.isHash() && p.slot.root.getSHA1().toString().equalsIgnoreCase(descriptor.getSHA1().toString()))))
                                            {
                                                translation = p.slot.translationTag + "_NAME";
                                                break l;
                                            }

                            String translatedTitle = translation == null ? null : LoadedData.loadedTranslationTable.translate(translation);
                            if((translatedTitle == null || translatedTitle.isEmpty() || translatedTitle.isBlank()) && LoadedData.loadedPatchTranslationTable != null)
                                translatedTitle = translation == null ? null : LoadedData.loadedPatchTranslationTable.translate(translation);

                            if (translatedTitle != null && !translatedTitle.isEmpty() && !translatedTitle.isBlank())
                                entry.translation = translatedTitle + ".bin";
                            else
                                entry.translation = entry.getPath().substring(entry.getPath().lastIndexOf("/") + 1);
                            entry.translatedFor = LoadedData.loadedTranslation;
                        }
                        return entry.translation;
                    }
                }

                String name = entry.getPath().substring(entry.getPath().lastIndexOf("/") + 1);

                int extInd = name.lastIndexOf(".");
                boolean nameIsHash = name.substring(0, extInd != -1 ? extInd : name.length()).equalsIgnoreCase(entry.getSHA1().toString());

                if(!(entry instanceof FileDBRow) && nameIsHash)
                    return LoadedData.digestedEntriesDescriptors.get(index).getType().getHeader().toLowerCase() + "_" + name.substring(name.length() - 12);

                return entry.getName();
            }

            @Override
            public boolean searchFilter(Object object, int index) {
                if(index > LoadedData.digestedEntriesDescriptors.size() - 1)
                    return false;

                FileEntry entry = (FileEntry) object;
                ResourceDescriptor descriptor = LoadedData.digestedEntriesDescriptors.get(index);
                try {

                    if ((descriptor.getType().getValue() == ResourceType.MESH.getValue() && filterMeshes.isChecked) ||
                            (descriptor.getType().getValue() == ResourceType.GFX_MATERIAL.getValue() && filterMaterials.isChecked) ||
                            (descriptor.getType().getValue() == ResourceType.PLAN.getValue() && filterPlans.isChecked) ||
                            (descriptor.getType().getValue() == ResourceType.LEVEL.getValue() && filterLevels.isChecked)) {
                        String text = availableAssetsSearch.getText();

                        if (text.isBlank() || text.isEmpty() || text.equalsIgnoreCase("") || text.equalsIgnoreCase(" "))
                            return true;

                        String sha1 = "h" + descriptor.getSHA1().toString().toLowerCase();

                        String name = buttonText(object, index);

                        int extInd = name.lastIndexOf(".");
                        boolean nameIsHash = name.substring(0, extInd != -1 ? extInd : name.length()).equalsIgnoreCase(entry.getSHA1().toString());

                        if(!(entry instanceof FileDBRow) && nameIsHash)
                            name = LoadedData.digestedEntriesDescriptors.get(index).getType().getHeader().toLowerCase() + "_" + name.substring(name.length() - 12);

                        name = name.toLowerCase();

                        String guid = "";
                        if(descriptor.isGUID())
                            guid = descriptor.getGUID().toString();

                        for (String search : text.split(" "))
                        {
                            if (!(name.contains(search.toLowerCase()) || sha1.contains(search.toLowerCase()) || guid.contains(search.toLowerCase())))
                                return false;
                        }

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

                this.pos.x = window.width - 308;
                this.size.y = window.height - 27;
            }

            @Override
            public void resize() {
                super.resize();

                this.pos.x = window.width - 308;
                this.size.y = window.height - 27;
            }
        };
        loadedEntitiesHitbox.pos = new Vector2f(window.width - 308, 21);
        loadedEntitiesHitbox.size = new Vector2f(305, window.height - 27);
        loadedEntitiesHitbox.id = "loadedEntitiesHitbox";
        loadedEntitiesHitbox.window = window;
        loadedEntities = new ButtonList("loadedEntities", mainView.things, new Vector2f(0, 5 + Math.floor(getFontHeight(10) * 1.25f + 2) * 2 + 22), new Vector2f(302 - getFontHeight(10) - 8, 0), 10, renderer, loader, window) {
            int lastSelected = 0;
            @Override
            public void clickedButton(Object object, int index, int button, int action, int mods) {
                Entity entity = ((Entity) object);

                boolean ctrl = mods == GLFW.GLFW_MOD_CONTROL;
                boolean shift = mods == GLFW.GLFW_MOD_SHIFT;
                boolean ctrlshift = mods == GLFW.GLFW_MOD_SHIFT + GLFW.GLFW_MOD_CONTROL;
                boolean hasSelection = false;
                ArrayList<Integer> selected = new ArrayList<>();

                for (int i = 0; i < mainView.things.size(); i++)
                    if (mainView.things.get(i).selected) {
                        hasSelection = true;
                        selected.add(i);
                    }

                if (button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS) {
                    if (!hasSelection || ctrl)
                    {
                        entity.selected = !entity.selected;
                        currentSelectionParts.selectionChange();
                    }
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
                            if(searchFilter(mainView.things.get(i), i))
                            {
                                mainView.things.get(i).selected = true;
                                currentSelectionParts.selectionChange();
                            }
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
                        {
                            mainView.things.get(i).selected = false;
                            currentSelectionParts.selectionChange();
                        }

                        for(int i = min; i <= max; i++)
                            if(searchFilter(mainView.things.get(i), i))
                            {
                                mainView.things.get(i).selected = true;
                                currentSelectionParts.selectionChange();
                            }
                    }
                    else {
                        for (Entity ent : mainView.things)
                            if (ent != entity)
                            {
                                ent.selected = false;
                            }

                        entity.selected = !entity.selected;
                        currentSelectionParts.selectionChange();
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
                return ((bog.lbpas.view3d.core.types.Thing) object).thing == null ? "null" : ((bog.lbpas.view3d.core.types.Thing) object).thing.name == null ? "null" : ((bog.lbpas.view3d.core.types.Thing) object).thing.name;
            }

            public void refreshOutline(int height)
            {
                if(outlineScrollbar != null)
                    this.outlineScrollbar.cleanup(loader);
                if(outlineButton != null)
                    this.outlineButton.cleanup(loader);
                if(outlineButtonExtra != null)
                    this.outlineButtonExtra.cleanup(loader);
                if(listLine != null)
                    this.listLine.cleanup(loader);
                this.outlineScrollbar = LineStrip.processVerts(LineStrip.getRectangle(new Vector2f(3, size.y - 6)), loader, window);
                this.listLine = Line.getLine(window, loader, new Vector2i(-(height + 2), 0), new Vector2i((int) (size.x - 1.0f - size.x * 0.05f), 0));
                this.outlineButton = getOutlineButton(height);

                if(deletable || draggable)
                    this.outlineButtonExtra = LineStrip.processVerts(LineStrip.getRectangle(new Vector2f(height)), loader, window);
            }

            @Override
            public boolean searchFilter(Object object, int index) {

                try {
                    String text = loadedEntitiesSearch.getText();

                    if (text.isBlank() || text.isEmpty() || text.equalsIgnoreCase("") || text.equalsIgnoreCase(" "))
                        return true;

                    for (String search : text.split(" "))
                        if (!((bog.lbpas.view3d.core.types.Thing) object).thing.name.toLowerCase().contains(search.toLowerCase()))
                            return false;

                    return true;
                } catch (Exception e) {}

                return false;
            }

            @Override
            public void secondThread() {
                super.secondThread();

                this.pos.x = window.width - (305 - getFontHeight(10) - 8);
                this.size.y = window.height - (2 + Math.floor(getFontHeight(10) * 1.25f + 2) * 2 + (Math.floor(getFontHeight(10) * 1.25f) + 4)) - 28;
            }

            @Override
            public void resize() {
                this.pos.x = window.width - (305 - getFontHeight(10) - 8);
                this.size.y = window.height - (2 + Math.floor(getFontHeight(10) * 1.25f + 2) * 2 + (Math.floor(getFontHeight(10) * 1.25f) + 4)) - 28;
                super.resize();
            }

            @Override
            public void drawButton(int posY, float scrollY, float scrollHeight, int height, Object object, int i, MouseInput mouseInput, boolean overElement) {
                super.drawButton(posY, scrollY, scrollHeight, height, object, i, mouseInput, overElement);

                renderer.endScissor();
                renderer.startScissor((int) pos.x - height, (int) scrollY, (int) size.x + 2 + height, (int) java.lang.Math.ceil(scrollHeight));
                bog.lbpas.view3d.core.types.Thing entity = (bog.lbpas.view3d.core.types.Thing) object;
                renderer.drawRect((int)pos.x - height + 2, posY, height, height, buttonColor(object, i));
                renderer.drawImageStatic(
                        entity.thing.hasPart(Part.SPRITE_LIGHT) ? ConstantTextures.getTexture(ConstantTextures.ICON_LIGHT, 30, 30, loader) :
                        entity.thing.hasPart(Part.LEVEL_SETTINGS) ? ConstantTextures.getTexture(ConstantTextures.ICON_LEVEL_SETTINGS, 30, 30, loader) :
                        entity.thing.hasPart(Part.JOINT) ? ConstantTextures.getTexture(ConstantTextures.ICON_JOINT, 30, 30, loader) :
                        entity.thing.hasPart(Part.AUDIO_WORLD) ? ConstantTextures.getTexture(ConstantTextures.ICON_AUDIO, 30, 30, loader) :
                        entity.thing.hasPart(Part.CHECKPOINT) ? ConstantTextures.getTexture(ConstantTextures.ICON_CHECKPOINT, 30, 30, loader) :
                        (entity.thing.hasPart(Part.TRIGGER) || entity.thing.hasPart(Part.SWITCH_INPUT) || entity.thing.hasPart(Part.SWITCH) || entity.thing.hasPart(Part.SWITCH_KEY)) ? ConstantTextures.getTexture(ConstantTextures.ICON_TRIGGER, 30, 30, loader) :
                        entity.thing.hasPart(Part.SCRIPT_NAME) || entity.thing.hasPart(Part.SCRIPT) ? ConstantTextures.getTexture(ConstantTextures.ICON_SCRIPT, 30, 30, loader) :
                        entity.thing.hasPart(Part.NPC) ? ConstantTextures.getTexture(ConstantTextures.ICON_NPC, 30, 30, loader) :
                        entity.thing.hasPart(Part.COSTUME) ? ConstantTextures.getTexture(ConstantTextures.ICON_COSTUME, 30, 30, loader) :
                        entity.thing.hasPart(Part.EMITTER) ? ConstantTextures.getTexture(ConstantTextures.ICON_EMITTER, 30, 30, loader) :
                        entity.thing.hasPart(Part.RENDER_MESH) ? ConstantTextures.getTexture(ConstantTextures.ICON_MESH, 30, 30, loader) :
                        (entity.thing.hasPart(Part.POS) && ((PPos)entity.thing.getPart(Part.POS)).thingOfWhichIAmABone != null) ? ConstantTextures.getTexture(ConstantTextures.ICON_BONE, 30, 30, loader) :
                        entity.thing.hasPart(Part.EFFECTOR) ? ConstantTextures.getTexture(ConstantTextures.ICON_EFFECTOR, 30, 30, loader) :
                        entity.thing.hasPart(Part.SHAPE) ? ConstantTextures.getTexture(ConstantTextures.ICON_SHAPE, 30, 30, loader) :
                        entity.thing.hasPart(Part.GROUP) ? ConstantTextures.getTexture(ConstantTextures.ICON_GROUP, 30, 30, loader) :
                        ConstantTextures.getTexture(ConstantTextures.ICON_UNKNOWN, 30, 30, loader)
                        , (int)pos.x - height + 2, posY, height, height);
                renderer.drawRectOutline(new Vector2f(pos.x - height + 2, posY), this.outlineButtonExtra, buttonColor2(object, i), false);
            }

            @Override
            public int buttonHeight() {
                return getFontHeight(fontSize) + 8;
            }

            @Override
            public void delete(int index) {
                mainView.deleteEntity(index);
            }
        }.deletable().draggable();
        loadPlanElements = new Button("loadPlanElements", "Load elements from PLAN/LEVEL", new Vector2f(window.width - 306, 26), new Vector2f(301, getFontHeight(10) * 1.25f), 10, renderer, loader, window) {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if (button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS) {
                    File file = null;
                    try {
                        file = FileChooser.openFile(null, "plan,pln,bin,lvl", false, false)[0];
                    } catch (Exception e) {}

                    if (file == null || !file.exists()) return;

                    for (Entity e : mainView.things)
                    {
                        e.selected = false;
                        currentSelectionParts.selectionChange();
                    }

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
                                things.remove(level.world);

                                mainView.addThings(things);
                            }catch (Exception ex) {ex.printStackTrace();}
                            break;
                        default:
                            System.err.println("Unknown file type.");
                            mainView.pushError("File Error", "Unknown file type.");
                            break;
                    }
                }
            }

            @Override
            public void secondThread() {
                super.secondThread();

                this.pos.x = window.width - 306;
            }

            @Override
            public void resize() {
                super.resize();

                this.pos.x = window.width - 306;
            }
        };
        Panel loadedSearchPanel = new Panel(
                new Vector2f(window.width - 306, (6 + getFontHeight(10) * 1.25f * 2) + 3),
                new Vector2f(301, getFontHeight(10) * 1.25f),
                renderer)
        {
            @Override
            public void secondThread() {
                super.secondThread();

                this.pos.x = window.width - 306;
            }

            @Override
            public void resize() {
                super.resize();

                this.pos.x = window.width - 306;
            }
        };
        loadedSearchPanel.window = window;
        loadedSearchPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("", "Search:", 10, renderer), 0.25f));
        loadedEntitiesSearch = new Textbox("loadedEntitiesSearch", new Vector2f(), new Vector2f(), 10, renderer, loader, window);
        loadedSearchPanel.elements.add(new Panel.PanelElement(loadedEntitiesSearch, 0.75f));

        clearAllEntites = new Button("clearAllEntities", "Clear all", new Vector2f(), new Vector2f(99f, getFontHeight(10) * 1.25f), 10, renderer, loader, window) {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if (button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)
                    mainView.clearEntities();
            }

            @Override
            public void secondThread() {
                super.secondThread();

                this.pos.x = window.width - 306;
                this.pos.y = Math.ceil(window.height - getFontHeight(10) * 1.25f) - 6;
            }

            @Override
            public void resize() {
                super.resize();

                this.pos.x = window.width - 306;
                this.pos.y = Math.ceil(window.height - getFontHeight(10) * 1.25f) - 6;
            }
        };
        newThing = new Button("newThing", "New", new Vector2f(), new Vector2f(99f, getFontHeight(10) * 1.25f), 10, renderer, loader, window) {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if (button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)
                {
                    bog.lbpas.view3d.core.types.Thing newThing = new bog.lbpas.view3d.core.types.Thing(new Thing(), loader);
                    newThing.thing.name = "some kind of thing";
                    mainView.things.add(newThing);
                }
            }

            @Override
            public void secondThread() {
                super.secondThread();

                this.pos.x = window.width - 306 + 99f + 2;
                this.pos.y = Math.ceil(window.height - getFontHeight(10) * 1.25f) - 6;
            }

            @Override
            public void resize() {
                super.resize();

                this.pos.x = window.width - 306 + 99f + 2;
                this.pos.y = Math.ceil(window.height - getFontHeight(10) * 1.25f) - 6;
            }
        };
        sortEntityList = new Button("sortEntityList", "Sort list", new Vector2f(), new Vector2f(99f, getFontHeight(10) * 1.25f), 10, renderer, loader, window) {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if (button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS) {
                    Collections.sort(mainView.things,
                            new Comparator<bog.lbpas.view3d.core.types.Thing>() {
                                @Override
                                public int compare(bog.lbpas.view3d.core.types.Thing e1, bog.lbpas.view3d.core.types.Thing e2) {
                                    return Boolean.compare(
                                            ((bog.lbpas.view3d.core.types.Thing)e2).thing.hasPart(Part.RENDER_MESH),
                                            ((bog.lbpas.view3d.core.types.Thing)e1).thing.hasPart(Part.RENDER_MESH));
                                }
                            }
                                    .thenComparing(new Comparator<bog.lbpas.view3d.core.types.Thing>() {
                                        @Override
                                        public int compare(bog.lbpas.view3d.core.types.Thing e1, bog.lbpas.view3d.core.types.Thing e2) {
                                            return e1.thing.name.compareToIgnoreCase(e2.thing.name);
                                        }
                                    }));
                }
            }

            @Override
            public void secondThread() {
                super.secondThread();

                this.pos.x = window.width - 306 + (99f + 2) * 2;
                this.pos.y = Math.ceil(window.height - getFontHeight(10) * 1.25f) - 6;
            }

            @Override
            public void resize() {
                super.resize();

                this.pos.x = window.width - 306 + (99f + 2) * 2;
                this.pos.y = Math.ceil(window.height - getFontHeight(10) * 1.25f) - 6;
            }
        };
        move = (new ButtonImage("move", new Vector2f(window.width - 345, 21 + 10), new Vector2f(30, 30), renderer, loader, window) {
            @Override
            public void clickedButton(int button, int action, int mods) {
            }

            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overOther) {
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

                this.pos.x = window.width - 345;
            }

            @Override
            public void resize() {
                super.resize();

                this.pos.x = window.width - 345;
            }

            @Override
            public void getImage() {
                buttonImage = ConstantTextures.getTexture(ConstantTextures.TRANSFORMATION_MOVE, 30, 30, loader);
            }
        }).clicked();
        rotate = new ButtonImage("rotate", new Vector2f(window.width - 345, 21 + 10 + 37), new Vector2f(30, 30), renderer, loader, window) {
            @Override
            public void clickedButton(int button, int action, int mods) {
            }

            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overOther) {
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

                this.pos.x = window.width - 345;
            }

            @Override
            public void resize() {
                super.resize();

                this.pos.x = window.width - 345;
            }

            @Override
            public void getImage() {
                buttonImage = ConstantTextures.getTexture(ConstantTextures.TRANSFORMATION_ROTATE, 30, 30, loader);
            }
        };
        scale = new ButtonImage("scale", new Vector2f(window.width - 345, 21 + 10 + 74), new Vector2f(30, 30), renderer, loader, window) {
            @Override
            public void clickedButton(int button, int action, int mods) {
            }

            @Override
            public void setClicked(boolean clicked) {
            }

            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overOther) {
                if (button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos) && !overOther) {
                    isClicked = true;
                    move.isClicked = false;
                    rotate.isClicked = false;
                }
            }

            @Override
            public void secondThread() {
                super.secondThread();

                this.pos.x = window.width - 345;
            }

            @Override
            public void resize() {
                super.resize();

                this.pos.x = window.width - 345;
            }

            @Override
            public void getImage() {
                buttonImage = ConstantTextures.getTexture(ConstantTextures.TRANSFORMATION_SCALE, 30, 30, loader);
            }
        };

        preview = new ButtonImage("preview", new Vector2f(window.width - 345, 21 + 10 + 111), new Vector2f(30, 30), renderer, loader, window) {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(action == GLFW.GLFW_PRESS)
                {
                    Config.PREVIEW_MODE = !Config.PREVIEW_MODE;
                    this.buttonImage = Config.PREVIEW_MODE ? ConstantTextures.getTexture(ConstantTextures.VISIBILITY_OFF, 30, 30, loader) : ConstantTextures.getTexture(ConstantTextures.VISIBILITY, 30, 30, loader);
                }
            }

            @Override
            public void secondThread() {
                super.secondThread();

                this.pos.x = window.width - 345;
            }

            @Override
            public void resize() {
                super.resize();

                this.pos.x = window.width - 345;
            }

            @Override
            public void getImage() {
                buttonImage = Config.PREVIEW_MODE ? ConstantTextures.getTexture(ConstantTextures.VISIBILITY_OFF, 30, 30, loader) : ConstantTextures.getTexture(ConstantTextures.VISIBILITY, 30, 30, loader);
            }
        };

        this.guiElements.add(loadedEntitiesHitbox);
        this.guiElements.add(loadedEntities);
        this.guiElements.add(loadPlanElements);
        this.guiElements.add(loadedSearchPanel);
        this.guiElements.add(clearAllEntites);
        this.guiElements.add(newThing);
        this.guiElements.add(sortEntityList);
        this.guiElements.add(move);
        this.guiElements.add(rotate);
        this.guiElements.add(scale);
        this.guiElements.add(preview);

        this.guiElements.add(camPos);
        this.guiElements.add(currentSelection);
        this.guiElements.add(helpers);
        this.guiElements.add(availableAssets);

    }

    @Override
    public void draw(MouseInput mouseInput) {

        boolean overElement = false;
        boolean hasSelection = false;
        int selectedAmount = 0;

        for(Element element : guiElements)
            if (element.isMouseOverElement(mouseInput))
                overElement = true;

        boolean hasPPos = false;

        for(Entity entity : mainView.things)
            if(entity.selected)
            {
                hasSelection = true;
                selectedAmount++;
                if(((bog.lbpas.view3d.core.types.Thing)entity).thing.hasPart(Part.POS))
                    hasPPos = true;
            }

        if(hasSelection && hasPPos)
            elementTool.updateModels(mainView, mainView.getSelectedPosition());

        if(mouseInput.inWindow)
        {
            for(Entity entity : mainView.things)
                entity.highlighted = false;

            elementTool.testForMouse(hasSelection && hasPPos, mainView.camera, mouseInput.mousePicker,
                    move.isClicked,
                    rotate.isClicked,
                    scale.isClicked);
        }

        if(!mouseInput.inWindow || overElement)
            for(int i = 0; i < mainView.things.size(); i++)
                mainView.things.get(i).highlighted = false;

        if(elementTool.isSelected() && hasPPos)
        {
            switch (elementTool.selected)
            {
                //0,1,2 pos
                //3,4,5 rot
                //7,8,9 scale
                case 0:
                    //x pos
                {
                    Vector3f ppos = mainView.getSelectedPosition();

                    boolean failed = false;

                    if ((mainView.camera.getWrappedRotation().x < 45f && mainView.camera.getWrappedRotation().x > -45f) ||
                            (mainView.camera.getWrappedRotation().x > 135f && mainView.camera.getWrappedRotation().x < -135f))
                    {
                        Vector3f currentPosOnZ = mouseInput.mousePicker.getPointOnPlaneZ(ppos.z);
                        if(currentPosOnZ == null || elementTool.initPosZ == null)
                            failed = true;
                        else
                        {
                            mainView.setSelectedPosition(new Vector3f(ppos.x + (currentPosOnZ.x - elementTool.initPosZ.x), ppos.y, ppos.z));
                            elementTool.initPosZ = currentPosOnZ;
                        }
                    }
                    else
                        failed = true;

                    if(failed)
                    {
                        Vector3f currentPosOnY = mouseInput.mousePicker.getPointOnPlaneY(ppos.y);

                        if(currentPosOnY != null && elementTool.initPosY != null)
                        {
                            mainView.setSelectedPosition(new Vector3f(ppos.x + (currentPosOnY.x - elementTool.initPosY.x), ppos.y, ppos.z));
                            elementTool.initPosY = currentPosOnY;
                        }
                    }
                }
                break;
                case 1:
                    //y pos
                {
                    Vector3f ppos = mainView.getSelectedPosition();

                    boolean failed = false;

                    if ((mainView.camera.getWrappedRotation().y < 45f && mainView.camera.getWrappedRotation().y > -45f) ||
                            (mainView.camera.getWrappedRotation().y > 135f && mainView.camera.getWrappedRotation().y < -135f))
                    {
                        Vector3f currentPosOnZ = mouseInput.mousePicker.getPointOnPlaneZ(ppos.z);
                        if(currentPosOnZ == null || elementTool.initPosZ == null)
                            failed = true;
                        else
                        {
                            mainView.setSelectedPosition(new Vector3f(ppos.x, ppos.y + (currentPosOnZ.y - elementTool.initPosZ.y), ppos.z));
                            elementTool.initPosZ = currentPosOnZ;
                        }
                    }
                    else
                        failed = true;

                    if(failed)
                    {
                        Vector3f currentPosOnX = mouseInput.mousePicker.getPointOnPlaneX(ppos.x);

                        if(currentPosOnX != null && elementTool.initPosX != null)
                        {
                            mainView.setSelectedPosition(new Vector3f(ppos.x, ppos.y + (currentPosOnX.y - elementTool.initPosX.y), ppos.z));
                            elementTool.initPosX = currentPosOnX;
                        }
                    }
                }
                break;
                case 2:
                    //z pos
                {
                    Vector3f ppos = mainView.getSelectedPosition();

                    boolean failed = false;

                    if ((mainView.camera.getWrappedRotation().x < 45f && mainView.camera.getWrappedRotation().x > -45f) ||
                            (mainView.camera.getWrappedRotation().x > 135f && mainView.camera.getWrappedRotation().x < -135f))
                    {
                        Vector3f currentPosOnX = mouseInput.mousePicker.getPointOnPlaneX(ppos.x);
                        if(currentPosOnX == null || elementTool.initPosX == null)
                            failed = true;
                        else
                        {
                            mainView.setSelectedPosition(new Vector3f(ppos.x, ppos.y, ppos.z + (currentPosOnX.z - elementTool.initPosX.z)));
                            elementTool.initPosX = currentPosOnX;
                        }
                    }
                    else
                        failed = true;

                    if(failed)
                    {
                        Vector3f currentPosOnY = mouseInput.mousePicker.getPointOnPlaneY(ppos.y);

                        if(currentPosOnY != null && elementTool.initPosY != null)
                        {
                            mainView.setSelectedPosition(new Vector3f(ppos.x, ppos.y, ppos.z + (currentPosOnY.z - elementTool.initPosY.z)));
                            elementTool.initPosY = currentPosOnY;
                        }
                    }
                }
                break;
                case 3:
                    //x rot
                {
                    double y = mouseInput.currentPos.y - elementTool.screenPos.y;
                    double x = mouseInput.currentPos.x - elementTool.screenPos.x;
                    float ang = (float) Math.atan2(y, x);
                    float diff = ang - elementTool.lastAng;
                    if(mainView.camera.getWrappedRotation().y < 0)
                        diff *= -1;
                    elementTool.lastAng = ang;
                    Vector3f centerPoint = new Vector3f(mainView.getSelectedPosition());

                    for (bog.lbpas.view3d.core.types.Thing ent : mainView.things)
                        if (ent.selected)
                        {
                            ent.getTransformation().rotateAroundLocal(new Quaternionf().rotateLocalX(diff), centerPoint.x, centerPoint.y, centerPoint.z, ent.getTransformation());
                            if(ent.forceOrtho)
                                ent.rotation = new Quaternionf().rotateXYZ(ent.rotation.x, ent.rotation.y, ent.rotation.z).rotateLocalX(diff).getEulerAnglesXYZ(new Vector3f());
                        }
                }
                break;
                case 4:
                    //y rot
                {
                    double y = mouseInput.currentPos.y - elementTool.screenPos.y;
                    double x = mouseInput.currentPos.x - elementTool.screenPos.x;
                    float ang = (float) Math.atan2(y, x);
                    float diff = ang - elementTool.lastAng;
                    if(mainView.camera.getWrappedRotation().x > 0)
                        diff *= -1;
                    elementTool.lastAng = ang;
                    Vector3f centerPoint = mainView.getSelectedPosition();

                    for (bog.lbpas.view3d.core.types.Thing ent : mainView.things)
                        if (ent.selected) {
                            ent.getTransformation().rotateAroundLocal(new Quaternionf().rotateLocalY(diff), centerPoint.x, centerPoint.y, centerPoint.z, ent.getTransformation());
                            if(ent.forceOrtho)
                                ent.rotation = new Quaternionf().rotateXYZ(ent.rotation.x, ent.rotation.y, ent.rotation.z).rotateLocalY(diff).getEulerAnglesXYZ(new Vector3f());
                        }
                }
                break;
                case 5:
                    //z rot
                {
                    double y = mouseInput.currentPos.y - elementTool.screenPos.y;
                    double x = mouseInput.currentPos.x - elementTool.screenPos.x;
                    float ang = (float) Math.atan2(y, x);
                    float diff = ang - elementTool.lastAng;
                    if(mainView.camera.getWrappedRotation().y < 90 && mainView.camera.getWrappedRotation().y > -90)
                        diff *= -1;
                    elementTool.lastAng = ang;
                    Vector3f centerPoint = mainView.getSelectedPosition();

                    for (bog.lbpas.view3d.core.types.Thing ent : mainView.things)
                        if (ent.selected)
                        {
                            ent.getTransformation().rotateAroundLocal(new Quaternionf().rotateLocalZ(diff), centerPoint.x, centerPoint.y, centerPoint.z, ent.getTransformation());
                            if(ent.forceOrtho)
                                ent.rotation = new Quaternionf().rotateXYZ(ent.rotation.x, ent.rotation.y, ent.rotation.z).rotateLocalZ(diff).getEulerAnglesXYZ(new Vector3f());
                        }
                }
                break;
                case 6:
                    //x scale
                {
                    Vector3f ppos = mainView.getSelectedPosition();

                    boolean failed = false;

                    if ((mainView.camera.getWrappedRotation().x < 45f && mainView.camera.getWrappedRotation().x > -45f) ||
                            (mainView.camera.getWrappedRotation().x > 135f && mainView.camera.getWrappedRotation().x < -135f))
                    {
                        Vector3f currentPosOnZ = mouseInput.mousePicker.getPointOnPlaneZ(ppos.z);
                        if(currentPosOnZ == null || elementTool.initPosZ == null)
                            failed = true;
                        else
                        {
                            for (bog.lbpas.view3d.core.types.Thing ent : mainView.things)
                                if (ent.selected)
                                {
                                    Vector3f difference = new Vector3f(currentPosOnZ).sub(elementTool.initPosZ);
                                    ent.getTransformation().scaleAroundLocal(1 + difference.x * 0.005f, 1, 1, ppos.x, ppos.y, ppos.z);
                                    if(ent.forceOrtho)
                                        ent.scale.x += difference.x * 0.005f;
                                }
                            elementTool.initPosZ = currentPosOnZ;
                        }
                    }
                    else
                        failed = true;

                    if(failed)
                    {
                        Vector3f currentPosOnY = mouseInput.mousePicker.getPointOnPlaneY(ppos.y);

                        if(currentPosOnY != null && elementTool.initPosY != null)
                        {
                            for (bog.lbpas.view3d.core.types.Thing ent : mainView.things)
                                if (ent.selected)
                                {
                                    Vector3f difference = new Vector3f(currentPosOnY).sub(elementTool.initPosY);
                                    ent.getTransformation().scaleAroundLocal(1 + difference.x * 0.005f, 1, 1, ppos.x, ppos.y, ppos.z);
                                    if(ent.forceOrtho)
                                        ent.scale.x += difference.x * 0.005f;
                                }
                            elementTool.initPosY = currentPosOnY;
                        }
                    }
                }
                break;
                case 7:
                    //y scale
                {
                    Vector3f ppos = mainView.getSelectedPosition();

                    boolean failed = false;

                    if ((mainView.camera.getWrappedRotation().y < 45f && mainView.camera.getWrappedRotation().y > -45f) ||
                            (mainView.camera.getWrappedRotation().y > 135f && mainView.camera.getWrappedRotation().y < -135f))
                    {
                        Vector3f currentPosOnZ = mouseInput.mousePicker.getPointOnPlaneZ(ppos.z);
                        if(currentPosOnZ == null || elementTool.initPosZ == null)
                            failed = true;
                        else
                        {
                            for (bog.lbpas.view3d.core.types.Thing ent : mainView.things)
                                if (ent.selected)
                                {
                                    Vector3f difference = new Vector3f(currentPosOnZ).sub(elementTool.initPosZ);
                                    ent.getTransformation().scaleAroundLocal(1, 1 + difference.y * 0.005f, 1, ppos.x, ppos.y, ppos.z);
                                    if(ent.forceOrtho)
                                        ent.scale.y += difference.y * 0.005f;
                                }
                            elementTool.initPosZ = currentPosOnZ;
                        }
                    }
                    else
                        failed = true;

                    if(failed) {
                        Vector3f currentPosOnX = mouseInput.mousePicker.getPointOnPlaneX(ppos.x);

                        if(currentPosOnX != null && elementTool.initPosX != null)
                        {
                            for (bog.lbpas.view3d.core.types.Thing ent : mainView.things)
                                if (ent.selected) {
                                    Vector3f difference = new Vector3f(currentPosOnX).sub(elementTool.initPosX);
                                    ent.getTransformation().scaleAroundLocal(1, 1 + difference.y * 0.005f, 1, ppos.x, ppos.y, ppos.z);
                                    if(ent.forceOrtho)
                                        ent.scale.y += difference.y * 0.005f;
                                }
                            elementTool.initPosX = currentPosOnX;
                        }
                    }
                }
                break;
                case 8:
                    //z scale
                {
                    Vector3f ppos = mainView.getSelectedPosition();

                    boolean failed = false;

                    if ((mainView.camera.getWrappedRotation().x < 45f && mainView.camera.getWrappedRotation().x > -45f) ||
                            (mainView.camera.getWrappedRotation().x > 135f && mainView.camera.getWrappedRotation().x < -135f))
                    {
                        Vector3f currentPosOnX = mouseInput.mousePicker.getPointOnPlaneX(ppos.x);
                        if(currentPosOnX == null || elementTool.initPosX == null)
                            failed = true;
                        else
                        {
                            for (bog.lbpas.view3d.core.types.Thing ent : mainView.things)
                                if (ent.selected)
                                {
                                    Vector3f difference = new Vector3f(currentPosOnX).sub(elementTool.initPosX);
                                    ent.getTransformation().scaleAroundLocal(1, 1, 1 + difference.z * 0.005f, ppos.x, ppos.y, ppos.z);
                                    if(ent.forceOrtho)
                                        ent.scale.z += difference.z * 0.005f;
                                    if(((bog.lbpas.view3d.core.types.Thing)ent).thing.hasPart(Part.SHAPE))
                                    {
                                        PShape shape = ((bog.lbpas.view3d.core.types.Thing)ent).thing.getPart(Part.SHAPE);
                                        shape.thickness += difference.z;
                                    }
                                }
                            elementTool.initPosX = currentPosOnX;
                        }
                    }
                    else
                        failed = true;

                    if(failed)
                    {
                        Vector3f currentPosOnY = mouseInput.mousePicker.getPointOnPlaneY(ppos.y);

                        if(currentPosOnY != null && elementTool.initPosY != null)
                        {
                            for (bog.lbpas.view3d.core.types.Thing ent : mainView.things)
                                if (ent.selected)
                                {
                                    Vector3f difference = new Vector3f(currentPosOnY).sub(elementTool.initPosY);
                                    ent.getTransformation().scaleAroundLocal(1, 1, 1 + difference.z * 0.005f, ppos.x, ppos.y, ppos.z);
                                    if(ent.forceOrtho)
                                        ent.scale.z += difference.z * 0.005f;
                                    if(((bog.lbpas.view3d.core.types.Thing)ent).thing.hasPart(Part.SHAPE))
                                    {
                                        Vector3f curTrans = ent.getTransformation().getTranslation(new Vector3f());
                                        ent.getTransformation().setTranslation(new Vector3f()).scaleLocal(1, 1, 1 / ent.getTransformation().getScale(new Vector3f()).z).setTranslation(curTrans);
                                        PShape shape = ((bog.lbpas.view3d.core.types.Thing)ent).thing.getPart(Part.SHAPE);
                                        shape.thickness += difference.z;
                                    }
                                }
                            elementTool.initPosY = currentPosOnY;
                        }
                    }
                }
                break;
                case 9:
                    //u scale
                {
                    float mousediff = (float) (mouseInput.currentPos.x - elementTool.initPos.x);

                    elementTool.initPos.x = (float) mouseInput.currentPos.x;
                    elementTool.initPos.y = (float) mouseInput.currentPos.y;

                    Vector3f centerPoint = mainView.getSelectedPosition();

                    for (bog.lbpas.view3d.core.types.Thing ent : mainView.things)
                        if (ent.selected)
                        {
                            ent.getTransformation().scaleAroundLocal(1 + (mousediff * (mainView.camera.pos.distance(centerPoint))) / 100000f,  centerPoint.x, centerPoint.y, centerPoint.z);
                            if(ent.forceOrtho)
                                ent.scale.add(new Vector3f((mousediff * (mainView.camera.pos.distance(centerPoint))) / 100000f));
                            if(((bog.lbpas.view3d.core.types.Thing)ent).thing.hasPart(Part.SHAPE))
                            {
                                Vector3f curTrans = ent.getTransformation().getTranslation(new Vector3f());
                                ent.getTransformation().setTranslation(new Vector3f()).scaleLocal(1, 1, 1 / ent.getTransformation().getScale(new Vector3f()).z).setTranslation(curTrans);
                                PShape shape = ((bog.lbpas.view3d.core.types.Thing)ent).thing.getPart(Part.SHAPE);
                                shape.thickness += (mousediff * (mainView.camera.pos.distance(centerPoint))) / 1000f;
                            }
                        }
                }
                break;
            }
        }

        if(hasPPos)
            elementTool.render(hasSelection,
                    move.isClicked,
                    rotate.isClicked,
                    scale.isClicked, window, loader, renderer, mouseInput);

        renderer.doBlur(Consts.GAUSSIAN_RADIUS, Consts.GAUSSIAN_KERNEL, window.width - 308, 24, 304, window.height - 28);
        renderer.drawRect(window.width - 308, 24, 304, window.height - 28, Config.PRIMARY_COLOR);
        renderer.drawLine(loader, new Vector2i(window.width - 307, 24), new Vector2i(window.width - 307, window.height - 3), Config.SECONDARY_COLOR, false);

        super.draw(mouseInput);
    }

    @Override
    public boolean onClick(MouseInput mouseInput, int button, int action, int mods) {
        boolean onclick = super.onClick(mouseInput, button, action, mods);

        boolean shift = mods == 1;
        boolean ctrl = mods == 2;
        boolean ctrlShift = mods == 3;
        boolean alt = mods == 4;
        boolean shiftAlt = mods == 5;
        boolean ctrlAlt = mods == 6;
        boolean ctrlShiftAlt = mods == 7;
        boolean winKey = mods == 8;

        boolean overElement = false;

        for(Element element : guiElements)
            if (element.isMouseOverElement(mouseInput.currentPos))
                overElement = true;

        elementTool.onClick(mouseInput, button, action, mods, window, mainView.camera);

        if(!overElement && button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && !elementTool.isSelected())
        {
            for (int i = 0; i < mainView.things.size(); i++)
                mainView.things.get(i).testForMouse = true;
            renderer.entityRenderer.triggerMousePick = true;
        }

        return onclick;
    }

    @Override
    public void secondaryThread() {
        super.secondaryThread();

        Config.LEVEL_BORDERS = levelBorders.isChecked;
        Config.POD_HELPER = podHelper.isChecked;

        currentSelectionParts.addParts(mainView.things);
    }

    @Override
    public boolean onKey(int key, int scancode, int action, int mods) {
        boolean elementFocused = super.onKey(key, scancode, action, mods);

        if((key == GLFW.GLFW_KEY_DELETE || key == GLFW.GLFW_KEY_BACKSPACE) && !elementFocused)
        {
            mainView.deleteSelected();
        }

        if(key == GLFW.GLFW_KEY_A && action == GLFW.GLFW_PRESS && mods == GLFW.GLFW_MOD_CONTROL && !elementFocused)
        {
            for(Entity e : mainView.things)
            {
                e.selected = false;
                currentSelectionParts.selectionChange();
            }
            for(Object i : loadedEntities.indexes)
            {
                mainView.things.get((int)i).selected = true;
                currentSelectionParts.selectionChange();
            }
        }

//        if(key == GLFW.GLFW_KEY_O && action == GLFW.GLFW_PRESS)//todo
//            for(Entity e : mainView.things)
//                if(e.selected)
//                    ((bog.lbpas.view3d.core.types.Thing) e).exportModelOBJ();

        return elementFocused;
    }
}