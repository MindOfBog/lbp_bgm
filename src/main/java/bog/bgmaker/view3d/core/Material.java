package bog.bgmaker.view3d.core;

import bog.bgmaker.view3d.utils.Const;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.awt.*;

/**
 * @author Bog
 */
public class Material {

    public Vector4f ambientColor, diffuseColor, specularColor, overlayColor;
    public float reflectance;
    public Texture texture;
    public boolean disableCulling;

    public Material()
    {
        this.ambientColor = Const.DEFAULT_COLOR;
        this.diffuseColor = Const.DEFAULT_COLOR;
        this.specularColor = Const.DEFAULT_COLOR;
        this.texture = null;
        this.disableCulling = false;
        this.reflectance = 0f;
    }

    public Material(Vector4f color, float reflectance)
    {
        this(color, color, color, reflectance, null);
    }

    public Material(Color color, float reflectance)
    {
        this(new Vector4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f), reflectance, null);
    }

    public Material(Vector4f color, float reflectance, Texture texture)
    {
        this(color, color, color, reflectance, texture);
    }

    public Material(Color color, float reflectance, Texture texture)
    {
        this(new Vector4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f), reflectance, texture);
    }

    public Material(Texture texture)
    {
        this(Const.DEFAULT_COLOR, Const.DEFAULT_COLOR, Const.DEFAULT_COLOR, 0f, texture);
    }

    public Material(Vector4f ambientColor, Vector4f diffuseColor, Vector4f specularColor, float reflectance, Texture texture)
    {
        this.ambientColor = ambientColor;
        this.diffuseColor = diffuseColor;
        this.specularColor = specularColor;
        this.reflectance = reflectance;
        this.texture = texture;
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
        return texture != null;
    }
}
