package bog.bgmaker.view3d;

import bog.bgmaker.Main;
import bog.bgmaker.view3d.core.*;
import bog.bgmaker.view3d.mainWindow.LoadedData;
import bog.bgmaker.view3d.utils.CWLibUtils.SkeletonUtils;
import bog.bgmaker.view3d.utils.Utils;
import cwlib.enums.BoxType;
import cwlib.resources.RGfxMaterial;
import cwlib.resources.RMesh;
import cwlib.structs.gmat.MaterialBox;
import cwlib.structs.gmat.MaterialWire;
import cwlib.structs.mesh.Primitive;
import cwlib.types.data.ResourceDescriptor;
import org.joml.*;
import org.lwjgl.opengl.*;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Bog
 */
public class ObjectLoader {

    public ArrayList<Integer> vaos = new ArrayList<>();
    public ArrayList<Integer> vbos = new ArrayList<>();
    public ArrayList<Integer> textures = new ArrayList<>();

    public Model loadOBJModel(String fileName)
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

            Vector3f delatPos1 = new Vector3f(vertices.get(face2.x)).sub(vertices.get(face1.x), new Vector3f());
            Vector3f delatPos2 = new Vector3f(vertices.get(face3.x)).sub(vertices.get(face1.x), new Vector3f());
            Vector4f uv0 = textures.get(face1.y);
            Vector4f uv1 = textures.get(face2.y);
            Vector4f uv2 = textures.get(face3.y);
            Vector4f deltaUv1 = new Vector4f(uv1).sub(uv0, new Vector4f());
            Vector4f deltaUv2 = new Vector4f(uv2).sub(uv0, new Vector4f());

            float r = 1.0f / (deltaUv1.x * deltaUv2.y - deltaUv1.y * deltaUv2.x);
            delatPos1.mul(deltaUv2.y);
            delatPos2.mul(deltaUv1.y);
            Vector3f tangent = new Vector3f(delatPos1).sub(delatPos2, new Vector3f());
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

        return loadModel(verticesArr, texCoordArr, normalsArr, indicesArr, tangentsArr);
    }

    public Model loadRMesh(RMesh mesh) {
        try {
            ArrayList<Vector3f> vertices = new ArrayList<>();
            ArrayList<Vector3f> normals = new ArrayList<>();
            ArrayList<Vector4f> textures = new ArrayList<>();
            ArrayList<Vector3i> faces = new ArrayList<>();
            ArrayList<Vector3f> tangents = new ArrayList();
            for (Vector3f vertex : mesh.getVertices()) {
                Vector3f verticesVec = new Vector3f(vertex.x, vertex.y, vertex.z);
                vertices.add(verticesVec);
                tangents.add(new Vector3f());
            }
            for (Vector3f normal : mesh.getNormals()) {
                Vector3f normalsVec = new Vector3f(normal.x, normal.y, normal.z);
                normals.add(normalsVec);
            }
            for (Vector2f texture : mesh.getUVs(0)) {
                Vector4f texturesVec = new Vector4f(texture.x, 1.0F - texture.y, texture.x, 1.0F - texture.y);
                textures.add(texturesVec);
            }
            int[] indices1 = mesh.getTriangles();
            for (int i = 0; i < indices1.length; i++)
                indices1[i] = indices1[i] + 1;
            for (int i = 0; i < indices1.length; i += 3) {
                Vector3i face1 = processFace("" + indices1[i] + "/" + indices1[i] + "/" + indices1[i], faces);
                Vector3i face2 = processFace("" + indices1[i + 1] + "/" + indices1[i + 1] + "/" + indices1[i + 1], faces);
                Vector3i face3 = processFace("" + indices1[i + 2] + "/" + indices1[i + 2] + "/" + indices1[i + 2], faces);

                Vector3f delatPos1 = new Vector3f(vertices.get(face2.x)).sub(vertices.get(face1.x), new Vector3f());
                Vector3f delatPos2 = new Vector3f(vertices.get(face3.x)).sub(vertices.get(face1.x), new Vector3f());
                Vector4f uv0 = textures.get(face1.y);
                Vector4f uv1 = textures.get(face2.y);
                Vector4f uv2 = textures.get(face3.y);
                Vector4f deltaUv1 = new Vector4f(uv1).sub(uv0, new Vector4f());
                Vector4f deltaUv2 = new Vector4f(uv2).sub(uv0, new Vector4f());

                float r = 1.0f / (deltaUv1.x * deltaUv2.y - deltaUv1.y * deltaUv2.x);
                delatPos1.mul(deltaUv2.y);
                delatPos2.mul(deltaUv1.y);
                Vector3f tangent = new Vector3f(delatPos1).sub(delatPos2, new Vector3f());
                tangent.mul(r);
                tangents.get(face1.x).add(tangent);
                tangents.get(face2.x).add(tangent);
                tangents.get(face3.x).add(tangent);
            }
            ArrayList<Integer> indices = new ArrayList<>();
            float[] verticesArr = new float[vertices.size() * 3];
            int j = 0;
            for (Vector3f posV : vertices) {
                verticesArr[j * 3] = posV.x;
                verticesArr[j * 3 + 1] = posV.y;
                verticesArr[j * 3 + 2] = posV.z;
                j++;
            }
            float[] texCoordArr = new float[vertices.size() * 4];
            float[] normalsArr = new float[vertices.size() * 3];
            float[] tangentsArr = new float[vertices.size() * 3];
            for (Vector3i face : faces)
                processVertex(face.x, face.y, face.z, textures, normals, indices, tangents, texCoordArr, normalsArr, tangentsArr);
            int[] indicesArr = indices.stream().mapToInt(v -> v.intValue()).toArray();

            byte[][] js = mesh.getJoints();
            int[] joints = new int[js.length * 4];

            for(int i = 0; i < js.length; i++)
            {
                joints[i * 4] = js[i][0];
                joints[i * 4 + 1] = js[i][1];
                joints[i * 4 + 2] = js[i][2];
                joints[i * 4 + 3] = js[i][3];
            }

            Vector4f[] ws = mesh.getWeights();
            float[] weights = new float[ws.length * 4];

            for(int i = 0; i < ws.length; i++)
            {
                weights[i * 4] = ws[i].x;
                weights[i * 4 + 1] = ws[i].y;
                weights[i * 4 + 2] = ws[i].z;
                weights[i * 4 + 3] = ws[i].w;
            }

            return loadModel(verticesArr, texCoordArr, normalsArr, indicesArr, joints, weights, tangentsArr);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<Model> loadRMeshArr(RMesh mesh) throws Exception {
        ArrayList<Model> models = new ArrayList<>();
        for (Primitive[] primitives : mesh.getSubmeshes())
            for (Primitive primitiveSubmesh : primitives) {
                Model model = loadSubmesh(mesh, primitiveSubmesh);
                ResourceDescriptor mat = primitiveSubmesh.getMaterial();
                model.material = LoadedData.getMaterial(mat, this);
                models.add(model);
            }
        return models;
    }

    public Model loadSubmesh(RMesh mastermesh, Primitive submesh) {
            ArrayList<Vector3f> vertices = new ArrayList<>();
            ArrayList<Vector3f> normals = new ArrayList<>();
            ArrayList<Vector4f> textures = new ArrayList<>();
            ArrayList<Vector3i> faces = new ArrayList<>();
            ArrayList<Vector3f> tangents = new ArrayList();

            for (Vector3f vertex : mastermesh.getVertices()) {
                Vector3f verticesVec = new Vector3f(vertex.x, vertex.y, vertex.z);
                vertices.add(verticesVec);
                tangents.add(new Vector3f());
            }
            for (Vector3f normal : mastermesh.getNormals()) {
                Vector3f normalsVec = new Vector3f(normal.x, normal.y, normal.z);
                normals.add(normalsVec);
            }

            boolean has2UVs = false;

            RGfxMaterial material = LoadedData.loadGfxMaterial(submesh.getMaterial());
            if (material != null)
            {
                int outputBox = material.getOutputBox();
                for (int k = 0; k < material.boxes.size(); k++) {
                    MaterialBox box = material.boxes.get(k);
                    MaterialWire wire = material.findWireFrom(k);
                    try
                    {
                        if (box.type == 1) {
                            while (wire.boxTo != outputBox)
                                wire = material.findWireFrom(wire.boxTo);
                            if (wire.portTo == 0)
                                if(box.getParameters()[4] == 1)
                                    has2UVs = true;
                        }
                    }catch (Exception e){e.printStackTrace();}
                }
            }

            Vector2f[] UV0 = mastermesh.getUVs(0);
            Vector2f[] UV1 = has2UVs ? mastermesh.getUVs(1) : UV0;

            for (int i = 0; i < UV0.length; i++) {
                Vector4f texturesVec = new Vector4f(UV0[i].x, 1.0F - UV0[i].y, UV1[i].x, 1.0F - UV1[i].y);
                textures.add(texturesVec);
            }

            int[] indices1 = mastermesh.getTriangles(submesh);

            for (int i = 0; i < indices1.length; i += 3)
            {
                faces.add(new Vector3i(indices1[i], indices1[i], indices1[i]));
                faces.add(new Vector3i(indices1[i + 1], indices1[i + 1], indices1[i + 1]));
                faces.add(new Vector3i(indices1[i + 2], indices1[i + 2], indices1[i + 2]));

                Vector3f delatPos1 = new Vector3f(vertices.get(indices1[i + 1])).sub(vertices.get(indices1[i]), new Vector3f());
                Vector3f delatPos2 = new Vector3f(vertices.get(indices1[i + 2])).sub(vertices.get(indices1[i]), new Vector3f());
                Vector4f uv0 = textures.get(indices1[i]);
                Vector4f uv1 = textures.get(indices1[i + 1]);
                Vector4f uv2 = textures.get(indices1[i + 2]);
                Vector4f deltaUv1 = new Vector4f(uv1).sub(uv0, new Vector4f());
                Vector4f deltaUv2 = new Vector4f(uv2).sub(uv0, new Vector4f());

                float r = 1.0f / (deltaUv1.x * deltaUv2.y - deltaUv1.y * deltaUv2.x);
                delatPos1.mul(deltaUv2.y);
                delatPos2.mul(deltaUv1.y);
                Vector3f tangent = new Vector3f(delatPos1).sub(delatPos2, new Vector3f());
                tangent.mul(r);
                tangents.get(indices1[i]).add(tangent);
                tangents.get(indices1[i + 1]).add(tangent);
                tangents.get(indices1[i + 2]).add(tangent);
            }

            ArrayList<Integer> indices = new ArrayList<>();
            float[] verticesArr = new float[vertices.size() * 3];
            int j = 0;
            for (Vector3f posV : vertices) {
                verticesArr[j * 3] = posV.x;
                verticesArr[j * 3 + 1] = posV.y;
                verticesArr[j * 3 + 2] = posV.z;
                j++;
            }
            float[] texCoordArr = new float[vertices.size() * 4];
            float[] normalsArr = new float[vertices.size() * 3];
            float[] tangentsArr = new float[vertices.size() * 3];

            for (Vector3i face : faces)
                processVertex(face.x, face.y, face.z, textures, normals, indices, tangents, texCoordArr, normalsArr, tangentsArr);
            int[] indicesArr = indices.stream().mapToInt(v -> v.intValue()).toArray();

            byte[][] js = mastermesh.getJoints();
            int[] joints = new int[js.length * 4];

            for(int i = 0; i < js.length; i++)
            {
                joints[i * 4] = js[i][0];
                joints[i * 4 + 1] = js[i][1];
                joints[i * 4 + 2] = js[i][2];
                joints[i * 4 + 3] = js[i][3];
            }

            Vector4f[] ws = mastermesh.getWeights();
            float[] weights = new float[ws.length * 4];

            for(int i = 0; i < ws.length; i++)
            {
                weights[i * 4] = ws[i].x;
                weights[i * 4 + 1] = ws[i].y;
                weights[i * 4 + 2] = ws[i].z;
                weights[i * 4 + 3] = ws[i].w;
            }

            return loadModel(verticesArr, texCoordArr, normalsArr, indicesArr, joints, weights, tangentsArr);

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
        return new Model(vao, vbos, indices.length, indices);
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
        return new Model(vao, vbos, indices.length, indices);
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
        return new Model(vao, vbos, indices.length, indices);
    }

    public Model loadModel(float[] vertices)
    {
        int vao = createVAO();
        int[] vbos = new int[]{storeDataInAttribList(0, 2, vertices)};
        unbind();
        return new Model(vao, vbos, vertices.length/2);
    }

    public Model loadModel(float[] vertices, float[] textureCoords)
    {
        int vao = createVAO();
        int[] vbos = new int[]{
                storeDataInAttribList(0, 2, vertices),
                storeDataInAttribList(1, 2, textureCoords)};
        unbind();
        return new Model(vao, vbos, vertices.length/2);
    }

    public int loadTexture(String filename) throws Exception
    {
        int width, height;
        ByteBuffer buffer;

        try(MemoryStack stack = MemoryStack.stackPush())
        {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer c = stack.mallocInt(1);

            buffer = STBImage.stbi_load(filename, w, h, c, 4);

            if(buffer == null)
                throw new Exception("Image file " + filename + " not loaded.\n" + STBImage.stbi_failure_reason());

            width = w.get();
            height = h.get();
        }

        int id = GL11.glGenTextures();
        textures.add(id);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
        STBImage.stbi_image_free(buffer);

        return id;
    }

    public int loadTexture(BufferedImage image, int minFilter, int magFilter) throws Exception
    {
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

        int id = GL11.glGenTextures();
        textures.add(id);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, minFilter);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, magFilter);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, image.getWidth(), image.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);

        return id;
    }

    public static ByteBuffer loadTextureBuffer(BufferedImage image) throws Exception
    {
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

    public int loadResourceTexture(String resourcePath, int minFilter, int magFilter) throws Exception
    {
        return loadTexture(ImageIO.read(Main.class.getResourceAsStream(resourcePath)), minFilter, magFilter);
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
