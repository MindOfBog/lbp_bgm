package bog.lbpas.view3d.managers;

import bog.lbpas.Main;
import bog.lbpas.view3d.managers.assetLoading.ObjectLoader;
import bog.lbpas.view3d.renderer.Camera;
import bog.lbpas.view3d.renderer.gui.cursor.ECursor;
import bog.lbpas.view3d.renderer.gui.elements.Element;
import bog.lbpas.view3d.utils.*;
import com.sun.jna.*;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.win32.StdCallLibrary;
import org.joml.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.windows.POINT;
import org.lwjgl.system.windows.User32;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.Math;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author Bog
 */
public class WindowMan {

    public String title;
    private int width, height;

    public int getWidth()
    {
        if(width < minWidth)
        {
            width = minWidth;
            resize = true;
        }

        return width;
    }

    public int getHeight()
    {
        if(height < minHeight)
        {
            height = minHeight;
            resize = true;
        }

        return height;
    }

    public void setWidth(int width) {
        this.width = Math.max(width, minWidth);
    }

    public void setHeight(int height) {
        this.height = Math.max(height, minHeight);
    }

    public final int minWidth, minHeight;
    public long window;
    public WinDef.HWND hwnd;
    public long sharedContext;
    public long glfwContext;
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

    public AudioMan audio;

    public ArrayList<Element> frameElements = new ArrayList<>();

    public GLCapabilities glCapabilities;
    public GLFWErrorCallback errorCallback;
    public WindowMan(String title, int width, int height, int minWidth, int minHeight)
    {
        this.title = title;
        this.width = width;
        this.height = height;
        this.minWidth = minWidth;
        this.minHeight = minHeight;
        this.vSync = true;
        this.projectionMatrix = new Matrix4f();
    }

    private int lastWidth, lastHeight;

    private Thread glfwThread;

    boolean windowFinishedInit = false;
    public void init()
    {
        CountDownLatch windowReady = new CountDownLatch(1);

        glfwThread = new Thread()
        {
            @Override
            public void run() {
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
                GLFW.glfwWindowHint(GLFW.GLFW_FLOATING, GLFW.GLFW_FALSE);

                if(Main.debug)
                    GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_DEBUG_CONTEXT, GLFW.GLFW_TRUE);

                boolean maximised = false;

                window = GLFW.glfwCreateWindow(WindowMan.this.getWidth(), WindowMan.this.getHeight(), title, MemoryUtil.NULL, MemoryUtil.NULL);
                setIcons(Main.iconList);

                if(WindowMan.this.width == 0 || WindowMan.this.height == 0)
                {
                    WindowMan.this.setWidth(WindowMan.this.minWidth);
                    WindowMan.this.setHeight(WindowMan.this.minHeight);
                    GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, GLFW.GLFW_TRUE);
                    maximised = true;
                    maximize();
                }

                Cursors.updateCursors();

                if(window == MemoryUtil.NULL)
                    throw new RuntimeException("Failed to create GLFW window.");

                sharedContext = GLFW.glfwCreateWindow(1, 1, "sharedContext", MemoryUtil.NULL, window);
                if(sharedContext == MemoryUtil.NULL)
                    throw new RuntimeException("Failed to create shared context.");

                if(maximised)
                    maximize();
                else
                {
                    GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
                    GLFW.glfwSetWindowPos(window, (vidMode.width() - WindowMan.this.getWidth()) / 2, (vidMode.height() - WindowMan.this.getHeight()) / 2);
                }

                /**
                 * GLFW Callbacks
                 */

                GLFW.glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));

                GLFW.glfwSetFramebufferSizeCallback(window, (w, width, height) -> {
                    Main.RunOnGraphicsThread(() ->
                    {
                        if (width != lastWidth || height != lastHeight) {
                            lastWidth = width;
                            lastHeight = height;
                            onWindowResized(width, height);
                        }
                    });
                });

                GLFW.glfwSetWindowPosCallback(window, (w, x, y) -> {
                    Main.RunOnGraphicsThread(() ->
                    {
                        onWindowMoved(x, y);
                    });
                });

                GLFW.glfwSetKeyCallback(window, (window, key, scancode, action, mods) ->
                {
                    Main.RunOnGraphicsThread(() ->
                    {
                        onKey(key, scancode, action, mods);
                    });
                });

                GLFW.glfwSetCharModsCallback(window, (window, codePoint, modifiers) ->
                {
                    Main.RunOnGraphicsThread(() ->
                    {
                        onChar(codePoint, modifiers);
                    });
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
                    Main.RunOnGraphicsThread(() ->
                    {
                        onMouseScroll(xOffset, yOffset);
                    });
                });

                GLFW.glfwSetWindowFocusCallback(window, (window, focused) ->
                {
                    isFocused = focused;
                    FilePicker.onWindowFocus(focused);
                });


                GLFW.glfwSetCursorPosCallback(window, (window, xpos, ypos) ->
                {
                    Main.RunOnGraphicsThread(() ->
                    {
                        onMousePos(xpos, ypos);
                    });
                });

                GLFW.glfwSetCursorEnterCallback(window, (window, entered) ->
                {
                    if(mouseInput != null)
                        mouseInput.inWindow = entered;
                });

                GLFW.glfwSetMouseButtonCallback(window, (window, button, action, mods) ->
                {
                    Main.RunOnGraphicsThread(() ->
                    {
                        onMouseClick(button, action, mods);
                    });
                });

                GLFW.glfwMakeContextCurrent(window);
                if(vSync)
                    GLFW.glfwSwapInterval(1);

                GLFW.glfwShowWindow(window);
                GL.createCapabilities();
                GL11.glClearColor(1f, 1f, 1f, 1f);
                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
                GLFW.glfwSwapBuffers(window);
                GLFW.glfwMakeContextCurrent(0);

                windowReady.countDown();

                while (!GLFW.glfwWindowShouldClose(window)) {
                    GLFW.glfwPollEvents();

                    if(windowFinishedInit)
                    {
                        Main.ExecuteGLFWThreadQueue();
                        mouseInput.updateGLFW(WindowMan.this);
                    }

                    Thread.yield();
                }
            }
        };

        glfwThread.setName("glfw-thread");
        glfwThread.start();

        try {
            windowReady.await();
        } catch (InterruptedException e) {
            print.stackTrace(e);
        }

        GLFW.glfwMakeContextCurrent(window);
        glCapabilities = GL.createCapabilities();

        if (glCapabilities.GL_ARB_parallel_shader_compile)
            ARBParallelShaderCompile.glMaxShaderCompilerThreadsARB(4);

        if(Main.debug)
            GLUtil.setupDebugMessageCallback(System.out);

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);

        this.audio = new AudioMan();
        this.audio.init();

        //todo

        if (Consts.OPERATING_SYSTEM.contains("win"))
        {
            this.winNative = new WindowNativeWin(this);

            if(Config.USE_NATIVE_WINDOW_HANDLER)
                this.winNative.setupWindowControl();
        }
        else if (Consts.OPERATING_SYSTEM.contains("mac"))
        {

        }
        else if (Consts.OPERATING_SYSTEM.contains("nux") || Consts.OPERATING_SYSTEM.contains("nix"))
        {

        }
    }

    public void setupNativeWindowHandler()
    {
        if (Consts.OPERATING_SYSTEM.contains("win"))
        {
            this.winNative.setupWindowControl();
        }
        else if (Consts.OPERATING_SYSTEM.contains("mac"))
        {

        }
        else if (Consts.OPERATING_SYSTEM.contains("nux") || Consts.OPERATING_SYSTEM.contains("nix"))
        {

        }
    }

    public void releaseNativeWindowHandler()
    {
        if (Consts.OPERATING_SYSTEM.contains("win"))
        {
            this.winNative.revertWindowControl();
        }
        else if (Consts.OPERATING_SYSTEM.contains("mac"))
        {

        }
        else if (Consts.OPERATING_SYSTEM.contains("nux") || Consts.OPERATING_SYSTEM.contains("nix"))
        {

        }
    }

    public MouseInput mouseInput;

    private static WindowNativeWin winNative;

    public int prevX = 0;
    public int prevY = 0;
    public int prevWidth = 0;
    public int prevHeight = 0;
    public Vector2d prevMousePos;

    public int newWidth = 0;
    public int newHeight = 0;

    public void update() {

        windowFinishedInit = true;
        GLFW.glfwSwapBuffers(window);
        for(ShaderMan shader : ShaderMan.SHADER_LINKING_QUEUE)
        {
            try {
                shader.link();
            }catch (Exception e)
            {
                print.stackTrace(e);
            }
        }
        ShaderMan.SHADER_LINKING_QUEUE.clear();
        GLFW.glfwPollEvents();

        if(!useNativeWindowHandler())
        {
            if (isDragging) {
                Cursors.setCursor(ECursor.move);
                Vector2d cursorPosRelative = getCursorPosition();
                Vector2i windowPos = getWindowPosition();
                Vector2d cursorPosAbsolute = new Vector2d(windowPos.x + cursorPosRelative.x, windowPos.y + cursorPosRelative.y);

                double diffX = (windowPos.x + cursorPosRelative.x) - (prevWindow.x + (prevCursor.x * width));
                double diffY = (windowPos.y + cursorPosRelative.y) - (prevWindow.y + prevCursor.y);

                if (diffX != 0 || diffY != 0) {
                    ArrayList<Vector4f> monitorAreas = getMonitorAreas(null);
                    Vector4f currentCursorMonitorArea = monitorAreas.get(0);
                    long currentCursorMonitor = -1;

                    for (int i = 0; i < monitorAreas.size(); i++)
                        if (cursorPosAbsolute.x >= monitorAreas.get(i).x && cursorPosAbsolute.x <= monitorAreas.get(i).x + monitorAreas.get(i).z &&
                                cursorPosAbsolute.y >= monitorAreas.get(i).y && cursorPosAbsolute.y <= monitorAreas.get(i).y + monitorAreas.get(i).w)
                            currentCursorMonitorArea = monitorAreas.get(i);

                    if (isMaximized) {
                        if ((diffX >= 5 || diffY >= 5) && windowPos.y + diffY > 5 + currentCursorMonitorArea.y) {
                            prevCursor = new Vector2d(cursorPosRelative.x / width, cursorPosRelative.y);
                            prevWindow = windowPos;
                            restore();
                            setWindowPosition((int) (windowPos.x + diffX), (int) (windowPos.y + diffY));
                            setOpacity(0.75f);
                        }
                    } else {
                        if (windowPos.y + diffY <= 5 + currentCursorMonitorArea.y) {
                            GLFW.glfwSetWindowPos(window, (int) (currentCursorMonitorArea.x + (currentCursorMonitorArea.z / 2) - (width / 2)), (int) (currentCursorMonitorArea.y + (currentCursorMonitorArea.w / 2) - (height / 2)));
                            maximize();
                            prevCursor = new Vector2d(cursorPosRelative.x / width, cursorPosRelative.y);
                            prevWindow = windowPos;
                        } else {
                            prevCursor = new Vector2d(cursorPosRelative.x / width, cursorPosRelative.y);
                            prevWindow = windowPos;
                            setWindowPosition((int) (windowPos.x + diffX), (int) (windowPos.y + diffY));
                        }
                    }
                }
                }

            if (resizing != 0) {
                boolean top = Utils.isBitwiseBool(resizing, RESIZE_TOP);
                boolean bottom = Utils.isBitwiseBool(resizing, RESIZE_BOTTOM);
                boolean left = Utils.isBitwiseBool(resizing, RESIZE_LEFT);
                boolean right = Utils.isBitwiseBool(resizing, RESIZE_RIGHT);

                if ((top && left) || (bottom && right)) {
                    Cursors.setCursor(ECursor.bd_double_arrow);
                } else if ((top && right) || (bottom && left)) {
                    Cursors.setCursor(ECursor.fd_double_arrow);
                } else if (top || bottom) {
                    Cursors.setCursor(ECursor.sb_v_double_arrow);
                } else if (left || right) {
                    Cursors.setCursor(ECursor.sb_h_double_arrow);
                }

                Vector2d cursorPosRelative = getCursorPosition();
                Vector2i windowPos = getWindowPosition();
                Vector2d cursorPosAbsolute = new Vector2d(windowPos.x + cursorPosRelative.x, windowPos.y + cursorPosRelative.y);
                Vector2d prevCursorPosAbsolute = new Vector2d(prevX + prevMousePos.x, prevY + prevMousePos.y);

                int newX = prevX;
                int newY = prevY;
                int newWidth = prevWidth;
                int newHeight = prevHeight;

                if (isMaximized)
                    restore();

                if (top) {
                    if (prevMousePos != null) {
                        double yDiff = prevCursorPosAbsolute.y - cursorPosAbsolute.y;

                        int h = (int) (prevHeight + yDiff);

                        if (h >= minHeight) {
                            newHeight = h;
                            Config.WINDOW_HEIGHT = newHeight;
                            newY = (int) (prevY - yDiff);
                        } else {
                            newHeight = minHeight;
                            Config.WINDOW_HEIGHT = newHeight;
                            newY = prevY + (prevHeight - minHeight);
                        }
                    }
                } else if (bottom) {
                    if (prevMousePos != null) {
                        double yDiff = cursorPosAbsolute.y - prevCursorPosAbsolute.y;

                        int h = (int) (prevHeight + yDiff);

                        if (h >= minHeight) {
                            newHeight = h;
                            Config.WINDOW_HEIGHT = newHeight;
                        } else {
                            newHeight = minHeight;
                            Config.WINDOW_HEIGHT = newHeight;
                        }
                    }
                }

                if (right) {
                    if (prevMousePos != null) {
                        double xDiff = cursorPosAbsolute.x - prevCursorPosAbsolute.x;

                        int w = (int) (prevWidth + xDiff);

                        if (w >= minWidth) {
                            newWidth = w;
                            Config.WINDOW_WIDTH = newWidth;
                        } else {
                            newWidth = minWidth;
                            Config.WINDOW_WIDTH = newWidth;
                        }
                    }
                } else if (left) {
                    if (prevMousePos != null) {
                        double xDiff = prevCursorPosAbsolute.x - cursorPosAbsolute.x;

                        int w = (int) (prevWidth + xDiff);

                        if (w >= minWidth) {
                            newWidth = w;
                            Config.WINDOW_WIDTH = newWidth;
                            newX = (int) (prevX - xDiff);
                        } else {
                            newWidth = minWidth;
                            Config.WINDOW_WIDTH = newWidth;
                            newX = prevX + (prevWidth - minWidth);
                        }
                    }
                }

                setWindowPosition(newX, newY);
                setWindowSize(newWidth, newHeight);

                Vector2i size = this.getWindowSize();
                Config.WINDOW_WIDTH = size.x;
                Config.WINDOW_HEIGHT = size.y;
            }
        }
        if(shouldResizeInABit != -1)
        {
            if(System.currentTimeMillis() - shouldResizeInABit > 100)
            {
                this.setWidth(this.newWidth);
                this.setHeight(this.newHeight);
                this.resize = true;

                Vector2i size = this.getWindowSize();
                Config.WINDOW_WIDTH = size.x;
                Config.WINDOW_HEIGHT = size.y;

                shouldResizeInABit = -1;
            }
        }
    }
    private long shouldResizeInABit = -1;

    final long[] prevMs = {0};
    public void onTitleBarClick(int action)
    {
        if(action == GLFW.GLFW_PRESS)
        {
            long currentMs = System.currentTimeMillis();
            if(currentMs - prevMs[0] <= 500)
            {
                if(this.isMaximized)
                    this.restore();
                else
                    this.maximize();
                prevMs[0] = 0;
            }
            else
            {
                prevMs[0] = currentMs;
                this.isDragging = true;
                this.beginDrag();
                if(!this.isMaximized)
                    this.setOpacity(0.75f);
                Vector2i windowPos = this.getWindowPosition();
                Vector2d cursorPos = this.getCursorPosition();
                this.prevCursor = new Vector2d(cursorPos.x / this.getWidth(), cursorPos.y);
                this.prevWindow = windowPos;
            }
        }
    }

    public boolean useNativeWindowHandler()
    {
        return Config.USE_NATIVE_WINDOW_HANDLER &&
                (Consts.OPERATING_SYSTEM.contains("win"));
    }
    public void beginDrag() {//todo

        if (Consts.OPERATING_SYSTEM.contains("win"))
        {

        }
        else if (Consts.OPERATING_SYSTEM.contains("mac"))
        {

        }
        else if (Consts.OPERATING_SYSTEM.contains("nux") || Consts.OPERATING_SYSTEM.contains("nix"))
        {

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

        if(FilePicker.dialogOpen || mouseInput == null)
            return;

        mouseInput.onMousePos(this, x, y);
    }

    public void onMouseClick(int button, int action, int mods)
    {
        if(FilePicker.dialogOpen || mouseInput == null)
            return;

        if(action == GLFW.GLFW_PRESS)
        {
            mouseInput.leftButtonPress = button == GLFW.GLFW_MOUSE_BUTTON_1 || mouseInput.leftButtonPress;
            mouseInput.rightButtonPress = button == GLFW.GLFW_MOUSE_BUTTON_2 || mouseInput.rightButtonPress;
            mouseInput.middleButtonPress = button == GLFW.GLFW_MOUSE_BUTTON_3 || mouseInput.middleButtonPress;
            mouseInput.mouse4Press = button == GLFW.GLFW_MOUSE_BUTTON_4 || mouseInput.mouse4Press;
            mouseInput.mouse5Press = button == GLFW.GLFW_MOUSE_BUTTON_5 || mouseInput.mouse5Press;
            mouseInput.mouse6Press = button == GLFW.GLFW_MOUSE_BUTTON_6 || mouseInput.mouse6Press;
            mouseInput.mouse7Press = button == GLFW.GLFW_MOUSE_BUTTON_7 || mouseInput.mouse7Press;
            mouseInput.mouse8Press = button == GLFW.GLFW_MOUSE_BUTTON_8 || mouseInput.mouse8Press;

            if(!useNativeWindowHandler())
            {
                boolean top = mouseInput.currentPos.y <= 3;
                boolean bottom = mouseInput.currentPos.y >= this.getHeight() - 3;
                boolean left = mouseInput.currentPos.x <= 3;
                boolean right = mouseInput.currentPos.x >= this.getWidth() - 3;

                this.resizing = Utils.setBitwiseBool(this.resizing, WindowMan.RESIZE_TOP, top);
                this.resizing = Utils.setBitwiseBool(this.resizing, WindowMan.RESIZE_BOTTOM, bottom);
                this.resizing = Utils.setBitwiseBool(this.resizing, WindowMan.RESIZE_LEFT, left);
                this.resizing = Utils.setBitwiseBool(this.resizing, WindowMan.RESIZE_RIGHT, right);
            }

            this.prevX = this.getWindowPosition().x;
            this.prevY = this.getWindowPosition().y;
            this.prevWidth = this.getWindowSize().x;
            this.prevHeight = this.getWindowSize().y;
            this.prevMousePos = new Vector2d(mouseInput.currentPos);
        }
        else if(action == GLFW.GLFW_RELEASE)
        {
            if(button == GLFW.GLFW_MOUSE_BUTTON_1) mouseInput.leftButtonPress = !mouseInput.leftButtonPress;
            if(button == GLFW.GLFW_MOUSE_BUTTON_2) mouseInput.rightButtonPress = !mouseInput.rightButtonPress;
            if(button == GLFW.GLFW_MOUSE_BUTTON_3) mouseInput.middleButtonPress = !mouseInput.middleButtonPress;
            if(button == GLFW.GLFW_MOUSE_BUTTON_4) mouseInput.mouse4Press = !mouseInput.mouse4Press;
            if(button == GLFW.GLFW_MOUSE_BUTTON_5) mouseInput.mouse5Press = !mouseInput.mouse5Press;
            if(button == GLFW.GLFW_MOUSE_BUTTON_6) mouseInput.mouse6Press = !mouseInput.mouse6Press;
            if(button == GLFW.GLFW_MOUSE_BUTTON_7) mouseInput.mouse7Press = !mouseInput.mouse7Press;
            if(button == GLFW.GLFW_MOUSE_BUTTON_8) mouseInput.mouse8Press = !mouseInput.mouse8Press;
        }

        mouseInput.onMouseClick(button, action, mods);
    }

    public void cleanup()
    {
        this.audio.cleanup();
        errorCallback.free();
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

    public void onWindowMoved(int x, int y)
    {
        //todo
    }

    public void onWindowResized(int width, int height)
    {
        //todo
        this.newWidth = width;
        this.newHeight = height;
        this.shouldResizeInABit = System.currentTimeMillis();
    }

    public void onChar(int codePoint, int modifiers)
    {
        Main.view.onChar(codePoint, modifiers);
    }

    public void onMouseScroll(double xOffset, double yOffset)
    {
        if(Main.view != null)
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

    public Matrix4f updateProjectionMatrix(RenderMan renderer, Camera camera)
    {
        if(!Config.FRONT_VIEW)
        {
            float aspectRatio = ((float)renderer.viewPortWidth / (float)renderer.viewPortHeight);
            return projectionMatrix.setPerspective(Config.FOV, aspectRatio, Config.Z_NEAR, Config.Z_FAR);
        }
        else
        {
            float aspectRatio = ((float)renderer.viewPortWidth / (float)renderer.viewPortHeight);
            float orthoWidth = renderer.viewPortWidth;
            float orthoHeight = renderer.viewPortHeight;

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
        } catch (Exception e) {print.stackTrace(e);}
    }

    public void setIcons(List<Image> icons)
    {
        GLFWImage.Buffer iconBuffer = GLFWImage.malloc(icons.size());

        for(int i = 0; i < icons.size(); i++)
        {
            BufferedImage icon = (BufferedImage) icons.get(i);
            GLFWImage iconImage = GLFWImage.malloc();

            try {
                iconImage.set(icon.getWidth(), icon.getHeight(), ObjectLoader.loadTextureBuffer(icon));
                iconBuffer.put(i, iconImage);
            } catch (Exception e) {print.stackTrace(e);}
        }

        GLFW.glfwSetWindowIcon(window, iconBuffer);
    }

    public void maximize()
    {
        GLFW.glfwMaximizeWindow(this.window);
        Vector2i size = getWindowSize();
        this.setWidth(size.x);
        this.setHeight(size.y);
        this.resize = true;
        shouldResizeInABit = -1;
    }

    public void restore()
    {
        GLFW.glfwRestoreWindow(this.window);
        setWindowSize(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
        Vector2i size = getWindowSize();
        this.setWidth(size.x);
        this.setHeight(size.y);
        this.resize = true;
        shouldResizeInABit = -1;
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

    public static class WindowNativeWin {
        private long hwndAddress;
        private WinDef.HWND hwnd;
        private long originalWndProc;

        private static final int HTCAPTION = 2;
        private static final int HTCLIENT = 1;
        private static final int HTLEFT = 10;
        private static final int HTRIGHT = 11;
        private static final int HTTOP = 12;
        private static final int HTTOPLEFT = 13;
        private static final int HTTOPRIGHT = 14;
        private static final int HTBOTTOM = 15;
        private static final int HTBOTTOMLEFT = 16;
        private static final int HTBOTTOMRIGHT = 17;

        private static final int WM_GETMINMAXINFO = 0x24;
        private static final int WM_NULL = 0x0;
        private static final int WM_NCCALCSIZE = 0x0083;
        private static final int WM_NCHITTEST = 0x84;
        private static final int WM_ENTERSIZEMOVE = 0x0231;
        private static final int WM_EXITSIZEMOVE  = 0x0232;
        private static final int WM_TIMER         = 0x0113;
        private static final int RENDER_TIMER_ID  = 1;
        private static final int TIMER_INTERVAL_MS = 8;

        private WindowMan windowMan;

        public WindowNativeWin(WindowMan window) {
            this.windowMan = window;
            hwndAddress = GLFWNativeWin32.glfwGetWin32Window(window.window);
            hwnd = new WinDef.HWND(new Pointer(hwndAddress));
        }

        public void setupWindowControl()
        {
            fixWindowStyles();
            subclassWindow();
        }

        public void revertWindowControl()
        {
            resetStyle();
            releaseWndProcCallback();
        }

        private long originalStyle;

        private void fixWindowStyles()
        {
            WinDef.HWND hwnd = new WinDef.HWND(new Pointer(hwndAddress));

            originalStyle = User32.GetWindowLongPtr(hwndAddress, User32.GWL_STYLE);
            long newStyle = (originalStyle & ~User32.WS_CAPTION) | User32.WS_THICKFRAME | User32.WS_SYSMENU | User32.WS_MAXIMIZEBOX | User32.WS_MINIMIZEBOX;
            User32.SetWindowLongPtr(null, hwndAddress, User32.GWL_STYLE, newStyle);

            Vector2i pos = windowMan.getWindowPosition();
            Vector2i size = windowMan.getWindowSize();
            User32.SetWindowPos(null, hwndAddress, User32.HWND_TOP, pos.x, pos.y, size.x, size.y, User32.SWP_FRAMECHANGED);
        }

        private void resetStyle()
        {
            User32.SetWindowLongPtr(null, hwndAddress, User32.GWL_STYLE, originalStyle);
            //update window
            Vector2i size = windowMan.getWindowSize();

            windowMan.newWidth = size.x;
            windowMan.newHeight = size.y;
            windowMan.shouldResizeInABit = System.currentTimeMillis() - 400;
        }

        private void subclassWindow()
        {
            originalWndProc = User32.GetWindowLongPtr(hwndAddress, User32.GWL_WNDPROC);
            User32.SetWindowLongPtr(null, hwndAddress, User32.GWL_WNDPROC, Pointer.nativeValue(CallbackReference.getFunctionPointer(wndProcCallback)));
            //update window
            User32.SetWindowPos(null, hwndAddress, 0, 0, 0, 0, 0, User32.SWP_NOMOVE | User32.SWP_NOSIZE | User32.SWP_NOZORDER | User32.SWP_FRAMECHANGED);
        }

        private void releaseWndProcCallback()
        {
            User32.SetWindowLongPtr(null, hwndAddress, User32.GWL_WNDPROC, originalWndProc);
        }

        public interface WndProcCallback extends StdCallLibrary.StdCallCallback
        {
            WinDef.LRESULT callback(WinDef.HWND hWnd, int msg, WinDef.WPARAM wParam, WinDef.LPARAM lParam);
        }

        private WndProcCallback wndProcCallback = new WndProcCallback()
        {
            @Override
            public WinDef.LRESULT callback(WinDef.HWND hWnd, int msg, WinDef.WPARAM wParam, WinDef.LPARAM lParam)
            {
                switch (msg)
                {
                    case WM_NCHITTEST:
                    {
                        POINT mousePos = new POINT(BufferUtils.createByteBuffer(8));
                        User32.GetCursorPos(mousePos);
                        POINT windowPos = new POINT(BufferUtils.createByteBuffer(8));
                        User32.ClientToScreen(Pointer.nativeValue(hWnd.getPointer()), windowPos);

                        Vector2i localMousePos = new Vector2i(mousePos.x() - windowPos.x(), mousePos.y() - windowPos.y());

                        boolean overOthers = false;

                        for(int i = windowMan.frameElements.size() - 1; i >= 1; i--)
                        {
                            Element element = windowMan.frameElements.get(i);

                            if(element.isMouseOverElement(new Vector2f((float) localMousePos.x, (float) localMousePos.y)))
                                overOthers = true;
                        }

                        if (!overOthers && windowMan.frameElements.size() > 0 && windowMan.frameElements.get(0).isMouseOverElement(new Vector2f((float) localMousePos.x, (float) localMousePos.y)))
                        {
                            windowMan.onMousePos(-999, -999);
                            return new WinDef.LRESULT(HTCAPTION);
                        }

                        Vector2i windowSize = windowMan.getWindowSize();

                        boolean top = localMousePos.y <= 3;
                        boolean bottom = localMousePos.y >= windowSize.y - 3;
                        boolean left = localMousePos.x <= 3;
                        boolean right = localMousePos.x >= windowSize.x - 3;

                        if(top && left)
                        {
                            windowMan.onMousePos(-999, -999);
                            return new WinDef.LRESULT(HTTOPLEFT);
                        }
                        if(top && right)
                        {
                            windowMan.onMousePos(-999, -999);
                            return new WinDef.LRESULT(HTTOPRIGHT);
                        }
                        if(bottom && left)
                        {
                            windowMan.onMousePos(-999, -999);
                            return new WinDef.LRESULT(HTBOTTOMLEFT);
                        }
                        if(bottom && right)
                        {
                            windowMan.onMousePos(-999, -999);
                            return new WinDef.LRESULT(HTBOTTOMRIGHT);
                        }

                        if(top)
                        {
                            windowMan.onMousePos(-999, -999);
                            return new WinDef.LRESULT(HTTOP);
                        }
                        if(bottom)
                        {
                            windowMan.onMousePos(-999, -999);
                            return new WinDef.LRESULT(HTBOTTOM);
                        }
                        if(left)
                        {
                            windowMan.onMousePos(-999, -999);
                            return new WinDef.LRESULT(HTLEFT);
                        }
                        if(right)
                        {
                            windowMan.onMousePos(-999, -999);
                            return new WinDef.LRESULT(HTRIGHT);
                        }

                        return new WinDef.LRESULT(HTCLIENT);
                    }
                    case WM_NCCALCSIZE:
                    {
                        if(wParam.intValue() == 1)
                        {
                            WinUser.WINDOWPLACEMENT placement = new WinUser.WINDOWPLACEMENT();
                            com.sun.jna.platform.win32.User32.INSTANCE.GetWindowPlacement(hWnd, placement);

                            boolean isMaximized = (placement.showCmd == WinUser.SW_MAXIMIZE);

                            if(isMaximized)
                                return new WinDef.LRESULT(0);

                            long defaultResult = User32.nCallWindowProc(originalWndProc, Pointer.nativeValue(hWnd.getPointer()), msg, Pointer.nativeValue(wParam.toPointer()), Pointer.nativeValue(lParam.toPointer()));

                            long lparamPointer = Pointer.nativeValue(lParam.toPointer());
                            Pointer p = new Pointer(lparamPointer);

                            int left   = p.getInt(0);
                            int top    = p.getInt(4);
                            int right  = p.getInt(8);
                            int bottom = p.getInt(12);

                            int frameThickness = com.sun.jna.platform.win32.User32.INSTANCE.GetSystemMetrics(WinUser.SM_CYFRAME);
                            int paddedBorder   = com.sun.jna.platform.win32.User32.INSTANCE.GetSystemMetrics(WinUser.SM_CXPADDEDBORDER);

                            p.setInt(4, top - ((frameThickness / 2) + paddedBorder));

                            return new WinDef.LRESULT(defaultResult);
                        }
                        break;
                    }
                    case WM_GETMINMAXINFO:
                    {
                        MINMAXINFO minmax = new MINMAXINFO(new Pointer(lParam.longValue()));
                        minmax.read();

                        int frameThickness = com.sun.jna.platform.win32.User32.INSTANCE.GetSystemMetrics(WinUser.SM_CYFRAME);
                        int paddedBorder = com.sun.jna.platform.win32.User32.INSTANCE.GetSystemMetrics(WinUser.SM_CXPADDEDBORDER);
                        int totalBorder = frameThickness + paddedBorder;

                        minmax.ptMinTrackSize.x = windowMan.minWidth + totalBorder;
                        minmax.ptMinTrackSize.y = windowMan.minHeight + totalBorder;

                        minmax.ptMaxTrackSize.x = 9999999;
                        minmax.ptMaxTrackSize.y = 9999999;

                        WinUser.WINDOWPLACEMENT placement = new WinUser.WINDOWPLACEMENT();
                        com.sun.jna.platform.win32.User32.INSTANCE.GetWindowPlacement(hWnd, placement);

                        if (!(placement.showCmd == WinUser.SW_MAXIMIZE))
                        {
                            WinUser.HMONITOR hMonitor = com.sun.jna.platform.win32.User32.INSTANCE.MonitorFromWindow(hwnd, WinUser.MONITOR_DEFAULTTONEAREST);

                            if (hMonitor != null)
                            {
                                WinUser.MONITORINFO monitorInfo = new WinUser.MONITORINFO();
                                com.sun.jna.platform.win32.User32.INSTANCE.GetMonitorInfo(hMonitor, monitorInfo);

                                int workWidth  = monitorInfo.rcWork.right - monitorInfo.rcWork.left;
                                int workHeight = monitorInfo.rcWork.bottom - monitorInfo.rcWork.top;

                                minmax.ptMaxSize.x = workWidth;
                                minmax.ptMaxSize.y = workHeight;

                                minmax.ptMaxPosition.x = monitorInfo.rcWork.left;
                                minmax.ptMaxPosition.y = monitorInfo.rcWork.top;
                            }
                        }

                        minmax.write();
                        return new WinDef.LRESULT(0);
                    }
                    case WM_ENTERSIZEMOVE:
                        Main.RunOnGraphicsThread(() -> windowMan.setOpacity(0.75f));
                        break;
                    case WM_EXITSIZEMOVE:
                        Main.RunOnGraphicsThread(() -> windowMan.setOpacity(1f));
                        break;
                }

                return new WinDef.LRESULT(User32.nCallWindowProc(originalWndProc, Pointer.nativeValue(hWnd.getPointer()), msg, Pointer.nativeValue(wParam.toPointer()), Pointer.nativeValue(lParam.toPointer())));
            }
        };

        @Structure.FieldOrder({"ptReserved", "ptMaxSize", "ptMaxPosition", "ptMinTrackSize", "ptMaxTrackSize"})
        public static class MINMAXINFO extends Structure {
            public WinDef.POINT ptReserved;
            public WinDef.POINT ptMaxSize;
            public WinDef.POINT ptMaxPosition;
            public WinDef.POINT ptMinTrackSize;
            public WinDef.POINT ptMaxTrackSize;

            public MINMAXINFO() {
                super();
            }

            public MINMAXINFO(Pointer pointer) {
                super(pointer);
            }
        }
    }
}
