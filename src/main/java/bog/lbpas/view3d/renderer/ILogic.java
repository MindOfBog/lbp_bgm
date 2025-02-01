package bog.lbpas.view3d.renderer;

import bog.lbpas.view3d.managers.MouseInput;

/**
 * @author Bog
 */
public interface ILogic {

    void init() throws Exception;
    void update(MouseInput mouseInput);
    void render();
    void cleanup();
    void onMouseClick(MouseInput mouseInput, int button, int action, int mods) throws Exception;
    void onMouseMove(MouseInput mouseInput, double x, double y);
    void onKey(int key, int scancode, int action, int mods);
    void onChar(int codePoint, int modifiers);
    void onMouseScroll(double xOffset, double yOffset);

}
