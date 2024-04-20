package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.source.AbstractSourceMachine;
import com.hollingsworth.arsnouveau.api.source.IMultiSourceTargetProvider;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.client.particle.ColorPos;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class RelaySplitterTile extends RelayTile implements IMultiSourceTargetProvider {

    ArrayList<BlockPos> toList = new ArrayList<>();
    ArrayList<BlockPos> fromList = new ArrayList<>();

    public RelaySplitterTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.RELAY_SPLITTER_TILE, pos, state);
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
        for(BlockPos toPos : toList){
            list.add(ColorPos.centered(toPos, ParticleColor.TO_HIGHLIGHT));
        }
        for(BlockPos fromPos : fromList){
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
        for (BlockPos fromPos : fromList) {
            if (!level.isLoaded(fromPos))
                continue;

            if (!(level.getBlockEntity(fromPos) instanceof AbstractSourceMachine fromTile)) {
                stale.add(fromPos);
                continue;
            }
            int fromRate = Math.min(ratePer, getTransferRate(fromTile, this));
            if (transferSource(fromTile, this, fromRate) > 0) {
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
        for (BlockPos toPos : toList) {
            if (!level.isLoaded(toPos))
                continue;
            if (!(level.getBlockEntity(toPos) instanceof AbstractSourceMachine toTile)) {
                stale.add(toPos);
                continue;
            }

            int transfer = transferSource(this, toTile, ratePer);
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
    public int getTransferRate() {
        return 2500;
    }

    @Override
    public int getMaxSource() {
        return 2500;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
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
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
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
