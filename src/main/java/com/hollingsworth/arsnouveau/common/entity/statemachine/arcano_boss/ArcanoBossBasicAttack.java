package com.hollingsworth.arsnouveau.common.entity.statemachine.arcano_boss;

import com.hollingsworth.arsnouveau.common.entity.ArcanoBoss;
import com.hollingsworth.arsnouveau.common.entity.arcano_boss.ArcanoLob;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class ArcanoBossBasicAttack extends ArcanoState {

    public int cooldown;
    public int castTickDelay;
    public double targetRange;

    private int ticksRunning;
    private boolean firedProjectile;
    private Player target;

    public ArcanoBossBasicAttack(ArcanoBoss arcanoBoss) {
        this(arcanoBoss, 60, 15, 50.0);
    }

    public ArcanoBossBasicAttack(ArcanoBoss arcanoBoss, int cooldown, int castTickDelay, double targetRange) {
        super(arcanoBoss);
        this.cooldown = cooldown;
        this.castTickDelay = castTickDelay;
        this.targetRange = targetRange;
    }

    @Override
    public void onStart() {
        super.onStart();
        target = arcanoBoss.level().getNearestPlayer(arcanoBoss, targetRange);
        arcanoBoss.setArcanoPose(ArcanoBoss.ArcanoBossState.SWING);
    }

    @Override
    public @Nullable ArcanoState tick() {
        ticksRunning++;
        if (target != null && !target.isRemoved()) {
            arcanoBoss.getLookControl().setLookAt(target, 30f, 30f);
        }
        if (!firedProjectile && ticksRunning >= castTickDelay) {
            firedProjectile = true;
            if (target != null && !target.isRemoved()) {
                arcanoBoss.level().addFreshEntity(
                        new ArcanoLob(arcanoBoss.level(), arcanoBoss.position().add(0, 1, 0), target, Direction.Axis.Y)
                );
            }
        }
        if (ticksRunning >= cooldown) {
            return new InitArcanoState(arcanoBoss);
        }
        return null;
    }

    @Override
    public void onEnd() {
        super.onEnd();
        arcanoBoss.setArcanoPose(ArcanoBoss.ArcanoBossState.IDLE);
    }
}