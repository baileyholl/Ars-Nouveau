package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.api.event.InvalidateMirrorweaveRender;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.block.MirrorWeave;
import com.hollingsworth.arsnouveau.common.block.SkyWeave;
import com.hollingsworth.arsnouveau.common.event.timed.SkyweaveVisibilityEvent;
import com.hollingsworth.arsnouveau.common.light.SkyLightOverrider;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;


public class SkyBlockTile extends MirrorWeaveTile implements ITickable {

    public int previousLight;
    private boolean hadFirstTick = false;

    public SkyBlockTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.SKYWEAVE_TILE.get(), pos, state);
    }

    @Override
    public void tick() {
        if (!hadFirstTick) {
            hadFirstTick = true;
            level.getLightEngine().checkBlock(worldPosition);
        }
        if (showFacade() && !level.isClientSide) {
            if (getBlockState().getValue(MirrorWeave.LIGHT_LEVEL) != this.mimicState.getLightEmission(level, worldPosition)) {
                level.setBlockAndUpdate(worldPosition, getBlockState().setValue(MirrorWeave.LIGHT_LEVEL, this.mimicState.getLightEmission(level, worldPosition)));
            }
            return;
        }
        if (!level.isClientSide && level.isDay()) {
            if (getBlockState().getValue(MirrorWeave.LIGHT_LEVEL) != 15) {
                previousLight = getBlockState().getValue(MirrorWeave.LIGHT_LEVEL);
                level.setBlockAndUpdate(worldPosition, getBlockState().setValue(MirrorWeave.LIGHT_LEVEL, 15));
            }
        }
        if (!level.isClientSide && !level.isDay()) {
            if (getBlockState().getValue(MirrorWeave.LIGHT_LEVEL) != previousLight) {
                level.setBlockAndUpdate(worldPosition, getBlockState().setValue(MirrorWeave.LIGHT_LEVEL, this.mimicState.getLightEmission(level, worldPosition)));
            }
        }
    }

    public void setShowFacade(boolean showFacade) {
        if (this.showFacade() == showFacade) {
            return;
        }

        int ticks = 1;
        for (Direction d : Direction.values()) {
            BlockPos offset = getBlockPos().relative(d);
            if (level.getBlockEntity(offset) instanceof SkyBlockTile neighbor) {
                if (this.showFacade() == neighbor.showFacade()) {
                    ticks++;
                    EventQueue.getServerInstance().addEvent(new SkyweaveVisibilityEvent(neighbor, ticks, showFacade));
                }
            }
        }
        EventQueue.getServerInstance().addEvent(new InvalidateMirrorweaveRender(getBlockPos(), level));
        level.setBlockAndUpdate(worldPosition, getBlockState().setValue(SkyWeave.SHOW_FACADE, showFacade));
    }

    @Override
    public BlockState getDefaultBlockState() {
        return BlockRegistry.SKY_WEAVE.defaultBlockState();
    }

    @Override
    public BlockState getStateForCulling() {
        return showFacade() ? super.getStateForCulling() : Blocks.COBBLESTONE.defaultBlockState();
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(tag, pRegistries);
        tag.putInt("previousLight", previousLight);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        previousLight = pTag.getInt("previousLight");
    }

    public boolean showFacade() {
        return getBlockState().getValue(SkyWeave.SHOW_FACADE);
    }

    @Override
    public boolean onDispel(@NotNull LivingEntity caster) {
        this.setShowFacade(!this.showFacade());
        return true;
    }
}
