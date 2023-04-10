package bog.bgmaker.view3d.core;

/**
 * @author Bog
 */
public class Model {

    public int vao;
    public int[] vbos;
    public int vertexCount;

    public Material material;
    public int[] indicesArr;

    public boolean noRender = false;

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

//    public Vector3f rayIntersectModel(Vector3f ray, Vector3f origin)
//    {
//        for(Triangle triangle : triangles)
//        {
//            Vector3f v0v1 = triangle.p2.sub(triangle.p1, new Vector3f());
//            Vector3f v0v2 = triangle.p3.sub(triangle.p1, new Vector3f());
//            Vector3f N = v0v1.cross(v0v2, new Vector3f());
//            float area2 = N.length();
//
//            triangle = new Triangle(triangle);
//
//            float D = -(N.x * triangle.p1.x + N.y * triangle.p1.y + N.z * triangle.p1.z);
//            float t = (-(N.dot(origin) + D)) / N.dot(ray);
//            Vector3f P = origin.add(ray.mul(t, new Vector3f()), new Vector3f());
//
//            Vector3f edge0 = triangle.p2.sub(triangle.p1, new Vector3f());
//            Vector3f edge1 = triangle.p3.sub(triangle.p2, new Vector3f());
//            Vector3f edge2 = triangle.p1.sub(triangle.p3, new Vector3f());
//            Vector3f C0 = P.sub(triangle.p1, new Vector3f());
//            Vector3f C1 = P.sub(triangle.p2, new Vector3f());
//            Vector3f C2 = P.sub(triangle.p3, new Vector3f());
//            if (N.dot(edge0.cross(C0, new Vector3f())) > 0 &&
//                    N.dot(edge1.cross(C1, new Vector3f())) > 0 &&
//                    N.dot(edge2.cross(C2, new Vector3f())) > 0) return P;
//        }
//        return null;
//    }
}
