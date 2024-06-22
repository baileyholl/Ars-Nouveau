package com.hollingsworth.arsnouveau.api.spell;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class TurretSpellCaster extends SpellCaster {

    public TurretSpellCaster(ItemStack stack) {
        super(stack);
    }

    public TurretSpellCaster(CompoundTag itemTag) {
        super(itemTag);
    }

    @Override
    public ResourceLocation getTagID() {
        return ArsNouveau.prefix( "turret_caster");
    }
}
