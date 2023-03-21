package bog.bgmaker.view3d;

import bog.bgmaker.view3d.managers.MouseInput;
import bog.bgmaker.view3d.utils.MousePicker;

/**
 * @author Bog
 */
public interface ILogic {

    void init() throws Exception;
    void update(MouseInput mouseInput, MousePicker mousePicker);
    void render();
    void cleanup();
    void onMouseClick(MouseInput mouseInput, int button, int action, int mods) throws Exception;
    void onKey(int key, int scancode, int action, int mods);
    void onChar(int codePoint, int modifiers);
    void onMouseScroll(double xOffset, double yOffset);

}
