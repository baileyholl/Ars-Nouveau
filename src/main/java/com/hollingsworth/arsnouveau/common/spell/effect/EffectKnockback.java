package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class EffectKnockback extends AbstractEffect {
    public static EffectKnockback INSTANCE = new EffectKnockback();

    private EffectKnockback() {
        super(GlyphLib.EffectKnockbackID, "Knockback");
    }

    @Override
    public void onResolveEntity(EntityRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        if(rayTraceResult.getEntity() instanceof LivingEntity && shooter != null){
            LivingEntity target = (LivingEntity) rayTraceResult.getEntity();
            float strength = (float) (GENERIC_DOUBLE.get() + AMP_VALUE.get() * spellStats.getAmpMultiplier());
            knockback(target, shooter, strength);
            target.hurtMarked = true;
        }
    }

    public void knockback(LivingEntity target, LivingEntity shooter, float strength){
        target.knockback(strength,MathHelper.sin(shooter.yRot * ((float)Math.PI / 180F)), (double)(-MathHelper.cos(shooter.yRot * ((float)Math.PI / 180F))));
    }

    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addGenericDouble(builder, 1.5, "Base knockback value", "base_value");
        addAmpConfig(builder, 1);
    }

    @Override
    public boolean wouldSucceed(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments) {
        return livingEntityHitSuccess(rayTraceResult);
    }

    @Override
    public boolean dampenIsAllowed() {
        return true;
    }

    @Override
    public int getManaCost() {
        return 15;
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.PISTON;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE);
    }

    @Override
    public String getBookDescription() {
        return "Knocks a target away a short distance from the caster";
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ELEMENTAL_AIR);
    }
}
