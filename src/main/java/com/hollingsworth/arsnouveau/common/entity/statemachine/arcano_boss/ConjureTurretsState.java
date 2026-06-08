package com.hollingsworth.arsnouveau.common.entity.statemachine.arcano_boss;

import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.common.block.BasicSpellTurret;
import com.hollingsworth.arsnouveau.common.block.tile.RotatingTurretTile;
import com.hollingsworth.arsnouveau.common.block.tile.TempSpellTurretTile;
import com.hollingsworth.arsnouveau.common.entity.ArcanoBoss;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ConjureTurretsState extends ArcanoState {

    public int delayBetweenAttacks;
    public int castTickDelay;
    public int totalTurrets;
    public int turretsPlaced;
    public int aimInterval;
    private int ticksInCycle;
    private int ticksSinceAim;
    public final List<BlockPos> turretPositions = new ArrayList<>();

    public ConjureTurretsState(ArcanoBoss arcanoBoss) {
        super(arcanoBoss);
        this.delayBetweenAttacks = 10;
        this.castTickDelay = 10;
        this.totalTurrets = 8;
        this.aimInterval = 10;
    }

    @Override
    public void onStart() {
        super.onStart();
        arcanoBoss.setArcanoPose(ArcanoBoss.ArcanoBossState.SWING);
    }

    @Override
    public @Nullable ArcanoState tick() {
        ticksInCycle++;
        ticksSinceAim++;

        if (ticksInCycle >= castTickDelay && turretsPlaced < totalTurrets) {
            ticksInCycle = 0;
            placeTurret();
            turretsPlaced++;
            arcanoBoss.setArcanoPose(ArcanoBoss.ArcanoBossState.SWING);
        }

        if (ticksSinceAim >= aimInterval) {
            ticksSinceAim = 0;
            aimTurretsAtTarget();
        }

        if (turretsPlaced >= totalTurrets && ticksInCycle > delayBetweenAttacks * 4) {
            return new InitArcanoState(arcanoBoss);
        }

        return null;
    }

    public void placeTurret() {
        // Place turrets along the back wall of the arena (Z = 30)
        int xPos = 2 + (turretsPlaced * 4);
        int zPos = 30;
        BlockPos pos = new BlockPos(xPos, 1, zPos);

        if (arcanoBoss.level().isEmptyBlock(pos)) {
            arcanoBoss.level().setBlockAndUpdate(pos, BlockRegistry.TEMP_SPELL_TURRET.get().defaultBlockState().setValue(BasicSpellTurret.FACING, Direction.NORTH));
            if (arcanoBoss.level().getBlockEntity(pos) instanceof TempSpellTurretTile turret) {
                Spell spell = new Spell(MethodProjectile.INSTANCE);
                turret.setSpell(spell);
                turret.setDirection(Direction.NORTH);
                turret.configure(200, 20);
                turretPositions.add(pos);
                aimTurretsAtTarget();
            }
        }
    }

    public void aimTurretsAtTarget() {
        LivingEntity target = arcanoBoss.getTarget();
        if (target == null) return;
        BlockPos targetPos = target.blockPosition();
        for (BlockPos turretPos : turretPositions) {
            if (arcanoBoss.level().getBlockEntity(turretPos) instanceof RotatingTurretTile turret) {
                aimTurret(turret, targetPos);
            }
        }
    }

    private static void aimTurret(RotatingTurretTile turret, BlockPos targetPos) {
        Vec3 thisVec = Vec3.atCenterOf(turret.getBlockPos());
        Vec3 blockVec = Vec3.atCenterOf(targetPos);
        Vec3 diffVec = blockVec.subtract(thisVec);

        Vec3 diffVec2D = new Vec3(diffVec.x, diffVec.z, 0);
        Vec3 rotVec = new Vec3(0, 1, 0);
        float angle = (float) (RotatingTurretTile.angleBetween(rotVec, diffVec2D) / Math.PI * 180.0f);
        if (blockVec.x < thisVec.x) {
            angle = -angle;
        }
        turret.neededRotationX = angle + 90f;

        rotVec = new Vec3(diffVec.x, 0, diffVec.z);
        angle = (float) (RotatingTurretTile.angleBetween(diffVec, rotVec) * 180F / (float) Math.PI);
        if (blockVec.y < thisVec.y) {
            angle = -angle;
        }
        turret.neededRotationY = angle;

        turret.updateBlock();
    }

    @Override
    public void onEnd() {
        super.onEnd();
        arcanoBoss.setArcanoPose(ArcanoBoss.ArcanoBossState.IDLE);
        turretPositions.clear();
    }
}