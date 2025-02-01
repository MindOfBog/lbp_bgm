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

/**
 * @author Bog
 */
public class Checkbox extends Element{

    String text;
    int fontSize;

    public boolean isChecked = false;

    Vector2f prevSize;
    Model outlineRect;

    public Checkbox(){}

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

    public Checkbox(String id, String text, int fontSize, RenderMan renderer, ObjectLoader loader, WindowMan window)
    {
        this.id = id;
        this.fontSize = fontSize;
        this.renderer = renderer;
        this.loader = loader;
        this.window = window;
        this.text = text;
        this.size = new Vector2f(getStringWidth(text, fontSize) + (getFontHeight(fontSize) * 0.85f) * 1.25f, getFontHeight(fontSize));
        this.prevSize = size;
        this.outlineRect = LineStrip.processVerts(LineStrip.getRectangle(new Vector2f(size.y * 0.85f, size.y * 0.85f)), loader, window);
    }

    public Checkbox(String id, String text)
    {
        this.id = id;
        this.text = text;
    }

    public Checkbox checked()
    {
        this.isChecked = true;
        return this;
    }

    @Override
    public void draw(MouseInput mouseInput, boolean overElement) {
        super.draw(mouseInput, overElement);

        if(size == null || prevSize == null)
        {
            this.size = new Vector2f(getStringWidth(text, fontSize) + (getFontHeight(fontSize) * 0.85f) * 1.25f, getFontHeight(fontSize));
            this.prevSize = size;
        }

        if(size.x != prevSize.x || size.y != prevSize.y)
        {
            refreshOutline();
            prevSize = size;
        }

        int posX = Math.round(pos.x + (size.y * 0.10f)/2);
        int posY = Math.round(pos.y + (size.y * 0.10f)/2);
        int radius = Math.round(size.y * 0.85f);

        renderer.drawRect(posX, posY, radius, radius, isMouseOverElement(mouseInput) && !overElement ? Config.INTERFACE_SECONDARY_COLOR : Config.INTERFACE_PRIMARY_COLOR);
        renderer.drawRectOutline(new Vector2f(posX, posY), outlineRect, isMouseOverElement(mouseInput) && !overElement ? Config.INTERFACE_SECONDARY_COLOR2 : Config.INTERFACE_PRIMARY_COLOR2, false);

        if(isChecked)
        {
            float checkSize = radius * 0.35f;
            while(Math.round(radius - checkSize) % 2 != 0)
                checkSize = checkSize - 1;
            renderer.drawRect(Math.round(posX + (radius/2f - checkSize / 2f)), Math.round(posY + (radius/2f - checkSize / 2f)), Math.round(checkSize), Math.round(checkSize), Config.FONT_COLOR);
        }

        renderer.drawString(text, Config.FONT_COLOR, Math.round(pos.x + size.y * 1.25f), Math.round(pos.y + size.y / 2 - getFontHeight(fontSize) / 2), fontSize);
    }
    @Override
    public void resize() {
        super.resize();
        refreshOutline();
    }
    @Override
    public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement) {
        super.onClick(mouseInput, pos, button, action, mods, overElement);

        if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos) && !overElement)
            this.isChecked = !this.isChecked;
    }

    public void refreshOutline()
    {
        if(this.outlineRect != null)
            this.outlineRect.cleanup(loader);
        if(size != null)
            this.outlineRect = LineStrip.processVerts(LineStrip.getRectangle(new Vector2f(Math.round(size.y * 0.85f), Math.round(size.y * 0.85f))), loader, window);
    }

    @Override
    public void hoverCursor() {
        Cursors.setCursor(ECursor.hand2);
    }
}
