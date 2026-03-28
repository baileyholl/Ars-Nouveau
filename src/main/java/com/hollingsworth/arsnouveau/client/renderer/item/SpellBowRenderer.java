package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.client.renderer.ANGeoModel;
import com.hollingsworth.arsnouveau.common.items.SpellBow;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class SpellBowRenderer extends GeoItemRenderer<SpellBow> {
    public SpellBowRenderer() {
        super(new ANGeoModel<>("spellbow", "textures/item/spellbow.png", "wand_animation"));
    }

    @Override
    public RenderType getRenderType(GeoRenderState renderState, Identifier texture) {
        return RenderTypes.entityTranslucent(texture);
    }
}
