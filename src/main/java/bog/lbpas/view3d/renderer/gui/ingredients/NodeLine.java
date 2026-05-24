package bog.lbpas.view3d.renderer.gui.ingredients;

import bog.lbpas.view3d.renderer.gui.font.FNT;
import org.joml.Vector2f;
import org.joml.Vector2i;

import java.awt.*;

/**
 * @author Bog
 */
public class NodeLine extends Quad{

    public Vector2i posStart, posEnd;

    public NodeLine(Vector2i posStart, Vector2i posEnd, Color color) {
        super(color, new Vector2f(), new Vector2f());
        this.posStart = posStart;
        this.posEnd = posEnd;

        float dist = Math.abs(posEnd.x - posStart.x);
        float offset = Math.max(dist * 0.5f, 50.0f);
        float padding = offset + 20.0f;

        float minX = Math.min(posStart.x, posEnd.x) - padding;
        float minY = Math.min(posStart.y, posEnd.y) - padding;
        float maxX = Math.max(posStart.x, posEnd.x) + padding;
        float maxY = Math.max(posStart.y, posEnd.y) + padding;

        this.pos.x = minX;
        this.pos.y = minY;

        this.scale.x = maxX - minX;
        this.scale.y = maxY - minY;
    }

    @Override
    public Type getType() {
        return Type.NODE_LINE ;
    }
}
