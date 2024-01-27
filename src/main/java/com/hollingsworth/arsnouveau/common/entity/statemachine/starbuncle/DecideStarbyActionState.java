package com.hollingsworth.arsnouveau.common.entity.statemachine.starbuncle;

import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.goal.carbuncle.StarbyTransportBehavior;
import org.jetbrains.annotations.Nullable;

public class DecideStarbyActionState extends StarbyState{

    public DecideStarbyActionState(Starbuncle starbuncle, StarbyTransportBehavior behavior) {
        super(starbuncle, behavior);
    }

    @Override
    public @Nullable StarbyState tick() {
        if(!starbuncle.getHeldStack().isEmpty() && behavior.getValidStorePos(starbuncle.getHeldStack()) != null){
            return new DepositItemState(starbuncle, behavior);
        }

        if(starbuncle.getHeldStack().isEmpty() && behavior.getValidTakePos() != null){
            return new TakeItemState(starbuncle, behavior);
        }

        return super.tick();
    }
}
