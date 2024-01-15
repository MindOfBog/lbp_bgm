package bog.bgmaker.view3d.mainWindow.screens;

import bog.bgmaker.Main;
import bog.bgmaker.view3d.mainWindow.View3D;
import bog.bgmaker.view3d.managers.InputMan;
import bog.bgmaker.view3d.managers.MouseInput;
import bog.bgmaker.view3d.renderer.gui.GuiKeybind;
import bog.bgmaker.view3d.renderer.gui.GuiScreen;
import bog.bgmaker.view3d.renderer.gui.cursor.Cursor;
import bog.bgmaker.view3d.renderer.gui.elements.*;
import bog.bgmaker.view3d.renderer.gui.elements.Button;
import bog.bgmaker.view3d.renderer.gui.elements.Checkbox;
import bog.bgmaker.view3d.renderer.gui.elements.Panel;
import bog.bgmaker.view3d.renderer.gui.font.FNT;
import bog.bgmaker.view3d.renderer.gui.font.FontRenderer;
import bog.bgmaker.view3d.utils.Config;
import bog.bgmaker.view3d.utils.Cursors;
import bog.bgmaker.view3d.utils.Utils;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

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
    public DropDownTab debug;

    public Textbox fps;
    public Textbox moveSpeed;
    public Textbox sensitivity;
    public Textbox zNear;
    public Textbox zFar;
    public Textbox outlineColorR;
    public Textbox outlineColorG;
    public Textbox outlineColorB;
    public Textbox outlineColorA;
    public Textbox borderColor1R;
    public Textbox borderColor1G;
    public Textbox borderColor1B;
    public Textbox borderColor1A;
    public Textbox borderColor2R;
    public Textbox borderColor2G;
    public Textbox borderColor2B;
    public Textbox borderColor2A;
    public Textbox borderColor3R;
    public Textbox borderColor3G;
    public Textbox borderColor3B;
    public Textbox borderColor3A;
    public Textbox borderColor4R;
    public Textbox borderColor4G;
    public Textbox borderColor4B;
    public Textbox borderColor4A;
    public Textbox earthColorR;
    public Textbox earthColorG;
    public Textbox earthColorB;
    public Textbox earthColorA;
    public Textbox podColorR;
    public Textbox podColorG;
    public Textbox podColorB;
    public Textbox podColorA;
    public Textbox fontColorR;
    public Textbox fontColorG;
    public Textbox fontColorB;
    public Textbox fontColorA;
    public Textbox primaryColorR;
    public Textbox primaryColorG;
    public Textbox primaryColorB;
    public Textbox primaryColorA;
    public Textbox secondaryColorR;
    public Textbox secondaryColorG;
    public Textbox secondaryColorB;
    public Textbox secondaryColorA;
    public Textbox interfacePrimaryColorR;
    public Textbox interfacePrimaryColorG;
    public Textbox interfacePrimaryColorB;
    public Textbox interfacePrimaryColorA;
    public Textbox interfacePrimaryColor2R;
    public Textbox interfacePrimaryColor2G;
    public Textbox interfacePrimaryColor2B;
    public Textbox interfacePrimaryColor2A;
    public Textbox interfaceSecondaryColorR;
    public Textbox interfaceSecondaryColorG;
    public Textbox interfaceSecondaryColorB;
    public Textbox interfaceSecondaryColorA;
    public Textbox interfaceSecondaryColor2R;
    public Textbox interfaceSecondaryColor2G;
    public Textbox interfaceSecondaryColor2B;
    public Textbox interfaceSecondaryColor2A;
    public Textbox interfaceTertiaryColorR;
    public Textbox interfaceTertiaryColorG;
    public Textbox interfaceTertiaryColorB;
    public Textbox interfaceTertiaryColorA;
    public Textbox interfaceTertiaryColor2R;
    public Textbox interfaceTertiaryColor2G;
    public Textbox interfaceTertiaryColor2B;
    public Textbox interfaceTertiaryColor2A;

    Textbox cursorSize;
    public Slider fov;
    public Checkbox culling;
    public Slider outlineSize;

    public void init() {
        rendererSettings = new DropDownTab("rendererSettings", "Renderer Settings", new Vector2f(7, 39), new Vector2f(200, getFontHeight(10) + 4), 10, mainView.renderer, mainView.loader, mainView.window).closed();
        culling = rendererSettings.addCheckbox("culling", "No culling", Config.NO_CULLING);

        Panel fovPanel = rendererSettings.addPanel("fovPanel");
        fovPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("", "FOV:", 10, mainView.renderer), 0.525f));
        fov = new Slider("", new Vector2f(), new Vector2f(), mainView.renderer, mainView.loader, mainView.window, (float) Math.toDegrees(Config.FOV), 20, 175);
        fovPanel.elements.add(new Panel.PanelElement(fov, 0.475f));

        Panel fpsPanel = rendererSettings.addPanel("fpsPanel");
        fpsPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("", "FPS:", 10, mainView.renderer), 0.525f));
        fps = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
        fps.noLetters().noOthers();
        fps.setText(Float.toString(Config.FRAMERATE));
        fpsPanel.elements.add(new Panel.PanelElement(fps, 0.475f));

        Panel moveSpeedPanel = rendererSettings.addPanel("moveSpeedPanel");
        moveSpeedPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("", "Move speed:", 10, mainView.renderer), 0.525f));
        moveSpeed = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
        moveSpeed.noLetters().noOthers();
        moveSpeed.setText(Float.toString(Config.CAMERA_MOVE_SPEED));
        moveSpeedPanel.elements.add(new Panel.PanelElement(moveSpeed, 0.475f));

        Panel sensitivityPanel = rendererSettings.addPanel("sensitivityPanel");
        sensitivityPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("", "Sensitivity:", 10, mainView.renderer), 0.525f));
        sensitivity = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
        sensitivity.noLetters().noOthers();
        sensitivity.setText(Float.toString(Config.MOUSE_SENS));
        sensitivityPanel.elements.add(new Panel.PanelElement(sensitivity, 0.475f));

        Panel zNearPanel = rendererSettings.addPanel("zNearPanel");
        zNearPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("", "Z Near:", 10, mainView.renderer), 0.525f));
        zNear = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
        zNear.noLetters().noOthers();
        zNear.setText(Float.toString(Config.Z_NEAR));
        zNearPanel.elements.add(new Panel.PanelElement(zNear, 0.475f));

        Panel zFarPanel = rendererSettings.addPanel("zFarPanel");
        zFarPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("", "Z Far:", 10, mainView.renderer), 0.525f));
        zFar = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
        zFar.noLetters().noOthers();
        zFar.setText(Float.toString(Config.Z_FAR));
        zFarPanel.elements.add(new Panel.PanelElement(zFar, 0.475f));

        rendererSettings.addString("outlineSizeLabel", "Outline Size:");
        outlineSize = rendererSettings.addSlider("outlineSize", Config.OUTLINE_DISTANCE, 0.525f, 1.1f);

        float spacing = 0.0175f;
        float textboxsize = (1f - (spacing * 3f)) / 4f;

        rendererSettings.addString("outlineColorLabel", "Outline Color:");
        {
            Panel panel = rendererSettings.addPanel("outlineColorPanel");
            outlineColorR = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            outlineColorR.noLetters().noOthers().numberLimits(0, 255);
            outlineColorR.setText(Integer.toString(Config.OUTLINE_COLOR.getRed()));
            panel.elements.add(new Panel.PanelElement(outlineColorR, textboxsize));

            panel.elements.add(new Panel.PanelElement(null, spacing));
            outlineColorG = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            outlineColorG.noLetters().noOthers().numberLimits(0, 255);
            outlineColorG.setText(Integer.toString(Config.OUTLINE_COLOR.getGreen()));
            panel.elements.add(new Panel.PanelElement(outlineColorG, textboxsize));

            panel.elements.add(new Panel.PanelElement(null, spacing));
            outlineColorB = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            outlineColorB.noLetters().noOthers().numberLimits(0, 255);
            outlineColorB.setText(Integer.toString(Config.OUTLINE_COLOR.getBlue()));
            panel.elements.add(new Panel.PanelElement(outlineColorB, textboxsize));

            panel.elements.add(new Panel.PanelElement(null, spacing));
            outlineColorA = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            outlineColorA.noLetters().noOthers().numberLimits(0, 255);
            outlineColorA.setText(Integer.toString(Config.OUTLINE_COLOR.getAlpha()));
            panel.elements.add(new Panel.PanelElement(outlineColorA, textboxsize));
        }

        rendererSettings.addString("borderColor1Label", "Border Color 1:");
        {
            Panel panel = rendererSettings.addPanel("borderColor1Panel");
            borderColor1R = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            borderColor1R.noLetters().noOthers().numberLimits(0, 255);
            borderColor1R.setText(Integer.toString(Config.BORDER_COLOR_1.getRed()));
            panel.elements.add(new Panel.PanelElement(borderColor1R, textboxsize));

            panel.elements.add(new Panel.PanelElement(null, spacing));
            borderColor1G = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            borderColor1G.noLetters().noOthers().numberLimits(0, 255);
            borderColor1G.setText(Integer.toString(Config.BORDER_COLOR_1.getGreen()));
            panel.elements.add(new Panel.PanelElement(borderColor1G, textboxsize));

            panel.elements.add(new Panel.PanelElement(null, spacing));
            borderColor1B = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            borderColor1B.noLetters().noOthers().numberLimits(0, 255);
            borderColor1B.setText(Integer.toString(Config.BORDER_COLOR_1.getBlue()));
            panel.elements.add(new Panel.PanelElement(borderColor1B, textboxsize));

            panel.elements.add(new Panel.PanelElement(null, spacing));
            borderColor1A = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            borderColor1A.noLetters().noOthers().numberLimits(0, 255);
            borderColor1A.setText(Integer.toString(Config.BORDER_COLOR_1.getAlpha()));
            panel.elements.add(new Panel.PanelElement(borderColor1A, textboxsize));
        }

        rendererSettings.addString("borderColor2Label", "Border Color 2:");
        {
            Panel panel = rendererSettings.addPanel("borderColor2Panel");
            borderColor2R = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            borderColor2R.noLetters().noOthers().numberLimits(0, 255);
            borderColor2R.setText(Integer.toString(Config.BORDER_COLOR_2.getRed()));
            panel.elements.add(new Panel.PanelElement(borderColor2R, textboxsize));

            panel.elements.add(new Panel.PanelElement(null, spacing));
            borderColor2G = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            borderColor2G.noLetters().noOthers().numberLimits(0, 255);
            borderColor2G.setText(Integer.toString(Config.BORDER_COLOR_2.getGreen()));
            panel.elements.add(new Panel.PanelElement(borderColor2G, textboxsize));

            panel.elements.add(new Panel.PanelElement(null, spacing));
            borderColor2B = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            borderColor2B.noLetters().noOthers().numberLimits(0, 255);
            borderColor2B.setText(Integer.toString(Config.BORDER_COLOR_2.getBlue()));
            panel.elements.add(new Panel.PanelElement(borderColor2B, textboxsize));

            panel.elements.add(new Panel.PanelElement(null, spacing));
            borderColor2A = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            borderColor2A.noLetters().noOthers().numberLimits(0, 255);
            borderColor2A.setText(Integer.toString(Config.BORDER_COLOR_2.getAlpha()));
            panel.elements.add(new Panel.PanelElement(borderColor2A, textboxsize));
        }

        rendererSettings.addString("borderColor3Label", "Border Color 3:");
        {
            Panel panel = rendererSettings.addPanel("borderColor3Panel");
            borderColor3R = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            borderColor3R.noLetters().noOthers().numberLimits(0, 255);
            borderColor3R.setText(Integer.toString(Config.BORDER_COLOR_3.getRed()));
            panel.elements.add(new Panel.PanelElement(borderColor3R, textboxsize));

            panel.elements.add(new Panel.PanelElement(null, spacing));
            borderColor3G = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            borderColor3G.noLetters().noOthers().numberLimits(0, 255);
            borderColor3G.setText(Integer.toString(Config.BORDER_COLOR_3.getGreen()));
            panel.elements.add(new Panel.PanelElement(borderColor3G, textboxsize));

            panel.elements.add(new Panel.PanelElement(null, spacing));
            borderColor3B = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            borderColor3B.noLetters().noOthers().numberLimits(0, 255);
            borderColor3B.setText(Integer.toString(Config.BORDER_COLOR_3.getBlue()));
            panel.elements.add(new Panel.PanelElement(borderColor3B, textboxsize));

            panel.elements.add(new Panel.PanelElement(null, spacing));
            borderColor3A = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            borderColor3A.noLetters().noOthers().numberLimits(0, 255);
            borderColor3A.setText(Integer.toString(Config.BORDER_COLOR_3.getAlpha()));
            panel.elements.add(new Panel.PanelElement(borderColor3A, textboxsize));
        }

        rendererSettings.addString("borderColor4Label", "Border Color 4:");
        {
            Panel panel = rendererSettings.addPanel("borderColor4Panel");
            borderColor4R = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            borderColor4R.noLetters().noOthers().numberLimits(0, 255);
            borderColor4R.setText(Integer.toString(Config.BORDER_COLOR_4.getRed()));
            panel.elements.add(new Panel.PanelElement(borderColor4R, textboxsize));

            panel.elements.add(new Panel.PanelElement(null, spacing));
            borderColor4G = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            borderColor4G.noLetters().noOthers().numberLimits(0, 255);
            borderColor4G.setText(Integer.toString(Config.BORDER_COLOR_4.getGreen()));
            panel.elements.add(new Panel.PanelElement(borderColor4G, textboxsize));

            panel.elements.add(new Panel.PanelElement(null, spacing));
            borderColor4B = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            borderColor4B.noLetters().noOthers().numberLimits(0, 255);
            borderColor4B.setText(Integer.toString(Config.BORDER_COLOR_4.getBlue()));
            panel.elements.add(new Panel.PanelElement(borderColor4B, textboxsize));

            panel.elements.add(new Panel.PanelElement(null, spacing));
            borderColor4A = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            borderColor4A.noLetters().noOthers().numberLimits(0, 255);
            borderColor4A.setText(Integer.toString(Config.BORDER_COLOR_4.getAlpha()));
            panel.elements.add(new Panel.PanelElement(borderColor4A, textboxsize));
        }

        rendererSettings.addString("earthColorLabel", "Earth Color:");
        {
            Panel panel = rendererSettings.addPanel("earthColorPanel");
            earthColorR = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            earthColorR.noLetters().noOthers().numberLimits(0, 255);
            earthColorR.setText(Integer.toString(Config.EARTH_COLOR.getRed()));
            panel.elements.add(new Panel.PanelElement(earthColorR, textboxsize));

            panel.elements.add(new Panel.PanelElement(null, spacing));
            earthColorG = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            earthColorG.noLetters().noOthers().numberLimits(0, 255);
            earthColorG.setText(Integer.toString(Config.EARTH_COLOR.getGreen()));
            panel.elements.add(new Panel.PanelElement(earthColorG, textboxsize));

            panel.elements.add(new Panel.PanelElement(null, spacing));
            earthColorB = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            earthColorB.noLetters().noOthers().numberLimits(0, 255);
            earthColorB.setText(Integer.toString(Config.EARTH_COLOR.getBlue()));
            panel.elements.add(new Panel.PanelElement(earthColorB, textboxsize));

            panel.elements.add(new Panel.PanelElement(null, spacing));
            earthColorA = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            earthColorA.noLetters().noOthers().numberLimits(0, 255);
            earthColorA.setText(Integer.toString(Config.EARTH_COLOR.getAlpha()));
            panel.elements.add(new Panel.PanelElement(earthColorA, textboxsize));
        }

        rendererSettings.addString("podColorLabel", "Pod Color:");
        {
            Panel panel = rendererSettings.addPanel("podColorPanel");
            podColorR = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            podColorR.noLetters().noOthers().numberLimits(0, 255);
            podColorR.setText(Integer.toString(Config.POD_COLOR.getRed()));
            panel.elements.add(new Panel.PanelElement(podColorR, textboxsize));

            panel.elements.add(new Panel.PanelElement(null, spacing));
            podColorG = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            podColorG.noLetters().noOthers().numberLimits(0, 255);
            podColorG.setText(Integer.toString(Config.POD_COLOR.getGreen()));
            panel.elements.add(new Panel.PanelElement(podColorG, textboxsize));

            panel.elements.add(new Panel.PanelElement(null, spacing));
            podColorB = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            podColorB.noLetters().noOthers().numberLimits(0, 255);
            podColorB.setText(Integer.toString(Config.POD_COLOR.getBlue()));
            panel.elements.add(new Panel.PanelElement(podColorB, textboxsize));

            panel.elements.add(new Panel.PanelElement(null, spacing));
            podColorA = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            podColorA.noLetters().noOthers().numberLimits(0, 255);
            podColorA.setText(Integer.toString(Config.POD_COLOR.getAlpha()));
            panel.elements.add(new Panel.PanelElement(podColorA, textboxsize));
        }

        //------------------------------------

        guiSettings = new DropDownTab("guiSettings", "GUI Settings", new Vector2f(7, 39 + 7 + rendererSettings.getFullHeight()), new Vector2f(200, getFontHeight(10) + 4), 10, mainView.renderer, mainView.loader, mainView.window).closed();

        guiSettings.addString("fontColorLabel", "Font Color:");
        {
            Panel panel = guiSettings.addPanel("fontColorPanel");
            fontColorR = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            fontColorR.noLetters().noOthers().numberLimits(0, 255);
            fontColorR.setText(Integer.toString(Config.FONT_COLOR.getRed()));
            panel.elements.add(new Panel.PanelElement(fontColorR, textboxsize));

            panel.elements.add(new Panel.PanelElement(null, spacing));
            fontColorG = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            fontColorG.noLetters().noOthers().numberLimits(0, 255);
            fontColorG.setText(Integer.toString(Config.FONT_COLOR.getGreen()));
            panel.elements.add(new Panel.PanelElement(fontColorG, textboxsize));

            panel.elements.add(new Panel.PanelElement(null, spacing));
            fontColorB = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            fontColorB.noLetters().noOthers().numberLimits(0, 255);
            fontColorB.setText(Integer.toString(Config.FONT_COLOR.getBlue()));
            panel.elements.add(new Panel.PanelElement(fontColorB, textboxsize));

            panel.elements.add(new Panel.PanelElement(null, spacing));
            fontColorA = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            fontColorA.noLetters().noOthers().numberLimits(0, 255);
            fontColorA.setText(Integer.toString(Config.FONT_COLOR.getAlpha()));
            panel.elements.add(new Panel.PanelElement(fontColorA, textboxsize));
        }

        guiSettings.addString("primaryColorLabel", "Backdrop Color:");
        {
            Panel panel = guiSettings.addPanel("primaryColorPanel");
            primaryColorR = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            primaryColorR.noLetters().noOthers().numberLimits(0, 255);
            primaryColorR.setText(Integer.toString(Config.PRIMARY_COLOR.getRed()));
            panel.elements.add(new Panel.PanelElement(primaryColorR, textboxsize));

            panel.elements.add(new Panel.PanelElement(null, spacing));
            primaryColorG = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            primaryColorG.noLetters().noOthers().numberLimits(0, 255);
            primaryColorG.setText(Integer.toString(Config.PRIMARY_COLOR.getGreen()));
            panel.elements.add(new Panel.PanelElement(primaryColorG, textboxsize));

            panel.elements.add(new Panel.PanelElement(null, spacing));
            primaryColorB = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            primaryColorB.noLetters().noOthers().numberLimits(0, 255);
            primaryColorB.setText(Integer.toString(Config.PRIMARY_COLOR.getBlue()));
            panel.elements.add(new Panel.PanelElement(primaryColorB, textboxsize));

            panel.elements.add(new Panel.PanelElement(null, spacing));
            primaryColorA = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            primaryColorA.noLetters().noOthers().numberLimits(0, 255);
            primaryColorA.setText(Integer.toString(Config.PRIMARY_COLOR.getAlpha()));
            panel.elements.add(new Panel.PanelElement(primaryColorA, textboxsize));
        }
        {
            Panel panel = guiSettings.addPanel("secondaryColorPanel");
            secondaryColorR = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            secondaryColorR.noLetters().noOthers().numberLimits(0, 255);
            secondaryColorR.setText(Integer.toString(Config.SECONDARY_COLOR.getRed()));
            panel.elements.add(new Panel.PanelElement(secondaryColorR, textboxsize));

            panel.elements.add(new Panel.PanelElement(null, spacing));
            secondaryColorG = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            secondaryColorG.noLetters().noOthers().numberLimits(0, 255);
            secondaryColorG.setText(Integer.toString(Config.SECONDARY_COLOR.getGreen()));
            panel.elements.add(new Panel.PanelElement(secondaryColorG, textboxsize));

            panel.elements.add(new Panel.PanelElement(null, spacing));
            secondaryColorB = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            secondaryColorB.noLetters().noOthers().numberLimits(0, 255);
            secondaryColorB.setText(Integer.toString(Config.SECONDARY_COLOR.getBlue()));
            panel.elements.add(new Panel.PanelElement(secondaryColorB, textboxsize));

            panel.elements.add(new Panel.PanelElement(null, spacing));
            secondaryColorA = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            secondaryColorA.noLetters().noOthers().numberLimits(0, 255);
            secondaryColorA.setText(Integer.toString(Config.SECONDARY_COLOR.getAlpha()));
            panel.elements.add(new Panel.PanelElement(secondaryColorA, textboxsize));
        }

        guiSettings.addString("interfacePrimaryColorLabel", "Interface Prim. Color:");
        {
            Panel panel = guiSettings.addPanel("interfacePrimaryColorPanel");
            interfacePrimaryColorR = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            interfacePrimaryColorR.noLetters().noOthers().numberLimits(0, 255);
            interfacePrimaryColorR.setText(Integer.toString(Config.INTERFACE_PRIMARY_COLOR.getRed()));
            panel.elements.add(new Panel.PanelElement(interfacePrimaryColorR, textboxsize));

            panel.elements.add(new Panel.PanelElement(null, spacing));
            interfacePrimaryColorG = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            interfacePrimaryColorG.noLetters().noOthers().numberLimits(0, 255);
            interfacePrimaryColorG.setText(Integer.toString(Config.INTERFACE_PRIMARY_COLOR.getGreen()));
            panel.elements.add(new Panel.PanelElement(interfacePrimaryColorG, textboxsize));

            panel.elements.add(new Panel.PanelElement(null, spacing));
            interfacePrimaryColorB = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            interfacePrimaryColorB.noLetters().noOthers().numberLimits(0, 255);
            interfacePrimaryColorB.setText(Integer.toString(Config.INTERFACE_PRIMARY_COLOR.getBlue()));
            panel.elements.add(new Panel.PanelElement(interfacePrimaryColorB, textboxsize));

            panel.elements.add(new Panel.PanelElement(null, spacing));
            interfacePrimaryColorA = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            interfacePrimaryColorA.noLetters().noOthers().numberLimits(0, 255);
            interfacePrimaryColorA.setText(Integer.toString(Config.INTERFACE_PRIMARY_COLOR.getAlpha()));
            panel.elements.add(new Panel.PanelElement(interfacePrimaryColorA, textboxsize));
        }
        {
            Panel panel = guiSettings.addPanel("interfacePrimaryColor2Panel");
            interfacePrimaryColor2R = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            interfacePrimaryColor2R.noLetters().noOthers().numberLimits(0, 255);
            interfacePrimaryColor2R.setText(Integer.toString(Config.INTERFACE_PRIMARY_COLOR2.getRed()));
            panel.elements.add(new Panel.PanelElement(interfacePrimaryColor2R, textboxsize));

            panel.elements.add(new Panel.PanelElement(null, spacing));
            interfacePrimaryColor2G = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            interfacePrimaryColor2G.noLetters().noOthers().numberLimits(0, 255);
            interfacePrimaryColor2G.setText(Integer.toString(Config.INTERFACE_PRIMARY_COLOR2.getGreen()));
            panel.elements.add(new Panel.PanelElement(interfacePrimaryColor2G, textboxsize));

            panel.elements.add(new Panel.PanelElement(null, spacing));
            interfacePrimaryColor2B = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            interfacePrimaryColor2B.noLetters().noOthers().numberLimits(0, 255);
            interfacePrimaryColor2B.setText(Integer.toString(Config.INTERFACE_PRIMARY_COLOR2.getBlue()));
            panel.elements.add(new Panel.PanelElement(interfacePrimaryColor2B, textboxsize));

            panel.elements.add(new Panel.PanelElement(null, spacing));
            interfacePrimaryColor2A = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            interfacePrimaryColor2A.noLetters().noOthers().numberLimits(0, 255);
            interfacePrimaryColor2A.setText(Integer.toString(Config.INTERFACE_PRIMARY_COLOR2.getAlpha()));
            panel.elements.add(new Panel.PanelElement(interfacePrimaryColor2A, textboxsize));
        }

        guiSettings.addString("interfaceSecondaryColorLabel", "Interface Sec. Color:");
        {
            Panel panel = guiSettings.addPanel("interfaceSecondaryColorPanel");
            interfaceSecondaryColorR = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            interfaceSecondaryColorR.noLetters().noOthers().numberLimits(0, 255);
            interfaceSecondaryColorR.setText(Integer.toString(Config.INTERFACE_SECONDARY_COLOR.getRed()));
            panel.elements.add(new Panel.PanelElement(interfaceSecondaryColorR, textboxsize));

            panel.elements.add(new Panel.PanelElement(null, spacing));
            interfaceSecondaryColorG = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            interfaceSecondaryColorG.noLetters().noOthers().numberLimits(0, 255);
            interfaceSecondaryColorG.setText(Integer.toString(Config.INTERFACE_SECONDARY_COLOR.getGreen()));
            panel.elements.add(new Panel.PanelElement(interfaceSecondaryColorG, textboxsize));

            panel.elements.add(new Panel.PanelElement(null, spacing));
            interfaceSecondaryColorB = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            interfaceSecondaryColorB.noLetters().noOthers().numberLimits(0, 255);
            interfaceSecondaryColorB.setText(Integer.toString(Config.INTERFACE_SECONDARY_COLOR.getBlue()));
            panel.elements.add(new Panel.PanelElement(interfaceSecondaryColorB, textboxsize));

            panel.elements.add(new Panel.PanelElement(null, spacing));
            interfaceSecondaryColorA = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            interfaceSecondaryColorA.noLetters().noOthers().numberLimits(0, 255);
            interfaceSecondaryColorA.setText(Integer.toString(Config.INTERFACE_SECONDARY_COLOR.getAlpha()));
            panel.elements.add(new Panel.PanelElement(interfaceSecondaryColorA, textboxsize));
        }
        {
            Panel panel = guiSettings.addPanel("interfaceSecondaryColor2Panel");
            interfaceSecondaryColor2R = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            interfaceSecondaryColor2R.noLetters().noOthers().numberLimits(0, 255);
            interfaceSecondaryColor2R.setText(Integer.toString(Config.INTERFACE_SECONDARY_COLOR2.getRed()));
            panel.elements.add(new Panel.PanelElement(interfaceSecondaryColor2R, textboxsize));

            panel.elements.add(new Panel.PanelElement(null, spacing));
            interfaceSecondaryColor2G = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            interfaceSecondaryColor2G.noLetters().noOthers().numberLimits(0, 255);
            interfaceSecondaryColor2G.setText(Integer.toString(Config.INTERFACE_SECONDARY_COLOR2.getGreen()));
            panel.elements.add(new Panel.PanelElement(interfaceSecondaryColor2G, textboxsize));

            panel.elements.add(new Panel.PanelElement(null, spacing));
            interfaceSecondaryColor2B = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            interfaceSecondaryColor2B.noLetters().noOthers().numberLimits(0, 255);
            interfaceSecondaryColor2B.setText(Integer.toString(Config.INTERFACE_SECONDARY_COLOR2.getBlue()));
            panel.elements.add(new Panel.PanelElement(interfaceSecondaryColor2B, textboxsize));

            panel.elements.add(new Panel.PanelElement(null, spacing));
            interfaceSecondaryColor2A = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            interfaceSecondaryColor2A.noLetters().noOthers().numberLimits(0, 255);
            interfaceSecondaryColor2A.setText(Integer.toString(Config.INTERFACE_SECONDARY_COLOR2.getAlpha()));
            panel.elements.add(new Panel.PanelElement(interfaceSecondaryColor2A, textboxsize));
        }

        guiSettings.addString("interfaceTertiaryColorLabel", "Interface Tert. Color:");
        {
            Panel panel = guiSettings.addPanel("interfaceTertiaryColorPanel");
            interfaceTertiaryColorR = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            interfaceTertiaryColorR.noLetters().noOthers().numberLimits(0, 255);
            interfaceTertiaryColorR.setText(Integer.toString(Config.INTERFACE_TERTIARY_COLOR.getRed()));
            panel.elements.add(new Panel.PanelElement(interfaceTertiaryColorR, textboxsize));

            panel.elements.add(new Panel.PanelElement(null, spacing));
            interfaceTertiaryColorG = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            interfaceTertiaryColorG.noLetters().noOthers().numberLimits(0, 255);
            interfaceTertiaryColorG.setText(Integer.toString(Config.INTERFACE_TERTIARY_COLOR.getGreen()));
            panel.elements.add(new Panel.PanelElement(interfaceTertiaryColorG, textboxsize));

            panel.elements.add(new Panel.PanelElement(null, spacing));
            interfaceTertiaryColorB = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            interfaceTertiaryColorB.noLetters().noOthers().numberLimits(0, 255);
            interfaceTertiaryColorB.setText(Integer.toString(Config.INTERFACE_TERTIARY_COLOR.getBlue()));
            panel.elements.add(new Panel.PanelElement(interfaceTertiaryColorB, textboxsize));

            panel.elements.add(new Panel.PanelElement(null, spacing));
            interfaceTertiaryColorA = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            interfaceTertiaryColorA.noLetters().noOthers().numberLimits(0, 255);
            interfaceTertiaryColorA.setText(Integer.toString(Config.INTERFACE_TERTIARY_COLOR.getAlpha()));
            panel.elements.add(new Panel.PanelElement(interfaceTertiaryColorA, textboxsize));
        }
        {
            Panel panel = guiSettings.addPanel("interfaceTertiaryColor2Panel");
            interfaceTertiaryColor2R = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            interfaceTertiaryColor2R.noLetters().noOthers().numberLimits(0, 255);
            interfaceTertiaryColor2R.setText(Integer.toString(Config.INTERFACE_TERTIARY_COLOR2.getRed()));
            panel.elements.add(new Panel.PanelElement(interfaceTertiaryColor2R, textboxsize));

            panel.elements.add(new Panel.PanelElement(null, spacing));
            interfaceTertiaryColor2G = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            interfaceTertiaryColor2G.noLetters().noOthers().numberLimits(0, 255);
            interfaceTertiaryColor2G.setText(Integer.toString(Config.INTERFACE_TERTIARY_COLOR2.getGreen()));
            panel.elements.add(new Panel.PanelElement(interfaceTertiaryColor2G, textboxsize));

            panel.elements.add(new Panel.PanelElement(null, spacing));
            interfaceTertiaryColor2B = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            interfaceTertiaryColor2B.noLetters().noOthers().numberLimits(0, 255);
            interfaceTertiaryColor2B.setText(Integer.toString(Config.INTERFACE_TERTIARY_COLOR2.getBlue()));
            panel.elements.add(new Panel.PanelElement(interfaceTertiaryColor2B, textboxsize));

            panel.elements.add(new Panel.PanelElement(null, spacing));
            interfaceTertiaryColor2A = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
            interfaceTertiaryColor2A.noLetters().noOthers().numberLimits(0, 255);
            interfaceTertiaryColor2A.setText(Integer.toString(Config.INTERFACE_TERTIARY_COLOR2.getAlpha()));
            panel.elements.add(new Panel.PanelElement(interfaceTertiaryColor2A, textboxsize));
        }

        Panel headerPanel = guiSettings.addPanel("headerPanel");
        headerPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("", "Header:", 10, mainView.renderer), 0.35f));

        ComboBox headerCombo = new ComboBox("", Config.FONT_HEADER, new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
        ButtonList headerList = new ButtonList("", FontRenderer.Fonts, new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window) {
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
        };
        headerCombo.addList("headerList", headerList, 80);

        headerPanel.elements.add(new Panel.PanelElement(headerCombo, 0.65f));

        Panel textPanel = guiSettings.addPanel("textPanel");
        textPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("", "Text:", 10, mainView.renderer), 0.35f));

        ComboBox textCombo = new ComboBox("", Config.FONT_TEXT, new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
        ButtonList textList = new ButtonList("", FontRenderer.Fonts, new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window) {
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
        };
        textCombo.addList("textList", textList, 80);

        textPanel.elements.add(new Panel.PanelElement(textCombo, 0.65f));

        Panel cursorPanel = guiSettings.addPanel("cursorPanel");
        cursorPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("", "Cursor:", 10, mainView.renderer), 0.35f));

        ComboBox cursorCombo = new ComboBox("", Config.CURSOR, new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
        ButtonList cursorList = new ButtonList("", Cursors.loadedCursors, new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window) {
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
        };
        cursorCombo.addList("cursorList", cursorList, 80);

        cursorPanel.elements.add(new Panel.PanelElement(cursorCombo, 0.65f));

        Panel cursorSizePanel = guiSettings.addPanel("cursorSizePanel");
        cursorSizePanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("", "Size:", 10, mainView.renderer), 0.35f));
        cursorSize = new Textbox("", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window);
        cursorSize.setText("" + Config.CURSOR_SCALE);
        cursorSizePanel.elements.add(new Panel.PanelElement(cursorSize, 0.65f));

        controls = new DropDownTab("controls", "Controls", new Vector2f(7, 39 + 14 + rendererSettings.getFullHeight() + guiSettings.getFullHeight()), new Vector2f(200, getFontHeight(10) + 4), 10, mainView.renderer, mainView.loader, mainView.window).closed();

        float buttonWidth = 0.62f;

        Panel forwardPanel = controls.addPanel("forwardPanel");
        forwardPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("", "Forward:", 10, mainView.renderer), 1f - buttonWidth));
        Button forward = new Button("", Config.FORWARD.inputName(), new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public void clickedButton(int button, int action, int mods) {

                if(!isClicked)
                    return;

                Button btn = this;
                mainView.setCurrentScreen(new GuiKeybind(Config.FORWARD, 10, renderer, loader, mainView.window) {
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
        backPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("", "Back:", 10, mainView.renderer), 1f - buttonWidth));
        Button back = new Button("", Config.BACK.inputName(), new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(!isClicked)
                    return;
                Button btn = this;
                mainView.setCurrentScreen(new GuiKeybind(Config.BACK, 10, renderer, loader, mainView.window) {
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
        leftPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("", "Left:", 10, mainView.renderer), 1f - buttonWidth));
        Button left = new Button("", Config.LEFT.inputName(), new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(!isClicked)
                    return;
                Button btn = this;
                mainView.setCurrentScreen(new GuiKeybind(Config.LEFT, 10, renderer, loader, mainView.window) {
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
        rightPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("", "Right:", 10, mainView.renderer), 1f - buttonWidth));
        Button right = new Button("", Config.RIGHT.inputName(), new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(!isClicked)
                    return;
                Button btn = this;
                mainView.setCurrentScreen(new GuiKeybind(Config.RIGHT, 10, renderer, loader, mainView.window) {
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
        upPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("", "Up:", 10, mainView.renderer), 1f - buttonWidth));
        Button up = new Button("", Config.UP.inputName(), new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(!isClicked)
                    return;
                Button btn = this;
                mainView.setCurrentScreen(new GuiKeybind(Config.UP, 10, renderer, loader, mainView.window) {
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
        downPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("", "Down:", 10, mainView.renderer), 1f - buttonWidth));
        Button down = new Button("", Config.DOWN.inputName(), new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(!isClicked)
                    return;
                Button btn = this;
                mainView.setCurrentScreen(new GuiKeybind(Config.DOWN, 10, renderer, loader, mainView.window) {
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
        shadingPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("", "Shading:", 10, mainView.renderer), 1f - buttonWidth));
        Button shading = new Button("", Config.SHADING.inputName(), new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(!isClicked)
                    return;
                Button btn = this;
                mainView.setCurrentScreen(new GuiKeybind(Config.SHADING, 10, renderer, loader, mainView.window) {
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
        cameraPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("", "Camera:", 10, mainView.renderer), 1f - buttonWidth));
        Button camera = new Button("", Config.CAMERA.inputName(), new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(!isClicked)
                    return;
                Button btn = this;
                mainView.setCurrentScreen(new GuiKeybind(Config.CAMERA, 10, renderer, loader, mainView.window) {
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

        this.guiElements.add(rendererSettings);
        this.guiElements.add(guiSettings);
        this.guiElements.add(controls);

        if (Main.debug) {
            debug = new DropDownTab("debug", "Debug", new Vector2f(214, 39), new Vector2f(200, getFontHeight(10) + 4), 10, mainView.renderer, mainView.loader, mainView.window).closed();
            debug.addCheckbox("aabb", "AABB");
            debug.addCheckbox("glScissorTest", "GL Scissor");
            debug.addCheckbox("vaoCount", "VAO Count");
            debug.addCheckbox("vboCount", "VBO Count");
            debug.addCheckbox("textureCount", "Texture Count");
            this.guiElements.add(debug);
        }
    }

    @Override
    public void secondaryThread() {
        super.secondaryThread();

        Config.NO_CULLING = culling.isChecked;

        Vector2f fovSlider = setSliderValue(fov, (float) Math.toDegrees(Config.FOV));
        if(fovSlider.y == 1)
        {
            Config.FOV = (float) Math.toRadians(fovSlider.x);
        }

        String fps = setTextboxValueString(this.fps, Float.toString(Config.FRAMERATE));
        if(fps != null)
            try{
                Config.FRAMERATE = Float.parseFloat(fps);
            }catch (Exception e){}

        String moveSpeed = setTextboxValueString(this.moveSpeed, Float.toString(Config.CAMERA_MOVE_SPEED));
        if(moveSpeed != null)
            try{
                Config.CAMERA_MOVE_SPEED = Float.parseFloat(moveSpeed);
            }catch (Exception e){}

        String sensitivity = setTextboxValueString(this.sensitivity, Float.toString(Config.MOUSE_SENS));
        if(sensitivity != null)
            try{
                Config.MOUSE_SENS = Float.parseFloat(sensitivity);
            }catch (Exception e){}

        String zNear = setTextboxValueString(this.zNear, Float.toString(Config.Z_NEAR));
        if(zNear != null)
            try{
                Config.Z_NEAR = Float.parseFloat(zNear);
            }catch (Exception e){}

        String zFar = setTextboxValueString(this.zFar, Float.toString(Config.Z_FAR));
        if(zFar != null)
            try{
                Config.Z_FAR = Float.parseFloat(zFar);
            }catch (Exception e){}

        Vector2f outlineDistSlider = setSliderValue(outlineSize, Config.OUTLINE_DISTANCE);
        if(outlineDistSlider.y == 1)
            Config.OUTLINE_DISTANCE = outlineDistSlider.x;

        {
            String colorR = setTextboxValueString(this.outlineColorR, Integer.toString(Config.OUTLINE_COLOR.getRed()));
            String colorG = setTextboxValueString(this.outlineColorG, Integer.toString(Config.OUTLINE_COLOR.getGreen()));
            String colorB = setTextboxValueString(this.outlineColorB, Integer.toString(Config.OUTLINE_COLOR.getBlue()));
            String colorA = setTextboxValueString(this.outlineColorA, Integer.toString(Config.OUTLINE_COLOR.getAlpha()));
            int r = Config.OUTLINE_COLOR.getRed();
            int g = Config.OUTLINE_COLOR.getGreen();
            int b = Config.OUTLINE_COLOR.getBlue();
            int a = Config.OUTLINE_COLOR.getAlpha();
            if(colorR != null)
                r = Utils.parseInt(colorR);
            if(colorG != null)
                g = Utils.parseInt(colorG);
            if(colorB != null)
                b = Utils.parseInt(colorB);
            if(colorA != null)
                a = Utils.parseInt(colorA);
            Config.OUTLINE_COLOR = new Color(r, g, b, a);
        }
        {
            String colorR = setTextboxValueString(this.borderColor1R, Integer.toString(Config.BORDER_COLOR_1.getRed()));
            String colorG = setTextboxValueString(this.borderColor1G, Integer.toString(Config.BORDER_COLOR_1.getGreen()));
            String colorB = setTextboxValueString(this.borderColor1B, Integer.toString(Config.BORDER_COLOR_1.getBlue()));
            String colorA = setTextboxValueString(this.borderColor1A, Integer.toString(Config.BORDER_COLOR_1.getAlpha()));
            int r = Config.BORDER_COLOR_1.getRed();
            int g = Config.BORDER_COLOR_1.getGreen();
            int b = Config.BORDER_COLOR_1.getBlue();
            int a = Config.BORDER_COLOR_1.getAlpha();
            if(colorR != null)r = Utils.parseInt(colorR);if(colorG != null)g = Utils.parseInt(colorG);if(colorB != null)b = Utils.parseInt(colorB);if(colorA != null)a = Utils.parseInt(colorA);
            Config.BORDER_COLOR_1 = new Color(r, g, b, a);
            mainView.borders.material.setColor(Config.BORDER_COLOR_1);
        }
        {
            String colorR = setTextboxValueString(this.borderColor2R, Integer.toString(Config.BORDER_COLOR_2.getRed()));
            String colorG = setTextboxValueString(this.borderColor2G, Integer.toString(Config.BORDER_COLOR_2.getGreen()));
            String colorB = setTextboxValueString(this.borderColor2B, Integer.toString(Config.BORDER_COLOR_2.getBlue()));
            String colorA = setTextboxValueString(this.borderColor2A, Integer.toString(Config.BORDER_COLOR_2.getAlpha()));
            int r = Config.BORDER_COLOR_2.getRed();
            int g = Config.BORDER_COLOR_2.getGreen();
            int b = Config.BORDER_COLOR_2.getBlue();
            int a = Config.BORDER_COLOR_2.getAlpha();
            if(colorR != null)r = Utils.parseInt(colorR);if(colorG != null)g = Utils.parseInt(colorG);if(colorB != null)b = Utils.parseInt(colorB);if(colorA != null)a = Utils.parseInt(colorA);
            Config.BORDER_COLOR_2 = new Color(r, g, b, a);
            mainView.borders1.material.setColor(Config.BORDER_COLOR_2);
        }
        {
            String colorR = setTextboxValueString(this.borderColor3R, Integer.toString(Config.BORDER_COLOR_3.getRed()));
            String colorG = setTextboxValueString(this.borderColor3G, Integer.toString(Config.BORDER_COLOR_3.getGreen()));
            String colorB = setTextboxValueString(this.borderColor3B, Integer.toString(Config.BORDER_COLOR_3.getBlue()));
            String colorA = setTextboxValueString(this.borderColor3A, Integer.toString(Config.BORDER_COLOR_3.getAlpha()));
            int r = Config.BORDER_COLOR_3.getRed();
            int g = Config.BORDER_COLOR_3.getGreen();
            int b = Config.BORDER_COLOR_3.getBlue();
            int a = Config.BORDER_COLOR_3.getAlpha();
            if(colorR != null)r = Utils.parseInt(colorR);if(colorG != null)g = Utils.parseInt(colorG);if(colorB != null)b = Utils.parseInt(colorB);if(colorA != null)a = Utils.parseInt(colorA);
            Config.BORDER_COLOR_3 = new Color(r, g, b, a);
            mainView.borders2.material.setColor(Config.BORDER_COLOR_3);
        }
        {
            String colorR = setTextboxValueString(this.borderColor4R, Integer.toString(Config.BORDER_COLOR_4.getRed()));
            String colorG = setTextboxValueString(this.borderColor4G, Integer.toString(Config.BORDER_COLOR_4.getGreen()));
            String colorB = setTextboxValueString(this.borderColor4B, Integer.toString(Config.BORDER_COLOR_4.getBlue()));
            String colorA = setTextboxValueString(this.borderColor4A, Integer.toString(Config.BORDER_COLOR_4.getAlpha()));
            int r = Config.BORDER_COLOR_4.getRed();
            int g = Config.BORDER_COLOR_4.getGreen();
            int b = Config.BORDER_COLOR_4.getBlue();
            int a = Config.BORDER_COLOR_4.getAlpha();
            if(colorR != null)r = Utils.parseInt(colorR);if(colorG != null)g = Utils.parseInt(colorG);if(colorB != null)b = Utils.parseInt(colorB);if(colorA != null)a = Utils.parseInt(colorA);
            Config.BORDER_COLOR_4 = new Color(r, g, b, a);
            mainView.borders3.material.setColor(Config.BORDER_COLOR_4);
        }
        {
            String colorR = setTextboxValueString(this.earthColorR, Integer.toString(Config.EARTH_COLOR.getRed()));
            String colorG = setTextboxValueString(this.earthColorG, Integer.toString(Config.EARTH_COLOR.getGreen()));
            String colorB = setTextboxValueString(this.earthColorB, Integer.toString(Config.EARTH_COLOR.getBlue()));
            String colorA = setTextboxValueString(this.earthColorA, Integer.toString(Config.EARTH_COLOR.getAlpha()));
            int r = Config.EARTH_COLOR.getRed();
            int g = Config.EARTH_COLOR.getGreen();
            int b = Config.EARTH_COLOR.getBlue();
            int a = Config.EARTH_COLOR.getAlpha();
            if(colorR != null)r = Utils.parseInt(colorR);if(colorG != null)g = Utils.parseInt(colorG);if(colorB != null)b = Utils.parseInt(colorB);if(colorA != null)a = Utils.parseInt(colorA);
            Config.EARTH_COLOR = new Color(r, g, b, a);
            mainView.earth.material.setOverlayColor(Config.EARTH_COLOR);
        }
        {
            String colorR = setTextboxValueString(this.podColorR, Integer.toString(Config.POD_COLOR.getRed()));
            String colorG = setTextboxValueString(this.podColorG, Integer.toString(Config.POD_COLOR.getGreen()));
            String colorB = setTextboxValueString(this.podColorB, Integer.toString(Config.POD_COLOR.getBlue()));
            String colorA = setTextboxValueString(this.podColorA, Integer.toString(Config.POD_COLOR.getAlpha()));
            int r = Config.POD_COLOR.getRed();
            int g = Config.POD_COLOR.getGreen();
            int b = Config.POD_COLOR.getBlue();
            int a = Config.POD_COLOR.getAlpha();
            if(colorR != null)r = Utils.parseInt(colorR);if(colorG != null)g = Utils.parseInt(colorG);if(colorB != null)b = Utils.parseInt(colorB);if(colorA != null)a = Utils.parseInt(colorA);
            Config.POD_COLOR = new Color(r, g, b, a);
            mainView.pod.material.setOverlayColor(Config.POD_COLOR);
        }
        {
            String colorR = setTextboxValueString(this.fontColorR, Integer.toString(Config.FONT_COLOR.getRed()));
            String colorG = setTextboxValueString(this.fontColorG, Integer.toString(Config.FONT_COLOR.getGreen()));
            String colorB = setTextboxValueString(this.fontColorB, Integer.toString(Config.FONT_COLOR.getBlue()));
            String colorA = setTextboxValueString(this.fontColorA, Integer.toString(Config.FONT_COLOR.getAlpha()));
            int r = Config.FONT_COLOR.getRed();
            int g = Config.FONT_COLOR.getGreen();
            int b = Config.FONT_COLOR.getBlue();
            int a = Config.FONT_COLOR.getAlpha();
            if(colorR != null)r = Utils.parseInt(colorR);if(colorG != null)g = Utils.parseInt(colorG);if(colorB != null)b = Utils.parseInt(colorB);if(colorA != null)a = Utils.parseInt(colorA);
            Config.FONT_COLOR = new Color(r, g, b, a);
        }
        {
            String colorR = setTextboxValueString(this.primaryColorR, Integer.toString(Config.PRIMARY_COLOR.getRed()));
            String colorG = setTextboxValueString(this.primaryColorG, Integer.toString(Config.PRIMARY_COLOR.getGreen()));
            String colorB = setTextboxValueString(this.primaryColorB, Integer.toString(Config.PRIMARY_COLOR.getBlue()));
            String colorA = setTextboxValueString(this.primaryColorA, Integer.toString(Config.PRIMARY_COLOR.getAlpha()));
            int r = Config.PRIMARY_COLOR.getRed();
            int g = Config.PRIMARY_COLOR.getGreen();
            int b = Config.PRIMARY_COLOR.getBlue();
            int a = Config.PRIMARY_COLOR.getAlpha();
            if(colorR != null)r = Utils.parseInt(colorR);if(colorG != null)g = Utils.parseInt(colorG);if(colorB != null)b = Utils.parseInt(colorB);if(colorA != null)a = Utils.parseInt(colorA);
            Config.PRIMARY_COLOR = new Color(r, g, b, a);
        }
        {
            String colorR = setTextboxValueString(this.secondaryColorR, Integer.toString(Config.SECONDARY_COLOR.getRed()));
            String colorG = setTextboxValueString(this.secondaryColorG, Integer.toString(Config.SECONDARY_COLOR.getGreen()));
            String colorB = setTextboxValueString(this.secondaryColorB, Integer.toString(Config.SECONDARY_COLOR.getBlue()));
            String colorA = setTextboxValueString(this.secondaryColorA, Integer.toString(Config.SECONDARY_COLOR.getAlpha()));
            int r = Config.SECONDARY_COLOR.getRed();
            int g = Config.SECONDARY_COLOR.getGreen();
            int b = Config.SECONDARY_COLOR.getBlue();
            int a = Config.SECONDARY_COLOR.getAlpha();
            if(colorR != null)r = Utils.parseInt(colorR);if(colorG != null)g = Utils.parseInt(colorG);if(colorB != null)b = Utils.parseInt(colorB);if(colorA != null)a = Utils.parseInt(colorA);
            Config.SECONDARY_COLOR = new Color(r, g, b, a);
        }
        {
            String colorR = setTextboxValueString(this.interfacePrimaryColorR, Integer.toString(Config.INTERFACE_PRIMARY_COLOR.getRed()));
            String colorG = setTextboxValueString(this.interfacePrimaryColorG, Integer.toString(Config.INTERFACE_PRIMARY_COLOR.getGreen()));
            String colorB = setTextboxValueString(this.interfacePrimaryColorB, Integer.toString(Config.INTERFACE_PRIMARY_COLOR.getBlue()));
            String colorA = setTextboxValueString(this.interfacePrimaryColorA, Integer.toString(Config.INTERFACE_PRIMARY_COLOR.getAlpha()));
            int r = Config.INTERFACE_PRIMARY_COLOR.getRed();
            int g = Config.INTERFACE_PRIMARY_COLOR.getGreen();
            int b = Config.INTERFACE_PRIMARY_COLOR.getBlue();
            int a = Config.INTERFACE_PRIMARY_COLOR.getAlpha();
            if(colorR != null)r = Utils.parseInt(colorR);if(colorG != null)g = Utils.parseInt(colorG);if(colorB != null)b = Utils.parseInt(colorB);if(colorA != null)a = Utils.parseInt(colorA);
            Config.INTERFACE_PRIMARY_COLOR = new Color(r, g, b, a);
        }
        {
            String colorR = setTextboxValueString(this.interfacePrimaryColor2R, Integer.toString(Config.INTERFACE_PRIMARY_COLOR2.getRed()));
            String colorG = setTextboxValueString(this.interfacePrimaryColor2G, Integer.toString(Config.INTERFACE_PRIMARY_COLOR2.getGreen()));
            String colorB = setTextboxValueString(this.interfacePrimaryColor2B, Integer.toString(Config.INTERFACE_PRIMARY_COLOR2.getBlue()));
            String colorA = setTextboxValueString(this.interfacePrimaryColor2A, Integer.toString(Config.INTERFACE_PRIMARY_COLOR2.getAlpha()));
            int r = Config.INTERFACE_PRIMARY_COLOR2.getRed();
            int g = Config.INTERFACE_PRIMARY_COLOR2.getGreen();
            int b = Config.INTERFACE_PRIMARY_COLOR2.getBlue();
            int a = Config.INTERFACE_PRIMARY_COLOR2.getAlpha();
            if(colorR != null)r = Utils.parseInt(colorR);if(colorG != null)g = Utils.parseInt(colorG);if(colorB != null)b = Utils.parseInt(colorB);if(colorA != null)a = Utils.parseInt(colorA);
            Config.INTERFACE_PRIMARY_COLOR2 = new Color(r, g, b, a);
        }
        {
            String colorR = setTextboxValueString(this.interfaceSecondaryColorR, Integer.toString(Config.INTERFACE_SECONDARY_COLOR.getRed()));
            String colorG = setTextboxValueString(this.interfaceSecondaryColorG, Integer.toString(Config.INTERFACE_SECONDARY_COLOR.getGreen()));
            String colorB = setTextboxValueString(this.interfaceSecondaryColorB, Integer.toString(Config.INTERFACE_SECONDARY_COLOR.getBlue()));
            String colorA = setTextboxValueString(this.interfaceSecondaryColorA, Integer.toString(Config.INTERFACE_SECONDARY_COLOR.getAlpha()));
            int r = Config.INTERFACE_SECONDARY_COLOR.getRed();
            int g = Config.INTERFACE_SECONDARY_COLOR.getGreen();
            int b = Config.INTERFACE_SECONDARY_COLOR.getBlue();
            int a = Config.INTERFACE_SECONDARY_COLOR.getAlpha();
            if(colorR != null)r = Utils.parseInt(colorR);if(colorG != null)g = Utils.parseInt(colorG);if(colorB != null)b = Utils.parseInt(colorB);if(colorA != null)a = Utils.parseInt(colorA);
            Config.INTERFACE_SECONDARY_COLOR = new Color(r, g, b, a);
        }
        {
            String colorR = setTextboxValueString(this.interfaceSecondaryColor2R, Integer.toString(Config.INTERFACE_SECONDARY_COLOR2.getRed()));
            String colorG = setTextboxValueString(this.interfaceSecondaryColor2G, Integer.toString(Config.INTERFACE_SECONDARY_COLOR2.getGreen()));
            String colorB = setTextboxValueString(this.interfaceSecondaryColor2B, Integer.toString(Config.INTERFACE_SECONDARY_COLOR2.getBlue()));
            String colorA = setTextboxValueString(this.interfaceSecondaryColor2A, Integer.toString(Config.INTERFACE_SECONDARY_COLOR2.getAlpha()));
            int r = Config.INTERFACE_SECONDARY_COLOR2.getRed();
            int g = Config.INTERFACE_SECONDARY_COLOR2.getGreen();
            int b = Config.INTERFACE_SECONDARY_COLOR2.getBlue();
            int a = Config.INTERFACE_SECONDARY_COLOR2.getAlpha();
            if(colorR != null)r = Utils.parseInt(colorR);if(colorG != null)g = Utils.parseInt(colorG);if(colorB != null)b = Utils.parseInt(colorB);if(colorA != null)a = Utils.parseInt(colorA);
            Config.INTERFACE_SECONDARY_COLOR2 = new Color(r, g, b, a);
        }
        {
            String colorR = setTextboxValueString(this.interfaceTertiaryColorR, Integer.toString(Config.INTERFACE_TERTIARY_COLOR.getRed()));
            String colorG = setTextboxValueString(this.interfaceTertiaryColorG, Integer.toString(Config.INTERFACE_TERTIARY_COLOR.getGreen()));
            String colorB = setTextboxValueString(this.interfaceTertiaryColorB, Integer.toString(Config.INTERFACE_TERTIARY_COLOR.getBlue()));
            String colorA = setTextboxValueString(this.interfaceTertiaryColorA, Integer.toString(Config.INTERFACE_TERTIARY_COLOR.getAlpha()));
            int r = Config.INTERFACE_TERTIARY_COLOR.getRed();
            int g = Config.INTERFACE_TERTIARY_COLOR.getGreen();
            int b = Config.INTERFACE_TERTIARY_COLOR.getBlue();
            int a = Config.INTERFACE_TERTIARY_COLOR.getAlpha();
            if(colorR != null)r = Utils.parseInt(colorR);if(colorG != null)g = Utils.parseInt(colorG);if(colorB != null)b = Utils.parseInt(colorB);if(colorA != null)a = Utils.parseInt(colorA);
            Config.INTERFACE_TERTIARY_COLOR = new Color(r, g, b, a);
        }
        {
            String colorR = setTextboxValueString(this.interfaceTertiaryColor2R, Integer.toString(Config.INTERFACE_TERTIARY_COLOR2.getRed()));
            String colorG = setTextboxValueString(this.interfaceTertiaryColor2G, Integer.toString(Config.INTERFACE_TERTIARY_COLOR2.getGreen()));
            String colorB = setTextboxValueString(this.interfaceTertiaryColor2B, Integer.toString(Config.INTERFACE_TERTIARY_COLOR2.getBlue()));
            String colorA = setTextboxValueString(this.interfaceTertiaryColor2A, Integer.toString(Config.INTERFACE_TERTIARY_COLOR2.getAlpha()));
            int r = Config.INTERFACE_TERTIARY_COLOR2.getRed();
            int g = Config.INTERFACE_TERTIARY_COLOR2.getGreen();
            int b = Config.INTERFACE_TERTIARY_COLOR2.getBlue();
            int a = Config.INTERFACE_TERTIARY_COLOR2.getAlpha();
            if(colorR != null)
                r = Utils.parseInt(colorR);
            if(colorG != null)
                g = Utils.parseInt(colorG);
            if(colorB != null)
                b = Utils.parseInt(colorB);
            if(colorA != null)
                a = Utils.parseInt(colorA);
            Config.INTERFACE_TERTIARY_COLOR2 = new Color(r, g, b, a);
        }
        {
            float cursorSize = Utils.parseFloat(this.cursorSize.getText());

            if(cursorSize == 0)
            {
                cursorSize = 1;

                if(!this.cursorSize.isFocused())
                    this.cursorSize.setText("1");
            }

            Config.CURSOR_SCALE = cursorSize;
            Cursors.updateCursors();
        }
    }

}