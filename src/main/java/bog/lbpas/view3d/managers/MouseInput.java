package bog.lbpas.view3d.managers;

import bog.lbpas.Main;
import bog.lbpas.view3d.renderer.ILogic;
import bog.lbpas.view3d.mainWindow.View3D;
import bog.lbpas.view3d.utils.Consts;
import bog.lbpas.view3d.utils.FilePicker;
import bog.lbpas.view3d.utils.MousePicker;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

/**
 * @author Bog
 */
public class MouseInput {

    public Vector2d currentPos;
    public Vector2f displayVec;

    public boolean inWindow = false,
            leftButtonPress = false, middleButtonPress = false, rightButtonPress = false,
            mouse4Press = false, mouse5Press = false, mouse6Press = false, mouse7Press = false, mouse8Press = false;

    ILogic viewLogic;

    private WindowMan window;

    public MousePicker mousePicker;

    public MouseInput(ILogic viewLogic)
    {
        currentPos = new Vector2d(0, 0);
        displayVec = new Vector2f();
        this.viewLogic = viewLogic;
    }

    public void init(WindowMan windowMan)
    {
        this.window = windowMan;
        mousePicker = new MousePicker(this, windowMan);
    }

    public void setMousePos(double x, double y)
    {
        GLFW.glfwSetCursorPos(window.window, x, y);
    }

    public void onMousePos(WindowMan window, double xPos, double yPos)
    {
//        currentPos.set(xPos, yPos);
        viewLogic.onMouseMove(this, xPos, yPos);
    }

    public long lastLeftDownMS = System.currentTimeMillis();
    public long lastLeftUpMS = System.currentTimeMillis();
    public long lastRightDownMS = System.currentTimeMillis();
    public long lastRightUpMS = System.currentTimeMillis();
    public long lastMiddleDownMS = System.currentTimeMillis();
    public long lastMiddleUpMS = System.currentTimeMillis();

    public void onMouseClick(int button, int action, int mods)
    {
        try {
            if(!((View3D)viewLogic).window.isFocused)
                currentPos = new Vector2d(Consts.NaNd, Consts.NaNd);
            viewLogic.onMouseClick(this, button, action, mods);
        } catch (Exception e) {e.printStackTrace();}

        switch (button)
        {
            case GLFW.GLFW_MOUSE_BUTTON_1:
                if(action == GLFW.GLFW_PRESS)
                    lastLeftDownMS = System.currentTimeMillis();
                else
                    lastLeftUpMS = System.currentTimeMillis();
                break;
            case GLFW.GLFW_MOUSE_BUTTON_2:
                if(action == GLFW.GLFW_PRESS)
                    lastRightDownMS = System.currentTimeMillis();
                else
                    lastRightUpMS = System.currentTimeMillis();
                break;
            case GLFW.GLFW_MOUSE_BUTTON_3:
                if(action == GLFW.GLFW_PRESS)
                    lastMiddleDownMS = System.currentTimeMillis();
                else
                    lastMiddleUpMS = System.currentTimeMillis();
                break;
        }
    }

    public void update(WindowMan windowMan)
    {
        Main.RunOnGLFWThread(() ->
        {
            displayVec.set(0, 0);

            double[] xBuffer = new double[1];
            double[] yBuffer = new double[1];
            GLFW.glfwGetCursorPos(windowMan.window, xBuffer, yBuffer);

            if(currentPos.x >= 0 && currentPos.y >= 0 && currentPos.x <= window.getWidth() && currentPos.y <= window.getHeight() && inWindow)
            {
                double x = xBuffer[0] - currentPos.x;
                double y = yBuffer[0] - currentPos.y;

                if(x != 0)
                    displayVec.y = (float) x;
                if(y != 0)
                    displayVec.x = (float) y;
            }

            currentPos.set(xBuffer[0], yBuffer[0]);
        });
    }

}
