package com.hollingsworth.arsnouveau.common.entity.goal.whirlisprig;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.tile.WhirlisprigTile;
import com.hollingsworth.arsnouveau.common.entity.EntityFlyingItem;
import com.hollingsworth.arsnouveau.common.entity.Whirlisprig;
import com.hollingsworth.arsnouveau.common.entity.goal.DistanceRestrictedGoal;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.world.entity.ai.goal.Goal.Flag;

public class InspectPlantGoal extends DistanceRestrictedGoal {
    Whirlisprig entity;
    BlockPos pos;
    int timeLooking;
    int timePerforming;

    public InspectPlantGoal(Whirlisprig entity, Supplier<BlockPos> from, int maxDistanceFrom) {
        super(from, maxDistanceFrom);
        this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
        this.entity = entity;
    }

    public boolean hasVisibleSide(BlockPos pos) {
        for (Direction d : Direction.values()) {
            if (entity.level.getBlockState(pos.relative(d)).getMaterial() == Material.AIR)
                return true;
        }
        return false;
    }

    @Override
    public void tick() {
        if (this.pos == null) {
            return;
        }
        timePerforming--;
        if (BlockUtil.distanceFrom(entity.blockPosition(), pos) > 1.5) {
            entity.getNavigation().moveTo(this.pos.getX(), this.pos.getY(), this.pos.getZ(), 1.2);
        } else {
            ServerLevel world = (ServerLevel) entity.level;
            entity.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(this.pos.getX(), this.pos.getY(), this.pos.getZ()));
            if (world.random.nextInt(20) == 0)
                world.sendParticles(ParticleTypes.HEART, this.pos.getX() + 0.5, this.pos.getY() + 1.1, this.pos.getZ() + 0.5, 1, ParticleUtil.inRange(-0.2, 0.2), 0, ParticleUtil.inRange(-0.2, 0.2), 0.01);
            this.timeLooking--;
        }
    }

    @Override
    public boolean canContinueToUse() {
        return pos != null && timeLooking > 0 && timePerforming > 0;
    }

    @Override
    public boolean canUse() {
        return (entity.timeSinceGen > 300 && entity.getTile() != null) ||
                (entity.getCommandSenderWorld().random.nextInt(100) <= 2 && entity.level.getGameTime() % 10 == 0 && entity.getTile() != null);
    }

    @Override
    public void start() {
        int range = 4;
        List<BlockPos> list = new ArrayList<>();
        BlockPos.betweenClosedStream(entity.blockPosition().offset(range, range, range), entity.blockPosition().offset(-range, -range, -range)).forEach(bp -> {
            if (WhirlisprigTile.getScore(entity.level.getBlockState(bp)) > 0 && hasVisibleSide(bp) && isInRange(bp)) {
                list.add(bp.immutable());
            }
        });
        if (list.isEmpty())
            return;
        pos = list.get(entity.level.random.nextInt(list.size()));
        this.timeLooking = 120;
        this.timePerforming = 240;
        EntityFlyingItem flyingItem = new EntityFlyingItem(entity.level, pos, entity.flowerPos, 50, 255, 40);
        flyingItem.getEntityData().set(EntityFlyingItem.HELD_ITEM, entity.level.getBlockState(pos).getBlock().asItem().getDefaultInstance());
        entity.level.addFreshEntity(flyingItem);
        entity.timeSinceGen = 0;
        entity.getTile().addProgress();
    }
}
