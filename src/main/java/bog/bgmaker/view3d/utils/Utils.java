package bog.bgmaker.view3d.utils;

import bog.bgmaker.view3d.ObjectLoader;
import bog.bgmaker.view3d.core.Material;
import bog.bgmaker.view3d.core.Model;
import bog.bgmaker.view3d.core.Triangle;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.text.DecimalFormat;
import java.util.List;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author Bog
 */
public class Utils {

    public static FloatBuffer storeDataInFloatBuffer(float[] data)
    {
        FloatBuffer buffer = MemoryUtil.memAllocFloat(data.length);
        buffer.put(data).flip();
        return buffer;
    }

    public static float[] storeDataInFloatArray(FloatBuffer data)
    {
        return data.flip().array();
    }

    public static IntBuffer storeDataInIntBuffer(int[] data)
    {
        IntBuffer buffer = MemoryUtil.memAllocInt(data.length);
        buffer.put(data).flip();
        return buffer;
    }

    public static String loadResource(String filename) throws Exception
    {
        String result;

        try(InputStream in = Utils.class.getResourceAsStream(filename);
            Scanner scanner = new Scanner(in, StandardCharsets.UTF_8.name()))
        {
            result = scanner.useDelimiter("\\A").next();
        }

        return result;
    }

    public static ArrayList<String> readAllLines(String fileName)
    {
        ArrayList<String> list = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new InputStreamReader(Utils.class.getResourceAsStream(fileName))))
        {
            String line;
            while((line = br.readLine()) != null)
            {
                list.add(line);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return list;
    }

    public static Model getCubeModel(ObjectLoader loader, Vector3f minPos, Vector3f maxPos, Material material)
    {
        float[] vertices = new float[] {
                minPos.x, maxPos.y, maxPos.z,
                minPos.x, minPos.y, maxPos.z,
                maxPos.x, minPos.y, maxPos.z,
                maxPos.x, maxPos.y, maxPos.z,
                minPos.x, maxPos.y, minPos.z,
                maxPos.x, maxPos.y, minPos.z,
                minPos.x, minPos.y, minPos.z,
                maxPos.x, minPos.y, minPos.z,
                minPos.x, maxPos.y, minPos.z,
                maxPos.x, maxPos.y, minPos.z,
                minPos.x, maxPos.y, maxPos.z,
                maxPos.x, maxPos.y, maxPos.z,
                maxPos.x, maxPos.y, maxPos.z,
                maxPos.x, minPos.y, maxPos.z,
                minPos.x, maxPos.y, maxPos.z,
                minPos.x, minPos.y, maxPos.z,
                minPos.x, minPos.y, minPos.z,
                maxPos.x, minPos.y, minPos.z,
                minPos.x, minPos.y, maxPos.z,
                maxPos.x, minPos.y, maxPos.z,
        };
        float[] textCoords = new float[]{
                0.0f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.5f, 0.0f,
                0.0f, 0.0f,
                0.5f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.0f, 1.0f,
                0.5f, 1.0f,
                0.0f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.0f,
                0.5f, 0.5f,
                0.5f, 0.0f,
                1.0f, 0.0f,
                0.5f, 0.5f,
                1.0f, 0.5f,
        };
        int[] indices = new int[]{
                0, 1, 3,
                3, 1, 2,
                8, 10, 11,
                9, 8, 11,
                12, 13, 7,
                5, 12, 7,
                14, 15, 6,
                4, 14, 6,
                16, 18, 19,
                17, 16, 19,
                4, 6, 7,
                5, 4, 7,
        };

        Model model = loader.loadModel(vertices, textCoords, new float[0], indices);
        model.material = material;

        return model;
    }

    public static BufferedImage setPngTransparentColor(BufferedImage bufferedImage, Color color){
        BufferedImage bi = new BufferedImage(bufferedImage.getWidth(),bufferedImage.getHeight(),BufferedImage.TYPE_INT_ARGB);
        for (int x=0;x<bufferedImage.getWidth();x++){
            for (int y=0;y<bufferedImage.getHeight();y++){
                int rgba = bufferedImage.getRGB(x,y);
                boolean isTrans = (rgba & 0xff000000) == 0;
                if (isTrans)
                    bi.setRGB(x,y, (color.getRGB()&0x00ffffff));
                else
                    bi.setRGB(x,y,rgba);
            }
        }
        return bi;
    }

    public static BufferedImage clone(BufferedImage bufferedImage) throws Exception{
        BufferedImage bi = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int x=0;x<bufferedImage.getWidth();x++)
            for (int y=0;y<bufferedImage.getHeight();y++)
            {
                int rgba = bufferedImage.getRGB(x, y);
                bi.setRGB(x, y, rgba);
            }

        return bi;
    }

    public static BufferedImage colorFilter(BufferedImage original, Color color)
    {
        BufferedImage filtered = new BufferedImage(original.getWidth(), original.getHeight(), original.getType());

        for(int x = 0; x < original.getWidth(); x++)
            for(int y = 0; y < original.getHeight(); y++)
            {
                Color c = new Color(original.getRGB(x, y), true).brighter();
                float mul = ((c.getRed() + c.getGreen() + c.getBlue()) / 3f) / 255f;
                filtered.setRGB(x, y, new Color((color.getRed() / 255f) * mul, (color.getGreen() / 255f) * mul, (color.getBlue() / 255f) * mul, ((color.getAlpha() + c.getAlpha())/2) / 255f).getRGB());
            }

        return filtered;
    }

    public static BufferedImage brightness(BufferedImage original, float multiplier)
    {
        RescaleOp rescaleOp = new RescaleOp(multiplier, 15, null);
        rescaleOp.filter(original, original);
        return original;
    }

    public static float round(float value, int decimals)
    {
        String dec = "###.#";
        for(int i = 1; i < decimals; i++)
            dec += "#";

        DecimalFormat df = new DecimalFormat(dec);

        return Float.parseFloat(df.format(value));
    }

    public static double round(double value, int decimals)
    {
        String dec = "###.#";
        for(int i = 1; i < decimals; i++)
            dec += "#";

        DecimalFormat df = new DecimalFormat(dec);

        return Double.parseDouble(df.format(value));
    }

    public static BufferedImage drawStringLegacy(String text, Color color, int size) {

        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, size));
        FontMetrics fm = g2d.getFontMetrics();

        img = new BufferedImage(fm.stringWidth(text), fm.getHeight(), BufferedImage.TYPE_INT_ARGB);
        g2d = img.createGraphics();
        g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, size));
        fm = g2d.getFontMetrics();

        g2d.setPaint(color);
        int x = 0;
        int y = fm.getHeight()/2 + fm.getHeight()/4;
        g2d.drawString(text, x, y);
        g2d.dispose();
        return img;
    }

    public static BufferedImage drawStringBackdropLegacy(String text, Color color, Color backdropColor, int size) {

        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, size));
        FontMetrics fm = g2d.getFontMetrics();

        img = new BufferedImage(fm.stringWidth(text), fm.getHeight(), BufferedImage.TYPE_INT_ARGB);
        g2d = img.createGraphics();
        g2d.setPaint(backdropColor);
        g2d.fillRect(0, 0, fm.stringWidth(text), fm.getHeight());
        g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, size));
        fm = g2d.getFontMetrics();

        g2d.setPaint(color);
        int x = 0;
        int y = fm.getHeight()/2 + fm.getHeight()/4;
        g2d.drawString(text, x, y);
        g2d.dispose();
        return img;
    }

    public static Vector2i getStringBoundsLegacy(String text, int size) {

        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, size));
        FontMetrics fm = g2d.getFontMetrics();
        g2d.dispose();
        return new Vector2i(fm.stringWidth(text), fm.getHeight());
    }

    public static boolean isPointInTriangle(Vector3f point, Triangle triange) {

        Vector3f e10 = triange.p2.sub(triange.p1);
        Vector3f e20 = triange.p3.sub(triange.p1);

        float a = e10.dot(e10);
        float b = e10.dot(e20);
        float c = e20.dot(e20);
        float ac_bb= (a * c) - (b * b);

        Vector3f vp = new Vector3f(point.x - triange.p1.x, point.y - triange.p1.y, point.z - triange.p1.z);

        float d = vp.dot(e10);
        float e = vp.dot(e20);
        float x = (d*c)-(e*b);
        float y = (e*a)-(d*b);
        float z = x+y-ac_bb;

        return z < 0 && x >= 0 && y >= 0;
    }

    static Cipher ecipher;
    static Cipher dcipher;
    static byte[] salt = {
            (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32,
            (byte) 0x56, (byte) 0x35, (byte) 0xE3, (byte) 0x03
    };
    static int iterationCount = 19;

    public static String encrypt(String plainText)
            throws NoSuchAlgorithmException,
            InvalidKeySpecException,
            NoSuchPaddingException,
            InvalidKeyException,
            InvalidAlgorithmParameterException,
            UnsupportedEncodingException,
            IllegalBlockSizeException,
            BadPaddingException {

        String secretKey = "megafart";
        KeySpec keySpec = new PBEKeySpec(secretKey.toCharArray(), salt, iterationCount);
        SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
        AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);

        ecipher = Cipher.getInstance(key.getAlgorithm());
        ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
        String charSet = "UTF-8";
        byte[] in = plainText.getBytes(charSet);
        byte[] out = ecipher.doFinal(in);
        String encStr = new String(Base64.getEncoder().encode(out));
        return encStr;
    }

    public static String decrypt(String encryptedText)
            throws NoSuchAlgorithmException,
            InvalidKeySpecException,
            NoSuchPaddingException,
            InvalidKeyException,
            InvalidAlgorithmParameterException,
            IllegalBlockSizeException,
            BadPaddingException,
            IOException {

        String secretKey = "megafart";
        KeySpec keySpec = new PBEKeySpec(secretKey.toCharArray(), salt, iterationCount);
        SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
        AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);
        dcipher = Cipher.getInstance(key.getAlgorithm());
        dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
        byte[] enc = Base64.getDecoder().decode(encryptedText);
        byte[] utf8 = dcipher.doFinal(enc);
        String charSet = "UTF-8";
        String plainStr = new String(utf8, charSet);
        return plainStr;
    }

    public static List<String> getFilenames(String directoryName) throws URISyntaxException, UnsupportedEncodingException, IOException {
        List<String> filenames = new ArrayList<>();

        URL url = Thread.currentThread().getContextClassLoader().getResource(directoryName);
        if (url != null) {
            if (url.getProtocol().equals("file")) {
                File file = Paths.get(url.toURI()).toFile();
                if (file != null) {
                    File[] files = file.listFiles();
                    if (files != null) {
                        for (File filename : files) {
                            filenames.add(filename.toString());
                        }
                    }
                }
            } else if (url.getProtocol().equals("jar")) {
                String dirname = directoryName + "/";
                String path = url.getPath();
                String jarPath = path.substring(5, path.indexOf("!"));
                try (JarFile jar = new JarFile(URLDecoder.decode(jarPath, StandardCharsets.UTF_8.name()))) {
                    Enumeration<JarEntry> entries = jar.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        String name = entry.getName();
                        if (name.startsWith(dirname) && !dirname.equals(name)) {
                            URL resource = Thread.currentThread().getContextClassLoader().getResource(name);
                            filenames.add(resource.toString());
                        }
                    }
                }
            }
        }
        return filenames;
    }

    private static HashMap<String, Integer> keymap;

    public static String getKeyName(int key)
    {
        if(keymap == null)
        {
            keymap = new HashMap<>();
            keymap.put("none", -1);
            keymap.put("0", 48);
            keymap.put("1", 49);
            keymap.put("2", 50);
            keymap.put("3", 51);
            keymap.put("4", 52);
            keymap.put("5", 53);
            keymap.put("6", 54);
            keymap.put("7", 55);
            keymap.put("8", 56);
            keymap.put("9", 57);
            keymap.put("a", 65);
            keymap.put("b", 66);
            keymap.put("c", 67);
            keymap.put("d", 68);
            keymap.put("e", 69);
            keymap.put("f", 70);
            keymap.put("g", 71);
            keymap.put("h", 72);
            keymap.put("i", 73);
            keymap.put("j", 74);
            keymap.put("k", 75);
            keymap.put("l", 76);
            keymap.put("m", 77);
            keymap.put("n", 78);
            keymap.put("o", 79);
            keymap.put("p", 80);
            keymap.put("q", 81);
            keymap.put("r", 82);
            keymap.put("s", 83);
            keymap.put("t", 84);
            keymap.put("u", 85);
            keymap.put("v", 86);
            keymap.put("w", 87);
            keymap.put("x", 88);
            keymap.put("y", 89);
            keymap.put("z", 90);
            keymap.put("f1", 290);
            keymap.put("f2", 291);
            keymap.put("f3", 292);
            keymap.put("f4", 293);
            keymap.put("f5", 294);
            keymap.put("f6", 295);
            keymap.put("f7", 296);
            keymap.put("f8", 297);
            keymap.put("f9", 298);
            keymap.put("f10", 299);
            keymap.put("f11", 300);
            keymap.put("f12", 301);
            keymap.put("f13", 302);
            keymap.put("f14", 303);
            keymap.put("f15", 304);
            keymap.put("f16", 305);
            keymap.put("f17", 306);
            keymap.put("f18", 307);
            keymap.put("f19", 308);
            keymap.put("f20", 309);
            keymap.put("f21", 310);
            keymap.put("f22", 311);
            keymap.put("f23", 312);
            keymap.put("f24", 313);
            keymap.put("f25", 314);
            keymap.put("num lock", 282);
            keymap.put("keypad 0", 320);
            keymap.put("keypad 1", 321);
            keymap.put("keypad 2", 322);
            keymap.put("keypad 3", 323);
            keymap.put("keypad 4", 324);
            keymap.put("keypad 5", 325);
            keymap.put("keypad 6", 326);
            keymap.put("keypad 7", 327);
            keymap.put("keypad 8", 328);
            keymap.put("keypad 9", 329);
            keymap.put("keypad add", 334);
            keymap.put("keypad decimal", 330);
            keymap.put("keypad enter", 335);
            keymap.put("keypad equal", 336);
            keymap.put("keypad multiply", 332);
            keymap.put("keypad divide", 331);
            keymap.put("keypad subtract", 333);
            keymap.put("down", 264);
            keymap.put("left", 263);
            keymap.put("right", 262);
            keymap.put("up", 265);
            keymap.put("apostrophe", 39);
            keymap.put("backslash", 92);
            keymap.put("comma", 44);
            keymap.put("equal", 61);
            keymap.put("grave accent", 96);
            keymap.put("left bracket", 91);
            keymap.put("minus", 45);
            keymap.put("period", 46);
            keymap.put("right bracket", 93);
            keymap.put("semicolon", 59);
            keymap.put("slash", 47);
            keymap.put("space", 32);
            keymap.put("tab", 258);
            keymap.put("left alt", 342);
            keymap.put("left control", 341);
            keymap.put("left shift", 340);
            keymap.put("left win", 343);
            keymap.put("right alt", 346);
            keymap.put("right control", 345);
            keymap.put("right shift", 344);
            keymap.put("right win", 347);
            keymap.put("enter", 257);
            keymap.put("escape", 256);
            keymap.put("backspace", 259);
            keymap.put("delete", 261);
            keymap.put("end", 269);
            keymap.put("home", 268);
            keymap.put("insert", 260);
            keymap.put("page down", 267);
            keymap.put("page up", 266);
            keymap.put("caps lock", 280);
            keymap.put("pause", 284);
            keymap.put("scroll lock", 281);
            keymap.put("menu", 348);
            keymap.put("print screen", 283);
            keymap.put("world 1", 161);
            keymap.put("world 2", 162);
        }

        for(String keyName : keymap.keySet())
            if(keymap.get(keyName) == key)
                return keyName;
        return "none";
    }

    public static Color parseHexColor(String hex)
    {
        try {
            if(hex.startsWith("#"))
                hex = hex.substring(1);

            int r = Integer.parseInt(hex.substring(0, 2),16);
            int g = Integer.parseInt(hex.substring(2, 4),16);
            int b = Integer.parseInt(hex.substring(4, 6),16);
            int a = Integer.parseInt("ff",16);

            if(hex.length() == 8)
                a = Integer.parseInt(hex.substring(6, 8),16);

            return new Color(r, g, b, a);
        }catch (Exception e){}
        return Color.black;
    }

    public static Vector4f parseHexColorVec(String hex)
    {
        try {
            int r = Integer.parseInt(hex.substring(0, 2),16);
            int g = Integer.parseInt(hex.substring(2, 4),16);
            int b = Integer.parseInt(hex.substring(4, 6),16);
            int a = Integer.parseInt("ff",16);

            if(hex.length() == 8)
                a = Integer.parseInt(hex.substring(6, 8),16);

            return new Vector4f(r/255f, g/255f, b/255f, a/255f);
        }catch (Exception e){}
        return new Vector4f(0f, 0f, 0f, 0f);
    }

    public static String toHexColor(Color color)
    {
        String hex = toHexString(color.getRed()) + toHexString(color.getGreen()) + toHexString(color.getBlue()) + toHexString(color.getAlpha());
        return hex;
    }

    private static String toHexString(int dec)
    {
        String hex = Integer.toHexString(dec);
        return hex.length() == 1 ? "0" + hex : hex;
    }

}
