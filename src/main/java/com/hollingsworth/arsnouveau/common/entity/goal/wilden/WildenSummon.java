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
    public boolean canUse() {
        return entity.getTarget() instanceof PlayerEntity && entity.summonCooldown <= 0;
    }



    @Override
    public void start() {
        super.start();
        Networking.sendToNearby(entity.level, entity, new PacketAnimEntity(entity.getId(), WildenHunter.Animations.HOWL.ordinal()));
        entity.level.playSound(null, entity.blockPosition(), SoundEvents.WOLF_HOWL, SoundCategory.HOSTILE, 1.0f, 0.3f);
        SummonWolf wolf = new SummonWolf(ModEntities.SUMMON_WOLF, entity.level);
        wolf.ticksLeft = 400;
        wolf.setPos(entity.getRandomX(1), entity.getY(), entity.getRandomZ(1));
        SummonWolf wolf2 = new SummonWolf(ModEntities.SUMMON_WOLF, entity.level);
        wolf2.ticksLeft = 400;
        wolf2.setPos(entity.getRandomX(1), entity.getY(), entity.getRandomZ(1));
        this.entity.summonCooldown = 400;
        wolf.setTarget(entity.getTarget());
        wolf.setAggressive(true);
        wolf2.setTarget(entity.getTarget());
        wolf2.setAggressive(true);
        wolf.isWildenSummon = true;
        wolf2.isWildenSummon = true;
        entity.level.addFreshEntity(wolf);
        entity.level.addFreshEntity(wolf2);
    }
}
