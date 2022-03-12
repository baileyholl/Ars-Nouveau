package com.hollingsworth.arsnouveau.api.spell;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class TurretSpellCaster extends SpellCaster {

    public TurretSpellCaster(ItemStack stack) {
        super(stack);
    }

    public TurretSpellCaster(CompoundTag itemTag) {
        super(itemTag);
    }

    @Override
    public String getTagID() {
        return "ars_nouveau_turret_caster";
    }
}
