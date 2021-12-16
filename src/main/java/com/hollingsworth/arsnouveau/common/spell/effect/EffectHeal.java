package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
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

public class EffectHeal extends AbstractEffect {
    public static EffectHeal INSTANCE = new EffectHeal();

    private EffectHeal() {
        super(GlyphLib.EffectHealID, "Heal");
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        if(rayTraceResult.getEntity() instanceof LivingEntity){
            LivingEntity entity = ((LivingEntity) rayTraceResult.getEntity());
            if(entity.isRemoved() || entity.getHealth() <= 0)
                return;

            float healVal = (float) (GENERIC_DOUBLE.get() + AMP_VALUE.get() * spellStats.getAmpMultiplier());
            if(spellStats.hasBuff(AugmentExtendTime.INSTANCE)){
                applyPotionWithCap(entity, MobEffects.REGENERATION, spellStats, 5, 5, 4);
            }else{
                if(entity.isInvertedHealAndHarm()){
                    dealDamage(world, shooter, healVal, spellStats, entity, buildDamageSource(world, shooter).setMagic());
                }else{
                    entity.heal(healVal);
                }
            }
        }
    }

    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addGenericDouble(builder, 3.0, "Base heal amount", "base_heal");
        addAmpConfig(builder, 3.0);
    }

    @Override
    public boolean wouldSucceed(HitResult rayTraceResult, Level world, LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        return livingEntityHitSuccess(rayTraceResult);
    }

    @Override
    public int getDefaultManaCost() {
        return 100;
    }

    @Override
    public Tier getTier() {
        return Tier.TWO;
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.GLISTERING_MELON_SLICE;
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
    public String getBookDescription() {
        return "Heals a small amount of health for the target. When used with Extend Time, the Regeneration buff is applied instead, up to level 5. When used on Undead, the spell will deal an equal amount of magic damage.";
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ABJURATION);
    }
}
