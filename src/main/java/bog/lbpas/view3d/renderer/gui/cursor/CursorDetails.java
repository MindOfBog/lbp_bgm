package bog.lbpas.view3d.renderer.gui.cursor;

import bog.lbpas.view3d.managers.assetLoading.ObjectLoader;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;

import java.awt.image.BufferedImage;

/**
 * @author Bog
 */
public class CursorDetails {

    public int xPos;
    public int yPos;
    public int xHot;
    public int yHot;
    public int width;
    public int height;

    public long cursor = -1;

    public CursorDetails(int xPos, int yPos, int xHot, int yHot, int width, int height) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.xHot = xHot;
        this.yHot = yHot;
        this.width = width;
        this.height = height;
    }

    public void setupCursorImage(BufferedImage atlas, float size) throws Exception {

        if(cursor != -1)
            GLFW.glfwDestroyCursor(cursor);

        GLFWImage iconImage = GLFWImage.malloc();
        iconImage.set((int) (this.width * size), (int) (this.height * size), ObjectLoader.loadTextureBuffer(atlas.getSubimage((int) (this.xPos * size), (int) (this.yPos * size), (int) (this.width * size), (int) (this.height * size))));
        cursor = GLFW.glfwCreateCursor(iconImage, (int) (this.xHot * size), (int) (this.yHot * size));
    }
}