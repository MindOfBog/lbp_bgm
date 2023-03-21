package bog.bgmaker.view3d.renderer.gui.ingredients;

import bog.bgmaker.view3d.ObjectLoader;
import bog.bgmaker.view3d.core.Model;
import bog.bgmaker.view3d.managers.WindowMan;
import org.joml.Vector2i;
import org.joml.Vector4f;

import java.awt.*;

/**
 * @author Bog
 */
public class Line extends Drawable{

    public Model model;
    ObjectLoader loader;
    public Vector4f color;

    public Line(Vector2i pos1, Vector2i pos2, ObjectLoader loader, WindowMan window, boolean staticVs) {
        this.color = new Vector4f(1, 1, 1, 1);
        this.loader = loader;

        if(!window.isMinimized) {
            float x1 = pos1.x / (window.width / 2f) - 1 + 1 / window.width;
            float y1 = -pos1.y / (window.height / 2f) + 1 - 1 / window.height;

            float x2 = pos2.x / (window.width / 2f) - 1 + 1 / window.width;
            float y2 = -pos2.y / (window.height / 2f) + 1 - 1 / window.height;

            this.model = loader.loadModel(new float[]{x1, y1, x2, y2});

            this.staticVAO = staticVs;
            this.staticVBO = staticVs;
        }
    }

    public Line(Vector2i pos1, Vector2i pos2, Color color, ObjectLoader loader, WindowMan window, boolean staticVs) {
        this.color = new Vector4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
        this.loader = loader;

        float x1 = pos1.x / (window.width/2f) - 1 + 1 / window.width;
        float y1 = -pos1.y / (window.height/2f) + 1 - 1 / window.height;

        float x2 = pos2.x / (window.width/2f) - 1 + 1 / window.width;
        float y2 = -pos2.y / (window.height/2f) + 1 - 1 / window.height;

        this.model = loader.loadModel(new float[]{x1, y1, x2, y2});

        this.staticVAO = staticVs;
        this.staticVBO = staticVs;
    }
}
