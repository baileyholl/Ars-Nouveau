package com.hollingsworth.arsnouveau.common.entity.goal.carbuncle;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.block.SourceBerryBush;
import com.hollingsworth.arsnouveau.common.entity.EntityCarbuncle;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import java.util.EnumSet;
import java.util.Optional;

import static com.hollingsworth.arsnouveau.common.block.SourceBerryBush.AGE;

public class ForageManaBerries extends Goal {
    private final EntityCarbuncle entity;
    private final Level world;
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
        if(entity.isStuck || !entity.getHeldStack().isEmpty() || world.random.nextDouble() > 0.02 || !entity.isValidItem(new ItemStack(BlockRegistry.SOURCEBERRY_BUSH)))
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
        }else if(world.getBlockState(pos).getBlock() instanceof SourceBerryBush){
            int i = world.getBlockState(pos).getValue(AGE);
            boolean flag = i == 3;
            entity.lookAt(EntityAnchorArgument.Anchor.EYES,new Vec3(this.pos.getX(), this.pos.getY(), this.pos.getZ()));
            int j = 1 + world.random.nextInt(2);
            SourceBerryBush.popResource(world, pos, new ItemStack(BlockRegistry.SOURCEBERRY_BUSH, j + (flag ? 1 : 0)));
            world.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 0.8F + world.random.nextFloat() * 0.4F);
            world.setBlock(pos, world.getBlockState(pos).setValue(AGE, 1), 2);
            pos = null;
        }
    }

    @Override
    public boolean canContinueToUse() {
        if(pos == null)
            return false;
      //  entity.getNavigation().tryMoveToBlockPos(pos, 1.3);
       //Path path = entity.getNavigation().createPath(pos, 0)=
        return timeSpent <= 20 * 30 && !entity.isStuck && world.getBlockState(pos).getBlock() instanceof SourceBerryBush && world.getBlockState(pos).getValue(AGE) == 3;
    }

    public BlockPos getNearbyManaBerry(){
        Optional<BlockPos> p = BlockPos.findClosestMatch(entity.blockPosition(), 10,3, (b)-> world.getBlockState(b).getBlock() instanceof SourceBerryBush && world.getBlockState(b).getValue(AGE) == 3);
        return p.orElse(null);
    }
}
