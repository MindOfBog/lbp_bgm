package bog.bgmaker.view3d;

import bog.bgmaker.Main;
import bog.bgmaker.view3d.core.AABB;
import bog.bgmaker.view3d.core.Model;
import bog.bgmaker.view3d.core.Triangle;
import bog.bgmaker.view3d.utils.Utils;
import cwlib.enums.BoxType;
import cwlib.resources.RGfxMaterial;
import cwlib.resources.RMesh;
import cwlib.structs.gmat.MaterialBox;
import cwlib.structs.gmat.MaterialWire;
import cwlib.structs.mesh.Primitive;
import cwlib.types.data.ResourceDescriptor;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Bog
 */
public class ObjectLoader {

    public static HashMap<ResourceDescriptor, ArrayList<Model>> loadedModels;

    public ArrayList<Integer> vaos = new ArrayList<>();
    public ArrayList<Integer> vbos = new ArrayList<>();
    public ArrayList<Integer> textures = new ArrayList<>();

    public Model loadOBJModel(String fileName)
    {
        ArrayList<String> lines = Utils.readAllLines(fileName);
        ArrayList<Vector3f> vertices = new ArrayList<>();
        ArrayList<Vector3f> normals = new ArrayList<>();
        ArrayList<Vector2f> textures = new ArrayList<>();
        ArrayList<Vector3i> faces = new ArrayList<>();

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
                    break;
                case "vt":
                    //textures
                    Vector2f texturesVec = new Vector2f(
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
                    processFace(tokens[1], faces);
                    processFace(tokens[2], faces);
                    processFace(tokens[3], faces);
                    break;
                default:
                    break;
            }
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

        float[] texCoordArr = new float[vertices.size() * 2];
        float[] normalsArr = new float[vertices.size() * 3];

        for(Vector3i face : faces)
        {
            processVertex(face.x, face.y, face.z, textures, normals, indices, texCoordArr, normalsArr);
        }

        int[] indicesArr = indices.stream().mapToInt((Integer v) -> v).toArray();

        return loadModel(verticesArr, texCoordArr, normalsArr, indicesArr, vertices);
    }
    public Model loadRMesh(RMesh mesh, Matrix4f transMat, Vector3f pos) {
        try {
            ArrayList<Vector3f> vertices = new ArrayList<>();
            ArrayList<Vector3f> normals = new ArrayList<>();
            ArrayList<Vector2f> textures = new ArrayList<>();
            ArrayList<Vector3i> faces = new ArrayList<>();
            for (Vector3f vertex : mesh.getVertices()) {
                Vector3f verticesVec = new Vector3f(vertex.x, vertex.y, vertex.z);
                vertices.add(verticesVec);
            }
            for (Vector3f normal : mesh.getNormals()) {
                Vector3f normalsVec = new Vector3f(normal.x, normal.y, normal.z);
                normals.add(normalsVec);
            }
            for (Vector2f texture : mesh.getUVs(0)) {
                Vector2f texturesVec = new Vector2f(texture.x, 1.0F - texture.y);
                textures.add(texturesVec);
            }
            int[] indices1 = mesh.getTriangles();
            for (int i = 0; i < indices1.length; i++)
                indices1[i] = indices1[i] + 1;
            for (int i = 0; i < indices1.length; i += 3) {
                processFace("" + indices1[i] + "/" + indices1[i] + "/" + indices1[i], faces);
                processFace("" + indices1[i + 1] + "/" + indices1[i + 1] + "/" + indices1[i + 1], faces);
                processFace("" + indices1[i + 2] + "/" + indices1[i + 2] + "/" + indices1[i + 2], faces);
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
            float[] texCoordArr = new float[vertices.size() * 2];
            float[] normalsArr = new float[vertices.size() * 3];
            for (Vector3i face : faces)
                processVertex(face.x, face.y, face.z, textures, normals, indices, texCoordArr, normalsArr);
            int[] indicesArr = indices.stream().mapToInt(v -> v.intValue()).toArray();
            return loadModel(verticesArr, texCoordArr, normalsArr, indicesArr, vertices);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Model loadSubmesh(RMesh mastermesh, Primitive submesh, Matrix4f transMat, Vector3f pos) {
        try {
            ArrayList<Vector3f> vertices = new ArrayList<>();
            ArrayList<Vector3f> normals = new ArrayList<>();
            ArrayList<Vector2f> textures = new ArrayList<>();
            ArrayList<Vector3i> faces = new ArrayList<>();
            for (Vector3f vertex : mastermesh.getVertices()) {
                Vector3f verticesVec = new Vector3f(vertex.x, vertex.y, vertex.z);
                vertices.add(verticesVec);
            }
            for (Vector3f normal : mastermesh.getNormals()) {
                Vector3f normalsVec = new Vector3f(normal.x, normal.y, normal.z);
                normals.add(normalsVec);
            }

            int channel = 0;
            RGfxMaterial material = Main.view.loadGfxMaterial(submesh.getMaterial());

            if(material != null)
                for (int i = 0; i < material.boxes.size(); ++i)
                {
                    MaterialBox box = material.boxes.get(i);
                    MaterialWire wire = material.findWireFrom(i);
                    int outputBox = material.getOutputBox();

                    if (box.type == BoxType.TEXTURE_SAMPLE)
                    {
                        while (wire.boxTo != outputBox)
                            wire = material.findWireFrom(wire.boxTo);

                        if(wire.portTo == 0)
                        {
                            channel = box.getParameters()[4];
                            break;
                        }
                    }
                }

            for (Vector2f texture : mastermesh.getUVs(channel)) {
                Vector2f texturesVec = new Vector2f(texture.x, 1.0F - texture.y);
                textures.add(texturesVec);
            }
            int[] indices1 = mastermesh.getTriangles(submesh);

            for (int i = 0; i < indices1.length; i += 3)
            {
                faces.add(new Vector3i(indices1[i], indices1[i], indices1[i]));
                faces.add(new Vector3i(indices1[i + 1], indices1[i + 1], indices1[i + 1]));
                faces.add(new Vector3i(indices1[i + 2], indices1[i + 2], indices1[i + 2]));
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
            float[] texCoordArr = new float[vertices.size() * 2];
            float[] normalsArr = new float[vertices.size() * 3];
            for (Vector3i face : faces)
                processVertex(face.x, face.y, face.z, textures, normals, indices, texCoordArr, normalsArr);
            int[] indicesArr = indices.stream().mapToInt(v -> v.intValue()).toArray();
            return loadModel(verticesArr, texCoordArr, normalsArr, indicesArr, vertices);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public AABB generateAABB(Model model, Matrix4f transMat, Vector3f pos)
    {
        AABB aabb = new AABB();

        ArrayList<Vector3f> vertices = model.vertices;

        for(Vector3f verticesVec : vertices)
        {
            Vector3f vertex = new Vector3f(verticesVec.mulProject(transMat, new Vector3f()));

            if(vertex.x < aabb.min.x)
                aabb.min.x = vertex.x;
            if(vertex.y < aabb.min.y)
                aabb.min.y = vertex.y;
            if(vertex.z < aabb.min.z)
                aabb.min.z = vertex.z;

            if(vertex.x > aabb.max.x)
                aabb.max.x = vertex.x;
            if(vertex.y > aabb.max.y)
                aabb.max.y = vertex.y;
            if(vertex.z > aabb.max.z)
                aabb.max.z = vertex.z;

            if(aabb.min.x == 0)
                aabb.min.x = pos.x;
            if(aabb.min.y == 0)
                aabb.min.y = pos.y;
            if(aabb.min.z == 0)
                aabb.min.z = pos.z;

            if(aabb.max.x == 0)
                aabb.max.x = pos.x;
            if(aabb.max.y == 0)
                aabb.max.y = pos.y;
            if(aabb.max.z == 0)
                aabb.max.z = pos.z;
        }

        return aabb;
    }
    public ArrayList<Triangle> generateTriangles(Model model, Matrix4f transMat)
    {
        try {
            ArrayList<Vector3f> vertices = model.vertices;
            ArrayList<Triangle> triangles = new ArrayList<>();

            int[] indices = model.indicesArr;

            for (int i = 0; i < indices.length; i += 3) {
                Vector3f p1 = new Vector3f(vertices.get(indices[i])).mulProject(transMat);
                Vector3f p2 = new Vector3f(vertices.get(indices[i + 1])).mulProject(transMat);
                Vector3f p3 = new Vector3f(vertices.get(indices[i + 2])).mulProject(transMat);

                triangles.add(new Triangle(p1, p2, p3));
            }

            return triangles;
        }catch (Exception e){e.printStackTrace();}
        return null;
    }

    public static void processVertex(int pos, int texCoord, int normal,
                                     ArrayList<Vector2f> texCoordList, ArrayList<Vector3f> normalList, ArrayList<Integer> indicesList,
                                     float[] texCoordArr, float[] normalArr)
    {
        indicesList.add(pos);

        if(texCoord >= 0)
        {
            Vector2f texCoordVec = texCoordList.get(texCoord);
            texCoordArr[pos * 2] = texCoordVec.x;
            texCoordArr[pos * 2 + 1] = 1 - texCoordVec.y;
        }

        if(normal >= 0)
        {
            Vector3f normalVec = normalList.get(normal);
            normalArr[pos * 3] = normalVec.x;
            normalArr[pos * 3 + 1] = normalVec.y;
            normalArr[pos * 3 + 2] = normalVec.z;
        }
    }

    public static void processFace(String token, ArrayList<Vector3i> faces)
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
    }

    public Model loadModel(float[] vertices, float[] textureCoords, float[] normals, int[] indices, ArrayList<Vector3f> vertices1)
    {
        int vao = createVAO();
        int[] vbos = new int[]{
        storeIndicesBuffer(indices),
        storeDataInAttribList(0, 3, vertices),
        storeDataInAttribList(1, 2, textureCoords),
        storeDataInAttribList(2, 3, normals)};
        unbind();
        return new Model(vao, vbos, indices.length, vertices1, indices);
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
