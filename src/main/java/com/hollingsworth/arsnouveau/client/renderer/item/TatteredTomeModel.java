package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.items.WornNotebook;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.Nullable;

public class TatteredTomeModel extends TransformAnimatedModel<WornNotebook> {
    public static final ResourceLocation CLOSED = ArsNouveau.prefix( "geo/spellbook_closed.geo.json");



    public TatteredTomeModel() {
    }

    @Override
    public ResourceLocation getModelResource(WornNotebook object) {
        return getModelResource(object, null);
    }

    @Override
    public ResourceLocation getModelResource(WornNotebook object, @Nullable ItemDisplayContext transformType) {
        return CLOSED;
    }

    @Override
    public ResourceLocation getTextureResource(WornNotebook object) {
        return ArsNouveau.prefix( "textures/item/tattered_tome.png");
    }

    @Override
    public ResourceLocation getAnimationResource(WornNotebook animatable) {
        return ArsNouveau.prefix( "animations/empty.json");
    }

    @Override
    public RenderType getRenderType(WornNotebook animatable, ResourceLocation texture) {
        return RenderType.entityCutoutNoCull(texture);
    }
}