package bog.bgmaker.view3d.managers;

import bog.bgmaker.Main;
import bog.bgmaker.view3d.ILogic;
import bog.bgmaker.view3d.utils.Config;
import bog.bgmaker.view3d.utils.MousePicker;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

import java.util.ArrayList;

/**
 * @author Bog
 */
public class EngineMan {
    public static int fps = 0;
    public static int avgFPS = 60;
    public static double ms = 0;
    public static float frameTime = 1f / Math.max(Config.FRAMERATE, 5);

    public boolean isRunning;
    public WindowMan window;
    public MouseInput mouseInput;
    public GLFWErrorCallback errorCallback;
    public ILogic viewLogic;

    ArrayList<Integer> fpsBuffer = new ArrayList<>();

    public void init(WindowMan window, ILogic view) throws Exception
    {
        GLFW.glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
        this.window = window;
        viewLogic = view;
        mouseInput = new MouseInput(viewLogic);
        window.init();
        viewLogic.init();
        mouseInput.init(window);
    }

    public void start(WindowMan window, ILogic viewLogic) throws Exception
    {
        init(window, viewLogic);

        if(isRunning)
            return;
        run();
    }

    public void run()
    {
        isRunning = true;
        double previousTime  = GLFW.glfwGetTime();
        long lastTime = System.nanoTime();
        double unprocessedTime = 0;

        while(isRunning)
        {
            boolean render = false;
            double currentTime  = GLFW.glfwGetTime();
            long startTime = System.nanoTime();
            long passedTime = startTime - lastTime;
            lastTime = startTime;

            unprocessedTime += passedTime / (double) Config.NANOSECOND;
            frameTime = 1f / Math.max(Config.FRAMERATE, 1f);

            while(unprocessedTime > frameTime)
            {
                render = true;
                unprocessedTime -= frameTime;

                if(window.windowShouldClose())
                    stop();
            }

            if(render)
            {
                ms = currentTime - previousTime;
                fps = (int)(1d / ms);
                previousTime = currentTime;

                fpsBuffer.add(fps);
                while(fpsBuffer.size() > Math.max(fps, 1))
                    fpsBuffer.remove(0);

                int fps = 0;
                for(int f : fpsBuffer)
                    fps += f;
                fps /= fpsBuffer.size();
                avgFPS = fps;

                update();
                render();
            }
        }
        cleanup();
    }

    public void stop()
    {
        if(!isRunning)
            return;
        isRunning = false;
        System.exit(0);
    }

    public void render()
    {
        viewLogic.render();
        window.update();
    }

    public void update()
    {
        viewLogic.update(mouseInput);
    }

    public void cleanup()
    {
        window.cleanup();
        viewLogic.cleanup();
        errorCallback.free();
        GLFW.glfwTerminate();
    }

}
