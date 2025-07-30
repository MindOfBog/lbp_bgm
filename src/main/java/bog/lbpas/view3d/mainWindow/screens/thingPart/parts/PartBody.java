package bog.lbpas.view3d.mainWindow.screens.thingPart.parts;

import bog.lbpas.view3d.core.types.Thing;
import bog.lbpas.view3d.mainWindow.View3D;
import bog.lbpas.view3d.managers.MouseInput;
import bog.lbpas.view3d.renderer.gui.elements.*;
import com.github.weisj.jsvg.nodes.text.Text;
import cwlib.enums.ResourceType;
import cwlib.structs.things.parts.PBody;
import cwlib.structs.things.parts.PRef;
import cwlib.types.data.ResourceDescriptor;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

/**
 * @author Bog
 */
public abstract class PartBody extends iPart {

    public PartBody(int tabWidth, float comboWidth, float panelHeight, float closeWidth, float finalGap, Element tab, View3D view) {
        super(cwlib.enums.Part.BODY, "PBody", "Body", tabWidth, comboWidth, panelHeight, closeWidth, finalGap, tab, view);
    }

    Textbox posVelX;
    Textbox posVelY;
    Textbox posVelZ;

    Textbox angVel;

    Checkbox frozen;

    @Override
    public void init(View3D view) {

        partComboBox.addString("posVel", "Positional Velocity:");

        Panel posVelPanel = partComboBox.addPanel("posVelPanel");

        posVelPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("x", "X:", view.renderer), 0.25f / 3f));
        posVelX = new Textbox("posVelX", view.renderer, view.loader, view.window);
        posVelPanel.elements.add(new Panel.PanelElement(posVelX, 0.75f / 3f));

        posVelPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("y", "Y:", view.renderer), 0.25f / 3f));
        posVelY = new Textbox("posVelY", view.renderer, view.loader, view.window);
        posVelPanel.elements.add(new Panel.PanelElement(posVelY, 0.75f / 3f));

        posVelPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("z", "Z:", view.renderer), 0.25f / 3f));
        posVelZ = new Textbox("posVelZ", view.renderer, view.loader, view.window);
        posVelPanel.elements.add(new Panel.PanelElement(posVelZ, 0.75f / 3f));

        Panel angVelPanel = partComboBox.addPanel("angVelPanel");
        angVelPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("angVel", "Angular Velocity:", view.renderer), 0.65f));
        angVel = new Textbox("angVel", view.renderer, view.loader, view.window);
        angVelPanel.elements.add(new Panel.PanelElement(angVel, 0.35f));

        frozen = partComboBox.addCheckbox("frozen", "Frozen", new Checkbox()
        {
            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement, boolean focusedOther) {
                super.onClick(mouseInput, pos, button, action, mods, overElement, focusedOther);

                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos) && !overElement)
                    for(Thing thing : view.things)
                        if(thing.thing.hasPart(part) && thing.selected)
                            ((PBody)thing.thing.getPart(part)).frozen = this.isChecked ? 513 : 1;
            }
        });
    }

    @Override
    public void addValues(ArrayList<Integer> selected, ArrayList<Thing> things) {

        float posVelX = Float.NEGATIVE_INFINITY;
        float posVelY = Float.NEGATIVE_INFINITY;
        float posVelZ = Float.NEGATIVE_INFINITY;
        float angVel = Float.NEGATIVE_INFINITY;
        int frozen = -1;

        for(int i : selected)
            if(things.get(i).thing.hasPart(part))
            {
                PBody body = ((PBody) things.get(i).thing.getPart(part));

                if(body.posVel == null)
                    body.posVel = new Vector3f();

                float pVelX = body.posVel.x;
                if(Float.isInfinite(posVelX))
                    posVelX = pVelX;
                else if(posVelX != pVelX)
                    posVelX = Float.NaN;

                float pVelY = body.posVel.y;
                if(Float.isInfinite(posVelY))
                    posVelY = pVelY;
                else if(posVelY != pVelY)
                    posVelY = Float.NaN;

                float pVelZ = body.posVel.z;
                if(Float.isInfinite(posVelZ))
                    posVelZ = pVelZ;
                else if(posVelZ != pVelZ)
                    posVelZ = Float.NaN;

                float aVel = body.angVel;
                if(Float.isInfinite(angVel))
                    angVel = aVel;
                else if(angVel != aVel)
                    angVel = Float.NaN;

                if(frozen == -1)
                    frozen = body.frozen == 513 ? 1 : 0;
                else if(frozen != (body.frozen == 513 ? 1 : 0))
                    frozen = 0;
            }

        Vector2f posVeX = this.posVelX.setTextboxValueFloat(posVelX);
        Vector2f posVeY = this.posVelY.setTextboxValueFloat(posVelY);
        Vector2f posVeZ = this.posVelZ.setTextboxValueFloat(posVelZ);

        Vector2f angVe = this.angVel.setTextboxValueFloat(angVel);

        this.frozen.isChecked = frozen == 1;

        for(int i : selected)
            if(things.get(i).thing.hasPart(part))
            {
                PBody body = ((PBody) things.get(i).thing.getPart(part));

                if(posVeX.y == 1)
                    body.posVel.x = (int) posVeX.x;
                if(posVeY.y == 1)
                    body.posVel.y = (int) posVeY.x;
                if(posVeZ.y == 1)
                    body.posVel.z = (int) posVeZ.x;

                if(angVe.y == 1)
                    body.angVel = (int) angVe.x;
            }
    }
}