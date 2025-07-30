package bog.lbpas.view3d.mainWindow.screens.thingPart.parts;

import bog.lbpas.view3d.core.types.Thing;
import bog.lbpas.view3d.mainWindow.View3D;
import bog.lbpas.view3d.renderer.gui.elements.DropDownTab;
import bog.lbpas.view3d.renderer.gui.elements.Element;
import bog.lbpas.view3d.renderer.gui.elements.Panel;
import bog.lbpas.view3d.renderer.gui.elements.Textbox;

import java.util.ArrayList;

/**
 * @author Bog
 */
public abstract class PartMetadata extends iPart {

    public PartMetadata(int tabWidth, float comboWidth, float panelHeight, float closeWidth, float finalGap, Element tab, View3D view) {
        super(cwlib.enums.Part.METADATA, "PMetadata", "Metadata", tabWidth, comboWidth, panelHeight, closeWidth, finalGap, tab, view);
    }

    Textbox nameTag;
    Textbox descriptionTag;
    Textbox locationTag;
    Textbox categoryTag;

    @Override
    public void init(View3D view) {

//        partComboBox.addString("tags", "Tags:");
//
//        Panel nameTagPanel = partComboBox.addPanel("nameTagPanel");
//        nameTagPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("nameTagStr", "Name:", view.renderer), 0.5f));
//        nameTag = new Textbox("nameTag", view.renderer, view.loader, view.window);
//        nameTagPanel.elements.add(new Panel.PanelElement(nameTag, 0.5f));
//
//        Panel descriptionTagPanel = partComboBox.addPanel("descriptionTagPanel");
//        descriptionTagPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("descriptionTagStr", "Description:", view.renderer), 0.5f));
//        descriptionTag = new Textbox("descriptionTag", view.renderer, view.loader, view.window);
//        descriptionTagPanel.elements.add(new Panel.PanelElement(descriptionTag, 0.5f));
//
//        Panel locationTagPanel = partComboBox.addPanel("locationTagPanel");
//        locationTagPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("locationTagStr", "Location:", view.renderer), 0.5f));
//        locationTag = new Textbox("locationTag", view.renderer, view.loader, view.window);
//        locationTagPanel.elements.add(new Panel.PanelElement(locationTag, 0.5f));
//
//        Panel categoryTagPanel = partComboBox.addPanel("categoryTagPanel");
//        categoryTagPanel.elements.add(new Panel.PanelElement(new DropDownTab.StringElement("categoryTagStr", "Category:", view.renderer), 0.5f));
//        categoryTag = new Textbox("categoryTag", view.renderer, view.loader, view.window);
//        categoryTagPanel.elements.add(new Panel.PanelElement(categoryTag, 0.5f));
//
//        partComboBox.addSeparator("sepTag");
//
//        partComboBox.addString("keys", "Keys:");
//
//        partComboBox.addSeparator("sepTag");
    }

    @Override
    public void addValues(ArrayList<Integer> selected, ArrayList<Thing> things) {

    }
}
