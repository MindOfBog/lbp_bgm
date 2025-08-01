package bog.lbpas.view3d.mainWindow.screens;

import bog.lbpas.Main;
import bog.lbpas.view3d.core.Texture;
import bog.lbpas.view3d.mainWindow.View3D;
import bog.lbpas.view3d.managers.InputMan;
import bog.lbpas.view3d.managers.MouseInput;
import bog.lbpas.view3d.renderer.gui.GuiKeybind;
import bog.lbpas.view3d.renderer.gui.GuiScreen;
import bog.lbpas.view3d.renderer.gui.cursor.Cursor;
import bog.lbpas.view3d.renderer.gui.elements.*;
import bog.lbpas.view3d.renderer.gui.elements.Button;
import bog.lbpas.view3d.renderer.gui.elements.Checkbox;
import bog.lbpas.view3d.renderer.gui.elements.Panel;
import bog.lbpas.view3d.renderer.gui.font.FNT;
import bog.lbpas.view3d.renderer.gui.font.FontRenderer;
import bog.lbpas.view3d.utils.Config;
import bog.lbpas.view3d.utils.Cursors;
import bog.lbpas.view3d.utils.Utils;
import bog.lbpas.view3d.utils.print;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3d;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * @author Bog
 */
public class Settings extends GuiScreen{

    View3D mainView;

    public Settings(View3D mainView)
    {
        super(mainView.renderer, mainView.loader, mainView.window);
        this.mainView = mainView;
        this.init();
    }

    public DropDownTab rendererSettings;
    public DropDownTab guiSettings;
    public DropDownTab controls;
    public DropDownTab performance;
    public DropDownTab debug;

    public Textbox fps;
    public Textbox moveSpeed;
    public Textbox sensitivity;
    public Textbox zNear;
    public Textbox zFar;
    public Textbox msaa;
    public ColorPicker outlineColor;
    public ColorPicker borderColor1;
    public ColorPicker borderColor2;
    public ColorPicker borderColor3;
    public ColorPicker borderColor4;
    public ColorPicker earthColor;
    public ColorPicker podColor;
    public ColorPicker fontColor;
    public ColorPicker primaryColor;
    public ColorPicker secondaryColor;
    public ColorPicker interfacePrimaryColor;
    public ColorPicker interfacePrimaryColor2;
    public ColorPicker interfaceSecondaryColor;
    public ColorPicker interfaceSecondaryColor2;
    public ColorPicker interfaceTertiaryColor;
    public ColorPicker interfaceTertiaryColor2;

    Textbox cursorSize;
    public Slider fov;
    public Checkbox culling;
    public Checkbox showFPS;
    public Checkbox legacyFD;
    public Slider outlineSize;

    public Checkbox debugScissorTest;
    public Checkbox debugOverlayImage;

    public Checkbox disableBlur;
    public Checkbox disableSSAO;
    public Textbox secondaryThreadTicks;
    public Textbox loaderThreadTicks;
    public Textbox entryDigestThreadTicks;

    public Slider guiSizeSlider;

    public void init() {
        rendererSettings = new DropDownTab("rendererSettings", "Renderer Settings", new Vector2f(10, 21 + 10), new Vector2f(200, getFontHeight() + 4), renderer, loader, window).closed();
        culling = rendererSettings.addCheckbox("culling", "No culling", Config.NO_CULLING);

        Panel fovPanel = rendererSettings.addPanel("fovPanel");
        fovPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("", "FOV:", renderer), 0.525f));
        fov = new Slider("", new Vector2f(), new Vector2f(), renderer, loader, window, (float) Math.toDegrees(Config.FOV), 20, 175);
        fovPanel.elements.add(new Panel.PanelElement(fov, 0.475f));

        Panel fpsPanel = rendererSettings.addPanel("fpsPanel");
        fpsPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("", "FPS:", renderer), 0.525f));
        fps = new Textbox("", new Vector2f(), new Vector2f(), renderer, loader, window);
        fps.noLetters().noOthers();
        fps.setText(Float.toString(Config.FRAMERATE));
        fpsPanel.elements.add(new Panel.PanelElement(fps, 0.475f));

        Panel zNearPanel = rendererSettings.addPanel("zNearPanel");
        zNearPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("", "Z Near:", renderer), 0.525f));
        zNear = new Textbox("", new Vector2f(), new Vector2f(), renderer, loader, window);
        zNear.noLetters().noOthers();
        zNear.setText(Float.toString(Config.Z_NEAR));
        zNearPanel.elements.add(new Panel.PanelElement(zNear, 0.475f));

        Panel zFarPanel = rendererSettings.addPanel("zFarPanel");
        zFarPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("", "Z Far:", renderer), 0.525f));
        zFar = new Textbox("", new Vector2f(), new Vector2f(), renderer, loader, window);
        zFar.noLetters().noOthers();
        zFar.setText(Float.toString(Config.Z_FAR));
        zFarPanel.elements.add(new Panel.PanelElement(zFar, 0.475f));

        Panel msaaPanel = rendererSettings.addPanel("msaaPanel");
        msaaPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("", "AA Samples:", renderer), 0.525f));
        msaa = new Textbox("", new Vector2f(), new Vector2f(), renderer, loader, window);
        msaa.noLetters().noOthers();
        msaa.numberLimits(1, 8);
        msaa.setText(Float.toString(Config.Z_FAR));
        msaaPanel.elements.add(new Panel.PanelElement(msaa, 0.475f));

        rendererSettings.addString("outlineSizeLabel", "Outline Size:");
        outlineSize = rendererSettings.addSlider("outlineSize", Config.OUTLINE_DISTANCE, 0.525f, 1.1f);

        Panel outlineColorPanel = rendererSettings.addPanel("outlineColorPickerPanel");
        outlineColorPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("outlineColorLabel", "Outline Color:", mainView.renderer), 0.7f));
        outlineColor = new ColorPicker("outlineColor", mainView.renderer, mainView.loader, mainView.window) {
            @Override
            public Color getColor() {
                return Config.OUTLINE_COLOR;
            }

            @Override
            public void setColor(Color color) {
                Config.OUTLINE_COLOR = color;
            }

            @Override
            public int[] getParentTransform() {
                return new int[]{(int) Math.round(rendererSettings.pos.x), (int) Math.round(rendererSettings.pos.y), (int) Math.round(rendererSettings.size.x)};
            }
        };
        outlineColorPanel.elements.add(new Panel.PanelElement(outlineColor, 0.3f));

        Panel borderColor1Panel = rendererSettings.addPanel("borderColor1PickerPanel");
        borderColor1Panel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("borderColor1Label", "Border Color 1:", mainView.renderer), 0.7f));
        borderColor1 = new ColorPicker("borderColor1", mainView.renderer, mainView.loader, mainView.window) {
            @Override
            public Color getColor() {
                return Config.BORDER_COLOR_1;
            }

            @Override
            public void setColor(Color color) {
                Config.BORDER_COLOR_1 = color;
                mainView.borders.material.setColor(color);
            }

            @Override
            public int[] getParentTransform() {
                return new int[]{(int) Math.round(rendererSettings.pos.x), (int) Math.round(rendererSettings.pos.y), (int) Math.round(rendererSettings.size.x)};
            }
        };
        borderColor1Panel.elements.add(new Panel.PanelElement(borderColor1, 0.3f));

        Panel borderColor2Panel = rendererSettings.addPanel("borderColor2PickerPanel");
        borderColor2Panel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("borderColor2Label", "Border Color 2:", mainView.renderer), 0.7f));
        borderColor2 = new ColorPicker("borderColor2", mainView.renderer, mainView.loader, mainView.window) {
            @Override
            public Color getColor() {
                return Config.BORDER_COLOR_2;
            }

            @Override
            public void setColor(Color color) {
                Config.BORDER_COLOR_2 = color;
                mainView.borders1.material.setColor(color);
            }

            @Override
            public int[] getParentTransform() {
                return new int[]{(int) Math.round(rendererSettings.pos.x), (int) Math.round(rendererSettings.pos.y), (int) Math.round(rendererSettings.size.x)};
            }
        };
        borderColor2Panel.elements.add(new Panel.PanelElement(borderColor2, 0.3f));

        Panel borderColor3Panel = rendererSettings.addPanel("borderColor3PickerPanel");
        borderColor3Panel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("borderColor3Label", "Border Color 3:", mainView.renderer), 0.7f));
        borderColor3 = new ColorPicker("borderColor3", mainView.renderer, mainView.loader, mainView.window) {
            @Override
            public Color getColor() {
                return Config.BORDER_COLOR_3;
            }

            @Override
            public void setColor(Color color) {
                Config.BORDER_COLOR_3 = color;
                mainView.borders2.material.setColor(color);
            }

            @Override
            public int[] getParentTransform() {
                return new int[]{(int) Math.round(rendererSettings.pos.x), (int) Math.round(rendererSettings.pos.y), (int) Math.round(rendererSettings.size.x)};
            }
        };
        borderColor3Panel.elements.add(new Panel.PanelElement(borderColor3, 0.3f));

        Panel borderColor4Panel = rendererSettings.addPanel("borderColor4PickerPanel");
        borderColor4Panel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("borderColor4Label", "Border Color 4:", mainView.renderer), 0.7f));
        borderColor4 = new ColorPicker("borderColor4", mainView.renderer, mainView.loader, mainView.window) {
            @Override
            public Color getColor() {
                return Config.BORDER_COLOR_4;
            }

            @Override
            public void setColor(Color color) {
                Config.BORDER_COLOR_4 = color;
                mainView.borders3.material.setColor(color);
            }

            @Override
            public int[] getParentTransform() {
                return new int[]{(int) Math.round(rendererSettings.pos.x), (int) Math.round(rendererSettings.pos.y), (int) Math.round(rendererSettings.size.x)};
            }
        };
        borderColor4Panel.elements.add(new Panel.PanelElement(borderColor4, 0.3f));

        Panel earthColorPanel = rendererSettings.addPanel("earthColorPickerPanel");
        earthColorPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("earthColorLabel", "Earth Color:", mainView.renderer), 0.7f));
        earthColor = new ColorPicker("earthColor", mainView.renderer, mainView.loader, mainView.window) {
            @Override
            public Color getColor() {
                return Config.EARTH_COLOR;
            }

            @Override
            public void setColor(Color color) {
                Config.EARTH_COLOR = color;
                mainView.earth.material.setOverlayColor(color);
            }

            @Override
            public int[] getParentTransform() {
                return new int[]{(int) Math.round(rendererSettings.pos.x), (int) Math.round(rendererSettings.pos.y), (int) Math.round(rendererSettings.size.x)};
            }
        };
        earthColorPanel.elements.add(new Panel.PanelElement(earthColor, 0.3f));

        Panel podColorPanel = rendererSettings.addPanel("podColorPickerPanel");
        podColorPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("podColorLabel", "Pod Color:", mainView.renderer), 0.7f));
        podColor = new ColorPicker("podColor", mainView.renderer, mainView.loader, mainView.window) {
            @Override
            public Color getColor() {
                return Config.POD_COLOR;
            }

            @Override
            public void setColor(Color color) {
                Config.POD_COLOR = color;
                mainView.pod.material.setOverlayColor(color);
            }

            @Override
            public int[] getParentTransform() {
                return new int[]{(int) Math.round(rendererSettings.pos.x), (int) Math.round(rendererSettings.pos.y), (int) Math.round(rendererSettings.size.x)};
            }
        };
        podColorPanel.elements.add(new Panel.PanelElement(podColor, 0.3f));

        //------------------------------------

        guiSettings = new DropDownTab("guiSettings", "GUI Settings", new Vector2f(10, 21 + 10 + 7 + rendererSettings.getFullHeight()), new Vector2f(200, getFontHeight() + 4), renderer, loader, window).closed();

        float gap = 1f / (guiSettings.size.x - 5f);
        float element = 0.5f - (gap / 2);

//        Panel guiSizePanel = guiSettings.addPanel("guiSizePanel");TODO
//        guiSizePanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("guiSizeStr", "Gui Size:", renderer), 0.4f));
//        guiSizeSlider = new Slider("guiSizeSlider", new Vector2f(), new Vector2f(), renderer, loader, window, 16, 12, 32);
//        guiSizePanel.elements.add(new Panel.PanelElement(guiSizeSlider, 0.6f));
//
//        guiSettings.addSeparator("backdropSep");

        guiSettings.addString("primarySecondaryColorLabel", "Backdrop Color:");

        Panel primaryColorPanel = guiSettings.addPanel("primaryColorPickerPanel");
        primaryColor = new ColorPicker("primaryColor", mainView.renderer, mainView.loader, mainView.window) {
            @Override
            public Color getColor() {
                return Config.PRIMARY_COLOR;
            }

            @Override
            public void setColor(Color color) {
                Config.PRIMARY_COLOR = color;
            }

            @Override
            public int[] getParentTransform() {
                return new int[]{(int) Math.round(guiSettings.pos.x), (int) Math.round(guiSettings.pos.y), (int) Math.round(guiSettings.size.x)};
            }
        };
        primaryColorPanel.elements.add(new Panel.PanelElement(primaryColor, element));
        primaryColorPanel.elements.add(new Panel.PanelElement(null, gap));
        secondaryColor = new ColorPicker("secondaryColor", mainView.renderer, mainView.loader, mainView.window) {
            @Override
            public Color getColor() {
                return Config.SECONDARY_COLOR;
            }

            @Override
            public void setColor(Color color) {
                Config.SECONDARY_COLOR = color;
            }

            @Override
            public int[] getParentTransform() {
                return new int[]{(int) Math.round(guiSettings.pos.x), (int) Math.round(guiSettings.pos.y), (int) Math.round(guiSettings.size.x)};
            }
        };
        primaryColorPanel.elements.add(new Panel.PanelElement(secondaryColor, element));

        guiSettings.addString("interfacePrimaryColorLabel", "Primary Color:");

        Panel interfacePrimaryColorPanel = guiSettings.addPanel("interfacePrimaryColorPickerPanel");
        interfacePrimaryColor = new ColorPicker("interfacePrimaryColor", mainView.renderer, mainView.loader, mainView.window) {
            @Override
            public Color getColor() {
                return Config.INTERFACE_PRIMARY_COLOR;
            }

            @Override
            public void setColor(Color color) {
                Config.INTERFACE_PRIMARY_COLOR = color;
            }

            @Override
            public int[] getParentTransform() {
                return new int[]{(int) Math.round(guiSettings.pos.x), (int) Math.round(guiSettings.pos.y), (int) Math.round(guiSettings.size.x)};
            }
        };
        interfacePrimaryColorPanel.elements.add(new Panel.PanelElement(interfacePrimaryColor, element));
        interfacePrimaryColorPanel.elements.add(new Panel.PanelElement(null, gap));
        interfacePrimaryColor2 = new ColorPicker("interfacePrimaryColor2", mainView.renderer, mainView.loader, mainView.window) {
            @Override
            public Color getColor() {
                return Config.INTERFACE_PRIMARY_COLOR2;
            }

            @Override
            public void setColor(Color color) {
                Config.INTERFACE_PRIMARY_COLOR2 = color;
            }

            @Override
            public int[] getParentTransform() {
                return new int[]{(int) Math.round(guiSettings.pos.x), (int) Math.round(guiSettings.pos.y), (int) Math.round(guiSettings.size.x)};
            }
        };
        interfacePrimaryColorPanel.elements.add(new Panel.PanelElement(interfacePrimaryColor2, element));

        guiSettings.addString("interfaceSecondaryColorLabel", "Secondary Color:");

        Panel interfaceSecondaryColorPanel = guiSettings.addPanel("interfaceSecondaryColorPanel");
        interfaceSecondaryColor = new ColorPicker("interfaceSecondaryColor", mainView.renderer, mainView.loader, mainView.window) {
            @Override
            public Color getColor() {
                return Config.INTERFACE_SECONDARY_COLOR;
            }

            @Override
            public void setColor(Color color) {
                Config.INTERFACE_SECONDARY_COLOR = color;
            }

            @Override
            public int[] getParentTransform() {
                return new int[]{(int) Math.round(guiSettings.pos.x), (int) Math.round(guiSettings.pos.y), (int) Math.round(guiSettings.size.x)};
            }
        };
        interfaceSecondaryColorPanel.elements.add(new Panel.PanelElement(interfaceSecondaryColor, element));
        interfaceSecondaryColorPanel.elements.add(new Panel.PanelElement(null, gap));
        interfaceSecondaryColor2 = new ColorPicker("interfaceSecondaryColor2", mainView.renderer, mainView.loader, mainView.window) {
            @Override
            public Color getColor() {
                return Config.INTERFACE_SECONDARY_COLOR2;
            }

            @Override
            public void setColor(Color color) {
                Config.INTERFACE_SECONDARY_COLOR2 = color;
            }

            @Override
            public int[] getParentTransform() {
                return new int[]{(int) Math.round(guiSettings.pos.x), (int) Math.round(guiSettings.pos.y), (int) Math.round(guiSettings.size.x)};
            }
        };
        interfaceSecondaryColorPanel.elements.add(new Panel.PanelElement(interfaceSecondaryColor2, element));

        guiSettings.addString("interfaceTertiaryColorLabel", "Tertiary Color:");

        Panel interfaceTertiaryColorPanel = guiSettings.addPanel("interfaceTertiaryColorPanel");
        interfaceTertiaryColor = new ColorPicker("interfaceTertiaryColor", mainView.renderer, mainView.loader, mainView.window) {
            @Override
            public Color getColor() {
                return Config.INTERFACE_TERTIARY_COLOR;
            }

            @Override
            public void setColor(Color color) {
                Config.INTERFACE_TERTIARY_COLOR = color;
            }

            @Override
            public int[] getParentTransform() {
                return new int[]{(int) Math.round(guiSettings.pos.x), (int) Math.round(guiSettings.pos.y), (int) Math.round(guiSettings.size.x)};
            }
        };
        interfaceTertiaryColorPanel.elements.add(new Panel.PanelElement(interfaceTertiaryColor, element));
        interfaceTertiaryColorPanel.elements.add(new Panel.PanelElement(null, gap));
        interfaceTertiaryColor2 = new ColorPicker("interfaceTertiaryColor2", mainView.renderer, mainView.loader, mainView.window) {
            @Override
            public Color getColor() {
                return Config.INTERFACE_TERTIARY_COLOR2;
            }

            @Override
            public void setColor(Color color) {
                Config.INTERFACE_TERTIARY_COLOR2 = color;
            }

            @Override
            public int[] getParentTransform() {
                return new int[]{(int) Math.round(guiSettings.pos.x), (int) Math.round(guiSettings.pos.y), (int) Math.round(guiSettings.size.x)};
            }
        };
        interfaceTertiaryColorPanel.elements.add(new Panel.PanelElement(interfaceTertiaryColor2, element));

        DropDownTab.SeparatorElement fontSep = guiSettings.addSeparator("fontSep");
        fontSep.size.y = 5;

        guiSettings.addString("fontSeg", "Font:");

        Panel fontColorPanel = guiSettings.addPanel("fontColorPickerPanel");
        fontColorPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("fontColorLabel", "Color:", mainView.renderer), 0.7f));
        fontColor = new ColorPicker("fontColor", mainView.renderer, mainView.loader, mainView.window) {
            @Override
            public Color getColor() {
                return Config.FONT_COLOR;
            }

            @Override
            public void setColor(Color color) {
                Config.FONT_COLOR = color;
            }

            @Override
            public int[] getParentTransform() {
                return new int[]{(int) Math.round(guiSettings.pos.x), (int) Math.round(guiSettings.pos.y), (int) Math.round(guiSettings.size.x)};
            }
        };
        fontColorPanel.elements.add(new Panel.PanelElement(fontColor, 0.3f));

        Panel headerPanel = guiSettings.addPanel("headerPanel");
        headerPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("", "Header:", renderer), 0.35f));

        ComboBox headerCombo = new ComboBox("", Config.FONT_HEADER, new Vector2f(), new Vector2f(), 200, renderer, loader, window);

        ButtonList headerList = new ButtonList("", FontRenderer.Fonts, new Vector2f(), new Vector2f(), renderer, loader, window) {
            @Override
            public void clickedButton(Object object, int index, int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)
                    FontRenderer.headerFont = index;
                Config.FONT_HEADER = FontRenderer.Fonts.get(index).info.face;
                headerCombo.tabTitle = Config.FONT_HEADER;
            }

            @Override
            public void hoveringButton(Object object, int index) {
                hovering = index;
            }

            int hovering = -1;

            @Override
            public void draw(MouseInput mouseInput, boolean overElement) {
                hovering = -1;
                super.draw(mouseInput, overElement);
            }

            @Override
            public boolean isHighlighted(Object object, int index) {
                return hovering == index;
            }

            @Override
            public boolean isSelected(Object object, int index) {
                return FontRenderer.headerFont == index;
            }

            @Override
            public String buttonText(Object object, int index) {
                FNT font = (FNT) object;
                return font.info.face;
            }

            @Override
            public boolean searchFilter(Object object, int index) {
                return true;
            }

            @Override
            public int buttonHeight() {
                return super.buttonHeight() * 2;
            }
        };
        headerCombo.addList("headerList", headerList, 80);

        headerPanel.elements.add(new Panel.PanelElement(headerCombo, 0.65f));

        Panel textPanel = guiSettings.addPanel("textPanel");
        textPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("", "Text:", renderer), 0.35f));

        ComboBox textCombo = new ComboBox("", Config.FONT_TEXT, new Vector2f(), new Vector2f(), 200, renderer, loader, window);

        ButtonList textList = new ButtonList("", FontRenderer.Fonts, new Vector2f(), new Vector2f(), renderer, loader, window) {
            @Override
            public void clickedButton(Object object, int index, int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)
                    FontRenderer.textFont = index;
                Config.FONT_TEXT = FontRenderer.Fonts.get(index).info.face;
                textCombo.tabTitle = Config.FONT_TEXT;
            }

            @Override
            public void hoveringButton(Object object, int index) {
                hovering = index;
            }

            int hovering = -1;

            @Override
            public void draw(MouseInput mouseInput, boolean overElement) {
                hovering = -1;
                super.draw(mouseInput, overElement);
            }

            @Override
            public boolean isHighlighted(Object object, int index) {
                return hovering == index;
            }

            @Override
            public boolean isSelected(Object object, int index) {
                return FontRenderer.textFont == index;
            }

            @Override
            public String buttonText(Object object, int index) {
                FNT font = (FNT) object;
                return font.info.face;
            }

            @Override
            public boolean searchFilter(Object object, int index) {
                return true;
            }

            @Override
            public int buttonHeight() {
                return super.buttonHeight() * 2;
            }
        };
        textCombo.addList("textList", textList, 80);

        textPanel.elements.add(new Panel.PanelElement(textCombo, 0.65f));

        DropDownTab.SeparatorElement cursorSep = guiSettings.addSeparator("cursorSep");
        cursorSep.size.y = 5;

        Panel cursorPanel = guiSettings.addPanel("cursorPanel");
        cursorPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("", "Cursor:", renderer), 0.35f));

        ComboBox cursorCombo = new ComboBox("", Config.CURSOR, new Vector2f(), new Vector2f(), 200, renderer, loader, window);
        ButtonList cursorList = new ButtonList("", Cursors.loadedCursors, new Vector2f(), new Vector2f(), renderer, loader, window) {
            @Override
            public void clickedButton(Object object, int index, int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)
                    Cursors.activeCursor = index;
                Config.CURSOR = Cursors.loadedCursors.get(index).name;
                cursorCombo.tabTitle = Config.CURSOR;
            }

            @Override
            public void hoveringButton(Object object, int index) {
                hovering = index;
            }

            int hovering = -1;

            @Override
            public void draw(MouseInput mouseInput, boolean overElement) {
                hovering = -1;
                super.draw(mouseInput, overElement);
            }

            @Override
            public boolean isHighlighted(Object object, int index) {
                return hovering == index;
            }

            @Override
            public boolean isSelected(Object object, int index) {
                return Cursors.activeCursor == index;
            }

            @Override
            public String buttonText(Object object, int index) {
                Cursor cursor = (Cursor) object;
                return cursor.name;
            }

            @Override
            public boolean searchFilter(Object object, int index) {
                return true;
            }

            @Override
            public int buttonHeight() {
                return super.buttonHeight() * 2;
            }
        };
        cursorCombo.addList("cursorList", cursorList, 80);

        cursorPanel.elements.add(new Panel.PanelElement(cursorCombo, 0.65f));

        Panel cursorSizePanel = guiSettings.addPanel("cursorSizePanel");
        cursorSizePanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("", "Size:", renderer), 0.35f));
        cursorSize = new Textbox("", new Vector2f(), new Vector2f(), renderer, loader, window);
        cursorSize.setText("" + Config.CURSOR_SCALE);
        cursorSizePanel.elements.add(new Panel.PanelElement(cursorSize, 0.65f));

        DropDownTab.SeparatorElement otherSep = guiSettings.addSeparator("otherSep");
        otherSep.size.y = 5;

        showFPS = guiSettings.addCheckbox("showFPS", "Show FPS", Config.SHOW_FPS);
        legacyFD = guiSettings.addCheckbox("legacyFD", "Native File Dialogue", Config.LEGACY_FD);

        controls = new DropDownTab("controls", "Controls", new Vector2f(10, 21 + 10 + 14 + rendererSettings.getFullHeight() + guiSettings.getFullHeight()), new Vector2f(200, getFontHeight() + 4), renderer, loader, window).closed();

        float buttonWidth = 0.62f;

        Panel forwardPanel = controls.addPanel("forwardPanel");
        forwardPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("", "Forward:", renderer), 1f - buttonWidth));
        Button forward = new Button("", Config.FORWARD.inputName(), new Vector2f(), new Vector2f(), renderer, loader, window)
        {
            @Override
            public void clickedButton(int button, int action, int mods) {

                if(!isClicked)
                    return;

                Button btn = this;
                mainView.setCurrentScreen(new GuiKeybind(Config.FORWARD, renderer, loader, window) {
                    @Override
                    public void keybind(InputMan currentKey) {
                        Config.FORWARD.key = currentKey.key;
                        Config.FORWARD.mouse = currentKey.mouse;
                        btn.buttonText = Config.FORWARD.inputName();

                        if(btn.buttonText == null)
                            btn.buttonText = "error";
                    }

                    @Override
                    public void returnPreviousScreen() {
                        mainView.returnToPreviousScreen();
                    }
                });
            }
        };
        forwardPanel.elements.add(new Panel.PanelElement(forward, buttonWidth));

        Panel backPanel = controls.addPanel("backPanel");
        backPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("", "Back:", renderer), 1f - buttonWidth));
        Button back = new Button("", Config.BACK.inputName(), new Vector2f(), new Vector2f(), renderer, loader, window)
        {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(!isClicked)
                    return;
                Button btn = this;
                mainView.setCurrentScreen(new GuiKeybind(Config.BACK, renderer, loader, window) {
                    @Override
                    public void keybind(InputMan currentKey) {
                        Config.BACK.key = currentKey.key;
                        Config.BACK.mouse = currentKey.mouse;
                        btn.buttonText = Config.BACK.inputName();

                        if(btn.buttonText == null)
                            btn.buttonText = "error";
                    }

                    @Override
                    public void returnPreviousScreen() {
                        mainView.returnToPreviousScreen();
                    }
                });
            }
        };
        backPanel.elements.add(new Panel.PanelElement(back, buttonWidth));

        Panel leftPanel = controls.addPanel("leftPanel");
        leftPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("", "Left:", renderer), 1f - buttonWidth));
        Button left = new Button("", Config.LEFT.inputName(), new Vector2f(), new Vector2f(), renderer, loader, window)
        {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(!isClicked)
                    return;
                Button btn = this;
                mainView.setCurrentScreen(new GuiKeybind(Config.LEFT, renderer, loader, window) {
                    @Override
                    public void keybind(InputMan currentKey) {
                        Config.LEFT.key = currentKey.key;
                        Config.LEFT.mouse = currentKey.mouse;
                        btn.buttonText = Config.LEFT.inputName();

                        if(btn.buttonText == null)
                            btn.buttonText = "error";
                    }

                    @Override
                    public void returnPreviousScreen() {
                        mainView.returnToPreviousScreen();
                    }
                });
            }
        };
        leftPanel.elements.add(new Panel.PanelElement(left, buttonWidth));

        Panel rightPanel = controls.addPanel("rightPanel");
        rightPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("", "Right:", renderer), 1f - buttonWidth));
        Button right = new Button("", Config.RIGHT.inputName(), new Vector2f(), new Vector2f(), renderer, loader, window)
        {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(!isClicked)
                    return;
                Button btn = this;
                mainView.setCurrentScreen(new GuiKeybind(Config.RIGHT, renderer, loader, window) {
                    @Override
                    public void keybind(InputMan currentKey) {
                        Config.RIGHT.key = currentKey.key;
                        Config.RIGHT.mouse = currentKey.mouse;
                        btn.buttonText = Config.RIGHT.inputName();

                        if(btn.buttonText == null)
                            btn.buttonText = "error";
                    }

                    @Override
                    public void returnPreviousScreen() {
                        mainView.returnToPreviousScreen();
                    }
                });
            }
        };
        rightPanel.elements.add(new Panel.PanelElement(right, buttonWidth));

        Panel upPanel = controls.addPanel("upPanel");
        upPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("", "Up:", renderer), 1f - buttonWidth));
        Button up = new Button("", Config.UP.inputName(), new Vector2f(), new Vector2f(), renderer, loader, window)
        {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(!isClicked)
                    return;
                Button btn = this;
                mainView.setCurrentScreen(new GuiKeybind(Config.UP, renderer, loader, window) {
                    @Override
                    public void keybind(InputMan currentKey) {
                        Config.UP.key = currentKey.key;
                        Config.UP.mouse = currentKey.mouse;
                        btn.buttonText = Config.UP.inputName();

                        if(btn.buttonText == null)
                            btn.buttonText = "error";
                    }

                    @Override
                    public void returnPreviousScreen() {
                        mainView.returnToPreviousScreen();
                    }
                });
            }
        };
        upPanel.elements.add(new Panel.PanelElement(up, buttonWidth));

        Panel downPanel = controls.addPanel("downPanel");
        downPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("", "Down:", renderer), 1f - buttonWidth));
        Button down = new Button("", Config.DOWN.inputName(), new Vector2f(), new Vector2f(), renderer, loader, window)
        {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(!isClicked)
                    return;
                Button btn = this;
                mainView.setCurrentScreen(new GuiKeybind(Config.DOWN, renderer, loader, window) {
                    @Override
                    public void keybind(InputMan currentKey) {
                        Config.DOWN.key = currentKey.key;
                        Config.DOWN.mouse = currentKey.mouse;
                        btn.buttonText = Config.DOWN.inputName();

                        if(btn.buttonText == null)
                            btn.buttonText = "error";
                    }

                    @Override
                    public void returnPreviousScreen() {
                        mainView.returnToPreviousScreen();
                    }
                });
            }
        };
        downPanel.elements.add(new Panel.PanelElement(down, buttonWidth));

        Panel shadingPanel = controls.addPanel("shadingPanel");
        shadingPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("", "Shading:", renderer), 1f - buttonWidth));
        Button shading = new Button("", Config.SHADING.inputName(), new Vector2f(), new Vector2f(), renderer, loader, window)
        {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(!isClicked)
                    return;
                Button btn = this;
                mainView.setCurrentScreen(new GuiKeybind(Config.SHADING, renderer, loader, window) {
                    @Override
                    public void keybind(InputMan currentKey) {
                        Config.SHADING.key = currentKey.key;
                        Config.SHADING.mouse = currentKey.mouse;
                        btn.buttonText = Config.SHADING.inputName();

                        if(btn.buttonText == null)
                            btn.buttonText = "error";
                    }

                    @Override
                    public void returnPreviousScreen() {
                        mainView.returnToPreviousScreen();
                    }
                });
            }
        };
        shadingPanel.elements.add(new Panel.PanelElement(shading, buttonWidth));

        Panel cameraPanel = controls.addPanel("cameraPanel");
        cameraPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("", "Camera:", renderer), 1f - buttonWidth));
        Button camera = new Button("", Config.CAMERA.inputName(), new Vector2f(), new Vector2f(), renderer, loader, window)
        {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(!isClicked)
                    return;
                Button btn = this;
                mainView.setCurrentScreen(new GuiKeybind(Config.CAMERA, renderer, loader, window) {
                    @Override
                    public void keybind(InputMan currentKey) {
                        Config.CAMERA.key = currentKey.key;
                        Config.CAMERA.mouse = currentKey.mouse;
                        btn.buttonText = Config.CAMERA.inputName();

                        if(btn.buttonText == null)
                            btn.buttonText = "error";
                    }

                    @Override
                    public void returnPreviousScreen() {
                        mainView.returnToPreviousScreen();
                    }
                });
            }
        };
        cameraPanel.elements.add(new Panel.PanelElement(camera, buttonWidth));

        Panel moveSpeedPanel = controls.addPanel("moveSpeedPanel");
        moveSpeedPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("", "Move speed:", renderer), 0.525f));
        moveSpeed = new Textbox("", new Vector2f(), new Vector2f(), renderer, loader, window);
        moveSpeed.noLetters().noOthers();
        moveSpeed.setText(Float.toString(Config.CAMERA_MOVE_SPEED));
        moveSpeedPanel.elements.add(new Panel.PanelElement(moveSpeed, 0.475f));

        Panel sensitivityPanel = controls.addPanel("sensitivityPanel");
        sensitivityPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("", "Sensitivity:", renderer), 0.525f));
        sensitivity = new Textbox("", new Vector2f(), new Vector2f(), renderer, loader, window);
        sensitivity.noLetters().noOthers();
        sensitivity.setText(Float.toString(Config.MOUSE_SENS));
        sensitivityPanel.elements.add(new Panel.PanelElement(sensitivity, 0.475f));

        performance = new DropDownTab("performance", "Performance", new Vector2f(10, 21 + 10 + 21 + rendererSettings.getFullHeight() + guiSettings.getFullHeight() + controls.getFullHeight()), new Vector2f(200, getFontHeight() + 4), renderer, loader, window).closed();
        disableBlur = performance.addCheckbox("disableBlur", "Disable blur", Config.DISABLE_BLUR);
        disableSSAO = performance.addCheckbox("disableSSAO", "Disable SSAO", Config.DISABLE_SSAO);

        performance.addString("tickRate", "Thread sleep time:(ms)");

        Panel secondaryThreadTicksPanel = performance.addPanel("secondaryThreadTicksPanel");
        secondaryThreadTicksPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("", "Secondary:", renderer), 0.7f));
        secondaryThreadTicks = new Textbox("", new Vector2f(), new Vector2f(), renderer, loader, window);
        secondaryThreadTicks.noLetters().noOthers().numberLimits(0, 5000);
        secondaryThreadTicks.setText(Integer.toString(Config.SECONDARY_THREAD));
        secondaryThreadTicksPanel.elements.add(new Panel.PanelElement(secondaryThreadTicks, 0.3f));

        Panel loaderThreadTicksPanel = performance.addPanel("loaderThreadTicksPanel");
        loaderThreadTicksPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("", "Loader:", renderer), 0.7f));
        loaderThreadTicks = new Textbox("", new Vector2f(), new Vector2f(), renderer, loader, window);
        loaderThreadTicks.noLetters().noOthers().numberLimits(0, 5000);
        loaderThreadTicks.setText(Integer.toString(Config.LOADER_THREAD));
        loaderThreadTicksPanel.elements.add(new Panel.PanelElement(loaderThreadTicks, 0.3f));

        Panel entryDigestThreadTicksPanel = performance.addPanel("entryDigestThreadTicksPanel");
        entryDigestThreadTicksPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("", "Entry digest:", renderer), 0.7f));
        entryDigestThreadTicks = new Textbox("", new Vector2f(), new Vector2f(), renderer, loader, window);
        entryDigestThreadTicks.noLetters().noOthers().numberLimits(0, 5000);
        entryDigestThreadTicks.setText(Integer.toString(Config.ENTRY_DIGEST_THREAD));
        entryDigestThreadTicksPanel.elements.add(new Panel.PanelElement(entryDigestThreadTicks, 0.3f));

        this.guiElements.add(rendererSettings);
        this.guiElements.add(guiSettings);
        this.guiElements.add(controls);
        this.guiElements.add(performance);
    }

    public void initDebug()
    {
        debug = new DropDownTab("debug", "Debug", new Vector2f(217, 21 + 10), new Vector2f(200, getFontHeight() + 4), renderer, loader, window).closed();
        debug.addCheckbox("vaoCount", "VAO Count");
        debug.addCheckbox("vboCount", "VBO Count");
        debug.addCheckbox("textureCount", "Texture Count");
        debug.addCheckbox("threads", "Threads");
        debugScissorTest = debug.addCheckbox("debugScissorTest", "Scissor Test");
        debugOverlayImage = debug.addCheckbox("debugOverlayImage", "Overlay Image");
        debug.addButton("Pick Image", new Button() {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(action == GLFW.GLFW_PRESS && button == GLFW.GLFW_MOUSE_BUTTON_1)
                    try{
                        int newImage = loader.loadTextureFilePicker();

                        if(overlayImage != null)
                        {
                            GL11.glDeleteTextures(overlayImage.id);
                            overlayImage.id = newImage;
                            overlayImage.loader = loader;
                        }
                        else
                            overlayImage = new Texture(newImage, loader);
                    }catch (Exception e){}
            }
        });

        this.guiElements.add(debug);
    }

    public Texture overlayImage;

    @Override
    public void secondaryThread() {
        super.secondaryThread();

        Config.NO_CULLING = culling.isChecked;
        Config.SHOW_FPS = showFPS.isChecked;
        Config.LEGACY_FD = legacyFD.isChecked;
        Config.DISABLE_BLUR = disableBlur.isChecked;
        Config.DISABLE_SSAO = disableSSAO.isChecked;

        Vector2f fovSlider = fov.setSliderValue((float) Math.toDegrees(Config.FOV));
        if(fovSlider.y == 1)
        {
            Config.FOV = (float) Math.toRadians(fovSlider.x);
        }

        Vector2i fps = this.fps.setTextboxValueInt(Config.FRAMERATE);
        if(fps.y == 1)
            Config.FRAMERATE = fps.x;

        Vector2f moveSpeed = this.moveSpeed.setTextboxValueFloat(Config.CAMERA_MOVE_SPEED);
        if(moveSpeed.y == 1)
            Config.CAMERA_MOVE_SPEED = moveSpeed.x;

        Vector2f sensitivity = this.sensitivity.setTextboxValueFloat(Config.MOUSE_SENS);
        if(sensitivity.y == 1)
            Config.MOUSE_SENS = sensitivity.x;

        Vector2f zNear = this.zNear.setTextboxValueFloat(Config.Z_NEAR);
        if(zNear.y == 1)
            Config.Z_NEAR = zNear.x;

        Vector2f zFar = this.zFar.setTextboxValueFloat(Config.Z_FAR);
        if(zFar.y == 1)
            Config.Z_FAR = zFar.x;

        Vector2i msaa = this.msaa.setTextboxValueInt(Config.MSAA_SAMPLES);
        if(msaa.y == 1)
            Config.MSAA_SAMPLES = msaa.x;

        Vector2f outlineDistSlider = outlineSize.setSliderValue(Config.OUTLINE_DISTANCE);
        if(outlineDistSlider.y == 1)
            Config.OUTLINE_DISTANCE = outlineDistSlider.x;

//        Vector2f guiSize = guiSizeSlider.setSliderValue(Config.GUI_SCALE);TODO
//        if(guiSize.y == 1)
//        {
//            Config.GUI_SCALE = Math.round(guiSize.x);
//            window.resize = true;
//        }

        float cursorSize = Utils.parseFloat(this.cursorSize.getText());
        if(cursorSize == 0)
        {
            cursorSize = 1;

            if(!this.cursorSize.isFocused())
                this.cursorSize.setText("1");
        }

        if(Config.CURSOR_SCALE != cursorSize)
        {
            Config.CURSOR_SCALE = cursorSize;
            Cursors.updateCursors();
        }

        Vector2i secondaryThreadTicks = this.secondaryThreadTicks.setTextboxValueInt(Config.SECONDARY_THREAD);
        if(secondaryThreadTicks.y == 1)
            Config.SECONDARY_THREAD = secondaryThreadTicks.x;
        Vector2i loaderThreadTicks = this.loaderThreadTicks.setTextboxValueInt(Config.LOADER_THREAD);
        if(loaderThreadTicks.y == 1)
            Config.LOADER_THREAD = loaderThreadTicks.x;
        Vector2i entryDigestThreadTicks = this.entryDigestThreadTicks.setTextboxValueInt(Config.ENTRY_DIGEST_THREAD);
        if(entryDigestThreadTicks.y == 1)
            Config.ENTRY_DIGEST_THREAD = entryDigestThreadTicks.x;
    }
}