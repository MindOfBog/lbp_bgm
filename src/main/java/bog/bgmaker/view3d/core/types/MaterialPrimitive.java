package bog.bgmaker.view3d.core.types;

import bog.bgmaker.Main;
import bog.bgmaker.view3d.ObjectLoader;
import bog.bgmaker.view3d.core.Model;
import bog.bgmaker.view3d.core.Texture;
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
import org.lwjgl.opengl.GL30;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

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

        if (this.modelParts == null || this.modelParts.isEmpty() || reloadModel) {
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

            Model model = loader.loadModel(verts, texCoords, norms, triangles, vertexList);

            try {
                BufferedImage img = null;
                boolean culling = false;
                RGfxMaterial material = Main.view.loadGfxMaterial(gmat);

                if (material != null) {
                    for (int i = 0; i < material.boxes.size(); ++i) {
                        MaterialBox box = material.boxes.get(i);
                        MaterialWire wire = material.findWireFrom(i);
                        int outputBox = material.getOutputBox();

                        if (box.type == BoxType.TEXTURE_SAMPLE) {
                            while (wire.boxTo != outputBox)
                                wire = material.findWireFrom(wire.boxTo);

                            if (wire.portTo == 0) {
                                int texture = box.getParameters()[5];
                                img = Main.view.loadTexture(material.textures[texture]);
                                culling = (material.flags & GfxMaterialFlags.TWO_SIDED) != 0;
                                break;
                            }

                        } else if (box.type == BoxType.COLOR && wire.boxTo == outputBox) {
                            img = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
                            float r = Float.intBitsToFloat((int) box.getParameters()[0]);
                            float g = Float.intBitsToFloat((int) box.getParameters()[1]);
                            float b = Float.intBitsToFloat((int) box.getParameters()[2]);

                            r = org.joml.Math.clamp(0, 1, r);
                            g = org.joml.Math.clamp(0, 1, g);
                            b = Math.clamp(0, 1, b);

                            for(int x = 0; x < img.getWidth(); x++)
                                for(int y = 0; y < img.getHeight(); y++)
                                    img.setRGB(x, y, new Color(r, g, b).getRGB());

                            culling = (material.flags & GfxMaterialFlags.TWO_SIDED) != 0;
                            break;
                        }
                    }
                }

                if (img == null) {
                    img = new BufferedImage(254, 254, BufferedImage.TYPE_INT_RGB);
                    for (int x = 0; x < img.getWidth(); x++)
                        for (int y = 0; y < img.getHeight(); y++)
                            img.setRGB(x, y, new Color((float)(x / 255f), (float)(y / 255f), 0f, 0.5f).getRGB());
                }

                model.material = new bog.bgmaker.view3d.core.Material(new Texture(this.loader.loadTexture(img)));
                model.material.disableCulling = culling;
            } catch (Exception e) {
                e.printStackTrace();
            }

            Matrix4f transMat = Transformation.createTransformationMatrix(this);
            bog.bgmaker.view3d.core.Material aabbMat = new bog.bgmaker.view3d.core.Material(new Vector4f(1.0F, 0.0F, 0.0F, 0.5F), 0.0F);
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

            model.aabb = this.loader.generateAABB(model, transMat, this.transformation.getTranslation(new Vector3f()));
            model.aabb.model = Utils.getCubeModel(this.loader, model.aabb.min, model.aabb.max, aabbMat);

            modelParts = new ArrayList<>();
            modelParts.add(model);
        }

        return this.modelParts;
    }
}
