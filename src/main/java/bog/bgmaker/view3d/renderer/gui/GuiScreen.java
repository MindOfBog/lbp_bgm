package bog.bgmaker.view3d.renderer.gui;

import bog.bgmaker.view3d.ObjectLoader;
import bog.bgmaker.view3d.managers.MouseInput;
import bog.bgmaker.view3d.managers.RenderMan;
import bog.bgmaker.view3d.managers.WindowMan;
import bog.bgmaker.view3d.renderer.gui.elements.*;
import bog.bgmaker.view3d.renderer.gui.elements.Panel;
import bog.bgmaker.view3d.renderer.gui.font.FontRenderer;
import bog.bgmaker.view3d.renderer.gui.ingredients.Line;
import bog.bgmaker.view3d.renderer.gui.ingredients.LineStrip;
import bog.bgmaker.view3d.renderer.gui.ingredients.Quad;
import bog.bgmaker.view3d.renderer.gui.ingredients.Scissor;
import bog.bgmaker.view3d.utils.MousePicker;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * @author Bog
 */
public class GuiScreen {

    public ArrayList<Element> guiElements;
    public GuiScreen previousScreen;

    RenderMan renderer;
    ObjectLoader loader;
    WindowMan window;

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
            if(element.isMouseOverElement(new Vector2f((float) mouseInput.currentPos.x, (float) mouseInput.currentPos.y)))
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
            if (element.isFocused()) {

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

    public boolean onClick(MouseInput mouseInput, int button, int action, int mods)
    {
        boolean overElement = false;
        for(int i = 0; i < guiElements.size(); i++)
        {
            Element element = guiElements.get(i);

            if(element.isMouseOverElement(new Vector2f((float) mouseInput.currentPos.x, (float) mouseInput.currentPos.y)))
                overElement = true;

            boolean overOther = false;

            for(int o = i + 1; o < guiElements.size(); o++)
                if(guiElements.get(o).isMouseOverElement(mouseInput.currentPos))
                    overOther = true;

            element.onClick(mouseInput.currentPos, button, action, mods, overOther);
        }

        for(int i = 0; i < guiElements.size(); i++)
        {
            Element element = guiElements.get(i);

            if(element instanceof DropDownTab && ((DropDownTab)element).dragging)
            {
                guiElements.add(element);
                guiElements.remove(i);
            }
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

    public Vector2f setTextboxValueFloat(Textbox box, float value)
    {
        if(box != null)
        {
            if (!box.isFocused())
            {
                String v = Float.toString(value);
                box.setText(v.equalsIgnoreCase("nan") ? "" : v);
            }
            else
                try
                {
                    return new Vector2f(Float.parseFloat(box.getText()), 1);
                }
                catch (Exception e)
                {
                    return new Vector2f(0, 1);
                }
        }

        return new Vector2f(0, 0);
    }

    public String setTextboxValueString(Textbox box, String value)
    {
        if(box != null)
        {
            if (!box.isFocused())
                box.setText(value == null ? "" : value);
            else
                try
                {
                    return box.getText();
                }
                catch (Exception e)
                {
                    return null;
                }
        }

        return null;
    }

    public Vector2f setSliderValue(Slider slider, float value)
    {
        if(slider != null)
        {
            if(!slider.isSliding)
                slider.setCurrentValue(value);
            else
                return new Vector2f(slider.getCurrentValue(), 1);

        }

        return new Vector2f(0, 0);
    }
}
