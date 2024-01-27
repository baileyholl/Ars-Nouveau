package com.hollingsworth.arsnouveau.common.entity.statemachine.starbuncle;

import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.goal.carbuncle.StarbyTransportBehavior;

public class DepositItemState extends StarbyState {
    public DepositItemState(Starbuncle starbuncle, StarbyTransportBehavior behavior) {
        super(starbuncle, behavior);
    }
}
