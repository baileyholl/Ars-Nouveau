package com.hollingsworth.arsnouveau.common.items.curios;

import com.hollingsworth.arsnouveau.api.item.ISpellModifierItem;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.common.items.ModItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;

public class ShapersFocus extends ModItem implements ISpellModifierItem {
    public ShapersFocus(Properties properties) {
        super(properties);
    }

    public ShapersFocus(Properties properties, String registryName) {
        super(properties, registryName);
    }

    public ShapersFocus(String registryName) {
        super(registryName);
    }

    @Override
    public SpellStats.Builder applyItemModifiers(ItemStack stack, SpellStats.Builder builder, AbstractSpellPart spellPart, HitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellContext spellContext) {
        builder.addDamageModifier(1.0f);
        return builder;
    }
}
