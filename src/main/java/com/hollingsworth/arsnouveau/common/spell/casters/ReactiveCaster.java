package com.hollingsworth.arsnouveau.common.spell.casters;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.spell.SpellCaster;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class ReactiveCaster extends SpellCaster {
    public ReactiveCaster(ItemStack stack) {
        super(stack);
    }

    public ReactiveCaster(CompoundTag tag) {
        super(tag);
    }

    @Override
    public ResourceLocation getTagID() {
        return new ResourceLocation(ArsNouveau.MODID, "reactive_caster");
    }
}
