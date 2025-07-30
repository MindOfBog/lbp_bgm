package bog.lbpas.view3d.renderer.gui.ingredients;

import bog.lbpas.view3d.core.Model;
import bog.lbpas.view3d.renderer.gui.font.FNT;
import org.joml.Vector2f;

import java.awt.*;

/**
 * @author Bog
 */
public class Glyph extends Quad{

    public FNT font;
    public FNT.character character;

    public boolean bold = false;
    public boolean italics = false;

    public Glyph(FNT font, FNT.character character, boolean bold, boolean italics, Vector2f pos, Vector2f scale, Color color) {
        super(character.glyph, font.texture.id, pos, scale, color);
        this.font = font;
        this.character = character;
        this.bold = bold;
        this.italics = italics;
    }

    @Override
    public Type getType() {
        return Type.GLYPH ;
    }
}
