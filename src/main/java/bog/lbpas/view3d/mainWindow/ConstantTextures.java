package bog.lbpas.view3d.mainWindow;

import bog.lbpas.Main;
import bog.lbpas.view3d.core.Texture;
import bog.lbpas.view3d.managers.assetLoading.ObjectLoader;
import bog.lbpas.view3d.utils.Utils;
import bog.lbpas.view3d.utils.print;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.*;
import java.util.HashMap;

public class ConstantTextures {

    public static HashMap<String, Texture> ICON;

    public static HashMap<String, Texture> CROSSHAIR;
    public static HashMap<String, Texture> CORNER_EDIT;
    public static HashMap<String, Texture> CORNER_EDIT_PICK;
    public static HashMap<String, Texture> SUN;

    public static HashMap<String, Texture> ICON_WORLD_AUDIO;
    public static HashMap<String, Texture> ICON_MESH;
    public static HashMap<String, Texture> ICON_SHAPE;
    public static HashMap<String, Texture> ICON_LIGHT;
    public static HashMap<String, Texture> ICON_AUDIO;
    public static HashMap<String, Texture> ICON_SCRIPT;
    public static HashMap<String, Texture> ICON_JOINT;
    public static HashMap<String, Texture> ICON_COSTUME;
    public static HashMap<String, Texture> ICON_BONE;
    public static HashMap<String, Texture> ICON_LEVEL_SETTINGS;
    public static HashMap<String, Texture> ICON_EFFECTOR;
    public static HashMap<String, Texture> ICON_TRIGGER;
    public static HashMap<String, Texture> ICON_EMITTER;
    public static HashMap<String, Texture> ICON_GROUP;
    public static HashMap<String, Texture> ICON_GROUP_OPEN;
    public static HashMap<String, Texture> ICON_GROUP_GEAR;
    public static HashMap<String, Texture> ICON_GROUP_GEAR_OPEN;
    public static HashMap<String, Texture> ICON_NPC;
    public static HashMap<String, Texture> ICON_CHECKPOINT;
    public static HashMap<String, Texture> ICON_UNKNOWN;

    public static HashMap<String, Texture> WINDOW_CLOSE;
    public static HashMap<String, Texture> WINDOW_MINIMIZE;
    public static HashMap<String, Texture> WINDOW_MAXIMIZE;
    public static HashMap<String, Texture> WINDOW_RESTORE;

    public static HashMap<String, Texture> TRANSFORMATION_MOVE;
    public static HashMap<String, Texture> TRANSFORMATION_ROTATE;
    public static HashMap<String, Texture> TRANSFORMATION_SCALE;

    public static HashMap<String, Texture> VISIBILITY;
    public static HashMap<String, Texture> VISIBILITY_OFF;
    public static HashMap<String, Texture> FRONT_VIEW;
    public static HashMap<String, Texture> FRONT_VIEW_OFF;
    public static HashMap<String, Texture> COPY;
    public static HashMap<String, Texture> PASTE;
    public static HashMap<String, Texture> DRAG;
    public static HashMap<String, Texture> FILE;
    public static HashMap<String, Texture> FILE_BEVEL;
    public static HashMap<String, Texture> FILE_GFX_MATERIAL;
    public static HashMap<String, Texture> FILE_MATERIAL;
    public static HashMap<String, Texture> FILE_MODEL;
    public static HashMap<String, Texture> FILE_PLAN;
    public static HashMap<String, Texture> FILE_SCRIPT;
    public static HashMap<String, Texture> FILE_SOFTBODY;
    public static HashMap<String, Texture> FILE_TEXTURE;
    public static HashMap<String, Texture> FILE_UNKNOWN;
    public static HashMap<String, Texture> PLAN;
    public static HashMap<String, Texture> TEXTURE;
    public static HashMap<String, Texture> RENAME;
    public static HashMap<String, Texture> OPTIONS;
    public static HashMap<String, Texture> FILTER;

    public static void initTextures(ObjectLoader loader)
    {
        ICON = new HashMap<>();
        ICON.put("/textures/icon.svg", new Texture());
        getTexture(ICON, 461, 461, loader);

        CROSSHAIR = new HashMap<>();
        CROSSHAIR.put("/textures/crosshair.png", new Texture());
        CORNER_EDIT = new HashMap<>();
        CORNER_EDIT.put("/textures/ui/transformation/corner_edit.svg", new Texture());
        CORNER_EDIT_PICK = new HashMap<>();
        CORNER_EDIT_PICK.put("/textures/ui/transformation/corner_edit_pick.svg", new Texture());
        SUN = new HashMap<>();
        SUN.put("/textures/world/sun.svg", new Texture());

        ICON_WORLD_AUDIO = new HashMap<>();
        ICON_WORLD_AUDIO.put("/textures/ui/type/sound.svg", new Texture());
        ICON_MESH = new HashMap<>();
        ICON_MESH.put("/textures/ui/type/mesh.svg", new Texture());
        ICON_SHAPE = new HashMap<>();
        ICON_SHAPE.put("/textures/ui/type/shape.svg", new Texture());
        ICON_LIGHT = new HashMap<>();
        ICON_LIGHT.put("/textures/ui/type/light.svg", new Texture());
        ICON_AUDIO = new HashMap<>();
        ICON_AUDIO.put("/textures/ui/type/sound.svg", new Texture());
        ICON_SCRIPT = new HashMap<>();
        ICON_SCRIPT.put("/textures/ui/type/script.svg", new Texture());
        ICON_JOINT = new HashMap<>();
        ICON_JOINT.put("/textures/ui/type/joint.svg", new Texture());
        ICON_COSTUME = new HashMap<>();
        ICON_COSTUME.put("/textures/ui/type/costume.svg", new Texture());
        ICON_BONE = new HashMap<>();
        ICON_BONE.put("/textures/ui/type/bone.svg", new Texture());
        ICON_LEVEL_SETTINGS = new HashMap<>();
        ICON_LEVEL_SETTINGS.put("/textures/ui/type/level_settings.svg", new Texture());
        ICON_EFFECTOR = new HashMap<>();
        ICON_EFFECTOR.put("/textures/ui/type/effector.svg", new Texture());
        ICON_TRIGGER = new HashMap<>();
        ICON_TRIGGER.put("/textures/ui/type/trigger.svg", new Texture());
        ICON_EMITTER = new HashMap<>();
        ICON_EMITTER.put("/textures/ui/type/emitter.svg", new Texture());
        ICON_GROUP = new HashMap<>();
        ICON_GROUP.put("/textures/ui/type/group.svg", new Texture());
        ICON_GROUP_OPEN = new HashMap<>();
        ICON_GROUP_OPEN.put("/textures/ui/type/group_open.svg", new Texture());
        ICON_GROUP_GEAR = new HashMap<>();
        ICON_GROUP_GEAR.put("/textures/ui/type/group_gear.svg", new Texture());
        ICON_GROUP_GEAR_OPEN = new HashMap<>();
        ICON_GROUP_GEAR_OPEN.put("/textures/ui/type/group_gear_open.svg", new Texture());
        ICON_NPC = new HashMap<>();
        ICON_NPC.put("/textures/ui/type/npc.svg", new Texture());
        ICON_CHECKPOINT = new HashMap<>();
        ICON_CHECKPOINT.put("/textures/ui/type/checkpoint.svg", new Texture());
        ICON_UNKNOWN = new HashMap<>();
        ICON_UNKNOWN.put("/textures/ui/type/unknown.svg", new Texture());

        WINDOW_CLOSE = new HashMap<>();
        WINDOW_CLOSE.put("/textures/window/close.svg", new Texture());
        getTexture(WINDOW_CLOSE, 23, 23, loader);
        WINDOW_MINIMIZE = new HashMap<>();
        WINDOW_MINIMIZE.put("/textures/window/minimize.svg", new Texture());
        getTexture(WINDOW_MINIMIZE, 23, 23, loader);
        WINDOW_MAXIMIZE = new HashMap<>();
        WINDOW_MAXIMIZE.put("/textures/window/maximize.svg", new Texture());
        getTexture(WINDOW_MAXIMIZE, 23, 23, loader);
        WINDOW_RESTORE = new HashMap<>();
        WINDOW_RESTORE.put("/textures/window/restore.svg", new Texture());
        getTexture(WINDOW_RESTORE, 23, 23, loader);

        TRANSFORMATION_MOVE = new HashMap<>();
        TRANSFORMATION_MOVE.put("/textures/ui/transformation/move.svg", new Texture());
        TRANSFORMATION_ROTATE = new HashMap<>();
        TRANSFORMATION_ROTATE.put("/textures/ui/transformation/rotate.svg", new Texture());
        TRANSFORMATION_SCALE = new HashMap<>();
        TRANSFORMATION_SCALE.put("/textures/ui/transformation/scale.svg", new Texture());

        VISIBILITY = new HashMap<>();
        VISIBILITY.put("/textures/ui/visibility.svg", new Texture());
        VISIBILITY_OFF = new HashMap<>();
        VISIBILITY_OFF.put("/textures/ui/visibility_off.svg", new Texture());
        FRONT_VIEW = new HashMap<>();
        FRONT_VIEW.put("/textures/ui/front_view.svg", new Texture());
        FRONT_VIEW_OFF = new HashMap<>();
        FRONT_VIEW_OFF.put("/textures/ui/front_view_off.svg", new Texture());
        COPY = new HashMap<>();
        COPY.put("/textures/ui/copy.svg", new Texture());
        PASTE = new HashMap<>();
        PASTE.put("/textures/ui/paste.svg", new Texture());
        DRAG = new HashMap<>();
        DRAG.put("/textures/ui/drag.svg", new Texture());
        FILE = new HashMap<>();
        FILE.put("/textures/ui/file.svg", new Texture());
        FILE_BEVEL = new HashMap<>();
        FILE_BEVEL.put("/textures/ui/file_bevel.svg", new Texture());
        FILE_GFX_MATERIAL = new HashMap<>();
        FILE_GFX_MATERIAL.put("/textures/ui/file_gfx_material.svg", new Texture());
        FILE_MATERIAL = new HashMap<>();
        FILE_MATERIAL.put("/textures/ui/file_material.svg", new Texture());
        FILE_MODEL = new HashMap<>();
        FILE_MODEL.put("/textures/ui/file_model.svg", new Texture());
        FILE_PLAN = new HashMap<>();
        FILE_PLAN.put("/textures/ui/file_plan.svg", new Texture());
        FILE_SCRIPT = new HashMap<>();
        FILE_SCRIPT.put("/textures/ui/file_script.svg", new Texture());
        FILE_SOFTBODY = new HashMap<>();
        FILE_SOFTBODY.put("/textures/ui/file_softbody.svg", new Texture());
        FILE_TEXTURE = new HashMap<>();
        FILE_TEXTURE.put("/textures/ui/file_texture.svg", new Texture());
        FILE_UNKNOWN = new HashMap<>();
        FILE_UNKNOWN.put("/textures/ui/file_unknown.svg", new Texture());
        PLAN = new HashMap<>();
        PLAN.put("/textures/ui/plan.svg", new Texture());
        TEXTURE = new HashMap<>();
        TEXTURE.put("/textures/ui/texture.svg", new Texture());
        RENAME = new HashMap<>();
        RENAME.put("/textures/ui/rename.svg", new Texture());
        OPTIONS = new HashMap<>();
        OPTIONS.put("/textures/ui/options.svg", new Texture());
        FILTER = new HashMap<>();
        FILTER.put("/textures/ui/filter.svg", new Texture());
    }

    public static Texture getTexture(HashMap<String, Texture> texture, int width, int height, ObjectLoader loader)
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

                for (HashMap.Entry<String, Texture> entry : texture.entrySet())
                    if (entry.getValue().id == -1)
                    {
                        path = entry.getKey();
                        break;
                    }

                Texture tex = LoadedData.missingTexture;

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
        return new Texture();
    }

    private static Texture loadTexture(String path, int width, int height, int minF, int magF, ObjectLoader loader) throws Exception {
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

    private static Texture loadTexture(String path, int width, int height, int minF, int magF, ObjectLoader loader, float mR, float mG, float mB) throws Exception {
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

    private static Texture loadTexture(String path, int width, int height, int minF, int magF, ObjectLoader loader, Color multiply) throws Exception {
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

                for (HashMap.Entry<String, Texture> entry : toLoad.texture.entrySet())
                    if (entry.getValue().id == -1)
                    {
                        path = entry.getKey();
                        break;
                    }

                Texture tex = LoadedData.missingTexture;

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
        HashMap<String, Texture> texture;
        int width;
        int height;
        ObjectLoader loader;

        public ToLoad(HashMap<String, Texture> texture, int width, int height, ObjectLoader loader) {
            this.texture = texture;
            this.width = width;
            this.height = height;
            this.loader = loader;
        }
    }
}
