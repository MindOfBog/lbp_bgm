package bog.lbpas.view3d.mainWindow.screens.thingPart.parts;

import bog.lbpas.view3d.core.types.Thing;
import bog.lbpas.view3d.mainWindow.View3D;
import bog.lbpas.view3d.managers.MouseInput;
import bog.lbpas.view3d.renderer.gui.elements.*;
import cwlib.enums.TriggerType;
import cwlib.structs.things.parts.PShape;
import cwlib.structs.things.parts.PTrigger;
import org.joml.Math;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Bog
 */
public abstract class PartTrigger extends iPart {

    public PartTrigger(int tabWidth, float comboWidth, float panelHeight, float closeWidth, float finalGap, DropDownTab tab, View3D view) {
        super(cwlib.enums.Part.TRIGGER, "PTrigger", "Trigger", tabWidth, comboWidth, panelHeight, closeWidth, finalGap, tab, view);
    }

    ComboBox triggerTypeCombo;
    Textbox radiusMultiplier;
    Textbox zRangeHundreds;
    Checkbox allZLayers;
    Textbox hysteresisMultiplier;
    Checkbox enabled;
    Textbox zOffset;
    Textbox scoreValue;

    @Override
    public void init(View3D view) {
        Panel triggerTypePanel = this.partComboBox.addPanel("triggerTypePanel");
        triggerTypePanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("triggerTypeStr", "Trigger Type:", 10, view.renderer), 0.525f));
        triggerTypeCombo = new ComboBox("triggerTypeCombo", "Radius", new Vector2f(), new Vector2f(), 10, 150, view.renderer, view.loader, view.window);

        ButtonList triggerTypeList = triggerTypeCombo.addList("triggerTypeList", new ButtonList(Arrays.asList(TriggerType.values()), 10, view.renderer, view.loader, view.window) {
            @Override
            public void clickedButton(Object object, int index, int button, int action, int mods) {
                String name = ((TriggerType)object).name();
                name = name.replaceAll("_", " ");
                name = name.substring(0, 1) + name.substring(1).toLowerCase();
                triggerTypeCombo.tabTitle = name;

                for(Thing thing : view.things)
                    if(thing.thing.hasPart(part) && thing.selected)
                        ((PTrigger)thing.thing.getPart(part)).triggerType = (TriggerType) object;
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

                boolean selected = true;

                for(Thing thing : view.things)
                    if(thing.thing.hasPart(part) && thing.selected)
                        if(((PTrigger)thing.thing.getPart(part)).triggerType.getValue() != ((TriggerType) object).getValue())
                            selected = false;

                return selected;
            }

            @Override
            public String buttonText(Object object, int index) {
                String name = ((TriggerType)object).name();
                name = name.replaceAll("_", " ");
                name = name.substring(0, 1) + name.substring(1).toLowerCase();
                return name;
            }

            @Override
            public boolean searchFilter(Object object, int index) {
                return true;
            }

            @Override
            public int buttonHeight() {
                return getFontHeight(this.fontSize) + 4;
            }

            @Override
            public void draw(MouseInput mouseInput, boolean overElement) {
                hovering = -1;
                super.draw(mouseInput, overElement);
            }
        }, 100);

        triggerTypePanel.elements.add(new Panel.PanelElement(triggerTypeCombo, 0.475f));

        Panel radiusMultiplierPanel = this.partComboBox.addPanel("radiusMultiplierPanel");
        radiusMultiplierPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("radiusMultiplierStr", "Radius Mul.:", 10, view.renderer), 0.7f));

        radiusMultiplier = new Textbox("radiusMultiplier", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window).noLetters().noOthers();

        radiusMultiplierPanel.elements.add(new Panel.PanelElement(radiusMultiplier, 0.3f));

        Panel zRangeHundredsPanel = this.partComboBox.addPanel("zRangeHundredsPanel");
        zRangeHundredsPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("zRangeHundredsStr", "Layer Range:", 10, view.renderer), 0.7f));

        zRangeHundreds = new Textbox("zRangeHundreds", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window).noLetters().noOthers();

        zRangeHundredsPanel.elements.add(new Panel.PanelElement(zRangeHundreds, 0.3f));

        allZLayers = this.partComboBox.addCheckbox("allZLayers", "All Z Layers", new Checkbox()
        {
            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement) {
                super.onClick(mouseInput, pos, button, action, mods, overElement);

                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos) && !overElement)
                    for(Thing thing : view.things)
                        if(thing.thing.hasPart(part) && thing.selected)
                            ((PTrigger)thing.thing.getPart(part)).allZLayers = this.isChecked;
            }
        });

        Panel hysteresisMultiplierPanel = this.partComboBox.addPanel("hysteresisMultiplierPanel");
        hysteresisMultiplierPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("hysteresisMultiplierStr", "Hysteresis Mul.:", 10, view.renderer), 0.7f));

        hysteresisMultiplier = new Textbox("hysteresisMultiplier", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window).noLetters().noOthers();

        hysteresisMultiplierPanel.elements.add(new Panel.PanelElement(hysteresisMultiplier, 0.3f));

        enabled = this.partComboBox.addCheckbox("enabled", "Enabled", new Checkbox()
        {
            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement) {
                super.onClick(mouseInput, pos, button, action, mods, overElement);

                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos) && !overElement)
                    for(Thing thing : view.things)
                        if(thing.thing.hasPart(part) && thing.selected)
                            ((PTrigger)thing.thing.getPart(part)).enabled = this.isChecked;
            }
        });

        Panel zOffsetPanel = this.partComboBox.addPanel("zOffsetPanel");
        zOffsetPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("zOffsetStr", "Z Offset:", 10, view.renderer), 0.7f));

        zOffset = new Textbox("zOffset", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window).noLetters().noOthers();

        zOffsetPanel.elements.add(new Panel.PanelElement(zOffset, 0.3f));

        Panel scoreValuePanel = this.partComboBox.addPanel("scoreValuePanel");
        scoreValuePanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("scoreValueStr", "Score Value:", 10, view.renderer), 0.7f));

        scoreValue = new Textbox("scoreValue", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window).noLetters().noOthers();

        scoreValuePanel.elements.add(new Panel.PanelElement(scoreValue, 0.3f));
    }

    @Override
    public void addValues(ArrayList<Integer> selected, ArrayList<Thing> things) {

        triggerTypeCombo.tabTitle = null;

        float radMul = Float.NEGATIVE_INFINITY;
        float zRanHun = Float.NEGATIVE_INFINITY;
        float hystMult = Float.NEGATIVE_INFINITY;
        float zOff = Float.NEGATIVE_INFINITY;
        float scoVal = Float.NEGATIVE_INFINITY;

        int allZ = -1;
        int ena = -1;

        for(int i : selected)
            if(things.get(i).thing.hasPart(part))
            {
                PTrigger trigger = ((PTrigger)things.get(i).thing.getPart(part));

                for(TriggerType type : TriggerType.values())
                    if(triggerTypeCombo.tabTitle == null)
                    {
                        String name = trigger.triggerType.name();
                        name = name.replaceAll("_", " ");
                        name = name.substring(0, 1) + name.substring(1).toLowerCase();
                        triggerTypeCombo.tabTitle = name;
                    }
                    else if(!trigger.triggerType.name().equalsIgnoreCase(triggerTypeCombo.tabTitle))
                    {
                        triggerTypeCombo.tabTitle = "";
                    }

                float radiusMultiplier = trigger.radiusMultiplier;
                if(Float.isInfinite(radMul))
                    radMul = radiusMultiplier;
                else if(radMul != radiusMultiplier)
                    radMul = Float.NaN;

                float zRangeHundreds = trigger.zRangeHundreds;
                if(Float.isInfinite(zRanHun))
                    zRanHun = zRangeHundreds;
                else if(zRanHun != zRangeHundreds)
                    zRanHun = Float.NaN;

                float hysteresisMultiplier = trigger.hysteresisMultiplier;
                if(Float.isInfinite(hystMult))
                    hystMult = hysteresisMultiplier;
                else if(hystMult != hysteresisMultiplier)
                    hystMult = Float.NaN;

                float zOffset = trigger.zOffset;
                if(Float.isInfinite(zOff))
                    zOff = zOffset;
                else if(zOff != zOffset)
                    zOff = Float.NaN;

                float scoreValue = trigger.scoreValue;
                if(Float.isInfinite(scoVal))
                    scoVal = scoreValue;
                else if(scoVal != scoreValue)
                    scoVal = Float.NaN;

                if(allZ == -1)
                    allZ = trigger.allZLayers ? 1 : 0;
                else if(allZ != (trigger.allZLayers ? 1 : 0))
                    allZ = 0;

                if(ena == -1)
                    ena = trigger.enabled ? 1 : 0;
                else if(ena != (trigger.enabled ? 1 : 0))
                    ena = 0;
            }

        Vector2f radiusMul = radiusMultiplier.setTextboxValueFloat(radMul);
        Vector2i zRangeHun = zRangeHundreds.setTextboxValueInt((int)Math.clamp(Math.floor(zRanHun), -128, 127));
        Vector2f hysteresisMul = hysteresisMultiplier.setTextboxValueFloat(hystMult);
        Vector2f zOffs = zOffset.setTextboxValueFloat(zOff);
        Vector2i scoreVal = scoreValue.setTextboxValueInt(Math.round(scoVal));

        allZLayers.isChecked = allZ == 1;
        enabled.isChecked = ena == 1;

        for(int i : selected)
            if(things.get(i).thing.hasPart(part))
            {
                PTrigger trigger = ((PTrigger)things.get(i).thing.getPart(part));

                if(radiusMul.y == 1)
                    trigger.radiusMultiplier = radiusMul.x;
                if(zRangeHun.y == 1)
                    trigger.zRangeHundreds = (byte) zRangeHun.x;
                if(hysteresisMul.y == 1)
                    trigger.hysteresisMultiplier = hysteresisMul.x;
                if(zOffs.y == 1)
                    trigger.zOffset = zOffs.x;
                if(scoreVal.y == 1)
                    trigger.scoreValue = scoreVal.x;
            }
    }
}
