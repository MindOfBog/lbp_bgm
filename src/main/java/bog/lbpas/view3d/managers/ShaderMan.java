package bog.lbpas.view3d.managers;

import bog.lbpas.Main;
import bog.lbpas.view3d.core.*;
import bog.lbpas.view3d.renderer.gui.ingredients.Blur;
import bog.lbpas.view3d.utils.print;
import cwlib.enums.Part;
import cwlib.structs.mesh.Bone;
import cwlib.structs.things.Thing;
import cwlib.structs.things.parts.PPos;
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

    String shaderName = "null";

    public ShaderMan(String name) throws Exception
    {
        programID = GL20.glCreateProgram();

        if(programID == 0)
            throw new Exception("Couldn't create shader.");

        uniforms = new HashMap<>();
        this.shaderName = name;
    }

    public boolean createUniform(String uniformName) throws Exception
    {
        int uniformLocation = GL20.glGetUniformLocation(programID, uniformName);

        if(uniformLocation < 0)
        {
            if(Main.debug)
                print.error("Could not find uniform " + uniformName + ".");
            return false;
        }

        uniforms.put(uniformName, uniformLocation);
        return true;
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

    public void createListUniform(String uniformName, int size) throws Exception
    {
        for(int i = 0; i < size; i++)
        {
            boolean success = createUniform(uniformName + "[" + i + "]");

            if(!success)
                break;
        }
    }

    public void create2DArrayUniform(String uniformName, int size, int size2) throws Exception
    {
        for(int i = 0; i < size; i++)
            for(int j = 0; j < size2; j++)
            {
                createUniform(uniformName + "[" + i + "][" + j + "]");
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

    public void createBlurUniform(String uniformName) throws Exception
    {
        createUniform(uniformName + ".isGaussian");
        createListUniform(uniformName + ".gaussKernel", 41);
        createUniform(uniformName + ".pixelSize");
        createUniform(uniformName + ".radius");
        createUniform(uniformName + ".vertical");
    }

    public void setUniform(String uniformName, Blur blur, float pixelSize, boolean vertical)
    {
        setUniform(uniformName + ".isGaussian", blur.gaussian);
        if(blur.gaussian)
            setUniform(uniformName + ".gaussKernel", blur.gaussKernel);
        setUniform(uniformName + ".pixelSize", pixelSize);
        setUniform(uniformName + ".radius", blur.radius);
        setUniform(uniformName + ".vertical", vertical);
    }

    public void setUniform(String uniformName, float pixelSize, boolean vertical)
    {
        setUniform(uniformName + ".pixelSize", pixelSize);
        setUniform(uniformName + ".vertical", vertical);
    }

    public void createDimensionUniform(String uniformName) throws Exception
    {
        createUniform(uniformName + ".position");
        createUniform(uniformName + ".size");
    }

    public void setUniform(String uniformName, Vector2i pos, Vector2i size)
    {
        setUniform(uniformName + ".position", pos);
        setUniform(uniformName + ".size", size);
    }

    public void setUniform(String uniformName, Matrix4f value)
    {
        try(MemoryStack stack = MemoryStack.stackPush())
        {
            Integer uniform = uniforms.get(uniformName);
            if(uniform != null)
                GL20.glUniformMatrix4fv(uniform, false, value.get(stack.mallocFloat(16)));
        }
    }

    public void setUniform(String uniformName, Matrix4f[] matrices)
    {
        int num = matrices != null ? matrices.length : 0;
        for(int i = 0; i < num; i++)
        {
            setUniform(uniformName, matrices[i], i);
        }
    }

    public void setUniform(String uniformName, float[] floats)
    {
        int num = floats != null ? floats.length : 0;
        for(int i = 0; i < num; i++)
        {
            setUniform(uniformName, floats[i], i);
        }
    }

    public void setUniform(String uniformName, Thing[] thingBones, Bone[] meshBones)
    {
        int num = thingBones != null && meshBones != null ? meshBones.length : 0;
        if(thingBones != null && thingBones.length < num)
            num = thingBones.length;

        if(thingBones != null && meshBones != null &&
                thingBones.length > 0 && meshBones.length > 0 &&
                thingBones[0] != null && thingBones[0].getPart(Part.POS) != null &&
                meshBones[0] != null)
            for(int i = 0; i < num; i++)
            {
                if(thingBones[i] == null || meshBones[i] == null ||
                        thingBones[i].getPart(Part.POS) == null)
                    continue;
                setUniform(uniformName, new Matrix4f(((PPos)thingBones[0].getPart(Part.POS)).worldPosition).invert().mul(((PPos)thingBones[i].getPart(Part.POS)).worldPosition).mul(meshBones[i].invSkinPoseMatrix), i);
            }
    }

    public void setUniform(String uniformName, int[] value)
    {
        int num = value != null ? value.length : 0;
        for(int i = 0; i < num; i++)
        {
            setUniform(uniformName, value[i], i);
        }
    }

    public void setUniform(String uniformName, Matrix4f value, int index)
    {
        setUniform(uniformName + "[" + index + "]", value);
    }

    public void setUniform(String uniformName, float value, int index)
    {
        setUniform(uniformName + "[" + index + "]", value);
    }

    public void setUniform(String uniformName, int value, int index)
    {
        setUniform(uniformName + "[" + index + "]", value);
    }

    public void setUniform(String uniformName, Vector2i value, int index)
    {
        setUniform(uniformName + "[" + index + "]", value);
    }

    public void setUniform(String uniformName, int value)
    {
        Integer uniform = uniforms.get(uniformName);
        if(uniform != null)
            GL20.glUniform1i(uniform, value);
    }

    public void setUniform(String uniformName, float value)
    {
        Integer uniform = uniforms.get(uniformName);
        if(uniform != null)
            GL20.glUniform1f(uniform, value);
    }

    public void setUniform(String uniformName, Vector3f value)
    {
        Integer uniform = uniforms.get(uniformName);
        if(uniform != null)
            GL20.glUniform3f(uniform, value.x, value.y, value.z);
    }

    public void setUniform(String uniformName, Vector2f value)
    {
        Integer uniform = uniforms.get(uniformName);
        if(uniform != null)
            GL20.glUniform2f(uniform, value.x, value.y);
    }

    public void setUniform(String uniformName, Vector2i value)
    {
        Integer uniform = uniforms.get(uniformName);
        if(uniform != null)
            GL20.glUniform2i(uniform, value.x, value.y);
    }

    public void setUniform(String uniformName, Vector4f value)
    {
        Integer uniform = uniforms.get(uniformName);
        if(uniform != null)
            GL20.glUniform4f(uniform, value.x, value.y, value.z, value.w);
    }

    public void setUniform(String uniformName, boolean value)
    {
        Integer uniform = uniforms.get(uniformName);
        if(uniform != null)
            GL20.glUniform1i(uniform, value ? 1 : 0);
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
        if(Main.debug)
            print.line("Creating vertex shader for: " + shaderName);
        vertexShaderID = createShader(shaderCode, GL20.GL_VERTEX_SHADER);
    }

    public void createGeometryShader(String shaderCode) throws Exception
    {
        if(Main.debug)
            print.line("Creating geometry shader for: " + shaderName);
        geometryShaderID = createShader(shaderCode, GL32.GL_GEOMETRY_SHADER);
    }

    public void createFragmentShader(String shaderCode) throws Exception
    {
        if(Main.debug)
            print.line("Creating fragment shader for: " + shaderName);
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
        if(Main.debug)
            print.line("Linking shader: " + shaderName);

        GL20.glLinkProgram(programID);

        if(GL20.glGetProgrami(programID, GL20.GL_LINK_STATUS) == 0)
        {
            print.error("Error linking shader code.\nInfo: " + GL20.glGetProgramInfoLog(programID, 1024));
            return;
        }

        if(vertexShaderID != 0)
            GL20.glDetachShader(programID, vertexShaderID);
        if(geometryShaderID != 0)
            GL32.glDetachShader(programID, geometryShaderID);
        if(fragmentShaderID != 0)
            GL20.glDetachShader(programID, fragmentShaderID);

        GL20.glValidateProgram(programID);

        if(GL20.glGetProgrami(programID, GL20.GL_VALIDATE_STATUS) == 0)
        {
            print.error("Unable to validate shader code.\nInfo: " + GL20.glGetProgramInfoLog(programID, 1024));
            return;
        }
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
