package bog.bgmaker.view3d.renderer.gui;

import bog.bgmaker.view3d.ObjectLoader;
import bog.bgmaker.view3d.core.Model;
import bog.bgmaker.view3d.managers.RenderMan;
import bog.bgmaker.view3d.managers.ShaderMan;
import bog.bgmaker.view3d.managers.WindowMan;
import bog.bgmaker.view3d.renderer.gui.ingredients.*;
import bog.bgmaker.view3d.utils.Transformation;
import bog.bgmaker.view3d.utils.Utils;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;

/**
 * @author Bog
 */
public class GuiRenderer {
    public ShaderMan shader;
    WindowMan window;
    static Model defaultQuad(ObjectLoader loader)
    {
        Model quad = loader.loadModel(new float[]{-1, 1, -1, -1, 1, 1, 1, -1});
        return quad;
    }
    public static Model defaultQuad;
    ObjectLoader loader;

    public GuiRenderer(ObjectLoader loader, WindowMan window) throws Exception {
        this.shader = new ShaderMan();
        this.window = window;
        this.loader = loader;
    }

    Model line;

    public void init() throws Exception {
        shader.createFragmentShader(Utils.loadResource("/shaders/fragment_gui.glsl"));
        shader.createVertexShader(Utils.loadResource("/shaders/vertex_gui.glsl"));
        shader.link();
        shader.createUniform("transformationMatrix");
        shader.createUniform("guiTexture");
        shader.createUniform("hasCoords");
        shader.createUniform("circle");
        shader.createUniform("hasColor");
        shader.createUniform("color");
        shader.createUniform("smoothst");
        shader.createUniform("alpha");

        line = loader.loadModel(new float[]{0f, 0f, 100f, 100f});
        defaultQuad = defaultQuad(loader);
    }

    public ArrayList<Drawable> elements = new ArrayList<>();

    public void render(int screenTexture, int blurBuffer, int blurTexture, int blurDepth)
    {
        shader.bind();

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        RenderMan.disableCulling();

        ArrayList<int[]> prevScissors = new ArrayList();

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
                            GL11.glBindTexture(GL11.GL_TEXTURE_2D, element.texture);
                        }

                        shader.setUniform("hasCoords", element.hasTexCoords);
                        shader.setUniform("guiTexture", 0);
                        shader.setUniform("transformationMatrix", Transformation.createTransformationMatrix(
                                new Vector2f(element.pos.x / (window.width/2f) - 1 + element.scale.x / window.width, (-element.pos.y) / (window.height/2f) + 1 - element.scale.y / window.height),
                                new Vector2f(element.scale.x / window.width, element.scale.y / window.height)));

                        if(element.color != null)
                            shader.setUniform("color", new Vector4f(element.color.getRed() / 255f, element.color.getGreen() / 255f, element.color.getBlue() / 255f, element.color.getAlpha() / 255f));

                        shader.setUniform("hasColor", element.color == null ? 0 : element.texture != -1 ? 1 : 2);
                        shader.setUniform("smoothst", element.smoothstep);

                        GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, element.model.vertexCount);

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
                        GL11.glBindTexture(GL11.GL_TEXTURE_2D, element.texture);

                        shader.setUniform("hasCoords", element.hasTexCoords);
                        shader.setUniform("guiTexture", 0);
                        shader.setUniform("transformationMatrix", Transformation.createTransformationMatrix(
                                new Vector2f(element.pos.x / (window.width/2f) - 1 + element.scale.x / window.width, (-element.pos.y) / (window.height/2f) + 1 - element.scale.y / window.height),
                                new Vector2f(element.scale.x / window.width, element.scale.y / window.height)));

                        if(element.color != null)
                            shader.setUniform("color", new Vector4f(element.color.getRed() / 255f, element.color.getGreen() / 255f, element.color.getBlue() / 255f, element.color.getAlpha() / 255f));

                        shader.setUniform("hasColor", element.color == null ? 0 : element.texture != -1 ? 1 : 2);
                        shader.setUniform("smoothst", element.smoothstep);

                        GL11.glDrawArrays(GL11.GL_TRIANGLE_FAN, 0, element.model.vertexCount);

                        if(!element.staticTexture)
                        {
//                            if(loader.textures.contains(element.texture))
//                                for(int i = 0; i < loader.textures.size(); i++)
//                                    if(loader.textures.get(i) == element.texture)
//                                        loader.textures.remove(i);
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
                        if(element.model == null)
                            continue;
                        GL30.glBindVertexArray(element.model.vao);
                        GL20.glEnableVertexAttribArray(0);

                        shader.setUniform("color", element.color);
                        shader.setUniform("hasCoords", false);
                        shader.setUniform("guiTexture", 0);
                        shader.setUniform("transformationMatrix", Transformation.createTransformationMatrix(new Vector2f(0, 0), new Vector2f(1, 1)));
                        shader.setUniform("hasColor", 2);

                        if(!element.smooth)
                            GL11.glDisable(GL11.GL_LINE_SMOOTH);

                        GL11.glDrawArrays(GL11.GL_LINES, 0, element.model.vertexCount);

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

                        shader.setUniform("color", element.color);
                        shader.setUniform("hasCoords", false);
                        shader.setUniform("guiTexture", 0);
                        shader.setUniform("transformationMatrix", Transformation.createTransformationMatrix(
                                new Vector2f(element.pos.x / (window.width/2f), (-element.pos.y) / (window.height/2f)), new Vector2f(1, 1)));
                        shader.setUniform("hasColor", 2);

                        if(!element.smooth)
                            GL11.glDisable(GL11.GL_LINE_SMOOTH);

                        GL11.glDrawArrays(GL11.GL_LINE_STRIP, 0, element.model.vertexCount);

                        if(!element.smooth)
                            GL11.glEnable(GL11.GL_LINE_SMOOTH);

                        clearElement(element);
                    }
                    break;
                case 4: //SCISSOR
                    {
                        Scissor scissor = (Scissor) drawable;

                        if(scissor.start)
                        {
                            boolean isScissorOn = GL11.glIsEnabled(GL11.GL_SCISSOR_TEST);

                            int minX = scissor.pos.x;
                            int minY = window.height - scissor.pos.y - scissor.size.y;
                            int maxX = scissor.pos.x + scissor.size.x;
                            int maxY = window.height - scissor.pos.y;

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

                            if (prevScissors.size() > 0) {
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
                        else
                        {
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
                            GL11.glBindTexture(GL11.GL_TEXTURE_2D, element.texture);
                        }

                        shader.setUniform("hasCoords", element.hasTexCoords);
                        shader.setUniform("circle", ((Circle) drawable).circle);
                        shader.setUniform("guiTexture", 0);
                        shader.setUniform("transformationMatrix", Transformation.createTransformationMatrix(
                                new Vector2f(element.pos.x / (window.width/2f) - 1 + element.scale.x / window.width, (-element.pos.y) / (window.height/2f) + 1 - element.scale.y / window.height),
                                new Vector2f(element.scale.x / window.width, element.scale.y / window.height)));

                        if(element.color != null)
                            shader.setUniform("color", new Vector4f(element.color.getRed() / 255f, element.color.getGreen() / 255f, element.color.getBlue() / 255f, element.color.getAlpha() / 255f));

                        shader.setUniform("hasColor", element.color == null ? 0 : element.texture != -1 ? 1 : 2);
                        shader.setUniform("smoothst", element.smoothstep);

                        GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, element.model.vertexCount);

                        if(!element.staticTexture)
                        {
                            if(loader.textures.contains(element.texture))
                                loader.textures.remove((Object)element.texture);
                            GL11.glDeleteTextures(element.texture);
                        }

                        shader.setUniform("circle", new Vector4f(-1));

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

        shader.unbind();
        elements.clear();
    }

    public void cleanup()
    {
        shader.cleanup();
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

        shader.setUniform("hasCoords", false);
        shader.setUniform("guiTexture", 0);
        shader.setUniform("transformationMatrix", Transformation.createTransformationMatrix(
                new Vector2f(1f / amount, 1f / amount),
                new Vector2f(1f / amount, -(1f / amount))
        ));
        shader.setUniform("hasColor", 0);
        shader.setUniform("smoothst", false);

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

        shader.setUniform("hasCoords", false);
        shader.setUniform("guiTexture", 0);
        shader.setUniform("transformationMatrix", Transformation.createTransformationMatrix(
                new Vector2f(-1f),
                new Vector2f(amount, -amount)
        ));
        shader.setUniform("hasColor", 0);
        shader.setUniform("smoothst", false);

        shader.setUniform("alpha", true);
        GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, GuiRenderer.defaultQuad.vertexCount);
        shader.setUniform("alpha", false);

        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
    }

    public void unbindFrameBuffer()
    {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }
}