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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import org.jetbrains.annotations.NotNull;


public class SkyBlockTile extends MirrorWeaveTile implements ITickable, IDispellable {

    private boolean showFacade;
    private final boolean[] shouldRender = {true, true, true, true, true, true};
    public int previousLight;

    public SkyBlockTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.SKYWEAVE_TILE.get(), pos, state);
    }

    @Override
    public void tick() {
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

    public void recalculateFaceVisibility() {
        if (this.level == null || !this.level.isClientSide) {
            return;
        }

        for (var direction : Direction.values()) {
            this.recalculateFaceVisibility(direction);
        }
    }

    public void recalculateFaceVisibility(Direction direction) {
        if (this.level == null || !this.level.isClientSide) {
            return;
        }

        if (direction == null) {
            recalculateFaceVisibility();
            return;
        }

        var blockingPos = this.getBlockPos().relative(direction);
        var blockingState = level.getBlockState(blockingPos);
        if (!blockingState.canOcclude() && !blockingState.is(BlockRegistry.SKY_WEAVE.get())) {
            this.shouldRender[direction.ordinal()] = true;
            return;
        }

        var blockingShape = blockingState.getOcclusionShape(this.level, blockingPos);

        this.shouldRender[direction.ordinal()] = !Shapes.blockOccudes(Shapes.block(), blockingShape, direction);
    }

    @Override
    public void onLoad() {
        super.onLoad();

        Level level = this.getLevel();
        if (level != null && level.isClientSide) {
            this.recalculateFaceVisibility();
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
        this.recalculateFaceVisibility();
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
