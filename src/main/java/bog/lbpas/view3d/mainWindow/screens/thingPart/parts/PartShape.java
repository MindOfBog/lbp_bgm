package bog.lbpas.view3d.mainWindow.screens.thingPart.parts;

import bog.lbpas.view3d.core.types.Thing;
import bog.lbpas.view3d.mainWindow.LoadedData;
import bog.lbpas.view3d.mainWindow.View3D;
import bog.lbpas.view3d.mainWindow.screens.MaterialEditing;
import bog.lbpas.view3d.managers.MouseInput;
import bog.lbpas.view3d.renderer.gui.elements.*;
import bog.lbpas.view3d.utils.Utils;
import cwlib.enums.AudioMaterial;
import cwlib.enums.LethalType;
import cwlib.enums.Part;
import cwlib.enums.ResourceType;
import cwlib.io.Serializable;
import cwlib.resources.RMesh;
import cwlib.structs.mesh.Bone;
import cwlib.structs.things.components.shapes.Polygon;
import cwlib.structs.things.parts.PRenderMesh;
import cwlib.structs.things.parts.PShape;
import cwlib.types.data.ResourceDescriptor;
import cwlib.util.Colors;
import org.joml.*;
import org.lwjgl.glfw.GLFW;

import java.lang.Math;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Bog
 */
public abstract class PartShape extends iPart {

    public PartShape(int tabWidth, float comboWidth, float panelHeight, float closeWidth, float finalGap, DropDownTab tab, View3D view) {
        super(cwlib.enums.Part.SHAPE, "PShape", "Shape", tabWidth, comboWidth, panelHeight, closeWidth, finalGap, tab, view);
    }

    public Button EditPolygon;
    public Textbox Material;
    public Textbox OldMaterial;
    public Textbox Thickness;
    public Textbox BevelSize;
    public Textbox ColorShininess;
    public Textbox ColorR;
    public Textbox ColorG;
    public Textbox ColorB;
    public Textbox ColorA;
    public Slider Brightness;
    public Slider Opacity;
    public Textbox ColorOffR;
    public Textbox ColorOffG;
    public Textbox ColorOffB;
    public Textbox ColorOffA;
    public Slider BrightnessOff;
    public Slider OpacityOff;
    public ComboBox LethalType;
    public ComboBox Flags;
    public Checkbox FlagCollidableGame;
    public Checkbox FlagCollidablePoppet;
    public Checkbox FlagCollidableWithParent;
    public ComboBox SoundOverride;
    public Checkbox CanCollect;
    public Checkbox Ghosty;
    public Checkbox DefaultClimbable;
    public Checkbox CurrentlyClimbable;
    public Checkbox HeadDucking;
    public Checkbox IsLBP2Shape;
    public Checkbox IsStatic;
    public Checkbox CollidableSackboy;
    public Checkbox CameraExcluderIsSticky;
    public Checkbox Ethereal;
    public Textbox ZBias;
    public Textbox ZBiasVita;
    public Textbox MassDepth;
    public Textbox Behavior;
    public Textbox PlayerNumberColor;
    public Textbox Stickiness;
    public Textbox Grabbability;
    public Textbox GrabFilter;
    public Textbox FireDensity;
    public Textbox FireLifetime;
    public ComboBox Extras;

    @Override
    public void init(View3D view) {

        EditPolygon = new Button() {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)
                {
                    boolean hasSelection = false;
                    ArrayList<Integer> selected = new ArrayList<>();

                    for(int i = 0; i < view.things.size(); i++)
                        if(view.things.get(i).selected)
                        {
                            hasSelection = true;
                            selected.add(i);
                        }

                    if(hasSelection && selected.size() == 1 && ((bog.lbpas.view3d.core.types.Thing)view.things.get(selected.get(0))).thing.hasPart(cwlib.enums.Part.SHAPE))
                    {
                        view.setCurrentScreen(view.MaterialEditing);
                        ((MaterialEditing)view.MaterialEditing).selectedVertices.clear();
                    }
                }
            }
        };
        partComboBox.addButton("editPolygon", "Edit Polygon", EditPolygon);

        Panel materialPanel = partComboBox.addPanel("materialPanel");
        materialPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("matstr", "Material:", 10, view.renderer), 0.55f));
        Material = new Textbox("ShapeMaterial", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window);
        materialPanel.elements.add(new Panel.PanelElement(Material, 0.45f));

        Panel oldMaterialPanel = partComboBox.addPanel("oldMaterialPanel");
        oldMaterialPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("oldmatstr", "Old Material:", 10, view.renderer), 0.55f));
        OldMaterial = new Textbox("OldShapeMaterial", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window);
        oldMaterialPanel.elements.add(new Panel.PanelElement(OldMaterial, 0.45f));

        Panel thicknessPanel = partComboBox.addPanel("thicknessPanel");
        thicknessPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("thicknstr", "Thickness:", 10, view.renderer), 0.55f));
        Thickness = new Textbox("ShapeThickness", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window);
        thicknessPanel.elements.add(new Panel.PanelElement(Thickness, 0.45f));

        Panel bevelSizePanel = partComboBox.addPanel("bevelSizePanel");
        bevelSizePanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("bevelaizestr", "Bevel Size:", 10, view.renderer), 0.55f));
        BevelSize = new Textbox("ShapeBevelSize", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window);
        bevelSizePanel.elements.add(new Panel.PanelElement(BevelSize, 0.45f));

        Panel shapeColorShininessPanel = partComboBox.addPanel("shapeColorShininessPanel");
        shapeColorShininessPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("shapecolshininess", "Color Shine:", 10, view.renderer), 0.55f));
        ColorShininess = new Textbox("ShapeColorShininess", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window);
        shapeColorShininessPanel.elements.add(new Panel.PanelElement(ColorShininess, 0.45f));

        partComboBox.addString("shapecolorstr", "Color:");

        float spacing = 0.015f;
        float textboxsize = (1f - (spacing * 3f)) / 4f;

        Panel shapecolor = partComboBox.addPanel("shapecolor");
        ColorR = new Textbox("", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window);
        ColorR.noLetters().noOthers().numberLimits(0, 255);
        shapecolor.elements.add(new Panel.PanelElement(ColorR, textboxsize));

        shapecolor.elements.add(new Panel.PanelElement(null, spacing));
        ColorG = new Textbox("", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window);
        ColorG.noLetters().noOthers().numberLimits(0, 255);
        shapecolor.elements.add(new Panel.PanelElement(ColorG, textboxsize));

        shapecolor.elements.add(new Panel.PanelElement(null, spacing));
        ColorB = new Textbox("", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window);
        ColorB.noLetters().noOthers().numberLimits(0, 255);
        shapecolor.elements.add(new Panel.PanelElement(ColorB, textboxsize));

        shapecolor.elements.add(new Panel.PanelElement(null, spacing));
        ColorA = new Textbox("", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window);
        ColorA.noLetters().noOthers().numberLimits(0, 255);
        shapecolor.elements.add(new Panel.PanelElement(ColorA, textboxsize));

        Panel shapebrightnessPanel = partComboBox.addPanel("shapebrightnessPanel");
        shapebrightnessPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("brightnessstr", "Brightness:", 10, view.renderer), 0.55f));
        Brightness = new Slider("ShapeBrightness", new Vector2f(), new Vector2f(), view.renderer, view.loader, view.window, 1, 0, 1);
        shapebrightnessPanel.elements.add(new Panel.PanelElement(Brightness, 0.45f));

        Panel shapeopacityPanel = partComboBox.addPanel("shapeopacityPanel");
        shapeopacityPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("shapeopacitystr", "Opacity:", 10, view.renderer), 0.55f));
        Opacity = new Slider("ShapeOpacity", new Vector2f(), new Vector2f(), view.renderer, view.loader, view.window, 1, 0, 1);
        shapeopacityPanel.elements.add(new Panel.PanelElement(Opacity, 0.45f));

        partComboBox.addString("shapeoffcolorstr", "Color Off:");

        Panel shapecoloroff = partComboBox.addPanel("shapecoloroff");
        ColorOffR = new Textbox("", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window);
        ColorOffR.noLetters().noOthers().numberLimits(0, 255);
        shapecoloroff.elements.add(new Panel.PanelElement(ColorOffR, textboxsize));

        shapecoloroff.elements.add(new Panel.PanelElement(null, spacing));
        ColorOffG = new Textbox("", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window);
        ColorOffG.noLetters().noOthers().numberLimits(0, 255);
        shapecoloroff.elements.add(new Panel.PanelElement(ColorOffG, textboxsize));

        shapecoloroff.elements.add(new Panel.PanelElement(null, spacing));
        ColorOffB = new Textbox("", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window);
        ColorOffB.noLetters().noOthers().numberLimits(0, 255);
        shapecoloroff.elements.add(new Panel.PanelElement(ColorOffB, textboxsize));

        shapecoloroff.elements.add(new Panel.PanelElement(null, spacing));
        ColorOffA = new Textbox("", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window);
        ColorOffA.noLetters().noOthers().numberLimits(0, 255);
        shapecoloroff.elements.add(new Panel.PanelElement(ColorOffA, textboxsize));

        Panel shapeoffbrightnessPanel = partComboBox.addPanel("shapeoffbrightnessPanel");
        shapeoffbrightnessPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("shapeoffbrightnessstr", "Brightn. Off:", 10, view.renderer), 0.55f));
        BrightnessOff = new Slider("ShapeBrightnessOff", new Vector2f(), new Vector2f(), view.renderer, view.loader, view.window, 1, 0, 1);
        shapeoffbrightnessPanel.elements.add(new Panel.PanelElement(BrightnessOff, 0.45f));

        Panel shapeoffopacityPanel = partComboBox.addPanel("shapeoffopacityPanel");
        shapeoffopacityPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("shapeoffopacitystr", "Opacity Off:", 10, view.renderer), 0.55f));
        OpacityOff = new Slider("ShapeOpacityOff", new Vector2f(), new Vector2f(), view.renderer, view.loader, view.window, 1, 0, 1);
        shapeoffopacityPanel.elements.add(new Panel.PanelElement(OpacityOff, 0.45f));

        Panel lethalPanel = partComboBox.addPanel("lethalPanel");
        lethalPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("lethalstr", "Lethal Type:", 10, view.renderer), 0.55f));
        LethalType = new ComboBox("ShapeLethalType", "Not", new Vector2f(), new Vector2f(), 10, 160,view.renderer, view.loader, view.window);
        lethalPanel.elements.add(new Panel.PanelElement(LethalType, 0.45f));

        LethalType.addList("lethalTypes", new ButtonList("lethalTypes", new ArrayList<>(Arrays.asList(cwlib.enums.LethalType.values())), new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window) {
            @Override
            public void clickedButton(Object object, int index, int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)
                {
                    clicked = index;

                    String name = ((LethalType)object).name().replaceAll("_", " ");
                    name = name.substring(0, 1) + name.substring(1).toLowerCase();
                    for (int i = 0; i < view.things.size(); i++)
                        if (view.things.get(i).selected)
                            ((PShape)((Thing)view.things.get(i)).thing.getPart(cwlib.enums.Part.SHAPE)).lethalType = (LethalType) object;
                    LethalType.tabTitle = name;
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
                String name = ((LethalType)object).name().replaceAll("_", " ");
                name = name.substring(0, 1) + name.substring(1).toLowerCase();
                return name;
            }

            @Override
            public boolean searchFilter(Object object, int index) {
                return true;
            }
        }, 175);

        Panel shapeoverridepanel = partComboBox.addPanel("shapeoverridepanel");
        shapeoverridepanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("soundoverridestr", "Sound Overr.:", 10, view.renderer), 0.55f));
        SoundOverride = new ComboBox("ShapeSoundOverride", "None", new Vector2f(), new Vector2f(), 10, 160, view.renderer, view.loader, view.window);
        shapeoverridepanel.elements.add(new Panel.PanelElement(SoundOverride, 0.45f));

        SoundOverride.addList("lethalTypes", new ButtonList("lethalTypes", new ArrayList<>(Arrays.asList(AudioMaterial.values())), new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window) {
            @Override
            public void clickedButton(Object object, int index, int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)
                {
                    clicked = index;

                    String name = ((AudioMaterial)object).name().replaceAll("_", " ");
                    name = name.substring(0, 1) + name.substring(1).toLowerCase();
                    for (int i = 0; i < view.things.size(); i++)
                        if (view.things.get(i).selected)
                            ((PShape)((Thing)view.things.get(i)).thing.getPart(cwlib.enums.Part.SHAPE)).soundEnumOverride = (AudioMaterial) object;
                    SoundOverride.tabTitle = name;
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
                String name = ((AudioMaterial)object).name().replaceAll("_", " ");
                name = name.substring(0, 1) + name.substring(1).toLowerCase();
                return name;
            }

            @Override
            public boolean searchFilter(Object object, int index) {
                return true;
            }
        }, 175);

        Flags = partComboBox.addComboBox("ShapeFlags", "Flags", 160);
        FlagCollidableGame = Flags.addCheckbox("COLLIDABLE_GAME", "Collidable Game", new Checkbox()
        {
            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement) {
                super.onClick(mouseInput, pos, button, action, mods, overElement);

                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos) && !overElement)
                    for (int i = 0; i < view.things.size(); i++)
                        if (view.things.get(i).selected)
                        {
                            PShape shape = ((PShape)((Thing)view.things.get(i)).thing.getPart(cwlib.enums.Part.SHAPE));
                            shape.flags = Utils.setBitwiseBool(shape.flags, cwlib.enums.ShapeFlags.COLLIDABLE_GAME, this.isChecked);
                        }
            }
        });
        FlagCollidablePoppet = Flags.addCheckbox("COLLIDABLE_POPPET", "Colli. Poppet", new Checkbox()
        {
            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement) {
                super.onClick(mouseInput, pos, button, action, mods, overElement);

                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos) && !overElement)
                    for (int i = 0; i < view.things.size(); i++)
                        if (view.things.get(i).selected)
                        {
                            PShape shape = ((PShape)((Thing)view.things.get(i)).thing.getPart(cwlib.enums.Part.SHAPE));
                            shape.flags = Utils.setBitwiseBool(shape.flags, cwlib.enums.ShapeFlags.COLLIDABLE_POPPET, this.isChecked);
                        }
            }
        });
        FlagCollidableWithParent = Flags.addCheckbox("COLLIDABLE_WITH_PARENT", "Colli. w/ Parent", new Checkbox()
        {
            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement) {
                super.onClick(mouseInput, pos, button, action, mods, overElement);

                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos) && !overElement)
                    for (int i = 0; i < view.things.size(); i++)
                        if (view.things.get(i).selected)
                        {
                            PShape shape = ((PShape)((Thing)view.things.get(i)).thing.getPart(cwlib.enums.Part.SHAPE));
                            shape.flags = Utils.setBitwiseBool(shape.flags, cwlib.enums.ShapeFlags.COLLIDABLE_WITH_PARENT, this.isChecked);
                        }
            }
        });
        Flags.addButton("DEFAULT_FLAGS", "Default Flags", new Button() {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)
                    for (int i = 0; i < view.things.size(); i++)
                        if (view.things.get(i).selected)
                        {
                            PShape shape = ((PShape)((Thing)view.things.get(i)).thing.getPart(cwlib.enums.Part.SHAPE));
                            shape.flags = cwlib.enums.ShapeFlags.DEFAULT_FLAGS;
                        }
            }
        });

        IsStatic = partComboBox.addCheckbox("isStatic", "Static", new Checkbox()
        {
            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement) {
                super.onClick(mouseInput, pos, button, action, mods, overElement);

                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos) && !overElement)
                    for (int i = 0; i < view.things.size(); i++)
                        if (view.things.get(i).selected)
                            ((PShape)((Thing)view.things.get(i)).thing.getPart(cwlib.enums.Part.SHAPE)).isStatic = this.isChecked;
            }
        });
        Ghosty = partComboBox.addCheckbox("ghosty", "Dephysicalized", new Checkbox()
        {
            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement) {
                super.onClick(mouseInput, pos, button, action, mods, overElement);

                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos) && !overElement)
                    for (int i = 0; i < view.things.size(); i++)
                        if (view.things.get(i).selected)
                            ((PShape)((Thing)view.things.get(i)).thing.getPart(cwlib.enums.Part.SHAPE)).ghosty = this.isChecked;
            }
        });
        Ethereal = partComboBox.addCheckbox("ethereal", "Etherial", new Checkbox()
        {
            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement) {
                super.onClick(mouseInput, pos, button, action, mods, overElement);

                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos) && !overElement)
                    for (int i = 0; i < view.things.size(); i++)
                        if (view.things.get(i).selected)
                            ((PShape)((Thing)view.things.get(i)).thing.getPart(cwlib.enums.Part.SHAPE)).ethereal = this.isChecked;
            }
        });

        Panel shapeZBiasPanel = partComboBox.addPanel("shapeZBiasPanel");
        shapeZBiasPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("shapezbiasstr", "Z Bias:", 10, view.renderer), 0.55f));
        ZBias = new Textbox("ShapeZBias", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window);
        ZBias.noLetters().noOthers().numberLimits(-128, 127);
        shapeZBiasPanel.elements.add(new Panel.PanelElement(ZBias, 0.45f));

        Extras = partComboBox.addComboBox("ShapeExtras", "Extras", 220);

        Panel shapeZBiasVitaPanel = Extras.addPanel("shapeZBiasVitaPanel");
        shapeZBiasVitaPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("shapezbiasvstr", "Z Bias (Vita):", 10, view.renderer), 0.55f));
        ZBiasVita = new Textbox("ShapeZBiasVita", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window);
        shapeZBiasVitaPanel.elements.add(new Panel.PanelElement(ZBiasVita, 0.45f));

        Panel shapeMassDepthPanel = Extras.addPanel("shapeMassDepthPanel");
        shapeMassDepthPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("shapemassdepthstr", "Mass Depth:", 10, view.renderer), 0.55f));
        MassDepth = new Textbox("ShapeMassDepth", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window);
        shapeMassDepthPanel.elements.add(new Panel.PanelElement(MassDepth, 0.45f));

        Panel shapeBehaviorPanel = Extras.addPanel("shapeBehaviorPanel");
        shapeBehaviorPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("shapebehaviorstr", "Behavior:", 10, view.renderer), 0.55f));
        Behavior = new Textbox("ShapeBehavior", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window);
        shapeBehaviorPanel.elements.add(new Panel.PanelElement(Behavior, 0.45f));

        Panel shapePlayerNumberColorPanel = Extras.addPanel("shapePlayerNumberColorPanel");
        shapePlayerNumberColorPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("shapeplayernumbercolorstr", "Player # Col.:", 10, view.renderer), 0.55f));
        PlayerNumberColor = new Textbox("ShapePlayerNumberColor", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window);
        PlayerNumberColor.noLetters().noOthers().numberLimits(-128, 127);
        shapePlayerNumberColorPanel.elements.add(new Panel.PanelElement(PlayerNumberColor, 0.45f));

        Panel shapeStickinessPanel = Extras.addPanel("shapeStickinessPanel");
        shapeStickinessPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("shapestickinessstr", "Stickiness:", 10, view.renderer), 0.55f));
        Stickiness = new Textbox("ShapeStickiness", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window);
        Stickiness.noLetters().noOthers().numberLimits(-128, 127);
        shapeStickinessPanel.elements.add(new Panel.PanelElement(Stickiness, 0.45f));

        Panel shapeGrabbabilityPanel = Extras.addPanel("shapeGrabbabilityPanel");
        shapeGrabbabilityPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("shapegrabbabilitystr", "Grabbability:", 10, view.renderer), 0.55f));
        Grabbability = new Textbox("ShapeGrabbability", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window);
        Grabbability.noLetters().noOthers().numberLimits(-128, 127);
        shapeGrabbabilityPanel.elements.add(new Panel.PanelElement(Grabbability, 0.45f));

        Panel shapeGrabFilterPanel = Extras.addPanel("shapeGrabFilterPanel");
        shapeGrabFilterPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("shapegrabfilterstr", "Grab Filter:", 10, view.renderer), 0.55f));
        GrabFilter = new Textbox("ShapeGrabFilter", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window);
        GrabFilter.noLetters().noOthers().numberLimits(-128, 127);
        shapeGrabFilterPanel.elements.add(new Panel.PanelElement(GrabFilter, 0.45f));

        Panel shapeFireDensityPanel = Extras.addPanel("shapeFireDensityPanel");
        shapeFireDensityPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("shapefiredensitystr", "Fire Density:", 10, view.renderer), 0.55f));
        FireDensity = new Textbox("ShapeFireDensity", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window);
        FireDensity.noLetters().noOthers().numberLimits(-128, 127);
        shapeFireDensityPanel.elements.add(new Panel.PanelElement(FireDensity, 0.45f));

        Panel shapeFireLifetimePanel = Extras.addPanel("shapeFireLifetimePanel");
        shapeFireLifetimePanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("shapefirelifetimestr", "Fire Lifetime:", 10, view.renderer), 0.55f));
        FireLifetime = new Textbox("ShapeFireLifetime", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window);
        FireLifetime.noLetters().noOthers().numberLimits(-128, 127);
        shapeFireLifetimePanel.elements.add(new Panel.PanelElement(FireLifetime, 0.45f));

        CanCollect = Extras.addCheckbox("canCollect", "Can collect", new Checkbox()
        {
            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement) {
                super.onClick(mouseInput, pos, button, action, mods, overElement);

                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos) && !overElement)
                    for (int i = 0; i < view.things.size(); i++)
                        if (view.things.get(i).selected)
                            ((PShape)((Thing)view.things.get(i)).thing.getPart(cwlib.enums.Part.SHAPE)).canCollect = this.isChecked;
            }
        });
        DefaultClimbable = Extras.addCheckbox("defaultClimbable", "Default Climbable", new Checkbox()
        {
            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement) {
                super.onClick(mouseInput, pos, button, action, mods, overElement);

                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos) && !overElement)
                    for (int i = 0; i < view.things.size(); i++)
                        if (view.things.get(i).selected)
                            ((PShape)((Thing)view.things.get(i)).thing.getPart(cwlib.enums.Part.SHAPE)).defaultClimbable = this.isChecked;
            }
        });
        CurrentlyClimbable = Extras.addCheckbox("currentlyClimbable", "Current Climbable", new Checkbox()
        {
            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement) {
                super.onClick(mouseInput, pos, button, action, mods, overElement);

                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos) && !overElement)
                    for (int i = 0; i < view.things.size(); i++)
                        if (view.things.get(i).selected)
                            ((PShape)((Thing)view.things.get(i)).thing.getPart(cwlib.enums.Part.SHAPE)).currentlyClimbable = this.isChecked;
            }
        });
        HeadDucking = Extras.addCheckbox("headDucking", "Head Ducking", new Checkbox()
        {
            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement) {
                super.onClick(mouseInput, pos, button, action, mods, overElement);

                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos) && !overElement)
                    for (int i = 0; i < view.things.size(); i++)
                        if (view.things.get(i).selected)
                            ((PShape)((Thing)view.things.get(i)).thing.getPart(cwlib.enums.Part.SHAPE)).headDucking = this.isChecked;
            }
        });
        IsLBP2Shape = Extras.addCheckbox("isLBP2Shape", "LBP2 Shape", new Checkbox()
        {
            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement) {
                super.onClick(mouseInput, pos, button, action, mods, overElement);

                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos) && !overElement)
                    for (int i = 0; i < view.things.size(); i++)
                        if (view.things.get(i).selected)
                            ((PShape)((Thing)view.things.get(i)).thing.getPart(cwlib.enums.Part.SHAPE)).isLBP2Shape = this.isChecked;
            }
        });
        CollidableSackboy = Extras.addCheckbox("collidableSackboy", "Collidable Sackboy", new Checkbox()
        {
            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement) {
                super.onClick(mouseInput, pos, button, action, mods, overElement);

                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos) && !overElement)
                    for (int i = 0; i < view.things.size(); i++)
                        if (view.things.get(i).selected)
                            ((PShape)((Thing)view.things.get(i)).thing.getPart(cwlib.enums.Part.SHAPE)).collidableSackboy = this.isChecked;
            }
        });
        CameraExcluderIsSticky = Extras.addCheckbox("cameraExcluderIsSticky", "Cam Excluder is sticky", new Checkbox()
        {
            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement) {
                super.onClick(mouseInput, pos, button, action, mods, overElement);

                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos) && !overElement)
                    for (int i = 0; i < view.things.size(); i++)
                        if (view.things.get(i).selected)
                            ((PShape)((Thing)view.things.get(i)).thing.getPart(cwlib.enums.Part.SHAPE)).cameraExcluderIsSticky = this.isChecked;
            }
        });
    }

    @Override
    public void addValues(ArrayList<Integer> selected, ArrayList<Thing> things) {

        if(!partComboBox.extended)
            return;

        String mat = null;
        String oldMat = null;
        float thickn = Float.NEGATIVE_INFINITY;
        float bevs = Float.NEGATIVE_INFINITY;
        float colshine = Float.NEGATIVE_INFINITY;
        Vector4f color = null;
        float brightness = Float.NEGATIVE_INFINITY;
        float opacity = Float.NEGATIVE_INFINITY;
        Vector4f colorOff = null;
        float brightnessOff = Float.NEGATIVE_INFINITY;
        float opacityOff = Float.NEGATIVE_INFINITY;
        float zBias = Float.NEGATIVE_INFINITY;
        float zBiasVita = Float.NEGATIVE_INFINITY;
        float massDepth = Float.NEGATIVE_INFINITY;
        float behavior = Float.NEGATIVE_INFINITY;
        float playerNumberColor = Float.NEGATIVE_INFINITY;
        float stickiness = Float.NEGATIVE_INFINITY;
        float grabbability = Float.NEGATIVE_INFINITY;
        float grabFilter = Float.NEGATIVE_INFINITY;
        float fireDensity = Float.NEGATIVE_INFINITY;
        float fireLifetime = Float.NEGATIVE_INFINITY;

        float lethal = Float.NEGATIVE_INFINITY;
        float sound = Float.NEGATIVE_INFINITY;

        int statiq = -1;
        int dephys = -1;
        int etherial = -1;
        int cancollect = -1;
        int defclimbable = -1;
        int curclimbable = -1;
        int headducking = -1;
        int lbp2shape = -1;
        int collisackboy = -1;
        int camexcl = -1;

        int collidGame = -1;
        int collidPoppet = -1;
        int collidParent = -1;

        for(int i : selected)
        {
            Thing thing = things.get(i);
            PShape shape = thing.thing.getPart(Part.SHAPE);

            if(shape == null)
                continue;

            if(statiq == -1)
                statiq = shape.isStatic ? 1 : 0;
            else if(statiq != (shape.isStatic ? 1 : 0))
                statiq = 0;
            if(dephys == -1)
                dephys = shape.ghosty ? 1 : 0;
            else if(dephys != (shape.ghosty ? 1 : 0))
                dephys = 0;
            if(etherial == -1)
                etherial = shape.ethereal ? 1 : 0;
            else if(etherial != (shape.ethereal ? 1 : 0))
                etherial = 0;
            if(cancollect == -1)
                cancollect = shape.canCollect ? 1 : 0;
            else if(cancollect != (shape.canCollect ? 1 : 0))
                cancollect = 0;
            if(defclimbable == -1)
                defclimbable = shape.defaultClimbable ? 1 : 0;
            else if(defclimbable != (shape.defaultClimbable ? 1 : 0))
                defclimbable = 0;
            if(curclimbable == -1)
                curclimbable = shape.currentlyClimbable ? 1 : 0;
            else if(curclimbable != (shape.currentlyClimbable ? 1 : 0))
                curclimbable = 0;
            if(headducking == -1)
                headducking = shape.headDucking ? 1 : 0;
            else if(headducking != (shape.headDucking ? 1 : 0))
                headducking = 0;
            if(lbp2shape == -1)
                lbp2shape = shape.isLBP2Shape ? 1 : 0;
            else if(lbp2shape != (shape.isLBP2Shape ? 1 : 0))
                lbp2shape = 0;
            if(collisackboy == -1)
                collisackboy = shape.collidableSackboy ? 1 : 0;
            else if(collisackboy != (shape.collidableSackboy ? 1 : 0))
                collisackboy = 0;
            if(camexcl == -1)
                camexcl = shape.cameraExcluderIsSticky ? 1 : 0;
            else if(camexcl != (shape.cameraExcluderIsSticky ? 1 : 0))
                camexcl = 0;

            if(collidGame == -1)
                collidGame = Utils.isBitwiseBool(shape.flags, cwlib.enums.ShapeFlags.COLLIDABLE_GAME) ? 1 : 0;
            else if(collidGame != (Utils.isBitwiseBool(shape.flags, cwlib.enums.ShapeFlags.COLLIDABLE_GAME) ? 1 : 0))
                collidGame = 0;
            if(collidPoppet == -1)
                collidPoppet = Utils.isBitwiseBool(shape.flags, cwlib.enums.ShapeFlags.COLLIDABLE_POPPET) ? 1 : 0;
            else if(collidPoppet != (Utils.isBitwiseBool(shape.flags, cwlib.enums.ShapeFlags.COLLIDABLE_POPPET) ? 1 : 0))
                collidPoppet = 0;
            if(collidParent == -1)
                collidParent = Utils.isBitwiseBool(shape.flags, cwlib.enums.ShapeFlags.COLLIDABLE_WITH_PARENT) ? 1 : 0;
            else if(collidParent != (Utils.isBitwiseBool(shape.flags, cwlib.enums.ShapeFlags.COLLIDABLE_WITH_PARENT) ? 1 : 0))
                collidParent = 0;

            String material = shape.material == null ? "" : shape.material.isGUID() ? shape.material.getGUID().toString() : shape.material.getSHA1().toString();
            if(mat == null)
                mat = material;
            else if(!mat.equalsIgnoreCase(material))
                mat = "";

            String oldMaterial = shape.oldMaterial == null ? "" : shape.oldMaterial.isGUID() ? shape.oldMaterial.getGUID().toString() : shape.oldMaterial.getSHA1().toString();
            if(oldMat == null)
                oldMat = oldMaterial;
            else if(!oldMat.equalsIgnoreCase(oldMaterial))
                oldMat = "";

            float thickness = shape.thickness;
            if(Float.isInfinite(thickn))
                thickn = thickness;
            else if(thickn != thickness)
                thickn = Float.NaN;

            float bevelsize = shape.bevelSize;
            if(Float.isInfinite(bevs))
                bevs = bevelsize;
            else if(bevs != bevelsize)
                bevs = Float.NaN;

            float colorshine = shape.colorShininess;
            if(Float.isInfinite(colshine))
                colshine = colorshine;
            else if(colshine != colorshine)
                colshine = Float.NaN;

            Vector4f col = Colors.RGBA32.fromARGB(shape.color);
            if(color == null)
                color = col;
            else if(!(color.x == col.x && color.y == col.y && color.z == col.z && color.w == col.w))
            {
                color.x = Float.NaN;
                color.y = Float.NaN;
                color.z = Float.NaN;
                color.w = Float.NaN;
            }

            float bright = shape.brightness;
            if(Float.isInfinite(brightness))
                brightness = bright;
            else if(brightness != bright)
                brightness = Float.NaN;

            float opac = shape.colorOpacity;
            if(Float.isInfinite(opacity))
                opacity = (128 + opac) / 255;
            else if(opacity != (128 + opac) / 255)
                opacity = Float.NaN;

            Vector4f colOff = Colors.RGBA32.fromARGB(shape.colorOff);
            if(colorOff == null)
                colorOff = colOff;
            else if(!(colorOff.x == colOff.x && colorOff.y == colOff.y && colorOff.z == colOff.z && colorOff.w == colOff.w))
            {
                colorOff.x = Float.NaN;
                colorOff.y = Float.NaN;
                colorOff.z = Float.NaN;
                colorOff.w = Float.NaN;
            }

            float brightOff = shape.brightnessOff;
            if(Float.isInfinite(brightnessOff))
                brightnessOff = brightOff;
            else if(brightnessOff != brightOff)
                brightnessOff = Float.NaN;

            float opacOff = shape.colorOffOpacity;
            if(Float.isInfinite(opacityOff))
                opacityOff = (128 + opacOff) / 255;
            else if(opacityOff != (128 + opacOff) / 255)
                opacityOff = Float.NaN;

            float zBs = shape.zBias;
            if(Float.isInfinite(zBias))
                zBias = zBs;
            else if(zBias != zBs)
                zBias = Float.NaN;

            float zBsV = shape.zBiasVita;
            if(Float.isInfinite(zBiasVita))
                zBiasVita = zBsV;
            else if(zBiasVita != zBsV)
                zBiasVita = Float.NaN;

            float msDepth = shape.massDepth;
            if(Float.isInfinite(massDepth))
                massDepth = msDepth;
            else if(massDepth != msDepth)
                massDepth = Float.NaN;

            float bhvr = shape.behavior;
            if(Float.isInfinite(behavior))
                behavior = bhvr;
            else if(behavior != bhvr)
                behavior = Float.NaN;

            float plnumcol = shape.playerNumberColor;
            if(Float.isInfinite(playerNumberColor))
                playerNumberColor = plnumcol;
            else if(playerNumberColor != plnumcol)
                playerNumberColor = Float.NaN;

            float stickn = shape.stickiness;
            if(Float.isInfinite(stickiness))
                stickiness = stickn;
            else if(stickiness != stickn)
                stickiness = Float.NaN;

            float grabbab = shape.grabbability;
            if(Float.isInfinite(grabbability))
                grabbability = grabbab;
            else if(grabbability != grabbab)
                grabbability = Float.NaN;

            float grabF = shape.grabFilter;
            if(Float.isInfinite(grabFilter))
                grabFilter = grabF;
            else if(grabFilter != grabF)
                grabFilter = Float.NaN;

            float fireD = shape.fireDensity;
            if(Float.isInfinite(fireDensity))
                fireDensity = fireD;
            else if(fireDensity != fireD)
                fireDensity = Float.NaN;

            float fireL = shape.fireLifetime;
            if(Float.isInfinite(fireLifetime))
                fireLifetime = fireL;
            else if(fireLifetime != fireL)
                fireLifetime = Float.NaN;

            float let = shape.lethalType.getValue();
            if(Float.isInfinite(lethal))
                lethal = let;
            else if(lethal != let)
                lethal = Float.NaN;

            float aud = shape.soundEnumOverride.getValue();
            if(Float.isInfinite(sound))
                sound = aud;
            else if(sound != aud)
                sound = Float.NaN;
        }

        IsStatic.isChecked = statiq == 1;
        Ghosty.isChecked = dephys == 1;
        Ethereal.isChecked = etherial == 1;
        CanCollect.isChecked = cancollect == 1;
        DefaultClimbable.isChecked = defclimbable == 1;
        CurrentlyClimbable.isChecked = curclimbable == 1;
        HeadDucking.isChecked = headducking == 1;
        IsLBP2Shape.isChecked = lbp2shape == 1;
        CollidableSackboy.isChecked = collisackboy == 1;
        CameraExcluderIsSticky.isChecked = camexcl == 1;

        FlagCollidableGame.isChecked = collidGame == 1;
        FlagCollidablePoppet.isChecked = collidPoppet == 1;
        FlagCollidableWithParent.isChecked = collidParent == 1;

        if(!Float.isNaN(lethal) && !Float.isInfinite(lethal))
        {
            String name = cwlib.enums.LethalType.fromValue((int) lethal).name().replaceAll("_", " ");
            name = name.substring(0, 1) + name.substring(1).toLowerCase();

            LethalType.tabTitle = name;
        }
        else
            LethalType.tabTitle = "";

        if(!Float.isNaN(sound) && !Float.isInfinite(sound))
        {
            String name = AudioMaterial.fromValue((int) sound).name().replaceAll("_", " ");
            name = name.substring(0, 1) + name.substring(1).toLowerCase();

            SoundOverride.tabTitle = name;
        }
        else
            SoundOverride.tabTitle = "";

        String material = Material.setTextboxValueString(mat);
        String oldMaterial = OldMaterial.setTextboxValueString(oldMat);
        Vector2f thickness = Thickness.setTextboxValueFloat(thickn);
        Vector2f bevelSize = BevelSize.setTextboxValueFloat(bevs);
        Vector2i colorShine = ColorShininess.setTextboxValueInt((byte)Math.round(colshine));
        Vector2i colorr = ColorR.setTextboxValueInt(Math.round(color.x * 255));
        Vector2i colorg = ColorG.setTextboxValueInt(Math.round(color.y * 255));
        Vector2i colorb = ColorB.setTextboxValueInt(Math.round(color.z * 255));
        Vector2i colora = ColorA.setTextboxValueInt(Math.round(color.w * 255));
        Vector2f brightn = Brightness.setSliderValue(brightness);
        Vector2f opac = Opacity.setSliderValue(opacity);
        Vector2i coloroffr = ColorOffR.setTextboxValueInt(Math.round(colorOff.x * 255));
        Vector2i coloroffg = ColorOffG.setTextboxValueInt(Math.round(colorOff.y * 255));
        Vector2i coloroffb = ColorOffB.setTextboxValueInt(Math.round(colorOff.z * 255));
        Vector2i coloroffa = ColorOffA.setTextboxValueInt(Math.round(colorOff.w * 255));
        Vector2f brightnoff = BrightnessOff.setSliderValue(brightnessOff);
        Vector2f opacoff = OpacityOff.setSliderValue(opacityOff);
        Vector2i zBs = ZBias.setTextboxValueInt((byte)Math.round(zBias));
        Vector2f zBsV = ZBiasVita.setTextboxValueFloat(zBiasVita);
        Vector2f massD = MassDepth.setTextboxValueFloat(massDepth);
        Vector2i beha = Behavior.setTextboxValueInt(Math.round(behavior));
        Vector2i playernumcol = PlayerNumberColor.setTextboxValueInt((byte)Math.round(playerNumberColor));
        Vector2i sticki = Stickiness.setTextboxValueInt((byte)Math.round(stickiness));
        Vector2i grabba = Grabbability.setTextboxValueInt((byte)Math.round(grabbability));
        Vector2i grabF = GrabFilter.setTextboxValueInt((byte)Math.round(grabFilter));
        Vector2i fireD = FireDensity.setTextboxValueInt((byte)Math.round(fireDensity));
        Vector2i fireL = FireLifetime.setTextboxValueInt((byte)Math.round(fireLifetime));

        for(int i : selected)
        {
            Thing thing = things.get(i);
            PShape shape = thing.thing.getPart(Part.SHAPE);

            if(shape == null)
                continue;

            if(material != null)
            {
                shape.material = null;
                try{shape.material = new ResourceDescriptor(material.trim(), ResourceType.MATERIAL);}catch (Exception e){}
            }
            if(oldMaterial != null)
            {
                shape.oldMaterial = null;
                try{shape.oldMaterial = new ResourceDescriptor(oldMaterial.trim(), ResourceType.MATERIAL);}catch (Exception e){}
            }
            if(thickness.y == 1)
                shape.thickness = thickness.x;
            if(bevelSize.y == 1)
                shape.bevelSize = bevelSize.x;
            if(colorShine.y == 1)
                shape.colorShininess = (byte) colorShine.x;
            if(colorr.y == 1 || colorg.y == 1 || colorb.y == 1 || colora.y == 1)
            {
                Vector4f col = Colors.RGBA32.fromARGB(shape.color);
                shape.color = Colors.RGBA32.getARGB(new Vector4f(colorr.y == 1 ? colorr.x / 255f : col.x, colorg.y == 1 ? colorg.x / 255f : col.y, colorb.y == 1 ? colorb.x / 255f : col.z, colora.y == 1 ? colora.x / 255f : col.w));
            }
            if(brightn.y == 1 && !Float.isNaN(brightn.x))
                shape.brightness = brightn.x;
            if(opac.y == 1)
                shape.colorOpacity = (byte) (opac.x * 255 - 128);
            if(coloroffr.y == 1 || coloroffg.y == 1 || coloroffb.y == 1 || coloroffa.y == 1)
            {
                Vector4f col = Colors.RGBA32.fromARGB(shape.colorOff);
                shape.colorOff = Colors.RGBA32.getARGB(new Vector4f(coloroffr.y == 1 ? coloroffr.x / 255f : col.x, coloroffg.y == 1 ? coloroffg.x / 255f : col.y, coloroffb.y == 1 ? coloroffb.x / 255f : col.z, coloroffa.y == 1 ? coloroffa.x / 255f : col.w));
            }
            if(brightnoff.y == 1)
                shape.brightnessOff = brightnoff.x;
            if(opacoff.y == 1)
                shape.colorOffOpacity = (byte) (opacoff.x * 255 - 128);
            if(zBs.y == 1)
                shape.zBias = (byte) zBs.x;
            if(zBsV.y == 1)
                shape.zBiasVita = zBsV.x;
            if(massD.y == 1)
                shape.massDepth = massD.x;
            if(beha.y == 1)
                shape.behavior = beha.x;
            if(playernumcol.y == 1)
                shape.playerNumberColor = (byte) playernumcol.x;
            if(sticki.y == 1)
                shape.stickiness = (byte) sticki.x;
            if(grabba.y == 1)
                shape.grabbability = (byte) grabba.x;
            if(grabF.y == 1)
                shape.grabFilter = (byte) grabF.x;
            if(fireD.y == 1)
                shape.fireDensity = (byte) fireD.x;
            if(fireL.y == 1)
                shape.fireLifetime = (byte) fireL.x;
        }
    }

    @Override
    public <T extends Serializable> void addPart(ArrayList<Thing> things, Thing thing, T p) {
        PShape newShape = (PShape) p;
        Matrix4f matrix = new Matrix4f(thing.getTransformation());
        Vector3f pos = matrix.getTranslation(new Vector3f());
        Matrix4f inv = matrix.invert();

        newShape.polygon.vertices[0].add(pos).mulProject(inv);
        newShape.polygon.vertices[1].add(pos).mulProject(inv);
        newShape.polygon.vertices[2].add(pos).mulProject(inv);
        newShape.polygon.vertices[3].add(pos).mulProject(inv);

        if(!thing.thing.hasPart(Part.RENDER_MESH))
            return;

        PRenderMesh pMesh = thing.thing.getPart(Part.RENDER_MESH);
        RMesh rMesh = LoadedData.loadedModels.get(pMesh.mesh).mesh;
        Bone[] bones = rMesh.getBones();

        for(int b = 0; b < bones.length; b++)
        {
            Bone bone = bones[b];
            PShape shape = new PShape();

            if(pMesh.boneThings[b].hasPart(Part.SHAPE))
                shape = pMesh.boneThings[b].getPart(Part.SHAPE);

            if(shape.polygon == null)
                shape.polygon = new Polygon();

            if(bone == null ||
                    bone.shapeInfos == null || bone.shapeInfos.length == 0 ||
                    bone.shapeVerts == null || bone.shapeVerts.length == 0)
                continue;

            shape.polygon.vertices = new Vector3f[bone.shapeVerts.length];
            shape.polygon.loops = new int[bone.shapeInfos.length];

            for(int l = 0; l < bone.shapeInfos.length; l++)
                shape.polygon.loops[l] = bone.shapeInfos[l].numVerts;

            for(int v = 0; v < bone.shapeVerts.length; v++)
                shape.polygon.vertices[v] = new Vector3f(bone.shapeVerts[v].localPos.x,
                        bone.shapeVerts[v].localPos.y,
                        bone.shapeVerts[v].localPos.z);

            pMesh.boneThings[b].setPart(Part.SHAPE, shape);

            int finalB = b;
            for(Thing t : things.stream().filter(Thing -> Thing.thing == pMesh.boneThings[finalB]).toArray(Thing[]::new))
                t.reloadModel();
        }
    }
}