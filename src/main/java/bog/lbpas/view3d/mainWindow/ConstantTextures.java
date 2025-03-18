package bog.lbpas.view3d.mainWindow;

import bog.lbpas.Main;
import bog.lbpas.view3d.managers.assetLoading.ObjectLoader;
import bog.lbpas.view3d.utils.Utils;
import bog.lbpas.view3d.utils.print;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.*;
import java.util.HashMap;

public class ConstantTextures {

    public static HashMap<String, Integer> ICON;

    public static HashMap<String, Integer> CROSSHAIR;
    public static HashMap<String, Integer> CORNER_EDIT;
    public static HashMap<String, Integer> CORNER_EDIT_PICK;
    public static HashMap<String, Integer> SUN;

    public static HashMap<String, Integer> ICON_WORLD_AUDIO;
    public static HashMap<String, Integer> ICON_MESH;
    public static HashMap<String, Integer> ICON_SHAPE;
    public static HashMap<String, Integer> ICON_LIGHT;
    public static HashMap<String, Integer> ICON_AUDIO;
    public static HashMap<String, Integer> ICON_SCRIPT;
    public static HashMap<String, Integer> ICON_JOINT;
    public static HashMap<String, Integer> ICON_COSTUME;
    public static HashMap<String, Integer> ICON_BONE;
    public static HashMap<String, Integer> ICON_LEVEL_SETTINGS;
    public static HashMap<String, Integer> ICON_EFFECTOR;
    public static HashMap<String, Integer> ICON_TRIGGER;
    public static HashMap<String, Integer> ICON_EMITTER;
    public static HashMap<String, Integer> ICON_GROUP;
    public static HashMap<String, Integer> ICON_NPC;
    public static HashMap<String, Integer> ICON_CHECKPOINT;
    public static HashMap<String, Integer> ICON_UNKNOWN;

    public static HashMap<String, Integer> WINDOW_CLOSE;
    public static HashMap<String, Integer> WINDOW_MINIMIZE;
    public static HashMap<String, Integer> WINDOW_MAXIMIZE;
    public static HashMap<String, Integer> WINDOW_RESTORE;

    public static HashMap<String, Integer> TRANSFORMATION_MOVE;
    public static HashMap<String, Integer> TRANSFORMATION_ROTATE;
    public static HashMap<String, Integer> TRANSFORMATION_SCALE;

    public static HashMap<String, Integer> VISIBILITY;
    public static HashMap<String, Integer> VISIBILITY_OFF;
    public static HashMap<String, Integer> FRONT_VIEW;
    public static HashMap<String, Integer> FRONT_VIEW_OFF;
    public static HashMap<String, Integer> COPY;
    public static HashMap<String, Integer> PASTE;
    public static HashMap<String, Integer> DRAG;

    public static void initTextures(ObjectLoader loader)
    {
        ICON = new HashMap<>();
        ICON.put("/textures/icon.svg", -1);
        getTexture(ICON, 461, 461, loader);

        CROSSHAIR = new HashMap<>();
        CROSSHAIR.put("/textures/crosshair.png", -1);
        CORNER_EDIT = new HashMap<>();
        CORNER_EDIT.put("/textures/ui/transformation/corner_edit.svg", -1);
        CORNER_EDIT_PICK = new HashMap<>();
        CORNER_EDIT_PICK.put("/textures/ui/transformation/corner_edit_pick.svg", -1);
        SUN = new HashMap<>();
        SUN.put("/textures/world/sun.svg", -1);

        ICON_WORLD_AUDIO = new HashMap<>();
        ICON_WORLD_AUDIO.put("/textures/ui/type/sound.svg", -1);
        ICON_MESH = new HashMap<>();
        ICON_MESH.put("/textures/ui/type/mesh.svg", -1);
        ICON_SHAPE = new HashMap<>();
        ICON_SHAPE.put("/textures/ui/type/shape.svg", -1);
        ICON_LIGHT = new HashMap<>();
        ICON_LIGHT.put("/textures/ui/type/light.svg", -1);
        ICON_AUDIO = new HashMap<>();
        ICON_AUDIO.put("/textures/ui/type/sound.svg", -1);
        ICON_SCRIPT = new HashMap<>();
        ICON_SCRIPT.put("/textures/ui/type/script.svg", -1);
        ICON_JOINT = new HashMap<>();
        ICON_JOINT.put("/textures/ui/type/joint.svg", -1);
        ICON_COSTUME = new HashMap<>();
        ICON_COSTUME.put("/textures/ui/type/costume.svg", -1);
        ICON_BONE = new HashMap<>();
        ICON_BONE.put("/textures/ui/type/bone.svg", -1);
        ICON_LEVEL_SETTINGS = new HashMap<>();
        ICON_LEVEL_SETTINGS.put("/textures/ui/type/level_settings.svg", -1);
        ICON_EFFECTOR = new HashMap<>();
        ICON_EFFECTOR.put("/textures/ui/type/effector.svg", -1);
        ICON_TRIGGER = new HashMap<>();
        ICON_TRIGGER.put("/textures/ui/type/trigger.svg", -1);
        ICON_EMITTER = new HashMap<>();
        ICON_EMITTER.put("/textures/ui/type/emitter.svg", -1);
        ICON_GROUP = new HashMap<>();
        ICON_GROUP.put("/textures/ui/type/group.svg", -1);
        ICON_NPC = new HashMap<>();
        ICON_NPC.put("/textures/ui/type/npc.svg", -1);
        ICON_CHECKPOINT = new HashMap<>();
        ICON_CHECKPOINT.put("/textures/ui/type/checkpoint.svg", -1);
        ICON_UNKNOWN = new HashMap<>();
        ICON_UNKNOWN.put("/textures/ui/type/unknown.svg", -1);

        WINDOW_CLOSE = new HashMap<>();
        WINDOW_CLOSE.put("/textures/window/close.svg", -1);
        getTexture(WINDOW_CLOSE, 23, 23, loader);
        WINDOW_MINIMIZE = new HashMap<>();
        WINDOW_MINIMIZE.put("/textures/window/minimize.svg", -1);
        getTexture(WINDOW_MINIMIZE, 23, 23, loader);
        WINDOW_MAXIMIZE = new HashMap<>();
        WINDOW_MAXIMIZE.put("/textures/window/maximize.svg", -1);
        getTexture(WINDOW_MAXIMIZE, 23, 23, loader);
        WINDOW_RESTORE = new HashMap<>();
        WINDOW_RESTORE.put("/textures/window/restore.svg", -1);
        getTexture(WINDOW_RESTORE, 23, 23, loader);

        TRANSFORMATION_MOVE = new HashMap<>();
        TRANSFORMATION_MOVE.put("/textures/ui/transformation/move.svg", -1);
        TRANSFORMATION_ROTATE = new HashMap<>();
        TRANSFORMATION_ROTATE.put("/textures/ui/transformation/rotate.svg", -1);
        TRANSFORMATION_SCALE = new HashMap<>();
        TRANSFORMATION_SCALE.put("/textures/ui/transformation/scale.svg", -1);

        VISIBILITY = new HashMap<>();
        VISIBILITY.put("/textures/ui/visibility.svg", -1);
        VISIBILITY_OFF = new HashMap<>();
        VISIBILITY_OFF.put("/textures/ui/visibility_off.svg", -1);
        FRONT_VIEW = new HashMap<>();
        FRONT_VIEW.put("/textures/ui/front_view.svg", -1);
        FRONT_VIEW_OFF = new HashMap<>();
        FRONT_VIEW_OFF.put("/textures/ui/front_view_off.svg", -1);
        COPY = new HashMap<>();
        COPY.put("/textures/ui/copy.svg", -1);
        PASTE = new HashMap<>();
        PASTE.put("/textures/ui/paste.svg", -1);
        DRAG = new HashMap<>();
        DRAG.put("/textures/ui/drag.svg", -1);
    }

    public static int getTexture(HashMap<String, Integer> texture, int width, int height, ObjectLoader loader)
    {
        if(Thread.currentThread().getName().equals("main"))
        {
            String resKey = width + ":" + height;
            if(texture.containsKey(resKey))
            {
                return texture.get(resKey);
            }
            else
            {
                int minF = GL11.GL_LINEAR_MIPMAP_NEAREST;
                int magF = GL11.GL_LINEAR;

                String path = "";

                for (HashMap.Entry<String, Integer> entry : texture.entrySet())
                    if (entry.getValue() == -1)
                    {
                        path = entry.getKey();
                        break;
                    }

                int tex = LoadedData.missingTexture.id;

                try {
                    tex = loadTexture(path, width, height, minF, magF, loader);
                    texture.put(resKey, tex);
                }catch (Exception e){e.printStackTrace();}

                return tex;
            }
        }
        else
        {
            toLoad = new ToLoad(texture, width, height, loader);
        }
        return -1;
    }

    private static int loadTexture(String path, int width, int height, int minF, int magF, ObjectLoader loader) throws Exception {
        if(path.endsWith(".svg"))
        {
            String p = path.startsWith("/") ? path.substring(1) : path;
            return loader.loadTexture(Utils.loadAndRenderSVG(Thread.currentThread().getContextClassLoader().getResourceAsStream(p), width, height, true), minF, magF);
        }
        else
        {
            String p = path.startsWith("/") ? path : "/" + path;
            return loader.loadTexture(ImageIO.read(Main.class.getResourceAsStream(p)), minF, magF);
        }
    }

    private static int loadTexture(String path, int width, int height, int minF, int magF, ObjectLoader loader, float mR, float mG, float mB) throws Exception {
        if(path.endsWith(".svg"))
        {
            String p = path.startsWith("/") ? path.substring(1) : path;
            return loader.loadTexture(Utils.loadAndRenderSVG(Thread.currentThread().getContextClassLoader().getResourceAsStream(p), width, height, true, mR, mG, mB), minF, magF);
        }
        else
        {
            String p = path.startsWith("/") ? path : "/" + path;
            return loader.loadTexture(ImageIO.read(Main.class.getResourceAsStream(p)), minF, magF);
        }
    }

    private static int loadTexture(String path, int width, int height, int minF, int magF, ObjectLoader loader, Color multiply) throws Exception {
        if(path.endsWith(".svg"))
        {
            String p = path.startsWith("/") ? path.substring(1) : path;
            return loader.loadTexture(Utils.loadAndRenderSVG(Thread.currentThread().getContextClassLoader().getResourceAsStream(p), width, height, true, multiply.getRed() / 255f, multiply.getGreen() / 255f, multiply.getBlue() / 255f), minF, magF);
        }
        else
        {
            String p = path.startsWith("/") ? path : "/" + path;
            return loader.loadTexture(ImageIO.read(Main.class.getResourceAsStream(p)), minF, magF);
        }
    }

    private static ToLoad toLoad = null;

    public static void mainThread()
    {
        if(toLoad != null)
        {
            String resKey = toLoad.width + ":" + toLoad.height;
            if(!toLoad.texture.containsKey(resKey))
            {
                int minF = GL11.GL_LINEAR_MIPMAP_NEAREST;
                int magF = GL11.GL_LINEAR;

                String path = "";

                for (HashMap.Entry<String, Integer> entry : toLoad.texture.entrySet())
                    if (entry.getValue() == -1)
                    {
                        path = entry.getKey();
                        break;
                    }

                int tex = LoadedData.missingTexture.id;

                try {
                    tex = loadTexture(path, toLoad.width, toLoad.height, minF, magF, toLoad.loader);
                    toLoad.texture.put(resKey, tex);
                }catch (Exception e){e.printStackTrace();}
            }
            toLoad = null;
        }
    }

    private static class ToLoad
    {
        HashMap<String, Integer> texture;
        int width;
        int height;
        ObjectLoader loader;

        public ToLoad(HashMap<String, Integer> texture, int width, int height, ObjectLoader loader) {
            this.texture = texture;
            this.width = width;
            this.height = height;
            this.loader = loader;
        }
    }
}
