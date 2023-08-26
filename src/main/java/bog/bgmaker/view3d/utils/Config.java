package bog.bgmaker.view3d.utils;

import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.net.URI;
import java.util.ArrayList;
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
    public static boolean MATERIAL_PREVIEW_SHADING = true;
    public static int KEY_FORWARD = GLFW.GLFW_KEY_W;
    public static int KEY_LEFT = GLFW.GLFW_KEY_A;
    public static int KEY_BACK = GLFW.GLFW_KEY_S;
    public static int KEY_RIGHT = GLFW.GLFW_KEY_D;
    public static int KEY_UP = GLFW.GLFW_KEY_SPACE;
    public static int KEY_DOWN = GLFW.GLFW_KEY_LEFT_SHIFT;
    public static int KEY_SHADING = GLFW.GLFW_KEY_Z;
    public static boolean LEVEL_BORDERS = true;
    public static boolean POD_HELPER = false;

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
        builtString += "MATERIAL_PREVIEW_SHADING:" + MATERIAL_PREVIEW_SHADING + ";";
        builtString += "KEY_FORWARD:" + KEY_FORWARD + ";";
        builtString += "KEY_LEFT:" + KEY_LEFT + ";";
        builtString += "KEY_BACK:" + KEY_BACK + ";";
        builtString += "KEY_RIGHT:" + KEY_RIGHT + ";";
        builtString += "KEY_UP:" + KEY_UP + ";";
        builtString += "KEY_DOWN:" + KEY_DOWN + ";";
        builtString += "KEY_SHADING:" + KEY_SHADING + ";";
        builtString += "LEVEL_BORDERS:" + LEVEL_BORDERS + ";";
        builtString += "POD_HELPER:" + POD_HELPER + ";";

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
        variables = new ArrayList<>();

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
                        case "MATERIAL_PREVIEW_SHADING" : MATERIAL_PREVIEW_SHADING = Boolean.parseBoolean(data); break;
                        case "KEY_FORWARD" : KEY_FORWARD = Integer.parseInt(data); break;
                        case "KEY_LEFT" : KEY_LEFT = Integer.parseInt(data); break;
                        case "KEY_BACK" : KEY_BACK = Integer.parseInt(data); break;
                        case "KEY_RIGHT" : KEY_RIGHT = Integer.parseInt(data); break;
                        case "KEY_UP" : KEY_UP = Integer.parseInt(data); break;
                        case "KEY_DOWN" : KEY_DOWN = Integer.parseInt(data); break;
                        case "KEY_SHADING" : KEY_SHADING = Integer.parseInt(data); break;
                        case "LEVEL_BORDERS" : LEVEL_BORDERS = Boolean.parseBoolean(data); break;
                        case "POD_HELPER" : POD_HELPER = Boolean.parseBoolean(data); break;
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

    public static ArrayList<Object> variables;

    public static boolean hasConfigChanged()
    {
        boolean hasChanged = false;
        loop : for(int i = 0; i < variables.size(); i++)
        {
            Object o = variables.get(i);
            switch (i)
            {
                case 0:
                    if(!o.equals(FOV))
                    {
                        hasChanged = true;
                        updateVariables();
                        break loop;
                    }
                    break;
                case 1:
                    if(!o.equals(Z_NEAR))
                    {
                        hasChanged = true;
                        updateVariables();
                        break loop;
                    }
                    break;
                case 2:
                    if(!o.equals(Z_FAR))
                    {
                        hasChanged = true;
                        updateVariables();
                        break loop;
                    }
                    break;
                case 3:
                    if(!o.equals(NO_CULLING))
                    {
                        hasChanged = true;
                        updateVariables();
                        break loop;
                    }
                    break;
                case 4:
                    if(!o.equals(MOUSE_SENS))
                    {
                        hasChanged = true;
                        updateVariables();
                        break loop;
                    }
                    break;
                case 5:
                    if(!o.equals(CAMERA_MOVE_SPEED))
                    {
                        hasChanged = true;
                        updateVariables();
                        break loop;
                    }
                    break;
                case 6:
                    if(!o.equals(DEFAULT_COLOR))
                    {
                        hasChanged = true;
                        updateVariables();
                        break loop;
                    }
                    break;
                case 7:
                    if(!o.equals(AMBIENT_LIGHT))
                    {
                        hasChanged = true;
                        updateVariables();
                        break loop;
                    }
                    break;
                case 8:
                    if(!o.equals(SPECULAR_POWER))
                    {
                        hasChanged = true;
                        updateVariables();
                        break loop;
                    }
                    break;
                case 9:
                    if(!o.equals(FRAMERATE))
                    {
                        hasChanged = true;
                        updateVariables();
                        break loop;
                    }
                    break;
                case 10:
                    if(!o.equals(OUTLINE_DISTANCE))
                    {
                        hasChanged = true;
                        updateVariables();
                        break loop;
                    }
                    break;
                case 11:
                    if(!o.equals(OUTLINE_COLOR))
                    {
                        hasChanged = true;
                        updateVariables();
                        break loop;
                    }
                    break;
                case 12:
                    if(!o.equals(BORDER_COLOR_1))
                    {
                        hasChanged = true;
                        updateVariables();
                        break loop;
                    }
                    break;
                case 13:
                    if(!o.equals(BORDER_COLOR_2))
                    {
                        hasChanged = true;
                        updateVariables();
                        break loop;
                    }
                    break;
                case 14:
                    if(!o.equals(BORDER_COLOR_3))
                    {
                        hasChanged = true;
                        updateVariables();
                        break loop;
                    }
                    break;
                case 15:
                    if(!o.equals(BORDER_COLOR_4))
                    {
                        hasChanged = true;
                        updateVariables();
                        break loop;
                    }
                    break;
                case 16:
                    if(!o.equals(POD_COLOR))
                    {
                        hasChanged = true;
                        updateVariables();
                        break loop;
                    }
                    break;
                case 17:
                    if(!o.equals(EARTH_COLOR))
                    {
                        hasChanged = true;
                        updateVariables();
                        break loop;
                    }
                    break;
                case 18:
                    if(!o.equals(FONT_COLOR))
                    {
                        hasChanged = true;
                        updateVariables();
                        break loop;
                    }
                    break;
                case 19:
                    if(!o.equals(PRIMARY_COLOR))
                    {
                        hasChanged = true;
                        updateVariables();
                        break loop;
                    }
                    break;
                case 20:
                    if(!o.equals(SECONDARY_COLOR))
                    {
                        hasChanged = true;
                        updateVariables();
                        break loop;
                    }
                    break;
                case 21:
                    if(!o.equals(INTERFACE_PRIMARY_COLOR))
                    {
                        hasChanged = true;
                        updateVariables();
                        break loop;
                    }
                    break;
                case 22:
                    if(!o.equals(INTERFACE_PRIMARY_COLOR2))
                    {
                        hasChanged = true;
                        updateVariables();
                        break loop;
                    }
                    break;
                case 23:
                    if(!o.equals(INTERFACE_SECONDARY_COLOR))
                    {
                        hasChanged = true;
                        updateVariables();
                        break loop;
                    }
                    break;
                case 24:
                    if(!o.equals(INTERFACE_SECONDARY_COLOR2))
                    {
                        hasChanged = true;
                        updateVariables();
                        break loop;
                    }
                    break;
                case 25:
                    if(!o.equals(INTERFACE_TERTIARY_COLOR))
                    {
                        hasChanged = true;
                        updateVariables();
                        break loop;
                    }
                    break;
                case 26:
                    if(!o.equals(INTERFACE_TERTIARY_COLOR2))
                    {
                        hasChanged = true;
                        updateVariables();
                        break loop;
                    }
                    break;
                case 27:
                    if(!o.equals(MATERIAL_PREVIEW_SHADING))
                    {
                        hasChanged = true;
                        updateVariables();
                        break loop;
                    }
                    break;
                case 28:
                    if(!o.equals(KEY_FORWARD))
                    {
                        hasChanged = true;
                        updateVariables();
                        break loop;
                    }
                    break;
                case 29:
                    if(!o.equals(KEY_LEFT))
                    {
                        hasChanged = true;
                        updateVariables();
                        break loop;
                    }
                    break;
                case 30:
                    if(!o.equals(KEY_BACK))
                    {
                        hasChanged = true;
                        updateVariables();
                        break loop;
                    }
                    break;
                case 31:
                    if(!o.equals(KEY_RIGHT))
                    {
                        hasChanged = true;
                        updateVariables();
                        break loop;
                    }
                    break;
                case 32:
                    if(!o.equals(KEY_UP))
                    {
                        hasChanged = true;
                        updateVariables();
                        break loop;
                    }
                    break;
                case 33:
                    if(!o.equals(KEY_DOWN))
                    {
                        hasChanged = true;
                        updateVariables();
                        break loop;
                    }
                    break;
                case 34:
                    if(!o.equals(KEY_SHADING))
                    {
                        hasChanged = true;
                        updateVariables();
                        break loop;
                    }
                    break;
                case 35:
                    if(!o.equals(LEVEL_BORDERS))
                    {
                        hasChanged = true;
                        updateVariables();
                        break loop;
                    }
                    break;
                case 36:
                    if(!o.equals(POD_HELPER))
                    {
                        hasChanged = true;
                        updateVariables();
                        break loop;
                    }
                    break;
            }
        }
        if(variables.size() == 0)
            updateVariables();
        return hasChanged;
    }

    private static void updateVariables()
    {
        variables.clear();
        variables.add(FOV);
        variables.add(Z_NEAR);
        variables.add(Z_FAR);
        variables.add(NO_CULLING);
        variables.add(MOUSE_SENS);
        variables.add(CAMERA_MOVE_SPEED);
        variables.add(DEFAULT_COLOR);
        variables.add(AMBIENT_LIGHT);
        variables.add(SPECULAR_POWER);
        variables.add(FRAMERATE);
        variables.add(OUTLINE_DISTANCE);
        variables.add(OUTLINE_COLOR);
        variables.add(BORDER_COLOR_1);
        variables.add(BORDER_COLOR_2);
        variables.add(BORDER_COLOR_3);
        variables.add(BORDER_COLOR_4);
        variables.add(POD_COLOR);
        variables.add(EARTH_COLOR);
        variables.add(FONT_COLOR);
        variables.add(PRIMARY_COLOR);
        variables.add(SECONDARY_COLOR);
        variables.add(INTERFACE_PRIMARY_COLOR);
        variables.add(INTERFACE_PRIMARY_COLOR2);
        variables.add(INTERFACE_SECONDARY_COLOR);
        variables.add(INTERFACE_SECONDARY_COLOR2);
        variables.add(INTERFACE_TERTIARY_COLOR);
        variables.add(INTERFACE_TERTIARY_COLOR2);
        variables.add(MATERIAL_PREVIEW_SHADING);
        variables.add(KEY_FORWARD);
        variables.add(KEY_LEFT);
        variables.add(KEY_BACK);
        variables.add(KEY_RIGHT);
        variables.add(KEY_UP);
        variables.add(KEY_DOWN);
        variables.add(KEY_SHADING);
        variables.add(LEVEL_BORDERS);
        variables.add(POD_HELPER);
    }
}
