package bog.lbpas.view3d.managers;

import bog.lbpas.Main;
import bog.lbpas.view3d.mainWindow.screens.Settings;
import bog.lbpas.view3d.managers.assetLoading.ObjectLoader;
import bog.lbpas.view3d.core.DirectionalLight;
import bog.lbpas.view3d.core.Model;
import bog.lbpas.view3d.core.PointLight;
import bog.lbpas.view3d.core.SpotLight;
import bog.lbpas.view3d.core.types.Entity;
import bog.lbpas.view3d.mainWindow.View3D;
import bog.lbpas.view3d.renderer.EntityRenderer;
import bog.lbpas.view3d.renderer.gui.GuiRenderer;
import bog.lbpas.view3d.renderer.gui.elements.Checkbox;
import bog.lbpas.view3d.renderer.gui.font.FontRenderer;
import bog.lbpas.view3d.renderer.gui.ingredients.*;
import bog.lbpas.view3d.utils.*;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;
import org.lwjgl.opengl.*;

import java.awt.*;
import java.nio.ByteBuffer;

/**
 * @author Bog
 */
public class RenderMan {

    public WindowMan window;
    public EntityRenderer entityRenderer;
    public GuiRenderer guiRenderer;
    private static boolean isCulling = false;

    public RenderMan(WindowMan window)
    {
        this.window = window;
        try {
            shaderOutline = new ShaderMan("shaderOutline");
            shaderSSAO = new ShaderMan("shaderSSAO");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init(ObjectLoader loader) throws Exception
    {
        entityRenderer = new EntityRenderer(loader, this.window)
        {
            @Override
            public void unbindFrameBuffer() {
                bindFrameBuffer(screenBuffer);
                bindColorTex(screenTexture);
                bindDepthTex(screenDepth);
            }

            @Override
            public int[] getOutlineFB() {
                return new int[]{outlineFB, outlineCT, outlineDT};
            }
        };
        entityRenderer.init();
        guiRenderer = new GuiRenderer(loader, this.window)
        {
            @Override
            public void unbindFrameBuffer() {
                bindFrameBuffer(screenBuffer);
                bindColorTex(screenTexture);
                bindDepthTex(screenDepth);
            }
        };
        guiRenderer.init();
        screenBuffer = initFrameBuffer();
        screenTexture = initColorTex(window);
        screenDepth = initDepthTex(window);

        blurBuffer = initFrameBuffer();
        blurTexture = initColorTex(window);
        blurDepth = initDepthTex(window);

        outlineFB = initFrameBuffer();
        outlineCT = initColorTex(window);
        outlineDT = initDepthTex(window);
        outlineFB2 = initFrameBuffer();
        outlineCT2 = initColorTex(window);
        outlineDT2 = initDepthTex(window);

        ssaoBuffer = initFrameBuffer();
        ssaoTexture = initColorTex(window);
        ssaoDepth = initDepthTex(window);
        ssaoBuffer2 = initFrameBuffer();
        ssaoTexture2 = initColorTex(window);
        ssaoDepth2 = initDepthTex(window);

        shaderOutline.createFragmentShader(Utils.loadResource("/shaders/fragment_outline.glsl"));
        shaderOutline.createVertexShader(Utils.loadResource("/shaders/vertex_outline.glsl"));
        shaderOutline.link();
        shaderOutline.createUniform("originalTexture");
        shaderOutline.createUniform("hasMask");
        shaderOutline.createUniform("maskTexture");
        shaderOutline.createUniform("pixelSize");
        shaderOutline.createUniform("radius");
        shaderOutline.createUniform("color");
        shaderOutline.createUniform("vertical");

        shaderSSAO.createFragmentShader(Utils.loadResource("/shaders/fragment_ssao.glsl"));
        shaderSSAO.createVertexShader(Utils.loadResource("/shaders/vertex_gui.glsl"));
        shaderSSAO.link();
        shaderSSAO.createUniform("transformationMatrix");
        shaderSSAO.createUniform("hasCoords");
        shaderSSAO.createUniform("depthTexture");
        shaderSSAO.createUniform("colorTexture");
        shaderSSAO.createUniform("zRatio");
        shaderSSAO.createUniform("camerarange");
        shaderSSAO.createUniform("screensize");
        shaderSSAO.createUniform("fogColor");
        shaderSSAO.createUniform("start");
    }

    int screenBuffer = -1;
    int screenTexture = -1;
    int screenDepth = -1;

    int blurBuffer = -1;
    int blurTexture = -1;
    int blurDepth = -1;

    int outlineFB = -1;
    int outlineCT = -1;
    int outlineDT = -1;
    int outlineFB2 = -1;
    int outlineCT2 = -1;
    int outlineDT2 = -1;

    int ssaoBuffer = -1;
    int ssaoTexture = -1;
    int ssaoDepth = -1;

    int ssaoBuffer2 = -1;
    int ssaoTexture2 = -1;
    int ssaoDepth2 = -1;

    ShaderMan shaderOutline;

    ShaderMan shaderSSAO;

    public void resize()
    {
        entityRenderer.resize();
        GL11.glDeleteTextures(new int[]{screenTexture, screenDepth, blurTexture, blurDepth, outlineCT, outlineDT, outlineDT2, ssaoTexture, ssaoDepth});
        GL11.glViewport(0, 0, window.width, window.height);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, screenBuffer);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, outlineFB);
        outlineCT = RenderMan.initColorTex(window);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, outlineFB2);
        outlineCT2 = RenderMan.initColorTex(window);
        screenTexture = initColorTex(window);
        screenDepth = initDepthTex(window);
        blurTexture = initColorTex(window);
        blurDepth = initDepthTex(window);
        outlineDT = RenderMan.initDepthTex(window);
        outlineDT2 = RenderMan.initDepthTex(window);
        ssaoTexture = initColorTex(window);
        ssaoDepth = initDepthTex(window);
        ssaoTexture2 = initColorTex(window);
        ssaoDepth2 = initDepthTex(window);
    }

    public void render(MouseInput mouseInput, View3D mainView)
    {
        clear();
        bindFrameBuffer(screenBuffer);
        bindColorTex(screenTexture);
        bindDepthTex(screenDepth);
        clear();
        entityRenderer.render(mouseInput, mainView);
        if(!Main.debug || !((Checkbox) ((Settings) mainView.Settings).debug.tabElements.get(4)).isChecked)
            doSSAO(mainView);
        doOutlines();
        entityRenderer.renderAfterPP(mainView);
        guiRenderer.render(screenTexture, blurBuffer, blurTexture, blurDepth);

        ShaderMan shader = guiRenderer.guiShader;
        shader.bind();

        unbindFrameBuffer();

        GL30.glBindVertexArray(GuiRenderer.defaultQuad.vao);
        GL20.glEnableVertexAttribArray(0);

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, screenTexture);

        shader.setUniform("hasCoords", false);
        shader.setUniform("guiTexture", 0);
        shader.setUniform("transformationMatrix", Transformation.createTransformationMatrix(
                new Vector2f(),
                new Vector2f(1, -1)
        ));

        shader.setUniform("hasColor", 0);
        shader.setUniform("smoothst", false);

        GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, GuiRenderer.defaultQuad.vertexCount);

        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);

        shader.unbind();
    }

    private void doSSAO(View3D mainView)
    {
        RenderMan.disableCulling();

        shaderSSAO.bind();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);

        bindFrameBuffer(ssaoBuffer);
        bindColorTex(ssaoTexture);
        bindDepthTex(ssaoDepth);

        GL11.glClearColor(0,0,0,0);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        clear();

        GL30.glBindVertexArray(GuiRenderer.defaultQuad.vao);
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, screenDepth);

        shaderSSAO.setUniform("start", true);
        shaderSSAO.setUniform("hasCoords", false);
        shaderSSAO.setUniform("depthTexture", 0);
        shaderSSAO.setUniform("zRatio", Config.Z_FAR / Config.Z_NEAR);
        shaderSSAO.setUniform("transformationMatrix", Transformation.createTransformationMatrix(
                new Vector2f(),
                new Vector2f(1f, -1f)
        ));
        shaderSSAO.setUniform("camerarange", new Vector2f(Config.Z_NEAR, Config.Z_FAR));
        shaderSSAO.setUniform("screensize", new Vector2f(window.width, window.height));
        //ssao pass
        GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, GuiRenderer.defaultQuad.vertexCount);

        ShaderMan shader = guiRenderer.guiShader;
        shader.bind();

        bindFrameBuffer(ssaoBuffer2);
        bindColorTex(ssaoTexture2);
        bindDepthTex(ssaoDepth2);

        GL11.glClearColor(0,0,0,0);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        clear();

        shader.setUniform("hasCoords", false);
        shader.setUniform("guiTexture", 0);
        shader.setUniform("transformationMatrix", Transformation.createTransformationMatrix(new Vector2f(), new Vector2f(1)));

        shader.setUniform("hasColor", 0);
        shader.setUniform("smoothst", false);

        GL30.glBindVertexArray(GuiRenderer.defaultQuad.vao);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, ssaoTexture);

        shader.setUniform("isBlur", true);
        shader.setUniform("isGaussian", true);
        shader.setUniform("pixelSize", 1.0f/window.height);
        shader.setUniform("radius", Consts.GAUSSIAN_RADIUS_SSAO);
        shader.setUniform("gaussKernel", Consts.GAUSSIAN_KERNEL_SSAO);
        shader.setUniform("vertical", true);
        //vertical blur pass
        GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, GuiRenderer.defaultQuad.vertexCount);

        bindFrameBuffer(ssaoBuffer);
        bindColorTex(ssaoTexture);
        bindDepthTex(ssaoDepth);

        GL11.glClearColor(0,0,0,0);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        clear();

        shader.setUniform("hasCoords", false);
        shader.setUniform("guiTexture", 0);
        shader.setUniform("transformationMatrix", Transformation.createTransformationMatrix(new Vector2f(), new Vector2f(1)));
        shader.setUniform("hasColor", 0);
        shader.setUniform("smoothst", false);

        GL30.glBindVertexArray(GuiRenderer.defaultQuad.vao);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, ssaoTexture2);

        shader.setUniform("pixelSize", 1.0f/window.width);
        shader.setUniform("vertical", false);
        //horizontal blur pass
        GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, GuiRenderer.defaultQuad.vertexCount);

        shader.setUniform("isBlur", false);

        bindFrameBuffer(screenBuffer);
        bindColorTex(screenTexture);
        bindDepthTex(screenDepth);

        shaderSSAO.bind();
        shaderSSAO.setUniform("hasCoords", true);
        shaderSSAO.setUniform("depthTexture", 0);
        shaderSSAO.setUniform("colorTexture", 1);
        shaderSSAO.setUniform("transformationMatrix", Transformation.createTransformationMatrix(
                new Vector2f(),
                new Vector2f(1, -1)
        ));

        shaderSSAO.setUniform("hasColor", 0);
        shaderSSAO.setUniform("smoothst", false);
        shaderSSAO.setUniform("start", false);
        shaderSSAO.setUniform("fogColor", mainView.fogColor);

        GL30.glBindVertexArray(GuiRenderer.defaultQuad.vao);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, ssaoTexture);

        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, screenTexture);

        //final pass
        GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, GuiRenderer.defaultQuad.vertexCount);

        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL30.glBindVertexArray(0);

        shaderSSAO.unbind();

        GL11.glEnable(GL11.GL_BLEND);
    }

    private void doOutlines()
    {
        int radius = Math.round((Config.OUTLINE_DISTANCE * 2f - 1f) * 10f);

        bindFrameBuffer(outlineFB2);
        bindColorTex(outlineCT2);
        bindDepthTex(outlineDT2);

        GL11.glClearColor(0,0,0,0);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);

        shaderOutline.bind();

        drawOutline(outlineCT, -1, shaderOutline, window.width, radius, false, Config.OUTLINE_COLOR);

        bindFrameBuffer(screenBuffer);
        bindColorTex(screenTexture);
        bindDepthTex(screenDepth);

        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);

        drawOutline(outlineCT2, outlineCT, shaderOutline, window.height, radius, true, Config.OUTLINE_COLOR);

        bindFrameBuffer(outlineFB);
        bindColorTex(outlineCT);
        bindDepthTex(outlineDT);

        GL11.glClearColor(0,0,0,0);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);

        bindFrameBuffer(screenBuffer);
        bindColorTex(screenTexture);
        bindDepthTex(screenDepth);
    }

    public static void enableCulling()
    {
        if(!isCulling)
        {
            GL11.glEnable(GL11.GL_CULL_FACE);
            isCulling = true;
        }
    }

    public static void disableCulling()
    {
        if(isCulling)
        {
            GL11.glDisable(GL11.GL_CULL_FACE);
            isCulling = false;
        }
    }

    public void processEntity(Entity entity) {
        this.entityRenderer.entities.add(entity);
    }

    public void processBasicMeshes(Entity entity) {
        this.entityRenderer.basicMeshes.add(entity);
    }

    public void processDirectionalLight(DirectionalLight directionalLight)
    {
        this.entityRenderer.directionalLights.add(directionalLight);
    }

    public void processPointLight(PointLight pointLight)
    {
        this.entityRenderer.pointLights.add(pointLight);
    }

    public void processSpotLight(SpotLight spotLight)
    {
        this.entityRenderer.spotLights.add(spotLight);
    }

    public void processThroughWallEntity(Entity entity) {
        this.entityRenderer.throughWallEntities.add(entity);
    }

    public void processThroughWallEntityForMousePick(Entity entity) {
        this.entityRenderer.throughWallEntitiesMouse.add(entity);
    }

    public void processGuiElement(Drawable element)
    {
        this.guiRenderer.elements.add(element);
    }

    public static void clear()
    {
        GL11.glClearStencil(0);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
    }

    public void cleanup()
    {
        shaderOutline.cleanup();
        entityRenderer.cleanup();
        guiRenderer.cleanup();
    }

    public void drawLine(ObjectLoader loader, Vector2i pos1, Vector2i pos2, boolean smooth)
    {
        this.processGuiElement(new Line(pos1, pos2, loader, window, smooth));
    }

    public void drawLine(ObjectLoader loader, Vector2i pos1, Vector2i pos2, Color color, boolean smooth)
    {
        this.processGuiElement(new Line(pos1, pos2, color, loader, window, smooth));
    }

    public void drawLine(Model line, boolean smooth)
    {
        this.processGuiElement(new Line(line, window, smooth));
    }

    public void drawLine(Model line, Color color, boolean smooth)
    {
        this.processGuiElement(new Line(line, color, window, smooth));
    }

    public void drawLine(Model line, Vector2f pos, Color color, boolean smooth)
    {
        Line l = new Line(line, color, window, smooth);
        l.pos = pos;
        this.processGuiElement(l);
    }

    public void drawLineStrip(Model lineStrip, Vector2f pos, boolean smooth)
    {
        LineStrip ls = new LineStrip(pos, lineStrip, smooth);
        this.processGuiElement(ls);
    }

    public void drawLineStrip(Model lineStrip, Vector2f pos, Color color, boolean smooth)
    {
        LineStrip ls = new LineStrip(pos, lineStrip, color, smooth);
        this.processGuiElement(ls);
    }

    public void drawImage(int image, float x, float y, float width, float height)
    {
        this.processGuiElement(new Quad(image, new Vector2f(x, y), new Vector2f(width, height)));
    }

    public void drawImageStatic(int image, float x, float y, float width, float height)
    {
        this.processGuiElement(new Quad(image, new Vector2f(x, y), new Vector2f(width, height)).staticTexture());
    }

    public void drawImageStatic(int image, float x, float y, float width, float height, Color color)
    {
        this.processGuiElement(new Quad(image, new Vector2f(x, y), new Vector2f(width, height), color).staticTexture());
    }

    public void drawRect(int x, int y, int width, int height, Color color) {
        this.processGuiElement(new Quad(color, new Vector2f(x, y), new Vector2f(width, height)));
    }

    public void drawRectInvert(int x, int y, int width, int height) {
        this.processGuiElement(new Quad(new Vector4f(1f), new Vector2f(x, y), new Vector2f(width, height)).invert());
    }

    public void drawRectOutline(Vector2f pos, Model outline, Color color, boolean smooth)
    {
        this.processGuiElement(new LineStrip(pos, outline, color, smooth));
    }

    public void drawTriangle(ObjectLoader loader, Vector2f p1, Vector2f p2, Vector2f p3, Color color)
    {
        this.processGuiElement(new Triangle(loader, window, color, p1, p2, p3));
    }

    public void drawCircle(ObjectLoader loader, Vector2f center, float radius, Color color)
    {
        this.processGuiElement(Circle.get(loader, window, color, center, radius, false));
    }

    public void drawCircleOutline(ObjectLoader loader, Vector2f center, float radius, Color color)
    {
        this.processGuiElement(Circle.get(loader, window, color, center, radius, true));
    }

    public void drawString(String text, Color color, int x, int y, int size)
    {
        FontRenderer.drawString(this, text, x, y, size, color, 0, text.length(), FontRenderer.Fonts.get(FontRenderer.textFont));
    }

    public void drawString(String text, Color color, int x, int y, int size, int begin, int end)
    {
        FontRenderer.drawString(this, text, x, y, size, color, begin, end, FontRenderer.Fonts.get(FontRenderer.textFont));
    }

    public void drawHeader(String text, Color color, int x, int y, int size)
    {
        FontRenderer.drawString(this, text, x, y, size, color, 0, text.length(), FontRenderer.Fonts.get(FontRenderer.headerFont));
    }

    public void startScissor(Vector2i pos, Vector2i size)
    {
        this.processGuiElement(Scissor.start(pos, size));
    }

    public void startScissor(int x, int y, int width, int height)
    {
        this.processGuiElement(Scissor.start(new Vector2i(x, y), new Vector2i(width, height)));
    }

    public void endScissor()
    {
        this.processGuiElement(Scissor.end());
    }

    public void startScissorEscape(){this.processGuiElement(Scissor.startEscape());}
    public void endScissorEscape(){this.processGuiElement(Scissor.endEscape());}

    public void startBlur(int radius)
    {
        this.processGuiElement(new Blur(radius, true));
    }

    public void endBlur(int radius)
    {
        this.processGuiElement(new Blur(radius, false));
    }

    public void doBlur(int radius)
    {
        this.startBlur(radius);
        this.endBlur(radius);
    }

    public void doBlur(int radius, int x, int y, int width, int height)
    {
        this.startBlur(radius);
        this.startScissor(x, y, width, height);
        this.endBlur(radius);
        this.endScissor();
    }

    public void startBlur(int radius, float[] kernel)
    {
        this.processGuiElement(new Blur(radius, kernel, true));
    }

    public void endBlur(int radius, float[] kernel)
    {
        this.processGuiElement(new Blur(radius, kernel, false));
    }

    public void doBlur(int radius, float[] kernel)
    {
        this.startBlur(radius, kernel);
        this.endBlur(radius, kernel);
    }

    public void doBlur(int radius, float[] kernel, int x, int y, int width, int height)
    {
        this.startBlur(radius, kernel);
        this.startScissor(x, y, width, height);
        this.endBlur(radius, kernel);
        this.endScissor();
    }

    public void drawHUERamp(int x, int y, int width, int height) {
        this.processGuiElement(ColorPickerPart.hueRamp(x, y, width, height, window));
    }

    public void drawSaturationLuminancePicker(int x, int y, int width, int height, Vector4f color) {
        this.processGuiElement(ColorPickerPart.saturationLuminancePicker(x, y, width, height, color, window));
    }

    public void drawHUERamp(Vector2f pos, Vector2f size) {
        this.processGuiElement(ColorPickerPart.hueRamp(Math.round(pos.x), Math.round(pos.y), Math.round(size.x), Math.round(size.y), window));
    }

    public void drawSaturationLuminancePicker(Vector2f pos, Vector2f size, Vector4f color) {
        this.processGuiElement(ColorPickerPart.saturationLuminancePicker(Math.round(pos.x), Math.round(pos.y), Math.round(size.x), Math.round(size.y), color, window));
    }

    public void drawTransparencyCheckerBoard(Vector2f pos, Vector2f size) {
        this.processGuiElement(ColorPickerPart.transparencyCheckerBoard(Math.round(pos.x), Math.round(pos.y), Math.round(size.x), Math.round(size.y), window));
    }

    public void drawHUERamp(Vector2i pos, Vector2i size) {
        this.processGuiElement(ColorPickerPart.hueRamp(pos.x, pos.y, size.x, size.y, window));
    }

    public void drawSaturationLuminancePicker(Vector2i pos, Vector2i size, Vector4f color) {
        this.processGuiElement(ColorPickerPart.saturationLuminancePicker(pos.x, pos.y, size.x, size.y, color, window));
    }

    public static int initFrameBuffer()
    {
        int frameBuffer = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer);
        GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, frameBuffer);

        return frameBuffer;
    }

    public static void bindFrameBuffer(int frameBuffer)
    {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer);
        GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, frameBuffer);
    }

    public static void bindColorTex(int colorTexture)
    {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorTexture);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, colorTexture, 0);
    }

    public static void bindDepthTex(int mouseDepthTexture)
    {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, mouseDepthTexture);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, mouseDepthTexture, 0);
    }

    public static int initColorTex(WindowMan window)
    {
        int colorTexture = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorTexture);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, window.width, window.height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, colorTexture, 0);
        return colorTexture;
    }

    public static int initFrameBufferINT()
    {
        int frameBuffer = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer);
        GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);

        return frameBuffer;
    }

    public static int initColorTexINT(WindowMan window)
    {
        int colorTexture = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorTexture);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_RGB32I, window.width, window.height, 0, GL30.GL_RGB_INTEGER, GL11.GL_INT, (ByteBuffer) null);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, colorTexture, 0);
        return colorTexture;
    }

    public static int initDepthTex(WindowMan window)
    {
        int depthTexture = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTexture);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_DEPTH_COMPONENT24, window.width, window.height, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (ByteBuffer) null);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL30.GL_DEPTH_TEXTURE_MODE, GL11.GL_LUMINANCE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL30.GL_TEXTURE_COMPARE_MODE, GL11.GL_NONE);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, depthTexture, 0);
        return depthTexture;
    }

    public static void unbindFrameBuffer()
    {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }


    private void drawOutline(int originalTexture, int maskTexture, ShaderMan outlineShader, float target, int radius, boolean vertical, Color color)
    {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL30.glBindVertexArray(GuiRenderer.defaultQuad.vao);
        GL20.glEnableVertexAttribArray(0);

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, originalTexture);

        if(maskTexture != -1)
        {
            GL13.glActiveTexture(GL13.GL_TEXTURE1);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, maskTexture);
        }

        outlineShader.setUniform("originalTexture", 0);
        outlineShader.setUniform("hasMask", maskTexture != -1);
        outlineShader.setUniform("maskTexture", 1);
        outlineShader.setUniform("pixelSize", 1f / target);
        outlineShader.setUniform("radius", radius);
        outlineShader.setUniform("vertical", vertical);
        outlineShader.setUniform("color", new Vector4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f));

        GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, GuiRenderer.defaultQuad.vertexCount);
        GL11.glDisable(GL11.GL_BLEND);
    }
}