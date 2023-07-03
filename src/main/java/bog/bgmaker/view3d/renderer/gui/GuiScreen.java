package bog.bgmaker.view3d.renderer.gui;

import bog.bgmaker.view3d.ObjectLoader;
import bog.bgmaker.view3d.managers.MouseInput;
import bog.bgmaker.view3d.managers.RenderMan;
import bog.bgmaker.view3d.managers.WindowMan;
import bog.bgmaker.view3d.renderer.gui.elements.*;
import bog.bgmaker.view3d.renderer.gui.font.FontRenderer;
import bog.bgmaker.view3d.renderer.gui.ingredients.Line;
import bog.bgmaker.view3d.renderer.gui.ingredients.LineStrip;
import bog.bgmaker.view3d.renderer.gui.ingredients.Quad;
import bog.bgmaker.view3d.renderer.gui.ingredients.Scissor;
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
        boolean foundNext = false;

        for(int i = 0; i < guiElements.size(); i++)
        {
            Element element = guiElements.get(i);

            if(element.isFocused())
                elementFocused = true;

            if(key == GLFW.GLFW_KEY_TAB && action == GLFW.GLFW_PRESS && (element instanceof Textbox || element instanceof DropDownTab.LabeledTextbox || element instanceof Textarea))
            {
                if(element.isFocused() && !foundNext)
                {
                    element.setFocused(false);

                    int o = i + 1;

                    while(!foundNext)
                    {
                        if(o == guiElements.size())
                            o = 0;

                        Element nelement = guiElements.get(o);

                        if(nelement instanceof Textbox || nelement instanceof DropDownTab.LabeledTextbox || nelement instanceof Textarea)
                        {
                            nelement.setFocused(true);
                            foundNext = true;
                        }

                        o++;
                    }
                }
            }

            element.onKey(key, scancode, action, mods);
        }

        return elementFocused;
    }

    public boolean onClick(Vector2d pos, int button, int action, int mods)
    {
        boolean overElement = false;
        for(int i = 0; i < guiElements.size(); i++)
        {
            Element element = guiElements.get(i);

            if(element.isMouseOverElement(new Vector2f((float) pos.x, (float) pos.y)))
                overElement = true;

            boolean overOther = false;

            for(int o = i + 1; o < guiElements.size(); o++)
                if(guiElements.get(o).isMouseOverElement(pos))
                    overOther = true;

            element.onClick(pos, button, action, mods, overOther);
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


    public void drawLine(Vector2i pos1, Vector2i pos2, boolean smooth)
    {
        renderer.processGuiElement(new Line(pos1, pos2, loader, window, smooth, false));
    }

    public void drawLine(Vector2i pos1, Vector2i pos2, Color color, boolean smooth)
    {
        renderer.processGuiElement(new Line(pos1, pos2, color, loader, window, smooth, false));
    }

    public void drawImage(String path, float x, float y, float width, float height)
    {
        renderer.processGuiElement(new Quad(loader, path, new Vector2f(x, y), new Vector2f(width, height)));
    }

    public void drawImage(BufferedImage image, float x, float y, float width, float height)
    {
        renderer.processGuiElement(new Quad(loader, image, new Vector2f(x, y), new Vector2f(width, height)));
    }

    public void drawImage(int image, float x, float y, float width, float height)
    {
        renderer.processGuiElement(new Quad(loader, image, new Vector2f(x, y), new Vector2f(width, height)));
    }

    public void drawImageStatic(int image, float x, float y, float width, float height)
    {
        renderer.processGuiElement(new Quad(loader, image, new Vector2f(x, y), new Vector2f(width, height)).staticTexture());
    }

    public void drawImageStatic(int image, float x, float y, float width, float height, Color color)
    {
        Quad tex = new Quad(loader, image, new Vector2f(x, y), new Vector2f(width, height));
        tex.color = color;
        renderer.processGuiElement(tex.staticTexture());
    }

    public void drawRect(int x, int y, int width, int height, Color color)
    {
        renderer.processGuiElement(new Quad(loader, color, new Vector2f(x, y), new Vector2f(width, height)));
    }

    public void drawRectOutline(int x, int y, int width, int height, Color color, boolean smooth)
    {
        renderer.processGuiElement(LineStrip.lineRectangle(new Vector2f(x, y), new Vector2f(width, height), color, loader, window, smooth, false));
    }

    public void drawRectOutline(int x, int y, int width, int height, Color color, boolean smooth, int openSide)
    {
        renderer.processGuiElement(LineStrip.lineRectangle(new Vector2f(x, y), new Vector2f(width, height), color, loader, window, smooth, false, openSide));
    }
    public void drawString(String text, Color color, int x, int y, int size)
    {
        FontRenderer.drawString(renderer, text, x, y, size, color, 0, text.length());
    }

    public void startScissor(Vector2i pos, Vector2i size)
    {
        renderer.processGuiElement(Scissor.start(pos, size));
    }

    public void startScissor(int x, int y, int width, int height)
    {
        renderer.processGuiElement(Scissor.start(new Vector2i(x, y), new Vector2i(width, height)));
    }

    public void endScissor()
    {
        renderer.processGuiElement(Scissor.end());
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
