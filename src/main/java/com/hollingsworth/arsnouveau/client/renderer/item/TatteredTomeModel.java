package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.items.WornNotebook;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class TatteredTomeModel extends TransformAnimatedModel<WornNotebook> {
    public static final Identifier CLOSED = ArsNouveau.prefix("spellbook_closed");

    public TatteredTomeModel() {
    }

    @Override
    public Identifier getModelResource(@Nullable ItemDisplayContext transformType) {
        return CLOSED;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return ArsNouveau.prefix("textures/item/tattered_tome.png");
    }

    @Override
    public Identifier getAnimationResource(WornNotebook animatable) {
        return ArsNouveau.prefix("empty");
    }

    // GeckoLib 5: getRenderType moved to GeoRenderer, not GeoModel
}
