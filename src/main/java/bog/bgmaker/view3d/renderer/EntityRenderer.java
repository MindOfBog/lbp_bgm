package bog.bgmaker.view3d.renderer;

import bog.bgmaker.Main;
import bog.bgmaker.view3d.Camera;
import bog.bgmaker.view3d.ObjectLoader;
import bog.bgmaker.view3d.core.*;
import bog.bgmaker.view3d.core.types.Entity;
import bog.bgmaker.view3d.managers.MouseInput;
import bog.bgmaker.view3d.managers.RenderMan;
import bog.bgmaker.view3d.managers.ShaderMan;
import bog.bgmaker.view3d.managers.WindowMan;
import bog.bgmaker.view3d.renderer.gui.ingredients.Quad;
import bog.bgmaker.view3d.renderer.gui.ingredients.TriStrip;
import bog.bgmaker.view3d.utils.Const;
import bog.bgmaker.view3d.utils.Transformation;
import bog.bgmaker.view3d.utils.Utils;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.*;

import java.awt.*;
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
        shader.createVertexShader(Utils.loadResource("/shaders/vertex.vs"));
        shader.createGeometryShader(Utils.loadResource("/shaders/geometry.gs"));
        shader.createFragmentShader(Utils.loadResource("/shaders/fragment.fs"));
        shader.link();
        shader.createUniform("textureSampler");
        shader.createUniform("transformationMatrix");
        shader.createUniform("projectionMatrix");
        shader.createUniform("viewMatrix");
        shader.createUniform("triangleOffset");
        shader.createUniform("extrusion");
        shader.createUniform("camPos");
        shader.createUniform("ambientLight");
        shader.createUniform("highlightMode");
        shader.createUniform("highlightColor");
        shader.createUniform("brightnessMul");
        shader.createMaterialUniform("material");
        shader.createUniform("specularPower");
        shader.createDirectionalLightListUniform("directionalLights", 5);
        shader.createUniform("directionalLightsSize");
        shader.createPointLightListUniform("pointLights", 70);
        shader.createUniform("pointLightsSize");
        shader.createSpotLightListUniform("spotLights", 70);
        shader.createUniform("spotLightsSize");

        shaderMousePick.createVertexShader(Utils.loadResource("/shaders/vertex.vs"));
        shaderMousePick.createGeometryShader(Utils.loadResource("/shaders/geometry.gs"));
        shaderMousePick.createFragmentShader(Utils.loadResource("/shaders/fragment_mouse.fs"));
        shaderMousePick.link();
        shaderMousePick.createUniform("transformationMatrix");
        shaderMousePick.createUniform("projectionMatrix");
        shaderMousePick.createUniform("viewMatrix");
        shaderMousePick.createUniform("triangleOffset");
        shaderMousePick.createUniform("extrusion");
        shaderMousePick.createUniform("camPos");
        shaderMousePick.createUniform("arrayIndex");

        shaderOutlineVertical.createFragmentShader(Utils.loadResource("/shaders/fragment_blur.fs"));
        shaderOutlineVertical.createVertexShader(Utils.loadResource("/shaders/vertex_blur_vert.vs"));
        shaderOutlineVertical.link();
        shaderOutlineVertical.createUniform("transformationMatrix");
        shaderOutlineVertical.createUniform("originalTexture");
        shaderOutlineVertical.createUniform("target");
        shaderOutlineVertical.createUniform("color");
        shaderOutlineVertical.createUniform("stipple");
        shaderOutlineVertical.createUniform("hasColor");

        shaderOutlineHorizontal.createFragmentShader(Utils.loadResource("/shaders/fragment_blur.fs"));
        shaderOutlineHorizontal.createVertexShader(Utils.loadResource("/shaders/vertex_blur_hor.vs"));
        shaderOutlineHorizontal.link();
        shaderOutlineHorizontal.createUniform("transformationMatrix");
        shaderOutlineHorizontal.createUniform("originalTexture");
        shaderOutlineHorizontal.createUniform("target");
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

        for (int i = 0; i < entities.size(); i++)
        {
            Entity entity = entities.get(i);
            ArrayList<Model> models = entity.getModel();
            shaderMousePick.setUniform("arrayIndex", new Vector2i(i + 1, 1));
            if(models != null && !models.isEmpty())
                for(Model model : models)
                    if(entity.testForMouse)
                    {
                        GL30.glBindVertexArray(model.vao);
                        GL20.glEnableVertexAttribArray(0);
                        GL20.glEnableVertexAttribArray(1);
                        GL20.glEnableVertexAttribArray(2);

                        RenderMan.disableCulling();

                        prepareMousePick(entity, camera);

                        // Mouse picker render to FBO

                        GL11.glDrawElements(GL11.GL_TRIANGLES, model.vertexCount, 5125, 0L);
                        hasMousePick = true;

                        unbind();
                    }
        }

        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        RenderMan.enableCulling();

        for (int i = 0; i < throughWallEntities.size(); i++)
        {
            Entity entity = throughWallEntities.get(i);
            ArrayList<Model> models = entity.getModel();
            shaderMousePick.setUniform("arrayIndex", new Vector2i(i + 1, 2));
            if(models != null && !models.isEmpty())
                for(Model model : models)
                    if(entity.testForMouse)
                    {
                        GL30.glBindVertexArray(model.vao);
                        GL20.glEnableVertexAttribArray(0);
                        GL20.glEnableVertexAttribArray(1);
                        GL20.glEnableVertexAttribArray(2);

                        prepareMousePick(entity, camera);

                        // Mouse picker render to FBO

                        GL11.glDrawElements(GL11.GL_TRIANGLES, model.vertexCount, 5125, 0L);
                        hasMousePick = true;

                        unbind();
                    }
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

        shader.bind();
        shader.setUniform("projectionMatrix", Main.window.updateProjectionMatrix());
        shader.setUniform("ambientLight", Const.AMBIENT_LIGHT);
        shader.setUniform("specularPower", Const.SPECULAR_POWER);
        shader.setUniform("directionalLights", directionalLights.toArray(DirectionalLight[]::new));
        shader.setUniform("directionalLightsSize", directionalLights.size());
        shader.setUniform("pointLights", pointLights);
        shader.setUniform("pointLightsSize", pointLights.size());
        shader.setUniform("spotLights", spotLights.toArray(SpotLight[]::new));
        shader.setUniform("spotLightsSize", spotLights.size());
        shader.setUniform("camPos", camera.pos);

        boolean outline = false;

        for (Entity entity : entities)
        {
            ArrayList<Model> models = entity.getModel();

            if(models != null && !models.isEmpty())
                for(Model model : models)
                {
                    bind(model);
                    prepare(entity, camera);

                    float[] elements = entity.transformation.get(new float[16]);

                    Vector3f col0 = new Vector3f(elements[0], elements[1], elements[2]);
                    Vector3f col1 = new Vector3f(elements[4], elements[5], elements[6]);
                    Vector3f col2 = new Vector3f(elements[8], elements[9], elements[10]);

                    boolean canDecompose = true;
                    if (col0.dot(col1) != 0.0f) canDecompose = false;
                    if (col1.dot(col2) != 0.0f) canDecompose = false;
                    if (col0.dot(col2) != 0.0f) canDecompose = false;

                    if(canDecompose)
                    {
                        RenderMan.disableCulling();
                    }
                    else{
                        Vector3f scale = new Vector3f(col0.length(), col1.length(), col2.length());

                        col0 = col0.div(scale.x);
                        col1 = col1.div(scale.y);
                        col2 = col2.div(scale.z);

                        if (col0.cross(col1, new Vector3f()).dot(col2) < 0.0f)
                            RenderMan.disableCulling();
                    }

                    if(entity.highlighted || entity.selected)
                        outline = true;

                    if(model.material.overlayColor != null)
                    {
                        shader.setUniform("highlightMode", 1);
                        shader.setUniform("highlightColor", model.material.overlayColor);
                    }

                    // Main render

                    GL11.glDrawElements(GL11.GL_TRIANGLES, model.vertexCount, 5125, 0L);

                    shader.setUniform("highlightMode", 0);
                    unbind();
                }
        }

        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        shader.setUniform("ambientLight", new Vector3f(1f, 1f, 1f));

        if(outline)
        {
            int vertSelectFB = initFrameBuffer();

            int vertWidth = Math.round(window.width * Const.OUTLINE_DISTANCE);
            int vertHeight = Math.round(window.height * Const.OUTLINE_DISTANCE);

            int vertSelectCT = initColorTex(vertWidth, vertHeight);

            GL11.glViewport(0, 0, vertWidth, vertHeight);

            for (Entity entity : entities)
            {
                ArrayList<Model> models = entity.getModel();

                if(models != null && !models.isEmpty() && entity.selected)
                    for(Model model : models)
                        if(!model.noRender)
                        {
                            bindNoCullColor(model, new Vector4f(252f / 255f, 173f / 255f, 3f / 255f, 1f));
                            prepare(entity, camera);

                            shader.setUniform("highlightMode", 0);

                            // Outline render to FBO

                            GL11.glDrawElements(GL11.GL_TRIANGLES, model.vertexCount, 5125, 0L);

                            unbind();
                        }
            }

            unbindFrameBuffer();

            GL11.glEnable(GL11.GL_STENCIL_TEST);
            GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);
            GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xff);
            GL11.glStencilMask(0xff);
            GL11.glColorMask(false, false, false, false);

            for (Entity entity : entities)
            {
                ArrayList<Model> models = entity.getModel();

                if(models != null && !models.isEmpty() && entity.selected)
                    for(Model model : models)
                        if(!model.noRender)
                        {
                            bindNoCullColor(model, new Vector4f(0f, 0f, 0f, 1f));
                            prepare(entity, camera);

                            shader.setUniform("highlightMode", 0);

                            // Stencil render for outline

                            GL11.glDrawElements(GL11.GL_TRIANGLES, model.vertexCount, 5125, 0L);

                            unbind();
                        }
            }

            GL11.glColorMask(true, true, true, true);

            GL11.glStencilFunc(GL11.GL_NOTEQUAL, 1, 0xff);
            GL11.glStencilMask(0x00);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);

            int horSelectFB = initFrameBuffer();

            int horWidth = Math.round(vertWidth * Const.OUTLINE_DISTANCE);
            int horHeight = Math.round(vertHeight * Const.OUTLINE_DISTANCE);

            int horSelectCT = initColorTex(horWidth, horHeight);

            GL11.glViewport(0, 0, horWidth, horHeight);

            drawOutline(vertSelectFB, vertSelectCT, shaderOutlineVertical, shader, false, horHeight * Const.OUTLINE_DISTANCE, Const.OUTLINE_COLOR);
            GL30.glDeleteFramebuffers(vertSelectFB);
            GL11.glDeleteTextures(vertSelectCT);

            unbindFrameBuffer();

            drawOutline(horSelectFB, horSelectCT, shaderOutlineHorizontal, shader, Const.STIPPLE_OUTLINE, horWidth * Const.OUTLINE_DISTANCE, Const.OUTLINE_COLOR);
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

        for(Entity entity : throughWallEntities)
            if(entity.getModel() != null && !entity.getModel().isEmpty())
                for (Model model : entity.getModel())
                    if(!model.noRender)
                    {
                        bindThroughWalls(model);
                        prepare(entity, camera);

                        if(entity.highlighted)
                        {
                            shader.setUniform("highlightMode", 2);
                            shader.setUniform("brightnessMul", 0.5f);
                        }

                        //Through wall

                        GL11.glDrawElements(GL11.GL_TRIANGLES, model.vertexCount, GL11.GL_UNSIGNED_INT, 0L);
                        shader.setUniform("highlightMode", 0);

                        unbind();
                    }

        throughWallEntities.clear();
        shader.unbind();
    }

    @Override
    public void bind(Model model)
    {
        GL30.glBindVertexArray(model.vao);
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);

        if(model.material.disableCulling || Const.NO_CULLING)
            RenderMan.disableCulling();
        else
            RenderMan.enableCulling();

        shader.setUniform("material", model.material);

        if(model.material.texture != null)
        {
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.material.texture.id);
        }

    }

    public void bindNoCullColor(Model model, Vector4f color)
    {
        GL30.glBindVertexArray(model.vao);
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);

        RenderMan.disableCulling();

        shader.setUniform("material", new Material(color, 0f));
    }

    public void bindThroughWalls(Model model)
    {
        GL30.glBindVertexArray(model.vao);
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);

        if(model.material.disableCulling)
            RenderMan.disableCulling();
        else
            RenderMan.enableCulling();

        shader.setUniform("material", model.material);

        if(model.material.texture != null)
        {
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.material.texture.id);
        }

    }

    @Override
    public void unbind()
    {
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }

    @Override
    public void prepare(Object entity, Camera camera) {
        shader.setUniform("textureSampler", 0);
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

    private void drawOutline(int frameBuffer, int colorTexture, ShaderMan outlineShader, ShaderMan prevShader, boolean stipple, float target, Color color)
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
