package bog.lbpas.view3d.mainWindow.screens;

import bog.lbpas.view3d.core.Model;
import bog.lbpas.view3d.core.Texture;
import bog.lbpas.view3d.core.Transformation3D;
import bog.lbpas.view3d.core.types.Entity;
import bog.lbpas.view3d.core.types.Thing;
import bog.lbpas.view3d.mainWindow.ConstantTextures;
import bog.lbpas.view3d.mainWindow.View3D;
import bog.lbpas.view3d.managers.MouseInput;
import bog.lbpas.view3d.renderer.gui.GuiScreen;
import bog.lbpas.view3d.renderer.gui.cursor.ECursor;
import bog.lbpas.view3d.renderer.gui.elements.Button;
import bog.lbpas.view3d.renderer.gui.elements.Panel;
import bog.lbpas.view3d.renderer.gui.elements.*;
import bog.lbpas.view3d.utils.Config;
import bog.lbpas.view3d.utils.Cursors;
import bog.lbpas.view3d.utils.Utils;
import cwlib.enums.Part;
import cwlib.resources.RBevel;
import cwlib.structs.bevel.BevelVertex;
import cwlib.structs.things.parts.PGeneratedMesh;
import cwlib.structs.things.parts.PShape;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;
import java.util.*;

/**
 * @author Bog
 */
public class GfxMaterialEditing extends GuiScreen {

    View3D mainView;

    public GfxMaterialEditing(View3D mainView)
    {
        super(mainView.renderer, mainView.loader, mainView.window);
        this.mainView = mainView;
        this.init();
    }

    public void init()
    {

    }

    @Override
    public void draw(MouseInput mouseInput) {

        super.draw(mouseInput);
    }

    @Override
    public void resize() {
        super.resize();

    }

    @Override
    public boolean onClick(MouseInput mouseInput, int button, int action, int mods) {
        boolean overOther = super.onClick(mouseInput, button, action, mods);

        boolean shift = mods == 1;
        boolean ctrl = mods == 2;
        boolean ctrlShift = mods == 3;
        boolean alt = mods == 4;
        boolean shiftAlt = mods == 5;
        boolean ctrlAlt = mods == 6;
        boolean ctrlShiftAlt = mods == 7;
        boolean winKey = mods == 8;

        return overOther;
    }

    @Override
    public boolean onKey(int key, int scancode, int action, int mods) {
        boolean elementFocused = super.onKey(key, scancode, action, mods);

        if(key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_PRESS && !elementFocused)
            mainView.setCurrentScreen(previousScreen);

        return elementFocused;
    }
}
