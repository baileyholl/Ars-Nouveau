package com.hollingsworth.arsnouveau.common.world.saved_data;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.TickEvent;
import var;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EventBusSubscriber(modid = ArsNouveau.MODID)
public class RedstoneSavedData extends SavedData {
    public final Map<BlockPos, Entry> SIGNAL_MAP = new HashMap<>();

    public RedstoneSavedData(){

    }

    public RedstoneSavedData(CompoundTag tag){
        ListTag signalList = tag.getList("SignalList", 10);
        for(int i = 0; i < signalList.size(); i++){
            var signal = new Entry(signalList.getCompound(i));
            SIGNAL_MAP.put(signal.pos, signal);
        }
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
            serverLevel.getBlockState(pos).neighborChanged(serverLevel, pos, serverLevel.getBlockState(pos).getBlock(), pos, false);
            serverLevel.updateNeighborsAt(pos, serverLevel.getBlockState(pos).getBlock());
        }
    }

    @Override
    public boolean isDirty() {
        return true;
    }

    @Override
    public CompoundTag save(CompoundTag pCompoundTag) {
        ListTag signalList = new ListTag();
        for(var signal : SIGNAL_MAP.values()){
            signalList.add(signal.save(new CompoundTag()));
        }
        pCompoundTag.put("SignalList", signalList);
        return pCompoundTag;
    }

    public static RedstoneSavedData from(ServerLevel level){
        return level.getDataStorage()
                .computeIfAbsent(RedstoneSavedData::new, RedstoneSavedData::new, "an_redstone_signals" );
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
    public static void serverTick(TickEvent.LevelTickEvent e) {

        if (e.level.isClientSide || e.phase != TickEvent.Phase.END)
            return;

        RedstoneSavedData data = RedstoneSavedData.from((ServerLevel) e.level);
        data.tick((ServerLevel) e.level);
    }
}
