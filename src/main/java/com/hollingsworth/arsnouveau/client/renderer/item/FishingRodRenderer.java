package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.items.EnchantersFishingRod;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class FishingRodRenderer extends GeoItemRenderer<EnchantersFishingRod> {
    public FishingRodRenderer() {
        super(new GeoModel<>() {
            @Override
            public Identifier getModelResource(GeoRenderState renderState) {
                return ArsNouveau.prefix("enchanters_rod");
            }

            @Override
            public Identifier getTextureResource(GeoRenderState renderState) {
                return ArsNouveau.proxy.getPlayer().fishing == null ? ArsNouveau.prefix("textures/item/enchanters_rod_stowed.png") : ArsNouveau.prefix("textures/item/enchanters_rod_cast.png");
            }

            @Override
            public Identifier getAnimationResource(EnchantersFishingRod wand) {
                return ArsNouveau.prefix("enchanters_fishing_rod");
            }
        });
    }
}
