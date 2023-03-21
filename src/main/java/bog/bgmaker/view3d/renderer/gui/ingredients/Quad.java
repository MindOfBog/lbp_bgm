package bog.bgmaker.view3d.renderer.gui.ingredients;

import bog.bgmaker.view3d.ObjectLoader;
import bog.bgmaker.view3d.core.Model;
import bog.bgmaker.view3d.renderer.gui.GuiRenderer;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author Bog
 */
public class Quad extends TriStrip {

    public Quad(ObjectLoader loader, String path, Vector2f pos, Vector2f scale, boolean staticVs) {
        this.loader = loader;
        try {
            this.texture = loader.loadResourceTexture(path);
        } catch (Exception e) {e.printStackTrace();}
        this.pos = pos;
        this.scale = scale;

        this.model = GuiRenderer.defaultQuad(loader);
        this.hasTexCoords = false;

        this.staticVAO = staticVs;
        this.staticVBO = staticVs;
    }

    public Quad(ObjectLoader loader, BufferedImage image, Vector2f pos, Vector2f scale, boolean staticVs) {
        this.loader = loader;
        try {
            this.texture = loader.loadTexture(image);
        } catch (Exception e) {e.printStackTrace();}
        this.pos = pos;
        this.scale = scale;

        this.model = GuiRenderer.defaultQuad(loader);
        this.hasTexCoords = false;

        this.staticVAO = staticVs;
        this.staticVBO = staticVs;
    }

    public Quad(ObjectLoader loader, int ID, Vector2f pos, Vector2f scale, boolean staticVs) {
        this.loader = loader;
        this.texture = ID;
        this.pos = pos;
        this.scale = scale;

        this.model = GuiRenderer.defaultQuad(loader);
        this.hasTexCoords = false;

        this.staticVAO = staticVs;
        this.staticVBO = staticVs;
    }

    public Quad(ObjectLoader loader, int ID, Vector2f pos, Vector2f scale, Model m, boolean staticVs) {
        this.loader = loader;
        this.texture = ID;
        this.pos = pos;
        this.scale = scale;

        this.model = m;
        this.hasTexCoords = true;

        this.staticVAO = staticVs;
        this.staticVBO = staticVs;
    }

    public Quad(ObjectLoader loader, int ID, Vector2f pos, Vector2f scale, Model model, Color color, boolean staticVs) {
        this.loader = loader;
        this.texture = ID;
        this.pos = pos;
        this.scale = scale;

        this.model = model;
        this.hasTexCoords = true;
        this.color = color;

        this.staticVAO = staticVs;
        this.staticVBO = staticVs;
    }
    public Quad(ObjectLoader loader, Color color, Vector2f pos, Vector2f scale, boolean staticVs) {
        this.loader = loader;
        this.texture = -1;
        this.pos = pos;
        this.scale = scale;

        this.model = GuiRenderer.defaultQuad(loader);
        this.hasTexCoords = false;
        this.color = color;

        this.staticVAO = staticVs;
        this.staticVBO = staticVs;
    }
    public Quad(ObjectLoader loader, Vector4f color, Vector2f pos, Vector2f scale, boolean staticVs) {
        this.loader = loader;
        this.texture = -1;
        this.pos = pos;
        this.scale = scale;

        this.model = GuiRenderer.defaultQuad(loader);
        this.hasTexCoords = false;
        this.color = new Color(color.x, color.y, color.z, color.w);

        this.staticVAO = staticVs;
        this.staticVBO = staticVs;
    }

    public Quad(ObjectLoader loader, Vector4f color, Vector2f pos, Vector2f scale, Model model, boolean staticVs)
    {
        this.loader = loader;
        this.texture = -1;
        this.pos = pos;
        this.scale = scale;

        this.model = model;
        this.hasTexCoords = false;
        this.color = new Color(color.x, color.y, color.z, color.w);

        this.staticVAO = staticVs;
        this.staticVBO = staticVs;
    }

    public Quad(ObjectLoader loader, Color color, Vector2f pos, Vector2f scale, Model model, boolean staticVs)
    {
        this.loader = loader;
        this.texture = -1;
        this.pos = pos;
        this.scale = scale;

        this.model = model;
        this.hasTexCoords = false;
        this.color = color;

        this.staticVAO = staticVs;
        this.staticVBO = staticVs;
    }

    public Quad staticTexture()
    {
        this.staticTexture = true;
        return this;
    }

    public Quad smoothstep()
    {
        this.smoothstep = true;
        return this;
    }
}
