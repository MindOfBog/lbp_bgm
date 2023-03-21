package bog.bgmaker.view3d.mainWindow.screens;

import bog.bgmaker.Main;
import bog.bgmaker.view3d.mainWindow.View3D;
import bog.bgmaker.view3d.renderer.gui.GuiKeybind;
import bog.bgmaker.view3d.renderer.gui.GuiScreen;
import bog.bgmaker.view3d.renderer.gui.elements.Button;
import bog.bgmaker.view3d.renderer.gui.elements.DropDownTab;
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

    public void init()
    {
        DropDownTab rendererSettings = new DropDownTab("rendererSettings", "Renderer Settings", new Vector2f(7, 19 + getFontHeight(10)), new Vector2f(200, getFontHeight(10) + 4), 10, mainView.renderer, mainView.loader, mainView.window).closed();
        rendererSettings.addCheckbox("culling", "No culling");
        rendererSettings.addLabeledSlider("fov", "FOV:  ", 63, 20, 175);
        rendererSettings.addLabeledTextbox("fps", "FPS:  ", true, false, false, "120");
        rendererSettings.addLabeledTextbox("moveSpeed", "Move speed:  ", true, false, false, "1500");
        rendererSettings.addLabeledTextbox("sensitivity", "Sensitivity:  ", true, false, false, "0.2");
        rendererSettings.addLabeledTextbox("zNear", "Z Near:  ", true, false, false, Float.toString(Const.Z_NEAR));
        rendererSettings.addLabeledTextbox("zFar", "Z Far:  ", true, false, false, Float.toString(Const.Z_FAR));

        DropDownTab controls = new DropDownTab("controls", "Controls", new Vector2f(7, 19 + getFontHeight(10) + 7 + rendererSettings.getFullHeight()), new Vector2f(200, getFontHeight(10) + 4), 10, mainView.renderer, mainView.loader, mainView.window).closed();
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
            DropDownTab debug = new DropDownTab("debug", "Debug", new Vector2f(214, 7), new Vector2f(200, getFontHeight(10) + 4), 10, mainView.renderer, mainView.loader, mainView.window).closed();
            debug.addCheckbox("aabb", "AABB");
            debug.addCheckbox("glScissorTest", "GL Scissor");
            debug.addCheckbox("vaoCount", "VAO Count");
            debug.addCheckbox("vboCount", "VBO Count");
            debug.addCheckbox("textureCount", "Texture Count");
            this.guiElements.add(debug);
        }
    }

}
