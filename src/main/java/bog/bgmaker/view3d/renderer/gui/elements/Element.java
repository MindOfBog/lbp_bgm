package bog.bgmaker.view3d.renderer.gui.elements;

import bog.bgmaker.view3d.ObjectLoader;
import bog.bgmaker.view3d.managers.MouseInput;
import bog.bgmaker.view3d.managers.RenderMan;
import bog.bgmaker.view3d.managers.WindowMan;
import bog.bgmaker.view3d.renderer.gui.font.FontRenderer;
import bog.bgmaker.view3d.renderer.gui.ingredients.Circle;
import bog.bgmaker.view3d.renderer.gui.ingredients.Quad;
import bog.bgmaker.view3d.renderer.gui.ingredients.Scissor;
import bog.bgmaker.view3d.renderer.gui.ingredients.Triangle;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector2i;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author Bog
 */
public class Element {

    public Vector2f pos;
    public Vector2f size;
    private boolean focused = false;
    public boolean hovering = false;
    public String id;
    public RenderMan renderer;
    public ObjectLoader loader;
    public WindowMan window;

    public void onClick(Vector2d pos, int button, int action, int mods, boolean overElement){}
    public void onKey(int key, int scancode, int action, int mods){}
    public void onChar(int codePoint, int modifiers){}
    public void onMouseScroll(Vector2d pos, double xOffset, double yOffset){}
    public void draw(MouseInput mouseInput, boolean overElement)
    {
        hovering = isMouseOverElement(mouseInput) && !overElement;
    }

    public void secondThread(){}

    public boolean isMouseOverElement(Vector2f mousePos)
    {
        try{return mousePos.x > pos.x && mousePos.y > pos.y && mousePos.x < pos.x + size.x && mousePos.y < pos.y + size.y;}catch (Exception e){}
        return false;
    }

    public boolean isMouseOverElement(Vector2d mousePos)
    {
        return isMouseOverElement(new Vector2f((float) mousePos.x, (float) mousePos.y));
    }

    public boolean isMouseOverElement(MouseInput mouseInput)
    {
        return isMouseOverElement(mouseInput.currentPos);
    }

    protected void drawImage(String path, float x, float y, float width, float height)
    {
        renderer.processGuiElement(new Quad(loader, path, new Vector2f(x, y), new Vector2f(width, height), false));
    }

    protected void drawImage(BufferedImage image, float x, float y, float width, float height)
    {
        renderer.processGuiElement(new Quad(loader, image, new Vector2f(x, y), new Vector2f(width, height), false));
    }

    protected void drawImage(int image, float x, float y, float width, float height)
    {
        renderer.processGuiElement(new Quad(loader, image, new Vector2f(x, y), new Vector2f(width, height), false));
    }

    protected void drawImageStatic(int image, float x, float y, float width, float height)
    {
        renderer.processGuiElement(new Quad(loader, image, new Vector2f(x, y), new Vector2f(width, height), false).staticTexture());
    }

    protected void drawRect(int x, int y, int width, int height, Color color) {
        renderer.processGuiElement(new Quad(loader, color, new Vector2f(x, y), new Vector2f(width, height), false).staticTexture());
    }

    protected void drawTriangle(Vector2f p1, Vector2f p2, Vector2f p3, Color color)
    {
        renderer.processGuiElement(new Triangle(loader, window, color, p1, p2, p3, false));
    }

    protected void drawCircle(Vector2f center, float radius, int tris, Color color)
    {
        renderer.processGuiElement(new Circle(loader, window, color, center, radius, tris, false));
    }

    protected void drawString(String text, Color color, int x, int y, int size)
    {
        FontRenderer.drawString(renderer, text, x, y, size, color, 0, text.length());
    }

    protected void drawString(String text, Color color, int x, int y, int size, int begin, int end)
    {
        FontRenderer.drawString(renderer, text, x, y, size, color, begin, end);
    }

    protected void startScissor(Vector2i pos, Vector2i size)
    {
        renderer.processGuiElement(Scissor.start(pos, size));
    }

    protected void startScissor(int x, int y, int width, int height)
    {
        renderer.processGuiElement(Scissor.start(new Vector2i(x, y), new Vector2i(width, height)));
    }

    protected void endScissor()
    {
        renderer.processGuiElement(Scissor.end());
    }

    protected int getStringWidth(String text, int size)
    {
        return (int)FontRenderer.getStringWidth(text, size);
    }

    protected int getFontHeight(int size)
    {
        return (int)FontRenderer.getFontHeight(size);
    }

    public boolean isFocused()
    {
        return focused;
    }

    public void setFocused(boolean focused)
    {
        this.focused = focused;
    }
}
