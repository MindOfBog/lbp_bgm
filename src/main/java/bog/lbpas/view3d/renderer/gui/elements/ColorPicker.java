package bog.lbpas.view3d.renderer.gui.elements;

import bog.lbpas.view3d.core.Model;
import bog.lbpas.view3d.mainWindow.ConstantTextures;
import bog.lbpas.view3d.managers.MouseInput;
import bog.lbpas.view3d.managers.RenderMan;
import bog.lbpas.view3d.managers.WindowMan;
import bog.lbpas.view3d.managers.assetLoading.ObjectLoader;
import bog.lbpas.view3d.renderer.gui.cursor.ECursor;
import bog.lbpas.view3d.renderer.gui.ingredients.LineStrip;
import bog.lbpas.view3d.utils.Config;
import bog.lbpas.view3d.utils.Cursors;
import bog.lbpas.view3d.utils.Utils;
import bog.lbpas.view3d.utils.print;
import com.github.weisj.jsvg.nodes.text.Text;
import org.joml.*;
import org.joml.Math;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

/**
 * @author Bog
 */
public abstract class ColorPicker extends ComboBox{


    public ColorPicker(String id, Vector2f pos, Vector2f size, int fontSize, RenderMan renderer, ObjectLoader loader, WindowMan window) {
        super();
        this.id = id;
        this.pos = pos;
        this.size = size;
        this.fontSize = fontSize;
        this.tabWidth = 250;
        this.renderer = renderer;
        this.loader = loader;
        this.window = window;

        init();
    }

    public ColorPicker(String id, int fontSize, RenderMan renderer, ObjectLoader loader, WindowMan window) {
        super();
        this.id = id;
        this.pos = new Vector2f();
        this.size = new Vector2f();
        this.fontSize = fontSize;
        this.tabWidth = 250;
        this.renderer = renderer;
        this.loader = loader;
        this.window = window;

        init();
    }

    public ColorPicker(String id, Vector4f color, int fontSize, RenderMan renderer, ObjectLoader loader, WindowMan window) {
        super();
        this.id = id;
        this.pos = new Vector2f();
        this.size = new Vector2f();
        this.fontSize = fontSize;
        this.tabWidth = 250;
        this.renderer = renderer;
        this.loader = loader;
        this.window = window;

        init();
    }

    Textbox red;
    Textbox green;
    Textbox blue;
    Textbox alpha;
    Textbox alphaPerc;
    Textbox hex;

    HUERamp hueRamp;
    SaturationLuminancePicker saturationLuminancePicker;
    private void init()
    {
        saturationLuminancePicker = new SaturationLuminancePicker("saturationLuminancePicker", new Vector2f(), new Vector2f(10, 150), this.renderer, this.loader, this.window);
        this.comboElements.add(saturationLuminancePicker);

        hueRamp = new HUERamp("hueRamp", new Vector2f(0, 0), new Vector2f(tabWidth - 4, getFontHeight(fontSize) + 4), renderer, loader, window, 0.5f, 0f, 1f);
        this.comboElements.add(hueRamp);

        updateColorValues();

        red = new Textbox("red", 10, renderer, loader, window).noLetters().noOthers().numberLimits(0, 255);
        green = new Textbox("green", 10, renderer, loader, window).noLetters().noOthers().numberLimits(0, 255);
        blue = new Textbox("blue", 10, renderer, loader, window).noLetters().noOthers().numberLimits(0, 255);
        alpha = new Textbox("alpha", 10, renderer, loader, window).noLetters().noOthers().numberLimits(0, 255);
        alphaPerc = new Textbox("alphaPerc", 10, renderer, loader, window).noLetters().noOthers().numberLimits(0, 100);
        hex = new Textbox("hex", 10, renderer, loader, window).noOthers();

        Panel rgbPanel = addPanel("rgbPanel");

        float panelWidth = Math.floor(tabWidth - 5f);
        float string = 20f / panelWidth;
        float element = ((panelWidth / 4f) / panelWidth) - string;

        rgbPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("redStr", "R:", 10, renderer), string));
        rgbPanel.elements.add(new Panel.PanelElement(red, element));
        rgbPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("greenStr", "G:", 10, renderer), string));
        rgbPanel.elements.add(new Panel.PanelElement(green, element));
        rgbPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("blueStr", "B:", 10, renderer), string));
        rgbPanel.elements.add(new Panel.PanelElement(blue, element));
        rgbPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("alphaStr", "A:", 10, renderer), string));
        rgbPanel.elements.add(new Panel.PanelElement(alpha, element));

        Panel hexPanel = addPanel("hexPanel");

        float string1 = 62f / panelWidth;
        float string2 = (getStringWidth("A:", 10) + 2f) / panelWidth;
        float string3 = (getStringWidth("%", 10) + 2) / panelWidth;

        float element1 = 61f / panelWidth;

        hexPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("hexStr", "HEX: #", 10, renderer), string1));
        hexPanel.elements.add(new Panel.PanelElement(hex, element1));
        hexPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("alphaStr", "A:", 10, renderer), string2));
        hexPanel.elements.add(new Panel.PanelElement(alphaPerc, element));
        hexPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("percStr", "%", 10, renderer), string3));
    }

    Model outlineColor;

    @Override
    public void draw(MouseInput mouseInput, boolean overOther)
    {
        Color color = getColor();
        color = color == null ? new Color(0f, 0f, 0f, 1f) : color;
        float fontHeight = getFontHeight(fontSize);

        hovering = isMouseOverTab(mouseInput) && !overOther;

        if(hovering)
            Cursors.setCursor(ECursor.hand2);

        float yOffset = 0;

        if (extended)
            yOffset = updateElements(yOffset);

        if(tabWidth != prevSize.x || size.y != prevSize.y || prevYOff != yOffset)
        {
            refreshOutline(yOffset);
            prevSize.x = size.x;
            prevSize.y = size.y;
            prevYOff = yOffset;
        }

        renderer.drawRect(Math.round(pos.x), Math.round(pos.y), Math.round(size.x), Math.round(size.y), ((mouseInput.rightButtonPress || mouseInput.leftButtonPress) && hovering) ? Config.INTERFACE_TERTIARY_COLOR : (hovering ? Config.INTERFACE_SECONDARY_COLOR : Config.INTERFACE_PRIMARY_COLOR));
        renderer.drawRectOutline(new Vector2f(pos.x, pos.y), outlineSelection, ((mouseInput.rightButtonPress || mouseInput.leftButtonPress) && hovering) ? Config.INTERFACE_TERTIARY_COLOR2 : (hovering ? Config.INTERFACE_SECONDARY_COLOR2 : Config.INTERFACE_PRIMARY_COLOR2), false);

        renderer.drawTransparencyCheckerBoard(new Vector2f(Math.round(pos.x + 4), Math.round(pos.y + 4)), new Vector2f(Math.round(size.x - 8), Math.round(size.y - 8)));
        renderer.drawRect(Math.round(pos.x + 3), Math.round(pos.y + 3), Math.round(size.x - 6), Math.round(size.y - 6), color);
        renderer.drawRectOutline(new Vector2f(pos.x + 3, pos.y + 3), outlineColor, extended ? Config.FONT_COLOR : Config.INTERFACE_PRIMARY_COLOR2, false);

        renderer.startScissor(Math.round(pos.x), Math.round(pos.y), Math.round(size.x - size.y), Math.round(size.y));
        renderer.drawString(tabTitle == null ? "" : tabTitle, Config.FONT_COLOR, Math.round(pos.x + (size.y + fontHeight) / 8f), Math.round(pos.y + size.y / 2f - fontHeight / 2f), fontSize);
        renderer.endScissor();

        if(extended)
        {
            renderer.startScissorEscape();
            drawBackdrop(yOffset);
            drawElements(mouseInput, overOther);
            renderer.endScissorEscape();
        }

        if(hueRamp.isSliding || saturationLuminancePicker.isPicking)
        {
            float h = hueRamp.getCurrentValue();
            Vector2f sv = saturationLuminancePicker.getSaturationBrightness();
            setColor(Utils.hsv2rgb(new Vector3f(h, sv.x, sv.y), color.getAlpha() / 255f));
            saturationLuminancePicker.hsv.x = h;
        }
    }

    public void updateColorValues()
    {
        Color c = this.getColor();
        c = c == null ? new Color(0f, 0f, 0f, 1f) : c;

        Vector3f hsv = Utils.rgbToHsv(c);
        saturationLuminancePicker.hsv = new Vector3f(hsv.x, hsv.y, 1 - hsv.z);
        hueRamp.setHue(hsv.x);
    }

    @Override
    public void refreshOutline(float yOffset)
    {
        super.refreshOutline(yOffset);
        if(outlineColor != null)
            this.outlineColor.cleanup(loader);
        this.outlineColor = LineStrip.processVerts(LineStrip.getRectangle(new Vector2f(size).sub(6, 6)), loader, window);
    }

    @Override
    public void secondThread() {
        super.secondThread();

        Color color = getColor();
        color = color == null ? new Color(0f, 0f, 0f, 1f) : color;

        int prevRed = color.getRed();
        int prevGreen = color.getGreen();
        int prevBlue = color.getBlue();
        int prevAlpha = color.getAlpha();

        Vector2i r = red.setTextboxValueInt(prevRed);
        Vector2i g = green.setTextboxValueInt(prevGreen);
        Vector2i b = blue.setTextboxValueInt(prevBlue);
        Vector2i a = alpha.setTextboxValueInt(prevAlpha);

        Vector2i a2 = alphaPerc.setTextboxValueInt(Math.clamp(0, 100, (prevAlpha / 255f) * 100f));
        String h = hex.setTextboxValueString(Utils.toHexColor(color));

        if(r.y == 1)
            setColor(new Color(r.x, prevGreen, prevBlue, prevAlpha));
        if(g.y == 1)
            setColor(new Color(prevRed, g.x, prevBlue, prevAlpha));
        if(b.y == 1)
            setColor(new Color(prevRed, prevGreen, b.x, prevAlpha));
        if(a.y == 1)
            setColor(new Color(prevRed, prevGreen, prevBlue, a.x));
        if(a2.y == 1)
            setColor(new Color(prevRed, prevGreen, prevBlue, Math.round((a2.x / 100f) * 255f)));
        if(h != null)
        {
            Color c = Utils.parseHexColor(h);
            setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), prevAlpha));
        }

        color = getColor();
        color = color == null ? new Color(0f, 0f, 0f, 1f) : color;

        if(prevRed != color.getRed() || prevGreen != color.getGreen() || prevBlue != color.getBlue() || prevAlpha != color.getAlpha())
            updateColorValues();
    }

    @Override
    public void onExtend() {
        super.onExtend();

        if(!extended)
            return;

        updateColorValues();
    }

    private static class SaturationLuminancePicker extends Element
    {
        Model outlineRect;
        boolean isPicking = false;

        public SaturationLuminancePicker(String id, Vector2f pos, Vector2f size, RenderMan renderer, ObjectLoader loader, WindowMan window) {
            this.id = id;
            this.pos = pos;
            this.size = size;
            this.renderer = renderer;
            this.loader = loader;
            this.window = window;
        }

        public Vector3f hsv = new Vector3f();

        @Override
        public void draw(MouseInput mouseInput, boolean overElement) {
            super.draw(mouseInput, overElement);

            Color c = Config.INTERFACE_PRIMARY_COLOR2;

            if(hovering)
                c = Config.INTERFACE_SECONDARY_COLOR2;

            renderer.drawSaturationLuminancePicker(new Vector2f(pos.x + 1, pos.y + 1), new Vector2f(size.x - 2, size.y - 2), Utils.hsv2rgbVec(new Vector3f(hsv.x, 1f, 1f), 1.0f));
            renderer.drawRectOutline(new Vector2f(Math.round(pos.x), Math.round(pos.y)), outlineRect, c, false);

            if(isPicking)
            {
                Cursors.setCursor(ECursor.crosshair);
                hsv.y = (float) Math.clamp(0f, 1f, (Math.clamp(pos.x, pos.x + size.x, mouseInput.currentPos.x) - pos.x) / size.x);
                hsv.z = (float) Math.clamp(0f, 1f, (Math.clamp(pos.y, pos.y + size.y, mouseInput.currentPos.y) - pos.y) / size.y);
            }
            else
                renderer.drawImageStatic(ConstantTextures.getTexture(ConstantTextures.CROSSHAIR, 15, 15, loader), Math.round(hsv.y * size.x + pos.x - 7.5f), Math.round(hsv.z * size.y + pos.y - 7.5f), 15, 15, Color.white);
        }

        public Vector2f getSaturationBrightness()
        {
            return new Vector2f(hsv.y, 1 - hsv.z);
        }

        @Override
        public void resize() {
            super.resize();
            if(this.outlineRect != null)
                this.outlineRect.cleanup(loader);
            this.outlineRect = LineStrip.processVerts(LineStrip.getRectangle(size), loader, window);
        }

        @Override
        public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement, boolean focusedOther) {
            super.onClick(mouseInput, pos, button, action, mods, overElement, focusedOther);

            if(button == GLFW.GLFW_MOUSE_BUTTON_1)
            {
                if(action == GLFW.GLFW_PRESS && isMouseOverElement(pos) && !overElement)
                    isPicking = true;
                else if(action == GLFW.GLFW_RELEASE)
                    isPicking = false;
            }
        }

        @Override
        public void setFocused(boolean focused) {
            super.setFocused(focused);
            if(!focused)
                isPicking = false;
        }

        @Override
        public boolean isFocused() {
            return super.isFocused() || isPicking;
        }

        @Override
        public void hoverCursor() {
            if(!isPicking)
                Cursors.setCursor(ECursor.hand2);
        }
    }
    private static class HUERamp extends Slider
    {
        public HUERamp(String id, Vector2f pos, Vector2f size, RenderMan renderer, ObjectLoader loader, WindowMan window) {
            super(id, pos, size, renderer, loader, window);
        }
        public HUERamp(String id, Vector2f pos, Vector2f size, RenderMan renderer, ObjectLoader loader, WindowMan window, float sliderPosition, float min, float max) {
            super(id, pos, size, renderer, loader, window, sliderPosition, min, max);
        }

        @Override
        public void draw(MouseInput mouseInput, boolean overElement) {
            hovering = isMouseOverElement(mouseInput) && !overElement;
            if(hovering)
                hoverCursor();

            if(size.x != prevSize.x || size.y != prevSize.y)
            {
                refreshOutline();
                prevSize = size;
            }

            if(isSliding)
            {
                sliderPosition = ((((float)mouseInput.currentPos.x - (size.y * 0.2f) / 2f) - pos.x)/(size.x - size.y * 0.2f)) * 100f;
                sliderPosition = Math.clamp(0, 100, sliderPosition);
            }

            Color c = Config.INTERFACE_PRIMARY_COLOR2;

            if(hovering || isSliding)
                c = Config.INTERFACE_SECONDARY_COLOR2;

            renderer.drawHUERamp(Math.round(pos.x), Math.round(pos.y + size.y/2f - size.y * 0.3f), Math.round(size.x), Math.round(size.y * 0.6f));
            renderer.drawRectOutline(new Vector2f(Math.round(pos.x), Math.round(pos.y + size.y/2f - size.y * 0.3f)), outlineRect, c, false);
            if(!Float.isNaN(sliderPosition))
                renderer.drawRect(Math.round(pos.x + (sliderPosition * ((size.x - size.y * 0.1f)/100))), Math.round(pos.y), Math.round(size.y * 0.15f), Math.round(size.y), Config.FONT_COLOR);

        }

        @Override
        public void refreshOutline()
        {
            if(this.outlineRect != null)
                this.outlineRect.cleanup(loader);
            this.outlineRect = LineStrip.processVerts(LineStrip.getRectangle(new Vector2f(Math.round(size.x), Math.round(size.y * 0.6f))), loader, window);
        }

        public void setHue(float hue)
        {
            sliderPosition = hue * 100f;
        }
    }

    public abstract Color getColor();
    public abstract void setColor(Color color);
}
