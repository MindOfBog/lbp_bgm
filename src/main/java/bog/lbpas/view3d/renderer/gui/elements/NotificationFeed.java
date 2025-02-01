package bog.lbpas.view3d.renderer.gui.elements;

import bog.lbpas.view3d.managers.MouseInput;
import bog.lbpas.view3d.managers.RenderMan;
import bog.lbpas.view3d.managers.WindowMan;
import bog.lbpas.view3d.managers.assetLoading.ObjectLoader;
import org.joml.Vector2d;
import org.joml.Vector2f;

import java.util.ArrayList;

public class NotificationFeed extends Element{

    boolean downwardFeed = true;
    public ArrayList<Notification> notifications;

    public NotificationFeed(boolean downwardFeed, Vector2f pos, float width, RenderMan renderer, ObjectLoader loader, WindowMan window) {
        this.downwardFeed = downwardFeed;
        this.pos = pos;
        this.size = new Vector2f(width);
        this.notifications = new ArrayList<>();
        this.renderer = renderer;
        this.loader = loader;
        this.window = window;
    }

    public NotificationFeed(Vector2f pos, float width, RenderMan renderer, ObjectLoader loader, WindowMan window) {
        this.pos = pos;
        this.size = new Vector2f(width);
        this.notifications = new ArrayList<>();
        this.renderer = renderer;
        this.loader = loader;
        this.window = window;
    }

    public void pushNotification(Notification notification)
    {
        if(notifications.contains(notification))
            return;
        notification.init("notif-" + notifications.size(), new Vector2f(this.pos.x, 0), this.size.x, 10, this.renderer, this.loader, this.window);
        notifications.add(notification);
    }

    @Override
    public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement) {
        super.onClick(mouseInput, pos, button, action, mods, overElement);

        for(int i = notifications.size() - 1; i >= 0; i--) {
            Notification n = notifications.get(i);
            n.onClick(mouseInput, pos, button, action, mods, overElement);
        }
    }

    @Override
    public void draw(MouseInput mouseInput, boolean overElement) {
        super.draw(mouseInput, overElement);

        int yOffset = 0;

        for(int i = notifications.size() - 1; i >= 0; i--)
        {
            Notification n = notifications.get(i);
            n.pos.y = this.pos.y;
            n.pos.x = this.pos.x;

            if(downwardFeed)
                n.pos.y += yOffset;
            else
                n.pos.y -= yOffset + n.size.y;

            n.draw(mouseInput, overElement);
            yOffset += n.size.y + 3;
        }
    }

    @Override
    public void resize() {
        super.resize();

        for(int i = notifications.size() - 1; i >= 0; i--) {
            Notification n = notifications.get(i);
            n.resize();
        }
    }
}
