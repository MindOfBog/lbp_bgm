package bog.lbpas.view3d.renderer;

import bog.lbpas.view3d.mainWindow.View3D;
import bog.lbpas.view3d.managers.MouseInput;

/**
 * @author Bog
 */
public interface IRenderer<T> {

    void init() throws Exception;

    void render(MouseInput mouseInput, View3D mainView);

    void cleanup();

}
