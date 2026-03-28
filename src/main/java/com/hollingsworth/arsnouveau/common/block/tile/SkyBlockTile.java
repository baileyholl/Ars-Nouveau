package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.api.event.InvalidateMirrorweaveRender;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.block.MirrorWeave;
import com.hollingsworth.arsnouveau.common.event.timed.SkyweaveVisibilityEvent;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;


public class SkyBlockTile extends MirrorWeaveTile implements ITickable {

    private boolean showFacade;
    public int previousLight;

    public SkyBlockTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.SKYWEAVE_TILE.get(), pos, state);
    }

    @Override
    public void tick() {
        if (showFacade && !level.isClientSide()) {
            // getLightEmission() takes no params in 1.21.11
            if (getBlockState().getValue(MirrorWeave.LIGHT_LEVEL) != this.mimicState.getLightEmission()) {
                level.setBlockAndUpdate(worldPosition, getBlockState().setValue(MirrorWeave.LIGHT_LEVEL, this.mimicState.getLightEmission()));
            }
            return;
        }
        if (!level.isClientSide() && level.isBrightOutside()) {
            if (getBlockState().getValue(MirrorWeave.LIGHT_LEVEL) != 15) {
                previousLight = getBlockState().getValue(MirrorWeave.LIGHT_LEVEL);
                level.setBlockAndUpdate(worldPosition, getBlockState().setValue(MirrorWeave.LIGHT_LEVEL, 15));
            }
        }
        if (!level.isClientSide() && !level.isBrightOutside()) {
            if (getBlockState().getValue(MirrorWeave.LIGHT_LEVEL) != previousLight) {
                level.setBlockAndUpdate(worldPosition, getBlockState().setValue(MirrorWeave.LIGHT_LEVEL, this.mimicState.getLightEmission()));
            }
        }
    }

    public void setShowFacade(boolean showFacade) {
        if (this.showFacade == showFacade) {
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
        this.showFacade = showFacade;
        EventQueue.getServerInstance().addEvent(new InvalidateMirrorweaveRender(getBlockPos(), level));
        this.updateBlock();
    }

    @Override
    public BlockState getDefaultBlockState() {
        return BlockRegistry.SKY_WEAVE.defaultBlockState();
    }

    @Override
    public BlockState getStateForCulling() {
        return showFacade ? super.getStateForCulling() : Blocks.COBBLESTONE.defaultBlockState();
    }

    @Override
    protected void saveAdditional(ValueOutput tag) {
        super.saveAdditional(tag);
        tag.putBoolean("showFacade", showFacade);
        tag.putInt("previousLight", previousLight);
    }

    @Override
    protected void loadAdditional(ValueInput pTag) {
        super.loadAdditional(pTag);
        showFacade = pTag.getBooleanOr("showFacade", false);
        previousLight = pTag.getIntOr("previousLight", 0);
    }

    public boolean showFacade() {
        return showFacade;
    }

    @Override
    public boolean onDispel(@NotNull LivingEntity caster) {
        this.setShowFacade(!this.showFacade());
        return true;
    }
}
