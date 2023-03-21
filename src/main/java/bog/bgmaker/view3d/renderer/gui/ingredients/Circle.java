package bog.bgmaker.view3d.renderer.gui.ingredients;

import bog.bgmaker.view3d.ObjectLoader;
import bog.bgmaker.view3d.managers.WindowMan;
import org.joml.Vector2f;

import java.awt.*;

/**
 * @author Bog
 */
public class Circle extends TriFan{

    public Circle(ObjectLoader loader, WindowMan window, Color color, Vector2f center, float radius, int triangleAmount, boolean staticVs) {
        this.loader = loader;
        this.texture = -1;
        this.pos = new Vector2f(0, 0);
        this.scale = new Vector2f(window.width, window.height);

        float[] verts = new float[(triangleAmount + 1) * 2 + 2];

        verts[0] = (center.x / (window.width / 2f)) - 1f;
        verts[1] = 2f * (1f - (center.y / window.height)) - 1f;

        for(int i = 1; i <= triangleAmount + 1;i++) {
            verts[i * 2] = ((center.x + (radius * (float) Math.cos(i * (Math.PI * 2d) / triangleAmount))) / (window.width / 2f)) - 1f;
            verts[i * 2 + 1] = 2f * (1f - ((center.y + (radius * (float) Math.sin(i * (Math.PI * 2d) / triangleAmount))) / window.height)) - 1f;
        }

        this.model = loader.loadModel(verts);
        this.hasTexCoords = false;
        this.color = color;

        this.staticVAO = staticVs;
        this.staticVBO = staticVs;
    }

}
