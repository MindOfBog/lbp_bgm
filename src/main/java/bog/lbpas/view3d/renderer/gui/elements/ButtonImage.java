package bog.lbpas.view3d.renderer.gui.elements;

import bog.lbpas.Main;
import bog.lbpas.view3d.managers.assetLoading.ObjectLoader;
import bog.lbpas.view3d.core.Model;
import bog.lbpas.view3d.managers.MouseInput;
import bog.lbpas.view3d.managers.RenderMan;
import bog.lbpas.view3d.managers.WindowMan;
import bog.lbpas.view3d.renderer.gui.cursor.ECursor;
import bog.lbpas.view3d.renderer.gui.ingredients.LineStrip;
import bog.lbpas.view3d.utils.Config;
import bog.lbpas.view3d.utils.Cursors;
import bog.lbpas.view3d.utils.Utils;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author Bog
 */
public abstract class ButtonImage extends Element{

    public int buttonImage = -1;
    public boolean isClicked = false;

    Vector2f prevSize;
    Model outlineRect;

    public Vector2f imageSize;

    public ButtonImage(String id, Vector2f pos, Vector2f size, RenderMan renderer, ObjectLoader loader, WindowMan window)
    {
        this.id = id;
        this.pos = pos;
        this.size = size;
        this.imageSize = this.size;
        this.prevSize = size;
        this.renderer = renderer;
        this.loader = loader;
        this.window = window;
    }

    public ButtonImage(String id, Vector2f pos, Vector2f size, Vector2f imageSize, RenderMan renderer, ObjectLoader loader, WindowMan window)
    {
        this.id = id;
        this.pos = pos;
        this.size = size;
        this.imageSize = imageSize;
        this.prevSize = size;
        this.renderer = renderer;
        this.loader = loader;
        this.window = window;
    }

    public ButtonImage clicked()
    {
        this.isClicked = true;
        return this;
    }

    @Override
    public void draw(MouseInput mouseInput, boolean overOther) {
        super.draw(mouseInput, overOther);

        if(this.buttonImage == -1)
            getImage();

        if(this.outlineRect == null)
            this.outlineRect = LineStrip.processVerts(LineStrip.getRectangle(size), loader, window);

        if(size.x != prevSize.x || size.y != prevSize.y)
        {
            refreshOutline();
            prevSize = size;
        }

        if(!isMouseOverElement(mouseInput) || overOther)
            setClicked(false);

        Color[] colors = getColors(mouseInput, overOther);

        renderer.drawRect(Math.round(pos.x), Math.round(pos.y), Math.round(size.x), Math.round(size.y), colors[0]);
        if(this.buttonImage != -1)
            renderer.drawImageStatic(buttonImage, Math.round(pos.x + (size.x / 2f) - (this.imageSize.x / 2f)), Math.round(pos.y + (size.y / 2f) - (this.imageSize.y / 2f)), Math.round(this.imageSize.x), Math.round(this.imageSize.y));
        renderer.drawRectOutline(pos, outlineRect, colors[1], false);
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
    public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overOther, boolean focusedOther) {

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

        super.onClick(mouseInput, pos, button, action, mods, overOther, focusedOther);
    }

    public abstract void clickedButton(int button, int action, int mods);

    public void refreshOutline()
    {
        if(this.outlineRect != null)
            this.outlineRect.cleanup(loader);
        this.outlineRect = LineStrip.processVerts(LineStrip.getRectangle(size), loader, window);
    }

    @Override
    public void hoverCursor() {
        Cursors.setCursor(ECursor.hand2);
    }

    public Color[] getColors(MouseInput mouseInput, boolean overOther)
    {
        if(isClicked)
            return new Color[]{Config.INTERFACE_TERTIARY_COLOR, Config.INTERFACE_TERTIARY_COLOR2};
        else if(isMouseOverElement(mouseInput) && !overOther)
            return new Color[]{Config.INTERFACE_SECONDARY_COLOR, Config.INTERFACE_SECONDARY_COLOR2};
        else
            return new Color[]{Config.INTERFACE_PRIMARY_COLOR, Config.INTERFACE_PRIMARY_COLOR2};
    }

    public abstract void getImage();

    public void updateImage()
    {
        buttonImage = -1;
    }
}
