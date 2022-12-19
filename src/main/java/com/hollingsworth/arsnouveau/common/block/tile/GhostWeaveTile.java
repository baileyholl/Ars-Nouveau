package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.common.event.timed.GhostweaveVisibilityEvent;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class GhostWeaveTile extends MirrorWeaveTile{

    private boolean invisible;

    public GhostWeaveTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.GHOST_WEAVE_TILE, pos, state);
    }


    public void setVisibility(boolean invisible){
        if(this.invisible == invisible){
            return;
        }

        int ticks = 1;
        for(Direction d : Direction.values()){
            BlockPos offset = getBlockPos().relative(d);
            if(level.getBlockEntity(offset) instanceof GhostWeaveTile neighbor){
                if(this.isInvisible() == neighbor.isInvisible()) {
                    ticks++;
                    EventQueue.getServerInstance().addEvent(new GhostweaveVisibilityEvent(neighbor, ticks, invisible));
                }
            }
        }
        this.invisible = invisible;
        this.updateBlock();
    }

    public boolean isInvisible(){
        return invisible;
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean("invisible", invisible);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        invisible = pTag.getBoolean("invisible");
    }

    @Override
    public BlockState getDefaultBlockState() {
        return BlockRegistry.GHOST_WEAVE.defaultBlockState();
    }
}
