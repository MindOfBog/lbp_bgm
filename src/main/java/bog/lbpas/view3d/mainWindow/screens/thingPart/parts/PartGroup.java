package bog.lbpas.view3d.mainWindow.screens.thingPart.parts;

import bog.lbpas.view3d.core.types.Thing;
import bog.lbpas.view3d.mainWindow.View3D;
import bog.lbpas.view3d.managers.MouseInput;
import bog.lbpas.view3d.renderer.gui.elements.*;
import bog.lbpas.view3d.utils.Consts;
import bog.lbpas.view3d.utils.Utils;
import cwlib.enums.GroupFlags;
import cwlib.enums.Part;
import cwlib.enums.ResourceType;
import cwlib.structs.things.parts.PGroup;
import cwlib.types.data.NetworkPlayerID;
import cwlib.types.data.ResourceDescriptor;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

/**
 * @author Bog
 */
public abstract class PartGroup extends iPart {

    public PartGroup(int tabWidth, float comboWidth, float panelHeight, float closeWidth, float finalGap, DropDownTab tab, View3D view) {
        super(cwlib.enums.Part.GROUP, "PGroup", "Group", tabWidth, comboWidth, panelHeight, closeWidth, finalGap, tab, view);
    }

    Textbox plan;
    Textbox creator;

    Checkbox copyright;
    Checkbox editable;
    Checkbox pickupAllMembers;

    Textbox lifetime;
    Textbox aliveFrames;
    ComboBox emitter;

    @Override
    public void init(View3D view) {

        Panel planPanel = partComboBox.addPanel("planPanel");
        planPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("planStr", "Plan:", 10, view.renderer), 0.4f));
        plan = new Textbox("plan", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window);
        planPanel.elements.add(new Panel.PanelElement(plan, 0.6f));

        Panel creatorPanel = partComboBox.addPanel("creatorPanel");
        creatorPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("creatorStr", "Creator:", 10, view.renderer), 0.4f));
        creator = new Textbox("creator", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window);
        creatorPanel.elements.add(new Panel.PanelElement(creator, 0.6f));

        copyright = partComboBox.addCheckbox("copyright", "Copyright", new Checkbox()
        {
            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement) {
                super.onClick(mouseInput, pos, button, action, mods, overElement);

                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos) && !overElement)
                    for (int i = 0; i < view.things.size(); i++)
                        if (view.things.get(i).selected)
                        {
                            bog.lbpas.view3d.core.types.Thing thing = (bog.lbpas.view3d.core.types.Thing) view.things.get(i);
                            PGroup group = thing.thing.getPart(Part.GROUP);

                            group.flags = Utils.setBitwiseBool(group.flags, GroupFlags.COPYRIGHT, this.isChecked);
                        }
            }
        });
        editable = partComboBox.addCheckbox("editable", "Editable", new Checkbox()
        {
            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement) {
                super.onClick(mouseInput, pos, button, action, mods, overElement);

                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos) && !overElement)
                    for (int i = 0; i < view.things.size(); i++)
                        if (view.things.get(i).selected)
                        {
                            bog.lbpas.view3d.core.types.Thing thing = (bog.lbpas.view3d.core.types.Thing) view.things.get(i);
                            PGroup group = thing.thing.getPart(Part.GROUP);

                            group.flags = Utils.setBitwiseBool(group.flags, GroupFlags.EDITABLE, this.isChecked);
                        }
            }
        });
        pickupAllMembers = partComboBox.addCheckbox("pickupAllMembers", "Pickup all members", new Checkbox()
        {
            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement) {
                super.onClick(mouseInput, pos, button, action, mods, overElement);

                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos) && !overElement)
                    for (int i = 0; i < view.things.size(); i++)
                        if (view.things.get(i).selected)
                        {
                            bog.lbpas.view3d.core.types.Thing thing = (bog.lbpas.view3d.core.types.Thing) view.things.get(i);
                            PGroup group = thing.thing.getPart(Part.GROUP);

                            group.flags = Utils.setBitwiseBool(group.flags, GroupFlags.PICKUP_ALL_MEMBERS, this.isChecked);
                        }
            }
        });

        Panel emitterPanel = partComboBox.addPanel("emitterPanel");
        emitterPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("emitterStr", "Emitter:", 10, view.renderer), 0.55f));
        emitter = new ComboBox("emitter", "None", new Vector2f(), new Vector2f(), 10, 250, view.renderer, view.loader, view.window);

        emitter.addButton("nullEmitter", "None", new Button() {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)
                    for (int i = 0; i < view.things.size(); i++)
                        if (view.things.get(i).selected)
                        {
                            bog.lbpas.view3d.core.types.Thing thing = (bog.lbpas.view3d.core.types.Thing) view.things.get(i);
                            PGroup group = thing.thing.getPart(Part.GROUP);

                            group.emitter = null;
                            emitter.tabTitle = "None";
                        }
            }
        });

        Panel searchPanel = emitter.addPanel("thingsPanel");
        searchPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("searchStr", "Search:", 10, view.renderer), 0.3f));
        Textbox searchAllThings = new Textbox("searchAllThings", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window);
        searchPanel.elements.add(new Panel.PanelElement(searchAllThings, 0.7f));

        emitter.addList("thingsList", new ButtonList(view.things, 10, view.renderer, view.loader, view.window) {
            @Override
            public void clickedButton(Object object, int index, int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)
                    for (int i = 0; i < view.things.size(); i++)
                        if (view.things.get(i).selected)
                        {
                            bog.lbpas.view3d.core.types.Thing thing = (bog.lbpas.view3d.core.types.Thing) view.things.get(i);
                            PGroup group = thing.thing.getPart(Part.GROUP);

                            group.emitter = ((bog.lbpas.view3d.core.types.Thing)object).thing;
                            emitter.tabTitle = group.emitter.name;
                        }
            }

            int hovering = -1;

            @Override
            public void hoveringButton(Object object, int index) {
                hovering = index;
            }

            @Override
            public boolean isHighlighted(Object object, int index) {
                return hovering == index;
            }

            @Override
            public boolean isSelected(Object object, int index) {
                bog.lbpas.view3d.core.types.Thing th = ((bog.lbpas.view3d.core.types.Thing)object);

                boolean bool = true;

                for (int i = 0; i < view.things.size(); i++)
                    if (view.things.get(i).selected)
                    {
                        bog.lbpas.view3d.core.types.Thing thing = (bog.lbpas.view3d.core.types.Thing) view.things.get(i);
                        PGroup group = thing.thing.getPart(Part.GROUP);

                        if(group.emitter != th.thing)
                            bool = false;
                    }

                return bool;
            }

            @Override
            public String buttonText(Object object, int index) {
                String name = ((bog.lbpas.view3d.core.types.Thing)object).thing.name;
                return name == null ? "null" : name;
            }

            @Override
            public boolean searchFilter(Object object, int index) {
                return buttonText(object, index).toLowerCase().contains(searchAllThings.getText().toLowerCase());
            }

            @Override
            public void draw(MouseInput mouseInput, boolean overElement) {
                hovering = -1;
                super.draw(mouseInput, overElement);
            }

            @Override
            public int buttonHeight() {
                return 20;
            }
        }, 150);

        emitterPanel.elements.add(new Panel.PanelElement(emitter, 0.45f));

        Panel lifetimePanel = partComboBox.addPanel("lifetimePanel");
        lifetimePanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("lifetimeStr", "Lifetime:", 10, view.renderer), 0.55f));
        lifetime = new Textbox("lifetime", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window).noLetters().noOthers();
        lifetimePanel.elements.add(new Panel.PanelElement(lifetime, 0.45f));

        Panel aliveFramesPanel = partComboBox.addPanel("aliveFramesPanel");
        aliveFramesPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("aliveFramesStr", "Frames Alive:", 10, view.renderer), 0.55f));
        aliveFrames = new Textbox("aliveFrames", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window).noLetters().noOthers();
        aliveFramesPanel.elements.add(new Panel.PanelElement(aliveFrames, 0.45f));
    }

    @Override
    public void addValues(ArrayList<Integer> selected, ArrayList<Thing> things) {
        String plan = null;
        String creator = null;

        int copyr = -1;
        int edita = -1;
        int pickupAllMemb = -1;

        String lifetime = null;
        String aliveFrames = null;

        String emitter = null;

        for(int i : selected)
        {
            bog.lbpas.view3d.core.types.Thing thing = things.get(i);
            PGroup group = thing.thing.getPart(Part.GROUP);

            if(group == null)
                continue;

            String em = group.emitter == null || group.emitter.name == null ? String.valueOf(Consts.LEFT_ARROW_WITH_SMALL_CIRCLE) : group.emitter.name;
            if(emitter == null)
                emitter = em;
            else if(!emitter.equalsIgnoreCase(em))
                emitter = String.valueOf(Consts.LEFT_ARROW_WITH_SMALL_CIRCLE);

            String grp = group.planDescriptor == null ? "" : group.planDescriptor.isGUID() ? group.planDescriptor.getGUID().toString() : group.planDescriptor.getSHA1().toString();
            if (plan == null)
                plan = grp;
            else if (!plan.equalsIgnoreCase(grp))
                plan = "";

            String crtr = group.creator == null ? "" : group.creator.toString();
            if (creator == null)
                creator = crtr;
            else if (!creator.equalsIgnoreCase(crtr))
                creator = "";

            if (lifetime == null)
                lifetime = Integer.toString(group.lifetime);
            else if (!lifetime.equalsIgnoreCase(Integer.toString(group.lifetime)))
                lifetime = "";

            if (aliveFrames == null)
                aliveFrames = Integer.toString(group.aliveFrames);
            else if (!aliveFrames.equalsIgnoreCase(Integer.toString(group.aliveFrames)))
                aliveFrames = "";

            if(copyr == -1)
                copyr = Utils.isBitwiseBool(group.flags, GroupFlags.COPYRIGHT) ? 1 : 0;
            else if(copyr != (Utils.isBitwiseBool(group.flags, GroupFlags.COPYRIGHT) ? 1 : 0))
                copyr = 0;

            if(edita == -1)
                edita = Utils.isBitwiseBool(group.flags, GroupFlags.EDITABLE) ? 1 : 0;
            else if(edita != (Utils.isBitwiseBool(group.flags, GroupFlags.EDITABLE) ? 1 : 0))
                edita = 0;

            if(pickupAllMemb == -1)
                pickupAllMemb = Utils.isBitwiseBool(group.flags, GroupFlags.PICKUP_ALL_MEMBERS) ? 1 : 0;
            else if(pickupAllMemb != (Utils.isBitwiseBool(group.flags, GroupFlags.PICKUP_ALL_MEMBERS) ? 1 : 0))
                pickupAllMemb = 0;
        }

        String pln = this.plan.setTextboxValueString(plan);
        String crtr = this.creator.setTextboxValueString(creator);
        String lftm = this.lifetime.setTextboxValueString(lifetime);
        String alfra = this.aliveFrames.setTextboxValueString(aliveFrames);

        this.emitter.tabTitle = emitter == null || emitter.equalsIgnoreCase(String.valueOf(Consts.LEFT_ARROW_WITH_SMALL_CIRCLE)) ? "None" : emitter;

        copyright.isChecked = copyr == 1;
        editable.isChecked = edita == 1;
        pickupAllMembers.isChecked = pickupAllMemb == 1;

        for(int i : selected)
        {
            bog.lbpas.view3d.core.types.Thing thing = things.get(i);
            PGroup group = thing.thing.getPart(Part.GROUP);

            if(group == null)
                continue;

            if(pln != null) {
                group.planDescriptor = null;
                try{group.planDescriptor = new ResourceDescriptor(pln, ResourceType.PLAN);}catch (Exception e){}
            }

            if(crtr != null)
                group.creator = new NetworkPlayerID(crtr);
            if(lftm != null)
                group.lifetime = Utils.parseInt(lftm);
            if(alfra != null)
                group.aliveFrames = Utils.parseInt(alfra);
        }
    }
}
