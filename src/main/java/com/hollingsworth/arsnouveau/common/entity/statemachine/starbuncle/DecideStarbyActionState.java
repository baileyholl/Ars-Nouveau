package com.hollingsworth.arsnouveau.common.entity.statemachine.starbuncle;

import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.goal.carbuncle.StarbyTransportBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DecideStarbyActionState extends StarbyState{

    public DecideStarbyActionState(Starbuncle starbuncle, StarbyTransportBehavior behavior) {
        super(starbuncle, behavior);
    }

    @Override
    public @Nullable StarbyState tick() {
        if(!starbuncle.isTamed()){
            return null;
        }

        BlockPos bedPos = behavior.getBedPos();
        if(bedPos != null  && behavior.isBedPowered()){
            return new GoToBedState(starbuncle, behavior, new DecideStarbyActionState(starbuncle, behavior));
        }

        BlockPos storePos = behavior.getValidStorePos(starbuncle.getHeldStack());
        boolean pickupDisabled = behavior.isPickupDisabled();
        if (storePos != null) {
            return new DepositItemState(starbuncle, behavior, storePos);
        }

        BlockPos takePos = starbuncle.getHeldStack().isEmpty() ? behavior.getValidTakePos() : null;
        if (takePos != null) {
            return new TakeItemState(starbuncle, behavior, takePos);
        }

        if(!pickupDisabled && starbuncle.getHeldStack().isEmpty()){
            List< ItemEntity> nearbyItems = FindItemState.nearbyItems(starbuncle, behavior);
            if(!nearbyItems.isEmpty()) {
                return new FindItemState(starbuncle, behavior, nearbyItems);
            }

            BlockPos takeBerryPos = HarvestBerryState.getNearbyManaBerry(starbuncle.level, starbuncle);
            if (takeBerryPos != null) {
                return new HarvestBerryState(starbuncle, behavior, takeBerryPos);
            }
        }
        if(bedPos != null  && starbuncle.getBedBackoff() <= 0){
            return new GoToBedState(starbuncle, behavior, new DecideStarbyActionState(starbuncle, behavior));
        }

        return super.tick();
    }
}
