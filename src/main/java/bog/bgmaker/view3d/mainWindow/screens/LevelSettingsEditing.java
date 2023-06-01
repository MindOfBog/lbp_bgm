package bog.bgmaker.view3d.mainWindow.screens;

import bog.bgmaker.view3d.core.Transformation3D;
import bog.bgmaker.view3d.mainWindow.View3D;
import bog.bgmaker.view3d.managers.MouseInput;
import bog.bgmaker.view3d.renderer.gui.GuiScreen;
import bog.bgmaker.view3d.renderer.gui.elements.Button;
import bog.bgmaker.view3d.renderer.gui.elements.*;
import bog.bgmaker.view3d.utils.CWLibUtils.LevelSettingsUtils;
import bog.bgmaker.view3d.utils.Config;
import bog.bgmaker.view3d.utils.Utils;
import common.FileChooser;
import cwlib.enums.Part;
import cwlib.resources.RLevel;
import cwlib.resources.RPlan;
import cwlib.structs.things.Thing;
import cwlib.structs.things.components.LevelSettings;
import cwlib.structs.things.parts.PLevelSettings;
import cwlib.types.Resource;
import org.joml.*;
import org.joml.Math;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Bog
 */
public class LevelSettingsEditing extends GuiScreen {

    View3D mainView;
    int sunFields = 210;
    Transformation3D.Tool sunTool;

    public LevelSettingsEditing(View3D mainView)
    {
        super(mainView.renderer, mainView.loader, mainView.window);
        this.mainView = mainView;
        init();
    }

    Element settingsHitbox;
    Element presetsHitbox;
    Textbox sunScale;
    Textbox sunX;
    Textbox sunY;
    Textbox sunZ;
    Textbox sunColor;
    Textbox sunMultiplier;
    Textbox ambientColor;
    Textbox exposure;
    Textbox fogColor;
    Textbox forNear;
    Textbox forFar;
    Textbox rimColor;
    Textbox rimColor2;
    Textbox bakedShadowAmount;
    Textbox bakedShadowBlur;
    Textbox bakedAOBias;
    Textbox bakedAOScale;
    Textbox dynamicAOAmount;
    Textbox dofNear;
    Textbox dofFar;
    Textbox zEffectAmount;
    Textbox zEffectBrightness;
    Textbox zEffectContrast;

    public void init()
    {
        sunTool = new Transformation3D.Tool(mainView.loader);

        settingsHitbox = new Element()
        {
            @Override
            public void draw(MouseInput mouseInput, boolean overElement) {
                this.pos.x = mainView.window.width - 305;
                this.size.y = mainView.window.height;
                super.draw(mouseInput, overElement);
            }
        };
        settingsHitbox.pos = new Vector2f(mainView.window.width - 305, 0);
        settingsHitbox.size = new Vector2f(305, mainView.window.height);
        presetsHitbox = new Element()
        {
            @Override
            public void draw(MouseInput mouseInput, boolean overElement) {
                this.pos.x = mainView.window.width - 320 - getStringWidth("Add Presets from .PLAN/.BIN", 10);
                this.size.y = 278;
                super.draw(mouseInput, overElement);
            }
        };
        presetsHitbox.pos = new Vector2f(mainView.window.width - 320 - getStringWidth("Add Presets from .PLAN/.BIN", 10), 0);
        presetsHitbox.size = new Vector2f(getStringWidth("Add Presets from .PLAN/.BIN", 10) + 12, 278);
        sunScale = new Textbox("sunScale", new Vector2f(mainView.window.width - sunFields + 5, 3 + (getFontHeight(10) + 3) * 1), new Vector2f(sunFields - 8, getFontHeight(10)), 10, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public void draw(MouseInput mouseInput, boolean overOther) {
                this.pos.x = mainView.window.width - sunFields + 5;
                super.draw(mouseInput, overOther);

                if(isFocused() && !mainView.levelSettings.isEmpty())
                    mainView.levelSettings.get(mainView.selectedPresetIndex).sunPositionScale = getText().isEmpty() ? 0f :  Float.parseFloat(getText());
            }
        }.noLetters().noOthers();
        sunX = new Textbox("sunX", new Vector2f(mainView.window.width - sunFields + 5, 3 + (getFontHeight(10) + 3) * 2), new Vector2f(sunFields - 8, getFontHeight(10)), 10, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public void draw(MouseInput mouseInput, boolean overOther) {
                this.pos.x = mainView.window.width - sunFields + 5;
                super.draw(mouseInput, overOther);
                if(isFocused() && !mainView.levelSettings.isEmpty())
                    mainView.levelSettings.get(mainView.selectedPresetIndex).sunPosition.x = getText().isEmpty() ? 0f :  Float.parseFloat(getText());
            }

            @Override
            public void secondThread() {
                super.secondThread();
                LevelSettings ls = null;
                try
                {
                    ls = mainView.levelSettings.get(mainView.selectedPresetIndex);
                }catch (Exception e){}

                if(ls != null)
                    setText(Float.toString(ls.sunPosition.x));
            }
        }.noLetters().noOthers();
        sunY = new Textbox("sunY", new Vector2f(mainView.window.width - sunFields + 5, 3 + (getFontHeight(10) + 3) * 3), new Vector2f(sunFields - 8, getFontHeight(10)), 10, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public void draw(MouseInput mouseInput, boolean overOther) {
                this.pos.x = mainView.window.width - sunFields + 5;
                super.draw(mouseInput, overOther);
                if(isFocused() && !mainView.levelSettings.isEmpty())
                    mainView.levelSettings.get(mainView.selectedPresetIndex).sunPosition.y = getText().isEmpty() ? 0f :  Float.parseFloat(getText());
            }

            @Override
            public void secondThread() {
                super.secondThread();
                LevelSettings ls = null;
                try
                {
                    ls = mainView.levelSettings.get(mainView.selectedPresetIndex);
                }catch (Exception e){}

                if(ls != null)
                    setText(Float.toString(ls.sunPosition.y));
            }
        }.noLetters().noOthers();
        sunZ = new Textbox("sunZ", new Vector2f(mainView.window.width - sunFields + 5, 3 + (getFontHeight(10) + 3) * 4), new Vector2f(sunFields - 8, getFontHeight(10)), 10, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public void draw(MouseInput mouseInput, boolean overOther) {
                this.pos.x = mainView.window.width - sunFields + 5;
                super.draw(mouseInput, overOther);
                if(isFocused() && !mainView.levelSettings.isEmpty())
                    mainView.levelSettings.get(mainView.selectedPresetIndex).sunPosition.z = getText().isEmpty() ? 0f :  Float.parseFloat(getText());
            }

            @Override
            public void secondThread() {
                super.secondThread();
                LevelSettings ls = null;
                try
                {
                    ls = mainView.levelSettings.get(mainView.selectedPresetIndex);
                }catch (Exception e){}

                if(ls != null)
                    setText(Float.toString(ls.sunPosition.z));
            }
        }.noLetters().noOthers();
        int textboxWidth = 105;
        sunColor = new Textbox("sunColor", new Vector2f(mainView.window.width - textboxWidth, 3 + (getFontHeight(10) + 3) * 5), new Vector2f(textboxWidth - 3, getFontHeight(10)), 10, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public Color textColor() {
                return Utils.parseHexColor(this.getText());
            }

            @Override
            public void draw(MouseInput mouseInput, boolean overOther) {
                this.pos.x = mainView.window.width - textboxWidth;
                super.draw(mouseInput, overOther);
                if(isFocused() && !mainView.levelSettings.isEmpty())
                    mainView.levelSettings.get(mainView.selectedPresetIndex).sunColor = Utils.parseHexColorVec(getText());
            }
        }.noOthers();
        sunMultiplier = new Textbox("sunMultiplier", new Vector2f(mainView.window.width - textboxWidth, 3 + (getFontHeight(10) + 3) * 6), new Vector2f(textboxWidth - 3, getFontHeight(10)), 10, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public void draw(MouseInput mouseInput, boolean overOther) {
                this.pos.x = mainView.window.width - textboxWidth;
                super.draw(mouseInput, overOther);
                if(isFocused() && !mainView.levelSettings.isEmpty())
                    mainView.levelSettings.get(mainView.selectedPresetIndex).sunMultiplier = getText().isEmpty() ? 0f :  Float.parseFloat(getText());
            }
        }.noLetters().noOthers();
        ambientColor = new Textbox("ambientColor", new Vector2f(mainView.window.width - textboxWidth, 3 + (getFontHeight(10) + 3) * 7), new Vector2f(textboxWidth - 3, getFontHeight(10)), 10, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public Color textColor() {
                return Utils.parseHexColor(this.getText());
            }

            @Override
            public void draw(MouseInput mouseInput, boolean overOther) {
                this.pos.x = mainView.window.width - textboxWidth;
                super.draw(mouseInput, overOther);
                if(isFocused() && !mainView.levelSettings.isEmpty())
                    mainView.levelSettings.get(mainView.selectedPresetIndex).ambientColor = Utils.parseHexColorVec(getText());
            }
        }.noOthers();
        exposure = new Textbox("exposure", new Vector2f(mainView.window.width - textboxWidth, 3 + (getFontHeight(10) + 3) * 8), new Vector2f(textboxWidth - 3, getFontHeight(10)), 10, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public void draw(MouseInput mouseInput, boolean overOther) {
                this.pos.x = mainView.window.width - textboxWidth;
                super.draw(mouseInput, overOther);
                if(isFocused() && !mainView.levelSettings.isEmpty())
                    mainView.levelSettings.get(mainView.selectedPresetIndex).exposure = getText().isEmpty() ? 0f :  Float.parseFloat(getText());
            }
        }.noLetters().noOthers();
        fogColor = new Textbox("fogColor", new Vector2f(mainView.window.width - textboxWidth, 3 + (getFontHeight(10) + 3) * 9), new Vector2f(textboxWidth - 3, getFontHeight(10)), 10, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public Color textColor() {
                return Utils.parseHexColor(this.getText());
            }

            @Override
            public void draw(MouseInput mouseInput, boolean overOther) {
                this.pos.x = mainView.window.width - textboxWidth;
                super.draw(mouseInput, overOther);
                if(isFocused() && !mainView.levelSettings.isEmpty())
                    mainView.levelSettings.get(mainView.selectedPresetIndex).fogColor = Utils.parseHexColorVec(getText());
            }
        }.noOthers();
        forNear = new Textbox("forNear", new Vector2f(mainView.window.width - textboxWidth, 3 + (getFontHeight(10) + 3) * 10), new Vector2f(textboxWidth - 3, getFontHeight(10)), 10, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public void draw(MouseInput mouseInput, boolean overOther) {
                this.pos.x = mainView.window.width - textboxWidth;
                super.draw(mouseInput, overOther);
                if(isFocused() && !mainView.levelSettings.isEmpty())
                    mainView.levelSettings.get(mainView.selectedPresetIndex).fogNear = getText().isEmpty() ? 0f :  Float.parseFloat(getText());
            }
        }.noLetters().noOthers();
        forFar = new Textbox("forFar", new Vector2f(mainView.window.width - textboxWidth, 3 + (getFontHeight(10) + 3) * 11), new Vector2f(textboxWidth - 3, getFontHeight(10)), 10, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public void draw(MouseInput mouseInput, boolean overOther) {
                this.pos.x = mainView.window.width - textboxWidth;
                super.draw(mouseInput, overOther);
                if(isFocused() && !mainView.levelSettings.isEmpty())
                    mainView.levelSettings.get(mainView.selectedPresetIndex).fogFar = getText().isEmpty() ? 0f :  Float.parseFloat(getText());
            }
        }.noLetters().noOthers();
        rimColor = new Textbox("rimColor", new Vector2f(mainView.window.width - textboxWidth, 3 + (getFontHeight(10) + 3) * 12), new Vector2f(textboxWidth - 3, getFontHeight(10)), 10, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public Color textColor() {
                return Utils.parseHexColor(this.getText());
            }

            @Override
            public void draw(MouseInput mouseInput, boolean overOther) {
                this.pos.x = mainView.window.width - textboxWidth;
                super.draw(mouseInput, overOther);
                if(isFocused() && !mainView.levelSettings.isEmpty())
                    mainView.levelSettings.get(mainView.selectedPresetIndex).rimColor = Utils.parseHexColorVec(getText());
            }
        }.noOthers();
        rimColor2 = new Textbox("rimColor2", new Vector2f(mainView.window.width - textboxWidth, 3 + (getFontHeight(10) + 3) * 13), new Vector2f(textboxWidth - 3, getFontHeight(10)), 10, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public Color textColor() {
                return Utils.parseHexColor(this.getText());
            }

            @Override
            public void draw(MouseInput mouseInput, boolean overOther) {
                this.pos.x = mainView.window.width - textboxWidth;
                super.draw(mouseInput, overOther);
                if(isFocused() && !mainView.levelSettings.isEmpty())
                    mainView.levelSettings.get(mainView.selectedPresetIndex).rimColor2 = Utils.parseHexColorVec(getText());
            }
        }.noOthers();
        bakedShadowAmount = new Textbox("bakedShadowAmount", new Vector2f(mainView.window.width - textboxWidth, 3 + (getFontHeight(10) + 3) * 14), new Vector2f(textboxWidth - 3, getFontHeight(10)), 10, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public void draw(MouseInput mouseInput, boolean overOther) {
                this.pos.x = mainView.window.width - textboxWidth;
                super.draw(mouseInput, overOther);
                if(isFocused() && !mainView.levelSettings.isEmpty())
                    mainView.levelSettings.get(mainView.selectedPresetIndex).bakedShadowAmount = getText().isEmpty() ? 0f :  Float.parseFloat(getText());
            }
        }.noLetters().noOthers();
        bakedShadowBlur = new Textbox("bakedShadowBlur", new Vector2f(mainView.window.width - textboxWidth, 3 + (getFontHeight(10) + 3) * 15), new Vector2f(textboxWidth - 3, getFontHeight(10)), 10, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public void draw(MouseInput mouseInput, boolean overOther) {
                this.pos.x = mainView.window.width - textboxWidth;
                super.draw(mouseInput, overOther);
                if(isFocused() && !mainView.levelSettings.isEmpty())
                    mainView.levelSettings.get(mainView.selectedPresetIndex).bakedShadowBlur = getText().isEmpty() ? 0f :  Float.parseFloat(getText());
            }
        }.noLetters().noOthers();
        bakedAOBias = new Textbox("bakedAOBias", new Vector2f(mainView.window.width - textboxWidth, 3 + (getFontHeight(10) + 3) * 16), new Vector2f(textboxWidth - 3, getFontHeight(10)), 10, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public void draw(MouseInput mouseInput, boolean overOther) {
                this.pos.x = mainView.window.width - textboxWidth;
                super.draw(mouseInput, overOther);
                if(isFocused() && !mainView.levelSettings.isEmpty())
                    mainView.levelSettings.get(mainView.selectedPresetIndex).bakedAOBias = getText().isEmpty() ? 0f : Float.parseFloat(getText());
            }
        }.noLetters().noOthers();
        bakedAOScale = new Textbox("bakedAOScale", new Vector2f(mainView.window.width - textboxWidth, 3 + (getFontHeight(10) + 3) * 17), new Vector2f(textboxWidth - 3, getFontHeight(10)), 10, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public void draw(MouseInput mouseInput, boolean overOther) {
                this.pos.x = mainView.window.width - textboxWidth;
                super.draw(mouseInput, overOther);
                if(isFocused() && !mainView.levelSettings.isEmpty())
                    mainView.levelSettings.get(mainView.selectedPresetIndex).bakedAOScale = getText().isEmpty() ? 0f : Float.parseFloat(getText());
            }
        }.noLetters().noOthers();
        dynamicAOAmount = new Textbox("dynamicAOAmount", new Vector2f(mainView.window.width - textboxWidth, 3 + (getFontHeight(10) + 3) * 18), new Vector2f(textboxWidth - 3, getFontHeight(10)), 10, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public void draw(MouseInput mouseInput, boolean overOther) {
                this.pos.x = mainView.window.width - textboxWidth;
                super.draw(mouseInput, overOther);
                if(isFocused() && !mainView.levelSettings.isEmpty())
                    mainView.levelSettings.get(mainView.selectedPresetIndex).dynamicAOAmount = getText().isEmpty() ? 0f : Float.parseFloat(getText());
            }
        }.noLetters().noOthers();
        dofNear = new Textbox("dofNear", new Vector2f(mainView.window.width - textboxWidth, 3 + (getFontHeight(10) + 3) * 19), new Vector2f(textboxWidth - 3, getFontHeight(10)), 10, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public void draw(MouseInput mouseInput, boolean overOther) {
                this.pos.x = mainView.window.width - textboxWidth;
                super.draw(mouseInput, overOther);
                if(isFocused() && !mainView.levelSettings.isEmpty())
                    mainView.levelSettings.get(mainView.selectedPresetIndex).dofNear = getText().isEmpty() ? 0f : Float.parseFloat(getText());
            }
        }.noLetters().noOthers();
        dofFar = new Textbox("dofFar", new Vector2f(mainView.window.width - textboxWidth, 3 + (getFontHeight(10) + 3) * 20), new Vector2f(textboxWidth - 3, getFontHeight(10)), 10, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public void draw(MouseInput mouseInput, boolean overOther) {
                this.pos.x = mainView.window.width - textboxWidth;
                super.draw(mouseInput, overOther);
                if(isFocused() && !mainView.levelSettings.isEmpty())
                    mainView.levelSettings.get(mainView.selectedPresetIndex).dofFar = getText().isEmpty() ? 0f : Float.parseFloat(getText());
            }
        }.noLetters().noOthers();
        zEffectAmount = new Textbox("zEffectAmount", new Vector2f(mainView.window.width - textboxWidth, 3 + (getFontHeight(10) + 3) * 21), new Vector2f(textboxWidth - 3, getFontHeight(10)), 10, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public void draw(MouseInput mouseInput, boolean overOther) {
                this.pos.x = mainView.window.width - textboxWidth;
                super.draw(mouseInput, overOther);
                if(isFocused() && !mainView.levelSettings.isEmpty())
                    mainView.levelSettings.get(mainView.selectedPresetIndex).zEffectAmount = getText().isEmpty() ? 0f : Float.parseFloat(getText());
            }
        }.noLetters().noOthers();
        zEffectBrightness = new Textbox("zEffectBrightness", new Vector2f(mainView.window.width - textboxWidth, 3 + (getFontHeight(10) + 3) * 22), new Vector2f(textboxWidth - 3, getFontHeight(10)), 10, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public void draw(MouseInput mouseInput, boolean overOther) {
                this.pos.x = mainView.window.width - textboxWidth;
                super.draw(mouseInput, overOther);
                if(isFocused() && !mainView.levelSettings.isEmpty())
                    mainView.levelSettings.get(mainView.selectedPresetIndex).zEffectBright = getText().isEmpty() ? 0f : Float.parseFloat(getText());
            }
        }.noLetters().noOthers();
        zEffectContrast = new Textbox("zEffectContrast", new Vector2f(mainView.window.width - textboxWidth, 3 + (getFontHeight(10) + 3) * 23), new Vector2f(textboxWidth - 3, getFontHeight(10)), 10, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public void draw(MouseInput mouseInput, boolean overOther) {
                this.pos.x = mainView.window.width - textboxWidth;
                super.draw(mouseInput, overOther);
                if(isFocused() && !mainView.levelSettings.isEmpty())
                    mainView.levelSettings.get(mainView.selectedPresetIndex).zEffectContrast = getText().isEmpty() ? 0f : Float.parseFloat(getText());
            }
        }.noLetters().noOthers();
        this.guiElements.add(settingsHitbox);
        this.guiElements.add(presetsHitbox);
        this.guiElements.add(sunScale);
        this.guiElements.add(sunX);
        this.guiElements.add(sunY);
        this.guiElements.add(sunZ);
        this.guiElements.add(sunColor);
        this.guiElements.add(sunMultiplier);
        this.guiElements.add(ambientColor);
        this.guiElements.add(exposure);
        this.guiElements.add(fogColor);
        this.guiElements.add(forNear);
        this.guiElements.add(forFar);
        this.guiElements.add(rimColor);
        this.guiElements.add(rimColor2);
        this.guiElements.add(bakedShadowAmount);
        this.guiElements.add(bakedShadowBlur);
        this.guiElements.add(bakedAOBias);
        this.guiElements.add(bakedAOScale);
        this.guiElements.add(dynamicAOAmount);
        this.guiElements.add(dofNear);
        this.guiElements.add(dofFar);
        this.guiElements.add(zEffectAmount);
        this.guiElements.add(zEffectBrightness);
        this.guiElements.add(zEffectContrast);

        {
            LevelSettings ls = mainView.levelSettings.get(mainView.selectedPresetIndex);

            sunScale.setText(Float.toString(ls.sunPositionScale));
            sunX.setText(Float.toString(ls.sunPosition.x));
            sunY.setText(Float.toString(ls.sunPosition.y));
            sunZ.setText(Float.toString(ls.sunPosition.z));

            Vector4f suncol = new Vector4f(ls.sunColor);
            Color suncol1 = new Color(Math.clamp(0, 1, suncol.x),
                    Math.clamp(0, 1, suncol.y),
                    Math.clamp(0, 1, suncol.z),
                    Math.clamp(0, 1, suncol.w));

            sunColor.setText(Utils.toHexColor(suncol1));
            sunMultiplier.setText(Float.toString(ls.sunMultiplier));

            Vector4f ambientcol = new Vector4f(ls.ambientColor);
            Color ambientcol1 = new Color(Math.clamp(0, 1, ambientcol.x),
                    Math.clamp(0, 1, ambientcol.y),
                    Math.clamp(0, 1, ambientcol.z),
                    Math.clamp(0, 1, ambientcol.w));

            ambientColor.setText(Utils.toHexColor(ambientcol1));
            exposure.setText(Float.toString(ls.exposure));

            Vector4f fogcol = new Vector4f(ls.fogColor);
            Color fogcol1 = new Color(Math.clamp(0, 1, fogcol.x),
                    Math.clamp(0, 1, fogcol.y),
                    Math.clamp(0, 1, fogcol.z),
                    Math.clamp(0, 1, fogcol.w));

            fogColor.setText(Utils.toHexColor(fogcol1));
            forNear.setText(Float.toString(ls.fogNear));
            forFar.setText(Float.toString(ls.fogFar));

            Vector4f rimcol = new Vector4f(ls.rimColor);
            Color rimcol1 = new Color(Math.clamp(0, 1, rimcol.x),
                    Math.clamp(0, 1, rimcol.y),
                    Math.clamp(0, 1, rimcol.z),
                    Math.clamp(0, 1, rimcol.w));

            rimColor.setText(Utils.toHexColor(rimcol1));

            Vector4f rimcol2 = new Vector4f(ls.rimColor2);
            Color rimcol21 = new Color(Math.clamp(0, 1, rimcol2.x),
                    Math.clamp(0, 1, rimcol2.y),
                    Math.clamp(0, 1, rimcol2.z),
                    Math.clamp(0, 1, rimcol2.w));

            rimColor2.setText(Utils.toHexColor(rimcol21));
            bakedShadowAmount.setText(Float.toString(ls.bakedShadowAmount));
            bakedShadowBlur.setText(Float.toString(ls.bakedShadowBlur));
            bakedAOBias.setText(Float.toString(ls.bakedAOBias));
            bakedAOScale.setText(Float.toString(ls.bakedAOScale));
            dynamicAOAmount.setText(Float.toString(ls.dynamicAOAmount));
            dofNear.setText(Float.toString(ls.dofNear));
            dofFar.setText(Float.toString(ls.dofFar));
            zEffectAmount.setText(Float.toString(ls.zEffectAmount));
            zEffectBrightness.setText(Float.toString(ls.zEffectBright));
            zEffectContrast.setText(Float.toString(ls.zEffectContrast));
        }

        bog.bgmaker.view3d.renderer.gui.elements.Button addFromPlanBin = new bog.bgmaker.view3d.renderer.gui.elements.Button("addFromPlanBin", "Add Presets from .PLAN/.BIN", new Vector2f(mainView.window.width - 317 - getStringWidth("Add Presets from .PLAN/.BIN", 10), 3), new Vector2f(getStringWidth("Add Presets from .PLAN/.BIN", 10) + 6, getFontHeight(10) + 4), 10, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public void clickedButton(int button, int action, int mods) {

                File file = null;
                try {
                    if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)
                        file = FileChooser.openFile(null, "plan,bin", false, false)[0];
                }catch (Exception ex){}

                if (file == null || !file.exists()) return;

                String ext = file.getAbsolutePath().toString();
                ext = ext.substring(ext.lastIndexOf(".") + 1);

                switch (ext) {
                    case "plan":
                    case "pln":
                        try {
                            RPlan plan = new Resource(file.getAbsolutePath()).loadResource(RPlan.class);
                            if (plan == null) return;

                            Thing[] things = plan.getThings();

                            for (Thing thing : things) {
                                if (thing == null) continue;

                                PLevelSettings ls = thing.getPart(Part.LEVEL_SETTINGS);
                                if (ls == null) continue;

                                for (LevelSettings setting : ls.presets)
                                    mainView.levelSettings.add(setting);

                                if (ls.presets.isEmpty())
                                    mainView.levelSettings.add(LevelSettingsUtils.translate(ls));
                            }

                        } catch (Exception ex) {ex.printStackTrace();}
                        break;
                    case "bin":
                        try {
                            RLevel level = new Resource(file.getAbsolutePath()).loadResource(RLevel.class);
                            if (level == null) return;

                            PLevelSettings ls = level.world.getPart(Part.LEVEL_SETTINGS);
                            mainView.levelSettings.add(LevelSettingsUtils.translate(ls));
                        } catch (Exception ex) {ex.printStackTrace();}
                        break;
                    default:
                        System.err.println("Unknown background file type.");
                        break;
                }
            }
        };
        ButtonList presetList = new ButtonList("presetList", mainView.levelSettings, new Vector2f(mainView.window.width - 317 - getStringWidth("Add Presets from .PLAN/.BIN", 10), 10 + getFontHeight(10)), new Vector2f(getStringWidth("Add Presets from .PLAN/.BIN", 10) + 6, 150), 10, mainView.renderer, mainView.loader, mainView.window) {
            @Override
            public void clickedButton(Object object, int index, int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)
                {
                    mainView.selectedPresetIndex = index;
                    LevelSettings ls = (LevelSettings)object;

                    sunScale.setText(Float.toString(ls.sunPositionScale));
                    sunX.setText(Float.toString(ls.sunPosition.x));
                    sunY.setText(Float.toString(ls.sunPosition.y));
                    sunZ.setText(Float.toString(ls.sunPosition.z));

                    Vector4f suncol = new Vector4f(ls.sunColor);
                    Color suncol1 = new Color(Math.clamp(0, 1, suncol.x),
                            Math.clamp(0, 1, suncol.y),
                            Math.clamp(0, 1, suncol.z),
                            Math.clamp(0, 1, suncol.w));

                    sunColor.setText(Utils.toHexColor(suncol1));
                    sunMultiplier.setText(Float.toString(ls.sunMultiplier));

                    Vector4f ambientcol = new Vector4f(ls.ambientColor);
                    Color ambientcol1 = new Color(Math.clamp(0, 1, ambientcol.x),
                            Math.clamp(0, 1, ambientcol.y),
                            Math.clamp(0, 1, ambientcol.z),
                            Math.clamp(0, 1, ambientcol.w));

                    ambientColor.setText(Utils.toHexColor(ambientcol1));
                    exposure.setText(Float.toString(ls.exposure));

                    Vector4f fogcol = new Vector4f(ls.fogColor);
                    Color fogcol1 = new Color(Math.clamp(0, 1, fogcol.x),
                            Math.clamp(0, 1, fogcol.y),
                            Math.clamp(0, 1, fogcol.z),
                            Math.clamp(0, 1, fogcol.w));

                    fogColor.setText(Utils.toHexColor(fogcol1));
                    forNear.setText(Float.toString(ls.fogNear));
                    forFar.setText(Float.toString(ls.fogFar));

                    Vector4f rimcol = new Vector4f(ls.rimColor);
                    Color rimcol1 = new Color(Math.clamp(0, 1, rimcol.x),
                            Math.clamp(0, 1, rimcol.y),
                            Math.clamp(0, 1, rimcol.z),
                            Math.clamp(0, 1, rimcol.w));

                    rimColor.setText(Utils.toHexColor(rimcol1));

                    Vector4f rimcol2 = new Vector4f(ls.rimColor2);
                    Color rimcol21 = new Color(Math.clamp(0, 1, rimcol2.x),
                            Math.clamp(0, 1, rimcol2.y),
                            Math.clamp(0, 1, rimcol2.z),
                            Math.clamp(0, 1, rimcol2.w));

                    rimColor2.setText(Utils.toHexColor(rimcol21));
                    bakedShadowAmount.setText(Float.toString(ls.bakedShadowAmount));
                    bakedShadowBlur.setText(Float.toString(ls.bakedShadowBlur));
                    bakedAOBias.setText(Float.toString(ls.bakedAOBias));
                    bakedAOScale.setText(Float.toString(ls.bakedAOScale));
                    dynamicAOAmount.setText(Float.toString(ls.dynamicAOAmount));
                    dofNear.setText(Float.toString(ls.dofNear));
                    dofFar.setText(Float.toString(ls.dofFar));
                    zEffectAmount.setText(Float.toString(ls.zEffectAmount));
                    zEffectBrightness.setText(Float.toString(ls.zEffectBright));
                    zEffectContrast.setText(Float.toString(ls.zEffectContrast));
                }
            }

            int hovering = -1;
            @Override
            public void hoveringButton(Object object, int index) {
                hovering = index;
            }

            @Override
            public boolean isHighlighted(Object object, int index) {
                return hovering == index;
            }

            @Override
            public boolean isSelected(Object object, int index) {
                return mainView.selectedPresetIndex == index;
            }

            @Override
            public String buttonText(Object object, int index) {
                if(object == null)
                    return "#" + Utils.toHexColor(Color.black);
                Vector4f fogcolor = ((LevelSettings)object).fogColor;
                Color fogcolor1 = new Color(Math.clamp(0, 1, fogcolor.x),
                        Math.clamp(0, 1, fogcolor.y),
                        Math.clamp(0, 1, fogcolor.z),
                        Math.clamp(0, 1, fogcolor.w));
                return "#" + Utils.toHexColor(fogcolor1);
            }

            @Override
            public Color buttonColor(Object object, int index) {
                if(object == null)
                    return super.buttonColor(object, index);
                Vector4f fogcolor = ((LevelSettings)object).fogColor;
                Color fogcolor1 = new Color(Math.clamp(0, 1, fogcolor.x),
                        Math.clamp(0, 1, fogcolor.y),
                        Math.clamp(0, 1, fogcolor.z),
                        0.5f);
                return fogcolor1;
            }

            @Override
            public Color buttonColorHighlighted(Object object, int index) {
                if(object == null)
                    return Color.black;
                Vector4f fogcolor = ((LevelSettings)object).fogColor;
                Color fogcolor1 = new Color(Math.clamp(0.1f, 1, fogcolor.x),
                        Math.clamp(0.1f, 1, fogcolor.y),
                        Math.clamp(0.1f, 1, fogcolor.z),
                        0.45f);
                return fogcolor1.brighter();
            }

            @Override
            public Color buttonColorSelected(Object object, int index) {
                if(object == null)
                    return Color.black;
                Vector4f fogcolor = ((LevelSettings)object).fogColor;
                Color fogcolor1 = new Color(Math.clamp(0.1f, 1, fogcolor.x),
                        Math.clamp(0.1f, 1, fogcolor.y),
                        Math.clamp(0.1f, 1, fogcolor.z),
                        0.75f);
                return fogcolor1.brighter();
            }

            @Override
            public Color buttonColor2(Object object, int index) {
                if(object == null)
                    return super.buttonColor(object, index);
                Vector4f fogcolor = ((LevelSettings)object).fogColor;
                Color fogcolor1 = new Color(Math.clamp(0, 1, fogcolor.x),
                        Math.clamp(0, 1, fogcolor.y),
                        Math.clamp(0, 1, fogcolor.z),
                        0.5f);
                return fogcolor1.darker().darker();
            }

            @Override
            public Color buttonColorHighlighted2(Object object, int index) {
                if(object == null)
                    return Color.black;
                Vector4f fogcolor = ((LevelSettings)object).fogColor;
                Color fogcolor1 = new Color(Math.clamp(0.1f, 1, fogcolor.x),
                        Math.clamp(0.1f, 1, fogcolor.y),
                        Math.clamp(0.1f, 1, fogcolor.z),
                        0.45f);
                return fogcolor1.darker();
            }

            @Override
            public Color buttonColorSelected2(Object object, int index) {
                if(object == null)
                    return Color.black;
                Vector4f fogcolor = ((LevelSettings)object).fogColor;
                Color fogcolor1 = new Color(Math.clamp(0.1f, 1, fogcolor.x),
                        Math.clamp(0.1f, 1, fogcolor.y),
                        Math.clamp(0.1f, 1, fogcolor.z),
                        0.75f);
                return fogcolor1;
            }

            @Override
            public boolean searchFilter(Object object, int index) {
                return true;
            }

            @Override
            public void draw(MouseInput mouseInput, boolean overElement) {
                hovering = -1;
                super.draw(mouseInput, overElement);
            }
        };
        Button addNew = new bog.bgmaker.view3d.renderer.gui.elements.Button("addNew", "Add", new Vector2f(mainView.window.width - 317 - getStringWidth("Add Presets from .PLAN/.BIN", 10), getFontHeight(10) + 163), new Vector2f(getStringWidth("Add Presets from .PLAN/.BIN", 10)/2 + 1, getFontHeight(10) + 4), 10, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)
                {
                    if(mainView.levelSettings.isEmpty())
                        mainView.levelSettings.add(LevelSettingsUtils.getBlank1Preset().get(1));
                    else
                        mainView.levelSettings.add(mainView.selectedPresetIndex + 1, LevelSettingsUtils.getBlank1Preset().get(1));

                    if(mainView.selectedPresetIndex < mainView.levelSettings.size() - 1)
                        mainView.selectedPresetIndex++;

                    LevelSettings ls = mainView.levelSettings.get(mainView.selectedPresetIndex);

                    sunScale.setText(Float.toString(ls.sunPositionScale));
                    sunX.setText(Float.toString(ls.sunPosition.x));
                    sunY.setText(Float.toString(ls.sunPosition.y));
                    sunZ.setText(Float.toString(ls.sunPosition.z));

                    Vector4f suncol = new Vector4f(ls.sunColor);
                    Color suncol1 = new Color(Math.clamp(0, 1, suncol.x),
                            Math.clamp(0, 1, suncol.y),
                            Math.clamp(0, 1, suncol.z),
                            Math.clamp(0, 1, suncol.w));

                    sunColor.setText(Utils.toHexColor(suncol1));
                    sunMultiplier.setText(Float.toString(ls.sunMultiplier));

                    Vector4f ambientcol = new Vector4f(ls.ambientColor);
                    Color ambientcol1 = new Color(Math.clamp(0, 1, ambientcol.x),
                            Math.clamp(0, 1, ambientcol.y),
                            Math.clamp(0, 1, ambientcol.z),
                            Math.clamp(0, 1, ambientcol.w));

                    ambientColor.setText(Utils.toHexColor(ambientcol1));
                    exposure.setText(Float.toString(ls.exposure));

                    Vector4f fogcol = new Vector4f(ls.fogColor);
                    Color fogcol1 = new Color(Math.clamp(0, 1, fogcol.x),
                            Math.clamp(0, 1, fogcol.y),
                            Math.clamp(0, 1, fogcol.z),
                            Math.clamp(0, 1, fogcol.w));

                    fogColor.setText(Utils.toHexColor(fogcol1));
                    forNear.setText(Float.toString(ls.fogNear));
                    forFar.setText(Float.toString(ls.fogFar));

                    Vector4f rimcol = new Vector4f(ls.rimColor);
                    Color rimcol1 = new Color(Math.clamp(0, 1, rimcol.x),
                            Math.clamp(0, 1, rimcol.y),
                            Math.clamp(0, 1, rimcol.z),
                            Math.clamp(0, 1, rimcol.w));

                    rimColor.setText(Utils.toHexColor(rimcol1));

                    Vector4f rimcol2 = new Vector4f(ls.rimColor2);
                    Color rimcol21 = new Color(Math.clamp(0, 1, rimcol2.x),
                            Math.clamp(0, 1, rimcol2.y),
                            Math.clamp(0, 1, rimcol2.z),
                            Math.clamp(0, 1, rimcol2.w));

                    rimColor2.setText(Utils.toHexColor(rimcol21));
                    bakedShadowAmount.setText(Float.toString(ls.bakedShadowAmount));
                    bakedShadowBlur.setText(Float.toString(ls.bakedShadowBlur));
                    bakedAOBias.setText(Float.toString(ls.bakedAOBias));
                    bakedAOScale.setText(Float.toString(ls.bakedAOScale));
                    dynamicAOAmount.setText(Float.toString(ls.dynamicAOAmount));
                    dofNear.setText(Float.toString(ls.dofNear));
                    dofFar.setText(Float.toString(ls.dofFar));
                    zEffectAmount.setText(Float.toString(ls.zEffectAmount));
                    zEffectBrightness.setText(Float.toString(ls.zEffectBright));
                    zEffectContrast.setText(Float.toString(ls.zEffectContrast));
                }
            }
        };
        Button moveUp = new bog.bgmaker.view3d.renderer.gui.elements.Button("moveUp", "Move Up", new Vector2f(mainView.window.width - 313 - getStringWidth("Add Presets from .PLAN/.BIN", 10) / 2, getFontHeight(10) + 163), new Vector2f(getStringWidth("Add Presets from .PLAN/.BIN", 10)/2 + 2, getFontHeight(10) + 4), 10, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && mainView.selectedPresetIndex > 0)
                {
                    Collections.swap(mainView.levelSettings, mainView.selectedPresetIndex - 1, mainView.selectedPresetIndex);
                    mainView.selectedPresetIndex--;
                }
            }
        };
        Button delete = new bog.bgmaker.view3d.renderer.gui.elements.Button("delete", "Delete", new Vector2f(mainView.window.width - 317 - getStringWidth("Add Presets from .PLAN/.BIN", 10), getFontHeight(10) * 2 + 170), new Vector2f(getStringWidth("Add Presets from .PLAN/.BIN", 10)/2 + 1, getFontHeight(10) + 4), 10, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)
                {
                    mainView.levelSettings.remove(mainView.selectedPresetIndex);

                    if(mainView.selectedPresetIndex > 0 && mainView.selectedPresetIndex == mainView.levelSettings.size())
                        mainView.selectedPresetIndex--;

                    if(mainView.levelSettings.isEmpty())
                    {
                        sunScale.setText("");
                        sunX.setText("");
                        sunY.setText("");
                        sunZ.setText("");
                        sunColor.setText("");
                        sunMultiplier.setText("");
                        ambientColor.setText("");
                        exposure.setText("");
                        fogColor.setText("");
                        forNear.setText("");
                        forFar.setText("");
                        rimColor.setText("");
                        rimColor2.setText("");
                        bakedShadowAmount.setText("");
                        bakedShadowBlur.setText("");
                        bakedAOBias.setText("");
                        bakedAOScale.setText("");
                        dynamicAOAmount.setText("");
                        dofNear.setText("");
                        dofFar.setText("");
                        zEffectAmount.setText("");
                        zEffectBrightness.setText("");
                        zEffectContrast.setText("");
                    }
                    else
                    {

                        LevelSettings ls = mainView.levelSettings.get(mainView.selectedPresetIndex);

                        sunScale.setText(Float.toString(ls.sunPositionScale));
                        sunX.setText(Float.toString(ls.sunPosition.x));
                        sunY.setText(Float.toString(ls.sunPosition.y));
                        sunZ.setText(Float.toString(ls.sunPosition.z));

                        Vector4f suncol = new Vector4f(ls.sunColor);
                        Color suncol1 = new Color(Math.clamp(0, 1, suncol.x),
                                Math.clamp(0, 1, suncol.y),
                                Math.clamp(0, 1, suncol.z),
                                Math.clamp(0, 1, suncol.w));

                        sunColor.setText(Utils.toHexColor(suncol1));
                        sunMultiplier.setText(Float.toString(ls.sunMultiplier));

                        Vector4f ambientcol = new Vector4f(ls.ambientColor);
                        Color ambientcol1 = new Color(Math.clamp(0, 1, ambientcol.x),
                                Math.clamp(0, 1, ambientcol.y),
                                Math.clamp(0, 1, ambientcol.z),
                                Math.clamp(0, 1, ambientcol.w));

                        ambientColor.setText(Utils.toHexColor(ambientcol1));
                        exposure.setText(Float.toString(ls.exposure));

                        Vector4f fogcol = new Vector4f(ls.fogColor);
                        Color fogcol1 = new Color(Math.clamp(0, 1, fogcol.x),
                                Math.clamp(0, 1, fogcol.y),
                                Math.clamp(0, 1, fogcol.z),
                                Math.clamp(0, 1, fogcol.w));

                        fogColor.setText(Utils.toHexColor(fogcol1));
                        forNear.setText(Float.toString(ls.fogNear));
                        forFar.setText(Float.toString(ls.fogFar));

                        Vector4f rimcol = new Vector4f(ls.rimColor);
                        Color rimcol1 = new Color(Math.clamp(0, 1, rimcol.x),
                                Math.clamp(0, 1, rimcol.y),
                                Math.clamp(0, 1, rimcol.z),
                                Math.clamp(0, 1, rimcol.w));

                        rimColor.setText(Utils.toHexColor(rimcol1));

                        Vector4f rimcol2 = new Vector4f(ls.rimColor2);
                        Color rimcol21 = new Color(Math.clamp(0, 1, rimcol2.x),
                                Math.clamp(0, 1, rimcol2.y),
                                Math.clamp(0, 1, rimcol2.z),
                                Math.clamp(0, 1, rimcol2.w));

                        rimColor2.setText(Utils.toHexColor(rimcol21));
                        bakedShadowAmount.setText(Float.toString(ls.bakedShadowAmount));
                        bakedShadowBlur.setText(Float.toString(ls.bakedShadowBlur));
                        bakedAOBias.setText(Float.toString(ls.bakedAOBias));
                        bakedAOScale.setText(Float.toString(ls.bakedAOScale));
                        dynamicAOAmount.setText(Float.toString(ls.dynamicAOAmount));
                        dofNear.setText(Float.toString(ls.dofNear));
                        dofFar.setText(Float.toString(ls.dofFar));
                        zEffectAmount.setText(Float.toString(ls.zEffectAmount));
                        zEffectBrightness.setText(Float.toString(ls.zEffectBright));
                        zEffectContrast.setText(Float.toString(ls.zEffectContrast));
                    }
                }
            }
        };
        Button moveDown = new Button("moveDown", "Move Down", new Vector2f(mainView.window.width - 313 - getStringWidth("Add Presets from .PLAN/.BIN", 10) / 2, getFontHeight(10) * 2 + 170), new Vector2f(getStringWidth("Add Presets from .PLAN/.BIN", 10)/2 + 2, getFontHeight(10) + 4), 10, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public void clickedButton(int button, int action, int mods){
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS && mainView.selectedPresetIndex < mainView.levelSettings.size() - 1)
                {
                    Collections.swap(mainView.levelSettings, mainView.selectedPresetIndex, mainView.selectedPresetIndex + 1);
                    mainView.selectedPresetIndex++;
                }
            }
        };

        DropDownTab presetEditor = new DropDownTab("presetEditor", "Presets", new Vector2f(7, 39), new Vector2f(getStringWidth("Add Presets from .PLAN/.BIN", 10) + 16, getFontHeight(10) + 4), 10, mainView.renderer, mainView.loader, mainView.window);

        presetEditor.addButton("addFromPlanBin", "Add Presets from .PLAN/.BIN", addFromPlanBin);
        presetEditor.addList("presetList", presetList, 150);
        presetEditor.addButton("moveUp", "Move Up", moveUp);
        presetEditor.addButton("moveDown", "Move Down", moveDown);
        presetEditor.addButton("addNew", "Add", addNew);
        presetEditor.addButton("delete", "Delete", delete);
        presetEditor.addString("amb", "Ambiance :");
        presetEditor.addTextbox("ambiance", "ambiences/amb_empty_world");

        Textbox ambiance = (Textbox) presetEditor.getElementByID("ambiance");

        DropDownTab templates = new DropDownTab("templates", "Preset Templates", new Vector2f(7, 39 + presetEditor.getFullHeight() + 7), new Vector2f(getStringWidth("Add Presets from .PLAN/.BIN", 10) + 16, getFontHeight(10) + 4), 10, mainView.renderer, mainView.loader, mainView.window)
        {
            @Override
            public void draw(MouseInput mouseInput, boolean overOther) {
                super.draw(mouseInput, overOther);
            }
        }.closed();
        ArrayList<String> templateNames = new ArrayList<>();
        templateNames.add("Blank (LBP1)");templateNames.add("Tutorials (LBP1)");templateNames.add("Gardens");templateNames.add("Savannah");templateNames.add("Wedding");templateNames.add("Canyons");templateNames.add("Metropolis");templateNames.add("Islands");templateNames.add("Temples");templateNames.add("Wilderness");templateNames.add("Blank (LBP2)");templateNames.add("Tutorials (LBP2)");templateNames.add("Da Vinci's Hideout");templateNames.add("Victoria's Laboratory");templateNames.add("Factory of a Better Tomorrow");templateNames.add("Avalonia");templateNames.add("Eve's Asylum");templateNames.add("Cosmos");
        ButtonList templateList = new ButtonList("templateList", templateNames, new Vector2f(), new Vector2f(), 10, mainView.renderer, mainView.loader, mainView.window) {
            @Override
            public void clickedButton(Object object, int index, int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)
                    switch((String)object)
                    {
                        case "Blank (LBP1)":
                            for(PLevelSettings ls : LevelSettingsUtils.getBlank1Preset()){mainView.levelSettings.add(LevelSettingsUtils.translate(ls));if(ls.backdropAmbience != null && !ls.backdropAmbience.isEmpty())ambiance.setText(ls.backdropAmbience);}
                            break;
                        case "Tutorials (LBP1)":
                            for(PLevelSettings ls : LevelSettingsUtils.getTutorial1Preset()){mainView.levelSettings.add(LevelSettingsUtils.translate(ls));if(ls.backdropAmbience != null && !ls.backdropAmbience.isEmpty())ambiance.setText(ls.backdropAmbience);}
                            break;
                        case "Gardens":
                            for(PLevelSettings ls : LevelSettingsUtils.getGardensPreset()){mainView.levelSettings.add(LevelSettingsUtils.translate(ls));if(ls.backdropAmbience != null && !ls.backdropAmbience.isEmpty())ambiance.setText(ls.backdropAmbience);}
                            break;
                        case "Savannah":
                            for(PLevelSettings ls : LevelSettingsUtils.getSavannahPreset()){mainView.levelSettings.add(LevelSettingsUtils.translate(ls));if(ls.backdropAmbience != null && !ls.backdropAmbience.isEmpty())ambiance.setText(ls.backdropAmbience);}
                            break;
                        case "Wedding":
                            for(PLevelSettings ls : LevelSettingsUtils.getWeddingPreset()){mainView.levelSettings.add(LevelSettingsUtils.translate(ls));if(ls.backdropAmbience != null && !ls.backdropAmbience.isEmpty())ambiance.setText(ls.backdropAmbience);}
                            break;
                        case "Canyons":
                            for(PLevelSettings ls : LevelSettingsUtils.getCanyonsPreset()){mainView.levelSettings.add(LevelSettingsUtils.translate(ls));if(ls.backdropAmbience != null && !ls.backdropAmbience.isEmpty())ambiance.setText(ls.backdropAmbience);}
                            break;
                        case "Metropolis":
                            for(PLevelSettings ls : LevelSettingsUtils.getMetroPreset()){mainView.levelSettings.add(LevelSettingsUtils.translate(ls));if(ls.backdropAmbience != null && !ls.backdropAmbience.isEmpty())ambiance.setText(ls.backdropAmbience);}
                            break;
                        case "Islands":
                            for(PLevelSettings ls : LevelSettingsUtils.getIslandsPreset()){mainView.levelSettings.add(LevelSettingsUtils.translate(ls));if(ls.backdropAmbience != null && !ls.backdropAmbience.isEmpty())ambiance.setText(ls.backdropAmbience);}
                            break;
                        case "Temples":
                            for(PLevelSettings ls : LevelSettingsUtils.getTemplesPreset()){mainView.levelSettings.add(LevelSettingsUtils.translate(ls));if(ls.backdropAmbience != null && !ls.backdropAmbience.isEmpty())ambiance.setText(ls.backdropAmbience);}
                            break;
                        case "Wilderness":
                            for(PLevelSettings ls : LevelSettingsUtils.getWildernessPreset()){mainView.levelSettings.add(LevelSettingsUtils.translate(ls));if(ls.backdropAmbience != null && !ls.backdropAmbience.isEmpty())ambiance.setText(ls.backdropAmbience);}
                            break;
                        case "Blank (LBP2)":
                            for(PLevelSettings ls : LevelSettingsUtils.getBlank2Preset()){mainView.levelSettings.add(LevelSettingsUtils.translate(ls));if(ls.backdropAmbience != null && !ls.backdropAmbience.isEmpty())ambiance.setText(ls.backdropAmbience);}
                            break;
                        case "Tutorials (LBP2)":
                            for(PLevelSettings ls : LevelSettingsUtils.getTutorial2Preset()){mainView.levelSettings.add(LevelSettingsUtils.translate(ls));if(ls.backdropAmbience != null && !ls.backdropAmbience.isEmpty())ambiance.setText(ls.backdropAmbience);}
                            break;
                        case "Da Vinci's Hideout":
                            for(PLevelSettings ls : LevelSettingsUtils.getDaVinciPreset()){mainView.levelSettings.add(LevelSettingsUtils.translate(ls));if(ls.backdropAmbience != null && !ls.backdropAmbience.isEmpty())ambiance.setText(ls.backdropAmbience);}
                            break;
                        case "Victoria's Laboratory":
                            for(PLevelSettings ls : LevelSettingsUtils.getVictoriaPreset()){mainView.levelSettings.add(LevelSettingsUtils.translate(ls));if(ls.backdropAmbience != null && !ls.backdropAmbience.isEmpty())ambiance.setText(ls.backdropAmbience);}
                            break;
                        case "Factory of a Better Tomorrow":
                            for(PLevelSettings ls : LevelSettingsUtils.getFactoryPreset()){mainView.levelSettings.add(LevelSettingsUtils.translate(ls));if(ls.backdropAmbience != null && !ls.backdropAmbience.isEmpty())ambiance.setText(ls.backdropAmbience);}
                            break;
                        case "Avalonia":
                            for(PLevelSettings ls : LevelSettingsUtils.getAvaloniaPreset()){mainView.levelSettings.add(LevelSettingsUtils.translate(ls));if(ls.backdropAmbience != null && !ls.backdropAmbience.isEmpty())ambiance.setText(ls.backdropAmbience);}
                            break;
                        case "Eve's Asylum":
                            for(PLevelSettings ls : LevelSettingsUtils.getEvePreset()){mainView.levelSettings.add(LevelSettingsUtils.translate(ls));if(ls.backdropAmbience != null && !ls.backdropAmbience.isEmpty())ambiance.setText(ls.backdropAmbience);}
                            break;
                        case "Cosmos":
                            for(PLevelSettings ls : LevelSettingsUtils.getCosmosPreset()){mainView.levelSettings.add(LevelSettingsUtils.translate(ls));if(ls.backdropAmbience != null && !ls.backdropAmbience.isEmpty())ambiance.setText(ls.backdropAmbience);}
                            break;
                    }
            }

            @Override
            public void draw(MouseInput mouseInput, boolean overElement) {
                hovering = -1;
                super.draw(mouseInput, overElement);
            }
            int hovering = -1;
            @Override
            public void hoveringButton(Object object, int index) {
                hovering = index;
            }

            @Override
            public boolean isHighlighted(Object object, int index) {
                return hovering == index;
            }

            @Override
            public boolean isSelected(Object object, int index) {
                return false;
            }

            @Override
            public String buttonText(Object object, int index) {
                return (String)object;
            }

            @Override
            public boolean searchFilter(Object object, int index) {
                return true;
            }

        };
        templates.addList("templateList", templateList, 240);

        this.guiElements.add(presetEditor);
        this.guiElements.add(templates);
    }

    @Override
    public void draw(MouseInput mouseInput) {

        drawRect(mainView.window.width - 305, 0, 305, mainView.window.height, Config.PRIMARY_COLOR);
        drawLine(new Vector2i(mainView.window.width - 304, 0), new Vector2i(mainView.window.width - 304, mainView.window.height), Config.SECONDARY_COLOR, false);
        drawString("Sun Position:", Config.FONT_COLOR, mainView.window.width - 302, 3, 10);
        drawString("Scale:", Config.FONT_COLOR, mainView.window.width - sunFields - getStringWidth("Scale:", 10), 3 + (getFontHeight(10) + 3) * 1, 10);
        drawString("X:", Config.FONT_COLOR, mainView.window.width - sunFields - getStringWidth("X:", 10), 3 + (getFontHeight(10) + 3) * 2, 10);
        drawString("Y:", Config.FONT_COLOR, mainView.window.width - sunFields - getStringWidth("Y:", 10), 3 + (getFontHeight(10) + 3) * 3, 10);
        drawString("Z:", Config.FONT_COLOR, mainView.window.width - sunFields - getStringWidth("Z:", 10), 3 + (getFontHeight(10) + 3) * 4, 10);
        drawString("Sun Color:", Config.FONT_COLOR, mainView.window.width - 302, 3 + (getFontHeight(10) + 3) * 5, 10);
        drawString("#", Config.FONT_COLOR, mainView.window.width - 108 - getStringWidth("#", 10), 3 + (getFontHeight(10) + 3) * 5, 10);
        drawString("Sun Multiplier:", Config.FONT_COLOR, mainView.window.width - 302, 3 + (getFontHeight(10) + 3) * 6, 10);
        drawString("Ambient Color:", Config.FONT_COLOR, mainView.window.width - 302, 3 + (getFontHeight(10) + 3) * 7, 10);
        drawString("#", Config.FONT_COLOR, mainView.window.width - 108 - getStringWidth("#", 10), 3 + (getFontHeight(10) + 3) * 7, 10);
        drawString("Exposure:", Config.FONT_COLOR, mainView.window.width - 302, 3 + (getFontHeight(10) + 3) * 8, 10);
        drawString("Fog Color:", Config.FONT_COLOR, mainView.window.width - 302, 3 + (getFontHeight(10) + 3) * 9, 10);
        drawString("#", Config.FONT_COLOR, mainView.window.width - 108 - getStringWidth("#", 10), 3 + (getFontHeight(10) + 3) * 9, 10);
        drawString("Fog Near:", Config.FONT_COLOR, mainView.window.width - 302, 3 + (getFontHeight(10) + 3) * 10, 10);
        drawString("Fog Far:", Config.FONT_COLOR, mainView.window.width - 302, 3 + (getFontHeight(10) + 3) * 11, 10);
        drawString("Rim Color:", Config.FONT_COLOR, mainView.window.width - 302, 3 + (getFontHeight(10) + 3) * 12, 10);
        drawString("#", Config.FONT_COLOR, mainView.window.width - 108 - getStringWidth("#", 10), 3 + (getFontHeight(10) + 3) * 12, 10);
        drawString("Rim Color 2:", Config.FONT_COLOR, mainView.window.width - 302, 3 + (getFontHeight(10) + 3) * 13, 10);
        drawString("#", Config.FONT_COLOR, mainView.window.width - 108 - getStringWidth("#", 10), 3 + (getFontHeight(10) + 3) * 13, 10);
        drawString("Baked Shadow Amount:", Config.FONT_COLOR, mainView.window.width - 302, 3 + (getFontHeight(10) + 3) * 14, 10);
        drawString("Baked Shadow Blur:", Config.FONT_COLOR, mainView.window.width - 302, 3 + (getFontHeight(10) + 3) * 15, 10);
        drawString("Baked AO Bias:", Config.FONT_COLOR, mainView.window.width - 302, 3 + (getFontHeight(10) + 3) * 16, 10);
        drawString("Baked AO Scale:", Config.FONT_COLOR, mainView.window.width - 302, 3 + (getFontHeight(10) + 3) * 17, 10);
        drawString("Dynamic AO Amount:", Config.FONT_COLOR, mainView.window.width - 302, 3 + (getFontHeight(10) + 3) * 18, 10);
        drawString("DOF Near:", Config.FONT_COLOR, mainView.window.width - 302, 3 + (getFontHeight(10) + 3) * 19, 10);
        drawString("DOF Far:", Config.FONT_COLOR, mainView.window.width - 302, 3 + (getFontHeight(10) + 3) * 20, 10);
        drawString("Z Effect Amount:", Config.FONT_COLOR, mainView.window.width - 302, 3 + (getFontHeight(10) + 3) * 21, 10);
        drawString("Z Effect Brightness:", Config.FONT_COLOR, mainView.window.width - 302, 3 + (getFontHeight(10) + 3) * 22, 10);
        drawString("Z Effect Contrast:", Config.FONT_COLOR, mainView.window.width - 302, 3 + (getFontHeight(10) + 3) * 23, 10);

        LevelSettings ls = null;

        try
        {
            ls = mainView.levelSettings.get(mainView.selectedPresetIndex);
        }catch (Exception e){}

        Vector3f sunPos3D = ls == null ? new Vector3f() : new Vector3f(ls.sunPosition).mul(ls.sunPositionScale);
        Vector3f sunPos2D = mainView.camera.worldToScreenPointF(sunPos3D, mainView.window);

        sunTool.updateModels(mainView.camera, sunPos2D, mainView.window);

        if(mouseInput.inWindow && sunTool.selected == -1)
            sunTool.testForMouse(true, mainView.camera, mainView.mousePicker, true, false, false);

        if(sunTool.selected != -1)
        {
            switch (sunTool.selected)
            {
                //0,1,2 pos
                //3,4,5 rot
                //7,8,9 scale
                case 0:
                    //x pos
                {
                    float mousediff = (float) (mouseInput.currentPos.x - sunTool.initPos.x);
                    boolean h = false;

                    if (mainView.camera.getWrappedRotation().y > 60f) {
                        mousediff = -(float) (mouseInput.currentPos.y - sunTool.initPos.y);
                        h = true;
                    }

                    if (mainView.camera.getWrappedRotation().y < -60f) {
                        mousediff = (float) (mouseInput.currentPos.y - sunTool.initPos.y);
                        h = true;
                    }

                    if (mainView.camera.getWrappedRotation().y > 120f || mainView.camera.getWrappedRotation().y < -120f)
                    {
                        mousediff = -(float) (mouseInput.currentPos.x - sunTool.initPos.x);
                        h = false;
                    }

                    Vector3f ppos = sunPos3D;
                    if(ls != null)
                        ls.sunPosition = new Vector3f(lastPos.x + (mousediff * (mainView.camera.pos.distance(new Vector3f(lastPos)))) / (h ? 100f : 400f), ppos.y, ppos.z).div(ls.sunPositionScale);
                }
                break;
                case 1:
                    //y pos
                {
                    float mousediff = -(float) (mouseInput.currentPos.y - sunTool.initPos.y);

                    boolean h = false;

                    if (mainView.camera.getWrappedRotation().x > 20f)
                        h = true;

                    if (mainView.camera.getWrappedRotation().x < -20f)
                        h = true;

                    Vector3f ppos = sunPos3D;
                    if(ls != null)
                        ls.sunPosition = new Vector3f(ppos.x, lastPos.y + (mousediff * (mainView.camera.pos.distance(new Vector3f(lastPos)))) / (h ? 100f : 400f), ppos.z).div(ls.sunPositionScale);
                }
                break;
                case 2:
                    //z pos
                {
                    float mousediff = (float) (mouseInput.currentPos.x - sunTool.initPos.x);
                    boolean h = false;

                    if (mainView.camera.getWrappedRotation().y > 60f + 90f) {
                        mousediff = -(float) (mouseInput.currentPos.y - sunTool.initPos.y);
                        h = true;
                    }

                    if (mainView.camera.getWrappedRotation().y < -60f + 90f) {
                        mousediff = (float) (mouseInput.currentPos.y - sunTool.initPos.y);
                        h = true;
                    }

                    if (mainView.camera.getWrappedRotation().y > 120f + 90f || mainView.camera.getWrappedRotation().y < -120f + 90f)
                    {
                        mousediff = -(float) (mouseInput.currentPos.x - sunTool.initPos.x);
                        h = false;
                    }

                    Vector3f ppos = sunPos3D;
                    if(ls != null)
                        ls.sunPosition = new Vector3f(ppos.x, ppos.y, lastPos.z + (mousediff * (mainView.camera.pos.distance(new Vector3f(lastPos)))) / (h ? 100f : 400f)).div(ls.sunPositionScale);

                }
                break;
            }
        }

        sunTool.render(true, true, false, false, mainView.crosshair, sunPos2D, mainView.window, mainView.loader, mainView.renderer, mouseInput);

        super.draw(mouseInput);
    }

    public Vector3f lastPos = new Vector3f();

    @Override
    public boolean onClick(Vector2d pos, int button, int action, int mods) {

        LevelSettings ls = null;

        try
        {
            ls = mainView.levelSettings.get(mainView.selectedPresetIndex);
        }catch (Exception e){}

        if(sunTool.onClick(pos, button, action, mods, mainView.window, mainView.camera) && ls != null)
            lastPos = new Vector3f(ls.sunPosition).mul(ls.sunPositionScale);

        return super.onClick(pos, button, action, mods);
    }
}
