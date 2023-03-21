package bog.bgmaker.view3d.core;

import bog.bgmaker.view3d.utils.Const;
import org.joml.Vector4f;

/**
 * @author Bog
 */
public class Material {

    public Vector4f ambientColor, diffuseColor, specularColor;
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

    public Material(Vector4f color, float reflectance, Texture texture)
    {
        this(color, color, color, reflectance, texture);
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
