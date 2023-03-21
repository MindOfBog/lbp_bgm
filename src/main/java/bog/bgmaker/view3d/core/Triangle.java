package bog.bgmaker.view3d.core;

import org.joml.Vector3f;

/**
 * @author Bog
 */
public class Triangle {

    public Vector3f p1;
    public Vector3f p2;
    public Vector3f p3;

    public Triangle(Vector3f p1, Vector3f p2, Vector3f p3) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
    }

    public Triangle() {
        this.p1 = new Vector3f(0f, 0f, 0f);
        this.p2 = new Vector3f(0f, 0f, 0f);
        this.p3 = new Vector3f(0f, 0f, 0f);
    }

    public Triangle(Triangle triangle) {
        this.p1 = new Vector3f(triangle.p1);
        this.p2 = new Vector3f(triangle.p2);
        this.p3 = new Vector3f(triangle.p3);
    }

    public void mul(Vector3f vec)
    {
        p1 = p1.mul(vec);
        p2 = p2.mul(vec);
        p3 = p3.mul(vec);
    }

    public void add(Vector3f vec)
    {
        p1 = p1.add(vec);
        p2 = p2.add(vec);
        p3 = p3.add(vec);
    }

    public void rotate(Vector3f vec)
    {
        p1 = p1.rotateX((float) Math.toRadians(vec.x));
        p1 = p1.rotateY((float) Math.toRadians(vec.y));
        p1 = p1.rotateZ((float) Math.toRadians(vec.z));

        p2 = p2.rotateX((float) Math.toRadians(vec.x));
        p2 = p2.rotateY((float) Math.toRadians(vec.y));
        p2 = p2.rotateZ((float) Math.toRadians(vec.z));

        p3 = p3.rotateX((float) Math.toRadians(vec.x));
        p3 = p3.rotateY((float) Math.toRadians(vec.y));
        p3 = p3.rotateZ((float) Math.toRadians(vec.z));
    }
}
