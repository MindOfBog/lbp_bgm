package bog.lbpas.view3d.renderer.gui.cursor;

/**
 * @author Bog
 */
public enum ECursor {

    all_scroll(1),
    bd_double_arrow(2),
    bottom_left_corner(3),
    bottom_right_corner(4),
    bottom_side(5),
    bottom_tee(6),
    circle(7),
    context_menu(8),
    copy(9),
    cross(10),
    crossed_circle(11),
    crosshair(12),
    dnd_ask(13),
    dnd_copy(14),
    dnd_link(15),
    dnd_move(16),
    dnd_no_drop(17),
    dnd_none(18),
    dotbox(19),
    fd_double_arrow(20),
    grabbing(21),
    hand1(22),
    hand2(23),
    left_ptr(24),
    left_side(25),
    left_tee(26),
    link(27),
    ll_angle(28),
    lr_angle(29),
    move(30),
    pencil(31),
    plus(32),
    pointer_move(33),
    question_arrow(34),
    right_ptr(35),
    right_side(36),
    right_tee(37),
    sb_down_arrow(38),
    sb_h_double_arrow(39),
    sb_left_arrow(40),
    sb_right_arrow(41),
    sb_up_arrow(42),
    sb_v_double_arrow(43),
    tcross(44),
    top_left_corner(45),
    top_right_corner(46),
    top_side(47),
    top_tee(48),
    ul_angle(49),
    ur_angle(50),
    vertical_text(51),
    X_cursor(52),
    xterm(53),
    zoom_in(54),
    zoom_out(55),

    watch(56),
    left_ptr_watch(57);

    public final int id;

    ECursor(int id)
    {
        this.id = id;
    }

    public static ECursor fromID(int id) {
        for (ECursor c : ECursor.values())
            if (c.id == id)
                return c;
        throw new IllegalArgumentException("No cursor with id \"" + id + "\".");
    }
}
