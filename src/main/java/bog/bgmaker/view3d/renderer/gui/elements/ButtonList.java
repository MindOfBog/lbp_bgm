package bog.bgmaker.view3d.renderer.gui.elements;

import bog.bgmaker.view3d.ObjectLoader;
import bog.bgmaker.view3d.core.Model;
import bog.bgmaker.view3d.managers.MouseInput;
import bog.bgmaker.view3d.managers.RenderMan;
import bog.bgmaker.view3d.managers.WindowMan;
import bog.bgmaker.view3d.renderer.gui.cursor.ECursor;
import bog.bgmaker.view3d.renderer.gui.ingredients.LineStrip;
import bog.bgmaker.view3d.renderer.gui.ingredients.Quad;
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
public abstract class ButtonList<T> extends Element{

    public ArrayList<T> list;
    public int fontSize;
    float yScroll = 0;
    boolean scrolling = false;

    Vector2f prevSize;
    int prevButtonHeight;
    Model outlineScrollbar;
    public Model outlineButton;

    public ButtonList(String id, ArrayList<T> list, Vector2f pos, Vector2f size, int fontSize, RenderMan renderer, ObjectLoader loader, WindowMan window)
    {
        this.id = id;
        this.list = list;
        this.renderer = renderer;
        this.loader = loader;
        this.window = window;
        this.fontSize = fontSize;
        this.pos = pos;
        this.size = size;
        this.prevSize = size;
        this.outlineScrollbar = LineStrip.processVerts(LineStrip.getRectangle(new Vector2f(3, (int) size.y)), loader, window);
        this.outlineButton = getOutlineButton(buttonHeight());
    }

    @Override
    public void onMouseScroll(Vector2d pos, double xOffset, double yOffset) {
        super.onMouseScroll(pos, xOffset, yOffset);

        if(!(pos.x >= this.pos.x && pos.x <= this.pos.x + this.size.x &&
                pos.y >= this.pos.y && pos.y <= this.pos.y + this.size.y))
            return;

        yScroll += yOffset * 50;
    }

    @Override
    public void secondThread() {
        super.secondThread();

        updateSearch();
    }

    public ArrayList<Integer> indexes;

    public void updateSearch()
    {
        ArrayList<Integer> is = new ArrayList<>();
        for (int i = 0; i < list.size(); i++)
            if (searchFilter(list.get(i), i))
                is.add(i);
        indexes = is;
    }

    @Override
    public void draw(MouseInput mouseInput, boolean overElement) {
        super.draw(mouseInput, overElement);

        int height = buttonHeight();

        if(size.x != prevSize.x || size.y != prevSize.y || prevButtonHeight != height)
        {
            refreshOutline(height);
            prevSize = size;
            prevButtonHeight = height;
        }

        if(indexes == null)
        {
            updateSearch();
        }

        float maxScroll = (4 + (height + 2) * (indexes.size() - 1) + height) - (size.y);
        float frac = size.x - (size.x - 4f - size.x * 0.05f);
        float scrollX = pos.x + size.x - 4f - size.x * 0.05f + (frac / 2f);
        float scrollY = pos.y;
        float scrollHeight = size.y;

        if(scrolling)
             yScroll = -(((((float)mouseInput.currentPos.y) - (frac/2)) - scrollY)/(scrollHeight - frac)) * maxScroll;

        if(yScroll < -maxScroll)
            yScroll = -maxScroll;

        if(yScroll > 0)
            yScroll = 0;

        renderer.drawRect((int) scrollX, (int) scrollY, 3, (int) scrollHeight, buttonColor(null, -1));
        renderer.drawRectOutline(new Vector2f((int) scrollX, (int) scrollY), outlineScrollbar, buttonColor2(null, -1), false);
        renderer.drawRect((int) scrollX, (int) (scrollY + ((Math.abs(yScroll) / maxScroll) * (scrollHeight - frac))), 3, (int) frac, textColor(null, -1));

        renderer.startScissor((int) pos.x, (int) scrollY, (int) size.x, (int) Math.ceil(scrollHeight));
        int ind = 0;

        for(int i : indexes)
        {
            T object = null;

            if(!(list.size() <= i))
                object = list.get(i);

            if(object == null)
                continue;

            int posY = (int) (yScroll + pos.y + 2 + (height + 2) * ind);
            ind++;

            if(posY <= pos.y + size.y &&
                    posY + height >= pos.y)
            {
                if(!overElement)
                    if(mouseInput.currentPos.y <= scrollY + scrollHeight &&
                            mouseInput.currentPos.y >= scrollY)
                    {
                        double posX = this.pos.x + 2d;
                        double width = size.x - 4d - size.x * 0.05d;

                        if(mouseInput.currentPos.x >= posX && mouseInput.currentPos.x <= posX + width &&
                                mouseInput.currentPos.y >= posY && mouseInput.currentPos.y <= posY + height)
                        {
                            hoveringButton(object, i);
                            Cursors.setCursor(ECursor.hand2);
                        }
                    }
                drawButton(posY, scrollY, scrollHeight, height, object, i);
            }
        }
        renderer.endScissor();
    }
    @Override
    public void resize() {
        super.resize();
        refreshOutline(buttonHeight());
    }
    @Override
    public void onClick(Vector2d pos, int button, int action, int mods, boolean overElement) {
        super.onClick(pos, button, action, mods, overElement);

        if(!overElement)
        {
            if(pos.x >= this.pos.x && pos.x <= this.pos.x + size.x &&
                    pos.y >= this.pos.y && pos.y <= this.pos.y + size.y)
            {
                if(indexes == null)
                {
                    updateSearch();
                }

                for(int i : indexes)
                {
                    T object = null;

                    try{
                        object = list.get(i);
                    }catch (Exception e)
                    {
                        continue;
                    }

                    if(isHighlighted(object, i))
                    {
                        double posX = this.pos.x + 2d;
                        double width = size.x - 4d - size.x * 0.05d;
                        double height = getFontHeight(fontSize);
                        clickedButton(object, i, button, action, mods);
                    }
                }
            }

            boolean hoveringScroll = pos.x > this.pos.x + size.x - 4f - size.x * 0.05f &&
                    pos.x < this.pos.x + size.x &&
                    pos.y > this.pos.y &&
                    pos.y < this.pos.y + size.y;

            if(button == GLFW.GLFW_MOUSE_BUTTON_1 && hoveringScroll && action == GLFW.GLFW_PRESS)
                scrolling = true;
        }

        if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_RELEASE)
            scrolling = false;
    }
    public Color textColor(T object, int index)
    {
        return Config.FONT_COLOR;
    }
    public Color buttonColor(T object, int index)
    {
        return Config.INTERFACE_PRIMARY_COLOR;
    }
    public Color buttonColor2(T object, int index)
    {
        return Config.INTERFACE_PRIMARY_COLOR2;
    }
    public Color buttonColorHighlighted(T object, int index)
    {
        return Config.INTERFACE_SECONDARY_COLOR;
    }
    public Color buttonColorHighlighted2(T object, int index)
    {
        return Config.INTERFACE_SECONDARY_COLOR2;
    }
    public Color buttonColorSelected(T object, int index)
    {
        return Config.INTERFACE_TERTIARY_COLOR;
    }
    public Color buttonColorSelected2(T object, int index)
    {
        return Config.INTERFACE_TERTIARY_COLOR2;
    }

    public void drawButton(int posY, float scrollY, float scrollHeight, int height, T object, int i)
    {
        renderer.startScissor(Math.round(pos.x + 4), Math.round(posY), Math.round(size.x - 6f - size.x * 0.05f), Math.round(height));
        renderer.drawRect(Math.round(pos.x + 4), Math.round(posY), Math.round(size.x - 6f - size.x * 0.05f), Math.round(height), !(isHighlighted(object, i) || isSelected(object, i)) ? buttonColor(object, i) : (isSelected(object, i) ? buttonColorSelected(object, i) : buttonColorHighlighted(object, i)));
        renderer.drawString(buttonText(object, i), textColor(object, i), Math.round(pos.x + (size.x - size.x * 0.05f) / 2f - getStringWidth(buttonText(object, i), fontSize) / 2), Math.round(posY + height / 2 - getFontHeight(fontSize) / 2), fontSize);
        renderer.drawRectOutline(new Vector2f(Math.round(pos.x + 4), Math.round(posY)), outlineButton, !(isHighlighted(object, i) || isSelected(object, i)) ? buttonColor2(object, i) : (isSelected(object, i) ? buttonColorSelected2(object, i) : buttonColorHighlighted2(object, i)), false);
        renderer.endScissor();
    }
    public int buttonHeight()
    {
        return getFontHeight(fontSize);
    }
    public Model getOutlineButton(int height)
    {
        return LineStrip.processVerts(LineStrip.getRectangle(new Vector2f(size.x - 6f - size.x * 0.05f, height)), loader, window);
    }
    public void refreshOutline(int height)
    {
        if(outlineScrollbar != null)
            this.outlineScrollbar.cleanup(loader);
        if(outlineButton != null)
            this.outlineButton.cleanup(loader);
        this.outlineScrollbar = LineStrip.processVerts(LineStrip.getRectangle(new Vector2f(3, (int) size.y)), loader, window);
        this.outlineButton = getOutlineButton(height);
    }
    public abstract void clickedButton(T object, int index, int button, int action, int mods);
    public abstract void hoveringButton(T object, int index);
    public abstract boolean isHighlighted(T object, int index);
    public abstract boolean isSelected(T object, int index);
    public abstract String buttonText(T object, int index);
    public abstract boolean searchFilter(T object, int index);
}
