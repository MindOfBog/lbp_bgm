package bog.lbpas.view3d.renderer.gui.elements;

import bog.lbpas.view3d.managers.MouseInput;
import bog.lbpas.view3d.managers.RenderMan;
import org.joml.Vector2d;
import org.joml.Vector2f;

import java.util.ArrayList;

/**
 * @author Bog
 */
public class Panel extends Element{

    public ArrayList<PanelElement> elements;
    float prevSizeX = -1;

    public Panel(Vector2f size, RenderMan renderer) {
        this.pos = new Vector2f();
        this.size = size;
        this.renderer = renderer;
        this.elements = new ArrayList<>();
    }

    public Panel(Vector2f pos, Vector2f size, RenderMan renderer) {
        this.pos = pos;
        this.size = size;
        this.renderer = renderer;
        this.elements = new ArrayList<>();
    }

    @Override
    public void draw(MouseInput mouseInput, boolean overElement) {
        super.draw(mouseInput, overElement);

        if(prevSizeX != this.size.x)
        {
            for(PanelElement e : elements)
            {
                if(e.element.size == null)
                    e.element.size = new Vector2f();
                e.element.size.x = e.width * this.size.x;
                e.element.size.y = this.size.y;
                e.element.resize();
            }
            prevSizeX = this.size.x;
        }

        float xOffset = 0;

        for(PanelElement e : elements)
        {
            if(e.element.pos == null)
                e.element.pos = new Vector2f();
            if(pos == null)
                pos = new Vector2f();
            e.element.pos.x = Math.round(pos.x + xOffset);
            xOffset += size.x * e.width;
            e.element.pos.y = pos.y;
            e.element.draw(mouseInput, overElement);
        }
    }

    @Override
    public void resize() {
        super.resize();

        for(PanelElement e : elements)
        {
            if(e.element.size == null)
                e.element.size = new Vector2f();
            e.element.size.x = e.width * this.size.x;
            e.element.size.y = this.size.y;
            e.element.resize();
        }
        prevSizeX = this.size.x;
    }

    @Override
    public boolean isMouseOverElement(Vector2f mousePos) {
        boolean overElement = pos != null && mousePos.x > pos.x && mousePos.y > pos.y && mousePos.x < pos.x + size.x && mousePos.y < pos.y + size.y;

        for(PanelElement e : elements)
            if(e.element.isMouseOverElement(mousePos))
                overElement = true;

        return overElement;
    }

    public boolean isMouseOverPanel(Vector2f mousePos) {

        boolean overElement = pos != null && mousePos.x > pos.x && mousePos.y > pos.y && mousePos.x < pos.x + size.x && mousePos.y < pos.y + size.y;

        for(PanelElement e : elements)
            if(e.element instanceof ComboBox || e.element instanceof ComboBoxImage ? ((ComboBox)e.element).isMouseOverTab(mousePos) : e.element.isMouseOverElement(mousePos))
                overElement = true;

        return overElement;
    }

    public boolean isMouseOverPanel(Vector2d mousePos) {
        return isMouseOverPanel(new Vector2f((float) mousePos.x, (float) mousePos.y));
    }

    public boolean isMouseOverPanel(MouseInput mouseInput) {
        return isMouseOverPanel(mouseInput.currentPos);
    }

    @Override
    public void secondThread() {
        super.secondThread();

        for(PanelElement e : elements)
            e.element.secondThread();
    }

    @Override
    public void onChar(int codePoint, int modifiers) {
        super.onChar(codePoint, modifiers);

        for(PanelElement e : elements)
            e.element.onChar(codePoint, modifiers);
    }

    @Override
    public void onMouseScroll(Vector2d pos, double xOffset, double yOffset) {
        super.onMouseScroll(pos, xOffset, yOffset);

        for(PanelElement e : elements)
            e.element.onMouseScroll(pos, xOffset, yOffset);
    }

    @Override
    public void onMouseMove(MouseInput mouseInput, double x, double y, boolean overElement) {
        super.onMouseMove(mouseInput, x, y, overElement);

        for(PanelElement e : elements)
            e.element.onMouseMove(mouseInput, x, y, overElement);
    }

    @Override
    public void onKey(int key, int scancode, int action, int mods) {
        super.onKey(key, scancode, action, mods);

        for(PanelElement e : elements)
            e.element.onKey(key, scancode, action, mods);
    }

    @Override
    public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement, boolean focusedOther) {
        super.onClick(mouseInput, pos, button, action, mods, overElement, focusedOther);

        for(PanelElement e : elements)
        {
            e.element.onClick(mouseInput, pos, button, action, mods, overElement, focusedOther);
            if(e.element.isMouseOverElement(pos))
                overElement = true;
        }
    }

    @Override
    public boolean isFocused() {

        boolean focused = super.isFocused();
        for(PanelElement element : elements)
            if(element.element.isFocused())
                focused = true;

        return focused;
    }

    @Override
    public void setFocused(boolean focused) {

        if(!focused)
            for(PanelElement element : elements)
                element.element.setFocused(false);

        super.setFocused(focused);
    }

    public void collapsed(boolean collapsed)
    {
        for(PanelElement element : elements)
            if(element.element instanceof ComboBox || element.element instanceof ComboBoxImage)
                ((ComboBox)element.element).collapsed(collapsed);
    }

    public static class PanelElement
    {
        public Element element;
        public float width;

        public PanelElement(Element element, float width) {
            this.element = element == null ? new Element(): element;
            this.width = width;
        }
    }
}
