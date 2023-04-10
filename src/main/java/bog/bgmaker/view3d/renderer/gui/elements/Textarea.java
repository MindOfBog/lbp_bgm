package bog.bgmaker.view3d.renderer.gui.elements;

import bog.bgmaker.view3d.ObjectLoader;
import bog.bgmaker.view3d.managers.MouseInput;
import bog.bgmaker.view3d.managers.RenderMan;
import bog.bgmaker.view3d.managers.WindowMan;
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
public class Textarea extends Element{

    private String text = "";
    Color textColor;
    Color textFieldColor;
    Color textFieldColorHighlighted;
    int fontSize;

    public Textarea(String id, Vector2f pos, Vector2f size, int fontSize, RenderMan renderer, ObjectLoader loader, WindowMan window)
    {
        this.id = id;
        this.pos = pos;
        this.size = size;
        this.fontSize = fontSize;
        this.renderer = renderer;
        this.loader = loader;
        this.window = window;
        this.textColor = Color.white;
        this.textFieldColor = new Color(0f, 0f, 0f, 0.5f);
        this.textFieldColorHighlighted = new Color(0.10f, 0.10f, 0.10f, 0.5f);
    }

    public Textarea text(String text)
    {
        this.text = text;
        return this;
    }

    @Override
    public void draw(MouseInput mouseInput, boolean overOther) {
        super.draw(mouseInput, overOther);

        startScissor((int)pos.x, (int)pos.y, (int)size.x, (int)size.y);
        drawRect((int)pos.x, (int)pos.y, (int)size.x, (int)size.y, isMouseOverElement(mouseInput) && !overOther || this.isFocused() ? textFieldColorHighlighted : textFieldColor);

        float xScroll = 0;
        float yScroll = 0;

        if(!this.isFocused())
        {
            currentSelection = text.length();
            selectedText[0] = -1;
            selectedText[1] = -1;
        }

        String[] lines = (text + ".").split(String.valueOf((char)10));
        String ll = lines[lines.length - 1];
        lines[lines.length - 1] = ll.substring(0, ll.length() - 1);
        int currentLength = 0;

        for(int o = 0; o < lines.length; o++) {
            String line = lines[o];

            try {
                if (currentSelection - currentLength == line.length() && getStringWidth(line + ((currentSelection - currentLength == line.length()) ? "_" : "|"), fontSize) > this.size.x - (getFontHeight(fontSize) / 2) * 2)
                    xScroll = -(getStringWidth((line + ((currentSelection - currentLength == line.length()) ? "_" : "|")), fontSize) - this.size.x + (getFontHeight(fontSize) / 2) * 2);
                else if (getStringWidth((line.substring(0, currentSelection - currentLength) + ((currentSelection - currentLength == line.length()) ? "_" : "|")), fontSize) > this.size.x - (getFontHeight(fontSize) / 2) * 2)
                    xScroll = -(getStringWidth(line.substring(0, currentSelection - currentLength) + ((currentSelection - currentLength == line.length()) ? "_" : "|"), fontSize) - this.size.x + (getFontHeight(fontSize) / 2) * 2);
            } catch (Exception e) {}
            try {
                int cursorpos = currentSelection - currentLength;
                    if(cursorpos < line.length() + 1 && cursorpos >= 0)
                    {
                        if((getFontHeight(fontSize) + 2) * o < yScroll)
                            yScroll = (getFontHeight(fontSize) + 2) * o;
                        if((getFontHeight(fontSize) + 2) * (o + 1) > yScroll + size.y - getFontHeight(fontSize))
                            yScroll = ((getFontHeight(fontSize) + 2) * (o + 1)) - size.y + getFontHeight(fontSize);
                    }
            } catch (Exception e) {}

            currentLength += line.length();
            currentLength++;
        }
        currentLength = 0;

        for(int o = 0; o < lines.length; o++)
        {
            String line = lines[o];

            int begin = 0;
            int end = line.length();

            for(int i = 0; i <= line.length(); i++)
            {
                if((int)(pos.x + xScroll + getFontHeight(fontSize) / 2) + getStringWidth(line.substring(0, i), fontSize) < pos.x)
                    begin = i;
                if((int)(pos.x + xScroll + getFontHeight(fontSize) / 2) + getStringWidth(line.substring(0, i), fontSize) < pos.x + size.x)
                    end = i;
            }

            if((int)(pos.y + getFontHeight(fontSize) / 2 + (getFontHeight(fontSize) + 2) * o - yScroll) > pos.y - getFontHeight(fontSize) &&
                    (int)(pos.y + getFontHeight(fontSize) / 2 + (getFontHeight(fontSize) + 2) * o - yScroll) + getFontHeight(fontSize) < pos.y + size.y + getFontHeight(fontSize))
                drawString(line, textColor, (int)(pos.x + xScroll + getFontHeight(fontSize) / 2), (int)(pos.y + getFontHeight(fontSize) / 2 + (getFontHeight(fontSize) + 2) * o - yScroll), fontSize, begin, end);

            try {
                int curSel = currentSelection - currentLength;
                if(curSel < 0)
                    curSel = 0;
                if(curSel > line.length())
                    curSel = line.length();

                float[] pos1 = {pos.x + xScroll + getFontHeight(fontSize)/2, pos.y + getFontHeight(fontSize)/2 - yScroll};

                boolean hasSelected = true;
                int selText0 = selectedText[0] - currentLength;
                if(selText0 < 0)
                    selText0 = 0;
                if(selText0 > line.length())
                {
                    selText0 = line.length();
                    hasSelected = false;
                }

                int selText1 = selectedText[1] - currentLength;
                if(selText1 < 0)
                {
                    selText1 = 0;
                    hasSelected = false;
                }
                if(selText1 > line.length())
                    selText1 = line.length();

                int x = (int) (pos1[0] + getStringWidth(line.substring(0, selText0), fontSize));
                int diff = 0;
                if(x < pos.x)
                {
                    diff = x - (int) pos.x;
                    x = (int) pos.x;
                }

                int width = (int) (getStringWidth(line.substring(selText0, selText1), fontSize) + 1 + diff);
                if(x + width > pos.x + size.x)
                    width = (int) (pos.x + size.x - x);

                if(hasSelected && width >= 0)
                    if((int)(pos.y + getFontHeight(fontSize) / 2 + (getFontHeight(fontSize) + 2) * o - yScroll) > pos.y - getFontHeight(fontSize) &&
                            (int)(pos.y + getFontHeight(fontSize) / 2 + (getFontHeight(fontSize) + 2) * o - yScroll) + getFontHeight(fontSize) < pos.y + size.y + getFontHeight(fontSize))
                        drawRect(x, (int) (pos1[1] - 1 + (getFontHeight(fontSize) + 2) * o), width, getFontHeight(fontSize) + 2, new Color(0f, 0f, 1f, 0.5f));

                if(350 > System.currentTimeMillis() % 500 && isFocused())
                    if(currentSelection - currentLength == line.length())
                        drawString("_", textColor, (int) (pos.x + xScroll + getStringWidth(line, fontSize) + 1 + getFontHeight(fontSize)/2), (int) (pos.y + getFontHeight(fontSize)/2 + (getFontHeight(fontSize) + 2) * o - yScroll), fontSize);
                    else if(currentSelection - currentLength >= 0 && currentSelection - currentLength <= line.length())
                        drawRect((int) (xScroll + pos.x + getStringWidth(line.substring(0, curSel), fontSize) + getFontHeight(fontSize)/2), (int) (pos.y + getFontHeight(fontSize)/2 - 1 + (getFontHeight(fontSize) + 2) * o - yScroll), 1, (int) (getFontHeight(fontSize)), textColor);

            }catch(Exception e)
            {
                //e.printStackTrace();
                selectedText[0] = -1;
                selectedText[1] = -1;
            }
            currentLength += line.length() + 1;
        }
        drawRectOutline((int)pos.x, (int)pos.y, (int)size.x, (int)size.y, isMouseOverElement(mouseInput) && !overOther || this.isFocused() ? textFieldColorHighlighted : textFieldColor, false);
        endScissor();

    }
    @Override
    public void onClick(Vector2d pos, int button, int action, int mods, boolean overOther) {

        if(isMouseOverElement(pos) && !overOther)
        {
            if(action == GLFW.GLFW_PRESS && button == GLFW.GLFW_MOUSE_BUTTON_1)
            {
                this.setFocused(true);


                float xScroll = 0;
                float yScroll = 0;

                if(!this.isFocused())
                {
                    currentSelection = text.length();
                    selectedText[0] = -1;
                    selectedText[1] = -1;
                }

                String[] lines = (text + ".").split(String.valueOf((char)10));
                String ll = lines[lines.length - 1];
                lines[lines.length - 1] = ll.substring(0, ll.length() - 1);
                int currentLength = 0;

                for(int o = 0; o < lines.length; o++) {
                    String line = lines[o];

                    try {
                        if (currentSelection - currentLength == line.length() && getStringWidth(line + ((currentSelection - currentLength == line.length()) ? "_" : "|"), fontSize) > this.size.x - (getFontHeight(fontSize) / 2) * 2)
                            xScroll = -(getStringWidth((line + ((currentSelection - currentLength == line.length()) ? "_" : "|")), fontSize) - this.size.x + (getFontHeight(fontSize) / 2) * 2);
                        else if (getStringWidth((line.substring(0, currentSelection - currentLength) + ((currentSelection - currentLength == line.length()) ? "_" : "|")), fontSize) > this.size.x - (getFontHeight(fontSize) / 2) * 2)
                            xScroll = -(getStringWidth(line.substring(0, currentSelection - currentLength) + ((currentSelection - currentLength == line.length()) ? "_" : "|"), fontSize) - this.size.x + (getFontHeight(fontSize) / 2) * 2);
                    } catch (Exception e) {}
                    try {
                        int cursorpos = currentSelection - currentLength;
                        if(cursorpos < line.length() + 1 && cursorpos >= 0)
                        {
                            if((getFontHeight(fontSize) + 2) * o < yScroll)
                                yScroll = (getFontHeight(fontSize) + 2) * o;
                            if((getFontHeight(fontSize) + 2) * (o + 1) > yScroll + size.y - getFontHeight(fontSize))
                                yScroll = ((getFontHeight(fontSize) + 2) * (o + 1)) - size.y + getFontHeight(fontSize);
                        }
                    } catch (Exception e) {}

                    currentLength += line.length();
                    currentLength++;
                }
                currentLength = 0;

                for(int o = 0; o < lines.length; o++) {
                    String line = lines[o];

                    int begin = 0;
                    int end = line.length();

                    for (int i = 0; i <= line.length(); i++) {
                        if ((int) (pos.x + xScroll + getFontHeight(fontSize) / 2) + getStringWidth(line.substring(0, i), fontSize) < pos.x)
                            begin = i;
                        if ((int) (pos.x + xScroll + getFontHeight(fontSize) / 2) + getStringWidth(line.substring(0, i), fontSize) < pos.x + size.x)
                            end = i;
                    }

                    if ((int) (pos.y + getFontHeight(fontSize) / 2 + (getFontHeight(fontSize) + 2) * o - yScroll) > pos.y - getFontHeight(fontSize) &&
                            (int) (pos.y + getFontHeight(fontSize) / 2 + (getFontHeight(fontSize) + 2) * o - yScroll) + getFontHeight(fontSize) < pos.y + size.y + getFontHeight(fontSize))
                    {
                        for(int i = 0; i <= line.length(); i++)
                            if(pos.y > this.pos.y + getFontHeight(fontSize) / 2 + (getFontHeight(fontSize) + 2) * o - yScroll &&
                                    pos.y < this.pos.y + getFontHeight(fontSize) / 2 + (getFontHeight(fontSize) + 2) * o - yScroll + getFontHeight(fontSize))
                            {
                                if (pos.x > this.pos.x + xScroll + getFontHeight(fontSize) / 2 + (i <= 0 ? 0 : getStringWidth(line.substring(0, i - 1), fontSize)) &&
                                        pos.x < this.pos.x + xScroll + getFontHeight(fontSize) / 2 + getStringWidth(line.substring(0, i), fontSize))
                                {
                                    if (Math.abs(this.pos.x + (i - 1 < 0 ? 0 : getStringWidth(line.substring(0, i - 1), fontSize)) + xScroll - pos.x) <
                                            Math.abs(this.pos.x + getStringWidth(line.substring(0, i), fontSize) + xScroll - pos.x))
                                        currentSelection = i - 2 + currentLength;
                                    else
                                        currentSelection = i - 1 + currentLength;

                                    selectedText[0] = -1;
                                    selectedText[1] = -1;
                                }

                                if (line.length() - 1 >= 0)
                                    if (this.pos.x + getStringWidth(line, fontSize) + getStringWidth(line.substring(line.length() - 1, line.length()), fontSize) / 2 + xScroll < pos.x)
                                    {
                                        currentSelection = line.length() + currentLength;
                                        selectedText[0] = -1;
                                        selectedText[1] = -1;
                                    }
                                if (this.pos.x + xScroll > pos.x) {
                                    currentSelection = currentLength;
                                    selectedText[0] = -1;
                                    selectedText[1] = -1;
                                }
                                if(line.length() == 0)
                                {
                                    currentSelection = currentLength;
                                    selectedText[0] = -1;
                                    selectedText[1] = -1;
                                }
                            }
                    }
                    currentLength += line.length() + 1;
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
                }catch (Exception e){e.printStackTrace();}

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
                        int indexNewline = text.lastIndexOf(String.valueOf((char)10), currentSelection) == -1 ? 0 : text.lastIndexOf(String.valueOf((char)10), currentSelection) == currentSelection ? text.lastIndexOf(String.valueOf((char)10), currentSelection - 1) == -1 ? 0 : text.lastIndexOf(String.valueOf((char)10), currentSelection - 1) + 1 : text.lastIndexOf(String.valueOf((char)10), currentSelection) + (currentSelection == text.length() ? 1 : 0);

                        if(Math.abs(currentSelection - indexSpace) < Math.abs(currentSelection - indexDash) &&
                                Math.abs(currentSelection - indexSpace) < Math.abs(currentSelection - indexNewline))
                        {
                            if(currentSelection != indexSpace)
                                currentSelection = indexSpace;
                            else
                                currentSelection = indexSpace - 1;
                        }
                        else if(Math.abs(currentSelection - indexDash) < Math.abs(currentSelection - indexSpace) &&
                                Math.abs(currentSelection - indexDash) < Math.abs(currentSelection - indexNewline))
                        {
                            if(currentSelection != indexDash)
                                currentSelection = indexDash;
                            else
                                currentSelection = indexDash - 1;
                        }
                        else if(Math.abs(currentSelection - indexNewline) < Math.abs(currentSelection - indexSpace) &&
                                Math.abs(currentSelection - indexNewline) < Math.abs(currentSelection - indexDash))
                        {
                            if(currentSelection != indexNewline)
                                currentSelection = indexNewline;
                            else
                                currentSelection = indexNewline - 1;
                        }
                        else
                            currentSelection = 0;
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
                        int indexNewline = text.indexOf(String.valueOf((char)10), currentSelection) == -1 ? text.length() : text.indexOf(String.valueOf((char)10), currentSelection) == currentSelection ? (currentSelection + 1) > text.length() ? currentSelection : currentSelection + 1 : text.indexOf(String.valueOf((char)10), currentSelection);

                        if(Math.abs(currentSelection - indexSpace) < Math.abs(currentSelection - indexDash) &&
                                Math.abs(currentSelection - indexSpace) < Math.abs(currentSelection - indexNewline))
                            currentSelection = indexSpace;
                        else if(Math.abs(currentSelection - indexDash) < Math.abs(currentSelection - indexSpace) &&
                                Math.abs(currentSelection - indexDash) < Math.abs(currentSelection - indexNewline))
                            currentSelection = indexDash;
                        else if(Math.abs(currentSelection - indexNewline) < Math.abs(currentSelection - indexSpace) &&
                                Math.abs(currentSelection - indexNewline) < Math.abs(currentSelection - indexDash))
                            currentSelection = indexNewline;
                        else
                            currentSelection = text.length();
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
            else if(key == GLFW.GLFW_KEY_ENTER)
            {
                try {
                    if(selectedText[1] != -1 && selectedText[0] != -1)
                    {
                        text = text.substring(0, selectedText[0]) + (char)10 + text.substring(selectedText[1], text.length());
                        currentSelection = selectedText[0] + String.valueOf((char)10).length();
                        selectedText[0] = -1;
                        selectedText[1] = -1;
                        bool = true;
                    }
                    else if (currentSelection == text.length())
                        text += (char)10;
                    else
                    {
                        bool = true;
                        text = text.substring(0, currentSelection) + (char)10 + text.substring(currentSelection, text.length());
                        currentSelection += String.valueOf((char)10).length();
                    }
                }catch (Exception e){}
            }
            else
                bool = true;

            if(!bool)
                currentSelection = text.length();
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

            if (!bool)
                currentSelection = text.length();
        }

        super.onChar(codePoint, modifiers);
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
}