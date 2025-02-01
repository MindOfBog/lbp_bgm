package bog.lbpas.view3d.renderer.gui.ingredients;

import bog.lbpas.view3d.managers.assetLoading.ObjectLoader;
import bog.lbpas.view3d.managers.WindowMan;
import org.joml.Math;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.awt.*;

/**
 * @author Bog
 */
public class Circle extends Triangle{

    public Vector4f circle;

    private Circle(ObjectLoader loader, WindowMan window, Color color, Vector2f p1, Vector2f p2, Vector2f p3, Vector4f circle) {
        super(loader, window, color, p1, p2, p3);
        this.circle = circle;
        this.texture = -1;
    }

    public static Circle get(ObjectLoader loader, WindowMan window, Color color, Vector2f center, float radius, boolean outline) {

        float lineHalfWidth = (float) Math.sqrt(java.lang.Math.pow((radius * 4f), 2d) - java.lang.Math.pow(radius * 2f, 2d));

        Vector2f vertex1 = new Vector2f(center.x - lineHalfWidth, center.y + radius * 2f);
        Vector2f vertex2 = new Vector2f(center.x + lineHalfWidth, center.y + radius * 2f);
        Vector2f vertex3 = new Vector2f(center.x, center.y - (4f * radius));

        return new Circle(loader, window, color, vertex1, vertex2, vertex3, new Vector4f(center.x, window.height - center.y, radius, outline ? 1 : -1));
    }

    @Override
    public int getType() {
        return 5;
    }
}
