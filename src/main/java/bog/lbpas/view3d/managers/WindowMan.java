package bog.lbpas.view3d.managers;

import bog.lbpas.Main;
import bog.lbpas.view3d.managers.assetLoading.ObjectLoader;
import bog.lbpas.view3d.renderer.Camera;
import bog.lbpas.view3d.renderer.gui.cursor.ECursor;
import bog.lbpas.view3d.utils.Config;
import bog.lbpas.view3d.utils.Cursors;
import bog.lbpas.view3d.utils.Utils;
import bog.lbpas.view3d.utils.print;
import org.joml.Matrix4f;
import org.joml.Vector2d;
import org.joml.Vector2i;
import org.joml.Vector4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.LongBuffer;
import java.util.ArrayList;

/**
 * @author Bog
 */
public class WindowMan {

    public String title;
    public int width, height, minWidth, minHeight;
    public long window;
    public long sharedContext;
    public boolean resize, vSync;
    public Matrix4f projectionMatrix;
    public boolean isMinimized = false;
    public boolean isMaximized = false;
    public boolean isFocused = true;
    public boolean isDragging = false;
    public int resizing = 0;

    public static final int RESIZE_TOP = 2;
    public static final int RESIZE_RIGHT = 4;
    public static final int RESIZE_BOTTOM = 8;
    public static final int RESIZE_LEFT = 16;

    public Vector2d prevCursor = new Vector2d();
    public Vector2i prevWindow = new Vector2i();

    public long hWnd;

    public WindowMan(String title, int width, int height, int minWidth, int minHeight)
    {
        this.title = title;
        this.width = width;
        this.height = height;
        this.minWidth = minWidth;
        this.minHeight = minHeight;
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
            width = 1280;
            height = 720;
            Config.WINDOW_WIDTH = 1280;
            Config.WINDOW_HEIGHT = 720;
            GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, GLFW.GLFW_TRUE);
            maximised = true;
            maximize();
        }

        window = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);
        setIcon("/textures/icon_tree_only.svg");

        Cursors.updateCursors();

        if(window == MemoryUtil.NULL)
            throw new RuntimeException("Failed to create GLFW window.");

        sharedContext = GLFW.glfwCreateWindow(1, 1, "sharedContext", MemoryUtil.NULL, window);
        if(sharedContext == MemoryUtil.NULL)
            throw new RuntimeException("Failed to create shared context.");

        GLFW.glfwSetFramebufferSizeCallback(window, (window, width, height) ->
        {
            if(resizing == 0)
            {
                this.width = width;
                this.height = height;
                this.resize = true;
            }
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
            Config.WINDOW_MAXIMIZED = maximized;
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

        if(Main.debug)
            GLUtil.setupDebugMessageCallback(System.out);

        GL11.glClearColor(0, 0, 0, 0);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
    }

    public int prevX = 0;
    public int prevY = 0;
    public int prevWidth = 0;
    public int prevHeight = 0;
    public Vector2d prevMousePos;

    public int newWidth = prevWidth;
    public int newHeight = prevHeight;

    public void update()
    {
//        setWindowSize(1920, 1080);

        GLFW.glfwSwapBuffers(window);
        GLFW.glfwPollEvents();

        if(isDragging)
        {
            Cursors.setCursor(ECursor.move);
            Vector2d cursorPosRelative = getCursorPosition();
            Vector2i windowPos = getWindowPosition();
            Vector2d cursorPosAbsolute = new Vector2d(windowPos.x + cursorPosRelative.x, windowPos.y + cursorPosRelative.y);

            double diffX = (windowPos.x + cursorPosRelative.x) - (prevWindow.x + (prevCursor.x * width));
            double diffY = (windowPos.y + cursorPosRelative.y) - (prevWindow.y + prevCursor.y);

            if(diffX != 0 || diffY != 0)
            {
                ArrayList<Vector4f> monitorAreas = getMonitorAreas(null);
                Vector4f currentCursorMonitorArea = monitorAreas.get(0);
                long currentCursorMonitor = -1;

                for(int i = 0; i < monitorAreas.size(); i++)
                    if(cursorPosAbsolute.x >= monitorAreas.get(i).x && cursorPosAbsolute.x <= monitorAreas.get(i).x + monitorAreas.get(i).z &&
                            cursorPosAbsolute.y >= monitorAreas.get(i).y && cursorPosAbsolute.y <= monitorAreas.get(i).y + monitorAreas.get(i).w)
                        currentCursorMonitorArea = monitorAreas.get(i);

                if (isMaximized)
                {
                    if((diffX >= 5 || diffY >= 5) && windowPos.y + diffY > 5 + currentCursorMonitorArea.y)
                    {
                        prevCursor = new Vector2d(cursorPosRelative.x / width, cursorPosRelative.y);
                        prevWindow = windowPos;
                        restore();
                        setWindowPosition((int) (windowPos.x + diffX), (int) (windowPos.y + diffY));
                        setOpacity(0.75f);
                    }
                }
                else
                {
                    if(windowPos.y + diffY <= 5 + currentCursorMonitorArea.y)
                    {
                        GLFW.glfwSetWindowPos(window, (int) (currentCursorMonitorArea.x + (currentCursorMonitorArea.z / 2) - (width / 2)), (int) (currentCursorMonitorArea.y + (currentCursorMonitorArea.w / 2) - (height / 2)));
                        maximize();
                        prevCursor = new Vector2d(cursorPosRelative.x / width, cursorPosRelative.y);
                        prevWindow = windowPos;
                    }
                    else
                    {
                        prevCursor = new Vector2d(cursorPosRelative.x / width, cursorPosRelative.y);
                        prevWindow = windowPos;
                        setWindowPosition((int) (windowPos.x + diffX), (int) (windowPos.y + diffY));
                    }
                }
            }
        }

        if(resizing != 0) {

            boolean top = Utils.isBitwiseBool(resizing, RESIZE_TOP);
            boolean bottom = Utils.isBitwiseBool(resizing, RESIZE_BOTTOM);
            boolean left = Utils.isBitwiseBool(resizing, RESIZE_LEFT);
            boolean right = Utils.isBitwiseBool(resizing, RESIZE_RIGHT);

            if((top && left) || (bottom && right))
            {
                Cursors.setCursor(ECursor.bd_double_arrow);
            }
            else if((top && right) || (bottom && left))
            {
                Cursors.setCursor(ECursor.fd_double_arrow);
            }
            else if(top || bottom)
            {
                Cursors.setCursor(ECursor.sb_v_double_arrow);
            }
            else if(left || right)
            {
                Cursors.setCursor(ECursor.sb_h_double_arrow);
            }

            Vector2d cursorPosRelative = getCursorPosition();
            Vector2i windowPos = getWindowPosition();
            Vector2d cursorPosAbsolute = new Vector2d(windowPos.x + cursorPosRelative.x, windowPos.y + cursorPosRelative.y);
            Vector2d prevCursorPosAbsolute = new Vector2d(prevX + prevMousePos.x, prevY + prevMousePos.y);

            int newX = prevX;
            int newY = prevY;
            newWidth = prevWidth;
            newHeight = prevHeight;

            if (isMaximized)
                restore();

            if (top)
            {
                if (prevMousePos != null) {
                    double yDiff = prevCursorPosAbsolute.y - cursorPosAbsolute.y;

                    int h = (int) (prevHeight + yDiff);

                    if(h >= minHeight)
                    {
                        newHeight = h;
                        Config.WINDOW_HEIGHT = newHeight;
                        newY = (int) (prevY - yDiff);
                    }
                    else
                    {
                        newHeight = minHeight;
                        Config.WINDOW_HEIGHT = newHeight;
                        newY = (int) (prevY + (prevHeight - minHeight));
                    }
                }
            }
            else if (bottom)
            {
                if (prevMousePos != null) {
                    double yDiff = cursorPosAbsolute.y - prevCursorPosAbsolute.y;

                    int h = (int) (prevHeight + yDiff);

                    if(h >= minHeight)
                    {
                        newHeight = h;
                        Config.WINDOW_HEIGHT = newHeight;
                    }
                    else
                    {
                        newHeight = minHeight;
                        Config.WINDOW_HEIGHT = newHeight;
                    }
                }
            }

            if (right)
            {
                if (prevMousePos != null) {
                    double xDiff = cursorPosAbsolute.x - prevCursorPosAbsolute.x;

                    int w = (int) (prevWidth + xDiff);

                    if(w >= minWidth)
                    {
                        newWidth = w;
                        Config.WINDOW_WIDTH = newWidth;
                    }
                    else
                    {
                        newWidth = minWidth;
                        Config.WINDOW_WIDTH = newWidth;
                    }
                }
            }
            else if (left)
            {
                if (prevMousePos != null) {
                    double xDiff = prevCursorPosAbsolute.x - cursorPosAbsolute.x;

                    int w = (int) (prevWidth + xDiff);

                    if(w >= minWidth)
                    {
                        newWidth = w;
                        Config.WINDOW_WIDTH = newWidth;
                        newX = (int) (prevX - xDiff);
                    }
                    else
                    {
                        newWidth = minWidth;
                        Config.WINDOW_WIDTH = newWidth;
                        newX = (int) (prevX + (prevWidth - minWidth));
                    }
                }
            }

            if(System.currentTimeMillis() - lastMouseMove < 100)
            {
                setWindowPosition(newX, newY);
                setWindowSize(newWidth, newHeight);
            }
            else
            {
                lastMouseMove = System.currentTimeMillis();
                this.width = newWidth;
                this.height = newHeight;
                this.resize = true;
            }
        }
    }

    long lastMouseMove = 0;
    double lastMouseMoveX = -1;
    double lastMouseMoveY = -1;
    public void onMousePos(double x, double y)
    {
        if(lastMouseMoveX != x || lastMouseMoveY != y)
            lastMouseMove = System.currentTimeMillis();
        lastMouseMoveX = x;
        lastMouseMoveY = y;
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

    public Matrix4f updateProjectionMatrix(Camera camera)
    {
        if(!Config.FRONT_VIEW)
        {
            float aspectRatio = ((width == 0 ? 1f : (float) width) / (height == 0 ? 1f : (float) height));
            return projectionMatrix.setPerspective(Config.FOV, aspectRatio, Config.Z_NEAR, Config.Z_FAR);
        }
        else
        {
            float aspectRatio = (width == 0 ? 1f : (float) width) / (height == 0 ? 1f : (float) height);
            float orthoWidth = width == 0 ? 1f : (float) width;
            float orthoHeight = height == 0 ? 1f : (float) height;

            float zoom = Math.max(camera.getPos().z / 1000f, 0.0f);

            orthoWidth *= zoom;
            orthoHeight *= zoom;

            float left = -orthoWidth / 2;
            float right = orthoWidth / 2;
            float bottom = -orthoHeight / 2;
            float top = orthoHeight / 2;
            float near = Config.Z_NEAR;
            float far = Config.Z_FAR;

            return projectionMatrix.setOrtho(left, right, bottom, top, near, far);
        }
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
        setWindowSize(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
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

    public void setWindowSize(int width, int height)
    {
        GLFW.glfwSetWindowSize(window, width, height);
    }
    public Vector2i getWindowSize()
    {
        int[] width = new int[1];
        int[] height = new int[1];
        GLFW.glfwGetWindowSize(window, width, height);
        return new Vector2i(width[0], height[0]);
    }

    public Vector2d getCursorPosition()
    {
        double[] x = new double[1];
        double[] y = new double[1];

        GLFW.glfwGetCursorPos(this.window, x, y);

        return new Vector2d(x[0], y[0]);
    }

    public ArrayList<Vector4f> getMonitorAreas(ArrayList<Long> handles)
    {
        PointerBuffer monitors = GLFW.glfwGetMonitors();

        ArrayList<Vector4f> out = new ArrayList<>();

        for(int i = 0; i < monitors.limit(); i++)
        {
            long monitor = monitors.get(i);

            int[] x = new int[1];
            int[] y = new int[1];
            int[] width = new int[1];
            int[] height = new int[1];
            GLFW.glfwGetMonitorWorkarea(monitor, x, y, width, height);

            if(handles != null)
                handles.add(monitor);
            out.add(new Vector4f(x[0], y[0], width[0], height[0]));
        }

        return out;
    }
}
