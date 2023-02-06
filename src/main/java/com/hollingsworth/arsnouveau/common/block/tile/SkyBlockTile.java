package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.block.MirrorWeave;
import com.hollingsworth.arsnouveau.common.event.timed.SkyweaveVisibilityEvent;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;


public class SkyBlockTile extends MirrorWeaveTile implements ITickable {

    private boolean showFacade;
    public boolean shouldLight;
    public int previousLight;

    public SkyBlockTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.SKY_BLOCK_TILE.get(), pos, state);
    }

    @Override
    public void tick() {
        if(showFacade)
            return;
        if(!level.isClientSide && level.isDay()){
            shouldLight = true;
            if(getBlockState().getValue(MirrorWeave.LIGHT_LEVEL) != 15){
                previousLight = getBlockState().getValue(MirrorWeave.LIGHT_LEVEL);
                level.setBlockAndUpdate(worldPosition, getBlockState().setValue(MirrorWeave.LIGHT_LEVEL, 15));
            }
        }
        if(!level.isClientSide && !level.isDay()){
            shouldLight = false;
            if(getBlockState().getValue(MirrorWeave.LIGHT_LEVEL) != previousLight){
                level.setBlockAndUpdate(worldPosition, getBlockState().setValue(MirrorWeave.LIGHT_LEVEL, previousLight));
            }
        }
    }


    public void setShowFacade(boolean showFacade){
        if(this.showFacade == showFacade){
            return;
        }

        int ticks = 1;
        for(Direction d : Direction.values()){
            BlockPos offset = getBlockPos().relative(d);
            if(level.getBlockEntity(offset) instanceof SkyBlockTile neighbor){
                if(this.showFacade() == neighbor.showFacade()) {
                    ticks++;
                    EventQueue.getServerInstance().addEvent(new SkyweaveVisibilityEvent(neighbor, ticks, showFacade));
                }
            }
        }
        this.showFacade = showFacade;
        this.updateBlock();
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean("showFacade", showFacade);
        tag.putBoolean("shouldLight", shouldLight);
        tag.putInt("previousLight", previousLight);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        showFacade = pTag.getBoolean("showFacade");
        shouldLight = pTag.getBoolean("shouldLight");
        previousLight = pTag.getInt("previousLight");
    }

    public boolean showFacade() {
        return showFacade;
    }
}
