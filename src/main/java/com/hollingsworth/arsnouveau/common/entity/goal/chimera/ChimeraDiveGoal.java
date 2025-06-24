package com.hollingsworth.arsnouveau.common.entity.goal.chimera;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.advancement.ANCriteriaTriggers;
import com.hollingsworth.arsnouveau.common.entity.WildenChimera;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketAnimEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class ChimeraDiveGoal extends Goal {
    WildenChimera boss;
    boolean finished;
    int ticksFlying;
    boolean isDiving;
    BlockPos divePos;
    BlockPos startPos;
    BlockPos hoverPos;

    public ChimeraDiveGoal(WildenChimera boss) {
        this.boss = boss;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public void start() {
        super.start();
        finished = false;
        divePos = null;
        ticksFlying = 0;
        isDiving = false;

        startPos = boss.blockPosition();
        hoverPos = startPos.above(8);

        boss.setFlying(true);
        boss.getNavigation().setCanFloat(true);
        Networking.sendToNearbyClient(boss.level, boss, new PacketAnimEntity(boss.getId(), WildenChimera.Animations.FLYING.ordinal()));
    }

    @Override
    public void stop() {
        super.stop();
        tearDownNavigation();
    }

    @Override
    public void tick() {
        super.tick();
        ticksFlying++;
        if (ticksFlying < 60) {
            boss.flyingNavigator.moveTo(hoverPos.getX(), hoverPos.getY(), hoverPos.getZ(), 1.0f);
            boss.setDeltaMovement(boss.getDeltaMovement().add(0, 0.005, 0));
            if (boss.getTarget() != null) {
                WildenChimera.faceBlock(boss.getTarget().blockPosition(), boss);
            }
        }

        if (ticksFlying > 60) {
            boss.setDiving(true);
            isDiving = true;
            boss.diving = true;

            if (divePos == null) {
                if (boss.getTarget() != null) {
                    divePos = boss.getTarget().blockPosition().below();
                    // Seek the ground below
                    for (int i = 1; i < 50; i++) {
                        if (boss.level.getBlockState(divePos).isAir()) {
                            divePos = divePos.below();
                        } else {
                            break;
                        }
                    }
                }
            }
            if (divePos != null) {
                boss.flyingNavigator.moveTo(divePos.getX() + 0.5, divePos.getY(), divePos.getZ(), 4f);
                boss.orbitOffset = new Vec3(divePos.getX() + 0.5, divePos.getY(), divePos.getZ() + 0.5);
            }
        }
        if ((isDiving && (boss.onGround() || BlockUtil.distanceFrom(boss.position, divePos) <= 1.0d) || (boss.orbitOffset != null && BlockUtil.distanceFrom(boss.position, boss.orbitOffset) <= 1.7d))) {
            makeExplosion();
            endGoal();
            return;
        }
        if (isDiving && (boss.isInWall() || boss.horizontalCollision || boss.verticalCollision)) {
            makeExplosion();
            endGoal();
            return;
        }
        if (isDiving && divePos == null && boss.getTarget() == null) {
            endGoal();
        }
    }

    public void endGoal() {
        tearDownNavigation();
        boss.diveCooldown = (int) (300 + ParticleUtil.inRange(-100, 100) + boss.getCooldownModifier());
        boss.diving = false;
        finished = true;
        ANCriteriaTriggers.rewardNearbyPlayers(ANCriteriaTriggers.CHIMERA_EXPLOSION.get(), (ServerLevel) boss.level, BlockPos.containing(boss.position().x, boss.position.y, boss.position.z), 10);
        for (int i = 0; i < 40; i++) {
            if (!boss.level.getBlockState(boss.getOnPos().below(i)).isAir()) {
                boss.setPos(boss.getX(), boss.getY() - i, boss.getZ());
                boss.setOnGround(true);
                return;
            }
        }
    }

    public void makeExplosion() {
        //TODO: restore destructive chimera config
        boss.level.explode(boss, boss.getX() + 0.5, boss.getY(), boss.getZ() + 0.5, 4.5f, Level.ExplosionInteraction.MOB);
        if (boss.hasSpikes()) {
            ChimeraSpikeGoal.spawnAOESpikes(boss);
        }
    }

    public void tearDownNavigation() {
        boss.getNavigation().setCanFloat(false);
        boss.getNavigation().stop();
        boss.setFlying(false);
        boss.setDiving(false);
        boss.setNoGravity(false);
        boss.setDeltaMovement(0, 0, 0);
    }

    @Override
    public boolean canContinueToUse() {
        boolean canContinue = !finished && !boss.getPhaseSwapping();
        if (!canContinue) {
            tearDownNavigation();
        }
        return canContinue;
    }

    @Override
    public boolean canUse() {
        return boss.canDive() && boss.getTarget() != null;
    }
}
