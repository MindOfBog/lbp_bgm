package bog.lbpas.view3d.renderer.gui.elements;

import bog.lbpas.view3d.core.Model;
import bog.lbpas.view3d.managers.MouseInput;
import bog.lbpas.view3d.managers.RenderMan;
import bog.lbpas.view3d.managers.WindowMan;
import bog.lbpas.view3d.managers.assetLoading.ObjectLoader;
import bog.lbpas.view3d.renderer.gui.ingredients.Line;
import bog.lbpas.view3d.renderer.gui.ingredients.LineStrip;
import bog.lbpas.view3d.utils.Config;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayList;

/**
 * @author Bog
 */
public class ElementList extends Element{

    public ArrayList<Element> elements;
    public int fontSize;

    float yScroll = 3;
    boolean scrolling = false;

    Vector2f prevSize = new Vector2f();
    Model outlineSelection;
    public Model outlineScrollbar;
    public Model listLine;
    public int lineOffset;

    public ElementList(String id, Vector2f pos, Vector2f size, int fontSize, RenderMan renderer, ObjectLoader loader, WindowMan window)
    {
        this.id = id;
        this.pos = pos;
        this.size = size;
        this.fontSize = fontSize;
        this.renderer = renderer;
        this.loader = loader;
        this.window = window;
        elements = new ArrayList<>();
    }

    public float getFullHeight()
    {
        float fullSize = 0;

        for(Element e : elements)
            fullSize += e.size.y + 2;

        return fullSize;
    }

    public Element getElementByID(String id)
    {
        for(Element element : elements)
            if(element.id.equalsIgnoreCase(id))
                return element;
        return null;
    }

    public void removeElementByID(String id)
    {
        for(int i = 0; i < elements.size(); i++)
            if(elements.get(i).id.equalsIgnoreCase(id))
                elements.remove(i);
    }

    public boolean containsElementByID(String id)
    {
        for(int i = 0; i < elements.size(); i++)
            if(elements.get(i).id.equalsIgnoreCase(id))
                return true;
        return false;
    }

    public Button addButton(String id, String buttonText, Button button)
    {
        if(!containsElementByID(id))
        {
            button.id = id;
            button.buttonText = buttonText;
            button.pos = new Vector2f(0, 0);
            if(button.size == null)
                button.size = new Vector2f(size.x - 4, getFontHeight(fontSize) + 4);
            else
                button.size.x = size.x - 4;
            button.fontSize = fontSize;
            button.renderer = renderer;
            button.window = window;
            button.loader = loader;
            elements.add(button);
        }
        return button;
    }

    public ElementList addElementList(ElementList elementList)
    {
        if(!containsElementByID(elementList.id))
        {
            elementList.pos = new Vector2f(0, 0);
            if(elementList.size == null)
                elementList.size = new Vector2f(this.size.x - 4, getFontHeight(fontSize) + 4);
            else
                elementList.size.x = this.size.x - 4;
            elementList.fontSize = fontSize;
            elementList.renderer = renderer;
            elementList.window = window;
            elementList.loader = loader;
            elements.add(elementList);
            return elementList;
        }
        else
            return (ElementList) getElementByID(elementList.id);
    }

    public ComboBox addComboBox(String id, String title, int tabWidth)
    {
        if(!containsElementByID(id))
        {
            ElementList elList = this;
            ComboBox comboBox = new ComboBox(id, title, new Vector2f(0, 0), new Vector2f(size.x - 4, getFontHeight(fontSize) + 4), fontSize, tabWidth, renderer, loader, window)
            {
                @Override
                public int[] getParentTransform() {
                    return elList.getParentTransform();
                }
            };
            elements.add(comboBox);
            return comboBox;
        }
        else
            return (ComboBox) getElementByID(id);
    }

    public Checkbox addCheckbox(String id, String text)
    {
        if(!containsElementByID(id))
        {
            Checkbox cb = new Checkbox(id, text, new Vector2f(0, 0), fontSize, renderer, loader, window);
            elements.add(cb);
            return cb;
        }
        return null;
    }

    public ElementList addElementList(String id)
    {
        if(!containsElementByID(id))
        {
            ElementList elementList = new ElementList(id, new Vector2f(0, 0), new Vector2f(this.size.x - 4, getFontHeight(fontSize) + 4), fontSize, renderer, loader, window);
            elements.add(elementList);
            return elementList;
        }
        else
            return (ElementList) getElementByID(id);
    }

    public Checkbox addCheckbox(String id, String text, Checkbox cb)
    {
        if(!containsElementByID(id))
        {
            cb.id = id;
            cb.text = text;
            cb.pos = new Vector2f(0, 0);
            cb.fontSize = fontSize;
            cb.renderer = renderer;
            cb.loader = loader;
            cb.window = window;
            elements.add(cb);
            return cb;
        }
        return null;
    }

    public Checkbox addCheckbox(Checkbox cb)
    {
        if(!containsElementByID(id))
        {
            cb.fontSize = fontSize;
            cb.renderer = renderer;
            cb.loader = loader;
            cb.window = window;
            cb.size = new Vector2f(getStringWidth(cb.text, cb.fontSize) + (getFontHeight(cb.fontSize) * 0.85f) * 1.25f, getFontHeight(cb.fontSize));
            cb.prevSize = cb.size;
            cb.outlineRect = LineStrip.processVerts(LineStrip.getRectangle(new Vector2f(cb.size.y * 0.85f, cb.size.y * 0.85f)), cb.loader, cb.window);
            elements.add(cb);
            return cb;
        }
        return null;
    }

    public Checkbox addCheckbox(String id, String text, boolean checked)
    {
        if(!containsElementByID(id))
        {
            Checkbox checkbox = new Checkbox(id, text, new Vector2f(0, 0), fontSize, renderer, loader, window);
            checkbox.isChecked = checked;
            elements.add(checkbox);
            return checkbox;
        }
        return null;
    }

    public void addSlider(String id)
    {
        if(!containsElementByID(id))
            elements.add(new Slider(id, new Vector2f(0, 0), new Vector2f(size.x - 4, getFontHeight(fontSize) + 4), renderer, loader, window));
    }

    public void addSlider(String id, float sliderPosition, float min, float max)
    {
        if(!containsElementByID(id))
            elements.add(new Slider(id, new Vector2f(0, 0), new Vector2f(size.x - 4, getFontHeight(fontSize) + 4), renderer, loader, window, sliderPosition, min, max));
    }

    public void addTextbox(String id)
    {
        if(!containsElementByID(id))
            elements.add(new Textbox(id, new Vector2f(0, 0), new Vector2f(size.x - 4, getFontHeight(fontSize) + 4), fontSize, renderer, loader, window));
    }

    public void addTextbox(String id, boolean numbers, boolean letters, boolean others)
    {
        if(!containsElementByID(id))
        {
            Textbox tb = new Textbox(id, new Vector2f(0, 0), new Vector2f(size.x - 4, getFontHeight(fontSize) + 4), fontSize, renderer, loader, window);
            tb.numbers = numbers;
            tb.letters = letters;
            tb.others = others;
            elements.add(tb);
        }
    }

    public void addTextbox(String id, String text)
    {
        if(!containsElementByID(id))
        {
            Textbox tb = new Textbox(id, new Vector2f(0, 0), new Vector2f(size.x - 4, getFontHeight(fontSize) + 4), fontSize, renderer, loader, window);
            tb.setText(text);
            elements.add(tb);
        }
    }

    public void addTextbox(String id, boolean numbers, boolean letters, boolean others, String text)
    {
        if(!containsElementByID(id))
        {
            Textbox tb = new Textbox(id, new Vector2f(0, 0), new Vector2f(size.x - 4, getFontHeight(fontSize) + 4), fontSize, renderer, loader, window);
            tb.numbers = numbers;
            tb.letters = letters;
            tb.others = others;
            tb.setText(text);
            elements.add(tb);
        }
    }

    public Panel addPanel(String id)
    {
        if(!containsElementByID(id))
        {
            Panel p = new Panel(new Vector2f(size.x - 4, getFontHeight(fontSize) + 4), renderer);
            p.id = id;
            elements.add(p);
            return p;
        }
        else
            return (Panel) getElementByID(id);
    }

    public Panel addPanel(Panel p)
    {
        if(!containsElementByID(p.id))
        {
            if(p.pos == null)
                p.pos = new Vector2f();
            if(p.size == null)
                p.size = new Vector2f(0, getFontHeight(fontSize) + 4);
            p.size.x = size.x - 4;
            if(p.size.y == 0)
                p.size.y = getFontHeight(fontSize) + 4;
            if(p.id == null)
                p.id = this.id + "-" + (int)(Math.random() * 1000000);
            elements.add(p);
            return p;
        }
        else
            return (Panel) getElementByID(p.id);
    }

    public void addString(String id, String string)
    {
        if(!containsElementByID(id))
            elements.add(new DropDownTab.StringElement(id, string, fontSize, renderer));
    }

    public void addRect(String id, float height)
    {
        if(!containsElementByID(id))
            elements.add(new DropDownTab.RectangleElement(id, height, renderer, loader, window));
    }

    public void addRect(String id, float height, Color color)
    {
        if(!containsElementByID(id))
            elements.add(new DropDownTab.RectangleElement(id, height, color, renderer, loader, window));
    }

    public void addList(String id, ButtonList buttonList, int height)
    {
        if(!containsElementByID(id))
        {
            buttonList.id = id;
            buttonList.pos = new Vector2f(0, 0);
            buttonList.size = new Vector2f(size.x - 4, height);
            elements.add(buttonList);
        }
    }

    public DropDownTab.SeparatorElement addSeparator(String id)
    {
        if(!containsElementByID(id))
        {
            DropDownTab.SeparatorElement sep = new DropDownTab.SeparatorElement(id, new Vector2f(0), size.x - 20, 10, renderer, loader, window);
            elements.add(sep);
            return sep;
        }
        else
            return (DropDownTab.SeparatorElement) getElementByID(id);
    }

    float prevYOff = -1;

    @Override
    public void draw(MouseInput mouseInput, boolean overOther) {
        super.draw(mouseInput, overOther);

        float yOffset = 0;

        float maxScroll = getFullHeight() + 5;
        float frac = size.x - (size.x - 4f - size.x * 0.05f);
        float scrollX = pos.x + size.x - 4f - size.x * 0.05f + (frac / 2f);
        float scrollY = pos.y + 3;
        float scrollHeight = size.y - 6;

        if(scrolling)
            yScroll = -(((((float)mouseInput.currentPos.y) - (frac/2)) - scrollY)/(scrollHeight - frac)) * (maxScroll - size.y);

        if(yScroll < size.y - maxScroll)
            yScroll = size.y - maxScroll;

        if(yScroll > 3)
            yScroll = 3;

        yOffset = updateElements(yOffset);

        if(size.x != prevSize.x || size.y != prevSize.y || prevYOff != yOffset)
        {
            refreshOutline(yOffset);
            prevSize.x = size.x;
            prevSize.y = size.y;
            prevYOff = yOffset;
        }

        renderer.drawRect((int) scrollX, (int) scrollY, 3, (int) scrollHeight, Config.INTERFACE_PRIMARY_COLOR);
        renderer.drawRectOutline(new Vector2f((int) scrollX, (int) scrollY), outlineScrollbar, Config.INTERFACE_PRIMARY_COLOR2, false);
        renderer.drawRect((int) scrollX, (int) (scrollY + (((yScroll * -1 + 3) / (maxScroll - size.y + 3)) * (scrollHeight - (int) frac))), 3, (int) frac, Config.FONT_COLOR);

        renderer.drawLine(listLine, new Vector2f((int) this.pos.x + 2 + lineOffset, (int) this.pos.y + 2), Config.INTERFACE_PRIMARY_COLOR2, false);
        renderer.drawLine(listLine, new Vector2f((int) this.pos.x + 2 + lineOffset, (int) (this.pos.y + this.size.y - 3)), Config.INTERFACE_PRIMARY_COLOR2, false);

        renderer.startScissor((int) pos.x, (int) scrollY, (int) size.x, (int) Math.ceil(scrollHeight));
        drawElements(mouseInput, overOther);
        renderer.endScissor();
    }

    @Override
    public void resize() {
        super.resize();

        for(int i = 0; i < elements.size(); i++)
            elements.get(i).resize();

        float yOffset = 0;
        yOffset = updateElements(yOffset);
        refreshOutline(yOffset);
    }

    public void refreshOutline(float yOffset)
    {
        if(outlineScrollbar != null)
            this.outlineScrollbar.cleanup(loader);
        if(outlineSelection != null)
            this.outlineSelection.cleanup(loader);
        if(listLine != null)
            this.listLine.cleanup(loader);
        this.outlineScrollbar = LineStrip.processVerts(LineStrip.getRectangle(new Vector2f(3, size.y - 6)), loader, window);
        this.outlineSelection = LineStrip.processVerts(LineStrip.getRectangle(size), loader, window);
        this.listLine = Line.getLine(window, loader, new Vector2i(0), new Vector2i((int) (size.x - 2.0f - size.x * 0.05f), 0));
    }

    public void drawElements(MouseInput mouseInput, boolean overOther)
    {
        MouseInput dummyMI = new MouseInput(null);
        dummyMI.currentPos = new Vector2d(-9999);

        for(int i = 0; i < elements.size(); i++)
        {
            Element element = elements.get(i);

            if(element.pos.y < this.pos.y + this.size.y &&
                element.pos.y + element.size.y > this.pos.y)
                element.draw(((element instanceof ComboBox ? ((ComboBox)element).isMouseOverTab(mouseInput) : element instanceof Panel ? ((Panel)element).isMouseOverPanel(mouseInput) : element.isMouseOverElement(mouseInput)) && (mouseInput.currentPos.y > this.pos.y + this.size.y || mouseInput.currentPos.y < this.pos.y)) ? dummyMI : mouseInput, overOther);
        }
    }

    public float updateElements(float yOffset)
    {
        float frac = size.x - (size.x - size.x * 0.05f);

        for (int i = 0; i < elements.size(); i++)
        {
            Element element = elements.get(i);
            element.pos = new Vector2f(Math.round(pos.x + (element instanceof ButtonList ? 0 : 2) + 2), Math.round(pos.y + 2 + yOffset + yScroll));

            if (element.size == null)
                element.size = new Vector2f(size.x - 2.0f - (element instanceof ButtonList ? 0 : 4) - frac, getFontHeight(fontSize) + 4);
            else
                element.size.x = size.x - 2.0f - (element instanceof ButtonList ? 0 : 4) - frac;

            yOffset += element.size.y + 2;

            if(!(element.pos.y < this.pos.y + this.size.y &&
                    element.pos.y + element.size.y > this.pos.y))
                if(element instanceof ComboBox)
                    ((ComboBox)element).collapsed(true);
                else if(element instanceof Panel)
                    ((Panel)element).collapsed(true);

        }

        return yOffset;
    }

    @Override
    public void secondThread() {
        super.secondThread();

        for(Element element : elements)
            element.secondThread();
    }

    @Override
    public void onChar(int codePoint, int modifiers) {
        super.onChar(codePoint, modifiers);

        for(Element element : elements)
            element.onChar(codePoint, modifiers);
    }

    @Override
    public void onMouseScroll(Vector2d pos, double xOffset, double yOffset) {
        super.onMouseScroll(pos, xOffset, yOffset);

        for(Element element : elements)
            element.onMouseScroll(pos, xOffset, yOffset);

        if(!(pos.x >= this.pos.x && pos.x <= this.pos.x + this.size.x &&
                pos.y >= this.pos.y && pos.y <= this.pos.y + this.size.y))
            return;

        yScroll += yOffset * 50;
    }

    @Override
    public boolean isMouseOverElement(Vector2f mousePos) {
        float yOffset = 0;

        try
        {
            for(int i = 0; i < elements.size(); i++)
            {
                Element element = elements.get(i);
                yOffset += element.size.y + 2;
            }
        }catch (Exception e){}

        boolean overElement = mousePos.x > pos.x && mousePos.y > pos.y && mousePos.x < pos.x + size.x && mousePos.y < pos.y + size.y;

        for(int i = 0; i < elements.size(); i++)
            if(!((elements.get(i) instanceof ComboBox ? ((ComboBox)elements.get(i)).isMouseOverTab(mousePos) : elements.get(i) instanceof Panel ? ((Panel)elements.get(i)).isMouseOverPanel(mousePos) : elements.get(i).isMouseOverElement(mousePos)) && (mousePos.y > this.pos.y + this.size.y || mousePos.y < this.pos.y)))
                if(elements.get(i).isMouseOverElement(mousePos))
                    overElement = true;

        return overElement;
    }

    @Override
    public void onKey(int key, int scancode, int action, int mods) {
        super.onKey(key, scancode, action, mods);

        for(int i = 0; i < elements.size(); i++)
        {
            Element element = elements.get(i);
            element.onKey(key, scancode, action, mods);

            if (key == GLFW.GLFW_KEY_TAB && action == GLFW.GLFW_PRESS) {
                if (element.isFocused() && (element instanceof Textbox || element instanceof Textarea)) {

                    element.setFocused(false);

                    Element nextElement = findNextFocusableElement(i + 1);
                    if(nextElement != null)
                        nextElement.setFocused(true);
                    break;
                }
                else if(element instanceof Panel)
                {
                    Element e = findNextFocusedElement((Panel) element);
                    if(e == null)
                    {
                        Element nextElement = findNextFocusableElement(i + 1);
                        nextElement.setFocused(true);
                        break;
                    }
                }
            }
        }
    }

    private Element findNextFocusedElement(Panel panel)
    {
        for(int i = 0; i < panel.elements.size(); i++)
        {
            Element element = panel.elements.get(i).element;
            if (element.isFocused() && (element instanceof Textbox || element instanceof Textarea)) {

                element.setFocused(false);

                Element nextElement = findNextFocusableElementInPanel(panel, i + 1);

                if(nextElement != null)
                    nextElement.setFocused(true);

                return nextElement;
            }
            else if(element instanceof Panel)
            {
                Element e = findNextFocusedElement((Panel) element);

                if(e == null)
                    return findNextFocusableElementInPanel(panel);
            }
        }
        return new Element();
    }

    private Element findNextFocusableElement(int startIndex) {
        for (int i = startIndex; i < elements.size(); i++) {
            Element element = elements.get(i);

            if (element instanceof Panel) {
                Element nestedElement = findNextFocusableElementInPanel((Panel) element);
                if (nestedElement != null) {
                    return nestedElement;
                }
            }

            if (element instanceof Textbox || element instanceof Textarea) {
                return element;
            }
        }

        if(startIndex != 0)
            return findNextFocusableElement(0);
        else return null;
    }

    private Element findNextFocusableElementInPanel(Panel panel) {
        for (Panel.PanelElement panelElement : panel.elements) {
            Element nestedElement = panelElement.element;

            if (nestedElement instanceof Panel) {
                Element result = findNextFocusableElementInPanel((Panel) nestedElement);
                if (result != null) {
                    return result;
                }
            }

            if (nestedElement instanceof Textbox || nestedElement instanceof Textarea) {
                return nestedElement;
            }
        }

        return null;
    }

    private Element findNextFocusableElementInPanel(Panel panel, int startIndex) {
        for (int i = startIndex; i < panel.elements.size(); i++) {
            Element nestedElement = panel.elements.get(i).element;

            if (nestedElement instanceof Panel) {
                Element result = findNextFocusableElementInPanel((Panel) nestedElement);
                if (result != null) {
                    return result;
                }
            }

            if (nestedElement instanceof Textbox || nestedElement instanceof Textarea) {
                return nestedElement;
            }
        }

        return null;
    }

    @Override
    public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overOther, boolean focusedOther) {

        float maxScroll = getFullHeight() + 4;

        for(Element element : elements)
            if(!((element instanceof ComboBox ? ((ComboBox)element).isMouseOverTab(mouseInput) : element instanceof Panel ? ((Panel)element).isMouseOverPanel(mouseInput) : element.isMouseOverElement(mouseInput)) && (mouseInput.currentPos.y > this.pos.y + this.size.y || mouseInput.currentPos.y < this.pos.y)))
                element.onClick(mouseInput, pos, button, action, mods, overOther, focusedOther);

        boolean hoveringScroll = pos.x > this.pos.x + size.x - 4f - size.x * 0.05f &&
                pos.x < this.pos.x + size.x &&
                pos.y > this.pos.y &&
                pos.y < this.pos.y + size.y;

        if(button == GLFW.GLFW_MOUSE_BUTTON_1)
        {
            if(hoveringScroll && action == GLFW.GLFW_PRESS)
                scrolling = true;
            else if (action == GLFW.GLFW_RELEASE)
                scrolling = false;
        }

        super.onClick(mouseInput, pos, button, action, mods, overOther, focusedOther);
    }

    @Override
    public boolean isFocused() {
        boolean focused = false;
        for(int i = 0; i < elements.size(); i++)
            if(elements.get(i).isFocused())
                focused = true;

        return focused;
    }

    @Override
    public void setFocused(boolean focused) {

        if(!focused)
            for(Element e : elements)
                e.setFocused(false);

        super.setFocused(focused);
    }

    public int[] getParentTransform(){return null;}

    @Override
    public void onMouseMove(MouseInput mouseInput, double x, double y, boolean overElement) {
        super.onMouseMove(mouseInput, x, y, overElement);

        for(Element element : elements)
        {
            element.onMouseMove(mouseInput, x, y, overElement);
            if(element.isMouseOverElement(pos))
                overElement = true;
        }
    }
}