package bog.bgmaker.view3d.mainWindow.screens;

import bog.bgmaker.Main;
import bog.bgmaker.view3d.core.Model;
import bog.bgmaker.view3d.core.Transformation3D;
import bog.bgmaker.view3d.core.types.Entity;
import bog.bgmaker.view3d.core.types.MaterialPrimitive;
import bog.bgmaker.view3d.mainWindow.View3D;
import bog.bgmaker.view3d.managers.MouseInput;
import bog.bgmaker.view3d.renderer.gui.GuiScreen;
import bog.bgmaker.view3d.renderer.gui.cursor.ECursor;
import bog.bgmaker.view3d.renderer.gui.elements.Button;
import bog.bgmaker.view3d.renderer.gui.ingredients.LineStrip;
import bog.bgmaker.view3d.utils.Cursors;
import bog.bgmaker.view3d.utils.MousePicker;
import bog.bgmaker.view3d.utils.Utils;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

/**
 * @author Bog
 */
public class MaterialEditing extends GuiScreen {

    View3D mainView;

    public int cornerEdit;
    public int cornerPick;

    public MaterialEditing(View3D mainView)
    {
        super(mainView.renderer, mainView.loader, mainView.window);
        this.mainView = mainView;
        this.init();
    }

    Transformation3D.Tool vertexTool;

    public void init()
    {
        vertexTool = new Transformation3D.Tool(mainView.loader);

        try
        {
            cornerEdit = mainView.loader.loadTexture(ImageIO.read(Main.class.getResourceAsStream("/textures/corner edit.png")), GL11.GL_LINEAR_MIPMAP_LINEAR, GL11.GL_LINEAR);
        }catch (Exception e){e.printStackTrace();}
        try
        {
            cornerPick = mainView.loader.loadTexture(ImageIO.read(Main.class.getResourceAsStream("/textures/corner edit pick.png")), GL11.GL_LINEAR_MIPMAP_LINEAR, GL11.GL_LINEAR);
        }catch (Exception e){e.printStackTrace();}

        this.guiElements.add(new Button("backButton", "Back", new Vector2f(mainView.window.width - 150, mainView.window.height - 20), new Vector2f(150, 50), 10, mainView.renderer, mainView.loader, mainView.window) {
            @Override
            public void clickedButton(int button, int action, int mods) {
                mainView.setCurrentScreen(previousScreen);
            }

            @Override
            public void draw(MouseInput mouseInput, boolean overOther) {
                this.pos = new Vector2f(mainView.window.width - 150, mainView.window.height - 50);
                super.draw(mouseInput, overOther);
            }
        });
    }

    MaterialPrimitive material;
    Model[] lines;
    int pickPoint = -1;
    float pickDist;
    int pickLoop;

    @Override
    public void draw(MouseInput mouseInput) {

        pickPoint = -1;

        for(Entity entity : mainView.entities)
            if(entity.selected && entity.getType() == 1)
            {
                material = (MaterialPrimitive)entity;

                HashMap<Vector3f, Integer> screenPositions = new HashMap<>();
                Vector2f[][] lineVerts = new Vector2f[material.shape.polygon.loops.length][];

                int seg = 0;
                int lo = 0;

                for(int loop : material.shape.polygon.loops)
                {
                    lineVerts[seg] = new Vector2f[loop + 1];

                    for(int i = 0; i <= loop; i++)
                        if(i == loop)
                        {
                            lineVerts[seg][i] = lineVerts[seg][0];
                        }
                        else
                        {
                            Vector3f vertex = material.shape.polygon.vertices[lo + i];
                            Vector3f position = new Vector3f(vertex);
                            position.z += material.shape.thickness;
                            position.mulProject(entity.transformation, position);

                            Vector3f screenPosition = mainView.camera.worldToScreenPointF(position, mainView.window);
                            screenPositions.put(screenPosition, lo + i);
                            lineVerts[seg][i] = new Vector2f(screenPosition.x, screenPosition.y);

                            if(screenPosition.z == 0)
                                mainView.renderer.drawString("" + (lo + i), Color.white, (int) screenPosition.x + 8, (int) screenPosition.y + 8, 10);
                        }

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

                material.closest = -1;

                for(int i = 0; i < positions.size(); i++)
                {
                    Vector3f screenPoint = positions.get(i);
                    if(screenPoint.z == 0)
                    {
                        if (mouseInput.currentPos.distance(screenPoint.x, screenPoint.y) < 15 && !overOther)
                        {
                            material.closest = i;
                            overOther = true;
                        }
                        else
                        {
                            if(material.selectedVertices.contains(screenPositions.get(positions.get(i))))
                                mainView.renderer.drawImageStatic(cornerEdit, (int)(screenPoint.x - 7.5f), (int)(screenPoint.y - 7.5f), 15, 15, new Color(0.25f, 0.063f, 0.63f));
                            else
                                mainView.renderer.drawImageStatic(cornerEdit, (int)(screenPoint.x - 7.5f), (int)(screenPoint.y - 7.5f), 15, 15);
                        }
                    }
                }

                if(((Vector3f)screenPositions.keySet().stream().toArray()[0]).z == 0)
                {
                    if(lines != null && lines.length > 0)
                        for(Model l : lines)
                            if(l != null)
                                l.cleanup(mainView.loader);
                    if(lines == null)
                        lines = new Model[lineVerts.length];

                    Vector2f point = null;
                    Vector2f mousePoint = new Vector2f((float) mouseInput.currentPos.x, (float) mouseInput.currentPos.y);

                    for(int i = 0; i < lineVerts.length; i++)
                    {
                        if(i < lineVerts.length)
                        {
                            lines[i] = LineStrip.processVerts(lineVerts[i], mainView.loader, mainView.window);
                            mainView.renderer.processGuiElement(new LineStrip(new Vector2f(), lines[i], Color.white, true));
                        }

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
                                if(!overOther && point.distance(mousePoint) < 15)
                                    pickPoint = j;
                            }
                            else
                            if(p.distance(mousePoint) < point.distance(mousePoint))
                            {
                                point = p;
                                pickDist = ap/ab;
                                pickLoop = i;
                                if(!overOther && point.distance(mousePoint) < 15)
                                    pickPoint = j;
                            }
                        }
                    }

                    if(!overOther && point.distance(mousePoint) < 15)
                    {
                        mainView.renderer.drawImageStatic(cornerPick, (int)(point.x - 7.5f), (int)(point.y - 7.5f), 15, 15);
                        Cursors.setCursor(ECursor.copy);
                    }
                    else
                        pickPoint = -1;
                }

                if(material.closest != -1)
                {
                    int closest = screenPositions.get(positions.get(material.closest));
                    mainView.renderer.drawImageStatic(cornerEdit, (int) (positions.get(material.closest).x - 7.5f), (int) (positions.get(material.closest).y - 7.5f), 15, 15, material.selectedVertices.contains(closest) ? new Color(0f, 0f, 1f).darker() : new Color(0, 0, 0));
                    Cursors.setCursor(ECursor.hand2);
                    material.closest = closest;
                }

                if(material.selectedVertices != null && material.selectedVertices.size() > 0)
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
                            for(int index : material.selectedVertices)
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
                        for(int i : material.selectedVertices)
                        {
                            Vector3f vpos = material.shape.polygon.vertices[i];
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
                        MouseInput mi = new MouseInput(new View3D(mainView.window));
                        mi.previousPos = new Vector2d(screenPos.x, screenPos.y);
                        mi.currentPos = mi.previousPos;
                        MousePicker mp = new MousePicker(mi, mainView.window);
                        mp.update(mainView.camera);
                        Vector3f worldPos = mp.getPointOnPlaneZ(entity.transformation.getTranslation(new Vector3f()).z + material.shape.thickness);

                        vertexTool.updateModels(mainView, worldPos);
                        vertexTool.testForMouse(true, mainView.camera, mouseInput.mousePicker, true, false, false);
                        vertexTool.render(true, true, true, false, false, false, false, false, false, false, -1, mainView.window, mainView.loader, mainView.renderer, mouseInput);

                        if(vertexTool.selected != -1) {
                            switch (vertexTool.selected) {
                                //0,1,2 pos
                                //3,4,5 rot
                                //7,8,9 scale
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
                                            for (int i : material.selectedVertices)
                                            {
                                                Vector3f ppos = material.shape.polygon.vertices[i];
                                                material.shape.polygon.vertices[i] = new Vector3f(ppos.x + (currentPosOnZ.x - vertexTool.initPosZ.x), ppos.y, ppos.z);
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
                                            for (int i : material.selectedVertices)
                                            {
                                                Vector3f ppos = material.shape.polygon.vertices[i];
                                                material.shape.polygon.vertices[i] = new Vector3f(ppos.x + (currentPosOnY.x - vertexTool.initPosY.x), ppos.y, ppos.z);
                                            }
                                            vertexTool.initPosY = currentPosOnY;
                                        }
                                    }
                                    material.reloadModel();
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
                                            for (int i : material.selectedVertices)
                                            {
                                                Vector3f ppos = material.shape.polygon.vertices[i];
                                                material.shape.polygon.vertices[i] = new Vector3f(ppos.x, ppos.y + (currentPosOnZ.y - vertexTool.initPosZ.y), ppos.z);
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
                                            for (int i : material.selectedVertices)
                                            {
                                                Vector3f ppos = material.shape.polygon.vertices[i];
                                                material.shape.polygon.vertices[i] = new Vector3f(ppos.x, ppos.y + (currentPosOnX.y - vertexTool.initPosX.y), ppos.z);
                                            }
                                            vertexTool.initPosX = currentPosOnX;
                                        }
                                    }
                                    material.reloadModel();
                                }
                                break;
                                case 2:
                                    //z pos
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
        boolean onclick = super.onClick(mouseInput, button, action, mods);

        for(Entity entity : mainView.entities)
            if(entity.selected && entity.getType() == 1)
            {
                MaterialPrimitive material = (MaterialPrimitive) entity;

                    if(button == GLFW.GLFW_MOUSE_BUTTON_1)
                    {
                        if(material.closest != -1 && action == GLFW.GLFW_PRESS)
                        {
                            if(mods == GLFW.GLFW_MOD_CONTROL)
                            {
                                if(material.selectedVertices.contains(material.closest))
                                    material.selectedVertices.remove((Object)material.closest);
                                else
                                    material.selectedVertices.add(material.closest);
                            }
                            else
                            {
                                boolean selectedPrev = material.selectedVertices.size() > 1 || !material.selectedVertices.contains(material.closest);
                                material.selectedVertices.clear();
                                if(selectedPrev)
                                    material.selectedVertices.add(material.closest);
                            }
                        }
                        else if(vertexTool.onClick(mouseInput, button, action, mods, mainView.window, mainView.camera))
                        {}
                        else if(pickPoint != -1 && action == GLFW.GLFW_PRESS)
                        {
                            int newPickPoint = pickPoint;
                            for(int i = 0; i < pickLoop; i++)
                                newPickPoint += material.shape.polygon.loops[i];

                            Vector3f a = material.shape.polygon.vertices[newPickPoint];
                            Vector3f b = newPickPoint == material.shape.polygon.vertices.length - 1 ? material.shape.polygon.vertices[0] : material.shape.polygon.vertices[newPickPoint + 1];
                            Vector3f dir = (new Vector3f(b).sub(a)).normalize();
                            float dist = pickDist * a.distance(b);
                            Vector3f offset = new Vector3f(a).add(new Vector3f(dir).mul(dist));

                            Vector3f[] newVerts = new Vector3f[material.shape.polygon.vertices.length + 1];

                            for(int i = 0; i < material.shape.polygon.vertices.length; i++)
                            {
                                if(i < newPickPoint)
                                    newVerts[i] = material.shape.polygon.vertices[i];
                                else if(i == newPickPoint)
                                {
                                    newVerts[i] = material.shape.polygon.vertices[i];
                                    newVerts[i + 1] = offset;
                                }
                                else if(i > newPickPoint)
                                    newVerts[i + 1] = material.shape.polygon.vertices[i];
                            }

                            material.shape.polygon.vertices = newVerts;
                            material.shape.polygon.loops[pickLoop]++;
                            material.selectedVertices.clear();
                            material.selectedVertices.add(newPickPoint + 1);
                        }
                        else if(action == GLFW.GLFW_PRESS)
                            material.selectedVertices.clear();
                    }
            }

        return onclick;
    }
}
