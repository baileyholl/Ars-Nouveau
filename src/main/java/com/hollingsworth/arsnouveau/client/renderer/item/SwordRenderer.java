package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.items.EnchantersSword;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class SwordRenderer extends GeoItemRenderer<EnchantersSword> {
    public SwordRenderer() {
        super(new GeoModel<>() {
            @Override
            public Identifier getModelResource(GeoRenderState renderState) {
                return ArsNouveau.prefix("sword");
            }

            @Override
            public Identifier getTextureResource(GeoRenderState renderState) {
                return ArsNouveau.prefix("textures/item/enchanters_sword.png");
            }

            @Override
            public Identifier getAnimationResource(EnchantersSword wand) {
                return ArsNouveau.prefix("sword");
            }
        });
    }

    @Override
    public RenderType getRenderType(GeoRenderState renderState, Identifier texture) {
        return RenderTypes.entityTranslucent(texture);
    }
}
