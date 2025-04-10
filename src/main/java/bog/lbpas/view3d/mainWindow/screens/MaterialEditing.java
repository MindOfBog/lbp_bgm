package bog.lbpas.view3d.mainWindow.screens;

import bog.lbpas.view3d.core.Model;
import bog.lbpas.view3d.core.Transformation3D;
import bog.lbpas.view3d.core.types.Entity;
import bog.lbpas.view3d.core.types.Thing;
import bog.lbpas.view3d.mainWindow.ConstantTextures;
import bog.lbpas.view3d.mainWindow.View3D;
import bog.lbpas.view3d.managers.MouseInput;
import bog.lbpas.view3d.renderer.gui.GuiScreen;
import bog.lbpas.view3d.renderer.gui.cursor.ECursor;
import bog.lbpas.view3d.renderer.gui.elements.*;
import bog.lbpas.view3d.renderer.gui.elements.Button;
import bog.lbpas.view3d.renderer.gui.elements.Panel;
import bog.lbpas.view3d.renderer.gui.ingredients.LineStrip;
import bog.lbpas.view3d.utils.*;
import cwlib.enums.Part;
import cwlib.resources.RBevel;
import cwlib.structs.bevel.BevelVertex;
import cwlib.structs.things.parts.PGeneratedMesh;
import cwlib.structs.things.parts.PShape;
import org.joml.*;
import org.joml.Math;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

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

    public Transformation3D.Tool vertexTool;

    Textbox xPos;
    Textbox yPos;

    ButtonImage frontView;

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
                return "Spongy: " + bev.spongy;
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

        frontView = new ButtonImage("frontView", new Vector2f(window.width - 40, 21 + 10), new Vector2f(30, 30), new Vector2f(26, 24), renderer, loader, window) {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(action == GLFW.GLFW_PRESS)
                {
                    Config.FRONT_VIEW = !Config.FRONT_VIEW;
                    this.buttonImage = Config.FRONT_VIEW ? ConstantTextures.getTexture(ConstantTextures.FRONT_VIEW, org.joml.Math.round(this.imageSize.x), org.joml.Math.round(this.imageSize.y), loader) : ConstantTextures.getTexture(ConstantTextures.FRONT_VIEW_OFF, org.joml.Math.round(this.imageSize.x), org.joml.Math.round(this.imageSize.y), loader);
                }
            }

            @Override
            public void secondThread() {
                super.secondThread();

                this.pos.x = window.width - 40;
            }

            @Override
            public void resize() {
                super.resize();

                this.pos.x = window.width - 40;
            }

            @Override
            public void getImage() {
                buttonImage = Config.FRONT_VIEW ? ConstantTextures.getTexture(ConstantTextures.FRONT_VIEW, org.joml.Math.round(this.imageSize.x), org.joml.Math.round(this.imageSize.y), loader) : ConstantTextures.getTexture(ConstantTextures.FRONT_VIEW_OFF, org.joml.Math.round(this.imageSize.x), Math.round(this.imageSize.y), loader);
            }
        };

        this.guiElements.add(frontView);
    }

    PShape shape;
    PGeneratedMesh generatedMesh;
    int pickPoint = -1;
    float pickDist;
    int pickLoop;

    RBevel bev;

    ArrayList<Model> lineStrips;

    @Override
    public void draw(MouseInput mouseInput) {

        pickPoint = -1;

        for(Entity entity : mainView.things)
            if(entity.selected && ((Thing)entity).thing.hasPart(Part.SHAPE))
            {
                shape = ((Thing)entity).thing.getPart(Part.SHAPE);
                generatedMesh = ((Thing)entity).thing.getPart(Part.GENERATED_MESH);
                bev = ((Thing)entity).bevelData;

                for(int i = 0; i < bev.vertices.size(); i++)
                {
                    BevelVertex v = bev.vertices.get(i);

                    renderer.drawString(v.mappingMode.name(), Color.white, 10, 30 + (3 + getFontHeight(10))*i, 10);
                }

                if(this.selectedVertices.size() > 0)
                {
                    float x = 0;
                    float y = 0;

                    for(int index : this.selectedVertices)
                    {
                        Vector3f vert = new Vector3f(shape.polygon.vertices[index]).mulProject(entity.getTransformation(), new Vector3f());
                        x += vert.x;
                        y += vert.y;
                    }

                    x /= selectedVertices.size();
                    y /= selectedVertices.size();

                    Vector2f xp = xPos.setTextboxValueFloat(Utils.round(x, 3));
                    if (xp.y == 1)
                        for(int index : this.selectedVertices)
                        {
                            Vector3f vpos = new Vector3f(shape.polygon.vertices[index]).mulProject(entity.getTransformation(), new Vector3f());
                            Vector3f offset = vpos.sub(new Vector3f(x, y, vpos.z), new Vector3f());
                            shape.polygon.vertices[index] = new Vector3f(xp.x + offset.x, vpos.y, vpos.z).mulProject(new Matrix4f(entity.getTransformation()).invert());
                        }
                    Vector2f yp = yPos.setTextboxValueFloat(Utils.round(y, 3));
                    if (yp.y == 1)
                        for(int index : this.selectedVertices)
                        {
                            Vector3f vpos = new Vector3f(shape.polygon.vertices[index]).mulProject(entity.getTransformation(), new Vector3f());
                            Vector3f offset = vpos.sub(new Vector3f(x, y, vpos.z), new Vector3f());
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
                Vector3f[][] lineVerts = new Vector3f[shape.polygon.loops.length][];

                int seg = 0;
                int lo = 0;

                if(lineStrips != null)
                    for(Model m : lineStrips)
                        m.cleanup(loader);

                lineStrips = new ArrayList<>();

                for(int loop : shape.polygon.loops)
                {
                    lineVerts[seg] = new Vector3f[loop + 1];

                    float[] verts = new float[(loop + 1) * 6];

                    ArrayList<Integer> indices = new ArrayList<>();
                    ArrayList<Integer> indices2 = new ArrayList<>();

                    boolean back = false;

                    if((loop - 1) % 2 != 0)
                    {
                        indices.add(0);
                        indices.add(1);
                    }

                    for(int i = 0; i < loop; i++)
                    {
                        Vector3f vertex = shape.polygon.vertices[lo + i];
                        Vector3f position = new Vector3f(vertex);
                        position.mulProject(entity.getTransformation(), position);
                        position.z = entity.getTransformation().getTranslation(new Vector3f()).z + shape.thickness;

                        if(back)
                        {
                            verts[i * 6 + 0] = position.x;
                            verts[i * 6 + 1] = position.y;
                            verts[i * 6 + 2] = entity.getTransformation().getTranslation(new Vector3f()).z - shape.thickness;

                            verts[i * 6 + 3] = position.x;
                            verts[i * 6 + 4] = position.y;
                            verts[i * 6 + 5] = position.z;
                        }
                        else
                        {
                            verts[i * 6] = position.x;
                            verts[i * 6 + 1] = position.y;
                            verts[i * 6 + 2] = position.z;

                            verts[i * 6 + 3] = position.x;
                            verts[i * 6 + 4] = position.y;
                            verts[i * 6 + 5] = entity.getTransformation().getTranslation(new Vector3f()).z - shape.thickness;
                        }

                        indices.add(i * 2);
                        indices.add(i * 2 + 1);

                        indices2.add(i * 2 + 1);
                        indices2.add(i * 2);

                        if(i == loop - 1)
                        {
                            indices.add(i % 2 == 0 ? 1 : 0);
                            indices2.add(i % 2 == 0 ? 0 : 1);
                        }

                        back = !back;

                        Vector3f screenPosition = mainView.camera.worldToScreen(position, window);
                        screenPositions.put(screenPosition, lo + i);
                        lineVerts[seg][i] = new Vector3f(screenPosition);

                        if(screenPosition.z == 0)
                        {
                            renderer.drawRect((int) screenPosition.x + 10, (int) screenPosition.y + 10, getStringWidth("" + (lo + i), 10) + 2, getFontHeight(10) + 2, new Color(0f, 0f, 0f, 0.5f));
                            renderer.drawString("" + (lo + i), Color.white, (int) screenPosition.x + 12, (int) screenPosition.y + 12, 10);
                        }
                    }

                    indices.addAll(indices2);

                    Model m = new Model();
                    m.primitive = GL11.GL_LINE_STRIP;
                    loader.loadModel(m, verts, indices.stream().mapToInt(Integer::valueOf).toArray());
                    m.material.setColor(Config.OUTLINE_COLOR);

                    lineStrips.add(m);

                    renderer.processThroughWallEntity(new Entity(m, new Matrix4f().identity(), loader));

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
                                renderer.drawImageStatic(ConstantTextures.getTexture(ConstantTextures.CORNER_EDIT, 20, 20, loader), (int)(screenPoint.x - 10), (int)(screenPoint.y - 10), 20, 20, Config.OUTLINE_COLOR);
                            else
                                renderer.drawImageStatic(ConstantTextures.getTexture(ConstantTextures.CORNER_EDIT_PICK, 16, 16, loader), (int)(screenPoint.x - 8), (int)(screenPoint.y - 8), 16, 16, Config.OUTLINE_COLOR.darker().darker());
                        }
                    }
                }

                if(((Vector3f)screenPositions.keySet().stream().toArray()[0]).z == 0)
                {
                    Vector2f point = null;
                    Vector2f mousePoint = new Vector2f((float) mouseInput.currentPos.x, (float) mouseInput.currentPos.y);

                    for(int i = 0; i < lineVerts.length; i++)
                    {
                        for(int j = 0; j < lineVerts[i].length - 1; j++)
                        {
                            Vector2f a = new Vector2f(lineVerts[i][j].x, lineVerts[i][j].y);
                            Vector2f b = new Vector2f(lineVerts[i][j + 1].x, lineVerts[i][j + 1].y);

                            if(lineVerts[i][j].z != 0 || lineVerts[i][j + 1].z != 0)
                                continue;

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

                    if(!overOther && point != null && point.distance(mousePoint) < 15 && !(vertexTool.isHovering() && selectedVertices != null && selectedVertices.size() > 0) && !(vertexTool.isSelected() && this.selectedVertices != null && this.selectedVertices.size() > 0))
                    {
                        renderer.drawImageStatic(ConstantTextures.getTexture(ConstantTextures.CORNER_EDIT_PICK, 30, 30, loader), (int)(point.x - 15), (int)(point.y - 15), 30, 30, Config.OUTLINE_COLOR);
                        Cursors.setCursor(ECursor.copy);
                    }
                    else
                        pickPoint = -1;
                }

                if(closest != -1)
                {
                    int closest = screenPositions.get(positions.get(this.closest));
                    renderer.drawImageStatic(this.selectedVertices.contains(closest) ? ConstantTextures.getTexture(ConstantTextures.CORNER_EDIT, 30, 30, loader) : ConstantTextures.getTexture(ConstantTextures.CORNER_EDIT_PICK, 30, 30, loader), (int) (positions.get(this.closest).x - 15), (int) (positions.get(this.closest).y - 15), 30, 30, Config.OUTLINE_COLOR);
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
                        for(Vector3f pos : screenPositions.keySet())
                            for(int index : this.selectedVertices)
                                if(screenPositions.get(pos) == index)
                                {
                                    x += pos.x;
                                    y += pos.y;
                                    z += pos.z;
                                }
                        screenPos = new Vector3f(x/this.selectedVertices.size(), y/this.selectedVertices.size(), z/this.selectedVertices.size());
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
                        Vector3f worldPos = new Vector3f();

                        for (int i : this.selectedVertices)
                        {
                            Vector3f position = new Vector3f(shape.polygon.vertices[i]);

                            Matrix4f m = new Matrix4f(entity.getTransformation());

                            position.mulProject(m);

                            worldPos.add(position);
                        }

                        worldPos.div(this.selectedVertices.size());
                        worldPos.add(0, 0, shape.thickness);

                        vertexTool.updateModels(mainView, worldPos);
                        vertexTool.testForMouse(true, mainView.camera, mouseInput.mousePicker, true, false, false);
//                        vertexTool.render(true, true, true, false, false, false, true, true, true, false, true, -1, window, loader, renderer, mouseInput);
                        vertexTool.render(true, true, true, false, false, false, false, false, false, false, false, -1, window, loader, renderer, mouseInput);

                        if(vertexTool.isSelected()) {
                            switch(Transformation3D.ToolType.fromValue(vertexTool.selected)) {
                                case POSITION_X:
                                {
                                    if(Config.FRONT_VIEW)
                                    {
                                        Vector3f currentPosOnZ = mouseInput.mousePicker.getPointOnPlaneZ(worldPos.z);

                                        if(currentPosOnZ != null)
                                        {
                                            for (int i : this.selectedVertices)
                                            {
                                                Vector3f position = new Vector3f(shape.polygon.vertices[i]);

                                                Matrix4f m = new Matrix4f(entity.getTransformation());

                                                position.mulProject(m);
                                                position.x += currentPosOnZ.x - vertexTool.initPosXY.x;
                                                position.mulProject(m.invert());

                                                shape.polygon.vertices[i] = position;
                                                reloadMesh();
                                            }

                                            vertexTool.initPosXY = currentPosOnZ;
                                        }
                                    }
                                    else
                                    {
                                        Vector3f normal = new Vector3f(new Vector3f(0, mainView.camera.getPos().y, mainView.camera.getPos().z)).sub(new Vector3f(0, worldPos.y, worldPos.z)).normalize();
                                        Vector3f intersection = mouseInput.mousePicker.getPointOnPlaneAbstract(worldPos, normal);

                                        if(intersection != null)
                                        {
                                            Vector3f pos = Utils.getClosestPointOnLine(worldPos, new Vector3f(1, 0, 0), intersection);

                                            for (int i : this.selectedVertices)
                                            {
                                                Vector3f position = new Vector3f(shape.polygon.vertices[i]);

                                                Matrix4f m = new Matrix4f(entity.getTransformation());

                                                position.mulProject(m);
                                                position.x += (pos.x - vertexTool.initPosX.x);
                                                position.mulProject(m.invert());

                                                shape.polygon.vertices[i] = position;
                                                reloadMesh();
                                            }
                                            vertexTool.initPosX = pos;
                                        }
                                    }
                                }
                                break;
                                case POSITION_Y:
                                {
                                    if(Config.FRONT_VIEW)
                                    {
                                        Vector3f currentPosOnZ = mouseInput.mousePicker.getPointOnPlaneZ(worldPos.z);

                                        if(currentPosOnZ != null)
                                        {
                                            for (int i : this.selectedVertices)
                                            {
                                                Vector3f position = new Vector3f(shape.polygon.vertices[i]);

                                                Matrix4f m = new Matrix4f(entity.getTransformation());

                                                position.mulProject(m);
                                                position.y += currentPosOnZ.y - vertexTool.initPosXY.y;
                                                position.mulProject(m.invert());

                                                shape.polygon.vertices[i] = position;
                                                reloadMesh();
                                            }

                                            vertexTool.initPosXY = currentPosOnZ;
                                        }
                                    }
                                    else
                                    {
                                        Vector3f normal = new Vector3f(new Vector3f(mainView.camera.getPos().x, 0, mainView.camera.getPos().z)).sub(new Vector3f(worldPos.x, 0, worldPos.z)).normalize();
                                        Vector3f intersection = mouseInput.mousePicker.getPointOnPlaneAbstract(worldPos, normal);

                                        if(intersection != null)
                                        {
                                            Vector3f pos = Utils.getClosestPointOnLine(worldPos, new Vector3f(0, 1, 0), intersection);

                                            for (int i : this.selectedVertices)
                                            {
                                                Vector3f position = new Vector3f(shape.polygon.vertices[i]);

                                                Matrix4f m = new Matrix4f(entity.getTransformation());

                                                position.mulProject(m);
                                                position.y += (pos.y - vertexTool.initPosY.y);
                                                position.mulProject(m.invert());

                                                shape.polygon.vertices[i] = position;
                                                reloadMesh();
                                            }
                                            vertexTool.initPosY = pos;
                                        }
                                    }
                                }
                                break;
                                case POSITION_XY:
                                {
                                    Vector3f currentPosOnZ = mouseInput.mousePicker.getPointOnPlaneZ(worldPos.z);

                                    if(currentPosOnZ != null)
                                    {
                                        for (int i : this.selectedVertices)
                                        {
                                            Vector3f position = new Vector3f(shape.polygon.vertices[i]);

                                            Matrix4f m = new Matrix4f(entity.getTransformation());

                                            position.mulProject(m);
                                            position.x += currentPosOnZ.x - vertexTool.initPosXY.x;
                                            position.y += currentPosOnZ.y - vertexTool.initPosXY.y;
                                            position.mulProject(m.invert());

                                            shape.polygon.vertices[i] = position;
                                            reloadMesh();
                                        }

                                        vertexTool.initPosXY = currentPosOnZ;
                                    }
                                }
                                break;
                                case ROTATION_Z:
                                {

                                }
                                break;
                            }
                        }
                    }
                }
                else
                    vertexTool.testForMouse(false, mainView.camera, mouseInput.mousePicker, true, false, false);
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
                        int loop = 0;
                        for(int i = 0; i < pickLoop; i++)
                            loop += shape.polygon.loops[i];

                        newPickPoint += loop;

                        int nextPoint = newPickPoint + 1;

                        if(nextPoint >= loop + shape.polygon.loops[pickLoop])
                            nextPoint = loop;

                        Vector3f a = shape.polygon.vertices[newPickPoint];
                        Vector3f b = shape.polygon.vertices[nextPoint];
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

                    if(action == GLFW.GLFW_RELEASE)
                        entity.reloadModel();
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
