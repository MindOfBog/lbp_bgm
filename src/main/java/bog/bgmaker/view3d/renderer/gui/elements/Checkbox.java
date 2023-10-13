package bog.bgmaker.view3d.renderer.gui.elements;

import bog.bgmaker.view3d.ObjectLoader;
import bog.bgmaker.view3d.core.Model;
import bog.bgmaker.view3d.managers.MouseInput;
import bog.bgmaker.view3d.managers.RenderMan;
import bog.bgmaker.view3d.managers.WindowMan;
import bog.bgmaker.view3d.renderer.gui.ingredients.LineStrip;
import bog.bgmaker.view3d.utils.Config;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

/**
 * @author Bog
 */
public class Checkbox extends Element{

    String text;
    int fontSize;

    public boolean isChecked = false;

    Vector2f prevSize;
    Model outlineRect;

    public Checkbox(String id, String text, Vector2f pos, int fontSize, RenderMan renderer, ObjectLoader loader, WindowMan window)
    {
        this.id = id;
        this.pos = pos;
        this.fontSize = fontSize;
        this.renderer = renderer;
        this.loader = loader;
        this.window = window;
        this.text = text;
        this.size = new Vector2f(getStringWidth(text, fontSize) + (getFontHeight(fontSize) * 0.85f) * 1.25f, getFontHeight(fontSize));
        this.prevSize = size;
        this.outlineRect = LineStrip.processVerts(LineStrip.getRectangle(new Vector2f(size.y * 0.85f, size.y * 0.85f)), loader, window);
    }

    public Checkbox checked()
    {
        this.isChecked = true;
        return this;
    }

    @Override
    public void draw(MouseInput mouseInput, boolean overElement) {
        super.draw(mouseInput, overElement);

        if(size.x != prevSize.x || size.y != prevSize.y)
        {
            refreshOutline();
            prevSize = size;
        }

        renderer.drawRect(Math.round(pos.x + (size.y * 0.10f)/2), Math.round(pos.y + (size.y * 0.10f)/2), Math.round(size.y * 0.85f), Math.round(size.y * 0.85f), isMouseOverElement(mouseInput) ? Config.INTERFACE_SECONDARY_COLOR : Config.INTERFACE_PRIMARY_COLOR);
        renderer.drawRectOutline(new Vector2f(Math.round(pos.x + (size.y * 0.10f)/2), Math.round(pos.y + (size.y * 0.10f)/2)), outlineRect, isMouseOverElement(mouseInput) ? Config.INTERFACE_SECONDARY_COLOR2 : Config.INTERFACE_PRIMARY_COLOR2, false);

        if(isChecked)
            renderer.drawRect(Math.round(pos.x + (size.y * 0.85f * 0.275f)), Math.round(pos.y + (size.y * 0.85f * 0.275f)), Math.round(size.y * 0.85f * 0.45f), Math.round(size.y * 0.85f * 0.45f), Config.FONT_COLOR);

        renderer.drawString(text, Config.FONT_COLOR, Math.round(pos.x + size.y * 1.25f), Math.round(pos.y + size.y / 2 - getFontHeight(fontSize) / 2), fontSize);

    }
    @Override
    public void resize() {
        super.resize();
        refreshOutline();
    }
    @Override
    public void onClick(Vector2d pos, int button, int action, int mods, boolean overElement) {
        super.onClick(pos, button, action, mods, overElement);

        if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos))
            this.isChecked = !this.isChecked;
    }

    public void refreshOutline()
    {
        if(this.outlineRect != null)
            this.outlineRect.cleanup();
        this.outlineRect = LineStrip.processVerts(LineStrip.getRectangle(new Vector2f(Math.round(size.y * 0.85f), Math.round(size.y * 0.85f))), loader, window);
    }
}
