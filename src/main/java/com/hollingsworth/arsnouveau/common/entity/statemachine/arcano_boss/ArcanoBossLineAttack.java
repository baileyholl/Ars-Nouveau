package com.hollingsworth.arsnouveau.common.entity.statemachine.arcano_boss;

import com.hollingsworth.arsnouveau.common.entity.ArcanoBoss;
import com.hollingsworth.arsnouveau.common.entity.arcano_boss.ArcanoLob;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class ArcanoBossLineAttack extends ArcanoState {

    public int totalAttacks;
    public int blocksPerSide;
    public int blockSpacing;
    public int delayBetweenAttacks;
    public int castTickDelay;

    private int attacksPerformed;
    private int ticksInCycle;
    private boolean firedThisCycle;
    private boolean fireNorth = true;

    public ArcanoBossLineAttack(ArcanoBoss arcanoBoss) {
        this(arcanoBoss, 4, 16, 2, 30, 15);
    }

    public ArcanoBossLineAttack(ArcanoBoss arcanoBoss, int totalAttacks, int blocksPerSide,
                                int blockSpacing, int delayBetweenAttacks, int castTickDelay) {
        super(arcanoBoss);
        this.totalAttacks = totalAttacks;
        this.blocksPerSide = blocksPerSide;
        this.blockSpacing = blockSpacing;
        this.delayBetweenAttacks = delayBetweenAttacks;
        this.castTickDelay = castTickDelay;
    }

    @Override
    public void onStart() {
        super.onStart();
        arcanoBoss.setArcanoPose(ArcanoBoss.ArcanoBossState.SWING);
    }

    @Override
    public @Nullable ArcanoState tick() {
        ticksInCycle++;

        if (!firedThisCycle && ticksInCycle >= castTickDelay) {
            firedThisCycle = true;
            fireRow(fireNorth ? Direction.NORTH : Direction.SOUTH);
            fireNorth = !fireNorth;
            attacksPerformed++;
            arcanoBoss.setArcanoPose(ArcanoBoss.ArcanoBossState.IDLE);
        }

        if (firedThisCycle && ticksInCycle >= delayBetweenAttacks) {
            if (attacksPerformed >= totalAttacks) {
                return new InitArcanoState(arcanoBoss);
            }
            ticksInCycle = 0;
            firedThisCycle = false;
            arcanoBoss.setArcanoPose(ArcanoBoss.ArcanoBossState.SWING);
        }
        return null;
    }

    private void fireRow(Direction travelDirection) {
        Vec3 bossPos = arcanoBoss.position();
        for (int i = (0 + (fireNorth ? 1 : 0)); i <= blocksPerSide; i += blockSpacing) {
            Vec3 spawnPos = bossPos.add(i, 1, 0);
            ArcanoLob lob = new ArcanoLob(arcanoBoss.level(), spawnPos, Direction.NORTH);
            ArcanoLob lob2 = new ArcanoLob(arcanoBoss.level(), spawnPos, Direction.SOUTH);

            Vec3 spawnPos2 = bossPos.add(-i, 1, 0);
            ArcanoLob lob3 = new ArcanoLob(arcanoBoss.level(), spawnPos2, Direction.NORTH);
            ArcanoLob lob4 = new ArcanoLob(arcanoBoss.level(), spawnPos2, Direction.SOUTH);

            arcanoBoss.level().addFreshEntity(lob);
            arcanoBoss.level().addFreshEntity(lob2);
            arcanoBoss.level().addFreshEntity(lob3);
            arcanoBoss.level().addFreshEntity(lob4);
        }
    }

    @Override
    public void onEnd() {
        super.onEnd();
        arcanoBoss.setArcanoPose(ArcanoBoss.ArcanoBossState.IDLE);
    }
}