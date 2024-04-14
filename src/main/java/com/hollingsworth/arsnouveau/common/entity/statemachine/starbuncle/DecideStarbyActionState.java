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
        if(!starbuncle.isTamed() || starbuncle.isPassenger() || starbuncle.isNoAi()){
            return null;
        }

        BlockPos bedPos = behavior.getBedPos();
        if (bedPos != null && starbuncle.getBedBackoff() <= 0 && behavior.isBedPowered()) {
            return new GoToBedState(starbuncle, behavior, new DecideStarbyActionState(starbuncle, behavior));
        }


        BlockPos storePos = behavior.getValidStorePos(starbuncle.getHeldStack());
        boolean pickupDisabled = behavior.isPickupDisabled();
        if (storePos != null) {
            return new DepositItemState(starbuncle, behavior, storePos);
        }

        if(behavior.takeItemBackoff <= 0) {
            BlockPos takePos = starbuncle.getHeldStack().isEmpty() ? behavior.getValidTakePos() : null;
            if (takePos != null) {
                return new TakeItemState(starbuncle, behavior, takePos);
            }
            behavior.takeItemBackoff = 5 + starbuncle.getRandom().nextInt(20);
        }
        if(!pickupDisabled && starbuncle.getHeldStack().isEmpty()){
            if(behavior.findItemBackoff <= 0) {
                List<ItemEntity> nearbyItems = FindItemState.nearbyItems(starbuncle, behavior);
                if (!nearbyItems.isEmpty()) {
                    return new FindItemState(starbuncle, behavior, nearbyItems);
                }
                behavior.findItemBackoff = 30 + starbuncle.getRandom().nextInt(30);
            }
            if(behavior.berryBackoff <= 0) {
                BlockPos takeBerryPos = HarvestBerryState.getNearbyManaBerry(starbuncle.level, starbuncle);
                if (takeBerryPos != null) {
                    return new HarvestBerryState(starbuncle, behavior, takeBerryPos);
                }
                behavior.berryBackoff = 20 + starbuncle.getRandom().nextInt(20);
            }
        }
        if(bedPos != null  && starbuncle.getBedBackoff() <= 0){
            return new GoToBedState(starbuncle, behavior, new DecideStarbyActionState(starbuncle, behavior));
        }

        return super.tick();
    }
}
