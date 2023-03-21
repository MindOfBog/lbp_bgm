package bog.bgmaker.view3d.managers;

import bog.bgmaker.view3d.core.DirectionalLight;
import bog.bgmaker.view3d.core.Material;
import bog.bgmaker.view3d.core.PointLight;
import bog.bgmaker.view3d.core.SpotLight;
import org.joml.*;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;
import org.lwjgl.system.MemoryStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Bog
 */
public class ShaderMan {

    public int programID, vertexShaderID, geometryShaderID, fragmentShaderID;
    public Map<String, Integer> uniforms;

    public ShaderMan() throws Exception
    {
        programID = GL20.glCreateProgram();

        if(programID == 0)
            throw new Exception("Couldn't create shader.");

        uniforms = new HashMap<>();
    }

    public void createUniform(String uniformName) throws Exception
    {
        int uniformLocation = GL20.glGetUniformLocation(programID, uniformName);

        if(uniformLocation < 0)
            throw new Exception("Could not find uniform " + uniformName + ".");

        uniforms.put(uniformName, uniformLocation);
    }

    public void createDirectionalLightUniform(String uniformName) throws Exception
    {
        createUniform(uniformName + ".color");
        createUniform(uniformName + ".direction");
        createUniform(uniformName + ".intensity");
    }

    public void createPointLightUniform(String uniformName) throws Exception
    {
        createUniform(uniformName + ".color");
        createUniform(uniformName + ".position");
        createUniform(uniformName + ".intensity");
        createUniform(uniformName + ".constant");
        createUniform(uniformName + ".linear");
        createUniform(uniformName + ".exponent");
    }

    public void createSpotLightUniform(String uniformName) throws Exception
    {
        createPointLightUniform(uniformName + ".pl");
        createUniform(uniformName + ".conedir");
        createUniform(uniformName + ".cutoff");
    }

    public void createPointLightListUniform(String uniformName, int size) throws Exception
    {
        for(int i = 0; i < size; i++)
        {
            createPointLightUniform(uniformName + "[" + i + "]");
        }
    }

    public void createSpotLightListUniform(String uniformName, int size) throws Exception
    {
        for(int i = 0; i < size; i++)
        {
            createSpotLightUniform(uniformName + "[" + i + "]");
        }
    }

    public void createDirectionalLightListUniform(String uniformName, int size) throws Exception
    {
        for(int i = 0; i < size; i++)
        {
            createDirectionalLightUniform(uniformName + "[" + i + "]");
        }
    }

    public void createMaterialUniform(String uniformName) throws Exception
    {
        createUniform(uniformName + ".ambient");
        createUniform(uniformName + ".diffuse");
        createUniform(uniformName + ".specular");
        createUniform(uniformName + ".hasTexture");
        createUniform(uniformName + ".reflectance");
    }

    public void setUniform(String uniformName, Material material)
    {
        setUniform(uniformName + ".ambient", material.ambientColor);
        setUniform(uniformName + ".diffuse", material.diffuseColor);
        setUniform(uniformName + ".specular", material.specularColor);
        setUniform(uniformName + ".hasTexture", material.hasTexture() ? 1 : 0);
        setUniform(uniformName + ".reflectance", material.reflectance);
    }

    public void setUniform(String uniformName, Matrix4f value)
    {
        try(MemoryStack stack = MemoryStack.stackPush())
        {
            GL20.glUniformMatrix4fv(uniforms.get(uniformName), false, value.get(stack.mallocFloat(16)));
        }
    }

    public void setUniform(String uniformName, Matrix4d value)
    {
        try(MemoryStack stack = MemoryStack.stackPush())
        {
            Matrix4f cValue = new Matrix4f();
            Vector3d trans = value.getTranslation(new Vector3d());
            Vector3d angles = value.getEulerAnglesZYX(new Vector3d());
            Vector3d scale = value.getScale(new Vector3d());

            cValue.identity().translate(new Vector3f((float) trans.x, (float) trans.y, (float) trans.z))
                    .rotateX((float) angles.x)
                    .rotateY((float) angles.y)
                    .rotateZ((float) angles.z)
                    .scale(new Vector3f((float) scale.x, (float) scale.y, (float) scale.z));

            GL20.glUniformMatrix4fv(uniforms.get(uniformName), false, cValue.get(stack.mallocFloat(16)));
        }
    }

    public void setUniform(String uniformName, int value)
    {
        GL20.glUniform1i(uniforms.get(uniformName), value);
    }

    public void setUniform(String uniformName, float value)
    {
        GL20.glUniform1f(uniforms.get(uniformName), value);
    }

    public void setUniform(String uniformName, Vector3f value)
    {
        GL20.glUniform3f(uniforms.get(uniformName), value.x, value.y, value.z);
    }

    public void setUniform(String uniformName, Vector2f value)
    {
        GL20.glUniform2f(uniforms.get(uniformName), value.x, value.y);
    }

    public void setUniform(String uniformName, Vector2i value)
    {
        GL20.glUniform2i(uniforms.get(uniformName), value.x, value.y);
    }

    public void setUniform(String uniformName, Vector4f value)
    {
        GL20.glUniform4f(uniforms.get(uniformName), value.x, value.y, value.z, value.w);
    }

    public void setUniform(String uniformName, boolean value)
    {
        GL20.glUniform1f(uniforms.get(uniformName), value ? 1 : 0);
    }

    public void setUniform(String uniformName, DirectionalLight directionalLight)
    {
        setUniform(uniformName + ".color", directionalLight.color);
        setUniform(uniformName + ".direction", directionalLight.direction);
        setUniform(uniformName + ".intensity", directionalLight.intensity);
    }

    public void setUniform(String uniformName, PointLight pointLight)
    {
        setUniform(uniformName + ".color", pointLight.color);
        setUniform(uniformName + ".position", pointLight.position);
        setUniform(uniformName + ".intensity", pointLight.intensity);
        setUniform(uniformName + ".constant", pointLight.constant);
        setUniform(uniformName + ".linear", pointLight.linear);
        setUniform(uniformName + ".exponent", pointLight.exponent);
    }

    public void setUniform(String uniformName, SpotLight spotLight)
    {
        setUniform(uniformName + ".pl", spotLight.pointLight);
        setUniform(uniformName + ".conedir", spotLight.coneDirection);
        setUniform(uniformName + ".cutoff", spotLight.cutoff);
    }

    public void setUniform(String uniformName, ArrayList<PointLight> pointLights)
    {
        int numLights = pointLights != null ? pointLights.size() : 0;
        for(int i = 0; i < numLights; i++)
        {
            setUniform(uniformName, pointLights.get(i), i);
        }
    }

    public void setUniform(String uniformName, PointLight pointLight, int index)
    {
        setUniform(uniformName + "[" + index + "]", pointLight);
    }

    public void setUniform(String uniformName, SpotLight[] spotLights)
    {
        int numLights = spotLights != null ? spotLights.length : 0;
        for(int i = 0; i < numLights; i++)
        {
            setUniform(uniformName, spotLights[i], i);
        }
    }

    public void setUniform(String uniformName, SpotLight spotLight, int index)
    {
        setUniform(uniformName + "[" + index + "]", spotLight);
    }

    public void setUniform(String uniformName, DirectionalLight[] directionalLights)
    {
        int numLights = directionalLights != null ? directionalLights.length : 0;
        for(int i = 0; i < numLights; i++)
        {
            setUniform(uniformName, directionalLights[i], i);
        }
    }

    public void setUniform(String uniformName, DirectionalLight directionalLight, int index)
    {
        setUniform(uniformName + "[" + index + "]", directionalLight);
    }

    public void createVertexShader(String shaderCode) throws Exception
    {
        vertexShaderID = createShader(shaderCode, GL20.GL_VERTEX_SHADER);
    }

    public void createGeometryShader(String shaderCode) throws Exception
    {
        geometryShaderID = createShader(shaderCode, GL32.GL_GEOMETRY_SHADER);
    }

    public void createFragmentShader(String shaderCode) throws Exception
    {
        fragmentShaderID = createShader(shaderCode, GL20.GL_FRAGMENT_SHADER);
    }

    public int createShader(String shaderCode, int shaderType) throws Exception
    {
        int shaderID = GL20.glCreateShader(shaderType);

        if(shaderID == 0)
            throw new Exception("Error creating shader. Type: " + shaderType + ".");

        GL20.glShaderSource(shaderID, shaderCode);
        GL20.glCompileShader(shaderID);

        if(GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == 0)
            throw new Exception("Error compiling shader code. Type: " + shaderType + ".\nInfo: " + GL20.glGetShaderInfoLog(shaderID, 1024));

        GL20.glAttachShader(programID, shaderID);

        return shaderID;
    }

    public void link() throws Exception
    {
        GL20.glLinkProgram(programID);

        if(GL20.glGetProgrami(programID, GL20.GL_LINK_STATUS) == 0)
            throw new Exception("Error linking shader code.\nInfo: " + GL20.glGetProgramInfoLog(programID, 1024));

        if(vertexShaderID != 0)
            GL20.glDetachShader(programID, vertexShaderID);
        if(geometryShaderID != 0)
            GL32.glDetachShader(programID, geometryShaderID);
        if(fragmentShaderID != 0)
            GL20.glDetachShader(programID, fragmentShaderID);

        GL20.glValidateProgram(programID);

        if(GL20.glGetProgrami(programID, GL20.GL_VALIDATE_STATUS) == 0)
            throw new Exception("Unable to validate shader code.\nInfo: " + GL20.glGetProgramInfoLog(programID, 1024));
    }

    public void bind()
    {
        GL20.glUseProgram(programID);
    }

    public void unbind()
    {
        GL20.glUseProgram(0);
    }

    public void cleanup()
    {
        unbind();

        if(programID != 0)
            GL20.glDeleteProgram(programID);
    }

    public void bindAttribute(int attribute, String name)
    {
        GL20.glBindAttribLocation(programID, attribute, name);
    }
}
