package bog.lbpas.view3d.utils;

import bog.lbpas.view3d.core.Texture;
import bog.lbpas.view3d.mainWindow.ConstantTextures;
import bog.lbpas.view3d.mainWindow.LoadedData;
import bog.lbpas.view3d.mainWindow.View3D;
import bog.lbpas.view3d.renderer.gui.elements.*;
import bog.lbpas.view3d.renderer.gui.elements.Button;
import bog.lbpas.view3d.renderer.gui.elements.Image;
import bog.lbpas.view3d.renderer.gui.elements.Panel;
import common.FileChooser;
import cwlib.types.archives.FileArchive;
import cwlib.types.databases.FileDB;
import cwlib.types.databases.FileDBRow;
import cwlib.types.save.BigSave;
import org.joml.Math;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;

public class FilePicker {

    private static boolean loadMap = false;
    private static boolean loadFarc = false;
    private static boolean loadFart = false;
    private static boolean saveMod = false;
    private static boolean selectProjectLocation = false;
    private static String loadProjectAssetsPath = null;
    private static FileTree.TreeFolder loadProjectAssetsFolder = null;
    private static FileTree.TreeItem replaceProjectItem = null;
    private static boolean loadProjectTextures = false;

    public static void loadingThread(View3D mainView)
    {
        if(mainView == null)
            return;

        if(loadMap)
        {
            try {
                File maps[] = FileChooser.openFiles("map");

                if(maps != null && maps.length != 0)
                    for(File file : maps)
                        LoadedData.MAPs.add(new FileDB(file));

                LoadedData.shouldSetupList = true;
            } catch (Exception ex) {
//                mainView.pushWarning("File Loading", ex.getMessage());
            }
            loadMap = false;
        }

        if(loadFarc)
        {
            try {
                File[] farcs = FileChooser.openFiles("farc");
                if (farcs != null && farcs.length != 0)
                        for (File farc : farcs)
                        {
                            FileArchive archive = null;
                                archive = new FileArchive(farc);

                            if (archive != null)
                                LoadedData.FARCs.add(archive);
                        }

                LoadedData.shouldSetupList = true;
            } catch (Exception ex) {
//                mainView.pushWarning("File Loading", ex.getMessage());
            }

            loadFarc = false;
        }

        if(loadFart)
        {
            try
            {
                File[] farts = FileChooser.openFiles(null);

                if(farts != null && farts.length != 0)
                    for(File file : farts)
                        LoadedData.BIGFARTs.add(new BigSave(file));

                LoadedData.shouldSetupList = true;
            } catch(Exception ex) {
//                mainView.pushWarning("File Loading", ex.getMessage());
            }

            loadFart = false;
        }

        if(saveMod)
        {
            try {
                File[] mod = FileChooser.openFiles(mainView.ProjectManager.modFileTree.root.itemName.getText(), "mod", true, false);
                LoadedData.PROJECT_DATA.save(mod[0]);
            } catch (Exception ex) {
//                mainView.pushWarning("File Loading", ex.getMessage());
            }

            saveMod = false;
        }

        if(selectProjectLocation)
        {
            try
            {
                File[] file = FileChooser.openFiles(mainView.ProjectManager.projectSavePath.getText(), "jar", true, false);
                mainView.ProjectManager.projectSavePath.setText(file[0].getPath());
            } catch(Exception ex) {
//                mainView.pushWarning("File Loading", ex.getMessage());
            }

            selectProjectLocation = false;
        }

        if(loadProjectAssetsPath != null)
        {
            try
            {
                File[] assets = FileChooser.openFiles("slt,tex,mol,msh,gmat,gmt,mat,ff,fsh,plan,pln,pal,oft,sph,bin,vpo,fpo,anim,anm,bev,smh,mus,fsb,txt,bpr,ipr");
                if(assets != null && assets.length != 0)
                {
                    for(File file : assets)
                    {
                        FileDBRow row = LoadedData.PROJECT_DATA.add(loadProjectAssetsPath + file.getName(), Files.readAllBytes(file.toPath()));
                        FileTree.TreeItem newItem = loadProjectAssetsFolder.addItem(String.valueOf(loadProjectAssetsFolder.children.size()), row, file.getName(), loadProjectAssetsFolder.size.y);
                    }

                    LoadedData.shouldSetupList = true;
                }
            } catch(Exception ex) {
//                mainView.pushWarning("File Loading", ex.getMessage());
            }

            loadProjectAssetsPath = null;
            loadProjectAssetsFolder = null;
        }

        if(replaceProjectItem != null)
        {
            try
            {
                File item = FileChooser.openFiles(null, (replaceProjectItem.itemName.getText().contains(".") ? replaceProjectItem.itemName.getText().substring(replaceProjectItem.itemName.getText().lastIndexOf(".") + 1) : ""), false, false)[0];
                if(item != null)
                {
                    FileDBRow row = ((FileDBRow)replaceProjectItem.item);
                    byte[] bytes = Files.readAllBytes(item.toPath());
                    row.setDetails(bytes);
                    LoadedData.PROJECT_DATA.add(bytes);

                    LoadedData.shouldSetupList = true;
                }
            } catch(Exception ex) {
//                mainView.pushWarning("File Loading", ex.getMessage());
            }

            replaceProjectItem = null;
        }

        if(loadProjectTextures)
        {
            try {
                File texs[] = FileChooser.openFiles("png,jpg,jpeg,dds");

                if(texs != null && texs.length != 0)
                    for(File file : texs)
                    {
                        int ind = mainView.ProjectManager.textureListTexImport.elements.size();
                        BufferedImage img = ImageIO.read(file);
                        Texture texture = new Texture(img);
                        mainView.ProjectManager.listTexImport.add(texture);

                        ElementList list = mainView.ProjectManager.textureListTexImport;
                        Panel panel = list.addPanel(String.valueOf(ind));

                        float gap = 2 / list.size.x;

                        panel.elements.add(new Panel.PanelElement(new Image(new Vector2f(), null, mainView.renderer, mainView.loader, mainView.window) {
                            @Override
                            public Texture getImage() {
                                return texture;
                            }
                        }, 1f - 0.2f - gap * 2));

                        panel.elements.add(new Panel.PanelElement(null, gap));

                        ComboBoxImage optionsCombo = new ComboBoxImage("optionsCombo", 200, new Vector2f(23, 23), mainView.renderer, mainView.loader, mainView.window) {
                            @Override
                            public Texture getImage() {
                                return ConstantTextures.getTexture(ConstantTextures.OPTIONS, 23, 23, loader);
                            }

                            @Override
                            public int[] getParentTransform() {
                                return list.getParentTransform();
                            }
                        };

                        String fileName = file.getName();
                        if(fileName.contains("."))
                            fileName = fileName.substring(0, fileName.lastIndexOf("."));
                        fileName += ".tex";
                        optionsCombo.addTextbox("name", fileName);
                        optionsCombo.addButton("Normal2Bump", new Button() {
                            @Override
                            public void clickedButton(int button, int action, int mods) {

                                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS) {
                                    BufferedImage bump = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
                                    for (int x = 0; x < img.getWidth(); x++)
                                        for (int y = 0; y < img.getHeight(); y++) {
                                            int r = new Color(img.getRGB(x, y)).getRed();
                                            int g = new Color(img.getRGB(x, y)).getGreen();
                                            bump.setRGB(x, y, new Color(g, g, g, r).getRGB());
                                        }
                                    texture.image = bump;
                                    texture.cleanup();
                                }
                            }
                        });

                        panel.elements.add(new Panel.PanelElement(optionsCombo, 0.1f));

                        panel.elements.add(new Panel.PanelElement(null, gap));

                        panel.elements.add(new Panel.PanelElement(new ButtonImage("closeButton", new Vector2f(0), new Vector2f(22, 22), new Vector2f(23, 23), mainView.renderer, mainView.loader, mainView.window) {
                            @Override
                            public void clickedButton(int button, int action, int mods) {
                                mainView.ProjectManager.listTexImport.remove(texture);
                                texture.cleanup();
                                list.elements.remove(panel);
                            }

                            @Override
                            public Texture getImage() {
                                return ConstantTextures.getTexture(ConstantTextures.WINDOW_CLOSE, 23, 23, loader);
                            }
                        }, 0.1f));
                        panel.size.y = 60;
                    }

            } catch (Exception ex) {
                mainView.pushWarning("File Loading", ex.getMessage());
                print.stackTrace(ex);
            }
            loadProjectTextures = false;
        }
    }

    public static void loadMap()
    {
        loadMap = true;
    }

    public static void loadFarc()
    {
        loadFarc = true;
    }
    public static void saveMod()
    {
        saveMod = true;
    }

    public static void loadFart()
    {
        loadFart = true;
    }

    public static void selectProjectLocation()
    {
        selectProjectLocation = true;
    }
    public static void loadProjectAssets(String path, FileTree.TreeFolder treeFolder)
    {
        loadProjectAssetsPath = path;
        loadProjectAssetsFolder = treeFolder;
    }

    public static void replaceProjectItem(FileTree.TreeItem item)
    {
        replaceProjectItem = item;
    }

    public static void loadProjectTextures()
    {
        loadProjectTextures = true;
    }
}
