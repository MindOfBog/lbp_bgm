package bog.lbpas.view3d.renderer.gui.ingredients;

import org.joml.Vector2i;

/**
 * @author Bog
 */
public class Scissor extends Drawable {

    public boolean start;
    public boolean escape = false;
    public Vector2i pos;
    public Vector2i size;

    public static Scissor start(Vector2i pos, Vector2i size)
    {
        Scissor scissor = new Scissor();
        scissor.start = true;

        scissor.pos = pos;
        scissor.size = size;

        return scissor;
    }

    public static Scissor end()
    {
        Scissor scissor = new Scissor();
        scissor.start = false;

        return scissor;
    }

    public static Scissor startEscape()
    {
        Scissor scissor = new Scissor();
        scissor.start = true;
        scissor.escape = true;
        return scissor;
    }

    public static Scissor endEscape()
    {
        Scissor scissor = new Scissor();
        scissor.start = false;
        scissor.escape = true;
        return scissor;
    }

    @Override
    public int getType() {
        return 4;
    }
}
