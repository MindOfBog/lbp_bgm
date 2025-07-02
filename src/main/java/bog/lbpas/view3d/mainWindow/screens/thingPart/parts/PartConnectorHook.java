package bog.lbpas.view3d.mainWindow.screens.thingPart.parts;

import bog.lbpas.view3d.core.types.Thing;
import bog.lbpas.view3d.mainWindow.View3D;
import bog.lbpas.view3d.renderer.gui.elements.DropDownTab;
import bog.lbpas.view3d.renderer.gui.elements.Element;

import java.util.ArrayList;

/**
 * @author Bog
 */
public abstract class PartConnectorHook extends iPart {

    public PartConnectorHook(int tabWidth, float comboWidth, float panelHeight, float closeWidth, float finalGap, Element tab, View3D view) {
        super(cwlib.enums.Part.CONNECTOR_HOOK, "PConnectorHook", "Connector Hook", tabWidth, comboWidth, panelHeight, closeWidth, finalGap, tab, view);
    }

    @Override
    public void init(View3D view) {

    }

    @Override
    public void addValues(ArrayList<Integer> selected, ArrayList<Thing> things) {

    }
}
