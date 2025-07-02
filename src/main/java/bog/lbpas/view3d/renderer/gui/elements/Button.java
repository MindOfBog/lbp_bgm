package bog.lbpas.view3d.renderer.gui.elements;

import bog.lbpas.view3d.managers.assetLoading.ObjectLoader;
import bog.lbpas.view3d.core.Model;
import bog.lbpas.view3d.managers.MouseInput;
import bog.lbpas.view3d.managers.RenderMan;
import bog.lbpas.view3d.managers.WindowMan;
import bog.lbpas.view3d.renderer.gui.cursor.ECursor;
import bog.lbpas.view3d.renderer.gui.ingredients.LineStrip;
import bog.lbpas.view3d.utils.Config;
import bog.lbpas.view3d.utils.Cursors;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

/**
 * @author Bog
 */
public abstract class Button extends Element{

    public String buttonText;
    int fontSize;
    public boolean isClicked = false;

    Vector2f prevSize;
    Model outlineRect;

    public Button()
    {
        this.prevSize = new Vector2f();
    }

    public Button(String id)
    {
        this.id = id;
        this.prevSize = new Vector2f();
    }

    public Button(String id, String buttonText, int fontSize, RenderMan renderer, ObjectLoader loader, WindowMan window)
    {
        this.id = id;
        this.pos = new Vector2f();
        this.size = new Vector2f();
        this.prevSize = new Vector2f();
        this.fontSize = fontSize;
        this.renderer = renderer;
        this.loader = loader;
        this.window = window;
        this.buttonText = buttonText;
    }

    public Button(String id, String buttonText, Vector2f pos, Vector2f size, int fontSize, RenderMan renderer, ObjectLoader loader, WindowMan window)
    {
        this.id = id;
        this.pos = pos;
        this.size = size;
        this.prevSize = size;
        this.outlineRect = LineStrip.processVerts(LineStrip.getRectangle(size), loader, window);
        this.fontSize = fontSize;
        this.renderer = renderer;
        this.loader = loader;
        this.window = window;
        this.buttonText = buttonText;
    }

    public Button(String id, String buttonText, Vector2f pos, Vector2f size, int fontSize, RenderMan renderer, ObjectLoader loader, WindowMan window, Color buttonColor, Color buttonColorHighlighted, Color buttonColorClicked)
    {
        this.id = id;
        this.pos = pos;
        this.size = size;
        this.prevSize = size;
        this.outlineRect = LineStrip.processVerts(LineStrip.getRectangle(size), loader, window);
        this.fontSize = fontSize;
        this.renderer = renderer;
        this.loader = loader;
        this.window = window;
        this.buttonText = buttonText;
    }

    @Override
    public void draw(MouseInput mouseInput, boolean overOther) {
        super.draw(mouseInput, overOther);

        if(size.x != prevSize.x || size.y != prevSize.y)
        {
            refreshOutline();
            prevSize = size;
        }

        if(!isMouseOverElement(mouseInput) || overOther)
            setClicked(false);

        Color c = Config.INTERFACE_PRIMARY_COLOR;
        Color c2 = Config.INTERFACE_PRIMARY_COLOR2;

        if(isMouseOverElement(mouseInput) && !overOther)
        {
            c = Config.INTERFACE_SECONDARY_COLOR;
            c2 = Config.INTERFACE_SECONDARY_COLOR2;
        }

        if(isClicked)
        {
            c = Config.INTERFACE_TERTIARY_COLOR;
            c2 = Config.INTERFACE_TERTIARY_COLOR2;
        }

        renderer.startScissor(Math.round(pos.x), Math.round(pos.y), Math.round(size.x), Math.round(size.y));
        renderer.drawRect(Math.round(pos.x), Math.round(pos.y), Math.round(size.x), Math.round(size.y), c);
        renderer.drawString(buttonText, Config.FONT_COLOR, Math.round(pos.x + size.x / 2 - getStringWidth(buttonText, fontSize) / 2), Math.round(pos.y + size.y / 2 - getFontHeight(fontSize) / 2), fontSize);
        renderer.endScissor();
        renderer.drawRectOutline(new Vector2f(Math.round(pos.x), Math.round(pos.y)), outlineRect, c2, false);
    }

    public void setClicked(boolean clicked) {
        isClicked = clicked;
    }

    @Override
    public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overOther, boolean focusedOther) {

        if(!overOther)
        {
            if(isMouseOverElement(pos))
            {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)
                    setClicked(true);
                clickedButton(button, action, mods);
            }
            if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_RELEASE)
                setClicked(false);
        }

        super.onClick(mouseInput, pos, button, action, mods, overOther, focusedOther);
    }

    @Override
    public void resize() {
        super.resize();
        refreshOutline();
    }

    public abstract void clickedButton(int button, int action, int mods);

    public void refreshOutline()
    {
        if(outlineRect != null)
            this.outlineRect.cleanup(loader);
        this.outlineRect = LineStrip.processVerts(LineStrip.getRectangle(size), loader, window);
    }

    @Override
    public void hoverCursor() {
        Cursors.setCursor(ECursor.hand2);
    }
}
