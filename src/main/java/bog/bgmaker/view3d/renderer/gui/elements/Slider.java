package bog.bgmaker.view3d.renderer.gui.elements;

import bog.bgmaker.view3d.ObjectLoader;
import bog.bgmaker.view3d.managers.MouseInput;
import bog.bgmaker.view3d.managers.RenderMan;
import bog.bgmaker.view3d.managers.WindowMan;
import org.joml.Math;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

/**
 * @author Bog
 */
public class Slider extends Element{

    float sliderPosition = 0;
    public boolean isSliding = false;
    float min = 0;
    float max = 100;
    Color track;
    Color slider;

    public Slider(String id, Vector2f pos, Vector2f size, RenderMan renderer, ObjectLoader loader, WindowMan window)
    {
        this.id = id;
        this.pos = pos;
        this.size = size;
        this.renderer = renderer;
        this.loader = loader;
        this.window = window;
        this.track = new Color(0f, 0f, 0f, 0.5f);
        this.slider = new Color(1f, 1f, 1f, 1f);
    }

    public Slider(String id, Vector2f pos, Vector2f size, RenderMan renderer, ObjectLoader loader, WindowMan window, float sliderPosition, float min, float max)
    {
        this.id = id;
        this.pos = pos;
        this.size = size;
        this.renderer = renderer;
        this.loader = loader;
        this.window = window;
        this.sliderPosition = ((sliderPosition - min)/(max - min)) * 100f;
        this.min = min;
        this.max = max;
        this.track = new Color(0f, 0f, 0f, 0.5f);
        this.slider = new Color(1f, 1f, 1f, 1f);
    }

    public Slider(String id, Vector2f pos, Vector2f size, RenderMan renderer, ObjectLoader loader, WindowMan window, Color track, Color slider)
    {
        this.id = id;
        this.pos = pos;
        this.size = size;
        this.renderer = renderer;
        this.loader = loader;
        this.window = window;
        this.track = track;
        this.slider = slider;
    }

    public Slider(String id, Vector2f pos, Vector2f size, RenderMan renderer, ObjectLoader loader, WindowMan window, float sliderPosition, float min, float max, Color track, Color slider)
    {
        this.id = id;
        this.pos = pos;
        this.size = size;
        this.renderer = renderer;
        this.loader = loader;
        this.window = window;
        this.sliderPosition = ((sliderPosition - min)/(max - min)) * 100f;
        this.min = min;
        this.max = max;
        this.track = track;
        this.slider = slider;
    }

    @Override
    public void draw(MouseInput mouseInput, boolean overElement) {
        super.draw(mouseInput, overElement);

        if(isSliding)
        {
            sliderPosition = ((((float)mouseInput.currentPos.x - (size.y * 0.2f) / 2f) - pos.x)/(size.x - size.y * 0.2f)) * 100f;
            sliderPosition = Math.clamp(0, 100, sliderPosition);
        }

        drawRect((int) pos.x, (int) (pos.y + size.y/2f - size.y * 0.1f), (int) size.x, (int) (size.y * 0.2f), track);
        drawRect((int) (pos.x + (sliderPosition * ((size.x - size.y * 0.1f)/100))), (int) (pos.y), (int) (size.y * 0.2f), (int) size.y, slider);

    }

    @Override
    public void onClick(Vector2d pos, int button, int action, int mods, boolean overElement) {
        super.onClick(pos, button, action, mods, overElement);

        if(button == GLFW.GLFW_MOUSE_BUTTON_1)
        {
            if(action == GLFW.GLFW_PRESS && isMouseOverElement(pos))
                isSliding = true;
            else if(action == GLFW.GLFW_RELEASE)
                isSliding = false;
        }
    }

    public float getCurrentValue()
    {
        return ((max - min) * sliderPosition / 100f) + min;
    }

    public void setCurrentValue(float sliderPosition)
    {
        this.sliderPosition = ((sliderPosition - min)/(max - min)) * 100f;
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if(!focused)
            isSliding = false;
    }
}
