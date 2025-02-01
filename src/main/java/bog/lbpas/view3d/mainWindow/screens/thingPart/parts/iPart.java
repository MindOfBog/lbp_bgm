package bog.lbpas.view3d.mainWindow.screens.thingPart.parts;

import bog.lbpas.view3d.mainWindow.ConstantTextures;
import bog.lbpas.view3d.mainWindow.View3D;
import bog.lbpas.view3d.renderer.gui.elements.*;
import cwlib.enums.Part;
import cwlib.io.Serializable;
import cwlib.structs.things.Thing;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

/**
 * @author Bog
 */
public abstract class iPart {

    public ComboBox partComboBox;
    public Panel partPanel;
    public boolean hasPart;
    String id;
    String name;

    public cwlib.enums.Part part;

    public iPart(Part part, String id, String name, int tabWidth, float comboWidth, float panelHeight, float closeWidth, float finalGap, DropDownTab tab, View3D view) {
        this.part = part;
        this.id = id;
        this.name = name;

        partComboBox = new ComboBox(id, name, null, null, 10, tabWidth, view.renderer, view.loader, view.window, false)
        {
            @Override
            public int[] getParentTransform() {
                return new int[]{Math.round(tab.pos.x), Math.round(tab.pos.y), Math.round(tab.size.x)};
            }
        };
        partPanel = new Panel(new Vector2f(0, panelHeight), view.renderer);
        partPanel.elements.add(new Panel.PanelElement(partComboBox, comboWidth));
        partPanel.elements.add(new Panel.PanelElement(null, finalGap));
        partPanel.elements.add(new Panel.PanelElement(new ButtonImage("closeButton", ConstantTextures.getTexture(ConstantTextures.WINDOW_CLOSE, 23, 23, view.loader), new Vector2f(0), new Vector2f(22, 22), new Vector2f(23, 23), view.renderer, view.loader, view.window) {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)
                    removePart(part);
            }
        }, closeWidth));

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

    public void removeElements(ArrayList<Element> list)
    {
        list.remove(partPanel);
    }

    public void addElements(ElementList list)
    {
        if(hasPart)
            list.addPanel(partPanel);
        else
            removeElements(list.elements);
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
    public abstract void init(View3D view);
    public <T extends Serializable> void addPart(ArrayList<bog.lbpas.view3d.core.types.Thing> things, bog.lbpas.view3d.core.types.Thing thing, T p){}

}
