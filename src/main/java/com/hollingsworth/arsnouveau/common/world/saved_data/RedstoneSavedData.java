package com.hollingsworth.arsnouveau.common.world.saved_data;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EventBusSubscriber(modid = ArsNouveau.MODID)
public class RedstoneSavedData extends SavedData {
    public final Map<BlockPos, Entry> SIGNAL_MAP = new HashMap<>();

    public RedstoneSavedData() {

    }

    // SavedDataType replaces SavedData.Factory in 1.21.11
    public static final SavedDataType<RedstoneSavedData> TYPE = new SavedDataType<>(
            "an_redstone_signals",
            level -> new RedstoneSavedData(),
            level -> net.minecraft.nbt.CompoundTag.CODEC.xmap(
                    tag -> RedstoneSavedData.load(tag, level.registryAccess()),
                    data -> data.save(new net.minecraft.nbt.CompoundTag(), level.registryAccess())
            )
    );

    public static RedstoneSavedData load(CompoundTag tag, HolderLookup.Provider p_323806_) {
        RedstoneSavedData data = new RedstoneSavedData();
        // 1.21.11: getList returns Optional; use getListOrEmpty. getCompound returns Optional.
        ListTag signalList = tag.getListOrEmpty("SignalList");
        for (int i = 0; i < signalList.size(); i++) {
            var signal = new Entry(signalList.getCompoundOrEmpty(i));
            data.SIGNAL_MAP.put(signal.pos, signal);
        }
        return data;
    }

    public void tick(ServerLevel serverLevel) {
        List<BlockPos> toRemove = new ArrayList<>();
        for (var signal : SIGNAL_MAP.values()) {
            if (!serverLevel.isLoaded(signal.pos))
                continue;
            if (signal.ticks-- <= 0) {
                toRemove.add(signal.pos);
            }
        }
        for (var pos : toRemove) {
            SIGNAL_MAP.remove(pos);
            serverLevel.getBlockState(pos).onNeighborChange(serverLevel, pos, pos);
            serverLevel.updateNeighborsAt(pos, serverLevel.getBlockState(pos).getBlock());
        }
    }

    @Override
    public boolean isDirty() {
        return true;
    }

    // save() is no longer an override of SavedData in 1.21.11; called by the Codec in SavedDataType
    public CompoundTag save(CompoundTag pCompoundTag, HolderLookup.Provider pRegistries) {
        ListTag signalList = new ListTag();
        for (var signal : SIGNAL_MAP.values()) {
            signalList.add(signal.save(new CompoundTag()));
        }
        pCompoundTag.put("SignalList", signalList);
        return pCompoundTag;
    }

    public static RedstoneSavedData from(ServerLevel level) {
        return level.getDataStorage()
                .computeIfAbsent(TYPE);
    }

    public static class Entry {
        public BlockPos pos;
        public int power;
        public int ticks;

        public Entry(BlockPos pos, int power, int ticks) {
            this.pos = pos;
            this.power = power;
            this.ticks = ticks;
        }

        public Entry(CompoundTag tag) {
            // 1.21.11: getInt returns Optional; use getIntOr
            this(new BlockPos(tag.getIntOr("x", 0), tag.getIntOr("y", 0), tag.getIntOr("z", 0)), tag.getIntOr("power", 0), tag.getIntOr("ticks", 0));
        }

        public CompoundTag save(CompoundTag tag) {
            tag.putInt("x", pos.getX());
            tag.putInt("y", pos.getY());
            tag.putInt("z", pos.getZ());
            tag.putInt("power", power);
            tag.putInt("ticks", ticks);
            return tag;
        }
    }

    @SubscribeEvent
    public static void serverTick(LevelTickEvent.Post e) {

        if (e.getLevel().isClientSide())
            return;

        RedstoneSavedData data = RedstoneSavedData.from((ServerLevel) e.getLevel());
        data.tick((ServerLevel) e.getLevel());
    }
}
