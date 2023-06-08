package bog.bgmaker.view3d.mainWindow.screens;

import bog.bgmaker.Main;
import bog.bgmaker.view3d.mainWindow.View3D;
import bog.bgmaker.view3d.renderer.gui.GuiKeybind;
import bog.bgmaker.view3d.renderer.gui.GuiScreen;
import bog.bgmaker.view3d.renderer.gui.elements.*;
import bog.bgmaker.view3d.renderer.gui.elements.Button;
import bog.bgmaker.view3d.renderer.gui.elements.Checkbox;
import bog.bgmaker.view3d.utils.Config;
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

    public void init()
    {
        rendererSettings = new DropDownTab("rendererSettings", "Renderer Settings", new Vector2f(7, 39), new Vector2f(200, getFontHeight(10) + 4), 10, mainView.renderer, mainView.loader, mainView.window).closed();
        rendererSettings.addCheckbox("culling", "No culling", Config.NO_CULLING);
        rendererSettings.addCheckbox("boners", "No Bone Transforms", Config.NO_BONE_TRANSFORMS);
        rendererSettings.addLabeledSlider("fov", "FOV:  ", (float) Math.toDegrees(Config.FOV), 20, 175);
        rendererSettings.addLabeledTextbox("fps", "FPS:  ", true, false, false, Float.toString(Config.FRAMERATE));
        rendererSettings.addLabeledTextbox("moveSpeed", "Move speed:  ", true, false, false, Float.toString(Config.CAMERA_MOVE_SPEED));
        rendererSettings.addLabeledTextbox("sensitivity", "Sensitivity:  ", true, false, false, Float.toString(Config.MOUSE_SENS));
        rendererSettings.addLabeledTextbox("zNear", "Z Near:  ", true, false, false);
        rendererSettings.addLabeledTextbox("zFar", "Z Far:  ", true, false, false);
        rendererSettings.addString("outlineSizeLabel", "Outline Size:");
        rendererSettings.addSlider("outlineSize", Config.OUTLINE_DISTANCE, 0.525f, 1.1f);
        rendererSettings.addString("outlineColorLabel", "Outline Color:");
        rendererSettings.addLabeledTextbox("outlineColor", "# ");
        rendererSettings.addString("borderColor1Label", "Border Color 1:");
        rendererSettings.addLabeledTextbox("borderColor1", "# ");
        rendererSettings.addString("borderColor2Label", "Border Color 2:");
        rendererSettings.addLabeledTextbox("borderColor2", "# ");
        rendererSettings.addString("borderColor3Label", "Border Color 3:");
        rendererSettings.addLabeledTextbox("borderColor3", "# ");
        rendererSettings.addString("borderColor4Label", "Border Color 4:");
        rendererSettings.addLabeledTextbox("borderColor4", "# ");
        rendererSettings.addString("earthColorLabel", "Earth Color:");
        rendererSettings.addLabeledTextbox("earthColor", "# ");
        rendererSettings.addString("podColorLabel", "Pod Color:");
        rendererSettings.addLabeledTextbox("podColor", "# ");

        int firstInd = 11;

        rendererSettings.tabElements.set(firstInd, new DropDownTab.LabeledTextbox("# ", "outlineColor", new Vector2f(0, 0), new Vector2f(rendererSettings.size.x - 4, getFontHeight(rendererSettings.fontSize) + 4), rendererSettings.fontSize, rendererSettings.renderer, rendererSettings.loader, rendererSettings.window)
        {
            @Override
            public Color textColor() {
                return Config.OUTLINE_COLOR;
            }
        });
        rendererSettings.tabElements.set(firstInd + 2, new DropDownTab.LabeledTextbox("# ", "borderColor1", new Vector2f(0, 0), new Vector2f(rendererSettings.size.x - 4, getFontHeight(rendererSettings.fontSize) + 4), rendererSettings.fontSize, rendererSettings.renderer, rendererSettings.loader, rendererSettings.window)
        {
            @Override
            public Color textColor() {
                return Config.BORDER_COLOR_1;
            }
        });
        rendererSettings.tabElements.set(firstInd + 4, new DropDownTab.LabeledTextbox("# ", "borderColor2", new Vector2f(0, 0), new Vector2f(rendererSettings.size.x - 4, getFontHeight(rendererSettings.fontSize) + 4), rendererSettings.fontSize, rendererSettings.renderer, rendererSettings.loader, rendererSettings.window)
        {
            @Override
            public Color textColor() {
                return Config.BORDER_COLOR_2;
            }
        });
        rendererSettings.tabElements.set(firstInd + 6, new DropDownTab.LabeledTextbox("# ", "borderColor3", new Vector2f(0, 0), new Vector2f(rendererSettings.size.x - 4, getFontHeight(rendererSettings.fontSize) + 4), rendererSettings.fontSize, rendererSettings.renderer, rendererSettings.loader, rendererSettings.window)
        {
            @Override
            public Color textColor() {
                return Config.BORDER_COLOR_3;
            }
        });
        rendererSettings.tabElements.set(firstInd + 8, new DropDownTab.LabeledTextbox("# ", "borderColor4", new Vector2f(0, 0), new Vector2f(rendererSettings.size.x - 4, getFontHeight(rendererSettings.fontSize) + 4), rendererSettings.fontSize, rendererSettings.renderer, rendererSettings.loader, rendererSettings.window)
        {
            @Override
            public Color textColor() {
                return Config.BORDER_COLOR_4;
            }
        });
        rendererSettings.tabElements.set(firstInd + 10, new DropDownTab.LabeledTextbox("# ", "earthColor", new Vector2f(0, 0), new Vector2f(rendererSettings.size.x - 4, getFontHeight(rendererSettings.fontSize) + 4), rendererSettings.fontSize, rendererSettings.renderer, rendererSettings.loader, rendererSettings.window)
        {
            @Override
            public Color textColor() {
                return Config.EARTH_COLOR;
            }
        });
        rendererSettings.tabElements.set(firstInd + 12, new DropDownTab.LabeledTextbox("# ", "podColor", new Vector2f(0, 0), new Vector2f(rendererSettings.size.x - 4, getFontHeight(rendererSettings.fontSize) + 4), rendererSettings.fontSize, rendererSettings.renderer, rendererSettings.loader, rendererSettings.window)
        {
            @Override
            public Color textColor() {
                return Config.POD_COLOR;
            }
        });

        guiSettings = new DropDownTab("guiSettings", "GUI Settings", new Vector2f(7, 39 + 7 + rendererSettings.getFullHeight()), new Vector2f(200, getFontHeight(10) + 4), 10, mainView.renderer, mainView.loader, mainView.window).closed();
        guiSettings.addString("fontColorLabel", "Font Color:");
        guiSettings.addLabeledTextbox("fontColor", "# ");
        guiSettings.addString("primaryColorLabel", "Backdrop Color:");
        guiSettings.addLabeledTextbox("primaryColor", "# ");
        guiSettings.addLabeledTextbox("secondaryColor", "# ");
        guiSettings.addString("interfacePrimaryColorLabel", "Interface Prim. Color:");
        guiSettings.addLabeledTextbox("interfacePrimaryColor", "# ");
        guiSettings.addLabeledTextbox("interfacePrimaryColor2", "# ");
        guiSettings.addString("interfaceSecondaryColorLabel", "Interface Sec. Color:");
        guiSettings.addLabeledTextbox("interfaceSecondaryColor", "# ");
        guiSettings.addLabeledTextbox("interfaceSecondaryColor2", "# ");
        guiSettings.addString("interfaceTertiaryColorLabel", "Interface Tert. Color:");
        guiSettings.addLabeledTextbox("interfaceTertiaryColor", "# ");
        guiSettings.addLabeledTextbox("interfaceTertiaryColor2", "# ");

        guiSettings.tabElements.set(1, new DropDownTab.LabeledTextbox("# ", "fontColor", new Vector2f(0, 0), new Vector2f(rendererSettings.size.x - 4, getFontHeight(rendererSettings.fontSize) + 4), rendererSettings.fontSize, rendererSettings.renderer, rendererSettings.loader, rendererSettings.window)
        {
            @Override
            public Color textColor() {
                return Config.FONT_COLOR;
            }
        });
        guiSettings.tabElements.set(3, new DropDownTab.LabeledTextbox("# ", "primaryColor", new Vector2f(0, 0), new Vector2f(rendererSettings.size.x - 4, getFontHeight(rendererSettings.fontSize) + 4), rendererSettings.fontSize, rendererSettings.renderer, rendererSettings.loader, rendererSettings.window)
        {
            @Override
            public Color textColor() {
                return Config.PRIMARY_COLOR;
            }
        });
        guiSettings.tabElements.set(4, new DropDownTab.LabeledTextbox("# ", "secondaryColor", new Vector2f(0, 0), new Vector2f(rendererSettings.size.x - 4, getFontHeight(rendererSettings.fontSize) + 4), rendererSettings.fontSize, rendererSettings.renderer, rendererSettings.loader, rendererSettings.window)
        {
            @Override
            public Color textColor() {
                return Config.SECONDARY_COLOR;
            }
        });
        guiSettings.tabElements.set(6, new DropDownTab.LabeledTextbox("# ", "interfacePrimaryColor", new Vector2f(0, 0), new Vector2f(rendererSettings.size.x - 4, getFontHeight(rendererSettings.fontSize) + 4), rendererSettings.fontSize, rendererSettings.renderer, rendererSettings.loader, rendererSettings.window)
        {
            @Override
            public Color textColor() {
                return Config.INTERFACE_PRIMARY_COLOR;
            }
        });
        guiSettings.tabElements.set(7, new DropDownTab.LabeledTextbox("# ", "interfacePrimaryColor2", new Vector2f(0, 0), new Vector2f(rendererSettings.size.x - 4, getFontHeight(rendererSettings.fontSize) + 4), rendererSettings.fontSize, rendererSettings.renderer, rendererSettings.loader, rendererSettings.window)
        {
            @Override
            public Color textColor() {
                return Config.INTERFACE_PRIMARY_COLOR2;
            }
        });
        guiSettings.tabElements.set(9, new DropDownTab.LabeledTextbox("# ", "interfaceSecondaryColor", new Vector2f(0, 0), new Vector2f(rendererSettings.size.x - 4, getFontHeight(rendererSettings.fontSize) + 4), rendererSettings.fontSize, rendererSettings.renderer, rendererSettings.loader, rendererSettings.window)
        {
            @Override
            public Color textColor() {
                return Config.INTERFACE_SECONDARY_COLOR;
            }
        });
        guiSettings.tabElements.set(10, new DropDownTab.LabeledTextbox("# ", "interfaceSecondaryColor2", new Vector2f(0, 0), new Vector2f(rendererSettings.size.x - 4, getFontHeight(rendererSettings.fontSize) + 4), rendererSettings.fontSize, rendererSettings.renderer, rendererSettings.loader, rendererSettings.window)
        {
            @Override
            public Color textColor() {
                return Config.INTERFACE_SECONDARY_COLOR2;
            }
        });
        guiSettings.tabElements.set(12, new DropDownTab.LabeledTextbox("# ", "interfaceTertiaryColor", new Vector2f(0, 0), new Vector2f(rendererSettings.size.x - 4, getFontHeight(rendererSettings.fontSize) + 4), rendererSettings.fontSize, rendererSettings.renderer, rendererSettings.loader, rendererSettings.window)
        {
            @Override
            public Color textColor() {
                return Config.INTERFACE_TERTIARY_COLOR;
            }
        });
        guiSettings.tabElements.set(13, new DropDownTab.LabeledTextbox("# ", "interfaceTertiaryColor2", new Vector2f(0, 0), new Vector2f(rendererSettings.size.x - 4, getFontHeight(rendererSettings.fontSize) + 4), rendererSettings.fontSize, rendererSettings.renderer, rendererSettings.loader, rendererSettings.window)
        {
            @Override
            public Color textColor() {
                return Config.INTERFACE_TERTIARY_COLOR2;
            }
        });

        controls = new DropDownTab("controls", "Controls", new Vector2f(7, 39 + 14 + rendererSettings.getFullHeight() + guiSettings.getFullHeight()), new Vector2f(200, getFontHeight(10) + 4), 10, mainView.renderer, mainView.loader, mainView.window).closed();
        controls.addLabeledButton("forward", "Forward:  ", "W", new Button() {
            @Override
            public void clickedButton(int button, int action, int mods) {
                Button btn = this;
                mainView.setCurrentScreen(new GuiKeybind(mainView.KEY_FORWARD, 10, renderer, loader, mainView.window) {
                    @Override
                    public void keybind(int key) {
                        mainView.KEY_FORWARD = key;
                        if(mainView.KEY_FORWARD != GLFW.GLFW_KEY_UNKNOWN)
                            btn.buttonText = Utils.getKeyName(key).toUpperCase();
                        else
                            btn.buttonText = "NONE";

                        if(btn.buttonText == null)
                            btn.buttonText = "error";
                    }

                    @Override
                    public void returnPreviousScreen() {
                        mainView.returnToPreviousScreen();
                    }
                });
            }
        });
        controls.addLabeledButton("left", "Left:  ", "A", new Button() {
            @Override
            public void clickedButton(int button, int action, int mods) {
                Button btn = this;
                mainView.setCurrentScreen(new GuiKeybind(mainView.KEY_LEFT, 10, renderer, loader, mainView.window) {
                    @Override
                    public void keybind(int key) {
                        mainView.KEY_LEFT = key;
                        if(mainView.KEY_LEFT != GLFW.GLFW_KEY_UNKNOWN)
                            btn.buttonText = Utils.getKeyName(key).toUpperCase();
                        else
                            btn.buttonText = "NONE";

                        if(btn.buttonText == null)
                            btn.buttonText = "error";
                    }

                    @Override
                    public void returnPreviousScreen() {
                        mainView.returnToPreviousScreen();
                    }
                });
            }
        });
        controls.addLabeledButton("back", "Back:  ", "S", new Button() {
            @Override
            public void clickedButton(int button, int action, int mods) {
                Button btn = this;
                mainView.setCurrentScreen(new GuiKeybind(mainView.KEY_BACK, 10, renderer, loader, mainView.window) {
                    @Override
                    public void keybind(int key) {
                        mainView.KEY_BACK = key;
                        if(mainView.KEY_BACK != GLFW.GLFW_KEY_UNKNOWN)
                            btn.buttonText = Utils.getKeyName(key).toUpperCase();
                        else
                            btn.buttonText = "NONE";

                        if(btn.buttonText == null)
                            btn.buttonText = "error";
                    }

                    @Override
                    public void returnPreviousScreen() {
                        mainView.returnToPreviousScreen();
                    }
                });
            }
        });
        controls.addLabeledButton("right", "Right:  ", "D", new Button() {
            @Override
            public void clickedButton(int button, int action, int mods) {
                Button btn = this;
                mainView.setCurrentScreen(new GuiKeybind(mainView.KEY_RIGHT, 10, renderer, loader, mainView.window) {
                    @Override
                    public void keybind(int key) {
                        mainView.KEY_RIGHT = key;
                        if(mainView.KEY_RIGHT != GLFW.GLFW_KEY_UNKNOWN)
                            btn.buttonText = Utils.getKeyName(key).toUpperCase();
                        else
                            btn.buttonText = "NONE";

                        if(btn.buttonText == null)
                            btn.buttonText = "error";
                    }

                    @Override
                    public void returnPreviousScreen() {
                        mainView.returnToPreviousScreen();
                    }
                });
            }
        });
        controls.addLabeledButton("up", "Up:  ", "SPACE", new Button() {
            @Override
            public void clickedButton(int button, int action, int mods) {
                Button btn = this;
                mainView.setCurrentScreen(new GuiKeybind(mainView.KEY_UP, 10, renderer, loader, mainView.window) {
                    @Override
                    public void keybind(int key) {
                        mainView.KEY_UP = key;
                        if(mainView.KEY_UP != GLFW.GLFW_KEY_UNKNOWN)
                            btn.buttonText = Utils.getKeyName(key).toUpperCase();
                        else
                            btn.buttonText = "NONE";

                        if(btn.buttonText == null)
                            btn.buttonText = "error";
                    }

                    @Override
                    public void returnPreviousScreen() {
                        mainView.returnToPreviousScreen();
                    }
                });
            }
        });
        controls.addLabeledButton("down", "Down:  ", "LEFT SHIFT", new Button() {
            @Override
            public void clickedButton(int button, int action, int mods) {
                Button btn = this;
                mainView.setCurrentScreen(new GuiKeybind(mainView.KEY_DOWN, 10, renderer, loader, mainView.window) {
                    @Override
                    public void keybind(int key) {
                        mainView.KEY_DOWN = key;
                        if(mainView.KEY_DOWN != GLFW.GLFW_KEY_UNKNOWN)
                            btn.buttonText = Utils.getKeyName(key).toUpperCase();
                        else
                            btn.buttonText = "NONE";

                        if(btn.buttonText == null)
                            btn.buttonText = "error";
                    }

                    @Override
                    public void returnPreviousScreen() {
                        mainView.returnToPreviousScreen();
                    }
                });
            }
        });

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

        int i = 0;

        Config.NO_CULLING = ((Checkbox)rendererSettings.tabElements.get(i)).isChecked;
        i++;
        Config.NO_BONE_TRANSFORMS = ((Checkbox)rendererSettings.tabElements.get(i)).isChecked;
        i++;

        Vector2f fovSlider = setSliderValue(((DropDownTab.LabeledSlider)rendererSettings.tabElements.get(i)).slider, (float) Math.toDegrees(Config.FOV));
        if(fovSlider.y == 1)
        {
            Config.FOV = (float) Math.toRadians(fovSlider.x);
            Config.updateFile();
        }
        i++;

        String fps = setTextboxValueString(((DropDownTab.LabeledTextbox) rendererSettings.tabElements.get(i)).textbox, Float.toString(Config.FRAMERATE));
        if(fps != null)
            try{
                Config.FRAMERATE = Float.parseFloat(fps);
                Config.updateFile();
            }catch (Exception e){}
        i++;

        String moveSpeed = setTextboxValueString(((DropDownTab.LabeledTextbox) rendererSettings.tabElements.get(i)).textbox, Float.toString(Config.CAMERA_MOVE_SPEED));
        if(moveSpeed != null)
            try{
                Config.CAMERA_MOVE_SPEED = Float.parseFloat(moveSpeed);
                Config.updateFile();
            }catch (Exception e){}
        i++;

        String sens = setTextboxValueString(((DropDownTab.LabeledTextbox) rendererSettings.tabElements.get(i)).textbox, Float.toString(Config.MOUSE_SENS));
        if(sens != null)
            try{
                Config.MOUSE_SENS = Float.parseFloat(sens);
                Config.updateFile();
            }catch (Exception e){}
        i++;

        String zNear = setTextboxValueString(((DropDownTab.LabeledTextbox) rendererSettings.tabElements.get(i)).textbox, Float.toString(Config.Z_NEAR));
        if(zNear != null)
            try{
                Config.Z_NEAR = Float.parseFloat(zNear);
                Config.updateFile();
            }catch (Exception e){}
        i++;

        String zFar = setTextboxValueString(((DropDownTab.LabeledTextbox) rendererSettings.tabElements.get(i)).textbox, Float.toString(Config.Z_FAR));
        if(zFar != null)
            try{
                Config.Z_FAR = Float.parseFloat(zFar);
                Config.updateFile();
            }catch (Exception e){}
        i += 2;

        Vector2f outlineDistSlider = setSliderValue(((Slider)rendererSettings.tabElements.get(i)), Config.OUTLINE_DISTANCE);
        if(outlineDistSlider.y == 1)
        {
            Config.OUTLINE_DISTANCE = outlineDistSlider.x;
            Config.updateFile();
        }
        i += 2;

        Textbox outlC = ((DropDownTab.LabeledTextbox) rendererSettings.tabElements.get(i)).textbox;
        String outlineColor = setTextboxValueString(outlC, Utils.toHexColor(Config.OUTLINE_COLOR));
        if(outlineColor != null)
            try{
                Config.OUTLINE_COLOR = Utils.parseHexColor(outlineColor);
                Config.updateFile();
            }catch (Exception e){}
        i += 2;

        Textbox borderCol1 = ((DropDownTab.LabeledTextbox) rendererSettings.tabElements.get(i)).textbox;
        String borderColor1 = setTextboxValueString(borderCol1, Utils.toHexColor(Config.BORDER_COLOR_1));
        if(borderColor1 != null)
            try{
                Config.BORDER_COLOR_1 = Utils.parseHexColor(borderColor1); mainView.borders.material.setColor(Config.BORDER_COLOR_1);
                Config.updateFile();
            }catch (Exception e){}
        i += 2;

        Textbox borderCol2 = ((DropDownTab.LabeledTextbox) rendererSettings.tabElements.get(i)).textbox;
        String borderColor2 = setTextboxValueString(borderCol2, Utils.toHexColor(Config.BORDER_COLOR_2));
        if(borderColor2 != null)
            try{
                Config.BORDER_COLOR_2 = Utils.parseHexColor(borderColor2); mainView.borders1.material.setColor(Config.BORDER_COLOR_2);
                Config.updateFile();
            }catch (Exception e){}
        i += 2;

        Textbox borderCol3 = ((DropDownTab.LabeledTextbox) rendererSettings.tabElements.get(i)).textbox;
        String borderColor3 = setTextboxValueString(borderCol3, Utils.toHexColor(Config.BORDER_COLOR_3));
        if(borderColor3 != null)
            try{
                Config.BORDER_COLOR_3 = Utils.parseHexColor(borderColor3); mainView.borders2.material.setColor(Config.BORDER_COLOR_3);
                Config.updateFile();
            }catch (Exception e){}
        i += 2;

        Textbox borderCol4 = ((DropDownTab.LabeledTextbox) rendererSettings.tabElements.get(i)).textbox;
        String borderColor4 = setTextboxValueString(borderCol4, Utils.toHexColor(Config.BORDER_COLOR_4));
        if(borderColor4 != null)
            try{
                Config.BORDER_COLOR_4 = Utils.parseHexColor(borderColor4); mainView.borders3.material.setColor(Config.BORDER_COLOR_4);
                Config.updateFile();
            }catch (Exception e){}
        i += 2;

        Textbox earthCol = ((DropDownTab.LabeledTextbox) rendererSettings.tabElements.get(i)).textbox;
        String earthColor = setTextboxValueString(earthCol, Utils.toHexColor(Config.EARTH_COLOR));
        if(earthColor != null)
            try{
                Config.EARTH_COLOR = Utils.parseHexColor(earthColor); mainView.earth.material.setOverlayColor(Config.EARTH_COLOR);
                Config.updateFile();
            }catch (Exception e){}
        i += 2;

        Textbox podCol = ((DropDownTab.LabeledTextbox) rendererSettings.tabElements.get(i)).textbox;
        String podColor = setTextboxValueString(podCol, Utils.toHexColor(Config.POD_COLOR));
        if(podColor != null)
            try{
                Config.POD_COLOR = Utils.parseHexColor(podColor); mainView.pod.material.setOverlayColor(Config.POD_COLOR);
                Config.updateFile();
            }catch (Exception e){}

        Textbox fontC = ((DropDownTab.LabeledTextbox) guiSettings.tabElements.get(1)).textbox;
        String fontColor = setTextboxValueString(fontC, Utils.toHexColor(Config.FONT_COLOR));
        if(fontColor != null)
            try{
                Config.FONT_COLOR = Utils.parseHexColor(fontColor);
                Config.updateFile();
            }catch (Exception e){}

        Textbox primC = ((DropDownTab.LabeledTextbox) guiSettings.tabElements.get(3)).textbox;
        String primColor = setTextboxValueString(primC, Utils.toHexColor(Config.PRIMARY_COLOR));
        if(primColor != null)
            try{
                Config.PRIMARY_COLOR = Utils.parseHexColor(primColor);
                Config.updateFile();
            }catch (Exception e){}

        Textbox secC = ((DropDownTab.LabeledTextbox) guiSettings.tabElements.get(4)).textbox;
        String secColor = setTextboxValueString(secC, Utils.toHexColor(Config.SECONDARY_COLOR));
        if(secColor != null)
            try{
                Config.SECONDARY_COLOR = Utils.parseHexColor(secColor);
                Config.updateFile();
            }catch (Exception e){}

        Textbox iprimC = ((DropDownTab.LabeledTextbox) guiSettings.tabElements.get(6)).textbox;
        String iprimColor = setTextboxValueString(iprimC, Utils.toHexColor(Config.INTERFACE_PRIMARY_COLOR));
        if(iprimColor != null)
            try{
                Config.INTERFACE_PRIMARY_COLOR = Utils.parseHexColor(iprimColor);
                Config.updateFile();
            }catch (Exception e){}

        Textbox iprimC2 = ((DropDownTab.LabeledTextbox) guiSettings.tabElements.get(7)).textbox;
        String iprimColor2 = setTextboxValueString(iprimC2, Utils.toHexColor(Config.INTERFACE_PRIMARY_COLOR2));
        if(iprimColor2 != null)
            try{
                Config.INTERFACE_PRIMARY_COLOR2 = Utils.parseHexColor(iprimColor2);
                Config.updateFile();
            }catch (Exception e){}

        Textbox isecC = ((DropDownTab.LabeledTextbox) guiSettings.tabElements.get(9)).textbox;
        String isecColor = setTextboxValueString(isecC, Utils.toHexColor(Config.INTERFACE_SECONDARY_COLOR));
        if(isecColor != null)
            try{
                Config.INTERFACE_SECONDARY_COLOR = Utils.parseHexColor(isecColor);
                Config.updateFile();
            }catch (Exception e){}

        Textbox isecC2 = ((DropDownTab.LabeledTextbox) guiSettings.tabElements.get(10)).textbox;
        String isecColor2 = setTextboxValueString(isecC2, Utils.toHexColor(Config.INTERFACE_SECONDARY_COLOR2));
        if(isecColor2 != null)
            try{
                Config.INTERFACE_SECONDARY_COLOR2 = Utils.parseHexColor(isecColor2);
                Config.updateFile();
            }catch (Exception e){}

        Textbox itertC = ((DropDownTab.LabeledTextbox) guiSettings.tabElements.get(12)).textbox;
        String itertColor = setTextboxValueString(itertC, Utils.toHexColor(Config.INTERFACE_TERTIARY_COLOR));
        if(itertColor != null)
            try{
                Config.INTERFACE_TERTIARY_COLOR = Utils.parseHexColor(itertColor);
                Config.updateFile();
            }catch (Exception e){}

        Textbox itertC2 = ((DropDownTab.LabeledTextbox) guiSettings.tabElements.get(13)).textbox;
        String itertColor2 = setTextboxValueString(itertC2, Utils.toHexColor(Config.INTERFACE_TERTIARY_COLOR2));
        if(itertColor2 != null)
            try{
                Config.INTERFACE_TERTIARY_COLOR2 = Utils.parseHexColor(itertColor2);
                Config.updateFile();
            }catch (Exception e){}
    }

}
