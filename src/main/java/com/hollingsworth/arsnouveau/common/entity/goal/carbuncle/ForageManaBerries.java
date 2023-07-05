package com.hollingsworth.arsnouveau.common.entity.goal.carbuncle;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.block.SourceBerryBush;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.debug.DebugEvent;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static com.hollingsworth.arsnouveau.common.block.SourceBerryBush.AGE;

public class ForageManaBerries extends Goal {
    private final Starbuncle entity;
    private final Level world;
    int timeSpent;
    BlockPos pos;
    StarbyTransportBehavior behavior;

    public ForageManaBerries(Starbuncle starbuncle, StarbyTransportBehavior transportBehavior) {
        this.entity = starbuncle;
        this.world = entity.level;
        this.setFlags(EnumSet.of(Flag.MOVE));
        this.behavior = transportBehavior;
    }

    @Override
    public void start() {
        super.start();
        timeSpent = 0;
        entity.goalState = Starbuncle.StarbuncleGoalState.FORAGING;
    }

    @Override
    public void stop() {
        super.stop();
        timeSpent = 0;
        entity.goalState = Starbuncle.StarbuncleGoalState.NONE;
    }

    @Override
    public boolean canUse() {
        if (behavior.isPickupDisabled() || !entity.getHeldStack().isEmpty() || world.random.nextDouble() > 0.05 || behavior.getValidStorePos(new ItemStack(BlockRegistry.SOURCEBERRY_BUSH)) == null)
            return false;
        this.pos = getNearbyManaBerry();
        if(pos == null){
            entity.addGoalDebug(this, new DebugEvent("NoBerries", "No Berries Nearby"));
            return false;
        }
        if(behavior.isBedPowered()){
            entity.addGoalDebug(this, new DebugEvent("Bed Powered", "Bed powered, no berry pickin"));
            return false;
        }
        return pos != null;
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        timeSpent++;
        if (this.pos == null) {
            return;
        }

        if (BlockUtil.distanceFrom(entity.position, pos) >= 2.0) {
            entity.getNavigation().tryMoveToBlockPos(pos, 1.2d);
            entity.addGoalDebug(this, new DebugEvent("PathTo", "Moving to berry " + pos.toString()));
        } else if (world.getBlockState(pos).getBlock() instanceof SourceBerryBush) {
            int i = world.getBlockState(pos).getValue(AGE);
            boolean flag = i == 3;
            entity.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(this.pos.getX(), this.pos.getY(), this.pos.getZ()));
            int j = 1 + world.random.nextInt(2);
            SourceBerryBush.popResource(world, pos, new ItemStack(BlockRegistry.SOURCEBERRY_BUSH, j + (flag ? 1 : 0)));
            world.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 0.8F + world.random.nextFloat() * 0.4F);
            world.setBlock(pos, world.getBlockState(pos).setValue(AGE, 1), 2);
            entity.addGoalDebug(this, new DebugEvent("PickedBerry", "Popped berries at " + pos.getX() + "," + pos.getY() + "," + pos.getZ()));
            pos = null;
        }
    }

    @Override
    public boolean canContinueToUse() {
        if (pos == null || behavior.isPickupDisabled())
            return false;

        return timeSpent <= 20 * 15 && world.getBlockState(pos).getBlock() instanceof SourceBerryBush && world.getBlockState(pos).getValue(AGE) > 1;
    }

    public BlockPos getNearbyManaBerry() {
        List<BlockPos> posList = new ArrayList<>();
        for (BlockPos blockpos : BlockPos.withinManhattan(entity.blockPosition(), 10, 3, 10)) {
            if (world.getBlockState(blockpos).getBlock() instanceof SourceBerryBush && world.getBlockState(blockpos).getValue(AGE) > 1) {
                posList.add(blockpos.immutable());
            }
        }
        return posList.isEmpty() ? null : posList.get(world.random.nextInt(posList.size()));
    }
}
