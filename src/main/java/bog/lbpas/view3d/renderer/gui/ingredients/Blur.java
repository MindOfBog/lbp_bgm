package bog.lbpas.view3d.renderer.gui.ingredients;

import bog.lbpas.view3d.renderer.gui.GuiRenderer;
import bog.lbpas.view3d.utils.Utils;

/**
 * @author Bog
 */
public class Blur extends Drawable{


    @Override
    public Type getType() {
        return Type.BLUR;
    }

    public int radius;
    public boolean start;

    public boolean gaussian;
    public float[] gaussKernel;

    public Blur(int radius, boolean start)
    {
        this.start = start;
        this.radius = radius;
        this.gaussian = false;
    }

    public Blur(int radius, float sigma, boolean start) {
        this.radius = radius;
        this.gaussKernel = Utils.gaussianKernel(sigma, radius * 2 + 1);
        this.start = start;
        this.gaussian = true;
    }

    public Blur(int radius, float[] gaussKernel, boolean start) {
        this.radius = radius;
        this.gaussKernel = gaussKernel;
        this.start = start;
        this.gaussian = true;
    }
}
