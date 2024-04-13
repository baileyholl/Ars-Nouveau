package com.hollingsworth.arsnouveau.common.entity.statemachine.starbuncle;

import com.hollingsworth.arsnouveau.common.block.SourceBerryBush;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.debug.DebugEvent;
import com.hollingsworth.arsnouveau.common.entity.goal.carbuncle.StarbyTransportBehavior;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

import static com.hollingsworth.arsnouveau.common.block.SourceBerryBush.AGE;

public class HarvestBerryState extends TravelToPosState{
    public HarvestBerryState(Starbuncle starbuncle, StarbyTransportBehavior behavior, BlockPos target) {
        super(starbuncle, behavior,target, new DecideStarbyActionState(starbuncle, behavior));
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public StarbyState onDestinationReached() {
        if (level.getBlockState(targetPos).getBlock() instanceof SourceBerryBush) {
            int i = level.getBlockState(targetPos).getValue(AGE);
            boolean flag = i == 3;
            starbuncle.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(targetPos.getX(), targetPos.getY(), targetPos.getZ()));
            int j = 1 + level.random.nextInt(2);
            SourceBerryBush.popResource(level, targetPos, new ItemStack(BlockRegistry.SOURCEBERRY_BUSH, j + (flag ? 1 : 0)));
            level.playSound(null, targetPos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 0.8F + level.random.nextFloat() * 0.4F);
            level.setBlock(targetPos, level.getBlockState(targetPos).setValue(AGE, 1), 2);
            starbuncle.addGoalDebug(this, new DebugEvent("PickedBerry", "Popped berries at " + targetPos.getX() + "," + targetPos.getY() + "," + targetPos.getZ()));
            for(ItemEntity itemEntity : level.getEntitiesOfClass(ItemEntity.class, starbuncle.getBoundingBox().inflate(2.0D))){
                if(itemEntity.getItem().getItem() == BlockRegistry.SOURCEBERRY_BUSH.asItem()){
                    starbuncle.pickUpItem(itemEntity);
                    break;
                }
            }
            starbuncle.getNavigation().stop();
        }
        return super.onDestinationReached();
    }

    @Override
    public boolean isDestinationStillValid(BlockPos pos) {
        return !behavior.isPickupDisabled() && starbuncle.getHeldStack().isEmpty() && level.getBlockState(pos).getBlock() instanceof SourceBerryBush && level.getBlockState(pos).getValue(AGE) > 1;
    }

    public static BlockPos getNearbyManaBerry(Level world, Starbuncle entity) {
        List<BlockPos> posList = new ArrayList<>();
        for (BlockPos blockpos : BlockPos.withinManhattan(entity.blockPosition(), 10, 3, 10)) {
            if (world.getBlockState(blockpos).getBlock() instanceof SourceBerryBush && world.getBlockState(blockpos).getValue(AGE) > 1) {
                posList.add(blockpos.immutable());
            }
        }
        return posList.isEmpty() ? null : posList.get(world.random.nextInt(posList.size()));
    }
}
