package bog.lbpas.view3d.renderer.gui.elements;

import bog.lbpas.view3d.managers.assetLoading.ObjectLoader;
import bog.lbpas.view3d.core.Model;
import bog.lbpas.view3d.managers.MouseInput;
import bog.lbpas.view3d.managers.RenderMan;
import bog.lbpas.view3d.managers.WindowMan;
import bog.lbpas.view3d.renderer.gui.cursor.ECursor;
import bog.lbpas.view3d.renderer.gui.ingredients.LineStrip;
import bog.lbpas.view3d.utils.*;
import org.joml.Math;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;

/**
 * @author Bog
 */
public class Textbox extends Element{

    public String text = "";
    boolean numbers = true;
    boolean letters = true;
    boolean others = true;
    public boolean disabled = false;
    Vector2f prevSize;
    Model outlineRect;

    float[] numberLimits = new float[0];

    public Textbox(String id, Vector2f pos, Vector2f size, RenderMan renderer, ObjectLoader loader, WindowMan window)
    {
        this.id = id;
        this.pos = pos;
        this.size = size;
        this.renderer = renderer;
        this.loader = loader;
        this.window = window;
    }

    public Textbox(String id, RenderMan renderer, ObjectLoader loader, WindowMan window)
    {
        this.id = id;
        this.pos = new Vector2f();
        this.size = new Vector2f();
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

        renderer.startScissor((int) Math.round(pos.x), (int) Math.round(pos.y), (int) Math.round(size.x), (int) Math.round(size.y));
        renderer.drawRect((int) Math.round(pos.x), (int) Math.round(pos.y), (int) Math.round(size.x), (int) Math.round(size.y), isMouseOverElement(mouseInput) && !overOther || this.isFocused() ? Config.INTERFACE_SECONDARY_COLOR : Config.INTERFACE_PRIMARY_COLOR);

        if(!this.isFocused())
        {
            currentSelection = text.length();
            selectedText[0] = -1;
            selectedText[1] = -1;
        }

        float xScroll = getXScroll(mouseInput);

        int begin = 0;
        int end = text.length();

        for(int i = 0; i < text.length(); i++)
        {
            try
            {
                if((int) Math.round(pos.x + xScroll + this.size.y / 2 - getFontHeight() / 2) + getStringWidth(text.substring(0, i)) < pos.x)
                    begin = i;

                if((int) Math.round(pos.x + xScroll + this.size.y / 2 - getFontHeight() / 2) + getStringWidth(text.substring(0, i + 1)) < pos.x + size.x)
                    end = i + 1;
            }catch (Exception e){}
        }

        renderer.drawString(text, textColor(), (int) Math.round(pos.x + xScroll + this.size.y / 2 - getFontHeight() / 2), (int) Math.round(pos.y + this.size.y / 2 - getFontHeight() / 2), begin, end);

        if(!this.disabled)
        {
            if(500 > (System.currentTimeMillis() - Consts.startMillis) % 1000 && isFocused())
                if(currentSelection == text.length())
                    renderer.drawString("_", textColor(), (int) Math.round(pos.x + xScroll + getStringWidth(text) + 1 + this.size.y/2 - getFontHeight()/2), (int) Math.round(pos.y + this.size.y/2 - getFontHeight()/2));
                else
                    renderer.drawRect((int) Math.round(xScroll + pos.x + getStringWidth(text.substring(0, currentSelection)) + this.size.y/2 - getFontHeight()/2 - 1), (int) Math.round(pos.y + this.size.y/2 - getFontHeight()/2), 1, (int) Math.round(getFontHeight() - 2), textColor());

            if(selectedText[0] >= 0 && selectedText[1] >= 0 && selectedText[0] != selectedText[1])
            {
                int selectionStart = selectedText[0];
                int selectionEnd = selectedText[1];

                if(selectionStart > selectionEnd)
                {
                    selectionStart = selectedText[1];
                    selectionEnd = selectedText[0];
                }

                float[] pos1 = {
                        pos.x + xScroll + this.size.y / 2 - getFontHeight() / 2,
                        pos.y + this.size.y / 2 - getFontHeight() / 2
                };

                float x = pos1[0] + getStringWidth(text.substring(0, selectionStart));
                float diff = 0;
                if (x < pos.x) {
                    diff = x - pos.x;
                    x = pos.x;
                }

                float width = getStringWidth(text.substring(selectionStart, selectionEnd)) + 1 + diff;
                if (x + width > pos.x + size.x)
                    width = pos.x + size.x - x;

                renderer.drawRectInvert((int) Math.round(x - 1), (int) Math.round(pos1[1] - 1), (int) Math.round(width + 1), getFontHeight() + 2);
            }
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
    public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overOther, boolean focusedOther) {
        if(isMouseOverElement(pos))
        {
            if(button == GLFW.GLFW_MOUSE_BUTTON_1 && !overOther)
            {
                if(action == GLFW.GLFW_PRESS)
                {
                    int cpos = getCursorPos(mouseInput);

                    if(System.currentTimeMillis() - mouseInput.lastLeftDownMS <= 500 && currentSelection == cpos && this.isFocused())
                    {
                        mouseInput.leftButtonPress = false;
                        for(int i = cpos; i >= 0; i--)
                            if(i - 1 <= 0)
                            {
                                selectedText[0] = i - 1 < 0 ? 0 : i - 1;
                                break;
                            }
                            else if(text.substring(i - 1, i).equalsIgnoreCase(" "))
                            {
                                selectedText[0] = i;
                                break;
                            }
                        for(int i = cpos; i <= text.length(); i++)
                            if(i + 1 >= text.length())
                            {
                                selectedText[1] = i + 1 > text.length() ? text.length() : i + 1;
                                currentSelection = selectedText[1];
                                break;
                            }
                            else if(text.substring(i, i + 1).equalsIgnoreCase(" "))
                            {
                                selectedText[1] = i;
                                currentSelection = i;
                                break;
                            }
                    }
                    else
                    {
                        this.setFocused(true);

                        currentSelection = cpos;
                        selectedText[0] = currentSelection;
                        selectedText[1] = currentSelection;
                    }
                }
            }
        }
        else
        {
            if(action == GLFW.GLFW_PRESS)
                this.setFocused(false);
        }

        if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_RELEASE && selectedText[0] == selectedText[1])
        {
            selectedText[0] = -1;
            selectedText[1] = -1;
        }

        super.onClick(mouseInput, pos, button, action, mods, overOther, focusedOther);
    }

    int[] selectedText = {-1, -1};
    int currentSelection;

    @Override
    public void onKey(int key, int scancode, int action, int mods) {

        if(this.isFocused() && action != 0 && !this.disabled)
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
                }catch (Exception e){}

                if(!letters)
                    clipboard = clipboard.replaceAll("[a-zA-Z]", "");
                if(!numbers)
                    clipboard = clipboard.replaceAll("[\\d]","");
                if(!others)
                    clipboard = clipboard.replaceAll(numbers ? "[^\\w.-]" : "[^\\w]","");

                if(selectionEnd != -1 && selectionEnd != -1)
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
            }
            else if(key == GLFW.GLFW_KEY_BACKSPACE) {
                if(selectionEnd != -1 && selectionStart != -1 && selectionStart != selectionEnd)
                {
                    currentSelection = selectionStart;
                    text = text.substring(0, selectionStart) + text.substring(selectionEnd, text.length());
                    selectedText[0] = -1;
                    selectedText[1] = -1;
                    bool = true;
                }
                else if (currentSelection == text.length())
                    text = text.substring(0, text.length() == 0 ? 0 : text.length() - 1);
                else if(currentSelection != 0)
                {
                    bool = true;
                    text = text.substring(0, currentSelection - 1) + text.substring(currentSelection, text.length());
                    currentSelection--;
                }
                else
                    bool = true;
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
                        StringSelection selection = new StringSelection(text.substring(selectionStart, selectionEnd));
                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        clipboard.setContents(selection, selection);
                    }catch (Exception e){e.printStackTrace();}

                    bool = true;
                }
            }
            else if(key == GLFW.GLFW_KEY_X && ctrl)
            {
                if(selectedText[1] != -1 && selectedText[0] != -1)
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
            else if (key == GLFW.GLFW_KEY_ESCAPE)
                this.setFocused(false);
            else
                bool = true;

            if(!bool)
                currentSelection = text.length();
        }

        if(numberLimits.length == 2 && !isFocused())
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

        if(this.isFocused() && !this.disabled) {
            boolean bool = false;

            boolean shift = modifiers == 1;
            boolean ctrl = modifiers == 2;
            boolean ctrlShift = modifiers == 3;
            boolean alt = modifiers == 4;
            boolean shiftAlt = modifiers == 5;
            boolean ctrlAlt = modifiers == 6;
            boolean ctrlShiftAlt = modifiers == 7;
            boolean winKey = modifiers == 8;

            int selectionStart = selectedText[0];
            int selectionEnd = selectedText[1];

            if(selectionStart > selectionEnd)
            {
                selectionStart = selectedText[1];
                selectionEnd = selectedText[0];
            }

            String s = Character.toString(codePoint);

            if(!letters)
                s = s.replaceAll("[a-zA-Z]", "");
            if(!numbers)
                s = s.replaceAll("[\\d]","");
            if(!others)
                s = s.replaceAll(numbers ? "[^\\w.-]" : "[^\\w]","");

            if (s != null) {
                if(selectionEnd != -1 && selectionEnd != -1)
                {
                    text = text.substring(0, selectionStart) + s + text.substring(selectionEnd, text.length());
                    currentSelection = selectionStart + s.length();
                    selectedText[0] = -1;
                    selectedText[1] = -1;
                    bool = true;
                }
                else if (currentSelection == text.length())
                    text += s;
                else
                {
                    bool = true;
                    text = text.substring(0, currentSelection) + s + text.substring(currentSelection, text.length());
                    currentSelection++;
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
        return disabled ? Config.FONT_DISABLED_COLOR : Config.FONT_COLOR;
    }

    public void setText(String text)
    {
        this.text = text;
        this.currentSelection = text.length();
    }

    public void setSelection(int selection)
    {
        this.currentSelection = selection;
    }

    public String getText()
    {
        return this.text;
    }

    @Override
    public void hoverCursor() {
        Cursors.setCursor(ECursor.xterm);
    }

    public Vector2f setTextboxValueFloat(float value)
    {
        if (!this.isFocused())
        {
            String v = Float.toString(value);
            this.setText(v.equalsIgnoreCase("nan") ? "" : v);
        }
        else
            try
            {
                float number = Utils.parseFloat(text);

                if(numberLimits.length == 2)
                {
                    float min = numberLimits[0];
                    float max = numberLimits[1];

                    if(number > max)
                        number = max;
                    if(number < min)
                        number = min;
                }

                return new Vector2f(number, 1);
            }
            catch (Exception e)
            {
                float number = 0;

                if(numberLimits.length == 2)
                {
                    float min = numberLimits[0];
                    float max = numberLimits[1];

                    if(number > max)
                        number = max;
                    if(number < min)
                        number = min;
                }
                return new Vector2f(number, 1);
            }

        return new Vector2f(0, 0);
    }


    public Vector2i setTextboxValueInt(int value)
    {
        if (!this.isFocused())
        {
            String v = Integer.toString(value);
            this.setText(v.equalsIgnoreCase("nan") ? "" : v);
        }
        else
            try
            {
                int number = Utils.parseInt(text);

                if(numberLimits.length == 2)
                {
                    float min = numberLimits[0];
                    float max = numberLimits[1];

                    if(number > max)
                        number = (int) max;
                    if(number < min)
                        number = (int) min;
                }

                return new Vector2i(number, 1);
            }
            catch (Exception e)
            {
                int number = 0;

                if(numberLimits.length == 2)
                {
                    float min = numberLimits[0];
                    float max = numberLimits[1];

                    if(number > max)
                        number = (int) max;
                    if(number < min)
                        number = (int) min;
                }

                return new Vector2i(number, 1);
            }

        return new Vector2i(0, 0);
    }

    public Vector2i setTextboxValueInt(float value)
    {
        return setTextboxValueInt(Math.round(value));
    }

    public String setTextboxValueString(String value)
    {
        if (!this.isFocused())
            this.setText(value == null ? "" : value);
        else
            try
            {
                return this.getText();
            }
            catch (Exception e)
            {
                return null;
            }
        return null;
    }

    int getCursorPos(MouseInput mouseInput)
    {
        Vector2d pos = mouseInput.currentPos;
        float xScroll = getXScroll(mouseInput);

        for(int i = text.length(); i >= 0; i--)
        {
            int fontHeight = getFontHeight();

            int cur = i;
            int prev = i - 1 < 0 ? 0 : i - 1;

            int widthPrev = getStringWidth(text.substring(0, prev)) - 1;
            int widthNextLetter = cur == text.length() ? 0 : getStringWidth(text.substring(cur, cur + 1));
            int widthCur = cur == text.length() ? getStringWidth(text) :
                    getStringWidth(text.substring(0, cur + 1)) - widthNextLetter;

            if(cur == text.length() && pos.x >= this.pos.x + widthCur + xScroll)
                return text.length();
            else if(cur == 0 && pos.x < this.pos.x + widthPrev + xScroll)
                return 0;
            else if(pos.x > this.pos.x + widthPrev + xScroll &&
                    (pos.x < this.pos.x + widthCur + xScroll))
                return i - 1;
        }
        return 0;
    }

    public float getXScroll(MouseInput mouseInput)
    {
        if(!isFocused())
            return 0;

        float xScroll = 0;

        float spot = this.size.x * 0.5f;

        int widthToSelection = getStringWidth(text.substring(0, currentSelection));

        if(xScroll + widthToSelection < spot)
            xScroll = spot - widthToSelection;
        else if(xScroll + widthToSelection > spot)
            xScroll = spot - widthToSelection;

        if(xScroll < -(getFontHeight() + (getStringWidth(text) - this.size.x)))
            xScroll = -(getFontHeight() + (getStringWidth(text) - this.size.x));
        if(xScroll > 0)
            xScroll = 0;

        return xScroll;
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
}
