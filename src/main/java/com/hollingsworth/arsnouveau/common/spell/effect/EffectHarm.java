package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Set;

public class EffectHarm extends AbstractEffect {
    public static EffectHarm INSTANCE = new EffectHarm();

    private EffectHarm() {
        super(GlyphLib.EffectHarmID, "Harm");
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nonnull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (!(rayTraceResult.getEntity() instanceof ItemEntity)) {
            double damage = DAMAGE.get() + AMP_VALUE.get() * spellStats.getAmpMultiplier();
            int time = (int) spellStats.getDurationMultiplier();
            if (time > 0 && rayTraceResult.getEntity() instanceof LivingEntity entity) {
                applyConfigPotion(entity, MobEffects.POISON, spellStats);
            } else {
                dealDamage(world, shooter, (float) damage, spellStats, rayTraceResult.getEntity(), DamageSource.playerAttack(getPlayer(shooter, (ServerLevel) world)));
            }
        }
    }

    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addDamageConfig(builder, 5.0);
        addAmpConfig(builder, 2.0);
        addPotionConfig(builder, 5);
        addExtendTimeConfig(builder, 5);
    }


    @Override
    public boolean defaultedStarterGlyph() {
        return true;
    }

    @Override
    public int getDefaultManaCost() {
        return 15;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(
                AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE,
                AugmentExtendTime.INSTANCE, AugmentDurationDown.INSTANCE,
                AugmentFortune.INSTANCE
        );
    }

    @Override
    protected void addDefaultAugmentLimits(Map<ResourceLocation, Integer> defaults) {
        defaults.put(AugmentAmplify.INSTANCE.getRegistryName(), 2);
    }

    @Override
    public String getBookDescription() {
        return "A spell you start with. Damages a target. May be increased by Amplify, or applies the Poison debuff when using Extend Time. Note, multiple Harms without a delay will not apply due to invincibility on hit.";
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ELEMENTAL_EARTH);
    }
}
