package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.*;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class EffectAquatic extends AbstractEffect {

    public static EffectAquatic INSTANCE = new EffectAquatic();

    private EffectAquatic() {
        super(GlyphLib.EffectAquatic, "Aquatic");
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        applyConfigPotion(((LivingEntity) rayTraceResult.getEntity()), MobEffects.WATER_BREATHING, spellStats);
    }

    @Override
    public boolean wouldSucceed(HitResult rayTraceResult, Level world, LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        return livingEntityHitSuccess(rayTraceResult);
    }

    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addDefaultPotionConfig(builder);
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ELEMENTAL_WATER);
    }

    @Override
    public int getDefaultManaCost() {
        return 25;
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.COD;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return POTION_AUGMENTS;
    }

    @Override
    public String getBookDescription() {
        return "Applies the Water Breathing buff.";
    }
}
