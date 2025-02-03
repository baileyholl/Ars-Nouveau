package com.hollingsworth.arsnouveau.common.world.saved_data;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EventBusSubscriber(modid = ArsNouveau.MODID)
public class RedstoneSavedData extends SavedData {
    public final Map<BlockPos, Entry> SIGNAL_MAP = new HashMap<>();

    public RedstoneSavedData(){

    }

    public static SavedData.Factory<RedstoneSavedData> factory() {
        return new SavedData.Factory<>(RedstoneSavedData::new, RedstoneSavedData::load, null);
    }

    public static RedstoneSavedData load(CompoundTag tag, HolderLookup.Provider p_323806_){
        RedstoneSavedData data = new RedstoneSavedData();
        ListTag signalList = tag.getList("SignalList", 10);
        for(int i = 0; i < signalList.size(); i++){
            var signal = new Entry(signalList.getCompound(i));
            data.SIGNAL_MAP.put(signal.pos, signal);
        }
        return data;
    }

    public void tick(ServerLevel serverLevel){
        List<BlockPos> toRemove = new ArrayList<>();
        for(var signal : SIGNAL_MAP.values()){
            if(!serverLevel.isLoaded(signal.pos))
                continue;
            if(signal.ticks-- <= 0){
                toRemove.add(signal.pos);
            }
        }
        for(var pos : toRemove){
            SIGNAL_MAP.remove(pos);
            serverLevel.getBlockState(pos).onNeighborChange(serverLevel, pos, pos);
            serverLevel.updateNeighborsAt(pos, serverLevel.getBlockState(pos).getBlock());
        }
    }

    @Override
    public boolean isDirty() {
        return true;
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag pCompoundTag, HolderLookup.@NotNull Provider pRegistries) {
        ListTag signalList = new ListTag();
        for(var signal : SIGNAL_MAP.values()){
            signalList.add(signal.save(new CompoundTag()));
        }
        pCompoundTag.put("SignalList", signalList);
        return pCompoundTag;
    }

    public static RedstoneSavedData from(ServerLevel level){
        return level.getDataStorage()
                .computeIfAbsent(factory(), "an_redstone_signals" );
    }

    public static class Entry{
        public BlockPos pos;
        public int power;
        public int ticks;

        public Entry(BlockPos pos, int power, int ticks){
            this.pos = pos;
            this.power = power;
            this.ticks = ticks;
        }

        public Entry(CompoundTag tag){
            this(new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z")), tag.getInt("power"), tag.getInt("ticks"));
        }

        public CompoundTag save(CompoundTag tag){
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

        if (e.getLevel().isClientSide )
            return;

        RedstoneSavedData data = RedstoneSavedData.from((ServerLevel) e.getLevel());
        data.tick((ServerLevel) e.getLevel());
    }
}
