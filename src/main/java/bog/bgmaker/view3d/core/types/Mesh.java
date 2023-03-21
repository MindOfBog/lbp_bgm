package bog.bgmaker.view3d.core.types;

import bog.bgmaker.Main;
import bog.bgmaker.view3d.ObjectLoader;
import bog.bgmaker.view3d.core.Material;
import bog.bgmaker.view3d.core.Model;
import bog.bgmaker.view3d.core.Texture;
import bog.bgmaker.view3d.utils.Transformation;
import bog.bgmaker.view3d.utils.Utils;
import cwlib.enums.BoxType;
import cwlib.enums.GfxMaterialFlags;
import cwlib.resources.RGfxMaterial;
import cwlib.resources.RMesh;
import cwlib.structs.gmat.MaterialBox;
import cwlib.structs.gmat.MaterialWire;
import cwlib.structs.mesh.Primitive;
import cwlib.types.data.ResourceDescriptor;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * @author Bog
 */
public class Mesh extends Entity{

    public RMesh mesh;
    public ResourceDescriptor meshDescriptor;

    public Mesh(Entity entity, ObjectLoader loader) {
        super(entity, loader);
        try {
            this.mesh = ((Mesh)entity).mesh;
        } catch (Exception e) {e.printStackTrace();}
    }

    public Mesh(RMesh mesh, ResourceDescriptor descriptor, Matrix4f transformation, String entityName, ObjectLoader loader) {
        super(transformation, entityName, loader);
        this.mesh = mesh;
        this.meshDescriptor = descriptor;
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public ArrayList<Model> getModel() {
        if (this.modelParts == null || this.modelParts.isEmpty() || reloadModel) {
            reloadModel = false;

            Matrix4f transMat = Transformation.createTransformationMatrix(this);

            if(ObjectLoader.loadedModels.containsKey(this.meshDescriptor))
            {
                ArrayList<Model> models = ObjectLoader.loadedModels.get(this.meshDescriptor);
                this.modelParts = new ArrayList<>();

                for(int i = 0; i < models.size(); i++)
                {
                    Model model = new Model(models.get(i));
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
                    this.modelParts.add(model);
                }
            }
            else
            {
                ArrayList<Model> models = new ArrayList<>();

                for (Primitive[] primitives : mesh.getSubmeshes())
                    for (Primitive primitiveSubmesh : primitives) {
                        Model model = this.loader.loadSubmesh(mesh, primitiveSubmesh, transMat, this.transformation.getTranslation(new Vector3f()));

                        if (model == null)
                            continue;

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

                        try {
                            BufferedImage img = null;
                            boolean culling = false;
                            RGfxMaterial material = Main.view.loadGfxMaterial(primitiveSubmesh.getMaterial());

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

                                        r = Math.clamp(0, 1, r);
                                        g = Math.clamp(0, 1, g);
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
                                img = new BufferedImage(254, 254, BufferedImage.TYPE_INT_ARGB);
                                for (int x = 0; x < img.getWidth(); x++)
                                    for (int y = 0; y < img.getHeight(); y++)
                                        img.setRGB(x, y, new Color((float)(x / 255f), (float)(y / 255f), 0f, 0.5f).getRGB());
                            }

                            model.material = new Material(new Texture(this.loader.loadTexture(img, GL11.GL_LINEAR_MIPMAP_LINEAR, GL11.GL_LINEAR)));
                            model.material.disableCulling = culling;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        models.add(model);
                    }

                ObjectLoader.loadedModels.put(this.meshDescriptor, models);
                this.modelParts = models;
            }
        }
        return this.modelParts;
    }
}
