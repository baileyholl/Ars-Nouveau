package com.hollingsworth.arsnouveau.common.entity.goal.carbuncle;

import com.hollingsworth.arsnouveau.api.potion.PotionData;
import com.hollingsworth.arsnouveau.common.block.tile.PotionJarTile;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import org.jetbrains.annotations.Nullable;

public class PotionStoreGoal extends GoToPosGoal<StarbyPotionBehavior>{
    public PotionStoreGoal(Starbuncle starbuncle, StarbyPotionBehavior behavior) {
        super(starbuncle, behavior, () -> !behavior.getHeldPotion().isEmpty());
    }

    @Nullable
    @Override
    public BlockPos getDestination() {
        return behavior.getJarForStorage(behavior.getHeldPotion());
    }

    @Override
    public boolean isDestinationStillValid(BlockPos pos) {
        return behavior.isPositionValidStore(pos, behavior.getHeldPotion());
    }

    @Override
    public boolean onDestinationReached() {
        if(starbuncle.level.getBlockEntity(targetPos) instanceof PotionJarTile jarTile){
            int room = jarTile.getMaxFill() - jarTile.getAmount();
            int diff = Math.min(room, behavior.getAmount());
            jarTile.add(behavior.getHeldPotion(), diff);
            behavior.setHeldPotion(new PotionData());
            starbuncle.level.playSound(null, targetPos, SoundEvents.BUCKET_EMPTY, SoundSource.NEUTRAL, 0.5f, 1.3f);
            behavior.setAmount(behavior.getAmount() - diff);
        }
        return true;
    }
}
