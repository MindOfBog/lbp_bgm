package bog.lbpas.view3d.mainWindow.screens;

import bog.lbpas.view3d.core.Model;
import bog.lbpas.view3d.core.Transformation3D;
import bog.lbpas.view3d.core.types.Entity;
import bog.lbpas.view3d.core.types.Thing;
import bog.lbpas.view3d.mainWindow.ConstantTextures;
import bog.lbpas.view3d.mainWindow.LoadedData;
import bog.lbpas.view3d.mainWindow.View3D;
import bog.lbpas.view3d.managers.MouseInput;
import bog.lbpas.view3d.renderer.gui.GuiScreen;
import bog.lbpas.view3d.renderer.gui.cursor.ECursor;
import bog.lbpas.view3d.renderer.gui.elements.Button;
import bog.lbpas.view3d.renderer.gui.elements.DropDownTab;
import bog.lbpas.view3d.renderer.gui.elements.Panel;
import bog.lbpas.view3d.renderer.gui.elements.Textbox;
import bog.lbpas.view3d.renderer.gui.ingredients.LineStrip;
import bog.lbpas.view3d.utils.Cursors;
import bog.lbpas.view3d.utils.MousePicker;
import bog.lbpas.view3d.utils.Utils;
import bog.lbpas.view3d.utils.print;
import cwlib.enums.Part;
import cwlib.resources.RBevel;
import cwlib.structs.things.parts.PGeneratedMesh;
import cwlib.structs.things.parts.PShape;
import org.joml.Matrix4f;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * @author Bog
 */
public class MaterialEditing extends GuiScreen {

    View3D mainView;
    public int closest = -1;
    public ArrayList<Integer> selectedVertices;

    public MaterialEditing(View3D mainView)
    {
        super(mainView.renderer, mainView.loader, mainView.window);
        this.mainView = mainView;
        this.init();
    }

    Transformation3D.Tool vertexTool;

    Textbox xPos;
    Textbox yPos;

    public void init()
    {
        selectedVertices = new ArrayList<>();
        vertexTool = new Transformation3D.Tool(loader);

        this.guiElements.add(new Button("backButton", "Back", new Vector2f(window.width - 110, window.height - 35), new Vector2f(100, 25), 10, renderer, loader, window) {
            @Override
            public void clickedButton(int button, int action, int mods) {
                mainView.setCurrentScreen(previousScreen);
            }

            @Override
            public void draw(MouseInput mouseInput, boolean overOther) {
                this.pos = new Vector2f(window.width - 110, window.height - 35);
                super.draw(mouseInput, overOther);
            }
        });

        DropDownTab bevelDataTab = new DropDownTab("bevelDataTab", "Bevel Data" , new Vector2f(7, 21 + 7), new Vector2f(350, getFontHeight(10) + 4), 10, mainView.renderer, mainView.loader, mainView.window);
        bevelDataTab.addString(new DropDownTab.StringElement("bev", 10, mainView.renderer)
        {
            @Override
            public String stringToDraw() {
                return "Bevel: " + (generatedMesh == null || generatedMesh.bevel == null ? "None" : generatedMesh.bevel.toString());
            }
        });
        bevelDataTab.addString(new DropDownTab.StringElement("uvScales", 10, mainView.renderer)
        {
            @Override
            public String stringToDraw() {
                return "UV Scales: [" + bev.getMaterialUVScale(0) + ", " + bev.getMaterialUVScale(1) + ", " + bev.getMaterialUVScale(2) + ", " + bev.getMaterialUVScale(3) + "]";
            }
        });
        bevelDataTab.addString(new DropDownTab.StringElement("autoSmoothCutoffAngle", 10, mainView.renderer)
        {
            @Override
            public String stringToDraw() {
                return "Auto Smooth Cutoff Angle: " + bev.autoSmoothCutoffAngle;
            }
        });
        bevelDataTab.addString(new DropDownTab.StringElement("vertices", 10, mainView.renderer)
        {
            @Override
            public String stringToDraw() {
                return "Vertices: " + bev.vertices.size();
            }
        });
        bevelDataTab.addString(new DropDownTab.StringElement("includeBackface", 10, mainView.renderer)
        {
            @Override
            public String stringToDraw() {
                return "Include Backface: " + bev.includeBackface;
            }
        });
        bevelDataTab.addString(new DropDownTab.StringElement("smoothWithFront", 10, mainView.renderer)
        {
            @Override
            public String stringToDraw() {
                return "Smooth With Front: " + bev.smoothWithFront;
            }
        });
        bevelDataTab.addString(new DropDownTab.StringElement("relaxStrength", 10, mainView.renderer)
        {
            @Override
            public String stringToDraw() {
                return "Relax Strength: " + bev.relaxStrength;
            }
        });
        bevelDataTab.addString(new DropDownTab.StringElement("subDivRadius", 10, mainView.renderer)
        {
            @Override
            public String stringToDraw() {
                return "Sub Division Radius: " + bev.subDivRadius;
            }
        });
        bevelDataTab.addString(new DropDownTab.StringElement("fixedBevelSize", 10, mainView.renderer)
        {
            @Override
            public String stringToDraw() {
                return "Fixed Bevel Size: " + bev.fixedBevelSize;
            }
        });
        bevelDataTab.addString(new DropDownTab.StringElement("spongy", 10, mainView.renderer)
        {
            @Override
            public String stringToDraw() {
                return "Spongy: " + bev.fixedBevelSize;
            }
        });
        bevelDataTab.addString(new DropDownTab.StringElement("textureRepeats", 10, mainView.renderer)
        {
            @Override
            public String stringToDraw() {
                return "Texture Repeats: " + bev.textureRepeats;
            }
        });
        this.guiElements.add(bevelDataTab);

        DropDownTab shapeDataTab = new DropDownTab("shapeDataTab", "Current Selection" , new Vector2f(7 * 2 + 350, 21 + 7), new Vector2f(200, getFontHeight(10) + 4), 10, mainView.renderer, mainView.loader, mainView.window);

        Panel xPosPanel = shapeDataTab.addPanel("xPosPanel");
        xPosPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("xPosStr", "X:", 10, mainView.renderer), 0.1f));
        xPos = new Textbox("xPos", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public void onChar(int codePoint, int modifiers) {
                super.onChar(codePoint, modifiers);

                for(Entity entity : mainView.things)
                    if(entity.selected && ((Thing)entity).thing.hasPart(Part.SHAPE))
                        entity.reloadModel();
            }

            @Override
            public void onKey(int key, int scancode, int action, int mods) {
                super.onKey(key, scancode, action, mods);

                for(Entity entity : mainView.things)
                    if(entity.selected && ((Thing)entity).thing.hasPart(Part.SHAPE))
                        entity.reloadModel();
            }
        }.noLetters().noOthers();
        xPosPanel.elements.add(new Panel.PanelElement(xPos, 0.9f));

        Panel yPosPanel = shapeDataTab.addPanel("yPosPanel");
        yPosPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("yPosStr", "Y:", 10, mainView.renderer), 0.1f));
        yPos = new Textbox("yPos", new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public void onChar(int codePoint, int modifiers) {
                super.onChar(codePoint, modifiers);

                for(Entity entity : mainView.things)
                    if(entity.selected && ((Thing)entity).thing.hasPart(Part.SHAPE))
                        entity.reloadModel();
            }

            @Override
            public void onKey(int key, int scancode, int action, int mods) {
                super.onKey(key, scancode, action, mods);

                for(Entity entity : mainView.things)
                    if(entity.selected && ((Thing)entity).thing.hasPart(Part.SHAPE))
                        entity.reloadModel();
            }
        }.noLetters().noOthers();
        yPosPanel.elements.add(new Panel.PanelElement(yPos, 0.9f));

        this.guiElements.add(shapeDataTab);
    }

    PShape shape;
    PGeneratedMesh generatedMesh;
    Model[] lines;
    int pickPoint = -1;
    float pickDist;
    int pickLoop;

    RBevel bev;

    @Override
    public void draw(MouseInput mouseInput) {

        pickPoint = -1;

        for(Entity entity : mainView.things)
            if(entity.selected && ((Thing)entity).thing.hasPart(Part.SHAPE))
            {
                shape = ((Thing)entity).thing.getPart(Part.SHAPE);
                generatedMesh = ((Thing)entity).thing.getPart(Part.GENERATED_MESH);
                bev = ((Thing)entity).bevelData;

                if(this.selectedVertices.size() > 0)
                {
                    Vector3f pos = new Vector3f();

                    for(int index : this.selectedVertices)
                        pos.add(shape.polygon.vertices[index].mulProject(entity.getTransformation(), new Vector3f()));

                    pos.div(shape.polygon.vertices.length);

                    Vector2f xp = xPos.setTextboxValueFloat(Utils.round(pos.x, 3));
                    if (xp.y == 1)
                        for(int index : this.selectedVertices)
                        {
                            Vector3f vpos = shape.polygon.vertices[index].mulProject(entity.getTransformation(), new Vector3f());
                            Vector3f offset = vpos.sub(pos, new Vector3f());
                            shape.polygon.vertices[index] = new Vector3f(xp.x + offset.x, vpos.y, vpos.z).mulProject(new Matrix4f(entity.getTransformation()).invert());
                        }
                    Vector2f yp = yPos.setTextboxValueFloat(Utils.round(pos.y, 3));
                    if (yp.y == 1)
                        for(int index : this.selectedVertices)
                        {
                            Vector3f vpos = shape.polygon.vertices[index].mulProject(entity.getTransformation(), new Vector3f());
                            Vector3f offset = vpos.sub(pos, new Vector3f());
                            shape.polygon.vertices[index] = new Vector3f(vpos.x, yp.x + offset.y, vpos.z).mulProject(new Matrix4f(entity.getTransformation()).invert());
                        }
                }
                else
                {
                    xPos.setTextboxValueString("");
                    yPos.setTextboxValueString("");
                }

                float bevelSize = bev == null ? -1 : bev.fixedBevelSize;
                if(bevelSize == -1)
                    bevelSize = shape.bevelSize;

                HashMap<Vector3f, Integer> screenPositions = new HashMap<>();
                Vector2f[][] lineVerts = new Vector2f[shape.polygon.loops.length][];

                int seg = 0;
                int lo = 0;

                for(int loop : shape.polygon.loops)
                {
                    lineVerts[seg] = new Vector2f[loop + 1];

                    for(int i = loop - 1; i >= 0; i--)
                    {
                        Vector3f vertex = shape.polygon.vertices[lo + i];
                        Vector3f position = new Vector3f(vertex);
                        position.mulProject(entity.getTransformation(), position);
                        position.z = entity.getTransformation().getTranslation(new Vector3f()).z + shape.thickness;

//                            int p0 = (i - 2);
//                            if(p0 < 0)
//                                p0 = loop + p0;
//
//                            int p1 = (i - 1);
//                            if(p1 < 0)
//                                p1 = loop - 1;
//
//                            int p2 = (i + 1);
//                            if(p2 >= loop)
//                                p2 = 0;
//
//                            if(selectedVertices.isEmpty() || selectedVertices.contains(lo + i) || selectedVertices.contains(lo + p1) || selectedVertices.contains(lo + p2))
//                            {
//                                Vector3f pos0 = new Vector3f(shape.polygon.vertices[lo + p0]);
//                                pos0.mulProject(entity.getTransformation(), pos0);
//
//                                Vector3f pos1 = new Vector3f(shape.polygon.vertices[lo + p1]);
//                                pos1.mulProject(entity.getTransformation(), pos1);
//
//                                Vector3f pos2 = new Vector3f(shape.polygon.vertices[lo + p2]);
//                                pos2.mulProject(entity.getTransformation(), pos2);
//
//                                if(bev != null) {
//                                    Vector2f newPos = Utils.offsetAndFindIntersection(new Vector2f(pos1.x, pos1.y), new Vector2f(position.x, position.y), new Vector2f(pos2.x, pos2.y), bev.vertices.get(bev.vertices.size() - 1).y * bevelSize);
//                                    Vector2f newPosPrev = Utils.offsetAndFindIntersection(new Vector2f(pos0.x, pos0.y), new Vector2f(pos1.x, pos1.y), new Vector2f(position.x, position.y), bev.vertices.get(bev.vertices.size() - 1).y * bevelSize);
//
//                                    Vector2f dir = new Vector2f(pos1.x, pos1.y).sub(new Vector2f(position.x, position.y)).normalize();
//                                    Vector2f dirOffset = new Vector2f(newPosPrev.x, newPosPrev.y).sub(new Vector2f(newPos.x, newPos.y)).normalize();
//
//                                    boolean isBugged = (dir.dot(dirOffset) <= -Math.cos(1));
//
//                                    Vector3f screenPosition = mainView.camera.worldToScreen(new Vector3f(newPos.x, newPos.y, position.z), window);
//                                    renderer.drawImageStatic(ConstantTextures.getTexture(ConstantTextures.CORNER_EDIT_PICK, 30, 30, loader), (int) (screenPosition.x - 15), (int) (screenPosition.y - 15), 30, 30, selectedVertices.contains(lo + p1) ? new Color(0.45f, 0.45f, 1f) : selectedVertices.contains(lo + p2) ? new Color(1f, 0.45f, 0.45f) : new Color(0.45f, 1f, 0.45f));
//
//                                    renderer.drawString("" + isBugged, Color.white, (int) screenPosition.x + 8, (int) screenPosition.y + 8, 10);
//                                }
//                            }

                        Vector3f screenPosition = mainView.camera.worldToScreen(position, window);
                        screenPositions.put(screenPosition, lo + i);
                        lineVerts[seg][i] = new Vector2f(screenPosition.x, screenPosition.y);

                        if(screenPosition.z == 0)
                            renderer.drawString("" + (lo + i), Color.white, (int) screenPosition.x + 8, (int) screenPosition.y + 8, 10);
                    }

                    lineVerts[seg][loop] = lineVerts[seg][0];

                    lo += loop;
                    seg++;
                }

                boolean overOther = false;

                ArrayList<Vector3f> positions = new ArrayList<>(screenPositions.keySet());

                positions.sort(new Comparator<Vector3f>() {
                    @Override
                    public int compare(Vector3f v1, Vector3f v2) {
                        if (v1.z == 1 && v2.z == 1)
                            return 0;
                        else if (v1.z == 1 && v2.z == 0) {
                            return -1;
                        } else if (v1.z == 0 && v2.z == 1) {
                            return 1;
                        } else
                            return Double.compare(mouseInput.currentPos.distance(v1.x, v1.y), mouseInput.currentPos.distance(v2.x, v2.y));
                    }
                });

                closest = -1;

                for(int i = 0; i < positions.size(); i++)
                {
                    Vector3f screenPoint = positions.get(i);
                    if(screenPoint.z == 0)
                    {
                        if (mouseInput.currentPos.distance(screenPoint.x, screenPoint.y) < 15 && !overOther && !(vertexTool.isHovering() && selectedVertices != null && this.selectedVertices.size() > 0) && !(vertexTool.isSelected() && this.selectedVertices != null && this.selectedVertices.size() > 0))
                        {
                            closest = i;
                            overOther = true;
                        }
                        else
                        {
                            if(selectedVertices.contains(screenPositions.get(positions.get(i))))
                                renderer.drawImageStatic(ConstantTextures.getTexture(ConstantTextures.CORNER_EDIT, 30, 30, loader), (int)(screenPoint.x - 15), (int)(screenPoint.y - 15), 30, 30);
                            else
                                renderer.drawImageStatic(ConstantTextures.getTexture(ConstantTextures.CORNER_EDIT_PICK, 30, 30, loader), (int)(screenPoint.x - 15), (int)(screenPoint.y - 15), 30, 30, new Color(0.65f, 0.65f, 0.65f));
                        }
                    }
                }

                if(((Vector3f)screenPositions.keySet().stream().toArray()[0]).z == 0)
                {
                    if(lines != null && lines.length > 0)
                        for(Model l : lines)
                            if(l != null)
                                l.cleanup(loader);

                    if(lines == null || lines.length != lineVerts.length)
                        lines = new Model[lineVerts.length];

                    Vector2f point = null;
                    Vector2f mousePoint = new Vector2f((float) mouseInput.currentPos.x, (float) mouseInput.currentPos.y);

                    for(int i = 0; i < lineVerts.length; i++)
                    {
                        if(i < lineVerts.length)
                            try{
                                lines[i] = LineStrip.processVerts(lineVerts[i], loader, window);
                                renderer.processGuiElement(new LineStrip(new Vector2f(), lines[i], Color.white, true));
                            }catch (Exception e){e.printStackTrace();}

                        for(int j = 0; j < lineVerts[i].length - 1; j++)
                        {
                            Vector2f a = lineVerts[i][j];
                            Vector2f b = lineVerts[i][j + 1];

                            Vector2f p = Utils.getClosestPointOnLine(a, b, mousePoint);

                            float ab = a.distance(b);
                            float ap = a.distance(p);

                            if(point == null)
                            {
                                point = p;
                                pickDist = ap/ab;
                                pickLoop = i;
                                if(!overOther && point.distance(mousePoint) < 15 && !(vertexTool.isHovering() && selectedVertices != null && selectedVertices.size() > 0) && !(vertexTool.isSelected() && this.selectedVertices != null && this.selectedVertices.size() > 0))
                                    pickPoint = j;
                            }
                            else
                            if(p.distance(mousePoint) < point.distance(mousePoint))
                            {
                                point = p;
                                pickDist = ap/ab;
                                pickLoop = i;
                                if(!overOther && point.distance(mousePoint) < 15 && !(vertexTool.isHovering() && selectedVertices != null && selectedVertices.size() > 0) && !(vertexTool.isSelected() && this.selectedVertices != null && this.selectedVertices.size() > 0))
                                    pickPoint = j;
                            }
                        }
                    }

                    if(!overOther && point.distance(mousePoint) < 15 && !(vertexTool.isHovering() && selectedVertices != null && selectedVertices.size() > 0) && !(vertexTool.isSelected() && this.selectedVertices != null && this.selectedVertices.size() > 0))
                    {
                        renderer.drawImageStatic(ConstantTextures.getTexture(ConstantTextures.CORNER_EDIT_PICK, 30, 30, loader), (int)(point.x - 15), (int)(point.y - 15), 30, 30);
                        Cursors.setCursor(ECursor.copy);
                    }
                    else
                        pickPoint = -1;
                }

                if(closest != -1)
                {
                    int closest = screenPositions.get(positions.get(this.closest));
                    renderer.drawImageStatic(this.selectedVertices.contains(closest) ? ConstantTextures.getTexture(ConstantTextures.CORNER_EDIT, 30, 30, loader) : ConstantTextures.getTexture(ConstantTextures.CORNER_EDIT_PICK, 30, 30, loader), (int) (positions.get(this.closest).x - 15), (int) (positions.get(this.closest).y - 15), 30, 30, new Color(1f, 1f, 1f));
                    Cursors.setCursor(ECursor.hand2);
                    this.closest = closest;
                }

                if(this.selectedVertices != null && this.selectedVertices.size() > 0)
                {
                    Vector3f screenPos = null;
                    Vector3f avgPos = null;
                    try
                    {
                        float x = 0;
                        float y = 0;
                        float z = 0;
                        int amount = 0;
                        for(Vector3f pos : screenPositions.keySet())
                            for(int index : this.selectedVertices)
                                if(screenPositions.get(pos) == index)
                                {
                                    x += pos.x;
                                    y += pos.y;
                                    z += pos.z;
                                    amount++;
                                }
                        screenPos = new Vector3f(x/amount, y/amount, z/amount);
                    }
                    catch (Exception e){e.printStackTrace();}
                    try
                    {
                        float x = 0;
                        float y = 0;
                        float z = 0;
                        int amount = 0;
                        for(int i : this.selectedVertices)
                        {
                            Vector3f vpos = shape.polygon.vertices[i];
                            x += vpos.x;
                            y += vpos.y;
                            z += vpos.z;
                            amount++;
                        }
                        avgPos = new Vector3f(x/amount, y/amount, z/amount);
                    }
                    catch (Exception e){e.printStackTrace();}

                    if(screenPos != null && screenPos.z >= 0)
                    {
                        MouseInput mi = new MouseInput(new View3D(window));
                        mi.previousPos = new Vector2d(screenPos.x, screenPos.y);
                        mi.currentPos = mi.previousPos;
                        MousePicker mp = new MousePicker(mi, window);
                        mp.update(mainView.camera);
                        Vector3f worldPos = mp.getPointOnPlaneZ(entity.getTransformation().getTranslation(new Vector3f()).z + shape.thickness);

                        vertexTool.updateModels(mainView, worldPos);
                        vertexTool.testForMouse(true, mainView.camera, mouseInput.mousePicker, true, false, false);
//                        vertexTool.render(true, true, true, false, false, false, true, true, true, false, true, -1, window, loader, renderer, mouseInput);
                        vertexTool.render(true, true, true, false, false, false, false, false, false, false, false, -1, window, loader, renderer, mouseInput);

                        if(vertexTool.selected != -1) {
                            switch (vertexTool.selected) {
                                //0,1,2   pos
                                //3,4,5   rot
                                //6,7,8,9 scale
                                case 0:
                                    //x pos
                                {
                                    boolean failed = false;

                                    if ((mainView.camera.getWrappedRotation().x < 45f && mainView.camera.getWrappedRotation().x > -45f) ||
                                            (mainView.camera.getWrappedRotation().x > 135f && mainView.camera.getWrappedRotation().x < -135f))
                                    {
                                        Vector3f currentPosOnZ = mouseInput.mousePicker.getPointOnPlaneZ(worldPos.z);
                                        if(currentPosOnZ == null || vertexTool.initPosZ == null)
                                            failed = true;
                                        else
                                        {
                                            for (int i : this.selectedVertices)
                                            {
                                                Vector3f position = new Vector3f(shape.polygon.vertices[i]);

                                                Matrix4f m = new Matrix4f(entity.getTransformation());

                                                position.mulProject(m);
                                                position.x += currentPosOnZ.x - vertexTool.initPosZ.x;
                                                position.mulProject(m.invert());

                                                shape.polygon.vertices[i] = position;
                                                reloadMesh();
                                            }
                                            vertexTool.initPosZ = currentPosOnZ;
                                        }
                                    }
                                    else
                                        failed = true;

                                    if(failed)
                                    {
                                        Vector3f currentPosOnY = mouseInput.mousePicker.getPointOnPlaneY(worldPos.y);

                                        if(currentPosOnY != null && vertexTool.initPosY != null)
                                        {
                                            for (int i : this.selectedVertices)
                                            {
                                                Vector3f position = new Vector3f(shape.polygon.vertices[i]);

                                                Matrix4f m = new Matrix4f(entity.getTransformation());

                                                position.mulProject(m);
                                                position.x += currentPosOnY.x - vertexTool.initPosY.x;
                                                position.mulProject(m.invert());

                                                shape.polygon.vertices[i] = position;
                                                reloadMesh();
                                            }
                                            vertexTool.initPosY = currentPosOnY;
                                        }
                                    }
                                }
                                break;
                                case 1:
                                    //y pos
                                {
                                    boolean failed = false;

                                    if ((mainView.camera.getWrappedRotation().y < 45f && mainView.camera.getWrappedRotation().y > -45f) ||
                                            (mainView.camera.getWrappedRotation().y > 135f && mainView.camera.getWrappedRotation().y < -135f))
                                    {
                                        Vector3f currentPosOnZ = mouseInput.mousePicker.getPointOnPlaneZ(worldPos.z);
                                        if(currentPosOnZ == null || vertexTool.initPosZ == null)
                                            failed = true;
                                        else
                                        {
                                            for (int i : this.selectedVertices)
                                            {
                                                Vector3f position = new Vector3f(shape.polygon.vertices[i]);

                                                Matrix4f m = new Matrix4f(entity.getTransformation());

                                                position.mulProject(m);
                                                position.y += currentPosOnZ.y - vertexTool.initPosZ.y;
                                                position.mulProject(m.invert());

                                                shape.polygon.vertices[i] = position;
                                                reloadMesh();
                                            }
                                            vertexTool.initPosZ = currentPosOnZ;
                                        }
                                    }
                                    else
                                        failed = true;

                                    if(failed)
                                    {
                                        Vector3f currentPosOnX = mouseInput.mousePicker.getPointOnPlaneX(worldPos.x);

                                        if(currentPosOnX != null && vertexTool.initPosX != null)
                                        {
                                            for (int i : this.selectedVertices)
                                            {
                                                Vector3f position = new Vector3f(shape.polygon.vertices[i]);

                                                Matrix4f m = new Matrix4f(entity.getTransformation());

                                                position.mulProject(m);
                                                position.y += currentPosOnX.y - vertexTool.initPosX.y;
                                                position.mulProject(m.invert());

                                                shape.polygon.vertices[i] = position;
                                                reloadMesh();
                                            }
                                            vertexTool.initPosX = currentPosOnX;
                                        }
                                    }
                                }
                                break;
                                case 5:
                                    //z rot
                                {

                                }
                                break;
                            }
                        }
                    }
                }
            }

        super.draw(mouseInput);
    }

    @Override
    public boolean onClick(MouseInput mouseInput, int button, int action, int mods) {
        boolean overOther = super.onClick(mouseInput, button, action, mods);

        for(Entity entity : mainView.things)
            if(entity.selected && ((Thing)entity).thing.hasPart(Part.SHAPE))
            {
                shape = ((Thing)entity).thing.getPart(Part.SHAPE);

                if(button == GLFW.GLFW_MOUSE_BUTTON_1)
                {
                    if(this.closest != -1 && action == GLFW.GLFW_PRESS)
                    {
                        if(mods == GLFW.GLFW_MOD_CONTROL)
                        {
                            if(this.selectedVertices.contains(this.closest))
                                this.selectedVertices.remove((Object)this.closest);
                            else
                                this.selectedVertices.add(this.closest);
                        }
                        else
                        {
                            boolean selectedPrev = this.selectedVertices.size() > 1 || !this.selectedVertices.contains(this.closest);
                            this.selectedVertices.clear();
                            if(selectedPrev)
                                this.selectedVertices.add(this.closest);
                        }
                    }
                    else if(vertexTool.onClick(mouseInput, button, action, mods, window, mainView.camera))
                    {}
                    else if(pickPoint != -1 && action == GLFW.GLFW_PRESS)
                    {
                        int newPickPoint = pickPoint;
                        for(int i = 0; i < pickLoop; i++)
                            newPickPoint += shape.polygon.loops[i];

                        Vector3f a = shape.polygon.vertices[newPickPoint];
                        Vector3f b = newPickPoint == shape.polygon.vertices.length - 1 ? shape.polygon.vertices[0] : shape.polygon.vertices[newPickPoint + 1];
                        Vector3f dir = (new Vector3f(b).sub(a)).normalize();
                        float dist = pickDist * a.distance(b);
                        Vector3f offset = new Vector3f(a).add(new Vector3f(dir).mul(dist));

                        Vector3f[] newVerts = new Vector3f[shape.polygon.vertices.length + 1];

                        for(int i = 0; i < shape.polygon.vertices.length; i++)
                        {
                            if(i < newPickPoint)
                                newVerts[i] = shape.polygon.vertices[i];
                            else if(i == newPickPoint)
                            {
                                newVerts[i] = shape.polygon.vertices[i];
                                newVerts[i + 1] = offset;
                            }
                            else if(i > newPickPoint)
                                newVerts[i + 1] = shape.polygon.vertices[i];
                        }

                        shape.polygon.vertices = newVerts;
                        shape.polygon.loops[pickLoop]++;
                        this.selectedVertices.clear();
                        this.selectedVertices.add(newPickPoint + 1);
                    }
                    else if(action == GLFW.GLFW_PRESS && !overOther)
                        this.selectedVertices.clear();
                }
            }

        return overOther;
    }

    @Override
    public boolean onKey(int key, int scancode, int action, int mods) {
        boolean elementFocused = super.onKey(key, scancode, action, mods);

        if((key == GLFW.GLFW_KEY_DELETE || key == GLFW.GLFW_KEY_BACKSPACE) && !elementFocused)
        {
            for(Entity entity : mainView.things)
                if(entity.selected && ((Thing)entity).thing.hasPart(Part.SHAPE))
                {
                    PShape shape = ((Thing)entity).thing.getPart(Part.SHAPE);

                    Collections.sort(this.selectedVertices, new Comparator<Integer>(){
                        @Override
                        public int compare(Integer o1, Integer o2) {
                            return o2.compareTo(o1);
                        }
                    });

                    for(int i = 0; i < this.selectedVertices.size(); i++)
                    {
                        int selectedVert = this.selectedVertices.get(i);
                        int loop = 0;

                        while(loop < shape.polygon.loops.length)
                        {
                            selectedVert -= shape.polygon.loops[loop];
                            if(selectedVert < 0)
                                break;
                            loop++;
                        }

                        if(shape.polygon.loops.length - 1 < loop)
                            continue;

                        ArrayList<Vector3f> verts = new ArrayList<>(List.of(shape.polygon.vertices));

                        if(shape.polygon.loops[loop] <= 3)
                        {
                            ArrayList<Integer> newLoops = new ArrayList<>();

                            int vert = 0;

                            for(int o = 0; o < loop; o++)
                                vert += shape.polygon.loops[o] - 1;

                            for(int o = vert + shape.polygon.loops[loop]; o > vert; o--)
                                verts.remove(o);

                            for(int j = 0; j < shape.polygon.loops.length; j++)
                                if(j != loop)
                                    newLoops.add(shape.polygon.loops[j]);

                            shape.polygon.loops = newLoops.stream().mapToInt(Integer::valueOf).toArray();
                        }
                        else
                        {
                            verts.remove((int)this.selectedVertices.get(i));
                            shape.polygon.loops[loop]--;
                        }

                        shape.polygon.vertices = verts.toArray(Vector3f[]::new);
                    }
                    this.selectedVertices.clear();
                }
            reloadMesh();
        }

        if(key == GLFW.GLFW_KEY_A && action == GLFW.GLFW_PRESS && mods == GLFW.GLFW_MOD_CONTROL && !elementFocused)
        {
            this.selectedVertices.clear();
            for(int i = 0; i < shape.polygon.vertices.length; i++)
                this.selectedVertices.add(i);
        }

        if(key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_PRESS && !elementFocused)
            mainView.setCurrentScreen(previousScreen);

        return elementFocused;
    }

    private void reloadMesh()
    {
        for(Entity entity : mainView.things)
            if(entity.selected && ((Thing)entity).thing.hasPart(Part.SHAPE))
                entity.reloadModel();
    }
}
