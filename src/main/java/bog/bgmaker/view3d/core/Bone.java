package bog.bgmaker.view3d.core;

import org.joml.Matrix4f;

/**
 * @author Bog
 */
public class Bone {

    public String name;

    public Bone parent;

    public float[] bones;

    public int animHash;
    public Matrix4f skinPoseMatrix;
    public Matrix4f invSkinPoseMatrix;
    public Matrix4f offset;

    public Bone(String name, Matrix4f skinPoseMatrix, Matrix4f invSkinPoseMatrix, Matrix4f offset, Bone parent, int animHash) {
        this.name = name;
        this.skinPoseMatrix = new Matrix4f(skinPoseMatrix);
        this.invSkinPoseMatrix = new Matrix4f(invSkinPoseMatrix);
        this.offset = new Matrix4f(offset);
        this.parent = parent;
        this.animHash = animHash;
    }

    public Matrix4f getLocalTransform() {
        if (this.parent == null) return this.skinPoseMatrix;
        Bone bone = this.parent;
        return new Matrix4f(bone.invSkinPoseMatrix).mul(this.skinPoseMatrix, new Matrix4f());
    }

}
