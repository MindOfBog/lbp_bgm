package bog.lbpas.view3d.mainWindow.screens.thingPart.parts;

import bog.lbpas.view3d.core.types.Thing;
import bog.lbpas.view3d.mainWindow.View3D;
import bog.lbpas.view3d.renderer.gui.elements.DropDownTab;

import java.util.ArrayList;

/**
 * @author Bog
 */
public abstract class PartSpriteLight extends iPart {

    public PartSpriteLight(int tabWidth, float comboWidth, float panelHeight, float closeWidth, float finalGap, DropDownTab tab, View3D view) {
        super(cwlib.enums.Part.SPRITE_LIGHT, "PSpriteLight", "Sprite Light", tabWidth, comboWidth, panelHeight, closeWidth, finalGap, tab, view);
    }

    @Override
    public void init(View3D view) {

    }

    @Override
    public void addValues(ArrayList<Integer> selected, ArrayList<Thing> things) {

    }
}
