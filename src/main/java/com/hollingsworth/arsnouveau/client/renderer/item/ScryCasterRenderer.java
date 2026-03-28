package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.items.ScryCaster;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class ScryCasterRenderer extends GeoItemRenderer<ScryCaster> {
    public ScryCasterRenderer() {
        super(new GeoModel<>() {
            @Override
            public Identifier getModelResource(GeoRenderState renderState) {
                return ArsNouveau.prefix("enchanters_eye");
            }

            @Override
            public Identifier getTextureResource(GeoRenderState renderState) {
                return ArsNouveau.prefix("textures/item/enchanters_eye.png");
            }

            @Override
            public Identifier getAnimationResource(ScryCaster wand) {
                return ArsNouveau.prefix("enchanters_eye");
            }
        });
    }

    @Override
    public RenderType getRenderType(GeoRenderState renderState, Identifier texture) {
        return RenderTypes.entityTranslucent(texture);
    }
}
