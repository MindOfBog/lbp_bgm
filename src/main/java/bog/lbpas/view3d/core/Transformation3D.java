package bog.lbpas.view3d.core;

import bog.lbpas.view3d.mainWindow.ConstantTextures;
import bog.lbpas.view3d.renderer.Camera;
import bog.lbpas.view3d.managers.assetLoading.ObjectLoader;
import bog.lbpas.view3d.core.types.Entity;
import bog.lbpas.view3d.mainWindow.View3D;
import bog.lbpas.view3d.managers.MouseInput;
import bog.lbpas.view3d.managers.RenderMan;
import bog.lbpas.view3d.managers.WindowMan;
import bog.lbpas.view3d.renderer.gui.ingredients.Line;
import bog.lbpas.view3d.renderer.gui.ingredients.Quad;
import bog.lbpas.view3d.utils.MousePicker;
import bog.lbpas.view3d.utils.print;
import org.joml.Math;
import org.joml.*;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayList;

/**
 * @author Bog
 */
public class Transformation3D{

    public static Model position;
    public static Model rotation;
    public static Model scale;
    public static Model scaleUniform;

    public static void init(ObjectLoader loader) throws Exception {
        position = loader.loadOBJModelDirect("/models/position.obj");
        rotation = loader.loadOBJModelDirect("/models/rotation.obj");
        scale = loader.loadOBJModelDirect("/models/scale.obj");
        scaleUniform = loader.loadOBJModelDirect("/models/scale_uniform.obj");
    }

    public static class Tool
    {

        public ArrayList<Entity> tools;
        public int selected = -1;
        public float lastAng = 0;
        public Vector3f initPosX = new Vector3f();
        public Vector3f initPosY = new Vector3f();
        public Vector3f initPosZ = new Vector3f();
        public Vector2f initPos = new Vector2f();
        public int hit = -1;

        public Tool(ObjectLoader loader) {
            tools = new ArrayList<>();

            Model posx = new Model(position);
            posx.material = new Material(new Vector4f(1f, 0.196f, 0.196f, 1f), 0f);
            tools.add(new Entity(posx, new Vector3f(0, 0, 0), new Vector3f(90, 0, -90), new Vector3f(50f, 50f, 50f), loader));
            Model posy = new Model(position);
            posy.material = new Material(new Vector4f(0.196f, 1f, 0.196f, 1f), 0f);
            tools.add(new Entity(posy, new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(50f, 50f, 50f), loader));
            Model posz = new Model(position);
            posz.material = new Material(new Vector4f(0.196f, 0.196f, 1f, 1f), 0f);
            tools.add(new Entity(posz, new Vector3f(0, 0, 0), new Vector3f(90, 90, 0), new Vector3f(50f, 50f, 50f), loader));

            Model rotx = new Model(rotation);
            rotx.material = new Material(new Vector4f(1f, 0.196f, 0.196f, 1f), 0f);
            tools.add(new Entity(rotx, new Vector3f(0, 0, 0), new Vector3f(180, 0, -90), new Vector3f(50f, 50f, 50f), loader));
            Model roty = new Model(rotation);
            roty.material = new Material(new Vector4f(0.196f, 1f, 0.196f, 1f), 0f);
            tools.add(new Entity(roty, new Vector3f(0, 0, 0), new Vector3f(0, 180, 180), new Vector3f(50f, 50f, 50f), loader));
            Model rotz = new Model(rotation);
            rotz.material = new Material(new Vector4f(0.196f, 0.196f, 1f, 1f), 0f);
            tools.add(new Entity(rotz, new Vector3f(0, 0, 0), new Vector3f(90, 0, 0), new Vector3f(50f, 50f, 50f), loader));

            Model scalx = new Model(scale);
            scalx.material = new Material(new Vector4f(1f, 0.196f, 0.196f, 1f), 0f);
            tools.add(new Entity(scalx, new Vector3f(0, 0, 0), new Vector3f(90, 0, -90), new Vector3f(50f, 50f, 50f), loader));
            Model scaly = new Model(scale);
            scaly.material = new Material(new Vector4f(0.196f, 1f, 0.196f, 1f), 0f);
            tools.add(new Entity(scaly, new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(50f, 50f, 50f), loader));
            Model scalz = new Model(scale);
            scalz.material = new Material(new Vector4f(0.196f, 0.196f, 1f, 1f), 0f);
            tools.add(new Entity(scalz, new Vector3f(0, 0, 0), new Vector3f(90, 90, 0), new Vector3f(50f, 50f, 50f), loader));
            Model scalu = new Model(scaleUniform);
            scalu.material = new Material(new Vector4f(1f, 1f, 0.341176f, 1f), 0f);
            tools.add(new Entity(scalu, new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(50f, 50f, 50f), loader));
        }

        public void testForMouse(boolean hasSelection, Camera camera, MousePicker mousePicker, boolean translate, boolean rotate, boolean scale)
        {
            for(Entity entity : tools)
                entity.highlighted = false;

            if(hasSelection)
            {
                for(int i = 0; i < tools.size(); i++)
                    tools.get(i).testForMouse = hasSelection;

                hit = -1;

                for(int i = 0; i < tools.size(); i++)
                {
                    Entity entity = tools.get(i);
                    if(entity.highlighted)
                        hit = i;
                }
            }
        }

        public void render(boolean hasSelection, boolean translate, boolean rotate, boolean scale, WindowMan window, ObjectLoader loader, RenderMan renderer, MouseInput mouseInput)
        {
            if(selected >= 3 && selected < 6)
            {
                Vector2i point = new Vector2i((int) screenPos.x, (int) screenPos.y);
                Vector2i mouse = new Vector2i((int) mouseInput.currentPos.x, (int) mouseInput.currentPos.y);

                renderer.processGuiElement(new Line(mouse, point, Color.black, loader, window, true));
                renderer.processGuiElement(new Quad(ConstantTextures.getTexture(ConstantTextures.CROSSHAIR, 15, 15, loader), new Vector2f((int)(screenPos.x - 7.5f), (int)(screenPos.y - 7.5f)), new Vector2f(15, 15)).staticTexture());
            }

            if(selected == -1)
                for(int i = 0; i < tools.size(); i++)
                    if(hasSelection)
                        if((i < 3 && translate) || (i >= 3 && i < 6 && rotate) || (i >= 6 && i < 11 && scale))
                            renderer.processThroughWallEntity(tools.get(i));
        }

        public void render(boolean hasSelection,
                           boolean translateX, boolean translateY, boolean translateZ,
                           boolean rotateX, boolean rotateY, boolean rotateZ,
                           boolean scaleX, boolean scaleY, boolean scaleZ, boolean scaleUniform,
                           int crosshair, WindowMan window, ObjectLoader loader, RenderMan renderer, MouseInput mouseInput)
        {
            if(selected >= 3 && selected < 6)
            {
                Vector2i point = new Vector2i((int) screenPos.x, (int) screenPos.y);
                Vector2i mouse = new Vector2i((int) mouseInput.currentPos.x, (int) mouseInput.currentPos.y);

                renderer.processGuiElement(new Line(mouse, point, Color.black, loader, window, true));
                renderer.processGuiElement(new Quad(crosshair, new Vector2f((int)(screenPos.x - 7.5f), (int)(screenPos.y - 7.5f)), new Vector2f(15, 15)).staticTexture());
            }

            if(selected == -1)
            {
                for(int i = 0; i < tools.size(); i++)
                    if(hasSelection)
                    {
                        if((i == 0 && translateX) || (i == 1 && translateY) || (i == 2 && translateZ) ||
                                (i == 3 && rotateX) || (i == 4 && rotateY) || (i == 5 && rotateZ)
                                || (i == 6 && scaleX) || (i == 7 && scaleY) || (i == 8 && scaleZ) || (i == 9 && scaleUniform))
                            renderer.processThroughWallEntity(tools.get(i));
                    }
            }
        }

        public boolean onClick(MouseInput mouseInput, int button, int action, int mods, WindowMan window, Camera camera)
        {
            hit = -1;

            for(int i = 0; i < tools.size(); i++)
            {
                Entity entity = tools.get(i);
                if(entity.highlighted)
                    hit = i;
            }

            if (hit != -1 && button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && !mouseInput.middleButtonPress) {
                selected = hit;

                initPosX = mouseInput.mousePicker.getPointOnPlaneX(currentPosition.x);
                initPosY = mouseInput.mousePicker.getPointOnPlaneY(currentPosition.y);
                initPosZ = mouseInput.mousePicker.getPointOnPlaneZ(currentPosition.z);
                initPos = new Vector2f((float) mouseInput.currentPos.x, (float) mouseInput.currentPos.y);

                Vector3f screenPos = camera.worldToScreen(tools.get(0).getTransformation().getTranslation(new Vector3f()), window);

                double y = mouseInput.currentPos.y - screenPos.y;
                double x = mouseInput.currentPos.x - screenPos.x;

                lastAng = ((float) Math.atan2(y, x));
                return true;
            }
            if (button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_RELEASE || button == GLFW.GLFW_MOUSE_BUTTON_3 && action == GLFW.GLFW_PRESS)
                selected = -1;

            return false;
        }

        public Vector3f currentPosition = new Vector3f();
        public Vector3f screenPos = new Vector3f();

        public void updateModels(View3D view, Vector3f currentPosition)
        {
            for (int i = 0; i < tools.size(); i++) {
                Entity entity = tools.get(i);

                MouseInput mi = new MouseInput(null);

                this.currentPosition = currentPosition;
                screenPos = view.camera.worldToScreen(this.currentPosition, view.window);

                if(screenPos != null && screenPos.z == 0)
                {
                    mi.currentPos = new Vector2d(screenPos.x, screenPos.y);
                    MousePicker posPicker = new MousePicker(mi, view.window);
                    posPicker.update(view.camera);
                    Vector3f pos = new Vector3f(posPicker.getPointOnRay(posPicker.currentRay, 4000));
                    entity.setTransformation(entity.getTransformation().setTranslation(pos));
                }
            }
        }

        public boolean isSelected()
        {
            return selected != -1;
        }

        public boolean isHovering()
        {
            boolean hovering = false;

            for(int i = 0; i < tools.size(); i++)
                if(tools.get(i).highlighted)
                    hovering = true;

            return hovering;
        }
    }
}
