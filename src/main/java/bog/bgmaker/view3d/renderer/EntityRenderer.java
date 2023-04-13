package bog.bgmaker.view3d.renderer;

import bog.bgmaker.Main;
import bog.bgmaker.view3d.Camera;
import bog.bgmaker.view3d.ObjectLoader;
import bog.bgmaker.view3d.core.*;
import bog.bgmaker.view3d.core.types.Entity;
import bog.bgmaker.view3d.core.types.Mesh;
import bog.bgmaker.view3d.managers.MouseInput;
import bog.bgmaker.view3d.managers.RenderMan;
import bog.bgmaker.view3d.managers.ShaderMan;
import bog.bgmaker.view3d.managers.WindowMan;
import bog.bgmaker.view3d.renderer.gui.ingredients.Quad;
import bog.bgmaker.view3d.renderer.gui.ingredients.TriStrip;
import bog.bgmaker.view3d.utils.Const;
import bog.bgmaker.view3d.utils.Transformation;
import bog.bgmaker.view3d.utils.Utils;
import editor.gl.objects.Shader;
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
        shaderMousePick = new ShaderMan();
        shaderOutlineVertical = new ShaderMan();
        shaderOutlineHorizontal = new ShaderMan();
        this.window = window;
        this.loader = loader;
    }

    @Override
    public void init() throws Exception
    {
        shader.createVertexShader(Utils.loadResource("/shaders/vertex.glsl"));
        String shaderCode = Utils.loadResource("/shaders/fragment.glsl").replaceAll("//%&AMBIENTC",
                "if(material.hasTexture == 1)" +
                        "    {" +
                        "        for(int i = 0; i < samplerCount; i++)" +
                        "        {" +
                        "           ambientC = texture(textureSampler[i], fragTextureCoord);" +
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
    }

    @Override
    public void render(Camera camera, MouseInput mouseInput)
    {

        int mouseFrameBuffer = initFrameBufferINT();
        int mouseColorTexture = initColorTexINT();

        int mouseDepthTexture = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, mouseDepthTexture);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_DEPTH_COMPONENT, window.width, window.height, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (FloatBuffer) null);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, mouseDepthTexture, 0);

        GL11.glViewport(0, 0, window.width, window.height);

        shaderMousePick.bind();
        shaderMousePick.setUniform("projectionMatrix", Main.window.updateProjectionMatrix());

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
                                GL30.glBindVertexArray(model.vao);
                                GL20.glEnableVertexAttribArray(0);
                                GL20.glEnableVertexAttribArray(1);
                                GL20.glEnableVertexAttribArray(2);

                                if(entity instanceof Mesh ? ((Mesh)entity).skeleton != null : false)
                                {
                                    GL20.glEnableVertexAttribArray(3);
                                    GL20.glEnableVertexAttribArray(4);
                                }

                                RenderMan.disableCulling();

                                prepareMousePick(entity, camera);

                                shaderMousePick.setUniform("bones", entity instanceof Mesh ? ((Mesh)entity).skeleton : null);
                                shaderMousePick.setUniform("hasBones", entity instanceof Mesh ? ((Mesh)entity).skeleton != null : false);

                                // Mouse picker render to FBO

                                GL11.glDrawElements(GL11.GL_TRIANGLES, model.vertexCount, 5125, 0L);

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
                                GL30.glBindVertexArray(model.vao);
                                GL20.glEnableVertexAttribArray(0);
                                GL20.glEnableVertexAttribArray(1);
                                GL20.glEnableVertexAttribArray(2);

                                if(entity instanceof Mesh ? ((Mesh)entity).skeleton != null : false)
                                {
                                    GL20.glEnableVertexAttribArray(3);
                                    GL20.glEnableVertexAttribArray(4);
                                }

                                prepareMousePick(entity, camera);

                                shaderMousePick.setUniform("bones", entity instanceof Mesh ? ((Mesh)entity).skeleton : null);
                                shaderMousePick.setUniform("hasBones", entity instanceof Mesh ? ((Mesh)entity).skeleton != null : false);

                                // Mouse picker render to FBO

                                GL11.glDrawElements(GL11.GL_TRIANGLES, model.vertexCount, 5125, 0L);

                                unbind();
                            }
                }catch (Exception e){System.err.println("Failed rendering throughwall-entity " + i + " for the mouse picking shader.");}
        }

        unbindFrameBuffer();

        if(hasMousePick)
        {
            GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, mouseFrameBuffer);
            GL11.glReadBuffer(GL30.GL_COLOR_ATTACHMENT0);

            int[] buffer = new int[3];

            GL30.glReadPixels((int)mouseInput.currentPos.x, (int)(window.height - mouseInput.currentPos.y), 1, 1, GL30.GL_RGB_INTEGER, GL30.GL_INT, buffer);

            if(buffer[0] - 1 != -1)
            {
                if(buffer[1] == 1)
                    entities.get(buffer[0] - 1).highlighted = true;
                else if(buffer[1] == 2)
                    throughWallEntities.get(buffer[0] - 1).highlighted = true;
            }

            GL11.glReadBuffer(GL30.GL_NONE);
            GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, 0);
        }

        GL30.glDeleteFramebuffers(mouseFrameBuffer);
        GL11.glDeleteTextures(mouseColorTexture);
        GL11.glDeleteTextures(mouseDepthTexture);

        shaderMousePick.unbind();

        Matrix4f projection = window.updateProjectionMatrix();

        shader.bind();
        shader.setUniform("projectionMatrix", projection);
        shader.setUniform("ambientLight", Const.AMBIENT_LIGHT);
        shader.setUniform("specularPower", Const.SPECULAR_POWER);
        shader.setUniform("directionalLights", directionalLights.toArray(DirectionalLight[]::new));
        shader.setUniform("directionalLightsSize", directionalLights.size());
        shader.setUniform("pointLights", pointLights);
        shader.setUniform("pointLightsSize", pointLights.size());
        shader.setUniform("spotLights", spotLights.toArray(SpotLight[]::new));
        shader.setUniform("spotLightsSize", spotLights.size());

        boolean outline = false;

        ArrayList<Integer> outlineEntities = new ArrayList<>();

        ShaderMan lastShader = shader;
        ArrayList<Material> setUpMats = new ArrayList<>();

        for (int i = 0; i < entities.size(); i++)
            if(entities.get(i) != null)
                try
                {
                    Entity entity = entities.get(i);
                    ArrayList<Model> models = entity.getModel();
                    if(models != null)
                        for(Model model : models)
                            if (model != null) {
                                if (model.material == null || model.material.customShader == null) {
                                    if (!lastShader.equals(shader)) {
                                        lastShader.unbind();
                                        shader.bind();
                                        lastShader = shader;
                                    }
                                } else {
                                    if (!lastShader.equals(model.material.customShader)) {
                                        lastShader.unbind();
                                        model.material.customShader.bind();
                                        lastShader = model.material.customShader;
                                    }

                                    if(!setUpMats.contains(model.material))
                                    {
                                        model.material.setupUniforms(projection, directionalLights, pointLights, spotLights);
                                        setUpMats.add(model.material);
                                    }
                                }

                                boolean hasBones = entity instanceof Mesh ? ((Mesh) entity).skeleton != null : false;
                                bind(model, hasBones, lastShader);
                                prepare(entity, camera, lastShader, model);

                                float[] elements = entity.transformation.get(new float[16]);

                                Vector3f col0 = new Vector3f(elements[0], elements[1], elements[2]);
                                Vector3f col1 = new Vector3f(elements[4], elements[5], elements[6]);
                                Vector3f col2 = new Vector3f(elements[8], elements[9], elements[10]);

                                boolean canDecompose = true;
                                if (col0.dot(col1) != 0.0f) canDecompose = false;
                                if (col1.dot(col2) != 0.0f) canDecompose = false;
                                if (col0.dot(col2) != 0.0f) canDecompose = false;

                                if (canDecompose) {
                                    RenderMan.disableCulling();
                                } else {
                                    Vector3f scale = new Vector3f(col0.length(), col1.length(), col2.length());

                                    col0 = col0.div(scale.x);
                                    col1 = col1.div(scale.y);
                                    col2 = col2.div(scale.z);

                                    if (col0.cross(col1, new Vector3f()).dot(col2) < 0.0f)
                                        RenderMan.disableCulling();
                                }

                                if (entity.selected) {
                                    outline = true;
                                    outlineEntities.add(i);
                                }

                                if (model.material.overlayColor != null) {
                                    lastShader.setUniform("highlightMode", 1);
                                    lastShader.setUniform("highlightColor", model.material.overlayColor);
                                }

                                lastShader.setUniform("bones", entity instanceof Mesh ? ((Mesh) entity).skeleton : null);
                                lastShader.setUniform("hasBones", hasBones);

                                // Main render

                                GL11.glDrawElements(GL11.GL_TRIANGLES, model.vertexCount, 5125, 0L);

                                lastShader.setUniform("highlightMode", 0);
                                unbind();
                            }
                }catch (Exception e){System.err.println("Failed rendering entity " + i + ".");}

        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        shader.setUniform("ambientLight", new Vector3f(1f, 1f, 1f));

        if(outline)
        {
            int vertSelectFB = initFrameBuffer();

            int vertWidth = Math.round(window.width);
            int vertHeight = Math.round(window.height);

            int vertSelectCT = initColorTex(vertWidth, vertHeight);

            GL11.glViewport(0, 0, vertWidth, vertHeight);

            for (int i : outlineEntities)
                    try{
                        Entity entity = entities.get(i);
                        ArrayList<Model> models = entity.getModel();
                        if(models != null)
                            for(Model model : models)
                                if(model != null)
                                {
                                    boolean hasBones = entity instanceof Mesh ? ((Mesh)entity).skeleton != null : false;
                                    bindNoCullColor(model, new Vector4f(0f, 0f, 0f, 1f), hasBones);
                                    prepare(entity, camera, shader, model);

                                    shader.setUniform("highlightMode", 0);

                                    shader.setUniform("bones", hasBones ? ((Mesh)entity).skeleton : null);
                                    shader.setUniform("hasBones", hasBones);

                                    // Outline render to FBO

                                    GL11.glDrawElements(GL11.GL_TRIANGLES, model.vertexCount, 5125, 0L);

                                    unbind();
                                }
                    }catch (Exception e){System.err.println("Failed rendering entity " + i + " for outline fbo.");}

            unbindFrameBuffer();

            GL11.glEnable(GL11.GL_STENCIL_TEST);
            GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);
            GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xff);
            GL11.glStencilMask(0xff);
            GL11.glColorMask(false, false, false, false);

            drawOutline(vertSelectFB, vertSelectCT, shaderOutlineVertical, shader, true, 1, 0, Const.OUTLINE_COLOR);

            GL11.glColorMask(true, true, true, true);

            GL11.glStencilFunc(GL11.GL_NOTEQUAL, 1, 0xff);
            GL11.glStencilMask(0x00);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);

            int horSelectFB = initFrameBuffer();

            int horWidth = Math.round(vertWidth);
            int horHeight = Math.round(vertHeight);

            int horSelectCT = initColorTex(horWidth, horHeight);

            GL11.glViewport(0, 0, horWidth, horHeight);

            int radius = Math.round((Const.OUTLINE_DISTANCE * 2f - 1f) * 10f);

            drawOutline(vertSelectFB, vertSelectCT, shaderOutlineVertical, shader, false, horHeight, radius, Const.OUTLINE_COLOR);
            GL30.glDeleteFramebuffers(vertSelectFB);
            GL11.glDeleteTextures(vertSelectCT);

            unbindFrameBuffer();

            drawOutline(horSelectFB, horSelectCT, shaderOutlineHorizontal, shader, false, horWidth, radius, Const.OUTLINE_COLOR);
            GL30.glDeleteFramebuffers(horSelectFB);
            GL11.glDeleteTextures(horSelectCT);

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
        shader.setUniform("ambientLight", new Vector3f(1f, 1f, 1f));

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
                                model.material.setupUniforms(projection, directionalLights, pointLights, spotLights);
                                setUpMats.add(model.material);
                            }
                        }

                        boolean hasBones = entity instanceof Mesh ? ((Mesh) entity).skeleton != null : false;
                        bindThroughWalls(model, hasBones, lastShader);
                        prepare(entity, camera, lastShader, model);

                        if (entity.highlighted) {
                            lastShader.setUniform("highlightMode", 2);
                            lastShader.setUniform("brightnessMul", 0.5f);
                        }

                        lastShader.setUniform("bones", entity instanceof Mesh ? ((Mesh) entity).skeleton : null);
                        lastShader.setUniform("hasBones", hasBones);

                        //Through wall

                        GL11.glDrawElements(GL11.GL_TRIANGLES, model.vertexCount, GL11.GL_UNSIGNED_INT, 0L);
                        lastShader.setUniform("highlightMode", 0);

                        unbind();
                    }
                }catch (Exception e){System.err.println("Failed rendering throughwall-entity " + i + ".");}

        throughWallEntities.clear();
        shader.unbind();
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

        if(model.material.disableCulling || Const.NO_CULLING)
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

    public void bindNoCullColor(Model model, Vector4f color, boolean hasBones)
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

        RenderMan.disableCulling();

        shader.setUniform("material", new Material(color, 0f));
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
    }

    private void drawOutline(int frameBuffer, int colorTexture, ShaderMan outlineShader, ShaderMan prevShader, boolean stipple, float target, int radius, Color color)
    {
        prevShader.unbind();
        outlineShader.bind();

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, frameBuffer);
        GL11.glReadBuffer(GL30.GL_COLOR_ATTACHMENT0);

        if(!stipple)
        {
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        }

        Quad outlineFBO = new Quad(loader, colorTexture, new Vector2f(), new Vector2f(window.width, window.height), false);

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

    private void unbindFrameBuffer()
    {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        GL11.glViewport(0, 0, window.width, window.height);
    }
}
