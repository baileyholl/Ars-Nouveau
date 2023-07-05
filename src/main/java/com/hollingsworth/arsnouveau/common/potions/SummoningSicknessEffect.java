package com.hollingsworth.arsnouveau.common.potions;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SummoningSicknessEffect extends MobEffect {
    public SummoningSicknessEffect() {
        super(MobEffectCategory.HARMFUL, 2039587);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return new ArrayList<>();
    }
}
