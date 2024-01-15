package bog.bgmaker.view3d.renderer.gui.cursor;

import com.github.weisj.jsvg.SVGDocument;
import com.github.weisj.jsvg.SVGRenderingHints;
import com.github.weisj.jsvg.attributes.ViewBox;
import com.github.weisj.jsvg.geometry.size.FloatSize;
import com.github.weisj.jsvg.parser.SVGLoader;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author Bog
 */
public class Cursor {

    public String[] path;
    public String name;
    public boolean aa;
    public CursorDetails all_scroll;
    public CursorDetails bd_double_arrow;
    public CursorDetails bottom_left_corner;
    public CursorDetails bottom_right_corner;
    public CursorDetails bottom_side;
    public CursorDetails bottom_tee;
    public CursorDetails circle;
    public CursorDetails context_menu;
    public CursorDetails copy;
    public CursorDetails cross;
    public CursorDetails crossed_circle;
    public CursorDetails crosshair;
    public CursorDetails dnd_ask;
    public CursorDetails dnd_copy;
    public CursorDetails dnd_link;
    public CursorDetails dnd_move;
    public CursorDetails dnd_no_drop;
    public CursorDetails dnd_none;
    public CursorDetails dotbox;
    public CursorDetails fd_double_arrow;
    public CursorDetails grabbing;
    public CursorDetails hand1;
    public CursorDetails hand2;
    public CursorDetails left_ptr;
    public CursorDetails left_side;
    public CursorDetails left_tee;
    public CursorDetails link;
    public CursorDetails ll_angle;
    public CursorDetails lr_angle;
    public CursorDetails move;
    public CursorDetails pencil;
    public CursorDetails plus;
    public CursorDetails pointer_move;
    public CursorDetails question_arrow;
    public CursorDetails right_ptr;
    public CursorDetails right_side;
    public CursorDetails right_tee;
    public CursorDetails sb_down_arrow;
    public CursorDetails sb_h_double_arrow;
    public CursorDetails sb_left_arrow;
    public CursorDetails sb_right_arrow;
    public CursorDetails sb_up_arrow;
    public CursorDetails sb_v_double_arrow;
    public CursorDetails tcross;
    public CursorDetails top_left_corner;
    public CursorDetails top_right_corner;
    public CursorDetails top_side;
    public CursorDetails top_tee;
    public CursorDetails ul_angle;
    public CursorDetails ur_angle;
    public CursorDetails vertical_text;
    public CursorDetails X_cursor;
    public CursorDetails xterm;
    public CursorDetails zoom_in;
    public CursorDetails zoom_out;

    public CursorDetailsAnim watch;
    public CursorDetailsAnim left_ptr_watch;

    public static Cursor fromJSON(JsonObject file)
    {
        Cursor cursor = new Cursor();

        cursor.aa = false;
        cursor.name = "null";

        try{cursor.aa = file.get("ANTIALIAS").getAsBoolean();}catch (Exception e){}
        try{cursor.name = file.get("NAME").getAsString();}catch (Exception e){}

        JsonObject HOTSPOTS = file.get("HOTSPOTS").getAsJsonObject();
        JsonObject HOTSPOTS_ANIMATED = file.get("HOTSPOTS_ANIMATED").getAsJsonObject();
        JsonObject COORDS = file.get("COORDS").getAsJsonObject();

        JsonArray all_scroll_hs = HOTSPOTS.get("all-scroll").getAsJsonArray();
        JsonArray all_scroll_coords = COORDS.get("all-scroll").getAsJsonArray();
        cursor.all_scroll = new CursorDetails(all_scroll_coords.get(0).getAsInt(), all_scroll_coords.get(1).getAsInt(),
                all_scroll_hs.get(0).getAsInt(), all_scroll_hs.get(1).getAsInt(),
                all_scroll_coords.get(2).getAsInt(), all_scroll_coords.get(3).getAsInt());

        JsonArray bd_double_arrow_hs = HOTSPOTS.get("bd_double_arrow").getAsJsonArray();
        JsonArray bd_double_arrow_coords = COORDS.get("bd_double_arrow").getAsJsonArray();
        cursor.bd_double_arrow = new CursorDetails(bd_double_arrow_coords.get(0).getAsInt(), bd_double_arrow_coords.get(1).getAsInt(),
                bd_double_arrow_hs.get(0).getAsInt(), bd_double_arrow_hs.get(1).getAsInt(),
                bd_double_arrow_coords.get(2).getAsInt(), bd_double_arrow_coords.get(3).getAsInt());

        JsonArray bottom_left_corner_hs = HOTSPOTS.get("bottom_left_corner").getAsJsonArray();
        JsonArray bottom_left_corner_coords = COORDS.get("bottom_left_corner").getAsJsonArray();
        cursor.bottom_left_corner = new CursorDetails(bottom_left_corner_coords.get(0).getAsInt(), bottom_left_corner_coords.get(1).getAsInt(),
                bottom_left_corner_hs.get(0).getAsInt(), bottom_left_corner_hs.get(1).getAsInt(),
                bottom_left_corner_coords.get(2).getAsInt(), bottom_left_corner_coords.get(3).getAsInt());

        JsonArray bottom_right_corner_hs = HOTSPOTS.get("bottom_right_corner").getAsJsonArray();
        JsonArray bottom_right_corner_coords = COORDS.get("bottom_right_corner").getAsJsonArray();
        cursor.bottom_right_corner = new CursorDetails(bottom_right_corner_coords.get(0).getAsInt(), bottom_right_corner_coords.get(1).getAsInt(),
                bottom_right_corner_hs.get(0).getAsInt(), bottom_right_corner_hs.get(1).getAsInt(),
                bottom_right_corner_coords.get(2).getAsInt(), bottom_right_corner_coords.get(3).getAsInt());

        JsonArray bottom_side_hs = HOTSPOTS.get("bottom_side").getAsJsonArray();
        JsonArray bottom_side_coords = COORDS.get("bottom_side").getAsJsonArray();
        cursor.bottom_side = new CursorDetails(bottom_side_coords.get(0).getAsInt(), bottom_side_coords.get(1).getAsInt(),
                bottom_side_hs.get(0).getAsInt(), bottom_side_hs.get(1).getAsInt(),
                bottom_side_coords.get(2).getAsInt(), bottom_side_coords.get(3).getAsInt());

        JsonArray bottom_tee_hs = HOTSPOTS.get("bottom_tee").getAsJsonArray();
        JsonArray bottom_tee_coords = COORDS.get("bottom_tee").getAsJsonArray();
        cursor.bottom_tee = new CursorDetails(bottom_tee_coords.get(0).getAsInt(), bottom_tee_coords.get(1).getAsInt(),
                bottom_tee_hs.get(0).getAsInt(), bottom_tee_hs.get(1).getAsInt(),
                bottom_tee_coords.get(2).getAsInt(), bottom_tee_coords.get(3).getAsInt());

        JsonArray circle_hs = HOTSPOTS.get("circle").getAsJsonArray();
        JsonArray circle_coords = COORDS.get("circle").getAsJsonArray();
        cursor.circle = new CursorDetails(circle_coords.get(0).getAsInt(), circle_coords.get(1).getAsInt(),
                circle_hs.get(0).getAsInt(), circle_hs.get(1).getAsInt(),
                circle_coords.get(2).getAsInt(), circle_coords.get(3).getAsInt());

        JsonArray context_menu_hs = HOTSPOTS.get("context-menu").getAsJsonArray();
        JsonArray context_menu_coords = COORDS.get("context-menu").getAsJsonArray();
        cursor.context_menu = new CursorDetails(context_menu_coords.get(0).getAsInt(), context_menu_coords.get(1).getAsInt(),
                context_menu_hs.get(0).getAsInt(), context_menu_hs.get(1).getAsInt(),
                context_menu_coords.get(2).getAsInt(), context_menu_coords.get(3).getAsInt());

        JsonArray copy_hs = HOTSPOTS.get("copy").getAsJsonArray();
        JsonArray copy_coords = COORDS.get("copy").getAsJsonArray();
        cursor.copy = new CursorDetails(copy_coords.get(0).getAsInt(), copy_coords.get(1).getAsInt(),
                copy_hs.get(0).getAsInt(), copy_hs.get(1).getAsInt(),
                copy_coords.get(2).getAsInt(), copy_coords.get(3).getAsInt());

        JsonArray cross_hs = HOTSPOTS.get("cross").getAsJsonArray();
        JsonArray cross_coords = COORDS.get("cross").getAsJsonArray();
        cursor.cross = new CursorDetails(cross_coords.get(0).getAsInt(), cross_coords.get(1).getAsInt(),
                cross_hs.get(0).getAsInt(), cross_hs.get(1).getAsInt(),
                cross_coords.get(2).getAsInt(), cross_coords.get(3).getAsInt());

        JsonArray crossed_circle_hs = HOTSPOTS.get("crossed_circle").getAsJsonArray();
        JsonArray crossed_circle_coords = COORDS.get("crossed_circle").getAsJsonArray();
        cursor.crossed_circle = new CursorDetails(crossed_circle_coords.get(0).getAsInt(), crossed_circle_coords.get(1).getAsInt(),
                crossed_circle_hs.get(0).getAsInt(), crossed_circle_hs.get(1).getAsInt(),
                crossed_circle_coords.get(2).getAsInt(), crossed_circle_coords.get(3).getAsInt());

        JsonArray crosshair_hs = HOTSPOTS.get("crosshair").getAsJsonArray();
        JsonArray crosshair_coords = COORDS.get("crosshair").getAsJsonArray();
        cursor.crosshair = new CursorDetails(crosshair_coords.get(0).getAsInt(), crosshair_coords.get(1).getAsInt(),
                crosshair_hs.get(0).getAsInt(), crosshair_hs.get(1).getAsInt(),
                crosshair_coords.get(2).getAsInt(), crosshair_coords.get(3).getAsInt());

        JsonArray dnd_ask_hs = HOTSPOTS.get("dnd-ask").getAsJsonArray();
        JsonArray dnd_ask_coords = COORDS.get("dnd-ask").getAsJsonArray();
        cursor.dnd_ask = new CursorDetails(dnd_ask_coords.get(0).getAsInt(), dnd_ask_coords.get(1).getAsInt(),
                dnd_ask_hs.get(0).getAsInt(), dnd_ask_hs.get(1).getAsInt(),
                dnd_ask_coords.get(2).getAsInt(), dnd_ask_coords.get(3).getAsInt());

        JsonArray dnd_copy_hs = HOTSPOTS.get("dnd-copy").getAsJsonArray();
        JsonArray dnd_copy_coords = COORDS.get("dnd-copy").getAsJsonArray();
        cursor.dnd_copy = new CursorDetails(dnd_copy_coords.get(0).getAsInt(), dnd_copy_coords.get(1).getAsInt(),
                dnd_copy_hs.get(0).getAsInt(), dnd_copy_hs.get(1).getAsInt(),
                dnd_copy_coords.get(2).getAsInt(), dnd_copy_coords.get(3).getAsInt());

        JsonArray dnd_link_hs = HOTSPOTS.get("dnd-link").getAsJsonArray();
        JsonArray dnd_link_coords = COORDS.get("dnd-link").getAsJsonArray();
        cursor.dnd_link = new CursorDetails(dnd_link_coords.get(0).getAsInt(), dnd_link_coords.get(1).getAsInt(),
                dnd_link_hs.get(0).getAsInt(), dnd_link_hs.get(1).getAsInt(),
                dnd_link_coords.get(2).getAsInt(), dnd_link_coords.get(3).getAsInt());

        JsonArray dnd_move_hs = HOTSPOTS.get("dnd-move").getAsJsonArray();
        JsonArray dnd_move_coords = COORDS.get("dnd-move").getAsJsonArray();
        cursor.dnd_move = new CursorDetails(dnd_move_coords.get(0).getAsInt(), dnd_move_coords.get(1).getAsInt(),
                dnd_move_hs.get(0).getAsInt(), dnd_move_hs.get(1).getAsInt(),
                dnd_move_coords.get(2).getAsInt(), dnd_move_coords.get(3).getAsInt());

        JsonArray dnd_no_drop_hs = HOTSPOTS.get("dnd-no-drop").getAsJsonArray();
        JsonArray dnd_no_drop_coords = COORDS.get("dnd-no-drop").getAsJsonArray();
        cursor.dnd_no_drop = new CursorDetails(dnd_no_drop_coords.get(0).getAsInt(), dnd_no_drop_coords.get(1).getAsInt(),
                dnd_no_drop_hs.get(0).getAsInt(), dnd_no_drop_hs.get(1).getAsInt(),
                dnd_no_drop_coords.get(2).getAsInt(), dnd_no_drop_coords.get(3).getAsInt());

        JsonArray dnd_none_hs = HOTSPOTS.get("dnd-none").getAsJsonArray();
        JsonArray dnd_none_coords = COORDS.get("dnd-none").getAsJsonArray();
        cursor.dnd_none = new CursorDetails(dnd_none_coords.get(0).getAsInt(), dnd_none_coords.get(1).getAsInt(),
                dnd_none_hs.get(0).getAsInt(), dnd_none_hs.get(1).getAsInt(),
                dnd_none_coords.get(2).getAsInt(), dnd_none_coords.get(3).getAsInt());

        JsonArray dotbox_hs = HOTSPOTS.get("dotbox").getAsJsonArray();
        JsonArray dotbox_coords = COORDS.get("dotbox").getAsJsonArray();
        cursor.dotbox = new CursorDetails(dotbox_coords.get(0).getAsInt(), dotbox_coords.get(1).getAsInt(),
                dotbox_hs.get(0).getAsInt(), dotbox_hs.get(1).getAsInt(),
                dotbox_coords.get(2).getAsInt(), dotbox_coords.get(3).getAsInt());

        JsonArray fd_double_arrow_hs = HOTSPOTS.get("fd_double_arrow").getAsJsonArray();
        JsonArray fd_double_arrow_coords = COORDS.get("fd_double_arrow").getAsJsonArray();
        cursor.fd_double_arrow = new CursorDetails(fd_double_arrow_coords.get(0).getAsInt(), fd_double_arrow_coords.get(1).getAsInt(),
                fd_double_arrow_hs.get(0).getAsInt(), fd_double_arrow_hs.get(1).getAsInt(),
                fd_double_arrow_coords.get(2).getAsInt(), fd_double_arrow_coords.get(3).getAsInt());

        JsonArray grabbing_hs = HOTSPOTS.get("grabbing").getAsJsonArray();
        JsonArray grabbing_coords = COORDS.get("grabbing").getAsJsonArray();
        cursor.grabbing = new CursorDetails(grabbing_coords.get(0).getAsInt(), grabbing_coords.get(1).getAsInt(),
                grabbing_hs.get(0).getAsInt(), grabbing_hs.get(1).getAsInt(),
                grabbing_coords.get(2).getAsInt(), grabbing_coords.get(3).getAsInt());

        JsonArray hand1_hs = HOTSPOTS.get("hand1").getAsJsonArray();
        JsonArray hand1_coords = COORDS.get("hand1").getAsJsonArray();
        cursor.hand1 = new CursorDetails(hand1_coords.get(0).getAsInt(), hand1_coords.get(1).getAsInt(),
                hand1_hs.get(0).getAsInt(), hand1_hs.get(1).getAsInt(),
                hand1_coords.get(2).getAsInt(), hand1_coords.get(3).getAsInt());

        JsonArray hand2_hs = HOTSPOTS.get("hand2").getAsJsonArray();
        JsonArray hand2_coords = COORDS.get("hand2").getAsJsonArray();
        cursor.hand2 = new CursorDetails(hand2_coords.get(0).getAsInt(), hand2_coords.get(1).getAsInt(),
                hand2_hs.get(0).getAsInt(), hand2_hs.get(1).getAsInt(),
                hand2_coords.get(2).getAsInt(), hand2_coords.get(3).getAsInt());

        JsonArray left_ptr_hs = HOTSPOTS.get("left_ptr").getAsJsonArray();
        JsonArray left_ptr_coords = COORDS.get("left_ptr").getAsJsonArray();
        cursor.left_ptr = new CursorDetails(left_ptr_coords.get(0).getAsInt(), left_ptr_coords.get(1).getAsInt(),
                left_ptr_hs.get(0).getAsInt(), left_ptr_hs.get(1).getAsInt(),
                left_ptr_coords.get(2).getAsInt(), left_ptr_coords.get(3).getAsInt());

        JsonArray left_side_hs = HOTSPOTS.get("left_side").getAsJsonArray();
        JsonArray left_side_coords = COORDS.get("left_side").getAsJsonArray();
        cursor.left_side = new CursorDetails(left_side_coords.get(0).getAsInt(), left_side_coords.get(1).getAsInt(),
                left_side_hs.get(0).getAsInt(), left_side_hs.get(1).getAsInt(),
                left_side_coords.get(2).getAsInt(), left_side_coords.get(3).getAsInt());

        JsonArray left_tee_hs = HOTSPOTS.get("left_tee").getAsJsonArray();
        JsonArray left_tee_coords = COORDS.get("left_tee").getAsJsonArray();
        cursor.left_tee = new CursorDetails(left_tee_coords.get(0).getAsInt(), left_tee_coords.get(1).getAsInt(),
                left_tee_hs.get(0).getAsInt(), left_tee_hs.get(1).getAsInt(),
                left_tee_coords.get(2).getAsInt(), left_tee_coords.get(3).getAsInt());

        JsonArray link_hs = HOTSPOTS.get("link").getAsJsonArray();
        JsonArray link_coords = COORDS.get("link").getAsJsonArray();
        cursor.link = new CursorDetails(link_coords.get(0).getAsInt(), link_coords.get(1).getAsInt(),
                link_hs.get(0).getAsInt(), link_hs.get(1).getAsInt(),
                link_coords.get(2).getAsInt(), link_coords.get(3).getAsInt());

        JsonArray ll_angle_hs = HOTSPOTS.get("ll_angle").getAsJsonArray();
        JsonArray ll_angle_coords = COORDS.get("ll_angle").getAsJsonArray();
        cursor.ll_angle = new CursorDetails(ll_angle_coords.get(0).getAsInt(), ll_angle_coords.get(1).getAsInt(),
                ll_angle_hs.get(0).getAsInt(), ll_angle_hs.get(1).getAsInt(),
                ll_angle_coords.get(2).getAsInt(), ll_angle_coords.get(3).getAsInt());

        JsonArray lr_angle_hs = HOTSPOTS.get("lr_angle").getAsJsonArray();
        JsonArray lr_angle_coords = COORDS.get("lr_angle").getAsJsonArray();
        cursor.lr_angle = new CursorDetails(lr_angle_coords.get(0).getAsInt(), lr_angle_coords.get(1).getAsInt(),
                lr_angle_hs.get(0).getAsInt(), lr_angle_hs.get(1).getAsInt(),
                lr_angle_coords.get(2).getAsInt(), lr_angle_coords.get(3).getAsInt());

        JsonArray move_hs = HOTSPOTS.get("move").getAsJsonArray();
        JsonArray move_coords = COORDS.get("move").getAsJsonArray();
        cursor.move = new CursorDetails(move_coords.get(0).getAsInt(), move_coords.get(1).getAsInt(),
                move_hs.get(0).getAsInt(), move_hs.get(1).getAsInt(),
                move_coords.get(2).getAsInt(), move_coords.get(3).getAsInt());

        JsonArray pencil_hs = HOTSPOTS.get("pencil").getAsJsonArray();
        JsonArray pencil_coords = COORDS.get("pencil").getAsJsonArray();
        cursor.pencil = new CursorDetails(pencil_coords.get(0).getAsInt(), pencil_coords.get(1).getAsInt(),
                pencil_hs.get(0).getAsInt(), pencil_hs.get(1).getAsInt(),
                pencil_coords.get(2).getAsInt(), pencil_coords.get(3).getAsInt());

        JsonArray plus_hs = HOTSPOTS.get("plus").getAsJsonArray();
        JsonArray plus_coords = COORDS.get("plus").getAsJsonArray();
        cursor.plus = new CursorDetails(plus_coords.get(0).getAsInt(), plus_coords.get(1).getAsInt(),
                plus_hs.get(0).getAsInt(), plus_hs.get(1).getAsInt(),
                plus_coords.get(2).getAsInt(), plus_coords.get(3).getAsInt());

        JsonArray pointer_move_hs = HOTSPOTS.get("pointer-move").getAsJsonArray();
        JsonArray pointer_move_coords = COORDS.get("pointer-move").getAsJsonArray();
        cursor.pointer_move = new CursorDetails(pointer_move_coords.get(0).getAsInt(), pointer_move_coords.get(1).getAsInt(),
                pointer_move_hs.get(0).getAsInt(), pointer_move_hs.get(1).getAsInt(),
                pointer_move_coords.get(2).getAsInt(), pointer_move_coords.get(3).getAsInt());

        JsonArray question_arrow_hs = HOTSPOTS.get("question_arrow").getAsJsonArray();
        JsonArray question_arrow_coords = COORDS.get("question_arrow").getAsJsonArray();
        cursor.question_arrow = new CursorDetails(question_arrow_coords.get(0).getAsInt(), question_arrow_coords.get(1).getAsInt(),
                question_arrow_hs.get(0).getAsInt(), question_arrow_hs.get(1).getAsInt(),
                question_arrow_coords.get(2).getAsInt(), question_arrow_coords.get(3).getAsInt());

        JsonArray right_ptr_hs = HOTSPOTS.get("right_ptr").getAsJsonArray();
        JsonArray right_ptr_coords = COORDS.get("right_ptr").getAsJsonArray();
        cursor.right_ptr = new CursorDetails(right_ptr_coords.get(0).getAsInt(), right_ptr_coords.get(1).getAsInt(),
                right_ptr_hs.get(0).getAsInt(), right_ptr_hs.get(1).getAsInt(),
                right_ptr_coords.get(2).getAsInt(), right_ptr_coords.get(3).getAsInt());

        JsonArray right_side_hs = HOTSPOTS.get("right_side").getAsJsonArray();
        JsonArray right_side_coords = COORDS.get("right_side").getAsJsonArray();
        cursor.right_side = new CursorDetails(right_side_coords.get(0).getAsInt(), right_side_coords.get(1).getAsInt(),
                right_side_hs.get(0).getAsInt(), right_side_hs.get(1).getAsInt(),
                right_side_coords.get(2).getAsInt(), right_side_coords.get(3).getAsInt());

        JsonArray right_tee_hs = HOTSPOTS.get("right_tee").getAsJsonArray();
        JsonArray right_tee_coords = COORDS.get("right_tee").getAsJsonArray();
        cursor.right_tee = new CursorDetails(right_tee_coords.get(0).getAsInt(), right_tee_coords.get(1).getAsInt(),
                right_tee_hs.get(0).getAsInt(), right_tee_hs.get(1).getAsInt(),
                right_tee_coords.get(2).getAsInt(), right_tee_coords.get(3).getAsInt());

        JsonArray sb_down_arrow_hs = HOTSPOTS.get("sb_down_arrow").getAsJsonArray();
        JsonArray sb_down_arrow_coords = COORDS.get("sb_down_arrow").getAsJsonArray();
        cursor.sb_down_arrow = new CursorDetails(sb_down_arrow_coords.get(0).getAsInt(), sb_down_arrow_coords.get(1).getAsInt(),
                sb_down_arrow_hs.get(0).getAsInt(), sb_down_arrow_hs.get(1).getAsInt(),
                sb_down_arrow_coords.get(2).getAsInt(), sb_down_arrow_coords.get(3).getAsInt());

        JsonArray sb_h_double_arrow_hs = HOTSPOTS.get("sb_h_double_arrow").getAsJsonArray();
        JsonArray sb_h_double_arrow_coords = COORDS.get("sb_h_double_arrow").getAsJsonArray();
        cursor.sb_h_double_arrow = new CursorDetails(sb_h_double_arrow_coords.get(0).getAsInt(), sb_h_double_arrow_coords.get(1).getAsInt(),
                sb_h_double_arrow_hs.get(0).getAsInt(), sb_h_double_arrow_hs.get(1).getAsInt(),
                sb_h_double_arrow_coords.get(2).getAsInt(), sb_h_double_arrow_coords.get(3).getAsInt());

        JsonArray sb_left_arrow_hs = HOTSPOTS.get("sb_left_arrow").getAsJsonArray();
        JsonArray sb_left_arrow_coords = COORDS.get("sb_left_arrow").getAsJsonArray();
        cursor.sb_left_arrow = new CursorDetails(sb_left_arrow_coords.get(0).getAsInt(), sb_left_arrow_coords.get(1).getAsInt(),
                sb_left_arrow_hs.get(0).getAsInt(), sb_left_arrow_hs.get(1).getAsInt(),
                sb_left_arrow_coords.get(2).getAsInt(), sb_left_arrow_coords.get(3).getAsInt());

        JsonArray sb_right_arrow_hs = HOTSPOTS.get("sb_right_arrow").getAsJsonArray();
        JsonArray sb_right_arrow_coords = COORDS.get("sb_right_arrow").getAsJsonArray();
        cursor.sb_right_arrow = new CursorDetails(sb_right_arrow_coords.get(0).getAsInt(), sb_right_arrow_coords.get(1).getAsInt(),
                sb_right_arrow_hs.get(0).getAsInt(), sb_right_arrow_hs.get(1).getAsInt(),
                sb_right_arrow_coords.get(2).getAsInt(), sb_right_arrow_coords.get(3).getAsInt());

        JsonArray sb_up_arrow_hs = HOTSPOTS.get("sb_up_arrow").getAsJsonArray();
        JsonArray sb_up_arrow_coords = COORDS.get("sb_up_arrow").getAsJsonArray();
        cursor.sb_up_arrow = new CursorDetails(sb_up_arrow_coords.get(0).getAsInt(), sb_up_arrow_coords.get(1).getAsInt(),
                sb_up_arrow_hs.get(0).getAsInt(), sb_up_arrow_hs.get(1).getAsInt(),
                sb_up_arrow_coords.get(2).getAsInt(), sb_up_arrow_coords.get(3).getAsInt());

        JsonArray sb_v_double_arrow_hs = HOTSPOTS.get("sb_v_double_arrow").getAsJsonArray();
        JsonArray sb_v_double_arrow_coords = COORDS.get("sb_v_double_arrow").getAsJsonArray();
        cursor.sb_v_double_arrow = new CursorDetails(sb_v_double_arrow_coords.get(0).getAsInt(), sb_v_double_arrow_coords.get(1).getAsInt(),
                sb_v_double_arrow_hs.get(0).getAsInt(), sb_v_double_arrow_hs.get(1).getAsInt(),
                sb_v_double_arrow_coords.get(2).getAsInt(), sb_v_double_arrow_coords.get(3).getAsInt());

        JsonArray tcross_hs = HOTSPOTS.get("tcross").getAsJsonArray();
        JsonArray tcross_coords = COORDS.get("tcross").getAsJsonArray();
        cursor.tcross = new CursorDetails(tcross_coords.get(0).getAsInt(), tcross_coords.get(1).getAsInt(),
                tcross_hs.get(0).getAsInt(), tcross_hs.get(1).getAsInt(),
                tcross_coords.get(2).getAsInt(), tcross_coords.get(3).getAsInt());

        JsonArray top_left_corner_hs = HOTSPOTS.get("top_left_corner").getAsJsonArray();
        JsonArray top_left_corner_coords = COORDS.get("top_left_corner").getAsJsonArray();
        cursor.top_left_corner = new CursorDetails(top_left_corner_coords.get(0).getAsInt(), top_left_corner_coords.get(1).getAsInt(),
                top_left_corner_hs.get(0).getAsInt(), top_left_corner_hs.get(1).getAsInt(),
                top_left_corner_coords.get(2).getAsInt(), top_left_corner_coords.get(3).getAsInt());

        JsonArray top_right_corner_hs = HOTSPOTS.get("top_right_corner").getAsJsonArray();
        JsonArray top_right_corner_coords = COORDS.get("top_right_corner").getAsJsonArray();
        cursor.top_right_corner = new CursorDetails(top_right_corner_coords.get(0).getAsInt(), top_right_corner_coords.get(1).getAsInt(),
                top_right_corner_hs.get(0).getAsInt(), top_right_corner_hs.get(1).getAsInt(),
                top_right_corner_coords.get(2).getAsInt(), top_right_corner_coords.get(3).getAsInt());

        JsonArray top_side_hs = HOTSPOTS.get("top_side").getAsJsonArray();
        JsonArray top_side_coords = COORDS.get("top_side").getAsJsonArray();
        cursor.top_side = new CursorDetails(top_side_coords.get(0).getAsInt(), top_side_coords.get(1).getAsInt(),
                top_side_hs.get(0).getAsInt(), top_side_hs.get(1).getAsInt(),
                top_side_coords.get(2).getAsInt(), top_side_coords.get(3).getAsInt());

        JsonArray top_tee_hs = HOTSPOTS.get("top_tee").getAsJsonArray();
        JsonArray top_tee_coords = COORDS.get("top_tee").getAsJsonArray();
        cursor.top_tee = new CursorDetails(top_tee_coords.get(0).getAsInt(), top_tee_coords.get(1).getAsInt(),
                top_tee_hs.get(0).getAsInt(), top_tee_hs.get(1).getAsInt(),
                top_tee_coords.get(2).getAsInt(), top_tee_coords.get(3).getAsInt());

        JsonArray ul_angle_hs = HOTSPOTS.get("ul_angle").getAsJsonArray();
        JsonArray ul_angle_coords = COORDS.get("ul_angle").getAsJsonArray();
        cursor.ul_angle = new CursorDetails(ul_angle_coords.get(0).getAsInt(), ul_angle_coords.get(1).getAsInt(),
                ul_angle_hs.get(0).getAsInt(), ul_angle_hs.get(1).getAsInt(),
                ul_angle_coords.get(2).getAsInt(), ul_angle_coords.get(3).getAsInt());

        JsonArray ur_angle_hs = HOTSPOTS.get("ur_angle").getAsJsonArray();
        JsonArray ur_angle_coords = COORDS.get("ur_angle").getAsJsonArray();
        cursor.ur_angle = new CursorDetails(ur_angle_coords.get(0).getAsInt(), ur_angle_coords.get(1).getAsInt(),
                ur_angle_hs.get(0).getAsInt(), ur_angle_hs.get(1).getAsInt(),
                ur_angle_coords.get(2).getAsInt(), ur_angle_coords.get(3).getAsInt());

        JsonArray vertical_text_hs = HOTSPOTS.get("vertical-text").getAsJsonArray();
        JsonArray vertical_text_coords = COORDS.get("vertical-text").getAsJsonArray();
        cursor.vertical_text = new CursorDetails(vertical_text_coords.get(0).getAsInt(), vertical_text_coords.get(1).getAsInt(),
                vertical_text_hs.get(0).getAsInt(), vertical_text_hs.get(1).getAsInt(),
                vertical_text_coords.get(2).getAsInt(), vertical_text_coords.get(3).getAsInt());

        JsonArray X_cursor_hs = HOTSPOTS.get("X_cursor").getAsJsonArray();
        JsonArray X_cursor_coords = COORDS.get("X_cursor").getAsJsonArray();
        cursor.X_cursor = new CursorDetails(X_cursor_coords.get(0).getAsInt(), X_cursor_coords.get(1).getAsInt(),
                X_cursor_hs.get(0).getAsInt(), X_cursor_hs.get(1).getAsInt(),
                X_cursor_coords.get(2).getAsInt(), X_cursor_coords.get(3).getAsInt());

        JsonArray xterm_hs = HOTSPOTS.get("xterm").getAsJsonArray();
        JsonArray xterm_coords = COORDS.get("xterm").getAsJsonArray();
        cursor.xterm = new CursorDetails(xterm_coords.get(0).getAsInt(), xterm_coords.get(1).getAsInt(),
                xterm_hs.get(0).getAsInt(), xterm_hs.get(1).getAsInt(),
                xterm_coords.get(2).getAsInt(), xterm_coords.get(3).getAsInt());

        JsonArray zoom_in_hs = HOTSPOTS.get("zoom-in").getAsJsonArray();
        JsonArray zoom_in_coords = COORDS.get("zoom-in").getAsJsonArray();
        cursor.zoom_in = new CursorDetails(zoom_in_coords.get(0).getAsInt(), zoom_in_coords.get(1).getAsInt(),
                zoom_in_hs.get(0).getAsInt(), zoom_in_hs.get(1).getAsInt(),
                zoom_in_coords.get(2).getAsInt(), zoom_in_coords.get(3).getAsInt());

        JsonArray zoom_out_hs = HOTSPOTS.get("zoom-out").getAsJsonArray();
        JsonArray zoom_out_coords = COORDS.get("zoom-out").getAsJsonArray();
        cursor.zoom_out = new CursorDetails(zoom_out_coords.get(0).getAsInt(), zoom_out_coords.get(1).getAsInt(),
                zoom_out_hs.get(0).getAsInt(), zoom_out_hs.get(1).getAsInt(),
                zoom_out_coords.get(2).getAsInt(), zoom_out_coords.get(3).getAsInt());

        JsonArray watch_hs = HOTSPOTS_ANIMATED.get("watch").getAsJsonArray();
        ArrayList<CursorDetails> watch_coords = new ArrayList<>();

        JsonArray left_ptr_watch_hs = HOTSPOTS_ANIMATED.get("left_ptr_watch").getAsJsonArray();
        ArrayList<CursorDetails> left_ptr_watch_coords = new ArrayList<>();

        for(Map.Entry<String,JsonElement> entry : COORDS.entrySet())
        {
            String name = entry.getKey();
            JsonArray coords = entry.getValue().getAsJsonArray();

            if(name.startsWith("watch_"))
            {
                int ind = Integer.parseInt(name.substring(6)) - 1;

                while(watch_coords.size() < ind + 1)
                    watch_coords.add(null);

                watch_coords.set(ind, new CursorDetails(coords.get(0).getAsInt(), coords.get(1).getAsInt(),
                        watch_hs.get(0).getAsInt(), watch_hs.get(1).getAsInt(),
                        coords.get(2).getAsInt(), coords.get(3).getAsInt()));
            }
            else if(name.startsWith("left_ptr_watch_"))
            {
                int ind = Integer.parseInt(name.substring(15)) - 1;

                while(left_ptr_watch_coords.size() < ind + 1)
                    left_ptr_watch_coords.add(null);

                left_ptr_watch_coords.set(ind, new CursorDetails(coords.get(0).getAsInt(), coords.get(1).getAsInt(),
                        left_ptr_watch_hs.get(0).getAsInt(), left_ptr_watch_hs.get(1).getAsInt(),
                        coords.get(2).getAsInt(), coords.get(3).getAsInt()));
            }
        }

        cursor.watch = new CursorDetailsAnim(watch_hs.get(2).getAsInt(), watch_coords);
        cursor.left_ptr_watch = new CursorDetailsAnim(left_ptr_watch_hs.get(2).getAsInt(), left_ptr_watch_coords);

        return cursor;
    }

    public static Cursor fromJSON(String[] path) throws IOException {
        BufferedReader json = new BufferedReader(new InputStreamReader(path[1].equalsIgnoreCase("jar") ? Thread.currentThread().getContextClassLoader().getResourceAsStream(path[0] + "/config.json") : Files.newInputStream(Paths.get(path[0] + "/config.json"))));

        Cursor cursor = fromJSON(JsonParser.parseReader(json).getAsJsonObject());
        cursor.path = path;
        return cursor;
    }

    public void updateCursors(float scale)
    {
        try {
            SVGLoader loader = new SVGLoader();
            InputStream svg = path[1].equalsIgnoreCase("jar") ? Thread.currentThread().getContextClassLoader().getResourceAsStream(path[0] + "/atlas.svg") : Files.newInputStream(Paths.get(path[0] + "/atlas.svg"));
            SVGDocument svgDocument = loader.load(svg);

            FloatSize size = svgDocument.size();
            BufferedImage atlas = new BufferedImage((int) (size.width * scale), (int) (size.height * scale), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = atlas.createGraphics();

            if(aa)
            {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g.setRenderingHint(SVGRenderingHints.KEY_IMAGE_ANTIALIASING, SVGRenderingHints.VALUE_IMAGE_ANTIALIASING_ON);
            }

            svgDocument.render(null, g, new ViewBox((int) (size.width * scale), (int) (size.height * scale)));
            g.dispose();

            all_scroll.setupCursorImage(atlas, scale);
            bd_double_arrow.setupCursorImage(atlas, scale);
            bottom_left_corner.setupCursorImage(atlas, scale);
            bottom_right_corner.setupCursorImage(atlas, scale);
            bottom_side.setupCursorImage(atlas, scale);
            bottom_tee.setupCursorImage(atlas, scale);
            circle.setupCursorImage(atlas, scale);
            context_menu.setupCursorImage(atlas, scale);
            copy.setupCursorImage(atlas, scale);
            cross.setupCursorImage(atlas, scale);
            crossed_circle.setupCursorImage(atlas, scale);
            crosshair.setupCursorImage(atlas, scale);
            dnd_ask.setupCursorImage(atlas, scale);
            dnd_copy.setupCursorImage(atlas, scale);
            dnd_link.setupCursorImage(atlas, scale);
            dnd_move.setupCursorImage(atlas, scale);
            dnd_no_drop.setupCursorImage(atlas, scale);
            dnd_none.setupCursorImage(atlas, scale);
            dotbox.setupCursorImage(atlas, scale);
            fd_double_arrow.setupCursorImage(atlas, scale);
            grabbing.setupCursorImage(atlas, scale);
            hand1.setupCursorImage(atlas, scale);
            hand2.setupCursorImage(atlas, scale);
            left_ptr.setupCursorImage(atlas, scale);
            left_side.setupCursorImage(atlas, scale);
            left_tee.setupCursorImage(atlas, scale);
            link.setupCursorImage(atlas, scale);
            ll_angle.setupCursorImage(atlas, scale);
            lr_angle.setupCursorImage(atlas, scale);
            move.setupCursorImage(atlas, scale);
            pencil.setupCursorImage(atlas, scale);
            plus.setupCursorImage(atlas, scale);
            pointer_move.setupCursorImage(atlas, scale);
            question_arrow.setupCursorImage(atlas, scale);
            right_ptr.setupCursorImage(atlas, scale);
            right_side.setupCursorImage(atlas, scale);
            right_tee.setupCursorImage(atlas, scale);
            sb_down_arrow.setupCursorImage(atlas, scale);
            sb_h_double_arrow.setupCursorImage(atlas, scale);
            sb_left_arrow.setupCursorImage(atlas, scale);
            sb_right_arrow.setupCursorImage(atlas, scale);
            sb_up_arrow.setupCursorImage(atlas, scale);
            sb_v_double_arrow.setupCursorImage(atlas, scale);
            tcross.setupCursorImage(atlas, scale);
            top_left_corner.setupCursorImage(atlas, scale);
            top_right_corner.setupCursorImage(atlas, scale);
            top_side.setupCursorImage(atlas, scale);
            top_tee.setupCursorImage(atlas, scale);
            ul_angle.setupCursorImage(atlas, scale);
            ur_angle.setupCursorImage(atlas, scale);
            vertical_text.setupCursorImage(atlas, scale);
            X_cursor.setupCursorImage(atlas, scale);
            xterm.setupCursorImage(atlas, scale);
            zoom_in.setupCursorImage(atlas, scale);
            zoom_out.setupCursorImage(atlas, scale);

            watch.setupCursorImage(atlas, scale);
            left_ptr_watch.setupCursorImage(atlas, scale);
        } catch (Exception e) {e.printStackTrace();}
    }


    private long lastMillis = 0;
    private int waitIndex = 0;

    public CursorDetails getCursor(ECursor cursor) {

        switch (cursor)
        {
            case all_scroll: waitIndex = 0; return all_scroll;
            case bd_double_arrow: waitIndex = 0; return bd_double_arrow;
            case bottom_left_corner: waitIndex = 0; return bottom_left_corner;
            case bottom_right_corner: waitIndex = 0; return bottom_right_corner;
            case bottom_side: waitIndex = 0; return bottom_side;
            case bottom_tee: waitIndex = 0; return bottom_tee;
            case circle: waitIndex = 0; return circle;
            case context_menu: waitIndex = 0; return context_menu;
            case copy: waitIndex = 0; return copy;
            case cross: waitIndex = 0; return cross;
            case crossed_circle: waitIndex = 0; return crossed_circle;
            case crosshair: waitIndex = 0; return crosshair;
            case dnd_ask: waitIndex = 0; return dnd_ask;
            case dnd_copy: waitIndex = 0; return dnd_copy;
            case dnd_link: waitIndex = 0; return dnd_link;
            case dnd_move: waitIndex = 0; return dnd_move;
            case dnd_no_drop: waitIndex = 0; return dnd_no_drop;
            case dnd_none: waitIndex = 0; return dnd_none;
            case dotbox: waitIndex = 0; return dotbox;
            case fd_double_arrow: waitIndex = 0; return fd_double_arrow;
            case grabbing: waitIndex = 0; return grabbing;
            case hand1: waitIndex = 0; return hand1;
            case hand2: waitIndex = 0; return hand2;
            case left_ptr: waitIndex = 0; return left_ptr;
            case left_side: waitIndex = 0; return left_side;
            case left_tee: waitIndex = 0; return left_tee;
            case link: waitIndex = 0; return link;
            case ll_angle: waitIndex = 0; return ll_angle;
            case lr_angle: waitIndex = 0; return lr_angle;
            case move: waitIndex = 0; return move;
            case pencil: waitIndex = 0; return pencil;
            case plus: waitIndex = 0; return plus;
            case pointer_move: waitIndex = 0; return pointer_move;
            case question_arrow: waitIndex = 0; return question_arrow;
            case right_ptr: waitIndex = 0; return right_ptr;
            case right_side: waitIndex = 0; return right_side;
            case right_tee: waitIndex = 0; return right_tee;
            case sb_down_arrow: waitIndex = 0; return sb_down_arrow;
            case sb_h_double_arrow: waitIndex = 0; return sb_h_double_arrow;
            case sb_left_arrow: waitIndex = 0; return sb_left_arrow;
            case sb_right_arrow: waitIndex = 0; return sb_right_arrow;
            case sb_up_arrow: waitIndex = 0; return sb_up_arrow;
            case sb_v_double_arrow: waitIndex = 0; return sb_v_double_arrow;
            case tcross: waitIndex = 0; return tcross;
            case top_left_corner: waitIndex = 0; return top_left_corner;
            case top_right_corner: waitIndex = 0; return top_right_corner;
            case top_side: waitIndex = 0; return top_side;
            case top_tee: waitIndex = 0; return top_tee;
            case ul_angle: waitIndex = 0; return ul_angle;
            case ur_angle: waitIndex = 0; return ur_angle;
            case vertical_text: waitIndex = 0; return vertical_text;
            case X_cursor: waitIndex = 0; return X_cursor;
            case xterm: waitIndex = 0; return xterm;
            case zoom_in: waitIndex = 0; return zoom_in;
            case zoom_out: waitIndex = 0; return zoom_out;

            case watch:
                if(watch.ms < System.currentTimeMillis() - lastMillis)
                {
                    lastMillis = System.currentTimeMillis();
                    waitIndex++;

                    if(waitIndex > watch.cursors.size() - 1)
                        waitIndex = 0;
                }
                return watch.cursors.get(waitIndex);
            case left_ptr_watch:
                if(left_ptr_watch.ms < System.currentTimeMillis() - lastMillis)
                {
                    lastMillis = System.currentTimeMillis();
                    waitIndex++;

                    if(waitIndex > left_ptr_watch.cursors.size() - 1)
                        waitIndex = 0;
                }
                return left_ptr_watch.cursors.get(waitIndex);
        }

        return left_ptr;
    }
}