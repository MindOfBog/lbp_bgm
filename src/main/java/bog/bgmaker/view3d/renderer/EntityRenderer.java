package bog.bgmaker.view3d.renderer;

import bog.bgmaker.Main;
import bog.bgmaker.view3d.Camera;
import bog.bgmaker.view3d.ObjectLoader;
import bog.bgmaker.view3d.core.*;
import bog.bgmaker.view3d.core.types.Entity;
import bog.bgmaker.view3d.core.types.Mesh;
import bog.bgmaker.view3d.mainWindow.LoadedData;
import bog.bgmaker.view3d.managers.MouseInput;
import bog.bgmaker.view3d.managers.RenderMan;
import bog.bgmaker.view3d.managers.ShaderMan;
import bog.bgmaker.view3d.managers.WindowMan;
import bog.bgmaker.view3d.renderer.gui.ingredients.Quad;
import bog.bgmaker.view3d.renderer.gui.ingredients.TriStrip;
import bog.bgmaker.view3d.utils.Config;
import bog.bgmaker.view3d.utils.Transformation;
import bog.bgmaker.view3d.utils.Utils;
import org.joml.*;
import org.lwjgl.opengl.*;

import java.awt.*;
import java.lang.Math;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;

/**
 * @author Bog
 */
public class EntityRenderer implements IRenderer{

    ShaderMan shader;
    ShaderMan solidShader;
    ShaderMan shaderMousePick;
    ShaderMan shaderOutlineVertical;
    ShaderMan shaderOutlineHorizontal;
    public ArrayList<Entity> entities;
    public ArrayList<DirectionalLight> directionalLights;
    public ArrayList<PointLight> pointLights;
    public ArrayList<SpotLight> spotLights;
    public ArrayList<Entity> throughWallEntities;
    public WindowMan window;
    public ObjectLoader loader;
    public EntityRenderer(ObjectLoader loader, WindowMan window) throws Exception
    {
        entities = new ArrayList<>();
        directionalLights = new ArrayList<>();
        pointLights = new ArrayList<>();
        spotLights = new ArrayList<>();
        throughWallEntities = new ArrayList<>();
        shader = new ShaderMan();
        solidShader = new ShaderMan();
        shaderMousePick = new ShaderMan();
        shaderOutlineVertical = new ShaderMan();
        shaderOutlineHorizontal = new ShaderMan();
        this.window = window;
        this.loader = loader;
    }

    @Override
    public void init() throws Exception
    {
        String vertex = Utils.loadResource("/shaders/vertex.glsl");

        shader.createVertexShader(vertex);
        String shaderCode = Utils.loadResource("/shaders/fragment.glsl").replaceAll("//%&AMBIENTC",
                "if(material.hasTexture == 1)" +
                        "    {" +
                        "        for(int i = 0; i < samplerCount; i++)" +
                        "        {" +
                        "           ambientC = texture(textureSampler[i], fragTextureCoord.xy);" +
                        "        }" +
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
                        "        ambientC = material.ambient[0];" +
                        "        diffuseC = material.diffuse[0];" +
                        "        specularC = material.specular[0];" +
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

        shader.createFragmentShader(shaderCode);
        shader.link();
        shader.createListUniform("textureSampler", 25);
        shader.createUniform("samplerCount");
        shader.createUniform("ambientLight");
        shader.createMaterialUniform("material");
        shader.createUniform("highlightMode");
        shader.createUniform("highlightColor");
        shader.createUniform("brightnessMul");
        shader.createUniform("specularPower");
        shader.createDirectionalLightListUniform("directionalLights", 5);
        shader.createUniform("directionalLightsSize");
        shader.createPointLightListUniform("pointLights", 50);
        shader.createUniform("pointLightsSize");
        shader.createSpotLightListUniform("spotLights", 50);
        shader.createUniform("spotLightsSize");

        shader.createUniform("transformationMatrix");
        shader.createUniform("projectionMatrix");
        shader.createUniform("viewMatrix");
        shader.createListUniform("bones", 100);
        shader.createUniform("hasBones");
        shader.createUniform("triangleOffset");

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
                "finalColor = pow(finalColor, vec3(0.4545));" +
                "ambientC = vec4((finalColor / 2) + vec3(0.25), 1.0); " +
                "diffuseC = ambientC;" +
                "specularC = ambientC;");
//                "vec3 normal = normalize(fragNormal);" +
//                        "vec3 cameraDirection = normalize(-fragPos);" +
//                        "float dotProduct = dot(normal, -normalize(vec3(0.5, 0.5, -0.6)));" +
//                        "float specularDotProduct = dot(normal, cameraDirection);" +
//                        "float diffuseShade = max(dotProduct, 0.0);" +
//                        "float specularShade = pow(max(specularDotProduct, 0.0), 32.0);" +
//                        "vec3 diffuseColor = vec3(0.8, 0.8, 0.8) * diffuseShade;" +
//                        "vec3 specularColor = vec3(1.0, 1.0, 1.0) * specularShade * 0.3;" +
//                        "vec3 finalColor = diffuseColor + specularColor;" +
//                        "finalColor = pow(finalColor, vec3(0.4545));" +
//                        "ambientC = vec4((finalColor / 2) + vec3(0.25), 1.0); " +
//                        "diffuseC = ambientC;" +
//                        "specularC = ambientC;");

        solidShader.createFragmentShader(solidShaderCode);
        solidShader.link();
        solidShader.createUniform("ambientLight");
        solidShader.createUniform("specularPower");
        solidShader.createDirectionalLightListUniform("directionalLights", 5);
        solidShader.createUniform("directionalLightsSize");
        solidShader.createPointLightListUniform("pointLights", 50);
        solidShader.createUniform("pointLightsSize");
        solidShader.createSpotLightListUniform("spotLights", 50);
        solidShader.createUniform("spotLightsSize");

        solidShader.createUniform("transformationMatrix");
        solidShader.createUniform("projectionMatrix");
        solidShader.createUniform("viewMatrix");
        solidShader.createListUniform("bones", 100);
        solidShader.createUniform("hasBones");
        solidShader.createUniform("triangleOffset");

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

        shaderOutlineVertical.createFragmentShader(Utils.loadResource("/shaders/fragment_blur.glsl"));
        shaderOutlineVertical.createVertexShader(Utils.loadResource("/shaders/vertex_blur_vert.glsl"));
        shaderOutlineVertical.link();
        shaderOutlineVertical.createUniform("transformationMatrix");
        shaderOutlineVertical.createUniform("originalTexture");
        shaderOutlineVertical.createUniform("target");
        shaderOutlineVertical.createUniform("radius");
        shaderOutlineVertical.createUniform("color");
        shaderOutlineVertical.createUniform("stipple");
        shaderOutlineVertical.createUniform("hasColor");

        shaderOutlineHorizontal.createFragmentShader(Utils.loadResource("/shaders/fragment_blur.glsl"));
        shaderOutlineHorizontal.createVertexShader(Utils.loadResource("/shaders/vertex_blur_hor.glsl"));
        shaderOutlineHorizontal.link();
        shaderOutlineHorizontal.createUniform("transformationMatrix");
        shaderOutlineHorizontal.createUniform("originalTexture");
        shaderOutlineHorizontal.createUniform("target");
        shaderOutlineHorizontal.createUniform("radius");
        shaderOutlineHorizontal.createUniform("color");
        shaderOutlineHorizontal.createUniform("stipple");
        shaderOutlineHorizontal.createUniform("hasColor");

        GL11.glEnable(GL11.GL_LINE_SMOOTH);

        outlineFB = initFrameBuffer();
        outlineCT = initColorTex(window.width, window.height);
        mouseFB = initFrameBufferINT();
        mouseCT = initColorTexINT();
        mouseDT = initDepthTex();
    }

    int outlineFB = -1;
    int outlineCT = -1;
    int mouseFB = -1;
    int mouseCT = -1;
    int mouseDT = -1;

    public void resize()
    {
        GL11.glViewport(0, 0, window.width, window.height);
        GL11.glDeleteTextures(outlineCT);
        outlineCT = initColorTex(window.width, window.height);
    }

    @Override
    public void render(Camera camera, MouseInput mouseInput)
    {
        Matrix4f projection = window.updateProjectionMatrix();

        bindFrameBuffer(mouseFB);
        bindColorTex(mouseCT);
        bindDepthTex(mouseDT);

        GL11.glClearColor(0,0,0,0);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

        shaderMousePick.bind();
        shaderMousePick.setUniform("projectionMatrix", projection);

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);

        boolean hasMousePick = false;

        ArrayList<Integer> testForEntities = new ArrayList<>();

        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            if (entity != null)
                if (entity.testForMouse)
                {
                    testForEntities.add(i);
                    hasMousePick = true;
                }
        }

        for (int i : testForEntities)
        {
            Entity entity = entities.get(i);
            shaderMousePick.setUniform("arrayIndex", new Vector2i(i + 1, 1));
            if(entity != null)
                try
                {
                    ArrayList<Model> models = entity.getModel();
                    if(models != null)
                        for(Model model : models)
                            if(model != null)
                            {
                                boolean hasBones = entity instanceof Mesh ? ((Mesh) entity).skeleton != null : false;
                                GL30.glBindVertexArray(model.vao);
                                GL20.glEnableVertexAttribArray(0);
                                GL20.glEnableVertexAttribArray(1);
                                GL20.glEnableVertexAttribArray(2);
                                if(hasBones)
                                {
                                    GL20.glEnableVertexAttribArray(3);
                                    GL20.glEnableVertexAttribArray(4);
                                }
                                GL20.glEnableVertexAttribArray(5);

                                RenderMan.disableCulling();

                                prepareMousePick(entity, camera);

                                shaderMousePick.setUniform("bones", entity instanceof Mesh ? ((Mesh)entity).skeleton : null);
                                shaderMousePick.setUniform("hasBones", hasBones);

                                // Mouse picker render to FBO

                                GL11.glDrawElements(GL11.GL_TRIANGLES, model.vertexCount, GL11.GL_UNSIGNED_INT, 0L);

                                unbind();
                            }
                }catch (Exception e){System.err.println("Failed rendering entity " + i + " for the mouse picking shader.");}
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
                try{
                    ArrayList<Model> models = entity.getModel();
                    if(models != null)
                        for(Model model : models)
                            if(model != null)
                            {
                                boolean hasBones = entity instanceof Mesh ? ((Mesh) entity).skeleton != null : false;
                                GL30.glBindVertexArray(model.vao);
                                GL20.glEnableVertexAttribArray(0);
                                GL20.glEnableVertexAttribArray(1);
                                GL20.glEnableVertexAttribArray(2);
                                if(hasBones)
                                {
                                    GL20.glEnableVertexAttribArray(3);
                                    GL20.glEnableVertexAttribArray(4);
                                }
                                GL20.glEnableVertexAttribArray(5);

                                prepareMousePick(entity, camera);

                                shaderMousePick.setUniform("bones", entity instanceof Mesh ? ((Mesh)entity).skeleton : null);
                                shaderMousePick.setUniform("hasBones", hasBones);

                                // Mouse picker render to FBO

                                GL11.glDrawElements(GL11.GL_TRIANGLES, model.vertexCount, GL11.GL_UNSIGNED_INT, 0L);

                                unbind();
                            }
                }catch (Exception e){System.err.println("Failed rendering throughwall-entity " + i + " for the mouse picking shader.");}
        }

        unbindFrameBuffer();

        if(hasMousePick)
        {
            GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, mouseFB);
            GL11.glReadBuffer(GL30.GL_COLOR_ATTACHMENT0);

            int[] buffer = new int[3];

            GL30.glReadPixels((int)mouseInput.currentPos.x, (int)(window.height - mouseInput.currentPos.y), 1, 1, GL30.GL_RGB_INTEGER, GL30.GL_INT, buffer);

            int outX = buffer[0];
            int outY = buffer[1];

            try
            {
                if(outX - 1 != -1)
                {
                    if(outY == 1)
                        entities.get(outX - 1).highlighted = true;
                    else if(outY == 2)
                        throughWallEntities.get(outX - 1).highlighted = true;
                }
            }catch (Exception e){}

            GL11.glReadBuffer(GL30.GL_NONE);
            GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, 0);
        }

        unbindFrameBuffer();

        shaderMousePick.unbind();

        shader.bind();
        shader.setUniform("projectionMatrix", projection);
        shader.setUniform("ambientLight", Config.AMBIENT_LIGHT);
        shader.setUniform("specularPower", Config.SPECULAR_POWER);
        shader.setUniform("directionalLights", directionalLights.toArray(DirectionalLight[]::new));
        shader.setUniform("directionalLightsSize", directionalLights.size());
        shader.setUniform("pointLights", pointLights);
        shader.setUniform("pointLightsSize", pointLights.size());
        shader.setUniform("spotLights", spotLights.toArray(SpotLight[]::new));
        shader.setUniform("spotLightsSize", spotLights.size());
        shader.unbind();

        solidShader.bind();
        solidShader.setUniform("projectionMatrix", projection);
        solidShader.setUniform("ambientLight", Config.AMBIENT_LIGHT);
        solidShader.setUniform("specularPower", Config.SPECULAR_POWER);
        solidShader.setUniform("directionalLights", directionalLights.toArray(DirectionalLight[]::new));
        solidShader.setUniform("directionalLightsSize", directionalLights.size());
        solidShader.setUniform("pointLights", pointLights);
        solidShader.setUniform("pointLightsSize", pointLights.size());
        solidShader.setUniform("spotLights", spotLights.toArray(SpotLight[]::new));
        solidShader.setUniform("spotLightsSize", spotLights.size());
        solidShader.unbind();

        boolean outline = false;

        ShaderMan lastShader = shader;
        lastShader.bind();
        ArrayList<Material> setUpMats = new ArrayList<>();

        ArrayList<Integer> selInd = new ArrayList<>();

        if(!Config.MATERIAL_PREVIEW_SHADING)
        {
            if (!lastShader.equals(solidShader)) {
                lastShader.unbind();
                lastShader = solidShader;
                lastShader.bind();
            }
        }

        for (int i = 0; i < entities.size(); i++)
            if(entities.get(i) != null)
                try
                {
                    Entity entity = entities.get(i);
                    ArrayList<Model> models = entity.getModel();

                    if (entity.selected)
                        outline = true;

                    if(models != null)
                        for(Model model : models)
                            if (model != null) {

                                if(Config.MATERIAL_PREVIEW_SHADING)
                                {
                                    if (model.material == null || model.material.customShader == null) {
                                        if (!lastShader.equals(shader)) {
                                            lastShader.unbind();
                                            lastShader = shader;
                                            lastShader.bind();
                                        }
                                    } else {
                                        if (!lastShader.equals(model.material.customShader)) {
                                            lastShader.unbind();
                                            lastShader = model.material.customShader;
                                            lastShader.bind();
                                        }
                                        if (!setUpMats.contains(model.material)) {
                                            model.material.setupUniforms(projection, camera, directionalLights, pointLights, spotLights);
                                            setUpMats.add(model.material);
                                        }
                                    }
                                }

                                boolean hasBones = entity instanceof Mesh ? ((Mesh) entity).skeleton != null : false;
                                bind(model, hasBones, lastShader);
                                prepare(entity, camera, lastShader, model);

                                if (Transformation.isMatrixParseable(entity.transformation)) {
                                    int negativeScaleChannels = 0;
                                    if (entity.transformation.getScale(new Vector3f()).x < 0)
                                        negativeScaleChannels++;
                                    if (entity.transformation.getScale(new Vector3f()).y < 0)
                                        negativeScaleChannels++;
                                    if (entity.transformation.getScale(new Vector3f()).z < 0)
                                        negativeScaleChannels++;

                                    if (negativeScaleChannels == 1 || negativeScaleChannels == 3)
                                        GL11.glCullFace(GL11.GL_FRONT);
                                }
                                else
                                    RenderMan.disableCulling();

                                if (model.material.overlayColor != null && Config.MATERIAL_PREVIEW_SHADING) {
                                    lastShader.setUniform("highlightMode", 1);
                                    lastShader.setUniform("highlightColor", model.material.overlayColor);
                                }

                                lastShader.setUniform("bones", entity instanceof Mesh ? ((Mesh) entity).skeleton : null);
                                lastShader.setUniform("hasBones", hasBones);

                                // Main render

                                GL11.glDrawElements(GL11.GL_TRIANGLES, model.vertexCount, GL11.GL_UNSIGNED_INT, 0L);

                                if(entity.selected)
                                    selInd.add(i);

                                if(Config.MATERIAL_PREVIEW_SHADING)
                                    lastShader.setUniform("highlightMode", 0);

                                unbind();
                            }
                }catch (Exception e){System.err.println("Failed rendering entity " + i + ".");e.printStackTrace();}

        if (!lastShader.equals(shader)) {
            lastShader.unbind();
            lastShader = shader;
            lastShader.bind();
        }
        bindFrameBuffer(outlineFB);
        bindColorTex(outlineCT);
        for(int i : selInd)
            try
            {
                Entity entity = entities.get(i);
                ArrayList<Model> models = entity.getModel();

                if(models != null)
                    for(Model model : models)
                        if (model != null)
                        {
                            boolean hasBones = entity instanceof Mesh ? ((Mesh) entity).skeleton != null : false;
                            bindNoCullColor(model, hasBones);
                            lastShader.setUniform("material", noCol);
                            prepare(entity, camera, lastShader, model);
                            lastShader.setUniform("bones", hasBones ? ((Mesh) entity).skeleton : null);
                            lastShader.setUniform("hasBones", hasBones);
                            //render entities to outline to FBO
                            GL11.glDrawElements(GL11.GL_TRIANGLES, model.vertexCount, GL11.GL_UNSIGNED_INT, 0L);

                        }
            }catch (Exception e){System.err.println("Failed rendering entity " + i + ".");e.printStackTrace();}
        unbindFrameBuffer();

        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);

        if(outline)
        {
            GL11.glEnable(GL11.GL_STENCIL_TEST);
            GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);
            GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xff);
            GL11.glStencilMask(0xff);

            GL11.glColorMask(false, false, false, false);

            //render FBO for stencil
            drawOutline(outlineFB, outlineCT, shaderOutlineVertical, lastShader, true, 1, 0, Config.OUTLINE_COLOR);

            GL11.glColorMask(true, true, true, true);

            GL11.glStencilFunc(GL11.GL_NOTEQUAL, 1, 0xff);
            GL11.glStencilMask(0x00);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);

            int radius = Math.round((Config.OUTLINE_DISTANCE * 2f - 1f) * 10f);

            bindFrameBuffer(outlineFB);
            drawOutline(outlineFB, outlineCT, shaderOutlineVertical, lastShader, false, window.height, radius, Config.OUTLINE_COLOR);
            unbindFrameBuffer();
            drawOutline(outlineFB, outlineCT, shaderOutlineHorizontal, lastShader, false, window.width, radius, Config.OUTLINE_COLOR);

            bindFrameBuffer(outlineFB);
            bindColorTex(outlineCT);
            GL11.glClearColor(0,0,0,0);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
            unbindFrameBuffer();

            GL11.glStencilMask(0xff);
            GL11.glStencilFunc(GL11.GL_ALWAYS, 0, 0xff);
        }

        GL11.glEnable(GL11.GL_DEPTH_TEST);

        entities.clear();
        directionalLights.clear();
        pointLights.clear();
        spotLights.clear();

        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glCullFace(GL11.GL_BACK);

        lastShader.setUniform("ambientLight", new Vector3f(1f, 1f, 1f));
        lastShader.setUniform("directionalLightsSize", 0);
        lastShader.setUniform("pointLightsSize", 0);
        lastShader.setUniform("spotLightsSize", 0);

        for(int i = 0; i < throughWallEntities.size(); i++)
            if(throughWallEntities.get(i) != null)
                try{
                    Entity entity = throughWallEntities.get(i);
                    ArrayList<Model> models = entity.getModel();
                    if(models != null)
                        for(Model model : models)
                            if (model != null) {
                        if (model.material == null || model.material.customShader == null) {
                            if (lastShader != shader) {
                                lastShader.unbind();
                                shader.bind();
                                lastShader = shader;
                            }
                        } else {
                            if (lastShader != model.material.customShader) {
                                lastShader.unbind();
                                model.material.customShader.bind();
                                lastShader = model.material.customShader;
                            }

                            if(!setUpMats.contains(model.material))
                            {
                                model.material.setupUniforms(projection, camera, directionalLights, pointLights, spotLights);
                                setUpMats.add(model.material);
                            }
                            model.material.setupUniformsThroughwall();
                        }

                        boolean hasBones = entity instanceof Mesh ? ((Mesh) entity).skeleton != null : false;
                        bindThroughWalls(model, hasBones, lastShader);
                        prepare(entity, camera, lastShader, model);

                        if (entity.highlighted) {
                            lastShader.setUniform("highlightMode", 2);
                            lastShader.setUniform("brightnessMul", 0.5f);
                        }

                        lastShader.setUniform("bones", hasBones ? ((Mesh) entity).skeleton : null);
                        lastShader.setUniform("hasBones", hasBones);

                        //Through wall

                        GL11.glDrawElements(GL11.GL_TRIANGLES, model.vertexCount, GL11.GL_UNSIGNED_INT, 0L);
                        lastShader.setUniform("highlightMode", 0);

                        unbind();
                    }
                }catch (Exception e){System.err.println("Failed rendering throughwall-entity " + i + ".");}

        throughWallEntities.clear();
        lastShader.unbind();
    }

    @Override
    public void bind(Model model, boolean hasBones, ShaderMan shader)
    {
        GL30.glBindVertexArray(model.vao);
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        if(hasBones)
        {
            GL20.glEnableVertexAttribArray(3);
            GL20.glEnableVertexAttribArray(4);
        }
        GL20.glEnableVertexAttribArray(5);

        if(model.material.disableCulling || Config.NO_CULLING)
            RenderMan.disableCulling();
        else
            RenderMan.enableCulling();

        if(Config.MATERIAL_PREVIEW_SHADING)
        {
            shader.setUniform("material", model.material);

            if (model.material.textures != null) {
                for (int i = 0; i < model.material.textures.length; i++) {
                    GL13.glActiveTexture(GL13.GL_TEXTURE0 + i);
                    GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.material.textures[i].id);
                }
            }
        }
    }

    private static Material noCol = new Material(new Vector4f(0f, 0f, 0f, 1f), 0f);
    public void bindNoCullColor(Model model, boolean hasBones)
    {
        GL30.glBindVertexArray(model.vao);
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        if(hasBones)
        {
            GL20.glEnableVertexAttribArray(3);
            GL20.glEnableVertexAttribArray(4);
        }
        GL20.glEnableVertexAttribArray(5);

        RenderMan.disableCulling();
    }

    public void bindThroughWalls(Model model, boolean hasBones, ShaderMan shader)
    {
        GL30.glBindVertexArray(model.vao);
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        if(hasBones)
        {
            GL20.glEnableVertexAttribArray(3);
            GL20.glEnableVertexAttribArray(4);
        }
        GL20.glEnableVertexAttribArray(5);

        if(model.material.disableCulling)
            RenderMan.disableCulling();
        else
            RenderMan.enableCulling();

        shader.setUniform("material", model.material);

        if(model.material.textures != null)
        {
            for(int i = 0; i < model.material.textures.length; i++)
            {
                GL13.glActiveTexture(GL13.GL_TEXTURE0 + i);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.material.textures[i].id);
            }
        }

    }

    @Override
    public void unbind()
    {
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL20.glDisableVertexAttribArray(3);
        GL20.glDisableVertexAttribArray(4);
        GL30.glBindVertexArray(0);
    }

    @Override
    public void prepare(Object entity, Camera camera, ShaderMan shader, Model model) {
        Texture[] tex = model.material.textures;
        if(Config.MATERIAL_PREVIEW_SHADING)
        {
            if(tex != null)
            {
                for (int i = 0; i < tex.length; i++)
                    shader.setUniform("textureSampler", i, i);
                shader.setUniform("samplerCount", tex.length);
            }
            else
            {
                shader.setUniform("textureSampler", 0, 0);
                shader.setUniform("samplerCount", 1);
            }
        }
        shader.setUniform("transformationMatrix", Transformation.createTransformationMatrix((Entity) entity));
        shader.setUniform("viewMatrix", Transformation.getViewMatrix(camera));
    }

    public void prepareMousePick(Object entity, Camera camera) {
        shaderMousePick.setUniform("transformationMatrix", Transformation.createTransformationMatrix((Entity) entity));
        shaderMousePick.setUniform("viewMatrix", Transformation.getViewMatrix(camera));
    }

    @Override
    public void cleanup() {
        shader.cleanup();
        solidShader.cleanup();
        shaderOutlineHorizontal.cleanup();
        shaderOutlineVertical.cleanup();
        shaderMousePick.cleanup();

        for(Material material : LoadedData.loadedGfxMaterials.values())
            if(material.customShader != null)
                material.customShader.cleanup();
    }

    private void drawOutline(int frameBuffer, int colorTexture, ShaderMan outlineShader, ShaderMan prevShader, boolean stipple, float target, int radius, Color color)
    {
        prevShader.unbind();
        outlineShader.bind();

        if(!stipple)
        {
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        }

        Quad outlineFBO = new Quad(loader, colorTexture, new Vector2f(), new Vector2f(window.width, window.height));

        GL30.glBindVertexArray(outlineFBO.model.vao);
        GL20.glEnableVertexAttribArray(0);

        if(outlineFBO.hasTexCoords)
            GL20.glEnableVertexAttribArray(1);

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, outlineFBO.texture);

        outlineShader.setUniform("originalTexture", 0);
        outlineShader.setUniform("target", target);
        outlineShader.setUniform("radius", radius);
        outlineShader.setUniform("transformationMatrix", Transformation.createTransformationMatrix(
                new Vector2f(outlineFBO.pos.x / (window.width/2f) - 1 + outlineFBO.scale.x / window.width, outlineFBO.pos.y / (window.height/2f) - 1 + outlineFBO.scale.y / window.height),
                new Vector2f(outlineFBO.scale.x / window.width, outlineFBO.scale.y / window.height)));

        outlineShader.setUniform("color", new Vector3f(color.getRed() / 255f,color.getGreen() / 255f,color.getBlue() / 255f));
        outlineShader.setUniform("stipple", stipple);
        outlineShader.setUniform("hasColor", true);

        GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, outlineFBO.model.vertexCount);

        if(!outlineFBO.staticVAO)
        {
            int vao = ((TriStrip)outlineFBO).model.vao;
            GL30.glDeleteVertexArrays(vao);
            loader.vaos.remove((Object)vao);
        }
        if(!outlineFBO.staticVBO)
        {
            int[] vbos = ((TriStrip)outlineFBO).model.vbos;
            for(int vbo : vbos)
            {
                GL30.glDeleteBuffers(vbo);
                loader.vbos.remove((Object)vbo);
            }
        }

        if(!stipple)
        {
            GL11.glDisable(GL11.GL_BLEND);
        }

        outlineShader.unbind();
        prevShader.bind();
    }

    private int initFrameBuffer()
    {
        int frameBuffer = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer);
        GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, frameBuffer);

        return frameBuffer;
    }

    private void bindFrameBuffer(int frameBuffer)
    {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer);
        GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, frameBuffer);
    }

    private int initColorTex(int width, int height)
    {
        int colorTexture = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorTexture);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, colorTexture, 0);
        return colorTexture;
    }

    private void bindColorTex(int colorTexture)
    {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorTexture);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, colorTexture, 0);
    }

    private void bindDepthTex(int mouseDepthTexture)
    {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, mouseDepthTexture);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, mouseDepthTexture, 0);
    }

    private int initFrameBufferINT()
    {
        int frameBuffer = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer);
        GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);

        return frameBuffer;
    }

    private int initColorTexINT()
    {
        int colorTexture = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorTexture);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_RGB32I, window.width, window.height, 0, GL30.GL_RGB_INTEGER, GL11.GL_INT, (int[]) null);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, colorTexture, 0);
        return colorTexture;
    }

    private int initDepthTex()
    {
        int mouseDepthTexture = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, mouseDepthTexture);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_DEPTH_COMPONENT, window.width, window.height, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (FloatBuffer) null);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, mouseDepthTexture, 0);
        return mouseDT;
    }

    private void unbindFrameBuffer()
    {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }
}
