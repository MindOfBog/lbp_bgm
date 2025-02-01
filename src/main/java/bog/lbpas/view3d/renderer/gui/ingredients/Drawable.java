package bog.lbpas.view3d.renderer.gui.ingredients;

/**
 * @author Bog
 */
public abstract class Drawable {

    public boolean staticVAO = true;
    public boolean staticVBO = true;

    public boolean invert = false;

    public abstract int getType();

    public Drawable invert()
    {
        invert = !invert;
        return this;
    }
}
