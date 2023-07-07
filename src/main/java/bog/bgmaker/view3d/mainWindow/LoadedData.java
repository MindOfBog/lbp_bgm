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
import cwlib.structs.gmat.MaterialWire;
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

            missingTexture = new Texture(loader.loadTexture(img, GL11.GL_LINEAR_MIPMAP_NEAREST, GL11.GL_LINEAR));
        }catch (Exception e){}
    }

    public static RMesh loadMesh(ResourceDescriptor descriptor) {
        if (descriptor == null)
        {
            System.err.println("Failed loading Mesh: descriptor null");
            return null;
        }

        byte[] data = extract(descriptor);
        if (data == null)
        {
            System.err.println("Failed loading Mesh: extracted data null");
            return null;
        }
        RMesh mesh = null;
        try { mesh = new Resource(data).loadResource(RMesh.class); }
        catch (Exception e) { e.printStackTrace(); }

        return mesh;
    }

    public static RGfxMaterial loadGfxMaterial(ResourceDescriptor descriptor) {
        if (descriptor == null)
        {
            System.err.println("Failed loading GFX Material: descriptor null");
            return null;
        }

        byte[] data = extract(descriptor);
        if (data == null)
        {
            System.err.println("Failed loading GFX Material: extracted data null");
            return null;
        }
        RGfxMaterial material = null;
        try { material = new Resource(data).loadResource(RGfxMaterial.class); }
        catch (Exception e) { e.printStackTrace(); }

        return material;
    }

    public static BufferedImage loadTexture(ResourceDescriptor descriptor) {
        if (descriptor == null)
        {
            System.err.println("Failed loading Texture: descriptor null");
            return null;
        }

        byte[] data = extract(descriptor);
        if (data == null)
        {
            System.err.println("Failed loading Texture: extracted data null");
            return null;
        }
        BufferedImage texture = null;
        try {
            RTexture resource = new RTexture(new Resource(data));
            texture = resource.getImage();
        }
        catch (Exception e) { e.printStackTrace(); }

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
            Material generatedMat = null;
            boolean culling = false;

            if(matDescriptor != null) {
                RGfxMaterial material = LoadedData.loadGfxMaterial(matDescriptor);
                ShaderMan generatedShader = new ShaderMan();
                generatedShader.createVertexShader(Utils.loadResource("/shaders/vertex.glsl"));

                ArrayList<Texture> textures = new ArrayList<>();
                ArrayList<Vector4f> colors = new ArrayList<>();
                String shaderColor = null;

                if (material != null) {
                    int output = material.getOutputBox();
                    MaterialBox outBox = material.getBoxConnectedToPort(output, 0);

                    String sh = buildColor(outBox, material, textures, colors, loader);

                    shaderColor = sh == null ? null : "ambientC = vec4(" + sh + ");" +
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
                } else {
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

                    for (int i = 0; i < textures.size(); i++)
                        generatedMat.textures[i] = textures.get(i);

                    generatedMat.ambientColor = new Vector4f[colors.size()];
                    generatedMat.diffuseColor = new Vector4f[colors.size()];
                    generatedMat.specularColor = new Vector4f[colors.size()];

                    for (int i = 0; i < colors.size(); i++) {
                        generatedMat.ambientColor[i] = colors.get(i);
                        generatedMat.diffuseColor[i] = colors.get(i);
                        generatedMat.specularColor[i] = colors.get(i);
                    }
                }
            }
            else
            {
                generatedMat = new Material(new Texture[]{missingTexture});
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

        if(box == null)
            return null;

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
//            case BoxType.MULTIPLY_ADD:
//            {
//                MaterialBox[] connectedBoxes = material.getBoxesConnected(box);
//                System.out.println("muladd: " + connectedBoxes.length);
//                return "";
//            }
            case BoxType.MIX:
            {
                MaterialBox[] connectedBoxes = material.getBoxesConnected(box);
                String in1 = buildColor(connectedBoxes[0], material, textures, colors, loader);
                String in2 = buildColor(connectedBoxes[1], material, textures, colors, loader);
                return "(mix(" + in1 + ", " + in2 + ", 0.5))";
            }
//            case BoxType.BLEND:
//                System.out.println("BLEND");
//                break;
//            case BoxType.EXPONENT:
//                System.out.println("EXPONENT");
//                break;
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
                Texture tex = new Texture(missingTexture.id);

                try
                {
                    tex = getTexture(material.textures[box.getParameters()[5]], loader);
                }catch (Exception e){}

                if(tex != null)
                    textures.add(tex);

                if(box.type != BoxType.TEXTURE_SAMPLE)
                    System.out.println("MISSING BOX TYPE: " + box.type);

                boolean UV0 = true;

                if(box.type == BoxType.TEXTURE_SAMPLE && box.getParameters()[4] == 1)
                    UV0 = false;

                return "vec4(texture2D(textureSampler[" + (textures.size() - 1) + "], " + (UV0 ? "vec2(fragTextureCoord.x, fragTextureCoord.y)" : "vec2(fragTextureCoord.z, fragTextureCoord.w)") + "))";
        }
    }

    private static Texture getTexture(ResourceDescriptor desc, ObjectLoader loader) throws Exception {
        if(loadedTextures.containsKey(desc))
            return loadedTextures.get(desc);
        else
        {
            Texture tex = new Texture(loader.loadTexture(loadTexture(desc), GL11.GL_LINEAR_MIPMAP_NEAREST, GL11.GL_LINEAR));
            loadedTextures.put(desc, tex);
            return tex;
        }
    }
}
