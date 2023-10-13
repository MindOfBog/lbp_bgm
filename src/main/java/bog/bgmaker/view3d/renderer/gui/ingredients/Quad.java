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

//    public Quad(ObjectLoader loader, String path, Vector2f pos, Vector2f scale) {
//        this.loader = loader;
//        try {
//            this.texture = loader.loadResourceTexture(path, GL11.GL_LINEAR_MIPMAP_LINEAR, GL11.GL_LINEAR);
//        } catch (Exception e) {e.printStackTrace();}
//        this.pos = pos;
//        this.scale = scale;
//
//        this.model = GuiRenderer.defaultQuad;
//        this.hasTexCoords = false;
//    }

//    public Quad(ObjectLoader loader, BufferedImage image, Vector2f pos, Vector2f scale) {
//        this.loader = loader;
//        try {
//            this.texture = loader.loadTexture(image, GL11.GL_LINEAR_MIPMAP_LINEAR, GL11.GL_LINEAR);
//        } catch (Exception e) {e.printStackTrace();}
//        this.pos = pos;
//        this.scale = scale;
//
//        this.model = GuiRenderer.defaultQuad;
//        this.hasTexCoords = false;
//    }

    public Quad(int ID, Vector2f pos, Vector2f scale) {
        this.texture = ID;
        this.pos = pos;
        this.scale = scale;
        this.model = GuiRenderer.defaultQuad;
        this.hasTexCoords = false;
    }
    public Quad(int ID, Vector2f pos, Vector2f scale, Color color) {
        this.texture = ID;
        this.pos = pos;
        this.scale = scale;
        this.model = GuiRenderer.defaultQuad;
        this.hasTexCoords = false;
        this.color = color;
    }
    public Quad(Model quad, int ID, Vector2f pos, Vector2f scale) {
        this.texture = ID;
        this.pos = pos;
        this.scale = scale;
        this.model = quad;
        this.hasTexCoords = true;
    }
    public Quad(Model quad, int ID, Vector2f pos, Vector2f scale, Color color) {
        this.texture = ID;
        this.pos = pos;
        this.scale = scale;
        this.model = quad;
        this.hasTexCoords = true;
        this.color = color;
    }
    public Quad(Color color, Vector2f pos, Vector2f scale) {
        this.pos = pos;
        this.scale = scale;
        this.model = GuiRenderer.defaultQuad;
        this.hasTexCoords = false;
        this.color = color;
    }
    public Quad(Vector4f color, Vector2f pos, Vector2f scale) {
        this.pos = pos;
        this.scale = scale;
        this.model = GuiRenderer.defaultQuad;
        this.hasTexCoords = false;
        this.color = new Color(color.x, color.y, color.z, color.w);
    }
    public Quad(Quad original)
    {
        this.hasTexCoords = original.hasTexCoords;
        this.texture = original.texture;
        this.pos = new Vector2f(original.pos.x, original.pos.y);
        this.scale = new Vector2f(original.scale.x, original.scale.y);
        this.staticTexture = original.staticTexture;
        this.model = original.model;
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
