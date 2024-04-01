package com.hollingsworth.arsnouveau.api.source;

import com.hollingsworth.arsnouveau.api.util.IWololoable;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.tile.ModdedTile;
import com.hollingsworth.arsnouveau.common.util.RegistryWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractSourceMachine extends ModdedTile implements ISourceTile, IWololoable {

    private int source = 0;
    private int maxSource = 0;
    private ParticleColor color = ParticleColor.PINK;
    public static String SOURCE_TAG = "source";
    public static String MAX_SOURCE_TAG = "max_source";
    public static String COLOR_TAG = "color";

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
        color = ParticleColor.fromInt(tag.getInt(COLOR_TAG));
        super.load(tag);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt(SOURCE_TAG, getSource());
        tag.putInt(MAX_SOURCE_TAG, getMaxSource());
        tag.putInt(COLOR_TAG, getColor().getColor());
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

    public boolean canAcceptSource() {
        return this.getSource() < this.getMaxSource();
    }

    public boolean canAcceptSource(int source) {
        return this.getSource() + source <= this.getMaxSource();
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

    /**
     * Gets the maximum amount of source that can be transferred from one tile to another.
     */
    public int getTransferRate(ISourceTile from, ISourceTile to) {
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

    @Override
    public ParticleColor getColor() {
        return color;
    }

    @Override
    public void setColor(ParticleColor color) {
        this.color = color;
    }
}

