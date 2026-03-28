package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.client.renderer.entity.ArsHumanoidRenderState;
import com.hollingsworth.arsnouveau.common.armor.AnimatedMagicArmor;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

// GeckoLib 5: GeoArmorRenderer requires R extends HumanoidRenderState & GeoRenderState
// ArsHumanoidRenderState satisfies this at compile time; GeckoLib's mixin handles it at runtime.
// Dye color is injected via DyeableGeoModel.addAdditionalStateData (GeoRenderState-typed, no cast issues).
public class ArmorRenderer extends GeoArmorRenderer<AnimatedMagicArmor, ArsHumanoidRenderState> {

    public ArmorRenderer(GeoModel<AnimatedMagicArmor> modelProvider) {
        super(modelProvider);
    }
}
