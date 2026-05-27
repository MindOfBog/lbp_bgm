package bog.lbpas.view3d.mainWindow.screens.thingPart.parts;

import bog.lbpas.swing.CodeEditor;
import bog.lbpas.view3d.core.Texture;
import bog.lbpas.view3d.mainWindow.ConstantTextures;
import bog.lbpas.view3d.mainWindow.View3D;
import bog.lbpas.view3d.managers.MouseInput;
import bog.lbpas.view3d.renderer.gui.elements.*;
import bog.lbpas.view3d.utils.Consts;
import bog.lbpas.view3d.utils.print;
import com.google.gson.*;
import cwlib.enums.Part;
import cwlib.io.Serializable;
import cwlib.structs.things.Thing;
import cwlib.util.GsonUtils;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Bog
 */
public abstract class iPart {

    public ComboBox partComboBox;
    public Panel partPanel;
    public boolean hasPart;
    String id;
    String name;

    public Part part;


    public iPart(Part part, String id, String name, int tabWidth, float comboWidth, float panelHeight, float closeWidth, float finalGap, Element tab, View3D view) {
        this.part = part;
        this.id = id;
        this.name = name;

        partComboBox = new ComboBox(id, name, null, null, view.renderer, view.loader, view.window, false)
        {
            @Override
            public int[] getParentTransform() {
                return tab instanceof DropDownTab ? new int[]{Math.round(tab.pos.x), Math.round(tab.pos.y), Math.round(tab.size.x)} : ((ComboBox)tab).getTabPosWidth();
            }

            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overOther, boolean focusedOther) {

                if(action == GLFW.GLFW_RELEASE && !isMouseOverElement(pos) && tab.isMouseOverElement(pos))
                {
                    this.extended = false;
                    for(Element e : comboElements)
                        e.setFocused(false);
                }

                super.onClick(mouseInput, pos, button, action, mods, overOther, focusedOther);
            }

            @Override
            public void onExtend() {
                super.onExtend();

                if(!extended)
                    return;
                
                onExtendPart();
            }


            @Override
            public int tabWidth() {
                return Math.round(tabWidth * (getFontHeight() / 12f));
            }
        };
        partPanel = new Panel(new Vector2f(0, panelHeight), view.renderer);
        partPanel.elements.add(new Panel.PanelElement(partComboBox, comboWidth));
        partPanel.elements.add(new Panel.PanelElement(null, finalGap));
        partPanel.elements.add(new Panel.PanelElement(new ButtonImage("closeButton", new Vector2f(0), new Vector2f(22, 22), view.renderer, view.loader, view.window) {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)
                    removePart(part);
            }

            @Override
            public Texture getImage() {
                return getTexture(ConstantTextures.WINDOW_CLOSE);
            }
        }, closeWidth));

        partComboBox.addButton("Edit JSON of \"" + this.name + "\"(s)", new Button() {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(action != GLFW.GLFW_PRESS)
                    return;
                for(bog.lbpas.view3d.core.types.Thing thing : view.things)
                {
                    if(!thing.selected)
                        continue;

                    CodeEditor partEditor = new CodeEditor("Thing: \"" + thing.thing.name + "\" | Part: \"" + iPart.this.name + "\"", GsonUtils.toJSONCodeEditor(thing.thing.getPart(iPart.this.part)), SyntaxConstants.SYNTAX_STYLE_JSON)
                    {
                        @Override
                        public boolean onSaveChanges(String json) {

                            try
                            {
                                Serializable edited = fromJSON(json);

                                if (edited != null)
                                {
                                    Object original = thing.thing.getPart(part);

                                    if(original != null)
                                        GsonUtils.CodeEditorUtil.mergeExcluding(edited, original, Thing.class);
                                    else
                                        thing.thing.setPart(part, edited);
                                }
                                else if(thing.thing.hasPart(part))
                                    thing.thing.setPart(part, null);

                                return true;
                            }catch (Exception e)
                            {
                                print.stackTrace(e);
                            }

                            return false;
                        }

                        private <T extends Serializable> T fromJSON(String json)
                        {
                            return GsonUtils.fromJSONCodeEditor(json, (Class<T>) iPart.this.part.getSerializable());
                        }

                        @Override
                        public void closeWindow() {
                            super.closeWindow();
                            thing.openEditors.remove(this);
                        }
                    };

                    thing.openEditors.add(partEditor);
                }
            }
        });
        partComboBox.addSeparator("jsonSeparator").size.y = 5;

        init(view);
    }

    public void addPartsReset()
    {
        hasPart = true;
    }

    public void hasPart(Thing thing)
    {
        if(!thing.hasPart(this.part))
        {
            hasPart = false;
            partComboBox.collapsed(true);
        }
    }

    public void addElements(ElementList list, View3D view)
    {
        if(hasPart)
        {
            if(!list.elements.contains(partPanel))
            {
                list.addPanel(partPanel);
                onAddedUI(view);
            }
        }
        else
            removeElements(list, view);
    }

    public void removeElements(ElementList list, View3D view)
    {
        if(list.elements.contains(partPanel))
        {
            list.elements.remove(partPanel);
            onRemovedUI(view);
        }
    }

    public void onAddedUI(View3D view)
    {

    }

    public void onRemovedUI(View3D view)
    {

    }

    public <T extends Serializable> void addPart(ArrayList<bog.lbpas.view3d.core.types.Thing> things, bog.lbpas.view3d.core.types.Thing thing, Part part, T p)
    {
        if(part == this.part)
            addPart(things, thing, p);
    }

    public void selectionChange()
    {
        partComboBox.setFocused(false);
    }

    public void collapse()
    {
        partComboBox.collapsed(true);
    }

    public abstract void addValues(ArrayList<Integer> selected, ArrayList<bog.lbpas.view3d.core.types.Thing> things);

    public abstract void removePart(Part part);
    public void init(View3D view)
    {
        partComboBox.addString("notAdded1", "The GUI for \"" + Consts.FONT_SET_ITALICS + this.id + Consts.FONT_RESET + "\"");
        partComboBox.addString("notAdded2", "has not yet been added.");
    };
    public <T extends Serializable> void addPart(ArrayList<bog.lbpas.view3d.core.types.Thing> things, bog.lbpas.view3d.core.types.Thing thing, T p){}

    public float compareNumber(float number, float valueToCompare)
    {
        float temp = valueToCompare;
        if (Float.isInfinite(number))
            return temp;
        else if (number != temp)
            return Float.NaN;
        return number;
    }

    public String compareString(String string, String valueToCompare)
    {
        if(string == null)
            return valueToCompare;
        else if(string != valueToCompare)
            return "";
        return string;
    }

    public int compareBoolean(int bool, boolean valueToCompare)
    {
        if(bool == -1)
            return valueToCompare ? 1 : 0;
        else if(bool != (valueToCompare ? 1 : 0))
            return 0;
        return bool;
    }

    public void onExtendPart(){}
    public abstract ArrayList<bog.lbpas.view3d.core.types.Thing> getThings();
    public void resize(View3D view){}
}
