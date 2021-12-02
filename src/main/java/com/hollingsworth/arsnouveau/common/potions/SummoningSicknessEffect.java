package com.hollingsworth.arsnouveau.common.potions;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

import java.util.ArrayList;
import java.util.List;

public class SummoningSicknessEffect extends MobEffect {
    protected SummoningSicknessEffect() {
        super(MobEffectCategory.HARMFUL, 2039587);
        setRegistryName(ArsNouveau.MODID, "summoning_sickness");
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return new ArrayList<>();
    }
}
