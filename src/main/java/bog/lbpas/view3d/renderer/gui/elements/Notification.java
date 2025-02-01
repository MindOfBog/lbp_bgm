package bog.lbpas.view3d.renderer.gui.elements;

import bog.lbpas.view3d.core.Model;
import bog.lbpas.view3d.mainWindow.ConstantTextures;
import bog.lbpas.view3d.managers.MouseInput;
import bog.lbpas.view3d.managers.RenderMan;
import bog.lbpas.view3d.managers.WindowMan;
import bog.lbpas.view3d.managers.assetLoading.ObjectLoader;
import bog.lbpas.view3d.renderer.gui.ingredients.LineStrip;
import bog.lbpas.view3d.utils.Config;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public abstract class Notification extends Element{

    private int fontSize;
    private Model outlineRect;
    private float prevHeight = 0;
    private ButtonImage closeButton;

    public long time = -1;
    private int lineLimit = 5;

    public Notification() {
    }

    public Notification(int lineLimit) {
        this.lineLimit = lineLimit;
    }

    public Notification(long time) {
        this.time = System.currentTimeMillis() + time;
    }

    public Notification(long time, int lineLimit) {
        this.time = System.currentTimeMillis() + time;
        this.lineLimit = lineLimit;
    }

    public void init(String id, Vector2f pos, float width, int fontSize, RenderMan renderer, ObjectLoader loader, WindowMan window) {
        this.id = id;
        this.pos = pos;
        this.size = new Vector2f(width, 0);
        this.fontSize = fontSize;
        this.renderer = renderer;
        this.loader = loader;
        this.window = window;

        Vector2f position = this.pos;
        Vector2f dims = this.size;
        closeButton = new ButtonImage("closeButton", ConstantTextures.getTexture(ConstantTextures.WINDOW_CLOSE, 23, 23, loader), new Vector2f(0), new Vector2f(22, 22), new Vector2f(23, 23), renderer, loader, window) {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)
                    closeNotification();
            }

            @Override
            public void draw(MouseInput mouseInput, boolean overOther) {
                this.pos.x = position.x + dims.x - 22 - 3;
                this.pos.y = position.y + 3;
                super.draw(mouseInput, overOther);
            }

            @Override
            public Color[] getColors(MouseInput mouseInput, boolean overOther)
            {
                if(isClicked)
                    return new Color[]{backgroundColor(), outlineColor()};
                else if(isMouseOverElement(mouseInput) && !overOther)
                    return new Color[]{new Color(backgroundColor().getRGB()).darker(), new Color(outlineColor().getRGB()).darker()};
                else
                    return new Color[]{backgroundColor(), outlineColor()};
            }
        };
    }

    @Override
    public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement) {
        super.onClick(mouseInput, pos, button, action, mods, overElement);
        closeButton.onClick(mouseInput, pos, button, action, mods, overElement);
    }

    @Override
    public void draw(MouseInput mouseInput, boolean overElement) {
        super.draw(mouseInput, overElement);

        if(this.outlineRect == null)
            this.outlineRect = LineStrip.processVerts(LineStrip.getRectangle(size), loader, window);

        if (time != -1 && System.currentTimeMillis() > time) {
            closeNotification();
            return;
        }

        int titleFontSize = (int) (fontSize * 1.3f);

        renderer.doBlur(1.0025f, (int) pos.x, (int) pos.y, (int) size.x, (int) size.y);
        renderer.doBlur(2, (int) pos.x, (int) pos.y, (int) size.x, (int) size.y);
        renderer.doBlur(3, (int) pos.x, (int) pos.y, (int) size.x, (int) size.y);
        renderer.doBlur(2, (int) pos.x, (int) pos.y, (int) size.x, (int) size.y);
        renderer.doBlur(1.5f, (int) pos.x, (int) pos.y, (int) size.x, (int) size.y);
        renderer.doBlur(1.25f, (int) pos.x, (int) pos.y, (int) size.x, (int) size.y);

        renderer.startScissor((int) pos.x, (int) pos.y, (int) size.x, (int) size.y);

        renderer.drawRect((int) pos.x, (int) pos.y, (int) size.x, (int) size.y, backgroundColor());
        renderer.drawString(getTitle(), Config.FONT_COLOR, (int) (pos.x + getFontHeight(fontSize) / 2), (int) (pos.y + getFontHeight(fontSize) / 2), titleFontSize);

        String content = getContent();
        if(content != null && !content.isEmpty() && !content.equalsIgnoreCase("") && !content.equalsIgnoreCase(" "))
        {
            String[] lines = content.split(String.valueOf((char) 10));

            if (lines.length > lineLimit) {
                String[] newlines = new String[lineLimit + 1];

                for (int i = 0; i < lineLimit + 1; i++)
                    newlines[i] = lines[i];

                newlines[lineLimit] = "...";
                lines = newlines;
            }

            size.y = getFontHeight(fontSize) / 2 + (getFontHeight(fontSize) + 2) * lines.length + (getFontHeight(titleFontSize) + 2);

            for (int o = 0; o < lines.length; o++) {
                String line = lines[o];
                renderer.drawString(line, Config.FONT_COLOR, (int) (pos.x + getFontHeight(fontSize) / 2), (int) (pos.y + getFontHeight(fontSize) / 2 + (getFontHeight(fontSize) + 2) * o + (getFontHeight(titleFontSize) + 2)), fontSize);
            }
        }
        else
            size.y = getFontHeight(fontSize) + getFontHeight(titleFontSize);

        if (prevHeight != size.y) {
            refreshOutline();
            prevHeight = size.y;
        }

        closeButton.draw(mouseInput, overElement);

        renderer.endScissor();
        renderer.drawRectOutline(pos, outlineRect, outlineColor(), false);
    }

    @Override
    public void resize() {
        super.resize();
        refreshOutline();
        closeButton.resize();
    }

    public void forceRefreshOutline()
    {
        prevHeight = 0;
    }

    public void refreshOutline()
    {
        if(this.outlineRect != null)
            this.outlineRect.cleanup(loader);
        this.outlineRect = LineStrip.processVerts(LineStrip.getRectangle(size), loader, window);
    }

    public abstract String getTitle();
    public abstract String getContent();
    public abstract void closeNotification();
    public abstract Color backgroundColor();
    public abstract Color outlineColor();
}
