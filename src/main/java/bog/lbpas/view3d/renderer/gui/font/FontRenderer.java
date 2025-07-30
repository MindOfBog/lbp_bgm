package bog.lbpas.view3d.renderer.gui.font;

import bog.lbpas.view3d.managers.assetLoading.ObjectLoader;
import bog.lbpas.view3d.managers.RenderMan;
import bog.lbpas.view3d.renderer.gui.GuiRenderer;
import bog.lbpas.view3d.renderer.gui.ingredients.Glyph;
import bog.lbpas.view3d.renderer.gui.ingredients.Quad;
import bog.lbpas.view3d.utils.Config;
import bog.lbpas.view3d.utils.Consts;
import bog.lbpas.view3d.utils.Utils;
import bog.lbpas.view3d.utils.print;
import org.joml.Math;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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

                        int minFilter = font.info.aa ? GL11.GL_LINEAR : GL11.GL_NEAREST;
                        int magFilter = font.info.aa ? GL11.GL_LINEAR : GL11.GL_NEAREST;

                        try {
                            font.map = ImageIO.read(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResourceAsStream(fontPath + "/" + font.page.file)));
                            font.texture = loader.loadTexture(font.map, minFilter, magFilter);
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

                                int minFilter = font.info.aa ? GL11.GL_LINEAR : GL11.GL_NEAREST;
                                int magFilter = font.info.aa ? GL11.GL_LINEAR : GL11.GL_NEAREST;

                                try {
                                    font.map = ImageIO.read(Objects.requireNonNull(Files.newInputStream(Paths.get(fnt.getParent() + "/" + font.page.file))));
                                    font.texture = loader.loadTexture(font.map, minFilter, magFilter);
                                } catch (Exception e) {e.printStackTrace();}

                                buildFont(font);
                            }
                        }

                }
        } catch (Exception e) {print.stackTrace(e);}

        boolean foundHeader = false;
        boolean foundText = false;

        for(int i = 0; i < Fonts.size(); i++)
        {
            if(Fonts.get(i).info.face.equalsIgnoreCase(Config.FONT_HEADER))
            {
                headerFont = i;
                foundHeader = true;
            }
            if(Fonts.get(i).info.face.equalsIgnoreCase(Config.FONT_TEXT))
            {
                textFont = i;
                foundText = true;
            }
        }

        if(!foundHeader)
        {
            Config.FONT_HEADER = "Comfortaa";

            for(int i = 0; i < Fonts.size(); i++)
                if(Fonts.get(i).info.face.equalsIgnoreCase(Config.FONT_HEADER))
                    headerFont = i;
        }
        if(!foundText)
        {
            Config.FONT_TEXT = "Comfortaa";

            for(int i = 0; i < Fonts.size(); i++)
                if(Fonts.get(i).info.face.equalsIgnoreCase(Config.FONT_TEXT))
                    textFont = i;
        }
    }

    public static void drawString(RenderMan renderer, String string, int x, int y, Color color, int begin, int end, FNT font)
    {
        drawString(renderer, string, x, y, Config.GUI_SCALE, color, begin, end, font);
    }

    public static void drawHeader(RenderMan renderer, String string, int x, int y, Color color, int begin, int end, FNT font)
    {
        drawString(renderer, string, x, y, Math.round(Config.GUI_SCALE * 1.25f), color, begin, end, font);
    }

    public static void drawString(RenderMan renderer, String string, int x, int y, int size, Color color, int begin, int end, FNT font)
    {
        float xPos = 0;

        float fontSize = (((float)size) / font.info.size);

        int sizeClamped = Math.clamp(0, 100, size);

        float width = ((sizeClamped / 100f) * (font.smoothstep.maxWidth - font.smoothstep.minWidth)) + font.smoothstep.minWidth;
        width = Math.clamp(0f, font.smoothstep.maxWidth, width);
        float edge = ((100f-sizeClamped)/(100f)) * (1-width-font.smoothstep.minWidth)+width+font.smoothstep.minWidth;
        edge = Math.clamp(width, 1f, edge);

        float boldPow = (float)java.lang.Math.pow((sizeClamped / 100f) * 0.65f, 2f)+0.25f;
        float widthBold = (float) java.lang.Math.pow(width, boldPow);
        widthBold = Math.clamp(0f, (float)java.lang.Math.pow(font.smoothstep.maxWidth, boldPow), widthBold);
        float edgeBold = ((100f-sizeClamped)/(100f)) * (1-widthBold-font.smoothstep.minWidth)+widthBold+font.smoothstep.minWidth;
        edgeBold = Math.clamp(widthBold, 1f, edgeBold);

        boolean bold = false;
        boolean italics = false;

        for(int i = 0; i < string.toCharArray().length; i++)
        {
            char c = string.toCharArray()[i];

            if(c == Consts.FONT_SET_BOLD)
            {
                bold = true;
                continue;
            }
            if(c == Consts.FONT_SET_ITALICS)
            {
                italics = true;
                continue;
            }
            if(c == Consts.FONT_RESET)
            {
                bold = false;
                italics = false;
                continue;
            }

            FNT.character character = font.getChar(c);

            if(character == null && c != " ".toCharArray()[0])
            {
                c = "?".toCharArray()[0];
                character = font.getChar(c);
            }
            if(c == " ".toCharArray()[0])
            {
                xPos += getFontHeight(size, font) * 0.5f;
            }
            else if (character != null)
            {
                if(i >= begin && i <= end)
                {
                    Vector2f pos = new Vector2f(x + xPos + character.xoffset * fontSize, y + (((float) character.height) * fontSize) + (character.yoffset * fontSize));
                    float dropShadowDist = (float) (java.lang.Math.pow(size * (bold ? widthBold : width), 0.2f));
                    Vector2f posDropShadow = new Vector2f(pos.x + dropShadowDist, pos.y + dropShadowDist);
                    Vector2f scale = new Vector2f((float) character.width * fontSize, ((float) -character.height) * fontSize);

                    if(character.glyph == null)
                        getNewChar(font, character);

                    renderer.processGuiElement(new Glyph(font, character, bold, italics, posDropShadow, scale, new Color(java.lang.Math.max((int)(color.getRed() * 0.2f), 0), java.lang.Math.max((int)(color.getGreen() * 0.2f), 0), java.lang.Math.max((int)(color.getBlue() * 0.2f), 0), color.getAlpha())).staticTexture().smoothstep(0f, 1f));
                    renderer.processGuiElement(new Glyph(font, character, bold, italics, pos, scale, color).staticTexture().smoothstep(Math.clamp(0f, 1f, bold ? widthBold : width), Math.clamp(0f, 1f, bold ? edgeBold : edge)));
                }
                xPos += character.xadvance * fontSize;
            }
        }
    }

    public static float getStringWidth(String string, int size, FNT font)
    {
        float xPos = 0;

        float fontSize = (((float)size) / font.info.size);

        for(int i = 0; i < string.toCharArray().length; i++)
        {
            char c = string.toCharArray()[i];

            if(c == Consts.FONT_SET_BOLD || c == Consts.FONT_SET_ITALICS || c == Consts.FONT_RESET)
                continue;

            FNT.character character = font.getChar(c);

            if(character == null && c != " ".toCharArray()[0])
            {
                c = "?".toCharArray()[0];
                character = font.getChar(c);
            }

            if(c == " ".toCharArray()[0])
            {
                xPos += getFontHeight(size, font) * 0.5f;
            }
            else if (character != null)
            {
                xPos += character.xadvance * fontSize;
            }
        }

        return xPos;
    }

    public static float getFontHeight(int size, FNT font)
    {
        float fontSize = (((float)size) / font.info.size);
        return font.common.lineHeight * fontSize;
    }

    public static void buildFont(FNT font)
    {
        for(char c : FNT.BASE_CHARSET.toCharArray())
        {
            FNT.character character = font.getChar(c);

            if(character == null)
                continue;

            getNewChar(font, character);
        }

        Fonts.add(font);
    }

    public static void getNewChar(FNT font, FNT.character character)
    {
        float minX = (float)character.x / (float)font.common.scaleW;
        float minY = (float)character.y / (float)font.common.scaleH;
        float maxX = minX + (float)character.width / (float)font.common.scaleW;
        float maxY = minY + (float)character.height / (float)font.common.scaleH;

        character.glyph = loader.loadModel(new int[]{GuiRenderer.defaultQuad.vbos[0], 8}, new float[]{minX, maxY, minX, minY, maxX, maxY, maxX, minY});
    }
}
