package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class EffectStrength extends AbstractEffect {
    public EffectStrength() {
        super(ModConfig.EffectStrength, "Strength");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, ArrayList<AbstractAugment> augments) {

    }

    @Override
    public int getManaCost() {
        return 0;
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.BLAZE_POWDER;
    }

    @Override
    protected String getBookDescription() {
        return "Applies the Strength buff.";
    }
}
