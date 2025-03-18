package bog.lbpas.view3d.mainWindow.screens;

import bog.lbpas.view3d.mainWindow.View3D;
import bog.lbpas.view3d.managers.MouseInput;
import bog.lbpas.view3d.renderer.gui.GuiScreen;
import bog.lbpas.view3d.renderer.gui.elements.Button;
import bog.lbpas.view3d.renderer.gui.elements.Element;
import bog.lbpas.view3d.utils.Config;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

/**
 * @author Bog
 */
public class OverrideScreen extends GuiScreen {

    View3D mainView;
    public OverrideScreen(View3D mainView) {
        super(mainView.renderer, mainView.loader, mainView.window);
        this.mainView = mainView;
        this.init();
    }

    public boolean shadingMenu = false;
    Vector2f shadingPos = new Vector2f();
    Button solidShading;
    Button materialShading;
    Button normalShading;
    Element hitbox;

    public void init()
    {
        solidShading = new Button("solidShading", "Solid", new Vector2f(-1000, 50), new Vector2f(200, 30), 10, renderer, loader, window) {
            @Override
            public void clickedButton(int button, int action, int mods) {
                Config.VIEWER_SHADING = 1;

                solidShading.isClicked = true;
                materialShading.isClicked = false;
                normalShading.isClicked = false;
            }

            @Override
            public void setClicked(boolean clicked) {

            }
        };
        materialShading = new Button("materialShading", "Material Preview", new Vector2f(-1000, 50), new Vector2f(200, 30), 10, renderer, loader, window) {
            @Override
            public void clickedButton(int button, int action, int mods) {
                Config.VIEWER_SHADING = 0;

                solidShading.isClicked = false;
                materialShading.isClicked = true;
                normalShading.isClicked = false;
            }

            @Override
            public void setClicked(boolean clicked) {

            }
        };
        normalShading = new Button("normalShading", "Normals", new Vector2f(-1000, 50), new Vector2f(200, 30), 10, renderer, loader, window) {
            @Override
            public void clickedButton(int button, int action, int mods) {
                Config.VIEWER_SHADING = 2;

                solidShading.isClicked = false;
                materialShading.isClicked = false;
                normalShading.isClicked = true;
            }

            @Override
            public void setClicked(boolean clicked) {

            }
        };

        switch (Config.VIEWER_SHADING)
        {
            case 0:
                solidShading.isClicked = false;
                materialShading.isClicked = true;
                normalShading.isClicked = false;
                break;
            case 1:
                solidShading.isClicked = true;
                materialShading.isClicked = false;
                normalShading.isClicked = false;
                break;
            case 2:
                solidShading.isClicked = false;
                materialShading.isClicked = false;
                normalShading.isClicked = true;
                break;
        }

        hitbox = new Element()
        {
            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement, boolean focusedOther) {
                super.onClick(mouseInput, pos, button, action, mods, overElement, focusedOther);

                if(isMouseOverElement(pos) && !overElement && action == GLFW.GLFW_PRESS)
                    shadingMenu = false;
            }
        };
        hitbox.size = new Vector2f(window.width, window.height);
        hitbox.pos = new Vector2f(0);

        guiElements.add(hitbox);
        guiElements.add(solidShading);
        guiElements.add(materialShading);
        guiElements.add(normalShading);
    }

    @Override
    public void draw(MouseInput mouseInput) {

        if(shadingMenu)
        {
            renderer.drawCircle(loader, shadingPos, (getStringWidth("Shading", 10) / 2) * 1.35f, Config.INTERFACE_PRIMARY_COLOR);
            renderer.drawCircleOutline(loader, shadingPos, (getStringWidth("Shading", 10) / 2) * 1.35f, Config.INTERFACE_PRIMARY_COLOR2);
            renderer.drawString("Shading", Config.FONT_COLOR, (int) (shadingPos.x - getStringWidth("Shading", 10) / 2), (int) (shadingPos.y - getFontHeight(10) / 2), 10);

            solidShading.pos.x = shadingPos.x + 50;
            solidShading.pos.y = shadingPos.y - solidShading.size.y / 2;

            materialShading.pos.x = shadingPos.x - 50 - materialShading.size.x;
            materialShading.pos.y = shadingPos.y - materialShading.size.y / 2;

            normalShading.pos.x = shadingPos.x - materialShading.size.x / 2;
            normalShading.pos.y = shadingPos.y - 50 - solidShading.size.y;

            hitbox.size.x = window.width;
            hitbox.size.y = window.height;
            hitbox.pos.x = 0;
            hitbox.pos.y = 0;
        }
        else
        {
            solidShading.pos.x = -1000;
            materialShading.pos.x = -1000;
            normalShading.pos.x = -1000;

            hitbox.size.x = 0;
            hitbox.size.y = 0;
            hitbox.pos.x = -1000;
            hitbox.pos.y = -1000;
        }

        super.draw(mouseInput);
    }

    @Override
    public boolean onClick(MouseInput mouseInput, int button, int action, int mods) {

        boolean elementFocused = super.onClick(mouseInput, button, action, mods);

        if(!elementFocused)
        {
            if(Config.SHADING.isButton(button) && action == GLFW.GLFW_PRESS)
            {
                shadingMenu = true;
                shadingPos.x = (float) mouseInput.currentPos.x;
                shadingPos.y = (float) mouseInput.currentPos.y;
            }
            else if(Config.SHADING.isButton(button) && action == GLFW.GLFW_RELEASE)
                shadingMenu = false;
        }

        return elementFocused;
    }

    @Override
    public boolean onKey(int key, int scancode, int action, int mods) {

        boolean elementFocused = super.onKey(key, scancode, action, mods);

        if(!elementFocused)
        {
            if(Config.SHADING.isKey(key) && action == GLFW.GLFW_PRESS)
            {
                shadingMenu = true;
                shadingPos.x = (float) mainView.mouseInput.currentPos.x;
                shadingPos.y = (float) mainView.mouseInput.currentPos.y;
            }
            else if(Config.SHADING.isKey(key) && action == GLFW.GLFW_RELEASE)
                shadingMenu = false;
        }

        return elementFocused;
    }
}
