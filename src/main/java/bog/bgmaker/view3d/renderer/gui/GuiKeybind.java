package bog.bgmaker.view3d.renderer.gui;

import bog.bgmaker.view3d.ObjectLoader;
import bog.bgmaker.view3d.core.Model;
import bog.bgmaker.view3d.managers.InputMan;
import bog.bgmaker.view3d.managers.MouseInput;
import bog.bgmaker.view3d.managers.RenderMan;
import bog.bgmaker.view3d.managers.WindowMan;
import bog.bgmaker.view3d.renderer.gui.elements.Button;
import bog.bgmaker.view3d.renderer.gui.elements.Element;
import bog.bgmaker.view3d.renderer.gui.ingredients.LineStrip;
import bog.bgmaker.view3d.utils.Config;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

/**
 * @author Bog
 */
public abstract class GuiKeybind extends GuiScreen{

    InputMan currentKey;
    int fontSize;
    Model outlineRect;
    Model outlineRect1;
    int prevFontSize;

    Element backHitbox;

    public GuiKeybind(InputMan key, int fontSize, RenderMan renderer, ObjectLoader loader, WindowMan window1)
    {
        super(renderer, loader, window1);
        outlineRect = LineStrip.processVerts(LineStrip.getRectangle(new Vector2f(200, (getFontHeight(fontSize) / 2 + 33) * 2)), loader, window1);
        outlineRect1 = LineStrip.processVerts(LineStrip.getRectangle(new Vector2f(190, 20)), loader, window1);

        backHitbox = new Element() {

            @Override
            public void draw(MouseInput mouseInput, boolean overElement) {
                this.size = new Vector2f(window1.width, window1.height);
                super.draw(mouseInput, overElement);
            }

        };
        backHitbox.pos = new Vector2f(0, 0);
        backHitbox.size = new Vector2f(window1.width, window1.height);
        backHitbox.id = "backHitbox";

        this.currentKey = new InputMan(key.key, key.mouse);
        this.fontSize = fontSize;
        this.prevFontSize = fontSize;

        this.guiElements.add(backHitbox);
        this.guiElements.add(new Button("save", "Save", new Vector2f(window1.width/2 - 95, window1.height / 2 - getFontHeight(fontSize) / 2 + 15), new Vector2f(90, 20), fontSize, renderer, loader, window1) {

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
        this.guiElements.add(new Button("cancel", "Cancel", new Vector2f(window1.width/2 + 5, window1.height / 2 - getFontHeight(fontSize) / 2 + 15), new Vector2f(90, 20), fontSize, renderer, loader, window1) {

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

        if(prevFontSize != fontSize)
        {
            outlineRect.cleanup(loader);
            outlineRect = LineStrip.processVerts(LineStrip.getRectangle(new Vector2f(200, (getFontHeight(fontSize) / 2 + 33) * 2)), loader, window);
            prevFontSize = fontSize;
        }

        renderer.doBlur(2);
        renderer.doBlur(3);
        renderer.doBlur(4);
        renderer.doBlur(5);
        renderer.doBlur(6);
        renderer.doBlur(7);
        renderer.doBlur(8);
        renderer.doBlur(9);
        renderer.doBlur(10);
        renderer.doBlur(9);
        renderer.doBlur(8);
        renderer.doBlur(7);
        renderer.doBlur(6);
        renderer.doBlur(5);
        renderer.doBlur(4);
        renderer.doBlur(3);
        renderer.doBlur(2);
        renderer.doBlur(1.5f);
        renderer.doBlur(1.25f);

        renderer.drawRect(window.width/2 - 100, window.height/2 + (getFontHeight(fontSize) / 2 - 10 - 45), 200, (getFontHeight(fontSize) / 2 + 33) * 2, Config.PRIMARY_COLOR);
        renderer.drawRect(window.width / 2 - 95, window.height / 2 - getFontHeight(fontSize) / 2 - 10, 190, 20, Config.INTERFACE_PRIMARY_COLOR);
        renderer.drawRectOutline(new Vector2f(window.width/2 - 100, window.height/2 + (getFontHeight(fontSize) / 2 - 10 - 45)), outlineRect, Config.SECONDARY_COLOR, false);
        renderer.drawRectOutline(new Vector2f(window.width / 2 - 95, window.height / 2 - getFontHeight(fontSize) / 2 - 10), outlineRect1, Config.INTERFACE_PRIMARY_COLOR2, false);
        renderer.drawString(currentKey.inputName(), Config.FONT_COLOR, window.width / 2 - getStringWidth(currentKey.inputName(), fontSize) / 2, window.height / 2 - getFontHeight(fontSize), fontSize);
        renderer.drawString("Select any key:", Config.FONT_COLOR, window.width / 2 - 94, window.height / 2 + (getFontHeight(fontSize) / 2 - 5 - 42), fontSize);

        super.draw(mouseInput);
    }

    @Override
    public boolean onKey(int key, int scancode, int action, int mods) {
        if(key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_PRESS)
        {
            currentKey.mouse = false;
            currentKey.key = GLFW.GLFW_KEY_UNKNOWN;
            keybind(currentKey);
            returnPreviousScreen();
        }
        if(backHitbox.hovering)
        {
            if(action == GLFW.GLFW_PRESS)
            {
                currentKey.mouse = false;
                currentKey.key = key;
            }
        }
        return super.onKey(key, scancode, action, mods);
    }

    @Override
    public boolean onClick(MouseInput mouseInput, int button, int action, int mods) {

        if(backHitbox.hovering)
        {
            if(action == GLFW.GLFW_PRESS)
            {
                currentKey.mouse = true;
                currentKey.key = button;
            }
        }

        return super.onClick(mouseInput, button, action, mods);
    }

    public void resize()
    {
        if(outlineRect != null)
            outlineRect.cleanup(loader);
        if(outlineRect1 != null)
            outlineRect1.cleanup(loader);
        outlineRect = LineStrip.processVerts(LineStrip.getRectangle(new Vector2f(200, (getFontHeight(fontSize) / 2 + 33) * 2)), loader, window);
        outlineRect1 = LineStrip.processVerts(LineStrip.getRectangle(new Vector2f(190, 20)), loader, window);
    }

    public abstract void keybind(InputMan currentKey);
    public abstract void returnPreviousScreen();

}
