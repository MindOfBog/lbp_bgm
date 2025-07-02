package bog.lbpas.view3d.mainWindow.screens.thingPart.parts;

import bog.lbpas.view3d.core.types.Thing;
import bog.lbpas.view3d.mainWindow.View3D;
import bog.lbpas.view3d.managers.MouseInput;
import bog.lbpas.view3d.renderer.gui.elements.*;
import bog.lbpas.view3d.utils.Utils;
import bog.lbpas.view3d.utils.print;
import com.github.weisj.jsvg.nodes.text.Text;
import cwlib.enums.ResourceType;
import cwlib.enums.SwitchBehavior;
import cwlib.enums.SwitchLogicType;
import cwlib.enums.SwitchType;
import cwlib.io.gson.GsonRevision;
import cwlib.structs.profile.DataLabelValue;
import cwlib.structs.things.components.GlobalThingDescriptor;
import cwlib.structs.things.components.switches.SwitchOutput;
import cwlib.structs.things.components.switches.SwitchSignal;
import cwlib.structs.things.parts.PRef;
import cwlib.structs.things.parts.PSwitch;
import cwlib.types.data.NetworkOnlineID;
import cwlib.types.data.ResourceDescriptor;
import org.joml.*;
import org.joml.Math;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Bog
 */
public abstract class PartSwitch extends iPart {

    public PartSwitch(int tabWidth, float comboWidth, float panelHeight, float closeWidth, float finalGap, Element tab, View3D view) {
        super(cwlib.enums.Part.SWITCH, "PSwitch", "Switch", tabWidth, comboWidth, panelHeight, closeWidth, finalGap, tab, view);
    }

    Textbox name;
    Checkbox inverted;
    Checkbox hideInPlayMode;
    Checkbox hideConnectors;
    Checkbox wiresVisible;
    Checkbox crappyOldLbp1Switch;
    Checkbox isLbp3Switch;
    Checkbox includeRigidConnectors;
    Textbox radius;
    Textbox minRadius;
    Textbox layerRange;
    Textbox angleRange;

    SwitchType type = SwitchType.INVALID;
    SwitchLogicType logicType = SwitchLogicType.AND;
    SwitchBehavior behavior = SwitchBehavior.OFF_ON;
    ComboBox typeCombo;
    ComboBox logicTypeCombo;
    ComboBox switchBehaviorCombo;

    //player sensor
    Checkbox requireAll;
    Checkbox detectUnspawnedPlayers;
    Textbox teamFilter;

    //sticker sensor
    Textbox stickerPlan;
//  todo  Textbox stickerSwitchMode;

    //paint ball / projectile sensor
    Textbox bulletsRequired;
    Textbox bulletsDetected;
    Textbox bulletPlayerNumber;
    Textbox bulletRefreshTime;
    ComboBox bulletType;
    ArrayList<String> bulletTypeList;
    Checkbox resetWhenFull;

    //timer
    Textbox activationHoldTime;
    Textbox activationHoldTimeFrames;
    Textbox timerCount;
    Textbox timerCountFrames;
    Textbox timerAutoCount;

    //randomizer
//  todo  public int randomPhaseOn, randomPhaseTime;
    ComboBox randomBehaviorCombo;
    ArrayList<String> randomBehaviorList;
    ComboBox randomPattern;
    ArrayList<String> randomPatternList;
    Textbox randomOnTimeMin;
    Textbox randomOnTimeMinFrames;
    Textbox randomOnTimeMax;
    Textbox randomOnTimeMaxFrames;
    Textbox randomOffTimeMin;
    Textbox randomOffTimeMinFrames;
    Textbox randomOffTimeMax;
    Textbox randomOffTimeMaxFrames;

    //tag sensor
    ArrayList<String> tagColorsList;
    ComboBox tagColorCombo;
    ArrayList<String> tagModesList;
    ComboBox tagModeCombo;
    Textbox labelName;
    Textbox labelIndex;
    Textbox labelCreator;

    //impact sensor
    Checkbox includeTouching;

    //todo connector
//    public SwitchOutput[] outputs;
//    public Vector4f[] connectorPos;
//    public Vector4f portPosOffset, looseConnectorPos, looseConnectorBaseOffset;
//    public Vector4f customPortOffset, customConnectorOffset;

    //todo other
//    public int behaviorOld;
//    public cwlib.structs.things.Thing referenceThing;
//    public SwitchSignal manualActivation;
//    public float platformVisualFactor;
//    public float oldActivation;
//    public boolean looseConnectorGrabbed;
//    public boolean[] connectorGrabbed;
//    public int updateFrame;
//    public cwlib.structs.things.Thing portThing;
//    public boolean retardedOldJoint;
//    public int userDefinedColour;
//    public byte unspawnedBehavior;
//    public boolean playSwitchAudio;
//    public byte playerMode;
//    public boolean relativeToSequencer;
//    public boolean breakSound;
//    public int colorTimer;
//    public boolean randomNonRepeating;

    //todo vita
//    public byte impactSensorMode;
//    public int switchTouchType;
//    public byte cursorScreenArea;
//    public byte cursorInteractionType;
//    public byte cursorTouchPanels;
//    public byte cursorTouchIndex;
//    public byte flags;
//    public int outputAndOr;
//    public byte includeSameChipTags;
//    public int glowFrontCol, glowBackCol, glowActiveCol;
//    public byte playerFilter;

    @Override
    public void init(View3D view) {

        Panel namePanel = partComboBox.addPanel("namePanel");
        namePanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("nameStr", "Name:", 10, view.renderer), 0.4f));
        name = new Textbox("name", 10, view.renderer, view.loader, view.window);
        namePanel.elements.add(new Panel.PanelElement(name, 0.6f));

        inverted = partComboBox.addCheckbox("inverted", "Inverted", new Checkbox()
        {
            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement, boolean focusedOther) {
                super.onClick(mouseInput, pos, button, action, mods, overElement, focusedOther);

                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos) && !overElement)
                    for(Thing thing : view.things)
                        if(thing.thing.hasPart(part) && thing.selected)
                            ((PSwitch)thing.thing.getPart(part)).inverted = this.isChecked;
            }
        });
        hideInPlayMode = partComboBox.addCheckbox("hideInPlayMode", "Hide in Play Mode", new Checkbox()
        {
            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement, boolean focusedOther) {
                super.onClick(mouseInput, pos, button, action, mods, overElement, focusedOther);

                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos) && !overElement)
                    for(Thing thing : view.things)
                        if(thing.thing.hasPart(part) && thing.selected)
                            ((PSwitch)thing.thing.getPart(part)).hideInPlayMode = this.isChecked;
            }
        });
        hideConnectors = partComboBox.addCheckbox("hideConnectors", "Hide Connectors", new Checkbox()
        {
            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement, boolean focusedOther) {
                super.onClick(mouseInput, pos, button, action, mods, overElement,  focusedOther);

                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos) && !overElement)
                    for(Thing thing : view.things)
                        if(thing.thing.hasPart(part) && thing.selected)
                            ((PSwitch)thing.thing.getPart(part)).hideConnectors = this.isChecked;
            }
        });
        wiresVisible = partComboBox.addCheckbox("wiresVisible", "Wires Visible", new Checkbox()
        {
            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement, boolean focusedOther) {
                super.onClick(mouseInput, pos, button, action, mods, overElement, focusedOther);

                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos) && !overElement)
                    for(Thing thing : view.things)
                        if(thing.thing.hasPart(part) && thing.selected)
                            ((PSwitch)thing.thing.getPart(part)).wiresVisible = this.isChecked;
            }
        });
        crappyOldLbp1Switch = partComboBox.addCheckbox("crappyOldLbp1Switch", "LBP1 Switch", new Checkbox()
        {
            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement, boolean focusedOther) {
                super.onClick(mouseInput, pos, button, action, mods, overElement, focusedOther);

                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos) && !overElement)
                    for(Thing thing : view.things)
                        if(thing.thing.hasPart(part) && thing.selected)
                            ((PSwitch)thing.thing.getPart(part)).crappyOldLbp1Switch = this.isChecked;
            }
        });
        isLbp3Switch = partComboBox.addCheckbox("isLbp3Switch", "LBP3 Switch", new Checkbox()
        {
            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement, boolean focusedOther) {
                super.onClick(mouseInput, pos, button, action, mods, overElement, focusedOther);

                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos) && !overElement)
                    for(Thing thing : view.things)
                        if(thing.thing.hasPart(part) && thing.selected)
                            ((PSwitch)thing.thing.getPart(part)).isLbp3Switch = this.isChecked;
            }
        });
        includeRigidConnectors = partComboBox.addCheckbox("includeRigidConnectors", "Include Connected", new Checkbox()
        {
            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement, boolean focusedOther) {
                super.onClick(mouseInput, pos, button, action, mods, overElement, focusedOther);

                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos) && !overElement)
                    for(Thing thing : view.things)
                        if(thing.thing.hasPart(part) && thing.selected)
                            ((PSwitch)thing.thing.getPart(part)).includeRigidConnectors = this.isChecked;
            }
        });

        Panel radiusPanel = partComboBox.addPanel("radiusPanel");
        radiusPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("radiusStr", "Radius:", 10, view.renderer), 0.6f));
        radius = new Textbox("radius", 10, view.renderer, view.loader, view.window).noLetters().noOthers();
        radiusPanel.elements.add(new Panel.PanelElement(radius, 0.4f));

        Panel minRadiusPanel = partComboBox.addPanel("minRadiusPanel");
        minRadiusPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("minRadiusStr", "Min Radius:", 10, view.renderer), 0.6f));
        minRadius = new Textbox("minRadius", 10, view.renderer, view.loader, view.window).noLetters().noOthers();
        minRadiusPanel.elements.add(new Panel.PanelElement(minRadius, 0.4f));

        Panel layerRangePanel = partComboBox.addPanel("layerRangePanel");
        layerRangePanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("layerRangeStr", "Layer Range:", 10, view.renderer), 0.6f));
        layerRange = new Textbox("layerRange", 10, view.renderer, view.loader, view.window).noLetters().noOthers();
        layerRangePanel.elements.add(new Panel.PanelElement(layerRange, 0.4f));

        Panel angleRangePanel = partComboBox.addPanel("angleRangePanel");
        angleRangePanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("angleRangeStr", "Angle Range:", 10, view.renderer), 0.6f));
        angleRange = new Textbox("angleRange", 10, view.renderer, view.loader, view.window).noLetters().noOthers();
        angleRangePanel.elements.add(new Panel.PanelElement(angleRange, 0.4f));

        ComboBox switchCombo = partComboBox.addComboBox("switchCombo", "Switch", 200);

        Panel typePanel = switchCombo.addPanel("typePanel");
        typePanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("typeStr", "Type:", 10, view.renderer), 0.5f));
        typeCombo = new ComboBox("typeCombo", "Invalid", 10, 200, view.renderer, view.loader, view.window);
        Panel searchTypePanel = typeCombo.addPanel("searchTypePanel");
        searchTypePanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("searchTypeStr", "Search:", 10, view.renderer), 0.4f));
        Textbox searchType = new Textbox("searchType", 10, view.renderer, view.loader, view.window);
        searchTypePanel.elements.add(new Panel.PanelElement(searchType, 0.6f));
        ButtonList typeList = new ButtonList(List.of(SwitchType.values()), 10, view.renderer, view.loader, view.window) {
            @Override
            public void clickedButton(Object object, int index, int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)
                {
                    type = (SwitchType) object;
                    typeCombo.tabTitle = buttonText(object, index);

                    for(Thing thing : view.things)
                        if(thing.thing.hasPart(part) && thing.selected)
                            ((PSwitch)thing.thing.getPart(part)).type = (SwitchType) object;
                }
            }

            @Override
            public void hoveringButton(Object object, int index) {
                highlighted = index;
            }

            @Override
            public boolean isHighlighted(Object object, int index) {
                return index == highlighted;
            }

            @Override
            public boolean isSelected(Object object, int index) {
                return type == object;
            }

            @Override
            public String buttonText(Object object, int index) {
                SwitchType type = (SwitchType) object;
                String name = type.toString();
                name = name.substring(0, 1) + name.substring(1).toLowerCase();
                name = name.replaceAll("_", " ");
                return name;
            }

            @Override
            public boolean searchFilter(Object object, int index) {
                return buttonText(object, index).toLowerCase().contains(searchType.getText().toLowerCase());
            }

            int highlighted = -1;

            @Override
            public void draw(MouseInput mouseInput, boolean overElement) {
                highlighted = -1;
                super.draw(mouseInput, overElement);
            }

            @Override
            public int buttonHeight() {
                return super.buttonHeight() + 4;
            }
        };
        typeCombo.addList("typeList", typeList, 200);
        typePanel.elements.add(new Panel.PanelElement(typeCombo, 0.5f));

        Panel logicTypePanel = switchCombo.addPanel("logicTypePanel");
        logicTypePanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("logicTypeStr", "Logic Type:", 10, view.renderer), 0.5f));
        logicTypeCombo = new ComboBox("logicTypeCombo", "And", 10, 200, view.renderer, view.loader, view.window);
        ButtonList logicTypeList = new ButtonList(List.of(SwitchLogicType.values()), 10, view.renderer, view.loader, view.window) {
            @Override
            public void clickedButton(Object object, int index, int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)
                {
                    logicType = (SwitchLogicType) object;
                    logicTypeCombo.tabTitle = buttonText(object, index);

                    for(Thing thing : view.things)
                        if(thing.thing.hasPart(part) && thing.selected)
                            ((PSwitch)thing.thing.getPart(part)).logicType = (SwitchLogicType) object;
                }
            }

            @Override
            public void hoveringButton(Object object, int index) {
                highlighted = index;
            }

            @Override
            public boolean isHighlighted(Object object, int index) {
                return index == highlighted;
            }

            @Override
            public boolean isSelected(Object object, int index) {
                return logicType == object;
            }

            @Override
            public String buttonText(Object object, int index) {
                SwitchLogicType type = (SwitchLogicType) object;
                String name = type.toString();
                name = name.substring(0, 1) + name.substring(1).toLowerCase();
                name = name.replaceAll("_", " ");
                return name;
            }

            @Override
            public boolean searchFilter(Object object, int index) {
                return true;
            }

            int highlighted = -1;

            @Override
            public void draw(MouseInput mouseInput, boolean overElement) {
                highlighted = -1;
                super.draw(mouseInput, overElement);
            }

            @Override
            public int buttonHeight() {
                return super.buttonHeight() + 4;
            }
        };
        logicTypeCombo.addList("logicTypeList", logicTypeList);
        logicTypePanel.elements.add(new Panel.PanelElement(logicTypeCombo, 0.5f));

        Panel switchBehaviorPanel = switchCombo.addPanel("switchBehaviorPanel");
        switchBehaviorPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("switchBehaviorStr", "Behavior:", 10, view.renderer), 0.5f));
        switchBehaviorCombo = new ComboBox("switchBehaviorCombo", "Off on", 10, 200, view.renderer, view.loader, view.window);
        ButtonList switchBehaviorList = new ButtonList(List.of(SwitchBehavior.values()), 10, view.renderer, view.loader, view.window) {
            @Override
            public void clickedButton(Object object, int index, int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)
                {
                    behavior = (SwitchBehavior) object;
                    switchBehaviorCombo.tabTitle = buttonText(object, index);

                    for(Thing thing : view.things)
                        if(thing.thing.hasPart(part) && thing.selected)
                            ((PSwitch)thing.thing.getPart(part)).behavior = (SwitchBehavior) object;
                }
            }

            @Override
            public void hoveringButton(Object object, int index) {
                highlighted = index;
            }

            @Override
            public boolean isHighlighted(Object object, int index) {
                return index == highlighted;
            }

            @Override
            public boolean isSelected(Object object, int index) {
                return behavior == object;
            }

            @Override
            public String buttonText(Object object, int index) {
                SwitchBehavior type = (SwitchBehavior) object;
                String name = type.toString();
                name = name.substring(0, 1) + name.substring(1).toLowerCase();
                name = name.replaceAll("_", " ");
                return name;
            }

            @Override
            public boolean searchFilter(Object object, int index) {
                return true;
            }

            int highlighted = -1;

            @Override
            public void draw(MouseInput mouseInput, boolean overElement) {
                highlighted = -1;
                super.draw(mouseInput, overElement);
            }

            @Override
            public int buttonHeight() {
                return super.buttonHeight() + 4;
            }
        };
        switchBehaviorCombo.addList("switchBehaviorList", switchBehaviorList);
        switchBehaviorPanel.elements.add(new Panel.PanelElement(switchBehaviorCombo, 0.5f));

        ComboBox playerSensorCombo = partComboBox.addComboBox("playerSensorCombo", "Player Sensor", 200);
        requireAll = playerSensorCombo.addCheckbox("requireAll", "Require All", new Checkbox()
        {
            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement, boolean focusedOther) {
                super.onClick(mouseInput, pos, button, action, mods, overElement, focusedOther);

                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos) && !overElement)
                    for(Thing thing : view.things)
                        if(thing.thing.hasPart(part) && thing.selected)
                            ((PSwitch)thing.thing.getPart(part)).requireAll = this.isChecked;
            }
        });
        detectUnspawnedPlayers = playerSensorCombo.addCheckbox("detectUnspawnedPlayers", "Detect Unspawned", new Checkbox()
        {
            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement, boolean focusedOther) {
                super.onClick(mouseInput, pos, button, action, mods, overElement, focusedOther);

                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos) && !overElement)
                    for(Thing thing : view.things)
                        if(thing.thing.hasPart(part) && thing.selected)
                            ((PSwitch)thing.thing.getPart(part)).detectUnspawnedPlayers = this.isChecked;
            }
        });

        Panel teamFilterPanel = playerSensorCombo.addPanel("teamFilterPanel");
        teamFilterPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("teamFilterStr", "Team Filter:", 10, view.renderer), 0.6f));
        teamFilter = new Textbox("teamFilter", 10, view.renderer, view.loader, view.window).noLetters().noOthers();
        teamFilterPanel.elements.add(new Panel.PanelElement(teamFilter, 0.4f));

        ComboBox stickerSensorCombo = partComboBox.addComboBox("stickerSensorCombo", "Sticker Sensor", 200);

        Panel stickerPlanPanel = stickerSensorCombo.addPanel("stickerPlanPanel");
        stickerPlanPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("stickerPlanStr", "Sticker Plan:", 10, view.renderer), 0.6f));
        stickerPlan = new Textbox("stickerPlan", 10, view.renderer, view.loader, view.window);
        stickerPlanPanel.elements.add(new Panel.PanelElement(stickerPlan, 0.4f));

//      todo  stickerSensorCombo.addString(new DropDownTab.StringElement("stickerSwitchMode", 10, view.renderer)
//        {
//            @Override
//            public String stringToDraw() {
//                int mode = -1;
//                for(Thing thing : view.things)
//                    if(thing.thing.hasPart(part) && thing.selected)
//                        mode = ((PSwitch)thing.thing.getPart(part)).stickerSwitchMode;
//                return "Switch mode: " + mode;
//            }
//        });

        ComboBox tagSensorCombo = partComboBox.addComboBox("tagSensorCombo", "Tag Sensor", 200);

        Panel tagColorPanel = tagSensorCombo.addPanel("tagColorPanel");
        tagColorPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("tagColorStr", "Tag Color:", 10, view.renderer), 0.5f));

        tagColorCombo = new ComboBox("tagColorCombo", "Blue", 10, 200, view.renderer, view.loader, view.window);
        tagColorsList = new ArrayList<>(Arrays.asList("Blue", "Purple", "Pink", "Red", "Yellow", "Light Green", "Green", "Cyan"));
        tagColorCombo.addList("tagColorList", new ButtonList(tagColorsList, 10, view.renderer, view.loader, view.window) {
            @Override
            public void clickedButton(Object object, int index, int button, int action, int mods) {
                for(Thing thing : view.things)
                    if(thing.thing.hasPart(part) && thing.selected)
                        ((PSwitch)thing.thing.getPart(part)).colorIndex = index;
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
                int colorIndex = -1;
                for(Thing thing : view.things)
                    if(thing.thing.hasPart(part) && thing.selected)
                    {
                        if(colorIndex == -1)
                            colorIndex = ((PSwitch)thing.thing.getPart(part)).colorIndex;
                        else if(colorIndex != ((PSwitch)thing.thing.getPart(part)).colorIndex)
                            colorIndex = -2;
                    }
                return colorIndex == index;
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
            public int buttonHeight() {
                return super.buttonHeight() + 4;
            }

            @Override
            public void draw(MouseInput mouseInput, boolean overElement) {
                hovering = -1;
                super.draw(mouseInput, overElement);
            }
        });

        tagColorPanel.elements.add(new Panel.PanelElement(tagColorCombo, 0.5f));

        Panel tagModePanel = tagSensorCombo.addPanel("tagModePanel");
        tagModePanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("tagModeStr", "Output:", 10, view.renderer), 0.5f));

        tagModeCombo = new ComboBox("tagModeCombo", "Closeness", 10, 200, view.renderer, view.loader, view.window);
        tagModesList = new ArrayList<>(Arrays.asList("Closeness", "Strength", "Count (LBP3)"));
        tagModeCombo.addList("tagModeList", new ButtonList(tagModesList, 10, view.renderer, view.loader, view.window) {
            @Override
            public void clickedButton(Object object, int index, int button, int action, int mods) {
                for(Thing thing : view.things)
                    if(thing.thing.hasPart(part) && thing.selected)
                        ((PSwitch)thing.thing.getPart(part)).keySensorMode = index;
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
                int keySensorMode = -1;
                for(Thing thing : view.things)
                    if(thing.thing.hasPart(part) && thing.selected)
                    {
                        if(keySensorMode == -1)
                            keySensorMode = ((PSwitch)thing.thing.getPart(part)).keySensorMode;
                        else if(keySensorMode != ((PSwitch)thing.thing.getPart(part)).keySensorMode)
                            keySensorMode = -2;
                    }
                return keySensorMode == index;
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
            public int buttonHeight() {
                return super.buttonHeight() + 4;
            }

            @Override
            public void draw(MouseInput mouseInput, boolean overElement) {
                hovering = -1;
                super.draw(mouseInput, overElement);
            }
        });

        tagModePanel.elements.add(new Panel.PanelElement(tagModeCombo, 0.5f));

        ComboBox tagLabelCombo = tagSensorCombo.addComboBox("tagLabelCombo", "Tag Label", 200);

        Panel tagLabelNamePanel = tagLabelCombo.addPanel("tagLabelNamePanel");
        tagLabelNamePanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("tagLabelNameStr", "Name:", 10, view.renderer), 0.4f));
        labelName = new Textbox("labelName", 10, view.renderer, view.loader, view.window);
        tagLabelNamePanel.elements.add(new Panel.PanelElement(labelName, 0.6f));

        Panel tagLabelIndexPanel = tagLabelCombo.addPanel("tagLabelIndexPanel");
        tagLabelIndexPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("tagLabelIndexStr", "Index:", 10, view.renderer), 0.4f));
        labelIndex = new Textbox("labelIndex", 10, view.renderer, view.loader, view.window).noLetters().noOthers();
        tagLabelIndexPanel.elements.add(new Panel.PanelElement(labelIndex, 0.6f));

        Panel tagLabelCreatorPanel = tagLabelCombo.addPanel("tagLabelCreatorPanel");
        tagLabelCreatorPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("tagLabelCreatorStr", "Creator:", 10, view.renderer), 0.4f));
        labelCreator = new Textbox("labelCreator", 10, view.renderer, view.loader, view.window);
        tagLabelCreatorPanel.elements.add(new Panel.PanelElement(labelCreator, 0.6f));

        ComboBox paintSensorCombo = partComboBox.addComboBox("paintSensorCombo", "Paint/Projectile Sensor", 200);

        Panel bulletsRequiredPanel = paintSensorCombo.addPanel("bulletsRequiredPanel");
        bulletsRequiredPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("bulletsRequiredStr", "Required count:", 10, view.renderer), 0.7f));
        bulletsRequired = new Textbox("bulletsRequired", 10, view.renderer, view.loader, view.window).noLetters().noOthers();
        bulletsRequiredPanel.elements.add(new Panel.PanelElement(bulletsRequired, 0.3f));

        Panel bulletsDetectedPanel = paintSensorCombo.addPanel("bulletsDetectedPanel");
        bulletsDetectedPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("bulletsDetectedStr", "Current count:", 10, view.renderer), 0.7f));
        bulletsDetected = new Textbox("bulletsDetected", 10, view.renderer, view.loader, view.window).noLetters().noOthers();
        bulletsDetectedPanel.elements.add(new Panel.PanelElement(bulletsDetected, 0.3f));

        Panel bulletTypePanel = paintSensorCombo.addPanel("bulletTypePanel");
        bulletTypePanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("bulletTypeStr", "Proj. Type:", 10, view.renderer), 0.5f));
        bulletType = new ComboBox("bulletType", "All", 10, 200, view.renderer, view.loader, view.window);
        bulletTypeList = new ArrayList<>(Arrays.asList("All", "Fire", "Electricity", "Plasma", "Water"));
        bulletType.addList("bulletType", new ButtonList(bulletTypeList, 10, view.renderer, view.loader, view.window) {
            @Override
            public void clickedButton(Object object, int index, int button, int action, int mods) {
                for(Thing thing : view.things)
                    if(thing.thing.hasPart(part) && thing.selected)
                        ((PSwitch)thing.thing.getPart(part)).bulletTypes = (byte) index;
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
                int bulletTypes = -1;
                for(Thing thing : view.things)
                    if(thing.thing.hasPart(part) && thing.selected)
                    {
                        if(bulletTypes == -1)
                            bulletTypes = ((PSwitch)thing.thing.getPart(part)).bulletTypes;
                        else if(bulletTypes != ((PSwitch)thing.thing.getPart(part)).bulletTypes)
                            bulletTypes = -2;
                    }
                return bulletTypes == index;
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
            public int buttonHeight() {
                return super.buttonHeight() + 4;
            }

            @Override
            public void draw(MouseInput mouseInput, boolean overElement) {
                hovering = -1;
                super.draw(mouseInput, overElement);
            }
        });

        bulletTypePanel.elements.add(new Panel.PanelElement(bulletType, 0.5f));

        resetWhenFull = paintSensorCombo.addCheckbox("resetWhenFull", "Reset when Full", new Checkbox()
        {
            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement, boolean focusedOther) {
                super.onClick(mouseInput, pos, button, action, mods, overElement, focusedOther);

                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos) && !overElement)
                    for(Thing thing : view.things)
                        if(thing.thing.hasPart(part) && thing.selected)
                            ((PSwitch)thing.thing.getPart(part)).resetWhenFull = this.isChecked;
            }
        });

        Panel bulletRefreshTimePanel = paintSensorCombo.addPanel("bulletRefreshTimePanel");
        bulletRefreshTimePanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("bulletRefreshTimeStr", "Refresh Time:", 10, view.renderer), 0.7f));
        bulletRefreshTime = new Textbox("bulletRefreshTime", 10, view.renderer, view.loader, view.window).noLetters().noOthers();
        bulletRefreshTimePanel.elements.add(new Panel.PanelElement(bulletRefreshTime, 0.3f));

        Panel bulletPlayerNumberPanel = paintSensorCombo.addPanel("bulletPlayerNumberPanel");
        bulletPlayerNumberPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("bulletPlayerNumberStr", "Shooting Player:", 10, view.renderer), 0.7f));
        bulletPlayerNumber = new Textbox("bulletPlayerNumber", 10, view.renderer, view.loader, view.window).noLetters().noOthers();
        bulletPlayerNumberPanel.elements.add(new Panel.PanelElement(bulletPlayerNumber, 0.3f));

        ComboBox impactSensorCombo = partComboBox.addComboBox("impactSensorCombo", "Impact Sensor", 200);

        includeTouching = impactSensorCombo.addCheckbox("includeTouching", "Include Touching", new Checkbox()
        {
            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement, boolean focusedOther) {
                super.onClick(mouseInput, pos, button, action, mods, overElement, focusedOther);

                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos) && !overElement)
                    for(Thing thing : view.things)
                        if(thing.thing.hasPart(part) && thing.selected)
                            ((PSwitch)thing.thing.getPart(part)).includeTouching = this.isChecked ? 1 : 0;
            }
        });

        ComboBox timerCombo = partComboBox.addComboBox("timerCombo", "Timer", 250);

        Panel activationHoldTimePanel = timerCombo.addPanel("activationHoldTimePanel");
        activationHoldTimePanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("activationHoldTimeStr", "Time:", 10, view.renderer), 0.2f));
        activationHoldTime = new Textbox("activationHoldTime", 10, view.renderer, view.loader, view.window).noLetters().noOthers();
        activationHoldTimePanel.elements.add(new Panel.PanelElement(activationHoldTime, 0.25f));
        activationHoldTimePanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("activationHoldTimeFrameStr", "Frames:", 10, view.renderer), 0.3f));
        activationHoldTimeFrames = new Textbox("activationHoldTimeFrames", 10, view.renderer, view.loader, view.window).noLetters().noOthers();
        activationHoldTimePanel.elements.add(new Panel.PanelElement(activationHoldTimeFrames, 0.25f));

        Panel timerCountPanel = timerCombo.addPanel("timerCountPanel");
        timerCountPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("timerCountStr", "Time:", 10, view.renderer), 0.2f));
        timerCount = new Textbox("timerCount", 10, view.renderer, view.loader, view.window).noLetters().noOthers();
        timerCountPanel.elements.add(new Panel.PanelElement(timerCount, 0.25f));
        timerCountPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("timerCountFrameStr", "Frames:", 10, view.renderer), 0.3f));
        timerCountFrames = new Textbox("timerCountFrames", 10, view.renderer, view.loader, view.window).noLetters().noOthers();
        timerCountPanel.elements.add(new Panel.PanelElement(timerCountFrames, 0.25f));

//    todo    Panel timerAutoCountPanel = timerCombo.addPanel("timerAutoCountPanel");
//        timerAutoCountPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("timerAutoCountStr", "Timer Auto Count?", 10, view.renderer), 0.4f));
//        timerAutoCount = new Textbox("timerAutoCount", 10, view.renderer, view.loader, view.window).noLetters().noOthers();
//        timerAutoCountPanel.elements.add(new Panel.PanelElement(timerAutoCount, 0.6f));

        ComboBox counterCombo = partComboBox.addComboBox("counterCombo", "Counter", 200);

        counterCombo.addPanel(bulletsRequiredPanel);
        counterCombo.addPanel(bulletsDetectedPanel);

        ComboBox randomizerCombo = partComboBox.addComboBox("randomizerCombo", "Randomizer", 250);

//      todo  randomizerCombo.addString(new DropDownTab.StringElement("randomPhaseOn", 10, view.renderer)
//        {
//            @Override
//            public String stringToDraw() {
//                int mode = -1;
//                for(Thing thing : view.things)
//                    if(thing.thing.hasPart(part) && thing.selected)
//                        mode = ((PSwitch)thing.thing.getPart(part)).randomPhaseOn;
//                return "randomPhaseOn " + mode;
//            }
//        });
//        randomizerCombo.addString(new DropDownTab.StringElement("randomPhaseTime", 10, view.renderer)
//        {
//            @Override
//            public String stringToDraw() {
//                int mode = -1;
//                for(Thing thing : view.things)
//                    if(thing.thing.hasPart(part) && thing.selected)
//                        mode = ((PSwitch)thing.thing.getPart(part)).randomPhaseTime;
//                return "randomPhaseTime " + mode;
//            }
//        });

        Panel randomPatternPanel = randomizerCombo.addPanel("randomPatternPanel");
        randomPatternPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("randomPatternStr", "Pattern:", 10, view.renderer), 0.5f));
        randomPattern = new ComboBox("randomPattern", "One At a Time", 10, 250, view.renderer, view.loader, view.window);
        randomPatternList = new ArrayList<>(Arrays.asList("One At a Time", "Add", "Add and Reset When Full", "Toggle"));
        randomPattern.addList("randomPatternList", new ButtonList(randomPatternList, 10, view.renderer, view.loader, view.window) {
            @Override
            public void clickedButton(Object object, int index, int button, int action, int mods) {
                for(Thing thing : view.things)
                    if(thing.thing.hasPart(part) && thing.selected)
                        ((PSwitch)thing.thing.getPart(part)).randomPattern = index;
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
                int randomPattern = -1;
                for(Thing thing : view.things)
                    if(thing.thing.hasPart(part) && thing.selected)
                    {
                        if(randomPattern == -1)
                            randomPattern = ((PSwitch)thing.thing.getPart(part)).randomPattern;
                        else if(randomPattern != ((PSwitch)thing.thing.getPart(part)).randomPattern)
                            randomPattern = -2;
                    }
                return randomPattern == index;
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
            public int buttonHeight() {
                return super.buttonHeight() + 4;
            }

            @Override
            public void draw(MouseInput mouseInput, boolean overElement) {
                hovering = -1;
                super.draw(mouseInput, overElement);
            }
        });

        randomPatternPanel.elements.add(new Panel.PanelElement(randomPattern, 0.5f));

        Panel randomBehaviorPanel = randomizerCombo.addPanel("randomBehaviorPanel");
        randomBehaviorPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("randomBehaviorStr", "Action:", 10, view.renderer), 0.5f));
        randomBehaviorCombo = new ComboBox("randomBehaviorCombo", "One At a Time", 10, 200, view.renderer, view.loader, view.window);
        randomBehaviorList = new ArrayList<>(Arrays.asList("On/Off", "Override Pattern"));
        randomBehaviorCombo.addList("randomBehaviorList", new ButtonList(randomBehaviorList, 10, view.renderer, view.loader, view.window) {
            @Override
            public void clickedButton(Object object, int index, int button, int action, int mods) {
                for(Thing thing : view.things)
                    if(thing.thing.hasPart(part) && thing.selected)
                        ((PSwitch)thing.thing.getPart(part)).randomBehavior = index;
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
                int randomBehavior = -1;
                for(Thing thing : view.things)
                    if(thing.thing.hasPart(part) && thing.selected)
                    {
                        if(randomBehavior == -1)
                            randomBehavior = ((PSwitch)thing.thing.getPart(part)).randomBehavior;
                        else if(randomBehavior != ((PSwitch)thing.thing.getPart(part)).randomBehavior)
                            randomBehavior = -2;
                    }
                return randomBehavior == index;
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
            public int buttonHeight() {
                return super.buttonHeight() + 4;
            }

            @Override
            public void draw(MouseInput mouseInput, boolean overElement) {
                hovering = -1;
                super.draw(mouseInput, overElement);
            }
        });

        randomBehaviorPanel.elements.add(new Panel.PanelElement(randomBehaviorCombo, 0.5f));

        randomizerCombo.addString("onTime", "On Time:");

        Panel onMinPanel = randomizerCombo.addPanel("onMinPanel");
        onMinPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("onMinStr", "Min:", 10, view.renderer), 0.2f));
        randomOnTimeMin = new Textbox("randomOnTimeMin", 10, view.renderer, view.loader, view.window).noLetters().noOthers();
        onMinPanel.elements.add(new Panel.PanelElement(randomOnTimeMin, 0.25f));
        onMinPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("onMinFrameStr", "Frames:", 10, view.renderer), 0.3f));
        randomOnTimeMinFrames = new Textbox("randomOnTimeMinFrames", 10, view.renderer, view.loader, view.window).noLetters().noOthers();
        onMinPanel.elements.add(new Panel.PanelElement(randomOnTimeMinFrames, 0.25f));

        Panel onMaxPanel = randomizerCombo.addPanel("onMaxPanel");
        onMaxPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("onMaxStr", "Max:", 10, view.renderer), 0.2f));
        randomOnTimeMax = new Textbox("randomOnTimeMax", 10, view.renderer, view.loader, view.window).noLetters().noOthers();
        onMaxPanel.elements.add(new Panel.PanelElement(randomOnTimeMax, 0.25f));
        onMaxPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("onMaxFrameStr", "Frames:", 10, view.renderer), 0.3f));
        randomOnTimeMaxFrames = new Textbox("randomOnTimeMaxFrames", 10, view.renderer, view.loader, view.window).noLetters().noOthers();
        onMaxPanel.elements.add(new Panel.PanelElement(randomOnTimeMaxFrames, 0.25f));

        randomizerCombo.addString("offTime", "Off Time:");

        Panel offMinPanel = randomizerCombo.addPanel("offMinPanel");
        offMinPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("offMinStr", "Min:", 10, view.renderer), 0.2f));
        randomOffTimeMin = new Textbox("randomOffTimeMin", 10, view.renderer, view.loader, view.window).noLetters().noOthers();
        offMinPanel.elements.add(new Panel.PanelElement(randomOffTimeMin, 0.25f));
        offMinPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("offMinFrameStr", "Frames:", 10, view.renderer), 0.3f));
        randomOffTimeMinFrames = new Textbox("randomOffTimeMinFrames", 10, view.renderer, view.loader, view.window).noLetters().noOthers();
        offMinPanel.elements.add(new Panel.PanelElement(randomOffTimeMinFrames, 0.25f));

        Panel offMaxPanel = randomizerCombo.addPanel("offMaxPanel");
        offMaxPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("offMaxStr", "Max:", 10, view.renderer), 0.2f));
        randomOffTimeMax = new Textbox("randomOffTimeMax", 10, view.renderer, view.loader, view.window).noLetters().noOthers();
        offMaxPanel.elements.add(new Panel.PanelElement(randomOffTimeMax, 0.25f));
        offMaxPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("offMaxFrameStr", "Frames:", 10, view.renderer), 0.3f));
        randomOffTimeMaxFrames = new Textbox("randomOffTimeMaxFrames", 10, view.renderer, view.loader, view.window).noLetters().noOthers();
        offMaxPanel.elements.add(new Panel.PanelElement(randomOffTimeMaxFrames, 0.25f));

//       todo ComboBox connectorCombo = partComboBox.addComboBox("connectorCombo", "Connector", 200);
//        public SwitchOutput[] outputs;
//        public Vector4f[] connectorPos;
//        public Vector4f portPosOffset, looseConnectorPos, looseConnectorBaseOffset;
//        public Vector4f customPortOffset, customConnectorOffset;
    }

    @Override
    public void addValues(ArrayList<Integer> selected, ArrayList<Thing> things) {

        int colorIndex = -1;
        int keySensorMode = -1;
        int bulletTypes = -1;
        int randomPattern = -1;
        int randomBehavior = -1;
        int type = -2;
        int logicType = -1;
        int behavior = -1;

        int inverted = -1;
        int hideInPlay = -1;
        int hideConnectors = -1;
        int wiresVisible = -1;
        int lbp1Switch = -1;
        int lbp3Switch = -1;
        int includeConnected = -1;

        int requireAll = -1;
        int detectUnspawned = -1;

        int resetWhenFull = -1;

        int includeTouching = -1;

        String name = null;

        String tagName = null;
        String tagCreator = null;

        String stickerPlan = null;

        float radius = Float.NEGATIVE_INFINITY;
        float minRadius = Float.NEGATIVE_INFINITY;
        float layerRange = Float.NEGATIVE_INFINITY;
        float angleRange = Float.NEGATIVE_INFINITY;

        float teamFilter = Float.NEGATIVE_INFINITY;

        float tagIndex = Float.NEGATIVE_INFINITY;

        float requiredProjectile = Float.NEGATIVE_INFINITY;
        float currentProjectile = Float.NEGATIVE_INFINITY;
        float refreshTime = Float.NEGATIVE_INFINITY;
        float shootingPlayer = Float.NEGATIVE_INFINITY;

        float activationHoldTime = Float.NEGATIVE_INFINITY;
        float timerTime = Float.NEGATIVE_INFINITY;
//        float timerAutoCount = Float.NEGATIVE_INFINITY;

        float randomOnMin = Float.NEGATIVE_INFINITY;
        float randomOnMax = Float.NEGATIVE_INFINITY;
        float randomOffMin = Float.NEGATIVE_INFINITY;
        float randomOffMax = Float.NEGATIVE_INFINITY;

        for(int i = 0; i < things.size(); i++)
        {
            Thing thing = things.get(i);

            if(thing == null)
                continue;

            if(thing.thing.hasPart(part) && thing.selected)
            {
                PSwitch pSwitch = ((PSwitch)thing.thing.getPart(part));

                radius = compareNumber(radius, pSwitch.radius / 21f);
                minRadius = compareNumber(minRadius, pSwitch.minRadius / 21f);
                layerRange = compareNumber(layerRange, pSwitch.layerRange);
                angleRange = compareNumber(angleRange, pSwitch.angleRange * 2f);
                teamFilter = compareNumber(teamFilter, pSwitch.teamFilter);
                requiredProjectile = compareNumber(requiredProjectile, pSwitch.bulletsRequired);
                currentProjectile = compareNumber(currentProjectile, pSwitch.bulletsDetected);
                refreshTime = compareNumber(refreshTime, pSwitch.bulletRefreshTime);
                shootingPlayer = compareNumber(shootingPlayer, pSwitch.bulletPlayerNumber);
                activationHoldTime = compareNumber(activationHoldTime, pSwitch.activationHoldTime);
                timerTime = compareNumber(timerTime, pSwitch.timerCount);
//                timerAutoCount = compareNumber(timerAutoCount, pSwitch.timerAutoCount);
                randomOnMin = compareNumber(randomOnMin, pSwitch.randomOnTimeMin);
                randomOnMax = compareNumber(randomOnMax, pSwitch.randomOnTimeMax);
                randomOffMin = compareNumber(randomOffMin, pSwitch.randomOffTimeMin);
                randomOffMax = compareNumber(randomOffMax, pSwitch.randomOffTimeMax);

                name = compareString(name, pSwitch.name);

                if(pSwitch.value != null)
                {
                    tagName = compareString(tagName, pSwitch.value.labelName == null ? "" : pSwitch.value.labelName);
                    tagIndex = compareNumber(tagIndex, pSwitch.value.labelIndex);
                    tagCreator = compareString(tagCreator, pSwitch.value.creatorID != null ? pSwitch.value.creatorID.toString() : "");
                }

                String stckrPlan = pSwitch.stickerPlan == null ? "" : pSwitch.stickerPlan.isGUID() ? pSwitch.stickerPlan.getGUID().toString() : pSwitch.stickerPlan.getSHA1().toString();

                if(colorIndex == -1)
                    colorIndex = pSwitch.colorIndex;
                else if(colorIndex != pSwitch.colorIndex)
                    colorIndex = -2;
                if(keySensorMode == -1)
                    keySensorMode = pSwitch.keySensorMode;
                else if(keySensorMode != pSwitch.keySensorMode)
                    keySensorMode = -2;
                if(bulletTypes == -1)
                    bulletTypes = pSwitch.bulletTypes;
                else if(bulletTypes != pSwitch.bulletTypes)
                    bulletTypes = -2;
                if(randomPattern  == -1)
                    randomPattern  = pSwitch.randomPattern;
                else if(randomPattern  != pSwitch.randomPattern)
                    randomPattern  = -2;
                if(randomBehavior == -1)
                    randomBehavior = pSwitch.randomBehavior;
                else if(randomBehavior != pSwitch.randomBehavior)
                    randomBehavior = -2;
                if(type == -2)
                    type = pSwitch.type.getValue();
                else if(type != pSwitch.type.getValue())
                    type = -3;
                if(logicType == -1)
                    logicType = pSwitch.logicType.getValue();
                else if(logicType != pSwitch.logicType.getValue())
                    logicType = -2;
                if(behavior == -1)
                    behavior = pSwitch.behavior == null ? 0 : pSwitch.behavior.getValue();
                else if(behavior != (pSwitch.behavior == null ? 0 : pSwitch.behavior.getValue()))
                    behavior = -2;

                inverted = compareBoolean(inverted, pSwitch.inverted);
                hideInPlay = compareBoolean(hideInPlay, pSwitch.hideInPlayMode);
                hideConnectors = compareBoolean(hideConnectors, pSwitch.hideConnectors);
                wiresVisible = compareBoolean(wiresVisible, pSwitch.wiresVisible);
                lbp1Switch = compareBoolean(lbp1Switch, pSwitch.crappyOldLbp1Switch);
                lbp3Switch = compareBoolean(lbp3Switch, pSwitch.isLbp3Switch);
                includeConnected = compareBoolean(includeConnected, pSwitch.includeRigidConnectors);
                requireAll = compareBoolean(requireAll, pSwitch.requireAll);
                detectUnspawned = compareBoolean(detectUnspawned, pSwitch.detectUnspawnedPlayers);
                resetWhenFull = compareBoolean(resetWhenFull, pSwitch.resetWhenFull);
                includeTouching = compareBoolean(includeTouching, pSwitch.includeTouching == 1);
                lbp3Switch = compareBoolean(lbp3Switch, pSwitch.isLbp3Switch);
            }
        }

        this.inverted.isChecked = inverted == 1;
        this.hideInPlayMode.isChecked = hideInPlay == 1;
        this.hideConnectors.isChecked = hideConnectors == 1;
        this.wiresVisible.isChecked = wiresVisible == 1;
        this.crappyOldLbp1Switch.isChecked = lbp1Switch == 1;
        this.isLbp3Switch.isChecked = lbp3Switch == 1;
        this.includeRigidConnectors.isChecked = includeConnected == 1;
        this.requireAll.isChecked = requireAll == 1;
        this.detectUnspawnedPlayers.isChecked = detectUnspawned == 1;
        this.resetWhenFull.isChecked = resetWhenFull == 1;
        this.includeTouching.isChecked = includeTouching == 1;

        if(colorIndex >= 0)
            tagColorCombo.tabTitle = tagColorsList.get(colorIndex);
        else
            tagColorCombo.tabTitle = "";
        if(keySensorMode >= 0)
            tagModeCombo.tabTitle = tagModesList.get(keySensorMode);
        else
            tagModeCombo.tabTitle = "";
        if(bulletTypes >= 0)
            bulletType.tabTitle = bulletTypeList.get(bulletTypes);
        else
            bulletType.tabTitle = "";
        if(randomPattern >= 0)
            this.randomPattern.tabTitle = randomPatternList.get(randomPattern);
        else
            this.randomPattern.tabTitle = "";
        if(randomBehavior >= 0)
            randomBehaviorCombo.tabTitle = randomBehaviorList.get(randomBehavior);
        else
            randomBehaviorCombo.tabTitle = "";
        if(type >= -1)
        {
            this.type = SwitchType.fromValue(type);
            String nm = this.type.name();
            nm = nm.substring(0, 1) + nm.substring(1).toLowerCase();
            nm = nm.replaceAll("_", " ");
            typeCombo.tabTitle = nm;
        }
        else
            typeCombo.tabTitle = "";
        if(logicType >= 0)
        {
            this.logicType = SwitchLogicType.fromValue(logicType);
            String nm = this.logicType.name();
            nm = nm.substring(0, 1) + nm.substring(1).toLowerCase();
            nm = nm.replaceAll("_", " ");
            logicTypeCombo.tabTitle = nm;
        }
        else
            logicTypeCombo.tabTitle = "";
        if(behavior >= 0)
        {
            this.behavior = SwitchBehavior.fromValue(behavior);
            String nm = this.behavior.name();
            nm = nm.substring(0, 1) + nm.substring(1).toLowerCase();
            nm = nm.replaceAll("_", " ");
            switchBehaviorCombo.tabTitle = nm;
        }
        else
            switchBehaviorCombo.tabTitle = "";

        String switchName = this.name.setTextboxValueString(name);
        String switchTagName = this.labelName.setTextboxValueString(tagName);
        String switchTagCreator = this.labelCreator.setTextboxValueString(tagCreator);

        String stckrPlan = this.stickerPlan.setTextboxValueString(stickerPlan);

        Vector2f rad = this.radius.setTextboxValueFloat(Utils.round(radius, 1));
        Vector2f minRad = this.minRadius.setTextboxValueFloat(minRadius);
        Vector2i layerRang = this.layerRange.setTextboxValueInt(Math.clamp(0, 255,layerRange));
        Vector2f angleRang = this.angleRange.setTextboxValueFloat(angleRange);

        Vector2i teamFilt = this.teamFilter.setTextboxValueInt(teamFilter);

        Vector2i tagInd = this.labelIndex.setTextboxValueInt(tagIndex);

        Vector2i requiredProj = this.bulletsRequired.setTextboxValueInt(requiredProjectile);
        Vector2i currentProj = this.bulletsDetected.setTextboxValueInt(currentProjectile);
        Vector2i refreshT = this.bulletRefreshTime.setTextboxValueInt(refreshTime);
        Vector2i shootingP = this.bulletPlayerNumber.setTextboxValueInt(shootingPlayer);

        Vector2f activationHoldT = this.activationHoldTime.setTextboxValueFloat(activationHoldTime / 30f);
        Vector2i activationHoldTFrames = this.activationHoldTimeFrames.setTextboxValueInt(activationHoldTime);
        Vector2f timerT = this.timerCount.setTextboxValueFloat(timerTime / 30f);
        Vector2i timerTFrames = this.timerCountFrames.setTextboxValueInt(timerTime);
//        Vector2i timerAutoC = this.timerAutoCount.setTextboxValueInt(Math.clamp(0, 255, timerAutoCount));

        Vector2f ranOnMin = this.randomOnTimeMin.setTextboxValueFloat(Utils.round(randomOnMin / 30f, 1));
        Vector2i ranOnMinFrames = this.randomOnTimeMinFrames.setTextboxValueInt(randomOnMin);
        Vector2f ranOnMax = this.randomOnTimeMax.setTextboxValueFloat(Utils.round(randomOnMax / 30f, 1));
        Vector2i ranOnMaxFrames = this.randomOnTimeMaxFrames.setTextboxValueInt(randomOnMin);
        Vector2f ranOffMin = this.randomOffTimeMin.setTextboxValueFloat(Utils.round(randomOffMin / 30f, 1));
        Vector2i ranOffMinFrames = this.randomOffTimeMinFrames.setTextboxValueInt(randomOnMin);
        Vector2f ranOffMax = this.randomOffTimeMax.setTextboxValueFloat(Utils.round(randomOffMax / 30f, 1));
        Vector2i ranOffMaxFrames = this.randomOffTimeMaxFrames.setTextboxValueInt(randomOnMin);

        for(int i = 0; i < things.size(); i++)
        {
            Thing thing = things.get(i);

            if(thing == null)
                continue;

            if(thing.thing.hasPart(part) && thing.selected)
            {
                PSwitch pSwitch = ((PSwitch)thing.thing.getPart(part));

                if(switchName != null)
                    pSwitch.name = switchName;
                if(switchTagName != null)
                {
                    if(pSwitch.value == null)
                        pSwitch.value = new DataLabelValue();
                    pSwitch.value.labelName = switchTagName;
                }
                if(switchTagCreator != null)
                {
                    if(pSwitch.value == null)
                        pSwitch.value = new DataLabelValue();
                    if(pSwitch.value.creatorID == null)
                        pSwitch.value.creatorID = new NetworkOnlineID();
                    pSwitch.value.creatorID.setData(switchTagCreator);
                }

                if (stckrPlan != null)
                    try{pSwitch.stickerPlan = new ResourceDescriptor(stckrPlan.trim(), ResourceType.PLAN);}catch (Exception e){}

                if(rad.y == 1)
                    pSwitch.radius = rad.x * 21f;
                if(minRad.y == 1)
                    pSwitch.minRadius = minRad.x * 21f;
                if(layerRang.y == 1)
                    pSwitch.layerRange = (byte) Math.clamp(0, 255, layerRang.x);
                if(angleRang.y == 1)
                    pSwitch.angleRange = angleRang.x / 2f;

                if(teamFilt.y == 1)
                    pSwitch.teamFilter = teamFilt.x;

                if(tagInd.y == 1)
                {
                    if(pSwitch.value == null)
                        pSwitch.value = new DataLabelValue();
                    pSwitch.value.labelIndex = tagInd.x;
                }

                if(requiredProj.y == 1)
                    pSwitch.bulletsRequired = requiredProj.x;
                if(currentProj.y == 1)
                    pSwitch.bulletsDetected = currentProj.x;
                if(refreshT.y == 1)
                    pSwitch.bulletRefreshTime = refreshT.x;
                if(shootingP.y == 1)
                    pSwitch.bulletPlayerNumber = shootingP.x;

                if(activationHoldT.y == 1)
                    pSwitch.activationHoldTime = Math.round(activationHoldT.x * 30f);
                if(activationHoldTFrames.y == 1)
                    pSwitch.activationHoldTime = activationHoldTFrames.x;
                if(timerT.y == 1)
                    pSwitch.timerCount = timerT.x * 30f;
                if(timerTFrames.y == 1)
                    pSwitch.timerCount = timerTFrames.x;
//                if(timerAutoC.y == 1)
//                    pSwitch.timerAutoCount = (byte) Math.clamp(0, 255, timerAutoC.x);

                if(ranOnMin.y == 1)
                    pSwitch.randomOnTimeMin = Math.round(ranOnMin.x * 30f);
                if(ranOnMinFrames.y == 1)
                    pSwitch.randomOnTimeMin = ranOnMinFrames.x;
                if(ranOnMax.y == 1)
                    pSwitch.randomOnTimeMax = Math.round(ranOnMax.x * 30f);
                if(ranOnMaxFrames.y == 1)
                    pSwitch.randomOnTimeMax = ranOnMaxFrames.x;
                if(ranOffMin.y == 1)
                    pSwitch.randomOffTimeMin = Math.round(ranOffMin.x * 30f);
                if(ranOffMinFrames.y == 1)
                    pSwitch.randomOffTimeMin = ranOffMinFrames.x;
                if(ranOffMax.y == 1)
                    pSwitch.randomOffTimeMax = Math.round(ranOffMax.x * 30f);
                if(ranOffMaxFrames.y == 1)
                    pSwitch.randomOffTimeMax = ranOffMaxFrames.x;
            }
        }
    }
}
