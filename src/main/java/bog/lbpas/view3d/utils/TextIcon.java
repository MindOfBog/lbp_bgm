package bog.lbpas.view3d.utils;

import javax.swing.*;
import java.awt.*;

public class TextIcon implements Icon {
    private final String text;
    private final Color color;
    private final Font font;

    private final int iconWidth;
    private final int iconHeight;

    public TextIcon(String text, Color color, int fontSize, int iconWidth, int iconHeight) {
        this.text = text;
        this.color = color;
        this.font = new Font("SansSerif", Font.BOLD, fontSize);
        this.iconWidth = iconWidth;
        this.iconHeight = iconHeight;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setColor(color);
        g2.setFont(font);

        FontMetrics fm = g2.getFontMetrics();
        int textX = x + (getIconWidth() - fm.stringWidth(text)) / 2;
        int textY = y + ((getIconHeight() - fm.getHeight()) / 2) + fm.getAscent();

        g2.drawString(text, textX, textY);
        g2.dispose();
    }

    @Override public int getIconWidth() { return iconWidth; }
    @Override public int getIconHeight() { return iconHeight; }
}
