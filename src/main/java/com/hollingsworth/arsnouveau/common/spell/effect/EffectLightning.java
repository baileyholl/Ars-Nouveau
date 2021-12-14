package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.entity.LightningEntity;
import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDurationDown;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class EffectLightning extends AbstractEffect {
    public static EffectLightning INSTANCE = new EffectLightning();

    private EffectLightning() {
        super(GlyphLib.EffectLightningID, "Lightning");
    }

    @Override
    public void onResolve(HitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        Vec3 pos = safelyGetHitPos(rayTraceResult);
        LightningEntity lightningBoltEntity = new LightningEntity(ModEntities.LIGHTNING_ENTITY,world);
        lightningBoltEntity.setPos(pos.x(), pos.y(), pos.z());
        lightningBoltEntity.setCause(shooter instanceof ServerPlayer ? (ServerPlayer) shooter : null);
        lightningBoltEntity.amps = (float) spellStats.getAmpMultiplier();
        lightningBoltEntity.extendTimes = (int) spellStats.getDurationMultiplier();
        lightningBoltEntity.ampScalar = AMP_VALUE.get().floatValue();
        lightningBoltEntity.wetBonus = GENERIC_DOUBLE.get().floatValue();
        lightningBoltEntity.setDamage(DAMAGE.get().floatValue());
        (world).addFreshEntity(lightningBoltEntity);
    }

    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addDamageConfig(builder, 5.0);
        addAmpConfig(builder, 3.0);
        addGenericDouble(builder, 2.0, "Bonus damage for wet entities", "wet_bonus");
    }

    @Override
    public int getManaCost() {
        return 100;
    }

    @Override
    public Tier getTier() {
        return Tier.THREE;
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.HEART_OF_THE_SEA;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(
                AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE,
                AugmentExtendTime.INSTANCE, AugmentDurationDown.INSTANCE
        );
    }

    @Override
    public String getBookDescription() {
        return "Summons a lightning bolt at the location. Entities struck will be given the Shocked effect. Shocked causes all additional lightning damage to deal bonus damage, and increases the level of Shocked up to III. Lightning also deals bonus damage to entities that are wet or wearing RF powered items. " +
                "Can be augmented with Amplify, Dampen, and Extend Time.";
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ELEMENTAL_AIR);
    }
}
