package com.hollingsworth.arsnouveau.common.entity.goal.carbuncle;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.block.ManaBerryBush;
import com.hollingsworth.arsnouveau.common.entity.EntityCarbuncle;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.EnumSet;
import java.util.Optional;

import static com.hollingsworth.arsnouveau.common.block.ManaBerryBush.AGE;

public class ForageManaBerries extends Goal {
    private final EntityCarbuncle entity;
    private final World world;
    int timeSpent;
    BlockPos pos;

    public ForageManaBerries(EntityCarbuncle entityCarbuncle) {
        this.entity = entityCarbuncle;
        this.world = entity.world;
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public void startExecuting() {
        super.startExecuting();
        timeSpent = 0;
    }

    @Override
    public void resetTask() {
        super.resetTask();
        timeSpent = 0;
    }

    @Override
    public boolean shouldExecute() {
        if(entity.isStuck || world.rand.nextDouble() > 0.02 || entity.getValidStorePos(new ItemStack(BlockRegistry.MANA_BERRY_BUSH.asItem())) == null)
            return false;
        this.pos = getNearbyManaBerry();
        return pos != null;
    }

    @Override
    public void tick() {
        super.tick();
        timeSpent++;
        if(this.pos == null || entity.isStuck) {
            return;
        }

        if(BlockUtil.distanceFrom(entity.getPosition(), pos) > 1.2){
            entity.getNavigator().tryMoveToXYZ(this.pos.getX(), this.pos.getY(), this.pos.getZ(), 1.2);
        }else if(world.getBlockState(pos).getBlock() instanceof ManaBerryBush){
            int i = world.getBlockState(pos).get(AGE);
            boolean flag = i == 3;
            entity.lookAt(EntityAnchorArgument.Type.EYES,new Vector3d(this.pos.getX(), this.pos.getY(), this.pos.getZ()));
            int j = 1 + world.rand.nextInt(2);
            ManaBerryBush.spawnAsEntity(world, pos, new ItemStack(BlockRegistry.MANA_BERRY_BUSH, j + (flag ? 1 : 0)));
            world.playSound(null, pos, SoundEvents.ITEM_SWEET_BERRIES_PICK_FROM_BUSH, SoundCategory.BLOCKS, 1.0F, 0.8F + world.rand.nextFloat() * 0.4F);
            world.setBlockState(pos, world.getBlockState(pos).with(AGE, Integer.valueOf(1)), 2);
            pos = null;
        }
    }

    @Override
    public boolean shouldContinueExecuting() {
        if(pos == null)
            return false;
        Path path = entity.getNavigator().getPathToPos(pos, 0);
        return timeSpent <= 20 * 30 && !entity.isStuck && world.getBlockState(pos).getBlock() instanceof ManaBerryBush && path != null && path.reachesTarget();
    }

    public BlockPos getNearbyManaBerry(){
        Optional<BlockPos> p = BlockPos.getClosestMatchingPosition(entity.getPosition(), 10,3, (b)-> world.getBlockState(b).getBlock() instanceof ManaBerryBush && world.getBlockState(b).get(AGE) == 3);
        return p.orElse(null);
    }
}
