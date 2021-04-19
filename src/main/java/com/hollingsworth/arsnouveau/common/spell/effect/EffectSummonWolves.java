package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import com.hollingsworth.arsnouveau.common.entity.SummonWolf;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

import com.hollingsworth.arsnouveau.api.spell.ISpellTier.Tier;

public class EffectSummonWolves extends AbstractEffect {

    public EffectSummonWolves() {
        super(GlyphLib.EffectSummonWolvesID, "Summon Wolves");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        super.onResolve(rayTraceResult, world, shooter, augments, spellContext);
        if(!canSummon(shooter))
            return;
        Vector3d hit = rayTraceResult.getLocation();
        int ticks = 60 * 20 * (1 + getDurationModifier(augments));
        for(int i = 0; i < 2; i++){
            SummonWolf wolf = new SummonWolf(ModEntities.SUMMON_WOLF, world);
            wolf.ticksLeft = ticks;
            wolf.setPos(hit.x(), hit.y(), hit.z());
            wolf.setTarget(shooter.getLastHurtMob());
            wolf.setAggressive(true);
            wolf.setTame(true);
            wolf.tame((PlayerEntity) shooter);
            summonLivingEntity(rayTraceResult, world, shooter, augments, spellContext, wolf);
        }
        applySummoningSickness(shooter, ticks);
    }

    @Override
    public int getManaCost() {
        return 100;
    }


    @Override
    public String getBookDescription() {
        return "Summons two wolves that will fight with you. Extend Time will increase the amount of time on the summons. Applies Summoning Sickness to the caster, preventing other summoning magic.";
    }

    @Override
    public Tier getTier() {
        return Tier.ONE;
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return ItemsRegistry.WILDEN_HORN;
    }
}
