package bog.lbpas.view3d.core.types;

import bog.lbpas.view3d.managers.assetLoading.ObjectLoader;
import bog.lbpas.view3d.core.Model;
import bog.lbpas.view3d.mainWindow.LoadedData;
import bog.lbpas.view3d.utils.CWLibUtils.SkeletonUtils;
import bog.lbpas.view3d.utils.FilePicker;
import com.formdev.flatlaf.util.SystemFileChooser;
import cwlib.enums.LethalType;
import cwlib.enums.Part;
import cwlib.resources.RBevel;
import cwlib.resources.RMesh;
import cwlib.resources.RStaticMesh;
import cwlib.structs.mesh.Bone;
import cwlib.structs.things.parts.*;
import cwlib.types.data.ResourceDescriptor;
import cwlib.types.databases.FileDBRow;
import cwlib.types.databases.FileEntry;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import javax.swing.filechooser.FileNameExtensionFilter;
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

    public boolean failedLoadingRMesh = false;

    public Thing(cwlib.structs.things.Thing thing, ObjectLoader loader)
    {
        this.thing = thing;
        this.loader = loader;
    }

    public cwlib.structs.things.Thing[] getBones()
    {
        PRenderMesh pMesh = thing.getPart(Part.RENDER_MESH);
        if(pMesh == null)
            return null;
        cwlib.structs.things.Thing[] ogbones = pMesh.boneThings;
        cwlib.structs.things.Thing[] bones = new cwlib.structs.things.Thing[renderMesh.mesh.getBones().length];

        for(int i = 0; i < bones.length; i++)
            bones[i] = new cwlib.structs.things.Thing();

        cwlib.structs.things.Thing t = new cwlib.structs.things.Thing();
        t.setPart(Part.POS, new PPos(getTransformation()));

        bones = SkeletonUtils.computeBoneThings(bones, t, this.getTransformation(), renderMesh.mesh.getBones());

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
        pos.recomputeLocalPos(this.thing);
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
        if(this.failedLoadingRMesh || meshDescriptor == null)
            return;

        if(LoadedData.loadedModels.containsKey(meshDescriptor))
        {
            this.renderMesh = LoadedData.loadedModels.get(meshDescriptor);

            buildName(this.thing);
        }
        else
        {
            RMesh mesh = LoadedData.loadMesh(meshDescriptor);

            if(mesh == null)
                this.failedLoadingRMesh = true;

            Model m = null;

            buildName(this.thing);

            try {
                m = this.loader.loadRMeshArr(mesh);
            }catch (Exception e){e.printStackTrace();}

            if(m != null && !this.failedLoadingRMesh)
            {
                LoadedData.loadedModels.put(meshDescriptor, m);
            }

            if(!this.failedLoadingRMesh)
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
        File file = FilePicker.saveFile(
                this.thing.name + ".obj",
                new SystemFileChooser.FileNameExtensionFilter[]{new SystemFileChooser.FileNameExtensionFilter("OBJ (*.obj)", "obj")}
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
                        Vector3f vPos = new Vector3f(m.vertices[i * 3],
                                m.vertices[i * 3 + 1],
                                m.vertices[i * 3 + 2]);
                        vPos = vPos.mulProject(this.getTransformation());

                        v += "v " + vPos.x + " " + vPos.y + " " + vPos.z + "\n";
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
                        Vector3f vNormal = new Vector3f(m.normals[i * 3],
                                m.normals[i * 3 + 1],
                                m.normals[i * 3 + 2]);
                        vNormal = vNormal.mul(new Matrix3f(new Matrix4f(this.getTransformation()).invert().transpose())).normalize();

                        vn += "vn " + vNormal.x + " " + vNormal.y + " " + vNormal.z + "\n";
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

    @Override
    public void reloadModel() {
        super.reloadModel();
        this.failedLoadingRMesh = false;
    }

    public static void buildName(cwlib.structs.things.Thing thing) {
        if(thing.name == null || thing.name.isEmpty() || thing.name.isBlank())
        {
            ResourceDescriptor ent = null;

            if (thing.hasPart(Part.RENDER_MESH) && ((PRenderMesh) thing.getPart(Part.RENDER_MESH)).mesh != null) {
                ent = ((PRenderMesh) thing.getPart(Part.RENDER_MESH)).mesh;
            } else if (thing.hasPart(Part.GENERATED_MESH) && ((PGeneratedMesh) thing.getPart(Part.GENERATED_MESH)).gfxMaterial != null) {
                ent = ((PGeneratedMesh) thing.getPart(Part.GENERATED_MESH)).gfxMaterial;
            } else if (thing.hasPart(Part.SCRIPT_NAME) && ((PScriptName) thing.getPart(Part.SCRIPT_NAME)).name != null) {
                thing.name = ((PScriptName) thing.getPart(Part.SCRIPT_NAME)).name;
            } else if (thing.hasPart(Part.SCRIPT) && ((PScript) thing.getPart(Part.SCRIPT)).instance.script != null) {
                ent = ((PScript) thing.getPart(Part.SCRIPT)).instance.script;
            } else if (thing.hasPart(Part.EFFECTOR)) {
                thing.name = "Effector";
            } else if (thing.hasPart(Part.LEVEL_SETTINGS)) {
                thing.name = "Level Settings";
            } else if (thing.hasPart(Part.SHAPE)) {
                thing.name = "Shape";
            } else if (thing.hasPart(Part.CHECKPOINT)) {
                thing.name = "Checkpoint";
            } else if (thing.hasPart(Part.TRIGGER) && ((PTrigger) thing.getPart(Part.TRIGGER)).triggerType != null) {
                thing.name = ((PTrigger) thing.getPart(Part.TRIGGER)).triggerType.name();
                thing.name = "Trigger " + thing.name.substring(0, 1).toUpperCase() + thing.name.substring(1).toLowerCase();
            } else if (thing.hasPart(Part.EMITTER)) {
                thing.name = "Emitter";
            } else if (thing.hasPart(Part.GROUP)) {
                thing.name = "Group";
            } else if (thing.hasPart(Part.AUDIO_WORLD)) {
                thing.name = "Audio";
            } else if (thing.hasPart(Part.SPRITE_LIGHT)) {
                thing.name = "Light";
            } else if (thing.hasPart(Part.SWITCH_INPUT)) {
                thing.name = "Switch Input";
            } else if (thing.hasPart(Part.SWITCH)) {
                thing.name = "Switch";
            } else if (thing.hasPart(Part.JOINT)) {
                thing.name = "Joint";
            } else if (thing.hasPart(Part.SWITCH_KEY)) {
                thing.name = "Tag";
            }

            if (ent != null) {
                FileEntry e = LoadedData.getDigestedEntry(ent);

                if (e != null) {
                    String name = e.getName();

                    int extInd = name.lastIndexOf(".");
                    boolean nameIsHash = name.substring(0, extInd != -1 ? extInd : name.length()).equalsIgnoreCase(e.getSHA1().toString());

                    if (!(e instanceof FileDBRow) && nameIsHash)
                        name = name.substring(name.length() - 12);

                    thing.name = name.substring(0, name.lastIndexOf("."));
                }
            }
        }

        if(thing.hasPart(Part.RENDER_MESH) && ((PRenderMesh)thing.getPart(Part.RENDER_MESH)).mesh != null)
        {
            RMesh msh = LoadedData.loadMesh(((PRenderMesh)thing.getPart(Part.RENDER_MESH)).mesh);
            if(msh != null)
                for(int i = 0; i < ((PRenderMesh)thing.getPart(Part.RENDER_MESH)).boneThings.length; i++)
                {
                    cwlib.structs.things.Thing th = ((PRenderMesh)thing.getPart(Part.RENDER_MESH)).boneThings[i];

                    if(!(th.name == null || th.name.isEmpty() || th.name.isBlank()))
                        continue;

                    Bone[] bonearray = msh.getBones();
                    if(th!= null && th.name == null && i < bonearray.length && bonearray[i] != null)
                        th.name = msh.getBones()[i].getName();

                }
        }
    }
}
