package com.hollingsworth.arsnouveau.common.entity.statemachine.arcano_boss;

import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.common.block.BasicSpellTurret;
import com.hollingsworth.arsnouveau.common.block.tile.TempSpellTurretTile;
import com.hollingsworth.arsnouveau.common.entity.arcano_boss.ArcanoBoss;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ConjureTurretsState extends ArcanoState {

    public int totalTurrets;
    public int spawnTickDelay;
    public int maxAimTicks;
    public int volleyCooldown;
    public int totalVolleys;

    private Phase phase = Phase.SPAWNING;
    private int phaseTicks;
    private int turretsPlaced;
    private int volleysFired;
    private double groundY;
    private final List<BlockPos> turretPositions = new ArrayList<>();

    public ConjureTurretsState(ArcanoBoss arcanoBoss) {
        super(arcanoBoss);
        this.totalTurrets = 8;
        this.spawnTickDelay = 6;
        this.maxAimTicks = 40;
        this.volleyCooldown = 50;
        this.totalVolleys = 3;
    }

    @Override
    public void onStart() {
        super.onStart();
        arcanoBoss.setArcanoPose(ArcanoBoss.ArcanoBossState.SWING);
        groundY = arcanoBoss.getY();
        arcanoBoss.setNoGravity(true);
    }

    @Override
    public @Nullable ArcanoState tick() {
        phaseTicks++;
        boolean atHoverTarget = arcanoBoss.hoverToward(phase == Phase.DESCENDING ? groundY : groundY + 3.0, 0.15);
        switch (phase) {
            case SPAWNING -> {
                if (phaseTicks >= spawnTickDelay) {
                    phaseTicks = 0;
                    placeTurret();
                    turretsPlaced++;
                    arcanoBoss.setArcanoPose(ArcanoBoss.ArcanoBossState.SWING);
                    if (turretsPlaced >= totalTurrets) {
                        beginAim();
                    }
                }
            }
            case AIMING -> {
                if (phaseTicks >= maxAimTicks || (phaseTicks >= 22 && allAimed())) {
                    fireVolley();
                }
            }
            case COOLDOWN -> {
                if (phaseTicks >= volleyCooldown) {
                    if (volleysFired >= totalVolleys) {
                        beginDescent();
                    } else {
                        beginAim();
                    }
                }
            }
            case DESCENDING -> {
                if (atHoverTarget || phaseTicks >= 60) {
                    return new InitArcanoState(arcanoBoss);
                }
            }
        }
        return null;
    }

    private void beginAim() {
        List<TempSpellTurretTile> turrets = liveTurrets();
        Vec3 targetVec = getTargetVec();
        if (turrets.isEmpty() || targetVec == null) {
            beginDescent();
            return;
        }
        for (TempSpellTurretTile turret : turrets) {
            turret.aimAt(targetVec);
        }
        phase = Phase.AIMING;
        phaseTicks = 0;
        arcanoBoss.setArcanoPose(ArcanoBoss.ArcanoBossState.IDLE);
    }

    private void beginDescent() {
        removeTurrets();
        phase = Phase.DESCENDING;
        phaseTicks = 0;
        arcanoBoss.setArcanoPose(ArcanoBoss.ArcanoBossState.IDLE);
    }

    private void fireVolley() {
        for (TempSpellTurretTile turret : liveTurrets()) {
            turret.shootSpell();
        }
        volleysFired++;
        phase = Phase.COOLDOWN;
        phaseTicks = 0;
        arcanoBoss.setArcanoPose(ArcanoBoss.ArcanoBossState.SWING);
    }

    private boolean allAimed() {
        for (TempSpellTurretTile turret : liveTurrets()) {
            if (!turret.isAimed()) {
                return false;
            }
        }
        return true;
    }

    private void placeTurret() {
        // Place turrets along the back wall of the arena (Z = 30)
        int xPos = 2 + (turretsPlaced * 4);
        int zPos = 30;
        BlockPos pos = new BlockPos(xPos, 1, zPos);
        Level level = arcanoBoss.level();

        if (!level.isEmptyBlock(pos) && !level.getBlockState(pos).is(BlockRegistry.TEMP_SPELL_TURRET.get())) {
            return;
        }
        level.setBlockAndUpdate(pos, BlockRegistry.TEMP_SPELL_TURRET.get().defaultBlockState().setValue(BasicSpellTurret.FACING, Direction.NORTH));
        if (level.getBlockEntity(pos) instanceof TempSpellTurretTile turret) {
            turret.setSpell(new Spell(MethodProjectile.INSTANCE));
            turret.turnRate = 0.35f;
            turret.setDirection(Direction.NORTH);
            turret.configure(turretLifetime(), 0);
            turret.updateBlock();
            turretPositions.add(pos);
        }
    }

    private int turretLifetime() {
        return spawnTickDelay * totalTurrets + totalVolleys * (maxAimTicks + volleyCooldown) + 40;
    }

    private @Nullable Vec3 getTargetVec() {
        LivingEntity target = arcanoBoss.getTarget();
        if (target == null || target.isRemoved()) {
            target = arcanoBoss.level().getNearestPlayer(arcanoBoss, 50.0);
        }
        return target == null ? null : target.getEyePosition();
    }

    private List<TempSpellTurretTile> liveTurrets() {
        List<TempSpellTurretTile> turrets = new ArrayList<>();
        List<BlockPos> stalePositions = new ArrayList<>();
        for (BlockPos pos : turretPositions) {
            if (arcanoBoss.level().getBlockEntity(pos) instanceof TempSpellTurretTile turret) {
                turrets.add(turret);
            } else {
                stalePositions.add(pos);
            }
        }
        turretPositions.removeAll(stalePositions);
        return turrets;
    }

    private void removeTurrets() {
        Level level = arcanoBoss.level();
        for (BlockPos pos : turretPositions) {
            if (level.getBlockState(pos).is(BlockRegistry.TEMP_SPELL_TURRET.get())) {
                level.destroyBlock(pos, false);
            }
        }
        turretPositions.clear();
    }

    @Override
    public void onEnd() {
        super.onEnd();
        arcanoBoss.setArcanoPose(ArcanoBoss.ArcanoBossState.IDLE);
        arcanoBoss.setNoGravity(false);
        removeTurrets();
    }

    private enum Phase {
        SPAWNING,
        AIMING,
        COOLDOWN,
        DESCENDING
    }
}
