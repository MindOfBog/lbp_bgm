package bog.lbpas.view3d.managers;

import bog.lbpas.Main;
import bog.lbpas.view3d.managers.assetLoading.ObjectLoader;
import bog.lbpas.view3d.utils.Config;
import bog.lbpas.view3d.utils.Cursors;
import bog.lbpas.view3d.utils.Utils;
import org.joml.Matrix4f;
import org.joml.Vector2d;
import org.joml.Vector2i;
import org.lwjgl.glfw.*;
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
    public boolean isMaximized = false;
    public boolean isFocused = true;
    public boolean isDragging = false;

    public Vector2d prevCursor = new Vector2d();
    public Vector2i prevWindow = new Vector2i();

    public long hWnd;

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
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_DECORATED, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);

        boolean maximised = false;

        if(width == 0 || height == 0)
        {
            width = 250;
            height = 250;
            GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, GLFW.GLFW_TRUE);
            maximised = true;
        }

        window = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);
        setIcon("/textures/icon_tree_only.svg");

        Cursors.updateCursors();

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

        GLFW.glfwSetWindowMaximizeCallback(window, (window, maximized) ->
        {
            isMaximized = maximized;
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
//      GL LOGS  GLUtil.setupDebugMessageCallback(System.err);
        GL11.glClearColor(0, 0, 0, 0);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
    }

    public void update()
    {
        GLFW.glfwSwapBuffers(window);
        GLFW.glfwPollEvents();

        if(isDragging)
        {
            Vector2d cursorPos = getCursorPosition();
            Vector2i windowPos = getWindowPosition();

            double diffX = (windowPos.x + cursorPos.x) - (prevWindow.x + (prevCursor.x * width));
            double diffY = (windowPos.y + cursorPos.y) - (prevWindow.y + prevCursor.y);

            if(diffX != 0 || diffY != 0)
            {
                if (isMaximized)
                {
                    if(diffX >= 5 || diffY >= 5)
                    {
                        prevCursor = new Vector2d(cursorPos.x / width, cursorPos.y);
                        prevWindow = windowPos;
                        restore();
                        setWindowPosition((int) (windowPos.x + diffX), (int) (windowPos.y + diffY));
                        setOpacity(0.75f);
                    }
                }
                else
                {
                    if(windowPos.y + diffY < 5)
                    {
                        maximize();
                        prevCursor = new Vector2d(cursorPos.x / width, cursorPos.y);
                        prevWindow = windowPos;
                    }
                    else
                    {
                        prevCursor = new Vector2d(cursorPos.x / width, cursorPos.y);
                        prevWindow = windowPos;
                        setWindowPosition((int) (windowPos.x + diffX), (int) (windowPos.y + diffY));
                    }
                }
            }
        }
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

    public void close()
    {
        GLFW.glfwSetWindowShouldClose(window, true);
    }

    public Matrix4f updateProjectionMatrix()
    {
        float aspectRatio = ((width == 0 ? 1f : (float) width) / (height == 0 ? 1f : (float) height));
        return projectionMatrix.setPerspective(Config.FOV, aspectRatio, Config.Z_NEAR, Config.Z_FAR);
    }

    public Matrix4f updateProjectionMatrix(Matrix4f matrix, int width, int height)
    {
        float aspectRatio = ((width == 0 ? 1f : (float) width) / (height == 0 ? 1f : (float) height));
        return matrix.setPerspective(Config.FOV, aspectRatio, Config.Z_NEAR, Config.Z_FAR);
    }

    public void setTitle(String title) {
        this.title = title;
        GLFW.glfwSetWindowTitle(window, title);
    }

    public void setIcon(String path)
    {
        try {
            BufferedImage icon;

            if(path.endsWith(".svg"))
            {
                String p = path.startsWith("/") ? path.substring(1) : path;
                icon = Utils.loadAndRenderSVG(Thread.currentThread().getContextClassLoader().getResourceAsStream(p), 128, 128, true);
            }
            else
            {
                String p = path.startsWith("/") ? path : "/" + path;
                icon = ImageIO.read(WindowMan.class.getResourceAsStream(p));
            }

            GLFWImage.Buffer iconBuffer = GLFWImage.malloc(1);
            GLFWImage iconImage = GLFWImage.malloc();
            iconImage.set(icon.getWidth(), icon.getHeight(), ObjectLoader.loadTextureBuffer(icon));
            iconBuffer.put(0, iconImage);
            GLFW.glfwSetWindowIcon(window, iconBuffer);
        } catch (Exception e) {e.printStackTrace();}
    }

    public void maximize()
    {
        GLFW.glfwMaximizeWindow(this.window);
    }

    public void restore()
    {
        GLFW.glfwRestoreWindow(this.window);
    }

    public void minimize()
    {
        GLFW.glfwIconifyWindow(this.window);
    }

    public void setOpacity(float opacity)
    {
        GLFW.glfwSetWindowOpacity(this.window, opacity);
    }

    public Vector2i getWindowPosition()
    {
        int[] x = new int[1];
        int[] y = new int[1];

        GLFW.glfwGetWindowPos(this.window, x, y);
        return new Vector2i(x[0], y[0]);
    }

    public void setWindowPosition(int x, int y)
    {
        GLFW.glfwSetWindowPos(window, x, y);
    }

    public Vector2d getCursorPosition()
    {
        double[] x = new double[1];
        double[] y = new double[1];

        GLFW.glfwGetCursorPos(this.window, x, y);

        return new Vector2d(x[0], y[0]);
    }
}
