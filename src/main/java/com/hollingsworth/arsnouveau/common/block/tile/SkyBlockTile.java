package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.block.MirrorWeave;
import com.hollingsworth.arsnouveau.common.event.timed.SkyweaveVisibilityEvent;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import org.jetbrains.annotations.NotNull;


public class SkyBlockTile extends MirrorWeaveTile implements ITickable, IDispellable {

    private boolean showFacade;
    private final boolean[] shouldRender = new boolean[6];
    public int previousLight;

    public SkyBlockTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.SKYWEAVE_TILE.get(), pos, state);
    }

    @Override
    public void tick() {
        if (level.isClientSide && !showFacade) {
            for (var direction : Direction.values()) {
                var blockingPos = this.getBlockPos().relative(direction);
                var blockingState = this.level.getBlockState(blockingPos);
                if (!blockingState.canOcclude() && !blockingState.is(BlockRegistry.SKY_WEAVE.get())) {
                    shouldRender[direction.ordinal()] = true;
                    continue;
                }

                var blockingShape = blockingState.getOcclusionShape(this.level, blockingPos);

                shouldRender[direction.ordinal()] = !Shapes.blockOccudes(Shapes.block(), blockingShape, direction);
            }
        }

        if(showFacade && !level.isClientSide) {
            if(getBlockState().getValue(MirrorWeave.LIGHT_LEVEL) != this.mimicState.getLightEmission(level, worldPosition)){
                level.setBlockAndUpdate(worldPosition, getBlockState().setValue(MirrorWeave.LIGHT_LEVEL, this.mimicState.getLightEmission(level, worldPosition)));
            }
            return;
        }
        if(!level.isClientSide && level.isDay()){
            if(getBlockState().getValue(MirrorWeave.LIGHT_LEVEL) != 15){
                previousLight = getBlockState().getValue(MirrorWeave.LIGHT_LEVEL);
                level.setBlockAndUpdate(worldPosition, getBlockState().setValue(MirrorWeave.LIGHT_LEVEL, 15));
            }
        }
        if(!level.isClientSide && !level.isDay()){
            if(getBlockState().getValue(MirrorWeave.LIGHT_LEVEL) != previousLight){
                level.setBlockAndUpdate(worldPosition, getBlockState().setValue(MirrorWeave.LIGHT_LEVEL, this.mimicState.getLightEmission(level, worldPosition)));
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
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(tag, pRegistries);
        tag.putBoolean("showFacade", showFacade);
        tag.putInt("previousLight", previousLight);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        showFacade = pTag.getBoolean("showFacade");
        previousLight = pTag.getInt("previousLight");
    }

    public boolean showFacade() {
        return showFacade;
    }

    @Override
    public boolean onDispel(@NotNull LivingEntity caster) {
        this.setShowFacade(!this.showFacade());
        return true;
    }

    public boolean shouldRenderFace(@NotNull Direction direction) {
        return showFacade || shouldRender[direction.ordinal()];
    }
}
