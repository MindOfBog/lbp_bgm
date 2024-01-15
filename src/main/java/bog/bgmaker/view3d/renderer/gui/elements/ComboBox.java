package bog.bgmaker.view3d.renderer.gui.elements;

import bog.bgmaker.view3d.ObjectLoader;
import bog.bgmaker.view3d.core.Model;
import bog.bgmaker.view3d.managers.MouseInput;
import bog.bgmaker.view3d.managers.RenderMan;
import bog.bgmaker.view3d.managers.WindowMan;
import bog.bgmaker.view3d.renderer.gui.cursor.ECursor;
import bog.bgmaker.view3d.renderer.gui.ingredients.LineStrip;
import bog.bgmaker.view3d.utils.Config;
import bog.bgmaker.view3d.utils.Cursors;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayList;

/**
 * @author Bog
 */
public class ComboBox extends Element{

    public String tabTitle = "";
    public ArrayList<Element> comboElements;
    public int fontSize;
    boolean extended = false;

    Vector2f prevSize = new Vector2f();
    Model outlineSelection;
    Model outlineElement;

    public ComboBox(String id, String tabTitle, Vector2f pos, Vector2f size, int fontSize, RenderMan renderer, ObjectLoader loader, WindowMan window)
    {
        this.id = id;
        this.tabTitle = tabTitle;
        this.pos = pos;
        this.size = size;
        this.fontSize = fontSize;
        this.renderer = renderer;
        this.loader = loader;
        this.window = window;
        comboElements = new ArrayList<>();
    }

    public float getFullHeight()
    {
        if(extended)
        {
            float fullSize = size.y;

            for(Element e : comboElements)
                fullSize += e.size == null ? e instanceof DropDownTab.StringElement ? getFontHeight(((DropDownTab.StringElement)e).fontSize) : 0 : e.size.y + 3;

            return fullSize;
        }
        else
            return size.y;
    }

    public Element getElementByID(String id)
    {
        for(Element element : comboElements)
            if(element.id.equalsIgnoreCase(id))
                return element;
        return null;
    }

    public void removeElementByID(String id)
    {
        for(int i = 0; i < comboElements.size(); i++)
            if(comboElements.get(i).id.equalsIgnoreCase(id))
                comboElements.remove(i);
    }

    public boolean containsElementByID(String id)
    {
        for(int i = 0; i < comboElements.size(); i++)
            if(comboElements.get(i).id.equalsIgnoreCase(id))
                return true;
        return false;
    }

    public void addButton(String id, String buttonText, Button button)
    {
        if(!containsElementByID(id))
        {
            button.id = id;
            button.buttonText = buttonText;
            button.pos = new Vector2f(0, 0);
            button.size = new Vector2f(size.x - 4, getFontHeight(fontSize) + 4);
            button.fontSize = fontSize;
            button.renderer = renderer;
            button.window = window;
            button.loader = loader;
            comboElements.add(button);
        }
    }

    public void addCheckbox(String id, String text)
    {
        if(!containsElementByID(id))
            comboElements.add(new Checkbox(id, text, new Vector2f(0, 0), fontSize, renderer, loader, window));
    }

    public void addCheckbox(String id, String text, boolean checked)
    {
        if(!containsElementByID(id))
        {
            Checkbox checkbox = new Checkbox(id, text, new Vector2f(0, 0), fontSize, renderer, loader, window);
            checkbox.isChecked = checked;
            comboElements.add(checkbox);
        }
    }

    public void addSlider(String id)
    {
        if(!containsElementByID(id))
            comboElements.add(new Slider(id, new Vector2f(0, 0), new Vector2f(size.x - 4, getFontHeight(fontSize) + 4), renderer, loader, window));
    }

    public void addSlider(String id, float sliderPosition, float min, float max)
    {
        if(!containsElementByID(id))
            comboElements.add(new Slider(id, new Vector2f(0, 0), new Vector2f(size.x - 4, getFontHeight(fontSize) + 4), renderer, loader, window, sliderPosition, min, max));
    }

    public void addTextbox(String id)
    {
        if(!containsElementByID(id))
            comboElements.add(new Textbox(id, new Vector2f(0, 0), new Vector2f(size.x - 4, getFontHeight(fontSize) + 4), fontSize, renderer, loader, window));
    }

    public void addTextbox(String id, boolean numbers, boolean letters, boolean others)
    {
        if(!containsElementByID(id))
        {
            Textbox tb = new Textbox(id, new Vector2f(0, 0), new Vector2f(size.x - 4, getFontHeight(fontSize) + 4), fontSize, renderer, loader, window);
            tb.numbers = numbers;
            tb.letters = letters;
            tb.others = others;
            comboElements.add(tb);
        }
    }

    public void addTextbox(String id, String text)
    {
        if(!containsElementByID(id))
        {
            Textbox tb = new Textbox(id, new Vector2f(0, 0), new Vector2f(size.x - 4, getFontHeight(fontSize) + 4), fontSize, renderer, loader, window);
            tb.setText(text);
            comboElements.add(tb);
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
            comboElements.add(tb);
        }
    }

    public void addString(String id, String string)
    {
        if(!containsElementByID(id))
            comboElements.add(new DropDownTab.StringElement(id, string, fontSize, renderer));
    }

    public void addRect(String id, float height)
    {
        if(!containsElementByID(id))
            comboElements.add(new DropDownTab.RectangleElement(id, height, renderer, loader, window));
    }

    public void addRect(String id, float height, Color color)
    {
        if(!containsElementByID(id))
            comboElements.add(new DropDownTab.RectangleElement(id, height, color, renderer, loader, window));
    }

    public void addList(String id, ButtonList buttonList, int height)
    {
        if(!containsElementByID(id))
        {
            buttonList.id = id;
            buttonList.pos = new Vector2f(0, 0);
            buttonList.size = new Vector2f(size.x - 4, height);
            comboElements.add(buttonList);
        }
    }

    @Override
    public void draw(MouseInput mouseInput, boolean overOther) {
        super.draw(mouseInput, overOther);

        hovering = isMouseOverTab(mouseInput) && !overOther;
        if(hovering)
            hoverCursor();

        float yOffset = 0;

        if (extended)
            yOffset = updateElements(yOffset);

        if(size.x != prevSize.x || size.y != prevSize.y)
        {
            refreshOutline(yOffset);
            this.prevSize = size;
        }

        drawBackdrop(yOffset);

        renderer.drawRect((int) pos.x, (int) pos.y, (int) size.x, (int) size.y, ((mouseInput.rightButtonPress || mouseInput.leftButtonPress) && hovering) ? Config.INTERFACE_TERTIARY_COLOR : (hovering ? Config.INTERFACE_SECONDARY_COLOR : Config.INTERFACE_PRIMARY_COLOR));
        renderer.drawRectOutline(new Vector2f(pos.x, pos.y), outlineSelection, ((mouseInput.rightButtonPress || mouseInput.leftButtonPress) && hovering) ? Config.INTERFACE_TERTIARY_COLOR2 : (hovering ? Config.INTERFACE_SECONDARY_COLOR2 : Config.INTERFACE_PRIMARY_COLOR2), false);

        renderer.startScissor((int) pos.x, (int) pos.y, (int) (size.x - size.y), (int) size.y);
        renderer.drawString(tabTitle, Config.FONT_COLOR, (int) (pos.x + size.y / 2 - getFontHeight(fontSize) / 2), (int) (pos.y + size.y / 2 - getFontHeight(fontSize) / 2), fontSize);
        renderer.endScissor();

        if(extended)
        {
            Vector2f p1 = new Vector2f(pos.x + size.x - size.y * 0.35f, pos.y + size.y * 0.25f);
            Vector2f p2 = new Vector2f(p1.x - size.y / 2f, p1.y);
            Vector2f p3 = new Vector2f(p1.x - size.y / 4f, pos.y + size.y * 0.75f);
            renderer.drawTriangle(loader, p1, p2, p3, Config.FONT_COLOR);

            drawElements(mouseInput, overOther);
        }
        else
        {
            Vector2f p1 = new Vector2f(pos.x + size.x - size.y * 0.35f, pos.y + size.y / 2f);
            Vector2f p2 = new Vector2f(p1.x - size.y / 2f, pos.y + size.y * 0.25f);
            Vector2f p3 = new Vector2f(p1.x - size.y / 2f, pos.y + size.y * 0.75f);
            renderer.drawTriangle(loader, p1, p2, p3, Config.FONT_COLOR);
        }
    }
    @Override
    public void resize() {
        super.resize();

        for(Element e : comboElements)
            e.resize();

        float yOffset = 0;
        if (extended)
            yOffset = updateElements(yOffset);
        refreshOutline(yOffset);
    }

    public void refreshOutline(float yOffset)
    {
        if(outlineSelection != null)
            this.outlineSelection.cleanup(loader);
        if(outlineElement != null)
            this.outlineElement.cleanup(loader);
        this.outlineSelection = LineStrip.processVerts(LineStrip.getRectangle(size), loader, window);
        this.outlineElement = LineStrip.processVerts(LineStrip.getRectangle(new Vector2f(size.x, (int) Math.round(2f + yOffset))), loader, window);
    }

    public void drawBackdrop(float yOffset)
    {
        int x = (int) Math.round(pos.x + size.x + size.y / 2);
        int y = (int) Math.round(pos.y);
        int xSize = (int) Math.round(size.x);
        int ySize = (int) Math.round((extended ? 2f : 0f) + yOffset);

        renderer.doBlur(1.0025f, x, y, xSize, ySize);
        renderer.doBlur(2, x, y, xSize, ySize);
        renderer.doBlur(3, x, y, xSize, ySize);
        renderer.doBlur(2, x, y, xSize, ySize);
        renderer.doBlur(1.5f, x, y, xSize, ySize);
        renderer.doBlur(1.25f, x, y, xSize, ySize);

        if (extended)
        {
            renderer.drawRect(x, (int) Math.round(pos.y), xSize, (int) Math.round(2f + yOffset), Config.PRIMARY_COLOR);
            renderer.drawRectOutline(new Vector2f(x, pos.y), outlineElement, Config.SECONDARY_COLOR, false);
        }
    }

    public void drawElements(MouseInput mouseInput, boolean overOther)
    {
        for(int i = 0; i < comboElements.size(); i++)
        {
            Element element = comboElements.get(i);
            element.draw(mouseInput, overOther);
        }
    }

    public float updateElements(float yOffset)
    {
        for (int i = 0; i < comboElements.size(); i++) {
            Element element = comboElements.get(i);
            element.pos = new Vector2f(pos.x + (element instanceof ButtonList ? 0 : 2) + size.x + size.y / 2, pos.y + 2 + yOffset);

            if (element.size == null)
                element.size = new Vector2f(size.x - (element instanceof ButtonList ? 0 : 4), getFontHeight(fontSize) + 4);
            else
                element.size = new Vector2f(size.x - (element instanceof ButtonList ? 0 : 4), element.size.y);

            yOffset += element.size.y + 2;
        }

        return yOffset;
    }

    @Override
    public void secondThread() {
        super.secondThread();

        if(extended)
            for(Element element : comboElements)
                element.secondThread();
    }

    @Override
    public void onChar(int codePoint, int modifiers) {
        super.onChar(codePoint, modifiers);

        if(extended)
            for(Element element : comboElements)
                element.onChar(codePoint, modifiers);
    }

    @Override
    public void onMouseScroll(Vector2d pos, double xOffset, double yOffset) {
        super.onMouseScroll(pos, xOffset, yOffset);

        if(extended)
            for(Element element : comboElements)
                element.onMouseScroll(pos, xOffset, yOffset);
    }

    public boolean isMouseOverTab(Vector2f mousePos)
    {
        return mousePos.x > pos.x && mousePos.y > pos.y && mousePos.x < pos.x + size.x && mousePos.y < pos.y + size.y;
    }
    public boolean isMouseOverTab(Vector2d mousePos)
    {
        return isMouseOverTab(new Vector2f((float) mousePos.x, (float) mousePos.y));
    }
    public boolean isMouseOverTab(MouseInput mouseInput)
    {
        return isMouseOverTab(mouseInput.currentPos);
    }
    @Override
    public boolean isMouseOverElement(Vector2f mousePos) {
        float yOffset = 0;

        try
        {
            if(extended)
                for(int i = 0; i < comboElements.size(); i++)
                {
                    Element element = comboElements.get(i);
                    yOffset += element.size.y + 2;
                }
        }catch (Exception e){}

        int x = (int) Math.round(pos.x + size.x + size.y / 2);
        int y = (int) Math.round(pos.y);
        int xSize = (int) Math.round(size.x);
        int ySize = (int) Math.round((extended ? 2f : 0f) + yOffset);

        boolean overElement = mousePos.x > x && mousePos.y > y && mousePos.x < x + xSize && mousePos.y < y + ySize;

        if(extended)
            for(Element e : comboElements)
                if(e.isMouseOverElement(mousePos))
                    overElement = true;

        return overElement;
    }

    @Override
    public void onKey(int key, int scancode, int action, int mods) {
        super.onKey(key, scancode, action, mods);

        if(extended)
        {
            for(int i = 0; i < comboElements.size(); i++)
            {
                Element element = comboElements.get(i);
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
        for (int i = startIndex; i < comboElements.size(); i++) {
            Element element = comboElements.get(i);

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
    public void onClick(Vector2d pos, int button, int action, int mods, boolean overOther) {

        if(!isMouseOverTab(pos) && !isMouseOverElement(pos))
            this.extended = false;

        if(isMouseOverTab(pos) && !overOther)
        {
            if((button == GLFW.GLFW_MOUSE_BUTTON_2 || button == GLFW.GLFW_MOUSE_BUTTON_1)&& action == GLFW.GLFW_PRESS)
            {
                extended = !extended;

                if(!extended)
                    for(Element element : comboElements)
                        element.setFocused(false);
                else
                {
                    float yOffset = 0;
                    if (extended)
                        yOffset = updateElements(yOffset);
                    refreshOutline(yOffset);
                }
            }
        }

        if(extended)
            for(Element element : comboElements)
                element.onClick(pos, button, action, mods, overOther);

        super.onClick(pos, button, action, mods, overOther);
    }

    @Override
    public boolean isFocused() {
        boolean focused = false;
        for(Element element : comboElements)
            if(element.isFocused())
                focused = true;

        return extended && focused;
    }

    @Override
    public void hoverCursor() {
        Cursors.setCursor(ECursor.hand2);
    }
}
