package com.hollingsworth.arsnouveau.common.entity.goal.chimera;

import com.hollingsworth.arsnouveau.common.entity.SummonWolf;
import com.hollingsworth.arsnouveau.common.entity.WildenHunter;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketAnimEntity;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;

public class WildenSummon extends Goal {
    private final WildenHunter entity;
    private int ticksSummoning;
    public final int maxSummoning = 45;

    public WildenSummon(WildenHunter entityIn) {
        this.entity = entityIn;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return entity.isAlive() && entity.getTarget() instanceof Player && entity.summonCooldown <= 0;
    }

    @Override
    public boolean canContinueToUse() {
        return ticksSummoning < maxSummoning;
    }

    @Override
    public void start() {
        super.start();
        Networking.sendToNearbyClient(entity.level, entity, new PacketAnimEntity(entity.getId(), WildenHunter.Animations.HOWL.ordinal()));
        entity.level.playSound(null, entity.blockPosition(), SoundEvents.WOLF_HOWL, SoundSource.HOSTILE, 1.0f, 0.3f);
        ticksSummoning = 0;
        this.entity.summonCooldown = 400;
        this.entity.getEntityData().set(WildenHunter.ANIM_STATE, WildenHunter.Animations.HOWL.name());
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        ticksSummoning++;
        if (ticksSummoning == 20) {
            SummonWolf wolf = new SummonWolf(ModEntities.SUMMON_WOLF.get(), entity.level);
            wolf.ticksLeft = 400;
            wolf.setPos(entity.getRandomX(1), entity.getY(), entity.getRandomZ(1));
            SummonWolf wolf2 = new SummonWolf(ModEntities.SUMMON_WOLF.get(), entity.level);
            wolf2.ticksLeft = 400;
            wolf2.setPos(entity.getRandomX(1), entity.getY(), entity.getRandomZ(1));
            if (entity.getTarget() != null) {
                wolf.setTarget(entity.getTarget());
                wolf2.setTarget(entity.getTarget());
            }
            wolf.setAggressive(true);
            wolf2.setAggressive(true);
            wolf.isWildenSummon = true;
            wolf2.isWildenSummon = true;
            entity.level.addFreshEntity(wolf);
            entity.level.addFreshEntity(wolf2);
        }
        if (ticksSummoning >= maxSummoning) {
            this.entity.getEntityData().set(WildenHunter.ANIM_STATE, WildenHunter.Animations.IDLE.name());
        }
    }
}
