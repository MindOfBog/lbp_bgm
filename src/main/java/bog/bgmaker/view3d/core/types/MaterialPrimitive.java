package bog.bgmaker.view3d.core.types;

import bog.bgmaker.Main;
import bog.bgmaker.view3d.ObjectLoader;
import bog.bgmaker.view3d.core.Material;
import bog.bgmaker.view3d.core.Model;
import bog.bgmaker.view3d.core.Texture;
import bog.bgmaker.view3d.mainWindow.LoadedData;
import bog.bgmaker.view3d.mainWindow.View3D;
import bog.bgmaker.view3d.utils.Extruder;
import bog.bgmaker.view3d.utils.Transformation;
import bog.bgmaker.view3d.utils.Utils;
import cwlib.enums.BoxType;
import cwlib.enums.GfxMaterialFlags;
import cwlib.resources.RGfxMaterial;
import cwlib.structs.gmat.MaterialBox;
import cwlib.structs.gmat.MaterialWire;
import cwlib.structs.things.parts.PShape;
import cwlib.types.data.ResourceDescriptor;
import org.joml.Math;
import org.joml.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Bog
 */
public class MaterialPrimitive extends Entity{

    public PShape shape;
    public ResourceDescriptor gmat;
    public ResourceDescriptor bev;
    public ResourceDescriptor mat;

    public int closest = -1;
    public ArrayList<Integer> selectedVertices;

    public MaterialPrimitive(Entity entity, ObjectLoader loader) {
        super(entity, loader);
        this.shape = ((MaterialPrimitive)entity).shape;
        this.gmat = ((MaterialPrimitive)entity).gmat;
        this.bev = ((MaterialPrimitive)entity).bev;
        this.mat = ((MaterialPrimitive)entity).mat;
        this.selectedVertices = new ArrayList<>();
    }

    public MaterialPrimitive(PShape shape, ResourceDescriptor gmat, ResourceDescriptor bev, ResourceDescriptor mat, Matrix4f transformation, String entityName, ObjectLoader loader) {
        super(transformation, entityName, loader);
        this.shape = shape;
        this.gmat = gmat;
        this.bev = bev;
        this.mat = mat;
        this.selectedVertices = new ArrayList<>();
    }

    @Override
    public int getType() {
        return 1;
    }

    @Override
    public ArrayList<Model> getModel() {

        if (this.model == null || reloadModel) {
            reloadModel = false;

            Vector2f[] bevelPoints = new Vector2f[] {
                    new Vector2f(0.2f, 0.0f),
                    new Vector2f(0.1f, 0),
                    new Vector2f(0.02928932188f, 0.02928932188f),
                    new Vector2f(0, 0.1f),
                    new Vector2f(0, 1)
            };

            Vector3f[] vertices = null; Vector2f[] uvs = null; int[] triangles = null;
            Vector3f[] normals = null;

            ArrayList<Vector3f> vertexList = new ArrayList<>();
            ArrayList<Vector2f> uvList = new ArrayList<>();
            ArrayList<Integer> triList = new ArrayList<>();
            ArrayList<Vector3f> normalList = new ArrayList<>();

            Extruder.generateTreviri(
                    shape.polygon, bevelPoints, shape.thickness,
                    vertexList, uvList, triList, normalList
            );

            for(Vector3f vertex : vertexList)
            {
                vertex.z += shape.thickness;
            }

            triangles = triList.stream().mapToInt(Integer::valueOf).toArray();
            vertices = vertexList.toArray(Vector3f[]::new);
            uvs = uvList.toArray(Vector2f[]::new);
            normals = normalList.toArray(Vector3f[]::new);

            int attributeCount = 0x6;
            int elementCount = 0x4 * attributeCount;
            int stride = 0x4 * elementCount;
            int numVerts = vertices.length;

            float[] texCoords = new float[uvs.length * 2];
            for(int i = 0; i < uvs.length; i++)
            {
                texCoords[i * 2] = uvs[i].x;
                texCoords[i * 2 + 1] = uvs[i].y;
            }

            float[] verts = new float[vertices.length * 3];
            for(int i = 0; i < vertices.length; i++)
            {
                verts[i * 3] = vertices[i].x;
                verts[i * 3 + 1] = vertices[i].y;
                verts[i * 3 + 2] = vertices[i].z;
            }

            float[] norms = new float[normals.length * 3];
            for(int i = 0; i < normals.length; i++)
            {
                norms[i * 3] = normals[i].x;
                norms[i * 3 + 1] = normals[i].y;
                norms[i * 3 + 2] = normals[i].z;
            }

            this.model = new ArrayList<Model>(Arrays.asList(new Model[]{loader.loadModel(verts, texCoords, norms, triangles)}));

            try {
                ResourceDescriptor matDescriptor = gmat;
                model.get(0).material = LoadedData.getMaterial(matDescriptor, loader);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return this.model;
    }
}
