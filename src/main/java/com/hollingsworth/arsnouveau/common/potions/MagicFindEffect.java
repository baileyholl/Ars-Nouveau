package com.hollingsworth.arsnouveau.common.potions;

import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.lib.EntityTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class MagicFindEffect extends MobEffect {

    public MagicFindEffect() {
        super(MobEffectCategory.BENEFICIAL, new ParticleColor(30, 200, 200).getColor());
    }

    @Override
    public boolean applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        Level level = pLivingEntity.level;
        if (level.isClientSide || level.getGameTime() % 60 != 0)
            return false;
        for (Entity e : level.getEntities(pLivingEntity, new AABB(pLivingEntity.blockPosition()).inflate(75))) {
            if (e instanceof LivingEntity living && living.getType().is(EntityTags.MAGIC_FIND)) {
                living.addEffect(new MobEffectInstance(MobEffects.GLOWING, 60 * 20));
            }
        }
        return true;
    }
}
