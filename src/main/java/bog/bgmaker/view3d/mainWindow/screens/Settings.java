package bog.bgmaker.view3d.mainWindow.screens;

import bog.bgmaker.Main;
import bog.bgmaker.view3d.mainWindow.View3D;
import bog.bgmaker.view3d.managers.MouseInput;
import bog.bgmaker.view3d.renderer.gui.GuiKeybind;
import bog.bgmaker.view3d.renderer.gui.GuiScreen;
import bog.bgmaker.view3d.renderer.gui.elements.*;
import bog.bgmaker.view3d.utils.Const;
import bog.bgmaker.view3d.utils.Utils;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

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
    public DropDownTab controls;
    public DropDownTab debug;

    public void init()
    {
        rendererSettings = new DropDownTab("rendererSettings", "Renderer Settings", new Vector2f(7, 19 + getFontHeight(10)), new Vector2f(200, getFontHeight(10) + 4), 10, mainView.renderer, mainView.loader, mainView.window).closed();
        rendererSettings.addCheckbox("culling", "No culling", Const.NO_CULLING);
        rendererSettings.addLabeledSlider("fov", "FOV:  ", (float) Math.toDegrees(Const.FOV), 20, 175);
        rendererSettings.addLabeledTextbox("fps", "FPS:  ", true, false, false, Float.toString(Const.FRAMERATE));
        rendererSettings.addLabeledTextbox("moveSpeed", "Move speed:  ", true, false, false, Float.toString(Const.CAMERA_MOVE_SPEED));
        rendererSettings.addLabeledTextbox("sensitivity", "Sensitivity:  ", true, false, false, Float.toString(Const.MOUSE_SENS));
        rendererSettings.addLabeledTextbox("zNear", "Z Near:  ", true, false, false);
        rendererSettings.addLabeledTextbox("zFar", "Z Far:  ", true, false, false);
        rendererSettings.addCheckbox("stippleOutline", "Stipple Outline", Const.STIPPLE_OUTLINE);
        rendererSettings.addString("outlineDistLabel", "Outline Distance:");
        rendererSettings.addSlider("outlineDistance", Const.OUTLINE_DISTANCE, 0f, 0.5f);
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

        controls = new DropDownTab("controls", "Controls", new Vector2f(7, 19 + getFontHeight(10) + 7 + rendererSettings.getFullHeight()), new Vector2f(200, getFontHeight(10) + 4), 10, mainView.renderer, mainView.loader, mainView.window).closed();
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
        this.guiElements.add(controls);

        if (Main.debug) {
            debug = new DropDownTab("debug", "Debug", new Vector2f(214, 7), new Vector2f(200, getFontHeight(10) + 4), 10, mainView.renderer, mainView.loader, mainView.window).closed();
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
            try{Const.FRAMERATE = Float.parseFloat(fps);}catch (Exception e){Const.FRAMERATE = 60;}

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

        Const.STIPPLE_OUTLINE = ((Checkbox)rendererSettings.tabElements.get(7)).isChecked;

        Vector2f outlineDistSlider = setSliderValue(((Slider)rendererSettings.tabElements.get(9)), 1 - Const.OUTLINE_DISTANCE);
        if(outlineDistSlider.y == 1)
            Const.OUTLINE_DISTANCE = 1 - outlineDistSlider.x;

        Textbox outlC = ((DropDownTab.LabeledTextbox) rendererSettings.tabElements.get(11)).textbox;
        String outlineColor = setTextboxValueString(outlC, Utils.toHexColor(Const.OUTLINE_COLOR));
        if(outlineColor != null)
            try{Const.OUTLINE_COLOR = Utils.parseHexColor(outlineColor);}catch (Exception e){}
        outlC.textColor = Const.OUTLINE_COLOR;

        Textbox borderCol1 = ((DropDownTab.LabeledTextbox) rendererSettings.tabElements.get(13)).textbox;
        String borderColor1 = setTextboxValueString(borderCol1, Utils.toHexColor(Const.BORDER_COLOR_1));
        if(borderColor1 != null)
            try{Const.BORDER_COLOR_1 = Utils.parseHexColor(borderColor1); mainView.borders.material.setColor(Const.BORDER_COLOR_1);}catch (Exception e){}
        borderCol1.textColor = Const.BORDER_COLOR_1;

        Textbox borderCol2 = ((DropDownTab.LabeledTextbox) rendererSettings.tabElements.get(15)).textbox;
        String borderColor2 = setTextboxValueString(borderCol2, Utils.toHexColor(Const.BORDER_COLOR_2));
        if(borderColor2 != null)
            try{Const.BORDER_COLOR_2 = Utils.parseHexColor(borderColor2); mainView.borders1.material.setColor(Const.BORDER_COLOR_2);}catch (Exception e){}
        borderCol2.textColor = Const.BORDER_COLOR_2;

        Textbox borderCol3 = ((DropDownTab.LabeledTextbox) rendererSettings.tabElements.get(17)).textbox;
        String borderColor3 = setTextboxValueString(borderCol3, Utils.toHexColor(Const.BORDER_COLOR_3));
        if(borderColor3 != null)
            try{Const.BORDER_COLOR_3 = Utils.parseHexColor(borderColor3); mainView.borders2.material.setColor(Const.BORDER_COLOR_3);}catch (Exception e){}
        borderCol3.textColor = Const.BORDER_COLOR_3;

        Textbox borderCol4 = ((DropDownTab.LabeledTextbox) rendererSettings.tabElements.get(19)).textbox;
        String borderColor4 = setTextboxValueString(borderCol4, Utils.toHexColor(Const.BORDER_COLOR_4));
        if(borderColor4 != null)
            try{Const.BORDER_COLOR_4 = Utils.parseHexColor(borderColor4); mainView.borders3.material.setColor(Const.BORDER_COLOR_4);}catch (Exception e){}
        borderCol4.textColor = Const.BORDER_COLOR_4;

        Textbox earthCol = ((DropDownTab.LabeledTextbox) rendererSettings.tabElements.get(21)).textbox;
        String earthColor = setTextboxValueString(earthCol, Utils.toHexColor(Const.EARTH_COLOR));
        if(earthColor != null)
            try{Const.EARTH_COLOR = Utils.parseHexColor(earthColor); mainView.earth.material.setOverlayColor(Const.EARTH_COLOR);}catch (Exception e){}
        earthCol.textColor = Const.EARTH_COLOR;

        Textbox podCol = ((DropDownTab.LabeledTextbox) rendererSettings.tabElements.get(23)).textbox;
        String podColor = setTextboxValueString(podCol, Utils.toHexColor(Const.POD_COLOR));
        if(podColor != null)
            try{Const.POD_COLOR = Utils.parseHexColor(podColor); mainView.pod.material.setOverlayColor(Const.POD_COLOR);}catch (Exception e){}
        podCol.textColor = Const.POD_COLOR;
    }

}
