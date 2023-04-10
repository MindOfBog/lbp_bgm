package bog.bgmaker.view3d.renderer.gui.ingredients;

import bog.bgmaker.view3d.ObjectLoader;
import bog.bgmaker.view3d.core.Model;
import bog.bgmaker.view3d.managers.WindowMan;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * @author Bog
 */
public class LineStrip extends Drawable{

    public Model model;
    ObjectLoader loader;
    public Vector4f color;
    public boolean smooth = false;

    public LineStrip(Vector2f[] positions, ObjectLoader loader, WindowMan window, boolean smooth, boolean staticVs) {
        this.color = new Vector4f(1, 1, 1, 1);
        this.loader = loader;
        this.smooth = smooth;
        this.staticVAO = staticVs;
        this.staticVBO = staticVs;

        if(window != null && !window.isMinimized) {

            float[] processed = new float[positions.length * 2];

            for(int i = 0; i < positions.length; i++)
            {
                Vector2f pos = positions[i];
                processed[i * 2] = pos.x / (window.width / 2f) - 1 + 1 / window.width;
                processed[i * 2 + 1] = -pos.y / (window.height / 2f) + 1 - 1 / window.height;
            }

            this.model = loader.loadModel(processed);
        }
    }

    public LineStrip(Vector2f[] positions, Color color, ObjectLoader loader, WindowMan window, boolean smooth, boolean staticVs) {
        this.color = new Vector4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
        this.loader = loader;
        this.smooth = smooth;
        this.staticVAO = staticVs;
        this.staticVBO = staticVs;

        if(window != null && !window.isMinimized) {

            float[] processed = new float[positions.length * 2];

            for(int i = 0; i < positions.length; i++)
            {
                Vector2f pos = positions[i];
                processed[i * 2] = pos.x / (window.width / 2f) - 1 + 1 / window.width;
                processed[i * 2 + 1] = -pos.y / (window.height / 2f) + 1 - 1 / window.height;
            }

            this.model = loader.loadModel(processed);
        }
    }

    public static LineStrip lineRectangle(Vector2f pos, Vector2f size, Color color, ObjectLoader loader, WindowMan window, boolean smooth, boolean staticVs)
    {
        float x1 = (float) Math.round(pos.x) + 0.5f;
        float y1 = (float) Math.round(pos.y) + 0.5f;

        float x2 = (float) Math.round(pos.x + size.x) - 0.5f;
        float y2 = (float) Math.round(pos.y + size.y) - 0.5f;

        return new LineStrip(new Vector2f[]{new Vector2f(x1, y1), new Vector2f(x1, y2), new Vector2f(x2, y2), new Vector2f(x2, y1), new Vector2f(x1, y1)}, color, loader, window, smooth, staticVs);
    }

    public static LineStrip lineRectangle(Vector2f pos, Vector2f size, ObjectLoader loader, WindowMan window, boolean smooth, boolean staticVs)
    {
        float x1 = (float) Math.round(pos.x) + 0.5f;
        float y1 = (float) Math.round(pos.y) + 0.5f;

        float x2 = (float) Math.round(pos.x + size.x) - 0.5f;
        float y2 = (float) Math.round(pos.y + size.y) - 0.5f;

        return new LineStrip(new Vector2f[]{new Vector2f(x1, y1), new Vector2f(x1, y2), new Vector2f(x2, y2), new Vector2f(x2, y1), new Vector2f(x1, y1)}, loader, window, smooth, staticVs);
    }

    @Override
    public int getType() {
        return 3;
    }
}
