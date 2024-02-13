package com.hollingsworth.arsnouveau.common.entity.statemachine.starbuncle;

import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.goal.carbuncle.StarbyTransportBehavior;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

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
        boolean bedValid = bedPos != null && starbuncle.getBedBackoff() <= 0;
        if(bedValid && behavior.isBedPowered()){
            return new GoToBedState(starbuncle, behavior, new DecideStarbyActionState(starbuncle, behavior));
        }

        BlockPos storePos = behavior.getValidStorePos(starbuncle.getHeldStack());
        if (storePos != null) {
            return new DepositItemState(starbuncle, behavior, storePos);
        }

        BlockPos takePos = starbuncle.getHeldStack().isEmpty() ? behavior.getValidTakePos() : null;
        if (takePos != null) {
            return new TakeItemState(starbuncle, behavior, takePos);
        }

        if(!behavior.isPickupDisabled() && FindItemState.nearbyItems(starbuncle, behavior).size() > 0){
            return new FindItemState(starbuncle, behavior);
        }

        if(bedValid){
            return new GoToBedState(starbuncle, behavior, new DecideStarbyActionState(starbuncle, behavior));
        }

        return super.tick();
    }
}
