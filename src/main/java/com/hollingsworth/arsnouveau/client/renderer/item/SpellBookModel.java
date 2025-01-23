package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.Nullable;

public class SpellBookModel extends TransformAnimatedModel<SpellBook> {
    public static final ResourceLocation OPEN = ArsNouveau.prefix( "geo/spellbook_open.geo.json");
    public static final ResourceLocation CLOSED = ArsNouveau.prefix( "geo/spellbook_closed.geo.json");

    public ResourceLocation modelLoc;

    public SpellBookModel(ResourceLocation modelLocation) {
        this.modelLoc = modelLocation;
    }

    @Override
    public ResourceLocation getModelResource(SpellBook object) {
        return getModelResource(object, null);
    }

    @Override
    public ResourceLocation getModelResource(SpellBook object, @Nullable ItemDisplayContext transformType) {
        if (transformType == ItemDisplayContext.GUI || transformType == ItemDisplayContext.FIXED) {
            return CLOSED;
        }
        return modelLoc;
    }


    @Override
    public ResourceLocation getTextureResource(SpellBook object) {
        return ArsNouveau.prefix( "textures/item/spellbook_purple.png");
    }

    @Override
    public ResourceLocation getAnimationResource(SpellBook animatable) {
        return ArsNouveau.prefix( "animations/empty.json");
    }

    @Override
    public RenderType getRenderType(SpellBook animatable, ResourceLocation texture) {
        return RenderType.entityCutoutNoCull(texture);
    }
}