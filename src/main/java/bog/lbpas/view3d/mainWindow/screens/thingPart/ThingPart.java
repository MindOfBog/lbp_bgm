package bog.lbpas.view3d.mainWindow.screens.thingPart;

import bog.lbpas.view3d.core.types.Thing;
import bog.lbpas.view3d.mainWindow.View3D;
import bog.lbpas.view3d.mainWindow.screens.thingPart.parts.*;
import bog.lbpas.view3d.renderer.gui.elements.*;
import bog.lbpas.view3d.utils.Utils;
import bog.lbpas.view3d.utils.print;
import cwlib.enums.Part;
import cwlib.io.Serializable;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

/**
 * @author Bog
 */
public class ThingPart {

    ElementList elementList;

    public ThingPart(View3D view, ElementList elements, DropDownTab currentSelectionTab, ArrayList<Thing> things) {
        elementList = elements;
        initParts(view, currentSelectionTab, things);
    }

    ArrayList<iPart> parts;

    public void addParts(ArrayList<Thing> things) {

        for(iPart part : parts)
            part.addPartsReset();

        int tabWidth = 200;

        ArrayList<Integer> selected = new ArrayList<>();

        for (int i = 0; i < things.size(); i++)
            if(things.get(i) != null && things.get(i).selected) {
                selected.add(i);
                cwlib.structs.things.Thing t = things.get(i).thing;
                for(iPart part : parts)
                    part.hasPart(t);
            }

        if(selected.size() <= 0)
        {
            for(iPart part : parts)
            {
                elementList.elements.remove(part.partPanel);
                part.collapse();
            }
            return;
        }

        for(iPart part : parts)
        {
            part.addElements(elementList);
            part.addValues(selected, things);
        }
    }

    private void initParts(View3D view, DropDownTab currentSelectionTab, ArrayList<Thing> things)
    {
        parts = new ArrayList<>();
        int tabWidth = 225;
        float panelWidth = Math.round(elementList.size.x - 4f);
        float panelHeight = 20f;
        float closeWidth = Utils.round(panelHeight / panelWidth, 3);
        float finalGap = Utils.round(2f / panelWidth, 3);
        float comboWidth = Utils.round(0.997f - closeWidth - finalGap, 3);

        ThingPart thingPart = this;

        parts.add(new PartAnimation(tabWidth, comboWidth, panelHeight, closeWidth, finalGap, currentSelectionTab, view) {
            @Override
            public void removePart(Part part) {
                thingPart.removePart(part, things);
            }
        });
        parts.add(new PartAnimationTweak(tabWidth, comboWidth, panelHeight, closeWidth, finalGap, currentSelectionTab, view) {
            @Override
            public void removePart(Part part) {
                thingPart.removePart(part, things);
            }
        });
        parts.add(new PartAtmosphericTweak(tabWidth, comboWidth, panelHeight, closeWidth, finalGap, currentSelectionTab, view) {
            @Override
            public void removePart(Part part) {
                thingPart.removePart(part, things);
            }
        });
        parts.add(new PartAudioWorld(tabWidth, comboWidth, panelHeight, closeWidth, finalGap, currentSelectionTab, view) {
            @Override
            public void removePart(Part part) {
                thingPart.removePart(part, things);
            }
        });
        parts.add(new PartBody(tabWidth, comboWidth, panelHeight, closeWidth, finalGap, currentSelectionTab, view) {
            @Override
            public void removePart(Part part) {
                thingPart.removePart(part, things);
            }
        });
        parts.add(new PartCameraTweak(tabWidth, comboWidth, panelHeight, closeWidth, finalGap, currentSelectionTab, view) {
            @Override
            public void removePart(Part part) {
                thingPart.removePart(part, things);
            }
        });
        parts.add(new PartCheckpoint(tabWidth, comboWidth, panelHeight, closeWidth, finalGap, currentSelectionTab, view) {
            @Override
            public void removePart(Part part) {
                thingPart.removePart(part, things);
            }
        });
        parts.add(new PartConnectorHook(tabWidth, comboWidth, panelHeight, closeWidth, finalGap, currentSelectionTab, view) {
            @Override
            public void removePart(Part part) {
                thingPart.removePart(part, things);
            }
        });
        parts.add(new PartControlinator(tabWidth, comboWidth, panelHeight, closeWidth, finalGap, currentSelectionTab, view) {
            @Override
            public void removePart(Part part) {
                thingPart.removePart(part, things);
            }
        });
        parts.add(new PartCostume(tabWidth, comboWidth, panelHeight, closeWidth, finalGap, currentSelectionTab, view) {
            @Override
            public void removePart(Part part) {
                thingPart.removePart(part, things);
            }
        });
        parts.add(new PartCreature(tabWidth, comboWidth, panelHeight, closeWidth, finalGap, currentSelectionTab, view) {
            @Override
            public void removePart(Part part) {
                thingPart.removePart(part, things);
            }
        });
        parts.add(new PartDecorations(tabWidth, comboWidth, panelHeight, closeWidth, finalGap, currentSelectionTab, view) {
            @Override
            public void removePart(Part part) {
                thingPart.removePart(part, things);
            }
        });
        parts.add(new PartEffector(tabWidth, comboWidth, panelHeight, closeWidth, finalGap, currentSelectionTab, view) {
            @Override
            public void removePart(Part part) {
                thingPart.removePart(part, things);
            }
        });
        parts.add(new PartEnemy(tabWidth, comboWidth, panelHeight, closeWidth, finalGap, currentSelectionTab, view) {
            @Override
            public void removePart(Part part) {
                thingPart.removePart(part, things);
            }
        });
        parts.add(new PartFader(tabWidth, comboWidth, panelHeight, closeWidth, finalGap, currentSelectionTab, view) {
            @Override
            public void removePart(Part part) {
                thingPart.removePart(part, things);
            }
        });
        parts.add(new PartGameplayData(tabWidth, comboWidth, panelHeight, closeWidth, finalGap, currentSelectionTab, view) {
            @Override
            public void removePart(Part part) {
                thingPart.removePart(part, things);
            }
        });
        parts.add(new PartGeneratedMesh(tabWidth, comboWidth, panelHeight, closeWidth, finalGap, currentSelectionTab, view) {
            @Override
            public void removePart(Part part) {
                thingPart.removePart(part, things);
            }
        });
        parts.add(new PartGroup(tabWidth, comboWidth, panelHeight, closeWidth, finalGap, currentSelectionTab, view) {
            @Override
            public void removePart(Part part) {
                thingPart.removePart(part, things);
            }
        });
        parts.add(new PartHudElem(tabWidth, comboWidth, panelHeight, closeWidth, finalGap, currentSelectionTab, view) {
            @Override
            public void removePart(Part part) {
                thingPart.removePart(part, things);
            }
        });
        parts.add(new PartInstrument(tabWidth, comboWidth, panelHeight, closeWidth, finalGap, currentSelectionTab, view) {
            @Override
            public void removePart(Part part) {
                thingPart.removePart(part, things);
            }
        });
        parts.add(new PartJoint(tabWidth, comboWidth, panelHeight, closeWidth, finalGap, currentSelectionTab, view) {
            @Override
            public void removePart(Part part) {
                thingPart.removePart(part, things);
            }
        });
        parts.add(new PartLevelSettings(tabWidth, comboWidth, panelHeight, closeWidth, finalGap, currentSelectionTab, view) {
            @Override
            public void removePart(Part part) {
                thingPart.removePart(part, things);
            }
        });
        parts.add(new PartMaterialOverride(tabWidth, comboWidth, panelHeight, closeWidth, finalGap, currentSelectionTab, view) {
            @Override
            public void removePart(Part part) {
                thingPart.removePart(part, things);
            }
        });
        parts.add(new PartMaterialTweak(tabWidth, comboWidth, panelHeight, closeWidth, finalGap, currentSelectionTab, view) {
            @Override
            public void removePart(Part part) {
                thingPart.removePart(part, things);
            }
        });
        parts.add(new PartMetadata(tabWidth, comboWidth, panelHeight, closeWidth, finalGap, currentSelectionTab, view) {
            @Override
            public void removePart(Part part) {
                thingPart.removePart(part, things);
            }
        });
        parts.add(new PartMicrochip(tabWidth, comboWidth, panelHeight, closeWidth, finalGap, currentSelectionTab, view) {
            @Override
            public void removePart(Part part) {
                thingPart.removePart(part, things);
            }
        });
        parts.add(new PartNpc(tabWidth, comboWidth, panelHeight, closeWidth, finalGap, currentSelectionTab, view) {
            @Override
            public void removePart(Part part) {
                thingPart.removePart(part, things);
            }
        });
        parts.add(new PartPhysicsTweak(tabWidth, comboWidth, panelHeight, closeWidth, finalGap, currentSelectionTab, view) {
            @Override
            public void removePart(Part part) {
                thingPart.removePart(part, things);
            }
        });
        parts.add(new PartPocketItem(tabWidth, comboWidth, panelHeight, closeWidth, finalGap, currentSelectionTab, view) {
            @Override
            public void removePart(Part part) {
                thingPart.removePart(part, things);
            }
        });
        parts.add(new PartPoppetPowerup(tabWidth, comboWidth, panelHeight, closeWidth, finalGap, currentSelectionTab, view) {
            @Override
            public void removePart(Part part) {
                thingPart.removePart(part, things);
            }
        });
        parts.add(new PartPos(tabWidth, comboWidth, panelHeight, closeWidth, finalGap, currentSelectionTab, view) {
            @Override
            public void removePart(Part part) {
                thingPart.removePart(part, things);
            }
        });
        parts.add(new PartPowerUp(tabWidth, comboWidth, panelHeight, closeWidth, finalGap, currentSelectionTab, view) {
            @Override
            public void removePart(Part part) {
                thingPart.removePart(part, things);
            }
        });
        parts.add(new PartQuest(tabWidth, comboWidth, panelHeight, closeWidth, finalGap, currentSelectionTab, view) {
            @Override
            public void removePart(Part part) {
                thingPart.removePart(part, things);
            }
        });
        parts.add(new PartRef(tabWidth, comboWidth, panelHeight, closeWidth, finalGap, currentSelectionTab, view) {
            @Override
            public void removePart(Part part) {
                thingPart.removePart(part, things);
            }
        });
        parts.add(new PartRenderMesh(tabWidth, comboWidth, panelHeight, closeWidth, finalGap, currentSelectionTab, view) {
            @Override
            public void removePart(Part part) {
                thingPart.removePart(part, things);
            }
        });
        parts.add(new PartScript(tabWidth, comboWidth, panelHeight, closeWidth, finalGap, currentSelectionTab, view) {
            @Override
            public void removePart(Part part) {
                thingPart.removePart(part, things);
            }
        });
        parts.add(new PartScriptName(tabWidth, comboWidth, panelHeight, closeWidth, finalGap, currentSelectionTab, view) {
            @Override
            public void removePart(Part part) {
                thingPart.removePart(part, things);
            }
        });
        parts.add(new PartSequencer(tabWidth, comboWidth, panelHeight, closeWidth, finalGap, currentSelectionTab, view) {
            @Override
            public void removePart(Part part) {
                thingPart.removePart(part, things);
            }
        });
        parts.add(new PartShape(tabWidth, comboWidth, panelHeight, closeWidth, finalGap, currentSelectionTab, view) {
            @Override
            public void removePart(Part part) {
                thingPart.removePart(part, things);
            }
        });
        parts.add(new PartSpriteLight(tabWidth, comboWidth, panelHeight, closeWidth, finalGap, currentSelectionTab, view) {
            @Override
            public void removePart(Part part) {
                thingPart.removePart(part, things);
            }
        });
        parts.add(new PartStickers(tabWidth, comboWidth, panelHeight, closeWidth, finalGap, currentSelectionTab, view) {
            @Override
            public void removePart(Part part) {
                thingPart.removePart(part, things);
            }
        });
        parts.add(new PartStreamingData(tabWidth, comboWidth, panelHeight, closeWidth, finalGap, currentSelectionTab, view) {
            @Override
            public void removePart(Part part) {
                thingPart.removePart(part, things);
            }
        });
        parts.add(new PartStreamingHint(tabWidth, comboWidth, panelHeight, closeWidth, finalGap, currentSelectionTab, view) {
            @Override
            public void removePart(Part part) {
                thingPart.removePart(part, things);
            }
        });
        parts.add(new PartSwitch(tabWidth, comboWidth, panelHeight, closeWidth, finalGap, currentSelectionTab, view) {
            @Override
            public void removePart(Part part) {
                thingPart.removePart(part, things);
            }
        });
        parts.add(new PartSwitchInput(tabWidth, comboWidth, panelHeight, closeWidth, finalGap, currentSelectionTab, view) {
            @Override
            public void removePart(Part part) {
                thingPart.removePart(part, things);
            }
        });
        parts.add(new PartSwitchKey(tabWidth, comboWidth, panelHeight, closeWidth, finalGap, currentSelectionTab, view) {
            @Override
            public void removePart(Part part) {
                thingPart.removePart(part, things);
            }
        });
        parts.add(new PartTagSynchroniser(tabWidth, comboWidth, panelHeight, closeWidth, finalGap, currentSelectionTab, view) {
            @Override
            public void removePart(Part part) {
                thingPart.removePart(part, things);
            }
        });
        parts.add(new PartTransition(tabWidth, comboWidth, panelHeight, closeWidth, finalGap, currentSelectionTab, view) {
            @Override
            public void removePart(Part part) {
                thingPart.removePart(part, things);
            }
        });
        parts.add(new PartTrigger(tabWidth, comboWidth, panelHeight, closeWidth, finalGap, currentSelectionTab, view) {
            @Override
            public void removePart(Part part) {
                thingPart.removePart(part, things);
            }
        });
        parts.add(new PartWindTweak(tabWidth, comboWidth, panelHeight, closeWidth, finalGap, currentSelectionTab, view) {
            @Override
            public void removePart(Part part) {
                thingPart.removePart(part, things);
            }
        });
        parts.add(new PartWorld(tabWidth, comboWidth, panelHeight, closeWidth, finalGap, currentSelectionTab, view) {
            @Override
            public void removePart(Part part) {
                thingPart.removePart(part, things);
            }
        });
        parts.add(new PartWormhole(tabWidth, comboWidth, panelHeight, closeWidth, finalGap, currentSelectionTab, view) {
            @Override
            public void removePart(Part part) {
                thingPart.removePart(part, things);
            }
        });
        parts.add(new PartYellowHead(tabWidth, comboWidth, panelHeight, closeWidth, finalGap, currentSelectionTab, view) {
            @Override
            public void removePart(Part part) {
                thingPart.removePart(part, things);
            }
        });
    }

    public void selectionChange()
    {
        for(iPart part : parts)
        {
            elementList.elements.remove(part.partPanel);
            part.selectionChange();
        }
    }

    public void removePart(Part part, ArrayList<Thing> things)
    {
        for (int i = 0; i < things.size(); i++)
            if (things.get(i).selected && things.get(i).thing.hasPart(part))
            {
                things.get(i).thing.setPart(part, null);
                things.get(i).reloadModel();
            }
    }

    public <T extends Serializable> void addPart(Part part, T p, ArrayList<Thing> things)
    {
        for (int i = 0; i < things.size(); i++)
            if (things.get(i).selected && !things.get(i).thing.hasPart(part))
            {
                things.get(i).thing.setPart(part, p);

                for(iPart iPrt : parts)
                    iPrt.addPart(things, things.get(i), part, p);

                things.get(i).reloadModel();
            }
    }

    public void addPart(Part part, ArrayList<Thing> things) throws Exception {

        for(iPart prt : parts)
            elementList.elements.remove(prt.partPanel);

        Class<?> sClass = part.getSerializable();
        Constructor<?> sConstructor = sClass.getDeclaredConstructor();
        Serializable newPart = (Serializable) sConstructor.newInstance();

        addPart(part, newPart, things);
    }
}
