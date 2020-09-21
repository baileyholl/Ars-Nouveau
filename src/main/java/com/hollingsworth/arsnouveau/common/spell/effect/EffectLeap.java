package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.List;

public class EffectLeap extends AbstractEffect {
    public EffectLeap(String tag, String description) {
        super(tag, description);
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments) {
//        PlayerEntity playerEntity = (PlayerEntity) e.getEntity();
//        playerEntity.setMotion(playerEntity.getMotion().add(0, 0.005, 0));
//        playerEntity.velocityChanged = true;
//        playerEntity.knockBack(e.getEntityLiving(),4, -playerEntity.getLookVec().x,-playerEntity.getLookVec().z);

    }

    @Override
    public int getManaCost() {
        return 0;
    }
}
