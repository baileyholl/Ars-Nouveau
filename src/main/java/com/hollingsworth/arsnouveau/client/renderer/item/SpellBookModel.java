package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.spell.SpellTier;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public class SpellBookModel extends TransformAnimatedModel<SpellBook> {
    ResourceLocation T1 = new ResourceLocation(ArsNouveau.MODID, "geo/spellbook_tier1.geo.json");
    ResourceLocation T2 = new ResourceLocation(ArsNouveau.MODID, "geo/spellbook_tier2.geo.json");
    ResourceLocation T3 = new ResourceLocation(ArsNouveau.MODID, "geo/spellbook_tier3.geo.json");
    ResourceLocation T3_CLOSED = new ResourceLocation(ArsNouveau.MODID, "geo/spellbook_tier3closed.geo.json");
    ResourceLocation T1_CLOSED = new ResourceLocation(ArsNouveau.MODID, "geo/spellbook_tier1closed.geo.json");
    ResourceLocation T2_CLOSED = new ResourceLocation(ArsNouveau.MODID, "geo/spellbook_tier2closed.geo.json");

    public boolean isOpen;

    @Override
    public ResourceLocation getModelResource(SpellBook book, @Nullable ItemTransforms.TransformType transformType) {

        if (transformType == ItemTransforms.TransformType.GUI || transformType == ItemTransforms.TransformType.FIXED) {
            if (book.tier == SpellTier.ONE)
                return T1_CLOSED;
            if (book.tier == SpellTier.TWO)
                return T2_CLOSED;
            return T3_CLOSED;
        }

        if (book.tier == SpellTier.ONE)
            return T1;
        if (book.tier == SpellTier.TWO)
            return T2;
        return T3;
    }


    @Override
    public ResourceLocation getModelResource(SpellBook object) {
        return getModelResource(object, null);
    }


    @Override
    public ResourceLocation getTextureResource(SpellBook object) {
        return new ResourceLocation(ArsNouveau.MODID, "textures/items/spellbook_purple.png");
    }

    @Override
    public ResourceLocation getAnimationResource(SpellBook animatable) {
        return new ResourceLocation(ArsNouveau.MODID, "animations/spellbook_animations.json");
    }
}