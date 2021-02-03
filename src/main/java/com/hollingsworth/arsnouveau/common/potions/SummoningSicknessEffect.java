package com.hollingsworth.arsnouveau.common.potions;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

import java.util.ArrayList;
import java.util.List;

public class SummoningSicknessEffect extends Effect {
    protected SummoningSicknessEffect() {
        super(EffectType.HARMFUL, 2039587);
        setRegistryName(ArsNouveau.MODID, "summoning_sickness");
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return new ArrayList<>();
    }
}
