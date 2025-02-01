package cwlib.enums;

/**
 * @author Bog
 */
public enum JointType {

     LEGACY(0),
     ELASTIC(1),
     SPRING(2),
     CHAIN(3),
     PISTON(4),
     STRING(5),
     ROD(6),
     BOLT(7),
     SPRING_ANGULAR(8),
     MOTOR(9),
     QUANTIZED(10);

    private final int value;
    JointType(int value) {
        this.value = value;
    }

    public Integer getValue() { return this.value; }

    public static JointType fromValue(int value) {
        for (JointType type : JointType.values()) {
            if (type.value == value)
                return type;
        }
        return JointType.LEGACY;
    }
}
