package bog.lbpas.view3d.renderer.gui.elements;

import bog.lbpas.view3d.mainWindow.ConstantTextures;
import bog.lbpas.view3d.managers.assetLoading.ObjectLoader;
import bog.lbpas.view3d.core.Model;
import bog.lbpas.view3d.managers.MouseInput;
import bog.lbpas.view3d.managers.RenderMan;
import bog.lbpas.view3d.managers.WindowMan;
import bog.lbpas.view3d.renderer.gui.cursor.ECursor;
import bog.lbpas.view3d.renderer.gui.ingredients.Line;
import bog.lbpas.view3d.renderer.gui.ingredients.LineStrip;
import bog.lbpas.view3d.utils.Config;
import bog.lbpas.view3d.utils.Cursors;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * @author Bog
 */
public abstract class ButtonList<T> extends Element{

    public List<T> list;
    public int fontSize;
    float yScroll = 0;
    boolean scrolling = false;

    Vector2f prevSize;
    int prevButtonHeight;
    public Model outlineScrollbar;
    public Model listLine;
    public int lineOffset;
    public Model outlineButton;
    public Model outlineButtonExtra;

    public boolean deletable;
    public boolean draggable;

    public ButtonList(String id, List list, Vector2f pos, Vector2f size, int fontSize, RenderMan renderer, ObjectLoader loader, WindowMan window)
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
    }

    public ButtonList(List list, int fontSize, RenderMan renderer, ObjectLoader loader, WindowMan window)
    {
        this.id = id;
        this.list = list;
        this.renderer = renderer;
        this.loader = loader;
        this.window = window;
        this.fontSize = fontSize;
        this.pos = new Vector2f();
        this.size = new Vector2f();
        this.prevSize = size;
    }

    @Override
    public void onMouseScroll(Vector2d pos, double xOffset, double yOffset) {
        super.onMouseScroll(pos, xOffset, yOffset);

        if(!(pos.x >= this.pos.x && pos.x <= this.pos.x + this.size.x &&
                pos.y >= this.pos.y && pos.y <= this.pos.y + this.size.y))
            return;

        yScroll += yOffset * 50;
    }

    public ButtonList deletable()
    {
        this.deletable = true;
        return this;
    }

    public ButtonList draggable()
    {
        this.draggable = true;
        return this;
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
        if(this.list != null)
            for (int i = 0; i < this.list.size(); i++)
                if (searchFilter(this.list.get(i), i))
                    is.add(i);
        indexes = is;
    }

    @Override
    public void draw(MouseInput mouseInput, boolean overElement) {
        super.draw(mouseInput, overElement);

        hoveringDelete = -1;
        hoveringDrag = -1;

        int height = buttonHeight();

        if(outlineButton == null || ((deletable || draggable) && outlineButtonExtra == null) || size.x != prevSize.x || size.y != prevSize.y || prevButtonHeight != height)
        {
            refreshOutline(height);
            prevSize = size;
            prevButtonHeight = height;
        }

        if(indexes == null)
        {
            updateSearch();
        }

        float maxScroll = (4 + (height + 2) * (indexes.size() - 1) + height) - (size.y - 6);
        float frac = size.x - (getWidth() + 1);
        float scrollX = pos.x + (getWidth() + 1) + (frac / 2f);
        float scrollY = pos.y + 3;
        float scrollHeight = size.y - 6;

        if(scrolling)
             yScroll = -(((((float)mouseInput.currentPos.y) - (frac/2)) - scrollY)/(scrollHeight - frac)) * maxScroll;

        if(yScroll < -maxScroll)
            yScroll = -maxScroll;

        if(yScroll > 0)
            yScroll = 0;

        renderer.drawRect((int) scrollX, (int) scrollY, 3, (int) scrollHeight, buttonColor(null, -1));
        renderer.drawRectOutline(new Vector2f((int) scrollX, (int) scrollY), outlineScrollbar, buttonColor2(null, -1), false);
        renderer.drawRect((int) scrollX, (int) (scrollY + ((Math.abs(yScroll) / maxScroll) * (scrollHeight - (int) frac))), 3, (int) frac, textColor(null, -1));

        renderer.drawLine(listLine, new Vector2f((int) this.pos.x + 2 + lineOffset, (int) this.pos.y + 2), buttonColor2(null, -1), false);
        renderer.drawLine(listLine, new Vector2f((int) this.pos.x + 2 + lineOffset, (int) (this.pos.y + this.size.y - 3)), buttonColor2(null, -1), false);

        renderer.startScissor((int) pos.x, (int) scrollY, (int) size.x, (int) Math.ceil(scrollHeight));
        int ind = 0;

        for(int i : indexes)
        {
            T object = null;

            if(!(this.list.size() <= i))
                object = this.list.get(i);

            if(object == null)
                continue;

            int posY = (int) (yScroll + pos.y + 5 + (height + 2) * ind);
            ind++;

            if(posY <= pos.y + size.y &&
                    posY + height >= pos.y)
            {
                if(!overElement)
                    if(mouseInput.currentPos.y <= scrollY + scrollHeight &&
                            mouseInput.currentPos.y >= scrollY)
                    {
                        double posX = this.pos.x + 2d;
                        double width = getWidth() + 2 - ((height + 2) * ((deletable ? 1 : 0) + (draggable ? 1 : 0)));
                        if(mouseInput.currentPos.x >= posX && mouseInput.currentPos.x <= posX + width &&
                                mouseInput.currentPos.y >= posY && mouseInput.currentPos.y <= posY + height)
                        {
                            hoveringCursor();
                            hoveringButton(object, i);
                        }
                    }
                drawButton(posY, scrollY, scrollHeight, height, object, i, mouseInput, overElement);
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
    public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement, boolean focusedOther) {
        super.onClick(mouseInput, pos, button, action, mods, overElement, focusedOther);

        if(!overElement)
        {
            if(pos.x >= this.pos.x && pos.x <= this.pos.x + size.x &&
                    pos.y >= this.pos.y && pos.y <= this.pos.y + size.y)
            {
                if(indexes == null)
                    updateSearch();

                for(int i : indexes)
                {
                    T object = null;

                    try{
                        object = this.list.get(i);
                    }catch (Exception e)
                    {
                        continue;
                    }

                    int height = buttonHeight();
                    float newWidthF = getWidth() - ((height + 2) * ((deletable ? 1 : 0) + (draggable ? 1 : 0)));
                    int newWidth = Math.round(newWidthF);
                    int newHeight = height;

                    if(isHighlighted(object, i))
                    {
                        double posX = this.pos.x + 2d;
                        double width = size.x - 4d - size.x * 0.05d;
                        clickedButton(object, i, button, action, mods);
                    }

                    if(hoveringDelete == i && button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)
                        delete(i);
                }
            }

            boolean hoveringScroll = pos.x > this.pos.x + getWidth() + 1 &&
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

    int hoveringDelete = -1;
    int hoveringDrag = -1;

    public void drawButton(int posY, float scrollY, float scrollHeight, int height, T object, int i, MouseInput mouseInput, boolean overElement)
    {
        float newWidthF = getWidth() - ((height + 2) * ((deletable ? 1 : 0) + (draggable ? 1 : 0)));
        int newWidth = Math.round(newWidthF);
        int newHeight = height;
        renderer.startScissor(Math.round(pos.x + 4), Math.round(posY), newWidth, newHeight);
        renderer.drawRect(Math.round(pos.x + 4), Math.round(posY), newWidth, newHeight, !(isHighlighted(object, i) || isSelected(object, i)) ? buttonColor(object, i) : (isSelected(object, i) ? buttonColorSelected(object, i) : buttonColorHighlighted(object, i)));
        renderer.drawString(buttonText(object, i), textColor(object, i), Math.round(pos.x + 6f + ((newWidthF / 2f) - (getStringWidth(buttonText(object, i), fontSize) / 2f))), Math.round(posY + height / 2 - getFontHeight(fontSize) / 2), fontSize);
        renderer.drawRectOutline(new Vector2f(Math.round(pos.x + 4), Math.round(posY)), outlineButton, !(isHighlighted(object, i) || isSelected(object, i)) ? buttonColor2(object, i) : (isSelected(object, i) ? buttonColorSelected2(object, i) : buttonColorHighlighted2(object, i)), false);
        renderer.endScissor();

        if(deletable)
        {
            int x = Math.round(pos.x + 4) + newWidth + 2 + (draggable ? height + 2 : 0);
            boolean hovering = false;
            if(!overElement)
                if(mouseInput.currentPos.y <= scrollY + scrollHeight &&
                        mouseInput.currentPos.y >= scrollY)
                {
                    double posX = this.pos.x + 4d + newWidth + 2 + (draggable ? height + 2 : 0);
                    if(mouseInput.currentPos.x >= posX && mouseInput.currentPos.x <= posX + height &&
                            mouseInput.currentPos.y >= posY && mouseInput.currentPos.y <= posY + height)
                    {
                        hovering = true;
                        hoveringDelete = i;
                        Cursors.setCursor(ECursor.hand2);
                    }
                }

            renderer.drawRect(x, Math.round(posY), newHeight, newHeight, !hovering ? buttonColor(object, i) : buttonColorHighlighted(object, i));
            renderer.drawImageStatic(ConstantTextures.getTexture(ConstantTextures.WINDOW_CLOSE, newHeight, newHeight, loader), x, Math.round(posY), newHeight, newHeight);
            renderer.drawRectOutline(new Vector2f(x, Math.round(posY)), outlineButtonExtra, !hovering ? buttonColor2(object, i) : buttonColorHighlighted2(object, i), false);
        }
        if(draggable)
        {
            boolean hovering = false;
            if(!overElement)
                if(mouseInput.currentPos.y <= scrollY + scrollHeight &&
                        mouseInput.currentPos.y >= scrollY)
                {
                    double posX = this.pos.x + 4d + newWidth + 2;
                    if(mouseInput.currentPos.x >= posX && mouseInput.currentPos.x <= posX + height &&
                            mouseInput.currentPos.y >= posY && mouseInput.currentPos.y <= posY + height)
                    {
                        hovering = true;
                        hoveringDrag = i;
                        Cursors.setCursor(ECursor.hand2);
                    }
                }

            renderer.drawRect(Math.round(pos.x + 4) + newWidth + 2, Math.round(posY), newHeight, newHeight, !hovering ? buttonColor(object, i) : buttonColorHighlighted(object, i));
            renderer.drawImageStatic(ConstantTextures.getTexture(ConstantTextures.DRAG, newHeight, newHeight, loader), Math.round(pos.x + 4) + newWidth + 2, Math.round(posY), newHeight, newHeight);
            renderer.drawRectOutline(new Vector2f(Math.round(pos.x + 4) + newWidth + 2, Math.round(posY)), outlineButtonExtra, !hovering ? buttonColor2(object, i) : buttonColorHighlighted2(object, i), false);
        }
    }
    public int buttonHeight()
    {
        return getFontHeight(fontSize);
    }
    public Model getOutlineButton(int height)
    {
        int newWidth = Math.round(getWidth() - ((height + 2) * ((deletable ? 1 : 0) + (draggable ? 1 : 0))));
        return LineStrip.processVerts(LineStrip.getRectangle(new Vector2f(newWidth, height)), loader, window);
    }
    public float getWidth()
    {
        return size.x - 5f - size.x * 0.05f;
    }
    public void refreshOutline(int height)
    {
        if(outlineScrollbar != null)
            this.outlineScrollbar.cleanup(loader);
        if(outlineButton != null)
            this.outlineButton.cleanup(loader);
        if(outlineButtonExtra != null)
            this.outlineButtonExtra.cleanup(loader);
        if(listLine != null)
            this.listLine.cleanup(loader);
        this.outlineScrollbar = LineStrip.processVerts(LineStrip.getRectangle(new Vector2f(3, size.y - 6)), loader, window);
        this.listLine = Line.getLine(window, loader, new Vector2i(0), new Vector2i((int) (size.x - 1.0f - size.x * 0.05f), 0));
        this.outlineButton = getOutlineButton(height);

        if(deletable || draggable)
            this.outlineButtonExtra = LineStrip.processVerts(LineStrip.getRectangle(new Vector2f(height)), loader, window);
    }
    public abstract void clickedButton(T object, int index, int button, int action, int mods);
    public abstract void hoveringButton(T object, int index);
    public void hoveringCursor()
    {
        Cursors.setCursor(ECursor.hand2);
    }
    public abstract boolean isHighlighted(T object, int index);
    public abstract boolean isSelected(T object, int index);
    public abstract String buttonText(T object, int index);
    public abstract boolean searchFilter(T object, int index);
    public void delete(int index)
    {
        this.list.remove(index);
    }
}
