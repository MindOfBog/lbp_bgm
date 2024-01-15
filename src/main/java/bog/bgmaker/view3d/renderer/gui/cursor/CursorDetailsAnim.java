package bog.bgmaker.view3d.renderer.gui.cursor;

import bog.bgmaker.view3d.utils.Cursors;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * @author Bog
 */
public class CursorDetailsAnim {

    public int ms;
    public ArrayList<CursorDetails> cursors;

    public CursorDetailsAnim(int ms, ArrayList<CursorDetails> cursors) {
        this.ms = ms;
        this.cursors = cursors;
    }

    public void setupCursorImage(BufferedImage atlas, float size) throws Exception
    {
        for(int i = 0; i < cursors.size(); i++)
            cursors.get(i).setupCursorImage(atlas, size);
    }
}
