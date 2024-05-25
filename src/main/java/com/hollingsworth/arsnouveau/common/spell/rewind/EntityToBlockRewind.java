package com.hollingsworth.arsnouveau.common.spell.rewind;

import com.hollingsworth.arsnouveau.api.spell.CancelReason;
import com.hollingsworth.arsnouveau.common.entity.EnchantedFallingBlock;
import com.hollingsworth.arsnouveau.common.event.timed.IRewindable;
import com.hollingsworth.arsnouveau.common.event.timed.RewindEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Stack;

public class EntityToBlockRewind implements IRewindCallback{
    public BlockPos pos;
    public BlockState state;
    EnchantedFallingBlock previousEntity;
    Stack<RewindEntityData> data;
    public EntityToBlockRewind(EnchantedFallingBlock previousEntity, BlockPos pos, BlockState state){
        this.pos = pos;
        this.state = state;
        this.previousEntity = previousEntity;
        // always true because mixin
        if(previousEntity instanceof IRewindable rewindable){
            data = rewindable.getMotions();
        }
    }

    @Override
    public void onRewind(RewindEvent event) {
        if(state != previousEntity.level.getBlockState(pos)) {
            return;
        }
        event.entity = EnchantedFallingBlock.fall(previousEntity.level, pos, previousEntity.getOwner(), previousEntity.context, previousEntity.resolver, previousEntity.spellStats);
        // Resurrect the context here because of rewind shenanigans
        if(event.entity instanceof EnchantedFallingBlock enchantedFallingBlock){
            enchantedFallingBlock.context = event.context;
        }
        if(previousEntity.context.getCancelReason() == CancelReason.NEW_CONTEXT){
//            previousEntity.context.setCanceled(false, null);
//            if(previousEntity.context.isDelayed()){
//                DelayedSpellEvent delayedSpellEvent = previousEntity.context.getDelayedSpellEvent();
//                DelayedSpellEvent newDelayedSpellEvent = new DelayedSpellEvent(delayedSpellEvent.duration, delayedSpellEvent.result, delayedSpellEvent.world, previousEntity.resolver);
//                EventQueue.getServerInstance().addEvent(newDelayedSpellEvent);
//                previousEntity.context.delay(newDelayedSpellEvent);
//            }
        }
        if(event.entity instanceof IRewindable rewindable) {
            rewindable.setRewinding(true);
            if (data != null) {
                rewindable.getMotions().addAll(data);
            }
            if (!rewindable.getMotions().isEmpty()) {
                RewindEntityData data = rewindable.getMotions().pop();
                data.onRewind(event);
            }
        }
    }
}
