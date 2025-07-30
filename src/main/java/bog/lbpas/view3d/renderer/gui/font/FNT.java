package bog.lbpas.view3d.renderer.gui.font;

import bog.lbpas.view3d.core.Model;
import bog.lbpas.view3d.core.Texture;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Bog
 */
public class FNT {

    public static final String BASE_CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890 \"!`?'.,;:()[]{}<>|/@\\^$-%+=#_&~*";

    public info info;
    public common common;
    public smoothstep smoothstep;
    public page page;
    public chars chars;
    public HashMap<Integer, character> characters;

    public BufferedImage map;
    public Texture texture;

    public FNT(FNT.info info, FNT.common common, FNT.smoothstep smoothstep, FNT.page page, FNT.chars chars, HashMap<Integer, character> characters) {
        this.info = info;
        this.common = common;
        this.smoothstep = smoothstep;
        this.page = page;
        this.chars = chars;
        this.characters = characters;
    }
    public character getChar(int c)
    {
        return characters.get(c);
    }
    public static FNT readFNT(InputStream stream)
    {
        BufferedReader FNT = new BufferedReader(new InputStreamReader(stream));
        List<String> lines = FNT.lines().collect(Collectors.toList());

        info info = new info();
        common common = new common();
        smoothstep smoothstep = new smoothstep();
        page page = new page();
        chars chars = new chars();
        HashMap<Integer, character> characters = new HashMap<>();

        for(String line : lines)
        {
            String[] quotes = line.split("\"");
            String newLine = "";
            for(int q = 0; q < quotes.length; q++)
            {
                if (q % 2 != 0)
                    quotes[q] = "\"" + quotes[q].replaceAll(" ", String.valueOf((char)0x0D9E)) + "\"";
                newLine += quotes[q];
            }

            String[] part = newLine.split(" ");

            if(part[0].equalsIgnoreCase("info"))
            {
                for(int i = 1; i < part.length; i++)
                {
                    String[] var = part[i].split("=");

                    if(var[0].equalsIgnoreCase("face"))
                    {
                        info.face = var[1].substring(1, var[1].length() - 1);
                        info.face = info.face.replaceAll(String.valueOf((char)0x0D9E), " ");
                    }
                    else if(var[0].equalsIgnoreCase("size"))
                        info.size = Integer.parseInt(var[1]);
                    else if(var[0].equalsIgnoreCase("bold"))
                        info.bold = var[1].equalsIgnoreCase("1");
                    else if(var[0].equalsIgnoreCase("italic"))
                        info.italic = var[1].equalsIgnoreCase("1");
                    else if(var[0].equalsIgnoreCase("charset"))
                        info.charset = var[1].substring(1, var[1].length() - 1);
                    else if(var[0].equalsIgnoreCase("unicode"))
                        info.unicode = var[1].equalsIgnoreCase("1");
                    else if(var[0].equalsIgnoreCase("stretchH"))
                        info.stretchH = Integer.parseInt(var[1]);
                    else if(var[0].equalsIgnoreCase("smooth"))
                        info.smooth = Integer.parseInt(var[1]);
                    else if(var[0].equalsIgnoreCase("aa"))
                        info.aa = var[1].equalsIgnoreCase("1");
                    else if(var[0].equalsIgnoreCase("padding"))
                    {
                        String[] arr = var[1].split(",");
                        int[] padding = new int[arr.length];
                        for(int i1 = 0; i1 < arr.length; i1++)
                            padding[i1] = Integer.parseInt(arr[i1]);
                        info.padding = padding;
                    }
                    else if(var[0].equalsIgnoreCase("spacing"))
                    {
                        String[] arr = var[1].split(",");
                        int[] spacing = new int[arr.length];
                        for(int i1 = 0; i1 < arr.length; i1++)
                            spacing[i1] = Integer.parseInt(arr[i1]);
                        info.spacing = spacing;
                    }
                }
            }
            else if(part[0].equalsIgnoreCase("common"))
            {
                for (int i = 1; i < part.length; i++)
                {
                    String[] var = part[i].split("=");

                    if (var[0].equalsIgnoreCase("lineHeight"))
                        common.lineHeight = Integer.parseInt(var[1]);
                    else if (var[0].equalsIgnoreCase("base"))
                        common.base = Integer.parseInt(var[1]);
                    else if (var[0].equalsIgnoreCase("scaleW"))
                        common.scaleW = Integer.parseInt(var[1]);
                    else if (var[0].equalsIgnoreCase("scaleH"))
                        common.scaleH = Integer.parseInt(var[1]);
                    else if (var[0].equalsIgnoreCase("pages"))
                        common.pages = Integer.parseInt(var[1]);
                    else if (var[0].equalsIgnoreCase("packed"))
                        common.packed = var[1].equalsIgnoreCase("1");
                }
            }
            else if(part[0].equalsIgnoreCase("smoothstep"))
            {
                for (int i = 1; i < part.length; i++)
                {
                    String[] var = part[i].split("=");

                    if (var[0].equalsIgnoreCase("minWidth"))
                        smoothstep.minWidth = Float.parseFloat(var[1]);
                    else if (var[0].equalsIgnoreCase("maxWidth"))
                        smoothstep.maxWidth = Float.parseFloat(var[1]);
                }
            }
            else if(part[0].equalsIgnoreCase("page"))
            {
                for (int i = 1; i < part.length; i++)
                {
                    String[] var = part[i].split("=");

                    if (var[0].equalsIgnoreCase("id"))
                        page.id = Integer.parseInt(var[1]);
                    else if (var[0].equalsIgnoreCase("file"))
                    {
                        page.file = var[1].substring(1, var[1].length() - 1);
                        page.file = page.file.replaceAll(String.valueOf((char)0x0D9E), " ");
                    }
                }
            }
            else if(part[0].equalsIgnoreCase("chars"))
            {
                for (int i = 1; i < part.length; i++)
                {
                    String[] var = part[i].split("=");

                    if (var[0].equalsIgnoreCase("count"))
                        chars.count = Integer.parseInt(var[1]);
                }
            }
            else if(part[0].equalsIgnoreCase("char"))
            {
                character character = new character();

                for (int i = 1; i < part.length; i++)
                {
                    String[] var = part[i].split("=");

                    switch(var[0].toLowerCase())
                    {
                        case "id":
                            character.id = Integer.parseInt(var[1]);
                            break;
                        case "x":
                            character.x = Integer.parseInt(var[1]);
                            break;
                        case "y":
                            character.y = Integer.parseInt(var[1]);
                            break;
                        case "width":
                            character.width = Integer.parseInt(var[1]);
                            break;
                        case "height":
                            character.height = Integer.parseInt(var[1]);
                            break;
                        case "xoffset":
                            character.xoffset = Integer.parseInt(var[1]);
                            break;
                        case "yoffset":
                            character.yoffset = Integer.parseInt(var[1]);
                            break;
                        case "xadvance":
                            character.xadvance = Integer.parseInt(var[1]);
                            break;
                        case "page":
                            character.page = Integer.parseInt(var[1]);
                            break;
                        case "chnl":
                            character.chnl = Integer.parseInt(var[1]);
                            break;
                    }
                }

                characters.put(character.id, character);
            }
        }

        return new FNT(info, common, smoothstep, page, chars, characters);
    }
    public static class info
    {
        public String face;
        public int size;
        public boolean bold;
        public boolean italic;
        public String charset;
        public boolean unicode;
        public int stretchH;
        public int smooth;
        public boolean aa;
        public int[] padding;
        public int[] spacing;

        public info() {}

        public info(String face, int size, boolean bold, boolean italic, String charset, boolean unicode, int stretchH, int smooth, boolean aa, int[] padding, int[] spacing) {
            this.face = face;
            this.size = size;
            this.bold = bold;
            this.italic = italic;
            this.charset = charset;
            this.unicode = unicode;
            this.stretchH = stretchH;
            this.smooth = smooth;
            this.aa = aa;
            this.padding = padding;
            this.spacing = spacing;
        }
    }
    public static class common
    {
        public int lineHeight;
        public int base;
        public int scaleW;
        public int scaleH;
        public int pages;
        public boolean packed;

        public common(){}
        public common(int lineHeight, int base, int scaleW, int scaleH, int pages, boolean packed) {
            this.lineHeight = lineHeight;
            this.base = base;
            this.scaleW = scaleW;
            this.scaleH = scaleH;
            this.pages = pages;
            this.packed = packed;
        }
    }
    public static class smoothstep
    {
        public float minWidth;
        public float maxWidth;

        public smoothstep(){}
        public smoothstep(float minWidth, float maxWidth) {
            this.minWidth = minWidth;
            this.maxWidth = maxWidth;
        }
    }
    public static class page
    {
        public int id;
        public String file;

        public page(){}
        public page(int id, String file) {
            this.id = id;
            this.file = file;
        }
    }
    public static class chars
    {
        public int count;

        public chars(){}
        public chars(int count) {
            this.count = count;
        }
    }
    public static class character
    {
        public int id;
        public int x;
        public int y;
        public int width;
        public int height;
        public int xoffset;
        public int yoffset;
        public int xadvance;
        public int page;
        public int chnl;

        public Model glyph;

        public character(){}
        public character(int id, int x, int y, int width, int height, int xoffset, int yoffset, int xadvance, int page, int chnl) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.xoffset = xoffset;
            this.yoffset = yoffset;
            this.xadvance = xadvance;
            this.page = page;
            this.chnl = chnl;
        }
    }

}
