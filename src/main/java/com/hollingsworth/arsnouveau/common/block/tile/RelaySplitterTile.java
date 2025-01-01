package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.source.AbstractSourceMachine;
import com.hollingsworth.arsnouveau.api.source.IMultiSourceTargetProvider;
import com.hollingsworth.arsnouveau.api.source.ISourceCap;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.client.particle.ColorPos;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.capability.SourceStorage;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RelaySplitterTile extends RelayTile implements IMultiSourceTargetProvider {

    ArrayList<BlockPos> toList = new ArrayList<>();
    ArrayList<BlockPos> fromList = new ArrayList<>();

    public RelaySplitterTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.RELAY_SPLITTER_TILE.get(), pos, state);
    }

    public RelaySplitterTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public boolean setTakeFrom(BlockPos pos) {
        return closeEnough(pos) && fromList.add(pos) && updateBlock();
    }

    @Override
    public boolean setSendTo(BlockPos pos) {
        return closeEnough(pos) && toList.add(pos) && updateBlock();
    }

    @Override
    public List<ColorPos> getWandHighlight(List<ColorPos> list) {
        for (BlockPos toPos : toList) {
            list.add(ColorPos.centered(toPos, ParticleColor.TO_HIGHLIGHT));
        }
        for (BlockPos fromPos : fromList) {
            list.add(ColorPos.centered(fromPos, ParticleColor.FROM_HIGHLIGHT));
        }
        return list;
    }

    @Override
    public void clearPos() {
        this.toList.clear();
        this.fromList.clear();
        updateBlock();
    }

    public void processFromList() {
        if (fromList.isEmpty())
            return;
        ArrayList<BlockPos> stale = new ArrayList<>();

        int ratePer = getTransferRate() / fromList.size();
        if(ratePer == 0){
            return;
        }
        for (BlockPos fromPos : fromList) {
            if (!level.isLoaded(fromPos))
                continue;
            int transfer;
            if (level.getCapability(CapabilityRegistry.SOURCE_CAPABILITY, fromPos, null) instanceof ISourceCap sourceHandler) {
                transfer = transferSource(sourceHandler, this.getSourceCapability(), ratePer);
            } else if (level.getBlockEntity(fromPos) instanceof AbstractSourceMachine fromTile) {
                int fromRate = Math.min(ratePer, getTransferRate(fromTile, this));
                transfer = transferSource(fromTile, this, fromRate);
            } else {
                stale.add(fromPos);
                continue;
            }

            if (transfer > 0) {
                createParticles(fromPos, worldPosition);
            }
        }
        for (BlockPos s : stale) {
            fromList.remove(s);
            updateBlock();
        }

    }

    public void createParticles(BlockPos from, BlockPos to) {
        ParticleUtil.spawnFollowProjectile(level, from, to, this.getColor());
    }

    public void processToList() {
        if (toList.isEmpty())
            return;
        ArrayList<BlockPos> stale = new ArrayList<>();
        int ratePer = getSource() / toList.size();
        if(ratePer == 0){
            return;
        }
        for (BlockPos toPos : toList) {
            if (!level.isLoaded(toPos))
                continue;
            int transfer;
            if (level.getCapability(CapabilityRegistry.SOURCE_CAPABILITY, toPos, null) instanceof ISourceCap sourceHandler) {
                transfer = transferSource(this.getSourceCapability(), sourceHandler, ratePer);
            } else if (level.getBlockEntity(toPos) instanceof AbstractSourceMachine toTile) {
                transfer = transferSource(this, toTile, ratePer);
            } else {
                stale.add(toPos);
                continue;
            }

            if (transfer > 0) {
                createParticles(worldPosition, toPos);
            }
        }
        for (BlockPos s : stale) {
            toList.remove(s);
            updateBlock();
        }
    }

    @Override
    public void tick() {
        if (level.getGameTime() % 20 != 0 || toList.isEmpty() || level.isClientSide || disabled)
            return;

        processFromList();
        processToList();
        updateBlock();
    }

    @Override
    protected @NotNull ISourceCap createDefaultSourceCapability() {
        return new SourceStorage(2500, 2500);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider pRegistries) {
        super.loadAdditional(tag, pRegistries);
        fromList = new ArrayList<>();
        toList = new ArrayList<>();
        int counter = 0;

        while (NBTUtil.hasBlockPos(tag, "from_" + counter)) {
            BlockPos pos = NBTUtil.getBlockPos(tag, "from_" + counter);
            if (!this.fromList.contains(pos))
                this.fromList.add(pos);
            counter++;
        }

        counter = 0;
        while (NBTUtil.hasBlockPos(tag, "to_" + counter)) {
            BlockPos pos = NBTUtil.getBlockPos(tag, "to_" + counter);
            if (!this.toList.contains(pos))
                this.toList.add(NBTUtil.getBlockPos(tag, "to_" + counter));
            counter++;
        }

    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider pRegistries) {
        super.saveAdditional(tag, pRegistries);
        int counter = 0;
        for (BlockPos p : this.fromList) {
            NBTUtil.storeBlockPos(tag, "from_" + counter, p);
            counter++;
        }
        counter = 0;
        for (BlockPos p : this.toList) {
            NBTUtil.storeBlockPos(tag, "to_" + counter, p);
            counter++;
        }
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        if (toList == null || toList.isEmpty()) {
            tooltip.add(Component.translatable("ars_nouveau.relay.no_to"));
        } else {
            tooltip.add(Component.translatable("ars_nouveau.relay.one_to", toList.size()));
        }
        if (fromList == null || fromList.isEmpty()) {
            tooltip.add(Component.translatable("ars_nouveau.relay.no_from"));
        } else {
            tooltip.add(Component.translatable("ars_nouveau.relay.one_from", fromList.size()));
        }
    }

    @Override
    public List<BlockPos> getFromList() {
        return fromList;
    }

    @Override
    public List<BlockPos> getToList() {
        return toList;
    }
}
