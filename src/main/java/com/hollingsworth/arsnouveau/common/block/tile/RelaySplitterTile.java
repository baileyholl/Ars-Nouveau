package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.source.ISourceTile;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.client.particle.ColorPos;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RelaySplitterTile extends RelayTile {
    private final Map<BlockPos, Direction> toMap = new HashMap<>();
    private final Map<BlockPos, Direction> fromMap = new HashMap<>();

    public RelaySplitterTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.RELAY_SPLITTER_TILE, pos, state);
    }

    public RelaySplitterTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public boolean setTakeFrom(BlockPos pos, Direction dir) {
        fromMap.put(pos, dir);
        return closeEnough(pos) && updateBlock();
    }

    @Override
    public boolean setSendTo(BlockPos pos, Direction dir) {
        toMap.put(pos, dir);
        return closeEnough(pos) && updateBlock();
    }

    @Override
    public List<ColorPos> getWandHighlight(List<ColorPos> list) {
        for(BlockPos toPos : toMap.keySet()){
            list.add(ColorPos.centered(toPos, ParticleColor.TO_HIGHLIGHT));
        }
        for(BlockPos fromPos : fromMap.keySet()){
            list.add(ColorPos.centered(fromPos, ParticleColor.FROM_HIGHLIGHT));
        }
        return list;
    }

    @Override
    public void clearPos() {
        this.toMap.clear();
        this.fromMap.clear();
        updateBlock();
    }

    protected void createParticles(BlockPos from, BlockPos to) {
        ParticleUtil.spawnFollowProjectile(level, from, to);
    }

    void processTargetMap(Map<BlockPos,Direction> targets, boolean sendSource) {
        if (targets.isEmpty()) return;

        List<BlockPos> stale = new ArrayList<>();
        int ratePer = getTransferRate() / targets.size();

        for (var target : targets.entrySet()) {
            if (!Objects.requireNonNull(level).isLoaded(target.getKey())) continue;

            BlockEntity be = level.getBlockEntity(target.getKey());

            if (be == null) {
                stale.add(target.getKey());
                continue;
            }

            LazyOptional<ISourceTile> cap = be.getCapability(CapabilityRegistry.SOURCE_TILE, target.getValue());

            cap.resolve().ifPresentOrElse(sourceTile -> {
                ISourceTile fromTile = sendSource ? this : sourceTile;
                ISourceTile toTile = sendSource ? sourceTile : this;

                if (transferSource(fromTile, toTile, ratePer) > 0) {
                    BlockPos fromPos = sendSource ? worldPosition : target.getKey();
                    BlockPos toPos = sendSource ? target.getKey() : worldPosition;
                    createParticles(fromPos, toPos);
                }
                }, () -> stale.add(target.getKey()));
        }

        for (BlockPos pos : stale) {
            targets.remove(pos);
            updateBlock();
        }
    }


    @Override
    public void tick() {
        if (level.getGameTime() % 20 != 0 || toMap.isEmpty() || level.isClientSide || disabled)
            return;

        processTargetMap(fromMap, false);
        processTargetMap(toMap, true);
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
        fromMap.clear();
        toMap.clear();
        int counter = 0;

        while (NBTUtil.hasBlockPos(tag, "from_" + counter)) {
            BlockPos pos = NBTUtil.getBlockPos(tag, "from_" + counter);
            Direction dir = null;
            if(tag.contains("from_dir_"+counter)){
                dir = Direction.valueOf(tag.getString("from_dir_"+counter));
            }
            dir = dir == null ? Direction.UP : dir;

            this.fromMap.put(pos, dir);
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

            this.toMap.put(pos, dir);
            counter++;
        }

    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        int counter = 0;
        for (var from : this.fromMap.entrySet()) {
            NBTUtil.storeBlockPos(tag, "from_" + counter, from.getKey());
            tag.putString("from_dir_"+counter, from.getValue().getName());
            counter++;
        }
        counter = 0;
        for (var to : this.toMap.entrySet()) {
            NBTUtil.storeBlockPos(tag, "to_" + counter, to.getKey());
            tag.putString("to_dir_"+counter, to.getValue().getName());
            counter++;
        }
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        if (toMap.isEmpty()) {
            tooltip.add(Component.translatable("ars_nouveau.relay.no_to"));
        } else {
            tooltip.add(Component.translatable("ars_nouveau.relay.one_to", toMap.size()));
        }
        if (fromMap.isEmpty()) {
            tooltip.add(Component.translatable("ars_nouveau.relay.no_from"));
        } else {
            tooltip.add(Component.translatable("ars_nouveau.relay.one_from", fromMap.size()));
        }
    }
}
