package cwlib.enums;

/**
 * @author Bog
 */
public enum JointPattern {
     WAVE(0),
     FORWARDS(1),
     FLIPPER(2);

    private final int value;
    JointPattern(int value) {
        this.value = value;
    }

    public Integer getValue() { return this.value; }

    public static JointPattern fromValue(int value) {
        for (JointPattern pattern : JointPattern.values()) {
            if (pattern.value == value)
                return pattern;
        }
        return JointPattern.WAVE;
    }
}
