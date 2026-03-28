package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.items.EnchantersShield;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

// GeckoLib 5: getModelResource/getTextureResource now take GeoRenderState
public class ShieldModel extends GeoModel<EnchantersShield> {

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return ArsNouveau.prefix("shield");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return ArsNouveau.prefix("textures/item/enchanters_shield.png");
    }

    @Override
    public Identifier getAnimationResource(EnchantersShield wand) {
        return ArsNouveau.prefix("shield");
    }
}
