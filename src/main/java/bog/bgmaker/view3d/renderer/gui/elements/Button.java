package bog.bgmaker.view3d.renderer.gui.elements;

import bog.bgmaker.view3d.ObjectLoader;
import bog.bgmaker.view3d.managers.MouseInput;
import bog.bgmaker.view3d.managers.RenderMan;
import bog.bgmaker.view3d.managers.WindowMan;
import bog.bgmaker.view3d.utils.Config;
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

    public Button()
    {}

    public Button(String id)
    {
        this.id = id;
    }

    public Button(String id, String buttonText, Vector2f pos, Vector2f size, int fontSize, RenderMan renderer, ObjectLoader loader, WindowMan window)
    {
        this.id = id;
        this.pos = pos;
        this.size = size;
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
        this.fontSize = fontSize;
        this.renderer = renderer;
        this.loader = loader;
        this.window = window;
        this.buttonText = buttonText;
    }

    @Override
    public void draw(MouseInput mouseInput, boolean overOther) {
        super.draw(mouseInput, overOther);

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

        startScissor((int)pos.x, (int)pos.y, (int)size.x, (int)size.y);
        drawRect((int)pos.x, (int)pos.y, (int)size.x, (int)size.y, c);
        drawString(buttonText, Config.FONT_COLOR, (int)(pos.x + size.x / 2 - getStringWidth(buttonText, fontSize) / 2), (int)(pos.y + size.y / 2 - getFontHeight(fontSize) / 2), fontSize);
        drawRectOutline((int)pos.x, (int)pos.y, (int)size.x, (int)size.y, c2, false);
        endScissor();
    }

    public void setClicked(boolean clicked) {
        isClicked = clicked;
    }

    @Override
    public void onClick(Vector2d pos, int button, int action, int mods, boolean overOther) {

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

        super.onClick(pos, button, action, mods, overOther);
    }

    public abstract void clickedButton(int button, int action, int mods);

}
