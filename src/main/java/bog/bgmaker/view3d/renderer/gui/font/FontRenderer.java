package bog.bgmaker.view3d.renderer.gui.font;

import bog.bgmaker.view3d.ObjectLoader;
import bog.bgmaker.view3d.managers.RenderMan;
import bog.bgmaker.view3d.renderer.gui.ingredients.Quad;
import bog.bgmaker.view3d.utils.Utils;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Bog
 */
public class FontRenderer {

    public static ArrayList<FNT> Fonts;
    public static int currentFont = 0;
    static ObjectLoader loader;

    public static void init(ObjectLoader objectLoader)
    {
        Fonts = new ArrayList<>();
        loader = objectLoader;

        try {
            for(String path : Utils.getFilenames("font"))
            {
                String ext = path.substring(path.lastIndexOf(".") + 1);
                String name = path.substring(path.lastIndexOf("\\") + 1);

                String name1 = path.substring(path.lastIndexOf("/") + 1);
                if(name1.length() < name.length())
                    name = name1;

                if(ext.equalsIgnoreCase("fnt"))
                {
                    FNT font = FNT.readFNT("/font/" + name);
                    try {
                        font.textureID = loader.loadTexture(font.map, GL11.GL_NEAREST, GL11.GL_LINEAR);
                    } catch (Exception e) {e.printStackTrace();}

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

                        font.glyphs.put(character.id, loader.loadModel(new float[]{-1, 1, -1, -1, 1, 1, 1, -1}, new float[]{minX, maxY, minX, minY, maxX, maxY, maxX, minY}));
                    }

                    font.height = maxHeight;

                    Fonts.add(font);
                }
            }
        } catch (Exception e) {e.printStackTrace();}
    }

    public static void drawString(RenderMan renderer, String string, int x, int y, int size, Color color, int begin, int end)
    {
        FNT font = Fonts.get(currentFont);

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
                    renderer.processGuiElement(new Quad(loader, font.textureID, new Vector2f(x + xPos + character.xoffset * ((float) size / 50f), y + (character.height) * ((float) size / 50f) + character.yoffset * ((float) size / 50f)), new Vector2f((float) character.width * ((float) size / 50f), ((float) -character.height) * ((float) size / 50f)), font.glyphs.get(character.id), color, true).staticTexture().smoothstep());
                xPos += character.xadvance * ((float) size / 50f);
            }
        }

    }

    public static float getStringWidth(String string, int size)
    {
        FNT font = Fonts.get(currentFont);

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
        return Fonts.get(currentFont).height * ((float) size / 50f);
    }
}
