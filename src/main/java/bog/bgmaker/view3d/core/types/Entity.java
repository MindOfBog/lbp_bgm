package bog.bgmaker.view3d.core.types;

import bog.bgmaker.Main;
import bog.bgmaker.view3d.ObjectLoader;
import bog.bgmaker.view3d.core.Material;
import bog.bgmaker.view3d.core.Model;
import bog.bgmaker.view3d.core.Triangle;
import bog.bgmaker.view3d.utils.Transformation;
import bog.bgmaker.view3d.utils.Utils;
import org.joml.Math;
import org.joml.*;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;

public class Entity {
    ArrayList<Model> modelParts;
    public Matrix4f transformation;
    public Matrix4f prevTransformation;
    public boolean testForMouse = false;
    public boolean highlighted = false;
    public boolean selected = false;
    public String entityName = "";
    ObjectLoader loader;

    public Entity(Model model, Matrix4f transformation, ObjectLoader loader) {
        this.prevTransformation = new Matrix4f();
        this.modelParts = new ArrayList<>();
        this.modelParts.add(model);
        this.transformation = transformation;
        this.loader = loader;
    }

    public Entity(Model model, Vector3f pos, Vector3f rotation, Vector3f scale, ObjectLoader loader) {
        this.prevTransformation = new Matrix4f();
        this.modelParts = new ArrayList<>();
        this.modelParts.add(model);
        this.transformation = new Matrix4f().identity()
                .translate(pos)
                .rotateX(Math.toRadians(rotation.x))
                .rotateY(Math.toRadians(rotation.y))
                .rotateZ(Math.toRadians(rotation.z))
                .scale(scale);
        this.loader = loader;
    }

    public Entity(Entity entity, ObjectLoader loader) {
        this.prevTransformation = new Matrix4f();
        this.modelParts = entity.modelParts;
        this.prevTransformation = entity.prevTransformation;
        this.entityName = entity.entityName;
        this.loader = loader;
    }

    public Entity(Matrix4f transformation, String entityName, ObjectLoader loader) {
        this.prevTransformation = new Matrix4f();
        this.transformation = transformation;
        this.entityName = entityName;
        this.loader = loader;
    }

    boolean reloadModel = false;
    public void reloadModel()
    {
        reloadModel = true;
    }
    public void updateModel() {
        this.transformation = new Matrix4f(this.transformation);
        if(this.modelParts != null && !this.modelParts.isEmpty())
            if ((!this.prevTransformation.equals(this.transformation)))
            {
                for (Model model : this.modelParts)
                {
                    Matrix4f transMat = Transformation.createTransformationMatrix(this);
                    Material aabbMat = new Material(new Vector4f(1.0F, 0.0F, 0.0F, 0.5F), 0.0F);
                    aabbMat.disableCulling = true;

                    if(model.aabb != null && model.aabb.model != null)
                    {
                        int vao = model.aabb.model.vao;
                        GL30.glDeleteVertexArrays(vao);
                        loader.vaos.remove((Object)vao);
                        int[] vbos = model.aabb.model.vbos;
                        for(int vbo : vbos)
                        {
                            GL30.glDeleteBuffers(vbo);
                            loader.vbos.remove((Object)vbo);
                        }
                    }

                    model.aabb = Main.view.loader.generateAABB(model, transMat, transformation.getTranslation(new Vector3f()));
                    model.aabb.model = Utils.getCubeModel(Main.view.loader, model.aabb.min, model.aabb.max, aabbMat);
                }
                if (!this.prevTransformation.equals(this.transformation))
                    this.prevTransformation = this.transformation;
            }
    }

    public void setModel(Model model) {
        this.modelParts = new ArrayList<>();
        this.modelParts.add(model);
    }

    public Vector3f rayIntersectModel(Vector3f ray, Vector3f origin) {

        ArrayList<Triangle> tris = new ArrayList<>();
        for(Model model : getModel())
            for(Triangle tri : model.triangles)
                tris.add(tri);

        for (Triangle triangle : tris) {
            Vector3f v0v1 = triangle.p2.sub((Vector3fc)triangle.p1, new Vector3f());
            Vector3f v0v2 = triangle.p3.sub((Vector3fc)triangle.p1, new Vector3f());
            Vector3f N = v0v1.cross((Vector3fc)v0v2, new Vector3f());
            float area2 = N.length();
            triangle = new Triangle(triangle);
            float D = -(N.x * triangle.p1.x + N.y * triangle.p1.y + N.z * triangle.p1.z);
            float t = -(N.dot((Vector3fc)origin) + D) / N.dot((Vector3fc)ray);
            Vector3f P = origin.add((Vector3fc)ray.mul(t, new Vector3f()), new Vector3f());
            Vector3f edge0 = triangle.p2.sub((Vector3fc)triangle.p1, new Vector3f());
            Vector3f edge1 = triangle.p3.sub((Vector3fc)triangle.p2, new Vector3f());
            Vector3f edge2 = triangle.p1.sub((Vector3fc)triangle.p3, new Vector3f());
            Vector3f C0 = P.sub((Vector3fc)triangle.p1, new Vector3f());
            Vector3f C1 = P.sub((Vector3fc)triangle.p2, new Vector3f());
            Vector3f C2 = P.sub((Vector3fc)triangle.p3, new Vector3f());
            if (N.dot((Vector3fc)edge0.cross((Vector3fc)C0, new Vector3f())) > 0.0F && N
                    .dot((Vector3fc)edge1.cross((Vector3fc)C1, new Vector3f())) > 0.0F && N
                    .dot((Vector3fc)edge2.cross((Vector3fc)C2, new Vector3f())) > 0.0F)
                return P;
        }

        return null;
    }

    public int getType(){return 0;}
    public ArrayList<Model> getModel(){return modelParts;}
}
