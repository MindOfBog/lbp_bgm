package bog.lbpas.view3d.renderer.gui.elements;

import bog.lbpas.view3d.managers.assetLoading.ObjectLoader;
import bog.lbpas.view3d.core.Model;
import bog.lbpas.view3d.managers.MouseInput;
import bog.lbpas.view3d.managers.RenderMan;
import bog.lbpas.view3d.managers.WindowMan;
import bog.lbpas.view3d.renderer.gui.cursor.ECursor;
import bog.lbpas.view3d.renderer.gui.ingredients.LineStrip;
import bog.lbpas.view3d.utils.Config;
import bog.lbpas.view3d.utils.Consts;
import bog.lbpas.view3d.utils.Cursors;
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
    public boolean extended = false;

    public int tabWidth;

    public boolean autoCollapse = true;

    Vector2f prevSize = new Vector2f();

    Model outlineSelection;
    Model outlineElement;

    public ComboBox(String id, String tabTitle, Vector2f pos, Vector2f size, int fontSize, int tabWidth, RenderMan renderer, ObjectLoader loader, WindowMan window)
    {
        this.id = id;
        this.tabTitle = tabTitle;
        this.pos = pos;
        this.size = size;
        this.fontSize = fontSize;
        this.renderer = renderer;
        this.loader = loader;
        this.window = window;
        this.tabWidth = tabWidth;
        comboElements = new ArrayList<>();
    }

    public ComboBox(String id, String tabTitle, int fontSize, int tabWidth, RenderMan renderer, ObjectLoader loader, WindowMan window)
    {
        this.id = id;
        this.tabTitle = tabTitle;
        this.pos = new Vector2f();
        this.size = new Vector2f();
        this.fontSize = fontSize;
        this.renderer = renderer;
        this.loader = loader;
        this.window = window;
        this.tabWidth = tabWidth;
        comboElements = new ArrayList<>();
    }

    public ComboBox(String id, String tabTitle, Vector2f pos, Vector2f size, int fontSize, int tabWidth, RenderMan renderer, ObjectLoader loader, WindowMan window, boolean autoCollapse)
    {
        this(id, tabTitle, pos, size, fontSize, tabWidth, renderer, loader, window);
        this.autoCollapse = autoCollapse;
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

    public Button addButton(String id, String buttonText, Button button)
    {
        if(!containsElementByID(id))
        {
            button.id = id;
            button.buttonText = buttonText;
            button.pos = new Vector2f(0, 0);
            if(button.size == null)
                button.size = new Vector2f(tabWidth - 4, getFontHeight(fontSize) + 4);
            else
                button.size.x = tabWidth - 4;
            button.fontSize = fontSize;
            button.renderer = renderer;
            button.window = window;
            button.loader = loader;
            comboElements.add(button);
        }
        return button;
    }

    public ElementList addElementList(ElementList elementList)
    {
        if(!containsElementByID(elementList.id))
        {
            elementList.pos = new Vector2f(0, 0);
            if(elementList.size == null)
                elementList.size = new Vector2f(tabWidth - 4, getFontHeight(fontSize) + 4);
            else
                elementList.size.x = tabWidth - 4;
            elementList.fontSize = fontSize;
            elementList.renderer = renderer;
            elementList.window = window;
            elementList.loader = loader;
            comboElements.add(elementList);
            return elementList;
        }
        else
            return (ElementList) getElementByID(elementList.id);
    }

    public ComboBox addComboBox(String id, String title, int tabWidth)
    {
        if(!containsElementByID(id))
        {
            ComboBox parent = this;
            ComboBox comboBox = new ComboBox(id, title, new Vector2f(0, 0), new Vector2f(this.tabWidth - 4, getFontHeight(fontSize) + 4), fontSize, tabWidth, renderer, loader, window)
            {
                @Override
                public int[] getParentTransform() {
                    return parent.getTabPosWidth();
                }
            };
            comboElements.add(comboBox);
            return comboBox;
        }
        else
            return (ComboBox) getElementByID(id);
    }

    public ElementList addElementList(String id, int height)
    {
        if(!containsElementByID(id))
        {
            ComboBox parent = this;
            ElementList elementList = new ElementList(id, new Vector2f(0, 0), new Vector2f(tabWidth - 4, height), fontSize, renderer, loader, window)
            {
                @Override
                public int[] getParentTransform() {
                    return parent.getTabPosWidth();
                }
            };
            comboElements.add(elementList);
            return elementList;
        }
        else
            return (ElementList) getElementByID(id);
    }

    public Checkbox addCheckbox(String id, String text)
    {
        if(!containsElementByID(id))
        {
            Checkbox cb = new Checkbox(id, text, new Vector2f(0, 0), fontSize, renderer, loader, window);
            comboElements.add(cb);
            return cb;
        }
        return null;
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
            comboElements.add(cb);
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
            comboElements.add(cb);
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
            comboElements.add(checkbox);
            return checkbox;
        }
        return null;
    }

    public void addSlider(String id)
    {
        if(!containsElementByID(id))
            comboElements.add(new Slider(id, new Vector2f(0, 0), new Vector2f(tabWidth - 4, getFontHeight(fontSize) + 4), renderer, loader, window));
    }

    public void addSlider(String id, float sliderPosition, float min, float max)
    {
        if(!containsElementByID(id))
            comboElements.add(new Slider(id, new Vector2f(0, 0), new Vector2f(tabWidth - 4, getFontHeight(fontSize) + 4), renderer, loader, window, sliderPosition, min, max));
    }

    public void addTextbox(String id)
    {
        if(!containsElementByID(id))
            comboElements.add(new Textbox(id, new Vector2f(0, 0), new Vector2f(tabWidth - 4, getFontHeight(fontSize) + 4), fontSize, renderer, loader, window));
    }

    public void addTextbox(String id, boolean numbers, boolean letters, boolean others)
    {
        if(!containsElementByID(id))
        {
            Textbox tb = new Textbox(id, new Vector2f(0, 0), new Vector2f(tabWidth - 4, getFontHeight(fontSize) + 4), fontSize, renderer, loader, window);
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
            Textbox tb = new Textbox(id, new Vector2f(0, 0), new Vector2f(tabWidth - 4, getFontHeight(fontSize) + 4), fontSize, renderer, loader, window);
            tb.setText(text);
            comboElements.add(tb);
        }
    }

    public void addTextbox(String id, boolean numbers, boolean letters, boolean others, String text)
    {
        if(!containsElementByID(id))
        {
            Textbox tb = new Textbox(id, new Vector2f(0, 0), new Vector2f(tabWidth - 4, getFontHeight(fontSize) + 4), fontSize, renderer, loader, window);
            tb.numbers = numbers;
            tb.letters = letters;
            tb.others = others;
            tb.setText(text);
            comboElements.add(tb);
        }
    }

    public Panel addPanel(String id)
    {
        if(!containsElementByID(id))
        {
            Panel p = new Panel(new Vector2f(tabWidth - 4, getFontHeight(fontSize) + 4), renderer);
            p.id = id;
            comboElements.add(p);
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
            p.size.x = tabWidth - 4;
            if(p.size.y == 0)
                p.size.y = getFontHeight(fontSize) + 4;
            if(p.id == null)
                p.id = "";
            comboElements.add(p);
            return p;
        }
        else
            return (Panel) getElementByID(p.id);
    }
    public void addString(String id, String string)
    {
        if(!containsElementByID(id))
            comboElements.add(new DropDownTab.StringElement(id, string, fontSize, renderer));
    }

    public void addString(DropDownTab.StringElement string)
    {
        if(!containsElementByID(id))
            comboElements.add(string);
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

    public ButtonList addList(String id, ButtonList buttonList, int height)
    {
        if(!containsElementByID(id))
        {
            buttonList.id = id;
            buttonList.pos = new Vector2f(0, 0);
            buttonList.size = new Vector2f(tabWidth - 4, height);
            comboElements.add(buttonList);
        }
        return buttonList;
    }

    public ButtonList addList(String id, ButtonList buttonList)
    {
        if(!containsElementByID(id))
        {
            buttonList.id = id;
            buttonList.pos = new Vector2f(0, 0);
            buttonList.size = new Vector2f(tabWidth - 4, 8 + (buttonList.buttonHeight() + 2) * buttonList.list.size());
            comboElements.add(buttonList);
        }
        return buttonList;
    }

    public DropDownTab.SeparatorElement addSeparator(String id)
    {
        if(!containsElementByID(id))
        {
            DropDownTab.SeparatorElement sep = new DropDownTab.SeparatorElement(id, new Vector2f(0), tabWidth - 4, 10, renderer, loader, window);
            comboElements.add(sep);
            return sep;
        }
        else
            return (DropDownTab.SeparatorElement) getElementByID(id);
    }

    float prevYOff = -1;
    @Override
    public void draw(MouseInput mouseInput, boolean overOther) {
        super.draw(mouseInput, overOther);

        float fontHeight = getFontHeight(fontSize);

        hovering = isMouseOverTab(mouseInput) && !overOther;

        if(hovering)
            Cursors.setCursor(ECursor.hand2);

        float yOffset = 0;

        if (extended)
            yOffset = updateElements(yOffset);

        if(tabWidth != prevSize.x || size.y != prevSize.y || prevYOff != yOffset)
        {
            refreshOutline(yOffset);
            prevSize.x = size.x;
            prevSize.y = size.y;
            prevYOff = yOffset;
        }


        renderer.drawRect(Math.round(pos.x), Math.round(pos.y), Math.round(size.x), Math.round(size.y), ((mouseInput.rightButtonPress || mouseInput.leftButtonPress) && hovering) ? Config.INTERFACE_TERTIARY_COLOR : (hovering ? Config.INTERFACE_SECONDARY_COLOR : Config.INTERFACE_PRIMARY_COLOR));
        renderer.drawRectOutline(new Vector2f(pos.x, pos.y), outlineSelection, ((mouseInput.rightButtonPress || mouseInput.leftButtonPress) && hovering) ? Config.INTERFACE_TERTIARY_COLOR2 : (hovering ? Config.INTERFACE_SECONDARY_COLOR2 : Config.INTERFACE_PRIMARY_COLOR2), false);

        renderer.startScissor(Math.round(pos.x), Math.round(pos.y), Math.round(size.x - size.y), Math.round(size.y));
        renderer.drawString(tabTitle == null ? "" : tabTitle, Config.FONT_COLOR, Math.round(pos.x + (size.y + fontHeight) / 8f), Math.round(pos.y + size.y / 2f - fontHeight / 2f), fontSize);
        renderer.endScissor();

        float triangleSize = fontHeight * 0.6f;

        if(extended)
        {
            Vector2f p1 = new Vector2f(pos.x + size.x - size.y / 2f + triangleSize / 2f, pos.y + size.y / 2f);
            Vector2f p2 = new Vector2f(p1.x - triangleSize, pos.y + size.y / 2f - triangleSize / 2f);
            Vector2f p3 = new Vector2f(p1.x - triangleSize, pos.y + size.y / 2f + triangleSize / 2f);
            renderer.drawTriangle(loader, p1, p2, p3, Config.FONT_COLOR);

            renderer.startScissorEscape();
            drawBackdrop(yOffset);
            drawElements(mouseInput, overOther);
            renderer.endScissorEscape();
        }
        else
        {
            Vector2f p1 = new Vector2f(pos.x + size.x - size.y / 2f - triangleSize / 2f, pos.y + size.y / 2f);
            Vector2f p2 = new Vector2f(p1.x + triangleSize, pos.y + size.y / 2f - triangleSize / 2f);
            Vector2f p3 = new Vector2f(p1.x + triangleSize, pos.y + size.y / 2f + triangleSize / 2f);
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
        this.outlineElement = LineStrip.processVerts(LineStrip.getRectangle(new Vector2f(tabWidth, (int) Math.round(2f + yOffset))), loader, window);
    }

    public void drawBackdrop(float yOffset)
    {
        int[] parentTransform = getParentTransform();

        float ypos = !comboElements.isEmpty() && comboElements.get(0).pos != null ? comboElements.get(0).pos.y - 2 : pos.y;
        float xpos = (parentTransform == null ? pos.x + size.x : parentTransform[0] + parentTransform[2]) + size.y / 2;

        int x = (int) Math.round(xpos);
        int y = (int) Math.round(ypos);
        int xSize = (int) Math.round(tabWidth);
        int ySize = (int) Math.round((extended ? 2f : 0f) + yOffset);

        if(x + xSize > window.width)
            x = (int) Math.round((parentTransform == null ? pos.x : parentTransform[0]) - size.y / 2 - xSize);

        renderer.doBlur(Consts.GAUSSIAN_RADIUS, Consts.GAUSSIAN_KERNEL, x, y, xSize, ySize);

        renderer.drawRect(x, y, xSize, (int) Math.round(2f + yOffset), Config.PRIMARY_COLOR);
        renderer.drawRectOutline(new Vector2f(x, y), outlineElement, Config.SECONDARY_COLOR, false);
    }

    public int[] getTabPosWidth()
    {
        int[] parentTransform = getParentTransform();

        float ypos = !comboElements.isEmpty() && comboElements.get(0).pos != null ? comboElements.get(0).pos.y - 2 : pos.y;
        float xpos = (parentTransform == null ? pos.x + size.x : parentTransform[0] + parentTransform[2]) + size.y / 2;

        int x = (int) Math.round(xpos);
        int y = (int) Math.round(ypos);
        int xSize = (int) Math.round(tabWidth);

        if(x + xSize > window.width)
            x = (int) Math.round((parentTransform == null ? pos.x : parentTransform[0]) - size.y / 2 - xSize);

        return new int[]{x, y, xSize};
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
        for (int i = 0; i < comboElements.size(); i++)
        {
            Element element = comboElements.get(i);

            int[] parentTransform = getParentTransform();

            int x = (int) Math.round((parentTransform == null ? pos.x + size.x : parentTransform[0] + parentTransform[2]) + size.y / 2);
            int y = (int) Math.round(pos.y);
            int xSize = (int) Math.round(tabWidth);

            if(x + xSize > window.width)
                x = (int) Math.round((parentTransform == null ? pos.x : parentTransform[0]) - xSize - size.y / 2);

            element.pos = new Vector2f(x + (element instanceof ButtonList ? 0 : 2), y + 2 + yOffset);

            if (element.size == null)
                element.size = new Vector2f(xSize - (element instanceof ButtonList ? 0 : 4), getFontHeight(fontSize) + 4);
            else
                element.size.x = xSize - (element instanceof ButtonList ? 0 : 4);

            yOffset += element.size.y + 2;
        }

        if(this.pos.y + yOffset + 2 > this.window.height)
        {
            for (int i = 0; i < comboElements.size(); i++)
            {
                Element element = comboElements.get(i);
                element.pos.y -= (this.pos.y + yOffset + 2) - this.window.height;
            }
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
        if(mousePos == null || pos == null || size == null)
            return false;
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

        if(pos == null || size == null)
            return false;

        try
        {
            if(extended)
                for(int i = 0; i < comboElements.size(); i++)
                {
                    Element element = comboElements.get(i);
                    yOffset += element.size.y + 2;
                }
        }catch (Exception e){}

        int[] parentTransform = getParentTransform();

        int x = (int) Math.round((parentTransform == null ? pos.x + size.x : parentTransform[0] + parentTransform[2]) + size.y / 2);
        int y = (int) Math.round(pos.y);
        int xSize = (int) Math.round(tabWidth);
        int ySize = (int) Math.round((extended ? 2f : 0f) + yOffset);

        if(x + xSize > window.width)
            x = (int) Math.round((parentTransform == null ? pos.x : parentTransform[0]) - size.y / 2 - xSize);

        boolean overElement = (mousePos.x > x && mousePos.y > y && mousePos.x < x + xSize && mousePos.y < y + ySize) || isMouseOverTab(mousePos);

        if(extended)
            for(int i = 0; i < comboElements.size(); i++)
                if(comboElements.get(i).isMouseOverElement(mousePos))
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

    public void collapsed(boolean collapsed)
    {
        this.extended = !collapsed;
    }

    @Override
    public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overOther) {

        if((!isMouseOverElement(pos) && autoCollapse) || (!autoCollapse && overOther))
        {
            this.extended = false;
            for(Element e : comboElements)
                e.setFocused(false);
        }

        if(isMouseOverTab(pos) && !overOther)
        {
            if((button == GLFW.GLFW_MOUSE_BUTTON_2 || button == GLFW.GLFW_MOUSE_BUTTON_1) && action == GLFW.GLFW_RELEASE)
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
                element.onClick(mouseInput, pos, button, action, mods, overOther);

        super.onClick(mouseInput, pos, button, action, mods, overOther);
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
    public void setFocused(boolean focused) {

        if(!focused)
            for(Element e : comboElements)
                e.setFocused(false);

        super.setFocused(focused);
    }

    public int[] getParentTransform(){return null;}

    @Override
    public void onMouseMove(MouseInput mouseInput, double x, double y, boolean overElement) {
        super.onMouseMove(mouseInput, x, y, overElement);

        if(extended)
            for(Element element : comboElements)
            {
                element.onMouseMove(mouseInput, x, y, overElement);
                if(element.isMouseOverElement(pos))
                    overElement = true;
            }
    }
}
