package bog.bgmaker.view3d.renderer.gui.ingredients;

/**
 * @author Bog
 */
public class Blur extends Drawable{


    @Override
    public int getType() {
        return 6;
    }

    public float amount;
    public boolean start;

    public Blur(float amount, boolean start) {
        this.amount = amount;
        this.start = start;
    }
}
