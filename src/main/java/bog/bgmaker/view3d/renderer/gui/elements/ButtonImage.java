package bog.bgmaker.view3d.renderer.gui.elements;

import bog.bgmaker.Main;
import bog.bgmaker.view3d.ObjectLoader;
import bog.bgmaker.view3d.managers.MouseInput;
import bog.bgmaker.view3d.managers.RenderMan;
import bog.bgmaker.view3d.managers.WindowMan;
import bog.bgmaker.view3d.utils.Const;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.*;

/**
 * @author Bog
 */
public abstract class ButtonImage extends Element{

    public int buttonImage;
    public boolean isClicked = false;

    public ButtonImage(String id, String imagePath, Vector2f pos, Vector2f size, RenderMan renderer, ObjectLoader loader, WindowMan window)
    {
        this.id = id;
        this.pos = pos;
        this.size = size;
        this.renderer = renderer;
        this.loader = loader;
        this.window = window;
        try {
            this.buttonImage = loader.loadTexture(ImageIO.read(Main.class.getResourceAsStream(imagePath)), GL11.GL_LINEAR_MIPMAP_LINEAR, GL11.GL_LINEAR);
        } catch (Exception e) {e.printStackTrace();}
    }

    public ButtonImage clicked()
    {
        this.isClicked = true;
        return this;
    }

    @Override
    public void draw(MouseInput mouseInput, boolean overOther) {
        super.draw(mouseInput, overOther);

        if(!isMouseOverElement(mouseInput) || overOther)
            setClicked(false);

        Color c = Const.INTERFACE_PRIMARY_COLOR;

        if(isMouseOverElement(mouseInput) && !overOther)
            c = Const.INTERFACE_SECONDARY_COLOR;

        if(isClicked)
            c = Const.INTERFACE_TERTIARY_COLOR;

        drawRect((int)pos.x, (int)pos.y, (int)size.x, (int)size.y, c);
        drawImageStatic(buttonImage, (int)pos.x, (int)pos.y, (int)size.x, (int)size.y);
        drawRectOutline((int)pos.x, (int)pos.y, (int)size.x, (int)size.y, c, false);
    }

    public void setClicked(boolean clicked) {
        isClicked = clicked;
    }

    @Override
    public void onClick(Vector2d pos, int button, int action, int mods, boolean overOther) {

        if(!overOther)
        {
            if(isMouseOverElement(pos))
            {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)
                    isClicked = true;
                clickedButton(button, action, mods);
            }
            if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_RELEASE)
                isClicked = false;
        }

        super.onClick(pos, button, action, mods, overOther);
    }

    public abstract void clickedButton(int button, int action, int mods);
}
