package bog.bgmaker.view3d.renderer;

import bog.bgmaker.view3d.Camera;
import bog.bgmaker.view3d.core.Model;
import bog.bgmaker.view3d.managers.MouseInput;
import bog.bgmaker.view3d.managers.ShaderMan;

/**
 * @author Bog
 */
public interface IRenderer<T> {

    public void init() throws Exception;

    public void render(Camera camera, MouseInput mouseInput);

    public void bind(Model model, boolean hasBones, ShaderMan shader);

    public void unbind();

    public void prepare(T t, Camera camera, ShaderMan shader, Model model);

    public void cleanup();

}
