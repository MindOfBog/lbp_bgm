package bog.lbpas.view3d.renderer;

import bog.lbpas.Main;
import bog.lbpas.view3d.managers.assetLoading.ObjectLoader;
import bog.lbpas.view3d.core.*;
import bog.lbpas.view3d.core.types.Entity;
import bog.lbpas.view3d.core.types.Thing;
import bog.lbpas.view3d.mainWindow.LoadedData;
import bog.lbpas.view3d.mainWindow.View3D;
import bog.lbpas.view3d.mainWindow.screens.ElementEditing;
import bog.lbpas.view3d.managers.MouseInput;
import bog.lbpas.view3d.managers.RenderMan;
import bog.lbpas.view3d.managers.ShaderMan;
import bog.lbpas.view3d.managers.WindowMan;
import bog.lbpas.view3d.utils.Config;
import bog.lbpas.view3d.utils.Transformation;
import bog.lbpas.view3d.utils.Utils;
import bog.lbpas.view3d.utils.print;
import cwlib.enums.Part;
import cwlib.enums.VisibilityFlags;
import cwlib.structs.mesh.Bone;
import cwlib.structs.things.parts.PRenderMesh;
import cwlib.structs.things.parts.PShape;
import cwlib.util.Colors;
import org.joml.*;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.*;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.*;

/**
 * @author Bog
 */
public class EntityRenderer implements IRenderer{

    public static int samplerCount = 31;
    public ShaderMan thingShader;
    public ShaderMan solidShader;
    public ShaderMan normalShader;
    public ShaderMan shaderMousePick;
    public ShaderMan basicShader;
    public ShaderMan outlineFlatShader;
    public ArrayList<Entity> entities;
    public ArrayList<Entity> basicMeshes;
    public ArrayList<DirectionalLight> directionalLights;
    public ArrayList<PointLight> pointLights;
    public ArrayList<SpotLight> spotLights;
    public ArrayList<Entity> throughWallEntities;
    public WindowMan window;
    public ObjectLoader loader;

    private Material defaultMaterial = new Material();

    public EntityRenderer(ObjectLoader loader, WindowMan window) throws Exception
    {
        entities = new ArrayList<>();
        basicMeshes = new ArrayList<>();
        directionalLights = new ArrayList<>();
        pointLights = new ArrayList<>();
        spotLights = new ArrayList<>();
        throughWallEntities = new ArrayList<>();
        thingShader = new ShaderMan("thingShader");
        solidShader = new ShaderMan("solidShader");
        normalShader = new ShaderMan("normalShader");
        shaderMousePick = new ShaderMan("shaderMousePick");
        basicShader = new ShaderMan("basicShader");
        outlineFlatShader = new ShaderMan("outlineFlatShader");
        this.window = window;
        this.loader = loader;
    }

    String vertex;

    @Override
    public void init() throws Exception
    {
        vertex = Utils.loadResource("/shaders/vertex.glsl");

        thingShader.createVertexShader(vertex);
        String shaderCode = Utils.loadResource("/shaders/fragment.glsl").replaceAll("//%&AMBIENTC",
                "if(material.hasTexture == 1)" +
                        "    {" +
                        "        ambientC = texture(textureSampler[0], fragTextureCoord.xy);" +
                        "        if(highlightMode == 1)" +
                        "        {" +
                        "            float mul = (ambientC.r + ambientC.g + ambientC.b) / 3.0;" +
                        "            ambientC = vec4(highlightColor.r * mul, highlightColor.g * mul, highlightColor.b * mul, ambientC.a * highlightColor.a);" +
                        "        }" +
                        "        else if(highlightMode == 2)" +
                        "        {" +
                        "            ambientC.r = ambientC.r * brightnessMul;" +
                        "            ambientC.g = ambientC.g * brightnessMul;" +
                        "            ambientC.b = ambientC.b * brightnessMul;" +
                        "        }" +
                        "        diffuseC = ambientC;" +
                        "        specularC = ambientC;" +
                        "    }" +
                        "    else" +
                        "    {" +
                        "        ambientC = material.ambient;" +
                        "        diffuseC = material.diffuse;" +
                        "        specularC = material.specular;" +
                        "        if(highlightMode == 1)" +
                        "        {" +
                        "            float mul = (ambientC.r + ambientC.g + ambientC.b) / 3.0;" +
                        "            ambientC = vec4(highlightColor.r * mul, highlightColor.g * mul, highlightColor.b * mul, ambientC.a * highlightColor.a);" +
                        "        }" +
                        "        else if(highlightMode == 2)" +
                        "        {" +
                        "            ambientC.r = ambientC.r * brightnessMul;" +
                        "            ambientC.g = ambientC.g * brightnessMul;" +
                        "            ambientC.b = ambientC.b * brightnessMul;" +
                        "        }" +
                        "    }");

        thingShader.createFragmentShader(shaderCode);
        thingShader.link();
        thingShader.createListUniform("textureSampler", samplerCount);
        thingShader.createUniform("preview");
        thingShader.createListUniform("gmatMAP", 100);
        thingShader.createUniform("gmatCount");
        thingShader.createUniform("ambientLight");
        thingShader.createMaterialUniform("material");
        thingShader.createUniform("highlightMode");
        thingShader.createUniform("highlightColor");
        thingShader.createUniform("brightnessMul");
        thingShader.createUniform("specularPower");
        thingShader.createDirectionalLightListUniform("directionalLights", 5);
        thingShader.createUniform("directionalLightsSize");
        thingShader.createPointLightListUniform("pointLights", 50);
        thingShader.createUniform("pointLightsSize");
        thingShader.createSpotLightListUniform("spotLights", 50);
        thingShader.createUniform("spotLightsSize");
        thingShader.createUniform("thingColor");
        thingShader.createUniform("rimColor");
        thingShader.createUniform("rimColor2");
        thingShader.createUniform("fogNear");
        thingShader.createUniform("fogFar");
        thingShader.createUniform("camPos");
        thingShader.createUniform("sunPos");
        thingShader.createUniform("noCulling");

        thingShader.createUniform("transformationMatrix");
        thingShader.createUniform("projectionMatrix");
        thingShader.createUniform("viewMatrix");
        thingShader.createListUniform("bones", 100);
        thingShader.createUniform("hasBones");
        thingShader.createUniform("triangleOffset");

        basicShader.createVertexShader(vertex);
        basicShader.createFragmentShader(shaderCode);
        basicShader.link();
        basicShader.createListUniform("textureSampler", samplerCount);
        basicShader.createUniform("ambientLight");
        basicShader.createMaterialUniform("material");
        basicShader.createUniform("highlightMode");
        basicShader.createUniform("highlightColor");
        basicShader.createUniform("brightnessMul");
        basicShader.createUniform("specularPower");
        basicShader.createDirectionalLightListUniform("directionalLights", 5);
        basicShader.createUniform("directionalLightsSize");
        basicShader.createPointLightListUniform("pointLights", 50);
        basicShader.createUniform("pointLightsSize");
        basicShader.createSpotLightListUniform("spotLights", 50);
        basicShader.createUniform("spotLightsSize");
        basicShader.createUniform("thingColor");
        basicShader.createUniform("fogNear");
        basicShader.createUniform("fogFar");
        basicShader.createUniform("camPos");
        basicShader.createUniform("sunPos");

        basicShader.createUniform("transformationMatrix");
        basicShader.createUniform("projectionMatrix");
        basicShader.createUniform("viewMatrix");
        basicShader.createListUniform("bones", 100);
        basicShader.createUniform("hasBones");
        basicShader.createUniform("triangleOffset");

        outlineFlatShader.createVertexShader(vertex);
        outlineFlatShader.createFragmentShader(
                "#version 330 core\n" +
                "\n" +
                "in vec4 fragTextureCoord;\n" +
                "in vec3 fragNormal;\n" +
                "in vec3 fragPos;\n" +
                "in vec3 fragTang;\n" +
                "in vec3 fragBitTang;\n" +
                "flat in int fragGmat;\n" +
                "in vec4 viewPosition;\n" +
                "\n" +
                "out vec4 fragmentColor;\n" +
                "\n" +
                "const mat4 thresholdMatrix = mat4(\n" +
                "    1, 9, 3, 11,\n" +
                "    13, 5, 15, 7,\n" +
                "    4, 12, 2, 10,\n" +
                "    16, 8, 14, 6);\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "    fragmentColor = vec4(1.0, 1.0, 1.0, 1.0);\n" +
                "}");
        outlineFlatShader.link();

        outlineFlatShader.createUniform("transformationMatrix");
        outlineFlatShader.createUniform("projectionMatrix");
        outlineFlatShader.createUniform("viewMatrix");
        outlineFlatShader.createListUniform("bones", 100);
        outlineFlatShader.createUniform("hasBones");
        outlineFlatShader.createUniform("triangleOffset");

        solidShader.createVertexShader(vertex);
        String solidShaderCode = Utils.loadResource("/shaders/fragment.glsl").replaceAll("//%&AMBIENTC",
        "vec3 normal = normalize(fragNormal);" +
                "vec3 tangent = normalize(fragTang);" +
                "vec3 bitangent = normalize(fragBitTang);" +
                "mat3 TBN = mat3(tangent, bitangent, normal);" +
                "vec3 cameraDirection = normalize(-fragPos);" +
                "float dotProduct = dot(normal, -normalize(vec3(0.5, 0.5, -0.6)));" +
                "float diffuseShade = max(dotProduct, 0.0);" +
                "float specularDotProduct = dot(normal, cameraDirection);" +
                "float specularIntensity = max(specularDotProduct, 0.0);" +
                "float specularPower = 16.0;" +
                "float specularShade = pow(specularIntensity, specularPower);" +
                "" +
                "vec3 diffuseColor = vec3(0.8, 0.8, 0.8) * diffuseShade;" +
                "vec3 specularColor = vec3(1.0, 1.0, 1.0) * specularShade * 0.1;" +
                "vec3 finalColor = diffuseColor + specularColor;" +
                "finalColor = pow(finalColor, vec3(0.2));" +
                "ambientC = vec4((finalColor / 2) + vec3(0.5), material.ambient.a < 0.25 ? 0.0 : 1.0);" +
                "diffuseC = ambientC;" +
                "specularC = ambientC;"
                );

        solidShader.createFragmentShader(solidShaderCode);
        solidShader.link();
        solidShader.createUniform("preview");
        solidShader.createUniform("ambientLight");
        solidShader.createMaterialUniform("material");
        solidShader.createUniform("specularPower");
        solidShader.createDirectionalLightListUniform("directionalLights", 5);
        solidShader.createUniform("directionalLightsSize");
        solidShader.createPointLightListUniform("pointLights", 50);
        solidShader.createUniform("pointLightsSize");
        solidShader.createSpotLightListUniform("spotLights", 50);
        solidShader.createUniform("spotLightsSize");
        solidShader.createUniform("thingColor");
        solidShader.createUniform("rimColor");
        solidShader.createUniform("rimColor2");
        solidShader.createUniform("fogNear");
        solidShader.createUniform("fogFar");
        solidShader.createUniform("camPos");
        solidShader.createUniform("sunPos");

        solidShader.createUniform("transformationMatrix");
        solidShader.createUniform("projectionMatrix");
        solidShader.createUniform("viewMatrix");
        solidShader.createListUniform("bones", 100);
        solidShader.createUniform("hasBones");
        solidShader.createUniform("triangleOffset");

        normalShader.createVertexShader(vertex);
        String normalShaderCode = Utils.loadResource("/shaders/fragment.glsl").replaceAll("//%&AMBIENTC",
                "ambientC = vec4(0.5 * fragNormal + 0.5, material.ambient.a < 0.25 ? 0.0 : 1.0); " +
                        "diffuseC = ambientC;" +
                        "specularC = ambientC;"
        );

        normalShader.createFragmentShader(normalShaderCode);
        normalShader.link();
        normalShader.createUniform("preview");
        normalShader.createUniform("ambientLight");
        normalShader.createMaterialUniform("material");
        normalShader.createUniform("specularPower");
        normalShader.createDirectionalLightListUniform("directionalLights", 5);
        normalShader.createUniform("directionalLightsSize");
        normalShader.createPointLightListUniform("pointLights", 50);
        normalShader.createUniform("pointLightsSize");
        normalShader.createSpotLightListUniform("spotLights", 50);
        normalShader.createUniform("spotLightsSize");
        normalShader.createUniform("thingColor");
        normalShader.createUniform("rimColor");
        normalShader.createUniform("rimColor2");
        normalShader.createUniform("fogNear");
        normalShader.createUniform("fogFar");
        normalShader.createUniform("camPos");
        normalShader.createUniform("sunPos");

        normalShader.createUniform("transformationMatrix");
        normalShader.createUniform("projectionMatrix");
        normalShader.createUniform("viewMatrix");
        normalShader.createListUniform("bones", 100);
        normalShader.createUniform("hasBones");
        normalShader.createUniform("triangleOffset");

        shaderMousePick.createVertexShader(Utils.loadResource("/shaders/vertex.glsl"));
        shaderMousePick.createFragmentShader(Utils.loadResource("/shaders/fragment_mouse.glsl"));
        shaderMousePick.link();
        shaderMousePick.createUniform("transformationMatrix");
        shaderMousePick.createUniform("projectionMatrix");
        shaderMousePick.createUniform("viewMatrix");
        shaderMousePick.createListUniform("bones", 100);
        shaderMousePick.createUniform("hasBones");
        shaderMousePick.createUniform("triangleOffset");
        shaderMousePick.createUniform("arrayIndex");

        mouseFB = RenderMan.initFrameBufferINT();
        mouseCT = RenderMan.initColorTexINT(window);
        mouseDT = RenderMan.initDepthTex(window);
    }

    int mouseFB = -1;
    int mouseCT = -1;
    int mouseDT = -1;

    public void resize()
    {
        GL11.glDeleteTextures(new int[]{mouseCT, mouseDT});
        GL11.glViewport(0, 0, window.width, window.height);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, mouseFB);
        mouseCT = RenderMan.initColorTexINT(window);
        mouseDT = RenderMan.initDepthTex(window);
    }

    public int[] getOutlineFB()
    {return null;}

    long lastShaderAddedMillis = System.currentTimeMillis();
    boolean actuallUpdateShader = false;

    Matrix4f projection;
    public boolean triggerMousePick = false;

    @Override
    public void render(MouseInput mouseInput, View3D mainView)
    {
        if(LoadedData.shouldUpdateShader)
        {
            lastShaderAddedMillis = System.currentTimeMillis();
            LoadedData.shouldUpdateShader = false;
            actuallUpdateShader = true;
        }

        if(actuallUpdateShader && System.currentTimeMillis() - lastShaderAddedMillis > 1000l)
        {
            thingShader.cleanup();
            String shaderCode = "";
            try
            {
                thingShader = new ShaderMan("thingShader");

                String switchStatement = "int gmatIndex = getGmatIndex(fragGmat);\n\n" +
                        "    switch (fragGmat) {\n";

                for(int i = 0 ; i < LoadedData.loadedGfxMaterials.size(); i++)
                {
                    String shader = LoadedData.loadedGfxMaterials.get(i);
                    switchStatement += "        case " + i + ":" + shader + "break;\n";
                }

                switchStatement += "        default:if(fragGmat != -1)ambientC = vec4(0.5f);break;\n    }\n\n";

                thingShader.createVertexShader(vertex);
                shaderCode = Utils.loadResource("/shaders/fragment.glsl").replaceAll("//%&AMBIENTC",
                        switchStatement +
                                "    if(material.hasTexture == 1 && !custom)\n" +
                                "    {\n" +
                                "        ambientC = texture(textureSampler[0], fragTextureCoord.xy);\n" +
                                "        if(highlightMode == 1)\n" +
                                "        {\n" +
                                "            float mul = (ambientC.r + ambientC.g + ambientC.b) / 3.0;\n" +
                                "            ambientC = vec4(highlightColor.r * mul, highlightColor.g * mul, highlightColor.b * mul, ambientC.a * highlightColor.a);\n" +
                                "        }\n" +
                                "        else if(highlightMode == 2)\n" +
                                "        {\n" +
                                "            ambientC.r = ambientC.r * brightnessMul;\n" +
                                "            ambientC.g = ambientC.g * brightnessMul;\n" +
                                "            ambientC.b = ambientC.b * brightnessMul;\n" +
                                "        }\n" +
                                "        diffuseC = ambientC;\n" +
                                "        specularC = ambientC;\n" +
                                "    }\n" +
                                "    else if(material.hasTexture == 0)\n" +
                                "    {\n" +
                                "        ambientC = material.ambient;\n" +
                                "        diffuseC = material.diffuse;\n" +
                                "        specularC = material.specular;\n" +
                                "        if(highlightMode == 1)\n" +
                                "        {\n" +
                                "            float mul = (ambientC.r + ambientC.g + ambientC.b) / 3.0;\n" +
                                "            ambientC = vec4(highlightColor.r * mul, highlightColor.g * mul, highlightColor.b * mul, ambientC.a * highlightColor.a);\n" +
                                "        }\n" +
                                "        else if(highlightMode == 2)\n" +
                                "        {\n" +
                                "            ambientC.r = ambientC.r * brightnessMul;\n" +
                                "            ambientC.g = ambientC.g * brightnessMul;\n" +
                                "            ambientC.b = ambientC.b * brightnessMul;\n" +
                                "        }\n" +
                                "    }\n" +
                                "    else\n" +
                                "    {\n" +
                                "        if(highlightMode == 1)\n" +
                                "        {\n" +
                                "            float mul = (ambientC.r + ambientC.g + ambientC.b) / 3.0;\n" +
                                "            ambientC = vec4(highlightColor.r * mul, highlightColor.g * mul, highlightColor.b * mul, ambientC.a * highlightColor.a);\n" +
                                "        }\n" +
                                "        else if(highlightMode == 2)\n" +
                                "        {\n" +
                                "            ambientC.r = ambientC.r * brightnessMul;\n" +
                                "            ambientC.g = ambientC.g * brightnessMul;\n" +
                                "            ambientC.b = ambientC.b * brightnessMul;\n" +
                                "        }\n" +
                                "        diffuseC = ambientC;\n" +
                                "        specularC = ambientC;\n" +
                                "    }\n");

                thingShader.createFragmentShader(shaderCode);
                thingShader.link();
                thingShader.createListUniform("textureSampler", samplerCount);
                thingShader.createUniform("preview");
                thingShader.createListUniform("gmatMAP", 100);
                thingShader.createUniform("gmatCount");
                thingShader.createUniform("ambientLight");
                thingShader.createMaterialUniform("material");
                thingShader.createUniform("highlightMode");
                thingShader.createUniform("highlightColor");
                thingShader.createUniform("brightnessMul");
                thingShader.createUniform("specularPower");
                thingShader.createDirectionalLightListUniform("directionalLights", 5);
                thingShader.createUniform("directionalLightsSize");
                thingShader.createPointLightListUniform("pointLights", 50);
                thingShader.createUniform("pointLightsSize");
                thingShader.createSpotLightListUniform("spotLights", 50);
                thingShader.createUniform("spotLightsSize");
                thingShader.createUniform("thingColor");
                thingShader.createUniform("rimColor");
                thingShader.createUniform("rimColor2");
                thingShader.createUniform("fogNear");
                thingShader.createUniform("fogFar");
                thingShader.createUniform("camPos");
                thingShader.createUniform("sunPos");
                thingShader.createUniform("noCulling");

                thingShader.createUniform("transformationMatrix");
                thingShader.createUniform("projectionMatrix");
                thingShader.createUniform("viewMatrix");
                thingShader.createListUniform("bones", 100);
                thingShader.createUniform("hasBones");
                thingShader.createUniform("triangleOffset");

                actuallUpdateShader = false;
            }catch (Exception e){
                print.stackTrace(e);

                if(Main.debug)
                {
                    StringSelection selection = new StringSelection(shaderCode);
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(selection, selection);
                    print.neutral("Shader copied to clipboard.");
                }
            }
        }

        projection = window.updateProjectionMatrix();
        Camera camera = mainView.camera;
        RenderMan.bindFrameBuffer(mouseFB);
        RenderMan.bindColorTex(mouseCT);
        RenderMan.bindDepthTex(mouseDT);

        shaderMousePick.bind();
        shaderMousePick.setUniform("projectionMatrix", projection);

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);

        boolean hasMousePick = false;

        ArrayList<Integer> testForEntities = new ArrayList<>();

        int selectedAmount = 0;

        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            if (entity != null)
            {
                if (entity.testForMouse)
                {
                    testForEntities.add(i);
                    hasMousePick = true;
                }
                if(entity.selected)
                    selectedAmount++;
            }
        }

        if(triggerMousePick)
        {
            for (int i : testForEntities) {
                Entity entity = entities.get(i);
                shaderMousePick.setUniform("arrayIndex", new Vector2i(i + 1, 1));
                if (entity != null) {
                    ArrayList<Model> models = entity.getModel();

                    if (models != null)
                        for (Model model : models)
                            if (model != null) {
                                GL30.glBindVertexArray(model.vao);
                                GL20.glEnableVertexAttribArray(0);
                                GL20.glEnableVertexAttribArray(1);
                                GL20.glEnableVertexAttribArray(2);
                                if (model.hasBones) {
                                    GL20.glEnableVertexAttribArray(3);
                                    GL20.glEnableVertexAttribArray(4);
                                }
                                GL20.glEnableVertexAttribArray(5);

                                RenderMan.disableCulling();

                                prepareMousePick(entity, camera);

                                shaderMousePick.setUniform("bones", model.hasBones ? ((Thing) entity).getBones() : null, model.hasBones ? model.mesh.getBones() : null);
                                shaderMousePick.setUniform("hasBones", model.hasBones);

                                // Mouse picker render to FBO

                                GL11.glDrawElements(GL11.GL_TRIANGLES, model.vertexCount, GL11.GL_UNSIGNED_INT, 0L);

                                unbind();
                            }
                }
            }
        }

        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        RenderMan.enableCulling();

        ArrayList<Integer> testForThroughWallEntities = new ArrayList<>();

        for (int i = 0; i < throughWallEntities.size(); i++)
        {
            Entity entity = throughWallEntities.get(i);
            if(entity != null)
                if (entity.testForMouse)
                {
                    hasMousePick = true;
                    testForThroughWallEntities.add(i);
                }
        }

        for (int i : testForThroughWallEntities)
        {
            Entity entity = throughWallEntities.get(i);
            shaderMousePick.setUniform("arrayIndex", new Vector2i(i + 1, 2));
            if(entity != null)
                {
                    ArrayList<Model> models = entity.getModel();
                    if(models != null)
                        for(Model model : models)
                            if(model != null)
                            {
                                GL30.glBindVertexArray(model.vao);
                                GL20.glEnableVertexAttribArray(0);
                                GL20.glEnableVertexAttribArray(1);
                                GL20.glEnableVertexAttribArray(2);
                                if(model.hasBones) {
                                    GL20.glEnableVertexAttribArray(3);
                                    GL20.glEnableVertexAttribArray(4);
                                }
                                GL20.glEnableVertexAttribArray(5);

                                prepareMousePick(entity, camera);

                                shaderMousePick.setUniform("bones", model.hasBones ? ((Thing)entity).getBones() : null, model.hasBones ? model.mesh.getBones() : null);
                                shaderMousePick.setUniform("hasBones", model.hasBones);

                                // Mouse picker render to FBO

                                GL11.glDrawElements(GL11.GL_TRIANGLES, model.vertexCount, GL11.GL_UNSIGNED_INT, 0L);

                                unbind();
                            }
                }
        }

        RenderMan.disableCulling();
        unbindFrameBuffer();

        if(hasMousePick)
        {
            GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, mouseFB);
            GL11.glReadBuffer(GL30.GL_COLOR_ATTACHMENT0);

            int[] buffer = new int[3];

            GL30.glReadPixels((int)mouseInput.currentPos.x, (int)(window.height - mouseInput.currentPos.y), 1, 1, GL30.GL_RGB_INTEGER, GL30.GL_INT, buffer);

            int outX = buffer[0] - 1;
            int outY = buffer[1];

            if(!mainView.currentScreen.isMouseOverElement(mouseInput) && !mainView.overrideScreen.isMouseOverElement(mouseInput))
                try
                {
                    if(outX != -1)
                    {
                        if(outY == 1 && triggerMousePick)
                        {
                            Entity entity = entities.get(outX);

                            if (GLFW.glfwGetKey(window.window, GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_RELEASE || GLFW.glfwGetKey(window.window, GLFW.GLFW_KEY_RIGHT_CONTROL) == GLFW.GLFW_PRESS)
                                if(!(selectedAmount == 1 && entity.selected))
                                    for(Entity e1 : mainView.things)
                                        e1.selected = false;

                            entity.selected = !entity.selected;
                            ((ElementEditing)mainView.ElementEditing).currentSelectionParts.selectionChange();
                        }
                        else if(outY == 2)
                        {
                            throughWallEntities.get(outX).highlighted = true;
                        }
                    }

                    if(triggerMousePick)
                    {
                        if(!(outY == 1 || outY == 2) || outX == -1)
                            for(Entity e1 : mainView.things)
                                e1.selected = false;
                        triggerMousePick = false;
                    }
                }catch (Exception e){e.printStackTrace();}
        }

        RenderMan.bindFrameBuffer(mouseFB);
        GL11.glClearColor(0,0,0,0);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        unbindFrameBuffer();

        ShaderMan lastShader = Config.VIEWER_SHADING == 0 ? thingShader : Config.VIEWER_SHADING == 1 ? solidShader : normalShader;

        lastShader.bind();
        lastShader.setUniform("projectionMatrix", projection);
        lastShader.setUniform("ambientLight", Config.AMBIENT_LIGHT);
        lastShader.setUniform("specularPower", Config.SPECULAR_POWER);
        lastShader.setUniform("directionalLights", directionalLights.toArray(DirectionalLight[]::new));
        lastShader.setUniform("directionalLightsSize", directionalLights.size());
        lastShader.setUniform("pointLights", pointLights);
        lastShader.setUniform("pointLightsSize", pointLights.size());
        lastShader.setUniform("spotLights", spotLights.toArray(SpotLight[]::new));
        lastShader.setUniform("spotLightsSize", spotLights.size());
        lastShader.setUniform("rimColor", mainView.rimColor);
        lastShader.setUniform("rimColor2", mainView.rimColor2);
        lastShader.setUniform("fogNear", mainView.fogNear);
        lastShader.setUniform("fogFar", mainView.fogFar);
        lastShader.setUniform("camPos", mainView.camera.pos);
        lastShader.setUniform("sunPos", mainView.sunPos);
        lastShader.setUniform("noCulling", Config.NO_CULLING);
        lastShader.setUniform("preview", Config.PREVIEW_MODE);

        boolean outline = false;

        ArrayList<Integer> selInd = new ArrayList<>();

        for (int i = 0; i < entities.size(); i++)
            if(entities.get(i) != null)
                {
                    Entity entity = entities.get(i);
                    ArrayList<Model> models = entity.getModel();

                    if (entity.selected)
                    {
                        selInd.add(i);
                        outline = true;
                    }

                    if(models != null)
                        for(Model model : models)
                            if (model != null)
                            {
                                boolean isThing = entity instanceof Thing;
                                bind(model, lastShader, isThing);
                                prepare(entity, camera, lastShader, model);

                                if(isThing)
                                {
                                    PRenderMesh rmesh = ((Thing)entity).thing.getPart(Part.RENDER_MESH);
                                    if(rmesh != null && !Utils.isBitwiseBool(rmesh.visibilityFlags, VisibilityFlags.PLAY_MODE) && Config.PREVIEW_MODE)
                                        continue;
                                }

                                if (model.material.overlayColor != null) {
                                    lastShader.setUniform("highlightMode", 1);
                                    lastShader.setUniform("highlightColor", model.material.overlayColor);
                                }

                                lastShader.setUniform("bones", model.hasBones ? ((Thing)entity).getBones() : null, model.hasBones ? model.mesh.getBones() : null);
                                lastShader.setUniform("hasBones", model.hasBones);

                                if(isThing)
                                {
                                    Vector4f color = new Vector4f(1f);
                                    if(((Thing) entity).thing.hasPart(Part.RENDER_MESH))
                                        color = Colors.RGBA32.fromARGB(((PRenderMesh)((Thing) entity).thing.getPart(Part.RENDER_MESH)).editorColor);
                                    if(((Thing) entity).thing.hasPart(Part.SHAPE))
                                    {
                                        PShape shape = ((Thing) entity).thing.getPart(Part.SHAPE);
                                        Vector4f col = Colors.RGBA32.fromARGB(shape.color);
                                        if(shape.brightness != Float.NaN) {
                                            col.y *= shape.brightness;
                                            col.z *= shape.brightness;
                                            col.w *= shape.brightness;
                                        }
                                        if(shape.colorOpacity != Float.NaN)
                                            col.x *= ((128f + shape.colorOpacity) / 255f);
                                        Vector4f colOff = Colors.RGBA32.fromARGB(shape.colorOff);
                                        if(shape.brightnessOff != Float.NaN) {
                                            colOff.y *= shape.brightnessOff;
                                            colOff.z *= shape.brightnessOff;
                                            colOff.w *= shape.brightnessOff;
                                        }
                                        if(shape.colorOffOpacity != Float.NaN)
                                            colOff.x *= (128f + shape.colorOffOpacity) / 255f;
                                        color.mul(System.currentTimeMillis() % 2000 <= 1000 ? col : colOff);
                                    }
                                    lastShader.setUniform("thingColor", color);
                                }

                                // Main render

                                GL11.glDrawElements(GL11.GL_TRIANGLES, model.vertexCount, GL11.GL_UNSIGNED_INT, 0L);

                                lastShader.setUniform("highlightMode", 0);

                                lastShader.setUniform("material", defaultMaterial);
                                unbind();
                            }
                }

        lastShader.setUniform("fogNear", -1);
        lastShader.setUniform("fogFar", -1);

        if(!(mainView.currentScreen instanceof ElementEditing))
            outline = false;

        lastShader = basicShader;
        lastShader.bind();

        lastShader.setUniform("projectionMatrix", projection);
        lastShader.setUniform("ambientLight", new Vector3f(1f, 1f, 1f));
        lastShader.setUniform("directionalLightsSize", 0);
        lastShader.setUniform("pointLightsSize", 0);
        lastShader.setUniform("spotLightsSize", 0);

        RenderMan.disableCulling();

        for (int i = 0; i < basicMeshes.size(); i++)
            if(basicMeshes.get(i) != null)
                {
                    Entity entity = basicMeshes.get(i);
                    ArrayList<Model> models = entity.getModel();

                    if(models != null)
                        for(Model model : models)
                            if (model != null)
                            {
                                bind(model, lastShader, false);
                                prepare(entity, camera, lastShader, model);

                                if (model.material.overlayColor != null) {
                                    lastShader.setUniform("highlightMode", 1);
                                    lastShader.setUniform("highlightColor", model.material.overlayColor);
                                }

                                lastShader.setUniform("bones", (cwlib.structs.things.Thing[])null, (Bone[])null);
                                lastShader.setUniform("hasBones", false);

                                GL11.glDrawElements(GL11.GL_TRIANGLES, model.vertexCount, GL11.GL_UNSIGNED_INT, 0L);

                                lastShader.setUniform("highlightMode", 0);

                                unbind();
                            }
                }

        lastShader = outlineFlatShader;
        lastShader.bind();

        lastShader.setUniform("projectionMatrix", projection);

        if(outline)
        {
            RenderMan.bindFrameBuffer(getOutlineFB()[0]);
            RenderMan.bindColorTex(getOutlineFB()[1]);
            RenderMan.bindDepthTex(getOutlineFB()[2]);

            GL11.glClearColor(0,0,0,0);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
            GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);

            for(int i : selInd)
            {
                Entity entity = entities.get(i);
                ArrayList<Model> models = entity.getModel();

                if(models != null)
                    for(Model model : models)
                        if (model != null)
                        {
                            boolean isThing = entity instanceof Thing;
                            bindNoCullColor(model, isThing);
                            Matrix4f mat = new Matrix4f(Transformation.createTransformationMatrix(entity));
                            lastShader.setUniform("transformationMatrix", mat);
                            lastShader.setUniform("viewMatrix", Transformation.getViewMatrix(camera));
                            lastShader.setUniform("bones", model.hasBones ? ((Thing)entity).getBones() : null, model.hasBones ? model.mesh.getBones() : null);
                            lastShader.setUniform("hasBones", model.hasBones);
                            //render entities to outline FBO
                            GL11.glDrawElements(GL11.GL_TRIANGLES, model.vertexCount, GL11.GL_UNSIGNED_INT, 0L);
                        }
            }

            unbindFrameBuffer();
        }

        GL11.glEnable(GL11.GL_DEPTH_TEST);

        entities.clear();
        basicMeshes.clear();
        directionalLights.clear();
        pointLights.clear();
        spotLights.clear();

        lastShader.unbind();
    }

    public void renderAfterPP(View3D mainView)
    {
        ShaderMan afterPPShader = basicShader;
        afterPPShader.bind();

        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glCullFace(GL11.GL_BACK);
        RenderMan.enableCulling();

        for(int i = 0; i < throughWallEntities.size(); i++)
            if(throughWallEntities.get(i) != null)
                try{
                    Entity entity = throughWallEntities.get(i);
                    ArrayList<Model> models = entity.getModel();
                    if(models != null)
                        for(Model model : models)
                            if (model != null) {

                                boolean isThing = entity instanceof Thing;
                                bindThroughWalls(model, afterPPShader, isThing);
                                prepare(entity, mainView.camera, afterPPShader, model);

                                if (entity.highlighted) {
                                    afterPPShader.setUniform("highlightMode", 2);
                                    afterPPShader.setUniform("brightnessMul", 0.5f);
                                }

                                afterPPShader.setUniform("bones", model.hasBones ? ((PRenderMesh)((Thing) entity).thing.getPart(Part.RENDER_MESH)).boneThings : null, model.hasBones ? model.mesh.getBones() : null);
                                afterPPShader.setUniform("hasBones", model.hasBones);

                                //Through wall

                                GL11.glDrawElements(GL11.GL_TRIANGLES, model.vertexCount, GL11.GL_UNSIGNED_INT, 0L);
                                afterPPShader.setUniform("highlightMode", 0);

                                unbind();
                            }
                }catch (Exception e){System.err.println("Failed rendering throughwall-entity " + i + ".");e.printStackTrace();}

        afterPPShader.unbind();
        throughWallEntities.clear();
    }

    public void bind(Model model, ShaderMan shader, boolean isThing)
    {
        GL30.glBindVertexArray(model.vao);
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        if(model.hasBones)
        {
            GL20.glEnableVertexAttribArray(3);
            GL20.glEnableVertexAttribArray(4);
        }
        GL20.glEnableVertexAttribArray(5);
        GL20.glEnableVertexAttribArray(6);

//        if(model.material.disableCulling || Config.NO_CULLING)
//            RenderMan.disableCulling();
//        else
//            RenderMan.enableCulling();

        shader.setUniform("material", model.material);

        if(Config.VIEWER_SHADING == 0 || !isThing)
        {
            if (model.material.textures != null) {
                for (int i = 0; i < 32; i++) {
                    GL13.glActiveTexture(GL45.GL_TEXTURE0 + i);
                    GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
                }
                for (int i = 0; i < model.material.texCount; i++)
                {
                    if(model.material.textures[i] == null)
                        continue;
                    GL13.glActiveTexture(GL45.GL_TEXTURE0 + i);
                    GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.material.textures[i].id);
                }
            }
        }
    }

    private static Material noCol = new Material(new Vector4f(0f, 0f, 0f, 1f), 0f);

    public void bindNoCullColor(Model model, boolean isThing)
    {
        GL30.glBindVertexArray(model.vao);
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        if(model.hasBones) {
            GL20.glEnableVertexAttribArray(3);
            GL20.glEnableVertexAttribArray(4);
        }
        GL20.glEnableVertexAttribArray(5);
        GL20.glEnableVertexAttribArray(6);

        RenderMan.disableCulling();
    }

    public void bindThroughWalls(Model model, ShaderMan shader, boolean isThing)
    {
        GL30.glBindVertexArray(model.vao);
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        if(model.hasBones) {
            GL20.glEnableVertexAttribArray(3);
            GL20.glEnableVertexAttribArray(4);
        }
        GL20.glEnableVertexAttribArray(5);
        GL20.glEnableVertexAttribArray(6);

        shader.setUniform("material", model.material);

        if(model.material.textures != null)
        {
            for (int i = 0; i < 32; i++) {
                GL13.glActiveTexture(GL45.GL_TEXTURE0 + i);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
            }
            for(int i = 0; i < model.material.texCount; i++)
            {
                GL13.glActiveTexture(GL13.GL_TEXTURE0 + i);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.material.textures[i].id);
            }
        }

    }

    public void unbind()
    {
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL20.glDisableVertexAttribArray(3);
        GL20.glDisableVertexAttribArray(4);

        if(isCW)
        {
            GL11.glFrontFace(GL11.GL_CCW);
            isCW = false;
        }
    }

    public void prepare(Entity entity, Camera camera, ShaderMan shader, Model model) {
        Texture[] tex = model.material.textures;
        int count = model.material.texCount;

        if((Config.VIEWER_SHADING == 0 || !(entity instanceof Thing)) && shader != solidShader)
        {
            if(tex != null)
            {
                for(int i = 0; i < count; i++)
                    shader.setUniform("textureSampler", i, i);
            }
            else
            {
                shader.setUniform("textureSampler", 0, 0);
            }

            for(int i = 0; i < model.material.gmatCount; i++)
                shader.setUniform("gmatMAP", model.material.gmatMAP[i], i);
            shader.setUniform("gmatCount", model.material.gmatCount);
        }
        Matrix4f mat = new Matrix4f(Transformation.createTransformationMatrix(entity));

        shader.setUniform("transformationMatrix", mat);
        shader.setUniform("viewMatrix", Transformation.getViewMatrix(camera));

        if (mat.determinant() < 0 && !isCW)
        {
            GL11.glFrontFace(GL11.GL_CW);
            isCW = true;
        }
    }

    boolean isCW = false;

    public void prepareMousePick(Object entity, Camera camera) {
        shaderMousePick.setUniform("transformationMatrix", Transformation.createTransformationMatrix((Entity) entity));
        shaderMousePick.setUniform("viewMatrix", Transformation.getViewMatrix(camera));
    }

    @Override
    public void cleanup() {
        thingShader.cleanup();
        solidShader.cleanup();
        shaderMousePick.cleanup();

//        for(Material material : LoadedData.loadedGfxMaterials.values())
//            if(material.customShader != null)
//                material.customShader.cleanup();
    }

    public void unbindFrameBuffer()
    {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }
}