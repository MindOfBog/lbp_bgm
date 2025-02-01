package bog.lbpas.view3d.renderer.gui.ingredients;

import bog.lbpas.view3d.managers.assetLoading.ObjectLoader;
import bog.lbpas.view3d.core.Model;
import bog.lbpas.view3d.managers.WindowMan;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.awt.*;

/**
 * @author Bog
 */
public class LineStrip extends Drawable{

    public Model model;
    public Vector4f color;
    public boolean smooth = false;
    public Vector2f pos;

    public LineStrip(Vector2f pos, Vector2f[] verts, ObjectLoader loader, WindowMan window, boolean smooth) {
        this.color = new Vector4f(1, 1, 1, 1);
        this.smooth = smooth;
        this.model = processVerts(verts, loader, window);
        this.staticVAO = false;
        this.staticVBO = false;
        this.pos = pos;
    }

    public LineStrip(Vector2f pos, Vector2f[] verts, Color color, ObjectLoader loader, WindowMan window, boolean smooth) {
        this.color = new Vector4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
        this.smooth = smooth;
        this.model = processVerts(verts, loader, window);
        this.staticVAO = false;
        this.staticVBO = false;
        this.pos = pos;
    }

    public LineStrip(Vector2f pos, Model lineStrip, boolean smooth) {
        this.color = new Vector4f(1, 1, 1, 1);
        this.smooth = smooth;
        this.model = lineStrip;
        this.pos = pos;
    }

    public LineStrip(Vector2f pos, Model lineStrip, Color color, boolean smooth) {
        this.color = new Vector4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
        this.smooth = smooth;
        this.model = lineStrip;
        this.pos = pos;
    }

    public static LineStrip lineRectangle(Vector2f pos, Vector2f size, Color color, ObjectLoader loader, WindowMan window, boolean smooth)
    {
        return new LineStrip(pos, getRectangle(size), color, loader, window, smooth);
    }
    public static LineStrip lineRectangle(Vector2f pos, Vector2f size, ObjectLoader loader, WindowMan window, boolean smooth)
    {
        return new LineStrip(pos, getRectangle(size), loader, window, smooth);
    }

    public static LineStrip lineRectangle(Vector2f pos, Vector2f size, Color color, ObjectLoader loader, WindowMan window, boolean smooth, int openSide)
    {
        return new LineStrip(pos, getRectangle(size, openSide), color, loader, window, smooth);
    }

    public static LineStrip lineRectangle(Vector2f pos, Vector2f size, ObjectLoader loader, WindowMan window, boolean smooth, int openSide)
    {
        return new LineStrip(pos, getRectangle(size, openSide), loader, window, smooth);
    }

    public static int UP = 0;
    public static int DOWN = 1;
    public static int LEFT = 2;
    public static int RIGHT = 3;

    public static Model processVerts(Vector2f[] positions, ObjectLoader loader, WindowMan window)
    {
        if(window != null && !window.isMinimized) {

            float[] processed = new float[positions.length * 2];

            for(int i = 0; i < positions.length; i++)
            {
                Vector2f pos = positions[i];
                processed[i * 2] = pos.x / (window.width / 2f) - 1 + 1 / window.width;
                processed[i * 2 + 1] = -pos.y / (window.height / 2f) + 1 - 1 / window.height;
            }

            return loader.loadModel(processed);
        }
        return null;
    }

    public static Vector2f[] getRectangle(Vector2f size)
    {
        float x1 = 0.5f;
        float y1 = 0.5f;

        float x2 = (float) Math.round(size == null ? 1 : size.x) - 0.5f;
        float y2 = (float) Math.round(size == null ? 1 : size.y) - 0.5f;
        return new Vector2f[]{new Vector2f(x1, y1), new Vector2f(x1, y2), new Vector2f(x2, y2), new Vector2f(x2, y1), new Vector2f(x1, y1)};
    }

    public static Vector2f[] getRectangle (Vector2f size, int openSide)
    {
        float x1 = 0.5f;
        float y1 = 0.5f;

        float x2 = (float) Math.round(size.x) - 0.5f;
        float y2 = (float) Math.round(size.y) - 0.5f;

        switch (openSide)
        {
            default:
            case 0:
                return new Vector2f[]{new Vector2f(x1, y1), new Vector2f(x1, y2), new Vector2f(x2, y2), new Vector2f(x2, y1 - 1)};
            case 1:
                return new Vector2f[]{new Vector2f(x2, y2), new Vector2f(x2, y1), new Vector2f(x1, y1), new Vector2f(x1, y2)};
            case 2:
                return new Vector2f[]{new Vector2f(x1, y2), new Vector2f(x2, y2), new Vector2f(x2, y1), new Vector2f(x1, y1)};
            case 3:
                return new Vector2f[]{new Vector2f(x2, y1), new Vector2f(x1, y1), new Vector2f(x1, y2), new Vector2f(x2, y2)};
        }
    }

    @Override
    public int getType() {
        return 3;
    }
}
