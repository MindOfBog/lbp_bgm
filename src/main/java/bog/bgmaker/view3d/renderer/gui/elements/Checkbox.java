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
public class Checkbox extends Element{

    String text;
    int fontSize;

    public boolean isChecked = false;

    public Checkbox(String id, String text, Vector2f pos, int fontSize, RenderMan renderer, ObjectLoader loader, WindowMan window)
    {
        this.id = id;
        this.pos = pos;
        this.fontSize = fontSize;
        this.renderer = renderer;
        this.loader = loader;
        this.window = window;
        this.text = text;
        this.size = new Vector2f(getStringWidth(text, fontSize) + (getFontHeight(fontSize) * 0.90f) * 1.25f, getFontHeight(fontSize));
    }

    public Checkbox checked()
    {
        this.isChecked = true;
        return this;
    }

    @Override
    public void draw(MouseInput mouseInput, boolean overElement) {
        super.draw(mouseInput, overElement);

        drawRect((int) Math.floor(pos.x + (size.y * 0.10f)/2), (int) Math.floor(pos.y + (size.y * 0.10f)/2), (int) Math.floor(size.y * 0.90f), (int) Math.floor(size.y * 0.90f), isMouseOverElement(mouseInput) ? Const.INTERFACE_SECONDARY_COLOR : Const.INTERFACE_PRIMARY_COLOR);
        drawRectOutline((int) Math.floor(pos.x + (size.y * 0.10f)/2), (int) Math.floor(pos.y + (size.y * 0.10f)/2), (int) Math.floor(size.y * 0.90f), (int) Math.floor(size.y * 0.90f), isMouseOverElement(mouseInput) ? Const.INTERFACE_SECONDARY_COLOR : Const.INTERFACE_PRIMARY_COLOR, false);

        if(isChecked)
            drawRect((int) Math.floor(pos.x + (size.y * 0.90f * 0.275f)), (int) Math.floor(pos.y + (size.y * 0.90f * 0.275f)), (int) Math.floor(size.y * 0.90f * 0.45f), (int) Math.floor(size.y * 0.90f * 0.45f), Const.FONT_COLOR);

        drawString(text, Const.FONT_COLOR, (int) (pos.x + (size.y * 0.90f) * 1.25f), (int) (pos.y + size.y / 2 - getFontHeight(fontSize) / 2), fontSize);

    }

    @Override
    public void onClick(Vector2d pos, int button, int action, int mods, boolean overElement) {
        super.onClick(pos, button, action, mods, overElement);

        if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos))
            this.isChecked = !this.isChecked;
    }
}
