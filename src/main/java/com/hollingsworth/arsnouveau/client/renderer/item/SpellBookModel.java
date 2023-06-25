package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animation.AnimationState;

public class SpellBookModel extends TransformAnimatedModel<SpellBook> {
    public static final ResourceLocation OPEN = new ResourceLocation(ArsNouveau.MODID, "geo/spellbook_open.geo.json");
    public static final ResourceLocation CLOSED = new ResourceLocation(ArsNouveau.MODID, "geo/spellbook_closed.geo.json");


    @Override
    public void setCustomAnimations(SpellBook entity, long uniqueID, @org.jetbrains.annotations.Nullable AnimationState customPredicate) {
        super.setCustomAnimations(entity, uniqueID, customPredicate);
        this.getBone("tier3").get().setHidden(entity.tier.value < 3);
        this.getBone("tier1").get().setHidden(entity.tier.value != 1);
        this.getBone("tier2").get().setHidden(entity.tier.value != 2);

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
        return OPEN;
    }


    @Override
    public ResourceLocation getTextureResource(SpellBook object) {
        return new ResourceLocation(ArsNouveau.MODID, "textures/item/spellbook_purple.png");
    }

    @Override
    public ResourceLocation getAnimationResource(SpellBook animatable) {
        return new ResourceLocation(ArsNouveau.MODID, "animations/empty.json");
    }
}