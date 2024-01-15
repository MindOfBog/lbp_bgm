package bog.bgmaker.view3d.managers;

import bog.bgmaker.view3d.Camera;
import bog.bgmaker.view3d.ObjectLoader;
import bog.bgmaker.view3d.core.DirectionalLight;
import bog.bgmaker.view3d.core.Model;
import bog.bgmaker.view3d.core.PointLight;
import bog.bgmaker.view3d.core.SpotLight;
import bog.bgmaker.view3d.core.types.Entity;
import bog.bgmaker.view3d.mainWindow.View3D;
import bog.bgmaker.view3d.renderer.EntityRenderer;
import bog.bgmaker.view3d.renderer.gui.GuiRenderer;
import bog.bgmaker.view3d.renderer.gui.font.FNT;
import bog.bgmaker.view3d.renderer.gui.font.FontRenderer;
import bog.bgmaker.view3d.renderer.gui.ingredients.*;
import bog.bgmaker.view3d.utils.Transformation;
import org.joml.Vector2f;
import org.joml.Vector2i;
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
    }

    int screenBuffer = -1;
    int screenTexture = -1;
    int screenDepth = -1;

    int blurBuffer = -1;
    int blurTexture = -1;
    int blurDepth = -1;

    public void resize()
    {
        entityRenderer.resize();
        GL11.glDeleteTextures(new int[]{screenTexture, screenDepth, blurTexture, blurDepth});
        GL11.glViewport(0, 0, window.width, window.height);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, screenBuffer);
        screenTexture = initColorTex(window);
        screenDepth = initDepthTex(window);
        blurTexture = initColorTex(window);
        blurDepth = initDepthTex(window);
    }

    public void render(MouseInput mouseInput, View3D mainView)
    {
        clear();
        bindFrameBuffer(blurBuffer);
        bindColorTex(blurTexture);
        bindDepthTex(blurDepth);
        clear();
        bindFrameBuffer(screenBuffer);
        bindColorTex(screenTexture);
        bindDepthTex(screenDepth);
        clear();
        entityRenderer.render(mouseInput, mainView);
        guiRenderer.render(screenTexture, blurBuffer, blurTexture, blurDepth);

        ShaderMan shader = guiRenderer.shader;
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

    public void drawRectOutline(Vector2f pos, Model outline, Color color, boolean smooth)
    {
        this.processGuiElement(new LineStrip(pos, outline, color, smooth));
    }

    public void drawTriangle(ObjectLoader loader, Vector2f p1, Vector2f p2, Vector2f p3, Color color)
    {
        this.processGuiElement(new Triangle(loader, window, color, p1, p2, p3));
    }

    public void drawCircle(ObjectLoader loader, Vector2f center, float radius, int tris, Color color)
    {
        this.processGuiElement(Circle.get(loader, window, color, center, radius, false));
    }

    public void drawCircleOutline(ObjectLoader loader, Vector2f center, float radius, int tris, Color color)
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

    public void startBlur(float amount)
    {
        this.processGuiElement(new Blur(amount, true));
    }

    public void endBlur(float amount)
    {
        this.processGuiElement(new Blur(amount, false));
    }

    public void doBlur(float amount)
    {
        this.startBlur(amount);
        this.endBlur(amount);
    }

    public void doBlur(float amount, int x, int y, int width, int height)
    {
        this.startBlur(amount);
        this.startScissor(x, y, width, height);
        this.endBlur(amount);
        this.endScissor();
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
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_DEPTH_COMPONENT, window.width, window.height, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (ByteBuffer) null);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, depthTexture, 0);
        return depthTexture;
    }

    public static void unbindFrameBuffer()
    {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }
}
