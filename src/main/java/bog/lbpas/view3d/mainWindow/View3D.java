package bog.lbpas.view3d.mainWindow;

import bog.lbpas.Main;
import bog.lbpas.view3d.renderer.Camera;
import bog.lbpas.view3d.renderer.ILogic;
import bog.lbpas.view3d.managers.assetLoading.ObjectLoader;
import bog.lbpas.view3d.core.*;
import bog.lbpas.view3d.core.types.Entity;
import bog.lbpas.view3d.mainWindow.screens.*;
import bog.lbpas.view3d.managers.EngineMan;
import bog.lbpas.view3d.managers.MouseInput;
import bog.lbpas.view3d.managers.RenderMan;
import bog.lbpas.view3d.managers.WindowMan;
import bog.lbpas.view3d.renderer.gui.GuiKeybind;
import bog.lbpas.view3d.renderer.gui.GuiScreen;
import bog.lbpas.view3d.renderer.gui.cursor.Cursor;
import bog.lbpas.view3d.renderer.gui.cursor.ECursor;
import bog.lbpas.view3d.renderer.gui.elements.Button;
import bog.lbpas.view3d.renderer.gui.elements.Checkbox;
import bog.lbpas.view3d.renderer.gui.elements.*;
import bog.lbpas.view3d.renderer.gui.font.FNT;
import bog.lbpas.view3d.renderer.gui.font.FontRenderer;
import bog.lbpas.view3d.renderer.gui.ingredients.*;
import bog.lbpas.view3d.utils.*;
import common.FileChooser;
import cwlib.enums.*;
import cwlib.resources.RMesh;
import cwlib.structs.mesh.Bone;
import cwlib.structs.things.Thing;
import cwlib.structs.things.parts.*;
import cwlib.types.data.ResourceDescriptor;
import cwlib.types.data.Revision;
import cwlib.types.databases.FileDBRow;
import cwlib.types.databases.FileEntry;
import cwlib.util.GsonUtils;
import org.joml.Math;
import org.joml.*;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.FocusEvent;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.List;
import java.util.*;
import java.util.Random;

/**
 * @author Bog
 */
public class View3D implements ILogic {

    public RenderMan renderer;
    public WindowMan window;
    public ObjectLoader loader;
    public ArrayList<bog.lbpas.view3d.core.types.Thing> things;
    public ArrayList<Entity> BORDERS;
    public ArrayList<Entity> POD_EARTH;
    public Camera camera;
    Vector3f cameraInc;

    public GuiScreen currentScreen;
    public GuiScreen overrideScreen;

    Model topBarLine;
    Model windowFrame;
    Model windowFrameOuter;

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

    public NotificationFeed notificationFeed;
    private Notification modelLoadNotif;
    private Notification textureLoadNotif;
    private Notification entryDigestionNotif;

    @Override
    public void init() throws Exception {
        topBarLine = Line.getLine(window, loader, new Vector2i(3, 23), new Vector2i(window.width - 3, 23));
        windowFrame = LineStrip.processVerts(LineStrip.getRectangle(new Vector2f(window.width - 6, window.height - 6)), loader, window);
        windowFrameOuter = LineStrip.processVerts(LineStrip.getRectangle(new Vector2f(window.width, window.height)), loader, window);
        renderer.init(this.loader);
        FontRenderer.init(this.loader);
        Transformation3D.init(this.loader);
        LoadedData.init(this);
        ConstantTextures.initTextures(this.loader);

        things = new ArrayList<>();
        BORDERS = new ArrayList<>();
        POD_EARTH = new ArrayList<>();

        notificationFeed = new NotificationFeed(false, 12, new Vector2f(this.window.width - 248 - 400, this.window.height - 8), 400, renderer, loader, window)
        {
            @Override
            public void draw(MouseInput mouseInput, boolean overElement) {
                this.pos.x = this.window.width - (currentScreen instanceof ElementEditing ? 305 : 0) - 400 - 8;
                this.pos.y = this.window.height - 8;
                super.draw(mouseInput, overElement);
            }
        };

        borders = loader.loadOBJModelDirect("/models/border.obj");
        borders.material = new Material(Config.BORDER_COLOR_1, 0f).disableCulling(true);
        BORDERS.add(new Entity(borders, new Vector3f(21219f, 1557f, 10f), new Vector3f(0), new Vector3f(1f), loader));
        BORDERS.add(new Entity(borders, new Vector3f(21219f, 1557f, -390f), new Vector3f(0), new Vector3f(1f), loader));

        borders1 = new Model(borders);
        borders1.material = new Material(Config.BORDER_COLOR_2, 0f).disableCulling(true);
        BORDERS.add(new Entity(borders1, new Vector3f(21219f, 1557f, -190f), new Vector3f(0), new Vector3f(1f), loader));

        borders2 = new Model(borders);
        borders2.material = new Material(Config.BORDER_COLOR_3, 0f).disableCulling(true);
        for(int layer = 0; layer < 7; layer++)
            BORDERS.add(new Entity(borders2, new Vector3f(21219f, 1557f, -590f + -400f * layer), new Vector3f(0), new Vector3f(1f), loader));

        borders3 = new Model(borders);
        borders3.material = new Material(Config.BORDER_COLOR_4, 0f).disableCulling(true);
        for(int layer = 0; layer < 6; layer++)
            BORDERS.add(new Entity(borders3, new Vector3f(21219f, 1557f, -790f + -400f * layer), new Vector3f(0), new Vector3f(1f), loader));

        bone = loader.loadOBJModelDirect("/models/bone.obj");
        bone.material = new Material(Color.white, 0f);

        pod = loader.loadOBJModelDirect("/models/pod.obj");
        pod.material = new Material(new Texture[]{loader.loadResourceTexture("/textures/pod.png", GL11.GL_LINEAR_MIPMAP_LINEAR, GL11.GL_LINEAR)});
        pod.material.overlayColor = new Vector4f(Config.POD_COLOR.getRed() / 255f, Config.POD_COLOR.getGreen() / 255f, Config.POD_COLOR.getBlue() / 255f, Config.POD_COLOR.getAlpha() / 255f);
        POD_EARTH.add(new Entity(pod, new Vector3f(25.0f, 260.0f, 13490.0f), new Vector3f(-105.0f, 0.0f, 0.0f), new Vector3f(1f), loader));
        earth = loader.loadOBJModelDirect("/models/earth.obj");
        earth.material = new Material(new Texture[]{loader.loadResourceTexture("/textures/earth.png", GL11.GL_LINEAR_MIPMAP_LINEAR, GL11.GL_LINEAR)});
        earth.material.overlayColor = new Vector4f(Config.EARTH_COLOR.getRed() / 255f, Config.EARTH_COLOR.getGreen() / 255f, Config.EARTH_COLOR.getBlue() / 255f, Config.EARTH_COLOR.getAlpha() / 255f);
        POD_EARTH.add(new Entity(earth, new Vector3f(30.71f, 60.38f, 243.31f), new Vector3f(0), new Vector3f(1.5f), loader));

        createUI();

        modelLoadNotif = new Notification() {
            @Override
            public void draw(MouseInput mouseInput, boolean overElement) {

                if(initialCount == -1)
                    initialCount = loader.modelLoader.totalDigestionCount;

                super.draw(mouseInput, overElement);

                if(!loader.modelLoader.isLoadingSomething())
                {
                    int count = loader.modelLoader.totalDigestionCount - initialCount;
                    pushSuccess("Done!", "Loaded " + count + (count == 1 ? " model." : " models."));
                    closeNotification();
                }
            }

            int initialCount = -1;

            @Override
            public String getTitle() {
                return "Loading Models...";
            }

            @Override
            public String getContent() {
                return "Digesting: " + loader.modelLoader.digestionCount() + "\nFinished: " + (loader.modelLoader.totalDigestionCount - initialCount);
            }

            @Override
            public void closeNotification() {
                notificationFeed.notifications.remove(this);
                initialCount = -1;
            }

            @Override
            public Color backgroundColor() {
                return new Color(52, 174, 235, Config.INTERFACE_PRIMARY_COLOR.getAlpha());
            }

            @Override
            public Color outlineColor() {
                return new Color(52, 174, 235, Config.INTERFACE_PRIMARY_COLOR2.getAlpha());
            }
        };
        textureLoadNotif = new Notification() {
            @Override
            public void draw(MouseInput mouseInput, boolean overElement) {
                if(initialCount == -1)
                    initialCount = loader.textureLoader.totalDigestionCount;

                super.draw(mouseInput, overElement);

                if(!loader.modelLoader.isLoadingSomething())
                {
                    int count = loader.textureLoader.totalDigestionCount - initialCount + 1;
                    pushSuccess("Done!", "Loaded " + count + (count == 1 ? " texture." : " textures."));
                    closeNotification();
                }
            }

            int initialCount = -1;

            @Override
            public String getTitle() {
                return "Loading Textures...";
            }

            @Override
            public String getContent() {
                return "Digesting: " + loader.textureLoader.digestionCount() + "\nFinished: " + (loader.textureLoader.totalDigestionCount - initialCount);
            }

            @Override
            public void closeNotification() {
                notificationFeed.notifications.remove(this);
                initialCount = -1;
            }

            @Override
            public Color backgroundColor() {
                return new Color(52, 174, 235, Config.INTERFACE_PRIMARY_COLOR.getAlpha());
            }

            @Override
            public Color outlineColor() {
                return new Color(52, 174, 235, Config.INTERFACE_PRIMARY_COLOR2.getAlpha());
            }
        };
        entryDigestionNotif = new Notification() {
            @Override
            public void draw(MouseInput mouseInput, boolean overElement) {
                super.draw(mouseInput, overElement);

                if(!LoadedData.shouldSetupList)
                {
                    int count = LoadedData.totalEntryCount;
                    pushSuccess("Done!", "Loaded " + count + (count == 1 ? " entry." : " entries."));
                    closeNotification();
                }
            }

            @Override
            public String getTitle() {
                return "Setting up entries... " + Utils.round(((float)LoadedData.currentEntryDigestion / (float)LoadedData.totalEntryCount) * 100, 1) + "%";
            }

            @Override
            public String getContent() {
                return "Digesting: " + LoadedData.totalEntryCount + "\nFinished: " + LoadedData.currentEntryDigestion;
            }

            @Override
            public void closeNotification() {
                notificationFeed.notifications.remove(this);
            }

            @Override
            public Color backgroundColor() {
                return new Color(52, 174, 235, Config.INTERFACE_PRIMARY_COLOR.getAlpha());
            }

            @Override
            public Color outlineColor() {
                return new Color(52, 174, 235, Config.INTERFACE_PRIMARY_COLOR2.getAlpha());
            }
        };
    }

    public void addEntity(bog.lbpas.view3d.core.types.Thing e)
    {
        things.add(e);
    }

    private int[] prevSelection = new int[0];
    public MousePicker centerPicker;
    public MouseInput mouseInput;

    boolean wasLoadingModels = false;
    boolean wasLoadingTextures = false;
    boolean wasDigestingEntries = false;

    @Override
    public void update(MouseInput mouseInput) {

        ConstantTextures.mainThread();
        loader.primaryThread();

        boolean isLoadingModels = loader.modelLoader.isLoadingSomething();
        if(isLoadingModels && !wasLoadingModels)
        {
            modelLoadNotif.forceRefreshOutline();
            pushNotification(modelLoadNotif);
        }
        wasLoadingModels = isLoadingModels;

        boolean isLoadingTextures = loader.textureLoader.isLoadingSomething();
        if(isLoadingTextures && !wasLoadingTextures)
        {
            textureLoadNotif.forceRefreshOutline();
            pushNotification(textureLoadNotif);
        }
        wasLoadingTextures = isLoadingTextures;

        boolean isDigestingEntries = LoadedData.shouldSetupList;
        if(isDigestingEntries && !wasDigestingEntries)
        {
            entryDigestionNotif.forceRefreshOutline();
            pushNotification(entryDigestionNotif);
        }
        wasDigestingEntries = isDigestingEntries;

        boolean elementFocused = overrideScreen == null ? false : overrideScreen.elementFocused();
        if(!elementFocused)
            elementFocused = currentScreen == null ? false : currentScreen.elementFocused();
        boolean overElement = overrideScreen == null ? false : overrideScreen.isMouseOverElement(mouseInput);
        if(!overElement)
            overElement = currentScreen == null ? false : currentScreen.isMouseOverElement(mouseInput);

        if(!elementFocused && !overElement)
            camera.movePos((cameraInc.x * Config.CAMERA_MOVE_SPEED) / (EngineMan.fps == 0 ? 60 : EngineMan.fps), (cameraInc.y * Config.CAMERA_MOVE_SPEED) / (EngineMan.fps == 0 ? 60 : EngineMan.fps), (cameraInc.z * Config.CAMERA_MOVE_SPEED) / (EngineMan.fps == 0 ? 60 : EngineMan.fps));

        if(!overElement)
            Cursors.setCursor(ECursor.left_ptr);

        if(window.resize)
        {
            renderer.resize();
            window.resize = false;
            for(Element e : ElementEditing.guiElements)
                e.resize();
            for(Element e : MaterialEditing.guiElements)
                e.resize();
            for(Element e : Archive.guiElements)
                e.resize();
            for(Element e : ProjectManager.guiElements)
                e.resize();
            for(Element e : Settings.guiElements)
                e.resize();
            for(Element e : overrideScreen.guiElements)
                e.resize();

            if(topBarLine != null)
                topBarLine.cleanup(loader);
            if(windowFrame != null)
                windowFrame.cleanup(loader);
            topBarLine = Line.getLine(window, loader, new Vector2i(3, 23), new Vector2i(window.width - 3, 23));
            windowFrame = LineStrip.processVerts(LineStrip.getRectangle(new Vector2f(window.width - 6, window.height - 6)), loader, window);
            windowFrameOuter = LineStrip.processVerts(LineStrip.getRectangle(new Vector2f(window.width, window.height)), loader, window);

            if(currentScreen instanceof GuiKeybind)
                ((GuiKeybind)currentScreen).resize();
        }

        this.mouseInput = mouseInput;

        boolean hasSelection = false;
        int selectedAmount = 0;

        for(bog.lbpas.view3d.core.types.Thing thing : things)
            if(thing.selected)
            {
                hasSelection = true;
                selectedAmount++;
            }

        try {
        } catch (Exception e) {
            e.printStackTrace();
        }

        mouseInput.mousePicker.update(camera);

        if(this.centerPicker == null)
            this.centerPicker = new MousePicker(null, window);
        this.centerPicker.update(camera, window.width / 2, window.height / 2);

        Vector3f screenPos = camera.worldToScreen(getSelectedPosition(), window);

        if(Config.CAMERA.isPressed(window, mouseInput) && !overElement && introPlayed)
        {
            if(camera.getWrappedRotation().x > -90 && camera.getWrappedRotation().x < 90)
                camera.moveRot(mouseInput.displayVec.x * Config.MOUSE_SENS, mouseInput.displayVec.y * Config.MOUSE_SENS, 0);

            mouseInput.displayVec.set(0, 0);

            Cursors.setCursor(ECursor.grabbing);
        }

        if(camera.getWrappedRotation().x <= -90)
            camera.setRotX(-89.999f);
        if(camera.getWrappedRotation().x >= 90)
            camera.setRotX(89.999f);

        if(Config.LEVEL_BORDERS)
            for(Entity entity : BORDERS)
                renderer.processBasicMeshes(entity);

        if(Config.POD_HELPER != 0)
            for(int i = 0; i < POD_EARTH.size(); i++)
                if(Config.POD_HELPER > 1 || i == 0)
                    renderer.processBasicMeshes(POD_EARTH.get(i));

//        Vector3f pos = new Vector3f(0, 0, 0);
//        try {
//            pos = new Vector3f(levelSettings.get(selectedPresetIndex).sunPosition).mul(levelSettings.get(selectedPresetIndex).sunPositionScale);TODO
//
//            Vector4f sunColor = levelSettings.get(selectedPresetIndex).sunColor;
//            renderer.processDirectionalLight(new DirectionalLight(new Vector3f(sunColor.x, sunColor.y, sunColor.z), new Vector3f(pos), levelSettings.get(selectedPresetIndex).sunMultiplier));
//            renderer.processPointLight(new PointLight(
//                    new Vector3f(sunColor.x, sunColor.y, sunColor.z),
//                    pos,
//                    levelSettings.get(selectedPresetIndex).sunMultiplier,
//                    1,
//                    0,
//                    0
//            ));
//        }catch (Exception e){}

//        Vector3f sunPos = camera.worldToScreen(pos, window);
//        if(sunPos.z == 0)
//            renderer.drawImageStatic(ConstantTextures.getTexture(ConstantTextures.SUN, 35, 35, loader), (int) (sunPos.x - 17.5f), (int) (sunPos.y - 17.5f), 35, 35);

        for(int ent = 0; ent < things.size(); ent++)
        {
            bog.lbpas.view3d.core.types.Thing thing = things.get(ent);

            renderer.processEntity(thing);
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
//            TODO else if(entity.getType() == 3)
//            {
//                Vector3f audioPos = camera.worldToScreenPointF(entity.transformation.getTranslation(new Vector3f()), window);
//                if(audioPos.z == 0)
//                    renderer.processGuiElement(new Quad(loader, worldAudio, new Vector2f((int)(audioPos.x - 17.5f), (int)(audioPos.y - 17.5f)), new Vector2f(35, 35), false).staticTexture());
//            }
        }
        drawUI(mouseInput);
    }

    public void secondaryThread()
    {
        if(currentScreen != null)
            currentScreen.secondaryThread();
        if(overrideScreen != null)
            overrideScreen.secondaryThread();
    }

    @Override
    public void onMouseClick(MouseInput mouseInput, int button, int action, int mods) throws Exception {

        if(!introPlayed)
            return;

        boolean elementFocused = currentScreen.onClick(mouseInput, button, action, mods);
        if(!elementFocused)
            elementFocused = overrideScreen.onClick(mouseInput, button, action, mods);

        if(!window.isKeyPressed(GLFW.GLFW_KEY_LEFT_CONTROL))
        {
            if (action == GLFW.GLFW_PRESS)
            {
                if(Config.FORWARD.isButton(button))
                    cameraInc.z = Config.BACK.isPressed(window, mouseInput) ? 0 : -1;
                if(Config.LEFT.isButton(button))
                    cameraInc.x = Config.RIGHT.isPressed(window, mouseInput) ? 0 : -1;
                if(Config.BACK.isButton(button))
                    cameraInc.z = Config.FORWARD.isPressed(window, mouseInput) ? 0 : 1;
                if(Config.RIGHT.isButton(button))
                    cameraInc.x = Config.LEFT.isPressed(window, mouseInput) ? 0 : 1;
                if(Config.UP.isButton(button))
                    cameraInc.y = Config.DOWN.isPressed(window, mouseInput) ? 0 : 1;
                if(Config.DOWN.isButton(button))
                    cameraInc.y = Config.UP.isPressed(window, mouseInput) ? 0 : -1;
            }
            else if (action == GLFW.GLFW_RELEASE)
            {
                if(Config.FORWARD.isButton(button))
                    cameraInc.z = Config.BACK.isPressed(window, mouseInput) ? 1 : 0;
                if(Config.BACK.isButton(button))
                    cameraInc.z = Config.FORWARD.isPressed(window, mouseInput) ? -1 : 0;
                if(Config.LEFT.isButton(button))
                    cameraInc.x = Config.RIGHT.isPressed(window, mouseInput) ? 1 : 0;
                if(Config.RIGHT.isButton(button))
                    cameraInc.x = Config.LEFT.isPressed(window, mouseInput) ? -1 : 0;
                if(Config.UP.isButton(button))
                    cameraInc.y = Config.DOWN.isPressed(window, mouseInput) ? -1 : 0;
                if(Config.DOWN.isButton(button))
                    cameraInc.y = Config.UP.isPressed(window, mouseInput) ? 1 : 0;
            }
        }
        else
        {
            cameraInc.x = 0;
            cameraInc.y = 0;
            cameraInc.z = 0;
        }
    }

    @Override
    public void onMouseMove(MouseInput mouseInput, double x, double y)
    {
        boolean elementFocused = currentScreen.onMouseMove(mouseInput, x, y);
        if(!elementFocused)
            elementFocused = overrideScreen.onMouseMove(mouseInput, x, y);
    }

    @Override
    public void onKey(int key, int scancode, int action, int mods)
    {
        if(!introPlayed)
            return;

        boolean elementFocused = currentScreen.onKey(key, scancode, action, mods);
        if(!elementFocused)
            elementFocused = overrideScreen.onKey(key, scancode, action, mods);

        if(key != GLFW.GLFW_KEY_LEFT_CONTROL && !window.isKeyPressed(GLFW.GLFW_KEY_LEFT_CONTROL))
        {
            if (action == 1)
            {
                if(Config.FORWARD.isKey(key))
                    cameraInc.z = Config.BACK.isPressed(window, mouseInput) ? 0 : -1;
                if(Config.LEFT.isKey(key))
                    cameraInc.x = Config.RIGHT.isPressed(window, mouseInput) ? 0 : -1;
                if(Config.BACK.isKey(key))
                    cameraInc.z = Config.FORWARD.isPressed(window, mouseInput) ? 0 : 1;
                if(Config.RIGHT.isKey(key))
                    cameraInc.x = Config.LEFT.isPressed(window, mouseInput) ? 0 : 1;
                if(Config.UP.isKey(key))
                    cameraInc.y = Config.DOWN.isPressed(window, mouseInput) ? 0 : 1;
                if(Config.DOWN.isKey(key))
                    cameraInc.y = Config.UP.isPressed(window, mouseInput) ? 0 : -1;
            }
            else if (action == 0)
            {
                if(Config.FORWARD.isKey(key))
                    cameraInc.z = Config.BACK.isPressed(window, mouseInput) ? 1 : 0;
                if(Config.BACK.isKey(key))
                    cameraInc.z = Config.FORWARD.isPressed(window, mouseInput) ? -1 : 0;
                if(Config.LEFT.isKey(key))
                    cameraInc.x = Config.RIGHT.isPressed(window, mouseInput) ? 1 : 0;
                if(Config.RIGHT.isKey(key))
                    cameraInc.x = Config.LEFT.isPressed(window, mouseInput) ? -1 : 0;
                if(Config.UP.isKey(key))
                    cameraInc.y = Config.DOWN.isPressed(window, mouseInput) ? -1 : 0;
                if(Config.DOWN.isKey(key))
                    cameraInc.y = Config.UP.isPressed(window, mouseInput) ? 1 : 0;
            }
        }
        else if(key == GLFW.GLFW_KEY_LEFT_CONTROL && action == GLFW.GLFW_RELEASE)
        {
            cameraInc.x = Config.RIGHT.isPressed(window, mouseInput) ? 1 : Config.LEFT.isPressed(window, mouseInput) ? -1 : 0;
            cameraInc.y = Config.DOWN.isPressed(window, mouseInput) ? -1 : Config.UP.isPressed(window, mouseInput) ? 1 : 0;
            cameraInc.z = Config.BACK.isPressed(window, mouseInput) ? 1 : Config.FORWARD.isPressed(window, mouseInput) ? -1 : 0;
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
    }

    @Override
    public void onChar(int codePoint, int modifiers) {
        if(!introPlayed)
            return;

        currentScreen.onChar(codePoint, modifiers);
        overrideScreen.onChar(codePoint, modifiers);
    }

    @Override
    public void onMouseScroll(double xOffset, double yOffset)
    {
        if(!introPlayed)
            return;

        currentScreen.onMouseScroll(mouseInput.currentPos, xOffset, yOffset);
        overrideScreen.onMouseScroll(mouseInput.currentPos, xOffset, yOffset);

        if(!(currentScreen.isMouseOverElement(mouseInput) || overrideScreen.isMouseOverElement(mouseInput)) &&
                !(currentScreen.isElementFocused() || overrideScreen.isElementFocused()))
        {
            if(Config.CAMERA_MOVE_SPEED == 0 && yOffset > 0)
                Config.CAMERA_MOVE_SPEED = 1;
            Config.CAMERA_MOVE_SPEED = yOffset > 0 ? Config.CAMERA_MOVE_SPEED * 1.2f : Config.CAMERA_MOVE_SPEED * 0.8f;
            Config.CAMERA_MOVE_SPEED = yOffset > 0 ? Math.ceil(Config.CAMERA_MOVE_SPEED) : Math.floor(Config.CAMERA_MOVE_SPEED);
            ((Settings) Settings).moveSpeed.setText(Float.toString(Config.CAMERA_MOVE_SPEED));
        }
    }

    public Vector4f fogColor = new Vector4f(154f / 255f, 195f / 255f, 199f / 255f, 1f);
    public Vector4f rimColor = new Vector4f(210f/255f, 178f/255f, 142f/255f, 1f);
    public Vector4f rimColor2 = new Vector4f(89f/255f, 177f/255f, 232f/255f, 1f);
    public float fogNear = 200f;
    public float fogFar = 15000f;
    public Vector3f sunPos = new Vector3f(25000, 75000, 55000);

    @Override
    public void render() {
        fogColor.x = Math.clamp(0, 1, fogColor.x);
        fogColor.y = Math.clamp(0, 1, fogColor.y);
        fogColor.z = Math.clamp(0, 1, fogColor.z);

        GL11.glClearColor(fogColor.x, fogColor.y, fogColor.z, 0f);

        renderer.render(mouseInput, this);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        loader.cleanup();
    }

    public ElementEditing ElementEditing;
    public ProjectManager ProjectManager;
    public Archive Archive;
    public Settings Settings;
    public MaterialEditing MaterialEditing;

    private void createUI() {

        int xCoord = 325;
        int w = 125;
        int yCoord = 3;
        Button elementEditing = new Button("elementEditing", "Scene", new Vector2f(xCoord, yCoord), new Vector2f(w, 21), renderer, loader, window) {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)setCurrentScreen(ElementEditing);
            }

            @Override
            public void draw(MouseInput mouseInput, boolean overOther) {
                super.draw(mouseInput, overOther);
            }
        };
        xCoord += w - 1;
        Button archive = new Button("archive", "Archive", new Vector2f(xCoord, yCoord), new Vector2f(w, 21), renderer, loader, window) {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)setCurrentScreen(Archive);
            }

            @Override
            public void draw(MouseInput mouseInput, boolean overOther) {
                super.draw(mouseInput, overOther);
            }
        };
        xCoord += w - 1;
        Button project = new Button("project", "Project", new Vector2f(xCoord, yCoord), new Vector2f(w, 21), renderer, loader, window) {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)setCurrentScreen(ProjectManager);
            }

            @Override
            public void draw(MouseInput mouseInput, boolean overOther) {
                super.draw(mouseInput, overOther);
            }
        };
        xCoord += w - 1;
        Button settingss = new Button("settingss", "Settings", new Vector2f(xCoord, yCoord), new Vector2f(w, 21), renderer, loader, window) {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)setCurrentScreen(Settings);
            }

            @Override
            public void draw(MouseInput mouseInput, boolean overOther) {super.draw(mouseInput, overOther);}
        };

        ButtonImage minimizeButton = new ButtonImage("minimizeButton", new Vector2f(window.width - 181, yCoord), new Vector2f(60, 21), new Vector2f(23), renderer, loader, window) {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)
                    window.minimize();
            }

            @Override
            public void draw(MouseInput mouseInput, boolean overOther) {
                this.pos.x = window.width - 181;
                super.draw(mouseInput, overOther);
            }

            @Override
            public Texture getImage() {
                return ConstantTextures.getTexture(ConstantTextures.WINDOW_MINIMIZE, 23, 23, loader);
            }
        };

        ButtonImage restMaxButton = new ButtonImage("restMaxButton", new Vector2f(window.width - 122, yCoord), new Vector2f(60, 21), new Vector2f(23, 23), renderer, loader, window) {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)
                {
                    if(this.window.isMaximized)
                        this.window.restore();
                    else
                        this.window.maximize();
                }
            }

            @Override
            public void draw(MouseInput mouseInput, boolean overOther) {
                this.pos.x = window.width - 122;
                super.draw(mouseInput, overOther);
            }

            @Override
            public void secondThread() {
                super.secondThread();
            }

            @Override
            public Texture getImage() {
                return ConstantTextures.getTexture(this.window.isMaximized ? ConstantTextures.WINDOW_RESTORE : ConstantTextures.WINDOW_MAXIMIZE, 23, 23, loader);
            }
        };

        ButtonImage closeButton = new ButtonImage("closeButton", new Vector2f(window.width - 63, yCoord), new Vector2f(60, 21), new Vector2f(23, 23), renderer, loader, window) {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)
                    window.close();
            }

            @Override
            public void draw(MouseInput mouseInput, boolean overOther) {
                this.pos.x = window.width - 63;

                super.draw(mouseInput, overOther);
            }

            @Override
            public Texture getImage() {
                return ConstantTextures.getTexture(ConstantTextures.WINDOW_CLOSE, 23, 23, loader);
            }
        };

        final long[] prevMs = {0};

        Element titleBar = new Element() {
            @Override
            public void secondThread() {
                this.size.x = window.width - 6;
            }

            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement, boolean focusedOther) {
                super.onClick(mouseInput, pos, button, action, mods, overElement, focusedOther);

                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && !overElement)
                {
                    if(pos.x > this.pos.x && pos.x < this.pos.x + this.size.x &&
                            pos.y > this.pos.y && pos.y < this.pos.y + this.size.y - 2)
                    {
                        if(action == GLFW.GLFW_PRESS)
                        {
                            long currentMs = System.currentTimeMillis();
                            if(currentMs - prevMs[0] <= 500)
                            {
                                if(this.window.isMaximized)
                                    this.window.restore();
                                else
                                    this.window.maximize();
                                prevMs[0] = 0;
                            }
                            else
                            {
                                prevMs[0] = currentMs;
                                this.window.isDragging = true;
                                if(!this.window.isMaximized)
                                    this.window.setOpacity(0.75f);
                                Vector2i windowPos = this.window.getWindowPosition();
                                Vector2d cursorPos = this.window.getCursorPosition();
                                this.window.prevCursor = new Vector2d(cursorPos.x / this.window.width, cursorPos.y);
                                this.window.prevWindow = windowPos;
                            }
                        }
                    }

                    if(action == GLFW.GLFW_PRESS)
                    {
                        boolean top = mouseInput.currentPos.y <= 3;
                        boolean bottom = mouseInput.currentPos.y >= window.height - 3;
                        boolean left = mouseInput.currentPos.x <= 3;
                        boolean right = mouseInput.currentPos.x >= window.width - 3;

                        this.window.resizing = Utils.setBitwiseBool(this.window.resizing, WindowMan.RESIZE_TOP, top);
                        this.window.resizing = Utils.setBitwiseBool(this.window.resizing, WindowMan.RESIZE_BOTTOM, bottom);
                        this.window.resizing = Utils.setBitwiseBool(this.window.resizing, WindowMan.RESIZE_LEFT, left);
                        this.window.resizing = Utils.setBitwiseBool(this.window.resizing, WindowMan.RESIZE_RIGHT, right);

                        this.window.prevX = this.window.getWindowPosition().x;
                        this.window.prevY = this.window.getWindowPosition().y;
                        this.window.prevWidth = this.window.width;
                        this.window.prevHeight = this.window.height;
                        this.window.prevMousePos = new Vector2d(pos);
                    }
                }
            }

            @Override
            public void draw(MouseInput mouseInput, boolean overElement) {
                super.draw(mouseInput, overElement);

                if(!mouseInput.leftButtonPress)
                {
                    if(this.window.resizing != 0)
                    {
                        this.window.resizing = 0;
                        this.window.width = this.window.newWidth;
                        this.window.height = this.window.newHeight;
                        this.window.resize = true;
                    }

                    if(this.window.isDragging)
                    {
                        this.window.isDragging = false;
                        this.window.setOpacity(1f);
                    }
                }
            }
        };
        titleBar.window = window;
        titleBar.pos = new Vector2f(3, 3);
        titleBar.size = new Vector2f(window.width - 6, 23);
        titleBar.id = "titleBar";

        ElementEditing = new ElementEditing(this);

        ElementEditing.guiElements.add(notificationFeed);
        ElementEditing.guiElements.add(titleBar);
        ElementEditing.guiElements.add(minimizeButton);
        ElementEditing.guiElements.add(restMaxButton);
        ElementEditing.guiElements.add(closeButton);
        ElementEditing.guiElements.add(elementEditing);
        ElementEditing.guiElements.add(archive);
        ElementEditing.guiElements.add(project);
        ElementEditing.guiElements.add(settingss);

        Archive = new Archive(this);

        Archive.guiElements.add(notificationFeed);
        Archive.guiElements.add(titleBar);
        Archive.guiElements.add(minimizeButton);
        Archive.guiElements.add(restMaxButton);
        Archive.guiElements.add(closeButton);
        Archive.guiElements.add(elementEditing);
        Archive.guiElements.add(archive);
        Archive.guiElements.add(project);
        Archive.guiElements.add(settingss);

        ProjectManager = new ProjectManager(this);

        ProjectManager.guiElements.add(notificationFeed);
        ProjectManager.guiElements.add(titleBar);
        ProjectManager.guiElements.add(minimizeButton);
        ProjectManager.guiElements.add(restMaxButton);
        ProjectManager.guiElements.add(closeButton);
        ProjectManager.guiElements.add(elementEditing);
        ProjectManager.guiElements.add(archive);
        ProjectManager.guiElements.add(project);
        ProjectManager.guiElements.add(settingss);

        Settings = new Settings(this);

        Settings.guiElements.add(notificationFeed);
        Settings.guiElements.add(titleBar);
        Settings.guiElements.add(minimizeButton);
        Settings.guiElements.add(restMaxButton);
        Settings.guiElements.add(closeButton);
        Settings.guiElements.add(elementEditing);
        Settings.guiElements.add(archive);
        Settings.guiElements.add(project);
        Settings.guiElements.add(settingss);

        MaterialEditing = new MaterialEditing(this);
        MaterialEditing.guiElements.add(titleBar);
        MaterialEditing.guiElements.add(minimizeButton);
        MaterialEditing.guiElements.add(restMaxButton);
        MaterialEditing.guiElements.add(closeButton);

        setCurrentScreen(ElementEditing);

        overrideScreen = new OverrideScreen(this);
    }

    public boolean introPlayed = false;

    public void pushNotification(Notification notification)
    {
        notificationFeed.pushNotification(notification);
    }

    public void pushError(String title, String content)
    {
        notificationFeed.pushNotification(new Notification(15000l, true) {
            @Override
            public String getTitle() {
                return title;
            }

            @Override
            public String getContent() {
                return content;
            }

            @Override
            public void closeNotification() {
                notificationFeed.notifications.remove(this);
            }

            @Override
            public Color backgroundColor() {
                return new Color(255, 84, 41, Config.INTERFACE_PRIMARY_COLOR.getAlpha());
            }

            @Override
            public Color outlineColor() {
                return new Color(255, 84, 41, Config.INTERFACE_PRIMARY_COLOR2.getAlpha());
            }
        });
    }

    public void pushWarning(String title, String content)
    {
        notificationFeed.pushNotification(new Notification(5000l) {
            @Override
            public String getTitle() {
                return title;
            }

            @Override
            public String getContent() {
                return content;
            }

            @Override
            public void closeNotification() {
                notificationFeed.notifications.remove(this);
            }

            @Override
            public Color backgroundColor() {
                return new Color(245, 125, 27, Config.INTERFACE_PRIMARY_COLOR.getAlpha());
            }

            @Override
            public Color outlineColor() {
                return new Color(245, 125, 27, Config.INTERFACE_PRIMARY_COLOR2.getAlpha());
            }
        });
    }

    public void pushSuccess(String title, String content)
    {
        notificationFeed.pushNotification(new Notification(5000l) {
            @Override
            public String getTitle() {
                return title;
            }

            @Override
            public String getContent() {
                return content;
            }

            @Override
            public void closeNotification() {
                notificationFeed.notifications.remove(this);
            }

            @Override
            public Color backgroundColor() {
                return new Color(34, 153, 38, Config.INTERFACE_PRIMARY_COLOR.getAlpha());
            }

            @Override
            public Color outlineColor() {
                return new Color(34, 153, 38, Config.INTERFACE_PRIMARY_COLOR2.getAlpha());
            }
        });
    }

    public void pushNotification(String title, String content)
    {
        notificationFeed.pushNotification(new Notification(5000l) {
            @Override
            public String getTitle() {
                return title;
            }

            @Override
            public String getContent() {
                return content;
            }

            @Override
            public void closeNotification() {
                notificationFeed.notifications.remove(this);
            }

            @Override
            public Color backgroundColor() {
                return Config.INTERFACE_PRIMARY_COLOR;
            }

            @Override
            public Color outlineColor() {
                return Config.INTERFACE_PRIMARY_COLOR2;
            }
        });
    }

    private void drawUI(MouseInput mouseInput) {

        boolean top = mouseInput.currentPos.y <= 3;
        boolean bottom = mouseInput.currentPos.y >= window.height - 3;
        boolean left = mouseInput.currentPos.x <= 3;
        boolean right = mouseInput.currentPos.x >= window.width - 3;

        if((top && left) || (bottom && right))
        {
            Cursors.setCursor(ECursor.bd_double_arrow);
        }
        else if((top && right) || (bottom && left))
        {
            Cursors.setCursor(ECursor.fd_double_arrow);
        }
        else if(top || bottom)
        {
            Cursors.setCursor(ECursor.sb_v_double_arrow);
        }
        else if(left || right)
        {
            Cursors.setCursor(ECursor.sb_h_double_arrow);
        }

        renderer.doBlur(Consts.GAUSSIAN_RADIUS, Consts.GAUSSIAN_KERNEL, 3, 3, window.width, 20);
        renderer.drawRect(3, 3, window.width - 6, 20, Config.PRIMARY_COLOR);

        renderer.drawRect(0, 3, 3, window.height - 6, Config.PRIMARY_COLOR);
        renderer.drawRect(0, 0, window.width, 3, Config.PRIMARY_COLOR);
        renderer.drawRect(0, window.height - 3, window.width, window.height, Config.PRIMARY_COLOR);
        renderer.drawRect(window.width - 3, 3, window.width, window.height - 6, Config.PRIMARY_COLOR);

        renderer.drawHeader(Consts.FONT_SET_BOLD + Consts.TITLE + " (v" + Consts.VERSION + ")" + (Config.SHOW_FPS ? " | FPS: " + EngineMan.avgFPS : ""), Config.FONT_COLOR, 7 + 3, (int)(11 - (getFontHeightHeader() / 2) + 3));

//        double bounce = (-java.lang.Math.pow(1f- (((float)(System.currentTimeMillis() % 5000))/5000f) *2f,2f)+1f) * 100;
//
//        renderer.drawHeader(Consts.FONT_SET_BOLD + "\"',.hijklmnxyz{|}~ ¡¢£¤¥¦§¨©ª«¬\u00AD®¯°±²³", Config.FONT_COLOR, 7 + 3, 30, (int) bounce);
//        renderer.drawHeader("\"',.hijklmnxyz{|}~ ¡¢£¤¥¦§¨©ª«¬\u00AD®¯°±²³", Config.FONT_COLOR, 7 + 3, 220, (int) bounce);

//        drawString("entRen ent: " + renderer.entityRenderer.entities.size(), Config.FONT_COLOR, 10, 32, 10);
//        drawString("entRen dir L: " + renderer.entityRenderer.directionalLights.size(), Config.FONT_COLOR, 10, 42, 10);
//        drawString("entRen poi L: " + renderer.entityRenderer.pointLights.size(), Config.FONT_COLOR, 10, 52, 10);
//        drawString("entRen spo L: " + renderer.entityRenderer.spotLights.size(), Config.FONT_COLOR, 10, 62, 10);
//        drawString("entRen thr ent: " + renderer.entityRenderer.throughWallEntities.size(), Config.FONT_COLOR, 10, 72, 10);
//        drawString("guiRen ele: " + renderer.guiRenderer.elements.size(), Config.FONT_COLOR, 10, 82, 10);

//        renderer.drawString("drawn things: " + renderer.entityRenderer.drawnThingMeshes, Config.FONT_COLOR, 10, 82, 10);

        if(Main.debug)
        {
            if(((Settings)this.Settings).debugOverlayImage.isChecked && (((Settings)this.Settings).overlayImage != null))
                renderer.drawImageStatic(((Settings)this.Settings).overlayImage.id, 0, 0, window.width, window.height, new Color(1f, 1f, 1f, 0.6f));

            if(((Settings)this.Settings).debugScissorTest.isChecked) {
                renderer.startScissor(20, 50, 120 - 20, 150 - 50);
                renderer.drawRect(0, 0, window.width, window.height, Color.red);
                renderer.startScissor(30, 60, 110 - 30, 140 - 60);
                renderer.drawRect(0, 0, window.width, window.height, Color.green);
                renderer.startScissor(40, 70, 100 - 40, 130 - 70);
                renderer.drawRect(0, 0, window.width, window.height, Color.blue);

                renderer.startScissor(50, 80, 60 - 50, 90 - 80);
                renderer.drawRect(0, 0, window.width, window.height, Color.cyan);
                renderer.endScissor();

                renderer.startScissor(65, 75, 90 - 70, 100 - 80);
                renderer.startScissor(70, 80, 80 - 70, 90 - 80);

                renderer.startScissorEscape();
                renderer.drawRect(0, 0, window.width, window.height, Color.gray);
                renderer.endScissorEscape();

                renderer.drawRect(0, 0, window.width, window.height, Color.orange);
                renderer.endScissor();
                renderer.endScissor();

                renderer.startScissor(50, 100, 80 - 70, 90 - 80);
                renderer.drawRect(0, 0, window.width, window.height, Color.magenta);
                renderer.endScissor();

                renderer.endScissor();
                renderer.endScissor();
                renderer.endScissor();
            }

            boolean vao = ((Checkbox) ((Settings) Settings).debug.tabElements.get(0)).isChecked;
            boolean vbo = ((Checkbox) ((Settings) Settings).debug.tabElements.get(1)).isChecked;
            boolean tex = ((Checkbox) ((Settings) Settings).debug.tabElements.get(2)).isChecked;
            boolean thread = ((Checkbox) ((Settings) Settings).debug.tabElements.get(3)).isChecked;

            int l = 1;

            if (vao)
            {
                renderer.drawRect(10 - 3, 20 - 3 + ((getFontHeight(Config.GUI_SCALE) + 2) + 3) * l, getStringWidth("VAOs: " + loader.vaos.size(), Config.GUI_SCALE) + 6, (getFontHeight(10) + 2) + 3, new Color(0f, 0f, 0f, 0.5f));
                renderer.drawString("VAOs: " + loader.vaos.size(), Config.FONT_COLOR, 20 + ((getFontHeight(Config.GUI_SCALE) + 2) + 3) * l, Config.GUI_SCALE);
                l++;
            }
            if (vbo)
            {
                renderer.drawRect(10 - 3, 20 - 3 + ((getFontHeight(Config.GUI_SCALE) + 2) + 3) * l, getStringWidth("VBOs: " + loader.vbos.size(), Config.GUI_SCALE) + 6, (getFontHeight(10) + 2) + 3, new Color(0f, 0f, 0f, 0.5f));
                renderer.drawString("VBOs: " + loader.vbos.size(), Config.FONT_COLOR, 20 + ((getFontHeight(Config.GUI_SCALE) + 2) + 3) * l, Config.GUI_SCALE);
                l++;
            }
            if (tex)
            {
                renderer.drawRect(10 - 3, 20 - 3 + ((getFontHeight(Config.GUI_SCALE) + 2) + 3) * l, getStringWidth("Textures: " + loader.textures.size(), Config.GUI_SCALE) + 6, (getFontHeight(10) + 2) + 3, new Color(0f, 0f, 0f, 0.5f));
                renderer.drawString("Textures: " + loader.textures.size(), Config.FONT_COLOR, 20 + ((getFontHeight(Config.GUI_SCALE) + 2) + 3) * l, Config.GUI_SCALE);
                l++;
            }
            if(thread)
            {
                ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
                long[] threadIds = threadMXBean.getAllThreadIds();
                for (long id : threadIds) {
                    long cpuTime = threadMXBean.getThreadCpuTime(id) / 1000000l;
                    String s = "Thread ID: " + id + " CPU Time: " + cpuTime + " ms";
                    renderer.drawRect(10 - 3, 20 - 3 + ((getFontHeight(10) + 2) + 3) * l, getStringWidth(s, Config.GUI_SCALE) + 6, (getFontHeight(Config.GUI_SCALE) + 2) + 3, new Color(0f, 0f, 0f, 0.5f));
                    renderer.drawString(s, Config.FONT_COLOR, 20 + ((getFontHeight(Config.GUI_SCALE) + 2) + 3) * l, Config.GUI_SCALE);
                    l++;
                }
            }
        }

        Vector2d prev = mouseInput.currentPos;
        if(!introPlayed)
            mouseInput.currentPos = new Vector2d();

        if(currentScreen != null)
            currentScreen.draw(((OverrideScreen)overrideScreen).shadingMenu ? new MouseInput(null) : mouseInput);
        if(overrideScreen != null)
            overrideScreen.draw(mouseInput);

        renderer.drawLine(topBarLine, Config.SECONDARY_COLOR,false);
        renderer.drawLineStrip(windowFrame, new Vector2f(3, 3), Color.black, false);
        renderer.drawLineStrip(windowFrameOuter, new Vector2f(0), Color.black, false);

        if(!introPlayed)
        {
            long msPassed = System.currentTimeMillis() - initMillis;

            int introDuration = 1300;

            Cursors.setCursor(ECursor.left_ptr_watch);
            mouseInput.currentPos = prev;
            float out = 3f - msPassed / (float)introDuration;
            float in = Math.clamp(0f, 1f, out - 1.5f);
            out = Math.clamp(0f, 1f, out);

            if (out != 0) {
                renderer.drawRect(0, 0, window.width, window.height, new Color(1f, 1f, 1f, out));
                renderer.drawImageStatic(ConstantTextures.getTexture(ConstantTextures.ICON, 461, 461, loader), window.width / 2 - 461 / 2, window.height / 2 - 461 / 2, 461, 461, new Color(1f, 1f, 1f, 1f - in), loader);

                String splashText = "Welcome to LBP Asset Studio v" + Consts.VERSION;
                StringBuilder target = new StringBuilder();

                String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
                int DURATION = (int) (introDuration * 1.5f);
                double FRAME_DELAY = EngineMan.ms;

                int frameCount = (int) (DURATION / FRAME_DELAY);
                int totalFrames = splashText.length() * frameCount;

                Random random = new Random();

                for (int i = 0; i < totalFrames; i++) {
                    int charIndex = (i / frameCount);
                    int currentTime = (int) Math.min(msPassed, DURATION);
                    char randomChar = CHARACTERS.charAt(random.nextInt(CHARACTERS.length()));

                    if (currentTime > (DURATION / splashText.length()) * charIndex) {

                        if(target.length() <= charIndex)
                            target.append(splashText.charAt(charIndex));
                        else
                            target.setCharAt(charIndex, splashText.charAt(charIndex));
                    }
                    else if (i % frameCount == 0 && charIndex < splashText.length()) {
                        target.append(randomChar);
                        break;
                    }

                    if (charIndex >= splashText.length()) {
                        break;
                    }
                }

                String finalSplash = Consts.FONT_SET_BOLD + target.toString();

                FontRenderer.drawString(renderer, finalSplash, window.width / 2 - getStringWidthHeader(splashText, 40) / 2, window.height / 2 + 461 / 2 + 10, 40, Config.OUTLINE_COLOR, 0, finalSplash.length(), FontRenderer.Fonts.get(FontRenderer.headerFont));

                renderer.drawRect(0, 0, window.width, window.height, new Color(1f, 0f, 0f, 0f));
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

    public int getStringWidth(String text)
    {
        return (int)FontRenderer.getStringWidth(text, Config.GUI_SCALE, FontRenderer.Fonts.get(FontRenderer.textFont));
    }

    public int getStringWidth(String text, int size)
    {
        return (int)FontRenderer.getStringWidth(text, size, FontRenderer.Fonts.get(FontRenderer.textFont));
    }

    public int getFontHeight()
    {
        return (int)FontRenderer.getFontHeight(Config.GUI_SCALE, FontRenderer.Fonts.get(FontRenderer.textFont));
    }

    public int getFontHeight(int size)
    {
        return (int)FontRenderer.getFontHeight(size, FontRenderer.Fonts.get(FontRenderer.textFont));
    }

    public int getStringWidthHeader(String text, int size)
    {
        return (int)FontRenderer.getStringWidth(text, size, FontRenderer.Fonts.get(FontRenderer.headerFont));
    }

    public int getStringWidthHeader(String text)
    {
        return (int)FontRenderer.getStringWidth(text, Math.round(Config.GUI_SCALE * 1.25f), FontRenderer.Fonts.get(FontRenderer.headerFont));
    }

    public int getFontHeightHeader()
    {
        return (int)FontRenderer.getFontHeight(Math.round(Config.GUI_SCALE * 1.25f), FontRenderer.Fonts.get(FontRenderer.headerFont));
    }

    public int getFontHeightHeader(int size)
    {
        return (int)FontRenderer.getFontHeight(size, FontRenderer.Fonts.get(FontRenderer.headerFont));
    }

    public void copySelected()
    {
        String copySelection = "";
        for(Entity entity : things)
            if(entity.selected)
            {
                Thing thing = ((bog.lbpas.view3d.core.types.Thing) entity).thing;
                GsonUtils.REVISION = new Revision(Branch.MIZUKI.getHead(), Branch.MIZUKI.getID(), Branch.MIZUKI.getRevision());
                copySelection += GsonUtils.toJSON(thing) + Consts.LEFT_ARROW_WITH_SMALL_CIRCLE;
            }

        if(copySelection.equalsIgnoreCase(""))
            return;

        try {
            String encrypted = Utils.encrypt(copySelection);

            StringSelection selection = new StringSelection("LBPAS" + new String(encrypted));
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);
        }catch (Exception e){e.printStackTrace();}
    }

    public void pasteClipboard()
    {
        String clipboard = "";

        for(Entity entity : things)
            if(entity.selected)
                entity.selected = false;

        try
        {
            String cb = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);

            if(!cb.substring(0, 5).equalsIgnoreCase("LBPAS"))
                return;

            clipboard = Utils.decrypt(cb.substring(5));
        }catch (Exception e){e.printStackTrace();}

        if(clipboard.equalsIgnoreCase(""))
            return;

        String[] objects = clipboard.split(String.valueOf(Consts.LEFT_ARROW_WITH_SMALL_CIRCLE));

        ArrayList<Thing> things = new ArrayList<>();

        for(int i = 0; i < objects.length; i++)
        {
            String json = objects[i];
            if(json == "")
                continue;

            things.add(GsonUtils.GSON.fromJson(json, Thing.class));
        }

        addThings(things, null);
    }

    public void deleteSelected()
    {
        for (int i = things.size() - 1; i >= 0; i--)
            if (things.get(i).selected)
                deleteEntity(i);
    }

    public void clearEntities()
    {
        for (int i = things.size() - 1; i >= 0; i--)
            deleteEntity(i);
    }

    public void deleteEntity(int index)
    {
        bog.lbpas.view3d.core.types.Thing thing = (bog.lbpas.view3d.core.types.Thing) things.get(index);
        thing.cleanup();

        for (int i = things.size() - 1; i >= 0; i--)
        {
            if(things.get(i).thing.parent == thing.thing)
                things.get(i).thing.parent = null;
            if(things.get(i).thing.groupHead == thing.thing)
                things.get(i).thing.groupHead = null;

            if(things.get(i).renderMesh != null)
            {
                Thing[] boneThings = ((PRenderMesh)things.get(i).thing.getPart(Part.RENDER_MESH)).boneThings;
                for(int bt = 0; bt < boneThings.length; bt++)
                    if(boneThings[bt] == thing.thing)
                        boneThings[bt] = null;
            }
        }

        things.remove(index);
    }

    public void setSelectedPosition(Vector3f pos)
    {
        boolean hasSelection = false;
        ArrayList<Integer> selected = new ArrayList<>();

        for(int i = 0; i < things.size(); i++)
            if(things.get(i).selected)
            {
                hasSelection = true;
                selected.add(i);
            }

        if(selected.size() == 1)
            for(int i : selected)
            {
                things.get(i).setTransformation(things.get(i).getTransformation().setTranslation(new Vector3f(pos)));
            }
        else
        {
            Vector3f avgpos = new Vector3f(getSelectedPosition());

            for(int i : selected)
            {
                Entity entity = things.get(i);
                Vector3f curTransl = new Vector3f(entity.getTransformation().getTranslation(new Vector3f()));
                Vector3f diff = new Vector3f(new Vector3f(curTransl).sub(avgpos, new Vector3f()));
                entity.setTransformation(entity.getTransformation().setTranslation(new Vector3f(pos).add(diff, new Vector3f())));
            }
        }
    }

    public void setSelectedParent(Thing parent)
    {
        for(int i = 0; i < things.size(); i++)
            if(things.get(i).selected)
            {
                things.get(i).thing.parent = parent;
                if(things.get(i).thing.hasPart(Part.POS))
                    ((PPos)things.get(i).thing.getPart(Part.POS)).recomputeLocalPos(things.get(i).thing);
            }
    }

    public void setSelectedGroup(Thing groupHead)
    {
        for(int i = 0; i < things.size(); i++)
            if(things.get(i).selected)
            {
                things.get(i).thing.groupHead = groupHead;
            }
    }

    public Thing getSelectedParent()
    {
        Thing parent = null;
        int index = -1;
        for(int i = 0; i < things.size(); i++)
        {
            if(things.get(i) == null)
                continue;
            if(things.get(i).selected)
            {
                if(index != -1 && parent != things.get(i).thing.parent)
                    return null;

                parent = things.get(i).thing.parent;
                index = i;
            }
        }
        return parent;
    }

    public Thing getSelectedGroup()
    {
        Thing group = null;
        int index = -1;
        for(int i = 0; i < things.size(); i++)
        {
            if(things.get(i) != null && things.get(i).selected)
            {
                if(index != -1 && group != things.get(i).thing.groupHead)
                    return null;

                group = things.get(i).thing.groupHead;
                index = i;
            }
        }
        return group;
    }

    public Vector3f getSelectedPosition()
    {
        boolean hasSelection = false;
        ArrayList<Integer> selected = new ArrayList<>();

        for(int i = 0; i < things.size(); i++)
            if(things.get(i).selected)
            {
                hasSelection = true;
                selected.add(i);
            }

        if(selected.size() == 1)
        {
            for (int i : selected)
                return new Vector3f(things.get(i).getTransformation().getTranslation(new Vector3f()));
        }
        else
        {
            float x = 0;
            float y = 0;
            float z = 0;
            int amount = 0;

            for (int i : selected)
                {
                    Vector3f curTransl = things.get(i).getTransformation().getTranslation(new Vector3f());
                    x += curTransl.x;
                    y += curTransl.y;
                    z += curTransl.z;
                    amount++;
                }

            return new Vector3f(x/amount, y/amount, z/amount);

        }

        return new Vector3f(Consts.NaNf, Consts.NaNf, Consts.NaNf);
    }

//    public String getSelectedMeshDescriptor()
//    {
//        boolean hasSelection = false;
//        ArrayList<Integer> selected = new ArrayList<>();
//
//        for(int i = 0; i < entities.size(); i++)
//            if(entities.get(i).selected)
//            {
//                hasSelection = true;
//                selected.add(i);
//            }
//
//        if(selected.size() > 0)
//        {
//            if(selected.size() == 1)
//                return ((Mesh)entities.get(selected.get(0))).meshDescriptor.isGUID() ? ((Mesh)entities.get(selected.get(0))).meshDescriptor.getGUID().toString() : "h" + ((Mesh)entities.get(selected.get(0))).meshDescriptor.getSHA1().toString();
//            else if(selected.size() > 1)
//            {
//                boolean isSame = true;
//                String descriptor = ((Mesh)entities.get(selected.get(0))).meshDescriptor.isGUID() ? ((Mesh)entities.get(selected.get(0))).meshDescriptor.getGUID().toString() : "h" + ((Mesh)entities.get(selected.get(0))).meshDescriptor.getSHA1().toString();
//                for(int i : selected)
//                {
//                    Mesh entity = (Mesh) entities.get(i);
//                    String desc = entity.meshDescriptor.isGUID() ? entity.meshDescriptor.getGUID().toString() : "h" + entity.meshDescriptor.getSHA1().toString();
//
//                    if(!descriptor.equalsIgnoreCase(desc))
//                        isSame = false;
//                }
//
//                if(isSame)
//                    return descriptor;
//            }
//        }
//
//        return "";
//    }
//
//    public String getSelectedGfxMaterialDescriptor()
//    {
//        boolean hasSelection = false;
//        ArrayList<Integer> selected = new ArrayList<>();
//
//        for(int i = 0; i < entities.size(); i++)
//            if(entities.get(i).selected)
//            {
//                hasSelection = true;
//                selected.add(i);
//            }
//
//        if(selected.size() > 0 && ((MaterialPrimitive)entities.get(selected.get(0))).gmat != null)
//        {
//            if(selected.size() == 1)
//                return ((MaterialPrimitive)entities.get(selected.get(0))).gmat.isGUID() ? ((MaterialPrimitive)entities.get(selected.get(0))).gmat.getGUID().toString() : "h" + ((MaterialPrimitive)entities.get(selected.get(0))).gmat.getSHA1().toString();
//            else if(selected.size() > 1)
//            {
//                boolean isSame = true;
//                String descriptor = ((MaterialPrimitive)entities.get(selected.get(0))).gmat.isGUID() ? ((MaterialPrimitive)entities.get(selected.get(0))).gmat.getGUID().toString() : "h" + ((MaterialPrimitive)entities.get(selected.get(0))).gmat.getSHA1().toString();
//                for(int i : selected)
//                {
//                    MaterialPrimitive entity = (MaterialPrimitive) entities.get(i);
//                    String desc = entity.gmat.isGUID() ? entity.gmat.getGUID().toString() : "h" + entity.gmat.getSHA1().toString();
//
//                    if(!descriptor.equalsIgnoreCase(desc))
//                        isSame = false;
//                }
//
//                if(isSame)
//                    return descriptor;
//            }
//        }
//
//        return "";
//    }
//
//    public String getSelectedMaterialDescriptor()
//    {
//        boolean hasSelection = false;
//        ArrayList<Integer> selected = new ArrayList<>();
//
//        for(int i = 0; i < entities.size(); i++)
//            if(entities.get(i).selected)
//            {
//                hasSelection = true;
//                selected.add(i);
//            }
//
//        if(selected.size() > 0 && ((MaterialPrimitive)entities.get(selected.get(0))).mat != null)
//        {
//            if(selected.size() == 1)
//                return ((MaterialPrimitive)entities.get(selected.get(0))).mat.isGUID() ? ((MaterialPrimitive)entities.get(selected.get(0))).mat.getGUID().toString() : "h" + ((MaterialPrimitive)entities.get(selected.get(0))).mat.getSHA1().toString();
//            else if(selected.size() > 1)
//            {
//                boolean isSame = true;
//                String descriptor = ((MaterialPrimitive)entities.get(selected.get(0))).mat.isGUID() ? ((MaterialPrimitive)entities.get(selected.get(0))).mat.getGUID().toString() : "h" + ((MaterialPrimitive)entities.get(selected.get(0))).mat.getSHA1().toString();
//                for(int i : selected)
//                {
//                    MaterialPrimitive entity = (MaterialPrimitive) entities.get(i);
//                    String desc = entity.mat.isGUID() ? entity.mat.getGUID().toString() : "h" + entity.mat.getSHA1().toString();
//
//                    if(!descriptor.equalsIgnoreCase(desc))
//                        isSame = false;
//                }
//
//                if(isSame)
//                    return descriptor;
//            }
//        }
//
//        return "";
//    }
//
//    public String getSelectedBevelDescriptor()
//    {
//        boolean hasSelection = false;
//        ArrayList<Integer> selected = new ArrayList<>();
//
//        for(int i = 0; i < entities.size(); i++)
//            if(entities.get(i).selected)
//            {
//                hasSelection = true;
//                selected.add(i);
//            }
//
//        if(selected.size() > 0 && ((MaterialPrimitive)entities.get(selected.get(0))).bev != null)
//        {
//            if(selected.size() == 1)
//                return ((MaterialPrimitive)entities.get(selected.get(0))).bev.isGUID() ? ((MaterialPrimitive)entities.get(selected.get(0))).bev.getGUID().toString() : "h" + ((MaterialPrimitive)entities.get(selected.get(0))).bev.getSHA1().toString();
//            else if(selected.size() > 1)
//            {
//                boolean isSame = true;
//
//                String descriptor = ((MaterialPrimitive)entities.get(selected.get(0))).bev.isGUID() ? ((MaterialPrimitive)entities.get(selected.get(0))).bev.getGUID().toString() : "h" + ((MaterialPrimitive)entities.get(selected.get(0))).bev.getSHA1().toString();
//                for(int i : selected)
//                {
//                    MaterialPrimitive entity = (MaterialPrimitive) entities.get(i);
//                    String desc = entity.bev.isGUID() ? entity.bev.getGUID().toString() : "h" + entity.bev.getSHA1().toString();
//
//                    if(!descriptor.equalsIgnoreCase(desc))
//                        isSame = false;
//                }
//
//                if(isSame)
//                    return descriptor;
//            }
//        }
//
//        return "";
//    }
//
//    public float getSelectedMaterialThickness()
//    {
//        boolean hasSelection = false;
//        ArrayList<Integer> selected = new ArrayList<>();
//
//        for(int i = 0; i < entities.size(); i++)
//            if(entities.get(i).selected)
//            {
//                hasSelection = true;
//                selected.add(i);
//            }
//
//        if(selected.size() > 0)
//        {
//            if(selected.size() == 1)
//                return ((MaterialPrimitive)entities.get(selected.get(0))).shape.thickness;
//            else if(selected.size() > 1)
//            {
//                boolean isSame = true;
//                float thickness = ((MaterialPrimitive)entities.get(selected.get(0))).shape.thickness;
//                for(int i : selected)
//                {
//                    MaterialPrimitive entity = (MaterialPrimitive) entities.get(i);
//
//                    if(entity.shape.thickness != thickness)
//                        isSame = false;
//                }
//
//                if(isSame)
//                    return thickness;
//            }
//        }
//
//        return Consts.NaNf;
//    }
//
//    public float getSelectedBevelSize()
//    {
//        boolean hasSelection = false;
//        ArrayList<Integer> selected = new ArrayList<>();
//
//        for(int i = 0; i < entities.size(); i++)
//            if(entities.get(i).selected)
//            {
//                hasSelection = true;
//                selected.add(i);
//            }
//
//        if(selected.size() > 0)
//        {
//            if(selected.size() == 1)
//                return ((MaterialPrimitive)entities.get(selected.get(0))).shape.bevelSize;
//            else if(selected.size() > 1)
//            {
//                boolean isSame = true;
//                float bevelSize = ((MaterialPrimitive)entities.get(selected.get(0))).shape.bevelSize;
//                for(int i : selected)
//                {
//                    MaterialPrimitive entity = (MaterialPrimitive) entities.get(i);
//
//                    if(entity.shape.bevelSize != bevelSize)
//                        isSame = false;
//                }
//
//                if(isSame)
//                    return bevelSize;
//            }
//        }
//
//        return Consts.NaNf;
//    }
//
//    public String getSelectedSoundName()
//    {
//        boolean hasSelection = false;
//        ArrayList<Integer> selected = new ArrayList<>();
//
//        for(int i = 0; i < entities.size(); i++)
//            if(entities.get(i).selected)
//            {
//                hasSelection = true;
//                selected.add(i);
//            }
//
//        if(selected.size() > 0)
//        {
//            if(selected.size() == 1)
//                return ((WorldAudio)entities.get(selected.get(0))).soundName;
//            else if(selected.size() > 1)
//            {
//                boolean isSame = true;
//                String soundName = ((WorldAudio)entities.get(selected.get(0))).soundName;
//                for(int i : selected)
//                {
//                    WorldAudio entity = (WorldAudio) entities.get(i);
//
//                    if(entity.soundName != soundName)
//                        isSame = false;
//                }
//
//                if(isSame)
//                    return soundName;
//            }
//        }
//
//        return "";
//    }
//
//    public float getSelectedSoundVolume()
//    {
//        boolean hasSelection = false;
//        ArrayList<Integer> selected = new ArrayList<>();
//
//        for(int i = 0; i < entities.size(); i++)
//            if(entities.get(i).selected)
//            {
//                hasSelection = true;
//                selected.add(i);
//            }
//
//        if(selected.size() > 0)
//        {
//            if(selected.size() == 1)
//                return ((WorldAudio)entities.get(selected.get(0))).initialVolume;
//            else if(selected.size() > 1)
//            {
//                boolean isSame = true;
//                float soundVolume = ((WorldAudio)entities.get(selected.get(0))).initialVolume;
//                for(int i : selected)
//                {
//                    WorldAudio entity = (WorldAudio) entities.get(i);
//
//                    if(entity.initialVolume != soundVolume)
//                        isSame = false;
//                }
//
//                if(isSame)
//                    return soundVolume;
//            }
//        }
//
//        return Consts.NaNf;
//    }
//
//    public float getSelectedSoundPitch()
//    {
//        boolean hasSelection = false;
//        ArrayList<Integer> selected = new ArrayList<>();
//
//        for(int i = 0; i < entities.size(); i++)
//            if(entities.get(i).selected)
//            {
//                hasSelection = true;
//                selected.add(i);
//            }
//
//        if(selected.size() > 0)
//        {
//            if(selected.size() == 1)
//                return ((WorldAudio)entities.get(selected.get(0))).initialPitch;
//            else if(selected.size() > 1)
//            {
//                boolean isSame = true;
//                float soundVolume = ((WorldAudio)entities.get(selected.get(0))).initialPitch;
//                for(int i : selected)
//                {
//                    WorldAudio entity = (WorldAudio) entities.get(i);
//
//                    if(entity.initialPitch != soundVolume)
//                        isSame = false;
//                }
//
//                if(isSame)
//                    return soundVolume;
//            }
//        }
//
//        return Consts.NaNf;
//    }

    public String getSelectedName()
    {
        boolean hasSelection = false;
        ArrayList<Integer> selected = new ArrayList<>();

        for(int i = 0; i < things.size(); i++)
            if(things.get(i).selected)
            {
                hasSelection = true;
                selected.add(i);
            }

        if(selected.size() == 1)
            return things.get(selected.get(0)).thing.name;
        else if(selected.size() > 1)
        {
            boolean isSame = true;
            String name = things.get(selected.get(0)).thing.name;
            for(int i : selected)
            {
                String n = things.get(i).thing.name;

                if(name != null && !name.equalsIgnoreCase(n))
                    isSame = false;
            }

            if(isSame)
                return name;
        }

        return "";
    }

    public ArrayList<Integer> selectedThingsBuiltThingArray;
    public ArrayList<Thing> buildThingArrayList(boolean selectionOnly)
    {
        if(selectedThingsBuiltThingArray == null)
            selectedThingsBuiltThingArray = new ArrayList<>();
        else
            selectedThingsBuiltThingArray.clear();

        ArrayList<Thing> things = new ArrayList();

        int UID = 1;

        for(Entity entity : this.things)
        {
            if(!entity.selected && selectionOnly)
                continue;

            Thing thing = ((bog.lbpas.view3d.core.types.Thing) entity).thing;
            thing.UID = UID;
            things.add(thing);
            if(entity.selected)
                selectedThingsBuiltThingArray.add(thing.UID);
            UID++;
        }
        return things;
    }

    public Thing[] buildThingArray(boolean selectionOnly)
    {
        return buildThingArrayList(selectionOnly).toArray(Thing[]::new);
    }

    public void setCurrentScreen(GuiScreen screen) {
        screen.previousScreen = this.currentScreen;
        this.currentScreen = screen;
    }

    public void returnToPreviousScreen() {
        this.currentScreen = this.currentScreen.previousScreen;
    }

    public void addThings(List<Thing> things, List<Number> selectedUIDs)
    {
        for(bog.lbpas.view3d.core.types.Thing t : this.things)
            t.selected = false;

        for (Thing thing : things)
            if (thing != null)
            {
                if(thing.name == null)
                    buildThingName(thing);

                if(thing.hasPart(Part.RENDER_MESH) && ((PRenderMesh)thing.getPart(Part.RENDER_MESH)).mesh != null)
                {
                    RMesh msh = LoadedData.loadMesh(((PRenderMesh)thing.getPart(Part.RENDER_MESH)).mesh);
                    if(msh != null)
                        for(int i = 0; i < ((PRenderMesh)thing.getPart(Part.RENDER_MESH)).boneThings.length; i++)
                        {
                            Thing th = ((PRenderMesh)thing.getPart(Part.RENDER_MESH)).boneThings[i];

                            Bone[] bonearray = msh.getBones();
                            if(th!= null && th.name == null && i < bonearray.length && bonearray[i] != null)
                                th.name = msh.getBones()[i].getName();

                        }
                }

                boolean selected = false;
                if(selectedUIDs != null)
                    for (Number num : selectedUIDs) {
                        if (num == null) {
                            continue;
                        }
                        if (Integer.compare(num.intValue(), thing.UID) == 0) {
                            selected = true;
                        }
                    }
                else
                    selected = true;

                bog.lbpas.view3d.core.types.Thing th = new bog.lbpas.view3d.core.types.Thing(thing, loader);
                th.selected = selected;
                this.things.add(th);
            }
    }

    public void buildThingName(Thing thing)
    {
        ResourceDescriptor ent = null;

        if(thing.hasPart(Part.RENDER_MESH) && ((PRenderMesh)thing.getPart(Part.RENDER_MESH)).mesh != null)
        {
            ent = ((PRenderMesh)thing.getPart(Part.RENDER_MESH)).mesh;
        }
        else if(thing.hasPart(Part.GENERATED_MESH) && ((PGeneratedMesh)thing.getPart(Part.GENERATED_MESH)).gfxMaterial != null)
        {
            ent = ((PGeneratedMesh)thing.getPart(Part.GENERATED_MESH)).gfxMaterial;
        }
        else if(thing.hasPart(Part.SCRIPT_NAME) && ((PScriptName)thing.getPart(Part.SCRIPT_NAME)).name != null)
        {
            thing.name = ((PScriptName)thing.getPart(Part.SCRIPT_NAME)).name;
        }
        else if(thing.hasPart(Part.SCRIPT) && ((PScript)thing.getPart(Part.SCRIPT)).instance.script != null)
        {
            ent = ((PScript)thing.getPart(Part.SCRIPT)).instance.script;
        }
        else if(thing.hasPart(Part.EFFECTOR))
        {
            thing.name = "Effector";
        }
        else if(thing.hasPart(Part.LEVEL_SETTINGS))
        {
            thing.name = "Level Settings";
        }
        else if(thing.hasPart(Part.SHAPE))
        {
            thing.name = "Shape";
        }
        else if(thing.hasPart(Part.CHECKPOINT))
        {
            thing.name = "Checkpoint";
        }
        else if(thing.hasPart(Part.TRIGGER) && ((PTrigger)thing.getPart(Part.TRIGGER)).triggerType != null)
        {
            thing.name = ((PTrigger)thing.getPart(Part.TRIGGER)).triggerType.name();
            thing.name = "Trigger " + thing.name.substring(0, 1).toUpperCase() + thing.name.substring(1).toLowerCase();
        }
        else if(thing.hasPart(Part.EMITTER))
        {
            thing.name = "Emitter";
        }
        else if(thing.hasPart(Part.GROUP))
        {
            thing.name = "Group";
        }
        else if(thing.hasPart(Part.AUDIO_WORLD))
        {
            thing.name = "Audio";
        }
        else if(thing.hasPart(Part.SPRITE_LIGHT))
        {
            thing.name = "Light";
        }
        else if(thing.hasPart(Part.SWITCH_INPUT))
        {
            thing.name = "Switch Input";
        }
        else if(thing.hasPart(Part.SWITCH))
        {
            thing.name = "Switch";
        }
        else if(thing.hasPart(Part.JOINT))
        {
            thing.name = "Joint";
        }
        else if(thing.hasPart(Part.SWITCH_KEY))
        {
            thing.name = "Tag";
        }

        if(ent != null)
        {
            FileEntry e = LoadedData.getDigestedEntry(ent);

            if(e != null)
            {
                String name = e.getName();

                int extInd = name.lastIndexOf(".");
                boolean nameIsHash = name.substring(0, extInd != -1 ? extInd : name.length()).equalsIgnoreCase(e.getSHA1().toString());

                if (!(e instanceof FileDBRow) && nameIsHash)
                    name = name.substring(name.length() - 12);

                thing.name = name.substring(0, name.lastIndexOf("."));
            }
        }
    }

    public void addThings(Thing[] things, List<Number> selectedUIDs)
    {
        addThings(Arrays.asList(things), selectedUIDs);
    }

    public void loaderThread()
    {
        if(things != null)
            for(int t = 0; t < things.size(); t++)
            {
                if(t >= things.size())
                    break;

                bog.lbpas.view3d.core.types.Thing thing = things.get(t);

                if(thing == null || thing.thing == null)
                    continue;

                Matrix4f transform = thing.getTransformation();

                if(transform == null)
                    continue;

                if(!transform.equals(thing.prevTransformation, 0))
                {
                    thing.reloadModel();
                    thing.prevTransformation = new Matrix4f(transform);
                }

                if(thing.thing.hasPart(Part.SHAPE))
                {
                    PShape shape = ((PShape)thing.thing.getPart(Part.SHAPE));
                    float thickness = shape.thickness;
                    float bevelSize = shape.bevelSize;
                    float zBias = shape.zBias;

                    if(thickness != thing.prevThickness || bevelSize != thing.prevBevSize || zBias != thing.prevZBias)
                    {
                        thing.reloadModel();
                        thing.prevThickness = thickness;
                        thing.prevBevSize = bevelSize;
                        thing.prevZBias = zBias;
                    }
                }
            }
        loader.loaderThread(this);
        renderer.loaderThread();
        if(ProjectManager != null)
            ((ProjectManager) ProjectManager).loaderThread();
    }
}