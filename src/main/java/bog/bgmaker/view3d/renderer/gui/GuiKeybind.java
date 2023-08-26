package bog.bgmaker.view3d.renderer.gui;

import bog.bgmaker.view3d.ObjectLoader;
import bog.bgmaker.view3d.managers.MouseInput;
import bog.bgmaker.view3d.managers.RenderMan;
import bog.bgmaker.view3d.managers.WindowMan;
import bog.bgmaker.view3d.renderer.gui.elements.Button;
import bog.bgmaker.view3d.utils.MousePicker;
import bog.bgmaker.view3d.utils.Utils;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

/**
 * @author Bog
 */
public abstract class GuiKeybind extends GuiScreen{

    int currentKey;
    int fontSize;

    public GuiKeybind(int key, int fontSize, RenderMan renderer, ObjectLoader loader, WindowMan window)
    {
        super(renderer, loader, window);
        this.currentKey = key;
        this.fontSize = fontSize;
        this.guiElements.add(new Button("save", "Save", new Vector2f(window.width/2 - 95, window.height / 2 - getFontHeight(fontSize) / 2 + 15), new Vector2f(90, 20), fontSize, renderer, loader, window) {

            @Override
            public void draw(MouseInput mouseInput, boolean overOther) {
                this.pos = new Vector2f(window.width/2 - 95, window.height / 2 - getFontHeight(fontSize) / 2 + 15);
                super.draw(mouseInput, overOther);
            }

            @Override
            public void clickedButton(int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)
                {
                    keybind(currentKey);
                    returnPreviousScreen();
                }
            }
        });
        this.guiElements.add(new Button("cancel", "Cancel", new Vector2f(window.width/2 + 5, window.height / 2 - getFontHeight(fontSize) / 2 + 15), new Vector2f(90, 20), fontSize, renderer, loader, window) {

            @Override
            public void draw(MouseInput mouseInput, boolean overOther) {
                this.pos = new Vector2f(window.width/2 + 5, window.height / 2 - getFontHeight(fontSize) / 2 + 15);
                super.draw(mouseInput, overOther);
            }

            @Override
            public void clickedButton(int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)
                    returnPreviousScreen();
            }
        });
    }

    @Override
    public void draw(MouseInput mouseInput) {
        this.previousScreen.draw(new MouseInput(null));

        drawRect(window.width/2 - 100, window.height/2 + (getFontHeight(fontSize) / 2 - 10 - 45), 200, (getFontHeight(fontSize) / 2 + 33) * 2, new Color(0, 0, 0, 0.5f));
        drawRect(window.width / 2 - 95, window.height / 2 - getFontHeight(fontSize) / 2 - 10, 190, 20, new Color(0, 0, 0, 0.5f));
        drawRectOutline(window.width/2 - 100, window.height/2 + (getFontHeight(fontSize) / 2 - 10 - 45), 200, (getFontHeight(fontSize) / 2 + 33) * 2, new Color(0, 0, 0, 0.5f), false);
        drawRectOutline(window.width / 2 - 95, window.height / 2 - getFontHeight(fontSize) / 2 - 10, 190, 20, new Color(0, 0, 0, 0.5f), false);
        String key = Utils.getKeyName(currentKey).toUpperCase();
        drawString(key, Color.white, window.width / 2 - getStringWidth(key, fontSize) / 2, window.height / 2 - getFontHeight(fontSize), fontSize);
        drawString("Select any key:", Color.white, window.width / 2 - 94, window.height / 2 + (getFontHeight(fontSize) / 2 - 5 - 42), fontSize);

        super.draw(mouseInput);
    }

    @Override
    public boolean onKey(int key, int scancode, int action, int mods) {
        if(key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_PRESS)
        {
            keybind(GLFW.GLFW_KEY_UNKNOWN);
            returnPreviousScreen();
        }
        if(action == GLFW.GLFW_PRESS)
            currentKey = key;
        return super.onKey(key, scancode, action, mods);
    }

    public abstract void keybind(int key);
    public abstract void returnPreviousScreen();

}
