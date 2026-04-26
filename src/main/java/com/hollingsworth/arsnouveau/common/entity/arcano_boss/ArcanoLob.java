package com.hollingsworth.arsnouveau.common.entity.arcano_boss;

import com.google.common.collect.Lists;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.EmptyCaster;
import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ArcanoLob extends EntityProjectileSpell {

    private static final double SPEED = 0.15;
    @Nullable
    private Entity finalTarget;
    @Nullable
    private Direction currentMoveDirection;
    private int flightSteps;
    private double targetDeltaX;
    private double targetDeltaY;
    private double targetDeltaZ;
    public ArcanoLob(EntityType<? extends EntityProjectileSpell> entityType, Level world) {
        super(ModEntities.ARCANO_LOB.get(), world);
    }

    public ArcanoLob(Level world, Vec3 position, Entity targetEntity, Direction.Axis axis) {
        super(ModEntities.ARCANO_LOB.get(), world);
        setPos(position);
        this.finalTarget = targetEntity;
        this.currentMoveDirection = Direction.UP;
        this.selectNextMoveDirection(axis);
        setResolver(new SpellResolver(new SpellContext(level, new Spell(), null, new EmptyCaster())));

    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void tickNextPosition() {
        this.targetDeltaX = Mth.clamp(this.targetDeltaX * 1.025, -1.0, 1.0);
        this.targetDeltaY = Mth.clamp(this.targetDeltaY * 1.025, -1.0, 1.0);
        this.targetDeltaZ = Mth.clamp(this.targetDeltaZ * 1.025, -1.0, 1.0);
        Vec3 vec3 = this.getDeltaMovement();
        this.setDeltaMovement(vec3.add((this.targetDeltaX - vec3.x) * 0.2, (this.targetDeltaY - vec3.y) * 0.2, (this.targetDeltaZ - vec3.z) * 0.2));

        Vec3 vec31 = this.getDeltaMovement();
        this.setPos(this.getX() + vec31.x, this.getY() + vec31.y, this.getZ() + vec31.z);
        ProjectileUtil.rotateTowardsMovement(this, 0.5F);
        if (this.finalTarget != null && !this.finalTarget.isRemoved()) {
            if (this.flightSteps > 0) {
                this.flightSteps--;
                if (this.flightSteps == 0) {
                    this.selectNextMoveDirection(this.currentMoveDirection == null ? null : this.currentMoveDirection.getAxis());
                }
            }

            if (this.currentMoveDirection != null) {
                BlockPos blockpos = this.blockPosition();
                Direction.Axis direction$axis = this.currentMoveDirection.getAxis();
                if (this.level().loadedAndEntityCanStandOn(blockpos.relative(this.currentMoveDirection), this)) {
                    this.selectNextMoveDirection(direction$axis);
                } else {
                    BlockPos blockpos1 = this.finalTarget.blockPosition();
                    if (direction$axis == Direction.Axis.X && blockpos.getX() == blockpos1.getX()
                            || direction$axis == Direction.Axis.Z && blockpos.getZ() == blockpos1.getZ()
                            || direction$axis == Direction.Axis.Y && blockpos.getY() == blockpos1.getY()) {
                        this.selectNextMoveDirection(direction$axis);
                    }
                }
            }
        }
    }

    @Nullable
    private Direction getMoveDirection() {
        return this.currentMoveDirection;
    }

    private void setMoveDirection(@Nullable Direction direction) {
        this.currentMoveDirection = direction;
    }

    private void selectNextMoveDirection(@Nullable Direction.Axis axis) {
        double d0 = 0.5;
        BlockPos blockpos;
        if (this.finalTarget == null) {
            blockpos = this.blockPosition().below();
        } else {
            d0 = (double)this.finalTarget.getBbHeight() * 0.5;
            blockpos = BlockPos.containing(this.finalTarget.getX(), this.finalTarget.getY() + d0, this.finalTarget.getZ());
        }

        double d1 = (double)blockpos.getX() + 0.5;
        double d2 = (double)blockpos.getY() + d0;
        double d3 = (double)blockpos.getZ() + 0.5;
        Direction direction = null;
        if (!blockpos.closerToCenterThan(this.position(), 2.0)) {
            BlockPos blockpos1 = this.blockPosition();
            List<Direction> list = Lists.newArrayList();
            if (axis != Direction.Axis.X) {
                if (blockpos1.getX() < blockpos.getX() && this.level().isEmptyBlock(blockpos1.east())) {
                    list.add(Direction.EAST);
                } else if (blockpos1.getX() > blockpos.getX() && this.level().isEmptyBlock(blockpos1.west())) {
                    list.add(Direction.WEST);
                }
            }

            if (axis != Direction.Axis.Y) {
                if (blockpos1.getY() < blockpos.getY() && this.level().isEmptyBlock(blockpos1.above())) {
                    list.add(Direction.UP);
                } else if (blockpos1.getY() > blockpos.getY() && this.level().isEmptyBlock(blockpos1.below())) {
                    list.add(Direction.DOWN);
                }
            }

            if (axis != Direction.Axis.Z) {
                if (blockpos1.getZ() < blockpos.getZ() && this.level().isEmptyBlock(blockpos1.south())) {
                    list.add(Direction.SOUTH);
                } else if (blockpos1.getZ() > blockpos.getZ() && this.level().isEmptyBlock(blockpos1.north())) {
                    list.add(Direction.NORTH);
                }
            }

            direction = Direction.getRandom(this.random);
            if (list.isEmpty()) {
                for (int i = 5; !this.level().isEmptyBlock(blockpos1.relative(direction)) && i > 0; i--) {
                    direction = Direction.getRandom(this.random);
                }
            } else {
                direction = list.get(this.random.nextInt(list.size()));
            }

            d1 = this.getX() + (double)direction.getStepX();
            d2 = this.getY() + (double)direction.getStepY();
            d3 = this.getZ() + (double)direction.getStepZ();
        }

        this.setMoveDirection(direction);
        double d6 = d1 - this.getX();
        double d7 = d2 - this.getY();
        double d4 = d3 - this.getZ();
        double d5 = Math.sqrt(d6 * d6 + d7 * d7 + d4 * d4);
        if (d5 == 0.0) {
            this.targetDeltaX = 0.0;
            this.targetDeltaY = 0.0;
            this.targetDeltaZ = 0.0;
        } else {
            this.targetDeltaX = d6 / d5 * 0.15;
            this.targetDeltaY = d7 / d5 * 0.15;
            this.targetDeltaZ = d4 / d5 * 0.15;
        }

        this.hasImpulse = true;
        this.flightSteps = 10 + this.random.nextInt(5) * 10;
    }


    @Override
    public int getExpirationTime() {
        return 800;
    }
}
