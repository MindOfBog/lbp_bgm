package bog.lbpas.view3d.utils.CWLibUtils;

import cwlib.enums.Part;
import cwlib.structs.mesh.Bone;
import cwlib.structs.things.Thing;
import cwlib.structs.things.parts.PPos;
import org.joml.Matrix4f;

/**
 * @author Bog
 */
public class SkeletonUtils {

    public static void skeletate(Thing[] boneThings, Bone[] bones, int bone, Thing parentOrRoot) {
        Thing root = boneThings[0];

        Thing boneThing = boneThings[bone];

        if (bones[bone].parent != -1) {

            Matrix4f ppos = ((PPos)parentOrRoot.getPart(Part.POS)).worldPosition;
            Matrix4f pos = bones[bone].getLocalTransform(bones);

            Matrix4f wpos = ppos.mul(pos, new Matrix4f());

            boneThing.setPart(Part.POS, new PPos(root, bones[bone].animHash, wpos));
            boneThing.name = bones[bone].getName();
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
        if(pos == null)
        {
            pos = new PPos(transform);
            boneThings[0].setPart(Part.POS, pos);
        }
        pos.animHash = bones[0].animHash;
        pos.worldPosition = new Matrix4f(transform);
        pos.localPosition = new Matrix4f(pos.worldPosition);

        for (int i = 0; i < bones.length; i++) {
            if (bones[i].parent != -1) continue;

            if (i != 0)
            {
                boneThings[i].setPart(Part.POS, new PPos(root, bones[i].animHash, transform.mul(bones[i].skinPoseMatrix, new Matrix4f())));
                boneThings[i].name = bones[i].getName();
            }

            skeletate(boneThings, bones, i, boneThings[0]);
        }

        return boneThings;
    }
}