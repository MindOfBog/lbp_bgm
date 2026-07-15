package bog.lbpas.view3d.managers;

import bog.lbpas.Main;
import bog.lbpas.view3d.renderer.ILogic;
import bog.lbpas.view3d.utils.Config;
import bog.lbpas.view3d.utils.Consts;
import bog.lbpas.view3d.utils.Cursors;
import bog.lbpas.view3d.utils.print;
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
    public ILogic viewLogic;

    ArrayList<Integer> fpsBuffer = new ArrayList<>();

    public void init(WindowMan window, ILogic view) throws Exception
    {
        this.window = window;
        viewLogic = view;
        mouseInput = new MouseInput(viewLogic);
        viewLogic.init();
        mouseInput.init(window);
        window.mouseInput = mouseInput;
    }

    public WindowMan initWindowMan(String title, int width, int height, int minWidth, int minHeight)
    {
        return new WindowMan(title, width, height, minWidth,minHeight);
    }

    public void start(WindowMan window, ILogic viewLogic) throws Exception
    {
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
            if(!Main.loaderThread.isAlive())
            {
                Main.loaderThread.stop();
                Main.loaderThread.start();
            }

            boolean render = false;
            double currentTime  = GLFW.glfwGetTime();
            long startTime = System.nanoTime();
            long passedTime = startTime - lastTime;
            lastTime = startTime;

            if(Config.FRAMERATE != 0)
            {
                unprocessedTime += passedTime / (double) Consts.NANOSECOND;
                frameTime = 1f / Config.FRAMERATE;
            }

            if(window.windowShouldClose())
                stop();

            while(Config.FRAMERATE != 0 && unprocessedTime > frameTime)
            {
                render = true;
                unprocessedTime -= frameTime;
            }

            if(render || Config.FRAMERATE == 0)
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

                Main.ExecuteGraphicsThreadQueue();
            }

            Cursors.updateCursor(window);
        }
        cleanup();
    }

    public void stop()
    {
        if(!isRunning)
            return;
        isRunning = false;
        Config.updateFile();
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
        GLFW.glfwTerminate();
    }

}
