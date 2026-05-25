package bog.lbpas.view3d.renderer.gui.ingredients;

import bog.lbpas.view3d.managers.WindowMan;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;

import java.awt.*;

public class QuadOutline extends Drawable{

    public Vector2i pos;
    public Vector2i size;
    public Color color;
    public int openSide = -1;

    public QuadOutline(Color color, int x, int y, int width, int height, WindowMan window) {

        this.pos = new Vector2i(x, window.getHeight() - y);
        this.size =  new Vector2i(width, height);
        this.color = color;
    }

    public QuadOutline(Color color, int x, int y, int width, int height, int openSide, WindowMan window)
    {
        this(color, x, y, width, height, window);
        this.openSide = openSide;
    }

    @Override
    public Type getType() {
        return Type.QUAD_OUTLINE;
    }
}
