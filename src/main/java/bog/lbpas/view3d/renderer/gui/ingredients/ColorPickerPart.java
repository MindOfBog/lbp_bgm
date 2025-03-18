package bog.lbpas.view3d.renderer.gui.ingredients;

import bog.lbpas.view3d.managers.WindowMan;
import org.joml.Vector2i;
import org.joml.Vector4f;

/**
 * @author Bog
 */
public class ColorPickerPart extends Drawable{

    public Vector2i pos;
    public Vector2i size;

    public int part;

    public Vector4f color;

    public static ColorPickerPart hueRamp(int x, int y, int width, int height, WindowMan window)
    {
        ColorPickerPart part = new ColorPickerPart();
        part.pos = new Vector2i(x, window.height - y);
        part.size =  new Vector2i(width, height);
        part.part = 0;
        return part;
    }

    public static ColorPickerPart saturationLuminancePicker(int x, int y, int width, int height, Vector4f color, WindowMan window)
    {
        ColorPickerPart part = new ColorPickerPart();
        part.pos = new Vector2i(x, window.height - y);
        part.size =  new Vector2i(width, height);
        part.part = 1;
        part.color = color;
        return part;
    }

    public static ColorPickerPart transparencyCheckerBoard(int x, int y, int width, int height, WindowMan window)
    {
        ColorPickerPart part = new ColorPickerPart();
        part.pos = new Vector2i(x, window.height - y);
        part.size =  new Vector2i(width, height);
        part.part = 2;
        return part;
    }

    @Override
    public Type getType() {
        return Type.COLOR_PICKER;
    }
}
