package bog.lbpas.view3d.managers;

import bog.lbpas.view3d.renderer.ILogic;
import bog.lbpas.view3d.mainWindow.View3D;
import bog.lbpas.view3d.utils.Consts;
import bog.lbpas.view3d.utils.MousePicker;
import common.FileChooser;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

/**
 * @author Bog
 */
public class MouseInput {

    public Vector2d previousPos, currentPos;
    public Vector2f displayVec;

    public boolean inWindow = false,
            leftButtonPress = false, middleButtonPress = false, rightButtonPress = false,
            mouse4Press = false, mouse5Press = false, mouse6Press = false, mouse7Press = false, mouse8Press = false;

    ILogic viewLogic;

    private WindowMan window;

    public MousePicker mousePicker;

    public MouseInput(ILogic viewLogic)
    {
        previousPos = new Vector2d(-1, -1);
        currentPos = new Vector2d(0, 0);
        displayVec = new Vector2f();
        this.viewLogic = viewLogic;
    }

    public void init(WindowMan windowMan)
    {
        this.window = windowMan;

        GLFW.glfwSetCursorPosCallback(windowMan.window, (window, xpos, ypos) ->
        {
            if(FileChooser.isLegacyFDopen)
                return;

            currentPos.set(xpos, ypos);
            onMousePos(windowMan, xpos, ypos);
        });

        GLFW.glfwSetCursorEnterCallback(windowMan.window, (window, entered) ->
        {
            inWindow = entered;
        });

        GLFW.glfwSetMouseButtonCallback(windowMan.window, (window, button, action, mods) ->
        {
            if(FileChooser.isLegacyFDopen)
                return;
            if(action == GLFW.GLFW_PRESS)
            {
                leftButtonPress = button == GLFW.GLFW_MOUSE_BUTTON_1 ? true : leftButtonPress;
                rightButtonPress = button == GLFW.GLFW_MOUSE_BUTTON_2 ? true : rightButtonPress;
                middleButtonPress = button == GLFW.GLFW_MOUSE_BUTTON_3 ? true : middleButtonPress;
                mouse4Press = button == GLFW.GLFW_MOUSE_BUTTON_4 ? true : mouse4Press;
                mouse5Press = button == GLFW.GLFW_MOUSE_BUTTON_5 ? true : mouse5Press;
                mouse6Press = button == GLFW.GLFW_MOUSE_BUTTON_6 ? true : mouse6Press;
                mouse7Press = button == GLFW.GLFW_MOUSE_BUTTON_7 ? true : mouse7Press;
                mouse8Press = button == GLFW.GLFW_MOUSE_BUTTON_8 ? true : mouse8Press;
            }
            else if(action == GLFW.GLFW_RELEASE)
            {
                leftButtonPress = button == GLFW.GLFW_MOUSE_BUTTON_1 ? false : leftButtonPress;
                rightButtonPress = button == GLFW.GLFW_MOUSE_BUTTON_2 ? false : rightButtonPress;
                middleButtonPress = button == GLFW.GLFW_MOUSE_BUTTON_3 ? false : middleButtonPress;
                mouse4Press = button == GLFW.GLFW_MOUSE_BUTTON_4 ? false : mouse4Press;
                mouse5Press = button == GLFW.GLFW_MOUSE_BUTTON_5 ? false : mouse5Press;
                mouse6Press = button == GLFW.GLFW_MOUSE_BUTTON_6 ? false : mouse6Press;
                mouse7Press = button == GLFW.GLFW_MOUSE_BUTTON_7 ? false : mouse7Press;
                mouse8Press = button == GLFW.GLFW_MOUSE_BUTTON_8 ? false : mouse8Press;
            }

            onMouseClick(button, action, mods);
        });
        mousePicker = new MousePicker(this, windowMan);
    }

    public void setMousePos(double x, double y)
    {
        GLFW.glfwSetCursorPos(window.window, x, y);
    }

    public void onMousePos(WindowMan window, double xPos, double yPos)
    {
        window.onMousePos(xPos, yPos);

        displayVec.set(0, 0);

        if(previousPos.x >= 0 && previousPos.y >= 0 && previousPos.x <= window.width && previousPos.y <= window.height && inWindow)
        {
            double x = xPos - previousPos.x;
            double y = yPos - previousPos.y;

            if(x != 0)
                displayVec.y = (float) x;
            if(y != 0)
                displayVec.x = (float) y;
        }

        previousPos.set(xPos, yPos);

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

}
