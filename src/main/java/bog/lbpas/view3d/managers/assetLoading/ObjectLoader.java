package bog.lbpas.view3d.managers.assetLoading;

import bog.lbpas.Main;
import bog.lbpas.view3d.core.*;
import bog.lbpas.view3d.core.types.Thing;
import bog.lbpas.view3d.mainWindow.View3D;
import bog.lbpas.view3d.utils.FilePicker;
import bog.lbpas.view3d.utils.Utils;
import bog.lbpas.view3d.utils.print;
import common.FileChooser;
import cwlib.resources.RBevel;
import cwlib.resources.RMesh;
import cwlib.resources.RStaticMesh;
import cwlib.structs.things.parts.PGeneratedMesh;
import cwlib.structs.things.parts.PShape;
import cwlib.types.data.ResourceDescriptor;
import org.joml.*;
import org.lwjgl.opengl.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Bog
 */
public class ObjectLoader {

    public ArrayList<Integer> vaos = new ArrayList<>();
    public ArrayList<Integer> vbos = new ArrayList<>();
    public ArrayList<Integer> textures = new ArrayList<>();

    public AsyncTextureMan textureLoader = new AsyncTextureMan(this);
    public AsyncModelMan modelLoader = new AsyncModelMan(this);

    public void primaryThread()
    {
        textureLoader.loadDigestedImages();
        modelLoader.loadDigestedMeshes();
    }

    public void loaderThread(View3D view)
    {
        textureLoader.digestImages();
        modelLoader.digestMeshes(view);
    }

    public Model loadOBJModel(String fileName)
    {
        Model model = new Model();
        modelLoader.digest(new AsyncModelMan.ModelDataOBJ(model, fileName));
        return model;
    }

    public Model loadOBJModelDirect(String fileName)
    {
        Model model = new Model();

        try
        {
            ArrayList<String> lines = Utils.readAllLines(fileName);
            ArrayList<Vector3f> vertices = new ArrayList<>();
            ArrayList<Vector3f> normals = new ArrayList<>();
            ArrayList<Vector4f> textures = new ArrayList<>();
            ArrayList<Vector3i> faces = new ArrayList<>();
            ArrayList<Vector3f> tangents = new ArrayList();

            ArrayList<String[]> f = new ArrayList<>();

            for(String line : lines)
            {
                String[] tokens = line.split("\\s+");
                switch (tokens[0])
                {
                    case "v":
                        //vertices
                        Vector3f verticesVec = new Vector3f(
                                Float.parseFloat(tokens[1]),
                                Float.parseFloat(tokens[2]),
                                Float.parseFloat(tokens[3])
                        );
                        vertices.add(verticesVec);
                        tangents.add(new Vector3f());
                        break;
                    case "vt":
                        //textures
                        Vector4f texturesVec = new Vector4f(
                                Float.parseFloat(tokens[1]),
                                Float.parseFloat(tokens[2]),
                                Float.parseFloat(tokens[1]),
                                Float.parseFloat(tokens[2])
                        );
                        textures.add(texturesVec);
                        break;
                    case "vn":
                        //normals
                        Vector3f normalsVec = new Vector3f(
                                Float.parseFloat(tokens[1]),
                                Float.parseFloat(tokens[2]),
                                Float.parseFloat(tokens[3])
                        );
                        normals.add(normalsVec);
                        break;
                    case "f":
                        //faces
                        f.add(tokens);
                        break;
                    default:
                        break;
                }
            }

            for(String[] tokens : f)
            {
                Vector3i face1 = processFace(tokens[1], faces);
                Vector3i face2 = processFace(tokens[2], faces);
                Vector3i face3 = processFace(tokens[3], faces);

                Vector3f deltaPos1 = new Vector3f(vertices.get(face2.x)).sub(vertices.get(face1.x), new Vector3f());
                Vector3f deltaPos2 = new Vector3f(vertices.get(face3.x)).sub(vertices.get(face1.x), new Vector3f());
                Vector4f uv0 = textures.get(face1.y);
                Vector4f uv1 = textures.get(face2.y);
                Vector4f uv2 = textures.get(face3.y);
                Vector4f deltaUv1 = new Vector4f(uv1).sub(uv0, new Vector4f());
                Vector4f deltaUv2 = new Vector4f(uv2).sub(uv0, new Vector4f());

                float r = 1.0f / (deltaUv1.x * deltaUv2.y - deltaUv1.y * deltaUv2.x);
                deltaPos1.mul(deltaUv2.y);
                deltaPos2.mul(deltaUv1.y);
                Vector3f tangent = new Vector3f(deltaPos1).sub(deltaPos2, new Vector3f());
                tangent.mul(r);
                tangents.get(face1.x).add(tangent);
                tangents.get(face2.x).add(tangent);
                tangents.get(face3.x).add(tangent);
            }

            ArrayList<Integer> indices = new ArrayList<>();
            float[] verticesArr = new float[vertices.size() * 3];
            int i = 0;

            for(Vector3f pos : vertices)
            {
                verticesArr[i * 3] = pos.x;
                verticesArr[i * 3 + 1] = pos.y;
                verticesArr[i * 3 + 2] = pos.z;
                i++;
            }

            float[] texCoordArr = new float[vertices.size() * 4];
            float[] normalsArr = new float[vertices.size() * 3];
            float[] tangentsArr = new float[vertices.size() * 3];

            for(Vector3i face : faces)
                processVertex(face.x, face.y, face.z, textures, normals, indices, tangents, texCoordArr, normalsArr, tangentsArr);

            int[] indicesArr = indices.stream().mapToInt((Integer v) -> v).toArray();

            int[] gmats = new int[verticesArr.length];
            for(int n = 0; n < gmats.length; n++)
                gmats[n] = -1;

            model.vertices = verticesArr;
            model.textureCoords = texCoordArr;
            model.normals = normalsArr;
            model.indices = indicesArr;
            model.tangents = tangentsArr;
            model.gmats = gmats;

            loadModel(
                    model,
                    verticesArr,
                    texCoordArr,
                    normalsArr,
                    indicesArr,
                    tangentsArr,
                    gmats);
        }catch (Exception e)
        {
            print.stackTrace(e);
        }

        return model;
    }

    public Model loadRMeshArr(RMesh mesh) throws Exception {
        Model model = new Model();
        modelLoader.digest(new AsyncModelMan.ModelDataRMesh(model, mesh));
        return model;
    }

    public ArrayList<Model> loadStaticMesh(RStaticMesh mesh, Thing thing) throws Exception {
        ArrayList<Model> models = new ArrayList<>();
        modelLoader.digest(new AsyncModelMan.ModelDataStaticMesh(models, mesh, thing));
        return models;
    }

    public Model generateMaterialMesh(Model model, PGeneratedMesh generatedMesh, PShape shape, RBevel bevel, Matrix4f transformation)
    {
        if(model == null)
            model = new Model();
        modelLoader.digest(new AsyncModelMan.ModelDataShape(model, generatedMesh, shape, bevel, transformation));
        return model;
    }

    public static void processVertex(int pos, int texCoord, int normal, ArrayList<Vector4f> texCoordList, ArrayList<Vector3f> normalList, ArrayList<Integer> indicesList, ArrayList<Vector3f> tangents, float[] texCoordArr, float[] normalArr, float[] tangentsArr)
    {
        indicesList.add(pos);

        if(texCoord >= 0)
        {
            Vector4f texCoordVec = texCoordList.get(texCoord);
            texCoordArr[pos * 4] = texCoordVec.x;
            texCoordArr[pos * 4 + 1] = 1 - texCoordVec.y;
            texCoordArr[pos * 4 + 2] = texCoordVec.z;
            texCoordArr[pos * 4 + 3] = 1 - texCoordVec.w;

            Vector3f tangent = tangents.get(pos).normalize();

            tangentsArr[pos * 3] = tangent.x;
            tangentsArr[pos * 3 + 1] = tangent.y;
            tangentsArr[pos * 3 + 2] = tangent.z;
        }

        if(normal >= 0)
        {
            Vector3f normalVec = normalList.get(normal);
            normalArr[pos * 3] = normalVec.x;
            normalArr[pos * 3 + 1] = normalVec.y;
            normalArr[pos * 3 + 2] = normalVec.z;
        }
    }

    public static Vector3i processFace(String token, ArrayList<Vector3i> faces)
    {
        String[] lineToken = token.split("/");
        int length = lineToken.length;
        int pos = -1, coords = -1, normal = -1;
        pos = Integer.parseInt(lineToken[0]) -1;

        if(length > 1)
        {
            String textCoord = lineToken[1];
            coords = textCoord.length() > 0 ? Integer.parseInt(textCoord) - 1 : -1;
            if(length > 2)
                normal = Integer.parseInt(lineToken[2]) - 1;
        }

        Vector3i facesVec = new Vector3i(pos, coords, normal);
        faces.add(facesVec);
        return facesVec;
    }

    public Model loadModel(float[] vertices, float[] textureCoords, float[] normals, int[] indices, float[] tangents)
    {
        int vao = createVAO();
        int[] vbos = new int[]{
        storeIndicesBuffer(indices),
        storeDataInAttribList(0, 3, vertices),
        storeDataInAttribList(1, 4, textureCoords),
        storeDataInAttribList(2, 3, normals),
        storeDataInAttribList(5, 3, tangents)};
        unbind();
        return new Model(vao, vbos, indices.length, indices, new int[]{0, 1, 2, 5});
    }

    public Model loadModel(float[] vertices, float[] textureCoords, float[] normals, int[] indices)
    {
        int vao = createVAO();
        int[] vbos = new int[]{
                storeIndicesBuffer(indices),
                storeDataInAttribList(0, 3, vertices),
                storeDataInAttribList(1, 4, textureCoords),
                storeDataInAttribList(2, 3, normals)};
        unbind();
        return new Model(vao, vbos, indices.length, indices, new int[]{0, 1, 2});
    }

    public Model loadModel(float[] vertices, float[] textureCoords, float[] normals, int[] indices, int[] joints, float[] weights, float[] tangents)
    {
        int vao = createVAO();
        int[] vbos = new int[]{
                storeIndicesBuffer(indices),
                storeDataInAttribList(0, 3, vertices),
                storeDataInAttribList(1, 4, textureCoords),
                storeDataInAttribList(2, 3, normals),
                storeDataInAttribList(3, 4, joints),
                storeDataInAttribList(4, 4, weights),
                storeDataInAttribList(5, 3, tangents)};
        unbind();
        return new Model(vao, vbos, indices.length, indices, new int[]{0, 1, 2, 3, 4, 5});
    }

    public void loadModel(Model model, float[] vertices, float[] textureCoords, float[] normals, int[] indices, int[] joints, float[] weights, float[] tangents, int[] gmats)
    {
        int vao = createVAO();
        int[] vbos = new int[]{
                storeIndicesBuffer(indices),
                storeDataInAttribList(0, 3, vertices),
                storeDataInAttribList(1, 4, textureCoords),
                storeDataInAttribList(2, 3, normals),
                storeDataInAttribList(3, 4, joints),
                storeDataInAttribList(4, 4, weights),
                storeDataInAttribList(5, 3, tangents),
                storeDataInAttribList(6, 1, gmats)};
        unbind();

        model.vao = vao;
        model.vbos = vbos;
        model.vertexCount = indices.length;
        model.indices = indices;
        model.attribs = new int[]{0, 1, 2, 3, 4, 5, 6};
    }

    public void loadModel(Model model, float[] vertices, float[] textureCoords, float[] normals, int[] indices, float[] tangents, int[] gmats)
    {
        int vao = createVAO();
        int[] vbos = new int[]{
                storeIndicesBuffer(indices),
                storeDataInAttribList(0, 3, vertices),
                storeDataInAttribList(1, 4, textureCoords),
                storeDataInAttribList(2, 3, normals),
                storeDataInAttribList(5, 3, tangents),
                storeDataInAttribList(6, 1, gmats)};
        unbind();

        model.vao = vao;
        model.vbos = vbos;
        model.indices = indices;
        model.vertexCount = indices.length;
        model.attribs = new int[]{0, 1, 2, 5, 6};
    }

    public void loadModel(Model model, float[] vertices, int[] indices)
    {
        int vao = createVAO();
        int[] vbos = new int[]{
                storeIndicesBuffer(indices),
                storeDataInAttribList(0, 3, vertices)};
        unbind();

        model.vao = vao;
        model.vbos = vbos;
        model.indices = indices;
        model.vertexCount = indices.length;
        model.attribs = new int[]{0};
    }

    public Model loadModel(float[] vertices)
    {
        int vao = createVAO();
        int[] vbos = new int[]{storeDataInAttribList(0, 2, vertices)};
        unbind();
        return new Model(vao, vbos, vertices.length/2, new int[]{0});
    }

    public Model loadModel(float[] vertices, float[] textureCoords)
    {
        int vao = createVAO();
        int[] vbos = new int[]{
                storeDataInAttribList(0, 2, vertices),
                storeDataInAttribList(1, 2, textureCoords)};
        unbind();
        return new Model(vao, vbos, vertices.length/2, new int[]{0, 1});
    }

    public Model loadModel(int[] verts, float[] textureCoords)
    {
        int vao = createVAO();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, verts[0]);
        GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        int[] vbos = new int[]{
                verts[0],
                storeDataInAttribList(1, 2, textureCoords)};
        unbind();
        return new Model(vao, vbos, verts[1]/2, new int[]{1});
    }

    public int loadTexture(String filename) throws Exception
    {
        int id = GL11.glGenTextures();
        textures.add(id);
        textureLoader.digest(new AsyncTextureMan.FilepathImageData(
                filename,
                GL11.GL_LINEAR_MIPMAP_LINEAR,
                GL11.GL_LINEAR,
                id));
        return id;
    }

    public int loadTextureFilePicker() throws Exception
    {
        File[] file = FileChooser.openFile(null, null, false, false);
        if(file.length < 1)
            return -1;

        int id = GL11.glGenTextures();
        textures.add(id);
        textureLoader.digest(new AsyncTextureMan.FilepathImageData(
                file[0].getPath(),
                GL11.GL_LINEAR_MIPMAP_LINEAR,
                GL11.GL_LINEAR,
                id));
        return id;
    }

    public Texture loadTexture(BufferedImage image, int minFilter, int magFilter) throws Exception
    {
        int id = GL11.glGenTextures();
        textures.add(id);
        textureLoader.digest(new AsyncTextureMan.BufferedImageData(
                image,
                minFilter,
                magFilter,
                id));
        return new Texture(id, this, image);
    }

    public Texture loadTexture2(BufferedImage image, int minFilter, int magFilter)
    {
        Texture texture = new Texture(image);
        textureLoader.digest(new AsyncTextureMan.BufferedImageData2(image, texture, minFilter, magFilter));
        return texture;
    }

    public Texture loadResourceTexture(String resourcePath, int minFilter, int magFilter) throws Exception
    {
        return loadTexture(ImageIO.read(Main.class.getResourceAsStream(resourcePath)), minFilter, magFilter);
    }

    public static ByteBuffer loadTextureBuffer(BufferedImage image) throws Exception
    {
        if(image == null)
            return null;

        ByteBuffer buffer = ByteBuffer.allocateDirect(image.getWidth() * image.getHeight() * 4);

        int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

        for(int h = 0; h < image.getHeight(); h++) {
            for(int w = 0; w < image.getWidth(); w++) {
                int pixel = pixels[h * image.getWidth() + w];

                buffer.put((byte) ((pixel >> 16) & 0xFF));
                buffer.put((byte) ((pixel >> 8) & 0xFF));
                buffer.put((byte) (pixel & 0xFF));
                buffer.put((byte) ((pixel >> 24) & 0xFF));
            }
        }

        buffer.flip();

        return buffer;
    }

    public int createVAO()
    {
        int vao = GL30.glGenVertexArrays();
        vaos.add(vao);
        GL30.glBindVertexArray(vao);
        return vao;
    }

    public int storeIndicesBuffer(int[] indices)
    {
        int vbo = GL15.glGenBuffers();
        vbos.add(vbo);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vbo);
        IntBuffer buffer = Utils.storeDataInIntBuffer(indices);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        return vbo;
    }

    public int storeDataInAttribList(int attribNo, int vertexCount, float[] data)
    {
        if(data == null)
            return -1;

        int vbo = GL15.glGenBuffers();
        vbos.add(vbo);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        FloatBuffer buffer = Utils.storeDataInFloatBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attribNo, vertexCount, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        return vbo;
    }

    public int storeDataInAttribList(int attribNo, int vertexCount, int[] data)
    {
        if(data == null)
            return -1;

        int vbo = GL15.glGenBuffers();
        vbos.add(vbo);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        IntBuffer buffer = Utils.storeDataInIntBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL30.glVertexAttribIPointer(attribNo, vertexCount, GL11.GL_INT, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        return vbo;
    }

    public void unbind()
    {
        GL30.glBindVertexArray(0);
    }

    public void cleanup()
    {
        for(int vao: vaos)
            GL30.glDeleteVertexArrays(vao);
        for(int vbo: vbos)
            GL30.glDeleteBuffers(vbo);
        for(int texture: textures)
            GL11.glDeleteTextures(texture);
    }

}
