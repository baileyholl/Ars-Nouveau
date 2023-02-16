package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;

import javax.annotation.Nullable;

public class SpellBookModel extends TransformAnimatedModel<SpellBook> {
    ResourceLocation OPEN = new ResourceLocation(ArsNouveau.MODID, "geo/spellbook_open.geo.json");
    ResourceLocation CLOSED = new ResourceLocation(ArsNouveau.MODID, "geo/spellbook_closed.geo.json");

    @Override
    public ResourceLocation getModelResource(SpellBook book, @Nullable ItemTransforms.TransformType transformType) {
        if (transformType == ItemTransforms.TransformType.GUI || transformType == ItemTransforms.TransformType.FIXED) {
            return CLOSED;
        }
        return OPEN;
    }

    @Override
    public void setCustomAnimations(SpellBook entity, int uniqueID, @org.jetbrains.annotations.Nullable AnimationEvent customPredicate) {
        super.setCustomAnimations(entity, uniqueID, customPredicate);
        this.getBone("tier3").setHidden(entity.tier.value < 3);
        this.getBone("tier1").setHidden(entity.tier.value != 1);
        this.getBone("tier2").setHidden(entity.tier.value != 2);

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
        return new ResourceLocation(ArsNouveau.MODID, "animations/empty.json");
    }
}