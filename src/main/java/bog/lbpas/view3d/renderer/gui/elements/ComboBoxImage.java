package bog.lbpas.view3d.renderer.gui.elements;

import bog.lbpas.view3d.core.Texture;
import bog.lbpas.view3d.managers.RenderMan;
import bog.lbpas.view3d.managers.WindowMan;
import bog.lbpas.view3d.managers.assetLoading.ObjectLoader;
import org.joml.Vector2f;

public abstract class ComboBoxImage extends ComboBox{

    Vector2f imageSize;

    public ComboBoxImage() {
    }

    public ComboBoxImage(String id, Vector2f pos, Vector2f size, RenderMan renderer, ObjectLoader loader, WindowMan window) {
        super(id, "", pos, size, renderer, loader, window);
        imageSize = size;
    }

    public ComboBoxImage(String id, RenderMan renderer, ObjectLoader loader, WindowMan window) {
        super(id, "", renderer, loader, window);
        imageSize = size;
    }

    public ComboBoxImage(String id, Vector2f pos, Vector2f size, RenderMan renderer, ObjectLoader loader, WindowMan window, boolean autoCollapse) {
        super(id, "", pos, size, renderer, loader, window, autoCollapse);
        imageSize = size;
    }

    public ComboBoxImage(String id, Vector2f imageSize, RenderMan renderer, ObjectLoader loader, WindowMan window) {
        super(id, "", renderer, loader, window);
        this.imageSize = imageSize;
    }

    @Override
    public void drawComboTab(float fontHeight) {
        renderer.drawImageStatic(getImage(), pos.x + (size.x - imageSize.x) / 2f, pos.y + (size.y - imageSize.y) / 2f, imageSize.x, imageSize.y, loader);
    }

    public abstract Texture getImage();
}
