package bog.bgmaker.view3d.mainWindow.screens;

import bog.bgmaker.Main;
import bog.bgmaker.view3d.core.Transformation3D;
import bog.bgmaker.view3d.core.types.Entity;
import bog.bgmaker.view3d.core.types.MaterialPrimitive;
import bog.bgmaker.view3d.mainWindow.View3D;
import bog.bgmaker.view3d.managers.MouseInput;
import bog.bgmaker.view3d.renderer.gui.GuiScreen;
import bog.bgmaker.view3d.renderer.gui.elements.Button;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * @author Bog
 */
public class MaterialEditing extends GuiScreen {

    View3D mainView;

    public int cornerEdit;

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

    @Override
    public void draw(MouseInput mouseInput) {

        for(Entity entity : mainView.entities)
            if(entity.selected && entity.getType() == 1)
            {
                material = (MaterialPrimitive)entity;

                HashMap<Vector3f, Integer> screenPositions = new HashMap<>();

                for(int i = 0; i < material.shape.polygon.vertices.length; i++)
                {
                    Vector3f vertex = material.shape.polygon.vertices[i];
                    Vector3f position = new Vector3f(vertex);
                    position.z += material.shape.thickness;
                    position.mulProject(entity.transformation, position);

                    Vector3f screenPosition = mainView.camera.worldToScreenPointF(position, mainView.window);
                    screenPositions.put(screenPosition, i);

                    if(screenPosition.z == 0)
                        drawString("" + i, Color.white, (int) screenPosition.x + 8, (int) screenPosition.y + 8, 10);
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
                        if (mouseInput.currentPos.distance(screenPoint.x, screenPoint.y) < 10 && !overOther)
                        {
                            material.closest = i;
                            overOther = true;
                        }
                        else
                        {
                            if(material.selectedVertices.contains(screenPositions.get(positions.get(i))))
                                drawImageStatic(cornerEdit, (int)(screenPoint.x - 7.5f), (int)(screenPoint.y - 7.5f), 15, 15, new Color(0.25f, 0.063f, 0.63f));
                            else
                                drawImageStatic(cornerEdit, (int)(screenPoint.x - 7.5f), (int)(screenPoint.y - 7.5f), 15, 15);
                        }
                    }
                }

                if(material.closest != -1)
                {
                    int closest = screenPositions.get(positions.get(material.closest));
                    drawImageStatic(cornerEdit, (int) (positions.get(material.closest).x - 7.5f), (int) (positions.get(material.closest).y - 7.5f), 15, 15, material.selectedVertices.contains(closest) ? new Color(0f, 0f, 1f).darker() : new Color(0, 0, 0));
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

                    if(screenPos != null)
                    {
                        vertexTool.updateModels(mainView.camera, screenPos, mainView.window);
                        vertexTool.testForMouse(true, mainView.camera, mainView.mousePicker, true, false, false);
                        vertexTool.render(true, true, true, false, false, false, false, false, false, false, mainView.crosshair, screenPos, mainView.window, mainView.loader, mainView.renderer, mouseInput);

                        if(vertexTool.selected != -1) {
                            switch (vertexTool.selected) {
                                //0,1,2 pos
                                //3,4,5 rot
                                //7,8,9 scale
                                case 0:
                                    //x pos
                                {
                                    float mousediff = (float) (mouseInput.currentPos.x - vertexTool.initPos.x);
                                    boolean h = false;

                                    if (mainView.camera.getWrappedRotation().y > 60f) {
                                        mousediff = -(float) (mouseInput.currentPos.y - vertexTool.initPos.y);
                                        h = true;
                                    }

                                    if (mainView.camera.getWrappedRotation().y < -60f) {
                                        mousediff = (float) (mouseInput.currentPos.y - vertexTool.initPos.y);
                                        h = true;
                                    }

                                    if (mainView.camera.getWrappedRotation().y > 120f || mainView.camera.getWrappedRotation().y < -120f) {
                                        mousediff = -(float) (mouseInput.currentPos.x - vertexTool.initPos.x);
                                        h = false;
                                    }

                                    for (int i : material.selectedVertices) {
                                        Vector3f ppos = material.shape.polygon.vertices[i];
                                        float diff = ppos.x - avgPos.x;
                                        material.shape.polygon.vertices[i] = new Vector3f(lastPos.x + diff + (mousediff * (mainView.camera.pos.distance(new Vector3f(lastPos)))) / (h ? 1000f : 4000f), ppos.y, ppos.z);
                                    }

                                    material.reloadModel();
                                }
                                break;
                                case 1:
                                    //y pos
                                {
                                    float mousediff = -(float) (mouseInput.currentPos.y - vertexTool.initPos.y);

                                    boolean h = false;

                                    if (mainView.camera.getWrappedRotation().x > 20f)
                                        h = true;

                                    if (mainView.camera.getWrappedRotation().x < -20f)
                                        h = true;

                                    for (int i : material.selectedVertices) {
                                        Vector3f ppos = material.shape.polygon.vertices[i];
                                        float diff = ppos.y - avgPos.y;
                                        material.shape.polygon.vertices[i] = new Vector3f(ppos.x, lastPos.y + diff + (mousediff * (mainView.camera.pos.distance(new Vector3f(lastPos)))) / (h ? 1000f : 4000f), ppos.z);
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

    public Vector3f lastPos = new Vector3f();

    @Override
    public boolean onClick(Vector2d pos, int button, int action, int mods) {
        boolean onclick = super.onClick(pos, button, action, mods);

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
                        else if(vertexTool.onClick(pos, button, action, mods, mainView.window, mainView.camera))
                        {
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
                                lastPos = new Vector3f(x/amount, y/amount, z/amount);
                            }
                            catch (Exception e){e.printStackTrace();}
                        }
                        else if(action == GLFW.GLFW_PRESS)
                            material.selectedVertices.clear();
                    }
            }

        return onclick;
    }
}
