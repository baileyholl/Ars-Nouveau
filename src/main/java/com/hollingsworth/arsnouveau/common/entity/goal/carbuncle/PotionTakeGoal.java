package com.hollingsworth.arsnouveau.common.entity.goal.carbuncle;

import com.hollingsworth.arsnouveau.common.block.tile.PotionJarTile;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.debug.DebugEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.alchemy.PotionContents;
import org.jetbrains.annotations.Nullable;

public class PotionTakeGoal extends GoToPosGoal<StarbyPotionBehavior> {
    public PotionTakeGoal(Starbuncle starbuncle, StarbyPotionBehavior behavior) {
        super(starbuncle, behavior, () -> behavior.getHeldPotion().getPotion() == PotionContents.EMPTY);
    }

    @Override
    public void start() {
        super.start();
        starbuncle.goalState = Starbuncle.StarbuncleGoalState.TAKING_ITEM;
    }

    @Override
    public boolean canUse() {
        boolean superCan = super.canUse();
        if(!superCan){
            return false;
        }
        if(behavior.isBedPowered()){
            starbuncle.setBackOff(20);
            starbuncle.addDebugEvent(new DebugEvent("BED_POWERED", "Cannot take potion, bed is powered"));
            return false;
        }
        return true;
    }

    @Nullable
    @Override
    public BlockPos getDestination() {
        return behavior.getJarForTake();
    }

    @Override
    public boolean isDestinationStillValid(BlockPos pos) {
        return behavior.isPositionValidTake(pos);
    }

    @Override
    public boolean onDestinationReached() {
        if(starbuncle.level.getBlockEntity(targetPos) instanceof PotionJarTile jarTile){
            BlockPos pos = behavior.getJarForStorage(jarTile.getData());
            if(pos == null)
                return true;
            if(starbuncle.level.getBlockEntity(pos) instanceof PotionJarTile destJar){
                int maxRoom = destJar.getMaxFill() - destJar.getAmount();
                if(maxRoom <= 0)
                    return true;
                behavior.setHeldPotion(jarTile.getData());
                int takeAmount = Math.min(jarTile.getAmount(), Math.min(maxRoom, 300));
                starbuncle.level.playSound(null, targetPos, SoundEvents.BUCKET_FILL, SoundSource.NEUTRAL, 0.5f, 1.3f);
                jarTile.remove(takeAmount);
                behavior.setAmount(takeAmount);
            }
        }
        return true;
    }
}
