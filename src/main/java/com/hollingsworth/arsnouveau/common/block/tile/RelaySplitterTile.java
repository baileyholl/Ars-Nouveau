package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.source.AbstractSourceMachine;
import com.hollingsworth.arsnouveau.api.source.IMultiSourceTargetProvider;
import com.hollingsworth.arsnouveau.api.source.ISourceTile;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.client.particle.ColorPos;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RelaySplitterTile extends RelayTile implements IMultiSourceTargetProvider {

    ArrayList<Pair<BlockPos, Direction>> toList = new ArrayList<>();
    ArrayList<Pair<BlockPos, Direction>> fromList = new ArrayList<>();

    public RelaySplitterTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.RELAY_SPLITTER_TILE, pos, state);
    }

    public RelaySplitterTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public boolean setTakeFrom(Pair<BlockPos, Direction> pos) {
        return closeEnough(pos.getFirst()) && fromList.add(pos) && updateBlock();
    }

    @Override
    public boolean setSendTo(Pair<BlockPos, Direction> pos) {
        return closeEnough(pos.getFirst()) && toList.add(pos) && updateBlock();
    }

    @Override
    public List<ColorPos> getWandHighlight(List<ColorPos> list) {
        for(Pair<BlockPos, Direction> toPos : toList){
            list.add(ColorPos.centered(toPos.getFirst(), ParticleColor.TO_HIGHLIGHT));
        }
        for(Pair<BlockPos, Direction> fromPos : fromList){
            list.add(ColorPos.centered(fromPos.getFirst(), ParticleColor.FROM_HIGHLIGHT));
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
        processTargetList(fromList, false);
    }

    public void createParticles(BlockPos from, BlockPos to) {
        ParticleUtil.spawnFollowProjectile(level, from, to);
    }

    public void processToList() {
        processTargetList(toList, true);
    }

    void processTargetList(List<Pair<BlockPos,Direction>> posList, boolean sendSource) {
        if (posList.isEmpty()) return;

        List<Pair<BlockPos,Direction>> stale = new ArrayList<>();
        int ratePer = getTransferRate() / posList.size();

        for (Pair<BlockPos,Direction> pos : posList) {
            if (!Objects.requireNonNull(level).isLoaded(pos.getFirst())) continue;

            BlockEntity be = level.getBlockEntity(pos.getFirst());

            if (be == null) {
                stale.add(pos);
                continue;
            }

            LazyOptional<ISourceTile> cap = be.getCapability(CapabilityRegistry.SOURCE_TILE, pos.getSecond());

            cap.resolve().ifPresentOrElse(
                    (sourceTile) -> {
                        ISourceTile fromTile = sendSource ? this : sourceTile;
                        ISourceTile toTile = sendSource ? sourceTile : this;

                        if (transferSource(fromTile, toTile, ratePer) > 0) {
                            BlockPos fromPos = sendSource ? worldPosition : pos.getFirst();
                            BlockPos toPos = sendSource ? pos.getFirst() : worldPosition;
                            createParticles(fromPos, toPos);
                        }
                    }, () -> stale.add(pos));
        }

        for (Pair<BlockPos,Direction> pos : stale) {
            posList.remove(pos);
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
            Direction dir = null;
            if(tag.contains("from_dir_"+counter)){
                dir = Direction.valueOf(tag.getString("from_dir_"+counter));
            }
            dir = dir == null ? Direction.UP : dir;

            Pair<BlockPos,Direction> pair = Pair.of(pos,dir);
            if (!this.fromList.contains(pair))
                this.fromList.add(pair);
            counter++;
        }

        counter = 0;
        while (NBTUtil.hasBlockPos(tag, "to_" + counter)) {
            BlockPos pos = NBTUtil.getBlockPos(tag, "to_" + counter);
            Direction dir = null;
            if(tag.contains("to_dir_"+counter)){
                dir = Direction.valueOf(tag.getString("to_dir_"+counter));
            }
            dir = dir == null ? Direction.UP : dir;

            Pair<BlockPos,Direction> pair = Pair.of(pos,dir);
            if (!this.toList.contains(pair))
                this.toList.add(pair);
            counter++;
        }

    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        int counter = 0;
        for (Pair<BlockPos,Direction> p : this.fromList) {
            NBTUtil.storeBlockPos(tag, "from_" + counter, p.getFirst());
            tag.putString("from_dir_"+counter, p.getSecond().getName());
            counter++;
        }
        counter = 0;
        for (Pair<BlockPos,Direction> p : this.toList) {
            NBTUtil.storeBlockPos(tag, "to_" + counter, p.getFirst());
            tag.putString("to_dir_"+counter, p.getSecond().getName());
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
    public List<Pair<BlockPos, Direction>> getFromList() {
        return fromList;
    }

    @Override
    public List<Pair<BlockPos, Direction>> getToList() {
        return toList;
    }
}
