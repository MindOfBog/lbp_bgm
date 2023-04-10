package bog.bgmaker.view3d.mainWindow;

import bog.bgmaker.view3d.ObjectLoader;
import bog.bgmaker.view3d.core.Material;
import bog.bgmaker.view3d.core.Model;
import bog.bgmaker.view3d.core.Texture;
import bog.bgmaker.view3d.managers.ShaderMan;
import bog.bgmaker.view3d.utils.Utils;
import cwlib.enums.BoxType;
import cwlib.enums.GfxMaterialFlags;
import cwlib.resources.RGfxMaterial;
import cwlib.resources.RMesh;
import cwlib.resources.RTexture;
import cwlib.structs.gmat.MaterialBox;
import cwlib.types.Resource;
import cwlib.types.archives.FileArchive;
import cwlib.types.data.ResourceDescriptor;
import cwlib.types.data.SHA1;
import cwlib.types.databases.FileDB;
import cwlib.types.databases.FileDBRow;
import cwlib.types.save.BigSave;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Bog
 */
public class LoadedData {

    public static FileDB MAP = null;
    public static ArrayList<FileArchive> FARCs;
    public static BigSave BIGFART = null;

    public static HashMap<ResourceDescriptor, ArrayList<Model>> loadedModels;
    public static HashMap<ResourceDescriptor, Material> loadedGfxMaterials;
    public static HashMap<ResourceDescriptor, Texture> loadedTextures;

    public static Texture missingTexture;

    public static void init(ObjectLoader loader)
    {
        try
        {
            BufferedImage img = new BufferedImage(254, 254, BufferedImage.TYPE_INT_ARGB);
            for (int x = 0; x < img.getWidth(); x++)
                for (int y = 0; y < img.getHeight(); y++)
                    img.setRGB(x, y, new Color((float)(x / 255f), (float)(y / 255f), 0f, 0.5f).getRGB());

            missingTexture = new Texture(loader.loadTexture(img, GL11.GL_LINEAR_MIPMAP_LINEAR, GL11.GL_LINEAR));
        }catch (Exception e){}
    }

    public static RMesh loadMesh(ResourceDescriptor descriptor) {
        if (descriptor == null) return null;

        byte[] data = extract(descriptor);
        if (data == null) return null;
        RMesh mesh = null;
        try { mesh = new Resource(data).loadResource(RMesh.class); }
        catch (Exception ex) { return null; }

        return mesh;
    }

    public static RGfxMaterial loadGfxMaterial(ResourceDescriptor descriptor) {
        if (descriptor == null) return null;

        byte[] data = extract(descriptor);
        if (data == null) return null;
        RGfxMaterial material = null;
        try { material = new Resource(data).loadResource(RGfxMaterial.class); }
        catch (Exception ex) { return null; }

        return material;
    }

    public static BufferedImage loadTexture(ResourceDescriptor descriptor) {
        if (descriptor == null) return null;

        byte[] data = extract(descriptor);
        if (data == null) return null;
        BufferedImage texture = null;
        try {
            RTexture resource = new RTexture(new Resource(data));
            texture = resource.getImage();
        }
        catch (Exception ex) { return null; }

        return texture;
    }

    public static byte[] extract(ResourceDescriptor descriptor) {
        if (descriptor == null) return null;

        SHA1 sha1;
        if (descriptor.isGUID()) {
            FileDBRow row = MAP.get(descriptor.getGUID());
            if (row == null) return null;
            sha1 = row.getSHA1();
        } else sha1 = descriptor.getSHA1();

        for (FileArchive archive : FARCs) {
            if (archive.exists(sha1))
                return archive.extract(sha1);
        }

        return BIGFART.extract(sha1);
    }

    public static Material getMaterial(ResourceDescriptor matDescriptor, ObjectLoader loader) throws Exception {
        if(!LoadedData.loadedGfxMaterials.containsKey(matDescriptor))
        {
            boolean culling = false;
            RGfxMaterial material = LoadedData.loadGfxMaterial(matDescriptor);

            Material generatedMat = null;

            ShaderMan generatedShader = new ShaderMan();
            generatedShader.createVertexShader(Utils.loadResource("/shaders/vertex.glsl"));

            ArrayList<Texture> textures = new ArrayList<>();
            ArrayList<Vector4f> colors = new ArrayList<>();
            String shaderColor = null;

            if (material != null) {
                int output = material.getOutputBox();
                MaterialBox outBox = material.getBoxConnectedToPort(output, 0);

                shaderColor = "ambientC = vec4(" + buildColor(outBox, material, textures, colors, loader) + ");" +
                        "        if(highlightMode == 1)" +
                        "        {" +
                        "            float mul = (ambientC.r + ambientC.g + ambientC.b) / 3.0;" +
                        "            ambientC = vec4(highlightColor.r * mul, highlightColor.g * mul, highlightColor.b * mul, ambientC.a * highlightColor.a);" +
                        "        }" +
                        "        else if(highlightMode == 2)" +
                        "        {" +
                        "            ambientC.r = ambientC.r * brightnessMul;" +
                        "            ambientC.g = ambientC.g * brightnessMul;" +
                        "            ambientC.b = ambientC.b * brightnessMul;" +
                        "        }" +
                        "        diffuseC = ambientC;" +
                        "        specularC = ambientC;" +
                        "    if(material.hasTexture != 1){" +
                        "        ambientC = material.ambient[0];" +
                        "        diffuseC = material.diffuse[0];" +
                        "        specularC = material.specular[0];" +
                        "        if(highlightMode == 1)" +
                        "        {" +
                        "            float mul = (ambientC.r + ambientC.g + ambientC.b) / 3.0;" +
                        "            ambientC = vec4(highlightColor.r * mul, highlightColor.g * mul, highlightColor.b * mul, ambientC.a * highlightColor.a);" +
                        "        }" +
                        "        else if(highlightMode == 2 && samplerCount != -5)" +
                        "        {" +
                        "            ambientC.r = ambientC.r * brightnessMul;" +
                        "            ambientC.g = ambientC.g * brightnessMul;" +
                        "            ambientC.b = ambientC.b * brightnessMul;" +
                        "        }" +
                        "    }";

                culling = (material.flags & GfxMaterialFlags.TWO_SIDED) != 0;
            }

            if (shaderColor == null) {
                generatedMat = new Material(new Texture[]{missingTexture});
            }
            else
            {
                generatedMat = new Material();

                generatedShader.createFragmentShader(Utils.loadResource("/shaders/fragment.glsl").replaceAll("//%&AMBIENTC", shaderColor));
                generatedShader.link();
                generatedShader.createListUniform("textureSampler", 25);
                generatedShader.createUniform("samplerCount");
                generatedShader.createUniform("ambientLight");
                generatedShader.createMaterialUniform("material");
                generatedShader.createUniform("highlightMode");
                generatedShader.createUniform("highlightColor");
                generatedShader.createUniform("brightnessMul");
                generatedShader.createUniform("specularPower");
                generatedShader.createDirectionalLightListUniform("directionalLights", 5);
                generatedShader.createUniform("directionalLightsSize");
                generatedShader.createPointLightListUniform("pointLights", 50);
                generatedShader.createUniform("pointLightsSize");
                generatedShader.createSpotLightListUniform("spotLights", 50);
                generatedShader.createUniform("spotLightsSize");

                generatedShader.createUniform("transformationMatrix");
                generatedShader.createUniform("projectionMatrix");
                generatedShader.createUniform("viewMatrix");
                generatedShader.createListUniform("bones", 100);
                generatedShader.createUniform("hasBones");
                generatedShader.createUniform("triangleOffset");

                generatedMat.customShader = generatedShader;

                generatedMat.textures = new Texture[textures.size()];

                for(int i = 0; i < textures.size(); i++)
                    generatedMat.textures[i] = textures.get(i);

                generatedMat.ambientColor = new Vector4f[colors.size()];
                generatedMat.diffuseColor = new Vector4f[colors.size()];
                generatedMat.specularColor = new Vector4f[colors.size()];

                for(int i = 0; i < colors.size(); i++)
                {
                    generatedMat.ambientColor[i] = colors.get(i);
                    generatedMat.diffuseColor[i] = colors.get(i);
                    generatedMat.specularColor[i] = colors.get(i);
                }
            }

            generatedMat.disableCulling = culling;

            LoadedData.loadedGfxMaterials.put(matDescriptor, generatedMat);
            return generatedMat;
        }
        else
            return LoadedData.loadedGfxMaterials.get(matDescriptor);
    }

    private static String buildColor(MaterialBox box, RGfxMaterial material, ArrayList<Texture> textures, ArrayList<Vector4f> colors, ObjectLoader loader) throws Exception {
        if(textures == null)
            textures = new ArrayList<>();
        if(colors == null)
            colors = new ArrayList<>();

        switch (box.type)
        {
            case BoxType.MULTIPLY:
            {
                MaterialBox[] connectedBoxes = material.getBoxesConnected(box);
                String in1 = buildColor(connectedBoxes[0], material, textures, colors, loader);
                String in2 = buildColor(connectedBoxes[1], material, textures, colors, loader);

                return "(" + in1 + " * " + in2 + ")";
            }
            case BoxType.ADD:
            {
                MaterialBox[] connectedBoxes = material.getBoxesConnected(box);
                String in1 = buildColor(connectedBoxes[0], material, textures, colors, loader);
                String in2 = buildColor(connectedBoxes[1], material, textures, colors, loader);

                return "(" + in1 + " + " + in2 + ")";
            }
            case BoxType.MULTIPLY_ADD:
                MaterialBox[] connectedBoxes = material.getBoxesConnected(box);
                System.out.println("MULTIPLY_ADD" + connectedBoxes.length);
                break;
            case BoxType.MIX:
                System.out.println("MIX");
                break;
            case BoxType.BLEND:
                System.out.println("BLEND");
                break;
            case BoxType.EXPONENT:
                System.out.println("EXPONENT");
                break;
            case BoxType.COLOR:
            {
                colors.add(new Vector4f(Float.intBitsToFloat((int) box.getParameters()[0]),
                        Float.intBitsToFloat((int) box.getParameters()[1]),
                        Float.intBitsToFloat((int) box.getParameters()[2]),
                        Float.intBitsToFloat((int) box.getParameters()[3])));
                return "vec4(material.ambient[" + (colors.size() - 1) + "])";
            }
            case BoxType.TEXTURE_SAMPLE:
            default:
                textures.add(getTexture(material.textures[box.getParameters()[5]], loader));
                return "vec4(texture2D(textureSampler[" + (textures.size() - 1) + "], fragTextureCoord))";
        }
        return "";
    }

    private static Texture getTexture(ResourceDescriptor desc, ObjectLoader loader) throws Exception {
        if(loadedTextures.containsKey(desc))
            return loadedTextures.get(desc);
        else
        {
            Texture tex = new Texture(loader.loadTexture(loadTexture(desc), GL11.GL_LINEAR_MIPMAP_LINEAR, GL11.GL_LINEAR));
            loadedTextures.put(desc, tex);
            return tex;
        }
    }
}
