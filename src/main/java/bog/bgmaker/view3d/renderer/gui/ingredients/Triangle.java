package bog.bgmaker.view3d.renderer.gui.ingredients;

import bog.bgmaker.view3d.ObjectLoader;
import bog.bgmaker.view3d.managers.WindowMan;
import org.joml.Vector2f;

import java.awt.*;

/**
 * @author Bog
 */
public class Triangle extends TriStrip{

    public Triangle(ObjectLoader loader, WindowMan window, Color color, Vector2f p1, Vector2f p2, Vector2f p3, boolean staticVs) {
        this.loader = loader;
        this.texture = -1;
        this.pos = new Vector2f(0, 0);
        this.scale = new Vector2f(window.width, window.height);

        this.model = loader.loadModel(new float[]{(p1.x / (window.width / 2f)) - 1f, 2f * (1f - (p1.y / window.height)) - 1f,
                (p2.x / (window.width / 2f)) - 1f, 2f * (1f - (p2.y / window.height)) - 1f,
                (p3.x / (window.width / 2f)) - 1f, 2f * (1f - (p3.y / window.height)) - 1f});
        this.hasTexCoords = false;
        this.color = color;

        this.staticVAO = staticVs;
        this.staticVBO = staticVs;
    }

}
