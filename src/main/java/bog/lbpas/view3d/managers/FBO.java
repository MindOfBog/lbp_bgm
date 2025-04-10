package bog.lbpas.view3d.managers;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

/**
 * @author Bog
 */
public abstract class FBO {

    public int buffer;
    public int colorTexture;
    public int depthTexture;

    public FBO() {
        buffer = initBuffer();
        colorTexture = initColorTexture();
        depthTexture = initDepthTexture();
    }

    public void bind()
    {
        RenderMan.bindFrameBuffer(buffer);
        RenderMan.bindColorTex(colorTexture);
        RenderMan.bindDepthTex(depthTexture);
    }

    public void resize()
    {
        GL11.glDeleteTextures(new int[]{colorTexture, depthTexture});
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, buffer);
        colorTexture = initColorTexture();
        depthTexture = initDepthTexture();
    }

    public void cleanup()
    {
        GL11.glDeleteTextures(new int[]{colorTexture, depthTexture});
        GL30.glDeleteBuffers(buffer);
    }

    public abstract int initBuffer();
    public abstract int initColorTexture();
    public abstract int initDepthTexture();
}
