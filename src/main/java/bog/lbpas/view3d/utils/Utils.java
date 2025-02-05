package bog.lbpas.view3d.utils;

import bog.lbpas.view3d.managers.assetLoading.ObjectLoader;
import bog.lbpas.view3d.core.Material;
import bog.lbpas.view3d.core.Model;
import com.github.weisj.jsvg.SVGDocument;
import com.github.weisj.jsvg.SVGRenderingHints;
import com.github.weisj.jsvg.attributes.ViewBox;
import com.github.weisj.jsvg.geometry.size.FloatSize;
import com.github.weisj.jsvg.parser.SVGLoader;
import org.joml.*;
import org.lwjgl.system.MemoryUtil;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.*;
import java.lang.Math;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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
import java.util.stream.Collectors;

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

    public static LongBuffer storeDataInLongBuffer(long[] data)
    {
        LongBuffer buffer = MemoryUtil.memAllocLong(data.length);
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
        return (float) (Math.round(value * Math.pow(10, decimals)) / Math.pow(10, decimals));
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

    public static ArrayList<String> getFilesInFileDir(String directoryName) {
        try {
            File jarFile = new File(Utils.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            File dir = new File(directoryName);
            File directory = dir.isAbsolute() ? dir : new File(jarFile.getParentFile(), directoryName);

            if (!directory.isDirectory())
                return null;

            Collection<Path> files = Files.list(Paths.get(directory.getPath())).collect(Collectors.toList());

            ArrayList<String> out = new ArrayList();

            for(Path f : files)
                out.add(f.toString());

            return out;
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<String> getFilesInJar(String directoryName) throws URISyntaxException, UnsupportedEncodingException, IOException {
        List<String> filenames = new ArrayList<>();

        URL url = Thread.currentThread().getContextClassLoader().getResource(directoryName);
        if (url != null) {
            if (url.getProtocol().equals("file")) {
                File file = Paths.get(url.toURI()).toFile();
                if (file != null) {
                    File[] files = file.listFiles();
                    if (files != null) {
                        for (File filename : files) {
                            filenames.add(directoryName + "/" + filename.getName());
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
                        if(name.endsWith("/"))
                            name = name.substring(0, name.length() - 1);
                        if (name.startsWith(dirname) && !name.equals(dirname) && !name.substring(dirname.length()).contains("/")) {
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
            keymap.put("None", -1);
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
            keymap.put("A", 65);
            keymap.put("B", 66);
            keymap.put("C", 67);
            keymap.put("D", 68);
            keymap.put("E", 69);
            keymap.put("F", 70);
            keymap.put("G", 71);
            keymap.put("H", 72);
            keymap.put("I", 73);
            keymap.put("J", 74);
            keymap.put("K", 75);
            keymap.put("L", 76);
            keymap.put("M", 77);
            keymap.put("N", 78);
            keymap.put("O", 79);
            keymap.put("P", 80);
            keymap.put("Q", 81);
            keymap.put("R", 82);
            keymap.put("S", 83);
            keymap.put("T", 84);
            keymap.put("U", 85);
            keymap.put("V", 86);
            keymap.put("W", 87);
            keymap.put("X", 88);
            keymap.put("Y", 89);
            keymap.put("Z", 90);
            keymap.put("F1", 290);
            keymap.put("F2", 291);
            keymap.put("F3", 292);
            keymap.put("F4", 293);
            keymap.put("F5", 294);
            keymap.put("F6", 295);
            keymap.put("F7", 296);
            keymap.put("F8", 297);
            keymap.put("F9", 298);
            keymap.put("F10", 299);
            keymap.put("F11", 300);
            keymap.put("F12", 301);
            keymap.put("F13", 302);
            keymap.put("F14", 303);
            keymap.put("F15", 304);
            keymap.put("F16", 305);
            keymap.put("F17", 306);
            keymap.put("F18", 307);
            keymap.put("F19", 308);
            keymap.put("F20", 309);
            keymap.put("F21", 310);
            keymap.put("F22", 311);
            keymap.put("F23", 312);
            keymap.put("F24", 313);
            keymap.put("F25", 314);
            keymap.put("Num Lock", 282);
            keymap.put("Keypad 0", 320);
            keymap.put("Keypad 1", 321);
            keymap.put("Keypad 2", 322);
            keymap.put("Keypad 3", 323);
            keymap.put("Keypad 4", 324);
            keymap.put("Keypad 5", 325);
            keymap.put("Keypad 6", 326);
            keymap.put("Keypad 7", 327);
            keymap.put("Keypad 8", 328);
            keymap.put("Keypad 9", 329);
            keymap.put("Keypad Add", 334);
            keymap.put("Keypad Decimal", 330);
            keymap.put("Keypad Enter", 335);
            keymap.put("Keypad Equal", 336);
            keymap.put("Keypad Multiply", 332);
            keymap.put("Keypad Divide", 331);
            keymap.put("Keypad Subtract", 333);
            keymap.put("Down", 264);
            keymap.put("Left", 263);
            keymap.put("Right", 262);
            keymap.put("Up", 265);
            keymap.put("Apostrophe", 39);
            keymap.put("Backslash", 92);
            keymap.put("Comma", 44);
            keymap.put("Equal", 61);
            keymap.put("Grave Accent", 96);
            keymap.put("Left Bracket", 91);
            keymap.put("Minus", 45);
            keymap.put("Period", 46);
            keymap.put("Right Bracket", 93);
            keymap.put("Semicolon", 59);
            keymap.put("Slash", 47);
            keymap.put("Space", 32);
            keymap.put("Tab", 258);
            keymap.put("Left Alt", 342);
            keymap.put("Left Control", 341);
            keymap.put("Left Shift", 340);
            keymap.put("Left Win", 343);
            keymap.put("Right Alt", 346);
            keymap.put("Right Control", 345);
            keymap.put("Right Shift", 344);
            keymap.put("Right Win", 347);
            keymap.put("Enter", 257);
            keymap.put("Escape", 256);
            keymap.put("Backspace", 259);
            keymap.put("Delete", 261);
            keymap.put("End", 269);
            keymap.put("Home", 268);
            keymap.put("Insert", 260);
            keymap.put("Page Down", 267);
            keymap.put("Page Up", 266);
            keymap.put("Caps Lock", 280);
            keymap.put("Pause", 284);
            keymap.put("Scroll Lock", 281);
            keymap.put("Menu", 348);
            keymap.put("Print Screen", 283);
        }

        for(String keyName : keymap.keySet())
            if(keymap.get(keyName) == key)
                return keyName;
        return "None";
    }

    private static HashMap<String, Integer> buttonmap;
    public static String getMouseButtonName(int button)
    {
        if(buttonmap == null)
        {
            buttonmap = new HashMap<>();
            buttonmap.put("None", -1);
            buttonmap.put("Left Mouse", 0);
            buttonmap.put("Right Mouse", 1);
            buttonmap.put("Middle Mouse", 2);
            buttonmap.put("Mouse 4", 3);
            buttonmap.put("Mouse 5", 4);
            buttonmap.put("Mouse 6", 5);
            buttonmap.put("Mouse 7", 6);
            buttonmap.put("Mouse 8", 7);
        }

        for(String buttonName : buttonmap.keySet())
            if(buttonmap.get(buttonName) == button)
                return buttonName;
        return "None";
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

    public static String toHexColor(Vector4f color)
    {
        String hex = toHexString((int) (color.x * 255f)) + toHexString((int) (color.y * 255f)) + toHexString((int) (color.z * 255f)) + toHexString((int) (color.w * 255f));
        return hex;
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

    public static int parseInt(String i)
    {
        try
        {
            return Math.round(parseFloat(i));
        }catch (Exception e)
        {
            e.printStackTrace();
            return 0;
        }
    }

    public static int parseIntA(String i)
    {
        try
        {
            return Integer.parseInt(i);
        }catch (Exception e)
        {
            e.printStackTrace();
            return 0;
        }
    }

    public static long parseLong(String i)
    {
        try
        {
            return Long.parseLong(i);
        }catch (Exception e)
        {
            e.printStackTrace();
            return 0;
        }
    }

    public static short parseShort(String i)
    {
        try
        {
            return Short.parseShort(i);
        }catch (Exception e)
        {
            e.printStackTrace();
            return 0;
        }
    }

    public static float parseFloat(String f)
    {
        try
        {
            return Float.parseFloat(f);
        }catch (Exception e)
        {
            e.printStackTrace();
            return 0;
        }
    }

    public static BufferedImage toBufferedImage(Image img)
    {
        if (img instanceof BufferedImage)
        {
            return (BufferedImage) img;
        }

        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        return bimage;
    }

    public static Vector2f getClosestPointOnLine(Vector2f a, Vector2f b, Vector2f p)
    {
        Vector2f a_to_p = new Vector2f(p.x - a.x, p.y - a.y);
        Vector2f a_to_b = new Vector2f(b.x - a.x, b.y - a.y);

        double atb2 = Math.pow(a_to_b.x, 2) + Math.pow(a_to_b.y, 2);

        float atp_dot_atb = a_to_p.x * a_to_b.x + a_to_p.y * a_to_b.y;

        double t = atp_dot_atb / atb2;

        Vector2f point = new Vector2f((float) (a.x + a_to_b.x * t), (float) (a.y + a_to_b.y * t));

        float p2a = point.distance(a);
        float p2b = point.distance(b);
        float b2a = b.distance(a);

        if(b2a < p2a || b2a < p2b)
        {
            if(p2a < p2b)
                point = a;
            else
                point = b;
        }

        return point;
    }

    public static Vector4f offsetLine(Vector2f p1, Vector2f p2, float distance)
    {
        Vector2f direction = new Vector2f(p2).sub(p1);
        Vector2f perpendicular = new Vector2f(-direction.y, direction.x);
        perpendicular.normalize();
        perpendicular.mul(distance);
        Vector2f offsetP12 = new Vector2f(p1).add(perpendicular);
        Vector2f offsetP21 = new Vector2f(p2).add(perpendicular);

        return new Vector4f(offsetP12.x, offsetP12.y, offsetP21.x, offsetP21.y);
    }

    public static Vector2f offsetAndFindIntersection(Vector2f p1, Vector2f p2, Vector2f p3, float distance) {
        Vector4f offsetLine1 = offsetLine(p1, p2, distance);
        Vector2f offsetP12 = new Vector2f(offsetLine1.x, offsetLine1.y);
        Vector2f offsetP21 = new Vector2f(offsetLine1.z, offsetLine1.w);
        Vector2f dir1 = new Vector2f(offsetP21).sub(offsetP12).normalize();

        Vector4f offsetLine2 = offsetLine(p2, p3, distance);
        Vector2f offsetP23 = new Vector2f(offsetLine2.x, offsetLine2.y);
        Vector2f offsetP32 = new Vector2f(offsetLine2.z, offsetLine2.w);
        Vector2f dir2 = new Vector2f(offsetP32).sub(offsetP23).normalize();

        float det = dir1.x * dir2.y - dir1.y * dir2.x;

        if (Math.abs(det) < 1e-6) {
            return offsetP21;
        }

        Vector2f diff = new Vector2f(offsetP23).sub(offsetP12);

        float t = (diff.x * dir2.y - diff.y * dir2.x) / det;
        Vector2f intersection = new Vector2f(offsetP12).add(new Vector2f(dir1).mul(t));
        return intersection;
    }

    public static Vector2f rotateAroundPoint(Vector2f point, Vector2f center, double angle) {
        double newX = center.x + (point.x - center.x) * Math.cos(angle) - (point.y - center.y) * Math.sin(angle);
        double newY = center.y + (point.x - center.x) * Math.sin(angle) + (point.y - center.y) * Math.cos(angle);

        point.x = (float) newX;
        point.y = (float) newY;

        return point;
    }

    private static BufferedImage loadAndRenderSVG(SVGDocument svgDocument, int w, int h, boolean antialias, float mR, float mG, float mB) throws IOException
    {
        int width = w <= 0 ? (int) svgDocument.size().width : w;
        int height = h <= 0 ? (int) svgDocument.size().height : h;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gfx = image.createGraphics();

        if(antialias)
        {
            gfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            gfx.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            gfx.setRenderingHint(SVGRenderingHints.KEY_IMAGE_ANTIALIASING, SVGRenderingHints.VALUE_IMAGE_ANTIALIASING_ON);
        }

        svgDocument.render(null, gfx, new ViewBox(width, height));

        gfx.dispose();

        return multiplyFilter(image, mR, mG, mB);
    }

    public static BufferedImage multiplyFilter(BufferedImage image, float mR, float mG, float mB)
    {
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color pixelColor = new Color(image.getRGB(x, y), true);

                int r = (int)(((pixelColor.getRed()/255f) * mR) * 255);
                int g = (int)(((pixelColor.getGreen()/255f) * mG) * 255);
                int b = (int)(((pixelColor.getBlue()/255f) * mB) * 255);

                r = Math.min(255, r);
                g = Math.min(255, g);
                b = Math.min(255, b);

                image.setRGB(x, y, new Color(r, g, b, pixelColor.getAlpha()).getRGB());
            }
        }

        return image;
    }

    public static BufferedImage loadAndRenderSVG(InputStream svg, float scale, boolean antialias) throws IOException
    {
        SVGLoader loader = new SVGLoader();
        SVGDocument svgDocument = loader.load(svg);

        FloatSize size = svgDocument.size();

        return loadAndRenderSVG(svgDocument, (int) (size.width * scale), (int) (size.height * scale), antialias, 1, 1, 1);
    }

    public static BufferedImage loadAndRenderSVG(InputStream svg, int width, int height, boolean antialias) throws IOException
    {
        SVGLoader loader = new SVGLoader();
        SVGDocument svgDocument = loader.load(svg);

        return loadAndRenderSVG(svgDocument, width, height, antialias, 1, 1, 1);
    }

    public static BufferedImage loadAndRenderSVG(InputStream svg, int width, int height, boolean antialias, float mR, float mG, float mB) throws IOException
    {
        SVGLoader loader = new SVGLoader();
        SVGDocument svgDocument = loader.load(svg);

        return loadAndRenderSVG(svgDocument, width, height, antialias, mR, mG, mB);
    }

    public static boolean isBitwiseBool(int flags, int flag)
    {
        return (flags & flag) != 0;
    }

    public static int setBitwiseBool(int flags , int flag, boolean bool)
    {
        return bool ? (flags |= flag) : (flags &= ~flag);
    }

    public static short setBitwiseBool(short flags , int flag, boolean bool)
    {
        return bool ? (flags |= flag) : (flags &= ~flag);
    }

    public static byte setBitwiseBool(byte flags , int flag, boolean bool)
    {
        return bool ? (flags |= flag) : (flags &= ~flag);
    }
}
