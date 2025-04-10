package bog.lbpas.view3d.renderer.gui.elements;

import bog.lbpas.view3d.managers.assetLoading.ObjectLoader;
import bog.lbpas.view3d.managers.MouseInput;
import bog.lbpas.view3d.managers.RenderMan;
import bog.lbpas.view3d.managers.WindowMan;
import bog.lbpas.view3d.renderer.gui.cursor.ECursor;
import bog.lbpas.view3d.utils.Config;
import bog.lbpas.view3d.utils.Cursors;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

/**
 * @author Bog
 */
public class Radiobutton extends Checkbox{

    public Radiobutton(String id, String text, Vector2f pos, int fontSize, RenderMan renderer, ObjectLoader loader, WindowMan window)
    {
        super(id, text, pos, fontSize, renderer, loader, window);
    }

    public Radiobutton(String id, String text, int fontSize, RenderMan renderer, ObjectLoader loader, WindowMan window)
    {
        super(id, text, new Vector2f(), fontSize, renderer, loader, window);
    }

    public Radiobutton()
    {
    }

    @Override
    public void draw(MouseInput mouseInput, boolean overElement) {
        hovering = isMouseOverElement(mouseInput) && !overElement;
        if(hovering)
            hoverCursor();

        float radius = (size.y * 0.90f) / 2.65f;

        renderer.drawCircle(loader, new Vector2f((pos.x + radius * 1.25f), (pos.y + size.y/2f)), radius, hovering ? Config.INTERFACE_SECONDARY_COLOR : Config.INTERFACE_PRIMARY_COLOR);
        renderer.drawCircleOutline(loader, new Vector2f((pos.x + radius * 1.25f), (pos.y + size.y/2f)), radius, hovering ? Config.INTERFACE_SECONDARY_COLOR2 : Config.INTERFACE_PRIMARY_COLOR2);

        if(isChecked)
            renderer.drawCircle(loader, new Vector2f((pos.x + radius * 1.25f), (pos.y + size.y/2f)), (size.y * 0.90f) / 7f, Config.FONT_COLOR);

        renderer.drawString(text, Config.FONT_COLOR, (int) (pos.x + radius * 3f), (int) (pos.y + size.y * 0.5335 - getFontHeight(fontSize) / 2), fontSize);
    }

    public Radiobutton checked()
    {
        this.isChecked = true;
        onCheck();
        return this;
    }
    public Radiobutton checked(boolean checked)
    {
        this.isChecked = checked;
        onCheck();
        return this;
    }

    @Override
    public void hoverCursor() {
        Cursors.setCursor(ECursor.hand2);
    }

    @Override
    public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement, boolean focusedOther) {
        if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos) && !overElement)
            check();
    }

    public void check()
    {
        this.isChecked = !this.isChecked;
        onCheck();
    }

    public void onCheck()
    {

    }
}
