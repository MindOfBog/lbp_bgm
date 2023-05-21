package bog.bgmaker.view3d.renderer.gui.elements;

import bog.bgmaker.view3d.ObjectLoader;
import bog.bgmaker.view3d.managers.MouseInput;
import bog.bgmaker.view3d.managers.RenderMan;
import bog.bgmaker.view3d.managers.WindowMan;
import bog.bgmaker.view3d.utils.Const;
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

        drawCircle(new Vector2f((pos.x + (size.y * 0.10f)/2) + (size.y * 0.90f) / 2f, (pos.y + (size.y * 0.10f)/2) + (size.y * 0.90f) / 2f), (size.y * 0.90f) / 2f, 16, isMouseOverElement(mouseInput) ? Const.INTERFACE_SECONDARY_COLOR : Const.INTERFACE_PRIMARY_COLOR);

        if(isChecked)
            drawCircle(new Vector2f((pos.x + (size.y * 0.10f)/2) + (size.y * 0.90f) / 2f, (pos.y + (size.y * 0.10f)/2) + (size.y * 0.90f) / 2f), (size.y * 0.90f) / 5f, 16, Const.FONT_COLOR);

        drawString(text, Const.FONT_COLOR, (int) (pos.x + (size.y * 0.90f) * 1.25f), (int) (pos.y + size.y / 2 - getFontHeight(fontSize) / 2), fontSize);
    }

    public Radiobutton checked()
    {
        this.isChecked = true;
        return this;
    }
}
