package bog.bgmaker.view3d.renderer.gui.ingredients;

import bog.bgmaker.view3d.ObjectLoader;
import bog.bgmaker.view3d.core.Model;
import bog.bgmaker.view3d.managers.WindowMan;
import org.joml.Vector2f;

import java.awt.*;

/**
 * @author Bog
 */
public class Triangle extends TriStrip{

    public Triangle(ObjectLoader loader, WindowMan window, Color color, Vector2f p1, Vector2f p2, Vector2f p3) {
        this.pos = new Vector2f(0, 0);
        this.scale = new Vector2f(window.width, window.height);

        this.model = getTriangle(loader, window, p1, p2, p3);
        this.hasTexCoords = false;
        this.color = color;
        this.staticVAO = false;
        this.staticVBO = false;
    }

    public Triangle(Model triangle, WindowMan window, Color color) {
        this.pos = new Vector2f(0, 0);
        this.scale = new Vector2f(window.width, window.height);

        this.model = triangle;
        this.hasTexCoords = false;
        this.color = color;
        this.staticVAO = false;
        this.staticVBO = false;
    }

    public static Model getTriangle(ObjectLoader loader, WindowMan window, Vector2f p1, Vector2f p2, Vector2f p3)
    {
        return loader.loadModel(new float[]{(p1.x / (window.width / 2f)) - 1f, 2f * (1f - (p1.y / window.height)) - 1f,
                (p2.x / (window.width / 2f)) - 1f, 2f * (1f - (p2.y / window.height)) - 1f,
                (p3.x / (window.width / 2f)) - 1f, 2f * (1f - (p3.y / window.height)) - 1f});
    }

}
