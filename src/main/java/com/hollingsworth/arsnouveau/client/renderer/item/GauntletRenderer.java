package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.items.EnchantersGauntlet;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class GauntletRenderer extends GeoItemRenderer<EnchantersGauntlet> {
    public GauntletRenderer() {
        super(new GeoModel<>() {
            @Override
            public Identifier getModelResource(GeoRenderState renderState) {
                return ArsNouveau.prefix("enchanters_gauntlet");
            }

            @Override
            public Identifier getTextureResource(GeoRenderState renderState) {
                return ArsNouveau.prefix("textures/item/enchanters_gauntlet.png");
            }

            @Override
            public Identifier getAnimationResource(EnchantersGauntlet wand) {
                return ArsNouveau.prefix("enchanters_gauntlet");
            }
        });
    }
}
