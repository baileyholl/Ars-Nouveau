package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.tile.PotionMelderTile;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

// GeckoLib 5.4.2: getModelResource and getTextureResource now take GeoRenderState, not T.
public class PotionMelderModel extends GeoModel<PotionMelderTile> {

    public static final Identifier model = ArsNouveau.prefix("potion_stirrer");
    public static final Identifier texture = ArsNouveau.prefix("textures/block/potion_stirrer.png");
    public static final Identifier anim = ArsNouveau.prefix("potion_melder_animation");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return model;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return texture;
    }

    @Override
    public Identifier getAnimationResource(PotionMelderTile volcanicTile) {
        return anim;
    }
}
