package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.util.FakePlayerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class EffectColdSnap extends AbstractEffect {

    public static EffectColdSnap INSTANCE = new EffectColdSnap();

    private EffectColdSnap() {
        super(GlyphLib.EffectColdSnapID, "Cold Snap");
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        Entity entity = rayTraceResult.getEntity();
        if(!(entity instanceof LivingEntity livingEntity))
            return;
        Vec3 vec = safelyGetHitPos(rayTraceResult);
        float damage = (float) (DAMAGE.get() + AMP_VALUE.get() * spellStats.getAmpMultiplier());
        int range = 3 + spellStats.getBuffCount(AugmentAOE.INSTANCE);
        int snareSec = (int) (POTION_TIME.get() + EXTEND_TIME.get() * spellStats.getDurationMultiplier());

        if(!canDamage(livingEntity))
            return;

        damage(vec, world, shooter, spellStats, damage, snareSec, livingEntity);

        for(Entity e : world.getEntitiesOfClass(LivingEntity.class, new AABB(livingEntity.blockPosition().north(range).east(range).above(range),  livingEntity.blockPosition().south(range).west(range).below(range)))){
            if(e.equals(livingEntity) || !(e instanceof LivingEntity) || e.equals(shooter))
                continue;
            if(canDamage((LivingEntity) e)){
                vec = e.position();
                damage(vec, world, shooter, spellStats, damage, snareSec, (LivingEntity) e);

            }else{
                ((LivingEntity) e).addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20 * snareSec, (int) spellStats.getAmpMultiplier()));
            }
        }
    }

    public boolean canDamage(LivingEntity livingEntity){
        return livingEntity.isInWaterOrRain() || livingEntity.getEffect(MobEffects.MOVEMENT_SLOWDOWN) != null;
    }

    public void damage(Vec3 vec, Level world, @Nullable LivingEntity shooter, SpellStats stats, float damage, int snareTime, LivingEntity livingEntity){
        EntityDamageSource damageSource = new EntityDamageSource("cold", shooter == null ? FakePlayerFactory.getMinecraft((ServerLevel) world) : shooter);
        damageSource.setMagic();
        dealDamage(world, shooter, damage, stats, livingEntity, damageSource);
        ((ServerLevel)world).sendParticles(ParticleTypes.SPIT, vec.x, vec.y +0.5, vec.z,50,
                ParticleUtil.inRange(-0.1, 0.1), ParticleUtil.inRange(-0.1, 0.1),ParticleUtil.inRange(-0.1, 0.1), 0.3);
        livingEntity.addEffect(new MobEffectInstance(ModPotions.SNARE_EFFECT, 20 * snareTime));
    }

    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addDamageConfig(builder, 6.0);
        addAmpConfig(builder, 2.5);
        addPotionConfig(builder, 5);
        addExtendTimeConfig(builder, 1);

    }

    @Override
    public int getManaCost() {
        return 30;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(
                AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE,
                AugmentExtendTime.INSTANCE, AugmentDurationDown.INSTANCE,
                AugmentAOE.INSTANCE,
                AugmentFortune.INSTANCE
        );
    }

    @Override
    public String getBookDescription() {
        return "Snares and causes a burst of damage to an entity that is afflicted by Slowness or is wet. Nearby enemies that are not afflicted by Slow will be slowed. Nearby Enemies afflicted by Slow or wetness will also be hit by Cold Snap. Can be augmented using Amplify, Extend Time, and AOE.";
    }

    @Override
    public Item getCraftingReagent() {
        return ArsNouveauAPI.getInstance().getGlyphItem(EffectFreeze.INSTANCE);
    }

    @Override
    public Tier getTier() {
        return Tier.TWO;
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ELEMENTAL_WATER);
    }
}