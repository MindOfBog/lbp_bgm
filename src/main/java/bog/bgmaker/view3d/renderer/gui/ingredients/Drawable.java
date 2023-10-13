package bog.bgmaker.view3d.renderer.gui.ingredients;

/**
 * @author Bog
 */
public abstract class Drawable {

    public boolean staticVAO = true;
    public boolean staticVBO = true;

    public abstract int getType();

}
