package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

public class EffectWither extends AbstractEffect implements IPotionEffect {
    public static EffectWither INSTANCE = new EffectWither();

    private EffectWither() {
        super(GlyphLib.EffectWitherID, "Wither");
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {

        if (rayTraceResult.getEntity() instanceof LivingEntity living) {
            ((IPotionEffect)this).applyConfigPotion(living, MobEffects.WITHER, spellStats);
        }

    }

    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addPotionConfig(builder, 30);
        addExtendTimeConfig(builder, 8);
    }

    @Override
    protected Map<ResourceLocation, Integer> getDefaultAugmentLimits(Map<ResourceLocation, Integer> defaults) {
        super.getDefaultAugmentLimits(defaults);
        defaults.put(AugmentAmplify.INSTANCE.getRegistryName(), 4);
        return defaults;
    }

    @Override
    public int getDefaultManaCost() {
        return 100;
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.THREE;
    }

   @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return getPotionAugments();
    }

    @Override
    public String getBookDescription() {
        return "Applies the Wither debuff.";
    }

   @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ABJURATION);
    }

    @Override
    public int getBaseDuration() {
        return POTION_TIME == null ? 30 : POTION_TIME.get();
    }

    @Override
    public int getExtendTimeDuration() {
        return EXTEND_TIME == null ? 8 : EXTEND_TIME.get();
    }
}

