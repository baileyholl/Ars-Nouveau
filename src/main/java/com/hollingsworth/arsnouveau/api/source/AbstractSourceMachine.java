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

//TODO: Make relevant source methods final and defaulted in interface
public abstract class AbstractSourceMachine extends ModdedTile implements ISourceTile, IWololoable {

    private ParticleColor color = ParticleColor.defaultParticleColor();
    public static String SOURCE_TAG = "source";
    public static String COLOR_TAG = "color";

    public AbstractSourceMachine(BlockEntityType<?> manaTile, BlockPos pos, BlockState state) {
        super(manaTile, pos, state);
    }

    public @Nullable SourceStorage sourceStorage;

    public @NotNull SourceStorage getSourceStorage() {
        if (sourceStorage == null) {
            sourceStorage = createDefaultStorage();
            if (level != null) level.invalidateCapabilities(worldPosition);
        }
        return sourceStorage;
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider pRegistries) {
        super.loadAdditional(tag, pRegistries);
        this.sourceStorage = createDefaultStorage();
        if (tag.contains(SOURCE_TAG)) {
            this.sourceStorage.setSource(tag.getInt(SOURCE_TAG));
        }
        color = ParticleColor.fromInt(tag.getInt(COLOR_TAG));
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider pRegistries) {
        super.saveAdditional(tag, pRegistries);
        tag.putInt(SOURCE_TAG, getSource());
        tag.putInt(COLOR_TAG, getColor().getColor());
    }

    protected @NotNull SourceStorage createDefaultStorage() {
        return new SourceStorage(10000, 1000, 1000, 0) {
            public void onContentsChanged() {
                AbstractSourceMachine.this.updateBlock();
            }
        };
    }

    @Override
    public int setSource(int source) {
        this.getSourceStorage().setSource(Math.clamp(source, 0, this.getMaxSource()));
        updateBlock();
        return this.getSourceStorage().getSource();
    }

    public boolean updateBlock() {
        if (level != null) {
            // force update the capability
            BlockState state = getBlockState();
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
        return this.getSourceStorage().getSource();
    }

    @Override
    public int getTransferRate() {
        return this.getSourceStorage().getMaxExtract();
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
    public int getMaxSource() {
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
        return transferSource(from, to, from.getMaxExtract());
    }

    public int transferSource(ISourceCap from, ISourceCap to, int amount) {
        int transfer = to.receiveSource(from.extractSource(amount, true), true);
        if (transfer == 0)
            return 0;
        from.extractSource(transfer, false);
        to.receiveSource(transfer, false);
        return transfer;
    }

    /**
     * TODO: 1.22 remove
     * Gets the maximum amount of source that can be transferred from one tile to another.
     */
    @Deprecated(forRemoval = true)
    public int getTransferRate(ISourceTile from, ISourceTile to) {
        return getTransferRate(from, to, from.getTransferRate());
    }

    @Deprecated(forRemoval = true)
    public int getTransferRate(ISourceTile from, ISourceTile to, int fromTransferRate) {
        return Math.min(Math.min(fromTransferRate, from.getSource()), to.getMaxSource() - to.getSource());
    }

    @Deprecated(forRemoval = true)
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
        this.getSourceStorage().setSource(fill.amount());
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.@NotNull Builder pComponents) {
        super.collectImplicitComponents(pComponents);
        int source = this.getSourceStorage().getSource();
        if (source != 0) {
            pComponents.set(DataComponentRegistry.BLOCK_FILL_CONTENTS, new BlockFillContents(source));
        }
    }
}

