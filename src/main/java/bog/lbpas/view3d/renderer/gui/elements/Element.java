package bog.lbpas.view3d.renderer.gui.elements;

import bog.lbpas.view3d.managers.assetLoading.ObjectLoader;
import bog.lbpas.view3d.managers.MouseInput;
import bog.lbpas.view3d.managers.RenderMan;
import bog.lbpas.view3d.managers.WindowMan;
import bog.lbpas.view3d.renderer.gui.cursor.ECursor;
import bog.lbpas.view3d.renderer.gui.font.FontRenderer;
import bog.lbpas.view3d.utils.Cursors;
import org.joml.Vector2d;
import org.joml.Vector2f;

/**
 * @author Bog
 */
public class Element {

    public Vector2f pos;
    public Vector2f size;
    private boolean focused = false;
    public boolean hovering = false;
    public String id;
    public RenderMan renderer;
    public ObjectLoader loader;
    public WindowMan window;

    Vector2f prevSize;

    public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement, boolean focusedElement){}
    public void onKey(int key, int scancode, int action, int mods){}
    public void onChar(int codePoint, int modifiers){}
    public void onMouseScroll(Vector2d pos, double xOffset, double yOffset){}
    public void onMouseMove(MouseInput mouseInput, double x, double y, boolean overElement){}
    public void draw(MouseInput mouseInput, boolean overElement)
    {
        hovering = isMouseOverElement(mouseInput) && !overElement;
        if(hovering)
            hoverCursor();

        if(prevSize == null || prevSize.x != size.x || prevSize.y != size.y)
        {
            resize();
            prevSize = new Vector2f(size);
        }
    }
    public void resize(){}

    public void secondThread(){}

    public void hoverCursor()
    {
        Cursors.setCursor(ECursor.left_ptr);
    }

    public boolean isMouseOverElement(Vector2f mousePos)
    {
        try{
            return mousePos.x > pos.x && mousePos.y > pos.y && mousePos.x < pos.x + size.x && mousePos.y < pos.y + size.y;
        }catch (Exception e){}
        return false;
    }

    public boolean isMouseOverElement(Vector2d mousePos)
    {
        return isMouseOverElement(new Vector2f((float) mousePos.x, (float) mousePos.y));
    }

    public boolean isMouseOverElement(MouseInput mouseInput)
    {
        return isMouseOverElement(mouseInput.currentPos);
    }

    protected int getStringWidth(String text, int size)
    {
        return (int)FontRenderer.getStringWidth(text, size);
    }

    protected int getFontHeight(int size)
    {
        return (int)FontRenderer.getFontHeight(size);
    }

    public boolean isFocused()
    {
        return focused;
    }

    public void setFocused(boolean focused)
    {
        this.focused = focused;
    }
}
