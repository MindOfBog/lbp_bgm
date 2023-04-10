package bog.bgmaker.view3d.renderer.gui.ingredients;

import bog.bgmaker.view3d.ObjectLoader;
import bog.bgmaker.view3d.core.Model;
import org.joml.Vector2f;

import java.awt.*;

/**
 * @author Bog
 */
public class TriFan extends Drawable{

    public Model model;
    public boolean hasTexCoords;
    public int texture = -1;
    public Vector2f pos, scale;
    ObjectLoader loader;
    public boolean staticTexture = false;
    public Color color = null;
    public boolean smoothstep = false;

    @Override
    public int getType() {
        return 1;
    }
}
