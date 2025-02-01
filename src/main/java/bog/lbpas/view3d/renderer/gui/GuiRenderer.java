package bog.lbpas.view3d.renderer.gui;

import bog.lbpas.view3d.managers.assetLoading.ObjectLoader;
import bog.lbpas.view3d.core.Model;
import bog.lbpas.view3d.managers.RenderMan;
import bog.lbpas.view3d.managers.ShaderMan;
import bog.lbpas.view3d.managers.WindowMan;
import bog.lbpas.view3d.renderer.gui.ingredients.*;
import bog.lbpas.view3d.utils.Transformation;
import bog.lbpas.view3d.utils.Utils;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.opengl.*;

import java.util.ArrayList;

/**
 * @author Bog
 */
public class GuiRenderer {
    public ShaderMan guiShader;
    WindowMan window;
    public static Model defaultQuad(ObjectLoader loader)
    {
        Model quad = loader.loadModel(new float[]{-1, 1, -1, -1, 1, 1, 1, -1}, new float[]{0, 0, 0, 1, 1, 0, 1, 1});
        return quad;
    }
    public static Model defaultQuad;
    ObjectLoader loader;

    public GuiRenderer(ObjectLoader loader, WindowMan window) throws Exception {
        this.guiShader = new ShaderMan("guiShader");
        this.window = window;
        this.loader = loader;
    }

    Model line;

    public void init() throws Exception {
        guiShader.createFragmentShader(Utils.loadResource("/shaders/fragment_gui.glsl"));
        guiShader.createVertexShader(Utils.loadResource("/shaders/vertex_gui.glsl"));
        guiShader.link();
        guiShader.createUniform("transformationMatrix");
        guiShader.createUniform("guiTexture");
        guiShader.createUniform("hasCoords");
        guiShader.createUniform("circle");
        guiShader.createUniform("hasColor");
        guiShader.createUniform("color");
        guiShader.createUniform("smoothst");
        guiShader.createUniform("alpha");

        line = loader.loadModel(new float[]{0f, 0f, 100f, 100f});
        defaultQuad = defaultQuad(loader);
    }

    public ArrayList<Drawable> elements = new ArrayList<>();

    public void render(int screenTexture, int blurBuffer, int blurTexture, int blurDepth)
    {
        guiShader.bind();

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        RenderMan.disableCulling();

        ArrayList<int[]> prevScissors = new ArrayList();
        int[] lastScissor = null;
        ArrayList<int[]> scissorEscapes = new ArrayList();

        for(Drawable drawable : elements)
            switch (drawable.getType())
            {
                case 0: //TRI STRIP
                    {
                        TriStrip element = (TriStrip) drawable;
                        GL30.glBindVertexArray(element.model.vao);
                        GL20.glEnableVertexAttribArray(0);

                        if(element.hasTexCoords)
                            GL20.glEnableVertexAttribArray(1);

                        if(element.texture != -1)
                        {
                            GL13.glActiveTexture(GL13.GL_TEXTURE0);
                            GL11.glBindTexture(GL11.GL_TEXTURE_2D, element.texture == -69 ? screenTexture : element.texture);
                        }

                        guiShader.setUniform("hasCoords", element.hasTexCoords);
                        guiShader.setUniform("guiTexture", 0);
                        guiShader.setUniform("transformationMatrix", Transformation.createTransformationMatrix(
                                new Vector2f(element.pos.x / (window.width/2f) - 1 + element.scale.x / window.width, (-element.pos.y) / (window.height/2f) + 1 - element.scale.y / window.height),
                                new Vector2f(element.scale.x / window.width, element.scale.y / window.height)));

                        if(element.color != null)
                            guiShader.setUniform("color", new Vector4f(
                                    element.invert ? 1f : element.color.getRed() / 255f,
                                    element.invert ? 1f : element.color.getGreen() / 255f,
                                    element.invert ? 1f : element.color.getBlue() / 255f,
                                    element.invert ? 1f : element.color.getAlpha() / 255f));

                        guiShader.setUniform("hasColor", element.color == null ? 0 : element.texture != -1 ? 1 : 2);
                        guiShader.setUniform("smoothst", element.smoothstep);

                        if(element.invert)
                        {
                            GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
                            GL14.glBlendEquation(GL14.GL_FUNC_SUBTRACT);
                        }

                        GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, element.model.vertexCount);

                        if(element.invert)
                        {
                            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                            GL14.glBlendEquation(GL14.GL_FUNC_ADD);
                        }

                        if(!element.staticTexture)
                        {
                            if(loader.textures.contains(element.texture))
                                loader.textures.remove((Object)element.texture);
                            GL11.glDeleteTextures(element.texture);
                        }

                        clearElement(element);
                    }
                    break;
                case 1: //TRI FAN
                    {
                        TriFan element = (TriFan) drawable;
                        GL30.glBindVertexArray(element.model.vao);
                        GL20.glEnableVertexAttribArray(0);

                        if(element.hasTexCoords)
                            GL20.glEnableVertexAttribArray(1);

                        GL13.glActiveTexture(GL13.GL_TEXTURE0);
                        GL11.glBindTexture(GL11.GL_TEXTURE_2D, element.texture == -69 ? screenTexture : element.texture);

                        guiShader.setUniform("hasCoords", element.hasTexCoords);
                        guiShader.setUniform("guiTexture", 0);
                        guiShader.setUniform("transformationMatrix", Transformation.createTransformationMatrix(
                                new Vector2f(element.pos.x / (window.width/2f) - 1 + element.scale.x / window.width, (-element.pos.y) / (window.height/2f) + 1 - element.scale.y / window.height),
                                new Vector2f(element.scale.x / window.width, element.scale.y / window.height)));

                        if(element.color != null)
                            guiShader.setUniform("color", new Vector4f(
                                    element.invert ? 1f : element.color.getRed() / 255f,
                                    element.invert ? 1f : element.color.getGreen() / 255f,
                                    element.invert ? 1f : element.color.getBlue() / 255f,
                                    element.invert ? 1f : element.color.getAlpha() / 255f));

                        guiShader.setUniform("hasColor", element.color == null ? 0 : element.texture != -1 ? 1 : 2);
                        guiShader.setUniform("smoothst", element.smoothstep);

                        if(element.invert)
                            GL11.glBlendFunc(GL11.GL_DST_COLOR, GL11.GL_ONE_MINUS_DST_COLOR);

                        if(element.invert)
                        {
                            GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
                            GL14.glBlendEquation(GL14.GL_FUNC_SUBTRACT);
                        }

                        GL11.glDrawArrays(GL11.GL_TRIANGLE_FAN, 0, element.model.vertexCount);

                        if(element.invert)
                        {
                            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                            GL14.glBlendEquation(GL14.GL_FUNC_ADD);
                        }

                        if(element.invert)
                            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

                        if(!element.staticTexture)
                        {
                            if(loader.textures.contains(element.texture))
                                loader.textures.remove((Object)element.texture);
                            GL11.glDeleteTextures(element.texture);
                        }

                        clearElement(element);
                    }
                    break;
                case 2: //LINE
                    {
                        Line element = (Line) drawable;
                        if(element.model == null || window.isMinimized)
                            continue;
                        GL30.glBindVertexArray(element.model.vao);
                        GL20.glEnableVertexAttribArray(0);

                        guiShader.setUniform("color", element.invert ? new Vector4f(1f) : element.color);
                        guiShader.setUniform("hasCoords", false);
                        guiShader.setUniform("guiTexture", 0);
                        guiShader.setUniform("transformationMatrix", Transformation.createTransformationMatrix(new Vector2f(element.pos.x / (window.width/2f) + 1 / window.width, (-element.pos.y) / (window.height/2f) - 1 / window.height), new Vector2f(1, 1)));
                        guiShader.setUniform("hasColor", 2);

                        if(!element.smooth)
                            GL11.glDisable(GL11.GL_LINE_SMOOTH);

                        if(element.invert)
                            GL11.glBlendFunc(GL11.GL_DST_COLOR, GL11.GL_ONE_MINUS_DST_COLOR);

                        if(element.invert)
                        {
                            GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
                            GL14.glBlendEquation(GL14.GL_FUNC_SUBTRACT);
                        }

                        GL11.glDrawArrays(GL11.GL_LINES, 0, element.model.vertexCount);

                        if(element.invert)
                        {
                            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                            GL14.glBlendEquation(GL14.GL_FUNC_ADD);
                        }

                        if(element.invert)
                            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

                        if(!element.smooth)
                            GL11.glEnable(GL11.GL_LINE_SMOOTH);

                        clearElement(element);
                    }
                    break;
                case 3: //LINE STRIP
                    {
                        LineStrip element = (LineStrip) drawable;
                        if(element.model == null)
                            continue;
                        GL30.glBindVertexArray(element.model.vao);
                        GL20.glEnableVertexAttribArray(0);

                        guiShader.setUniform("color", element.invert ? new Vector4f(1f) : element.color);
                        guiShader.setUniform("hasCoords", false);
                        guiShader.setUniform("guiTexture", 0);
                        guiShader.setUniform("transformationMatrix", Transformation.createTransformationMatrix(
                                new Vector2f(element.pos.x / (window.width/2f), (-element.pos.y) / (window.height/2f)), new Vector2f(1, 1)));
                        guiShader.setUniform("hasColor", 2);

                        if(!element.smooth)
                            GL11.glDisable(GL11.GL_LINE_SMOOTH);

                        if(element.invert)
                            GL11.glBlendFunc(GL11.GL_DST_COLOR, GL11.GL_ONE_MINUS_DST_COLOR);

                        if(element.invert)
                        {
                            GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
                            GL14.glBlendEquation(GL14.GL_FUNC_SUBTRACT);
                        }

                        GL11.glDrawArrays(GL11.GL_LINE_STRIP, 0, element.model.vertexCount);

                        if(element.invert)
                        {
                            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                            GL14.glBlendEquation(GL14.GL_FUNC_ADD);
                        }

                        if(element.invert)
                            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

                        if(!element.smooth)
                            GL11.glEnable(GL11.GL_LINE_SMOOTH);

                        clearElement(element);
                    }
                    break;
                case 4: //SCISSOR
                    {
                        Scissor scissor = (Scissor) drawable;

                        boolean isScissorOn = GL11.glIsEnabled(GL11.GL_SCISSOR_TEST);

                        if((scissor.start && !scissor.escape) || (!scissor.start && scissor.escape))
                        {
                            int minX = 0;
                            int minY = 0;
                            int maxX = 0;
                            int maxY = 0;

                            if(scissor.escape)
                            {
                                if(scissorEscapes.size() != 0)
                                {
                                    int[] scissorEscape = scissorEscapes.get(scissorEscapes.size() - 1);

                                    if (scissorEscape == null)
                                    {
                                        scissorEscapes.remove(scissorEscapes.size() - 1);
                                        break;
                                    }

                                    minX = scissorEscape[0];
                                    minY = scissorEscape[1];
                                    maxX = scissorEscape[2] + scissorEscape[0];
                                    maxY = scissorEscape[3] + scissorEscape[1];

                                    scissorEscapes.remove(scissorEscapes.size() - 1);
                                }
                                else break;
                            }
                            else
                            {
                                minX = scissor.pos.x;
                                minY = window.height - scissor.pos.y - scissor.size.y;
                                maxX = scissor.pos.x + scissor.size.x;
                                maxY = window.height - scissor.pos.y;
                            }

                            int temp;

                            if (minY > maxY) {
                                temp = maxY;
                                maxY = minY;
                                minY = temp;
                            }
                            if (minX > maxX) {
                                temp = maxX;
                                maxX = minX;
                                minX = temp;
                            }

                            if (isScissorOn)
                            {
                                int[] box = new int[4];
                                GL11.glGetIntegerv(GL11.GL_SCISSOR_BOX, box);
                                prevScissors.add(box);
                            }
                            else
                                GL11.glEnable(GL11.GL_SCISSOR_TEST);

                            if(prevScissors.size() > 0){
                                int[] s = prevScissors.get(prevScissors.size() - 1);

                                if (minX < s[0])
                                    minX = s[0];
                                if (minX > s[0] + s[2])
                                    minX = s[0] + s[2];

                                if (minY < s[1])
                                    minY = s[1];
                                if (minY > s[1] + s[3])
                                    minY = s[1] + s[3];

                                if (maxX < s[0])
                                    maxX = s[0];
                                if (maxX > s[0] + s[2])
                                    maxX = s[0] + s[2];

                                if (maxY < s[1])
                                    maxY = s[1];
                                if (maxY > s[1] + s[3])
                                    maxY = s[1] + s[3];
                            }

                            GL11.glScissor(minX, minY, maxX - minX, maxY - minY);
                        }
                        if((!scissor.start && !scissor.escape) || (scissor.start && scissor.escape))
                        {
                            if(scissor.escape)
                            {
                                int[] box = new int[4];
                                GL11.glGetIntegerv(GL11.GL_SCISSOR_BOX, box);
                                scissorEscapes.add(isScissorOn ? box : null);
                            }

                            if (prevScissors.size() == 0) {
                                GL11.glDisable(GL11.GL_SCISSOR_TEST);
                            } else {
                                int[] prevScissor = prevScissors.get(prevScissors.size() - 1);
                                GL11.glScissor(prevScissor[0], prevScissor[1], prevScissor[2], prevScissor[3]);
                                prevScissors.remove(prevScissors.size() - 1);
                            }
                        }
                    }
                    break;
                case 5: //CIRCLE
                    {
                        TriStrip element = (TriStrip) drawable;
                        GL30.glBindVertexArray(element.model.vao);
                        GL20.glEnableVertexAttribArray(0);

                        if(element.hasTexCoords)
                            GL20.glEnableVertexAttribArray(1);

                        if(element.texture != -1)
                        {
                            GL13.glActiveTexture(GL13.GL_TEXTURE0);
                            GL11.glBindTexture(GL11.GL_TEXTURE_2D, element.texture == -69 ? screenTexture : element.texture);
                        }

                        guiShader.setUniform("hasCoords", element.hasTexCoords);
                        guiShader.setUniform("circle", ((Circle) drawable).circle);
                        guiShader.setUniform("guiTexture", 0);
                        guiShader.setUniform("transformationMatrix", Transformation.createTransformationMatrix(
                                new Vector2f(element.pos.x / (window.width/2f) - 1 + element.scale.x / window.width, (-element.pos.y) / (window.height/2f) + 1 - element.scale.y / window.height),
                                new Vector2f(element.scale.x / window.width, element.scale.y / window.height)));

                        if(element.color != null)
                            guiShader.setUniform("color", new Vector4f(
                                    element.invert ? 1f : element.color.getRed() / 255f,
                                    element.invert ? 1f : element.color.getGreen() / 255f,
                                    element.invert ? 1f : element.color.getBlue() / 255f,
                                    element.invert ? 1f : element.color.getAlpha() / 255f));

                        guiShader.setUniform("hasColor", element.color == null ? 0 : element.texture != -1 ? 1 : 2);
                        guiShader.setUniform("smoothst", element.smoothstep);

                        if(element.invert)
                            GL11.glBlendFunc(GL11.GL_DST_COLOR, GL11.GL_ONE_MINUS_DST_COLOR);

                        if(element.invert)
                        {
                            GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
                            GL14.glBlendEquation(GL14.GL_FUNC_SUBTRACT);
                        }

                        GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, element.model.vertexCount);

                        if(element.invert)
                        {
                            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                            GL14.glBlendEquation(GL14.GL_FUNC_ADD);
                        }

                        if(element.invert)
                            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

                        if(!element.staticTexture)
                        {
                            if(loader.textures.contains(element.texture))
                                loader.textures.remove((Object)element.texture);
                            GL11.glDeleteTextures(element.texture);
                        }

                        guiShader.setUniform("circle", new Vector4f(-1));

                        clearElement(element);
                    }
                    break;
                case 6: //BLUR
                    {
                        Blur blur = (Blur)drawable;
                        if(blur.start)
                            startBlur(screenTexture, blurBuffer, blurTexture, blurDepth, blur.amount);
                        else
                            endBlur(blurTexture, blur.amount);
                    }
                    break;
            }

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL30.glBindVertexArray(0);

        guiShader.unbind();
        elements.clear();
    }

    public void cleanup()
    {
        guiShader.cleanup();
    }

    private void clearElement(Drawable element)
    {
        if(!element.staticVAO)
        {
            int vao = element instanceof TriStrip ? ((TriStrip)element).model.vao : element instanceof TriFan ? ((TriFan)element).model.vao : element instanceof Line ? ((Line)element).model.vao : ((LineStrip)element).model.vao;
            GL30.glDeleteVertexArrays(vao);
            loader.vaos.remove((Object)vao);
        }
        if(!element.staticVBO)
        {
            int[] vbos = element instanceof TriStrip ? ((TriStrip)element).model.vbos : element instanceof TriFan ? ((TriFan)element).model.vbos : element instanceof Line ? ((Line)element).model.vbos : ((LineStrip)element).model.vbos;
            GL30.glDeleteBuffers(vbos);
            for(int vbo : vbos)
                loader.vbos.remove((Object)vbo);
        }
    }

    private void startBlur(int screenTexture, int blurBuffer, int blurTexture, int blurDepth, float amount)
    {
        RenderMan.bindFrameBuffer(blurBuffer);
        RenderMan.bindColorTex(blurTexture);
        RenderMan.bindDepthTex(blurDepth);
        RenderMan.clear();

        GL30.glBindVertexArray(GuiRenderer.defaultQuad.vao);
        GL20.glEnableVertexAttribArray(0);

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, screenTexture);

        guiShader.setUniform("hasCoords", false);
        guiShader.setUniform("guiTexture", 0);
        guiShader.setUniform("transformationMatrix", Transformation.createTransformationMatrix(
                new Vector2f(1f / amount, 1f / amount),
                new Vector2f(1f / amount, -(1f / amount))
        ));
        guiShader.setUniform("hasColor", 0);
        guiShader.setUniform("smoothst", false);

        GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, GuiRenderer.defaultQuad.vertexCount);

        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);

        unbindFrameBuffer();
    }

    public void endBlur(int blurTexture, float amount)
    {
        GL30.glBindVertexArray(GuiRenderer.defaultQuad.vao);
        GL20.glEnableVertexAttribArray(0);

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, blurTexture);

        guiShader.setUniform("hasCoords", false);
        guiShader.setUniform("guiTexture", 0);
        guiShader.setUniform("transformationMatrix", Transformation.createTransformationMatrix(
                new Vector2f(-1f),
                new Vector2f(amount, -amount)
        ));
        guiShader.setUniform("hasColor", 0);
        guiShader.setUniform("smoothst", false);

        guiShader.setUniform("alpha", true);
        GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, GuiRenderer.defaultQuad.vertexCount);
        guiShader.setUniform("alpha", false);

        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
    }

    public void unbindFrameBuffer()
    {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }
}