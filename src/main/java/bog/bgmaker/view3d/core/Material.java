package bog.bgmaker.view3d.core;

import bog.bgmaker.view3d.managers.ShaderMan;
import bog.bgmaker.view3d.utils.Config;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.awt.*;
import java.util.ArrayList;

/**
 * @author Bog
 */
public class Material {

    public Vector4f[] ambientColor, diffuseColor, specularColor;
    public Vector4f overlayColor;
    public float reflectance;
    public Texture[] textures;
    public boolean disableCulling;

    public ShaderMan customShader = null;

    public Material()
    {
        this.ambientColor = new Vector4f[]{Config.DEFAULT_COLOR};
        this.diffuseColor = new Vector4f[]{Config.DEFAULT_COLOR};
        this.specularColor = new Vector4f[]{Config.DEFAULT_COLOR};
        this.textures = null;
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

    public Material(Vector4f color, float reflectance, Texture[] textures)
    {
        this(color, color, color, reflectance, textures);
    }

    public Material(Color color, float reflectance, Texture[] textures)
    {
        this(new Vector4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f), reflectance, textures);
    }

    public Material(Texture[] textures)
    {
        this(Config.DEFAULT_COLOR, Config.DEFAULT_COLOR, Config.DEFAULT_COLOR, 0f, textures);
    }

    public Material(Vector4f ambientColor, Vector4f diffuseColor, Vector4f specularColor, float reflectance, Texture[] textures)
    {
        this.ambientColor = new Vector4f[]{ambientColor};
        this.diffuseColor = new Vector4f[]{diffuseColor};
        this.specularColor = new Vector4f[]{specularColor};
        this.reflectance = reflectance;
        this.textures = textures;
        this.disableCulling = false;
    }

    public void setColor(Vector4f color)
    {
        this.diffuseColor = new Vector4f[]{color};
        this.ambientColor = new Vector4f[]{color};
        this.specularColor = new Vector4f[]{color};
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

    public void setupUniforms(Matrix4f projection, ArrayList<DirectionalLight> directionalLights, ArrayList<PointLight> pointLights, ArrayList<SpotLight> spotLights) {
        customShader.setUniform("projectionMatrix", projection);
        customShader.setUniform("ambientLight", Config.AMBIENT_LIGHT);
        customShader.setUniform("specularPower", Config.SPECULAR_POWER);
        customShader.setUniform("directionalLights", directionalLights.toArray(DirectionalLight[]::new));
        customShader.setUniform("directionalLightsSize", directionalLights.size());
        customShader.setUniform("pointLights", pointLights);
        customShader.setUniform("pointLightsSize", pointLights.size());
        customShader.setUniform("spotLights", spotLights.toArray(SpotLight[]::new));
        customShader.setUniform("spotLightsSize", spotLights.size());
    }

    public void setupUniformsThroughwall() {
        customShader.setUniform("ambientLight", new Vector3f(1f, 1f, 1f));
        customShader.setUniform("directionalLightsSize", 0);
        customShader.setUniform("pointLightsSize", 0);
        customShader.setUniform("spotLightsSize", 0);
    }
}
