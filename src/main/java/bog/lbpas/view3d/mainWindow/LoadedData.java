package bog.lbpas.view3d.mainWindow;

import bog.lbpas.view3d.managers.assetLoading.ObjectLoader;
import bog.lbpas.view3d.core.Model;
import bog.lbpas.view3d.core.Texture;
import bog.lbpas.view3d.utils.Utils;
import bog.lbpas.view3d.utils.print;
import cwlib.enums.*;
import cwlib.resources.*;
import cwlib.structs.bevel.BevelVertex;
import cwlib.structs.gmat.MaterialBox;
import cwlib.structs.texture.CellGcmTexture;
import cwlib.types.Resource;
import cwlib.types.archives.Fat;
import cwlib.types.archives.FileArchive;
import cwlib.types.archives.SaveArchive;
import cwlib.types.data.GUID;
import cwlib.types.data.ResourceDescriptor;
import cwlib.types.data.Revision;
import cwlib.types.data.SHA1;
import cwlib.types.databases.FileDB;
import cwlib.types.databases.FileDBRow;
import cwlib.types.databases.FileEntry;
import cwlib.types.mods.Mod;
import cwlib.types.save.BigSave;
import cwlib.types.save.SaveEntry;
import executables.gfx.GfxAssembler;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * @author Bog
 */
public class LoadedData {

    public static Mod PROJECT_DATA;

    public static ArrayList<FileDB> MAPs;
    public static ArrayList<FileArchive> FARCs;
    public static ArrayList<BigSave> BIGFARTs;
    public static RTranslationTable loadedTranslationTable;
    public static RTranslationTable loadedPatchTranslationTable;
    public static long loadedTranslation = -1;
    public static long loadedPatchTranslation = -1;
    public static ArrayList<RSlotList> slotLists;
    public static ArrayList<RPacks> packs;
    public static ArrayList<FileEntry> digestedEntries;
    public static ArrayList<ResourceDescriptor> digestedEntriesDescriptors;
    public static HashMap<Long, FileEntry> digestedEntriesGUID;
    public static HashMap<String, FileEntry> digestedEntriesSHA1;

    public static HashMap<ResourceDescriptor, Model> loadedModels;
    public static HashMap<ResourceDescriptor, ArrayList<Model>> loadedStaticModels;
    public static ArrayList<String> loadedGfxMaterials;
    public static ArrayList<ArrayList<Texture>> loadedMaterialTextures;
    public static HashMap<ResourceDescriptor, Integer> loadedGMATS;
    public static HashMap<ResourceDescriptor, Texture> loadedTextures;

    public static Texture missingTexture;

    private static View3D mainView;
    
    public static void init(View3D view)
    {
        if(PROJECT_DATA == null)
            PROJECT_DATA = new Mod(new Revision(
                    Branch.MIZUKI.getHead(),
                    Branch.MIZUKI.getID(),
                    Branch.MIZUKI.getRevision()));
        mainView = view;
        loadedModels = new HashMap<>();
        loadedStaticModels = new HashMap<>();
        loadedGfxMaterials = new ArrayList<>();
        loadedMaterialTextures = new ArrayList<>();
        loadedGMATS = new HashMap<>();
        loadedTextures = new HashMap<>();
        MAPs = new ArrayList<>();
        FARCs = new ArrayList<>();
        BIGFARTs = new ArrayList<>();
        digestedEntries = new ArrayList<>();
        digestedEntriesDescriptors = new ArrayList<>();
        digestedEntriesGUID = new HashMap<>();
        digestedEntriesSHA1 = new HashMap<>();

        slotLists = new ArrayList<>();
        packs = new ArrayList<>();

        try
        {
            BufferedImage img = new BufferedImage(254, 254, BufferedImage.TYPE_INT_ARGB);
            for (int x = 0; x < img.getWidth(); x++)
                for (int y = 0; y < img.getHeight(); y++)
                    img.setRGB(x, y, new Color((float)(x / 255f), (float)(y / 255f), 0f, 0.5f).getRGB());

            missingTexture = view.loader.loadTexture(img, GL11.GL_LINEAR_MIPMAP_NEAREST, GL11.GL_LINEAR);
            loadedTextures.put(null, missingTexture);
        }catch (Exception e){}

        int id = loadedGfxMaterials.size();
        ArrayList<Texture> missingTextures = new ArrayList<>();
        missingTextures.add(missingTexture);
        loadedMaterialTextures.add(missingTextures);
        loadedGfxMaterials.add("ambientC = vec4(0.0);custom = true;");
        loadedGMATS.put(null, id);

        defaultBevel = new RBevel();
        defaultBevel.setMaterialUVScale(1.0f, 0);
        defaultBevel.setMaterialUVScale(1.0f, 1);
        defaultBevel.setMaterialUVScale(1.0f, 2);
        defaultBevel.setMaterialUVScale(1.0f, 3);
        defaultBevel.setMaterial(null, 0);
        defaultBevel.setMaterial(null, 1);
        defaultBevel.setMaterial(null, 2);
        defaultBevel.setMaterial(null, 3);
        defaultBevel.autoSmoothCutoffAngle = 20f;
        BevelVertex bv0 = new BevelVertex();
        bv0.y = 0f;
        bv0.z = -1f;
        bv0.rigidity = 1f;
        bv0.smoothWithPrevious = 0;
        bv0.gmatSlot = 0;
        bv0.mappingMode = MappingMode.DAVE;
        defaultBevel.vertices.add(bv0);
        BevelVertex bv1 = new BevelVertex();
        bv1.y = 0f;
        bv1.z = 0.5f;
        bv1.rigidity = 1f;
        bv1.smoothWithPrevious = 0;
        bv1.gmatSlot = 0;
        bv1.mappingMode = MappingMode.DAVE;
        defaultBevel.vertices.add(bv1);
        BevelVertex bv2 = new BevelVertex();
        bv2.y = -0.5f;
        bv2.z = 1f;
        bv2.rigidity = 1f;
        bv2.smoothWithPrevious = 0;
        bv2.gmatSlot = 0;
        bv2.mappingMode = MappingMode.DAVE;
        defaultBevel.vertices.add(bv2);
        defaultBevel.includeBackface = false;
        defaultBevel.smoothWithFront = false;
        defaultBevel.relaxStrength = 0f;
        defaultBevel.subDivRadius = 0.3f;
        defaultBevel.fixedBevelSize = -1f;
        defaultBevel.spongy = false;
        defaultBevel.textureRepeats = 1f;
    }

    public static RMesh loadMesh(ResourceDescriptor descriptor) {
        if (descriptor == null)
        {
            print.error("Failed loading Mesh: descriptor null");
            mainView.pushError("Failed loading Mesh", "Descriptor is null");
            return null;
        }

        byte[] data = extract(descriptor);
        if (data == null)
        {
            print.error("Failed loading Mesh: extracted data null");
            mainView.pushError("Failed loading Msh (" + descriptor.toString() + ")", "Extracted data is null");
            return null;
        }
        RMesh mesh = null;
        try { mesh = new Resource(data).loadResource(RMesh.class); }
        catch (Exception e) { print.stackTrace(e); }

        return mesh;
    }

    public static RStaticMesh loadStaticMesh(ResourceDescriptor descriptor) {
        if (descriptor == null)
        {
            print.error("Failed loading Mesh: descriptor null");
            mainView.pushError("Failed loading Mesh", "Descriptor is null");
            return null;
        }

        byte[] data = extract(descriptor);
        if (data == null)
        {
            print.error("Failed loading Mesh: extracted data null");
            mainView.pushError("Failed loading Msh (" + descriptor.toString() + ")", "Extracted data is null");
            return null;
        }
        RStaticMesh mesh = null;
        try {mesh = new RStaticMesh(new Resource(data));}
        catch (Exception e) { print.stackTrace(e); }

        return mesh;
    }

    public static RPlan loadPlan(ResourceDescriptor descriptor) {
        if (descriptor == null)
        {
            print.error("Failed loading Plan: descriptor null");
            mainView.pushError("Failed loading Plan", "Descriptor is null");
            return null;
        }

        byte[] data = extract(descriptor);
        if (data == null)
        {
            print.error("Failed loading Plan: extracted data null");
            mainView.pushError("Failed loading Pln (" + descriptor.toString() + ")", "Extracted data is null");
            return null;
        }
        RPlan mesh = null;
        try { mesh = new Resource(data).loadResource(RPlan.class); }
        catch (Exception e) { print.stackTrace(e); }

        return mesh;
    }

    public static RLevel loadLevel(ResourceDescriptor descriptor) {
        if (descriptor == null)
        {
            print.error("Failed loading Level: descriptor null");
            mainView.pushError("Failed loading Level", "Descriptor is null");
            return null;
        }

        byte[] data = extract(descriptor);
        if (data == null)
        {
            print.error("Failed loading Level: extracted data null");
            mainView.pushError("Failed loading Lvl (" + descriptor.toString() + ")", "Extracted data is null");
            return null;
        }
        RLevel mesh = null;
        try { mesh = new Resource(data).loadResource(RLevel.class); }
        catch (Exception e) { print.stackTrace(e); }

        return mesh;
    }

    public static RGfxMaterial loadGfxMaterial(ResourceDescriptor descriptor) {
        if (descriptor == null)
        {
            print.error("Failed loading GFX Material: descriptor null");
            mainView.pushError("Failed loading GFX Material", "Descriptor is null");
            return null;
        }

        byte[] data = extract(descriptor);
        if (data == null)
        {
            print.error("Failed loading GFX Material: extracted data null");
            mainView.pushError("Failed loading GMat (" + descriptor.toString() + ")", "Extracted data is null");
            return null;
        }
        RGfxMaterial material = null;
        try { material = new Resource(data).loadResource(RGfxMaterial.class); }
        catch (Exception e) { print.stackTrace(e); }

        return material;
    }

    public static RBevel defaultBevel;

    public static RBevel loadBevel(ResourceDescriptor descriptor) {

        if (descriptor == null)
            return defaultBevel;

        byte[] data = extract(descriptor);

        if (data == null)
            return defaultBevel;

        RBevel RBevel = null;
        try { RBevel = new Resource(data).loadResource(RBevel.class); }
        catch (Exception e) { print.stackTrace(e); }

        return RBevel;
    }

    public static BufferedImage loadTexture(ResourceDescriptor descriptor) {
        if (descriptor == null)
        {
            print.error("Failed loading Texture: descriptor null");
            mainView.pushError("Failed loading Texture", "Descriptor is null");
            return null;
        }

        byte[] data = extract(descriptor);
        if (data == null)
        {
            print.error("Failed loading Texture: extracted data null");
            mainView.pushError("Failed loading Tex (" + descriptor.toString() + ")", "Extracted data is null");
            return null;
        }
        BufferedImage texture = null;
        try {
            RTexture resource = new RTexture(new Resource(data));
            texture = resource.getImage();
        }
        catch (Exception e) { print.stackTrace(e); }

        return texture;
    }

    public static byte[] extract(ResourceDescriptor descriptor) {
        if (descriptor == null) return null;
        FileEntry ent = getDigestedEntry(descriptor);
        if(ent == null) return  null;

        SHA1 sha1 = ent.getSHA1();
//        if (descriptor.isGUID() && !descriptor.isHash())
//            for(FileDB map : MAPs)
//            {
//                FileDBRow row = map.get(descriptor.getGUID());
//                if (row == null || row.getSHA1() == null) continue;
//                sha1 = row.getSHA1();
//                break;
//            }

        if(sha1 == null) return null;

        if(PROJECT_DATA.archive.exists(sha1))
        {
            return PROJECT_DATA.archive.extract(sha1);
        }

        for(FileArchive archive : FARCs) {
            if(archive.exists(sha1))
                return archive.extract(sha1);
        }

        for(BigSave fart : BIGFARTs)
        {
            SaveEntry entry = fart.get(sha1);
            if(entry == null)
                continue;
            return fart.extract(sha1);
        }

        return null;
    }

    public static FileEntry getSourceEntry(ResourceDescriptor descriptor) {
        if (descriptor == null) return null;
        if (descriptor.isGUID())
            for(FileDB map : MAPs)
            {
                FileDBRow row = map.get(descriptor.getGUID());
                if (row == null) continue;
                return row;
            }

        SHA1 sha1 = descriptor.getSHA1();

        for(FileArchive archive : FARCs) {
            if(archive.exists(sha1))
                for(Fat entry : archive.getEntries())
                    if(entry.getSHA1().equals(sha1))
                        return new FileEntry(null, sha1.toString(), sha1, entry.getSize()) {};
        }

        for(BigSave fart : BIGFARTs)
        {
            SaveEntry entry = fart.get(sha1);
            if(entry == null)
                continue;
            return entry;
        }

        return null;
    }

    public static boolean shouldSetupList = false;
    public static int totalEntryCount = 0;
    public static int currentEntryDigestion = 0;

    public static void setupList()
    {
        if(!shouldSetupList)
            return;
        totalEntryCount = 0;
        currentEntryDigestion = 0;
        digestedEntries.clear();
        digestedEntriesDescriptors.clear();

        HashSet<Long> digestedEntriesGUIDs = new HashSet<>();
        HashSet<String> digestedEntriesSHA1s = new HashSet<>();

        if(!LoadedData.MAPs.isEmpty() && !LoadedData.FARCs.isEmpty())
            for(FileDB map : MAPs)
                totalEntryCount += map.entries.size();

        if(!LoadedData.BIGFARTs.isEmpty())
            for(BigSave bigfart : BIGFARTs)
                totalEntryCount += bigfart.entries.size();

        if(!LoadedData.BIGFARTs.isEmpty())
            for(BigSave bigfart : BIGFARTs)
                for(SaveEntry ent : bigfart.entries)
                {
                    ResourceDescriptor descriptor = new ResourceDescriptor(ent, bigfart);

                    if(!digestedEntriesSHA1s.contains(descriptor.getSHA1().toString()))
                    {
                        digestedEntriesSHA1s.add(descriptor.getSHA1().toString());
                        digestedEntries.add(ent);
                        digestedEntriesDescriptors.add(descriptor);
                        digestedEntriesSHA1.put(descriptor.getSHA1().toString(), ent);

                        if(descriptor.getType() == ResourceType.SLOT_LIST)
                            try{
                                slotLists.add(new Resource(bigfart.extract(descriptor.getSHA1())).loadResource(RSlotList.class));
                            }catch (Exception e){}
                        if(descriptor.getType() == ResourceType.PACKS)
                            try{
                                packs.add(new Resource(bigfart.extract(descriptor.getSHA1())).loadResource(RPacks.class));
                            }catch (Exception e){}
                    }

                    currentEntryDigestion++;
                }

        if(!LoadedData.MAPs.isEmpty() && !LoadedData.FARCs.isEmpty())
            for(FileDB map : MAPs)
                for(FileDBRow entry : map.entries)
                {
                    for(FileArchive farc : FARCs)
                    {
                        if(farc.exists(entry.getSHA1()))
                        {
                            ResourceDescriptor descriptor = new ResourceDescriptor(entry, farc);

                            if(!digestedEntriesGUIDs.contains(descriptor.getGUID().getValue()))
                            {
                                entry.archive = farc;
                                digestedEntriesGUIDs.add(descriptor.getGUID().getValue());
                                digestedEntriesSHA1s.add(descriptor.getSHA1().toString());
                                digestedEntries.add(entry);
                                digestedEntriesDescriptors.add(descriptor);
                                digestedEntriesSHA1.put(descriptor.getSHA1().toString(), entry);
                                digestedEntriesGUID.put(descriptor.getGUID().getValue(), entry);

                                if(descriptor.getType() == ResourceType.SLOT_LIST)
                                    try{
                                        slotLists.add(new Resource(farc.extract(descriptor.getSHA1())).loadResource(RSlotList.class));
                                    }catch (Exception e){}
                                if(descriptor.getType() == ResourceType.PACKS)
                                    try{
                                        packs.add(new Resource(farc.extract(descriptor.getSHA1())).loadResource(RPacks.class));
                                    }catch (Exception e){}
                            }
                        }
                        else
                        {
                            ResourceDescriptor descriptor = new ResourceDescriptor(entry);

                            if(!digestedEntriesGUIDs.contains(descriptor.getGUID().getValue()))
                            {
                                entry.archive = farc;
                                digestedEntriesGUIDs.add(descriptor.getGUID().getValue());
                                digestedEntriesSHA1s.add(descriptor.getSHA1().toString());
                                digestedEntries.add(entry);
                                digestedEntriesDescriptors.add(descriptor);
                                digestedEntriesSHA1.put(descriptor.getSHA1().toString(), entry);
                                digestedEntriesGUID.put(descriptor.getGUID().getValue(), entry);

                                if(descriptor.getType() == ResourceType.SLOT_LIST)
                                    try{
                                        slotLists.add(new Resource(farc.extract(descriptor.getSHA1())).loadResource(RSlotList.class));
                                    }catch (Exception e){print.stackTrace(e);}
                                if(descriptor.getType() == ResourceType.PACKS)
                                    try{
                                        packs.add(new Resource(farc.extract(descriptor.getSHA1())).loadResource(RPacks.class));
                                    }catch (Exception e){}
                            }
                        }
                    }

                    currentEntryDigestion++;
                }

        for(FileDBRow entry : LoadedData.PROJECT_DATA.entries)
        {
            ResourceDescriptor descriptor = new ResourceDescriptor(entry);

            if(!digestedEntriesGUIDs.contains(descriptor.getGUID().getValue()))
            {
                entry.archive = LoadedData.PROJECT_DATA.archive;
                digestedEntriesGUIDs.add(descriptor.getGUID().getValue());
                digestedEntriesSHA1s.add(descriptor.getSHA1().toString());
                digestedEntries.add(entry);
                digestedEntriesDescriptors.add(descriptor);
                digestedEntriesSHA1.put(descriptor.getSHA1().toString(), entry);
                digestedEntriesGUID.put(descriptor.getGUID().getValue(), entry);

                if(descriptor.getType() == ResourceType.SLOT_LIST)
                    try{
                        slotLists.add(new Resource(LoadedData.PROJECT_DATA.extract(descriptor.getSHA1())).loadResource(RSlotList.class));
                    }catch (Exception e){}
                if(descriptor.getType() == ResourceType.PACKS)
                    try{
                        packs.add(new Resource(LoadedData.PROJECT_DATA.extract(descriptor.getSHA1())).loadResource(RPacks.class));
                    }catch (Exception e){}
            }
        }

        shouldSetupList = false;
    }

    public static FileEntry getDigestedEntry(ResourceDescriptor descriptor)
    {
        FileEntry entry = null;
        if(descriptor.isGUID())
            entry = getDigestedEntry(descriptor.getGUID());
        if(descriptor.isHash() && entry == null)
            entry = getDigestedEntry(descriptor.getSHA1());
        return entry;
    }

    public static FileEntry getDigestedEntry(SHA1 sha1)
    {
        return digestedEntriesSHA1.get(sha1.toString());
//        return digestedEntries.stream().filter(FileEntry -> FileEntry.getSHA1().equals(sha1)).findFirst().get();
    }

    public static FileEntry getDigestedEntry(GUID guid)
    {
        return digestedEntriesGUID.get(guid.getValue());
//        ResourceDescriptor descriptor = digestedEntriesDescriptors.stream().filter(ResourceDescriptor -> ResourceDescriptor.getGUID().equals(guid)).findFirst().get();
//        return digestedEntries.get(digestedEntriesDescriptors.indexOf(descriptor));
    }

    public static boolean shouldUpdateShader = false;

    public static int getMaterial(ResourceDescriptor matDescriptor, ObjectLoader loader, Texture[] textures, Vector2i[] gmatMAP) throws Exception {

        int texIndex = 0;
        while(texIndex <= gmatMAP.length && gmatMAP[texIndex].x != -1)
            texIndex++;

        if(!LoadedData.loadedGMATS.containsKey(matDescriptor))
        {
            int id = loadedGfxMaterials.size();
            RGfxMaterial material = LoadedData.loadGfxMaterial(matDescriptor);

            if(material == null)
            {
                for(int i = 0; i < loadedMaterialTextures.get(0).size(); i++)
                {
                    if(texIndex + i >= 32)
                        break;

                    textures[texIndex + i] = loadedMaterialTextures.get(0).get(i);
                    gmatMAP[texIndex + i].x = id;
                    gmatMAP[texIndex + i].y = texIndex + i;
                }
                return -1;
            }

            String shaderColor = null;

            int output = material.getOutputBox();
            MaterialBox outBox = material.getBoxConnectedToPort(output, 0);
            ArrayList<Texture> texturs = new ArrayList<>();

            String sh = "ambientC = vec4(" + buildColor(outBox, material, texturs, loader)[0] + ");custom = true;";
            if(!Utils.isBitwiseBool(material.flags, GfxMaterialFlags.TWO_SIDED))
                sh += "cullBackFace();";

            loadedMaterialTextures.add(texturs);

            for(int i = 0; i < texturs.size(); i++)
            {
                int preexisting = -1;
                int nextIndex = 0;
                for(int j = 0; j < textures.length; j++)
                {
                    nextIndex = j;
                    if(textures[j] == null)
                        break;

                    if(textures[j].id == texturs.get(i).id)
                        preexisting = j;
                }

                int nextMapIndex = 0;
                for(int j = 0; j < gmatMAP.length; j++)
                    if(gmatMAP[j].x == -1)
                    {
                        nextMapIndex = j;
                        break;
                    }

                if(preexisting == -1)
                {
                    textures[nextIndex] = texturs.get(i);
                    gmatMAP[nextMapIndex].x = id;
                    gmatMAP[nextMapIndex].y = nextIndex;
                }
                else
                {
                    gmatMAP[nextMapIndex].x = id;
                    gmatMAP[nextMapIndex].y = preexisting;
                }
            }

            loadedGfxMaterials.add(sh);
            loadedGMATS.put(matDescriptor, id);
            shouldUpdateShader = true;
            return id;
        }
        else
        {
            int id = LoadedData.loadedGMATS.get(matDescriptor);

            for(int i = 0; i < loadedMaterialTextures.get(id).size(); i++)
            {
                int preexisting = -1;
                int nextIndex = 0;
                for(int j = 0; j < textures.length; j++)
                {
                    nextIndex = j;
                    if(textures[j] == null)
                        break;

                    if(textures[j].id == loadedMaterialTextures.get(id).get(i).id)
                        preexisting = j;
                }

                int nextMapIndex = 0;
                for(int j = 0; j < gmatMAP.length; j++)
                    if(gmatMAP[j].x == -1)
                    {
                        nextMapIndex = j;
                        break;
                    }

                if(preexisting == -1)
                {
                    textures[nextIndex] = loadedMaterialTextures.get(id).get(i);
                    gmatMAP[nextMapIndex].x = id;
                    gmatMAP[nextMapIndex].y = nextIndex;
                }
                else
                {
                    gmatMAP[nextMapIndex].x = id;
                    gmatMAP[nextMapIndex].y = preexisting;
                }
            }

            return id;
        }
    }

    public static String[] buildColor(MaterialBox box, RGfxMaterial material, ArrayList<Texture> textures, ObjectLoader loader) throws Exception {
        if(box == null)
            return new String[]{"vec4(0, 0, 0, 1)", "false"};

        float reinhardToneMapping = 2.5f;

        switch (box.type)
        {
            case BoxType.MULTIPLY:
            {
                MaterialBox[] connectedBoxes = material.getBoxesConnected(box);
                String[] in1 = buildColor(connectedBoxes[0], material, textures, loader);
                String[] in2 = buildColor(connectedBoxes[1], material, textures, loader);

                boolean isBump = Boolean.parseBoolean(in1[1]) & Boolean.parseBoolean(in2[1]);

                String color1 = Boolean.parseBoolean(in1[1]) ? "vec4(" + in1[0] + ".xyz, 1.0)" : in1[0];
                String color2 = Boolean.parseBoolean(in2[1]) ? "vec4(" + in2[0] + ".xyz, 1.0)" : in2[0];

                return new String[]{"(" + color1 + " * " + color2 + ")", Boolean.toString(isBump)};
            }
            case BoxType.ADD:
            {
                MaterialBox[] connectedBoxes = material.getBoxesConnected(box);
                String[] in1 = buildColor(connectedBoxes[0], material, textures, loader);
                String[] in2 = buildColor(connectedBoxes[1], material, textures, loader);

                boolean isBump = Boolean.parseBoolean(in1[1]) & Boolean.parseBoolean(in2[1]);

                String color1 = Boolean.parseBoolean(in1[1]) ? "vec4(" + in1[0] + ".xyz, 1.0)" : in1[0];
                String color2 = Boolean.parseBoolean(in2[1]) ? "vec4(" + in2[0] + ".xyz, 1.0)" : in2[0];

                return new String[]{"(" + color1 + " + " + color2 + ")", Boolean.toString(isBump)};
            }
            case BoxType.MULTIPLY_ADD:
            {
                MaterialBox[] connectedBoxes = material.getBoxesConnected(box);
                String[] in1 = buildColor(connectedBoxes[0], material, textures, loader);

                boolean isBump = Boolean.parseBoolean(in1[1]);

                String color1 = isBump ? "vec4(" + in1[0] + ".xyz, 1.0)" : in1[0];

                return new String[]{"((" + color1 + " * " + box.getParameters()[0] + ") + " + box.getParameters()[1] + ")", Boolean.toString(isBump)};
            }
            case BoxType.MIX:
            {
                MaterialBox[] connectedBoxes = material.getBoxesConnected(box);
                String[] in1 = buildColor(connectedBoxes[0], material, textures, loader);
                String[] in2 = buildColor(connectedBoxes[1], material, textures, loader);

                boolean isBump = Boolean.parseBoolean(in1[1]) & Boolean.parseBoolean(in2[1]);

                String color1 = Boolean.parseBoolean(in1[1]) ? "vec4(" + in1[0] + ".xyz, 1.0)" : in1[0];
                String color2 = Boolean.parseBoolean(in2[1]) ? "vec4(" + in2[0] + ".xyz, 1.0)" : in2[0];

                return new String[]{"(mix(" + color1 + ", vec4(" + color2 + "), 0.5))", Boolean.toString(isBump)};
            }
            case BoxType.BLEND:
            {
                MaterialBox[] connectedBoxes = material.getBoxesConnected(box);
                String[] in1 = buildColor(connectedBoxes[0], material, textures, loader);
                String[] in2 = buildColor(connectedBoxes[1], material, textures, loader);

                boolean isBump = Boolean.parseBoolean(in1[1]) & Boolean.parseBoolean(in2[1]);

                String color1 = Boolean.parseBoolean(in1[1]) ? "vec4(" + in1[0] + ".xyz, 1.0)" : in1[0];
                String color2 = Boolean.parseBoolean(in2[1]) ? "vec4(" + in2[0] + ".xyz, 1.0)" : in2[0];

                return new String[]{"mix(" + color2 + ", " + color1 + ", " + color1 + ".w)", Boolean.toString(isBump)};
            }
            case BoxType.EXPONENT:
            {
                MaterialBox[] connectedBoxes = material.getBoxesConnected(box);
                String[] in1 = buildColor(connectedBoxes[0], material, textures, loader);
                String[] in2 = buildColor(connectedBoxes[1], material, textures, loader);

                boolean isBump = Boolean.parseBoolean(in1[1]) & Boolean.parseBoolean(in2[1]);

                String color1 = Boolean.parseBoolean(in1[1]) ? "vec4(" + in1[0] + ".xyz, 1.0)" : in1[0];
                String color2 = Boolean.parseBoolean(in2[1]) ? "vec4(" + in2[0] + ".xyz, 1.0)" : in2[0];

                return new String[]{"pow(" + color1 + ", " + color2 + ")", Boolean.toString(isBump)};
            }
            case BoxType.COLOR:
            {
                return new String[]{"vec4(reinhardToneMapping(vec3(" + Float.intBitsToFloat(box.getParameters()[0]) + ", " +
                        Float.intBitsToFloat(box.getParameters()[1]) + ", " +
                        Float.intBitsToFloat(box.getParameters()[2]) + "), " + reinhardToneMapping + ").rgb, " +
                        Float.intBitsToFloat(box.getParameters()[3]) + ")", "false"};
            }
            case BoxType.CONSTANT:
            {
                return new String[]{"(" + Float.intBitsToFloat(box.getParameters()[0]) + ")", "false"};
            }
            case BoxType.MAKE_FLOAT2:
            case BoxType.CONSTANT2:
            {
                return new String[]{"vec2(" + Float.intBitsToFloat(box.getParameters()[0]) + ", " + Float.intBitsToFloat(box.getParameters()[1]) + ")", "false"};
            }
            case BoxType.MAKE_FLOAT3:
            case BoxType.CONSTANT3:
            {
                return new String[]{"vec3(" + Float.intBitsToFloat(box.getParameters()[0]) + ", " + Float.intBitsToFloat(box.getParameters()[1]) + ", " + Float.intBitsToFloat(box.getParameters()[2]) + ")", "false"};
            }
            case BoxType.MAKE_FLOAT4:
            case BoxType.CONSTANT4:
            {
                return new String[]{"vec4(" + Float.intBitsToFloat(box.getParameters()[0]) + ", " + Float.intBitsToFloat(box.getParameters()[1]) + ", " + Float.intBitsToFloat(box.getParameters()[2]) + ", " + Float.intBitsToFloat(box.getParameters()[3]) + ")", "false"};
            }
            case BoxType.THING_COLOR:
            {
                return new String[]{"vec4(reinhardToneMapping(thingColor.rgb, " + reinhardToneMapping + ").rgb, thingColor.a)", "false"};
            }
            case BoxType.TEXTURE_SAMPLE:
            default:
                Texture tex = new Texture(missingTexture.id, loader);

                boolean isBump = false;

                if (material.textures[box.getParameters()[5]] == null)
                {
                    print.error("Failed loading Texture: descriptor null");
                    mainView.pushError("Failed loading Texture", "Descriptor is null");
                }
                else
                {
                    byte[] data = extract(material.textures[box.getParameters()[5]]);
                    BufferedImage texture = null;
                    if (data == null)
                    {
                        print.error("Failed loading Texture: extracted data null");
                        mainView.pushError("Failed loading Tex (" + material.textures[box.getParameters()[5]].toString() + ")", "Extracted data is null");
                    }
                    else
                        try {
                            RTexture resource = new RTexture(new Resource(data));
                            CellGcmTexture t = resource.getInfo();
                            if(t != null)
                                isBump = t.isBumpTexture();
                            texture = resource.getImage();
                        }
                        catch (Exception e) {print.stackTrace(e);}

                    tex = getTexture(material.textures[box.getParameters()[5]], texture, loader);
                }

                if(tex != null)
                    textures.add(tex);

                if(box.type != BoxType.TEXTURE_SAMPLE)
                    print.warning("MISSING BOX TYPE: " + box.type);

                boolean UV0 = true;

                if(box.type == BoxType.TEXTURE_SAMPLE && box.getParameters()[4] == 1)
                    UV0 = false;

                return new String[]{"vec4(texture(textureSampler[gmatMAP[gmatIndex + " + (textures.size() - 1) + "].y], " + (UV0 ? "((vec2(fragTextureCoord.x, fragTextureCoord.y) * vec2(" + Float.intBitsToFloat(box.getParameters()[0]) + ", " + Float.intBitsToFloat(box.getParameters()[1]) + ")) + vec2(" + Float.intBitsToFloat(box.getParameters()[2]) + ", " + Float.intBitsToFloat(box.getParameters()[3]) + "))" : "((vec2(fragTextureCoord.z, fragTextureCoord.w) * vec2(" + Float.intBitsToFloat(box.getParameters()[0]) + ", " + Float.intBitsToFloat(box.getParameters()[1]) + ")) + vec2(" + Float.intBitsToFloat(box.getParameters()[2]) + ", " + Float.intBitsToFloat(box.getParameters()[3]) + "))") + "))", Boolean.toString(isBump)};
        }
    }

    private static Texture getTexture(ResourceDescriptor desc, BufferedImage image, ObjectLoader loader) throws Exception {

        try
        {
            if(loadedTextures.containsKey(desc))
                return loadedTextures.get(desc);
            else
            {
                Texture tex = loader.loadTexture2(image, GL11.GL_LINEAR_MIPMAP_NEAREST, GL11.GL_LINEAR);
                loadedTextures.put(desc, tex);
                return tex;
            }
        }catch (Exception e){print.stackTrace(e);}
        return null;
    }
}
