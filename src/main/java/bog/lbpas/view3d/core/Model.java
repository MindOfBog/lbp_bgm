package bog.lbpas.view3d.core;

import bog.lbpas.view3d.managers.assetLoading.ObjectLoader;
import cwlib.resources.RMesh;
import org.lwjgl.opengl.GL30;

/**
 * @author Bog
 */
public class Model {

    public int vao;
    public int[] vbos;
    public int vertexCount;

    public Material material;

    public float[] vertices, textureCoords, normals, tangents, weights;
    public int[] joints, indices, gmats;

    public boolean hasBones = false;
    public RMesh mesh = null;

    public Model() {
        this.material = new Material();
    }

    public Model(int vao, int[] vbos, int vertexCount)
    {
        this.vao = vao;
        this.vbos = vbos;
        this.vertexCount = vertexCount;
        this.material = new Material();
    }

    public Model(int vao, int[] vbos, int vertexCount, int[] indicesArr)
    {
        this.vao = vao;
        this.vbos = vbos;
        this.vertexCount = vertexCount;
        this.material = new Material();
    }
    public Model(Model model, Texture[] texture)
    {
        this.vao = model.vao;
        this.vbos = model.vbos;
        this.vertexCount = model.vertexCount;
        this.material = model.material;
        this.material.textures = texture;
    }

    public Model(Model model)
    {
        vao = model.vao;
        this.vbos = model.vbos;
        vertexCount = model.vertexCount;
        material = model.material;
    }

    public void cleanup(ObjectLoader loader)
    {
        loader.vaos.remove((Object)vao);

        if(vbos != null)
            for(int vbo : vbos)
                loader.vbos.remove((Object)vbo);

        GL30.glDeleteVertexArrays(vao);
        if(vbos != null)
            GL30.glDeleteBuffers(vbos);
    }
}