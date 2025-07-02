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

    public void blit(FBO destination, int[] src, int[] dst, int mask)
    {
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, buffer);
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, destination.buffer);

        GL11.glClear(mask);

        GL30.glBlitFramebuffer(
                src[0], src[1], src[2], src[3],
                dst[0], dst[1], dst[2], dst[3],
                mask,
                GL11.GL_NEAREST
        );
    }

    public abstract int initBuffer();
    public abstract int initColorTexture();
    public abstract int initDepthTexture();
}
