package bog.lbpas.view3d.renderer.gui.font;

import bog.lbpas.view3d.managers.assetLoading.ObjectLoader;
import bog.lbpas.view3d.managers.RenderMan;
import bog.lbpas.view3d.renderer.gui.GuiRenderer;
import bog.lbpas.view3d.renderer.gui.ingredients.Quad;
import bog.lbpas.view3d.utils.Config;
import bog.lbpas.view3d.utils.Utils;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * @author Bog
 */
public class FontRenderer {

    public static ArrayList<FNT> Fonts;

    public static int headerFont = 0;
    public static int textFont = 0;

    static ObjectLoader loader;

    public static void init(ObjectLoader objectLoader)
    {
        Fonts = new ArrayList<>();
        loader = objectLoader;

        try {
            for(String f1 : Utils.getFilesInJar("fonts"))
            {
                String fontPath = f1.contains("!/") ? f1.substring(f1.lastIndexOf("!/") + 2) : f1;
                for(String f2 : Utils.getFilesInJar(fontPath))
                {
                    if(f2.endsWith(".fnt"))
                    {
                        String fntPath = f2.contains("!/") ? f2.substring(f2.lastIndexOf("!/") + 2) : f2;
                        FNT font = FNT.readFNT(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResourceAsStream(fntPath)));

                        try {
                            font.map = ImageIO.read(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResourceAsStream(fontPath + "/" + font.page.file)));
                            font.texture = loader.loadTexture(font.map, GL11.GL_NEAREST, GL11.GL_LINEAR);
                        } catch (Exception e) {e.printStackTrace();}

                        buildFont(font);
                    }

                }
            }
            ArrayList<String> f = Utils.getFilesInFileDir("fonts");
            if(f != null)
                for(String f1 : f)
                {
                    ArrayList<String> ff = Utils.getFilesInFileDir(f1);
                    if(ff != null)
                        for(String f2 : ff)
                        {
                            if(f2.endsWith(".fnt"))
                            {
                                File fnt = new File(f2);
                                FNT font = FNT.readFNT(Objects.requireNonNull(Files.newInputStream(Paths.get(f2))));

                                try {
                                    font.map = ImageIO.read(Objects.requireNonNull(Files.newInputStream(Paths.get(fnt.getParent() + "/" + font.page.file))));
                                    font.texture = loader.loadTexture(font.map, GL11.GL_NEAREST, GL11.GL_LINEAR);
                                } catch (Exception e) {e.printStackTrace();}

                                buildFont(font);
                            }
                        }
                }

        } catch (Exception e) {e.printStackTrace();}

        for(int i = 0; i < Fonts.size(); i++)
            if(Fonts.get(i).info.face.equals(Config.FONT_HEADER))
                headerFont = i;
            else if(Fonts.get(i).info.face.equals(Config.FONT_TEXT))
                textFont = i;
    }

    public static void drawString(RenderMan renderer, String string, int x, int y, int size, Color color, int begin, int end, FNT font)
    {
        float xPos = 0;

        for(int i = 0; i < string.toCharArray().length; i++)
        {
            char c = string.toCharArray()[i];
            FNT.character character = font.getChar(c);

            if(character == null || c == " ".toCharArray()[0])
            {
                xPos += 40f * ((float) size / 50f);
            }
            else
            {
                if(i >= begin && i <= end)
                {
                    Vector2f pos = new Vector2f(x + xPos + character.xoffset * ((float) size / 50f), y + (character.height) * ((float) size / 50f) + character.yoffset * ((float) size / 50f));
                    Vector2f scale = new Vector2f((float) character.width * ((float) size / 50f), ((float) -character.height) * ((float) size / 50f));
                    renderer.processGuiElement(new Quad(font.glyphs.get(character.id), font.texture.id, pos, scale, color).staticTexture().smoothstep());
                }
                xPos += character.xadvance * ((float) size / 50f);
            }
        }

    }

    public static float getStringWidth(String string, int size)
    {
        FNT font = Fonts.get(textFont);

        float xPos = 0;

        for(int i = 0; i < string.toCharArray().length; i++)
        {
            char c = string.toCharArray()[i];
            FNT.character character = font.getChar(c);

            if(character == null || c == " ".toCharArray()[0])
            {
                xPos += 40f * ((float) size / 50f);
            }
            else
            {
                xPos += character.xadvance * ((float) size / 50f);
            }
        }

        return xPos;
    }

    public static float getFontHeight(int size)
    {
        return Fonts.get(textFont).height * ((float) size / 50f);
    }

    public static void buildFont(FNT font)
    {
        font.glyphs = new HashMap<>();

        int maxHeight = 0;

        for(FNT.character character : font.characters)
        {
            float minX = (float)character.x / (float)font.common.scaleW;
            float minY = (float)character.y / (float)font.common.scaleH;
            float maxX = minX + (float)character.width / (float)font.common.scaleW;
            float maxY = minY + (float)character.height / (float)font.common.scaleH;

            if(maxHeight < character.height + character.yoffset)
                maxHeight = character.height + character.yoffset;

            font.glyphs.put(character.id, loader.loadModel(new int[]{GuiRenderer.defaultQuad.vbos[0], 8}, new float[]{minX, maxY, minX, minY, maxX, maxY, maxX, minY}));
        }

        font.height = maxHeight;

        Fonts.add(font);
    }
}
