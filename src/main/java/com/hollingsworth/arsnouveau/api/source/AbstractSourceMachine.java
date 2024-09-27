package com.hollingsworth.arsnouveau.api.source;

import com.hollingsworth.arsnouveau.api.util.IWololoable;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.block.tile.ModdedTile;
import com.hollingsworth.arsnouveau.common.capability.SourceStorage;
import com.hollingsworth.arsnouveau.common.items.data.BlockFillContents;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractSourceMachine extends ModdedTile implements ISourceTile, IWololoable {

    private int source = 0;
    private int maxSource = 0;
    private ParticleColor color = ParticleColor.defaultParticleColor();
    public static String SOURCE_TAG = "source";
    public static String MAX_SOURCE_TAG = "max_source";
    public static String COLOR_TAG = "color";

    public AbstractSourceMachine(BlockEntityType<?> manaTile, BlockPos pos, BlockState state) {
        super(manaTile, pos, state);
    }

    public @Nullable SourceStorage sourceStorage;

    public @NotNull SourceStorage getSourceStorage() {
        if (sourceStorage == null) {
            sourceStorage = new SourceStorage(getMaxSource(), getTransferRate(), getTransferRate(), source) {
                public void onContentsChanged() {
                    /*
                     * This is called when the source changes in the capability, and is used to update the tile's value.
                     * Opposite of the setSource method called in updateBlock.
                     */
                    AbstractSourceMachine.this.source = this.source;
                    AbstractSourceMachine.this.updateBlock();
                }
            };
            if (level != null) level.invalidateCapabilities(worldPosition);
        }
        return sourceStorage;
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider pRegistries) {
        super.loadAdditional(tag, pRegistries);
        source = tag.getInt(SOURCE_TAG);
        maxSource = tag.getInt(MAX_SOURCE_TAG);
        color = ParticleColor.fromInt(tag.getInt(COLOR_TAG));
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider pRegistries) {
        super.saveAdditional(tag, pRegistries);
        tag.putInt(SOURCE_TAG, getSource());
        tag.putInt(MAX_SOURCE_TAG, getMaxSource());
        tag.putInt(COLOR_TAG, getColor().getColor());
    }

    @Override
    public int setSource(int source) {
        if (this.source == source)
            return this.source;
        this.source = Math.clamp(source, 0, this.getMaxSource());
        updateBlock();
        return this.source;
    }

    public boolean updateBlock() {
        if (level != null) {
            // force update the capability
            getSourceStorage().setSource(this.source);
            BlockState state = level.getBlockState(worldPosition);
            level.sendBlockUpdated(worldPosition, state, state, 3);
            setChanged();
            return true;
        }
        return false;
    }

    @Override
    public int addSource(int source, boolean simulate) {
        return getSourceStorage().receiveSource(source, simulate);
    }

    @Override
    public int addSource(int source) {
        return this.setSource(this.getSource() + source);
    }

    @Override
    public int getSource() {
        if (this.sourceStorage == null)
            return this.source;
        return this.getSourceStorage().getSource();
    }

    @Override
    public int removeSource(int source, boolean simulate) {
        return getSourceStorage().extractSource(source, simulate);
    }

    @Override
    public int removeSource(int source) {
        if (source == 0)
            return this.getSource();
        this.setSource(this.getSource() - source);
        //updateBlock(); updateBlock is called in setSource
        return this.getSource();
    }

    @Override
    public void setMaxSource(int max) {
        this.maxSource = max;
        updateBlock();
    }

    @Override
    public int getMaxSource() {
        if (this.sourceStorage == null)
            return this.maxSource;
        return this.getSourceStorage().getSourceCapacity();
    }

    public boolean canAcceptSource() {
        return this.getSource() < this.getMaxSource();
    }

    public boolean canProvideSource() {
        return this.getSource() > 0;
    }

    public boolean canAcceptSource(int source) {
        return this.getSourceStorage().canAcceptSource(source);
    }

    /**
     * Transfers the maximum possible amount of source from one tile to another.
     * Takes the maximum transfer rate of the two tiles into account, and the space remaining.
     *
     * @return The amount of source that was transferred.
     */
    public int transferSource(ISourceTile from, ISourceTile to) {
        int transferRate = getTransferRate(from, to);
        from.removeSource(transferRate);
        to.addSource(transferRate);
        return transferRate;
    }

    /*
     * Transfers the maximum possible amount of source from one SourceStorage to another,
     * checking the transfer rate of the source and destination via simulation.
     */
    public int transferSource(ISourceCap from, ISourceCap to) {
        int transfer = to.receiveSource(from.extractSource(from.getMaxExtract(), true), true);
        if (transfer == 0)
            return 0;
        from.extractSource(transfer, false);
        to.receiveSource(transfer, false);
        return transfer;
    }

    /**
     * Gets the maximum amount of source that can be transferred from one tile to another.
     */
    public int getTransferRate(ISourceTile from, ISourceTile to) {
        return getTransferRate(from, to, from.getTransferRate());
    }

    public int getTransferRate(ISourceTile from, ISourceTile to, int fromTransferRate) {
        return Math.min(Math.min(fromTransferRate, from.getSource()), to.getMaxSource() - to.getSource());
    }

    public int transferSource(ISourceTile from, ISourceTile to, int fromTransferRate) {
        int transferRate = getTransferRate(from, to, fromTransferRate);
        if (transferRate == 0)
            return 0;
        from.removeSource(transferRate);
        to.addSource(transferRate);
        return transferRate;
    }

    @Override
    public ParticleColor getColor() {
        return color;
    }

    @Override
    public void setColor(ParticleColor color) {
        this.color = color;
    }

    @Override
    protected void applyImplicitComponents(@NotNull DataComponentInput pComponentInput) {
        super.applyImplicitComponents(pComponentInput);
        var fill = pComponentInput.getOrDefault(DataComponentRegistry.BLOCK_FILL_CONTENTS, new BlockFillContents(0));
        this.source = fill.amount();
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.@NotNull Builder pComponents) {
        super.collectImplicitComponents(pComponents);
        pComponents.set(DataComponentRegistry.BLOCK_FILL_CONTENTS, new BlockFillContents(this.source));
    }

}

