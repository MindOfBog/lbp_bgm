package bog.lbpas.view3d.mainWindow.screens.thingPart.parts;

import bog.lbpas.view3d.core.types.Thing;
import bog.lbpas.view3d.mainWindow.View3D;
import bog.lbpas.view3d.managers.MouseInput;
import bog.lbpas.view3d.renderer.gui.elements.*;
import bog.lbpas.view3d.utils.Utils;
import cwlib.enums.Part;
import cwlib.enums.ResourceType;
import cwlib.structs.things.parts.PGeneratedMesh;
import cwlib.types.data.ResourceDescriptor;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

/**
 * @author Bog
 */
public abstract class PartGeneratedMesh extends iPart {

    public PartGeneratedMesh(int tabWidth, float comboWidth, float panelHeight, float closeWidth, float finalGap, DropDownTab tab, View3D view) {
        super(Part.GENERATED_MESH, "PGeneratedMesh", "Generated Mesh", tabWidth, comboWidth, panelHeight, closeWidth, finalGap, tab, view);
    }

    public Textbox Gmat;
    public Textbox Bevel;
    public Textbox UV0X;
    public Textbox UV0Y;
    public Textbox UV1X;
    public Textbox UV1Y;
    public ComboBox VisibilityFlags;
    public Checkbox FlagPlayMode;
    public Checkbox FlagEditMode;
    public Textbox AnimationSpeed;
    public Textbox AnimationSpeedOff;
    public Checkbox NoBevel;
    public Checkbox Sharded;
    public Checkbox IncludeSides;
    public Textbox SlideImpactDamping;
    public Textbox SlideSteer;
    public Textbox SlideSpeed;

    @Override
    public void init(View3D view) {

        Panel gmatPanel = partComboBox.addPanel("gmatPanel");
        gmatPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("gmatstr", "Gmat:", 10, view.renderer), 0.55f));
        Gmat = new Textbox("Gmat", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window);
        gmatPanel.elements.add(new Panel.PanelElement(Gmat, 0.45f));

        Panel bevelPanel = partComboBox.addPanel("bevelPanel");
        bevelPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("bevelstr", "Bevel:", 10, view.renderer), 0.55f));
        Bevel = new Textbox("Bevel", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window);
        bevelPanel.elements.add(new Panel.PanelElement(Bevel, 0.45f));

        Panel uv0Panel = partComboBox.addPanel("uv0Panel");
        uv0Panel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("uv0str", "UV 0 Offset:", 10, view.renderer), 0.55f));
        UV0X = new Textbox("UV0X", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window);
        UV0Y = new Textbox("UV0Y", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window);
        uv0Panel.elements.add(new Panel.PanelElement(UV0X, 0.22f));
        uv0Panel.elements.add(new Panel.PanelElement(null, 0.01f));
        uv0Panel.elements.add(new Panel.PanelElement(UV0Y, 0.22f));

        Panel uv1Panel = partComboBox.addPanel("uv1Panel");
        uv1Panel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("uv1str", "UV 1 Offset:", 10, view.renderer), 0.55f));
        UV1X = new Textbox("UV1X", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window);
        UV1Y = new Textbox("UV1Y", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window);
        uv1Panel.elements.add(new Panel.PanelElement(UV1X, 0.22f));
        uv1Panel.elements.add(new Panel.PanelElement(null, 0.01f));
        uv1Panel.elements.add(new Panel.PanelElement(UV1Y, 0.22f));

        VisibilityFlags = partComboBox.addComboBox("VisibilityFlags", "Visibility Flags", 160);
        FlagPlayMode = VisibilityFlags.addCheckbox("PLAY_MODE", "Play mode", new Checkbox()
        {
            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement) {
                super.onClick(mouseInput, pos, button, action, mods, overElement);

                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos) && !overElement)
                    for (int i = 0; i < view.things.size(); i++)
                        if (view.things.get(i).selected)
                        {
                            PGeneratedMesh gmesh = ((PGeneratedMesh)((Thing)view.things.get(i)).thing.getPart(cwlib.enums.Part.GENERATED_MESH));
                            gmesh.visibilityFlags = Utils.setBitwiseBool(gmesh.visibilityFlags, cwlib.enums.VisibilityFlags.PLAY_MODE, this.isChecked);
                        }
            }
        });
        FlagEditMode = VisibilityFlags.addCheckbox("EDIT_MODE", "Edit mode", new Checkbox()
        {
            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement) {
                super.onClick(mouseInput, pos, button, action, mods, overElement);

                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos) && !overElement)
                    for (int i = 0; i < view.things.size(); i++)
                        if (view.things.get(i).selected)
                        {
                            PGeneratedMesh gmesh = ((PGeneratedMesh)((Thing)view.things.get(i)).thing.getPart(cwlib.enums.Part.GENERATED_MESH));
                            gmesh.visibilityFlags = Utils.setBitwiseBool(gmesh.visibilityFlags, cwlib.enums.VisibilityFlags.EDIT_MODE, this.isChecked);
                        }
            }
        });

        Panel animSpeedPanel = partComboBox.addPanel("animSpeedPanel");
        animSpeedPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("animspeedstr", "Anim. Speed:", 10, view.renderer), 0.55f));
        AnimationSpeed = new Textbox("AnimationSpeed", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window);
        animSpeedPanel.elements.add(new Panel.PanelElement(AnimationSpeed, 0.45f));

        Panel animSpeedOffPanel = partComboBox.addPanel("animSpeedOffPanel");
        animSpeedOffPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("animspeedoffstr", "A. Speed Off:", 10, view.renderer), 0.55f));
        AnimationSpeedOff = new Textbox("AnimationSpeedOff", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window);
        animSpeedOffPanel.elements.add(new Panel.PanelElement(AnimationSpeedOff, 0.45f));

        NoBevel = partComboBox.addCheckbox("NoBevel", "No Bevel", new Checkbox()
        {
            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement) {
                super.onClick(mouseInput, pos, button, action, mods, overElement);

                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos) && !overElement)
                    for (int i = 0; i < view.things.size(); i++)
                        if (view.things.get(i).selected)
                        {
                            PGeneratedMesh gmesh = ((PGeneratedMesh)((Thing)view.things.get(i)).thing.getPart(cwlib.enums.Part.GENERATED_MESH));
                            gmesh.noBevel = !gmesh.noBevel;
                        }
            }
        });
        Sharded = partComboBox.addCheckbox("Sharded", "Sharded", new Checkbox()
        {
            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement) {
                super.onClick(mouseInput, pos, button, action, mods, overElement);

                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos) && !overElement)
                    for (int i = 0; i < view.things.size(); i++)
                        if (view.things.get(i).selected)
                        {
                            PGeneratedMesh gmesh = ((PGeneratedMesh)((Thing)view.things.get(i)).thing.getPart(cwlib.enums.Part.GENERATED_MESH));
                            gmesh.sharded = !gmesh.sharded;
                        }
            }
        });
        IncludeSides = partComboBox.addCheckbox("IncludeSides", "Include Sides", new Checkbox()
        {
            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement) {
                super.onClick(mouseInput, pos, button, action, mods, overElement);

                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos) && !overElement)
                    for (int i = 0; i < view.things.size(); i++)
                        if (view.things.get(i).selected)
                        {
                            PGeneratedMesh gmesh = ((PGeneratedMesh)((Thing)view.things.get(i)).thing.getPart(cwlib.enums.Part.GENERATED_MESH));
                            gmesh.includeSides = !gmesh.includeSides;
                        }
            }
        });

        partComboBox.addString("slidestuff", "Slide Settings:");

        Panel slideImpactDampPanel = partComboBox.addPanel("slideImpactDampPanel");
        slideImpactDampPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("slideimpdampstr", "Impact Damp:", 10, view.renderer), 0.55f));
        SlideImpactDamping = new Textbox("SlideImpactDamping", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window);
        slideImpactDampPanel.elements.add(new Panel.PanelElement(SlideImpactDamping, 0.45f));

        Panel slideSteerPanel = partComboBox.addPanel("slideSteerPanel");
        slideSteerPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("slidesteerstr", "Steer:", 10, view.renderer), 0.55f));
        SlideSteer = new Textbox("SlideSteer", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window);
        slideSteerPanel.elements.add(new Panel.PanelElement(SlideSteer, 0.45f));

        Panel slideSpeedPanel = partComboBox.addPanel("slideSpeedPanel");
        slideSpeedPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("slidespeedstr", "Speed:", 10, view.renderer), 0.55f));
        SlideSpeed = new Textbox("SlideSpeed", new Vector2f(), new Vector2f(), 10, view.renderer, view.loader, view.window);
        slideSpeedPanel.elements.add(new Panel.PanelElement(SlideSpeed, 0.45f));
    }

    @Override
    public void addValues(ArrayList<Integer> selected, ArrayList<Thing> things) {
        String gmat = null;
        String bevel = null;
        Vector4f uvOffset = null;
        int visiblePlay = -1;
        int visibleEdit = -1;
        float animSpeed = Float.NEGATIVE_INFINITY;
        float animSpeedOff = Float.NEGATIVE_INFINITY;
        int noBevel = -1;
        int sharded = -1;
        int includeSides = -1;
        float slideImpactDamping = Float.NEGATIVE_INFINITY;
        float slideSteer = Float.NEGATIVE_INFINITY;
        float slideSpeed = Float.NEGATIVE_INFINITY;

        for(int i : selected)
        {
            Thing thing = things.get(i);
            PGeneratedMesh gmesh = thing.thing.getPart(Part.GENERATED_MESH);

            if(gmesh == null)
                continue;

            String gmt = gmesh.gfxMaterial == null ? "" : gmesh.gfxMaterial.isGUID() ? gmesh.gfxMaterial.getGUID().toString() : gmesh.gfxMaterial.getSHA1().toString();
            if (gmat == null)
                gmat = gmt;
            else if (!gmat.equalsIgnoreCase(gmt))
                gmat = "";

            String bev = gmesh.bevel == null ? "" : gmesh.bevel.isGUID() ? gmesh.bevel.getGUID().toString() : gmesh.bevel.getSHA1().toString();
            if (bevel == null)
                bevel = bev;
            else if (!bevel.equalsIgnoreCase(bev))
                bevel = "";

            if(visiblePlay == -1)
                visiblePlay = Utils.isBitwiseBool(gmesh.visibilityFlags, cwlib.enums.VisibilityFlags.PLAY_MODE) ? 1 : 0;
            else if(visiblePlay != (Utils.isBitwiseBool(gmesh.visibilityFlags, cwlib.enums.VisibilityFlags.PLAY_MODE) ? 1 : 0))
                visiblePlay = 0;
            if(visibleEdit == -1)
                visibleEdit = Utils.isBitwiseBool(gmesh.visibilityFlags, cwlib.enums.VisibilityFlags.EDIT_MODE) ? 1 : 0;
            else if(visibleEdit != (Utils.isBitwiseBool(gmesh.visibilityFlags, cwlib.enums.VisibilityFlags.EDIT_MODE) ? 1 : 0))
                visibleEdit = 0;

            float animSp = gmesh.textureAnimationSpeed;
            if(Float.isInfinite(animSpeed))
                animSpeed = animSp;
            else if(animSpeed != animSp)
                animSpeed = Float.NaN;

            float animSpOff = gmesh.textureAnimationSpeedOff;
            if(Float.isInfinite(animSpeedOff))
                animSpeedOff = animSpOff;
            else if(animSpeedOff != animSpOff)
                animSpeedOff = Float.NaN;

            if(noBevel == -1)
                noBevel = gmesh.noBevel ? 1 : 0;
            else if(noBevel != (gmesh.noBevel ? 1 : 0))
                noBevel = 0;

            if(sharded == -1)
                sharded = gmesh.sharded ? 1 : 0;
            else if(sharded != (gmesh.sharded ? 1 : 0))
                sharded = 0;

            if(includeSides == -1)
                includeSides = gmesh.includeSides ? 1 : 0;
            else if(includeSides != (gmesh.includeSides ? 1 : 0))
                includeSides = 0;

            float slideImpDamp = gmesh.slideImpactDamping;
            if(Float.isInfinite(slideImpactDamping))
                slideImpactDamping = slideImpDamp;
            else if(slideImpactDamping != slideImpDamp)
                slideImpactDamping = Float.NaN;

            float slideStr = gmesh.slideSteer;
            if(Float.isInfinite(slideSteer))
                slideSteer = slideStr;
            else if(slideSteer != slideStr)
                slideSteer = Float.NaN;

            float slideStp = gmesh.slideSpeed;
            if(Float.isInfinite(slideSpeed))
                slideSpeed = slideStp;
            else if(slideSpeed != slideStp)
                slideSpeed = Float.NaN;

            Vector4f uvO = gmesh.uvOffset == null ? new Vector4f() : new Vector4f(gmesh.uvOffset);
            if(uvOffset == null)
                uvOffset = uvO;
            else
            {
                if(uvOffset.x != uvO.x)
                    uvOffset.x = Float.NaN;
                if(uvOffset.y != uvO.y)
                    uvOffset.y = Float.NaN;
                if(uvOffset.z != uvO.z)
                    uvOffset.z = Float.NaN;
                if(uvOffset.w != uvO.w)
                    uvOffset.w = Float.NaN;
            }
        }

        FlagPlayMode.isChecked = visiblePlay == 1;
        FlagEditMode.isChecked = visibleEdit == 1;
        NoBevel.isChecked = noBevel == 1;
        Sharded.isChecked = sharded == 1;
        IncludeSides.isChecked = includeSides == 1;

        String gmt = Gmat.setTextboxValueString(gmat);
        String bev = Bevel.setTextboxValueString(bevel);
        Vector2f animSp = AnimationSpeed.setTextboxValueFloat(animSpeed);
        Vector2f animSpOff = AnimationSpeedOff.setTextboxValueFloat(animSpeedOff);

        if(uvOffset != null) {
            Vector2f uv0x = UV0X.setTextboxValueFloat(uvOffset.x);
            Vector2f uv0y = UV0Y.setTextboxValueFloat(uvOffset.y);
            Vector2f uv1x = UV1X.setTextboxValueFloat(uvOffset.z);
            Vector2f uv1y = UV1Y.setTextboxValueFloat(uvOffset.w);

            for (int i : selected) {
                Thing thing = things.get(i);
                PGeneratedMesh gmesh = thing.thing.getPart(Part.GENERATED_MESH);

                if (gmesh == null)
                    continue;

                long prevGuidGFX = gmesh.gfxMaterial == null || !gmesh.gfxMaterial.isGUID() ? -1 : gmesh.gfxMaterial.getGUID().getValue();
                String prevSHA1GFX = gmesh.gfxMaterial == null || !gmesh.gfxMaterial.isHash() ? "" : gmesh.gfxMaterial.getSHA1().toString();

                if(gmt != null) {
                    gmesh.gfxMaterial = null;
                    try{gmesh.gfxMaterial = new ResourceDescriptor(gmt, ResourceType.GFX_MATERIAL);}catch (Exception e){}

                    if((gmesh.gfxMaterial.isGUID() && gmesh.gfxMaterial.getGUID().getValue() != prevGuidGFX) ||
                            (gmesh.gfxMaterial.isHash() && !gmesh.gfxMaterial.getSHA1().toString().equalsIgnoreCase(prevSHA1GFX)))
                        thing.reloadModel();
                }

                long prevGuidBev = gmesh.bevel == null || !gmesh.bevel.isGUID() ? -1 : gmesh.bevel.getGUID().getValue();
                String prevSHA1Bev = gmesh.bevel == null || !gmesh.bevel.isHash() ? "" : gmesh.bevel.getSHA1().toString();

                if(bev != null) {
                    gmesh.bevel = null;
                    try{gmesh.bevel = new ResourceDescriptor(bev, ResourceType.BEVEL);}catch (Exception e){}

                    if((gmesh.bevel.isGUID() && gmesh.bevel.getGUID().getValue() != prevGuidBev) ||
                            (gmesh.bevel.isHash() && !gmesh.bevel.getSHA1().toString().equalsIgnoreCase(prevSHA1Bev)))
                        thing.reloadModel();
                }

                if (animSp.y == 1)
                    gmesh.textureAnimationSpeed = animSp.x;
                if (animSpOff.y == 1)
                    gmesh.textureAnimationSpeedOff = animSpOff.x;

                if (gmesh.uvOffset == null)
                    gmesh.uvOffset = new Vector4f();

                if (uv0x.y == 1)
                    gmesh.uvOffset.x = uv0x.x;
                if (uv0y.y == 1)
                    gmesh.uvOffset.y = uv0y.x;
                if (uv1x.y == 1)
                    gmesh.uvOffset.z = uv1x.x;
                if (uv1y.y == 1)
                    gmesh.uvOffset.w = uv1y.x;
            }
        }
    }
}
