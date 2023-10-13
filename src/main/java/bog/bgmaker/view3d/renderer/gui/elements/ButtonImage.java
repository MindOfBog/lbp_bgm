package bog.bgmaker.view3d.renderer.gui.elements;

import bog.bgmaker.Main;
import bog.bgmaker.view3d.ObjectLoader;
import bog.bgmaker.view3d.core.Model;
import bog.bgmaker.view3d.managers.MouseInput;
import bog.bgmaker.view3d.managers.RenderMan;
import bog.bgmaker.view3d.managers.WindowMan;
import bog.bgmaker.view3d.renderer.gui.ingredients.LineStrip;
import bog.bgmaker.view3d.utils.Config;
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

    Vector2f prevSize;
    Model outlineRect;

    public ButtonImage(String id, String imagePath, Vector2f pos, Vector2f size, RenderMan renderer, ObjectLoader loader, WindowMan window)
    {
        this.id = id;
        this.pos = pos;
        this.size = size;
        this.prevSize = size;
        this.outlineRect = LineStrip.processVerts(LineStrip.getRectangle(size), loader, window);
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

        if(size.x != prevSize.x || size.y != prevSize.y)
        {
            refreshOutline();
            prevSize = size;
        }

        if(!isMouseOverElement(mouseInput) || overOther)
            setClicked(false);

        Color c = Config.INTERFACE_PRIMARY_COLOR;
        Color c2 = Config.INTERFACE_PRIMARY_COLOR2;

        if(isMouseOverElement(mouseInput) && !overOther)
        {
            c = Config.INTERFACE_SECONDARY_COLOR;
            c2 = Config.INTERFACE_SECONDARY_COLOR2;
        }

        if(isClicked)
        {
            c = Config.INTERFACE_TERTIARY_COLOR;
            c2 = Config.INTERFACE_TERTIARY_COLOR2;
        }

        renderer.drawRect((int)pos.x, (int)pos.y, (int)size.x, (int)size.y, c);
        renderer.drawImageStatic(buttonImage, (int)pos.x, (int)pos.y, (int)size.x, (int)size.y);
        renderer.drawRectOutline(pos, outlineRect, c2, false);
    }
    @Override
    public void resize() {
        super.resize();
        refreshOutline();
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
                    setClicked(true);
                clickedButton(button, action, mods);
            }
            if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_RELEASE)
                setClicked(false);
        }

        super.onClick(pos, button, action, mods, overOther);
    }

    public abstract void clickedButton(int button, int action, int mods);

    public void refreshOutline()
    {
        if(this.outlineRect != null)
            this.outlineRect.cleanup();
        this.outlineRect = LineStrip.processVerts(LineStrip.getRectangle(size), loader, window);
    }
}
