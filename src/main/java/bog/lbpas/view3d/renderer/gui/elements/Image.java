package bog.lbpas.view3d.renderer.gui.elements;

import bog.lbpas.view3d.core.Texture;
import bog.lbpas.view3d.managers.MouseInput;
import bog.lbpas.view3d.managers.RenderMan;
import bog.lbpas.view3d.managers.WindowMan;
import bog.lbpas.view3d.managers.assetLoading.ObjectLoader;
import bog.lbpas.view3d.utils.Utils;
import bog.lbpas.view3d.utils.print;
import org.joml.Math;
import org.joml.Vector2f;
import org.joml.Vector2i;

/**
 * @author Bog
 */
public abstract class Image extends Element{

    boolean stretch = false;

    public Image(Vector2f pos, Vector2f size, RenderMan renderer, ObjectLoader loader, WindowMan window)
    {
        this.pos = pos;
        this.size = size;
        this.renderer = renderer;
        this.loader = loader;
        this.window = window;
    }

    public Image(boolean stretch, Vector2f pos, Vector2f size, RenderMan renderer, ObjectLoader loader, WindowMan window)
    {
        this.stretch = stretch;
        this.pos = pos;
        this.size = size;
        this.renderer = renderer;
        this.loader = loader;
        this.window = window;
    }

    @Override
    public void draw(MouseInput mouseInput, boolean overElement) {
        super.draw(mouseInput, overElement);

        if(stretch)
        {
            renderer.drawTransparencyCheckerBoard(new Vector2f(Math.round(pos.x), Math.round(pos.y)), new Vector2f(Math.round(size.x), Math.round(size.y)));
            renderer.drawImageStatic(getImage(), Math.round(pos.x), Math.round(pos.y), Math.round(size.x), Math.round(size.y), loader);
        }
        else
        {
            Texture image = getImage();
            int width = image.image.getWidth();
            int height = image.image.getHeight();

            float targetWidth = size.x;
            float targetHeight = size.y;

            float imageAspectRatio = (float) width / (float) height;
            float targetAspectRatio = targetWidth / targetHeight;

            float scale;

            if (imageAspectRatio > targetAspectRatio)
                scale = targetWidth / width;
            else
                scale = targetHeight / height;

            float newWidth = width * scale;
            float newHeight = height * scale;

            Vector2f newSize = new Vector2f(newWidth, newHeight);

            float offsetX = (targetWidth - newWidth) / 2;
            float offsetY = (targetHeight - newHeight) / 2;

            renderer.drawTransparencyCheckerBoard(new Vector2f(Math.round(pos.x + offsetX), Math.round(pos.y + offsetY)), new Vector2f(newSize));
            renderer.drawImageStatic(getImage(), Math.round(pos.x + offsetX), Math.round(pos.y + offsetY), newSize.x, newSize.y, loader);
        }
    }

    public abstract Texture getImage();
}
