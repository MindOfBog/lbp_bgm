package bog.lbpas.view3d.managers;

import bog.lbpas.view3d.utils.Utils;

/**
 * @author Bog
 */
public class InputMan {

    public int key;
    public boolean mouse;

    public InputMan(int key, boolean mouse) {
        this.key = key;
        this.mouse = mouse;
    }

    public String inputName()
    {
        if(mouse)
            return Utils.getMouseButtonName(key);
        else
            return Utils.getKeyName(key);
    }

    public boolean isKey(int key)
    {
        return (key == this.key && !mouse);
    }

    private boolean isKeyPressed(WindowMan window)
    {
        return (window.isKeyPressed(key) && !mouse);
    }

    public boolean isButton(int button)
    {
        return (button == this.key && mouse);
    }

    private boolean isButtonPressed(MouseInput mouse)
    {
        switch (key)
        {
            case 0:
                return (mouse.leftButtonPress && this.mouse);
            case 1:
                return (mouse.rightButtonPress && this.mouse);
            case 2:
                return (mouse.middleButtonPress && this.mouse);
            case 3:
                return (mouse.mouse4Press && this.mouse);
            case 4:
                return (mouse.mouse5Press && this.mouse);
            case 5:
                return (mouse.mouse6Press && this.mouse);
            case 6:
                return (mouse.mouse7Press && this.mouse);
            case 7:
                return (mouse.mouse8Press && this.mouse);
        }
        return false;
    }

    public boolean isPressed(WindowMan window, MouseInput mouse)
    {
        if(this.mouse)
            return isButtonPressed(mouse);
        else
            return isKeyPressed(window);
    }
}
