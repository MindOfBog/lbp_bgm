package bog.lbpas.view3d.mainWindow.screens.thingPart.parts;

import bog.lbpas.view3d.core.types.Entity;
import bog.lbpas.view3d.core.types.Thing;
import bog.lbpas.view3d.mainWindow.View3D;
import bog.lbpas.view3d.managers.MouseInput;
import bog.lbpas.view3d.renderer.gui.elements.*;
import bog.lbpas.view3d.utils.Consts;
import org.joml.Matrix4f;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

/**
 * @author Bog
 */
public abstract class PartPos extends iPart {

    public PartPos(int tabWidth, float comboWidth, float panelHeight, float closeWidth, float finalGap, Element tab, View3D view) {
        super(cwlib.enums.Part.POS, "PPos", "Position", tabWidth, comboWidth, panelHeight, closeWidth, finalGap, tab, view);
    }

    public Textbox PositionX;
    public Textbox PositionY;
    public Textbox PositionZ;
    public Textbox RotationX;
    public Textbox RotationY;
    public Textbox RotationZ;
    public Textbox ScaleX;
    public Textbox ScaleY;
    public Textbox ScaleZ;
    public Checkbox ForcedOrtho;

    @Override
    public void init(View3D view) {
        Panel positionPanel = partComboBox.addPanel("positionPanel");
        positionPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("", "Position:", 10, view.renderer), 0.525f));
        positionPanel.elements.add(new Panel.PanelElement(new Button("goto", "Go To", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window) {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if (button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS) {
                    Vector3f cpos = new Vector3f(view.getSelectedPosition());
                    if (!(Float.isNaN(cpos.x) || Float.isNaN(cpos.y) || Float.isNaN(cpos.z)))
                        view.camera.setPos(cpos);
                }
            }
        }, 0.475f));

        Panel xPosSel = partComboBox.addPanel("xpos");
        xPosSel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("xposstr", "X:", 10, view.renderer), 0.1f));
        PositionX = new Textbox("xpostex", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window);
        PositionX.noLetters().noOthers();
        xPosSel.elements.add(new Panel.PanelElement(PositionX, 0.9f));
        Panel yPosSel = partComboBox.addPanel("ypos");
        yPosSel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("yposstr", "Y:", 10, view.renderer), 0.1f));
        PositionY = new Textbox("ypostex", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window);
        PositionY.noLetters().noOthers();
        yPosSel.elements.add(new Panel.PanelElement(PositionY, 0.9f));
        Panel zPosSel = partComboBox.addPanel("zpos");
        zPosSel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("zposstr", "Z:", 10, view.renderer), 0.1f));
        PositionZ = new Textbox("zpostex", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window);
        PositionZ.noLetters().noOthers();
        zPosSel.elements.add(new Panel.PanelElement(PositionZ, 0.9f));

        ForcedOrtho = new Checkbox("forcedOrtho", "Orthogonal")
        {
            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement, boolean focusedOther) {

                super.onClick(mouseInput, pos, button, action, mods, overElement, focusedOther);

                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos) && !overElement)
                {
                    for (int i = 0; i < view.things.size(); i++)
                        if (view.things.get(i).selected)
                        {
                            ((Thing)view.things.get(i)).forceOrtho = this.isChecked;

                            if(!((Thing)view.things.get(i)).forceOrtho)
                            {
                                ((Thing)view.things.get(i)).rotation = null;
                                ((Thing)view.things.get(i)).scale = null;
                            }
                            else
                            {
                                if(((Thing)view.things.get(i)).rotation == null)
                                    ((Thing)view.things.get(i)).rotation = ((Thing)view.things.get(i)).getTransformation().getEulerAnglesXYZ(new Vector3f());
                                if(((Thing)view.things.get(i)).scale == null)
                                    ((Thing)view.things.get(i)).scale = ((Thing)view.things.get(i)).getTransformation().getScale(new Vector3f());

                                Vector3f translation = ((Thing)view.things.get(i)).getTransformation().getTranslation(new Vector3f());
                                ((Thing)view.things.get(i)).setTransformation(
                                        new Matrix4f().identity().translate(translation)
                                                .rotateXYZ(((Thing)view.things.get(i)).rotation)
                                                .scale(((Thing)view.things.get(i)).scale));
                            }
                        }
                }
            }
        };
        partComboBox.addCheckbox(ForcedOrtho);

        partComboBox.addString("rotstr", "Rotation:");

        Panel xRotSel = partComboBox.addPanel("xrot");
        xRotSel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("xrotstr", "X:", 10, view.renderer), 0.1f));
        RotationX = new Textbox("xrottex", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window);
        RotationX.noLetters().noOthers();
        xRotSel.elements.add(new Panel.PanelElement(RotationX, 0.9f));
        Panel yRotSel = partComboBox.addPanel("yrot");
        yRotSel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("yrotstr", "Y:", 10, view.renderer), 0.1f));
        RotationY = new Textbox("yrottex", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window);
        RotationY.noLetters().noOthers();
        yRotSel.elements.add(new Panel.PanelElement(RotationY, 0.9f));
        Panel zRotSel = partComboBox.addPanel("zrot");
        zRotSel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("zrotstr", "Z:", 10, view.renderer), 0.1f));
        RotationZ = new Textbox("zrottex", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window);
        RotationZ.noLetters().noOthers();
        zRotSel.elements.add(new Panel.PanelElement(RotationZ, 0.9f));

        partComboBox.addString("scastr", "Scale:");

        Panel xScaSel = partComboBox.addPanel("xsca");
        xScaSel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("xscastr", "X:", 10, view.renderer), 0.1f));
        ScaleX = new Textbox("xscatex", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window);
        ScaleX.noLetters().noOthers();
        xScaSel.elements.add(new Panel.PanelElement(ScaleX, 0.9f));
        Panel yScaSel = partComboBox.addPanel("ysca");
        yScaSel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("yscastr", "Y:", 10, view.renderer), 0.1f));
        ScaleY = new Textbox("yscatex", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window);
        ScaleY.noLetters().noOthers();
        yScaSel.elements.add(new Panel.PanelElement(ScaleY, 0.9f));
        Panel zScaSel = partComboBox.addPanel("zsca");
        zScaSel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("zscastr", "Z:", 10, view.renderer), 0.1f));
        ScaleZ = new Textbox("zscatex", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window);
        ScaleZ.noLetters().noOthers();
        zScaSel.elements.add(new Panel.PanelElement(ScaleZ, 0.9f));
    }

    @Override
    public void addValues(ArrayList<Integer> selected, ArrayList<Thing> things) {
        Vector3f selectedPos = getSelectedPosition(things);
        Vector2f posX = PositionX.setTextboxValueFloat(selectedPos.x);
        Vector2f posY = PositionY.setTextboxValueFloat(selectedPos.y);
        Vector2f posZ = PositionZ.setTextboxValueFloat(selectedPos.z);
        if(posX.y == 1 || posY.y == 1 || posZ.y == 1)
            setSelectedPosition(things, new Vector3f(posX.y == 1 ? posX.x : selectedPos.x, posY.y == 1 ? posY.x : selectedPos.y, posZ.y == 1 ? posZ.x : selectedPos.z));

        boolean force = true;

        for(int i : selected)
            if(!things.get(i).forceOrtho)
                force = false;

        ForcedOrtho.isChecked = selected.size() == 0 ? false : force;

        if(ForcedOrtho.isChecked && selected.size() == 1)
        {
            RotationX.disabled = false;
            RotationY.disabled = false;
            RotationZ.disabled = false;
            ScaleX.disabled = false;
            ScaleY.disabled = false;
            ScaleZ.disabled = false;

            Thing thing = things.get(selected.get(0));
            Matrix4f transform = new Matrix4f(thing.getTransformation());
            if(thing.rotation == null)
                thing.rotation = transform.getEulerAnglesXYZ(new Vector3f());
            if(thing.scale == null)
                thing.scale = transform.getScale(new Vector3f());

            Matrix4f newTransform = new Matrix4f().identity().translate(transform.getTranslation(new Vector3f()));

            Vector2f rotX = RotationX.setTextboxValueFloat((float) Math.toDegrees(thing.rotation.x));
            Vector2f rotY = RotationY.setTextboxValueFloat((float) Math.toDegrees(thing.rotation.y));
            Vector2f rotZ = RotationZ.setTextboxValueFloat((float) Math.toDegrees(thing.rotation.z));
            if(rotX.y == 1 || rotY.y == 1 || rotZ.y == 1)
            {
                thing.rotation = new Vector3f(rotX.y == 1 ? (float)Math.toRadians(rotX.x) : thing.rotation.x, rotY.y == 1 ? (float)Math.toRadians(rotY.x) : thing.rotation.y, rotZ.y == 1 ? (float)Math.toRadians(rotZ.x) : thing.rotation.z);
                newTransform.rotateXYZ(thing.rotation);
            }
            else
                newTransform.rotateXYZ(thing.rotation);

            Vector2f scaX = ScaleX.setTextboxValueFloat(thing.scale.x);
            Vector2f scaY = ScaleY.setTextboxValueFloat(thing.scale.y);
            Vector2f scaZ = ScaleZ.setTextboxValueFloat(thing.scale.z);
            if(scaX.y == 1 || scaY.y == 1 || scaZ.y == 1)
            {
                thing.scale = new Vector3f(scaX.y == 1 ? scaX.x : thing.scale.x, scaY.y == 1 ? scaY.x : thing.scale.y, scaZ.y == 1 ? scaZ.x : thing.scale.z);
                newTransform.scale(thing.scale);
            }
            else
                newTransform.scale(thing.scale);

            thing.setTransformation(newTransform);
        }
        else
        {
            RotationX.disabled = true;
            RotationY.disabled = true;
            RotationZ.disabled = true;
            ScaleX.disabled = true;
            ScaleY.disabled = true;
            ScaleZ.disabled = true;
            RotationX.setText("N/A");
            RotationY.setText("N/A");
            RotationZ.setText("N/A");
            ScaleX.setText("N/A");
            ScaleY.setText("N/A");
            ScaleZ.setText("N/A");
        }
    }

    public Vector3f getSelectedPosition(ArrayList<bog.lbpas.view3d.core.types.Thing> things)
    {
        boolean hasSelection = false;
        ArrayList<Integer> selected = new ArrayList<>();

        for(int i = 0; i < things.size(); i++)
            if(things.get(i).selected)
            {
                hasSelection = true;
                selected.add(i);
            }

        if(selected.size() == 1)
        {
            for (int i : selected)
                return new Vector3f(things.get(i).getTransformation().getTranslation(new Vector3f()));
        }
        else
        {
            float x = 0;
            float y = 0;
            float z = 0;
            int amount = 0;

            for (int i : selected)
            {
                Vector3f curTransl = things.get(i).getTransformation().getTranslation(new Vector3f());
                x += curTransl.x;
                y += curTransl.y;
                z += curTransl.z;
                amount++;
            }

            return new Vector3f(x/amount, y/amount, z/amount);

        }

        return new Vector3f(Consts.NaNf, Consts.NaNf, Consts.NaNf);
    }

    public void setSelectedPosition(ArrayList<Thing> things, Vector3f pos)
    {
        boolean hasSelection = false;
        ArrayList<Integer> selected = new ArrayList<>();

        for(int i = 0; i < things.size(); i++)
            if(things.get(i).selected)
            {
                hasSelection = true;
                selected.add(i);
            }

        if(selected.size() == 1)
            for(int i : selected)
            {
                things.get(i).getTransformation().setTranslation(new Vector3f(pos));
            }
        else
        {
            Vector3f avgpos = new Vector3f(getSelectedPosition(things));

            for(int i : selected)
            {
                Entity entity = things.get(i);
                Vector3f curTransl = new Vector3f(entity.getTransformation().getTranslation(new Vector3f()));
                Vector3f diff = new Vector3f(new Vector3f(curTransl).sub(avgpos, new Vector3f()));
                entity.getTransformation().setTranslation(new Vector3f(pos).add(diff, new Vector3f()));
            }
        }
    }
}
