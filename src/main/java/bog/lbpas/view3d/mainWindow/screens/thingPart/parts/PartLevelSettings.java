package bog.lbpas.view3d.mainWindow.screens.thingPart.parts;

import bog.lbpas.view3d.mainWindow.View3D;
import bog.lbpas.view3d.managers.MouseInput;
import bog.lbpas.view3d.renderer.gui.elements.*;
import bog.lbpas.view3d.renderer.gui.elements.Button;
import bog.lbpas.view3d.renderer.gui.elements.Checkbox;
import bog.lbpas.view3d.renderer.gui.elements.Panel;
import bog.lbpas.view3d.utils.CWLibUtils.LevelSettingsUtils;
import bog.lbpas.view3d.utils.print;
import com.github.weisj.jsvg.nodes.text.Text;
import cwlib.enums.Part;
import cwlib.enums.ResourceType;
import cwlib.io.Serializable;
import cwlib.resources.RLevel;
import cwlib.resources.RPlan;
import cwlib.structs.things.Thing;
import cwlib.structs.things.components.LevelSettings;
import cwlib.structs.things.parts.PLevelSettings;
import cwlib.structs.things.parts.PShape;
import cwlib.structs.things.parts.PTrigger;
import cwlib.types.Resource;
import cwlib.types.data.ResourceDescriptor;
import org.joml.*;
import org.joml.Math;
import org.lwjgl.glfw.GLFW;
import toolkit.utilities.FileChooser;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;

/**
 * @author Bog
 */
public abstract class PartLevelSettings extends iPart {

    public PartLevelSettings(int tabWidth, float comboWidth, float panelHeight, float closeWidth, float finalGap, DropDownTab tab, View3D view) {
        super(cwlib.enums.Part.LEVEL_SETTINGS, "PLevelSettings", "Level Settings", tabWidth, comboWidth, panelHeight, closeWidth, finalGap, tab, view);
    }

    boolean multiple;

    public void hasPart(Thing thing)
    {
        if (!thing.hasPart(this.part) || (thing.hasPart(this.part) && multiple))
        {
            hasPart = false;
            partComboBox.collapsed(true);
        }
        else multiple = true;
    }

    @Override
    public void addPartsReset() {
        super.addPartsReset();

        multiple = false;
    }

    Button ImportPlanBin;
    ButtonList Presets;
    ComboBox PresetEdit;
    ArrayList<LevelSettings> presets;
    LevelSettings selectedPreset;
    Textbox Ambiance;

    Checkbox nonLinearFog;
    Textbox backdropMesh;
    Textbox backgroundSkyHeight;

    Textbox sunScale;
    Textbox sunX;
    Textbox sunY;
    Textbox sunZ;
    Textbox sunColorR;
    Textbox sunColorG;
    Textbox sunColorB;
    Textbox sunColorA;
    Textbox sunMultiplier;
    Textbox ambientColorR;
    Textbox ambientColorG;
    Textbox ambientColorB;
    Textbox ambientColorA;
    Textbox exposure;
    Textbox fogColorR;
    Textbox fogColorG;
    Textbox fogColorB;
    Textbox fogColorA;
    Textbox fogNear;
    Textbox fogFar;
    Textbox rimColorR;
    Textbox rimColorG;
    Textbox rimColorB;
    Textbox rimColorA;
    Textbox rimColor2R;
    Textbox rimColor2G;
    Textbox rimColor2B;
    Textbox rimColor2A;
    Textbox bakedShadowAmount;
    Textbox bakedShadowBlur;
    Textbox bakedAOBias;
    Textbox bakedAOScale;
    Textbox dynamicAOAmount;
    Textbox dofNear;
    Textbox dofFar;
    Textbox zEffectAmount;
    Textbox zEffectBrightness;
    Textbox zEffectContrast;

    @Override
    public void init(View3D view) {

        Panel importPanel = partComboBox.addPanel("importPanel");
        ImportPlanBin = new Button("ImportPlanBin", "Import", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window) {
            @Override
            public void clickedButton(int button, int action, int mods) {
                File file = null;
                try {
                    if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)
                        file = FileChooser.openFile(null, "plan,pln,bin", false, false)[0];
                }catch (Exception ex){}

                if (file == null || !file.exists()) return;

                String ext = file.getAbsolutePath().toString();
                ext = ext.substring(ext.lastIndexOf(".") + 1);

                switch (ext) {
                    case "plan":
                    case "pln":
                        try {
                            RPlan plan = new Resource(file.getAbsolutePath()).loadResource(RPlan.class);
                            if (plan == null) return;

                            Thing[] things = plan.getThings();

                            for (Thing thing : things) {
                                if (thing == null) continue;

                                PLevelSettings ls = thing.getPart(Part.LEVEL_SETTINGS);
                                if (ls == null) continue;

                                for (LevelSettings setting : ls.presets)
                                    presets.add(setting);

                                if (ls.presets.isEmpty())
                                    presets.add(LevelSettingsUtils.translate(ls));

                                for(bog.lbpas.view3d.core.types.Thing th : view.things)
                                    if(th.selected && th.thing.hasPart(Part.LEVEL_SETTINGS))
                                        ((PLevelSettings)th.thing.getPart(Part.LEVEL_SETTINGS)).backdropAmbience = ls.backdropAmbience;
                            }

                        } catch (Exception ex) {
                            view.pushError("File Loading", ex.getLocalizedMessage());
                            print.stackTrace(ex);
                        }
                        break;
                    case "bin":
                        try {
                            RLevel level = new Resource(file.getAbsolutePath()).loadResource(RLevel.class);
                            if (level == null) return;

                            PLevelSettings ls = level.world.getPart(Part.LEVEL_SETTINGS);
                            presets.add(LevelSettingsUtils.translate(ls));
                        } catch (Exception ex) {
                            view.pushError("File Loading", ex.getLocalizedMessage());
                            print.stackTrace(ex);
                        }
                        break;
                    default:
                        view.pushError("File Loading", "Unknown file type.");
                        break;
                }
            }
        };
        importPanel.elements.add(new Panel.PanelElement(ImportPlanBin, 0.4975f));
        importPanel.elements.add(new Panel.PanelElement(null, 0.005f));

        ComboBox importTemplatesCombo = new ComboBox("importTemplatesCombo", "Templates", new Vector2f(), new Vector2f(), 10, 200, view.renderer, view.loader, view.window);

        ArrayList<String> templateNames = new ArrayList<>();
        templateNames.add("Blank (LBP1)");templateNames.add("Tutorials (LBP1)");templateNames.add("Gardens");templateNames.add("Savannah");templateNames.add("Wedding");templateNames.add("Canyons");templateNames.add("Metropolis");templateNames.add("Islands");templateNames.add("Temples");templateNames.add("Wilderness");templateNames.add("Blank (LBP2)");templateNames.add("Tutorials (LBP2)");templateNames.add("Da Vinci's Hideout");templateNames.add("Victoria's Laboratory");templateNames.add("Factory of a Better Tomorrow");templateNames.add("Avalonia");templateNames.add("Eve's Asylum");templateNames.add("Cosmos");
        ButtonList templateList = new ButtonList("templateList", templateNames, new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window) {
            @Override
            public void clickedButton(Object object, int index, int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)
                    switch((String)object)
                    {
                        case "Blank (LBP1)":
                            for(PLevelSettings ls : LevelSettingsUtils.getBlank1Preset())
                                presets.add(LevelSettingsUtils.translate(ls));

                            for(bog.lbpas.view3d.core.types.Thing th : view.things)
                                if(th.selected && th.thing.hasPart(Part.LEVEL_SETTINGS))
                                    ((PLevelSettings)th.thing.getPart(Part.LEVEL_SETTINGS)).backdropAmbience = LevelSettingsUtils.getBlank1Preset().get(0).backdropAmbience;
                            break;
                        case "Tutorials (LBP1)":
                            for(PLevelSettings ls : LevelSettingsUtils.getTutorial1Preset())
                                presets.add(LevelSettingsUtils.translate(ls));

                            for(bog.lbpas.view3d.core.types.Thing th : view.things)
                                if(th.selected && th.thing.hasPart(Part.LEVEL_SETTINGS))
                                    ((PLevelSettings)th.thing.getPart(Part.LEVEL_SETTINGS)).backdropAmbience = LevelSettingsUtils.getTutorial1Preset().get(0).backdropAmbience;
                            break;
                        case "Gardens":
                            for(PLevelSettings ls : LevelSettingsUtils.getGardensPreset())
                                presets.add(LevelSettingsUtils.translate(ls));

                            for(bog.lbpas.view3d.core.types.Thing th : view.things)
                                if(th.selected && th.thing.hasPart(Part.LEVEL_SETTINGS))
                                    ((PLevelSettings)th.thing.getPart(Part.LEVEL_SETTINGS)).backdropAmbience = LevelSettingsUtils.getGardensPreset().get(0).backdropAmbience;
                            break;
                        case "Savannah":
                            for(PLevelSettings ls : LevelSettingsUtils.getSavannahPreset())
                                presets.add(LevelSettingsUtils.translate(ls));

                            for(bog.lbpas.view3d.core.types.Thing th : view.things)
                                if(th.selected && th.thing.hasPart(Part.LEVEL_SETTINGS))
                                    ((PLevelSettings)th.thing.getPart(Part.LEVEL_SETTINGS)).backdropAmbience = LevelSettingsUtils.getSavannahPreset().get(0).backdropAmbience;
                            break;
                        case "Wedding":
                            for(PLevelSettings ls : LevelSettingsUtils.getWeddingPreset())
                                presets.add(LevelSettingsUtils.translate(ls));

                            for(bog.lbpas.view3d.core.types.Thing th : view.things)
                                if(th.selected && th.thing.hasPart(Part.LEVEL_SETTINGS))
                                    ((PLevelSettings)th.thing.getPart(Part.LEVEL_SETTINGS)).backdropAmbience = LevelSettingsUtils.getWeddingPreset().get(0).backdropAmbience;
                            break;
                        case "Canyons":
                            for(PLevelSettings ls : LevelSettingsUtils.getCanyonsPreset())
                                presets.add(LevelSettingsUtils.translate(ls));

                            for(bog.lbpas.view3d.core.types.Thing th : view.things)
                                if(th.selected && th.thing.hasPart(Part.LEVEL_SETTINGS))
                                    ((PLevelSettings)th.thing.getPart(Part.LEVEL_SETTINGS)).backdropAmbience = LevelSettingsUtils.getCanyonsPreset().get(0).backdropAmbience;
                            break;
                        case "Metropolis":
                            for(PLevelSettings ls : LevelSettingsUtils.getMetroPreset())
                                presets.add(LevelSettingsUtils.translate(ls));

                            for(bog.lbpas.view3d.core.types.Thing th : view.things)
                                if(th.selected && th.thing.hasPart(Part.LEVEL_SETTINGS))
                                    ((PLevelSettings)th.thing.getPart(Part.LEVEL_SETTINGS)).backdropAmbience = LevelSettingsUtils.getMetroPreset().get(0).backdropAmbience;
                            break;
                        case "Islands":
                            for(PLevelSettings ls : LevelSettingsUtils.getIslandsPreset())
                                presets.add(LevelSettingsUtils.translate(ls));

                            for(bog.lbpas.view3d.core.types.Thing th : view.things)
                                if(th.selected && th.thing.hasPart(Part.LEVEL_SETTINGS))
                                    ((PLevelSettings)th.thing.getPart(Part.LEVEL_SETTINGS)).backdropAmbience = LevelSettingsUtils.getIslandsPreset().get(0).backdropAmbience;
                            break;
                        case "Temples":
                            for(PLevelSettings ls : LevelSettingsUtils.getTemplesPreset())
                                presets.add(LevelSettingsUtils.translate(ls));

                            for(bog.lbpas.view3d.core.types.Thing th : view.things)
                                if(th.selected && th.thing.hasPart(Part.LEVEL_SETTINGS))
                                    ((PLevelSettings)th.thing.getPart(Part.LEVEL_SETTINGS)).backdropAmbience = LevelSettingsUtils.getTemplesPreset().get(0).backdropAmbience;
                            break;
                        case "Wilderness":
                            for(PLevelSettings ls : LevelSettingsUtils.getWildernessPreset())
                                presets.add(LevelSettingsUtils.translate(ls));

                            for(bog.lbpas.view3d.core.types.Thing th : view.things)
                                if(th.selected && th.thing.hasPart(Part.LEVEL_SETTINGS))
                                    ((PLevelSettings)th.thing.getPart(Part.LEVEL_SETTINGS)).backdropAmbience = LevelSettingsUtils.getWildernessPreset().get(0).backdropAmbience;
                            break;
                        case "Blank (LBP2)":
                            for(PLevelSettings ls : LevelSettingsUtils.getBlank2Preset())
                                presets.add(LevelSettingsUtils.translate(ls));

                            for(bog.lbpas.view3d.core.types.Thing th : view.things)
                                if(th.selected && th.thing.hasPart(Part.LEVEL_SETTINGS))
                                    ((PLevelSettings)th.thing.getPart(Part.LEVEL_SETTINGS)).backdropAmbience = LevelSettingsUtils.getBlank2Preset().get(0).backdropAmbience;
                            break;
                        case "Tutorials (LBP2)":
                            for(PLevelSettings ls : LevelSettingsUtils.getTutorial2Preset())
                                presets.add(LevelSettingsUtils.translate(ls));

                            for(bog.lbpas.view3d.core.types.Thing th : view.things)
                                if(th.selected && th.thing.hasPart(Part.LEVEL_SETTINGS))
                                    ((PLevelSettings)th.thing.getPart(Part.LEVEL_SETTINGS)).backdropAmbience = LevelSettingsUtils.getTutorial2Preset().get(0).backdropAmbience;
                            break;
                        case "Da Vinci's Hideout":
                            for(PLevelSettings ls : LevelSettingsUtils.getDaVinciPreset())
                                presets.add(LevelSettingsUtils.translate(ls));

                            for(bog.lbpas.view3d.core.types.Thing th : view.things)
                                if(th.selected && th.thing.hasPart(Part.LEVEL_SETTINGS))
                                    ((PLevelSettings)th.thing.getPart(Part.LEVEL_SETTINGS)).backdropAmbience = LevelSettingsUtils.getDaVinciPreset().get(0).backdropAmbience;
                            break;
                        case "Victoria's Laboratory":
                            for(PLevelSettings ls : LevelSettingsUtils.getVictoriaPreset())
                                presets.add(LevelSettingsUtils.translate(ls));

                            for(bog.lbpas.view3d.core.types.Thing th : view.things)
                                if(th.selected && th.thing.hasPart(Part.LEVEL_SETTINGS))
                                    ((PLevelSettings)th.thing.getPart(Part.LEVEL_SETTINGS)).backdropAmbience = LevelSettingsUtils.getVictoriaPreset().get(0).backdropAmbience;
                            break;
                        case "Factory of a Better Tomorrow":
                            for(PLevelSettings ls : LevelSettingsUtils.getFactoryPreset())
                                presets.add(LevelSettingsUtils.translate(ls));

                            for(bog.lbpas.view3d.core.types.Thing th : view.things)
                                if(th.selected && th.thing.hasPart(Part.LEVEL_SETTINGS))
                                    ((PLevelSettings)th.thing.getPart(Part.LEVEL_SETTINGS)).backdropAmbience = LevelSettingsUtils.getFactoryPreset().get(0).backdropAmbience;
                            break;
                        case "Avalonia":
                            for(PLevelSettings ls : LevelSettingsUtils.getAvaloniaPreset())
                                presets.add(LevelSettingsUtils.translate(ls));

                            for(bog.lbpas.view3d.core.types.Thing th : view.things)
                                if(th.selected && th.thing.hasPart(Part.LEVEL_SETTINGS))
                                    ((PLevelSettings)th.thing.getPart(Part.LEVEL_SETTINGS)).backdropAmbience = LevelSettingsUtils.getAvaloniaPreset().get(0).backdropAmbience;
                            break;
                        case "Eve's Asylum":
                            for(PLevelSettings ls : LevelSettingsUtils.getEvePreset())
                                presets.add(LevelSettingsUtils.translate(ls));

                                for(bog.lbpas.view3d.core.types.Thing th : view.things)
                                    if(th.selected && th.thing.hasPart(Part.LEVEL_SETTINGS))
                                        ((PLevelSettings)th.thing.getPart(Part.LEVEL_SETTINGS)).backdropAmbience = LevelSettingsUtils.getEvePreset().get(0).backdropAmbience;
                            break;
                        case "Cosmos":
                            for(PLevelSettings ls : LevelSettingsUtils.getCosmosPreset())
                                presets.add(LevelSettingsUtils.translate(ls));

                            for(bog.lbpas.view3d.core.types.Thing th : view.things)
                                if(th.selected && th.thing.hasPart(Part.LEVEL_SETTINGS))
                                    ((PLevelSettings)th.thing.getPart(Part.LEVEL_SETTINGS)).backdropAmbience = LevelSettingsUtils.getCosmosPreset().get(0).backdropAmbience;
                            break;
                    }
            }

            @Override
            public void draw(MouseInput mouseInput, boolean overElement) {
                hovering = -1;
                super.draw(mouseInput, overElement);
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

            @Override
            public int buttonHeight() {
                return getFontHeight(fontSize) + 4;
            }
        };
        importTemplatesCombo.addList("templateList", templateList, 240);

        importPanel.elements.add(new Panel.PanelElement(importTemplatesCombo, 0.4975f));

        partComboBox.addString("presetsStr", "Presets:");

        Presets = partComboBox.addList("Presets", new ButtonList(presets, 10, view.renderer, view.loader, view.window) {
            int hovering = -1;
            @Override
            public void clickedButton(Object object, int index, int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)
                {
                    LevelSettings levelSettings = (LevelSettings) object;
                    selectedPreset = levelSettings;
                    view.fogColor = new Vector4f(levelSettings.fogColor);
                    view.fogNear = levelSettings.fogNear;
                    view.fogFar = levelSettings.fogFar;
                    view.rimColor = new Vector4f(levelSettings.rimColor);
                    view.sunPos = new Vector3f(levelSettings.sunPosition).mul(levelSettings.sunPositionScale);
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
                return selectedPreset == object;
            }

            @Override
            public String buttonText(Object object, int index) {
                if(object == null)
                    return "(0,0,0,255)";
                Vector4f fogcolor = ((LevelSettings)object).fogColor;
                return "(" + Math.round(fogcolor.x * 255f) + "," + Math.round(fogcolor.y * 255f) + "," + Math.round(fogcolor.z * 255f) + "," + Math.round(fogcolor.w * 255f) + ")";
            }

            @Override
            public Color buttonColor(Object object, int index) {
                if(object == null)
                    return super.buttonColor(object, index);
                Vector4f fogcolor = ((LevelSettings)object).fogColor;
                Color fogcolor1 = new Color(Math.clamp(0, 1, fogcolor.x),
                        Math.clamp(0, 1, fogcolor.y),
                        Math.clamp(0, 1, fogcolor.z),
                        0.5f);
                return fogcolor1;
            }

            @Override
            public Color buttonColorHighlighted(Object object, int index) {
                if(object == null)
                    return super.buttonColorHighlighted(object, index);
                Vector4f fogcolor = ((LevelSettings)object).fogColor;
                Color fogcolor1 = new Color(Math.clamp(0.1f, 1, fogcolor.x),
                        Math.clamp(0.1f, 1, fogcolor.y),
                        Math.clamp(0.1f, 1, fogcolor.z),
                        0.45f);
                return fogcolor1.brighter();
            }

            @Override
            public Color buttonColorSelected(Object object, int index) {
                if(object == null)
                    return super.buttonColorSelected(object, index);
                Vector4f fogcolor = ((LevelSettings)object).fogColor;
                Color fogcolor1 = new Color(Math.clamp(0.1f, 1, fogcolor.x),
                        Math.clamp(0.1f, 1, fogcolor.y),
                        Math.clamp(0.1f, 1, fogcolor.z),
                        0.75f);
                return fogcolor1.brighter();
            }

            @Override
            public Color buttonColor2(Object object, int index) {
                if(object == null)
                    return super.buttonColor2(object, index);
                Vector4f fogcolor = ((LevelSettings)object).fogColor;
                Color fogcolor1 = new Color(Math.clamp(0, 1, fogcolor.x),
                        Math.clamp(0, 1, fogcolor.y),
                        Math.clamp(0, 1, fogcolor.z),
                        0.5f);
                return fogcolor1.darker().darker();
            }

            @Override
            public Color buttonColorHighlighted2(Object object, int index) {
                if(object == null)
                    return super.buttonColorHighlighted2(object, index);
                Vector4f fogcolor = ((LevelSettings)object).fogColor;
                Color fogcolor1 = new Color(Math.clamp(0.1f, 1, fogcolor.x),
                        Math.clamp(0.1f, 1, fogcolor.y),
                        Math.clamp(0.1f, 1, fogcolor.z),
                        0.45f);
                return fogcolor1.darker();
            }

            @Override
            public Color buttonColorSelected2(Object object, int index) {
                if(object == null)
                    return super.buttonColorSelected2(object, index);
                Vector4f fogcolor = ((LevelSettings)object).fogColor;
                Color fogcolor1 = new Color(Math.clamp(0.1f, 1, fogcolor.x),
                        Math.clamp(0.1f, 1, fogcolor.y),
                        Math.clamp(0.1f, 1, fogcolor.z),
                        0.75f);
                return fogcolor1;
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
                return getFontHeight(10) + 4;
            }
        }, 150).deletable().draggable();

        Panel presetsPanel = partComboBox.addPanel("presetsPanel");
        presetsPanel.elements.add(new Panel.PanelElement(new Button("add", "Add", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window) {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)
                    presets.add(new LevelSettings());
            }
        }, 0.4975f));
        presetsPanel.elements.add(new Panel.PanelElement(null, 0.005f));
        PresetEdit = new ComboBox("PresetEdit", "Edit", new Vector2f(), new Vector2f(), 10, 250, view.renderer, view.loader, view.window);

        ElementList presetEditList = PresetEdit.addElementList("presetEditList", 350);

        {
            Panel sunScalePanel = presetEditList.addPanel("sunScalePanel");
            sunScalePanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("sunScaleStr", "Sun Pos Scale:", 10, view.renderer), 0.7f));
            sunScale = new Textbox("sunScale", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window).noLetters().noOthers();
            sunScalePanel.elements.add(new Panel.PanelElement(sunScale, 0.3f));

            Panel sunXPanel = presetEditList.addPanel("sunXPanel");
            sunXPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("sunXStr", "Sun X:", 10, view.renderer), 0.7f));
            sunX = new Textbox("sunX", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window).noLetters().noOthers();
            sunXPanel.elements.add(new Panel.PanelElement(sunX, 0.3f));

            Panel sunYPanel = presetEditList.addPanel("sunYPanel");
            sunYPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("sunYStr", "Sun Y:", 10, view.renderer), 0.7f));
            sunY = new Textbox("sunY", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window).noLetters().noOthers();
            sunYPanel.elements.add(new Panel.PanelElement(sunY, 0.3f));

            Panel sunZPanel = presetEditList.addPanel("sunZPanel");
            sunZPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("sunZStr", "Sun Z:", 10, view.renderer), 0.7f));
            sunZ = new Textbox("sunZ", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window).noLetters().noOthers();
            sunZPanel.elements.add(new Panel.PanelElement(sunZ, 0.3f));

            presetEditList.addString("sunColorStr", "Sun Color:");
            Panel sunColorPanel = presetEditList.addPanel("sunColorPanel");
            sunColorR = new Textbox("sunColorR", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window).noLetters().noOthers();
            sunColorG = new Textbox("sunColorG", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window).noLetters().noOthers();
            sunColorB = new Textbox("sunColorB", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window).noLetters().noOthers();
            sunColorA = new Textbox("sunColorA", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window).noLetters().noOthers();
            sunColorPanel.elements.add(new Panel.PanelElement(sunColorR, 0.2425f));
            sunColorPanel.elements.add(new Panel.PanelElement(null, 0.01f));
            sunColorPanel.elements.add(new Panel.PanelElement(sunColorG, 0.2425f));
            sunColorPanel.elements.add(new Panel.PanelElement(null, 0.01f));
            sunColorPanel.elements.add(new Panel.PanelElement(sunColorB, 0.2425f));
            sunColorPanel.elements.add(new Panel.PanelElement(null, 0.01f));
            sunColorPanel.elements.add(new Panel.PanelElement(sunColorA, 0.2425f));

            Panel sunMultiplierPanel = presetEditList.addPanel("sunMultiplierPanel");
            sunMultiplierPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("sunMultiplierStr", "Sun Multiplier:", 10, view.renderer), 0.7f));
            sunMultiplier = new Textbox("sunMultiplier", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window).noLetters().noOthers();
            sunMultiplierPanel.elements.add(new Panel.PanelElement(sunMultiplier, 0.3f));

            presetEditList.addString("ambientColorStr", "Ambient Color:");
            Panel ambientColorPanel = presetEditList.addPanel("ambientColorPanel");
            ambientColorR = new Textbox("ambientColorR", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window).noLetters().noOthers();
            ambientColorG = new Textbox("ambientColorG", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window).noLetters().noOthers();
            ambientColorB = new Textbox("ambientColorB", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window).noLetters().noOthers();
            ambientColorA = new Textbox("ambientColorA", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window).noLetters().noOthers();
            ambientColorPanel.elements.add(new Panel.PanelElement(ambientColorR, 0.2425f));
            ambientColorPanel.elements.add(new Panel.PanelElement(null, 0.01f));
            ambientColorPanel.elements.add(new Panel.PanelElement(ambientColorG, 0.2425f));
            ambientColorPanel.elements.add(new Panel.PanelElement(null, 0.01f));
            ambientColorPanel.elements.add(new Panel.PanelElement(ambientColorB, 0.2425f));
            ambientColorPanel.elements.add(new Panel.PanelElement(null, 0.01f));
            ambientColorPanel.elements.add(new Panel.PanelElement(ambientColorA, 0.2425f));

            Panel exposurePanel = presetEditList.addPanel("exposurePanel");
            exposurePanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("exposureStr", "Exposure:", 10, view.renderer), 0.7f));
            exposure = new Textbox("exposure", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window).noLetters().noOthers();
            exposurePanel.elements.add(new Panel.PanelElement(exposure, 0.3f));

            presetEditList.addString("fogColorStr", "Fog Color:");
            Panel fogColorPanel = presetEditList.addPanel("fogColorPanel");
            fogColorR = new Textbox("fogColorR", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window).noLetters().noOthers();
            fogColorG = new Textbox("fogColorG", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window).noLetters().noOthers();
            fogColorB = new Textbox("fogColorB", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window).noLetters().noOthers();
            fogColorA = new Textbox("fogColorA", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window).noLetters().noOthers();
            fogColorPanel.elements.add(new Panel.PanelElement(fogColorR, 0.2425f));
            fogColorPanel.elements.add(new Panel.PanelElement(null, 0.01f));
            fogColorPanel.elements.add(new Panel.PanelElement(fogColorG, 0.2425f));
            fogColorPanel.elements.add(new Panel.PanelElement(null, 0.01f));
            fogColorPanel.elements.add(new Panel.PanelElement(fogColorB, 0.2425f));
            fogColorPanel.elements.add(new Panel.PanelElement(null, 0.01f));
            fogColorPanel.elements.add(new Panel.PanelElement(fogColorA, 0.2425f));

            Panel fogNearPanel = presetEditList.addPanel("fogNearPanel");
            fogNearPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("fogNearStr", "Fog Near:", 10, view.renderer), 0.7f));
            fogNear = new Textbox("fogNear", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window).noLetters().noOthers();
            fogNearPanel.elements.add(new Panel.PanelElement(fogNear, 0.3f));

            Panel fogFarPanel = presetEditList.addPanel("fogFarPanel");
            fogFarPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("fogFarStr", "Fog Far:", 10, view.renderer), 0.7f));
            fogFar = new Textbox("fogFar", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window).noLetters().noOthers();
            fogFarPanel.elements.add(new Panel.PanelElement(fogFar, 0.3f));

            presetEditList.addString("rimColorStr", "Rim Color:");
            Panel rimColorPanel = presetEditList.addPanel("rimColorPanel");
            rimColorR = new Textbox("rimColorR", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window).noLetters().noOthers();
            rimColorG = new Textbox("rimColorG", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window).noLetters().noOthers();
            rimColorB = new Textbox("rimColorB", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window).noLetters().noOthers();
            rimColorA = new Textbox("rimColorA", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window).noLetters().noOthers();
            rimColorPanel.elements.add(new Panel.PanelElement(rimColorR, 0.2425f));
            rimColorPanel.elements.add(new Panel.PanelElement(null, 0.01f));
            rimColorPanel.elements.add(new Panel.PanelElement(rimColorG, 0.2425f));
            rimColorPanel.elements.add(new Panel.PanelElement(null, 0.01f));
            rimColorPanel.elements.add(new Panel.PanelElement(rimColorB, 0.2425f));
            rimColorPanel.elements.add(new Panel.PanelElement(null, 0.01f));
            rimColorPanel.elements.add(new Panel.PanelElement(rimColorA, 0.2425f));

            presetEditList.addString("rimColor2Str", "Rim Color 2:");
            Panel rimColor2Panel = presetEditList.addPanel("rimColor2Panel");
            rimColor2R = new Textbox("rimColor2R", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window).noLetters().noOthers();
            rimColor2G = new Textbox("rimColor2G", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window).noLetters().noOthers();
            rimColor2B = new Textbox("rimColor2B", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window).noLetters().noOthers();
            rimColor2A = new Textbox("rimColor2A", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window).noLetters().noOthers();
            rimColor2Panel.elements.add(new Panel.PanelElement(rimColor2R, 0.2425f));
            rimColor2Panel.elements.add(new Panel.PanelElement(null, 0.01f));
            rimColor2Panel.elements.add(new Panel.PanelElement(rimColor2G, 0.2425f));
            rimColor2Panel.elements.add(new Panel.PanelElement(null, 0.01f));
            rimColor2Panel.elements.add(new Panel.PanelElement(rimColor2B, 0.2425f));
            rimColor2Panel.elements.add(new Panel.PanelElement(null, 0.01f));
            rimColor2Panel.elements.add(new Panel.PanelElement(rimColor2A, 0.2425f));

            Panel bakedShadowAmountPanel = presetEditList.addPanel("bakedShadowAmountPanel");
            bakedShadowAmountPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("bakedShadowAmountStr", "Shadow Amount:", 10, view.renderer), 0.7f));
            bakedShadowAmount = new Textbox("bakedShadowAmount", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window).noLetters().noOthers();
            bakedShadowAmountPanel.elements.add(new Panel.PanelElement(bakedShadowAmount, 0.3f));

            Panel bakedShadowBlurPanel = presetEditList.addPanel("bakedShadowBlurPanel");
            bakedShadowBlurPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("bakedShadowBlurStr", "Shadow Blur:", 10, view.renderer), 0.7f));
            bakedShadowBlur = new Textbox("bakedShadowBlur", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window).noLetters().noOthers();
            bakedShadowBlurPanel.elements.add(new Panel.PanelElement(bakedShadowBlur, 0.3f));

            Panel bakedAOBiasPanel = presetEditList.addPanel("bakedAOBiasPanel");
            bakedAOBiasPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("bakedAOBiasStr", "AO Bias:", 10, view.renderer), 0.7f));
            bakedAOBias = new Textbox("bakedAOBias", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window).noLetters().noOthers();
            bakedAOBiasPanel.elements.add(new Panel.PanelElement(bakedAOBias, 0.3f));

            Panel bakedAOScalePanel = presetEditList.addPanel("bakedAOScalePanel");
            bakedAOScalePanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("bakedAOScaleStr", "AO Scale:", 10, view.renderer), 0.7f));
            bakedAOScale = new Textbox("bakedAOScale", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window).noLetters().noOthers();
            bakedAOScalePanel.elements.add(new Panel.PanelElement(bakedAOScale, 0.3f));

            Panel dynamicAOAmountPanel = presetEditList.addPanel("dynamicAOAmountPanel");
            dynamicAOAmountPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("dynamicAOAmountStr", "Dynamic AO:", 10, view.renderer), 0.7f));
            dynamicAOAmount = new Textbox("dynamicAOAmount", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window).noLetters().noOthers();
            dynamicAOAmountPanel.elements.add(new Panel.PanelElement(dynamicAOAmount, 0.3f));

            Panel dofNearPanel = presetEditList.addPanel("dofNearPanel");
            dofNearPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("dofNearStr", "DOF Near:", 10, view.renderer), 0.7f));
            dofNear = new Textbox("dofNear", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window).noLetters().noOthers();
            dofNearPanel.elements.add(new Panel.PanelElement(dofNear, 0.3f));

            Panel dofFarPanel = presetEditList.addPanel("dofFarPanel");
            dofFarPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("dofFarStr", "DOF Far:", 10, view.renderer), 0.7f));
            dofFar = new Textbox("dofFar", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window).noLetters().noOthers();
            dofFarPanel.elements.add(new Panel.PanelElement(dofFar, 0.3f));

            Panel zEffectAmountPanel = presetEditList.addPanel("zEffectAmountPanel");
            zEffectAmountPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("zEffectAmountStr", "Z Effect Amount:", 10, view.renderer), 0.7f));
            zEffectAmount = new Textbox("zEffectAmount", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window).noLetters().noOthers();
            zEffectAmountPanel.elements.add(new Panel.PanelElement(zEffectAmount, 0.3f));

            Panel zEffectBrightnessPanel = presetEditList.addPanel("zEffectBrightnessPanel");
            zEffectBrightnessPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("zEffectBrightnessStr", "Z Effect Brightn.:", 10, view.renderer), 0.7f));
            zEffectBrightness = new Textbox("zEffectBrightness", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window).noLetters().noOthers();
            zEffectBrightnessPanel.elements.add(new Panel.PanelElement(zEffectBrightness, 0.3f));

            Panel zEffectContrastPanel = presetEditList.addPanel("zEffectContrastPanel");
            zEffectContrastPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("zEffectContrastStr", "Z Effect Contrast:", 10, view.renderer), 0.7f));
            zEffectContrast = new Textbox("zEffectContrast", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window).noLetters().noOthers();
            zEffectContrastPanel.elements.add(new Panel.PanelElement(zEffectContrast, 0.3f));
        }

        presetsPanel.elements.add(new Panel.PanelElement(PresetEdit, 0.4975f));

        Panel ambiancePanel = partComboBox.addPanel("ambiancePanel");
        ambiancePanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("ambstr", "Ambiance:", 10, view.renderer), 0.4f));
        Ambiance = new Textbox("Ambiance", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window);
        ambiancePanel.elements.add(new Panel.PanelElement(Ambiance, 0.6f));

        nonLinearFog = partComboBox.addCheckbox("nonLinearFog", "Non Linear Fog", new Checkbox()
        {
            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement) {
                super.onClick(mouseInput, pos, button, action, mods, overElement);

                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos) && !overElement)
                    for(bog.lbpas.view3d.core.types.Thing thing : view.things)
                        if(thing.thing.hasPart(part) && thing.selected)
                            ((PLevelSettings)thing.thing.getPart(part)).nonLinearFog = this.isChecked;
            }
        });

//        ComboBox flags = partComboBox.addComboBox("flags", "BG Repeat Flags", 150);
//        flags.addList("flagList", , 200);
        partComboBox.addString(new DropDownTab.StringElement("backgroundRepeatFlags", 10, view.renderer)
        {
            @Override
            public String stringToDraw() {
                int mode = -1;
                for(bog.lbpas.view3d.core.types.Thing thing : view.things)
                    if(thing.thing.hasPart(part) && thing.selected)
                        mode = ((PLevelSettings)thing.thing.getPart(part)).backgroundRepeatFlags;
                return "BG Repeat Flags: 0x" + Integer.toHexString(mode);
            }
        });

        Panel backdropMeshPanel = partComboBox.addPanel("backdropMeshPanel");
        backdropMeshPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("backdropMeshstr", "BG Mesh:", 10, view.renderer), 0.6f));
        backdropMesh = new Textbox("backdropMesh", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window);
        backdropMeshPanel.elements.add(new Panel.PanelElement(backdropMesh, 0.4f));

        Panel backgroundSkyHeightPanel = partComboBox.addPanel("backgroundSkyHeightPanel");
        backgroundSkyHeightPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("backgroundSkyHeightstr", "BG Sky Height:", 10, view.renderer), 0.6f));
        backgroundSkyHeight = new Textbox("backgroundSkyHeight", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window);
        backgroundSkyHeightPanel.elements.add(new Panel.PanelElement(backgroundSkyHeight, 0.4f));
    }

    @Override
    public void addValues(ArrayList<Integer> selected, ArrayList<bog.lbpas.view3d.core.types.Thing> things) {

        String amb = null;

        boolean noPresets = presets == null || presets.isEmpty() || selectedPreset == null;

        if(noPresets)
        {
            sunScale.setTextboxValueString("");
            sunX.setTextboxValueString("");
            sunY.setTextboxValueString("");
            sunZ.setTextboxValueString("");
            sunColorR.setTextboxValueString("");
            sunColorG.setTextboxValueString("");
            sunColorB.setTextboxValueString("");
            sunColorA.setTextboxValueString("");
            sunMultiplier.setTextboxValueString("");
            ambientColorR.setTextboxValueString("");
            ambientColorG.setTextboxValueString("");
            ambientColorB.setTextboxValueString("");
            ambientColorA.setTextboxValueString("");
            exposure.setTextboxValueString("");
            fogColorR.setTextboxValueString("");
            fogColorG.setTextboxValueString("");
            fogColorB.setTextboxValueString("");
            fogColorA.setTextboxValueString("");
            fogNear.setTextboxValueString("");
            fogFar.setTextboxValueString("");
            rimColorR.setTextboxValueString("");
            rimColorG.setTextboxValueString("");
            rimColorB.setTextboxValueString("");
            rimColorA.setTextboxValueString("");
            rimColor2R.setTextboxValueString("");
            rimColor2G.setTextboxValueString("");
            rimColor2B.setTextboxValueString("");
            rimColor2A.setTextboxValueString("");
            bakedShadowAmount.setTextboxValueString("");
            bakedShadowBlur.setTextboxValueString("");
            bakedAOBias.setTextboxValueString("");
            bakedAOScale.setTextboxValueString("");
            dynamicAOAmount.setTextboxValueString("");
            dofNear.setTextboxValueString("");
            dofFar.setTextboxValueString("");
            zEffectAmount.setTextboxValueString("");
            zEffectBrightness.setTextboxValueString("");
            zEffectContrast.setTextboxValueString("");
        }
        else {
            Vector2f sunScale = this.sunScale.setTextboxValueFloat(selectedPreset.sunPositionScale);
            Vector2f sunX = this.sunX.setTextboxValueFloat(selectedPreset.sunPosition.x);
            Vector2f sunY = this.sunY.setTextboxValueFloat(selectedPreset.sunPosition.y);
            Vector2f sunZ = this.sunZ.setTextboxValueFloat(selectedPreset.sunPosition.z);
            Vector2i sunColorR = this.sunColorR.setTextboxValueInt((int)Math.clamp(Math.round(Math.clamp(selectedPreset.sunColor.x, 0, 1) * 255f), 0, 255));
            Vector2i sunColorG = this.sunColorG.setTextboxValueInt((int)Math.clamp(Math.round(Math.clamp(selectedPreset.sunColor.y, 0, 1) * 255f), 0, 255));
            Vector2i sunColorB = this.sunColorB.setTextboxValueInt((int)Math.clamp(Math.round(Math.clamp(selectedPreset.sunColor.z, 0, 1) * 255f), 0, 255));
            Vector2i sunColorA = this.sunColorA.setTextboxValueInt((int)Math.clamp(Math.round(Math.clamp(selectedPreset.sunColor.w, 0, 1) * 255f), 0, 255));
            Vector2f sunMultiplier = this.sunMultiplier.setTextboxValueFloat(selectedPreset.sunMultiplier);
            Vector2i ambientColorR = this.ambientColorR.setTextboxValueInt((int)Math.clamp(Math.round(Math.clamp(selectedPreset.ambientColor.x, 0, 1) * 255f), 0, 255));
            Vector2i ambientColorG = this.ambientColorG.setTextboxValueInt((int)Math.clamp(Math.round(Math.clamp(selectedPreset.ambientColor.y, 0, 1) * 255f), 0, 255));
            Vector2i ambientColorB = this.ambientColorB.setTextboxValueInt((int)Math.clamp(Math.round(Math.clamp(selectedPreset.ambientColor.z, 0, 1) * 255f), 0, 255));
            Vector2i ambientColorA = this.ambientColorA.setTextboxValueInt((int)Math.clamp(Math.round(Math.clamp(selectedPreset.ambientColor.w, 0, 1) * 255f), 0, 255));
            Vector2f exposure = this.exposure.setTextboxValueFloat(selectedPreset.exposure);
            Vector2i fogColorR = this.fogColorR.setTextboxValueInt((int)Math.clamp(Math.round(Math.clamp(selectedPreset.fogColor.x, 0, 1) * 255f), 0, 255));
            Vector2i fogColorG = this.fogColorG.setTextboxValueInt((int)Math.clamp(Math.round(Math.clamp(selectedPreset.fogColor.y, 0, 1) * 255f), 0, 255));
            Vector2i fogColorB = this.fogColorB.setTextboxValueInt((int)Math.clamp(Math.round(Math.clamp(selectedPreset.fogColor.z, 0, 1) * 255f), 0, 255));
            Vector2i fogColorA = this.fogColorA.setTextboxValueInt((int)Math.clamp(Math.round(Math.clamp(selectedPreset.fogColor.w, 0, 1) * 255f), 0, 255));
            Vector2f fogNear = this.fogNear.setTextboxValueFloat(selectedPreset.fogNear);
            Vector2f fogFar = this.fogFar.setTextboxValueFloat(selectedPreset.fogFar);
            Vector2i rimColorR = this.rimColorR.setTextboxValueInt((int)Math.clamp(Math.round(Math.clamp(selectedPreset.rimColor.x, 0, 1) * 255f), 0, 255));
            Vector2i rimColorG = this.rimColorG.setTextboxValueInt((int)Math.clamp(Math.round(Math.clamp(selectedPreset.rimColor.y, 0, 1) * 255f), 0, 255));
            Vector2i rimColorB = this.rimColorB.setTextboxValueInt((int)Math.clamp(Math.round(Math.clamp(selectedPreset.rimColor.z, 0, 1) * 255f), 0, 255));
            Vector2i rimColorA = this.rimColorA.setTextboxValueInt((int)Math.clamp(Math.round(Math.clamp(selectedPreset.rimColor.w, 0, 1) * 255f), 0, 255));
            Vector2i rimColor2R = this.rimColor2R.setTextboxValueInt((int)Math.clamp(Math.round(Math.clamp(selectedPreset.rimColor2.x, 0, 1) * 255f), 0, 255));
            Vector2i rimColor2G = this.rimColor2G.setTextboxValueInt((int)Math.clamp(Math.round(Math.clamp(selectedPreset.rimColor2.y, 0, 1) * 255f), 0, 255));
            Vector2i rimColor2B = this.rimColor2B.setTextboxValueInt((int)Math.clamp(Math.round(Math.clamp(selectedPreset.rimColor2.z, 0, 1) * 255f), 0, 255));
            Vector2i rimColor2A = this.rimColor2A.setTextboxValueInt((int)Math.clamp(Math.round(Math.clamp(selectedPreset.rimColor2.w, 0, 1) * 255f), 0, 255));
            Vector2f bakedShadowAmount = this.bakedShadowAmount.setTextboxValueFloat(selectedPreset.bakedShadowAmount);
            Vector2f bakedShadowBlur = this.bakedShadowBlur.setTextboxValueFloat(selectedPreset.bakedShadowBlur);
            Vector2f bakedAOBias = this.bakedAOBias.setTextboxValueFloat(selectedPreset.bakedAOBias);
            Vector2f bakedAOScale = this.bakedAOScale.setTextboxValueFloat(selectedPreset.bakedAOScale);
            Vector2f dynamicAOAmount = this.dynamicAOAmount.setTextboxValueFloat(selectedPreset.dynamicAOAmount);
            Vector2f dofNear = this.dofNear.setTextboxValueFloat(selectedPreset.dofNear);
            Vector2f dofFar = this.dofFar.setTextboxValueFloat(selectedPreset.dofFar);
            Vector2f zEffectAmount = this.zEffectAmount.setTextboxValueFloat(selectedPreset.zEffectAmount);
            Vector2f zEffectBrightness = this.zEffectBrightness.setTextboxValueFloat(selectedPreset.zEffectBright);
            Vector2f zEffectContrast = this.zEffectContrast.setTextboxValueFloat(selectedPreset.zEffectContrast);

            if (sunScale.y == 1)
            {
                selectedPreset.sunPositionScale = sunScale.x;
                Presets.clickedButton(selectedPreset, 0, 0, 1, 0);
            }
            if (sunX.y == 1)
            {
                selectedPreset.sunPosition.x = sunX.x;
                Presets.clickedButton(selectedPreset, 0, 0, 1, 0);
            }
            if (sunY.y == 1)
            {
                selectedPreset.sunPosition.y = sunY.x;
                Presets.clickedButton(selectedPreset, 0, 0, 1, 0);
            }
            if (sunZ.y == 1)
            {
                selectedPreset.sunPosition.z = sunZ.x;
                Presets.clickedButton(selectedPreset, 0, 0, 1, 0);
            }
            if (sunColorR.y == 1)
                selectedPreset.sunColor.x = Math.clamp(0f, 1f, sunColorR.x/255f);
            if (sunColorG.y == 1)
                selectedPreset.sunColor.y = Math.clamp(0f, 1f, sunColorG.x/255f);
            if (sunColorB.y == 1)
                selectedPreset.sunColor.z = Math.clamp(0f, 1f, sunColorB.x/255f);
            if (sunColorA.y == 1)
                selectedPreset.sunColor.w = Math.clamp(0f, 1f, sunColorA.x/255f);
            if (sunMultiplier.y == 1)
                selectedPreset.sunMultiplier = sunMultiplier.x;
            if (ambientColorR.y == 1)
                selectedPreset.ambientColor.x = Math.clamp(0f, 1f, ambientColorR.x/255f);
            if (ambientColorG.y == 1)
                selectedPreset.ambientColor.y = Math.clamp(0f, 1f, ambientColorG.x/255f);
            if (ambientColorB.y == 1)
                selectedPreset.ambientColor.z = Math.clamp(0f, 1f, ambientColorB.x/255f);
            if (ambientColorA.y == 1)
                selectedPreset.ambientColor.w = Math.clamp(0f, 1f, ambientColorA.x/255f);
            if (exposure.y == 1)
                selectedPreset.exposure = exposure.x;
            if (fogColorR.y == 1)
            {
                selectedPreset.fogColor.x = Math.clamp(0f, 1f, fogColorR.x/255f);
                Presets.clickedButton(selectedPreset, 0, 0, 1, 0);
            }
            if (fogColorG.y == 1)
            {
                selectedPreset.fogColor.y = Math.clamp(0f, 1f, fogColorG.x/255f);
                Presets.clickedButton(selectedPreset, 0, 0, 1, 0);
            }
            if (fogColorB.y == 1)
            {
                selectedPreset.fogColor.z = Math.clamp(0f, 1f, fogColorB.x/255f);
                Presets.clickedButton(selectedPreset, 0, 0, 1, 0);
            }
            if (fogColorA.y == 1)
            {
                selectedPreset.fogColor.w = Math.clamp(0f, 1f, fogColorA.x/255f);
                Presets.clickedButton(selectedPreset, 0, 0, 1, 0);
            }
            if (fogNear.y == 1)
            {
                selectedPreset.fogNear = fogNear.x;
                Presets.clickedButton(selectedPreset, 0, 0, 1, 0);
            }
            if (fogFar.y == 1)
            {
                selectedPreset.fogFar = fogFar.x;
                Presets.clickedButton(selectedPreset, 0, 0, 1, 0);
            }
            if (rimColorR.y == 1)
            {
                selectedPreset.rimColor.x = Math.clamp(0f, 1f, rimColorR.x/255f);
                Presets.clickedButton(selectedPreset, 0, 0, 1, 0);
            }
            if (rimColorG.y == 1)
            {
                selectedPreset.rimColor.y = Math.clamp(0f, 1f, rimColorG.x/255f);
                Presets.clickedButton(selectedPreset, 0, 0, 1, 0);
            }
            if (rimColorB.y == 1)
            {
                selectedPreset.rimColor.z = Math.clamp(0f, 1f, rimColorB.x/255f);
                Presets.clickedButton(selectedPreset, 0, 0, 1, 0);
            }
            if (rimColorA.y == 1)
            {
                selectedPreset.rimColor.w = Math.clamp(0f, 1f, rimColorA.x/255f);
                Presets.clickedButton(selectedPreset, 0, 0, 1, 0);
            }
            if (rimColor2R.y == 1)
            {
                selectedPreset.rimColor2.x = Math.clamp(0f, 1f, rimColor2R.x/255f);
                Presets.clickedButton(selectedPreset, 0, 0, 1, 0);
            }
            if (rimColor2G.y == 1)
            {
                selectedPreset.rimColor2.y = Math.clamp(0f, 1f, rimColor2G.x/255f);
                Presets.clickedButton(selectedPreset, 0, 0, 1, 0);
            }
            if (rimColor2B.y == 1)
            {
                selectedPreset.rimColor2.z = Math.clamp(0f, 1f, rimColor2B.x/255f);
                Presets.clickedButton(selectedPreset, 0, 0, 1, 0);
            }
            if (rimColor2A.y == 1)
            {
                selectedPreset.rimColor2.w = Math.clamp(0f, 1f, rimColor2A.x/255f);
                Presets.clickedButton(selectedPreset, 0, 0, 1, 0);
            }
            if (bakedShadowAmount.y == 1)
                selectedPreset.bakedShadowAmount = bakedShadowAmount.x;
            if (bakedShadowBlur.y == 1)
                selectedPreset.bakedShadowBlur = bakedShadowBlur.x;
            if (bakedAOBias.y == 1)
                selectedPreset.bakedAOBias = bakedAOBias.x;
            if (bakedAOScale.y == 1)
                selectedPreset.bakedAOScale = bakedAOScale.x;
            if (dynamicAOAmount.y == 1)
                selectedPreset.dynamicAOAmount = dynamicAOAmount.x;
            if (dofNear.y == 1)
                selectedPreset.dofNear = dofNear.x;
            if (dofFar.y == 1)
                selectedPreset.dofFar = dofFar.x;
            if (zEffectAmount.y == 1)
                selectedPreset.zEffectAmount = zEffectAmount.x;
            if (zEffectBrightness.y == 1)
                selectedPreset.zEffectBright = zEffectBrightness.x;
            if (zEffectContrast.y == 1)
                selectedPreset.zEffectContrast = zEffectContrast.x;
        }

        int nlFog = -1;
        String bgMesh = null;
        float skyHeight = Float.NEGATIVE_INFINITY;

        for(int s : selected)
            if(things.get(s).thing.hasPart(Part.LEVEL_SETTINGS))
            {
                PLevelSettings levelSettings = ((PLevelSettings)things.get(s).thing.getPart(Part.LEVEL_SETTINGS));
                presets = levelSettings.presets;
                Presets.list = presets;

                if(amb == null)
                    amb = levelSettings.backdropAmbience;
                else if(!amb.equalsIgnoreCase(levelSettings.backdropAmbience))
                    amb = "";

                if(nlFog == -1)
                    nlFog = levelSettings.nonLinearFog ? 1 : 0;
                else if(nlFog != (levelSettings.nonLinearFog ? 1 : 0))
                    nlFog = 0;

                String bgMsh = levelSettings.backdropMesh == null ? "" : levelSettings.backdropMesh.isGUID() ? levelSettings.backdropMesh.getGUID().toString() : levelSettings.backdropMesh.getSHA1().toString();
                if(bgMesh == null)
                    bgMesh = bgMsh;
                else if(!bgMesh.equalsIgnoreCase(bgMsh))
                    bgMesh = "";

                float skHigh = levelSettings.backgroundSkyHeight;
                if(Float.isInfinite(skyHeight))
                    skyHeight = skHigh;
                else if(skyHeight != skHigh)
                    skyHeight = Float.NaN;
            }

        if(presets != null && !presets.contains(selectedPreset) && presets.size() > 0)
            selectedPreset = presets.get(0);

        String ambience = Ambiance.setTextboxValueString(amb);
        nonLinearFog.isChecked = nlFog == 1;
        String bgMsh = backdropMesh.setTextboxValueString(bgMesh);
        Vector2f skHigh = backgroundSkyHeight.setTextboxValueFloat(skyHeight);

        for(int s : selected)
            if(things.get(s).thing.hasPart(Part.LEVEL_SETTINGS))
            {
                PLevelSettings levelSettings = ((PLevelSettings)things.get(s).thing.getPart(Part.LEVEL_SETTINGS));
                presets = levelSettings.presets;
                Presets.list = presets;

                if(ambience != null)
                    levelSettings.backdropAmbience = ambience;

                if(bgMsh != null)
                {
                    levelSettings.backdropMesh = null;
                    try{levelSettings.backdropMesh = new ResourceDescriptor(bgMsh, ResourceType.STATIC_MESH);}catch (Exception e){}
                }

                if(skHigh.y == 1)
                    levelSettings.backgroundSkyHeight = skHigh.x;
            }
    }

    @Override
    public <T extends Serializable> void addPart(ArrayList<bog.lbpas.view3d.core.types.Thing> things, bog.lbpas.view3d.core.types.Thing thing, T p) {

        ((PLevelSettings)p).backdropAmbience = "ambiences/amb_empty_world";

        super.addPart(things, thing, p);
    }
}
