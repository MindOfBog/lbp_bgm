package bog.bgmaker.view3d.utils.CWLibUtils;

import cwlib.enums.Part;
import cwlib.structs.mesh.Bone;
import cwlib.structs.things.Thing;
import cwlib.structs.things.parts.PPos;
import cwlib.structs.things.parts.PRenderMesh;
import org.joml.Matrix4f;

/**
 * @author Bog
 */
public class SkeletonUtils {

    public static void skeletate(Thing[] boneThings, bog.bgmaker.view3d.core.Bone[] bones, int bone, Thing parentOrRoot) {
        Thing root = boneThings[0];

        Thing boneThing = boneThings[bone];

        if (bones[bone].parent != null) {

            Matrix4f ppos = ((PPos)parentOrRoot.getPart(Part.POS)).worldPosition;
            Matrix4f pos = bones[bone].getLocalTransform();

            Matrix4f wpos = ppos.mul(pos, new Matrix4f()).mul(bones[bone].offset);

            boneThing.setPart(Part.POS, new PPos(root, bones[bone].animHash, wpos));
        }

        int index = 0;
        for (bog.bgmaker.view3d.core.Bone child : bones) {
            if (child.equals(bones[bone])) boneThings[index] = boneThing;
            if (child.parent != null && child.parent.equals(bones[bone]))
                skeletate(boneThings, bones, index, boneThing);
            index++;
        }
    }

    public static Thing[] computeBoneThings(Thing[] boneThings, Thing root, Matrix4f transform, bog.bgmaker.view3d.core.Bone[] bones) {

        boneThings[0] = root;

        PPos pos = boneThings[0].getPart(Part.POS);
        pos.animHash = bones[0].animHash;
        pos.worldPosition = new Matrix4f(transform);
        pos.localPosition = new Matrix4f(pos.worldPosition);
        boneThings[0].setPart(Part.POS, pos);

        for (int i = 0; i < bones.length; i++) {
            if (bones[i].parent != null) continue;

            if (i != 0)
                boneThings[i].setPart(Part.POS, new PPos(root, bones[i].animHash, transform.mul(bones[0].invSkinPoseMatrix, new Matrix4f()).mul(bones[i].skinPoseMatrix, new Matrix4f())));

            skeletate(boneThings, bones, i, boneThings[0]);
        }

        return boneThings;
    }

    public static void skeletate(Thing[] boneThings, Bone[] bones, int bone, Thing parentOrRoot) {
        Thing root = boneThings[0];

        Thing boneThing = boneThings[bone];

        if (bones[bone].parent != -1) {

            Matrix4f ppos = ((PPos)parentOrRoot.getPart(Part.POS)).worldPosition;
            Matrix4f pos = bones[bone].getLocalTransform(bones);

            Matrix4f wpos = ppos.mul(pos, new Matrix4f());

            boneThing.setPart(Part.POS, new PPos(root, bones[bone].animHash, wpos));
        }

        int index = 0;
        for (Bone child : bones) {
            if (child.equals(bones[bone])) boneThings[index] = boneThing;
            if (child.parent != -1 && bones[child.parent].equals(bones[bone]))
                skeletate(boneThings, bones, index, boneThing);
            index++;
        }
    }

    public static Thing[] computeBoneThings(Thing[] boneThings, Thing root, Matrix4f transform, Bone[] bones) {

        boneThings[0] = root;

        PPos pos = boneThings[0].getPart(Part.POS);
        pos.animHash = bones[0].animHash;
        pos.worldPosition = new Matrix4f(transform);
        pos.localPosition = new Matrix4f(pos.worldPosition);

        for (int i = 0; i < bones.length; i++) {
            if (bones[i].parent != -1) continue;

            if (i != 0)
                boneThings[i].setPart(Part.POS, new PPos(root, bones[i].animHash, transform.mul(bones[0].invSkinPoseMatrix, new Matrix4f()).mul(bones[i].skinPoseMatrix, new Matrix4f())));

            skeletate(boneThings, bones, i, boneThings[0]);
        }

        return boneThings;
    }

    public static bog.bgmaker.view3d.core.Bone[] buildSkeleton(Bone[] bones)
    {
        bog.bgmaker.view3d.core.Bone[] builtSkeleton = new bog.bgmaker.view3d.core.Bone[bones.length];

        for(int i = 0; i < bones.length; i++)
        {
            builtSkeleton[i] = buildParents(i, bones);
        }

        return builtSkeleton;
    }

    private static bog.bgmaker.view3d.core.Bone buildParents(int b, Bone[] bones)
    {
        Bone bone = bones[b];

        if(bone.parent == -1)
            return new bog.bgmaker.view3d.core.Bone(bone.getName(), bone.skinPoseMatrix, bone.invSkinPoseMatrix, new Matrix4f().identity(), null, bone.animHash);
        return new bog.bgmaker.view3d.core.Bone(bone.getName(), bone.skinPoseMatrix, bone.invSkinPoseMatrix, new Matrix4f().identity(), buildParents(bone.parent, bones), bone.animHash);
    }

    public static bog.bgmaker.view3d.core.Bone[] buildSkeleton(Bone[] bones, Thing[] boneThings)
    {
        bog.bgmaker.view3d.core.Bone[] builtSkeleton = new bog.bgmaker.view3d.core.Bone[bones.length];

        for(int i = 0; i < bones.length; i++)
        {
            builtSkeleton[i] = buildParents(i, bones, boneThings);
        }

        return builtSkeleton;
    }

    private static bog.bgmaker.view3d.core.Bone buildParents(int b, Bone[] bones, Thing[] boneThings)
    {
        Bone bone = bones[b];
//        Thing boneThing = boneThings[b];
//        PPos pos = boneThing.getPart(Part.POS);

        //TODO BONER IMPORT WHAT THE HELL AM I SUPPOSED TO DO
        if(bone.parent == -1)
        {
            return new bog.bgmaker.view3d.core.Bone(bone.getName(), bone.skinPoseMatrix, bone.invSkinPoseMatrix, new Matrix4f(), null, bone.animHash);
        }

        bog.bgmaker.view3d.core.Bone parent = buildParents(bone.parent, bones, boneThings);

        return new bog.bgmaker.view3d.core.Bone(bone.getName(), bone.skinPoseMatrix, bone.invSkinPoseMatrix, new Matrix4f(), parent, bone.animHash);
    }
}