package bog.bgmaker.view3d.renderer.gui.elements;

import bog.bgmaker.view3d.ObjectLoader;
import bog.bgmaker.view3d.managers.MouseInput;
import bog.bgmaker.view3d.managers.RenderMan;
import bog.bgmaker.view3d.managers.WindowMan;
import bog.bgmaker.view3d.renderer.gui.cursor.ECursor;
import bog.bgmaker.view3d.utils.Config;
import bog.bgmaker.view3d.utils.Cursors;
import org.joml.Vector2f;

/**
 * @author Bog
 */
public class Radiobutton extends Checkbox{

    public Radiobutton(String id, String text, Vector2f pos, int fontSize, RenderMan renderer, ObjectLoader loader, WindowMan window)
    {
        super(id, text, pos, fontSize, renderer, loader, window);
    }

    @Override
    public void draw(MouseInput mouseInput, boolean overElement) {
        hovering = isMouseOverElement(mouseInput) && !overElement;
        if(hovering)
            hoverCursor();

        renderer.drawCircle(loader, new Vector2f((pos.x + (size.y * 0.10f)/2) + (size.y * 0.90f) / 2f, (pos.y + (size.y * 0.10f)/2) + (size.y * 0.90f) / 2f), (size.y * 0.90f) / 2f, 16, hovering ? Config.INTERFACE_SECONDARY_COLOR : Config.INTERFACE_PRIMARY_COLOR);
        renderer.drawCircleOutline(loader, new Vector2f((pos.x + (size.y * 0.10f)/2) + (size.y * 0.90f) / 2f, (pos.y + (size.y * 0.10f)/2) + (size.y * 0.90f) / 2f), (size.y * 0.90f) / 2f, 16, hovering ? Config.INTERFACE_SECONDARY_COLOR2 : Config.INTERFACE_PRIMARY_COLOR2);

        if(isChecked)
            renderer.drawCircle(loader, new Vector2f((pos.x + (size.y * 0.10f)/2) + (size.y * 0.90f) / 2f, (pos.y + (size.y * 0.10f)/2) + (size.y * 0.90f) / 2f), (size.y * 0.90f) / 5f, 16, Config.FONT_COLOR);

        renderer.drawString(text, Config.FONT_COLOR, (int) (pos.x + size.y* 1.3f), (int) (pos.y + size.y * 0.5335 - getFontHeight(fontSize) / 2), fontSize);
    }

    public Radiobutton checked()
    {
        this.isChecked = true;
        return this;
    }

    @Override
    public void hoverCursor() {
        Cursors.setCursor(ECursor.hand2);
    }
}
