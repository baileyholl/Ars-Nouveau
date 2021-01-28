package com.hollingsworth.arsnouveau.common.entity.goal.wilden;

import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import com.hollingsworth.arsnouveau.common.entity.SummonWolf;
import com.hollingsworth.arsnouveau.common.entity.WildenHunter;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketAnimEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;

public class WildenSummon extends Goal {
    private final WildenHunter entity;

    public WildenSummon(WildenHunter entityIn) {
        this.entity = entityIn;
    }

    @Override
    public boolean shouldExecute() {
        return entity.getAttackTarget() instanceof PlayerEntity && entity.summonCooldown <= 0;
    }



    @Override
    public void startExecuting() {
        super.startExecuting();
        Networking.sendToNearby(entity.world, entity, new PacketAnimEntity(entity.getEntityId(), WildenHunter.Animations.HOWL.ordinal()));
        entity.world.playSound(null, entity.getPosition(), SoundEvents.ENTITY_WOLF_HOWL, SoundCategory.HOSTILE, 1.0f, 0.3f);
        SummonWolf wolf = new SummonWolf(ModEntities.SUMMON_WOLF, entity.world);
        wolf.ticksLeft = 400;
        wolf.setPosition(entity.getPosXRandom(1), entity.getPosY(), entity.getPosZRandom(1));
        SummonWolf wolf2 = new SummonWolf(ModEntities.SUMMON_WOLF, entity.world);
        wolf2.ticksLeft = 400;
        wolf2.setPosition(entity.getPosXRandom(1), entity.getPosY(), entity.getPosZRandom(1));
        this.entity.summonCooldown = 400;
        wolf.setAttackTarget(entity.getAttackTarget());
        wolf.setAggroed(true);
        wolf2.setAttackTarget(entity.getAttackTarget());
        wolf2.setAggroed(true);
        entity.world.addEntity(wolf);
        entity.world.addEntity(wolf2);
    }
}
