package bog.lbpas.view3d.mainWindow.screens.thingPart.parts;

import bog.lbpas.view3d.core.types.Thing;
import bog.lbpas.view3d.mainWindow.View3D;
import bog.lbpas.view3d.managers.MouseInput;
import bog.lbpas.view3d.renderer.gui.elements.*;
import bog.lbpas.view3d.renderer.gui.elements.Button;
import bog.lbpas.view3d.renderer.gui.elements.Checkbox;
import bog.lbpas.view3d.renderer.gui.elements.Panel;
import bog.lbpas.view3d.utils.Config;
import bog.lbpas.view3d.utils.Utils;
import cwlib.enums.Part;
import cwlib.enums.ResourceType;
import cwlib.enums.ShadowType;
import cwlib.structs.mesh.Bone;
import cwlib.structs.things.parts.PRenderMesh;
import cwlib.types.data.ResourceDescriptor;
import cwlib.util.Colors;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Bog
 */
public abstract class PartRenderMesh extends iPart {

    public PartRenderMesh(int tabWidth, float comboWidth, float panelHeight, float closeWidth, float finalGap, DropDownTab tab, View3D view) {
        super(cwlib.enums.Part.RENDER_MESH, "PRenderMesh", "Render Mesh", tabWidth, comboWidth, panelHeight, closeWidth, finalGap, tab, view);
    }

    public Textbox Mesh;
    public Textbox Anim;
    public Textbox AnimPos;
    public Textbox AnimSpeed;
    public Textbox AnimStart;
    public Textbox AnimEnd;
    public ColorPicker EditorColor;
    public Checkbox RTT;
    public Checkbox FlagPlayMode;
    public Checkbox FlagEditMode;
    public Checkbox AnimLoop;
    public ComboBox CastShadows;
    public ComboBox VisibilityFlags;
    public Textbox RenderScale;
    public Textbox DistanceFront;
    public Textbox DistanceSide;

    public ArrayList<String> bones;
    int selectedBone = 0;
    ComboBox setThingCombo;

    @Override
    public void init(View3D view) {

        bones = new ArrayList<>();

        Panel meshPanel = partComboBox.addPanel("meshPanel");
        meshPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("meshstr", "Mesh:", 10, view.renderer), 0.55f));
        Mesh = new Textbox("RMeshMesh", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window);
        meshPanel.elements.add(new Panel.PanelElement(Mesh, 0.45f));

        ComboBox boneThings = partComboBox.addComboBox("boneThingsPanel", "Bone Things", 200);
        boneThings.addList("bones", new ButtonList(bones, 10, view.renderer, view.loader, view.window) {
            int hovering = -1;

            @Override
            public void clickedButton(Object object, int index, int button, int action, int mods) {
                selectedBone = index;
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
                return selectedBone == index;
            }

            @Override
            public String buttonText(Object object, int index) {
                return (String) object;
            }

            @Override
            public boolean searchFilter(Object object, int index) {
                return true;
            }

            @Override
            public void draw(MouseInput mouseInput, boolean overElement) {
                hovering = -1;
                super.draw(mouseInput, overElement);
            }

            @Override
            public int buttonHeight() {
                return super.buttonHeight() + 4;
            }
        }, 250);

        Panel setBoneThingPanel = boneThings.addPanel("setBoneThingPanel");
        setBoneThingPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("setBoneThingstr", "Thing:", 10, view.renderer), 0.275f));
        setThingCombo = new ComboBox("setThingCombo", "None", 10, 200, view.renderer, view.loader, view.window)
        {
            @Override
            public int[] getParentTransform() {
                return boneThings.getTabPosWidth();
            }
        };

        Button clearParent = setThingCombo.addButton("clearParent", "None", new Button() {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)
                {
                    for(int i = 0; i < view.things.size(); i++)
                        if(view.things.get(i).selected)
                            if(view.things.get(i).renderMesh != null)
                            {
                                PRenderMesh rmesh = view.things.get(i).thing.getPart(Part.RENDER_MESH);
                                rmesh.boneThings[selectedBone] = null;
                            }
                    setThingCombo.tabTitle = "None";
                }
            }
        });

        Textbox searchBoneThings = new Textbox("searchBoneThings", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window);
        Panel searchBoneThingsPanel = setThingCombo.addPanel("searchBoneThingsPanel");
        searchBoneThingsPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("srchPrnt", "Search:", 10, view.renderer), 0.4f));
        searchBoneThingsPanel.elements.add(new Panel.PanelElement(searchBoneThings, 0.6f));

        setThingCombo.addList("boneThingList", new ButtonList(view.things, 10, view.renderer, view.loader, view.window) {

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
                        for(int i = 0; i < view.things.size(); i++)
                            if(view.things.get(i).selected)
                                if(view.things.get(i).renderMesh != null)
                                {
                                    PRenderMesh rmesh = view.things.get(i).thing.getPart(Part.RENDER_MESH);
                                    rmesh.boneThings[selectedBone] = ((bog.lbpas.view3d.core.types.Thing)object).thing;
                                }
                        setThingCombo.tabTitle = ((bog.lbpas.view3d.core.types.Thing)object).thing.name;
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
                return ((bog.lbpas.view3d.core.types.Thing)object).thing == view.getSelectedParent();
            }

            @Override
            public String buttonText(Object object, int index) {
                String name = ((bog.lbpas.view3d.core.types.Thing)object).thing.name;
                return name == null ? "null" : name;
            }

            @Override
            public boolean searchFilter(Object object, int index) {
                return buttonText(object, index).toLowerCase().contains(searchBoneThings.getText().toLowerCase());
            }

            @Override
            public int buttonHeight() {
                return super.buttonHeight() + 4;
            }
        }, 295);

        setBoneThingPanel.elements.add(new Panel.PanelElement(setThingCombo, 0.725f));

        Panel animPanel = partComboBox.addPanel("animPanel");
        animPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("animstr", "Animation:", 10, view.renderer), 0.55f));
        Anim = new Textbox("RMeshAnim", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window);
        animPanel.elements.add(new Panel.PanelElement(Anim, 0.45f));

        Panel animPosPanel = partComboBox.addPanel("animPosPanel");
        animPosPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("animposstr", "Anim. Pos.:", 10, view.renderer), 0.55f));
        AnimPos = new Textbox("RMeshAnimPos", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window);
        AnimPos.noLetters().noOthers();
        animPosPanel.elements.add(new Panel.PanelElement(AnimPos, 0.45f));

        Panel animSpeedPanel = partComboBox.addPanel("animSpeedPanel");
        animSpeedPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("animspeedstr", "Anim. Speed:", 10, view.renderer), 0.55f));
        AnimSpeed = new Textbox("RMeshAnimSpeed", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window);
        AnimSpeed.noLetters().noOthers();
        animSpeedPanel.elements.add(new Panel.PanelElement(AnimSpeed, 0.45f));

        Panel animStartPanel = partComboBox.addPanel("animStartPanel");
        animStartPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("animstartstr", "Anim. Start:", 10, view.renderer), 0.55f));
        AnimStart = new Textbox("RMeshAnimStart", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window);
        AnimStart.noLetters().noOthers();
        animStartPanel.elements.add(new Panel.PanelElement(AnimStart, 0.45f));

        Panel animEndPanel = partComboBox.addPanel("animEndPanel");
        animEndPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("animendstr", "Anim. End:", 10, view.renderer), 0.55f));
        AnimEnd = new Textbox("RMeshAnimEnd", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window);
        AnimEnd.noLetters().noOthers();
        animEndPanel.elements.add(new Panel.PanelElement(AnimEnd, 0.45f));

        AnimLoop = partComboBox.addCheckbox("RMeshAnimLoop", "Animation Loop", new Checkbox()
        {
            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement, boolean focusedOther) {
                super.onClick(mouseInput, pos, button, action, mods, overElement, focusedOther);

                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos) && !overElement)
                    for (int i = 0; i < view.things.size(); i++)
                        if (view.things.get(i).selected)
                            ((PRenderMesh)((Thing)view.things.get(i)).thing.getPart(cwlib.enums.Part.RENDER_MESH)).animLoop = this.isChecked;
            }
        });

        Panel editcolor = partComboBox.addPanel("editcolor");
        editcolor.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("editcolorstr", "Editor Color:", 10, view.renderer), 0.55f));
        EditorColor = new ColorPicker("EditorColor", 10, view.renderer, view.loader, view.window) {
            @Override
            public Color getColor() {
                if(edColor == null)
                    return null;

                return new Color(edColor.x, edColor.y, edColor.z, edColor.w);
            }

            @Override
            public void setColor(Color color) {

                for(Thing t : view.things)
                    if(t.selected)
                    {
                        PRenderMesh rmesh = t.thing.getPart(Part.RENDER_MESH);
                        rmesh.editorColor = Colors.RGBA32.getARGB(Utils.toVectorColor(color));
                    }

            }
        };
        editcolor.elements.add(new Panel.PanelElement(EditorColor, 0.45f));

        RTT = partComboBox.addCheckbox("RMeshRTT", "RTT Enable", new Checkbox()
        {
            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement, boolean focusedOther) {
                super.onClick(mouseInput, pos, button, action, mods, overElement, focusedOther);

                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos) && !overElement)
                    for (int i = 0; i < view.things.size(); i++)
                        if (view.things.get(i).selected)
                            ((PRenderMesh)((Thing)view.things.get(i)).thing.getPart(cwlib.enums.Part.RENDER_MESH)).RTTEnable = this.isChecked;
            }
        });

        Panel shadowCastPanel = partComboBox.addPanel("shadowCastPanel");
        shadowCastPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("shadowstr", "Cast Shadows:", 10, view.renderer), 0.55f));
        CastShadows = new ComboBox("RMeshCastShadows", "Always", new Vector2f(), new Vector2f(), 10, 160,view.renderer, view.loader, view.window);
        shadowCastPanel.elements.add(new Panel.PanelElement(CastShadows, 0.45f));

        CastShadows.addList("shadowTypes", new ButtonList("shadowTypes", new ArrayList<>(Arrays.asList(ShadowType.values())), new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window) {
            @Override
            public void clickedButton(Object object, int index, int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)
                {
                    clicked = index;

                    String name = ((ShadowType)object).name().replaceAll("_", " ");
                    name = name.substring(0, 1) + name.substring(1).toLowerCase();
                    for (int i = 0; i < view.things.size(); i++)
                        if (view.things.get(i).selected)
                            ((PRenderMesh)((Thing)view.things.get(i)).thing.getPart(cwlib.enums.Part.RENDER_MESH)).castShadows = (ShadowType) object;
                    CastShadows.tabTitle = name;
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
                String name = ((ShadowType)object).name().replaceAll("_", " ");
                name = name.substring(0, 1) + name.substring(1).toLowerCase();
                return name;
            }

            @Override
            public boolean searchFilter(Object object, int index) {
                return true;
            }
        }, 52);

        VisibilityFlags = partComboBox.addComboBox("RMeshVisibilityFlags", "Visibility Flags", 160);
        FlagPlayMode = VisibilityFlags.addCheckbox("PLAY_MODE", "Play mode", new Checkbox()
        {
            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement, boolean focusedOther) {
                super.onClick(mouseInput, pos, button, action, mods, overElement, focusedOther);

                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos) && !overElement)
                    for (int i = 0; i < view.things.size(); i++)
                        if (view.things.get(i).selected)
                        {
                            PRenderMesh rmesh = ((PRenderMesh)((Thing)view.things.get(i)).thing.getPart(cwlib.enums.Part.RENDER_MESH));
                            rmesh.visibilityFlags = Utils.setBitwiseBool(rmesh.visibilityFlags, cwlib.enums.VisibilityFlags.PLAY_MODE, this.isChecked);
                        }
            }
        });
        FlagEditMode = VisibilityFlags.addCheckbox("EDIT_MODE", "Edit mode", new Checkbox()
        {
            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement, boolean focusedOther) {
                super.onClick(mouseInput, pos, button, action, mods, overElement, focusedOther);

                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos) && !overElement)
                    for (int i = 0; i < view.things.size(); i++)
                        if (view.things.get(i).selected)
                        {
                            PRenderMesh rmesh = ((PRenderMesh)((Thing)view.things.get(i)).thing.getPart(cwlib.enums.Part.RENDER_MESH));
                            rmesh.visibilityFlags = Utils.setBitwiseBool(rmesh.visibilityFlags, cwlib.enums.VisibilityFlags.EDIT_MODE, this.isChecked);
                        }
            }
        });

        Panel renderScalePanel = partComboBox.addPanel("renderScalePanel");
        renderScalePanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("renderscalestr", "Render Scale:", 10, view.renderer), 0.55f));
        RenderScale = new Textbox("RMeshRenderScale", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window);
        RenderScale.noLetters().noOthers();
        renderScalePanel.elements.add(new Panel.PanelElement(RenderScale, 0.45f));

        Panel distFrontPanel = partComboBox.addPanel("distFrontPanel");
        distFrontPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("distfrontstr", "Dist. Front:", 10, view.renderer), 0.55f));
        DistanceFront = new Textbox("RMeshDistanceFront", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window);
        DistanceFront.noLetters().noOthers();
        distFrontPanel.elements.add(new Panel.PanelElement(DistanceFront, 0.45f));

        Panel distSidePanel = partComboBox.addPanel("distSidePanel");
        distSidePanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("distsidestr", "Dist. Side:", 10, view.renderer), 0.55f));
        DistanceSide = new Textbox("RMeshDistanceSide", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window);
        DistanceSide.noLetters().noOthers();
        distSidePanel.elements.add(new Panel.PanelElement(DistanceSide, 0.45f));
    }

    Vector4f edColor = null;

    @Override
    public void addValues(ArrayList<Integer> selected, ArrayList<Thing> things) {

        String mesh = null;
        String animation = null;
        float animPosition = Float.NEGATIVE_INFINITY;
        float animSpeed = Float.NEGATIVE_INFINITY;
        float animStart = Float.NEGATIVE_INFINITY;
        float animEnd = Float.NEGATIVE_INFINITY;
        int animationLoop = -1;
        int rttEnable = -1;
        int visiblePlay = -1;
        int visibleEdit = -1;
        String castShadows = null;
        float renderScale = Float.NEGATIVE_INFINITY;
        float distanceFront = Float.NEGATIVE_INFINITY;
        float distanceSide = Float.NEGATIVE_INFINITY;
        edColor = null;

        bones.clear();

        for(int i : selected) {
            Thing thing = things.get(i);
            PRenderMesh rmesh = thing.thing.getPart(Part.RENDER_MESH);

            if (rmesh == null)
                continue;

            if(selected.size() == 1)
            {
                setThingCombo.tabTitle = rmesh.boneThings[selectedBone] == null ? "None" : rmesh.boneThings[selectedBone].name;
                for(Bone bt : thing.renderMesh.mesh.getBones())
                    bones.add(bt.getName());
            }
            else
                setThingCombo.tabTitle = "";

            String msh = rmesh.mesh == null ? "" : rmesh.mesh.isGUID() ? rmesh.mesh.getGUID().toString() : rmesh.mesh.getSHA1().toString();
            if (mesh == null)
                mesh = msh;
            else if (!mesh.equalsIgnoreCase(msh))
                mesh = "";

            String anim = rmesh.anim == null ? "" : rmesh.anim.isGUID() ? rmesh.anim.getGUID().toString() : rmesh.anim.getSHA1().toString();
            if (animation == null)
                animation = anim;
            else if (!animation.equalsIgnoreCase(anim))
                animation = "";

            float animPos = rmesh.animPos;
            if (Float.isInfinite(animPosition))
                animPosition = animPos;
            else if (animPosition != animPos)
                animPosition = Float.NaN;

            float animSp = rmesh.animSpeed;
            if (Float.isInfinite(animSpeed))
                animSpeed = animSp;
            else if (animSpeed != animSp)
                animSpeed = Float.NaN;

            float animSta = rmesh.loopStart;
            if (Float.isInfinite(animStart))
                animStart = animSta;
            else if (animStart != animSta)
                animStart = Float.NaN;

            float animEn = rmesh.loopEnd;
            if (Float.isInfinite(animEnd))
                animEnd = animEn;
            else if (animEnd != animEn)
                animEnd = Float.NaN;

            if (animationLoop == -1)
                animationLoop = rmesh.animLoop ? 1 : 0;
            else if (animationLoop != (rmesh.animLoop ? 1 : 0))
                animationLoop = 0;

            if (rttEnable == -1)
                rttEnable = rmesh.RTTEnable ? 1 : 0;
            else if (rttEnable != (rmesh.RTTEnable ? 1 : 0))
                rttEnable = 0;

            if (visiblePlay == -1)
                visiblePlay = Utils.isBitwiseBool(rmesh.visibilityFlags, cwlib.enums.VisibilityFlags.PLAY_MODE) ? 1 : 0;
            else if (visiblePlay != (Utils.isBitwiseBool(rmesh.visibilityFlags, cwlib.enums.VisibilityFlags.PLAY_MODE) ? 1 : 0))
                visiblePlay = 0;
            if (visibleEdit == -1)
                visibleEdit = Utils.isBitwiseBool(rmesh.visibilityFlags, cwlib.enums.VisibilityFlags.EDIT_MODE) ? 1 : 0;
            else if (visibleEdit != (Utils.isBitwiseBool(rmesh.visibilityFlags, cwlib.enums.VisibilityFlags.EDIT_MODE) ? 1 : 0))
                visibleEdit = 0;

            if (rmesh.castShadows != null)
            {
                String cstshadow = rmesh.castShadows.name();
                if (castShadows == null)
                    castShadows = cstshadow;
                else if (!castShadows.equalsIgnoreCase(cstshadow))
                    castShadows = "";
            }

            float renScale = rmesh.poppetRenderScale;
            if(Float.isInfinite(renderScale))
                renderScale = renScale;
            else if(renderScale != renScale)
                renderScale = Float.NaN;

            float distFront = rmesh.parentDistanceFront;
            if(Float.isInfinite(distanceFront))
                distanceFront = distFront;
            else if(distanceFront != distFront)
                distanceFront = Float.NaN;

            float distSide = rmesh.parentDistanceSide;
            if(Float.isInfinite(distanceSide))
                distanceSide = distSide;
            else if(distanceSide != distSide)
                distanceSide = Float.NaN;

            Vector4f col = Colors.RGBA32.fromARGB(rmesh.editorColor);

            if(edColor == null)
                edColor = col;
            else if(!(edColor.x == col.x && edColor.y == col.y && edColor.z == col.z && edColor.w == col.w))
            {
                edColor.x = Float.NaN;
                edColor.y = Float.NaN;
                edColor.z = Float.NaN;
                edColor.w = Float.NaN;
            }
        }

        AnimLoop.isChecked = animationLoop == 1;
        RTT.isChecked = rttEnable == 1;
        FlagPlayMode.isChecked = visiblePlay == 1;
        FlagEditMode.isChecked = visibleEdit == 1;

        if(castShadows != null)
        {
            String name = castShadows.replaceAll("_", " ");

            if(name.length() > 0)
            name = name.substring(0, 1) + name.substring(1).toLowerCase();

            CastShadows.tabTitle = name;
        }
        else
            CastShadows.tabTitle = "";

        String msh = Mesh.setTextboxValueString(mesh);
        String anim = Anim.setTextboxValueString(animation);
        Vector2f animPos = AnimPos.setTextboxValueFloat(animPosition);
        Vector2f animSp = AnimSpeed.setTextboxValueFloat(animSpeed);
        Vector2f animSt = AnimStart.setTextboxValueFloat(animStart);
        Vector2f animEn = AnimEnd.setTextboxValueFloat(animEnd);
        Vector2f renSca = RenderScale.setTextboxValueFloat(renderScale);
        Vector2f distFront = DistanceFront.setTextboxValueFloat(distanceFront);
        Vector2f distSide = DistanceSide.setTextboxValueFloat(distanceSide);

        for(int i : selected) {
            Thing thing = things.get(i);
            PRenderMesh rmesh = thing.thing.getPart(Part.RENDER_MESH);

            if(rmesh == null)
                continue;

            long prevGuidMesh = rmesh.mesh == null || !rmesh.mesh.isGUID() ? -1 : rmesh.mesh.getGUID().getValue();
            String prevSHA1Mesh = rmesh.mesh == null || !rmesh.mesh.isHash() ? "" : rmesh.mesh.getSHA1().toString();

            if (msh != null)
            {
                try{rmesh.mesh = new ResourceDescriptor(msh.trim(), ResourceType.MESH);}catch (Exception e){}

                if((rmesh.mesh.isGUID() && rmesh.mesh.getGUID().getValue() != prevGuidMesh) ||
                        (rmesh.mesh.isHash() && !rmesh.mesh.getSHA1().toString().equalsIgnoreCase(prevSHA1Mesh)))
                    thing.reloadModel();
            }
            if (anim != null)
                try{rmesh.anim = new ResourceDescriptor(anim.trim(), ResourceType.ANIMATION);}catch (Exception e){}

            if(animPos.y == 1)
                rmesh.animPos = animPos.x;
            if(animSp.y == 1)
                rmesh.animSpeed = animSp.x;
            if(animSt.y == 1)
                rmesh.loopStart = animSt.x;
            if(animEn.y == 1)
                rmesh.loopEnd = animEn.x;
            if(renSca.y == 1)
                rmesh.poppetRenderScale = renSca.x;
            if(distFront.y == 1)
                rmesh.parentDistanceFront = distFront.x;
            if(distSide.y == 1)
                rmesh.parentDistanceSide = distSide.x;
        }

    }

    @Override
    public void selectionChange() {
        super.selectionChange();
        EditorColor.updateColorValues();
        selectedBone = 0;
        setThingCombo.tabTitle = "None";
    }

    @Override
    public void onExtendPart() {
        super.onExtendPart();
        EditorColor.updateColorValues();
    }
}
