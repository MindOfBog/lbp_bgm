package bog.bgmaker.view3d.utils;

import org.joml.Vector3f;
import org.joml.Vector4f;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.net.URI;
import java.util.Scanner;

/**
 * @author Bog
 */
public class Config {

    public static long NANOSECOND = 1000000000L;
    public static float NaNf = Float.intBitsToFloat(0x7fc00000);
    public static double NaNd = Double.longBitsToDouble(0x7ff8000000000000L);


    public static String TITLE = "LBP Background Maker";
    public static float FOV = (float) Math.toRadians(63);
    public static float Z_NEAR = 10f;
    public static float Z_FAR = 500000f;
    public static boolean NO_CULLING;
    public static float MOUSE_SENS = 0.2f;
    public static float CAMERA_MOVE_SPEED = 1500f;
    public static Vector4f DEFAULT_COLOR = new Vector4f(1f, 1f, 1f, 1f);
    public static Vector3f AMBIENT_LIGHT = new Vector3f(1f, 1f, 1f);
    public static float SPECULAR_POWER = 10f;
    public static float FRAMERATE = 120;
    public static float OUTLINE_DISTANCE = 0.6f;
    public static Color OUTLINE_COLOR = new Color(252, 173, 3);
    public static Color BORDER_COLOR_1 = new Color(156, 0, 0, 166);
    public static Color BORDER_COLOR_2 = new Color(230, 0, 0, 166);
    public static Color BORDER_COLOR_3 = new Color(1, 41, 143, 166);
    public static Color BORDER_COLOR_4 = new Color(0, 62, 219, 166);
    public static Color POD_COLOR = new Color(154, 236, 93, 166);
    public static Color EARTH_COLOR = new Color(236, 93, 154, 166);
    public static Color FONT_COLOR = new Color(255, 255, 255, 255);
    public static Color PRIMARY_COLOR = new Color(0, 0, 0, 127);
    public static Color SECONDARY_COLOR = new Color(0, 0, 0, 255);
    public static Color INTERFACE_PRIMARY_COLOR = new Color(0, 0, 0, 157);
    public static Color INTERFACE_PRIMARY_COLOR2 = new Color(0, 0, 0, 255);
    public static Color INTERFACE_SECONDARY_COLOR = new Color(50, 50, 50, 200);
    public static Color INTERFACE_SECONDARY_COLOR2 = new Color(50, 50, 50, 255);
    public static Color INTERFACE_TERTIARY_COLOR = new Color(100, 100, 100, 210);
    public static Color INTERFACE_TERTIARY_COLOR2 = new Color(100, 100, 100, 255);

    private static String buildSettings()
    {
        String builtString = "";

        builtString += "FOV:" + FOV + ";";
        builtString += "Z_NEAR:" + Z_NEAR + ";";
        builtString += "Z_FAR:" + Z_FAR + ";";
        builtString += "NO_CULLING:" + NO_CULLING + ";";
        builtString += "MOUSE_SENS:" + MOUSE_SENS + ";";
        builtString += "CAMERA_MOVE_SPEED:" + CAMERA_MOVE_SPEED + ";";
        builtString += "DEFAULT_COLOR:" + DEFAULT_COLOR.x + "," + DEFAULT_COLOR.y + "," + DEFAULT_COLOR.z + "," + DEFAULT_COLOR.w + ";";
        builtString += "AMBIENT_LIGHT:" + AMBIENT_LIGHT.x + "," + AMBIENT_LIGHT.y + "," + AMBIENT_LIGHT.z + ";";
        builtString += "SPECULAR_POWER:" + SPECULAR_POWER + ";";
        builtString += "FRAMERATE:" + FRAMERATE + ";";
        builtString += "OUTLINE_DISTANCE:" + OUTLINE_DISTANCE + ";";
        builtString += "OUTLINE_COLOR:" + OUTLINE_COLOR.getRed() + "," + OUTLINE_COLOR.getGreen() + "," + OUTLINE_COLOR.getBlue() + "," + OUTLINE_COLOR.getAlpha() + ";";
        builtString += "BORDER_COLOR_1:" + BORDER_COLOR_1.getRed() + "," + BORDER_COLOR_1.getGreen() + "," + BORDER_COLOR_1.getBlue() + "," + BORDER_COLOR_1.getAlpha() + ";";
        builtString += "BORDER_COLOR_2:" + BORDER_COLOR_2.getRed() + "," + BORDER_COLOR_2.getGreen() + "," + BORDER_COLOR_2.getBlue() + "," + BORDER_COLOR_2.getAlpha() + ";";
        builtString += "BORDER_COLOR_3:" + BORDER_COLOR_3.getRed() + "," + BORDER_COLOR_3.getGreen() + "," + BORDER_COLOR_3.getBlue() + "," + BORDER_COLOR_3.getAlpha() + ";";
        builtString += "BORDER_COLOR_4:" + BORDER_COLOR_4.getRed() + "," + BORDER_COLOR_4.getGreen() + "," + BORDER_COLOR_4.getBlue() + "," + BORDER_COLOR_4.getAlpha() + ";";
        builtString += "POD_COLOR:" + POD_COLOR.getRed() + "," + POD_COLOR.getGreen() + "," + POD_COLOR.getBlue() + "," + POD_COLOR.getAlpha() + ";";
        builtString += "EARTH_COLOR:" + EARTH_COLOR.getRed() + "," + EARTH_COLOR.getGreen() + "," + EARTH_COLOR.getBlue() + "," + EARTH_COLOR.getAlpha() + ";";
        builtString += "FONT_COLOR:" + FONT_COLOR.getRed() + "," + FONT_COLOR.getGreen() + "," + FONT_COLOR.getBlue() + "," + FONT_COLOR.getAlpha() + ";";
        builtString += "PRIMARY_COLOR:" + PRIMARY_COLOR.getRed() + "," + PRIMARY_COLOR.getGreen() + "," + PRIMARY_COLOR.getBlue() + "," + PRIMARY_COLOR.getAlpha() + ";";
        builtString += "SECONDARY_COLOR:" + SECONDARY_COLOR.getRed() + "," + SECONDARY_COLOR.getGreen() + "," + SECONDARY_COLOR.getBlue() + "," + SECONDARY_COLOR.getAlpha() + ";";
        builtString += "INTERFACE_PRIMARY_COLOR:" + INTERFACE_PRIMARY_COLOR.getRed() + "," + INTERFACE_PRIMARY_COLOR.getGreen() + "," + INTERFACE_PRIMARY_COLOR.getBlue() + "," + INTERFACE_PRIMARY_COLOR.getAlpha() + ";";
        builtString += "INTERFACE_PRIMARY_COLOR2:" + INTERFACE_PRIMARY_COLOR2.getRed() + "," + INTERFACE_PRIMARY_COLOR2.getGreen() + "," + INTERFACE_PRIMARY_COLOR2.getBlue() + "," + INTERFACE_PRIMARY_COLOR2.getAlpha() + ";";
        builtString += "INTERFACE_SECONDARY_COLOR:" + INTERFACE_SECONDARY_COLOR.getRed() + "," + INTERFACE_SECONDARY_COLOR.getGreen() + "," + INTERFACE_SECONDARY_COLOR.getBlue() + "," + INTERFACE_SECONDARY_COLOR.getAlpha() + ";";
        builtString += "INTERFACE_SECONDARY_COLOR2:" + INTERFACE_SECONDARY_COLOR2.getRed() + "," + INTERFACE_SECONDARY_COLOR2.getGreen() + "," + INTERFACE_SECONDARY_COLOR2.getBlue() + "," + INTERFACE_SECONDARY_COLOR2.getAlpha() + ";";
        builtString += "INTERFACE_TERTIARY_COLOR:" + INTERFACE_TERTIARY_COLOR.getRed() + "," + INTERFACE_TERTIARY_COLOR.getGreen() + "," + INTERFACE_TERTIARY_COLOR.getBlue() + "," + INTERFACE_TERTIARY_COLOR.getAlpha() + ";";
        builtString += "INTERFACE_TERTIARY_COLOR2:" + INTERFACE_TERTIARY_COLOR2.getRed() + "," + INTERFACE_TERTIARY_COLOR2.getGreen() + "," + INTERFACE_TERTIARY_COLOR2.getBlue() + "," + INTERFACE_TERTIARY_COLOR2.getAlpha() + ";";

        return builtString;
    }

    private static File getConfig()
    {
        try {
            String path = (Config.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
            path = path.substring(0, path.lastIndexOf("/"));
            path += "/config.bgm";

            File config = new File(path);

            try {
                Scanner scanner = new Scanner(config);
                scanner.hasNextLine();
                scanner.close();
            } catch (FileNotFoundException e) {
                config.createNewFile();
                updateFile(config);
            }

            return config;
        }catch (Exception e){e.printStackTrace();}

        return null;
    }

    public static void init()
    {
        try
        {
            Scanner myReader = new Scanner(getConfig());
            while (myReader.hasNextLine()) {
                String fileData = myReader.nextLine();
                if(fileData.startsWith("LBPBGM"))
                    fileData = fileData.substring(6);
                fileData = Utils.decrypt(fileData);

                for(String settingData : fileData.split(";"))
                {
                    String setting = settingData.split(":")[0];
                    String data = settingData.split(":")[1];
                    String[] d = data.split(",");

                    switch (setting.toUpperCase())
                    {
                        case "FOV": FOV = Float.parseFloat(data); break;
                        case "Z_NEAR" : Z_NEAR = Float.parseFloat(data); break;
                        case "Z_FAR" : Z_FAR = Float.parseFloat(data); break;
                        case "NO_CULLING" : NO_CULLING = Boolean.parseBoolean(data); break;
                        case "MOUSE_SENS" : MOUSE_SENS = Float.parseFloat(data); break;
                        case "CAMERA_MOVE_SPEED" : CAMERA_MOVE_SPEED = Float.parseFloat(data); break;
                        case "DEFAULT_COLOR" : DEFAULT_COLOR = new Vector4f(Float.parseFloat(d[0]), Float.parseFloat(d[1]), Float.parseFloat(d[2]), Float.parseFloat(d[3])); break;
                        case "AMBIENT_LIGHT" : AMBIENT_LIGHT = new Vector3f(Float.parseFloat(d[0]), Float.parseFloat(d[1]), Float.parseFloat(d[2])); break;
                        case "SPECULAR_POWER" : SPECULAR_POWER = Float.parseFloat(data); break;
                        case "FRAMERATE" : FRAMERATE = Float.parseFloat(data); break;
                        case "OUTLINE_DISTANCE" : OUTLINE_DISTANCE = Float.parseFloat(data); break;
                        case "OUTLINE_COLOR" : OUTLINE_COLOR = new Color(Integer.parseInt(d[0]), Integer.parseInt(d[1]), Integer.parseInt(d[2]), Integer.parseInt(d[3])); break;
                        case "BORDER_COLOR_1" : BORDER_COLOR_1 = new Color(Integer.parseInt(d[0]), Integer.parseInt(d[1]), Integer.parseInt(d[2]), Integer.parseInt(d[3])); break;
                        case "BORDER_COLOR_2" : BORDER_COLOR_2 = new Color(Integer.parseInt(d[0]), Integer.parseInt(d[1]), Integer.parseInt(d[2]), Integer.parseInt(d[3])); break;
                        case "BORDER_COLOR_3" : BORDER_COLOR_3 = new Color(Integer.parseInt(d[0]), Integer.parseInt(d[1]), Integer.parseInt(d[2]), Integer.parseInt(d[3])); break;
                        case "BORDER_COLOR_4" : BORDER_COLOR_4 = new Color(Integer.parseInt(d[0]), Integer.parseInt(d[1]), Integer.parseInt(d[2]), Integer.parseInt(d[3])); break;
                        case "POD_COLOR" : POD_COLOR = new Color(Integer.parseInt(d[0]), Integer.parseInt(d[1]), Integer.parseInt(d[2]), Integer.parseInt(d[3])); break;
                        case "EARTH_COLOR" : EARTH_COLOR = new Color(Integer.parseInt(d[0]), Integer.parseInt(d[1]), Integer.parseInt(d[2]), Integer.parseInt(d[3])); break;
                        case "FONT_COLOR" : FONT_COLOR = new Color(Integer.parseInt(d[0]), Integer.parseInt(d[1]), Integer.parseInt(d[2]), Integer.parseInt(d[3])); break;
                        case "PRIMARY_COLOR" : PRIMARY_COLOR = new Color(Integer.parseInt(d[0]), Integer.parseInt(d[1]), Integer.parseInt(d[2]), Integer.parseInt(d[3])); break;
                        case "SECONDARY_COLOR" : SECONDARY_COLOR = new Color(Integer.parseInt(d[0]), Integer.parseInt(d[1]), Integer.parseInt(d[2]), Integer.parseInt(d[3])); break;
                        case "INTERFACE_PRIMARY_COLOR" : INTERFACE_PRIMARY_COLOR = new Color(Integer.parseInt(d[0]), Integer.parseInt(d[1]), Integer.parseInt(d[2]), Integer.parseInt(d[3])); break;
                        case "INTERFACE_PRIMARY_COLOR2" : INTERFACE_PRIMARY_COLOR2 = new Color(Integer.parseInt(d[0]), Integer.parseInt(d[1]), Integer.parseInt(d[2]), Integer.parseInt(d[3])); break;
                        case "INTERFACE_SECONDARY_COLOR" : INTERFACE_SECONDARY_COLOR = new Color(Integer.parseInt(d[0]), Integer.parseInt(d[1]), Integer.parseInt(d[2]), Integer.parseInt(d[3])); break;
                        case "INTERFACE_SECONDARY_COLOR2" : INTERFACE_SECONDARY_COLOR2 = new Color(Integer.parseInt(d[0]), Integer.parseInt(d[1]), Integer.parseInt(d[2]), Integer.parseInt(d[3])); break;
                        case "INTERFACE_TERTIARY_COLOR" : INTERFACE_TERTIARY_COLOR = new Color(Integer.parseInt(d[0]), Integer.parseInt(d[1]), Integer.parseInt(d[2]), Integer.parseInt(d[3])); break;
                        case "INTERFACE_TERTIARY_COLOR2" : INTERFACE_TERTIARY_COLOR2 = new Color(Integer.parseInt(d[0]), Integer.parseInt(d[1]), Integer.parseInt(d[2]), Integer.parseInt(d[3])); break;
                    }
                }

            }
            myReader.close();
        }catch (Exception e){e.printStackTrace();}
    }

    private static void updateFile(File config)
    {
        try
        {
            FileWriter writer = new FileWriter(config);
            writer.write("LBPBGM" + Utils.encrypt(buildSettings()));
            writer.close();
        }catch (Exception e){e.printStackTrace();}
    }

    public static void updateFile()
    {
        updateFile(getConfig());
    }
}
