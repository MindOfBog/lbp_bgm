package bog.lbpas.view3d.utils;

import bog.lbpas.view3d.managers.WindowMan;
import bog.lbpas.view3d.renderer.gui.cursor.Cursor;
import bog.lbpas.view3d.renderer.gui.cursor.ECursor;
import common.FileChooser;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

/**
 * @author Bog
 */
public class Cursors {

    private static long currentCursor = -1;
    private static long previousCursor = -1;

    public static ArrayList<Cursor> loadedCursors;
    public static int activeCursor;
    public static int currentCursorType = ECursor.left_ptr.id;

    public static void setCursor(ECursor cursor)
    {
        if(!FileChooser.isLegacyFDopen)
            currentCursorType = cursor.id;
    }

    public static void updateCursor(WindowMan window)
    {
        currentCursor = loadedCursors.get(activeCursor).getCursor(ECursor.fromID(currentCursorType)).cursor;

        if(currentCursor != previousCursor)
        {
            GLFW.glfwSetCursor(window.window, currentCursor);
            previousCursor = currentCursor;
        }
    }

    public static void loadCursors()
    {
        loadedCursors = new ArrayList<>();

        try {
            for(String c1 : Utils.getFilesInJar("cursors"))
            {
                String cursorPath = c1.contains("!/") ? c1.substring(c1.lastIndexOf("!/") + 2) : c1;
                for(String c2 : Utils.getFilesInJar(cursorPath))
                {
                    if(c2.endsWith(".json"))
                    {
                        String jsonPath = c2.contains("!/") ? c2.substring(c2.lastIndexOf("!/") + 2) : c2;

                        int lio = jsonPath.lastIndexOf("/");
                        int lio2 = jsonPath.lastIndexOf("\\");

                        if(lio2 > lio)
                            lio = lio2;

                        jsonPath = jsonPath.substring(0, lio);

                        Cursor cursor = Cursor.fromJSON(new String[]{jsonPath, "jar"});
                        loadedCursors.add(cursor);
                    }
                }
            }
            ArrayList<String> c = Utils.getFilesInFileDir("cursors");
            if(c != null)
                for(String c1 : c)
                {
                    ArrayList<String> cc = Utils.getFilesInFileDir(c1);
                    if(cc != null)
                        for(String c2 : cc)
                        {
                            if(c2.endsWith(".json"))
                            {
                                String jsonPath = c2.contains("!/") ? c2.substring(c2.lastIndexOf("!/") + 2) : c2;

                                int lio = jsonPath.lastIndexOf("/");
                                int lio2 = jsonPath.lastIndexOf("\\");

                                if(lio2 > lio)
                                    lio = lio2;

                                jsonPath = jsonPath.substring(0, lio);

                                Cursor cursor = Cursor.fromJSON(new String[]{jsonPath, "jar"});
                                loadedCursors.add(cursor);
                            }
                        }
                }

        } catch (Exception e) {e.printStackTrace();}
    }

    public static void updateCursors()
    {
        for(Cursor cursor: loadedCursors)
            cursor.updateCursors(Config.CURSOR_SCALE);
    }
}
