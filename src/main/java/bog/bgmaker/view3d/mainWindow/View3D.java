package bog.bgmaker.view3d.mainWindow;

import bog.bgmaker.Main;
import bog.bgmaker.view3d.Camera;
import bog.bgmaker.view3d.ILogic;
import bog.bgmaker.view3d.ObjectLoader;
import bog.bgmaker.view3d.core.*;
import bog.bgmaker.view3d.core.types.Entity;
import bog.bgmaker.view3d.core.types.MaterialPrimitive;
import bog.bgmaker.view3d.core.types.Mesh;
import bog.bgmaker.view3d.core.types.WorldAudio;
import bog.bgmaker.view3d.mainWindow.screens.*;
import bog.bgmaker.view3d.managers.EngineMan;
import bog.bgmaker.view3d.managers.MouseInput;
import bog.bgmaker.view3d.managers.RenderMan;
import bog.bgmaker.view3d.managers.WindowMan;
import bog.bgmaker.view3d.renderer.gui.GuiScreen;
import bog.bgmaker.view3d.renderer.gui.elements.Button;
import bog.bgmaker.view3d.renderer.gui.elements.Checkbox;
import bog.bgmaker.view3d.renderer.gui.elements.*;
import bog.bgmaker.view3d.renderer.gui.font.FontRenderer;
import bog.bgmaker.view3d.renderer.gui.ingredients.*;
import bog.bgmaker.view3d.renderer.gui.ingredients.Triangle;
import bog.bgmaker.view3d.utils.CWLibUtils.LevelSettingsUtils;
import bog.bgmaker.view3d.utils.CWLibUtils.SkeletonUtils;
import bog.bgmaker.view3d.utils.Config;
import bog.bgmaker.view3d.utils.MousePicker;
import bog.bgmaker.view3d.utils.Utils;
import cwlib.enums.*;
import cwlib.resources.RMesh;
import cwlib.resources.RPlan;
import cwlib.structs.inventory.CreationHistory;
import cwlib.structs.inventory.InventoryItemDetails;
import cwlib.structs.inventory.UserCreatedDetails;
import cwlib.structs.things.Thing;
import cwlib.structs.things.components.LevelSettings;
import cwlib.structs.things.parts.*;
import cwlib.types.archives.FileArchive;
import cwlib.types.data.NetworkPlayerID;
import cwlib.types.data.ResourceDescriptor;
import cwlib.types.data.Revision;
import cwlib.types.data.SHA1;
import cwlib.types.databases.FileDBRow;
import cwlib.types.databases.FileEntry;
import cwlib.types.save.SaveEntry;
import org.joml.Math;
import org.joml.*;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.*;

/**
 * @author Bog
 */
public class View3D implements ILogic {

    public RenderMan renderer;
    public WindowMan window;
    public ObjectLoader loader;
    public ArrayList<Entity> entities;
    public ArrayList<LevelSettings> levelSettings;
    public ArrayList<Entity> BORDERS;
    public ArrayList<Entity> POD_EARTH;
    public Camera camera;
    Vector3f cameraInc;
    public int crosshair;
    public int sun;
    public int worldAudio;
    public int logo;
    public int modelIcon;
    public int materialIcon;
    public int lightIcon;
    public int audioIcon;

    public ArrayList<FileEntry> entries;

    GuiScreen currentScreen;
    public View3D(WindowMan window)
    {
        this.window = window;
        this.renderer = new RenderMan(this.window);
        this.loader = new ObjectLoader();
        this.camera = new Camera();
        this.camera.setPos(-15150, 4000, 4500);
        this.cameraInc = new Vector3f(0, 0, 0);
    }

    public long initMillis = 0;

    public Model borders, borders1, borders2, borders3, pod, earth, bone;

    @Override
    public void init() throws Exception {
        renderer.init(this.loader);
        FontRenderer.init(this.loader);
        Transformation3D.init(this.loader);
        LoadedData.init(this.loader);
        this.window.setIcon("/icon32.png");

        entities = new ArrayList<>();
        levelSettings = new ArrayList<>();
        BORDERS = new ArrayList<>();
        POD_EARTH = new ArrayList<>();
        entries = new ArrayList<>();

        for(LevelSettings ls : LevelSettingsUtils.getBlank1Preset())
            levelSettings.add(ls);

        initMillis = System.currentTimeMillis();

        int minF = GL11.GL_LINEAR_MIPMAP_NEAREST;
        int magF = GL11.GL_LINEAR;

        crosshair = loader.loadTexture(ImageIO.read(Main.class.getResourceAsStream("/textures/crosshair.png")), minF, magF);
        sun = loader.loadTexture(ImageIO.read(Main.class.getResourceAsStream("/textures/sun.png")), GL11.GL_NEAREST, magF);
        worldAudio = loader.loadTexture(ImageIO.read(Main.class.getResourceAsStream("/textures/world audio.png")), GL11.GL_NEAREST, magF);
        logo = loader.loadTexture(ImageIO.read(Main.class.getResourceAsStream("/icon.png")), minF, magF);
        modelIcon = loader.loadTexture(ImageIO.read(Main.class.getResourceAsStream("/textures/model.png")), minF, magF);
        materialIcon = loader.loadTexture(ImageIO.read(Main.class.getResourceAsStream("/textures/material.png")), minF, magF);
        lightIcon = loader.loadTexture(ImageIO.read(Main.class.getResourceAsStream("/textures/light.png")), minF, magF);
        audioIcon = loader.loadTexture(ImageIO.read(Main.class.getResourceAsStream("/textures/audio.png")), minF, magF);

        createUI();

        borders = loader.loadOBJModel("/models/border.obj");
        borders.material = new Material(Config.BORDER_COLOR_1, 0f).disableCulling(true);
        BORDERS.add(new Entity(borders, new Vector3f(21219f, 1557f, 10f), new Vector3f(0, 0, 0), new Vector3f(1f, 1f, 1f), loader));
        BORDERS.add(new Entity(borders, new Vector3f(21219f, 1557f, -390f), new Vector3f(0, 0, 0), new Vector3f(1f, 1f, 1f), loader));

        borders1 = new Model(borders);
        borders1.material = new Material(Config.BORDER_COLOR_2, 0f).disableCulling(true);
        BORDERS.add(new Entity(borders1, new Vector3f(21219f, 1557f, -190f), new Vector3f(0, 0, 0), new Vector3f(1f, 1f, 1f), loader));

        borders2 = new Model(borders);
        borders2.material = new Material(Config.BORDER_COLOR_3, 0f).disableCulling(true);
        for(int layer = 0; layer < 7; layer++)
            BORDERS.add(new Entity(borders2, new Vector3f(21219f, 1557f, -590f + -400f * layer), new Vector3f(0, 0, 0), new Vector3f(1f, 1f, 1f), loader));

        borders3 = new Model(borders);
        borders3.material = new Material(Config.BORDER_COLOR_4, 0f).disableCulling(true);
        for(int layer = 0; layer < 6; layer++)
            BORDERS.add(new Entity(borders3, new Vector3f(21219f, 1557f, -790f + -400f * layer), new Vector3f(0, 0, 0), new Vector3f(1f, 1f, 1f), loader));

        bone = loader.loadOBJModel("/models/bone.obj");
        bone.material = new Material(Color.blue, 0f);

        pod = loader.loadOBJModel("/models/pod.obj");
        pod.material = new Material(new Texture[]{new Texture(loader.loadTexture(ImageIO.read(Main.class.getResourceAsStream("/textures/pod.png")), GL11.GL_LINEAR_MIPMAP_LINEAR, GL11.GL_LINEAR))});
        pod.material.overlayColor = new Vector4f(Config.POD_COLOR.getRed() / 255f, Config.POD_COLOR.getGreen() / 255f, Config.POD_COLOR.getBlue() / 255f, Config.POD_COLOR.getAlpha() / 255f);
        POD_EARTH.add(new Entity(pod, new Vector3f(25.0f, 260.0f, 13490.0f), new Vector3f(-105.0f, 0.0f, 0.0f), new Vector3f(1f, 1f, 1f), loader));
        earth = loader.loadOBJModel("/models/earth.obj");
        earth.material = new Material(new Texture[]{new Texture(loader.loadTexture(ImageIO.read(Main.class.getResourceAsStream("/textures/earth.png")), GL11.GL_LINEAR_MIPMAP_LINEAR, GL11.GL_LINEAR))});
        earth.material.overlayColor = new Vector4f(Config.EARTH_COLOR.getRed() / 255f, Config.EARTH_COLOR.getGreen() / 255f, Config.EARTH_COLOR.getBlue() / 255f, Config.EARTH_COLOR.getAlpha() / 255f);
        POD_EARTH.add(new Entity(earth, new Vector3f(30.71f, 60.38f, 243.31f), new Vector3f(0, 0, 0), new Vector3f(1.5f, 1.5f, 1.5f), loader));
    }

    public void addEntity(Entity e)
    {
        entities.add(e);
    }
    
    private int[] prevSelection = new int[0];
    public MousePicker mousePicker;
    public MousePicker centerPicker;
    public MouseInput mouseInput;

    @Override
    public void update(MouseInput mouseInput, MousePicker mousePicker) {
        boolean elementFocused = currentScreen == null ? false : currentScreen.elementFocused();
        boolean overElement = currentScreen == null ? false : currentScreen.isMouseOverElement(mouseInput);

        if(!elementFocused && !overElement)
            camera.movePos((cameraInc.x * Config.CAMERA_MOVE_SPEED) / (EngineMan.fps == 0 ? 60 : EngineMan.fps), (cameraInc.y * Config.CAMERA_MOVE_SPEED) / (EngineMan.fps == 0 ? 60 : EngineMan.fps), (cameraInc.z * Config.CAMERA_MOVE_SPEED) / (EngineMan.fps == 0 ? 60 : EngineMan.fps));

        this.mouseInput = mouseInput;

        boolean hasSelection = false;
        int selectedAmount = 0;

        for(Entity entity : entities)
            if(entity.selected)
            {
                hasSelection = true;
                selectedAmount++;
            }

        try {
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(this.mousePicker == null)
            this.mousePicker = mousePicker;
        this.mousePicker.update(camera);

        if(this.centerPicker == null)
            this.centerPicker = new MousePicker(null, window);
        this.centerPicker.update(camera, window.width / 2, window.height / 2);

        Vector3f screenPos = camera.worldToScreenPointF(getSelectedPosition(), window);

        if(mouseInput.middleButtonPress && !overElement && introPlayed)
        {
            if(camera.getWrappedRotation().x > -90 && camera.getWrappedRotation().x < 90)
                camera.moveRot(mouseInput.displayVec.x * Config.MOUSE_SENS, mouseInput.displayVec.y * Config.MOUSE_SENS, 0);

            mouseInput.displayVec.set(0, 0);
        }

        while(camera.getWrappedRotation().x <= -90)
            camera.moveRot(0.01f, 0, 0);
        while(camera.getWrappedRotation().x >= 90)
            camera.moveRot(-0.01f, 0, 0);

        Checkbox level = (Checkbox) ((ElementEditing) ElementEditing).helpers.tabElements.get(0);
        Checkbox pod = (Checkbox) ((ElementEditing) ElementEditing).helpers.tabElements.get(1);

        if(level.isChecked)
            for(Entity entity : BORDERS)
                renderer.processEntity(entity);

        if(pod.isChecked)
            for(Entity entity : POD_EARTH)
                renderer.processEntity(entity);

        Vector3f pos = new Vector3f(0, 0, 0);
        try {
            pos = new Vector3f(levelSettings.get(selectedPresetIndex).sunPosition).mul(levelSettings.get(selectedPresetIndex).sunPositionScale);

            Vector4f sunColor = levelSettings.get(selectedPresetIndex).sunColor;
//            renderer.processDirectionalLight(new DirectionalLight(new Vector3f(sunColor.x, sunColor.y, sunColor.z), new Vector3f(pos), levelSettings.get(selectedPresetIndex).sunMultiplier));
//            TODO renderer.processPointLight(new PointLight(
//                    new Vector3f(sunColor.x, sunColor.y, sunColor.z),
//                    pos,
//                    levelSettings.get(selectedPresetIndex).sunMultiplier,
//                    1,
//                    0,
//                    0
//            ));
        }catch (Exception e){}

        Vector3f sunPos = camera.worldToScreenPointF(pos, window);
        if(sunPos.z == 0)
            drawImageStatic(sun, (int) (sunPos.x - 17.5f), (int) (sunPos.y - 17.5f), 35, 35);

        for(int ent = 0; ent < entities.size(); ent++)
        {
            Entity entity = entities.get(ent);
            if(entity.getType() == 0 || entity.getType() == 1)
            {
                renderer.processEntity(entity);
//               TODO if(entity.getType() == 0)
//                {
//                    Bone[] skeleton = ((Mesh)entity).skeleton;
//                    if(skeleton != null)
//                        for(Bone bone : skeleton)
//                        {
//                            Matrix4f transform = new Matrix4f().identity().translate(new Matrix4f(entity.transformation).mul(skeleton[0].invSkinPoseMatrix).mul(bone.skinPoseMatrix).mul(bone.offset).getTranslation(new Vector3f()));
//                            this.renderer.processThroughWallEntity(new Entity(this.bone, transform, loader));
//                        }
//                }
            }
            else if(entity.getType() == 2)
            {

            }
            else if(entity.getType() == 3)
            {
                Vector3f audioPos = camera.worldToScreenPointF(entity.transformation.getTranslation(new Vector3f()), window);
                //TODO if(audioPos.z == 0)
                //    renderer.processGuiElement(new Quad(loader, worldAudio, new Vector2f((int)(audioPos.x - 17.5f), (int)(audioPos.y - 17.5f)), new Vector2f(35, 35), false).staticTexture());
            }
        }
        drawUI(mouseInput);
    }

    public void secondaryThread()
    {
        if(currentScreen != null)
            currentScreen.secondaryThread();
    }

    @Override
    public void onMouseClick(MouseInput mouseInput, int button, int action, int mods) throws Exception {

        if(!introPlayed)
            return;

        currentScreen.onClick(mouseInput.currentPos, button, action, mods);
    }

    public int KEY_FORWARD = GLFW.GLFW_KEY_W;
    public int KEY_LEFT = GLFW.GLFW_KEY_A;
    public int KEY_BACK = GLFW.GLFW_KEY_S;
    public int KEY_RIGHT = GLFW.GLFW_KEY_D;
    public int KEY_UP = GLFW.GLFW_KEY_SPACE;
    public int KEY_DOWN = GLFW.GLFW_KEY_LEFT_SHIFT;

    @Override
    public void onKey(int key, int scancode, int action, int mods)
    {
        if(!introPlayed)
            return;

        boolean elementFocused = currentScreen.onKey(key, scancode, action, mods);

        if(key != GLFW.GLFW_KEY_LEFT_CONTROL && !window.isKeyPressed(GLFW.GLFW_KEY_LEFT_CONTROL))
        {
            if (action == 1)
            {
                if(key == KEY_FORWARD)
                    cameraInc.z = window.isKeyPressed(KEY_BACK) ? 0 : -1;
                if(key == KEY_LEFT)
                    cameraInc.x = window.isKeyPressed(KEY_RIGHT) ? 0 : -1;
                if(key == KEY_BACK)
                    cameraInc.z = window.isKeyPressed(KEY_FORWARD) ? 0 : 1;
                if(key == KEY_RIGHT)
                    cameraInc.x = window.isKeyPressed(KEY_LEFT) ? 0 : 1;
                if(key == KEY_UP)
                    cameraInc.y = window.isKeyPressed(KEY_DOWN) ? 0 : 1;
                if(key == KEY_DOWN)
                    cameraInc.y = window.isKeyPressed(KEY_UP) ? 0 : -1;
                if((key == GLFW.GLFW_KEY_DELETE || key == GLFW.GLFW_KEY_BACKSPACE) && !elementFocused)
                {
                    for (int i = entities.size() - 1; i >= 0; i--)
                        if (entities.get(i).selected)
                            entities.remove(i);
                }
            }
            else if (action == 0)
            {
                if(key == KEY_FORWARD)
                    cameraInc.z = window.isKeyPressed(KEY_BACK) ? 1 : 0;
                if(key == KEY_BACK)
                    cameraInc.z = window.isKeyPressed(KEY_FORWARD) ? -1 : 0;
                if(key == KEY_LEFT)
                    cameraInc.x = window.isKeyPressed(KEY_RIGHT) ? 1 : 0;
                if(key == KEY_RIGHT)
                    cameraInc.x = window.isKeyPressed(KEY_LEFT) ? -1 : 0;
                if(key == KEY_UP)
                    cameraInc.y = window.isKeyPressed(KEY_DOWN) ? -1 : 0;
                if(key == KEY_DOWN)
                    cameraInc.y = window.isKeyPressed(KEY_UP) ? 1 : 0;
            }
        }
        else if(key == GLFW.GLFW_KEY_LEFT_CONTROL && action == 0)
        {
            cameraInc.x = window.isKeyPressed(KEY_RIGHT) ? 1 : window.isKeyPressed(KEY_LEFT) ? -1 : 0;
            cameraInc.y = window.isKeyPressed(KEY_DOWN) ? -1 : window.isKeyPressed(KEY_UP) ? 1 : 0;
            cameraInc.z = window.isKeyPressed(KEY_BACK) ? 1 : window.isKeyPressed(KEY_FORWARD) ? -1 : 0;
        }
        else
        {
            cameraInc.x = 0;
            cameraInc.y = 0;
            cameraInc.z = 0;
        }

        if(action == 1 && mods == GLFW.GLFW_MOD_CONTROL && !elementFocused)
        {
            if(key == GLFW.GLFW_KEY_C)
                copySelected();
            if(key == GLFW.GLFW_KEY_V)
                pasteClipboard();
        }

        if(key == GLFW.GLFW_KEY_A && action == GLFW.GLFW_PRESS && mods == GLFW.GLFW_MOD_CONTROL && !elementFocused && currentScreen == ElementEditing)
        {
            for(Entity e : entities)
                e.selected = false;
            for(Object i : ((ElementEditing)ElementEditing).loadedEntities.indexes)
                entities.get((int)i).selected = true;
        }
    }

    @Override
    public void onChar(int codePoint, int modifiers) {
        if(!introPlayed)
            return;

        currentScreen.onChar(codePoint, modifiers);
    }

    @Override
    public void onMouseScroll(double xOffset, double yOffset)
    {
        if(!introPlayed)
            return;

        currentScreen.onMouseScroll(mouseInput.currentPos, xOffset, yOffset);

        if(!currentScreen.isMouseOverElement(mouseInput) && !currentScreen.isElementFocused())
        {
            if(Config.CAMERA_MOVE_SPEED == 0 && yOffset > 0)
                Config.CAMERA_MOVE_SPEED = 1;
            Config.CAMERA_MOVE_SPEED = yOffset > 0 ? Config.CAMERA_MOVE_SPEED * 1.2f : Config.CAMERA_MOVE_SPEED * 0.8f;
            Config.CAMERA_MOVE_SPEED = yOffset > 0 ? Math.ceil(Config.CAMERA_MOVE_SPEED) : Math.floor(Config.CAMERA_MOVE_SPEED);
            DropDownTab settings = ((Settings) Settings).rendererSettings;
            Textbox speed = ((DropDownTab.LabeledTextbox) settings.tabElements.get(3)).textbox;
            speed.text(Float.toString(Config.CAMERA_MOVE_SPEED));
        }
    }

    @Override
    public void render() {
        Vector4f color = new Vector4f(0f, 0f, 0f, 0f);

        try{
            color = levelSettings.get(selectedPresetIndex).fogColor;
        }catch (Exception e){}

        color.x = Math.clamp(0, 1, color.x);
        color.y = Math.clamp(0, 1, color.y);
        color.z = Math.clamp(0, 1, color.z);

        GL11.glClearColor(color.x, color.y, color.z, 1f);

        if(window.resize)
        {
            renderer.entityRenderer.resize();
            window.resize = false;
        }

        renderer.render(camera, mouseInput);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        loader.cleanup();
    }

    public GuiScreen ElementEditing;
    public GuiScreen LevelSettingsEditing;
    public GuiScreen Export;
    public GuiScreen Settings;
    public GuiScreen MaterialEditing;
    public int selectedPresetIndex = 1;
    private void createUI() {

        Button elementEditing = new Button("elementEditing", "Scene", new Vector2f(164, 7), new Vector2f(150, 25), 10, renderer, loader, window) {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)setCurrentScreen(ElementEditing);
            }

            @Override
            public void draw(MouseInput mouseInput, boolean overOther) {
                super.draw(mouseInput, overOther);
            }
        };
        Button levelSettingsEditing = new Button("levelSettingsEditing", "Global Settings", new Vector2f(321, 7), new Vector2f(150, 25), 10, renderer, loader, window) {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)setCurrentScreen(LevelSettingsEditing);
            }

            @Override
            public void draw(MouseInput mouseInput, boolean overOther) {
                super.draw(mouseInput, overOther);
            }
        };
        Button export = new Button("export", "Export", new Vector2f(478, 7), new Vector2f(150, 25), 10, renderer, loader, window) {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)setCurrentScreen(Export);
            }

            @Override
            public void draw(MouseInput mouseInput, boolean overOther) {
                super.draw(mouseInput, overOther);
            }
        };
        Button settingss = new Button("settingss", "Settings", new Vector2f(7, window.height - 32), new Vector2f(150, 25), 10, renderer, loader, window) {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)setCurrentScreen(Settings);
            }

            @Override
            public void draw(MouseInput mouseInput, boolean overOther) {
                this.pos = new Vector2f(7, window.height - 32);
                super.draw(mouseInput, overOther);
            }
        };

        ElementEditing = new ElementEditing(this);

        ElementEditing.guiElements.add(elementEditing);
        ElementEditing.guiElements.add(levelSettingsEditing);
        ElementEditing.guiElements.add(export);
        ElementEditing.guiElements.add(settingss);

        LevelSettingsEditing = new LevelSettingsEditing(this);

        LevelSettingsEditing.guiElements.add(elementEditing);
        LevelSettingsEditing.guiElements.add(levelSettingsEditing);
        LevelSettingsEditing.guiElements.add(export);
        LevelSettingsEditing.guiElements.add(settingss);

        Export = new Export(this);

        Export.guiElements.add(elementEditing);
        Export.guiElements.add(levelSettingsEditing);
        Export.guiElements.add(export);
        Export.guiElements.add(settingss);

        Settings = new Settings(this);

        Settings.guiElements.add(elementEditing);
        Settings.guiElements.add(levelSettingsEditing);
        Settings.guiElements.add(export);
        Settings.guiElements.add(settingss);

        MaterialEditing = new MaterialEditing(this);

        setCurrentScreen(ElementEditing);
    }

    public boolean introPlayed = false;


    private void drawUI(MouseInput mouseInput) {

        drawRect(7, 7, 150, 25, Config.INTERFACE_PRIMARY_COLOR);
        drawRectOutline(7, 7, 150, 25, Config.INTERFACE_PRIMARY_COLOR2, false);
        drawString("FPS: " + EngineMan.avgFPS, Config.FONT_COLOR, 85 - (getStringWidth("FPS: " + EngineMan.avgFPS, 10) / 2), 12, 10);

        if(Main.debug)
        {
            boolean scissor = ((Checkbox) ((Settings) Settings).debug.tabElements.get(1)).isChecked;
            boolean vao = ((Checkbox) ((Settings) Settings).debug.tabElements.get(2)).isChecked;
            boolean vbo = ((Checkbox) ((Settings) Settings).debug.tabElements.get(3)).isChecked;
            boolean tex = ((Checkbox) ((Settings) Settings).debug.tabElements.get(4)).isChecked;

            int l = 1;

            if (vao)
            {
                drawRect(10 - 3, 10 - 3 + ((getFontHeight(10) + 2) + 3) * l, getStringWidth("VAOs: " + loader.vaos.size(), 10) + 6, (getFontHeight(10) + 2) + 3, new Color(0f, 0f, 0f, 0.5f));
                drawString("VAOs: " + loader.vaos.size(), Config.FONT_COLOR, 10, 10 + ((getFontHeight(10) + 2) + 3) * l, 10);
                l++;
            }
            if (vbo)
            {
                drawRect(10 - 3, 10 - 3 + ((getFontHeight(10) + 2) + 3) * l, getStringWidth("VBOs: " + loader.vbos.size(), 10) + 6, (getFontHeight(10) + 2) + 3, new Color(0f, 0f, 0f, 0.5f));
                drawString("VBOs: " + loader.vbos.size(), Config.FONT_COLOR, 10, 10 + ((getFontHeight(10) + 2) + 3) * l, 10);
                l++;
            }
            if (tex)
            {
                drawRect(10 - 3, 10 - 3 + ((getFontHeight(10) + 2) + 3) * l, getStringWidth("Textures: " + loader.textures.size(), 10) + 6, (getFontHeight(10) + 2) + 3, new Color(0f, 0f, 0f, 0.5f));
                drawString("Textures: " + loader.textures.size(), Config.FONT_COLOR, 10, 10 + ((getFontHeight(10) + 2) + 3) * l, 10);
                l++;
            }

            //GLScissor Test
            if (scissor)
            {
                startScissor(20, 50, 120 - 20, 150 - 50);
                drawRect(0, 0, window.width, window.height, Color.red);
                startScissor(30, 60, 110 - 30, 140 - 60);
                drawRect(0, 0, window.width, window.height, Color.green);
                startScissor(40, 70, 100 - 40, 130 - 70);
                drawRect(0, 0, window.width, window.height, Color.blue);

                startScissor(50, 80, 60 - 50, 90 - 80);
                drawRect(0, 0, window.width, window.height, Color.cyan);
                endScissor();

                startScissor(70, 80, 80 - 70, 90 - 80);
                drawRect(0, 0, window.width, window.height, Color.orange);
                endScissor();

                endScissor();
                endScissor();
                endScissor();
            }
        }

        Vector2d prev = mouseInput.currentPos;
        if(!introPlayed)
            mouseInput.currentPos = new Vector2d();
        if(currentScreen != null)
            currentScreen.draw(mouseInput);

        if(!introPlayed)
        {
            mouseInput.currentPos = prev;
            float out = 3f - (System.currentTimeMillis() - initMillis) / 1300f;
            float in = Math.clamp(0f, 1f, out - 1.5f);
            out = Math.clamp(0f, 1f, out);

            if (out != 0) {
                drawRect(0, 0, window.width, window.height, new Color(1f, 1f, 1f, out));
                drawImageStatic(this.logo, window.width / 2 - 461 / 2, window.height / 2 - 461 / 2, 461, 461);
                drawRect(0, 0, window.width, window.height, new Color(1f, 1f, 1f, in));
            } else introPlayed = true;
        }
    }

    private Vector2f setTextboxValueFloat(Textbox box, float value)
    {
        if(box != null)
        {
            if (!box.isFocused())
            {
                String v = Float.toString(value);
                box.setText(v.equalsIgnoreCase("nan") ? "" : v);
            }
            else
                try
                {
                    return new Vector2f(Float.parseFloat(box.getText()), 1);
                }
                catch (Exception e)
                {
                    return new Vector2f(0, 1);
                }
        }

        return new Vector2f(0, 0);
    }

    private String setTextboxValueString(Textbox box, String value)
    {
        if(box != null)
        {
            if (!box.isFocused())
            {
                box.setText(value == null ? "" : value);
            }
            else
                try
                {
                    return box.getText();
                }
                catch (Exception e)
                {
                    return null;
                }
        }

        return null;
    }

    private void drawLine(Vector2i pos1, Vector2i pos2, boolean smooth)
    {
        renderer.processGuiElement(new Line(pos1, pos2, loader, window, smooth, false));
    }

    private void drawLine(Vector2i pos1, Vector2i pos2, boolean smooth, Color color)
    {
        renderer.processGuiElement(new Line(pos1, pos2, color, loader, window, smooth,false));
    }

    private void drawImage(String path, float x, float y, float width, float height)
    {
        renderer.processGuiElement(new Quad(loader, path, new Vector2f(x, y), new Vector2f(width, height), false));
    }

    private void drawImage(BufferedImage image, float x, float y, float width, float height)
    {
        renderer.processGuiElement(new Quad(loader, image, new Vector2f(x, y), new Vector2f(width, height), false));
    }

    private void drawImage(int image, float x, float y, float width, float height)
    {
        renderer.processGuiElement(new Quad(loader, image, new Vector2f(x, y), new Vector2f(width, height), false));
    }

    private void drawImageStatic(int image, float x, float y, float width, float height)
    {
        renderer.processGuiElement(new Quad(loader, image, new Vector2f(x, y), new Vector2f(width, height), false).staticTexture());
    }

    private void drawRect(int x, int y, int width, int height, Color color)
    {
        renderer.processGuiElement(new Quad(loader, color, new Vector2f(x, y), new Vector2f(width, height), false));
    }

    private void drawRectOutline(int x, int y, int width, int height, Color color, boolean smooth)
    {
        renderer.processGuiElement(LineStrip.lineRectangle(new Vector2f(x, y), new Vector2f(width, height), color, loader, window, smooth, false));
    }

    private void drawRectOutline(int x, int y, int width, int height, Color color, boolean smooth, int openSide)
    {
        renderer.processGuiElement(LineStrip.lineRectangle(new Vector2f(x, y), new Vector2f(width, height), color, loader, window, smooth, false, openSide));
    }

    private void drawTriangle(Vector2f p1, Vector2f p2, Vector2f p3, Color color)
    {
        renderer.processGuiElement(new Triangle(loader, window, color, p1, p2, p3, false));
    }

    private void drawCircle(Vector2f center, float radius, int tris, Color color)
    {
        renderer.processGuiElement(new Circle(loader, window, color, center, radius, tris, false));
    }

    private void drawString(String text, Color color, int x, int y, int size)
    {
        FontRenderer.drawString(renderer, text, x, y, size, color, 0, text.length());
    }

    private void startScissor(Vector2i pos, Vector2i size)
    {
        renderer.processGuiElement(Scissor.start(pos, size));
    }

    private void startScissor(int x, int y, int width, int height)
    {
        renderer.processGuiElement(Scissor.start(new Vector2i(x, y), new Vector2i(width, height)));
    }

    private void endScissor()
    {
        renderer.processGuiElement(Scissor.end());
    }

    private int getStringWidth(String text, int size)
    {
        return (int)FontRenderer.getStringWidth(text, size);
    }

    private int getFontHeight(int size)
    {
        return (int)FontRenderer.getFontHeight(size);
    }

    public boolean legacyFileDialogue()
    {
        return ((Checkbox)((ElementEditing)ElementEditing).fileLoading.tabElements.get(0)).isChecked;
    }

    public void copySelected()
    {
        String copySelection = "";
        for(Entity entity : entities)
            if(entity.selected)
            {
                if(entity.getType() == 0)
                {
                    copySelection += "0" + (char)0x2b30 + entity.entityName + (char)0x2b30 + (((Mesh)entity).meshDescriptor.isGUID() ? ((Mesh)entity).meshDescriptor.getGUID().toString() : ((Mesh)entity).meshDescriptor.getSHA1().toString());
                    for(float m : entity.transformation.get(new float[16]))
                        copySelection += "," + m;
                    copySelection += ";";
                }
                else if(entity.getType() == 1)
                {
                    copySelection += "1" + (char)0x2b30 + entity.entityName + (char)0x2b30 + (((MaterialPrimitive)entity).gmat.isGUID() ? ((MaterialPrimitive)entity).gmat.getGUID().toString() : ((MaterialPrimitive)entity).gmat.getSHA1().toString()) + ",";
                    for(float m : entity.transformation.get(new float[16]))
                        copySelection += m + ",";
                    copySelection += (((MaterialPrimitive)entity).bev.isGUID() ? ((MaterialPrimitive)entity).bev.getGUID().toString() : ((MaterialPrimitive)entity).bev.getSHA1().toString()) + "," +
                            (((MaterialPrimitive)entity).mat.isGUID() ? ((MaterialPrimitive)entity).mat.getGUID().toString() : ((MaterialPrimitive)entity).mat.getSHA1().toString()) + "," +
                            ((MaterialPrimitive)entity).shape.thickness;

                    for(Vector3f vertex : ((MaterialPrimitive)entity).shape.polygon.vertices)
                        copySelection += ",v" + vertex.x + "," + vertex.y + "," + vertex.z;
                    for(int loop : ((MaterialPrimitive)entity).shape.polygon.loops)
                        copySelection += ",l" + loop;
                    copySelection += ";";
                }
            }

        if(copySelection.equalsIgnoreCase(""))
            return;

        try {
            String encrypted = Utils.encrypt(copySelection);

            StringSelection selection = new StringSelection("LBPBGM" + new String(encrypted));
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);
        }catch (Exception e){e.printStackTrace();}
    }

    public void pasteClipboard()
    {
        String clipboard = "";

        try
        {
            String cb = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);

            if(!cb.substring(0, 6).equalsIgnoreCase("LBPBGM"))
                return;

            clipboard = Utils.decrypt(cb.substring(6));
        }catch (Exception e){e.printStackTrace();}

        if(clipboard.equalsIgnoreCase(""))
            return;

        String[] objects = clipboard.split(";");

        for(Entity e : entities)
            e.selected = false;

        for(String object : objects) {
            String[] parts = object.split(String.valueOf((char) 0x2b30));
            String[] data = parts[2].split(",");

            if (parts[0].equalsIgnoreCase("0")) {
                String entityName = parts[1] + " Copy";
                ResourceDescriptor mesh = data[0].substring(0, 1).equalsIgnoreCase("g") ? new ResourceDescriptor(Long.parseLong(data[0].substring(1)), ResourceType.MESH) : new ResourceDescriptor(new SHA1(data[0]), ResourceType.MESH);

                float[] matrix = new float[16];

                for(int i = 1; i <= 16; i++)
                    matrix[i-1] = Float.parseFloat(data[i]);

                try {
                    addMesh(entityName, mesh,
                            new Matrix4f(matrix[0], matrix[1], matrix[2], matrix[3],
                                    matrix[4], matrix[5], matrix[6], matrix[7],
                                    matrix[8], matrix[9], matrix[10], matrix[11],
                                    matrix[12], matrix[13], matrix[14], matrix[15]));
                }catch (Exception e){e.printStackTrace();}
            }
            else if (parts[0].equalsIgnoreCase("1"))
            {
                String entityName = parts[1] + " Copy";
                ResourceDescriptor gmat = data[0].substring(0, 1).equalsIgnoreCase("g") ? new ResourceDescriptor(Long.parseLong(data[0].substring(1)), ResourceType.GFX_MATERIAL) : new ResourceDescriptor(new SHA1(data[0]), ResourceType.GFX_MATERIAL);

                float[] matrix = new float[16];

                for(int i = 1; i <= 16; i++)
                    matrix[i-1] = Float.parseFloat(data[i]);

                ResourceDescriptor bev = data[17].substring(0, 1).equalsIgnoreCase("g") ? new ResourceDescriptor(Long.parseLong(data[17].substring(1)), ResourceType.BEVEL) : new ResourceDescriptor(new SHA1(data[17]), ResourceType.BEVEL);
                ResourceDescriptor mat = data[18].substring(0, 1).equalsIgnoreCase("g") ? new ResourceDescriptor(Long.parseLong(data[18].substring(1)), ResourceType.MATERIAL) : new ResourceDescriptor(new SHA1(data[18]), ResourceType.MATERIAL);
                float thickness = Float.parseFloat(data[19]);

                PShape shape = new PShape();
                shape.material = mat;

                ArrayList<Vector3f> vertices = new ArrayList<>();
                ArrayList<Integer> loops = new ArrayList<>();

                for(int i = 0; i < data.length - 13; i++)
                {
                    String value = data[i + 13];
                    if(value.startsWith("v"))
                    {
                        vertices.add(new Vector3f(Float.parseFloat(value.substring(1)), Float.parseFloat(data[i + 13 + 1]), Float.parseFloat(data[i + 13 + 2])));
                        i = i + 2;
                    }
                    else if(value.startsWith("l"))
                        loops.add(Integer.parseInt(value.substring(1)));
                }

                shape.polygon.vertices = vertices.toArray(Vector3f[]::new);
                shape.polygon.loops = loops.stream().mapToInt(Integer::valueOf).toArray();
                shape.thickness = thickness;

                try {
                    addMaterial(entityName, shape, gmat, bev, mat,
                            new Matrix4f(matrix[0], matrix[1], matrix[2], matrix[3],
                            matrix[4], matrix[5], matrix[6], matrix[7],
                            matrix[8], matrix[9], matrix[10], matrix[11],
                            matrix[12], matrix[13], matrix[14], matrix[15]));
                }catch (Exception e){e.printStackTrace();}
            }
        }
    }

    public void addMesh(String name, ResourceDescriptor descriptor, Matrix4f transformation) {
        Mesh mesh = new Mesh(descriptor, transformation, name, loader);
        mesh.selected = true;

        addEntity(mesh);
    }

    public void addMeshBoned(String name, ResourceDescriptor descriptor, Thing[] boneThings, Matrix4f transformation) {
        Mesh mesh = new Mesh(descriptor, transformation, name, loader);
        mesh.boneThings = boneThings;
        mesh.selected = true;

        addEntity(mesh);
    }

    public void addMaterial(String name, PShape shape, ResourceDescriptor gmat, ResourceDescriptor bev, ResourceDescriptor mat, Matrix4f transformation)
    {
        MaterialPrimitive material = new MaterialPrimitive(shape, gmat, bev, mat, transformation, name, loader);
        material.selected = true;
        addEntity(material);
    }

    public void setupList()
    {
        entries.clear();

        if(LoadedData.MAP != null && !LoadedData.FARCs.isEmpty())
            for(FileArchive farc : LoadedData.FARCs)
                for(FileDBRow entry : LoadedData.MAP.entries)
                    if (farc.exists(entry.getSHA1()))
                        entries.add(entry);

        if(LoadedData.BIGFART != null)
            for(SaveEntry ent : LoadedData.BIGFART.entries)
                entries.add(ent);
    }

    public FileEntry getSourceEntry(ResourceDescriptor descriptor) {
        if (descriptor == null) return null;
        if (descriptor.isGUID()) return LoadedData.MAP.get(descriptor.getGUID());
        SHA1 sha1 = descriptor.getSHA1();
        for (FileEntry entry : this.entries)
            if (entry.getSHA1().equals(sha1))
                return entry;
        return null;
    }

    public void setSelectedPosition(Vector3f pos)
    {
        boolean hasSelection = false;
        ArrayList<Integer> selected = new ArrayList<>();

        for(int i = 0; i < entities.size(); i++)
            if(entities.get(i).selected)
            {
                hasSelection = true;
                selected.add(i);
            }

        if(selected.size() == 1)
            for(int i : selected)
            {
                entities.get(i).transformation.setTranslation(new Vector3f(pos));
            }
        else
        {
            Vector3f avgpos = new Vector3f(getSelectedPosition());

            for(int i : selected)
            {
                Entity entity = entities.get(i);
                Vector3f curTransl = new Vector3f(entity.transformation.getTranslation(new Vector3f()));
                Vector3f diff = new Vector3f(new Vector3f(curTransl).sub(avgpos, new Vector3f()));
                entity.transformation.setTranslation(new Vector3f(pos).add(diff, new Vector3f()));
            }
        }
    }

    public Vector3f getSelectedPosition()
    {
        boolean hasSelection = false;
        ArrayList<Integer> selected = new ArrayList<>();

        for(int i = 0; i < entities.size(); i++)
            if(entities.get(i).selected)
            {
                hasSelection = true;
                selected.add(i);
            }

        if(selected.size() == 1)
        {
            for (int i : selected)
                return new Vector3f(entities.get(i).transformation.getTranslation(new Vector3f()));
        }
        else
        {
            float x = 0;
            float y = 0;
            float z = 0;
            int amount = 0;

            for (int i : selected)
                {
                    Vector3f curTransl = entities.get(i).transformation.getTranslation(new Vector3f());
                    x += curTransl.x;
                    y += curTransl.y;
                    z += curTransl.z;
                    amount++;
                }

            return new Vector3f(x/amount, y/amount, z/amount);

        }

        return new Vector3f(Config.NaNf, Config.NaNf, Config.NaNf);
    }

    public String getSelectedMeshDescriptor()
    {
        boolean hasSelection = false;
        ArrayList<Integer> selected = new ArrayList<>();

        for(int i = 0; i < entities.size(); i++)
            if(entities.get(i).selected)
            {
                hasSelection = true;
                selected.add(i);
            }

        if(selected.size() > 0)
        {
            if(selected.size() == 1)
                return ((Mesh)entities.get(selected.get(0))).meshDescriptor.isGUID() ? ((Mesh)entities.get(selected.get(0))).meshDescriptor.getGUID().toString() : "h" + ((Mesh)entities.get(selected.get(0))).meshDescriptor.getSHA1().toString();
            else if(selected.size() > 1)
            {
                boolean isSame = true;
                String descriptor = ((Mesh)entities.get(selected.get(0))).meshDescriptor.isGUID() ? ((Mesh)entities.get(selected.get(0))).meshDescriptor.getGUID().toString() : "h" + ((Mesh)entities.get(selected.get(0))).meshDescriptor.getSHA1().toString();
                for(int i : selected)
                {
                    Mesh entity = (Mesh) entities.get(i);
                    String desc = entity.meshDescriptor.isGUID() ? entity.meshDescriptor.getGUID().toString() : "h" + entity.meshDescriptor.getSHA1().toString();

                    if(!descriptor.equalsIgnoreCase(desc))
                        isSame = false;
                }

                if(isSame)
                    return descriptor;
            }
        }

        return "";
    }

    public String getSelectedGfxMaterialDescriptor()
    {
        boolean hasSelection = false;
        ArrayList<Integer> selected = new ArrayList<>();

        for(int i = 0; i < entities.size(); i++)
            if(entities.get(i).selected)
            {
                hasSelection = true;
                selected.add(i);
            }

        if(selected.size() > 0 && ((MaterialPrimitive)entities.get(selected.get(0))).gmat != null)
        {
            if(selected.size() == 1)
                return ((MaterialPrimitive)entities.get(selected.get(0))).gmat.isGUID() ? ((MaterialPrimitive)entities.get(selected.get(0))).gmat.getGUID().toString() : "h" + ((MaterialPrimitive)entities.get(selected.get(0))).gmat.getSHA1().toString();
            else if(selected.size() > 1)
            {
                boolean isSame = true;
                String descriptor = ((MaterialPrimitive)entities.get(selected.get(0))).gmat.isGUID() ? ((MaterialPrimitive)entities.get(selected.get(0))).gmat.getGUID().toString() : "h" + ((MaterialPrimitive)entities.get(selected.get(0))).gmat.getSHA1().toString();
                for(int i : selected)
                {
                    MaterialPrimitive entity = (MaterialPrimitive) entities.get(i);
                    String desc = entity.gmat.isGUID() ? entity.gmat.getGUID().toString() : "h" + entity.gmat.getSHA1().toString();

                    if(!descriptor.equalsIgnoreCase(desc))
                        isSame = false;
                }

                if(isSame)
                    return descriptor;
            }
        }

        return "";
    }

    public String getSelectedMaterialDescriptor()
    {
        boolean hasSelection = false;
        ArrayList<Integer> selected = new ArrayList<>();

        for(int i = 0; i < entities.size(); i++)
            if(entities.get(i).selected)
            {
                hasSelection = true;
                selected.add(i);
            }

        if(selected.size() > 0 && ((MaterialPrimitive)entities.get(selected.get(0))).mat != null)
        {
            if(selected.size() == 1)
                return ((MaterialPrimitive)entities.get(selected.get(0))).mat.isGUID() ? ((MaterialPrimitive)entities.get(selected.get(0))).mat.getGUID().toString() : "h" + ((MaterialPrimitive)entities.get(selected.get(0))).mat.getSHA1().toString();
            else if(selected.size() > 1)
            {
                boolean isSame = true;
                String descriptor = ((MaterialPrimitive)entities.get(selected.get(0))).mat.isGUID() ? ((MaterialPrimitive)entities.get(selected.get(0))).mat.getGUID().toString() : "h" + ((MaterialPrimitive)entities.get(selected.get(0))).mat.getSHA1().toString();
                for(int i : selected)
                {
                    MaterialPrimitive entity = (MaterialPrimitive) entities.get(i);
                    String desc = entity.mat.isGUID() ? entity.mat.getGUID().toString() : "h" + entity.mat.getSHA1().toString();

                    if(!descriptor.equalsIgnoreCase(desc))
                        isSame = false;
                }

                if(isSame)
                    return descriptor;
            }
        }

        return "";
    }

    public String getSelectedBevelDescriptor()
    {
        boolean hasSelection = false;
        ArrayList<Integer> selected = new ArrayList<>();

        for(int i = 0; i < entities.size(); i++)
            if(entities.get(i).selected)
            {
                hasSelection = true;
                selected.add(i);
            }

        if(selected.size() > 0 && ((MaterialPrimitive)entities.get(selected.get(0))).bev != null)
        {
            if(selected.size() == 1)
                return ((MaterialPrimitive)entities.get(selected.get(0))).bev.isGUID() ? ((MaterialPrimitive)entities.get(selected.get(0))).bev.getGUID().toString() : "h" + ((MaterialPrimitive)entities.get(selected.get(0))).bev.getSHA1().toString();
            else if(selected.size() > 1)
            {
                boolean isSame = true;

                String descriptor = ((MaterialPrimitive)entities.get(selected.get(0))).bev.isGUID() ? ((MaterialPrimitive)entities.get(selected.get(0))).bev.getGUID().toString() : "h" + ((MaterialPrimitive)entities.get(selected.get(0))).bev.getSHA1().toString();
                for(int i : selected)
                {
                    MaterialPrimitive entity = (MaterialPrimitive) entities.get(i);
                    String desc = entity.bev.isGUID() ? entity.bev.getGUID().toString() : "h" + entity.bev.getSHA1().toString();

                    if(!descriptor.equalsIgnoreCase(desc))
                        isSame = false;
                }

                if(isSame)
                    return descriptor;
            }
        }

        return "";
    }

    public float getSelectedMaterialThickness()
    {
        boolean hasSelection = false;
        ArrayList<Integer> selected = new ArrayList<>();

        for(int i = 0; i < entities.size(); i++)
            if(entities.get(i).selected)
            {
                hasSelection = true;
                selected.add(i);
            }

        if(selected.size() > 0)
        {
            if(selected.size() == 1)
                return ((MaterialPrimitive)entities.get(selected.get(0))).shape.thickness;
            else if(selected.size() > 1)
            {
                boolean isSame = true;
                float thickness = ((MaterialPrimitive)entities.get(selected.get(0))).shape.thickness;
                for(int i : selected)
                {
                    MaterialPrimitive entity = (MaterialPrimitive) entities.get(i);

                    if(entity.shape.thickness != thickness)
                        isSame = false;
                }

                if(isSame)
                    return thickness;
            }
        }

        return Config.NaNf;
    }

    public float getSelectedBevelSize()
    {
        boolean hasSelection = false;
        ArrayList<Integer> selected = new ArrayList<>();

        for(int i = 0; i < entities.size(); i++)
            if(entities.get(i).selected)
            {
                hasSelection = true;
                selected.add(i);
            }

        if(selected.size() > 0)
        {
            if(selected.size() == 1)
                return ((MaterialPrimitive)entities.get(selected.get(0))).shape.bevelSize;
            else if(selected.size() > 1)
            {
                boolean isSame = true;
                float bevelSize = ((MaterialPrimitive)entities.get(selected.get(0))).shape.bevelSize;
                for(int i : selected)
                {
                    MaterialPrimitive entity = (MaterialPrimitive) entities.get(i);

                    if(entity.shape.bevelSize != bevelSize)
                        isSame = false;
                }

                if(isSame)
                    return bevelSize;
            }
        }

        return Config.NaNf;
    }

    public String getSelectedSoundName()
    {
        boolean hasSelection = false;
        ArrayList<Integer> selected = new ArrayList<>();

        for(int i = 0; i < entities.size(); i++)
            if(entities.get(i).selected)
            {
                hasSelection = true;
                selected.add(i);
            }

        if(selected.size() > 0)
        {
            if(selected.size() == 1)
                return ((WorldAudio)entities.get(selected.get(0))).soundName;
            else if(selected.size() > 1)
            {
                boolean isSame = true;
                String soundName = ((WorldAudio)entities.get(selected.get(0))).soundName;
                for(int i : selected)
                {
                    WorldAudio entity = (WorldAudio) entities.get(i);

                    if(entity.soundName != soundName)
                        isSame = false;
                }

                if(isSame)
                    return soundName;
            }
        }

        return "";
    }

    public float getSelectedSoundVolume()
    {
        boolean hasSelection = false;
        ArrayList<Integer> selected = new ArrayList<>();

        for(int i = 0; i < entities.size(); i++)
            if(entities.get(i).selected)
            {
                hasSelection = true;
                selected.add(i);
            }

        if(selected.size() > 0)
        {
            if(selected.size() == 1)
                return ((WorldAudio)entities.get(selected.get(0))).initialVolume;
            else if(selected.size() > 1)
            {
                boolean isSame = true;
                float soundVolume = ((WorldAudio)entities.get(selected.get(0))).initialVolume;
                for(int i : selected)
                {
                    WorldAudio entity = (WorldAudio) entities.get(i);

                    if(entity.initialVolume != soundVolume)
                        isSame = false;
                }

                if(isSame)
                    return soundVolume;
            }
        }

        return Config.NaNf;
    }

    public float getSelectedSoundPitch()
    {
        boolean hasSelection = false;
        ArrayList<Integer> selected = new ArrayList<>();

        for(int i = 0; i < entities.size(); i++)
            if(entities.get(i).selected)
            {
                hasSelection = true;
                selected.add(i);
            }

        if(selected.size() > 0)
        {
            if(selected.size() == 1)
                return ((WorldAudio)entities.get(selected.get(0))).initialPitch;
            else if(selected.size() > 1)
            {
                boolean isSame = true;
                float soundVolume = ((WorldAudio)entities.get(selected.get(0))).initialPitch;
                for(int i : selected)
                {
                    WorldAudio entity = (WorldAudio) entities.get(i);

                    if(entity.initialPitch != soundVolume)
                        isSame = false;
                }

                if(isSame)
                    return soundVolume;
            }
        }

        return Config.NaNf;
    }

    public String getSelectedName()
    {
        boolean hasSelection = false;
        ArrayList<Integer> selected = new ArrayList<>();

        for(int i = 0; i < entities.size(); i++)
            if(entities.get(i).selected)
            {
                hasSelection = true;
                selected.add(i);
            }

        if(selected.size() == 1)
            return entities.get(selected.get(0)).entityName;
        else if(selected.size() > 1)
        {
            boolean isSame = true;
            String name = entities.get(selected.get(0)).entityName;
            for(int i : selected)
            {
                String n = entities.get(i).entityName;

                if(!name.equalsIgnoreCase(n))
                    isSame = false;
            }

            if(isSame)
                return name;
        }

        return "";
    }

    public RPlan buildPlan()
    {
        ArrayList<Thing> things = new ArrayList();

        int UID = 1;

        Thing lighting = new Thing(UID);
        lighting.setPart(Part.POS, new PPos());

        PLevelSettings settings = LevelSettingsUtils.translate(levelSettings.get(levelSettings.size() - 1));

        ArrayList<LevelSettings> presets = new ArrayList<>();
        for(LevelSettings preset : levelSettings)
            presets.add(LevelSettingsUtils.clone(preset));

        settings.presets = presets;
        settings.backdropAmbience = ((Textbox)((DropDownTab)LevelSettingsEditing.getElementByID("presetEditor")).getElementByID("ambiance")).getText().isEmpty() ? "ambiences/amb_empty_world" : ((Textbox)LevelSettingsEditing.getElementByID("ambiance")).getText();

        lighting.setPart(Part.LEVEL_SETTINGS, settings);

        UID++;

        Thing borderStart = new Thing(UID);

        PPos bStart = new PPos();
        bStart.worldPosition = new Matrix4f().identity().translate(new Vector3f(-31147.4453125f, 3143.436279296875f, 0f))
                                .rotate(new Quaternionf(0, 0, 0, 1))
                                .scale(new Vector3f(11.509700775146484f, 1496.25048828125f, 1f));
        bStart.localPosition = bStart.worldPosition;

        borderStart.setPart(Part.POS, bStart);

        PScriptName bStartName = new PScriptName();
        bStartName.name = "BorderStart";

        borderStart.setPart(Part.SCRIPT_NAME, bStartName);

        UID++;

        Thing borderEnd = new Thing(UID);

        PPos bEnd = new PPos();
        bEnd.worldPosition = new Matrix4f().identity().translate(new Vector3f(69882.4609375f, 3103.467041015625f, 0f))
                .rotate(new Quaternionf(0, 0, 0, 1))
                .scale(new Vector3f(11.509700775146484f, 1481.7734375f, 1f));
        bEnd.localPosition = bEnd.worldPosition;

        borderEnd.setPart(Part.POS, bEnd);

        PScriptName bEndName = new PScriptName();
        bEndName.name = "BorderEnd";

        borderEnd.setPart(Part.SCRIPT_NAME, bEndName);

        UID++;

        things.add(lighting);
        things.add(borderStart);
        things.add(borderEnd);

        //main objects
        for(Entity entity : entities)
        {
            Thing thing = new Thing(UID);

            PPos thingPos = new PPos();
            thingPos.worldPosition = new Matrix4f(entity.transformation);
            thingPos.localPosition = thingPos.worldPosition;

            thing.setPart(Part.POS, thingPos);

            switch (entity.getType())
            {
                case 0:
                {
                    Mesh mesh = (Mesh)entity;
                    Thing[] boneThings = new Thing[mesh.skeleton.length];

                    for(int i = 0; i < mesh.skeleton.length; i++)
                    {
                        Thing boneThing = new Thing(UID);
                        boneThing.parent = lighting;
                        boneThing.groupHead = lighting;
                        boneThings[i] = boneThing;
                        UID++;
                    }

                    boneThings = SkeletonUtils.computeBoneThings(boneThings, thing, new Matrix4f(((PPos)thing.getPart(Part.POS)).worldPosition), mesh.skeleton);

                    PRenderMesh renderMesh = new PRenderMesh(mesh.meshDescriptor);
                    renderMesh.boneThings = boneThings;
                    renderMesh.castShadows = ShadowType.ALWAYS;
                    thing.setPart(Part.RENDER_MESH, renderMesh);

                    for(Thing boneThing : boneThings)
                        if(!boneThing.equals(thing))
                            things.add(boneThing);
                }
                    break;
                case 1:
                {
                    PGeneratedMesh details = new PGeneratedMesh();
                    details.gfxMaterial = ((MaterialPrimitive)entity).gmat;
                    details.bevel = ((MaterialPrimitive)entity).bev;
                    details.uvOffset = new Vector4f();
                    details.visibilityFlags = 3;
                    details.textureAnimationSpeed = 1;
                    details.textureAnimationSpeed = 1;

                    thing.setPart(Part.GENERATED_MESH, details);
                    thing.setPart(Part.SHAPE, ((MaterialPrimitive)entity).shape);
                }
                    break;
                case 3:
                {
                    ResourceDescriptor soundObjectMeshDescriptor = new ResourceDescriptor(95344l, ResourceType.MESH);
                    RMesh soundObjectMesh = LoadedData.loadMesh(soundObjectMeshDescriptor);
                    Thing[] boneThings = new Thing[soundObjectMesh.getBones().length];

                    for(int i = 0; i < soundObjectMesh.getBones().length; i++)
                    {
                        Thing boneThing = new Thing(UID);
                        boneThing.parent = lighting;
                        boneThing.groupHead = lighting;
                        boneThings[i] = boneThing;
                        UID++;
                    }

                    boneThings = SkeletonUtils.computeBoneThings(boneThings, thing, new Matrix4f(((PPos)thing.getPart(Part.POS)).worldPosition), soundObjectMesh.getBones());

                    PRenderMesh renderMesh = new PRenderMesh(soundObjectMeshDescriptor);
                    renderMesh.boneThings = boneThings;
                    renderMesh.castShadows = ShadowType.NEVER;
                    renderMesh.visibilityFlags = 0x0;
                    thing.setPart(Part.RENDER_MESH, renderMesh);

                    PAudioWorld audioWorld = new PAudioWorld();
                    audioWorld.soundName = ((WorldAudio)entity).soundName;
                    audioWorld.initialVolume = ((WorldAudio)entity).initialVolume;
                    audioWorld.initialPitch = ((WorldAudio)entity).initialPitch;
                    audioWorld.isLocal = ((WorldAudio)entity).isLocal;
                    audioWorld.soundNames = ((WorldAudio)entity).soundNames;
                    audioWorld.triggerByFalloff = true;
                    audioWorld.maxFalloff = 200;

                    for(Thing boneThing : boneThings)
                        if(!boneThing.equals(thing))
                            things.add(boneThing);
                }
                    break;
            }

            thing.parent = lighting;
            thing.groupHead = lighting;

            things.add(thing);

            UID++;
        }

        RPlan plan = new RPlan();

        if(!((Checkbox)Export.getElementByID("restrictedLevel")).isChecked && !((Checkbox)Export.getElementByID("restrictedPod")).isChecked)
            plan.revision = new Revision(Branch.LEERDAMMER.getHead(), Branch.LEERDAMMER.getID(), Revisions.LD_LAMS_KEYS);
        else
            plan.revision = new Revision(0x335);

        plan.compressionFlags = CompressionFlags.USE_NO_COMPRESSION;
        plan.setThings(things.toArray(Thing[]::new));

        ResourceDescriptor icon = new ResourceDescriptor(2551, ResourceType.TEXTURE);
        String planIcon = ((Textbox)Export.getElementByID("icon")).getText();
        if(planIcon.length() == 40)
            icon = new ResourceDescriptor(new SHA1(planIcon), ResourceType.TEXTURE);
        else if(planIcon.length() == 41)
            icon = new ResourceDescriptor(new SHA1(planIcon.substring(1)), ResourceType.TEXTURE);
        else if(planIcon.substring(0, 1).equalsIgnoreCase("g"))
            icon = new ResourceDescriptor(Long.parseLong(planIcon.substring(1)), ResourceType.TEXTURE);
        else
            icon = new ResourceDescriptor(Long.parseLong(planIcon), ResourceType.TEXTURE);

        PMetadata metadata = new PMetadata();

        String creator = ((Textbox)Export.getElementByID("creator")).getText();

        plan.inventoryData = new InventoryItemDetails();
        plan.inventoryData.creator = new NetworkPlayerID(creator);
        plan.inventoryData.creationHistory = new CreationHistory();
        plan.inventoryData.creationHistory.creators = new String[]{creator};
        plan.inventoryData.userCreatedDetails = new UserCreatedDetails(((Textbox)Export.getElementByID("title")).getText(), ((Textarea)Export.getElementByID("description")).getText());
        plan.inventoryData.icon = icon;
        plan.inventoryData.type = EnumSet.of(InventoryObjectType.BACKGROUND);

        if(((Checkbox)Export.getElementByID("restrictedLevel")).isChecked)
            plan.inventoryData.flags |= InventoryItemFlags.RESTRICTED_LEVEL;
        else if(((Checkbox)Export.getElementByID("restrictedPod")).isChecked)
            plan.inventoryData.flags |= InventoryItemFlags.RESTRICTED_POD;

        return plan;
    }

    public void setCurrentScreen(GuiScreen screen) {
        screen.previousScreen = this.currentScreen;
        this.currentScreen = screen;
    }

    public void returnToPreviousScreen() {
        this.currentScreen = this.currentScreen.previousScreen;
    }

    public void addThings(List<Thing> things)
    {
        for (Thing thing : things) {
            if (thing == null) continue;

            PPos ppos = thing.getPart(Part.POS);
            PRenderMesh renderMesh = thing.getPart(Part.RENDER_MESH);
            PShape shape = thing.getPart(Part.SHAPE);
            PAudioWorld audioWorld = thing.getPart(Part.AUDIO_WORLD);

            boolean isAudioWorld = audioWorld != null && (renderMesh != null && renderMesh.visibilityFlags == 0);

            if(ppos == null) continue;
            if (renderMesh != null && !isAudioWorld)
            {
                FileEntry entry = getSourceEntry(renderMesh.mesh);
                if (entry == null) continue;

                String name = entry.getName();

                if(renderMesh.mesh.isHash())
                    name = name.substring(name.length() - 12);

                addMeshBoned(
                        name.substring(0, name.lastIndexOf(".")),
                        renderMesh.mesh,
                        renderMesh.boneThings,
                        ppos.worldPosition
                );
            }
            else if(shape != null)
            {
                ResourceDescriptor gmat = new ResourceDescriptor(10811l, ResourceType.GFX_MATERIAL);
                ResourceDescriptor bev = new ResourceDescriptor(10790l, ResourceType.BEVEL);

                PGeneratedMesh details = thing.getPart(Part.GENERATED_MESH);
                if(details != null)
                {
                    gmat = details.gfxMaterial;
                    bev = details.bevel;
                }

                String name = getSourceEntry(gmat).getPath();
                name = name.substring(name.lastIndexOf("/") + 1, name.lastIndexOf("."));

                addMaterial(name, shape, gmat, bev, shape.material, ppos.worldPosition);
            }
            else if(isAudioWorld)
            {
                WorldAudio audio = new WorldAudio(ppos.worldPosition.getTranslation(new Vector3f()), audioWorld.soundName.substring(audioWorld.soundName.lastIndexOf("/") + 1), audioWorld.soundName, audioWorld.initialVolume, audioWorld.initialPitch, audioWorld.isLocal, audioWorld.soundNames, loader);
                audio.selected = true;
                addEntity(audio);
            }
        }
    }

    public void addThings(Thing[] things)
    {
        addThings(Arrays.asList(things));
    }
}