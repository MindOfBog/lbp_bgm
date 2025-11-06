package bog.lbpas.view3d.mainWindow.screens.thingPart.parts;

import bog.lbpas.view3d.core.types.Thing;
import bog.lbpas.view3d.mainWindow.View3D;
import bog.lbpas.view3d.managers.MouseInput;
import bog.lbpas.view3d.renderer.gui.elements.*;
import bog.lbpas.view3d.utils.Consts;
import bog.lbpas.view3d.utils.Utils;
import bog.lbpas.view3d.utils.print;
import cwlib.enums.InventoryObjectSubType;
import cwlib.enums.InventoryObjectType;
import cwlib.enums.Part;
import cwlib.enums.ResourceType;
import cwlib.io.gson.GsonRevision;
import cwlib.structs.inventory.PhotoMetadata;
import cwlib.structs.things.components.Value;
import cwlib.structs.things.parts.PMetadata;
import cwlib.structs.things.parts.PRenderMesh;
import cwlib.types.data.ResourceDescriptor;
import org.joml.Math;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;

import java.util.*;

/**
 * @author Bog
 */
public abstract class PartMetadata extends iPart {

    public PartMetadata(int tabWidth, float comboWidth, float panelHeight, float closeWidth, float finalGap, Element tab, View3D view) {
        super(cwlib.enums.Part.METADATA, "PMetadata", "Metadata", tabWidth, comboWidth, panelHeight, closeWidth, finalGap, tab, view);
    }

//    public Value value = new Value();todo
//    public PhotoMetadata photoMetadata;

    Textbox icon;
    Textbox nameTag;
    Textbox descriptionTag;
    Textbox locationTag;
    Textbox categoryTag;

    Textbox nameKey;
    Textbox descriptionKey;
    Textbox locationKey;
    Textbox categoryKey;

    Textbox primaryIndex;
    Textbox fluffCost;

    ComboBox type; //InventoryObjectType
    ElementList typeList;
    ArrayList<Checkbox> types;
    ComboBox subType;
    Radiobutton madeByAnyRB;
    Radiobutton madeByOthersRB;
    Radiobutton madeByMeRB;

    Radiobutton earthRB;
    Radiobutton moonRB;
    Radiobutton adventureRB;
    Radiobutton externalRB;

    Radiobutton nonSackboyRB;
    Radiobutton giantRB;
    Radiobutton dwarfRB;
    Radiobutton birdRB;
    Radiobutton quadRB;
    ElementList subTypeList;

    Textbox dateCreatedSecond;
    Textbox dateCreatedMinute;
    Textbox dateCreatedHour;
    Textbox dateCreatedDay;
    Textbox dateCreatedMonth;
    Textbox dateCreatedYear;

    Checkbox referencable;
    Checkbox allowEmit;

    @Override
    public void init(View3D view) {

        types = new ArrayList<>();

        Panel iconPanel = partComboBox.addPanel("iconPanel");
        iconPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("iconStr", "Icon:", view.renderer), 0.5f));
        icon = new Textbox("icon", view.renderer, view.loader, view.window);
        iconPanel.elements.add(new Panel.PanelElement(icon, 0.5f));

        partComboBox.addString("tags", Consts.FONT_SET_BOLD + "Tags:");

        Panel nameTagPanel = partComboBox.addPanel("nameTagPanel");
        nameTagPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("nameTagStr", "Name:", view.renderer), 0.5f));
        nameTag = new Textbox("nameTag", view.renderer, view.loader, view.window);
        nameTagPanel.elements.add(new Panel.PanelElement(nameTag, 0.5f));

        Panel descriptionTagPanel = partComboBox.addPanel("descriptionTagPanel");
        descriptionTagPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("descriptionTagStr", "Description:", view.renderer), 0.5f));
        descriptionTag = new Textbox("descriptionTag", view.renderer, view.loader, view.window);
        descriptionTagPanel.elements.add(new Panel.PanelElement(descriptionTag, 0.5f));

        Panel locationTagPanel = partComboBox.addPanel("locationTagPanel");
        locationTagPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("locationTagStr", "Location:", view.renderer), 0.5f));
        locationTag = new Textbox("locationTag", view.renderer, view.loader, view.window);
        locationTagPanel.elements.add(new Panel.PanelElement(locationTag, 0.5f));

        Panel categoryTagPanel = partComboBox.addPanel("categoryTagPanel");
        categoryTagPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("categoryTagStr", "Category:", view.renderer), 0.5f));
        categoryTag = new Textbox("categoryTag", view.renderer, view.loader, view.window);
        categoryTagPanel.elements.add(new Panel.PanelElement(categoryTag, 0.5f));

        partComboBox.addString("keys", Consts.FONT_SET_BOLD + "Keys:");

        Panel nameKeyPanel = partComboBox.addPanel("nameKeyPanel");
        nameKeyPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("nameKeyStr", "Name:", view.renderer), 0.5f));
        nameKey = new Textbox("nameKey", view.renderer, view.loader, view.window);
        nameKeyPanel.elements.add(new Panel.PanelElement(nameKey, 0.5f));

        Panel descriptionKeyPanel = partComboBox.addPanel("descriptionKeyPanel");
        descriptionKeyPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("descriptionKeyStr", "Description:", view.renderer), 0.5f));
        descriptionKey = new Textbox("descriptionKey", view.renderer, view.loader, view.window);
        descriptionKeyPanel.elements.add(new Panel.PanelElement(descriptionKey, 0.5f));

        Panel locationKeyPanel = partComboBox.addPanel("locationKeyPanel");
        locationKeyPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("locationKeyStr", "Location:", view.renderer), 0.5f));
        locationKey = new Textbox("locationKey", view.renderer, view.loader, view.window);
        locationKeyPanel.elements.add(new Panel.PanelElement(locationKey, 0.5f));

        Panel categoryKeyPanel = partComboBox.addPanel("categoryKeyPanel");
        categoryKeyPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("categoryKeyStr", "Category:", view.renderer), 0.5f));
        categoryKey = new Textbox("categoryKey", view.renderer, view.loader, view.window);
        categoryKeyPanel.elements.add(new Panel.PanelElement(categoryKey, 0.5f));

        referencable = partComboBox.addCheckbox("refr", "Referencable", new Checkbox()
        {
            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement, boolean focusedOther) {
                super.onClick(mouseInput, pos, button, action, mods, overElement, focusedOther);

                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos) && !overElement)
                    for (int i = 0; i < getThings().size(); i++)
                    {
                        Thing selected = getThings().get(i);
                        if(!selected.selected)
                            continue;

                        PMetadata metadata = selected.thing.getPart(Part.METADATA);
                        metadata.referencable = this.isChecked;
                    }
            }
        });
        allowEmit = partComboBox.addCheckbox("allowEmit", "Allow Emit", new Checkbox()
        {
            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement, boolean focusedOther) {
                super.onClick(mouseInput, pos, button, action, mods, overElement, focusedOther);

                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos) && !overElement)
                    for (int i = 0; i < getThings().size(); i++)
                    {
                        Thing selected = getThings().get(i);
                        if(!selected.selected)
                            continue;

                        PMetadata metadata = selected.thing.getPart(Part.METADATA);
                        metadata.allowEmit = this.isChecked;
                    }
            }
        });

        Panel typePanel = partComboBox.addPanel("typePanel");
        type = new ComboBox("type", "Type", view.renderer, view.loader, view.window) {
            @Override
            public int tabWidth() {
                return Math.round(200f * (getFontHeight() / 12f));
            }

            @Override
            public int[] getParentTransform() {
                return partComboBox.getTabPosWidth();
            }
        };

        typeList = type.addElementList("types", Math.round(200f * (view.getFontHeight() / 12f)));

        List<InventoryObjectType> typeArray = Arrays.asList(InventoryObjectType.values());

        typeArray.sort(new Comparator<InventoryObjectType>() {
            @Override
            public int compare(InventoryObjectType o1, InventoryObjectType o2) {
                return Integer.compare(o1.getGameVersion(), o2.getGameVersion());
            }
        });

        byte lbpType = 1;
        for(InventoryObjectType type : typeArray)
        {
            String name = type.name().replaceAll("_", " ");
            name = name.charAt(0) + name.substring(1).toLowerCase();

            if(lbpType == 1 && type.getGameVersion() == 2)
            {
                typeList.addSeparator("2sep");
                typeList.addString("lbp2Types", Consts.FONT_SET_BOLD + "LBP2:");
                lbpType = 2;
            }
            if(lbpType == 2 && type.getGameVersion() == 3)
            {
                typeList.addSeparator("3sep");
                typeList.addString("lbp3Types", Consts.FONT_SET_BOLD + "LBP3:");
                lbpType = 3;
            }

            Checkbox cb = typeList.addCheckbox(type.name(), name, new Checkbox()
            {
                @Override
                public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement, boolean focusedOther) {
                    super.onClick(mouseInput, pos, button, action, mods, overElement, focusedOther);

                    if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos) && !overElement)
                        for (int i = 0; i < getThings().size(); i++)
                        {
                            Thing selected = getThings().get(i);
                            if(!selected.selected)
                                continue;

                            PMetadata metadata = selected.thing.getPart(Part.METADATA);

                            if(metadata.type.contains(type))
                                metadata.type.remove(type);
                            else
                                metadata.type.add(type);
                        }
                }
            });
            types.add(cb);
        }

        typePanel.elements.add(new Panel.PanelElement(type, 0.6f));
        typePanel.elements.add(new Panel.PanelElement(subType, 0.005f));
        subType = new ComboBox("subType", "Sub Type", view.renderer, view.loader, view.window) {
            @Override
            public int tabWidth() {
                return Math.round(200f * (getFontHeight() / 12f));
            }

            @Override
            public int[] getParentTransform() {
                return partComboBox.getTabPosWidth();
            }
        };

        subTypeList = subType.addElementList("subTypeList", Math.round(200f * (view.getFontHeight() / 12f)));

        earthRB = new Radiobutton()
        {
            @Override
            public void onCheck() {
                moonRB.isChecked = false;
                adventureRB.isChecked = false;
                externalRB.isChecked = false;

                for (int i = 0; i < getThings().size(); i++)
                {
                    Thing selected = getThings().get(i);
                    if(!selected.selected)
                        continue;

                    PMetadata metadata = selected.thing.getPart(Part.METADATA);

                    metadata.subType = Utils.setBitwiseBool(metadata.subType, InventoryObjectSubType.EARTH, this.isChecked);
                    metadata.subType = Utils.setBitwiseBool(metadata.subType, InventoryObjectSubType.MOON, false);
                    metadata.subType = Utils.setBitwiseBool(metadata.subType, InventoryObjectSubType.ADVENTURE, false);
                    metadata.subType = Utils.setBitwiseBool(metadata.subType, InventoryObjectSubType.EXTERNAL, false);
                }
            }
        };
        moonRB = new Radiobutton()
        {
            @Override
            public void onCheck() {
                earthRB.isChecked = false;
                adventureRB.isChecked = false;
                externalRB.isChecked = false;

                for (int i = 0; i < getThings().size(); i++)
                {
                    Thing selected = getThings().get(i);
                    if(!selected.selected)
                        continue;

                    PMetadata metadata = selected.thing.getPart(Part.METADATA);

                    metadata.subType = Utils.setBitwiseBool(metadata.subType, InventoryObjectSubType.MOON, this.isChecked);
                    metadata.subType = Utils.setBitwiseBool(metadata.subType, InventoryObjectSubType.EARTH, false);
                    metadata.subType = Utils.setBitwiseBool(metadata.subType, InventoryObjectSubType.ADVENTURE, false);
                    metadata.subType = Utils.setBitwiseBool(metadata.subType, InventoryObjectSubType.EXTERNAL, false);
                }
            }
        };
        adventureRB = new Radiobutton()
        {
            @Override
            public void onCheck() {
                moonRB.isChecked = false;
                earthRB.isChecked = false;
                externalRB.isChecked = false;

                for (int i = 0; i < getThings().size(); i++)
                {
                    Thing selected = getThings().get(i);
                    if(!selected.selected)
                        continue;

                    PMetadata metadata = selected.thing.getPart(Part.METADATA);

                    metadata.subType = Utils.setBitwiseBool(metadata.subType, InventoryObjectSubType.ADVENTURE, this.isChecked);
                    metadata.subType = Utils.setBitwiseBool(metadata.subType, InventoryObjectSubType.MOON, false);
                    metadata.subType = Utils.setBitwiseBool(metadata.subType, InventoryObjectSubType.EARTH, false);
                    metadata.subType = Utils.setBitwiseBool(metadata.subType, InventoryObjectSubType.EXTERNAL, false);
                }
            }
        };
        externalRB = new Radiobutton()
        {
            @Override
            public void onCheck() {
                moonRB.isChecked = false;
                adventureRB.isChecked = false;
                earthRB.isChecked = false;

                for (int i = 0; i < getThings().size(); i++)
                {
                    Thing selected = getThings().get(i);
                    if(!selected.selected)
                        continue;

                    PMetadata metadata = selected.thing.getPart(Part.METADATA);

                    metadata.subType = Utils.setBitwiseBool(metadata.subType, InventoryObjectSubType.EXTERNAL, this.isChecked);
                    metadata.subType = Utils.setBitwiseBool(metadata.subType, InventoryObjectSubType.MOON, false);
                    metadata.subType = Utils.setBitwiseBool(metadata.subType, InventoryObjectSubType.ADVENTURE, false);
                    metadata.subType = Utils.setBitwiseBool(metadata.subType, InventoryObjectSubType.EARTH, false);
                }
            }
        };

        subTypeList.addString("PlanetText", Consts.FONT_SET_BOLD + "Planets:");
        subTypeList.addCheckbox("EARTH", "Earth", earthRB);
        subTypeList.addCheckbox("MOON", "Moon", moonRB);
        subTypeList.addCheckbox("ADVENTURE", "Adventure", adventureRB);
        subTypeList.addCheckbox("EXTERNAL", "External", externalRB);

        nonSackboyRB = new Radiobutton()
        {
            @Override
            public void onCheck() {

                for (int i = 0; i < getThings().size(); i++)
                {
                    Thing selected = getThings().get(i);
                    if(!selected.selected)
                        continue;

                    PMetadata metadata = selected.thing.getPart(Part.METADATA);

                    metadata.subType = Utils.setBitwiseBool(metadata.subType, InventoryObjectSubType.CREATURE_MASK_BIRD, false);
                    metadata.subType = Utils.setBitwiseBool(metadata.subType, InventoryObjectSubType.CREATURE_MASK_DWARF, false);
                    metadata.subType = Utils.setBitwiseBool(metadata.subType, InventoryObjectSubType.CREATURE_MASK_QUAD, false);
                    metadata.subType = Utils.setBitwiseBool(metadata.subType, InventoryObjectSubType.CREATURE_MASK_GIANT, false);

                    metadata.subType = Utils.setBitwiseBool(metadata.subType, InventoryObjectSubType.CREATURE_MASK, this.isChecked);
                }
            }
        };
        giantRB = new Radiobutton()
        {
            @Override
            public void onCheck() {
                nonSackboyRB.isChecked = false;
                dwarfRB.isChecked = false;
                birdRB.isChecked = false;
                quadRB.isChecked = false;

                for (int i = 0; i < getThings().size(); i++)
                {
                    Thing selected = getThings().get(i);
                    if(!selected.selected)
                        continue;

                    PMetadata metadata = selected.thing.getPart(Part.METADATA);

                    metadata.subType = Utils.setBitwiseBool(metadata.subType, InventoryObjectSubType.CREATURE_MASK_BIRD, false);
                    metadata.subType = Utils.setBitwiseBool(metadata.subType, InventoryObjectSubType.CREATURE_MASK_DWARF, false);
                    metadata.subType = Utils.setBitwiseBool(metadata.subType, InventoryObjectSubType.CREATURE_MASK_QUAD, false);
                    metadata.subType = Utils.setBitwiseBool(metadata.subType, InventoryObjectSubType.CREATURE_MASK, false);

                    metadata.subType = Utils.setBitwiseBool(metadata.subType, InventoryObjectSubType.CREATURE_MASK_GIANT, this.isChecked);
                }
            }
        };
        dwarfRB = new Radiobutton()
        {
            @Override
            public void onCheck() {
                giantRB.isChecked = false;
                nonSackboyRB.isChecked = false;
                birdRB.isChecked = false;
                quadRB.isChecked = false;

                for (int i = 0; i < getThings().size(); i++)
                {
                    Thing selected = getThings().get(i);
                    if(!selected.selected)
                        continue;

                    PMetadata metadata = selected.thing.getPart(Part.METADATA);

                    metadata.subType = Utils.setBitwiseBool(metadata.subType, InventoryObjectSubType.CREATURE_MASK_BIRD, false);
                    metadata.subType = Utils.setBitwiseBool(metadata.subType, InventoryObjectSubType.CREATURE_MASK_GIANT, false);
                    metadata.subType = Utils.setBitwiseBool(metadata.subType, InventoryObjectSubType.CREATURE_MASK_QUAD, false);
                    metadata.subType = Utils.setBitwiseBool(metadata.subType, InventoryObjectSubType.CREATURE_MASK, false);

                    metadata.subType = Utils.setBitwiseBool(metadata.subType, InventoryObjectSubType.CREATURE_MASK_DWARF, this.isChecked);
                }
            }
        };
        birdRB = new Radiobutton()
        {
            @Override
            public void onCheck() {
                giantRB.isChecked = false;
                dwarfRB.isChecked = false;
                nonSackboyRB.isChecked = false;
                quadRB.isChecked = false;

                for (int i = 0; i < getThings().size(); i++)
                {
                    Thing selected = getThings().get(i);
                    if(!selected.selected)
                        continue;

                    PMetadata metadata = selected.thing.getPart(Part.METADATA);

                    metadata.subType = Utils.setBitwiseBool(metadata.subType, InventoryObjectSubType.CREATURE_MASK_DWARF, false);
                    metadata.subType = Utils.setBitwiseBool(metadata.subType, InventoryObjectSubType.CREATURE_MASK_GIANT, false);
                    metadata.subType = Utils.setBitwiseBool(metadata.subType, InventoryObjectSubType.CREATURE_MASK_QUAD, false);
                    metadata.subType = Utils.setBitwiseBool(metadata.subType, InventoryObjectSubType.CREATURE_MASK, false);

                    metadata.subType = Utils.setBitwiseBool(metadata.subType, InventoryObjectSubType.CREATURE_MASK_BIRD, this.isChecked);
                }
            }
        };
        quadRB = new Radiobutton()
        {
            @Override
            public void onCheck() {
                giantRB.isChecked = false;
                dwarfRB.isChecked = false;
                birdRB.isChecked = false;
                nonSackboyRB.isChecked = false;

                for (int i = 0; i < getThings().size(); i++)
                {
                    Thing selected = getThings().get(i);
                    if(!selected.selected)
                        continue;

                    PMetadata metadata = selected.thing.getPart(Part.METADATA);

                    metadata.subType = Utils.setBitwiseBool(metadata.subType, InventoryObjectSubType.CREATURE_MASK_BIRD, false);
                    metadata.subType = Utils.setBitwiseBool(metadata.subType, InventoryObjectSubType.CREATURE_MASK_DWARF, false);
                    metadata.subType = Utils.setBitwiseBool(metadata.subType, InventoryObjectSubType.CREATURE_MASK_GIANT, false);
                    metadata.subType = Utils.setBitwiseBool(metadata.subType, InventoryObjectSubType.CREATURE_MASK, false);

                    metadata.subType = Utils.setBitwiseBool(metadata.subType, InventoryObjectSubType.CREATURE_MASK_QUAD, this.isChecked);
                }
            }
        };
        subTypeList.addString("costumeText", Consts.FONT_SET_BOLD + "Costumes:");
        subTypeList.addCheckbox("CREATURE_MASK", "Non Sackboy", nonSackboyRB);
        subTypeList.addCheckbox("CREATURE_MASK_GIANT", "Big Toggle", giantRB);
        subTypeList.addCheckbox("CREATURE_MASK_DWARF", "Small Toggle", dwarfRB);
        subTypeList.addCheckbox("CREATURE_MASK_BIRD", "Swoop", birdRB);
        subTypeList.addCheckbox("CREATURE_MASK_QUAD", "Oddsock", quadRB);

        madeByMeRB = new Radiobutton()
        {
            @Override
            public void onCheck() {
                madeByAnyRB.isChecked = false;
                madeByOthersRB.isChecked = false;

                for (int i = 0; i < getThings().size(); i++)
                {
                    Thing selected = getThings().get(i);
                    if(!selected.selected)
                        continue;

                    PMetadata metadata = selected.thing.getPart(Part.METADATA);

                    metadata.subType = Utils.setBitwiseBool(metadata.subType, InventoryObjectSubType.MADE_BY_ME, this.isChecked);
                    metadata.subType = Utils.setBitwiseBool(metadata.subType, InventoryObjectSubType.MADE_BY_OTHERS, false);
                }
            }
        };
        madeByOthersRB = new Radiobutton()
        {
            @Override
            public void onCheck() {
                madeByMeRB.isChecked = false;
                madeByAnyRB.isChecked = false;

                for (int i = 0; i < getThings().size(); i++)
                {
                    Thing selected = getThings().get(i);
                    if(!selected.selected)
                        continue;

                    PMetadata metadata = selected.thing.getPart(Part.METADATA);

                    metadata.subType = Utils.setBitwiseBool(metadata.subType, InventoryObjectSubType.MADE_BY_OTHERS, this.isChecked);
                    metadata.subType = Utils.setBitwiseBool(metadata.subType, InventoryObjectSubType.MADE_BY_ME, false);
                }
            }
        };
        madeByAnyRB = new Radiobutton()
        {
            @Override
            public void onCheck() {
                madeByMeRB.isChecked = false;
                madeByOthersRB.isChecked = false;

                for (int i = 0; i < getThings().size(); i++)
                {
                    Thing selected = getThings().get(i);
                    if(!selected.selected)
                        continue;

                    PMetadata metadata = selected.thing.getPart(Part.METADATA);

                    metadata.subType = Utils.setBitwiseBool(metadata.subType, InventoryObjectSubType.MADE_BY_ME, this.isChecked);
                    metadata.subType = Utils.setBitwiseBool(metadata.subType, InventoryObjectSubType.MADE_BY_OTHERS, this.isChecked);
                }
            }
        };

        subTypeList.addSeparator("cosSep");
        subTypeList.addCheckbox("MADE_BY_ME", "Made by me", madeByMeRB);
        subTypeList.addCheckbox("MADE_BY_OTHERS", "Made by others", madeByOthersRB);
        subTypeList.addCheckbox("MADE_BY_ANYONE", "Made by anyone", madeByAnyRB);

        subTypeList.addSeparator("cosSep2");
        subTypeList.addCheckbox("SPECIAL_COSTUME", "Special Costume", new Checkbox()
        {
            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement, boolean focusedOther) {
                super.onClick(mouseInput, pos, button, action, mods, overElement, focusedOther);

                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos) && !overElement)
                    for (int i = 0; i < getThings().size(); i++)
                    {
                        Thing selected = getThings().get(i);
                        if(!selected.selected)
                            continue;

                        PMetadata metadata = selected.thing.getPart(Part.METADATA);

                        metadata.subType = Utils.setBitwiseBool(metadata.subType, InventoryObjectSubType.SPECIAL_COSTUME, this.isChecked);
                    }
            }
        });
        subTypeList.addCheckbox("FULL_COSTUME", "Full costume", new Checkbox()
        {
            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement, boolean focusedOther) {
                super.onClick(mouseInput, pos, button, action, mods, overElement, focusedOther);

                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos) && !overElement)
                    for (int i = 0; i < getThings().size(); i++)
                    {
                        Thing selected = getThings().get(i);
                        if(!selected.selected)
                            continue;

                        PMetadata metadata = selected.thing.getPart(Part.METADATA);

                        metadata.subType = Utils.setBitwiseBool(metadata.subType, InventoryObjectSubType.FULL_COSTUME, this.isChecked);
                    }
            }
        });
        subTypeList.addCheckbox("PLAYER_AVATAR", "Player avatar", new Checkbox()
        {
            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement, boolean focusedOther) {
                super.onClick(mouseInput, pos, button, action, mods, overElement, focusedOther);

                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos) && !overElement)
                    for (int i = 0; i < getThings().size(); i++)
                    {
                        Thing selected = getThings().get(i);
                        if(!selected.selected)
                            continue;

                        PMetadata metadata = selected.thing.getPart(Part.METADATA);

                        metadata.subType = Utils.setBitwiseBool(metadata.subType, InventoryObjectSubType.PLAYER_AVATAR, this.isChecked);
                    }
            }
        });

        subTypeList.addString("stickersText", Consts.FONT_SET_BOLD + "Stickers & Decorations:");
        subTypeList.addCheckbox("PAINTING", "Painting", new Checkbox()
        {
            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement, boolean focusedOther) {
                super.onClick(mouseInput, pos, button, action, mods, overElement, focusedOther);

                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos) && !overElement)
                    for (int i = 0; i < getThings().size(); i++)
                    {
                        Thing selected = getThings().get(i);
                        if(!selected.selected)
                            continue;

                        PMetadata metadata = selected.thing.getPart(Part.METADATA);

                        metadata.subType = Utils.setBitwiseBool(metadata.subType, InventoryObjectSubType.PAINTING, this.isChecked);
                    }
            }
        });
        subTypeList.addCheckbox("EARTH_DECORATION", "Earth decoration", new Checkbox()
        {
            @Override
            public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement, boolean focusedOther) {
                super.onClick(mouseInput, pos, button, action, mods, overElement, focusedOther);

                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && isMouseOverElement(pos) && !overElement)
                    for (int i = 0; i < getThings().size(); i++)
                    {
                        Thing selected = getThings().get(i);
                        if(!selected.selected)
                            continue;

                        PMetadata metadata = selected.thing.getPart(Part.METADATA);

                        metadata.subType = Utils.setBitwiseBool(metadata.subType, InventoryObjectSubType.EARTH_DECORATION, this.isChecked);
                    }
            }
        });
        typePanel.elements.add(new Panel.PanelElement(subType, 0.395f));

        ComboBox moreStuff = partComboBox.addComboBox("moreStuff", "More stuff", 250);

        Panel fluffCostPanel = moreStuff.addPanel("fluffCostPanel");
        fluffCostPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("fluffCostStr", "Fluff Cost:", view.renderer), 0.5f));
        fluffCost = new Textbox("fluffCost", view.renderer, view.loader, view.window);
        fluffCostPanel.elements.add(new Panel.PanelElement(fluffCost, 0.5f));

        Panel primaryIndexPanel = moreStuff.addPanel("primaryIndexPanel");
        primaryIndexPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("primaryIndexStr", "Primary Index:", view.renderer), 0.5f));
        primaryIndex = new Textbox("primaryIndex", view.renderer, view.loader, view.window);
        primaryIndexPanel.elements.add(new Panel.PanelElement(primaryIndex, 0.5f));

        float gap = 0.6f;
        float boxes = 1f - gap;

        moreStuff.addString("dateCreatedStr", "Date Created:");
        Panel dateCreatedPanel1 = moreStuff.addPanel("dateCreatedPanel1");

        dateCreatedPanel1.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("day", "Day:", view.renderer), 0.16f));
        dateCreatedDay = new Textbox("dateCreatedDay", new Vector2f(), new Vector2f(view.getFontHeight()), view.renderer, view.loader, view.window)
        {
            @Override
            public void setFocused(boolean focused) {
                super.setFocused(focused);

                if(focused)
                    return;

                String text = this.getText();

                if(text.isEmpty() || text.isBlank() || text.equalsIgnoreCase(" "))
                    return;

                int number = Utils.parseInt(text);

                if(number < 1)
                    this.setText("1");
                if(number > 31)
                    this.setText("31");
            }
        }.noLetters().noOthers();
        dateCreatedPanel1.elements.add(new Panel.PanelElement(dateCreatedDay, (boxes + 0.02f)/3f));
        dateCreatedPanel1.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("month", "Month:", view.renderer), gap/3f));
        dateCreatedMonth = new Textbox("dateCreatedMonth", new Vector2f(), new Vector2f(view.getFontHeight()), view.renderer, view.loader, view.window)
        {
            @Override
            public void setFocused(boolean focused) {
                super.setFocused(focused);

                if(focused)
                    return;

                String text = this.getText();

                if(text.isEmpty() || text.isBlank() || text.equalsIgnoreCase(" "))
                    return;

                int number = Utils.parseInt(text);

                if(number < 1)
                    this.setText("1");
                if(number > 12)
                    this.setText("12");
            }
        }.noLetters().noOthers();
        dateCreatedPanel1.elements.add(new Panel.PanelElement(dateCreatedMonth, (boxes + 0.02f)/3f));
        dateCreatedPanel1.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("year", "Year:", view.renderer), gap/3f + 0.02f));
        dateCreatedYear = new Textbox("dateCreatedYear", new Vector2f(), new Vector2f(view.getFontHeight()), view.renderer, view.loader, view.window)
        {
            @Override
            public void setFocused(boolean focused) {
                super.setFocused(focused);

                if(focused)
                    return;

                String text = this.getText();

                if(text.isEmpty() || text.isBlank() || text.equalsIgnoreCase(" "))
                    return;

                int number = Utils.parseInt(text);

                if(number < 1900)
                    this.setText("1900");
            }
        }.noLetters().noOthers();
        dateCreatedPanel1.elements.add(new Panel.PanelElement(dateCreatedYear, (boxes + 0.02f)/3f));

        Panel dateCreatedPanel2 = moreStuff.addPanel("dateCreatedPanel2");

        dateCreatedPanel2.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("hour", "Hour:", view.renderer), 0.16f));
        dateCreatedHour = new Textbox("dateCreatedHour", new Vector2f(), new Vector2f(view.getFontHeight()), view.renderer, view.loader, view.window)
        {
            @Override
            public void setFocused(boolean focused) {
                super.setFocused(focused);

                if(focused)
                    return;

                String text = this.getText();

                if(text.isEmpty() || text.isBlank() || text.equalsIgnoreCase(" "))
                    return;

                int number = Utils.parseInt(text);

                if(number < 0)
                    this.setText("0");
                if(number > 23)
                    this.setText("23");
            }
        }.noLetters().noOthers();
        dateCreatedPanel2.elements.add(new Panel.PanelElement(dateCreatedHour, (boxes + 0.02f)/3f));
        dateCreatedPanel2.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("minute", "Minute:", view.renderer), gap/3f));
        dateCreatedMinute = new Textbox("dateCreatedMinute", new Vector2f(), new Vector2f(view.getFontHeight()), view.renderer, view.loader, view.window)
        {
            @Override
            public void setFocused(boolean focused) {
                super.setFocused(focused);

                if(focused)
                    return;

                String text = this.getText();

                if(text.isEmpty() || text.isBlank() || text.equalsIgnoreCase(" "))
                    return;

                int number = Utils.parseInt(text);

                if(number < 0)
                    this.setText("0");
                if(number > 59)
                    this.setText("59");
            }
        }.noLetters().noOthers();
        dateCreatedPanel2.elements.add(new Panel.PanelElement(dateCreatedMinute, (boxes + 0.02f)/3f));
        dateCreatedPanel2.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("second", "Second:", view.renderer), gap/3f + 0.02f));
        dateCreatedSecond = new Textbox("dateCreatedSecond", new Vector2f(), new Vector2f(view.getFontHeight()), view.renderer, view.loader, view.window)
        {
            @Override
            public void setFocused(boolean focused) {
                super.setFocused(focused);

                if(focused)
                    return;

                String text = this.getText();

                if(text.isEmpty() || text.isBlank() || text.equalsIgnoreCase(" "))
                    return;

                int number = Utils.parseInt(text);

                if(number < 0)
                    this.setText("0");
                if(number > 59)
                    this.setText("59");
            }
        }.noLetters().noOthers();
        dateCreatedPanel2.elements.add(new Panel.PanelElement(dateCreatedSecond, (boxes + 0.02f)/3f));
    }

    @Override
    public void addValues(ArrayList<Integer> selected, ArrayList<Thing> things) {

        String iconDescr = null;
        String nameTag = null;
        String descTag = null;
        String locationTag = null;
        String categoryTag = null;

        float titleKey = Float.POSITIVE_INFINITY;
        float descKey = Float.POSITIVE_INFINITY;
        float locationKey = Float.POSITIVE_INFINITY;
        float categoryKey = Float.POSITIVE_INFINITY;

        int refere = -1;
        int allowEmit = -1;

        EnumSet<InventoryObjectType> types = EnumSet.noneOf(InventoryObjectType.class);
        int subType = -1;

        float fluff = Float.POSITIVE_INFINITY;
        float primIndex = Float.POSITIVE_INFINITY;

        float day = Float.POSITIVE_INFINITY;
        float month = Float.POSITIVE_INFINITY;
        float year = Float.POSITIVE_INFINITY;
        float hour = Float.POSITIVE_INFINITY;
        float minute = Float.POSITIVE_INFINITY;
        float second = Float.POSITIVE_INFINITY;

        for(int i : selected) {
            Thing thing = things.get(i);
            PMetadata metadata = thing.thing.getPart(Part.METADATA);

            if (metadata == null)
                continue;

            String icn = metadata.icon == null ? "" : metadata.icon.isGUID() ? metadata.icon.getGUID().toString() : metadata.icon.getSHA1().toString();
            if (iconDescr == null)
                iconDescr = icn;
            else if (!iconDescr.equalsIgnoreCase(icn))
                iconDescr = "";

            nameTag = compareString(nameTag, metadata.nameTranslationTag);
            descTag = compareString(descTag, metadata.descTranslationTag);
            locationTag = compareString(locationTag, metadata.locationTag);
            categoryTag = compareString(categoryTag, metadata.categoryTag);

            if(Float.isInfinite(titleKey))
                titleKey = metadata.titleKey;
            else if(titleKey != metadata.titleKey)
                titleKey = Consts.NaNf;
            if(Float.isInfinite(descKey))
                descKey = metadata.descriptionKey;
            else if(descKey != metadata.descriptionKey)
                descKey = Consts.NaNf;
            if(Float.isInfinite(locationKey))
                locationKey = metadata.location;
            else if(locationKey != metadata.location)
                locationKey = Consts.NaNf;
            if(Float.isInfinite(categoryKey))
                categoryKey = metadata.category;
            else if(categoryKey != metadata.category)
                categoryKey = Consts.NaNf;

            if (refere == -1)
                refere = metadata.referencable ? 1 : 0;
            else if (refere != (metadata.referencable ? 1 : 0))
                refere = 0;
            if (allowEmit == -1)
                allowEmit = metadata.allowEmit ? 1 : 0;
            else if (allowEmit != (metadata.allowEmit ? 1 : 0))
                allowEmit = 0;

            if(types != null && types.equals(EnumSet.noneOf(InventoryObjectType.class)))
            {
                types = metadata.type;
            }
            else if(types != null && !types.equals(metadata.type))
                types = null;

            if(subType == -1)
                subType = metadata.subType;
            else if(subType != metadata.subType)
                subType = -2;

            if(Float.isInfinite(fluff))
                fluff = metadata.fluffCost;
            else if(fluff != metadata.fluffCost)
                fluff = Consts.NaNf;

            if(Float.isInfinite(primIndex))
                primIndex = metadata.primaryIndex;
            else if(primIndex != metadata.primaryIndex)
                primIndex = Consts.NaNf;

            Date cDate = new Date(metadata.creationDate * 1000);

            if(Float.isInfinite(day))
                day = cDate.getDate();
            else if(day != cDate.getDate())
                day = Consts.NaNf;

            if(Float.isInfinite(month))
                month = cDate.getMonth();
            else if(month != cDate.getMonth())
                month = Consts.NaNf;

            if(Float.isInfinite(year))
                year = cDate.getYear();
            else if(year != cDate.getYear())
                year = Consts.NaNf;

            if(Float.isInfinite(hour))
                hour = cDate.getHours();
            else if(hour != cDate.getHours())
                hour = Consts.NaNf;

            if(Float.isInfinite(minute))
                minute = cDate.getMinutes();
            else if(minute != cDate.getMinutes())
                minute = Consts.NaNf;

            if(Float.isInfinite(second))
                second = cDate.getSeconds();
            else if(second != cDate.getSeconds())
                second = Consts.NaNf;
        }

        if(subType != -1 && subType != -2)
            for(Element e : subTypeList.elements)
            {
                try {
                    if(e instanceof Checkbox)
                    {
                        int flag = InventoryObjectSubType.class.getField(e.id).getInt(InventoryObjectSubType.class);

                        switch (e.id.toUpperCase())
                        {
                            case "MADE_BY_ANYONE":
                                ((Checkbox)e).isChecked = Utils.isBitwiseBool(subType, InventoryObjectSubType.MADE_BY_ME) && Utils.isBitwiseBool(subType, InventoryObjectSubType.MADE_BY_OTHERS);
                                continue;
                            case "MADE_BY_ME":
                                ((Checkbox)e).isChecked = Utils.isBitwiseBool(subType, InventoryObjectSubType.MADE_BY_ME) && !Utils.isBitwiseBool(subType, InventoryObjectSubType.MADE_BY_OTHERS);
                                continue;
                            case "MADE_BY_OTHERS":
                                ((Checkbox)e).isChecked = !Utils.isBitwiseBool(subType, InventoryObjectSubType.MADE_BY_ME) && Utils.isBitwiseBool(subType, InventoryObjectSubType.MADE_BY_OTHERS);
                                continue;
                            case "EARTH":
                                ((Checkbox)e).isChecked = !Utils.isBitwiseBool(subType, InventoryObjectSubType.MOON) && !Utils.isBitwiseBool(subType, InventoryObjectSubType.ADVENTURE) && !Utils.isBitwiseBool(subType, InventoryObjectSubType.EXTERNAL);
                                continue;
                            case "CREATURE_MASK":
                                ((Checkbox)e).isChecked = Utils.isBitwiseBool(subType, InventoryObjectSubType.CREATURE_MASK);
                                continue;
                            case "CREATURE_MASK_DWARF":
                                ((Checkbox)e).isChecked = Utils.isBitwiseBool(subType, InventoryObjectSubType.CREATURE_MASK_DWARF) && !Utils.isBitwiseBool(subType, InventoryObjectSubType.CREATURE_MASK_GIANT);
                                continue;
                            case "CREATURE_MASK_GIANT":
                                ((Checkbox)e).isChecked = Utils.isBitwiseBool(subType, InventoryObjectSubType.CREATURE_MASK_GIANT) && !Utils.isBitwiseBool(subType, InventoryObjectSubType.CREATURE_MASK_DWARF);
                                continue;
                            case "CREATURE_MASK_BIRD":
                                ((Checkbox)e).isChecked = Utils.isBitwiseBool(subType, InventoryObjectSubType.CREATURE_MASK_BIRD) && !Utils.isBitwiseBool(subType, InventoryObjectSubType.CREATURE_MASK_QUAD);
                                continue;
                            case "CREATURE_MASK_QUAD":
                                ((Checkbox)e).isChecked = Utils.isBitwiseBool(subType, InventoryObjectSubType.CREATURE_MASK_QUAD) && !Utils.isBitwiseBool(subType, InventoryObjectSubType.CREATURE_MASK_BIRD);
                                continue;
                        }

                        ((Checkbox)e).isChecked = Utils.isBitwiseBool(subType, flag);
                    }
                } catch (Exception ex) {
                    print.stackTrace(ex);
                }
            }

        String icn = icon.setTextboxValueString(iconDescr);
        String nameTag1 = this.nameTag.setTextboxValueString(nameTag);
        String descTag1 = descriptionTag.setTextboxValueString(descTag);
        String locationTag1 = this.locationTag.setTextboxValueString(locationTag);
        String categoryTag1 = this.categoryTag.setTextboxValueString(categoryTag);

        Vector2f nameKey = this.nameKey.setTextboxValueFloat(titleKey);
        Vector2f descKey1 = this.descriptionKey.setTextboxValueFloat(descKey);
        Vector2f categoryKey1 = this.categoryKey.setTextboxValueFloat(categoryKey);
        Vector2f locationKey1 = this.locationKey.setTextboxValueFloat(locationKey);

        referencable.isChecked = refere == 1;
        this.allowEmit.isChecked = allowEmit == 1;

        if(types == null)
        {
            for(Checkbox c : this.types)
                c.isChecked = false;
        }
        else
        {
            for(Checkbox c : this.types) {
                c.isChecked = false;

                for(InventoryObjectType type : types)
                    if (c.id.equalsIgnoreCase(type.name()))
                        c.isChecked = true;
            }
        }

        Vector2f fluffCost = this.fluffCost.setTextboxValueFloat(fluff);
        Vector2f primaryIndex = this.primaryIndex.setTextboxValueFloat(primIndex);

        Date currentDate = new Date();

        Vector2f cDay = dateCreatedDay.setTextboxValueFloat(Float.isInfinite(day) || Float.isNaN(day) ? currentDate.getDate() : day);
        Vector2f cMonth = dateCreatedMonth.setTextboxValueFloat(Float.isInfinite(month) || Float.isNaN(month) ? currentDate.getMonth() : month + 1);
        Vector2f cYear = dateCreatedYear.setTextboxValueFloat(Float.isInfinite(year) || Float.isNaN(year) ? currentDate.getYear() : year + 1900);
        Vector2f cHour = dateCreatedHour.setTextboxValueFloat(Float.isInfinite(hour) || Float.isNaN(hour) ? currentDate.getHours() : hour);
        Vector2f cMinute = dateCreatedMinute.setTextboxValueFloat(Float.isInfinite(minute) || Float.isNaN(minute) ? currentDate.getMinutes() : minute);
        Vector2f cSecond = dateCreatedSecond.setTextboxValueFloat(Float.isInfinite(second) || Float.isNaN(second) ? currentDate.getSeconds() : second);

        for(int i : selected) {
            Thing thing = things.get(i);
            PMetadata metadata = thing.thing.getPart(Part.METADATA);

            if (metadata == null)
                continue;

            if (icn != null)
                try{metadata.icon = new ResourceDescriptor(icn.trim(), ResourceType.TEXTURE);}catch (Exception e){}

            if(nameTag1 != null)
                metadata.nameTranslationTag = nameTag1;
            if(descTag1 != null)
                metadata.descTranslationTag = descTag1;
            if(locationTag1 != null)
                metadata.locationTag = locationTag1;
            if(categoryTag1 != null)
                metadata.categoryTag = categoryTag1;

            if(nameKey.y == 1)
                metadata.titleKey = (long) nameKey.x;
            if(descKey1.y == 1)
                metadata.descriptionKey = (long) descKey1.x;
            if(categoryKey1.y == 1)
                metadata.category = (long) categoryKey1.x;
            if(locationKey1.y == 1)
                metadata.location = (long) locationKey1.x;

            if(fluffCost.y == 1)
                metadata.fluffCost = (int) fluffCost.x;
            if(primaryIndex.y == 1)
                metadata.primaryIndex = (int) primaryIndex.x;

            Date cDate = new Date(metadata.creationDate * 1000);

            int creationDay = cDay.y == 1 ? (int) Math.clamp(1, 31, cDay.x) : cDate.getDate();
            int creationMonth = cMonth.y == 1 ? (int) Math.clamp(1, 12, cMonth.x) - 1 : cDate.getMonth();
            int creationYear = cYear.y == 1 ? (int) Math.max(cYear.x - 1900, 0) : cDate.getYear();
            int creationHour = cHour.y == 1 ? (int) Math.clamp(0, 23, cHour.x) : cDate.getHours();
            int creationMinute = cMinute.y == 1 ? (int) Math.clamp(0, 59, cMinute.x) : cDate.getMinutes();
            int creationSecond = cSecond.y == 1 ? (int) Math.clamp(0, 59, cSecond.x) : cDate.getSeconds();

            metadata.creationDate = new Date(creationYear, creationMonth, creationDay, creationHour, creationMinute, creationSecond).getTime() / 1000;
        }
    }

    @Override
    public void resize(View3D view) {
        super.resize(view);
        typeList.size.y = Math.round(200f * (view.getFontHeight() / 12f));
        subTypeList.size.y = Math.round(200f * (view.getFontHeight() / 12f));
    }
}
