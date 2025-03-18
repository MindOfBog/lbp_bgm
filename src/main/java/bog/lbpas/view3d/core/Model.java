package bog.lbpas.view3d.core;

import bog.lbpas.view3d.managers.assetLoading.ObjectLoader;
import cwlib.resources.RMesh;
import cwlib.resources.RStaticMesh;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bog
 */
public class Model {

    public int primitive = GL11.GL_TRIANGLES;

    public int[] attribs;

    public int vao;
    public int[] vbos;
    public int vertexCount;

    public Material material;

    public float[] vertices, textureCoords, normals, tangents, weights;
    public int[] joints, indices, gmats;

    public boolean hasBones = false;
    public RMesh mesh = null;
    public RStaticMesh staticMesh = null;

    public Model() {
        this.material = new Material();
    }

    public Model(int vao, int[] vbos, int vertexCount, int[] attribs)
    {
        this.vao = vao;
        this.vbos = vbos;
        this.vertexCount = vertexCount;
        this.material = new Material();
        this.attribs = attribs;
    }

    public Model(int vao, int[] vbos, int vertexCount, int[] indicesArr, int[] attribs)
    {
        this.vao = vao;
        this.vbos = vbos;
        this.vertexCount = vertexCount;
        this.material = new Material();
        this.attribs = attribs;
    }
    public Model(Model model, Texture[] texture, int[] attribs)
    {
        this.vao = model.vao;
        this.vbos = model.vbos;
        this.vertexCount = model.vertexCount;
        this.material = model.material;
        this.material.textures = texture;
        this.attribs = attribs;
    }

    public Model(Model model)
    {
        this.vao = model.vao;
        this.vbos = model.vbos;
        this.vertexCount = model.vertexCount;
        this.material = model.material;
        this.attribs = model.attribs;
        this.primitive = model.primitive;
    }

    public void drawModel()
    {
        GL11.glDrawElements(primitive, vertexCount, GL11.GL_UNSIGNED_INT, 0L);
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