package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
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

public class EffectHarm extends AbstractEffect {
    public static EffectHarm INSTANCE = new EffectHarm();

    private EffectHarm() {super(GlyphLib.EffectHarmID, "Harm" ); }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        double damage = DAMAGE.get() + AMP_VALUE.get() * spellStats.getAmpMultiplier();
        Entity entity = rayTraceResult.getEntity();
        int time = (int) spellStats.getDurationMultiplier();
        if(time > 0){
            if(entity instanceof LivingEntity)
                applyConfigPotion((LivingEntity) entity, MobEffects.POISON, spellStats);
        }else{
            dealDamage(world, shooter, (float) damage, spellStats, entity, DamageSource.playerAttack(getPlayer(shooter, (ServerLevel) world)));
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
    public boolean wouldSucceed(HitResult rayTraceResult, Level world, LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        return rayTraceResult instanceof EntityHitResult;
    }

    @Override
    public boolean dampenIsAllowed() {
        return true;
    }

    @Override
    public int getDefaultManaCost() {
        return 15;
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.IRON_SWORD;
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
        return "A spell you start with. Damages a target. May be increased by Amplify, or applies the Poison debuff when using Extend Time. Note, multiple Harms without a delay will not apply due to invincibility on hit.";
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ELEMENTAL_EARTH);
    }
}
