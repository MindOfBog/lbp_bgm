package bog.bgmaker.view3d.renderer.gui.ingredients;

import bog.bgmaker.view3d.ObjectLoader;
import bog.bgmaker.view3d.core.Model;
import bog.bgmaker.view3d.renderer.gui.GuiRenderer;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author Bog
 */
public class Quad extends TriStrip {

    public Quad(ObjectLoader loader, String path, Vector2f pos, Vector2f scale) {
        this.loader = loader;
        try {
            this.texture = loader.loadResourceTexture(path, GL11.GL_LINEAR_MIPMAP_LINEAR, GL11.GL_LINEAR);
        } catch (Exception e) {e.printStackTrace();}
        this.pos = pos;
        this.scale = scale;

        this.model = GuiRenderer.defaultQuad;
        this.hasTexCoords = false;

        this.staticVAO = true;
        this.staticVBO = true;
    }

    public Quad(ObjectLoader loader, BufferedImage image, Vector2f pos, Vector2f scale) {
        this.loader = loader;
        try {
            this.texture = loader.loadTexture(image, GL11.GL_LINEAR_MIPMAP_LINEAR, GL11.GL_LINEAR);
        } catch (Exception e) {e.printStackTrace();}
        this.pos = pos;
        this.scale = scale;

        this.model = GuiRenderer.defaultQuad;
        this.hasTexCoords = false;

        this.staticVAO = true;
        this.staticVBO = true;
    }

    public Quad(ObjectLoader loader, int ID, Vector2f pos, Vector2f scale) {
        this.loader = loader;
        this.texture = ID;
        this.pos = pos;
        this.scale = scale;

        this.model = GuiRenderer.defaultQuad;
        this.hasTexCoords = false;

        this.staticVAO = true;
        this.staticVBO = true;
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
    public Quad(ObjectLoader loader, Color color, Vector2f pos, Vector2f scale) {
        this.loader = loader;
        this.texture = -1;
        this.pos = pos;
        this.scale = scale;

        this.model = GuiRenderer.defaultQuad;
        this.hasTexCoords = false;
        this.color = color;

        this.staticVAO = true;
        this.staticVBO = true;
    }
    public Quad(ObjectLoader loader, Vector4f color, Vector2f pos, Vector2f scale) {
        this.loader = loader;
        this.texture = -1;
        this.pos = pos;
        this.scale = scale;

        this.model = GuiRenderer.defaultQuad;
        this.hasTexCoords = false;
        this.color = new Color(color.x, color.y, color.z, color.w);

        this.staticVAO = true;
        this.staticVBO = true;
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

    public Quad(Quad original)
    {
        this.model = original.model;
        this.hasTexCoords = original.hasTexCoords;
        this.texture = original.texture;
        this.pos = new Vector2f(original.pos.x, original.pos.y);
        this.scale = new Vector2f(original.scale.x, original.scale.y);
        this.loader = original.loader;
        this.staticTexture = original.staticTexture;
        this.color = new Color(original.color.getRed(), original.color.getGreen(), original.color.getBlue(), original.color.getAlpha());
        this.smoothstep = original.smoothstep;
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
