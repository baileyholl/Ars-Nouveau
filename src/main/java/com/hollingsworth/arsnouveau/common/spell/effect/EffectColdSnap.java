package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class EffectColdSnap extends AbstractEffect {

    public static EffectColdSnap INSTANCE = new EffectColdSnap();

    private EffectColdSnap() {
        super(GlyphLib.EffectColdSnapID, "Cold Snap");
    }

    @Override
    public void onResolveEntity(EntityRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        super.onResolveEntity(rayTraceResult, world, shooter, augments, spellContext);
        Entity entity = ((EntityRayTraceResult) rayTraceResult).getEntity();
        if(!(entity instanceof LivingEntity))
            return;
        LivingEntity livingEntity = (LivingEntity) entity;
        Vector3d vec = safelyGetHitPos(rayTraceResult);
        float damage = (float) (DAMAGE.get() + AMP_VALUE.get()*getAmplificationBonus(augments));
        int range = 3 + getBuffCount(augments, AugmentAOE.class);
        int snareSec = POTION_TIME.get() + EXTEND_TIME.get() * getDurationModifier(augments);

        if(!canDamage(livingEntity))
            return;

        damage(vec, world, shooter, augments, damage, snareSec, livingEntity);

        for(Entity e : world.getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(livingEntity.blockPosition().north(range).east(range).above(range),  livingEntity.blockPosition().south(range).west(range).below(range)))){
            if(e.equals(livingEntity) || !(e instanceof LivingEntity))
                continue;
            if(((LivingEntity) e).getEffect(Effects.MOVEMENT_SLOWDOWN) != null || e.isInWaterOrRain()){
                vec = e.position();
                damage(vec, world, shooter, augments, damage, snareSec, (LivingEntity) e);

            }else{
                ((LivingEntity) e).addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 20*snareSec, getAmplificationBonus(augments)));
            }
        }

    }

    public boolean canDamage(LivingEntity livingEntity){
        return livingEntity.isInWaterOrRain() || livingEntity.getEffect(Effects.MOVEMENT_SLOWDOWN) != null;
    }

    public void damage(Vector3d vec, World world, @Nullable LivingEntity shooter, List<AbstractAugment> augments, float damage, int snareTime, LivingEntity livingEntity){
        dealDamage(world, shooter, damage, augments, livingEntity, buildDamageSource(world, shooter).setMagic());
        ((ServerWorld)world).sendParticles(ParticleTypes.SPIT, vec.x, vec.y +0.5, vec.z,50,
                ParticleUtil.inRange(-0.1, 0.1), ParticleUtil.inRange(-0.1, 0.1),ParticleUtil.inRange(-0.1, 0.1), 0.3);
        livingEntity.addEffect(new EffectInstance(ModPotions.SNARE_EFFECT, 20 * snareTime));
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
}