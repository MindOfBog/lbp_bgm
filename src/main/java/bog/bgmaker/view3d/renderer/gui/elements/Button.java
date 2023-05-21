package bog.bgmaker.view3d.renderer.gui.elements;

import bog.bgmaker.view3d.ObjectLoader;
import bog.bgmaker.view3d.managers.MouseInput;
import bog.bgmaker.view3d.managers.RenderMan;
import bog.bgmaker.view3d.managers.WindowMan;
import bog.bgmaker.view3d.utils.Const;
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
    {
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
            isClicked = false;

        startScissor((int)pos.x, (int)pos.y, (int)size.x, (int)size.y);
        drawRect((int)pos.x, (int)pos.y, (int)size.x, (int)size.y, (!isMouseOverElement(mouseInput) || overOther ? Const.INTERFACE_PRIMARY_COLOR : (isClicked ? Const.INTERFACE_TERTIARY_COLOR : Const.INTERFACE_SECONDARY_COLOR)));
        drawString(buttonText, Const.FONT_COLOR, (int)(pos.x + size.x / 2 - getStringWidth(buttonText, fontSize) / 2), (int)(pos.y + size.y / 2 - getFontHeight(fontSize) / 2), fontSize);
        drawRectOutline((int)pos.x, (int)pos.y, (int)size.x, (int)size.y, (!isMouseOverElement(mouseInput) || overOther ? Const.INTERFACE_PRIMARY_COLOR2 : (isClicked ? Const.INTERFACE_TERTIARY_COLOR2 : Const.INTERFACE_SECONDARY_COLOR2)), false);
        endScissor();
    }

    @Override
    public void onClick(Vector2d pos, int button, int action, int mods, boolean overOther) {

        if(!overOther)
        {
            if(isMouseOverElement(pos))
            {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)
                    isClicked = true;
                clickedButton(button, action, mods);
            }
            if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_RELEASE)
                isClicked = false;
        }

        super.onClick(pos, button, action, mods, overOther);
    }

    public abstract void clickedButton(int button, int action, int mods);

}
