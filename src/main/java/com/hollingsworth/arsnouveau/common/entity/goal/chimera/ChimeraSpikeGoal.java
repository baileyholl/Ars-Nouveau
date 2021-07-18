package com.hollingsworth.arsnouveau.common.entity.goal.chimera;

import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.entity.EntityChimera;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.projectile.ArrowEntity;

public class ChimeraSpikeGoal extends Goal {

    EntityChimera boss;
    boolean finished;
    int ticks;
    public ChimeraSpikeGoal(EntityChimera boss){
        this.boss = boss;
    }


    @Override
    public void start() {
        finished = false;
        ticks = 0;
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
        boss.setDeltaMovement(0,0,0);
        if(ticks % 20 == 0 ){
            for(int i = 0; i < 50; i++){
                ArrowEntity entity = new ArrowEntity(boss.level, boss);
                entity.shootFromRotation(boss, boss.level.random.nextInt(360), 60 + boss.level.random.nextInt(90), 0.0f, (float) (1.0F + ParticleUtil.inRange(0.0, 0.5)), 1.0F);
                entity.setPos(boss.position.x, boss.position.y + 2, boss.position.z);
                boss.level.addFreshEntity(entity);
            }

        }
        if(ticks >= 120) {
            boss.setDefensiveMode(false);
            finished = true;
            boss.spikeCooldown = 500;
        }
    }

    @Override
    public boolean canContinueToUse() {
        return !finished;
    }

    @Override
    public boolean canUse() {
        return boss.canSpike();
    }
}
