package bog.lbpas.view3d.renderer.gui.elements;

import bog.lbpas.view3d.managers.assetLoading.ObjectLoader;
import bog.lbpas.view3d.core.Model;
import bog.lbpas.view3d.managers.MouseInput;
import bog.lbpas.view3d.managers.RenderMan;
import bog.lbpas.view3d.managers.WindowMan;
import bog.lbpas.view3d.renderer.gui.cursor.ECursor;
import bog.lbpas.view3d.renderer.gui.ingredients.LineStrip;
import bog.lbpas.view3d.utils.Config;
import bog.lbpas.view3d.utils.Consts;
import bog.lbpas.view3d.utils.Cursors;
import org.joml.Math;
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
    int fontSize;
    Vector2f prevSize;
    Model outlineRect;
    public Textarea(String id, Vector2f pos, Vector2f size, int fontSize, RenderMan renderer, ObjectLoader loader, WindowMan window)
    {
        this.id = id;
        this.pos = pos;
        this.size = size;
        this.prevSize = size;
        this.outlineRect = LineStrip.processVerts(LineStrip.getRectangle(size), loader, window);
        this.fontSize = fontSize;
        this.renderer = renderer;
        this.loader = loader;
        this.window = window;
    }

    public Textarea text(String text)
    {
        this.text = text;
        return this;
    }

    float xScroll = 0;
    float yScroll = 0;

    @Override
    public void draw(MouseInput mouseInput, boolean overOther) {
        super.draw(mouseInput, overOther);

        if(size.x != prevSize.x || size.y != prevSize.y)
        {
            refreshOutline();
            prevSize = size;
        }

        renderer.startScissor((int)pos.x, (int)pos.y, (int)size.x, (int)size.y);
        renderer.drawRect((int)pos.x, (int)pos.y, (int)size.x, (int)size.y, isMouseOverElement(mouseInput) && !overOther || this.isFocused() ? Config.INTERFACE_SECONDARY_COLOR : Config.INTERFACE_PRIMARY_COLOR);

        if(!this.isFocused())
        {
            currentSelection = 0;
            selectedText[0] = -1;
            selectedText[1] = -1;
        }

        String[] lines = (text).split(String.valueOf((char)10), -1);

        int currentLength = 0;

        int selectionStart = selectedText[0];
        int selectionEnd = selectedText[1];

        if(selectionStart > selectionEnd)
        {
            selectionStart = selectedText[1];
            selectionEnd = selectedText[0];
        }

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
                renderer.drawString(line, Config.FONT_COLOR, (int)(pos.x + xScroll + getFontHeight(fontSize) / 2), (int)(pos.y + getFontHeight(fontSize) / 2 + (getFontHeight(fontSize) + 2) * o - yScroll), fontSize, begin, end);

            int curSel = currentSelection - currentLength;
            if(curSel < 0)
                curSel = 0;
            if(curSel > line.length())
                curSel = line.length();

            float[] pos1 = {pos.x + xScroll + getFontHeight(fontSize)/2, pos.y + getFontHeight(fontSize)/2 - yScroll};

            boolean hasSelected = true;
            int selText0 = selectionStart - currentLength;
            if(selText0 < 0)
                selText0 = 0;
            if(selText0 > line.length())
            {
                selText0 = line.length();
                hasSelected = false;
            }

            int selText1 = selectionEnd - currentLength;
            if(selText1 < 0)
            {
                selText1 = 0;
                hasSelected = false;
            }
            if(selText1 > line.length())
                selText1 = line.length();

            float x = pos1[0] + getStringWidth(line.substring(0, selText0), fontSize);
            float diff = 0;
            if(x < pos.x)
            {
                diff = x - pos.x;
                x = pos.x;
            }

            float width = getStringWidth(line.substring(selText0, selText1), fontSize) + 1 + diff;
            if(x + width > pos.x + size.x)
                width = pos.x + size.x - x;

            if(500 > (System.currentTimeMillis() - Consts.startMillis) % 1000 && isFocused())
                if (currentSelection - currentLength == line.length())
                    renderer.drawString("_", Config.FONT_COLOR, (int) Math.round(pos.x + xScroll + getStringWidth(line, fontSize) + 1 + getFontHeight(fontSize) / 2), (int) Math.round(pos.y + getFontHeight(fontSize) / 2 + (getFontHeight(fontSize) + 2) * o - yScroll), fontSize);
                else if (currentSelection - currentLength >= 0 && currentSelection - currentLength <= line.length())
                    renderer.drawRect((int) Math.round(xScroll + pos.x + getStringWidth(line.substring(0, curSel), fontSize) + getFontHeight(fontSize) / 2 - 1), (int) Math.round(pos.y + getFontHeight(fontSize) / 2 + (getFontHeight(fontSize) + 2) * o - yScroll), 1, (int) Math.round(getFontHeight(fontSize) - 2), Config.FONT_COLOR);

            if(hasSelected && width >= 0 && selectedText[0] >= 0 && selectedText[1] >= 0 && selectedText[0] != selectedText[1])
                if((int)(pos.y + getFontHeight(fontSize) / 2 + (getFontHeight(fontSize) + 2) * o - yScroll) > pos.y - getFontHeight(fontSize) &&
                        (int)(pos.y + getFontHeight(fontSize) / 2 + (getFontHeight(fontSize) + 2) * o - yScroll) + getFontHeight(fontSize) < pos.y + size.y + getFontHeight(fontSize))
                    renderer.drawRectInvert(Math.round(x - 1), Math.round(pos1[1] - 1 + (getFontHeight(fontSize) + 2) * o), Math.round(width + 1), getFontHeight(fontSize) + 2);

            currentLength += line.length() + 1;
        }
        renderer.endScissor();
        renderer.drawRectOutline(pos, outlineRect, isMouseOverElement(mouseInput) && !overOther || this.isFocused() ? Config.INTERFACE_SECONDARY_COLOR2 : Config.INTERFACE_PRIMARY_COLOR2, false);

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
        this.outlineRect = LineStrip.processVerts(LineStrip.getRectangle(size), loader, window);
    }

    @Override
    public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overOther, boolean focusedOther) {

        if(isMouseOverElement(pos) && !overOther)
        {
            if(action == GLFW.GLFW_PRESS && button == GLFW.GLFW_MOUSE_BUTTON_1)
            {
                this.setFocused(true);

                if(!this.isFocused())
                {
                    currentSelection = text.length();
                    selectedText[0] = -1;
                    selectedText[1] = -1;
                }

                int cpos = getCursorPos(mouseInput);

                if(System.currentTimeMillis() - mouseInput.lastLeftDownMS <= 500 && currentSelection == cpos)
                {
                    mouseInput.leftButtonPress = false;
                    for(int i = cpos; i >= 0; i--)
                        if(i - 1 <= 0)
                        {
                            selectedText[0] = i - 1 < 0 ? 0 : i - 1;
                            break;
                        }
                        else if(text.substring(i - 1, i).equalsIgnoreCase(" ") || text.substring(i - 1, i).equalsIgnoreCase(String.valueOf((char)10)))
                        {
                            selectedText[0] = i;
                            break;
                        }
                    for(int i = cpos; i < text.length(); i++)
                        if(i + 1 >= text.length())
                        {
                            selectedText[1] = i + 1;
                            currentSelection = i + 1;
                            break;
                        }
                        else if(text.substring(i, i + 1).equalsIgnoreCase(" ") || text.substring(i, i + 1).equalsIgnoreCase(String.valueOf((char)10)))
                        {
                            selectedText[1] = i;
                            currentSelection = i;
                            break;
                        }
                }
                else
                {
                    currentSelection = cpos;
                    selectedText[0] = currentSelection;
                    selectedText[1] = currentSelection;
                }
            }
        }
        else
        {
            if(action == GLFW.GLFW_PRESS)
                this.setFocused(false);
        }

        super.onClick(mouseInput, pos, button, action, mods, overOther, focusedOther);
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

            int selectionStart = selectedText[0];
            int selectionEnd = selectedText[1];

            if(selectionStart > selectionEnd)
            {
                selectionStart = selectedText[1];
                selectionEnd = selectedText[0];
            }

            if(ctrl && key == GLFW.GLFW_KEY_V)
            {
                String clipboard = "";

                try
                {
                    clipboard = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
                }catch (Exception e){e.printStackTrace();}

                try {
                    if(selectedText[1] != -1 && selectedText[0] != -1 && selectedText[1] != selectedText[0])
                    {
                        text = text.substring(0, selectionStart) + clipboard + text.substring(selectionEnd, text.length());
                        currentSelection = selectionStart + clipboard.length();
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

                    if(selectedText[1] != -1 && selectedText[0] != -1 && selectedText[1] != selectedText[0])
                    {
                        currentSelection = selectionStart;
                        text = text.substring(0, selectionStart) + text.substring(selectionEnd, text.length());
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
                Consts.startMillis = System.currentTimeMillis();
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
                Consts.startMillis = System.currentTimeMillis();
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
                if(selectedText[1] != -1 && selectedText[0] != -1 && selectedText[1] != selectedText[0])
                {
                    try {
                        StringSelection selection = new StringSelection(text.substring(selectionStart, selectionEnd));
                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        clipboard.setContents(selection, selection);
                    }catch (Exception e){e.printStackTrace();}

                    bool = true;
                }
            }
            else if(key == GLFW.GLFW_KEY_X && ctrl)
            {
                if(selectedText[1] != -1 && selectedText[0] != -1 && selectedText[1] != selectedText[0])
                {
                    try {

                        StringSelection selection = new StringSelection(text.substring(selectionStart, selectionEnd));
                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        clipboard.setContents(selection, selection);
                    }catch (Exception e){}

                    currentSelection = selectionStart;
                    text = text.substring(0, selectionStart) + text.substring(selectionEnd, text.length());
                    selectedText[0] = -1;
                    selectedText[1] = -1;

                    bool = true;
                }
            }
            else if(key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER)
            {
                try {
                    if(selectedText[1] != -1 && selectedText[0] != -1 && selectedText[1] != selectedText[0])
                    {
                        text = text.substring(0, selectionStart) + (char)10 + text.substring(selectionEnd, text.length());
                        currentSelection = selectionStart + String.valueOf((char)10).length();
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
            else if(key == GLFW.GLFW_KEY_UP)
            {
                Consts.startMillis = System.currentTimeMillis();
                String[] lines = (text).split(String.valueOf((char)10), -1);

                int currentLength = 0;
                int prevLength = 0;

                for(int o = 0; o < lines.length; o++) {

                    if(currentLength > currentSelection)
                        break;

                    String line = lines[o];

                    for(int i = 0; i <= line.length(); i++)
                    {
                        if(i + currentLength == currentSelection)
                        {
                            if(o == 0)
                            {
                                if(shift)
                                {
                                    if(selectedText[0] == -1)
                                        selectedText[0] = currentSelection;
                                    if(selectedText[1] == -1)
                                        selectedText[1] = currentSelection;

                                    if(selectedText[0] == currentSelection)
                                        selectedText[0] = 0;
                                    else if(selectedText[1] == currentSelection)
                                        selectedText[1] = 0;
                                }
                                else
                                {
                                    selectedText[0] = currentSelection;
                                    selectedText[1] = currentSelection;
                                }

                                currentSelection = 0;

                                bool = true;
                                break;
                            }

                            int width = getStringWidth(line.substring(0, i), fontSize);

                            int prevDist = Integer.MAX_VALUE;
                            int prevInd = -1;

                            for(int j = 0; j <= lines[o - 1].length(); j++)
                            {
                                int widthPrev = getStringWidth(lines[o - 1].substring(0, j), fontSize);

                                int dist = Math.abs(widthPrev - width);

                                if(dist < prevDist)
                                {
                                    prevDist = dist;
                                    prevInd = j;
                                }
                            }

                            if(prevInd != -1)
                            {
                                int newInd = prevLength + prevInd;

                                if(shift)
                                {
                                    if(selectedText[0] == -1)
                                        selectedText[0] = currentSelection;
                                    if(selectedText[1] == -1)
                                        selectedText[1] = currentSelection;

                                    if(selectedText[1] == selectedText[0])
                                    {
                                        selectedText[0] = currentSelection;
                                        selectedText[1] = currentSelection;
                                    }

                                    if(selectedText[0] == currentSelection)
                                        selectedText[0] = newInd;
                                    else if(selectedText[1] == currentSelection)
                                        selectedText[1] = newInd;
                                }
                                else
                                {
                                    selectedText[0] = currentSelection;
                                    selectedText[1] = currentSelection;
                                }

                                currentSelection = newInd;
                            }

                            bool = true;
                            break;
                        }
                    }

                    prevLength = currentLength;
                    currentLength += line.length() + 1;
                }
            }
            else if(key == GLFW.GLFW_KEY_DOWN)
            {
                Consts.startMillis = System.currentTimeMillis();
                String[] lines = (text).split(String.valueOf((char)10), -1);

                int currentLength = 0;
                int nextLength = lines[0].length() + 1;

                for(int o = 0; o < lines.length; o++) {

                    if(currentLength > currentSelection)
                        break;

                    String line = lines[o];

                    boolean breakl = false;

                    for(int i = 0; i <= line.length(); i++)
                    {
                        if(i + currentLength == currentSelection)
                        {
                            if(o == lines.length - 1)
                            {
                                if(shift)
                                {
                                    if(selectedText[1] == -1)
                                        selectedText[1] = currentSelection;
                                    if(selectedText[0] == -1)
                                        selectedText[0] = currentSelection;

                                    if(selectedText[1] == selectedText[0])
                                    {
                                        selectedText[0] = currentSelection;
                                        selectedText[1] = currentSelection;
                                    }

                                    if(selectedText[1] == currentSelection)
                                        selectedText[1] = text.length();
                                    else if(selectedText[0] == currentSelection)
                                        selectedText[0] = text.length();
                                }
                                else
                                {
                                    selectedText[0] = currentSelection;
                                    selectedText[1] = currentSelection;
                                }

                                currentSelection = text.length();

                                bool = true;
                                breakl = true;
                                break;
                            }

                            int width = getStringWidth(line.substring(0, i), fontSize);

                            int prevDist = Integer.MAX_VALUE;
                            int prevInd = -1;

                            for(int j = 0; j <= lines[o + 1].length(); j++)
                            {
                                int widthNext = getStringWidth(lines[o + 1].substring(0, j), fontSize);

                                int dist = Math.abs(widthNext - width);

                                if(dist < prevDist)
                                {
                                    prevDist = dist;
                                    prevInd = j;
                                }
                            }

                            if(prevInd != -1)
                            {
                                int newInd = nextLength + prevInd;

                                if(shift)
                                {
                                    if(selectedText[1] == -1)
                                        selectedText[1] = currentSelection;
                                    if(selectedText[0] == -1)
                                        selectedText[0] = currentSelection;

                                    if(selectedText[1] == selectedText[0])
                                    {
                                        selectedText[0] = currentSelection;
                                        selectedText[1] = currentSelection;
                                    }

                                    if(selectedText[1] == currentSelection)
                                        selectedText[1] = newInd;
                                    else if(selectedText[0] == currentSelection)
                                        selectedText[0] = newInd;
                                }
                                else
                                {
                                    selectedText[0] = currentSelection;
                                    selectedText[1] = currentSelection;
                                }

                                currentSelection = newInd;
                            }

                            bool = true;
                            breakl = true;
                            break;
                        }
                    }

                    if(breakl)
                        break;

                    nextLength += lines[o + 1].length() + 1;
                    currentLength += line.length() + 1;
                }
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

            int selectionStart = selectedText[0];
            int selectionEnd = selectedText[1];

            if(selectionStart > selectionEnd)
            {
                selectionStart = selectedText[1];
                selectionEnd = selectedText[0];
            }

            if (s != null) {
                if (selectedText[1] != -1 && selectedText[0] != -1 && selectedText[1] != selectedText[0]) {
                    currentSelection = text.substring(0, selectionStart).length() + 1;
                    text = text.substring(0, selectionStart) + s + text.substring(selectionEnd, text.length());
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

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if(focused)
            Consts.startMillis = System.currentTimeMillis();
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

    private int getCursorPos(MouseInput mouseInput)
    {
        String[] lines = (text).split(String.valueOf((char)10), -1);

        int fontHeight = getFontHeight(fontSize);
        float topMargin = this.pos.y + fontHeight / 2f;

        int currentLength = 0;
        float heightPrev = 0;

        for(int o = 0; o < lines.length; o++) {
            String line = lines[o];

            int widthPrev = 0;

            for(int i = 0; i <= line.length(); i++)
                if((mouseInput.currentPos.y >= topMargin + heightPrev - yScroll - 1 || o <= 0) &&
                        (mouseInput.currentPos.y <= topMargin + heightPrev + fontHeight + 2 - yScroll + 1 || o >= lines.length - 1))
                {
                    int cur = i;
                    int prev = i - 1 < 0 ? 0 : i - 1;

                    int widthCur = cur >= line.length() ? 0 : getStringWidth(line.substring(0, cur + 1), fontSize);

                    if((mouseInput.currentPos.x >= this.pos.x + widthPrev + xScroll - 1 || cur <= 0) &&
                            (mouseInput.currentPos.x <= this.pos.x + widthCur + xScroll + 1 || cur >= line.length()))
                        return i + currentLength;

                    widthPrev = widthCur;
                }

            heightPrev += fontHeight + 2;
            currentLength += line.length() + 1;
        }

        return 0;
    }

    @Override
    public void onMouseMove(MouseInput mouseInput, double x, double y, boolean overElement) {
        super.onMouseMove(mouseInput, x, y, overElement);

        if(mouseInput.leftButtonPress && isFocused())
        {
            currentSelection = getCursorPos(mouseInput);

            selectedText[1] = currentSelection;

            if(selectedText[0] == -1)
                selectedText[0] = currentSelection;
        }
    }

    @Override
    public void onMouseScroll(Vector2d pos, double xOffset, double yOffset) {
        super.onMouseScroll(pos, xOffset, yOffset);
    }
}