package bog.lbpas.view3d.renderer.gui;

import bog.lbpas.view3d.managers.assetLoading.ObjectLoader;
import bog.lbpas.view3d.managers.MouseInput;
import bog.lbpas.view3d.managers.RenderMan;
import bog.lbpas.view3d.managers.WindowMan;
import bog.lbpas.view3d.renderer.gui.elements.*;
import bog.lbpas.view3d.renderer.gui.elements.Panel;
import bog.lbpas.view3d.renderer.gui.font.FontRenderer;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * @author Bog
 */
public class GuiScreen {

    public ArrayList<Element> guiElements;
    public GuiScreen previousScreen;

    public RenderMan renderer;
    public ObjectLoader loader;
    public WindowMan window;

    public GuiScreen(RenderMan renderer, ObjectLoader loader, WindowMan window)
    {
        this.renderer = renderer;
        this.loader = loader;
        this.window = window;
        guiElements = new ArrayList<>();
    }

    public void draw(MouseInput mouseInput)
    {
        for (int i = 0; i < guiElements.size(); i++) {
            Element element = guiElements.get(i);

            boolean overOther = false;

            for (int o = i + 1; o < guiElements.size(); o++)
                if (guiElements.get(o).isMouseOverElement(mouseInput))
                    overOther = true;

            element.draw(mouseInput, overOther);
        }
    }

    public void secondaryThread()
    {
        if(this.guiElements != null)
            for(int i = 0; i < this.guiElements.size(); i++)
                this.guiElements.get(i).secondThread();
    }

    public void removeElementByID(String id)
    {
        for(int i = 0; i < guiElements.size(); i++)
            if(guiElements.get(i).id.equalsIgnoreCase(id))
                guiElements.remove(i);
    }

    public Element getElementByID(String id)
    {
        for(Element element : guiElements)
            if(element.id != null && element.id.equalsIgnoreCase(id))
                return element;
        return null;
    }

    public boolean isMouseOverElement(MouseInput mouseInput)
    {
        boolean overElement = false;
        for(Element element : guiElements)
            if(element.isMouseOverElement(mouseInput.currentPos))
                overElement = true;
        return overElement;
    }

    public boolean isElementFocused()
    {
        boolean focused = false;
        for(Element element : guiElements)
            if(element.isFocused())
                focused = true;
        return focused;
    }

    public void onChar(int codePoint, int modifiers)
    {
        for(Element element : guiElements)
        {
            element.onChar(codePoint, modifiers);
        }
    }

    public boolean onKey(int key, int scancode, int action, int mods)
    {
        boolean elementFocused = false;

        for(int i = 0; i < guiElements.size(); i++)
        {
            Element element = guiElements.get(i);
            if(element.isFocused())
                elementFocused = true;
            element.onKey(key, scancode, action, mods);

            if (key == GLFW.GLFW_KEY_TAB && action == GLFW.GLFW_PRESS) {
                if (element.isFocused() && (element instanceof Textbox || element instanceof Textarea)){
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

        return elementFocused;
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
        for (int i = startIndex; i < guiElements.size(); i++) {
            Element element = guiElements.get(i);

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

    private HashMap<Integer, Element> movetoEnd = new HashMap();

    public boolean onClick(MouseInput mouseInput, int button, int action, int mods)
    {
        boolean overElement = false;
        movetoEnd.clear();

        for(int i = 0; i < guiElements.size(); i++)
        {
            Element element = guiElements.get(i);

            if(element.isMouseOverElement(new Vector2f((float) mouseInput.currentPos.x, (float) mouseInput.currentPos.y)))
                overElement = true;

            boolean overOther = false;

            for(int o = i + 1; o < guiElements.size(); o++)
                if(guiElements.get(o).isMouseOverElement(mouseInput.currentPos))
                    overOther = true;

            element.onClick(mouseInput, mouseInput.currentPos, button, action, mods, overOther);

            if(element instanceof DropDownTab &&
                    (button == GLFW.GLFW_MOUSE_BUTTON_1 || button == GLFW.GLFW_MOUSE_BUTTON_2) &&
                    (((DropDownTab)element).isMouseOverTab(mouseInput) || ((DropDownTab)element).isMouseOverElement(mouseInput)) &&
                    !overOther)
                movetoEnd.put(i, element);
        }

        if(!movetoEnd.isEmpty())
        {
            for(int i : movetoEnd.keySet())
                guiElements.add(movetoEnd.get(i));
            for(int i : movetoEnd.keySet().stream().sorted(Collections.reverseOrder()).collect(Collectors.toList()))
                guiElements.remove(i);
        }

        return overElement;
    }

    public void onMouseScroll(Vector2d pos, double xOffset, double yOffset)
    {
        for(Element element : guiElements)
        {
            element.onMouseScroll(pos, xOffset, yOffset);
        }
    }

    public boolean onMouseMove(MouseInput mouseInput, double x, double y)
    {
        boolean overElement = false;
        for(int i = 0; i < guiElements.size(); i++)
        {
            Element element = guiElements.get(i);

            if(element.isMouseOverElement(mouseInput))
                overElement = true;

            boolean overOther = false;

            for(int o = i + 1; o < guiElements.size(); o++)
                if(guiElements.get(o).isMouseOverElement(mouseInput.currentPos))
                    overOther = true;

            element.onMouseMove(mouseInput, x, y, overOther);
        }

        return overElement;
    }

    public int getStringWidth(String text, int size)
    {
        return (int)FontRenderer.getStringWidth(text, size);
    }

    public int getFontHeight(int size)
    {
        return (int)FontRenderer.getFontHeight(size);
    }

    public boolean elementFocused()
    {
        boolean elementFocused = false;

        for(Element element : guiElements)
            if(element.isFocused())
                elementFocused = true;

        return elementFocused;
    }
}
