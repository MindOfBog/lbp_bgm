package bog.lbpas.view3d.core.types;

import bog.lbpas.view3d.managers.assetLoading.ObjectLoader;
import bog.lbpas.view3d.core.Model;
import org.joml.Math;
import org.joml.*;

import java.util.ArrayList;
import java.util.Arrays;

public class Entity {
    public ArrayList<Model> model;
    private Matrix4f transformation;
    public Matrix4f prevTransformation = new Matrix4f();
    public float prevThickness = -1;
    public boolean testForMouse = false;
    public boolean highlighted = false;
    public boolean selected = false;
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
                .rotateAffineXYZ(Math.toRadians(rotation.x), Math.toRadians(rotation.y), Math.toRadians(rotation.z))
                .scale(scale);
        this.loader = loader;
    }

    public Entity(Entity entity, ObjectLoader loader) {
        this.model = entity.model;
        this.loader = loader;
    }

    public Entity(Matrix4f transformation, String entityName, ObjectLoader loader) {
        this.transformation = transformation;
        this.loader = loader;
    }

    public Entity(){}

    boolean reloadModel = false;
    public void reloadModel()
    {
        reloadModel = true;
    }

    public ArrayList<Model> getModel(){return model;}

    public Matrix4f getTransformation() {
        return transformation;
    }

    public void setHighlighted(boolean highlighted)
    {
        this.highlighted = highlighted;
    }

    public void setTransformation(Matrix4f transformation) {
        this.transformation = transformation;
    }
}