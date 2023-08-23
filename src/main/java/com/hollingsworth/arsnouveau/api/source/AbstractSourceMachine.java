package com.hollingsworth.arsnouveau.api.source;

import com.hollingsworth.arsnouveau.common.block.tile.ModdedTile;
import com.hollingsworth.arsnouveau.common.util.RegistryWrapper;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractSourceMachine extends ModdedTile implements ISourceTile {
    private int source = 0;
    private int maxSource = 0;
    public static String SOURCE_TAG = "source";
    public static String MAX_SOURCE_TAG = "max_source";

    private LazyOptional<ISourceTile> sourceTileOptional;

    public AbstractSourceMachine(BlockEntityType<?> manaTile, BlockPos pos, BlockState state) {
        super(manaTile, pos, state);
    }

    public AbstractSourceMachine(RegistryWrapper< ? extends BlockEntityType<?>> manaTile, BlockPos pos, BlockState state) {
        this(manaTile.get(), pos, state);
    }

    @Override
    public void load(CompoundTag tag) {
        source = tag.getInt(SOURCE_TAG);
        maxSource = tag.getInt(MAX_SOURCE_TAG);
        super.load(tag);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt(SOURCE_TAG, getSource());
        tag.putInt(MAX_SOURCE_TAG, getMaxSource());
    }

    @Override
    public void onLoad() {
        super.onLoad();
        sourceTileOptional = LazyOptional.of(() -> this);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return CapabilityRegistry.SOURCE_TILE.orEmpty(cap, sourceTileOptional);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        sourceTileOptional.invalidate();
    }

    @Override
    public int setSource(int source) {
        if (this.source == source)
            return this.source;
        this.source = source;
        if (this.source > this.getMaxSource())
            this.source = this.getMaxSource();
        if (this.source < 0)
            this.source = 0;
        updateBlock();
        return this.source;
    }

    @Override
    public int addSource(int source) {
        return this.setSource(this.getSource() + source);
    }

    @Override
    public int getSource() {
        return this.source;
    }

    @Override
    public int removeSource(int source) {
        if (source == 0)
            return this.getSource();
        this.setSource(this.getSource() - source);
        updateBlock();
        return this.getSource();
    }

    @Override
    public void setMaxSource(int max) {
        this.maxSource = max;
        updateBlock();
    }

    @Override
    public int getMaxSource() {
        return maxSource;
    }

    @Override
    public boolean canAcceptSource() {
        return this.getSource() < this.getMaxSource();
    }

    @Override
    public boolean canProvideSource() {
        return false;
    }

    public boolean canAcceptSource(int source) {
        return this.getSource() + source <= this.getMaxSource();
    }

    @Override
    public boolean sourcelinksCanProvideSource() {
        return false;
    }

    @Override
    public boolean machinesCanTakeSource() {
        return false;
    }

    /**
     * Transfers the maximum possible amount of source from one tile to another.
     * Takes the maximum transfer rate of the two tiles into account, and the space remaining.
     *
     * @return The amount of source that was transferred.
     */
    public int transferSource(ISourceTile from, ISourceTile to) {
        int transferRate = getTransferRate(from, to);
        if (transferRate == 0)
            return 0;
        from.removeSource(transferRate);
        to.addSource(transferRate);
        return transferRate;
    }

    /**
     * Gets the maximum amount of source that can be transferred from one tile to another.
     */
    public int getTransferRate(ISourceTile from, ISourceTile to) {
        if(!from.canProvideSource() || !to.canAcceptSource()){
            return 0;//tiles don't support transferc
        }
        return Math.min(Math.min(from.getTransferRate(), from.getSource()), to.getMaxSource() - to.getSource());
    }

    public int transferSource(ISourceTile from, ISourceTile to, int fromTransferRate) {
        int transferRate = getTransferRate(from, to, fromTransferRate);
        if (transferRate == 0)
            return 0;
        from.removeSource(transferRate);
        to.addSource(transferRate);
        return transferRate;
    }

    public int getTransferRate(ISourceTile from, ISourceTile to, int fromTransferRate) {
        return Math.min(Math.min(fromTransferRate, from.getSource()), to.getMaxSource() - to.getSource());
    }
}

