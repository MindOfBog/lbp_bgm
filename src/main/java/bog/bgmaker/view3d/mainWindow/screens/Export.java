package bog.bgmaker.view3d.mainWindow.screens;

import bog.bgmaker.view3d.core.Model;
import bog.bgmaker.view3d.mainWindow.View3D;
import bog.bgmaker.view3d.managers.MouseInput;
import bog.bgmaker.view3d.renderer.gui.GuiScreen;
import bog.bgmaker.view3d.renderer.gui.elements.Button;
import bog.bgmaker.view3d.renderer.gui.elements.*;
import bog.bgmaker.view3d.renderer.gui.ingredients.LineStrip;
import bog.bgmaker.view3d.utils.Config;
import bog.bgmaker.view3d.utils.MousePicker;
import common.FileChooser;
import cwlib.types.Resource;
import cwlib.types.data.GUID;
import cwlib.types.mods.Mod;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.nio.file.Files;

/**
 * @author Bog
 */
public class Export extends GuiScreen {

    View3D mainView;

    public Export(View3D mainView)
    {
        super(mainView.renderer, mainView.loader, mainView.window);
        this.mainView = mainView;
        init();
    }

    Element exportHitbox;
    Textbox title;
    Textarea description;
    Textbox creator;
    Textbox icon;
    Radiobutton unrestricted;
    Radiobutton restrictedLevel;
    Radiobutton restrictedPod;
    Button planExport;
    Button modExport;

    Model outlineRect;

    public void init()
    {
        outlineRect = LineStrip.processVerts(LineStrip.getRectangle(new Vector2f(400, 223)), mainView.loader, mainView.window);

        exportHitbox = new Element() {

            @Override
            public void draw(MouseInput mouseInput, boolean overElement) {
                this.pos = new Vector2f(mainView.window.width / 2 - 200, mainView.window.height / 2 - 112);
                super.draw(mouseInput, overElement);
            }

        };
        exportHitbox.pos = new Vector2f(mainView.window.width / 2 - 200, mainView.window.height / 2 - 112);
        exportHitbox.size = new Vector2f(400, 223);
        exportHitbox.id = "exportHitbox";

        title = new Textbox("title", new Vector2f(mainView.window.width / 2 - 90, mainView.window.height / 2 - 107), new Vector2f(285, getFontHeight(10) + 4), 10, mainView.renderer, mainView.loader, mainView.window) {
            @Override
            public void draw(MouseInput mouseInput, boolean overOther) {
                this.pos = new Vector2f(mainView.window.width / 2 - 90, mainView.window.height / 2 - 107);
                super.draw(mouseInput, overOther);
            }
        };
        title.setText("Background");
        description = new Textarea("description", new Vector2f(mainView.window.width / 2 - 90, mainView.window.height / 2 - 98 + getFontHeight(10)), new Vector2f(285, getFontHeight(10) * 4 + 4), 10, mainView.renderer, mainView.loader, mainView.window) {
            @Override
            public void draw(MouseInput mouseInput, boolean overOther) {
                this.pos = new Vector2f(mainView.window.width / 2 - 90, mainView.window.height / 2 - 98 + getFontHeight(10));
                super.draw(mouseInput, overOther);
            }
        };
        creator = new Textbox("creator", new Vector2f(mainView.window.width / 2 - 90, mainView.window.height / 2 - 89 + getFontHeight(10) * 5), new Vector2f(285, getFontHeight(10) + 4), 10, mainView.renderer, mainView.loader, mainView.window) {
            @Override
            public void draw(MouseInput mouseInput, boolean overOther) {
                this.pos = new Vector2f(mainView.window.width / 2 - 90, mainView.window.height / 2 - 89 + getFontHeight(10) * 5);
                if(this.getText().length() > 16)
                    this.setText(this.getText().substring(0, 16));
                super.draw(mouseInput, overOther);
            }
        };
        creator.setText("MM_Studio");
        icon = new Textbox("icon", new Vector2f(mainView.window.width / 2 - 90, mainView.window.height / 2 - 80 + getFontHeight(10) * 6), new Vector2f(285, getFontHeight(10) + 4), 10, mainView.renderer, mainView.loader, mainView.window) {
            @Override
            public void draw(MouseInput mouseInput, boolean overOther) {
                this.pos = new Vector2f(mainView.window.width / 2 - 90, mainView.window.height / 2 - 80 + getFontHeight(10) * 6);
                super.draw(mouseInput, overOther);
            }
        };
        icon.setText("2551");
        unrestricted = new Radiobutton("unrestricted", "None (LBP1)", new Vector2f(mainView.window.width / 2 - 195, mainView.window.height / 2 - 66 + getFontHeight(10) * 8 + (getFontHeight(10) + 4) / 2 - getFontHeight(10) / 2), 10, mainView.renderer, mainView.loader, mainView.window) {
            @Override
            public void draw(MouseInput mouseInput, boolean overElement) {
                this.pos = new Vector2f(mainView.window.width / 2 - 195, mainView.window.height / 2 - 66 + getFontHeight(10) * 8 + (getFontHeight(10) + 4) / 2 - getFontHeight(10) / 2);
                super.draw(mouseInput, overElement);
            }

            @Override
            public void onClick(Vector2d pos, int button, int action, int mods, boolean overElement) {
                if (button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos)) {
                    this.isChecked = true;
                    restrictedLevel.isChecked = false;
                    restrictedPod.isChecked = false;
                }
            }
        }.checked();
        restrictedLevel = new Radiobutton("restrictedLevel", "Level (LBP2)", new Vector2f(0, mainView.window.height / 2 - 66 + getFontHeight(10) * 8 + (getFontHeight(10) + 4) / 2 - getFontHeight(10) / 2), 10, mainView.renderer, mainView.loader, mainView.window) {
            @Override
            public void draw(MouseInput mouseInput, boolean overElement) {
                this.pos = new Vector2f(mainView.window.width / 2 - this.size.x / 2, mainView.window.height / 2 - 66 + getFontHeight(10) * 8 + (getFontHeight(10) + 4) / 2 - getFontHeight(10) / 2);
                super.draw(mouseInput, overElement);
            }

            @Override
            public void onClick(Vector2d pos, int button, int action, int mods, boolean overElement) {
                if (button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos)) {
                    this.isChecked = true;
                    unrestricted.isChecked = false;
                    restrictedPod.isChecked = false;
                }
            }
        };
        restrictedPod = new Radiobutton("restrictedPod", "Pod (LBP2)", new Vector2f(0, mainView.window.height / 2 - 66 + getFontHeight(10) * 8 + (getFontHeight(10) + 4) / 2 - getFontHeight(10) / 2), 10, mainView.renderer, mainView.loader, mainView.window) {
            @Override
            public void draw(MouseInput mouseInput, boolean overElement) {
                this.pos = new Vector2f(mainView.window.width / 2 + 193 - this.size.x, mainView.window.height / 2 - 66 + getFontHeight(10) * 8 + (getFontHeight(10) + 4) / 2 - getFontHeight(10) / 2);
                super.draw(mouseInput, overElement);
            }

            @Override
            public void onClick(Vector2d pos, int button, int action, int mods, boolean overElement) {
                if (button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos)) {
                    this.isChecked = true;
                    unrestricted.isChecked = false;
                    restrictedLevel.isChecked = false;
                }
            }
        };
        planExport = new Button("planExport", "Export .PLAN", new Vector2f(mainView.window.width / 2 - 195, mainView.window.height / 2 - 59 + getFontHeight(10) * 9 + (getFontHeight(10) + 4) / 2 - getFontHeight(10) / 2), new Vector2f(192.5f, 28), 10, mainView.renderer, mainView.loader, mainView.window) {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if (button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)
                    try {
                        File file = FileChooser.openFile(title.getText(), "plan", true, false)[0];
                        Files.write(file.toPath(), Resource.compress(mainView.buildPlan().build()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }

            @Override
            public void draw(MouseInput mouseInput, boolean overOther) {
                this.pos = new Vector2f(mainView.window.width / 2 - 195, mainView.window.height / 2 - 59 + getFontHeight(10) * 9 + (getFontHeight(10) + 4) / 2 - getFontHeight(10) / 2);
                super.draw(mouseInput, overOther);
            }
        };
        modExport = new Button("modExport", "Export .MOD", new Vector2f(mainView.window.width / 2 + 3f, mainView.window.height / 2 - 59 + getFontHeight(10) * 9 + (getFontHeight(10) + 4) / 2 - getFontHeight(10) / 2), new Vector2f(192.5f, 28), 10, mainView.renderer, mainView.loader, mainView.window) {

            @Override
            public void clickedButton(int button, int action, int mods) {
                if (button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)
                    try {
                        Mod mod = new Mod();
                        String title1 = title.getText();
                        mod.add("lbp_bgm/" + title1 + ".plan", Resource.compress(mainView.buildPlan().build()), new GUID(Math.round(Math.random() * 1000000)));
                        File file = FileChooser.openFile(title1, "mod", true, false)[0];
                        mod.save(file);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }

            @Override
            public void draw(MouseInput mouseInput, boolean overOther) {
                this.pos = new Vector2f(mainView.window.width / 2 + 3f, mainView.window.height / 2 - 59 + getFontHeight(10) * 9 + (getFontHeight(10) + 4) / 2 - getFontHeight(10) / 2);
                super.draw(mouseInput, overOther);
            }
        };

        this.guiElements.add(exportHitbox);
        this.guiElements.add(title);
        this.guiElements.add(description);
        this.guiElements.add(creator);
        this.guiElements.add(icon);
        this.guiElements.add(unrestricted);
        this.guiElements.add(restrictedLevel);
        this.guiElements.add(restrictedPod);
        this.guiElements.add(planExport);
        this.guiElements.add(modExport);
    }

    @Override
    public void draw(MouseInput mouseInput) {

        mainView.renderer.doBlur(1.0025f, mainView.window.width / 2 - 200, mainView.window.height / 2 - 112, 400, 223);
        mainView.renderer.doBlur(2, mainView.window.width / 2 - 200, mainView.window.height / 2 - 112, 400, 223);
        mainView.renderer.doBlur(3, mainView.window.width / 2 - 200, mainView.window.height / 2 - 112, 400, 223);
        mainView.renderer.doBlur(2, mainView.window.width / 2 - 200, mainView.window.height / 2 - 112, 400, 223);
        mainView.renderer.doBlur(1.5f, mainView.window.width / 2 - 200, mainView.window.height / 2 - 112, 400, 223);
        mainView.renderer.doBlur(1.25f, mainView.window.width / 2 - 200, mainView.window.height / 2 - 112, 400, 223);

        mainView.renderer.drawRect(mainView.window.width / 2 - 200, mainView.window.height / 2 - 112, 400, 223, Config.PRIMARY_COLOR);
        mainView.renderer.drawRectOutline(new Vector2f(mainView.window.width / 2 - 200, mainView.window.height / 2 - 112), outlineRect, Config.SECONDARY_COLOR, false);
        mainView.renderer.drawString("Title:", Config.FONT_COLOR, mainView.window.width / 2 - 193, mainView.window.height / 2 - 107 + (getFontHeight(10) + 4) / 2 - getFontHeight(10) / 2, 10);
        mainView.renderer.drawString("Description:", Config.FONT_COLOR, mainView.window.width / 2 - 193, mainView.window.height / 2 - 102 + getFontHeight(10) + (getFontHeight(10) * 4 + 4) / 2 - getFontHeight(10) / 2, 10);
        mainView.renderer.drawString("Creator:", Config.FONT_COLOR, mainView.window.width / 2 - 193, mainView.window.height / 2 - 89 + getFontHeight(10) * 5 + (getFontHeight(10) + 4) / 2 - getFontHeight(10) / 2, 10);
        mainView.renderer.drawString("Icon:", Config.FONT_COLOR, mainView.window.width / 2 - 193, mainView.window.height / 2 - 81 + getFontHeight(10) * 6 + (getFontHeight(10) + 4) / 2 - getFontHeight(10) / 2, 10);
        mainView.renderer.drawString("Restriction:", Config.FONT_COLOR, mainView.window.width / 2 - 193, mainView.window.height / 2 - 73 + getFontHeight(10) * 7 + (getFontHeight(10) + 4) / 2 - getFontHeight(10) / 2, 10);
        super.draw(mouseInput);
    }

    public void resize()
    {
        if(this.outlineRect != null)
            outlineRect.cleanup(creator.loader);
        outlineRect = LineStrip.processVerts(LineStrip.getRectangle(new Vector2f(400, 223)), mainView.loader, mainView.window);
    }
}
