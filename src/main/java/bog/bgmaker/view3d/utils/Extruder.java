package bog.bgmaker.view3d.utils;

import cwlib.structs.things.components.shapes.Polygon;
import earcut4j.Earcut;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class's functionality is pretty much taken from https://github.com/cellomonster/craftplane/tree/master/craftplane
 */
public class Extruder {
    public static ArrayList<Vector3f> createNormals(Vector3f[] verts, int[] tris) {
        Vector3f[] normals = new Vector3f[verts.length];
        for (int i = 0; i < normals.length; ++i) normals[i] = new Vector3f();

        for (int i = 0; i < tris.length; i += 3) {
            Vector3f v1 = verts[tris[i]].sub(verts[tris[i + 1]], new Vector3f());
            Vector3f v2 = verts[tris[i + 1]].sub(verts[tris[i + 2]], new Vector3f());
            normals[tris[i + 1]].add(v1.cross(v2, new Vector3f()));

            v1 = verts[tris[i + 1]].sub(verts[tris[i + 2]], new Vector3f());
            v2 = verts[tris[i + 2]].sub(verts[tris[i]], new Vector3f());
            normals[tris[i + 2]].add(v1.cross(v2, new Vector3f()));

            v1 = verts[tris[i + 2]].sub(verts[tris[i]], new Vector3f());
            v2 = verts[tris[i]].sub(verts[tris[i + 1]], new Vector3f());
            normals[tris[i]].add(v1.cross(v2, new Vector3f()));
        }


        for (Vector3f normal : normals)
            normal.normalize();

        return new ArrayList<>(Arrays.asList(normals));
    }

    public static void generateTreviri(Polygon polygon, Vector2f[] bevelPoints, float thickness, ArrayList<Vector3f> vertexList, ArrayList<Vector2f> uvList, ArrayList<Integer> triList, ArrayList<Vector3f> normalList) {
        Vector3f[] shapePoints = polygon.vertices;
        Vector3f[] offsetPath = new Vector3f[polygon.vertices.length];

        int bpSize = bevelPoints.length;
        int c = bpSize;
        int k = 0;
        float o = bevelPoints[0].x;
        float scale = 0.0038f;

        int[] loops = polygon.loops;
        int currentLoop = 0;
        int pointInLoop = 0;
        int firstPointInCurrentLoop = 0;
        int previousPointInCurrentLoop = 0;

        for (int i = 0; i < shapePoints.length; ++i) {
            Vector3f v = new Vector3f(shapePoints[i].x, shapePoints[i].y, 0.0f);
            int j = (i + 1) % shapePoints.length;
            Vector2f na = new Vector2f(shapePoints[i].y - shapePoints[j].y, shapePoints[j].x - shapePoints[i].x).normalize();
            j = (i - 1);
            if (j < 0) j += shapePoints.length;
            Vector2f nb = new Vector2f(shapePoints[j].y - shapePoints[i].y, shapePoints[i].x - shapePoints[j].x).normalize();
            Vector2f nab = na.add(nb, new Vector2f()).normalize();
            Vector3f b = new Vector3f(nab.x, nab.y, 0.0f);
            b = b.mul((float) (1.41421 / Math.sqrt(1 + na.dot(nb))));
            v.add(b.mul(bevelPoints[0].x, new Vector3f()));
            offsetPath[i] = v;
            // float angle = (float) Math.toDegrees(Math.acos(na.dot(nb)));
            // boolean sh = angle > cutoff;

            int a = 0;

            pointInLoop++;

            for (int n = 0; n < bpSize - 1; ++n) {
                Vector3f z = new Vector3f(0.0f, 0.0f, bevelPoints[n].y);
                Vector3f z2;

                boolean bool0 = pointInLoop == loops[currentLoop] && currentLoop < loops.length - 1;
                boolean bool1 = n == bpSize - 2 || bool0 || i == shapePoints.length - 1;

                if (bool1)
                    z2 = new Vector3f(0, 0, -thickness * 2f);
                else
                    z2 = new Vector3f(0, 0, bevelPoints[n + 1].y);
                Vector3f nn;
                // if (sh) nn = v.add(new Vector3f(na.x, na.y, 0.0f).mul(z.z - z2.z), new Vector3f());
                // else 
                nn = v.add(b.normalize(new Vector3f()).mul(z.z - z2.z), new Vector3f());

                // if (s[n]) a++;

                if (bool0 || i == shapePoints.length - 1) {

                    triList.add(vertexList.size());
                    triList.add(vertexList.size() + 1);
                    triList.add(previousPointInCurrentLoop);

                    triList.add(previousPointInCurrentLoop + 1);
                    triList.add(previousPointInCurrentLoop);
                    triList.add(vertexList.size() + 1);

                    triList.add(firstPointInCurrentLoop);
                    triList.add(vertexList.size() + 1);
                    triList.add(vertexList.size());

                    triList.add(vertexList.size() + 1);
                    triList.add(firstPointInCurrentLoop);
                    triList.add(firstPointInCurrentLoop + 1);

                    if(currentLoop < loops.length - 1)
                        currentLoop++;
                    pointInLoop = 0;
                }
                else if(pointInLoop == loops[currentLoop] - 1)
                {
                    if(pointInLoop == 1)
                        firstPointInCurrentLoop = vertexList.size();
                    else
                        previousPointInCurrentLoop = vertexList.size();
                }
                else
                {
                    triList.add(vertexList.size() + 1);
                    triList.add(vertexList.size());
                    triList.add(vertexList.size() + c);

                    triList.add(vertexList.size() + c + 1);
                    triList.add(vertexList.size() + 1);
                    triList.add(vertexList.size() + c);

                    if(pointInLoop == 1)
                        firstPointInCurrentLoop = vertexList.size();
                }

                Vector3f newVert = b.mul(bevelPoints[n].x - o, new Vector3f()).add(z).add(v);
                vertexList.add(newVert);
                uvList.add(new Vector2f(
                    newVert.x * scale,
                    newVert.y * scale
                ));

                if (bool1)
                {
                    vertexList.add(b.mul(bevelPoints[n + 1].x - o, new Vector3f()).add(z2).add(v));
                    uvList.add(new Vector2f(
                        nn.x * scale,
                        nn.y * scale
                    ));
                } //else if (s[n + 1]) {

                //}
                if(bool0)
                    break;
            }
        }

        normalList.addAll(createNormals(vertexList.toArray(Vector3f[]::new), triList.stream().mapToInt(Integer::valueOf).toArray()));
        k = vertexList.size();

        for (int i = 0; i < offsetPath.length; ++i) {
            vertexList.add(offsetPath[i]);
            uvList.add(new Vector2f(offsetPath[i].x * scale, offsetPath[i].y * scale));
            normalList.add(new Vector3f(0.0f, 0.0f, -1.0f));
        }

        double[] flat = new double[shapePoints.length * 2];
        for (int i = 0, j = 0; i < shapePoints.length; ++i) {
            flat[j++] = shapePoints[i].x;
            flat[j++] = shapePoints[i].y;
        }

        int[] holes = new int[loops.length - 1];
        int index = loops[0];
        for (int i = 0; i < holes.length; ++i) {
            holes[i] = index;
            index += loops[i + 1];
        }

        for(int i : holes)
            System.out.println(i);

        int[] frontTris = Earcut.earcut(flat, holes, 2).stream().mapToInt(Integer::valueOf).toArray();
        for (int i = 0; i < frontTris.length; i++) {
            triList.add(frontTris[i] + k);
        }
    }
}
