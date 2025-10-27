package com.hollingsworth.arsnouveau.common.entity.goal.chimera;

import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.entity.EntityChimeraProjectile;
import com.hollingsworth.arsnouveau.common.entity.WildenChimera;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class ChimeraSpikeGoal extends Goal {

    WildenChimera boss;
    boolean finished;
    int ticks;

    public ChimeraSpikeGoal(WildenChimera boss) {
        this.boss = boss;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }


    @Override
    public void start() {
        finished = false;
        ticks = 0;
    }

    @Override
    public void stop() {
        super.stop();
        tearDownGoal();
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        ticks++;
        boss.setDefensiveMode(true);
        boss.setDeltaMovement(0, 0, 0);
        if (ticks % 20 == 0) {
            spawnAOESpikes(boss);
            for (int i = 0; i < 3; i++) {
                if (this.boss.getTarget() != null) {
                    EntityChimeraProjectile abstractarrowentity = new EntityChimeraProjectile(boss.level);
                    abstractarrowentity.setPos(boss.getX(), boss.getY(), boss.getZ());
                    double d0 = boss.getTarget().getX() - boss.getX();
                    double d1 = boss.getTarget().getY(0.3333333333333333D) - abstractarrowentity.getY();
                    double d2 = boss.getTarget().getZ() - boss.getZ();
                    double d3 = Mth.sqrt((float) (d0 * d0 + d2 * d2));
                    abstractarrowentity.shoot(d0, d1 + d3 * (double) 0.2F, d2, 1.6F, 1.0f);
                    this.boss.level.addFreshEntity(abstractarrowentity);
                }
            }
        }
        if (ticks >= 120) {
            finished = true;
            boss.spikeCooldown = (int) (500 + ParticleUtil.inRange(-100, 100) + boss.getCooldownModifier());
            tearDownGoal();
        }
    }

    public static void spawnAOESpikes(WildenChimera boss) {
        for (int i = 0; i < 100; i++) {
            EntityChimeraProjectile entity = new EntityChimeraProjectile(boss.level);
            entity.shootFromRotation(boss, boss.level.random.nextInt(360), boss.level.random.nextInt(360), 0.0f, (float) (1.0F + ParticleUtil.inRange(0.0, 0.5)), 1.0F);
            entity.setPos(boss.position.x, boss.position.y + 2, boss.position.z);
            boss.level.addFreshEntity(entity);
        }
    }

    public void tearDownGoal() {
        boss.setDefensiveMode(false);
    }

    @Override
    public boolean canContinueToUse() {
        boolean canContinue = !finished && !boss.getPhaseSwapping();
        if (!canContinue) {
            tearDownGoal();
        }
        return canContinue;
    }

    @Override
    public boolean canUse() {
        return boss.canSpike();
    }
}
