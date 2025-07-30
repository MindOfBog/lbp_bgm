package bog.lbpas.view3d.renderer.gui.ingredients;

import bog.lbpas.view3d.core.Transformation3D;

/**
 * @author Bog
 */
public abstract class Drawable {

    public boolean staticVAO = true;
    public boolean staticVBO = true;

    public boolean invert = false;

    public abstract Type getType();

    public Drawable invert()
    {
        invert = !invert;
        return this;
    }

    public static enum Type
    {
        INVALID(-1),
        TRI_STRIP(0),
        TRI_FAN(1),
        LINE(2),
        LINE_STRIP(3),
        SCISSOR(4),
        CIRCLE(5),
        BLUR(6),
        COLOR_PICKER(7),
        GLYPH(8);

        public final int value;

        private Type(int value) {
            this.value = value;
        }

        public static Type fromValue(int value) {
            for (Type type : Type.values()) {
                if (type.value == value)
                    return type;
            }
            return INVALID;
        }
    }
}
