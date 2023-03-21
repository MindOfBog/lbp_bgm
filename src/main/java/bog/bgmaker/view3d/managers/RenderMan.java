package bog.bgmaker.view3d.managers;

import bog.bgmaker.view3d.Camera;
import bog.bgmaker.view3d.ObjectLoader;
import bog.bgmaker.view3d.core.DirectionalLight;
import bog.bgmaker.view3d.core.PointLight;
import bog.bgmaker.view3d.core.SpotLight;
import bog.bgmaker.view3d.core.types.Entity;
import bog.bgmaker.view3d.renderer.EntityRenderer;
import bog.bgmaker.view3d.renderer.gui.GuiRenderer;
import bog.bgmaker.view3d.renderer.gui.ingredients.Drawable;
import org.lwjgl.opengl.GL11;

/**
 * @author Bog
 */
public class RenderMan {

    public WindowMan window;
    public EntityRenderer entityRenderer;
    public GuiRenderer guiRenderer;
    private static boolean isCulling = false;

    public RenderMan(WindowMan window)
    {
        this.window = window;
    }

    public void init(ObjectLoader loader) throws Exception
    {
        entityRenderer = new EntityRenderer(loader, this.window);
        entityRenderer.init();
        guiRenderer = new GuiRenderer(loader, this.window);
        guiRenderer.init();
    }

    public void render(Camera camera, MouseInput mouseInput)
    {
        clear();
        entityRenderer.render(camera, mouseInput);
        guiRenderer.render();
    }

    public static void enableCulling()
    {
        if(!isCulling)
        {
            GL11.glEnable(GL11.GL_CULL_FACE);
            isCulling = true;
        }
    }

    public static void disableCulling()
    {
        if(isCulling)
        {
            GL11.glDisable(GL11.GL_CULL_FACE);
            isCulling = false;
        }
    }

    public void processEntity(Entity entity) {
        this.entityRenderer.entities.add(entity);
    }

    public void processDirectionalLight(DirectionalLight directionalLight)
    {
        this.entityRenderer.directionalLights.add(directionalLight);
    }

    public void processPointLight(PointLight pointLight)
    {
        this.entityRenderer.pointLights.add(pointLight);
    }

    public void processSpotLight(SpotLight spotLight)
    {
        this.entityRenderer.spotLights.add(spotLight);
    }

    public void processThroughWallEntity(Entity entity) {
        this.entityRenderer.throughWallEntities.add(entity);
    }

    public void processGuiElement(Drawable element)
    {
        this.guiRenderer.elements.add(element);
    }

    public void clear()
    {
        GL11.glClearStencil(0);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
    }

    public void cleanup()
    {
        entityRenderer.cleanup();
        guiRenderer.cleanup();
    }

}
