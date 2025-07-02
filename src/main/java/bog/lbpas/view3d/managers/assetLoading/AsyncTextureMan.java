package bog.lbpas.view3d.managers.assetLoading;

import bog.lbpas.view3d.core.Texture;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

public class AsyncTextureMan {

    private  ArrayList<DigestedImageData> toLoad;
    private ArrayList<BufferedImageData> toDigestBI;
    private ArrayList<BufferedImageData2> toDigestBI2;
    private ArrayList<FilepathImageData> toDigestFPI;
    private ObjectLoader loader;

    public int totalDigestionCount = 0;

    public AsyncTextureMan(ObjectLoader loader) {
        this.toDigestBI = new ArrayList<>();
        this.toDigestBI2 = new ArrayList<>();
        this.toDigestFPI = new ArrayList<>();
        this.toLoad = new ArrayList<>();
        this.loader = loader;
    }

    public int digestionCount()
    {
        return toDigestBI2.size() + toDigestFPI.size();
    }

    public int loadingCount()
    {
        return toLoad.size();
    }

    public boolean isLoadingSomething()
    {
        return digestionCount() != 0 || loadingCount() != 0;
    }

    public void loadDigestedImages()
    {//todo free stbi image
        for(int i = toLoad.size() - 1; i >= 0; i--)
        {
            if(toLoad.get(i).texture == null)
            {
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, toLoad.get(i).id);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, toLoad.get(i).minFilter);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, toLoad.get(i).magFilter);
                GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
                GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, toLoad.get(i).width, toLoad.get(i).height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, toLoad.get(i).image);
                GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
                toLoad.remove(i);
            }
            else
            {
                int id = GL11.glGenTextures();
                loader.textures.add(id);
                toLoad.get(i).texture.id = id;
                toLoad.get(i).texture.loader = loader;
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, toLoad.get(i).minFilter);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, toLoad.get(i).magFilter);
                GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
                GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, toLoad.get(i).width, toLoad.get(i).height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, toLoad.get(i).image);
                GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
                toLoad.remove(i);
            }
        }
    }

    public void digestImages()
    {
        for(int i = toDigestBI.size() - 1; i >= 0 ; i--)
        {
            try
            {
                ByteBuffer buffer = ObjectLoader.loadTextureBuffer(toDigestBI.get(i).image);
                toLoad.add(new DigestedImageData(
                        buffer,
                        toDigestBI.get(i).minFilter,
                        toDigestBI.get(i).magFilter,
                        toDigestBI.get(i).id,
                        toDigestBI.get(i).image.getWidth(),
                        toDigestBI.get(i).image.getHeight()));
                toDigestBI.remove(i);
                totalDigestionCount++;
            }catch (Exception e){e.printStackTrace();}
        }

        for(int i = toDigestFPI.size() - 1; i >= 0 ; i--)
        {
            int width, height;
            ByteBuffer buffer;

            try(MemoryStack stack = MemoryStack.stackPush())
            {
                IntBuffer w = stack.mallocInt(1);
                IntBuffer h = stack.mallocInt(1);
                IntBuffer c = stack.mallocInt(1);

                buffer = STBImage.stbi_load(toDigestFPI.get(i).path, w, h, c, 4);

                if(buffer == null)
                {
                    System.err.println("Image file " + toDigestFPI.get(i).path + " not loaded.\n" + STBImage.stbi_failure_reason());
                    toDigestFPI.remove(i);
                    continue;
                }

                width = w.get();
                height = h.get();

                toLoad.add(new DigestedImageData(
                        buffer,
                        toDigestFPI.get(i).minFilter,
                        toDigestFPI.get(i).magFilter,
                        toDigestFPI.get(i).id,
                        width,
                        height));
                toDigestFPI.remove(i);
            }
        }

        for(int i = toDigestBI2.size() - 1; i >= 0 ; i--)
        {
            try
            {
                if(toDigestBI2.get(i).image == null)
                {
                    toDigestBI2.remove(i);
                    totalDigestionCount++;
                    continue;
                }

                ByteBuffer buffer = ObjectLoader.loadTextureBuffer(toDigestBI2.get(i).image);
                toLoad.add(new DigestedImageData(
                        buffer,
                        toDigestBI2.get(i).minFilter,
                        toDigestBI2.get(i).magFilter,
                        toDigestBI2.get(i).texture,
                        toDigestBI2.get(i).image.getWidth(),
                        toDigestBI2.get(i).image.getHeight()));

                while(toDigestBI2.get(i).texture.id == -1)
                {
                    Thread.sleep(100);
                }

                toDigestBI2.remove(i);
                totalDigestionCount++;
            }catch (Exception e){e.printStackTrace();}
        }
    }

    public void digest(BufferedImageData image)
    {
        toDigestBI.add(image);
    }
    public void digest(FilepathImageData image)
    {
        toDigestFPI.add(image);
    }
    public void digest(BufferedImageData2 image)
    {
        toDigestBI2.add(image);
        digestImages();
    }

    private static class DigestedImageData
    {
        ByteBuffer image;
        public int minFilter, magFilter, id, width, height;
        Texture texture;

        public DigestedImageData(ByteBuffer image, int minFilter, int magFilter, int id, int width, int height) {
            this.image = image;
            this.minFilter = minFilter;
            this.magFilter = magFilter;
            this.id = id;
            this.width = width;
            this.height = height;
        }

        public DigestedImageData(ByteBuffer image, int minFilter, int magFilter, Texture texture, int width, int height) {
            this.image = image;
            this.minFilter = minFilter;
            this.magFilter = magFilter;
            this.texture = texture;
            this.width = width;
            this.height = height;
        }
    }
    public static class BufferedImageData
    {
        public BufferedImage image;
        public int minFilter, magFilter, id;

        public BufferedImageData(BufferedImage image, int minFilter, int magFilter, int id) {
            this.image = image;
            this.minFilter = minFilter;
            this.magFilter = magFilter;
            this.id = id;
        }
    }
    public static class FilepathImageData
    {
        public String path;
        public int minFilter, magFilter, id;

        public FilepathImageData(String path, int minFilter, int magFilter, int id) {
            this.path = path;
            this.minFilter = minFilter;
            this.magFilter = magFilter;
            this.id = id;
        }
    }
    public static class BufferedImageData2
    {
        BufferedImage image;
        Texture texture;
        public int minFilter, magFilter;

        public BufferedImageData2(BufferedImage image, Texture texture, int minFilter, int magFilter) {
            this.image = image;
            this.texture = texture;
            this.minFilter = minFilter;
            this.magFilter = magFilter;
        }
    }
}
