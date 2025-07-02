package bog.projectLoader;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.Configuration;
import org.lwjgl.system.MemoryStack;

import javax.swing.*;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.system.MemoryStack.stackPush;

/**
 * @author Bog
 */
public class Main{

    private static final String CONFIG_PATH = "/launcher.json";
    private static JsonObject configData;


    public static void main(String[] args){

        System.out.println("starting project launcher");

//        Configuration.GLFW_CHECK_THREAD0.set(false);
//        try{
//            if (!GLFW.glfwInit()){
//                throw new IllegalStateException("Unable to initialize GLFW");
//            }
//        } catch (UnsatisfiedLinkError e){
//            showErrorDialog("GLFW native libraries not found. Check your setup.");
//            return;
//        }

        System.out.println("getting config");

        try(InputStream is = Main.class.getResourceAsStream(CONFIG_PATH)) {
            if (is == null) {
                showErrorDialog("Data Missing.");
                System.exit(1);
            }
            Reader reader = new InputStreamReader(is);
            Gson gson = new Gson();
            configData = gson.fromJson(reader, JsonObject.class);
        } catch (IOException e) {
            showErrorDialog("Error reading data: " + e.getMessage());
            System.exit(1);
        }

        boolean debug = false;
        for (String arg : args) {
            if (arg.equalsIgnoreCase("-debug")) {
                debug = true;
                break;
            }
        }

        System.out.println("launching asset studio");

        launchProgram(getProgramPath(), debug);
    }

    private static String getProgramPath()
    {
        String programJarPath = null;
        if(configData.has("programJar"))
        {
            programJarPath = configData.get("programJar").getAsString();

            if (!Files.exists(Paths.get(programJarPath)))
                programJarPath = promptForJarFileTinyFD();
        }
        else {
            programJarPath = promptForJarFileTinyFD();
        }

        if(programJarPath == null)
            return getProgramPath();
        else
            return programJarPath;
    }

    private static String promptForJarFileTinyFD() {

        int selection = JOptionPane.showOptionDialog(null, "LBPAS executable not found, would you like to choose a path?", "LBPAS Project File Error", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"Yes", "Cancel"}, "Yes");

        if(selection == 0) {
            String result = null;

            try (MemoryStack stack = stackPush()) {

                PointerBuffer patterns = stack.mallocPointer(1);
                patterns.put(stack.UTF8("*.jar"));
                patterns.flip();

                result = org.lwjgl.util.tinyfd.TinyFileDialogs.tinyfd_openFileDialog(
                        "Locate Asset Studio JAR",
                        Paths.get(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toString(),
                        patterns,
                        null,
                        false
                );
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }

            if (result == null)
                System.exit(0);

            return result;
        }
        else
            System.exit(0);

        return "";
    }

    private static void launchProgram(String programJar, boolean debug) {
        try {
            List<String> command = new ArrayList<>();
            command.add("java");
            command.add("-jar");
            command.add(programJar);
            if(debug)
                command.add("-debug");
            command.add("projectPath=" + Paths.get(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toString());

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(new File(System.getProperty("user.dir")));

            pb.inheritIO();

            Process process = pb.start();

            int exitCode = process.waitFor();
            System.out.println("Process finished with exit code " + exitCode);
            System.exit(exitCode);
        } catch (Exception e) {
            showErrorDialog("Error launching Asset Studio: " + e.getMessage());
        }
    }

    private static void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "LBPAS Project File Error", JOptionPane.ERROR_MESSAGE);
    }
}
