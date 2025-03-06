package bog.lbpas.view3d.mainWindow.screens.thingPart.parts;

import bog.lbpas.view3d.core.types.Thing;
import bog.lbpas.view3d.mainWindow.View3D;
import bog.lbpas.view3d.managers.MouseInput;
import bog.lbpas.view3d.renderer.gui.elements.Checkbox;
import bog.lbpas.view3d.renderer.gui.elements.DropDownTab;
import bog.lbpas.view3d.renderer.gui.elements.Panel;
import bog.lbpas.view3d.renderer.gui.elements.Textbox;
import cwlib.enums.ResourceType;
import cwlib.structs.things.parts.PRef;
import cwlib.structs.things.parts.PTrigger;
import cwlib.types.data.ResourceDescriptor;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

/**
 * @author Bog
 */
public abstract class PartRef extends iPart {

    public PartRef(int tabWidth, float comboWidth, float panelHeight, float closeWidth, float finalGap, DropDownTab tab, View3D view) {
        super(cwlib.enums.Part.REF, "PRef", "Ref", tabWidth, comboWidth, panelHeight, closeWidth, finalGap, tab, view);
    }

    Textbox plan;
    Textbox oldLifetime;
    Textbox oldAliveFrames;
    Checkbox childrenSelectable;
    Checkbox stripChildren;

    @Override
    public void init(View3D view) {

        Panel planPanel = partComboBox.addPanel("planPanel");
        planPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("planStr", "Plan:", 10, view.renderer), 0.5f));
        plan = new Textbox("plan", 10, view.renderer, view.loader, view.window);
        planPanel.elements.add(new Panel.PanelElement(plan, 0.5f));

        Panel oldLifetimePanel = partComboBox.addPanel("oldLifetimePanel");
        oldLifetimePanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("oldLifetimeStr", "Lifetime:", 10, view.renderer), 0.7f));
        oldLifetime = new Textbox("oldLifetime", 10, view.renderer, view.loader, view.window);
        oldLifetimePanel.elements.add(new Panel.PanelElement(oldLifetime, 0.3f));

        Panel oldAliveFramesPanel = partComboBox.addPanel("oldAliveFramesPanel");
        oldAliveFramesPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("oldAliveFramesStr", "Alive Frames:", 10, view.renderer), 0.7f));
        oldAliveFrames = new Textbox("oldAliveFrames", 10, view.renderer, view.loader, view.window);
        oldAliveFramesPanel.elements.add(new Panel.PanelElement(oldAliveFrames, 0.3f));

        childrenSelectable = partComboBox.addCheckbox("childrenSelectable", "Children Selectable", new Checkbox()
        {
            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement) {
                super.onClick(mouseInput, pos, button, action, mods, overElement);

                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos) && !overElement)
                    for(Thing thing : view.things)
                        if(thing.thing.hasPart(part) && thing.selected)
                            ((PRef)thing.thing.getPart(part)).childrenSelectable = this.isChecked;
            }
        });
        stripChildren = partComboBox.addCheckbox("stripChildren", "Strip Children", new Checkbox()
        {
            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement) {
                super.onClick(mouseInput, pos, button, action, mods, overElement);

                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos) && !overElement)
                    for(Thing thing : view.things)
                        if(thing.thing.hasPart(part) && thing.selected)
                            ((PRef)thing.thing.getPart(part)).stripChildren = this.isChecked;
            }
        });
    }

    @Override
    public void addValues(ArrayList<Integer> selected, ArrayList<Thing> things) {

        float oldLifetime = Float.NEGATIVE_INFINITY;
        float oldAliveFrames = Float.NEGATIVE_INFINITY;
        int childrenSelectable = -1;
        int stripChildren = -1;
        String plan = null;

        for(int i : selected)
            if(things.get(i).thing.hasPart(part))
            {
                PRef ref = ((PRef) things.get(i).thing.getPart(part));

                float oldLif = ref.oldLifetime;
                if(Float.isInfinite(oldLifetime))
                    oldLifetime = oldLif;
                else if(oldLifetime != oldLif)
                    oldLifetime = Float.NaN;

                float oldAliveFra = ref.oldAliveFrames;
                if(Float.isInfinite(oldAliveFrames))
                    oldAliveFrames = oldAliveFra;
                else if(oldAliveFrames != oldAliveFra)
                    oldAliveFrames = Float.NaN;

                if(childrenSelectable == -1)
                    childrenSelectable = ref.childrenSelectable ? 1 : 0;
                else if(childrenSelectable != (ref.childrenSelectable ? 1 : 0))
                    childrenSelectable = 0;

                if(stripChildren == -1)
                    stripChildren = ref.stripChildren ? 1 : 0;
                else if(stripChildren != (ref.stripChildren ? 1 : 0))
                    stripChildren = 0;

                String pln = ref.plan == null ? "" : ref.plan.isGUID() ? ref.plan.getGUID().toString() : ref.plan.getSHA1().toString();
                if(plan == null)
                    plan = pln;
                else if(!plan.equalsIgnoreCase(pln))
                    plan = "";
            }

        Vector2f oldLif = this.oldLifetime.setTextboxValueFloat(oldLifetime);
        Vector2f oldAliveFra = this.oldAliveFrames.setTextboxValueFloat(oldAliveFrames);

        this.childrenSelectable.isChecked = childrenSelectable == 1;
        this.stripChildren.isChecked = stripChildren == 1;

        String pln = this.plan.setTextboxValueString(plan);

        for(int i : selected)
            if(things.get(i).thing.hasPart(part))
            {
                PRef ref = ((PRef) things.get(i).thing.getPart(part));

                if(oldLif.y == 1)
                    ref.oldLifetime = (int) oldLif.x;
                if(oldAliveFra.y == 1)
                    ref.oldAliveFrames = (int) oldAliveFra.x;

                if(pln != null)
                {
                    ref.plan = null;
                    try{ref.plan = new ResourceDescriptor(pln.trim(), ResourceType.PLAN);}catch (Exception e){}
                }
            }
    }
}
