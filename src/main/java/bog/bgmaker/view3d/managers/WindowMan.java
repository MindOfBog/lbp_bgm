package bog.bgmaker.view3d.managers;

import bog.bgmaker.Main;
import bog.bgmaker.view3d.ObjectLoader;
import bog.bgmaker.view3d.utils.Const;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

/**
 * @author Bog
 */
public class WindowMan {

    public String title;
    public int width, height;
    public long window;
    public boolean resize, vSync;
    public Matrix4f projectionMatrix;
    public boolean isMinimized = false;
    public boolean isFocused = true;

    public WindowMan(String title, int width, int height)
    {
        this.title = title;
        this.width = width;
        this.height = height;
        vSync = true;
        projectionMatrix = new Matrix4f();
    }

    public void init()
    {
        GLFWErrorCallback.createPrint(System.err).set();

        if(!GLFW.glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW.");

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GL11.GL_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GL11.GL_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GL11.GL_TRUE);

        boolean maximised = false;

        if(width == 0 || height == 0)
        {
            width = 250;
            height = 250;
            GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, GLFW.GLFW_TRUE);
            maximised = true;
        }

        window = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);

        if(window == MemoryUtil.NULL)
            throw new RuntimeException("Failed to create GLFW window.");

        GLFW.glfwSetFramebufferSizeCallback(window, (window, width, height) ->
        {
            this.width = width;
            this.height = height;
            this.resize = true;
        });

        GLFW.glfwSetKeyCallback(window, (window, key, scancode, action, mods) ->
        {
            onKey(key, scancode, action, mods);
        });

        GLFW.glfwSetCharModsCallback(window, (window, codePoint, modifiers) ->
        {
            onChar(codePoint, modifiers);
        });

        GLFW.glfwSetWindowIconifyCallback(window, (window, iconified) ->
        {
            isMinimized = iconified;
        });

        GLFW.glfwSetScrollCallback(window, (window, xOffset, yOffset) ->
        {
            onMouseScroll(xOffset, yOffset);
        });

        if(maximised)
            GLFW.glfwMaximizeWindow(window);
        else
        {
            GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
            GLFW.glfwSetWindowPos(window, (vidMode.width() - width) / 2, (vidMode.height() - height) / 2);
        }

        GLFW.glfwSetWindowFocusCallback(window, (window, focused) ->
        {
            isFocused = focused;
        });

        GLFW.glfwMakeContextCurrent(window);

        if(vSync)
            GLFW.glfwSwapInterval(1);

        GLFW.glfwShowWindow(window);
        GL.createCapabilities();
        GL11.glClearColor(0, 0, 0, 0);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
    }

    public void update()
    {
        GLFW.glfwSwapBuffers(window);
        GLFW.glfwPollEvents();
    }

    public void cleanup()
    {
        GLFW.glfwDestroyWindow(window);
    }

    public void setClearColor(float r, float g, float b, float a)
    {
        GL11.glClearColor(r, g, b, a);
    }

    public boolean isKeyPressed(int key)
    {
        return GLFW.glfwGetKey(window, key) == GLFW.GLFW_PRESS;
    }

    public void onKey(int key, int scancode, int action, int mods)
    {
        Main.view.onKey(key, scancode, action, mods);
    }

    public void onChar(int codePoint, int modifiers)
    {
        Main.view.onChar(codePoint, modifiers);
    }

    public void onMouseScroll(double xOffset, double yOffset)
    {
        Main.view.onMouseScroll(xOffset, yOffset);
    }

    public boolean windowShouldClose()
    {
        return GLFW.glfwWindowShouldClose(window);
    }

    public Matrix4f updateProjectionMatrix()
    {
        float aspectRatio = ((width == 0 ? 1f : (float) width) / (height == 0 ? 1f : (float) height));
        return projectionMatrix.setPerspective(Const.FOV, aspectRatio, Const.Z_NEAR, Const.Z_FAR);
    }

    public Matrix4f updateProjectionMatrix(Matrix4f matrix, int width, int height)
    {
        float aspectRatio = ((width == 0 ? 1f : (float) width) / (height == 0 ? 1f : (float) height));
        return matrix.setPerspective(Const.FOV, aspectRatio, Const.Z_NEAR, Const.Z_FAR);
    }

    public void setTitle(String title) {
        this.title = title;
        GLFW.glfwSetWindowTitle(window, title);
    }

    public void setIcon(String path)
    {
        try {
            BufferedImage icon = ImageIO.read(WindowMan.class.getResourceAsStream(path));
            GLFWImage.Buffer iconBuffer = GLFWImage.malloc(1);
            GLFWImage iconImage = GLFWImage.malloc();
            iconImage.set(icon.getWidth(), icon.getHeight(), ObjectLoader.loadTextureBuffer(icon));
            iconBuffer.put(0, iconImage);
            GLFW.glfwSetWindowIcon(window, iconBuffer);
        } catch (Exception e) {e.printStackTrace();}
    }
}
