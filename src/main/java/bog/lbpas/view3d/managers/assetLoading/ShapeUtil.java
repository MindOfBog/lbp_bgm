package bog.lbpas.view3d.managers.assetLoading;

import bog.lbpas.view3d.utils.print;
import cwlib.resources.RBevel;
import cwlib.structs.bevel.BevelVertex;
import cwlib.structs.things.components.shapes.Polygon;
import cwlib.structs.things.parts.PGeneratedMesh;
import cwlib.structs.things.parts.PShape;
import io.github.earcut4j.Earcut;
import org.joml.*;

import java.lang.Math;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for extruding a 2D polygon shape into a 3D mesh,
 * ported from the original PS3 GenerateMesh / BevelFunction implementation.
 */
public class ShapeUtil {

    public static final float FixedScaleUV = 0.002f; //random value that seems to work

    /** Output mesh data, ready for upload to any renderer. */
    public static final class ExtrusionResult {
        /** Interleaved XYZ positions, length = vertexCount * 3. */
        public float[] positions;
        /** Interleaved XYZ normals, length = vertexCount * 3. */
        public float[] normals;
        /**
         * Interleaved UV pairs, length = vertexCount * 4.
         * [u0, v0, u1, v1] per vertex
         */
        public float[] uvs;
        /** Triangle indices. */
        public int[] indices;
        /** Per-vertex material slot (0–3), length = vertexCount. */
        public int[] materialSlots;
    }

    // Internal vertex type (mirrors CTempVertex fields)
    private static final class TempVertex {
        final float[] pos = new float[3];  // local XYZ position
        final float[] n   = new float[3];  // smooth shading normal
        final float[] sn  = new float[3];  // sharp / geometric normal
        final float[] t   = new float[3];  // tangent
        float uv0u, uv0v;
        float uv1u, uv1v;
        int   matSlot;
    }

    /**
     * Extrudes a 2D polygon shape into a 3D mesh, applying the bevel profile
     * from {@code bevel}.
     *
     * Vertex positions and normals are emitted in local space.
     *
     * @param transformation world transform of the object
     * @param polygon        the 2D outline (vertices + loop partition)
     * @param thickness      half-depth of the slab (from {@code PShape.thickness})
     * @param bevelSize      bevel width (from {@code PShape.bevelSize})
     * @param bevel          bevel resource carrying the profile vertices and materials
     * @return a fully populated {@link ExtrusionResult} in local space
     */
    public static ExtrusionResult extrudePolygon(Matrix4f transformation, PShape shape, PGeneratedMesh generatedMesh, RBevel bevel)
    {

        float thickness = shape.thickness;
        float bevelSize = shape.bevelSize + bevel.fixedBevelSize;

        //orientation & transform decomposition

        float determinant = transformation.determinant();
        boolean isFlipped = determinant < 0;

        Matrix3f RS = transformation.get3x3(new Matrix3f());
        Matrix3f iRS = transformation.get3x3(new Matrix3f()).invert();

        //smooth/subdivide bevel

        Polygon polygon = new Polygon();
        polygon.requiresZ = shape.polygon.requiresZ;

        List<Integer> ls = Arrays.stream(shape.polygon.loops).boxed().collect(Collectors.toList());
        List<Vector3f> vs = new ArrayList<>();

        for(Vector3f v : shape.polygon.vertices)
            vs.add(new Vector3f(v));

        if(bevel.relaxStrength < bevelSize || bevel.spongy)
            BevelRelaxer.relaxBevel(vs, ls, bevelSize, bevel, 1f);

//        for(Vector3f v : vs)
//            v.mul(iRS);

        BevelRelaxer.cleanup(vs, ls, transformation);

        polygon.loops = ls.stream().mapToInt(Integer::valueOf).toArray();
        polygon.vertices = new Vector3f[vs.size()];

        for(int i = 0; i < vs.size(); i++)
            polygon.vertices[i] = vs.get(i);

        Vector2f offsetUV0 = generatedMesh == null || generatedMesh.uvOffset == null ? new Vector2f(0f) : new Vector2f(generatedMesh.uvOffset.x, generatedMesh.uvOffset.y);
        Vector2f offsetUV1 = generatedMesh == null || generatedMesh.uvOffset == null ? new Vector2f(0f) : new Vector2f(generatedMesh.uvOffset.z, generatedMesh.uvOffset.w);

        //extract the Z-axis scale of the transform (column 2 magnitude).
        float zScale = (float) Math.sqrt(
                transformation.m02() * transformation.m02() +
                        transformation.m12() * transformation.m12() +
                        transformation.m22() * transformation.m22());
        if (zScale < 1e-9f) zScale = 1.0f;

        float correctedThickness = thickness / zScale;

        //validate inputs

        if (polygon == null || polygon.vertices == null || polygon.vertices.length < 3)
            return emptyResult();

        final int   numBevelLoops  = (bevel == null) ? 0 : bevel.vertices.size();
        final float textureRepeats = (bevel == null) ? 1.0f : bevel.textureRepeats;
        final float bevelZScale    = computeBevelZScale(thickness, bevelSize);

        //build polygon loop table
        //loops[i] = vertex count of loop i.
        //loop 0 = outer boundary; subsequent loops = holes (Earcut convention).

        final int[] rawLoops  = (polygon.loops != null && polygon.loops.length > 0)
                ? polygon.loops
                : new int[]{ polygon.vertices.length };
        final int   numLoops  = rawLoops.length;
        final int[] loopStart = new int[numLoops];
        int running = 0;
        for (int i = 0; i < numLoops; i++) {
            loopStart[i] = running;
            running      += rawLoops[i];
        }
        final int polyVertCount = running;

        //triangulate the front face with Earcut4J
        //(porting original triangulation code was a miss, for now use earcut4j)

        float frontInset = 0.0f;
        if (numBevelLoops > 0)
            frontInset = bevelSize * bevel.vertices.get(numBevelLoops - 1).y;

        // Build the inset polygon for Earcut
        Vector2f[] insetVerts = insetPolygon(polygon, rawLoops, loopStart, polyVertCount, frontInset * -1f, RS, iRS);

        double[] flat = new double[polyVertCount * 2];
        for (int i = 0; i < polyVertCount; i++) {
            flat[i * 2    ] = insetVerts[i].x;
            flat[i * 2 + 1] = insetVerts[i].y;
        }

        int[] holeIndices = (numLoops > 1) ? new int[numLoops - 1] : null;
        if (numLoops > 1)
            for (int i = 1; i < numLoops; i++)
                holeIndices[i - 1] = loopStart[i];
//
//        List<Integer> frontTris = new ArrayList<>();
//
//        try
//        {
//            boolean triangulationSuccess = triangulateByEarClipping(insetVerts, rawLoops, frontTris, isFlipped);
//        }catch (Exception e){print.stackTrace(e);}

//        print.neutral("polygon:");
//        for (Vector2f o : insetVerts)
//            print.neutral(o);
//        print.warning("\nloops:");
//        print.warning(rawLoops);
//        print.neutral("\nisFlipped:");
//        print.neutral(isFlipped);
//
//        print.error("\noutTris:");
//        print.error(frontTris);

        //Earcut.earcut(flat, holeIndices, 2);
        List<Integer> frontTris = Earcut.earcut(flat, holeIndices, 2);
        //todo

        final int numFrontTris  = frontTris.size() / 3;

        //pre-compute bevel Z-depths for every bevel loop vertex

        final float[] depthAt = new float[numBevelLoops];
        for (int i = 0; i < numBevelLoops; i++)
            depthAt[i] = bevelZFromNormalized(bevel.vertices.get(i).z, thickness, bevelSize);

        //compute arc-length UV weights along the bevel profile

        final float[] bevelArcLen = new float[numBevelLoops];
        float totalArc = 0.0f;
        for (int i = 0; i < numBevelLoops - 1; i++) {
            float y0 = bevel.vertices.get(i).y;
            float y1 = bevel.vertices.get(i + 1).y;
            float dY = bevelSize * (y1 - y0);
            float dZ = bevelZScale * (depthAt[i + 1] - depthAt[i]);
            float seg = (float) Math.sqrt(dY * dY + dZ * dZ);
            bevelArcLen[i] = totalArc;
            totalArc      += seg;
        }
        if (numBevelLoops > 0)
            bevelArcLen[numBevelLoops - 1] = totalArc;
        float arcScale = (totalArc > 1e-9f) ? (textureRepeats / totalArc) : 1.0f;

        //compute per-edge tangents, outward normals, and edge lengths

        final float[] edgeDirX = new float[polyVertCount];
        final float[] edgeDirY = new float[polyVertCount];
        final float[] edgeNrmX = new float[polyVertCount];
        final float[] edgeNrmY = new float[polyVertCount];
        final float[] edgeLen  = new float[polyVertCount];
        for (int li = 0; li < numLoops; li++) {
            int start = loopStart[li];
            int count = rawLoops[li];
            for (int ei = 0; ei < count; ei++) {
                int vi0 = start + ei;
                int vi1 = start + (ei + 1) % count;

                Vector3f ver0 = new Vector3f(polygon.vertices[vi0].x, polygon.vertices[vi0].y, 0).mul(RS);
                Vector3f ver1 = new Vector3f(polygon.vertices[vi1].x, polygon.vertices[vi1].y, 0).mul(RS);

                float dx = ver1.x - ver0.x;
                float dy = ver1.y - ver0.y;
                float len = (float) Math.sqrt(dx * dx + dy * dy);
                edgeLen[vi0] = len;
                if (len > 1e-9f) {
                    edgeDirX[vi0] =  dx / len;
                    edgeDirY[vi0] =  dy / len;
                    edgeNrmX[vi0] =  dy / len;
                    edgeNrmY[vi0] = -dx / len;
                } else {
                    edgeDirX[vi0] = 1.0f;
                    edgeDirY[vi0] = 0.0f;
                    edgeNrmX[vi0] = 0.0f;
                    edgeNrmY[vi0] = -1.0f;
                }
            }
        }

        //compute smooth vertex normals (averaged from adjacent edge normals)

        final float[] smNrmX  = new float[polyVertCount];
        final float[] smNrmY  = new float[polyVertCount];
        final boolean[] isSharp = new boolean[polyVertCount];
        float cosCutoff = (bevel != null)
                ? (float) Math.cos(Math.toRadians(bevel.autoSmoothCutoffAngle))
                : -1.0f;

        for (int li = 0; li < numLoops; li++) {
            int start = loopStart[li];
            int count = rawLoops[li];
            for (int vi = 0; vi < count; vi++) {
                int cur  = start + vi;
                int prev = start + ((vi - 1 + count) % count);
                float inNX = edgeNrmX[prev], inNY = edgeNrmY[prev];
                float outNX = edgeNrmX[cur], outNY = edgeNrmY[cur];
                float dot = inNX * outNX + inNY * outNY;
                isSharp[cur] = (dot < cosCutoff);
                float ax = inNX + outNX;
                float ay = inNY + outNY;
                float alen = (float) Math.sqrt(ax * ax + ay * ay);
                if (alen > 1e-9f) { ax /= alen; ay /= alen; }
                smNrmX[cur] = ax;
                smNrmY[cur] = ay;
            }
        }

        //determine front/back cap Z depths

        float frontZ = (numBevelLoops > 0) ? depthAt[numBevelLoops - 1] :  thickness * 0.5f;
        float backZ  = (numBevelLoops > 0) ? depthAt[0]                 : -thickness * 0.5f;

        //emit bevel-ring (side-wall) vertices

        List<TempVertex> verts   = new ArrayList<>();
        List<Integer>    idxList = new ArrayList<>();

        int[] ringVertBase = new int[numBevelLoops];

        for (int bi = 0; bi < numBevelLoops; bi++) {
            ringVertBase[bi] = verts.size();
            BevelVertex bv  = bevel.vertices.get(bi);

            float outY      = bv.y;
            float depth     = depthAt[bi];
            float arcV      = bevelArcLen[bi] * arcScale;

            float profTanY, profTanZ;
            if (bi < numBevelLoops - 1) {
                BevelVertex bvNext = bevel.vertices.get(bi + 1);
                float dY  = bevelSize * (bvNext.y   - bv.y);
                float dZ  = bevelZScale * (depthAt[bi + 1] - depth);
                float pLen = (float) Math.sqrt(dY * dY + dZ * dZ);
                profTanY = (pLen > 1e-9f) ? dY / pLen : 0.0f;
                profTanZ = (pLen > 1e-9f) ? dZ / pLen : 1.0f;
            } else if (bi > 0) {
                BevelVertex bvPrev = bevel.vertices.get(bi - 1);
                float dY  = bevelSize * (bv.y - bvPrev.y);
                float dZ  = bevelZScale * (depth - depthAt[bi - 1]);
                float pLen = (float) Math.sqrt(dY * dY + dZ * dZ);
                profTanY = (pLen > 1e-9f) ? dY / pLen : 0.0f;
                profTanZ = (pLen > 1e-9f) ? dZ / pLen : 1.0f;
            } else {
                profTanY = 0.0f;
                profTanZ = 1.0f;
            }

            float profNrmRadial =  profTanZ;
            float profNrmZ      = -profTanY;

            for (int li = 0; li < numLoops; li++) {
                int start = loopStart[li];
                int count = rawLoops[li];
                for (int ei = 0; ei < count; ei++) {
                    int vi = start + ei;

                    float amount = outY * bevelSize * -1;

                    float ix = polygon.vertices[vi].x;
                    float iy = polygon.vertices[vi].y;

                    Vector3f offset = new Vector3f(smNrmX[vi] * amount, smNrmY[vi] * amount, 0).mul(iRS);
                    Vector3f worldXY = new Vector3f(ix, iy, depth).add(offset);

                    float px = worldXY.x;//todo
                    float py = worldXY.y;

                    worldXY = new Vector3f(ix, iy, depth).mul(RS).mul(FixedScaleUV);

                    float pu = worldXY.x + smNrmX[vi] * outY * bevelSize;//todo
                    float pv = worldXY.y + smNrmY[vi] * outY * bevelSize;

                    float pz = depth;

                    boolean useSharp = isSharp[vi]
                            || (bv.smoothWithPrevious == 0 && bi > 0);
                    float nBaseX = useSharp ? edgeNrmX[vi] : smNrmX[vi];
                    float nBaseY = useSharp ? edgeNrmY[vi] : smNrmY[vi];

                    float nx = nBaseX * profNrmRadial;
                    float ny = nBaseY * profNrmRadial;
                    float nz = profNrmZ;
                    float nl = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);
                    if (nl > 1e-9f) { nx /= nl; ny /= nl; nz /= nl; }

                    float tx = edgeDirX[vi];
                    float ty = edgeDirY[vi];

                    float edgeU = computeEdgeU(polygon, loopStart, rawLoops, li, ei, bevelSize); //todo
                    float uv0u  = edgeU;
                    float uv0v  = arcV;

                    float uv1u = pu / bevelSize;
                    float uv1v = pv / bevelSize;

                    float[] uvOverride = applyMappingMode(
                            bv, pu, pv, pz, bevelSize, textureRepeats,
                            uv0u, uv0v);
                    uv0u = uvOverride[0];
                    uv0v = uvOverride[1];

                    TempVertex tv = new TempVertex();
                    tv.pos[0] = px; tv.pos[1] = py; tv.pos[2] = pz / zScale;//todo
                    tv.n  [0] = nx; tv.n  [1] = ny; tv.n  [2] = nz;
                    tv.sn [0] = edgeNrmX[vi] * profNrmRadial;
                    tv.sn [1] = edgeNrmY[vi] * profNrmRadial;
                    tv.sn [2] = profNrmZ;
                    normalise3(tv.sn);
                    tv.t[0] = tx; tv.t[1] = ty; tv.t[2] = 0.0f;
                    tv.uv0u    = uv0u;
                    tv.uv0v    = uv0v;
                    tv.uv1u    = uv1u;
                    tv.uv1v    = uv1v;
                    tv.matSlot = bv.gmatSlot & 0x03;
                    verts.add(tv);
                }
            }
        }

        //emit bevel-ring quad indices (side wall triangles)

        for (int bi = 0; bi < numBevelLoops - 1; bi++) {
            for (int li = 0; li < numLoops; li++) {
                int start = loopStart[li];
                int count = rawLoops[li];
                for (int ei = 0; ei < count; ei++) {
                    int ei1  = (ei + 1) % count;
                    int idxA = ringVertBase[bi]     + start + ei;
                    int idxB = ringVertBase[bi]     + start + ei1;
                    int idxC = ringVertBase[bi + 1] + start + ei;
                    int idxD = ringVertBase[bi + 1] + start + ei1;
                    if (!isFlipped) {
                        idxList.add(idxA); idxList.add(idxC); idxList.add(idxB);
                        idxList.add(idxB); idxList.add(idxC); idxList.add(idxD);
                    } else {
                        idxList.add(idxA); idxList.add(idxB); idxList.add(idxC);
                        idxList.add(idxB); idxList.add(idxD); idxList.add(idxC);
                    }
                }
            }
        }

        //emit front-face cap vertices and triangles

        int frontCapBase = verts.size();
        int frontMatSlot = (numBevelLoops > 0)
                ? (bevel.vertices.get(numBevelLoops - 1).gmatSlot & 0x03)
                : 0;
        float frontNZ = 1.0f;

        for (int i = 0; i < polyVertCount; i++) {
            float px = insetVerts[i].x;
            float py = insetVerts[i].y;
            float pz = frontZ;

            Vector3f worldXY = new Vector3f(px, py, pz).mul(RS);

            TempVertex tv = new TempVertex();
            tv.pos[0] = px; tv.pos[1] = py; tv.pos[2] = pz / zScale;
            tv.n  [0] = 0;  tv.n  [1] = 0;  tv.n  [2] = frontNZ;
            tv.sn [0] = 0;  tv.sn [1] = 0;  tv.sn [2] = frontNZ;
            tv.t  [0] = 1;  tv.t  [1] = 0;  tv.t  [2] = 0;

            tv.uv0u = worldXY.x * FixedScaleUV;
            tv.uv0v = worldXY.y * FixedScaleUV;

            tv.uv1u = tv.uv0u + offsetUV1.x;
            tv.uv1v = tv.uv0v + offsetUV1.y;

            tv.uv0u += offsetUV0.x;
            tv.uv0v += offsetUV0.y;

            tv.uv1v = 1f - tv.uv1v;
            tv.uv0v = 1f - tv.uv0v;

            tv.matSlot = frontMatSlot;
            verts.add(tv);
        }

        for (int t = 0; t < numFrontTris; t++) {
            int a = frontTris.get(t * 3    ) + frontCapBase;
            int b = frontTris.get(t * 3 + 1) + frontCapBase;
            int c = frontTris.get(t * 3 + 2) + frontCapBase;

            idxList.add(a); idxList.add(b); idxList.add(c);
        }

        //emit back-face cap, controlled by bevel.includeBackface

//        if (bevel != null && bevel.includeBackface) {todo
//
//            float backInset = 0.0f;
//            if (numBevelLoops > 0)
//                backInset = bevelSize * bevel.vertices.get(0).y;
//
//            Vector2f[] insetVerts1 = insetPolygon(polygon, rawLoops, loopStart, polyVertCount, backInset * -1, RS, iRS);
//
//            double[] flat1 = new double[polyVertCount * 2];
//            for (int i = 0; i < polyVertCount; i++) {
//                flat1[i * 2] = insetVerts1[i].x;
//                flat1[i * 2 + 1] = insetVerts1[i].y;
//            }
//
//            List<Integer> backTris = Earcut.earcut(flat1, holeIndices, 2);
//            final int numBackTris = backTris.size() / 3;
//
//            int backCapBase = verts.size();
//            int backMatSlot = (numBevelLoops > 0)
//                    ? (bevel.vertices.get(0).gmatSlot & 0x03)
//                    : 0;
//            float backNZ = isFlipped ? 1.0f : -1.0f;
//
//            for (int i = 0; i < polyVertCount; i++)
//            {
//                float px = insetVerts1[i].x;
//                float py = insetVerts1[i].y;
//                float pz = backZ;
//
//                TempVertex tv = new TempVertex();
//                tv.pos[0] = px; tv.pos[1] = py; tv.pos[2] = pz / zScale;
//                tv.n  [0] = 0;  tv.n  [1] = 0;  tv.n  [2] = backNZ;
//                tv.sn [0] = 0;  tv.sn [1] = 0;  tv.sn [2] = backNZ;
//                tv.t  [0] = 1;  tv.t  [1] = 0;  tv.t  [2] = 0;
//                tv.uv0u    = px * 2 + generatedMesh.uvOffset.x;
//                tv.uv0v    = py * 2 + generatedMesh.uvOffset.y;//todo
//                tv.uv1u    = tv.uv0u + generatedMesh.uvOffset.z;
//                tv.uv1v    = tv.uv0v + generatedMesh.uvOffset.w;
//                tv.matSlot = backMatSlot;
//                verts.add(tv);
//            }
//
//            for (int t = 0; t < numBackTris; t++) {
//                int a = backTris.get(t * 3    ) + backCapBase;
//                int b = backTris.get(t * 3 + 1) + backCapBase;
//                int c = backTris.get(t * 3 + 2) + backCapBase;
//
//                idxList.add(a); idxList.add(c); idxList.add(b);
//            }
//        }

        //pack into ExtrusionResult
        return packResult(verts, idxList);
    }

    // helpers

    /**
     * Insets each polygon vertex along the averaged smooth normal by {@code inset}
     * units.  This replicates the {@code Reduce()} call in the original that shrinks
     * the polygon before front-face triangulation.
     *
     * When inset is 0 the original vertices are returned unchanged.
     */
    private static Vector2f[] insetPolygon(Polygon polygon, int[] rawLoops, int[] loopStart, int polyVertCount, float inset, Matrix3f RS, Matrix3f iRS)
    {
        Vector2f[] result = new Vector2f[polyVertCount];

        if (Math.abs(inset) < 1e-9f) {
            for (int i = 0; i < polyVertCount; i++)
                result[i] = new Vector2f(polygon.vertices[i].x, polygon.vertices[i].y);
            return result;
        }

        int numLoops = rawLoops.length;
        for (int li = 0; li < numLoops; li++) {
            int start = loopStart[li];
            int count = rawLoops[li];
            for (int vi = 0; vi < count; vi++) {
                int cur  = start + vi;
                int prev = start + ((vi - 1 + count) % count);
                int next = start + ((vi + 1) % count);

                Vector3f previousV = new Vector3f(polygon.vertices[prev].x, polygon.vertices[prev].y, 0).mul(RS);
                Vector3f currentV = new Vector3f(polygon.vertices[cur].x, polygon.vertices[cur].y, 0).mul(RS);
                Vector3f nextV = new Vector3f(polygon.vertices[next].x, polygon.vertices[next].y, 0).mul(RS);

                float ax = previousV.x,
                        ay = previousV.y;

                float bx = currentV.x,
                        by = currentV.y;

                float cx = nextV.x,
                        cy = nextV.y;

                //incoming edge normal (outward, normalised)
                float d0x = bx - ax, d0y = by - ay;
                float l0 = (float) Math.sqrt(d0x * d0x + d0y * d0y);
                if (l0 > 1e-9f) { d0x /= l0; d0y /= l0; }
                float n0x =  d0y, n0y = -d0x;

                //outgoing edge normal (outward, normalised)
                float d1x = cx - bx, d1y = cy - by;
                float l1 = (float) Math.sqrt(d1x * d1x + d1y * d1y);
                if (l1 > 1e-9f) { d1x /= l1; d1y /= l1; }
                float n1x =  d1y, n1y = -d1x;

                //average inset direction
                float mx = n0x + n1x;
                float my = n0y + n1y;
                float ml = (float) Math.sqrt(mx * mx + my * my);
                if (ml > 1e-9f) { mx /= ml; my /= ml; }

                Vector3f out = new Vector3f(bx + mx * inset, by + my * inset, 0).mul(iRS);

                result[cur] = new Vector2f(out.x, out.y);//todo
            }
        }
        return result;
    }

    /**
     * Converts a normalised {@code BevelVertex.z} into an actual local-space depth value.
     */
    private static float bevelZFromNormalized(float z, float thickness, float bevelSize) {
        if (z >= 0.5f)
            return bevelSize * (z - 1.0f) + thickness;
        else if (z > -0.5f)
            return z * (2.0f * thickness - bevelSize);
        else
            return z * bevelSize + bevelSize - thickness;
    }

    /**
     * Computes the Z-axis scale factor for the bevel profile depth range.
     * When bevelSize is near zero we return 1 to avoid division by zero.
     */
    private static float computeBevelZScale(float thickness, float bevelSize) {
        return (bevelSize > 1e-6f) ? (thickness / bevelSize) : 1.0f;
    }

    /**
     * Computes UV-u for a polygon edge vertex as the cumulative edge length
     * up to (but not including) that vertex, divided by bevelSize.
     */
    private static float computeEdgeU(Polygon polygon, int[] loopStart, int[] rawLoops, int loopIdx, int edgeIdx, float bevelSize)
    {
        int   start  = loopStart[loopIdx];
        int   count  = rawLoops[loopIdx];
        float cumulativeLength = 0.0f;
        for (int i = 0; i < edgeIdx; i++) {
            int vi0 = start + i;
            int vi1 = start + (i + 1) % count;
            float dx = polygon.vertices[vi1].x - polygon.vertices[vi0].x;
            float dy = polygon.vertices[vi1].y - polygon.vertices[vi0].y;
            cumulativeLength  += (float) Math.sqrt(dx * dx + dy * dy);
        }
        return (bevelSize > 1e-6f) ? (cumulativeLength / bevelSize) : cumulativeLength;
    }

    private static float[] applyMappingMode(
            BevelVertex bv,
            float px, float py, float pz,
            float bevelSize, float textureRepeats,
            float currentU, float currentV)
    {
        switch (bv.mappingMode) {
            case HIDDEN:
                return new float[]{ 0.0f, 0.0f };
            case CYLINDER_01:
            case CYLINDER:
                // U wraps around the perimeter; V maps depth.
                return new float[]{ currentU * textureRepeats, currentV };
            case PLANARXZ_01:
            case PLANARXZ:
                // Project onto XZ plane; Y-axis stretches onto V.
                return new float[]{ px / bevelSize,
                        pz / bevelSize };
            case DAVE:
            default:
                return new float[]{ currentU, currentV };
        }
    }

    private static void normalise3(float[] v)
    {
        float len = (float) Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
        if (len > 1e-9f) { v[0] /= len; v[1] /= len; v[2] /= len; }
    }

    private static ExtrusionResult packResult(List<TempVertex> verts, List<Integer> idx) {
        int vc = verts.size();
        int ic = idx.size();

        ExtrusionResult r  = new ExtrusionResult();
        r.positions        = new float[vc * 3];
        r.normals          = new float[vc * 3];
        r.uvs              = new float[vc * 4];
        r.indices          = new int[ic];
        r.materialSlots    = new int[vc];

        for (int i = 0; i < vc; i++) {
            TempVertex tv          = verts.get(i);
            r.positions[i * 3    ] = tv.pos[0];
            r.positions[i * 3 + 1] = tv.pos[1];
            r.positions[i * 3 + 2] = tv.pos[2];
            r.normals  [i * 3    ] = tv.n[0];
            r.normals  [i * 3 + 1] = tv.n[1];
            r.normals  [i * 3 + 2] = tv.n[2];
            r.uvs      [i * 4    ] = tv.uv0u;
            r.uvs      [i * 4 + 1] = tv.uv0v;
            r.uvs      [i * 4 + 2] = tv.uv1u;
            r.uvs      [i * 4 + 3] = tv.uv1v;
            r.materialSlots[i]     = tv.matSlot;
        }
        for (int i = 0; i < ic; i++)
            r.indices[i] = idx.get(i);

        return r;
    }

    private static ExtrusionResult emptyResult() {
        ExtrusionResult r  = new ExtrusionResult();
        r.positions        = new float[0];
        r.normals          = new float[0];
        r.uvs              = new float[0];
        r.indices          = new int[0];
        r.materialSlots    = new int[0];
        return r;
    }

    public static class BevelRelaxer {

        public static final float MIN_BEVEL_SIZE = 1e-5f;
        private static final float MIN_DIAMETER = 1f;

        public static float getEffectiveBevelSize(float bevelSize, RBevel bevel, float lodScale)
        {
            float effectiveBevelSize = (bevelSize / lodScale) * bevel.subDivRadius;
            if (effectiveBevelSize < MIN_BEVEL_SIZE) {
                effectiveBevelSize = MIN_BEVEL_SIZE;
            }

            if (bevel.spongy)
            {
                effectiveBevelSize = bevelSize / lodScale;
            }

            return effectiveBevelSize;
        }

        /**
         * Relaxes/smooths a bevel polygon by iteratively averaging vertex positions
         * and optionally subdividing edges based on length relative to bevel size.
         *
         * @param polygon    Input/output polygon vertices (modified in place)
         * @param loops      Loop sizes: each entry defines how many vertices belong to that loop
         * @param bevelSize  Base bevel size in world units
         * @param bevel      Bevel configuration object
         * @param lodScale   Level-of-detail scale factor
         */
        public static void relaxBevel(List<Vector3f> polygon, List<Integer> loops, float bevelSize, RBevel bevel, float lodScale) {

            float effectiveBevelSize = getEffectiveBevelSize(bevelSize, bevel, lodScale);

            List<Vector3f> workingPolygon = new ArrayList<>();
            List<Integer> workingLoops = new ArrayList<>();

            float relaxScale = lodScale * bevel.relaxStrength;
            float halfRelax = relaxScale * 0.5f;

            if (loops.isEmpty()) {
                runRelaxPasses(polygon, loops, workingPolygon, workingLoops, halfRelax, relaxScale);
                return;
            }

            // Subdivide edges

            // Walk each loop and each edge within that loop.
            // If an edge is longer than effectiveBevelSize, insert intermediate vertices.

            int globalVertexOffset = 0;

            for (int loopIndex = 0; loopIndex < loops.size(); loopIndex++)
            {
                int loopSize = loops.get(loopIndex);

                // Reserve a new loop entry in the working loops
                workingLoops.add(0);

                for (int edgeIndex = 0; edgeIndex < loopSize; edgeIndex++)
                {
                    int vertexBase = globalVertexOffset + edgeIndex;

                    Vector3f currentVertex = polygon.get(vertexBase);

                    // Next vertex wraps around within this loop
                    int nextIndex = (edgeIndex + 1) % loopSize;
                    Vector3f nextVertex = polygon.get(globalVertexOffset + nextIndex);

                    // Edge vector and its length
                    float edgeDx = nextVertex.x - currentVertex.x;
                    float edgeDy = nextVertex.y - currentVertex.y;
                    float edgeLengthSq = edgeDx * edgeDx + edgeDy * edgeDy;
                    float edgeLength = (edgeLengthSq > 0f) ? (float) Math.sqrt(edgeLengthSq) : 0f;

                    int subdivisionCount = (int) (edgeLength / effectiveBevelSize + 0.5f);

                    float stepT;
                    int startStep, endStep, totalSteps;

                    if (subdivisionCount == 0) {
                        // Edge is short enough: emit just the current vertex (t=0 only)
                        startStep = 0;
                        endStep = 1;   // exclusive: we emit steps [startStep, endStep)
                        totalSteps = 1;
                        stepT = 1.0f; // unused when totalSteps==1
                    } else {
                        stepT = 1.0f / subdivisionCount;

                        if (subdivisionCount < 8) {
                            // Small subdivision count: split into two halves
                            int half = subdivisionCount / 2;
                            int kStart = subdivisionCount - 5;
                            if (kStart < half) kStart = half;

                            // Emit [0, kStart) then [kStart, subdivisionCount) with wrap-around steps
                            startStep = 0;
                            endStep = kStart;
                            totalSteps = subdivisionCount;
                        } else {
                            // Large subdivision count
                            int kStart = subdivisionCount - 5;
                            if (kStart < 4) kStart = 4;

                            startStep = 0;
                            endStep = kStart;
                            totalSteps = subdivisionCount;
                        }

                        // Emit first segment [startStep, endStep)
                        for (int step = startStep; step < endStep; step++) {
                            float t = stepT * step;
                            Vector3f interpolated = new Vector3f(
                                    currentVertex.x + edgeDx * t,
                                    currentVertex.y + edgeDy * t,
                                    currentVertex.z);
                            workingPolygon.add(interpolated);
                            int lastLoop = workingLoops.size() - 1;
                            workingLoops.set(lastLoop, workingLoops.get(lastLoop) + 1);
                        }

                        // Emit second segment [endStep, totalSteps) with wrap-around accounting
                        int remainingSteps = totalSteps - endStep;
                        startStep = endStep;
                        endStep = totalSteps;
                    }

                    // Emit the primary segment (or only segment for subdivisionCount == 0)
                    for (int step = startStep; step < endStep; step++) {
                        float t = stepT * step;
                        Vector3f interpolated = new Vector3f(
                                currentVertex.x + edgeDx * t,
                                currentVertex.y + edgeDy * t,
                                currentVertex.z);
                        workingPolygon.add(interpolated);
                        int lastLoop = workingLoops.size() - 1;
                        workingLoops.set(lastLoop, workingLoops.get(lastLoop) + 1);
                    }
                }

                globalVertexOffset += loopSize;
            }

            // Replace polygon and loops with subdivided versions
            polygon.clear();
            polygon.addAll(workingPolygon);
            loops.clear();
            loops.addAll(workingLoops);

            // 4 passes of Laplacian relaxation
            runRelaxPasses(polygon, loops, new ArrayList<>(), new ArrayList<>(), halfRelax, relaxScale);
        }

        /**
         * Runs exactly 4 Laplacian smoothing passes over the polygon.
         * Each vertex is moved toward the average of its two loop-neighbors,
         * weighted by relaxScale (half for neighbors, remainder for self).
         *
         * @param polygon        Vertex list (modified in place via swap)
         * @param loops          Loop size list
         * @param scratchPolygon Scratch buffer (will be populated and swapped)
         * @param scratchLoops   Scratch loop buffer
         * @param neighborWeight Weight applied to the sum of two neighbors (0.5 * relaxScale)
         * @param selfWeight     Weight applied to the current vertex position (relaxScale)
         */
        private static void runRelaxPasses(
                List<Vector3f> polygon,
                List<Integer> loops,
                List<Vector3f> scratchPolygon,
                List<Integer> scratchLoops,
                float neighborWeight,
                float selfWeight) {

            // Copy loops into scratch (they don't change during relax)
            scratchLoops.clear();
            scratchLoops.addAll(loops);

            for (int pass = 0; pass < 4; pass++) {
                // Resize scratch polygon to match current polygon
                scratchPolygon.clear();
                for (int i = 0; i < polygon.size(); i++) {
                    scratchPolygon.add(new Vector3f());
                }

                int globalOffset = 0;
                for (int loopIndex = 0; loopIndex < loops.size(); loopIndex++) {
                    int loopSize = loops.get(loopIndex);

                    for (int vertIndex = 0; vertIndex < loopSize; vertIndex++) {
                        int prevIndex = (vertIndex - 1 + loopSize) % loopSize;
                        int nextIndex = (vertIndex + 1) % loopSize;

                        Vector3f prev    = polygon.get(globalOffset + prevIndex);
                        Vector3f current = polygon.get(globalOffset + vertIndex);
                        Vector3f next    = polygon.get(globalOffset + nextIndex);

                        // neighborSum = prev + next
                        float neighborSumX = prev.x + next.x;
                        float neighborSumY = prev.y + next.y;

                        // relaxed = current + neighborSum * neighborWeight
                        // then     relaxed = relaxed * selfWeight
                        // This matches: out = (current + (prev+next)*halfRelax) * selfWeight

                        float relaxedX = current.x + (neighborSumX * 0.5f - current.x) * selfWeight;
                        float relaxedY = current.y + (neighborSumY * 0.5f - current.y) * selfWeight;

                        scratchPolygon.get(globalOffset + vertIndex).set(relaxedX, relaxedY, current.z);
                    }

                    globalOffset += loopSize;
                }

                // Swap polygon <-> scratchPolygon
                List<Vector3f> temp = new ArrayList<>(polygon);
                polygon.clear();
                polygon.addAll(scratchPolygon);
                scratchPolygon.clear();
                scratchPolygon.addAll(temp);
            }
        }

        public static boolean cleanup(
                List<Vector3f> polygon,
                List<Integer>  loops,
                Matrix4f       worldTransform) {

            final int originalSize = polygon.size();

            //transform all vertices into world space (temporary parallel buffer).
            List<Vector3f> worldVertices = transformVerticesToWorldSpace(polygon, worldTransform);

            //degenerate-vertex removal.
            //globalVertexBase tracks the start index of the current loop in both arrays.
            int globalVertexBase = 0;
            int loopIndex        = 0;

            int loopSize;
            outer:
            while (loopIndex < loops.size()) {

                int i = 0;
                while (true) {
                    loopSize = loops.get(loopIndex);

                    if (loopSize <= 2)
                    {
                        removeLoop(polygon, worldVertices, loops, loopIndex, globalVertexBase);
                        continue outer;
                    }

                    if (i >= loopSize)
                        break;

                    if (isDegenerateVertex(worldVertices, globalVertexBase, i, loopSize))
                        removeVertex(polygon, worldVertices, loops, loopIndex, globalVertexBase, i);

                    i++;
                }

                loopSize = loops.get(loopIndex);
                if (loopSize <= 2)
                {
                    removeLoop(polygon, worldVertices, loops, loopIndex, globalVertexBase);
                    continue;
                }

                globalVertexBase += loopSize;
                loopIndex++;
            }

            return polygon.size() != originalSize;
        }

        private static List<Vector3f> transformVerticesToWorldSpace(
                List<Vector3f> polygon,
                Matrix4f       worldTransform) {

            List<Vector3f> result = new ArrayList<>(polygon.size());
            Vector4f       tmp    = new Vector4f();

            for (Vector3f v : polygon) {
                worldTransform.transform(v.x, v.y, v.z, 1f, tmp);
                result.add(new Vector3f(tmp.x, tmp.y, tmp.z));
            }

            return result;
        }

        private static boolean isDegenerateVertex(List<Vector3f> worldVertices, int globalVertexBase, int i, int loopSize)
        {
            int prevIdx = (i + loopSize - 1) % loopSize;
            int nextIdx = (i + 1)            % loopSize;

            Vector3f a = worldVertices.get(globalVertexBase + prevIdx);
            Vector3f b = worldVertices.get(globalVertexBase + i);
            Vector3f c = worldVertices.get(globalVertexBase + nextIdx);

            // Edge vectors.
            float abX = b.x - a.x,  abY = b.y - a.y;
            float bcX = c.x - b.x,  bcY = c.y - b.y;
            float caX = a.x - c.x,  caY = a.y - c.y;

            // Squared edge lengths.
            float abSq = abX * abX + abY * abY;
            float bcSq = bcX * bcX + bcY * bcY;
            float caSq = caX * caX + caY * caY;

            // fsel-based max: max(caSq, bcSq) then max with abSq (0012f3dc–0012f3f4).
            float maxEdgeSq = (caSq >= bcSq) ? caSq : bcSq;
            if (abSq > maxEdgeSq) maxEdgeSq = abSq;

            // 2D cross product ab × bc.
            float cross = abX * bcY - abY * bcX;

            return cross * cross <= MIN_DIAMETER * MIN_DIAMETER * maxEdgeSq;
        }

        /**
         * Removes vertex at {@code localIndex} within the loop from both lists
         * and decrements the loop's vertex count.
         */
        private static void removeVertex(List<Vector3f> polygon, List<Vector3f> worldVertices, List<Integer> loops, int loopIndex, int globalVertexBase, int localIndex) {
            int globalIndex = globalVertexBase + localIndex;
            polygon.remove(globalIndex);
            worldVertices.remove(globalIndex);
            loops.set(loopIndex, loops.get(loopIndex) - 1);
        }

        /**
         * Drops the entire loop from all three lists.
         * Called whenever loopSize <= 2, whether detected at inner-loop entry.
         */
        private static void removeLoop(List<Vector3f> polygon, List<Vector3f> worldVertices, List<Integer> loops, int loopIndex, int globalVertexBase)
        {
            int loopSize = loops.get(loopIndex);
            for (int k = 0; k < loopSize; k++)
            {
                polygon.remove(globalVertexBase);
                worldVertices.remove(globalVertexBase);
            }
            loops.remove(loopIndex);
        }
    }
}