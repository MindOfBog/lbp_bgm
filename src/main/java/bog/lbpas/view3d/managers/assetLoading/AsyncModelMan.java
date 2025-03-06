package bog.lbpas.view3d.managers.assetLoading;

import bog.lbpas.view3d.core.Model;
import bog.lbpas.view3d.core.Texture;
import bog.lbpas.view3d.mainWindow.ConstantTextures;
import bog.lbpas.view3d.mainWindow.LoadedData;
import bog.lbpas.view3d.utils.Utils;
import bog.lbpas.view3d.utils.print;
import cwlib.resources.RBevel;
import cwlib.resources.RGfxMaterial;
import cwlib.resources.RMesh;
import cwlib.structs.bevel.BevelVertex;
import cwlib.structs.gmat.MaterialBox;
import cwlib.structs.mesh.Primitive;
import cwlib.structs.things.components.shapes.Polygon;
import cwlib.structs.things.parts.PShape;
import cwlib.types.data.ResourceDescriptor;
import io.github.earcut4j.Earcut;
import org.joml.*;

import java.awt.*;
import java.lang.Math;
import java.util.ArrayList;
import java.util.List;

public class AsyncModelMan {

    private ArrayList<Model> toLoad;
    private ArrayList<ModelDataOBJ> toDigestOBJ;
    private ArrayList<ModelDataRMesh> toDigestRMesh;
    private ArrayList<ModelDataShape> toDigestShape;
    private ObjectLoader loader;

    public int totalDigestionCount = 0;

    public AsyncModelMan(ObjectLoader loader) {
        toDigestOBJ = new ArrayList<>();
        toDigestRMesh = new ArrayList<>();
        toDigestShape = new ArrayList<>();
        toLoad = new ArrayList<>();
        this.loader = loader;
    }

    public int digestionCount()
    {
        return toDigestShape.size() + toDigestRMesh.size() + toDigestOBJ.size();
    }

    public int loadingCount()
    {
        return toLoad.size();
    }

    public boolean isLoadingSomething()
    {
        return digestionCount() != 0 || loadingCount() != 0;
    }

    public void loadDigestedMeshes()
    {
        for(int i = toLoad.size() - 1; i >= 0; i--)
        {
            if(toLoad.get(i) == null)
            {
                toLoad.remove(i);
                continue;
            }

            toLoad.get(i).cleanup(loader);

            if(toLoad.get(i).hasBones)
                loader.loadModel(
                        toLoad.get(i),
                        toLoad.get(i).vertices,
                        toLoad.get(i).textureCoords,
                        toLoad.get(i).normals,
                        toLoad.get(i).indices,
                        toLoad.get(i).joints,
                        toLoad.get(i).weights,
                        toLoad.get(i).tangents,
                        toLoad.get(i).gmats);
            else
                loader.loadModel(
                        toLoad.get(i),
                        toLoad.get(i).vertices,
                        toLoad.get(i).textureCoords,
                        toLoad.get(i).normals,
                        toLoad.get(i).indices,
                        toLoad.get(i).tangents,
                        toLoad.get(i).gmats);

            toLoad.remove(i);
        }
    }

    public void digestMeshes()
    {
        digestionOBJ();
        digestionRMesh();

        for(int i = toDigestShape.size() - 1; i >= 0; i--)
        {
            try
            {
                ModelDataShape shapeData = toDigestShape.get(i);

                extrudeShape(
                        shapeData.model,
                        shapeData.parentGmat,
                        shapeData.shape,
                        shapeData.bevel,
                        shapeData.transformation,
                        loader);

                toLoad.add(shapeData.model);
                toDigestShape.remove(i);
                totalDigestionCount++;
            }catch (Exception e){

                toDigestShape.remove(i);
                totalDigestionCount++;

                print.stackTrace(e);
            }
        }
    }

    private void digestionOBJ()
    {
        for(int p = toDigestOBJ.size() - 1; p >= 0; p--)
        {
            try
            {
                String fileName = toDigestOBJ.get(p).filePath;

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
                    Vector3i face1 = loader.processFace(tokens[1], faces);
                    Vector3i face2 = loader.processFace(tokens[2], faces);
                    Vector3i face3 = loader.processFace(tokens[3], faces);

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
                    loader.processVertex(face.x, face.y, face.z, textures, normals, indices, tangents, texCoordArr, normalsArr, tangentsArr);

                int[] indicesArr = indices.stream().mapToInt((Integer v) -> v).toArray();

                int[] gmats = new int[verticesArr.length];
                for(int n = 0; n < gmats.length; n++)
                    gmats[n] = -1;

                toDigestOBJ.get(p).model.vertices = verticesArr;
                toDigestOBJ.get(p).model.textureCoords = texCoordArr;
                toDigestOBJ.get(p).model.normals = normalsArr;
                toDigestOBJ.get(p).model.indices = indicesArr;
                toDigestOBJ.get(p).model.tangents = tangentsArr;
                toDigestOBJ.get(p).model.gmats = gmats;

                toLoad.add(toDigestOBJ.get(p).model);

                toDigestOBJ.remove(p);
                totalDigestionCount++;
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private void digestionRMesh()
    {
        for(int p = toDigestRMesh.size() - 1; p >= 0; p--)
        {
            try
            {
                RMesh mesh = toDigestRMesh.get(p).mesh;

                if(mesh == null)
                {
                    toDigestRMesh.remove(p);
                    totalDigestionCount++;
                    continue;
                }

                Vector3f[] meshVertices = mesh.getVertices();
                Vector3f[] meshNormals = mesh.getNormals();

                float[] verticesArr = new float[meshVertices.length * 3];
                float[] normalsArr = new float[meshNormals.length * 3];
                float[] texCoordArr = new float[meshVertices.length * 4];
                float[] tangentsArr = new float[meshVertices.length * 3];
                int[] materialArray = new int[meshVertices.length];
                Texture[] textures = new Texture[32];

                for (int i = 0; i < meshVertices.length; i++) {
                    verticesArr[i * 3] = meshVertices[i].x;
                    verticesArr[i * 3 + 1] = meshVertices[i].y;
                    verticesArr[i * 3 + 2] = meshVertices[i].z;
                }

                for (int i = 0; i < meshNormals.length; i++) {
                    normalsArr[i * 3] = meshNormals[i].x;
                    normalsArr[i * 3 + 1] = meshNormals[i].y;
                    normalsArr[i * 3 + 2] = meshNormals[i].z;
                }

                Vector2i[] gmatMAP = new Vector2i[100];
                for(int i = 0; i < gmatMAP.length; i++)
                    gmatMAP[i] = new Vector2i(-1);

                Primitive[][] meshSubmeshes = mesh.getSubmeshes();
                int attributeCount = mesh.getAttributeCount();
                for (Primitive[] primitives : meshSubmeshes)
                    for (Primitive submesh : primitives) {

                        ResourceDescriptor mat = submesh.getMaterial();

                        if(mat == null)
                            continue;

                        int material = LoadedData.getMaterial(mat, loader, textures, gmatMAP);
                        RGfxMaterial gmat = LoadedData.loadGfxMaterial(mat);

                        if(gmat == null)
                            continue;

                        int output = gmat.getOutputBox();
                        MaterialBox box = gmat.getBoxConnectedToPort(output, 0);
                        Vector2f[] UV0 = mesh.getUVs(0);
                        Vector2f[] UV1 = mesh.getUVs(attributeCount >= 2 ? 1 : 0);

                        int[] indices = mesh.getTriangles(submesh);

                        for (int ind : indices)
                        {
                            texCoordArr[ind * 4] = UV0[ind].x;
                            texCoordArr[ind * 4 + 1] = UV0[ind].y;
                            texCoordArr[ind * 4 + 2] = UV1[ind].x;
                            texCoordArr[ind * 4 + 3] = UV1[ind].y;

                            materialArray[ind] = material;
                        }

                        for (int i = 0; i < indices.length; i += 3) {
                            Vector3f v2 = new Vector3f(verticesArr[indices[i] * 3], verticesArr[indices[i] * 3 + 1], verticesArr[indices[i] * 3 + 2]);
                            Vector3f v1 = new Vector3f(verticesArr[indices[i + 1] * 3], verticesArr[indices[i + 1] * 3 + 1], verticesArr[indices[i + 1] * 3 + 2]);
                            Vector3f v3 = new Vector3f(verticesArr[indices[i + 2] * 3], verticesArr[indices[i + 2] * 3 + 1], verticesArr[indices[i + 2] * 3 + 2]);

                            Vector3f deltaPos1 = new Vector3f(v1).sub(v2, new Vector3f());
                            Vector3f deltaPos2 = new Vector3f(v3).sub(v2, new Vector3f());

                            Vector4f uv0 = new Vector4f(texCoordArr[indices[i] * 4], texCoordArr[indices[i] * 4 + 1], texCoordArr[indices[i] * 4 + 2], texCoordArr[indices[i] * 4 + 3]);
                            Vector4f uv1 = new Vector4f(texCoordArr[indices[i + 1] * 4], texCoordArr[indices[i + 1] * 4 + 1], texCoordArr[indices[i + 1] * 4 + 2], texCoordArr[indices[i + 1] * 4 + 3]);
                            Vector4f uv2 = new Vector4f(texCoordArr[indices[i + 2] * 4], texCoordArr[indices[i + 2] * 4 + 1], texCoordArr[indices[i + 2] * 4 + 2], texCoordArr[indices[i + 2] * 4 + 3]);

                            Vector4f deltaUv1 = new Vector4f(uv1).sub(uv0, new Vector4f());
                            Vector4f deltaUv2 = new Vector4f(uv2).sub(uv0, new Vector4f());

                            float r = 1.0f / (deltaUv1.x * deltaUv2.y - deltaUv1.y * deltaUv2.x);
                            deltaPos1.mul(deltaUv2.y);
                            deltaPos2.mul(deltaUv1.y);
                            Vector3f tangent = new Vector3f(deltaPos1).sub(deltaPos2, new Vector3f());
                            tangent.mul(r);

                            tangentsArr[indices[i] * 3] += tangent.x;
                            tangentsArr[indices[i] * 3 + 1] += tangent.y;
                            tangentsArr[indices[i] * 3 + 2] += tangent.z;

                            tangentsArr[indices[i + 1] * 3] += tangent.x;
                            tangentsArr[indices[i + 1] * 3 + 1] += tangent.y;
                            tangentsArr[indices[i + 1] * 3 + 2] += tangent.z;

                            tangentsArr[indices[i + 2] * 3] += tangent.x;
                            tangentsArr[indices[i + 2] * 3 + 1] += tangent.y;
                            tangentsArr[indices[i + 2] * 3 + 2] += tangent.z;
                        }
                    }

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

                int texCount = 0;
                for(Texture t : textures)
                    if(t != null)
                        texCount++;

                int gmatCount = 0;
                for(Vector2i v : gmatMAP)
                    if(v.x != -1)
                        gmatCount++;

                Model model = toDigestRMesh.get(p).model;

                int[] tris = mesh.getTriangles();
                ArrayList<Integer> indices = new ArrayList<>();

                for(int i = 0; i < tris.length / 3; i++)
                {
                    int v1 = tris[i * 3];
                    int v2 = tris[i * 3 + 1];
                    int v3 = tris[i * 3 + 2];

                    if(v1 < 65535 && v2 < 65535 && v3 < 65535)
                    {
                        indices.add(v1);
                        indices.add(v2);
                        indices.add(v3);
                    }
                }

                model.vertices = verticesArr;
                model.textureCoords = texCoordArr;
                model.normals = normalsArr;
                model.indices = indices.stream().mapToInt((Integer v) -> v).toArray();
                model.joints = joints;
                model.weights = weights;
                model.tangents = tangentsArr;
                model.gmats = materialArray;
                model.hasBones = weights.length > 0;
                model.mesh = mesh;
                model.material.textures = textures;
                model.material.texCount = texCount;
                model.material.gmatMAP = gmatMAP;
                model.material.gmatCount = gmatCount;

                toLoad.add(model);

                toDigestRMesh.remove(p);
                totalDigestionCount++;
            }catch (Exception e)
            {
                toDigestRMesh.remove(p);
                totalDigestionCount++;
                e.printStackTrace();
            }
        }
    }

    public static void extrudeShape(Model model, ResourceDescriptor parentGmat, PShape shape, RBevel bevel, Matrix4f transformation, ObjectLoader loader) {

        Polygon polygon = shape.polygon;
        float thickness = shape.thickness;
        int[] loops = polygon.loops;
        Vector3f[] polygonVertices = polygon.vertices;

        ensureWindingOrder(polygonVertices, loops, transformation);

        BevelVertex offset = bevel.vertices.get(bevel.vertices.size() - 1);
        float bevelSize = bevel.fixedBevelSize;
        if(bevelSize == -1)
            bevelSize = shape.bevelSize;

        float offZ = offset.z * bevelSize;

        int material = 0;

        Vector2i[] gmatMAP = new Vector2i[100];
        for(int i = 0; i < gmatMAP.length; i++)
            gmatMAP[i] = new Vector2i(-1);

        Texture[] textures = new Texture[32];

        try
        {
            if(parentGmat == null)
            {
                ResourceDescriptor gmat = bevel.getMaterial(bevel.vertices.get(0).gmatSlot);
                material =  LoadedData.getMaterial(gmat, loader, textures, gmatMAP);
            }
            else material = LoadedData.getMaterial(parentGmat, loader, textures, gmatMAP);
        }catch (Exception e){e.printStackTrace();}

        int count = (polygonVertices.length * bevel.vertices.size()) + (polygonVertices.length * (bevel.vertices.size() - 1));

        float[] vertices = new float[count * 3],
                texCoords = new float[count * 4],
                normals = new float[vertices.length],
                tangents = new float[vertices.length];
        int[] gmats = new int[count];

        Matrix4f invTransform = transformation.invert(new Matrix4f());
        float uvScale = 0.0038f;
        Vector3f scale = transformation.getScale(new Vector3f());

        //push in verts
        int l = 0;
        for (int loop : loops)
        {
            for (int i = 0; i < loop; i++)
            {
                Vector3f curVert = new Vector3f(polygonVertices[l]);
                curVert.mulProject(transformation, curVert);
                int p = l - 1;
                if (p < l - i)
                    p = l + (loop - 1 - i);
                Vector3f prevVert = new Vector3f(polygonVertices[p]);
                prevVert.mulProject(transformation, prevVert);
                int k = l - 2;
                if (k < l - i)
                    k = l + (loop - 2 - i);
                Vector3f prevPrevVert = new Vector3f(polygonVertices[k]);
                prevPrevVert.mulProject(transformation, prevPrevVert);
                int n = l + 1;
                if (n >= l + (loop - i))
                    n = l - i;
                Vector3f nextVert = new Vector3f(polygonVertices[n]);
                nextVert.mulProject(transformation, nextVert);
                int n2 = l + 2;
                if (n2 >= l + (loop - i))
                    n2 = l - i + 1;
                Vector3f nextNextVert = new Vector3f(polygonVertices[n2]);
                nextNextVert.mulProject(transformation, nextNextVert);

                Vector2f newPos = Utils.offsetAndFindIntersection(
                        new Vector2f(prevVert.x, prevVert.y),
                        new Vector2f(curVert.x, curVert.y),
                        new Vector2f(nextVert.x, nextVert.y), offset.y * bevelSize);

                Vector2f newPosPrev = Utils.offsetAndFindIntersection(
                        new Vector2f(prevPrevVert.x, prevPrevVert.y),
                        new Vector2f(prevVert.x, prevVert.y),
                        new Vector2f(curVert.x, curVert.y), offset.y * bevelSize);

                Vector2f newPosNext = Utils.offsetAndFindIntersection(
                        new Vector2f(curVert.x, curVert.y),
                        new Vector2f(nextVert.x, nextVert.y),
                        new Vector2f(nextNextVert.x, nextNextVert.y), offset.y * bevelSize);

                Vector2f dir = new Vector2f(prevVert.x, prevVert.y).sub(new Vector2f(curVert.x, curVert.y)).normalize();
                Vector2f dirOffset = new Vector2f(newPosPrev.x, newPosPrev.y).sub(new Vector2f(newPos.x, newPos.y)).normalize();

//                boolean bugged = dir.dot(dirOffset) <= -Math.cos(90);
//                float dist = (offset.y * bevelSize) * 0.9f;
//                int iteration = 0;
//                while(bugged && iteration < 100)
//                {
//                    newPos = Utils.offsetAndFindIntersection(
//                            new Vector2f(prevVert.x, prevVert.y),
//                            new Vector2f(curVert.x, curVert.y),
//                            new Vector2f(nextVert.x, nextVert.y), dist);
//                    print.neutral(dist + " : " + iteration);
//                    dist *= 0.9f;
//                    iteration++;
//
//                    dirOffset = new Vector2f(newPosPrev.x, newPosPrev.y).sub(new Vector2f(newPos.x, newPos.y)).normalize();
//                    bugged = dir.dot(dirOffset) <= -Math.cos(90);
//
//                    if(dist > -0.01f || iteration >= 100)
//                    {
//                        newPos = new Vector2f(curVert.x, curVert.y);
//                        bugged = false;
//                    }
//                }

                Vector3f newVertex = new Vector3f(newPos.x, newPos.y, transformation.getTranslation(new Vector3f()).z + thickness).mulProject(invTransform);

                vertices[l * 3] = newVertex.x;
                vertices[l * 3 + 1] = newVertex.y;
                vertices[l * 3 + 2] = newVertex.z + (shape.zBias * 2);

                texCoords[l * 4] = newVertex.x * uvScale * scale.x;
                texCoords[l * 4 + 1] = newVertex.y * uvScale * scale.y;
                texCoords[l * 4 + 2] = newVertex.x * uvScale * scale.x;
                texCoords[l * 4 + 3] = newVertex.y * uvScale * scale.y;

                normals[l * 3] = 0.0f;
                normals[l * 3 + 1] = 0.0f;
                normals[l * 3 + 2] = 1.0f;

                gmats[l] = material;
                l++;
            }
        }

        //temporary bandage for buggy corners
//        for(int asd = 0; asd < 1; asd++){
//        int lo = 0;
//        for(int loop : loops)
//        {
//            for(int i = 0; i < loop; i++)
//            {
//                int p = lo + i;
//                int p1 = (i - 1);
//                if(p1 < 0)
//                    p1 = loop - 1;
//                p1 = lo + p1;
//
//                Vector3f position = new Vector3f(vertices[p * 3], vertices[p * 3 + 1], vertices[p * 3 + 2]);
//                position.mulProject(transformation, position);
//                Vector3f positionPrev = new Vector3f(vertices[p1 * 3], vertices[p1* 3 + 1], vertices[p1 * 3 + 2]);
//                positionPrev.mulProject(transformation, positionPrev);
//
//                Vector3f curVert = new Vector3f(polygonVertices[p]);
//                curVert.mulProject(transformation, curVert);
//                Vector3f prevVert = new Vector3f(polygonVertices[p1]);
//                prevVert.mulProject(transformation, prevVert);
//
//                Vector2f dir = new Vector2f(positionPrev.x, positionPrev.y).sub(new Vector2f(position.x, position.y)).normalize();
//                Vector2f dirOffset = new Vector2f(prevVert.x, prevVert.y).sub(new Vector2f(curVert.x, curVert.y)).normalize();
//
//                if(dir.dot(dirOffset) <= -Math.cos(90))
//                {
//                    float[] temp = new float[]{vertices[p1 * 3], vertices[p1* 3 + 1], vertices[p1 * 3 + 2]};
//
//                    vertices[p1 * 3] = vertices[p * 3];
//                    vertices[p1* 3 + 1] = vertices[p * 3 + 1];
//                    vertices[p1 * 3 + 2] = vertices[p * 3 + 2];
//
//                    vertices[p * 3] = temp[0];
//                    vertices[p * 3 + 1] = temp[1];
//                    vertices[p * 3 + 2] = temp[2];
//
//                    temp = new float[]{texCoords[p * 4], texCoords[p * 4 + 1], texCoords[p * 4 + 2], texCoords[p * 4 + 3]};
//
//                    texCoords[p * 4] = texCoords[p1 * 4];
//                    texCoords[p * 4 + 1] = texCoords[p1 * 4 + 1];
//                    texCoords[p * 4 + 2] = texCoords[p1 * 4 + 2];
//                    texCoords[p * 4 + 3] = texCoords[p1 * 4 + 3];
//
//                    texCoords[p1 * 4] = temp[0];
//                    texCoords[p1 * 4 + 1] = temp[1];
//                    texCoords[p1 * 4 + 2] = temp[2];
//                    texCoords[p1 * 4 + 3] = temp[3];
//                }
//            }
//            lo += loop;
//        }}

        double[] flat = new double[polygonVertices.length * 2];
        for (int i = 0; i < polygonVertices.length; i++) {
            flat[i * 2] = vertices[i * 3];
            flat[i * 2 + 1] = vertices[i * 3 + 1];
        }

        int[] holes = new int[loops.length - 1];
        int index = loops[0];
        for (int i = 0; i < holes.length; ++i) {
            holes[i] = index;
            index += loops[i + 1];
        }

        //triangulate front
        List<Integer> ind = Earcut.earcut(flat, holes, 2);

        Model[] bevels = new Model[bevel.vertices.size() - 1];

        for (int i = 0; i < bevels.length; i++) {
            BevelVertex current = bevel.vertices.get(i);
            BevelVertex next = bevel.vertices.get(i + 1);

            Vector2f curPos = new Vector2f((current.y - offset.y) * bevelSize, current.z);
            Vector2f nextPos = new Vector2f((next.y - offset.y) * bevelSize, next.z);

            int c = polygonVertices.length * 2;

            float[] vertis = new float[c * 3];
            float[] textureCoords = new float[c * 4];
            float[] normas = new float[vertis.length];
            float[] tanges = new float[vertis.length];
            int[] indices = new int[vertis.length];

            l = 0;
            for (int loop : loops) {
                for (int i3 = 0; i3 < loop; i3++) {
                    Vector3f curVert = new Vector3f(vertices[l * 3], vertices[l * 3 + 1], vertices[l * 3 + 2]);
                    curVert.mulProject(transformation, curVert);
                    int p = l - 1;
                    if (p < l - i3)
                        p = l + loop - 1 - i3;
                    Vector3f prevVert = new Vector3f(vertices[p * 3], vertices[p * 3 + 1], vertices[p * 3 + 2]);
                    prevVert.mulProject(transformation, prevVert);
                    int n1 = l + 1;
                    if (n1 >= l + loop - i3)
                        n1 = l - i3;
                    Vector3f nextVert = new Vector3f(vertices[n1 * 3], vertices[n1 * 3 + 1], vertices[n1 * 3 + 2]);
                    nextVert.mulProject(transformation, nextVert);
                    Vector2f firstVert1 = Utils.offsetAndFindIntersection(new Vector2f(prevVert.x, prevVert.y), new Vector2f(curVert.x, curVert.y), new Vector2f(nextVert.x, nextVert.y), current.y * bevelSize - offset.y * bevelSize);

                    float zThicknessFirst = curPos.y * bevelSize;
                    zThicknessFirst -= (Math.abs(curPos.y) < 0.01f ? thickness - offZ : curPos.y < 0 ? (thickness - offZ) * 2 : 0);

                    if(!(Math.abs(curPos.y) < 0.01f))
                    {
                        if(curPos.y < 0)
                        {
                            if(zThicknessFirst > -(thickness - offZ))
                                zThicknessFirst = -(thickness - offZ);
                        }
                        else
                        {
                            if(zThicknessFirst < -(thickness - offZ))
                                zThicknessFirst = -(thickness - offZ);
                        }
                    }

                    zThicknessFirst -= offZ;

                    Vector3f firstVert = new Vector3f(firstVert1.x, firstVert1.y, curVert.z + zThicknessFirst);

                    firstVert.mulProject(invTransform, firstVert);
                    vertis[l * 6] = firstVert.x;
                    vertis[l * 6 + 1] = firstVert.y;
                    vertis[l * 6 + 2] = firstVert.z;
                    Vector2f secondVert1 = Utils.offsetAndFindIntersection(new Vector2f(prevVert.x, prevVert.y), new Vector2f(curVert.x, curVert.y), new Vector2f(nextVert.x, nextVert.y), next.y * bevelSize - offset.y * bevelSize);

                    float zThicknessSecond = nextPos.y * bevelSize;
                    zThicknessSecond -= (Math.abs(nextPos.y) < 0.01f ? thickness - offZ : nextPos.y < 0 ? (thickness - offZ) * 2 : 0);

                    if(!(Math.abs(nextPos.y) < 0.01f))
                    {
                        if(nextPos.y < 0)
                        {
                            if(zThicknessSecond > -(thickness - offZ))
                                zThicknessSecond = -(thickness - offZ);
                        }
                        else
                        {
                            if(zThicknessSecond < -(thickness - offZ))
                                zThicknessSecond = -(thickness - offZ);
                        }
                    }

                    zThicknessSecond -= offZ;

                    Vector3f secondVert = new Vector3f(secondVert1.x, secondVert1.y, curVert.z + zThicknessSecond);

                    secondVert.mulProject(invTransform, secondVert);
                    vertis[l * 6 + 3] = secondVert.x;
                    vertis[l * 6 + 4] = secondVert.y;
                    vertis[l * 6 + 5] = secondVert.z;

                    int i4 = l + 1;
                    if (i4 >= l + loop - i3)
                        i4 = l - i3;

                    if(transformation.determinant() < 0)
                    {
                        indices[l * 6 + 0] = l * 2;
                        indices[l * 6 + 1] = i4 * 2;
                        indices[l * 6 + 2] = i4 * 2 + 1;
                        indices[l * 6 + 3] = i4 * 2 + 1;
                        indices[l * 6 + 4] = l * 2 + 1;
                        indices[l * 6 + 5] = l * 2;
                    }
                    else
                    {
                        indices[l * 6] = i4 * 2 + 1;
                        indices[l * 6 + 1] = i4 * 2;
                        indices[l * 6 + 2] = l * 2;
                        indices[l * 6 + 3] = l * 2;
                        indices[l * 6 + 4] = l * 2 + 1;
                        indices[l * 6 + 5] = i4 * 2 + 1;
                    }

                    textureCoords[l * 8] = firstVert.x * uvScale * scale.x;
                    textureCoords[l * 8 + 1] = firstVert.y * uvScale * scale.y;
                    textureCoords[l * 8 + 2] = firstVert.x * uvScale * scale.x;
                    textureCoords[l * 8 + 3] = firstVert.y * uvScale * scale.y;
                    textureCoords[l * 8 + 4] = secondVert.x * uvScale * scale.x;
                    textureCoords[l * 8 + 5] = secondVert.y * uvScale * scale.y;
                    textureCoords[l * 8 + 6] = secondVert.x * uvScale * scale.x;
                    textureCoords[l * 8 + 7] = secondVert.y * uvScale * scale.y;
                    Vector3f deltaPos1 = (new Vector3f(vertis[indices[l * 6 + 1] * 3], vertis[indices[l * 6 + 1] * 3 + 1], vertis[indices[l * 6 + 1] * 3 + 2])).sub(new Vector3f(vertis[indices[l * 6] * 3], vertis[indices[l * 6] * 3 + 1], vertis[indices[l * 6] * 3 + 2]), new Vector3f());
                    Vector3f deltaPos2 = (new Vector3f(vertis[indices[l * 6 + 2] * 3], vertis[indices[l * 6 + 2] * 3 + 1], vertis[indices[l * 6 + 2] * 3 + 2])).sub(new Vector3f(vertis[indices[l * 6] * 3], vertis[indices[l * 6] * 3 + 1], vertis[indices[l * 6] * 3 + 2]), new Vector3f());
                    Vector4f uv0 = new Vector4f(textureCoords[indices[l * 6] * 2], textureCoords[indices[l * 6] * 2 + 1], textureCoords[indices[l * 6] * 2], textureCoords[indices[l * 6] * 2 + 1]);
                    Vector4f uv1 = new Vector4f(textureCoords[indices[l * 6 + 1] * 2], textureCoords[indices[l * 6 + 1] * 2 + 1], textureCoords[indices[l * 6 + 1] * 2], textureCoords[indices[l * 6 + 1] * 2 + 1]);
                    Vector4f uv2 = new Vector4f(textureCoords[indices[l * 6 + 2] * 2], textureCoords[indices[l * 6 + 2] * 2 + 1], textureCoords[indices[l * 6 + 2] * 2], textureCoords[indices[l * 6 + 2] * 2 + 1]);
                    Vector4f deltaUv1 = (new Vector4f((Vector4fc)uv1)).sub((Vector4fc)uv0, new Vector4f());
                    Vector4f deltaUv2 = (new Vector4f((Vector4fc)uv2)).sub((Vector4fc)uv0, new Vector4f());
                    float r = 1.0F / (deltaUv1.x * deltaUv2.y - deltaUv1.y * deltaUv2.x);
                    deltaPos1.mul(deltaUv2.y);
                    deltaPos2.mul(deltaUv1.y);
                    Vector3f tangent = (new Vector3f(deltaPos1)).sub(deltaPos2, new Vector3f());
                    tangent.mul(r).normalize();
                    tanges[indices[l * 6] * 3] = tangent.x;
                    tanges[indices[l * 6] * 3 + 1] = tangent.y;
                    tanges[indices[l * 6] * 3 + 2] = tangent.z;
                    tanges[indices[l * 6 + 1] * 3] = tangent.x;
                    tanges[indices[l * 6 + 1] * 3 + 1] = tangent.y;
                    tanges[indices[l * 6 + 1] * 3 + 2] = tangent.z;
                    tanges[indices[l * 6 + 2] * 3] = tangent.x;
                    tanges[indices[l * 6 + 2] * 3 + 1] = tangent.y;
                    tanges[indices[l * 6 + 2] * 3 + 2] = tangent.z;
                    l++;
                }
            }

            for(int in = 0; in < indices.length / 3; in++)
            {
                int triInd = in * 3;

                int triIndA = indices[triInd] * 3;
                int triIndB = indices[triInd + 1] * 3;
                int triIndC = indices[triInd + 2] * 3;

                Vector3f normal = normalFromIndices(
                        new Vector3f(vertis[triIndA], vertis[triIndA + 1], vertis[triIndA + 2]),
                        new Vector3f(vertis[triIndB], vertis[triIndB + 1], vertis[triIndB + 2]),
                        new Vector3f(vertis[triIndC], vertis[triIndC + 1], vertis[triIndC + 2]));

                Vector3f normalA = new Vector3f(normas[triIndA], normas[triIndA + 1], normas[triIndA + 2]).add(normal);
                Vector3f normalB = new Vector3f(normas[triIndB], normas[triIndB + 1], normas[triIndB + 2]).add(normal);
                Vector3f normalC = new Vector3f(normas[triIndC], normas[triIndC + 1], normas[triIndC + 2]).add(normal);

                normas[triIndA] = normalA.x;
                normas[triIndA + 1] = normalA.y;
                normas[triIndA + 2] = normalA.z;

                normas[triIndB] = normalB.x;
                normas[triIndB + 1] = normalB.y;
                normas[triIndB + 2] = normalB.z;

                normas[triIndC] = normalC.x;
                normas[triIndC + 1] = normalC.y;
                normas[triIndC + 2] = normalC.z;
            }

            bevels[i] = new Model(0, null, 0);
            bevels[i].vertices = vertis;
            bevels[i].textureCoords = textureCoords;
            bevels[i].normals = normas;
            bevels[i].indices = indices;
            bevels[i].tangents = tanges;

            try {
                ResourceDescriptor gmat = bevel.getMaterial(current.gmatSlot);
                int mat = gmat == null ? material : LoadedData.getMaterial(gmat, loader, textures, gmatMAP);

                int[] gmts = new int[vertis.length/3];
                for(int g = 0; g < gmts.length; g++)
                    gmts[g] = mat;
                bevels[i].gmats = gmts;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        int offs = polygonVertices.length;

        for(Model bevelSegment : bevels)
        {
            for(int v = 0; v < bevelSegment.vertices.length; v++)
                vertices[offs * 3 + v] = bevelSegment.vertices[v];
            for(int tc = 0; tc < bevelSegment.textureCoords.length; tc++)
                texCoords[offs * 4 + tc] = bevelSegment.textureCoords[tc];
            for(int n = 0; n < bevelSegment.normals.length; n++)
                normals[offs * 3 + n] = bevelSegment.normals[n];
            for(int t = 0; t < bevelSegment.tangents.length; t++)
                tangents[offs * 3 + t] = bevelSegment.tangents[t];
            for(int g = 0; g < bevelSegment.gmats.length; g++)
                gmats[offs + g] = bevelSegment.gmats[g];

            for(int indi : bevelSegment.indices)
                ind.add(offs + indi);

            offs += bevelSegment.vertices.length/3;
        }

        int texCount = 0;
        for(Texture t : textures)
            if(t != null)
                texCount++;

        int gmatCount = 0;
        for(Vector2i v : gmatMAP)
            if(v.x != -1)
                gmatCount++;

        for(int i = polygonVertices.length * 3; i < vertices.length/3; i++)
        {
            float x = vertices[i * 3];
            float y = vertices[i * 3 + 1];
            float z = vertices[i * 3 + 2];
            Vector3f vertex = new Vector3f(x, y, z);

            for(int o = i + 1; o < vertices.length/3; o++)
            {
                float x2 = vertices[o * 3];
                float y2 = vertices[o * 3 + 1];
                float z2 = vertices[o * 3 + 2];
                Vector3f vertex2 = new Vector3f(x2, y2, z2);

                float dist = vertex.distance(vertex2);

                if(dist < 0.0001)
                {
                    float dx = normals[i * 3];
                    float dy = normals[i * 3 + 1];
                    float dz = normals[i * 3 + 2];
                    float dx2 = normals[o * 3];
                    float dy2 = normals[o * 3 + 1];
                    float dz2 = normals[o * 3 + 2];

                    Vector3f newDir = new Vector3f(dx, dy, dz).add(new Vector3f(dx2, dy2, dz2)).normalize();

                    normals[i * 3] = newDir.x;
                    normals[i * 3 + 1] = newDir.y;
                    normals[i * 3 + 2] = newDir.z;
                    normals[o * 3] = newDir.x;
                    normals[o * 3 + 1] = newDir.y;
                    normals[o * 3 + 2] = newDir.z;
                    break;
                }
            }
        }

        model.vertices = vertices;
        model.textureCoords = texCoords;
        model.normals = normals;
        model.indices = ind.stream().mapToInt(Integer::valueOf).toArray();
        model.tangents = tangents;
        model.gmats = gmats;
        model.hasBones = false;
        model.material.textures = textures;
        model.material.texCount = texCount;
        model.material.gmatMAP = gmatMAP;
        model.material.gmatCount = gmatCount;
    }

    private static Vector3f normalFromIndices(Vector3f a, Vector3f b, Vector3f c)
    {
        return new Vector3f(b).sub(a).cross(new Vector3f(c).sub(a)).normalize();
    }

    public static void ensureWindingOrder(Vector3f[] vertices, int[] loops, Matrix4f transformation) {
        int offset = 0;

        for (int i = 0; i < loops.length; i++) {
            int loopSize = loops[i];

            List<Vector3f> loop = new ArrayList<>();
            for (int j = 0; j < loopSize; j++) {
                loop.add(vertices[offset + j]);
            }

            boolean isClockwise = isClockwise(loop, transformation);
            if (i == 0 && !isClockwise)
                reverseLoop(vertices, offset, loopSize);
            else if (i > 0 && isClockwise)
                reverseLoop(vertices, offset, loopSize);

            offset += loopSize;
        }
    }

    private static boolean isClockwise(List<Vector3f> loop, Matrix4f transformation) {
        float signedArea = 0.0f;

        for (int i = 0; i < loop.size(); i++) {
            Vector3f current = new Vector3f(loop.get(i));
            current.mulProject(transformation, current);
            Vector3f next = new Vector3f(loop.get((i + 1) % loop.size()));
            next.mulProject(transformation, next);

            signedArea += (next.x - current.x) * (next.y + current.y);
        }

        return signedArea > 0.0f;
    }

    private static void reverseLoop(Vector3f[] vertices, int offset, int loopSize) {
        for (int i = 0; i < loopSize / 2; i++) {
            int j = offset + i;
            int k = offset + loopSize - 1 - i;

            Vector3f temp = vertices[j];
            vertices[j] = vertices[k];
            vertices[k] = temp;
        }
    }

    public void digest(ModelDataOBJ data)
    {
        toDigestOBJ.add(data);
    }

    public void digest(ModelDataRMesh data)
    {
        toDigestRMesh.add(data);
    }

    public void digest(ModelDataShape data)
    {
        toDigestShape.add(data);
    }

    public static class ModelDataOBJ
    {
        public Model model;
        public String filePath;

        public ModelDataOBJ(Model model, String filePath) {
            this.model = model;
            this.filePath = filePath;
        }
    }
    public static class ModelDataRMesh
    {
        public Model model;
        public RMesh mesh;

        public ModelDataRMesh(Model model, RMesh mesh) {
            this.model = model;
            this.mesh = mesh;
        }
    }
    public static class ModelDataShape
    {
        Model model;
        ResourceDescriptor parentGmat;
        PShape shape;
        RBevel bevel;
        Matrix4f transformation;

        public ModelDataShape(Model model, ResourceDescriptor parentGmat, PShape shape, RBevel bevel, Matrix4f transformation) {
            this.parentGmat = parentGmat;
            this.shape = shape;
            this.bevel = bevel;
            this.transformation = transformation;
            this.model = model;
        }
    }
}
