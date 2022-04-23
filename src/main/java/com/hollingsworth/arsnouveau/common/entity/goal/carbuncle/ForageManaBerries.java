package com.hollingsworth.arsnouveau.common.entity.goal.carbuncle;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.block.ManaBerryBush;
import com.hollingsworth.arsnouveau.common.entity.EntityCarbuncle;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static com.hollingsworth.arsnouveau.common.block.ManaBerryBush.AGE;

public class ForageManaBerries extends Goal {
    private final EntityCarbuncle entity;
    private final World world;
    int timeSpent;
    BlockPos pos;

    public ForageManaBerries(EntityCarbuncle entityCarbuncle) {
        this.entity = entityCarbuncle;
        this.world = entity.level;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public void start() {
        super.start();
        timeSpent = 0;
    }

    @Override
    public void stop() {
        super.stop();
        timeSpent = 0;
    }

    @Override
    public boolean canUse() {
        if(entity.isStuck || !entity.getHeldStack().isEmpty() || world.random.nextDouble() > 0.05 || !entity.isValidItem(new ItemStack(BlockRegistry.MANA_BERRY_BUSH)))
            return false;
        this.pos = getNearbyManaBerry();
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
        if(this.pos == null || entity.isStuck) {
            return;
        }

        if(BlockUtil.distanceFrom(entity.position, pos) >= 2.0){
            entity.getNavigation().tryMoveToBlockPos(pos, 1.3);
        }else if(world.getBlockState(pos).getBlock() instanceof ManaBerryBush){
            int i = world.getBlockState(pos).getValue(AGE);
            boolean flag = i == 3;
            entity.lookAt(EntityAnchorArgument.Type.EYES,new Vector3d(this.pos.getX(), this.pos.getY(), this.pos.getZ()));
            int j = 1 + world.random.nextInt(2);
            ManaBerryBush.popResource(world, pos, new ItemStack(BlockRegistry.MANA_BERRY_BUSH, j + (flag ? 1 : 0)));
            world.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundCategory.BLOCKS, 1.0F, 0.8F + world.random.nextFloat() * 0.4F);
            world.setBlock(pos, world.getBlockState(pos).setValue(AGE, 1), 2);
            pos = null;
        }
    }

    @Override
    public boolean canContinueToUse() {
        if(pos == null)
            return false;
        return timeSpent <= 20 * 15 && !entity.isStuck && world.getBlockState(pos).getBlock() instanceof ManaBerryBush && world.getBlockState(pos).getValue(AGE) > 1;
    }

    public BlockPos getNearbyManaBerry(){
        List<BlockPos> posList = new ArrayList<>();
        for(BlockPos blockpos : BlockPos.withinManhattan(entity.blockPosition(), 10, 3, 10)) {
            if (world.getBlockState(blockpos).getBlock() instanceof ManaBerryBush && world.getBlockState(blockpos).getValue(AGE) > 1) {
                posList.add(blockpos.immutable());
            }
        }
        return posList.isEmpty() ? null : posList.get(world.random.nextInt(posList.size()));
    }
}
