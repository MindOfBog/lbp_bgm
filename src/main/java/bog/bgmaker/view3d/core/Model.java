package bog.bgmaker.view3d.core;

import bog.bgmaker.view3d.ObjectLoader;
import org.lwjgl.opengl.GL30;

/**
 * @author Bog
 */
public class Model {

    public int vao;
    public int[] vbos;
    public int vertexCount;

    public Material material;
    public int[] indicesArr;

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
        this.indicesArr = indicesArr;
    }

    public Model(int vao, int[] vbos, int vertexCount, Texture[] texture)
    {
        this.vao = vao;
        this.vbos = vbos;
        this.vertexCount = vertexCount;
        this.material = new Material(texture);
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
        indicesArr = model.indicesArr;
    }

    public void cleanup(ObjectLoader loader)
    {
        loader.vaos.remove((Object)vao);

        for(int vbo : vbos)
            loader.vbos.remove((Object)vbo);

        GL30.glDeleteVertexArrays(vao);
        GL30.glDeleteBuffers(vbos);
    }
}
