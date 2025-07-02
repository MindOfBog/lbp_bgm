package bog.lbpas.view3d.core;

import bog.lbpas.view3d.managers.assetLoading.ObjectLoader;
import org.lwjgl.opengl.GL11;

import java.awt.image.BufferedImage;

/**
 * @author Bog
 */
public class Texture {

    public int id = -1;
    public BufferedImage image;
    public ObjectLoader loader;

    public Texture() {}

    public Texture(int id, ObjectLoader loader)
    {
        this.id = id;
        this.loader = loader;
    }

    public Texture(int id, ObjectLoader loader, BufferedImage image) {
        this.id = id;
        this.loader = loader;
        this.image = image;
    }

    public Texture(BufferedImage image) {
        this.image = image;
    }

    public void cleanup()
    {
        if(loader.textures.contains(id))
            loader.textures.remove((Object)id);
        GL11.glDeleteTextures(id);
        id = -1;
    }
}
