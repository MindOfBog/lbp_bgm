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
import bog.lbpas.view3d.utils.Config;
import bog.lbpas.view3d.utils.MousePicker;
import bog.lbpas.view3d.utils.Utils;
import bog.lbpas.view3d.utils.print;
import org.joml.Math;
import org.joml.*;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

/**
 * @author Bog
 */
public class Transformation3D{

    public static Model position;
    public static Model positionBiAxial;
    public static Model positionThick;
    public static Model positionBiAxialThick;
    public static Model rotation;
    public static Model rotationThick;
    public static Model scale;
    public static Model scaleThick;
    public static Model scaleUniform;

    public static Model lineX;

    public static Model lineY;

    public static Model lineZ;

    public static void init(ObjectLoader loader) throws Exception {
        position = loader.loadOBJModelDirect("/models/position.obj");
        positionThick = loader.loadOBJModelDirect("/models/position_thick.obj");
        positionBiAxial = loader.loadOBJModelDirect("/models/position_bi_axial.obj");
        positionBiAxialThick = loader.loadOBJModelDirect("/models/position_bi_axial_thick.obj");
        rotation = loader.loadOBJModelDirect("/models/rotation.obj");
        rotationThick = loader.loadOBJModelDirect("/models/rotation_thick.obj");
        scale = loader.loadOBJModelDirect("/models/scale.obj");
        scaleThick = loader.loadOBJModelDirect("/models/scale_thick.obj");
        scaleUniform = loader.loadOBJModelDirect("/models/scale_uniform.obj");

        float neg = -10000000f;
        float pos = 10000000f;

        lineX = new Model();
        loader.loadModel(
                lineX,
                new float[]{neg, 0, 0, pos, 0, 0},
                new int[]{0, 1});
        lineY = new Model();
        loader.loadModel(
                lineY,
                new float[]{0, neg, 0, 0, pos, 0},
                new int[]{0, 1});
        lineZ = new Model();
        loader.loadModel(
                lineZ,
                new float[]{0, 0, neg, 0, 0, pos},
                new int[]{0, 1});
    }

    public static enum ToolType
    {
        INVALID(-1),

        POSITION_X(0),
        POSITION_Y(1),
        POSITION_Z(2),

        POSITION_YZ(3),
        POSITION_ZX(4),
        POSITION_XY(5),

        ROTATION_X(6),
        ROTATION_Y(7),
        ROTATION_Z(8),

        SCALE_X(9),
        SCALE_Y(10),
        SCALE_Z(11),
        SCALE_UNIFORM(12);

        public final int value;

        private ToolType(int value) {
            this.value = value;
        }

        public static ToolType fromValue(int value) {
            for (ToolType type : ToolType.values()) {
                if (type.value == value)
                    return type;
            }
            return INVALID;
        }
    }

    public static class Tool
    {
        public ArrayList<Entity> tools;
        public ArrayList<Entity> toolsMousePicking;
        public int selected = -1;
        public float lastAng = 0;
        public Vector3f initPosYZ = new Vector3f();
        public Vector3f initPosZX = new Vector3f();
        public Vector3f initPosXY = new Vector3f();

        public Vector3f initPosX = new Vector3f();
        public Vector3f initPosY = new Vector3f();
        public Vector3f initPosZ = new Vector3f();
        public Vector2f initPos = new Vector2f();
        public int hit = -1;

        Entity LineX;
        Entity LineY;
        Entity LineZ;

        public Tool(ObjectLoader loader) {
            setupToolModels(loader);
        }

        private void setupToolModels(ObjectLoader loader)
        {
            Vector4f red = new Vector4f(1f, 0.196f, 0.196f, 1f);
            Vector4f green = new Vector4f(0.196f, 1f, 0.196f, 1f);
            Vector4f blue = new Vector4f(0.196f, 0.196f, 1f, 1f);
            Vector4f yellow = new Vector4f(1f, 1f, 0.341176f, 1f);

            Model lX = new Model(lineX);
            lX.material = new Material(red, 0f);
            lX.primitive = GL11.GL_LINES;
            LineX = new Entity(lX, new Vector3f(), new Vector3f(), new Vector3f(1), loader);
            Model lY = new Model(lineY);
            lY.material = new Material(green, 0f);
            lY.primitive = GL11.GL_LINES;
            LineY = new Entity(lY, new Vector3f(), new Vector3f(), new Vector3f(1), loader);
            Model lZ = new Model(lineZ);
            lZ.material = new Material(blue, 0f);
            lZ.primitive = GL11.GL_LINES;
            LineZ = new Entity(lZ, new Vector3f(), new Vector3f(), new Vector3f(1), loader);

            tools = new ArrayList<>();
            toolsMousePicking = new ArrayList<>();

            Vector3f xRotation = new Vector3f(90, 0, -90);
            Vector3f yRotation = new Vector3f(0, 0, 0);
            Vector3f zRotation = new Vector3f(90, 90, 0);

            {
                Model posx = new Model(position);
                posx.material = new Material(red, 0f);
                tools.add(new Entity(posx, new Vector3f(), xRotation, new Vector3f(50f), loader));
                Model posy = new Model(position);
                posy.material = new Material(green, 0f);
                tools.add(new Entity(posy, new Vector3f(), yRotation, new Vector3f(50f), loader));
                Model posz = new Model(position);
                posz.material = new Material(blue, 0f);
                tools.add(new Entity(posz, new Vector3f(), zRotation, new Vector3f(50f), loader));

                Model posyz = new Model(positionBiAxial);
                posyz.material = new Material(red, 0f);
                tools.add(new Entity(posyz, new Vector3f(), new Vector3f(90, 0, 0), new Vector3f(50f), loader));
                Model poszx = new Model(positionBiAxial);
                poszx.material = new Material(green, 0f);
                tools.add(new Entity(poszx, new Vector3f(), new Vector3f(0, 180, 90), new Vector3f(50f), loader));
                Model posxy = new Model(positionBiAxial);
                posxy.material = new Material(blue, 0f);
                tools.add(new Entity(posxy, new Vector3f(), new Vector3f(0, -90, 0), new Vector3f(50f), loader));

                Model rotx = new Model(rotation);
                rotx.material = new Material(red, 0f);
                tools.add(new Entity(rotx, new Vector3f(), new Vector3f(180, 0, -90), new Vector3f(50f), loader));
                Model roty = new Model(rotation);
                roty.material = new Material(green, 0f);
                tools.add(new Entity(roty, new Vector3f(), new Vector3f(0, 180, 180), new Vector3f(50f), loader));
                Model rotz = new Model(rotation);
                rotz.material = new Material(blue, 0f);
                tools.add(new Entity(rotz, new Vector3f(), new Vector3f(90, 0, 0), new Vector3f(50f), loader));

                Model scalx = new Model(scale);
                scalx.material = new Material(red, 0f);
                tools.add(new Entity(scalx, new Vector3f(), xRotation, new Vector3f(50f), loader));
                Model scaly = new Model(scale);
                scaly.material = new Material(green, 0f);
                tools.add(new Entity(scaly, new Vector3f(), yRotation, new Vector3f(50f), loader));
                Model scalz = new Model(scale);
                scalz.material = new Material(blue, 0f);
                tools.add(new Entity(scalz, new Vector3f(), zRotation, new Vector3f(50f), loader));
                Model scalu = new Model(scaleUniform);
                scalu.material = new Material(yellow, 0f);
                tools.add(new Entity(scalu, new Vector3f(), yRotation, new Vector3f(50f), loader));
            }
            {
                Model posx = new Model(positionThick);
                posx.material = new Material(red, 0f);
                toolsMousePicking.add(new Entity(posx, new Vector3f(), xRotation, new Vector3f(50f), loader){
                    @Override
                    public void setHighlighted(boolean highlighted) {
                        tools.get(0).setHighlighted(highlighted);
                        super.setHighlighted(highlighted);
                    }
                });
                Model posy = new Model(positionThick);
                posy.material = new Material(green, 0f);
                toolsMousePicking.add(new Entity(posy, new Vector3f(), yRotation, new Vector3f(50f), loader){
                    @Override
                    public void setHighlighted(boolean highlighted) {
                        tools.get(1).setHighlighted(highlighted);
                        super.setHighlighted(highlighted);
                    }
                });
                Model posz = new Model(positionThick);
                posz.material = new Material(blue, 0f);
                toolsMousePicking.add(new Entity(posz, new Vector3f(), zRotation, new Vector3f(50f), loader){
                    @Override
                    public void setHighlighted(boolean highlighted) {
                        tools.get(2).setHighlighted(highlighted);
                        super.setHighlighted(highlighted);
                    }
                });

                Model posyz = new Model(positionBiAxialThick);
                posyz.material = new Material(red, 0f);
                toolsMousePicking.add(new Entity(posyz, new Vector3f(), new Vector3f(90, 0, 0), new Vector3f(50f), loader){
                    @Override
                    public void setHighlighted(boolean highlighted) {
                        tools.get(3).setHighlighted(highlighted);
                        super.setHighlighted(highlighted);
                    }
                });
                Model poszx = new Model(positionBiAxialThick);
                poszx.material = new Material(green, 0f);
                toolsMousePicking.add(new Entity(poszx, new Vector3f(), new Vector3f(0, 180, 90), new Vector3f(50f), loader){
                    @Override
                    public void setHighlighted(boolean highlighted) {
                        tools.get(4).setHighlighted(highlighted);
                        super.setHighlighted(highlighted);
                    }
                });
                Model posxy = new Model(positionBiAxialThick);
                posxy.material = new Material(blue, 0f);
                toolsMousePicking.add(new Entity(posxy, new Vector3f(), new Vector3f(0, -90, 0), new Vector3f(50f), loader){
                    @Override
                    public void setHighlighted(boolean highlighted) {
                        tools.get(5).setHighlighted(highlighted);
                        super.setHighlighted(highlighted);
                    }
                });

                Model rotx = new Model(rotationThick);
                rotx.material = new Material(red, 0f);
                toolsMousePicking.add(new Entity(rotx, new Vector3f(), new Vector3f(180, 0, -90), new Vector3f(50f), loader){
                    @Override
                    public void setHighlighted(boolean highlighted) {
                        tools.get(6).setHighlighted(highlighted);
                        super.setHighlighted(highlighted);
                    }
                });
                Model roty = new Model(rotationThick);
                roty.material = new Material(green, 0f);
                toolsMousePicking.add(new Entity(roty, new Vector3f(), new Vector3f(0, 180, 180), new Vector3f(50f), loader){
                    @Override
                    public void setHighlighted(boolean highlighted) {
                        tools.get(7).setHighlighted(highlighted);
                        super.setHighlighted(highlighted);
                    }
                });
                Model rotz = new Model(rotationThick);
                rotz.material = new Material(blue, 0f);
                toolsMousePicking.add(new Entity(rotz, new Vector3f(), new Vector3f(90, 0, 0), new Vector3f(50f), loader){
                    @Override
                    public void setHighlighted(boolean highlighted) {
                        tools.get(8).setHighlighted(highlighted);
                        super.setHighlighted(highlighted);
                    }
                });

                Model scalx = new Model(scaleThick);
                scalx.material = new Material(red, 0f);
                toolsMousePicking.add(new Entity(scalx, new Vector3f(), xRotation, new Vector3f(50f), loader){
                    @Override
                    public void setHighlighted(boolean highlighted) {
                        tools.get(9).setHighlighted(highlighted);
                        super.setHighlighted(highlighted);
                    }
                });
                Model scaly = new Model(scaleThick);
                scaly.material = new Material(green, 0f);
                toolsMousePicking.add(new Entity(scaly, new Vector3f(), yRotation, new Vector3f(50f), loader){
                    @Override
                    public void setHighlighted(boolean highlighted) {
                        tools.get(10).setHighlighted(highlighted);
                        super.setHighlighted(highlighted);
                    }
                });
                Model scalz = new Model(scaleThick);
                scalz.material = new Material(blue, 0f);
                toolsMousePicking.add(new Entity(scalz, new Vector3f(), zRotation, new Vector3f(50f), loader){
                    @Override
                    public void setHighlighted(boolean highlighted) {
                        tools.get(11).setHighlighted(highlighted);
                        super.setHighlighted(highlighted);
                    }
                });
                Model scalu = new Model(scaleUniform);
                scalu.material = new Material(yellow, 0f);
                toolsMousePicking.add(new Entity(scalu, new Vector3f(), yRotation, new Vector3f(50f), loader){
                    @Override
                    public void setHighlighted(boolean highlighted) {
                        tools.get(12).setHighlighted(highlighted);
                        super.setHighlighted(highlighted);
                    }
                });
            }
        }

        public void testForMouse(boolean hasSelection, Camera camera, MousePicker mousePicker, boolean translate, boolean rotate, boolean scale)
        {
            for(int i = 0; i < tools.size(); i++)
            {
                tools.get(i).highlighted = false;
                toolsMousePicking.get(i).highlighted = false;
            }

            if(hasSelection)
            {
                hit = -1;

                for(int i = 0; i < tools.size(); i++)
                {
                    toolsMousePicking.get(i).testForMouse = hasSelection;

                    Entity entity = tools.get(i);
                    if(entity.highlighted)
                        hit = i;
                }
            }
        }

        public void render(boolean hasSelection, boolean translate, boolean rotate, boolean scale, WindowMan window, ObjectLoader loader, RenderMan renderer, MouseInput mouseInput)
        {
            if(selected >= ToolType.ROTATION_X.value && selected <= ToolType.ROTATION_Z.value)
            {
                Vector2i point = new Vector2i((int) screenPos.x, (int) screenPos.y);
                Vector2i mouse = new Vector2i((int) mouseInput.currentPos.x, (int) mouseInput.currentPos.y);

                renderer.processGuiElement(new Line(mouse, point, Color.black, loader, window, true));
                renderer.processGuiElement(new Quad(ConstantTextures.getTexture(ConstantTextures.CROSSHAIR, 15, 15, loader).id, new Vector2f((int)(screenPos.x - 7.5f), (int)(screenPos.y - 7.5f)), new Vector2f(15, 15)).staticTexture());
            }

            if(hasSelection)
            {
                if(selected == ToolType.INVALID.value)
                {
                    for(int i = 0; i < tools.size(); i++)
                        if((i <= ToolType.POSITION_XY.value && translate) || (i >= ToolType.ROTATION_X.value && i <= ToolType.ROTATION_Z.value && rotate) || (i >= ToolType.SCALE_X.value && i <= ToolType.SCALE_UNIFORM.value && scale))
                        {
                            renderer.processThroughWallEntity(tools.get(i));
                            renderer.processThroughWallEntityForMousePick(toolsMousePicking.get(i));
                        }
                }
                else
                switch (ToolType.fromValue(selected))
                {
                    case POSITION_X:
                    case ROTATION_X:
                    case SCALE_X:
                        renderer.processThroughWallEntity(LineX);
                        break;
                    case POSITION_Y:
                    case ROTATION_Y:
                    case SCALE_Y:
                        renderer.processThroughWallEntity(LineY);
                        break;
                    case POSITION_Z:
                    case ROTATION_Z:
                    case SCALE_Z:
                        renderer.processThroughWallEntity(LineZ);
                        break;
                    case POSITION_YZ:
                        renderer.processThroughWallEntity(LineY);
                        renderer.processThroughWallEntity(LineZ);
                        break;
                    case POSITION_ZX:
                        renderer.processThroughWallEntity(LineZ);
                        renderer.processThroughWallEntity(LineX);
                        break;
                    case POSITION_XY:
                        renderer.processThroughWallEntity(LineX);
                        renderer.processThroughWallEntity(LineY);
                        break;
                }

                if(Float.isNaN(tools.get(0).getTransformation().getScale(new Vector3f()).x))
                    setupToolModels(loader);
            }
        }

        public void render(boolean hasSelection,
                           boolean translateX, boolean translateY, boolean translateZ,
                           boolean rotateX, boolean rotateY, boolean rotateZ,
                           boolean scaleX, boolean scaleY, boolean scaleZ, boolean scaleUniform,
                           int crosshair, WindowMan window, ObjectLoader loader, RenderMan renderer, MouseInput mouseInput)
        {
            if(selected >= ToolType.ROTATION_X.value && selected <= ToolType.ROTATION_Z.value)
            {
                Vector2i point = new Vector2i((int) screenPos.x, (int) screenPos.y);
                Vector2i mouse = new Vector2i((int) mouseInput.currentPos.x, (int) mouseInput.currentPos.y);

                renderer.processGuiElement(new Line(mouse, point, Color.black, loader, window, true));
                renderer.processGuiElement(new Quad(crosshair, new Vector2f((int)(screenPos.x - 7.5f), (int)(screenPos.y - 7.5f)), new Vector2f(15, 15)).staticTexture());
            }


            if(hasSelection)
            {
                if(selected == ToolType.INVALID.value)
                {
                    for(int i = 0; i < tools.size(); i++)
                        if(     (i == ToolType.POSITION_X.value && translateX) || (i == ToolType.POSITION_Y.value && translateY) || (i == ToolType.POSITION_Z.value && translateZ) ||
                                (i == ToolType.POSITION_XY.value && translateX && translateY) || (i == ToolType.POSITION_YZ.value && translateY && translateZ) || (i == ToolType.POSITION_ZX.value && translateX && translateZ) ||
                                (i == ToolType.ROTATION_X.value && rotateX) || (i == ToolType.ROTATION_Y.value && rotateY) || (i == ToolType.ROTATION_Z.value && rotateZ) ||
                                (i == ToolType.SCALE_X.value && scaleX) || (i == ToolType.SCALE_Y.value && scaleY) || (i == ToolType.SCALE_Z.value && scaleZ) || (i == ToolType.SCALE_UNIFORM.value && scaleUniform))
                        {
                            renderer.processThroughWallEntity(tools.get(i));
                            renderer.processThroughWallEntityForMousePick(toolsMousePicking.get(i));
                        }
                }
                else
                switch (ToolType.fromValue(selected))
                {
                    case POSITION_X:
                    case ROTATION_X:
                    case SCALE_X:
                        renderer.processThroughWallEntity(LineX);
                        break;
                    case POSITION_Y:
                    case ROTATION_Y:
                    case SCALE_Y:
                        renderer.processThroughWallEntity(LineY);
                        break;
                    case POSITION_Z:
                    case ROTATION_Z:
                    case SCALE_Z:
                        renderer.processThroughWallEntity(LineZ);
                        break;
                    case POSITION_YZ:
                        renderer.processThroughWallEntity(LineY);
                        renderer.processThroughWallEntity(LineZ);
                        break;
                    case POSITION_ZX:
                        renderer.processThroughWallEntity(LineZ);
                        renderer.processThroughWallEntity(LineX);
                        break;
                    case POSITION_XY:
                        renderer.processThroughWallEntity(LineX);
                        renderer.processThroughWallEntity(LineY);
                        break;
                }

                if(Float.isNaN(tools.get(0).getTransformation().getScale(new Vector3f()).x))
                    setupToolModels(loader);
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

                Vector3f normal = new Vector3f(new Vector3f(0, camera.getPos().y, camera.getPos().z)).sub(new Vector3f(0, currentPosition.y, currentPosition.z)).normalize();//new Vector3f(0, 0, 1);
//                Matrix3f rotationMatrix = new Matrix3f().rotateX(-camera.getRotation().x);
//                normal.mul(rotationMatrix).normalize();
                Vector3f intersection = mouseInput.mousePicker.getPointOnPlaneAbstract(currentPosition, normal);
                if(intersection != null)
                    initPosX = Utils.getClosestPointOnLine(currentPosition, new Vector3f(1, 0, 0), intersection);

                normal = new Vector3f(new Vector3f(camera.getPos().x, 0, camera.getPos().z)).sub(new Vector3f(currentPosition.x, 0, currentPosition.z)).normalize();//new Vector3f(0, 0, 1);
//                rotationMatrix = new Matrix3f().rotateY(-camera.getRotation().y);
//                normal.mul(rotationMatrix).normalize();
                intersection = mouseInput.mousePicker.getPointOnPlaneAbstract(currentPosition, normal);
                if(intersection != null)
                    initPosY = Utils.getClosestPointOnLine(currentPosition, new Vector3f(0, 1, 0), intersection);

                normal = new Vector3f(new Vector3f(camera.getPos().x, camera.getPos().y, 0)).sub(new Vector3f(currentPosition.x, currentPosition.y, 0)).normalize();//new Vector3f(0, 0, 1);
//                rotationMatrix = new Matrix3f().rotateY(Math.toRadians(90)).rotateX(-camera.getRotation().x);
//                normal.mul(rotationMatrix).normalize();
                intersection = mouseInput.mousePicker.getPointOnPlaneAbstract(currentPosition, normal);
                if(intersection != null)
                    initPosZ = Utils.getClosestPointOnLine(currentPosition, new Vector3f(0, 0, 1), intersection);

                initPosYZ = mouseInput.mousePicker.getPointOnPlaneX(currentPosition.x);
                initPosZX = mouseInput.mousePicker.getPointOnPlaneY(currentPosition.y);
                initPosXY = mouseInput.mousePicker.getPointOnPlaneZ(currentPosition.z);
                initPos = new Vector2f((float) mouseInput.currentPos.x, (float) mouseInput.currentPos.y);

                screenPos = camera.worldToScreen(tools.get(0).getTransformation().getTranslation(new Vector3f()), window);

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
            if(selected == -1)
            {
                this.currentPosition = currentPosition;

                LineX.setTransformation(LineX.getTransformation().setTranslation(0, currentPosition.y, currentPosition.z));
                LineY.setTransformation(LineY.getTransformation().setTranslation(currentPosition.x, 0, currentPosition.z));
                LineZ.setTransformation(LineZ.getTransformation().setTranslation(currentPosition.x, currentPosition.y, 0));

                float zoom = (java.lang.Math.max(view.camera.getPos().z / 1000f, 0.0f));

                for (int i = 0; i < tools.size(); i++)
                {
                    tools.get(i).setTransformation(new Matrix4f().identity().setTranslation(currentPosition).rotate(tools.get(i).getTransformation().getRotation(new AxisAngle4f())).scale(Config.FRONT_VIEW ? 10f * zoom : 0.01f * view.camera.getPos().distance(currentPosition)));
                    toolsMousePicking.get(i).setTransformation(new Matrix4f().identity().setTranslation(currentPosition).rotate(toolsMousePicking.get(i).getTransformation().getRotation(new AxisAngle4f())).scale(Config.FRONT_VIEW ? 10f * zoom : 0.01f * view.camera.getPos().distance(currentPosition)));
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
