package bog.lbpas.view3d.core.types;

import bog.lbpas.view3d.managers.assetLoading.ObjectLoader;
import bog.lbpas.view3d.core.Model;
import bog.lbpas.view3d.mainWindow.LoadedData;
import bog.lbpas.view3d.utils.CWLibUtils.SkeletonUtils;
import bog.lbpas.view3d.utils.print;
import cwlib.enums.LethalType;
import cwlib.enums.Part;
import cwlib.resources.RBevel;
import cwlib.resources.RMesh;
import cwlib.resources.RStaticMesh;
import cwlib.structs.things.parts.*;
import cwlib.types.data.ResourceDescriptor;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import toolkit.utilities.FileChooser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

/**
 * @author Bog
 */
public class Thing extends Entity{

    public cwlib.structs.things.Thing thing;

    public boolean forceOrtho = false;
    public Vector3f rotation;
    public Vector3f scale;

    public Model shapeMesh;
    public Model renderMesh;
    public ArrayList<Model> staticMesh;

    public RBevel bevelData;

    public Thing(cwlib.structs.things.Thing thing, ObjectLoader loader)
    {
        this.thing = thing;
        this.loader = loader;
    }

    public cwlib.structs.things.Thing[] getBones()
    {
        PRenderMesh pMesh = ((PRenderMesh)thing.getPart(Part.RENDER_MESH));
        if(pMesh == null)
            return null;
        cwlib.structs.things.Thing[] ogbones = pMesh.boneThings;
        cwlib.structs.things.Thing[] bones = new cwlib.structs.things.Thing[renderMesh.mesh.getBones().length];

        for(int i = 0; i < bones.length; i++)
            bones[i] = new cwlib.structs.things.Thing();
        bones = SkeletonUtils.computeBoneThings(bones, new cwlib.structs.things.Thing().setPart(Part.POS, new PPos(getTransformation())), this.getTransformation(), renderMesh.mesh.getBones());

        if(ogbones != null && ogbones.length == bones.length)
            for(int i = 0; i < ogbones.length; i++)
            {
                if(ogbones[i] == null)
                    continue;
                PPos ogbppos = ogbones[i].getPart(Part.POS);
                if(ogbppos != null)
                    bones[i].setPart(Part.POS, new PPos(new Matrix4f(ogbppos.worldPosition)));
            }

        PJoint joint = this.thing.getPart(Part.JOINT);

        for(int i = 0; i < bones.length; i++)
        {
            cwlib.structs.things.Thing boneThing = bones[i];
            if(joint != null)
            {
//      todo          Matrix4f aOffset = new Matrix4f().identity()
//                        .translate(joint.aContact)
//                        .rotate(joint.aAngleOffset, new Vector3f(0, 0, 1))
//                        .scale(0);
//                Matrix4f bOffset = new Matrix4f().identity()
//                        .translate(joint.bContact)
//                        .rotate(joint.bAngleOffset, new Vector3f(0, 0, 1))
//                        .scale(0);
//
//                Matrix4f aTranformation = new Matrix4f(((PPos)joint.a.getPart(Part.POS)).worldPosition)
//                        .mul(aOffset);
//                Matrix4f bTranformation =  new Matrix4f(((PPos)joint.b.getPart(Part.POS)).worldPosition)
//                        .mul(bOffset);




//                Matrix4f aTranformation = new Matrix4f(((PPos)joint.a.getPart(Part.POS)).worldPosition)
//                        .translate(joint.aContact)
//                        .rotate(joint.aAngleOffset, new Vector3f(0, 0, 1));
//                Matrix4f bTranformation = new Matrix4f(((PPos)joint.b.getPart(Part.POS)).worldPosition)
//                        .translate(joint.bContact)
//                        .rotate(joint.bAngleOffset, new Vector3f(0, 0, 1));

//                PPos ppos = new PPos(i == 0 ? aTranformation : bTranformation);
//                boneThing.setPart(Part.POS, ppos);//.scale(joint.modScale, joint.modScale, 1);
            }
        }

        return bones;
    }

    @Override
    public Matrix4f getTransformation() {
        if(thing == null)
            return new Matrix4f();

        PPos ppos = this.thing.getPart(Part.POS);
        Matrix4f pos = ppos == null ? new Matrix4f().identity().scale(1) : ppos.worldPosition;

        PJoint joint = this.thing.getPart(Part.JOINT);

        if(joint != null)
        {
//      todo      Matrix4f aOffset = new Matrix4f().identity()
//                    .translate(joint.aContact == null ? new Vector3f() : joint.aContact)
//                    .rotate(joint.aAngleOffset, new Vector3f(0, 0, 1));
//
//            PPos pposj = (PPos)joint.a.getPart(Part.POS);
//
//            Matrix4f aTranformation = new Matrix4f(pposj.worldPosition)
//                    .mul(aOffset);
//            Matrix4f aTranformation = new Matrix4f(((PPos)joint.a.getPart(Part.POS)).worldPosition)
//                    .translate(joint.aContact)
//                    .rotate(joint.aAngleOffset, new Vector3f(0, 0, 1));
//            PPos jppos = new PPos(aTranformation);
//            this.thing.setPart(Part.POS, jppos);
//            pos.translate(pos1.getTranslation(new Vector3f()));//pos.mul(pos1).rotate(joint.aAngleOffset, new Vector3f(0, 0, joint.invertAngle ? -1 : 1)).scale(joint.modScale, joint.modScale, 1);
        }

//        if(forceOrtho && rotation != null && scale != null)
//        {
//            Vector3f wpos = pos.worldPosition.getTranslation(new Vector3f());
//            pos.worldPosition.identity()
//                    .translate(wpos)
//                    .rotateXYZ(rotation)
//                    .scale(scale);
//        }

        return pos;
    }

    @Override
    public void setTransformation(Matrix4f transformation) {
        PPos pos = this.thing.getPart(Part.POS);
        if(pos == null)
            pos = new PPos();
        pos.worldPosition = transformation;
        pos.localPosition = transformation;
        this.thing.setPart(Part.POS, pos);
    }

    @Override
    public ArrayList<Model> getModel() {

        if(this.model == null || this.reloadModel)
        {
            this.reloadModel = false;

            this.model = new ArrayList<>();

            PRenderMesh renderMesh = this.thing.getPart(Part.RENDER_MESH);
            if(renderMesh != null)
                getMesh(renderMesh.mesh);
            else
                this.renderMesh = null;

            if(this.renderMesh != null)
                this.model.add(this.renderMesh);

            PLevelSettings levelSettings = this.thing.getPart(Part.LEVEL_SETTINGS);
            if(levelSettings != null)
                getStaticMesh(levelSettings.backdropMesh);
            else
                this.staticMesh = null;

            if(this.staticMesh != null)
            {
                for (Model subMesh : this.staticMesh)
                    this.model.add(subMesh);
            }

            PShape shape = this.thing.getPart(Part.SHAPE);
            PGeneratedMesh generatedMesh = this.thing.getPart(Part.GENERATED_MESH);

            bevelData = LoadedData.loadBevel(generatedMesh == null ? null : generatedMesh.bevel);

            if(shape != null)
                generateShape(shape, generatedMesh);
            else
            {
                this.cleanup();
                this.shapeMesh = null;
            }
            if(this.shapeMesh != null)
                this.model.add(shapeMesh);
        }

        return super.getModel();
    }

    public void generateShape(PShape shape, PGeneratedMesh generatedMesh)
    {
        boolean lethal = shape.lethalType == LethalType.GAS ||
                            shape.lethalType == LethalType.GAS2 ||
                            shape.lethalType == LethalType.GAS3 ||
                            shape.lethalType == LethalType.GAS4 ||
                            shape.lethalType == LethalType.GAS5 ||
                            shape.lethalType == LethalType.GAS6;

        RBevel bev = LoadedData.loadBevel(generatedMesh == null || lethal || (shape.thickness == 10 && shape.material.isGUID() && shape.material.getGUID().getValue() == 10724) ? null : generatedMesh.bevel);

        if(this.shapeMesh == null)
            this.shapeMesh = new Model();
        this.shapeMesh = loader.generateMaterialMesh(this.shapeMesh,
                generatedMesh,
                shape, bev,
                new Matrix4f(this.getTransformation())
        );

        if(generatedMesh == null || generatedMesh.gfxMaterial == null)
            this.shapeMesh.material.ambientColor.w = 0;
    }

    public void getMesh(ResourceDescriptor meshDescriptor)
    {
        if(meshDescriptor == null)
            return;

        if(LoadedData.loadedModels.containsKey(meshDescriptor))
        {
            this.renderMesh = LoadedData.loadedModels.get(meshDescriptor);
        }
        else
        {
            RMesh mesh = LoadedData.loadMesh(meshDescriptor);

            Model m = null;

            try {
                m = this.loader.loadRMeshArr(mesh);
            }catch (Exception e){e.printStackTrace();}

            if(m != null)
            {
                LoadedData.loadedModels.put(meshDescriptor, m);
            }

            this.renderMesh = LoadedData.loadedModels.get(meshDescriptor);
        }
    }

    public void getStaticMesh(ResourceDescriptor meshDescriptor)
    {
        if(meshDescriptor == null)
            return;

        if(LoadedData.loadedStaticModels.containsKey(meshDescriptor))
        {
            this.staticMesh = LoadedData.loadedStaticModels.get(meshDescriptor);
        }
        else
        {
            RStaticMesh mesh = LoadedData.loadStaticMesh(meshDescriptor);

            ArrayList<Model> m = null;

            try {
                m = this.loader.loadStaticMesh(mesh, this);
            }catch (Exception e){e.printStackTrace();}

            if(m != null)
            {
                LoadedData.loadedStaticModels.put(meshDescriptor, m);
            }

            this.staticMesh = LoadedData.loadedStaticModels.get(meshDescriptor);
        }
    }

    public void cleanup()
    {
        if(shapeMesh != null)
            shapeMesh.cleanup(loader);
    }

    public void exportModelOBJ()
    {
        File file = FileChooser.openFile(
                this.thing.name + ".obj",
                "obj",
                true
        );

        if (file != null)
        {
            String v = "";
            String vt = "";
            String vn = "";
            String f = "";

            int offset = 0;

            for(Model m : this.getModel())
            {
                if(m == null || m.vertices == null || m.indices == null)
                    continue;

                for(int i = 0; i < m.vertices.length / 3; i++)
                {
                    {
                        float x = m.vertices[i * 3];
                        float y = m.vertices[i * 3 + 1];
                        float z = m.vertices[i * 3 + 2];

                        v += "v " + x + " " + y + " " + z + "\n";
                    }

                    if(m.textureCoords != null)
                    {
                        float x = m.textureCoords[i * 4];
                        float y = m.textureCoords[i * 4 + 1];

                        vt += "vt " + x + " " + y + "\n";
                    }
                    else
                        vt += "vt 0.0 0.0\n";

                    if(m.normals != null)
                    {
                        float x = m.normals[i * 3];
                        float y = m.normals[i * 3 + 1];
                        float z = m.normals[i * 3 + 2];

                        vn += "vn " + x + " " + y + " " + z + "\n";
                    }
                    else
                        vn += "vn 0 0 0\n";
                }

                for(int i = 0; i < m.indices.length / 3; i++)
                {
                    int ind1 = m.indices[i * 3] + 1 + offset;
                    int ind2 = m.indices[i * 3 + 1] + 1 + offset;
                    int ind3 = m.indices[i * 3 + 2] + 1 + offset;

                    f += "f " +
                            ind1 + "/" + ind1 + "/" + ind1 + " " +
                            ind2 + "/" + ind2 + "/" + ind2 + " " +
                            ind3 + "/" + ind3 + "/" + ind3 + "\n";
                }

                offset += m.indices.length / 3;
            }

            try
            {
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                writer.write("# Mesh exported with dn extractor \n\no " + this.thing.name + "\n" + v + vt + vn + "s off\n" + f);
                writer.close();
            }catch (Exception e){e.printStackTrace();}
        }
    }
}
