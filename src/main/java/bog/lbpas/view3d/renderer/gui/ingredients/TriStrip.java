package bog.lbpas.view3d.renderer.gui.ingredients;

import bog.lbpas.view3d.core.Model;
import org.joml.Vector2f;

import java.awt.*;

/**
 * @author Bog
 */
public class TriStrip extends Drawable{

    public Model model;
    public boolean hasTexCoords;
    public int texture = -1;
    public Vector2f pos, scale;
    public boolean staticTexture = false;
    public Color color = null;
    public boolean smoothstep = false;

    public float smoothstepWidth;
    public float smoothstepEdge;

    @Override
    public Type getType() {
        return Type.TRI_STRIP;
    }
}
