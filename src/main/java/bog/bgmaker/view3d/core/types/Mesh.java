package bog.bgmaker.view3d.core.types;

import bog.bgmaker.Main;
import bog.bgmaker.view3d.ObjectLoader;
import bog.bgmaker.view3d.core.Bone;
import bog.bgmaker.view3d.core.Material;
import bog.bgmaker.view3d.core.Model;
import bog.bgmaker.view3d.core.Texture;
import bog.bgmaker.view3d.mainWindow.LoadedData;
import bog.bgmaker.view3d.mainWindow.View3D;
import bog.bgmaker.view3d.utils.CWLibUtils.SkeletonUtils;
import bog.bgmaker.view3d.utils.Transformation;
import bog.bgmaker.view3d.utils.Utils;
import cwlib.enums.BoxType;
import cwlib.enums.GfxMaterialFlags;
import cwlib.resources.RGfxMaterial;
import cwlib.resources.RMesh;
import cwlib.structs.gmat.MaterialBox;
import cwlib.structs.gmat.MaterialWire;
import cwlib.structs.mesh.Primitive;
import cwlib.structs.things.Thing;
import cwlib.types.data.ResourceDescriptor;
import executables.gfx.GfxAssembler;
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
    public ResourceDescriptor meshDescriptor;
    public Bone[] skeleton = null;
    public Thing[] boneThings = null;
    public Model singleMesh = null;

    public Mesh(Entity entity, ObjectLoader loader) {
        super(entity, loader);
        try {
            this.meshDescriptor = ((Mesh)entity).meshDescriptor;
        } catch (Exception e) {e.printStackTrace();}
    }

    public Mesh(ResourceDescriptor descriptor, Matrix4f transformation, String entityName, ObjectLoader loader) {
        super(transformation, entityName, loader);
        this.meshDescriptor = descriptor;
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public ArrayList<Model> getModel() {
        if (this.model == null || this.singleMesh == null || reloadModel) {
            reloadModel = false;

            Matrix4f transMat = Transformation.createTransformationMatrix(this);

            if(LoadedData.loadedModels.containsKey(this.meshDescriptor) && LoadedData.loadedSingleModels.containsKey(this.meshDescriptor))
            {
                RMesh mesh = LoadedData.loadMesh(meshDescriptor);
                if(boneThings == null)
                    skeleton = SkeletonUtils.buildSkeleton(mesh.getBones());
                else
                {
                    skeleton = SkeletonUtils.buildSkeleton(mesh.getBones(), boneThings);
                    boneThings = null;
                }
                this.model = LoadedData.loadedModels.get(this.meshDescriptor);
                this.singleMesh = LoadedData.loadedSingleModels.get(this.meshDescriptor);
            }
            else
            {
                RMesh mesh = LoadedData.loadMesh(meshDescriptor);
                if(boneThings == null)
                    skeleton = SkeletonUtils.buildSkeleton(mesh.getBones());
                else
                {
                    skeleton = SkeletonUtils.buildSkeleton(mesh.getBones(), boneThings);
                    boneThings = null;
                }
                try {
                    this.model = this.loader.loadRMeshArr(mesh);
                    this.singleMesh = this.loader.loadRMesh(mesh);
                }catch (Exception e){e.printStackTrace();}

                if(this.model != null && this.singleMesh != null)
                {
                    LoadedData.loadedModels.put(this.meshDescriptor, this.model);
                    LoadedData.loadedSingleModels.put(this.meshDescriptor, this.singleMesh);
                }
            }
        }
        return this.model;
    }
}
