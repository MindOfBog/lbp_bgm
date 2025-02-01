package bog.lbpas.view3d.core;

import bog.lbpas.view3d.utils.Config;
import org.joml.Vector2i;
import org.joml.Vector4f;

import java.awt.*;

/**
 * @author Bog
 */
public class Material {

    public Vector4f ambientColor = new Vector4f(Config.DEFAULT_COLOR),
            diffuseColor = new Vector4f(Config.DEFAULT_COLOR),
            specularColor = new Vector4f(Config.DEFAULT_COLOR),
            overlayColor;
    public float reflectance;

    public Texture[] textures;
    public int texCount;
    public Vector2i gmatMAP[];
    public int gmatCount;
    public boolean disableCulling;

    public Material()
    {
        this.disableCulling = false;
        this.reflectance = 0f;
    }

    public Material(Vector4f color, float reflectance)
    {
        this(color, color, color, reflectance);
    }

    public Material(Texture[] textures)
    {
        this.disableCulling = false;
        this.reflectance = 0f;
        this.textures = textures;
        this.texCount = textures.length;
    }

    public Material(Color color, float reflectance)
    {
        this(new Vector4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f), reflectance);
    }

    public Material(Vector4f ambientColor, Vector4f diffuseColor, Vector4f specularColor, float reflectance)
    {
        this.ambientColor = ambientColor;
        this.diffuseColor = diffuseColor;
        this.specularColor = specularColor;
        this.reflectance = reflectance;
        this.disableCulling = false;
    }

    public void setColor(Vector4f color)
    {
        this.diffuseColor = color;
        this.ambientColor = color;
        this.specularColor = color;
    }

    public void setColor(Color color)
    {
        this.setColor(new Vector4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f));
    }

    public void setOverlayColor(Color color)
    {
        this.overlayColor = new Vector4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
    }

    public Material disableCulling(boolean disableCulling)
    {
        this.disableCulling = disableCulling;
        return this;
    }
    public boolean hasTexture()
    {
        return textures != null;
    }

}
