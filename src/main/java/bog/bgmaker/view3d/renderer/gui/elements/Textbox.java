package bog.bgmaker.view3d.renderer.gui.elements;

import bog.bgmaker.view3d.ObjectLoader;
import bog.bgmaker.view3d.core.Model;
import bog.bgmaker.view3d.managers.MouseInput;
import bog.bgmaker.view3d.managers.RenderMan;
import bog.bgmaker.view3d.managers.WindowMan;
import bog.bgmaker.view3d.renderer.gui.cursor.ECursor;
import bog.bgmaker.view3d.renderer.gui.ingredients.LineStrip;
import bog.bgmaker.view3d.utils.Config;
import bog.bgmaker.view3d.utils.Consts;
import bog.bgmaker.view3d.utils.Cursors;
import bog.bgmaker.view3d.utils.Utils;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;

/**
 * @author Bog
 */
public class Textbox extends Element{

    private String text = "";
    int fontSize;
    boolean numbers = true;
    boolean letters = true;
    boolean others = true;

    Vector2f prevSize;
    Model outlineRect;

    float[] numberLimits = new float[0];

    public Textbox(String id, Vector2f pos, Vector2f size, int fontSize, RenderMan renderer, ObjectLoader loader, WindowMan window)
    {
        this.id = id;
        this.pos = pos;
        this.size = size;
        this.fontSize = fontSize;
        this.renderer = renderer;
        this.loader = loader;
        this.window = window;
    }

    public Textbox noNumbers()
    {
        this.numbers = false;
        return this;
    }
    public Textbox noLetters()
    {
        this.letters = false;
        return this;
    }
    public Textbox noOthers()
    {
        this.others = false;
        return this;
    }

    public Textbox numberLimits(float min, float max)
    {
        this.numberLimits = new float[]{min, max};
        return this;
    }

    @Override
    public void draw(MouseInput mouseInput, boolean overOther) {
        super.draw(mouseInput, overOther);

        if(prevSize == null || size.x != prevSize.x || size.y != prevSize.y)
        {
            refreshOutline();
            prevSize = size;
        }

        renderer.startScissor((int) Math.round(pos.x), (int) Math.round(pos.y), (int) Math.round(size.x), (int) Math.round(size.y));
        renderer.drawRect((int) Math.round(pos.x), (int) Math.round(pos.y), (int) Math.round(size.x), (int) Math.round(size.y), isMouseOverElement(mouseInput) && !overOther || this.isFocused() ? Config.INTERFACE_SECONDARY_COLOR : Config.INTERFACE_PRIMARY_COLOR);

        float xScroll = 0;
        if(!this.isFocused())
        {
            currentSelection = text.length();
            selectedText[0] = -1;
            selectedText[1] = -1;
        }
        boolean bool = false;

        try {
            String cursor = this.isFocused() ? currentSelection == text.length() ? "_" : "|" : "";
            if (currentSelection == text.length() && getStringWidth(text + cursor, fontSize) > this.size.x - (this.size.y / 2 - getFontHeight(fontSize) / 2) * 2)
                xScroll = -(getStringWidth(text + cursor, fontSize) - this.size.x + (this.size.y / 2 - getFontHeight(fontSize) / 2) * 2);

            else if (getStringWidth((text.substring(0, currentSelection) + cursor), fontSize) > this.size.x - (this.size.y / 2 - getFontHeight(fontSize) / 2) * 2)
                xScroll = -(getStringWidth(text.substring(0, currentSelection) + cursor, fontSize) - this.size.x + (this.size.y / 2 - getFontHeight(fontSize) / 2) * 2);
        }catch(Exception e){}

        int begin = 0;
        int end = text.length();

        for(int i = 0; i < text.length(); i++)
        {
            try
            {
                if((int) Math.round(pos.x + xScroll + this.size.y / 2 - getFontHeight(fontSize) / 2) + getStringWidth(text.substring(0, i), fontSize) < pos.x)
                    begin = i;

                if((int) Math.round(pos.x + xScroll + this.size.y / 2 - getFontHeight(fontSize) / 2) + getStringWidth(text.substring(0, i + 1), fontSize) < pos.x + size.x)
                    end = i + 1;
            }catch (Exception e){}
        }

        renderer.drawString(text, textColor(), (int) Math.round(pos.x + xScroll + this.size.y / 2 - getFontHeight(fontSize) / 2), (int) Math.round(pos.y + this.size.y / 2 - getFontHeight(fontSize) / 2), fontSize, begin, end);

        try {
            if(500 > (System.currentTimeMillis() - Consts.startMillis) % 1000 && isFocused())
                if(currentSelection == text.length())
                    renderer.drawString("_", textColor(), (int) Math.round(pos.x + xScroll + getStringWidth(text, fontSize) + 1 + this.size.y/2 - getFontHeight(fontSize)/2), (int) Math.round(pos.y + this.size.y/2 - getFontHeight(fontSize)/2), fontSize);
                else
                {
                    bool = true;
                }

            float[] pos1 = {pos.x + xScroll + this.size.y/2 - getFontHeight(fontSize)/2, pos.y + this.size.y/2 - getFontHeight(fontSize)/2};

            int x = (int) Math.round(pos1[0] + getStringWidth(text.substring(0, selectedText[0]), fontSize));
            int diff = 0;
            if(x < pos.x)
            {
                diff = x - (int) Math.round(pos.x);
                x = (int) Math.round(pos.x);
            }

            int width = (int) Math.round(getStringWidth(text.substring(selectedText[0], selectedText[1]), fontSize) + 1 + diff);
            if(x + width > pos.x + size.x)
                width = (int) Math.round(pos.x + size.x - x);

            renderer.drawRect(x, (int) Math.round(pos1[1] - 1), width, getFontHeight(fontSize) + 2, new Color(0f, 0f, 1f, 0.5f));

        }catch(Exception e)
        {
            selectedText[0] = -1;
            selectedText[1] = -1;
        }

        if(bool)
        {
            try
            {
                renderer.drawRect((int) Math.round(xScroll + pos.x + getStringWidth(text.substring(0, currentSelection), fontSize) + this.size.y/2 - getFontHeight(fontSize)/2), (int) Math.round(pos.y + this.size.y/2 - getFontHeight(fontSize)/2 - 1), 1, (int) Math.round(getFontHeight(fontSize)), textColor());
            }catch (Exception e){}
        }
        renderer.drawRectOutline(new Vector2f((int) Math.round(pos.x), (int) Math.round(pos.y)), outlineRect, isMouseOverElement(mouseInput) && !overOther || this.isFocused() ? Config.INTERFACE_SECONDARY_COLOR2 : Config.INTERFACE_PRIMARY_COLOR2, false);
        renderer.endScissor();
    }
    @Override
    public void resize() {
        super.resize();
        refreshOutline();
    }
    public void refreshOutline()
    {
        if(this.outlineRect != null)
            this.outlineRect.cleanup(loader);
        this.outlineRect = LineStrip.processVerts(LineStrip.getRectangle(new Vector2f((int) Math.round(size.x), (int) Math.round(size.y))), loader, window);
    }
    @Override
    public void onClick(Vector2d pos, int button, int action, int mods, boolean overOther) {

        if(isMouseOverElement(pos) && !overOther)
        {
            if(action == GLFW.GLFW_PRESS && button == GLFW.GLFW_MOUSE_BUTTON_1)
            {
                this.setFocused(true);

                float xScroll = 0;

                try {
                    String cursor = this.isFocused() ? currentSelection == text.length() ? "_" : "|" : "";
                    if(currentSelection == text.length() && getStringWidth(text + cursor, fontSize) > this.size.x - (this.size.y/2 - getFontHeight(fontSize)/2) * 2)
                        xScroll = -(getStringWidth(text + cursor, fontSize) - this.size.x + (this.size.y/2 - getFontHeight(fontSize)/2) * 2);

                    else if(getStringWidth(text.substring(0, currentSelection) + cursor, fontSize) > this.size.x - (this.size.y/2 - getFontHeight(fontSize)/2) * 2)
                        xScroll = -(getStringWidth(text.substring(0, currentSelection) + cursor, fontSize) - this.size.x + (this.size.y/2 - getFontHeight(fontSize)/2) * 2);
                }catch(Exception e){}

                for(int i = 0; i <= text.length(); i++)
                    if (pos.x > this.pos.x + (i - 1 < 0 ? 0 : getStringWidth(text.substring(0, i - 1), fontSize)) + xScroll &&
                            pos.x < this.pos.x + getStringWidth(text.substring(0, i), fontSize) + xScroll &&
                            pos.y > this.pos.y + (this.size.y / 2 - getFontHeight(fontSize) / 2) &&
                            pos.y < this.pos.y + this.size.y - (this.size.y / 2 - getFontHeight(fontSize) / 2)) {


                        if(Math.abs(this.pos.x + (i - 1 < 0 ? 0 : getStringWidth(text.substring(0, i - 1), fontSize)) + xScroll - pos.x) <
                                Math.abs(this.pos.x + getStringWidth(text.substring(0, i), fontSize) + xScroll - pos.x))
                            currentSelection = i - 2;
                        else
                            currentSelection = i - 1;

                        selectedText[0] = -1;
                        selectedText[1] = -1;
                    }

                if(text.length() - 1 >= 0)
                    if(this.pos.x + getStringWidth(text, fontSize) + getStringWidth(text.substring(text.length() - 1, text.length()), fontSize)/2 + xScroll < pos.x)
                    {
                        currentSelection = text.length();
                        selectedText[0] = -1;
                        selectedText[1] = -1;
                    }
                if(this.pos.x + xScroll > pos.x)
                {
                    currentSelection = 0;
                    selectedText[0] = -1;
                    selectedText[1] = -1;
                }
            }
        }
        else
        {
            if(action == GLFW.GLFW_PRESS)
                this.setFocused(false);
        }

        super.onClick(pos, button, action, mods, overOther);
    }

    int[] selectedText = {-1, -1};
    int currentSelection;

    @Override
    public void onKey(int key, int scancode, int action, int mods) {

        if(this.isFocused() && action != 0)
        {
            boolean bool = false;

            boolean shift = mods == 1;
            boolean ctrl = mods == 2;
            boolean ctrlShift = mods == 3;
            boolean alt = mods == 4;
            boolean shiftAlt = mods == 5;
            boolean ctrlAlt = mods == 6;
            boolean ctrlShiftAlt = mods == 7;
            boolean winKey = mods == 8;

            if(ctrl && key == GLFW.GLFW_KEY_V)
            {
                String clipboard = "";

                try
                {
                    clipboard = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
                }catch (Exception e){}

                if(!letters)
                    clipboard = clipboard.replaceAll("[a-zA-Z]", "");
                if(!numbers)
                    clipboard = clipboard.replaceAll("[\\d]","");
                if(!others)
                    clipboard = clipboard.replaceAll(numbers ? "[^\\w.]" : "[^\\w]","");

                try {
                    if(selectedText[1] != -1 && selectedText[0] != -1)
                    {
                        text = text.substring(0, selectedText[0]) + clipboard + text.substring(selectedText[1], text.length());
                        currentSelection = selectedText[0] + clipboard.length();
                        selectedText[0] = -1;
                        selectedText[1] = -1;
                        bool = true;
                    }
                    else if (currentSelection == text.length())
                        text += clipboard;
                    else
                    {
                        bool = true;
                        text = text.substring(0, currentSelection) + clipboard + text.substring(currentSelection, text.length());
                        currentSelection += clipboard.length();
                    }
                }catch (Exception e){}
            }
            else if(key == GLFW.GLFW_KEY_BACKSPACE) {
                try {

                    if(selectedText[1] != -1 && selectedText[0] != -1)
                    {
                        currentSelection = selectedText[0];
                        text = text.substring(0, selectedText[0]) + text.substring(selectedText[1], text.length());
                        selectedText[0] = -1;
                        selectedText[1] = -1;
                        bool = true;
                    }
                    else if (currentSelection == text.length())
                        text = text.substring(0, text.length() - 1);
                    else
                    {
                        bool = true;
                        text = text.substring(0, currentSelection - 1) + text.substring(currentSelection, text.length());
                        currentSelection--;
                    }
                }catch (Exception e){}
            }
            else if(key == GLFW.GLFW_KEY_LEFT)
            {
                int prevSelection = 0;
                if(currentSelection != 0)
                {
                    if(!ctrl && !ctrlShift)
                    {
                        prevSelection = currentSelection;
                        currentSelection--;
                    }
                    else
                    {
                        prevSelection = currentSelection;
                        int indexSpace = text.lastIndexOf(" ", currentSelection) == -1 ? 0 : text.lastIndexOf(" ", currentSelection) == currentSelection ? text.lastIndexOf(" ", currentSelection - 1) == -1 ? 0 : text.lastIndexOf(" ", currentSelection - 1) + 1 : text.lastIndexOf(" ", currentSelection) + (currentSelection == text.length() ? 1 : 0);
                        int indexDash = text.lastIndexOf("-", currentSelection) == -1 ? 0 : text.lastIndexOf("-", currentSelection) == currentSelection ? text.lastIndexOf("-", currentSelection - 1) == -1 ? 0 : text.lastIndexOf("-", currentSelection - 1) + 1 : text.lastIndexOf("-", currentSelection) + (currentSelection == text.length() ? 1 : 0);

                        if(Math.abs(currentSelection - indexSpace) < Math.abs(currentSelection - indexDash))
                        {
                            if(currentSelection != indexSpace)
                                currentSelection = indexSpace;
                            else
                                currentSelection = indexSpace - 1;
                        }
                        else
                        {
                            if(currentSelection != indexDash)
                                currentSelection = indexDash;
                            else
                                currentSelection = indexDash - 1;
                        }
                    }
                }

                if(!shift && !ctrlShift)
                {
                    selectedText[0] = -1;
                    selectedText[1] = -1;
                }

                if((shift || ctrlShift) && selectedText[1] == currentSelection + (prevSelection - currentSelection) && selectedText[1] != -1)
                {
                    if(currentSelection == selectedText[0])
                    {
                        selectedText[0] = -1;
                        selectedText[1] = -1;
                    }
                    else
                        selectedText[1] = currentSelection;
                }
                else
                if((shift || ctrlShift) && selectedText[0] == currentSelection + (prevSelection - currentSelection) && selectedText[0] != -1)
                {
                    if(currentSelection == selectedText[1])
                    {
                        selectedText[0] = -1;
                        selectedText[1] = -1;
                    }
                    else
                        selectedText[0] = currentSelection;
                }
                else
                if((shift || ctrlShift) && (selectedText[1] == -1 || selectedText[0] == -1))
                {
                    selectedText[1] = currentSelection + (prevSelection - currentSelection);
                    selectedText[0] = currentSelection;
                }

                bool = true;
            }
            else if(key == GLFW.GLFW_KEY_RIGHT)
            {
                int prevSelection = 0;
                if(currentSelection != text.length())
                {
                    if(!ctrl && !ctrlShift)
                    {
                        prevSelection = currentSelection;
                        currentSelection++;
                    }
                    else
                    {
                        prevSelection = currentSelection;
                        int indexSpace = text.indexOf(" ", currentSelection) == -1 ? text.length() : text.indexOf(" ", currentSelection) == currentSelection ? (currentSelection + 1) > text.length() ? currentSelection : currentSelection + 1 : text.indexOf(" ", currentSelection);
                        int indexDash = text.indexOf("-", currentSelection) == -1 ? text.length() : text.indexOf("-", currentSelection) == currentSelection ? (currentSelection + 1) > text.length() ? currentSelection : currentSelection + 1 : text.indexOf("-", currentSelection);

                        if(Math.abs(currentSelection - indexSpace) < Math.abs(currentSelection - indexDash))
                            currentSelection = indexSpace;
                        else
                            currentSelection = indexDash;
                    }
                }

                if(!shift && !ctrlShift)
                {
                    selectedText[0] = -1;
                    selectedText[1] = -1;
                }

                if((shift || ctrlShift) && selectedText[1] == currentSelection - (currentSelection - prevSelection) && selectedText[1] != -1)
                {
                    if(currentSelection == selectedText[0])
                    {
                        selectedText[0] = -1;
                        selectedText[1] = -1;
                    }
                    else
                        selectedText[1] = currentSelection;
                }
                else
                if((shift || ctrlShift) && selectedText[0] == currentSelection - (currentSelection - prevSelection) && selectedText[0] != -1)
                {
                    if(currentSelection == selectedText[1])
                    {
                        selectedText[0] = -1;
                        selectedText[1] = -1;
                    }
                    else
                        selectedText[0] = currentSelection;
                }
                else
                if((shift || ctrlShift) && (selectedText[1] == -1 || selectedText[0] == -1))
                {
                    selectedText[1] = currentSelection;
                    selectedText[0] = currentSelection - (currentSelection - prevSelection);
                }

                bool = true;
            }
            else if(key == GLFW.GLFW_KEY_A && ctrl)
            {
                currentSelection = text.length();
                selectedText[0] = 0;
                selectedText[1] = text.length();
                bool = true;
            }
            else if(key == GLFW.GLFW_KEY_C && ctrl)
            {
                if(selectedText[1] != -1 && selectedText[0] != -1)
                {
                    try {

                        StringSelection selection = new StringSelection(text.substring(selectedText[0], selectedText[1]));
                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        clipboard.setContents(selection, selection);
                    }catch (Exception e){}

                    bool = true;
                }
            }
            else if(key == GLFW.GLFW_KEY_X && ctrl)
            {
                if(selectedText[1] != -1 && selectedText[0] != -1)
                {
                    try {

                        StringSelection selection = new StringSelection(text.substring(selectedText[0], selectedText[1]));
                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        clipboard.setContents(selection, selection);
                    }catch (Exception e){}

                    currentSelection = selectedText[0];
                    text = text.substring(0, selectedText[0]) + text.substring(selectedText[1], text.length());
                    selectedText[0] = -1;
                    selectedText[1] = -1;

                    bool = true;
                }
            }
            else if (key == GLFW.GLFW_KEY_ESCAPE)
                this.setFocused(false);
            else
                bool = true;

            if(!bool)
                currentSelection = text.length();
        }

        if(numberLimits.length == 2)
        {
            float min = numberLimits[0];
            float max = numberLimits[1];

            float number = Utils.parseFloat(text);

            if(number > max)
                text = number % 1 == 0 ? Integer.toString((int) max) : Float.toString(max);
            if(number < min)
                text = number % 1 == 0 ? Integer.toString((int) min) : Float.toString(min);
        }

        super.onKey(key, scancode, action, mods);
    }

    @Override
    public void onChar(int codePoint, int modifiers) {

        if(this.isFocused()) {
            boolean bool = false;

            boolean shift = modifiers == 1;
            boolean ctrl = modifiers == 2;
            boolean ctrlShift = modifiers == 3;
            boolean alt = modifiers == 4;
            boolean shiftAlt = modifiers == 5;
            boolean ctrlAlt = modifiers == 6;
            boolean ctrlShiftAlt = modifiers == 7;
            boolean winKey = modifiers == 8;

            String s = Character.toString(codePoint);

            if(!letters)
                s = s.replaceAll("[a-zA-Z]", "");
            if(!numbers)
                s = s.replaceAll("[\\d]","");
            if(!others)
                s = s.replaceAll(numbers ? "[^\\w.-]" : "[^\\w]","");

            if (s != null) {
                if (selectedText[1] != -1 && selectedText[0] != -1) {
                    currentSelection = text.substring(0, selectedText[0]).length() + 1;
                    text = text.substring(0, selectedText[0]) + s + text.substring(selectedText[1], text.length());
                    selectedText[0] = -1;
                    selectedText[1] = -1;
                    bool = true;
                } else if (currentSelection == text.length())
                    text += s;
                else {
                    bool = true;
                    try {
                        text = text.substring(0, currentSelection) + s + text.substring(currentSelection, text.length());
                        currentSelection++;
                    } catch (Exception e) {
                    }
                }
            }

            if(numberLimits.length == 2)
            {
                float min = numberLimits[0];
                float max = numberLimits[1];

                float number = Utils.parseFloat(text);

                if(number > max)
                    text = number % 1 == 0 ? Integer.toString((int) max) : Float.toString(max);
                if(number < min)
                    text = number % 1 == 0 ? Integer.toString((int) min) : Float.toString(min);
            }

            if (!bool)
                currentSelection = text.length();
        }
        super.onChar(codePoint, modifiers);
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if(focused)
            Consts.startMillis = System.currentTimeMillis();
    }

    public Color textColor()
    {
        return Config.FONT_COLOR;
    }

    public void setText(String text)
    {
        this.text = text;
        this.currentSelection = text.length();
    }

    public String getText()
    {
        return this.text;
    }

    @Override
    public void hoverCursor() {
        Cursors.setCursor(ECursor.xterm);
    }
}
