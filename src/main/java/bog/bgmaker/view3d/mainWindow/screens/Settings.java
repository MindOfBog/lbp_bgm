package bog.bgmaker.view3d.mainWindow.screens;

import bog.bgmaker.Main;
import bog.bgmaker.view3d.mainWindow.View3D;
import bog.bgmaker.view3d.managers.MouseInput;
import bog.bgmaker.view3d.renderer.gui.GuiKeybind;
import bog.bgmaker.view3d.renderer.gui.GuiScreen;
import bog.bgmaker.view3d.renderer.gui.elements.*;
import bog.bgmaker.view3d.renderer.gui.elements.Button;
import bog.bgmaker.view3d.renderer.gui.elements.Checkbox;
import bog.bgmaker.view3d.utils.Const;
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
        rendererSettings.addCheckbox("culling", "No culling", Const.NO_CULLING);
        rendererSettings.addLabeledSlider("fov", "FOV:  ", (float) Math.toDegrees(Const.FOV), 20, 175);
        rendererSettings.addLabeledTextbox("fps", "FPS:  ", true, false, false, Float.toString(Const.FRAMERATE));
        rendererSettings.addLabeledTextbox("moveSpeed", "Move speed:  ", true, false, false, Float.toString(Const.CAMERA_MOVE_SPEED));
        rendererSettings.addLabeledTextbox("sensitivity", "Sensitivity:  ", true, false, false, Float.toString(Const.MOUSE_SENS));
        rendererSettings.addLabeledTextbox("zNear", "Z Near:  ", true, false, false);
        rendererSettings.addLabeledTextbox("zFar", "Z Far:  ", true, false, false);
        rendererSettings.addString("outlineSizeLabel", "Outline Size:");
        rendererSettings.addSlider("outlineSize", Const.OUTLINE_DISTANCE, 0.525f, 1.1f);
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

        rendererSettings.tabElements.set(10, new DropDownTab.LabeledTextbox("# ", "outlineColor", new Vector2f(0, 0), new Vector2f(rendererSettings.size.x - 4, getFontHeight(rendererSettings.fontSize) + 4), rendererSettings.fontSize, rendererSettings.renderer, rendererSettings.loader, rendererSettings.window)
        {
            @Override
            public Color textColor() {
                return Const.OUTLINE_COLOR;
            }
        });
        rendererSettings.tabElements.set(12, new DropDownTab.LabeledTextbox("# ", "borderColor1", new Vector2f(0, 0), new Vector2f(rendererSettings.size.x - 4, getFontHeight(rendererSettings.fontSize) + 4), rendererSettings.fontSize, rendererSettings.renderer, rendererSettings.loader, rendererSettings.window)
        {
            @Override
            public Color textColor() {
                return Const.BORDER_COLOR_1;
            }
        });
        rendererSettings.tabElements.set(14, new DropDownTab.LabeledTextbox("# ", "borderColor2", new Vector2f(0, 0), new Vector2f(rendererSettings.size.x - 4, getFontHeight(rendererSettings.fontSize) + 4), rendererSettings.fontSize, rendererSettings.renderer, rendererSettings.loader, rendererSettings.window)
        {
            @Override
            public Color textColor() {
                return Const.BORDER_COLOR_2;
            }
        });
        rendererSettings.tabElements.set(16, new DropDownTab.LabeledTextbox("# ", "borderColor3", new Vector2f(0, 0), new Vector2f(rendererSettings.size.x - 4, getFontHeight(rendererSettings.fontSize) + 4), rendererSettings.fontSize, rendererSettings.renderer, rendererSettings.loader, rendererSettings.window)
        {
            @Override
            public Color textColor() {
                return Const.BORDER_COLOR_3;
            }
        });
        rendererSettings.tabElements.set(18, new DropDownTab.LabeledTextbox("# ", "borderColor4", new Vector2f(0, 0), new Vector2f(rendererSettings.size.x - 4, getFontHeight(rendererSettings.fontSize) + 4), rendererSettings.fontSize, rendererSettings.renderer, rendererSettings.loader, rendererSettings.window)
        {
            @Override
            public Color textColor() {
                return Const.BORDER_COLOR_4;
            }
        });
        rendererSettings.tabElements.set(20, new DropDownTab.LabeledTextbox("# ", "earthColor", new Vector2f(0, 0), new Vector2f(rendererSettings.size.x - 4, getFontHeight(rendererSettings.fontSize) + 4), rendererSettings.fontSize, rendererSettings.renderer, rendererSettings.loader, rendererSettings.window)
        {
            @Override
            public Color textColor() {
                return Const.EARTH_COLOR;
            }
        });
        rendererSettings.tabElements.set(22, new DropDownTab.LabeledTextbox("# ", "podColor", new Vector2f(0, 0), new Vector2f(rendererSettings.size.x - 4, getFontHeight(rendererSettings.fontSize) + 4), rendererSettings.fontSize, rendererSettings.renderer, rendererSettings.loader, rendererSettings.window)
        {
            @Override
            public Color textColor() {
                return Const.POD_COLOR;
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
                return Const.FONT_COLOR;
            }
        });
        guiSettings.tabElements.set(3, new DropDownTab.LabeledTextbox("# ", "primaryColor", new Vector2f(0, 0), new Vector2f(rendererSettings.size.x - 4, getFontHeight(rendererSettings.fontSize) + 4), rendererSettings.fontSize, rendererSettings.renderer, rendererSettings.loader, rendererSettings.window)
        {
            @Override
            public Color textColor() {
                return Const.PRIMARY_COLOR;
            }
        });
        guiSettings.tabElements.set(4, new DropDownTab.LabeledTextbox("# ", "secondaryColor", new Vector2f(0, 0), new Vector2f(rendererSettings.size.x - 4, getFontHeight(rendererSettings.fontSize) + 4), rendererSettings.fontSize, rendererSettings.renderer, rendererSettings.loader, rendererSettings.window)
        {
            @Override
            public Color textColor() {
                return Const.SECONDARY_COLOR;
            }
        });
        guiSettings.tabElements.set(6, new DropDownTab.LabeledTextbox("# ", "interfacePrimaryColor", new Vector2f(0, 0), new Vector2f(rendererSettings.size.x - 4, getFontHeight(rendererSettings.fontSize) + 4), rendererSettings.fontSize, rendererSettings.renderer, rendererSettings.loader, rendererSettings.window)
        {
            @Override
            public Color textColor() {
                return Const.INTERFACE_PRIMARY_COLOR;
            }
        });
        guiSettings.tabElements.set(7, new DropDownTab.LabeledTextbox("# ", "interfacePrimaryColor2", new Vector2f(0, 0), new Vector2f(rendererSettings.size.x - 4, getFontHeight(rendererSettings.fontSize) + 4), rendererSettings.fontSize, rendererSettings.renderer, rendererSettings.loader, rendererSettings.window)
        {
            @Override
            public Color textColor() {
                return Const.INTERFACE_PRIMARY_COLOR2;
            }
        });
        guiSettings.tabElements.set(9, new DropDownTab.LabeledTextbox("# ", "interfaceSecondaryColor", new Vector2f(0, 0), new Vector2f(rendererSettings.size.x - 4, getFontHeight(rendererSettings.fontSize) + 4), rendererSettings.fontSize, rendererSettings.renderer, rendererSettings.loader, rendererSettings.window)
        {
            @Override
            public Color textColor() {
                return Const.INTERFACE_SECONDARY_COLOR;
            }
        });
        guiSettings.tabElements.set(10, new DropDownTab.LabeledTextbox("# ", "interfaceSecondaryColor2", new Vector2f(0, 0), new Vector2f(rendererSettings.size.x - 4, getFontHeight(rendererSettings.fontSize) + 4), rendererSettings.fontSize, rendererSettings.renderer, rendererSettings.loader, rendererSettings.window)
        {
            @Override
            public Color textColor() {
                return Const.INTERFACE_SECONDARY_COLOR2;
            }
        });
        guiSettings.tabElements.set(12, new DropDownTab.LabeledTextbox("# ", "interfaceTertiaryColor", new Vector2f(0, 0), new Vector2f(rendererSettings.size.x - 4, getFontHeight(rendererSettings.fontSize) + 4), rendererSettings.fontSize, rendererSettings.renderer, rendererSettings.loader, rendererSettings.window)
        {
            @Override
            public Color textColor() {
                return Const.INTERFACE_TERTIARY_COLOR;
            }
        });
        guiSettings.tabElements.set(13, new DropDownTab.LabeledTextbox("# ", "interfaceTertiaryColor2", new Vector2f(0, 0), new Vector2f(rendererSettings.size.x - 4, getFontHeight(rendererSettings.fontSize) + 4), rendererSettings.fontSize, rendererSettings.renderer, rendererSettings.loader, rendererSettings.window)
        {
            @Override
            public Color textColor() {
                return Const.INTERFACE_TERTIARY_COLOR2;
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

        Const.NO_CULLING = ((Checkbox)rendererSettings.tabElements.get(0)).isChecked;

        Vector2f fovSlider = setSliderValue(((DropDownTab.LabeledSlider)rendererSettings.tabElements.get(1)).slider, (float) Math.toDegrees(Const.FOV));
        if(fovSlider.y == 1)
            Const.FOV = (float) Math.toRadians(fovSlider.x);

        String fps = setTextboxValueString(((DropDownTab.LabeledTextbox) rendererSettings.tabElements.get(2)).textbox, Float.toString(Const.FRAMERATE));
        if(fps != null)
            try{Const.FRAMERATE = Float.parseFloat(fps);}catch (Exception e){}

        String moveSpeed = setTextboxValueString(((DropDownTab.LabeledTextbox) rendererSettings.tabElements.get(3)).textbox, Float.toString(Const.CAMERA_MOVE_SPEED));
        if(moveSpeed != null)
            try{Const.CAMERA_MOVE_SPEED = Float.parseFloat(moveSpeed);}catch (Exception e){}

        String sens = setTextboxValueString(((DropDownTab.LabeledTextbox) rendererSettings.tabElements.get(4)).textbox, Float.toString(Const.MOUSE_SENS));
        if(sens != null)
            try{Const.MOUSE_SENS = Float.parseFloat(sens);}catch (Exception e){}

        String zNear = setTextboxValueString(((DropDownTab.LabeledTextbox) rendererSettings.tabElements.get(5)).textbox, Float.toString(Const.Z_NEAR));
        if(zNear != null)
            try{Const.Z_NEAR = Float.parseFloat(zNear);}catch (Exception e){}

        String zFar = setTextboxValueString(((DropDownTab.LabeledTextbox) rendererSettings.tabElements.get(6)).textbox, Float.toString(Const.Z_FAR));
        if(zFar != null)
            try{Const.Z_FAR = Float.parseFloat(zFar);}catch (Exception e){}

        Vector2f outlineDistSlider = setSliderValue(((Slider)rendererSettings.tabElements.get(8)), Const.OUTLINE_DISTANCE);
        if(outlineDistSlider.y == 1)
            Const.OUTLINE_DISTANCE = outlineDistSlider.x;

        Textbox outlC = ((DropDownTab.LabeledTextbox) rendererSettings.tabElements.get(10)).textbox;
        String outlineColor = setTextboxValueString(outlC, Utils.toHexColor(Const.OUTLINE_COLOR));
        if(outlineColor != null)
            try{Const.OUTLINE_COLOR = Utils.parseHexColor(outlineColor);}catch (Exception e){}

        Textbox borderCol1 = ((DropDownTab.LabeledTextbox) rendererSettings.tabElements.get(12)).textbox;
        String borderColor1 = setTextboxValueString(borderCol1, Utils.toHexColor(Const.BORDER_COLOR_1));
        if(borderColor1 != null)
            try{Const.BORDER_COLOR_1 = Utils.parseHexColor(borderColor1); mainView.borders.material.setColor(Const.BORDER_COLOR_1);}catch (Exception e){}

        Textbox borderCol2 = ((DropDownTab.LabeledTextbox) rendererSettings.tabElements.get(14)).textbox;
        String borderColor2 = setTextboxValueString(borderCol2, Utils.toHexColor(Const.BORDER_COLOR_2));
        if(borderColor2 != null)
            try{Const.BORDER_COLOR_2 = Utils.parseHexColor(borderColor2); mainView.borders1.material.setColor(Const.BORDER_COLOR_2);}catch (Exception e){}

        Textbox borderCol3 = ((DropDownTab.LabeledTextbox) rendererSettings.tabElements.get(16)).textbox;
        String borderColor3 = setTextboxValueString(borderCol3, Utils.toHexColor(Const.BORDER_COLOR_3));
        if(borderColor3 != null)
            try{Const.BORDER_COLOR_3 = Utils.parseHexColor(borderColor3); mainView.borders2.material.setColor(Const.BORDER_COLOR_3);}catch (Exception e){}

        Textbox borderCol4 = ((DropDownTab.LabeledTextbox) rendererSettings.tabElements.get(18)).textbox;
        String borderColor4 = setTextboxValueString(borderCol4, Utils.toHexColor(Const.BORDER_COLOR_4));
        if(borderColor4 != null)
            try{Const.BORDER_COLOR_4 = Utils.parseHexColor(borderColor4); mainView.borders3.material.setColor(Const.BORDER_COLOR_4);}catch (Exception e){}

        Textbox earthCol = ((DropDownTab.LabeledTextbox) rendererSettings.tabElements.get(20)).textbox;
        String earthColor = setTextboxValueString(earthCol, Utils.toHexColor(Const.EARTH_COLOR));
        if(earthColor != null)
            try{Const.EARTH_COLOR = Utils.parseHexColor(earthColor); mainView.earth.material.setOverlayColor(Const.EARTH_COLOR);}catch (Exception e){}

        Textbox podCol = ((DropDownTab.LabeledTextbox) rendererSettings.tabElements.get(22)).textbox;
        String podColor = setTextboxValueString(podCol, Utils.toHexColor(Const.POD_COLOR));
        if(podColor != null)
            try{Const.POD_COLOR = Utils.parseHexColor(podColor); mainView.pod.material.setOverlayColor(Const.POD_COLOR);}catch (Exception e){}

        Textbox fontC = ((DropDownTab.LabeledTextbox) guiSettings.tabElements.get(1)).textbox;
        String fontColor = setTextboxValueString(fontC, Utils.toHexColor(Const.FONT_COLOR));
        if(fontColor != null)
            try{Const.FONT_COLOR = Utils.parseHexColor(fontColor);}catch (Exception e){}

        Textbox primC = ((DropDownTab.LabeledTextbox) guiSettings.tabElements.get(3)).textbox;
        String primColor = setTextboxValueString(primC, Utils.toHexColor(Const.PRIMARY_COLOR));
        if(primColor != null)
            try{Const.PRIMARY_COLOR = Utils.parseHexColor(primColor);}catch (Exception e){}

        Textbox secC = ((DropDownTab.LabeledTextbox) guiSettings.tabElements.get(4)).textbox;
        String secColor = setTextboxValueString(secC, Utils.toHexColor(Const.SECONDARY_COLOR));
        if(secColor != null)
            try{Const.SECONDARY_COLOR = Utils.parseHexColor(secColor);}catch (Exception e){}

        Textbox iprimC = ((DropDownTab.LabeledTextbox) guiSettings.tabElements.get(6)).textbox;
        String iprimColor = setTextboxValueString(iprimC, Utils.toHexColor(Const.INTERFACE_PRIMARY_COLOR));
        if(iprimColor != null)
            try{Const.INTERFACE_PRIMARY_COLOR = Utils.parseHexColor(iprimColor);}catch (Exception e){}

        Textbox iprimC2 = ((DropDownTab.LabeledTextbox) guiSettings.tabElements.get(7)).textbox;
        String iprimColor2 = setTextboxValueString(iprimC2, Utils.toHexColor(Const.INTERFACE_PRIMARY_COLOR2));
        if(iprimColor2 != null)
            try{Const.INTERFACE_PRIMARY_COLOR2 = Utils.parseHexColor(iprimColor2);}catch (Exception e){}

        Textbox isecC = ((DropDownTab.LabeledTextbox) guiSettings.tabElements.get(9)).textbox;
        String isecColor = setTextboxValueString(isecC, Utils.toHexColor(Const.INTERFACE_SECONDARY_COLOR));
        if(isecColor != null)
            try{Const.INTERFACE_SECONDARY_COLOR = Utils.parseHexColor(isecColor);}catch (Exception e){}

        Textbox isecC2 = ((DropDownTab.LabeledTextbox) guiSettings.tabElements.get(10)).textbox;
        String isecColor2 = setTextboxValueString(isecC2, Utils.toHexColor(Const.INTERFACE_SECONDARY_COLOR2));
        if(isecColor2 != null)
            try{Const.INTERFACE_SECONDARY_COLOR2 = Utils.parseHexColor(isecColor2);}catch (Exception e){}

        Textbox itertC = ((DropDownTab.LabeledTextbox) guiSettings.tabElements.get(12)).textbox;
        String itertColor = setTextboxValueString(itertC, Utils.toHexColor(Const.INTERFACE_TERTIARY_COLOR));
        if(itertColor != null)
            try{Const.INTERFACE_TERTIARY_COLOR = Utils.parseHexColor(itertColor);}catch (Exception e){}

        Textbox itertC2 = ((DropDownTab.LabeledTextbox) guiSettings.tabElements.get(13)).textbox;
        String itertColor2 = setTextboxValueString(itertC2, Utils.toHexColor(Const.INTERFACE_TERTIARY_COLOR2));
        if(itertColor2 != null)
            try{Const.INTERFACE_TERTIARY_COLOR2 = Utils.parseHexColor(itertColor2);}catch (Exception e){}
    }

}
