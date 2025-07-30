package bog.lbpas.view3d.renderer.gui.elements;

import bog.lbpas.view3d.managers.assetLoading.ObjectLoader;
import bog.lbpas.view3d.core.Model;
import bog.lbpas.view3d.managers.MouseInput;
import bog.lbpas.view3d.managers.RenderMan;
import bog.lbpas.view3d.managers.WindowMan;
import bog.lbpas.view3d.renderer.gui.cursor.ECursor;
import bog.lbpas.view3d.renderer.gui.ingredients.Line;
import bog.lbpas.view3d.renderer.gui.ingredients.LineStrip;
import bog.lbpas.view3d.utils.Config;
import bog.lbpas.view3d.utils.Consts;
import bog.lbpas.view3d.utils.Cursors;
import bog.lbpas.view3d.utils.print;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayList;

/**
 * @author Bog
 */
public class DropDownTab extends Element{

    String tabTitle = "";
    public ArrayList<Element> tabElements;
    public boolean extended = true;

    public boolean resizeX = false;
    public boolean resizeY = false;

    Vector2f prevSize = new Vector2f();
    Model outlineSelection;
    Model outlineElement;

    public DropDownTab(String id, String tabTitle, Vector2f pos, Vector2f size, RenderMan renderer, ObjectLoader loader, WindowMan window)
    {
        this.id = id;
        this.tabTitle = tabTitle;
        this.pos = pos;
        this.size = size;
        this.renderer = renderer;
        this.loader = loader;
        this.window = window;
        tabElements = new ArrayList<>();
    }

    public DropDownTab closed()
    {
        this.extended = false;
        return this;
    }

    public DropDownTab resizeX()
    {
        this.resizeX = true;
        return this;
    }

    public DropDownTab resizeY()
    {
        this.resizeY = true;
        return this;
    }

    public float getFullHeight()
    {
        if(extended)
        {
            float fullSize = size.y;

            for(Element e : tabElements)
                fullSize += e.size == null ? e instanceof DropDownTab.StringElement ? getFontHeight() : 0 : e.size.y + 3;

            return fullSize;
        }
        else
            return size.y;
    }

    public Element getElementByID(String id)
    {
        for(Element element : tabElements)
            if(element.id.equalsIgnoreCase(id))
                return element;
        return null;
    }

    public void removeElementByID(String id)
    {
        for(int i = 0; i < tabElements.size(); i++)
            if(tabElements.get(i).id.equalsIgnoreCase(id))
                tabElements.remove(i);
    }

    public boolean containsElementByID(String id)
    {
        for(int i = 0; i < tabElements.size(); i++)
            if(tabElements.get(i).id != null && tabElements.get(i).id.equalsIgnoreCase(id))
                return true;
        return false;
    }

    public Button addButton(String buttonText, Button button)
    {
        if(!containsElementByID(button.id))
        {
            button.buttonText = buttonText;
            button.pos = new Vector2f(0, 0);
            if(button.size == null)
                button.size = new Vector2f(size.x - 4, getFontHeight() + 4);
            else
                button.size.x = size.x - 4;
            button.renderer = renderer;
            button.window = window;
            button.loader = loader;
            tabElements.add(button);
            return button;
        }
        else
            return (Button) getElementByID(button.id);
    }

    public ElementList addElementList(ElementList elementList)
    {
        if(!containsElementByID(elementList.id))
        {
            elementList.pos = new Vector2f(0, 0);
            if(elementList.size == null)
                elementList.size = new Vector2f(size.x - 4, getFontHeight() + 4);
            else
                elementList.size.x = size.x - 4;
            elementList.renderer = renderer;
            elementList.window = window;
            elementList.loader = loader;
            tabElements.add(elementList);
            return elementList;
        }
        else
            return (ElementList) getElementByID(elementList.id);
    }

    public ComboBox addComboBox(String id, String title, int tabWidth)
    {
        if(!containsElementByID(id))
        {
            ComboBox comboBox = new ComboBox(id, title, new Vector2f(0, 0), new Vector2f(size.x - 4, getFontHeight() + 4), tabWidth, renderer, loader, window)
            {
                @Override
                public int[] getParentTransform() {
                    return new int[]{(int) Math.round(this.pos.x), (int) Math.round(this.pos.y), (int) Math.round(this.size.x)};
                }
            };
            tabElements.add(comboBox);
            return comboBox;
        }
        else
            return (ComboBox) getElementByID(id);
    }

    public ElementList addElementList(String id, int height)
    {
        if(!containsElementByID(id))
        {
            DropDownTab parent = this;
            ElementList elementList = new ElementList(id, new Vector2f(0, 0), new Vector2f(size.x - 4, height), renderer, loader, window)
            {
                @Override
                public int[] getParentTransform() {
                    return new int[]{(int) Math.round(parent.pos.x), (int) Math.round(parent.pos.y), (int) Math.round(parent.size.x)};
                }
            };
            tabElements.add(elementList);
            return elementList;
        }
        else
            return (ElementList) getElementByID(id);
    }

    public Checkbox addCheckbox(String id, String text)
    {
        if(!containsElementByID(id))
        {
            Checkbox cb = new Checkbox(id, text, new Vector2f(0, 0), renderer, loader, window);
            tabElements.add(cb);
            return cb;
        }
        else
            return (Checkbox) getElementByID(id);
    }

    public Checkbox addCheckbox(String id, String text, boolean checked)
    {
        if(!containsElementByID(id))
        {
            Checkbox checkbox = new Checkbox(id, text, new Vector2f(0, 0), renderer, loader, window);
            checkbox.isChecked = checked;
            tabElements.add(checkbox);
            return checkbox;
        }
        else
            return (Checkbox) getElementByID(id);
    }

    public Slider addSlider(String id)
    {
        if(!containsElementByID(id))
        {
            Slider sl = new Slider(id, new Vector2f(0, 0), new Vector2f(size.x - 4, getFontHeight() + 4), renderer, loader, window);
            tabElements.add(sl);
            return sl;
        }
        else
            return (Slider) getElementByID(id);
    }

    public Slider addSlider(String id, float sliderPosition, float min, float max)
    {
        if(!containsElementByID(id))
        {
            Slider sl = new Slider(id, new Vector2f(0, 0), new Vector2f(size.x - 4, getFontHeight() + 4), renderer, loader, window, sliderPosition, min, max);
            tabElements.add(sl);
            return sl;
        }
        else
            return (Slider) getElementByID(id);
    }

    public Textbox addTextbox(String id)
    {
        if(!containsElementByID(id))
        {
            Textbox tb = new Textbox(id, new Vector2f(0, 0), new Vector2f(size.x - 4, getFontHeight() + 4), renderer, loader, window);
            tabElements.add(tb);
            return  tb;
        }
        else
            return (Textbox) getElementByID(id);
    }

    public Textbox addTextbox(String id, boolean numbers, boolean letters, boolean others)
    {
        if(!containsElementByID(id))
        {
            Textbox tb = new Textbox(id, new Vector2f(0, 0), new Vector2f(size.x - 4, getFontHeight() + 4), renderer, loader, window);
            tb.numbers = numbers;
            tb.letters = letters;
            tb.others = others;
            tabElements.add(tb);
            return tb;
        }
        else
            return (Textbox) getElementByID(id);
    }

    public Textbox addTextbox(String id, String text)
    {
        if(!containsElementByID(id))
        {
            Textbox tb = new Textbox(id, new Vector2f(0, 0), new Vector2f(size.x - 4, getFontHeight() + 4), renderer, loader, window);
            tb.setText(text);
            tabElements.add(tb);
            return tb;
        }
        else
            return (Textbox) getElementByID(id);
    }

    public Textbox addTextbox(String id, boolean numbers, boolean letters, boolean others, String text)
    {
        if(!containsElementByID(id))
        {
            Textbox tb = new Textbox(id, new Vector2f(0, 0), new Vector2f(size.x - 4, getFontHeight() + 4), renderer, loader, window);
            tb.numbers = numbers;
            tb.letters = letters;
            tb.others = others;
            tb.setText(text);
            tabElements.add(tb);
            return tb;
        }
        else
            return (Textbox) getElementByID(id);
    }

    public Panel addPanel(String id)
    {
        if(!containsElementByID(id))
        {
            Panel p = new Panel(new Vector2f(size.x - 4, getFontHeight() + 4), renderer);
            p.id = id;
            tabElements.add(p);
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
                p.size = new Vector2f(0, getFontHeight() + 4);
            p.size.x = size.x - 4;
            if(p.size.y == 0)
                p.size.y = getFontHeight() + 4;
            if(p.id == null)
                p.id = "";
            tabElements.add(p);
            return p;
        }
        else
            return (Panel) getElementByID(p.id);
    }

    public StringElement addString(String id, String string)
    {
        if(!containsElementByID(id))
        {
            StringElement s = new StringElement(id, string, renderer);
            tabElements.add(s);
            return s;
        }
        else
            return (StringElement) getElementByID(id);
    }

    public StringElement addString(StringElement string)
    {
        tabElements.add(string);
        return string;
    }

    public ButtonList addList(String id, ButtonList buttonList, int height)
    {
        if(!containsElementByID(id))
        {
            buttonList.id = id;
            buttonList.pos = new Vector2f(0, 0);
            buttonList.size = new Vector2f(size.x - 4, height);
            tabElements.add(buttonList);
            return buttonList;
        }
        else
            return (ButtonList) getElementByID(id);
    }

    public FileTree addFileTree(FileTree tree){
        if(!containsElementByID(tree.id))
        {
            tabElements.add(tree);
            tree.size = new Vector2f(size.x - 4, 0);
            return tree;
        }
        else
            return (FileTree) getElementByID(tree.id);
    }

    public SeparatorElement addSeparator(String id)
    {
        if(!containsElementByID(id))
        {
            SeparatorElement sep = new SeparatorElement(id, new Vector2f(0), size.x - 4, renderer, loader, window);
            tabElements.add(sep);
            return sep;
        }
        else
            return (SeparatorElement) getElementByID(id);
    }

    public boolean dragging = false;
    Vector2f dragOffset = new Vector2f(0, 0);
    float prevYOff = -1;
    @Override
    public void draw(MouseInput mouseInput, boolean overOther) {

        float yOffset = 0;
        float fontHeight = getFontHeightHeader();

        if (dragging) {
            this.pos.x = ((float) mouseInput.currentPos.x) - dragOffset.x;
            this.pos.y = ((float) mouseInput.currentPos.y) - dragOffset.y;
            Cursors.setCursor(ECursor.grabbing);
        }
        else
        {
            if(isMouseOverTab(mouseInput) && !overOther)
                Cursors.setCursor(ECursor.hand1);
        }

        if (!renderer.window.isMinimized) {
            if (this.pos.x < 3)
                this.pos.x = 3;
            if (this.pos.y < 23)
                this.pos.y = 23;

            if (this.pos.x + this.size.x > renderer.window.width - 3)
                this.pos.x = renderer.window.width - 3 - this.size.x;
            if (this.pos.y + this.size.y > renderer.window.height - 3)
                this.pos.y = renderer.window.height - 3 - this.size.y;
        }

        if(extended)
            yOffset = updateElements(yOffset);

        if(prevSize == null || size.x != prevSize.x || size.y != prevSize.y || prevYOff != yOffset)
        {
            refreshOutline(yOffset);
            resize();
            prevSize = new Vector2f(size);
            prevYOff = yOffset;
        }

        drawBackdrop(yOffset);
        renderer.drawRect(Math.round(Math.round(pos.x)), Math.round(Math.round(pos.y)), Math.round(Math.round(size.x)), Math.round(Math.round(size.y)), (dragging || (mouseInput.rightButtonPress && isMouseOverTab(mouseInput))) && !overOther ? Config.INTERFACE_TERTIARY_COLOR : (isMouseOverTab(mouseInput) && !overOther ? Config.INTERFACE_SECONDARY_COLOR : Config.INTERFACE_PRIMARY_COLOR));
        renderer.drawRectOutline(pos, outlineSelection, (dragging || (mouseInput.rightButtonPress && isMouseOverTab(mouseInput))) && !overOther ? Config.INTERFACE_TERTIARY_COLOR2 : (isMouseOverTab(mouseInput) && !overOther ? Config.INTERFACE_SECONDARY_COLOR2 : Config.INTERFACE_PRIMARY_COLOR2), false);

        if(extended && (resizeX || resizeY))
        {
//         TODO   Vector2f p1 = new Vector2f(pos.x + size.x - 2, pos.y + size.y + yOffset);
//            Vector2f p2 = new Vector2f(p1.x - 10, p1.y);
//            Vector2f p3 = new Vector2f(p1.x, p1.y - 10);
//            boolean mouseInTriangle = Utils.pointInTriangle2D(new Vector2f((float) mouseInput.currentPos.x, (float) mouseInput.currentPos.y), p1, p2, p3);
//            drawTriangle(p1, p2, p3, mouseInTriangle ? Color.blue : new Color(0f, 0f, 0f, 0.5f));
        }

        renderer.startScissor(Math.round(pos.x), Math.round(pos.y), Math.round(size.x - size.y), Math.round(size.y));
        renderer.drawHeader(Consts.FONT_SET_BOLD + tabTitle, Config.FONT_COLOR, Math.round(pos.x + (size.y + fontHeight) / 8f), Math.round(pos.y + size.y / 2f - fontHeight / 2f));
        renderer.endScissor();

        float triangleSize = fontHeight * 0.6f;

        if(extended)
        {
            Vector2f p1 = new Vector2f(pos.x + size.x - size.y / 2f + triangleSize / 2f, pos.y + size.y / 2f - triangleSize / 2f);
            Vector2f p2 = new Vector2f(p1.x - triangleSize, p1.y);
            Vector2f p3 = new Vector2f(p1.x - triangleSize / 2f, pos.y + size.y / 2f + triangleSize / 2f);
            renderer.drawTriangle(loader, p1, p2, p3, Config.FONT_COLOR);

            drawElements(mouseInput, overOther);
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

        for(int i = 0; i < tabElements.size(); i++)
            tabElements.get(i).resize();

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
        this.outlineElement = LineStrip.processVerts(LineStrip.getRectangle(new Vector2f(size.x, (int) Math.round(2f + yOffset)), LineStrip.UP), loader, window);
    }

    public void drawBackdrop(float yOffset)
    {
        int x = (int) Math.round(pos.x);
        int y = (int) Math.round(pos.y);
        int xSize = (int) Math.round(size.x);
        int ySize = (int) Math.round((extended ? 2f : 0f) + yOffset + size.y);
        if(Config.PRIMARY_COLOR.getAlpha() < 253 ||
                Config.INTERFACE_TERTIARY_COLOR.getAlpha() < 253 ||
                Config.INTERFACE_SECONDARY_COLOR.getAlpha() < 253 ||
                Config.INTERFACE_PRIMARY_COLOR.getAlpha() < 253)
        {
            renderer.doBlur(Consts.GAUSSIAN_RADIUS, Consts.GAUSSIAN_KERNEL, x, y, xSize, ySize);
        }

        if (extended)
        {
            renderer.drawRect(x, (int) Math.round(pos.y + size.y), xSize, (int) Math.round(2f + yOffset), Config.PRIMARY_COLOR);
            renderer.drawRectOutline(new Vector2f(pos.x, pos.y + size.y), outlineElement, Config.SECONDARY_COLOR, false);
        }
    }

    public void drawElements(MouseInput mouseInput, boolean overOther)
    {
        ArrayList<Integer> combos = new ArrayList<>();
        ArrayList<Boolean> combosB = new ArrayList<>();

        for(int i = 0; i < tabElements.size(); i++)
        {
            Element element = tabElements.get(i);

            if(element instanceof ComboBox)
            {
                combos.add(i);
                combosB.add(overOther);
                if(element.isMouseOverElement(mouseInput))
                    overOther = true;
            }
            else
            {
                element.draw(mouseInput, overOther);
                if(element.isMouseOverElement(mouseInput))
                    overOther = true;
            }
        }

        try
        {
            for(int i = combos.size() - 1; i >= 0; i--)
                tabElements.get(combos.get(i)).draw(mouseInput, combosB.get(i));
        }catch (Exception e){e.printStackTrace();}
    }

    public float updateElements(float yOffset)
    {
        for (int i = 0; i < tabElements.size(); i++) {
            Element element = tabElements.get(i);
            element.pos = new Vector2f(pos.x + (element instanceof ButtonList ? 0 : 2), pos.y + size.y + 2 + yOffset);

            if (element.size == null)
                element.size = new Vector2f(size.x - (element instanceof ButtonList ? 0 : 4), getFontHeight() + 4);
            else
                element.size = new Vector2f(size.x - (element instanceof ButtonList ? 0 : 4), element.size.y);

            yOffset += element.size.y + 2;
        }
        if (resizeX || resizeY)
            yOffset += 12;

        return yOffset;
    }

    @Override
    public void secondThread() {
        super.secondThread();

        if(extended)
            for(Element element : tabElements)
                element.secondThread();
    }

    @Override
    public void onChar(int codePoint, int modifiers) {
        super.onChar(codePoint, modifiers);

        if(extended)
            for(Element element : tabElements)
                element.onChar(codePoint, modifiers);
    }

    @Override
    public void onMouseScroll(Vector2d pos, double xOffset, double yOffset) {
        super.onMouseScroll(pos, xOffset, yOffset);

        if(extended)
            for(Element element : tabElements)
                element.onMouseScroll(pos, xOffset, yOffset);
    }

    @Override
    public void onMouseMove(MouseInput mouseInput, double x, double y, boolean overElement) {
        super.onMouseMove(mouseInput, x, y, overElement);

        if(extended)
            for(Element element : tabElements)
            {
                element.onMouseMove(mouseInput, x, y, overElement);
                if(element.isMouseOverElement(pos))
                    overElement = true;
            }
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
                for(int i = 0; i < tabElements.size(); i++)
                {
                    Element element = tabElements.get(i);
                    yOffset += element.size.y + 2;
                }
        }catch (Exception e){}

        boolean overElement = mousePos.x > pos.x && mousePos.y > pos.y && mousePos.x < pos.x + size.x && mousePos.y < pos.y + size.y + (extended ? 2 + yOffset : 0);

        if(extended)
            for(int i = 0; i < tabElements.size(); i++)
                if(tabElements.get(i).isMouseOverElement(mousePos))
                    overElement = true;

        return overElement;
    }

    @Override
    public void onKey(int key, int scancode, int action, int mods) {
        super.onKey(key, scancode, action, mods);

        if(extended)
        {
            for(int i = 0; i < tabElements.size(); i++)
            {
                Element element = tabElements.get(i);
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
                Element nextElement = findNextFocusableElementInPanel(panel, i + 1);

                element.setFocused(false);

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
        for (int i = startIndex; i < tabElements.size(); i++) {
            Element element = tabElements.get(i);

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
        if(isMouseOverTab(pos) && !overOther)
        {
            if(button == GLFW.GLFW_MOUSE_BUTTON_2 && action == GLFW.GLFW_RELEASE)
            {
                extended = !extended;

                float yOffset = 0;
                if (extended)
                    yOffset = updateElements(yOffset);
                refreshOutline(yOffset);

                if(!extended)
                    for(Element element : tabElements)
                        element.setFocused(false);
            }
            else if(button == GLFW.GLFW_MOUSE_BUTTON_1)
                if(action == GLFW.GLFW_PRESS)
                {
                    dragging = true;
                    dragOffset = new Vector2f((float) (pos.x - this.pos.x), (float) (pos.y - this.pos.y));
                }
        }

        if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_RELEASE)
                dragging = false;

        if(extended)
            for(Element element : tabElements)
            {
                element.onClick(mouseInput, pos, button, action, mods, overOther, focusedOther);
                if(element.isMouseOverElement(pos))
                    overOther = true;
            }

        super.onClick(mouseInput, pos, button, action, mods, overOther, focusedOther);
    }

    @Override
    public boolean isFocused() {
        boolean focused = false;
        for(int i = 0; i < tabElements.size(); i++)
            if(tabElements.get(i).isFocused())
                focused = true;

        return extended && focused;
    }

    public static class StringElement extends  Element
    {
        public String string = "";

        public StringElement(String id, String string, RenderMan renderer)
        {
            this.string = string;
            this.id = id;
            this.renderer = renderer;
        }

        public StringElement(String id, RenderMan renderer)
        {
            this.string = "";
            this.id = id;
            this.renderer = renderer;
        }

        @Override
        public void draw(MouseInput mouseInput, boolean overElement) {
            super.draw(mouseInput, overElement);

            try{renderer.drawString(stringToDraw(), Config.FONT_COLOR, (int) Math.round(pos.x + getFontHeight() / 4), (int) Math.round(pos.y + size.y / 2 - getFontHeight() / 2));}catch (Exception e){}
        }

        public String stringToDraw()
        {
            return string;
        }
    }

    public static class SeparatorElement extends Element
    {
        Model line;

        public SeparatorElement(String id, Vector2f pos, float width, RenderMan renderer, ObjectLoader loader, WindowMan window) {
            this.id = id;
            this.pos = pos;
            this.size = new Vector2f(width, 1);
            this.renderer = renderer;
            this.loader = loader;
            this.window = window;

            line = Line.getLine(window, loader, new Vector2i(0), new Vector2i((int) Math.round(pos.x + width), 0));
        }

        @Override
        public void resize() {
            line = Line.getLine(window, loader, new Vector2i(0), new Vector2i((int) Math.round(size.x), 0));
        }

        @Override
        public void draw(MouseInput mouseInput, boolean overOther) {
            renderer.drawLine(line, new Vector2f(this.pos.x, this.pos.y + (this.size.y / 2f)), Config.INTERFACE_PRIMARY_COLOR2,false);
        }
    }
}
