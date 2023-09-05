package bog.bgmaker.view3d.core.types;

import bog.bgmaker.Main;
import bog.bgmaker.view3d.ObjectLoader;
import bog.bgmaker.view3d.core.Material;
import bog.bgmaker.view3d.core.Model;
import bog.bgmaker.view3d.core.Triangle;
import bog.bgmaker.view3d.mainWindow.View3D;
import bog.bgmaker.view3d.utils.Transformation;
import bog.bgmaker.view3d.utils.Utils;
import org.joml.Math;
import org.joml.*;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.Arrays;

public class Entity {
    public ArrayList<Model> model;
    public Matrix4f transformation;
    public boolean testForMouse = false;
    public boolean highlighted = false;
    public boolean selected = false;
    public String entityName = "";
    ObjectLoader loader;

    public Entity(Model model, Matrix4f transformation, ObjectLoader loader) {
        this.model = new ArrayList<Model>(Arrays.asList(new Model[]{model}));
        this.transformation = transformation;
        this.loader = loader;
    }

    public Entity(Model model, Vector3f pos, Vector3f rotation, Vector3f scale, ObjectLoader loader) {
        this.model = new ArrayList<Model>(Arrays.asList(new Model[]{model}));
        this.transformation = new Matrix4f().identity()
                .translate(pos)
                .rotateX(Math.toRadians(rotation.x))
                .rotateY(Math.toRadians(rotation.y))
                .rotateZ(Math.toRadians(rotation.z))
                .scale(scale);
        this.loader = loader;
    }

    public Entity(Entity entity, ObjectLoader loader) {
        this.model = entity.model;
        this.entityName = entity.entityName;
        this.loader = loader;
    }

    public Entity(Matrix4f transformation, String entityName, ObjectLoader loader) {
        this.transformation = transformation;
        this.entityName = entityName;
        this.loader = loader;
    }

    boolean reloadModel = false;
    public void reloadModel()
    {
        reloadModel = true;
    }
    public void setModel(ArrayList<Model> model) {
        this.model = model;
    }

    public void setModel(Model model) {
        this.model = new ArrayList<Model>(Arrays.asList(new Model[]{model}));
    }

    public int getType(){return -1;}
    public ArrayList<Model> getModel(){return model;}
}
