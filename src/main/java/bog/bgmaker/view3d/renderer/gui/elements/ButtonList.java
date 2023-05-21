package bog.bgmaker.view3d.renderer.gui.elements;

import bog.bgmaker.view3d.ObjectLoader;
import bog.bgmaker.view3d.managers.MouseInput;
import bog.bgmaker.view3d.managers.RenderMan;
import bog.bgmaker.view3d.managers.WindowMan;
import bog.bgmaker.view3d.utils.Const;
import cwlib.types.databases.FileEntry;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector2i;
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

        if(indexes == null)
        {
            updateSearch();
        }

        int height = buttonHeight();
        float maxScroll = (4 + (height + 2) * (indexes.size() - 1) + height) - (size.y);
        float frac = size.x - (size.x - 4f - size.x * 0.05f);
        float scrollX = pos.x + size.x - 4f - size.x * 0.05f + (frac / 2f);
        float scrollY = pos.y + (frac / 4f);
        float scrollHeight = size.y - (frac / 4f) * 2;

        if(scrolling)
             yScroll = -(((((float)mouseInput.currentPos.y) - (frac/2)) - scrollY)/(scrollHeight - frac)) * maxScroll;

        if(yScroll < -maxScroll)
            yScroll = -maxScroll;

        if(yScroll > 0)
            yScroll = 0;

        drawRect((int) scrollX, (int) scrollY, 3, (int) scrollHeight, buttonColor(null, -1));
        drawRectOutline((int) scrollX, (int) scrollY, 3, (int) scrollHeight, buttonColor2(null, -1), false);
        drawRect((int) scrollX, (int) (scrollY + ((Math.abs(yScroll) / maxScroll) * (scrollHeight - frac))), 3, (int) frac, textColor(null, -1));

        startScissor((int) pos.x, (int) scrollY, (int) size.x, (int) Math.ceil(scrollHeight));
        int ind = 0;

        for(int i : indexes)
        {
            T object = null;

            try{
                object = list.get(i);
            }catch (Exception e)
            {
                continue;
            }

            int posY = (int) (yScroll + pos.y + 2 + (height + 2) * ind);
            ind++;

            if(!overElement)
                if(mouseInput.currentPos.y <= scrollY + scrollHeight &&
                        mouseInput.currentPos.y >= scrollY)
                {
                    double posX = this.pos.x + 2d;
                    double width = size.x - 4d - size.x * 0.05d;

                    if(mouseInput.currentPos.x >= posX && mouseInput.currentPos.x <= posX + width &&
                            mouseInput.currentPos.y >= posY && mouseInput.currentPos.y <= posY + height)
                        hoveringButton(object, i);
                }

            if(posY <= pos.y + size.y &&
                    posY + height >= pos.y)
            {
                drawButton(posY, scrollY, scrollHeight, height, object, i);
            }
        }
        endScissor();
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
        return Const.FONT_COLOR;
    }
    public Color buttonColor(T object, int index)
    {
        return Const.INTERFACE_PRIMARY_COLOR;
    }
    public Color buttonColor2(T object, int index)
    {
        return Const.INTERFACE_PRIMARY_COLOR2;
    }
    public Color buttonColorHighlighted(T object, int index)
    {
        return Const.INTERFACE_SECONDARY_COLOR;
    }
    public Color buttonColorSelected(T object, int index)
    {
        return Const.INTERFACE_TERTIARY_COLOR;
    }
    public void drawButton(int posY, float scrollY, float scrollHeight, int height, T object, int i)
    {
        startScissor((int)pos.x + 4, posY, (int)(size.x - 6f - size.x * 0.05f), (int) height);
        drawRect((int)pos.x + 4, posY, (int)(size.x - 6f - size.x * 0.05f), (int) height, !(isHighlighted(object, i) || isSelected(object, i)) ? buttonColor(object, i) : (isSelected(object, i) ? buttonColorSelected(object, i) : buttonColorHighlighted(object, i)));
        drawString(buttonText(object, i), textColor(object, i), (int)(pos.x + (size.x - size.x * 0.05f) / 2f - getStringWidth(buttonText(object, i), fontSize) / 2), posY + height / 2 - getFontHeight(fontSize) / 2, fontSize);
        drawRectOutline((int)pos.x + 4, posY, (int)(size.x - 6f - size.x * 0.05f), (int) height, !(isHighlighted(object, i) || isSelected(object, i)) ? buttonColor(object, i) : (isSelected(object, i) ? buttonColorSelected(object, i) : buttonColorHighlighted(object, i)), false);
        endScissor();
    }
    public int buttonHeight()
    {
        return getFontHeight(fontSize);
    }

    public abstract void clickedButton(T object, int index, int button, int action, int mods);
    public abstract void hoveringButton(T object, int index);
    public abstract boolean isHighlighted(T object, int index);
    public abstract boolean isSelected(T object, int index);
    public abstract String buttonText(T object, int index);
    public abstract boolean searchFilter(T object, int index);
}
