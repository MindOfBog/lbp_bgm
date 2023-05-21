package bog.bgmaker.view3d.managers;

import bog.bgmaker.Main;
import bog.bgmaker.view3d.ILogic;
import bog.bgmaker.view3d.mainWindow.View3D;
import bog.bgmaker.view3d.utils.Config;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

/**
 * @author Bog
 */
public class MouseInput {

    public Vector2d previousPos, currentPos;
    public Vector2f displayVec;

    public boolean inWindow = false, leftButtonPress = false, middleButtonPress = false, rightButtonPress = false;

    ILogic viewLogic;

    public MouseInput(ILogic viewLogic)
    {
        previousPos = new Vector2d(-1, -1);
        currentPos = new Vector2d(0, 0);
        displayVec = new Vector2f();
        this.viewLogic = viewLogic;
    }

    public void init()
    {
        GLFW.glfwSetCursorPosCallback(Main.window.window, (window, xpos, ypos) ->
        {
            currentPos.set(xpos, ypos);
            onMousePos(xpos, ypos);
        });

        GLFW.glfwSetCursorEnterCallback(Main.window.window, (window, entered) ->
        {
            inWindow = entered;
        });

        GLFW.glfwSetMouseButtonCallback(Main.window.window, (window, button, action, mods) ->
        {
            if(action == GLFW.GLFW_PRESS)
            {
                leftButtonPress = button == GLFW.GLFW_MOUSE_BUTTON_1 ? true : leftButtonPress;
                rightButtonPress = button == GLFW.GLFW_MOUSE_BUTTON_2 ? true : rightButtonPress;
                middleButtonPress = button == GLFW.GLFW_MOUSE_BUTTON_3 ? true : middleButtonPress;
            }
            else if(action == GLFW.GLFW_RELEASE)
            {
                leftButtonPress = button == GLFW.GLFW_MOUSE_BUTTON_1 ? false : leftButtonPress;
                rightButtonPress = button == GLFW.GLFW_MOUSE_BUTTON_2 ? false : rightButtonPress;
                middleButtonPress = button == GLFW.GLFW_MOUSE_BUTTON_3 ? false : middleButtonPress;
            }

            onMouseClick(button, action, mods);
        });
    }

    public void onMousePos(double xPos, double yPos)
    {
        displayVec.set(0, 0);

        if(previousPos.x >= 0 && previousPos.y >= 0 && previousPos.x <= Main.window.width && previousPos.y <= Main.window.height && inWindow)
        {
            double x = xPos - previousPos.x;
            double y = yPos - previousPos.y;

            if(x != 0)
                displayVec.y = (float) x;
            if(y != 0)
                displayVec.x = (float) y;
        }

        previousPos.set(xPos, yPos);
    }

    public void onMouseClick(int button, int action, int mods)
    {
        try {
            if(!((View3D)viewLogic).window.isFocused)
                currentPos = new Vector2d(Config.NaNd, Config.NaNd);
            viewLogic.onMouseClick(this, button, action, mods);
        } catch (Exception e) {e.printStackTrace();}
    }

}
