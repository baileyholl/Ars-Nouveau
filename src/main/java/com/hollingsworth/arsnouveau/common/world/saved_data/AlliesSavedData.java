package com.hollingsworth.arsnouveau.common.world.saved_data;

import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketSetAllies;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber
public class AlliesSavedData extends SavedData {

    static Set<UUID> localAllies = new HashSet<>();

    Map<UUID, Set<UUID>> allies = new ConcurrentHashMap<>();

    public static AlliesSavedData get(ServerLevel world) {
        return world.getDataStorage().computeIfAbsent(factory(), "ars_allies");
    }

    public static Set<UUID> getAllies(ServerLevel world, UUID owner) {
        return get(world).allies.getOrDefault(owner, new HashSet<>());
    }

    public static void setAllies(ServerLevel world, UUID owner, Set<UUID> allies) {
        var data = get(world);
        data.allies.put(owner, allies);
        data.setDirty();
    }

    public static void setLocalAllies(Set<UUID> allies) {
        localAllies = allies;
    }

    public static Set<UUID> getLocalAllies() {
        return localAllies;
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        // Save the allies data to the tag
        ListTag alliesList = new ListTag();
        for (var entry : allies.entrySet()) {
            CompoundTag entryTag = new CompoundTag();
            entryTag.putUUID("Owner", entry.getKey());
            // save the number of allies
            entryTag.putInt("NumAllies", entry.getValue().size());
            // prefix each ally uuid with index and save
            int i = 0;
            for (var ally : entry.getValue()) {
                entryTag.putUUID("Ally" + i++, ally);
            }
            alliesList.add(entryTag);
        }
        tag.put("ArsAllies", alliesList);
        return tag;
    }

    public static AlliesSavedData load(CompoundTag tag, HolderLookup.Provider p_323806_) {
        AlliesSavedData data = new AlliesSavedData();
        ListTag alliesList = tag.getList("ArsAllies", 10);
        for (int i = 0; i < alliesList.size(); i++) {
            var entryTag = alliesList.getCompound(i);
            UUID owner = entryTag.getUUID("Owner");
            int numAllies = entryTag.getInt("NumAllies");
            Set<UUID> allies = new HashSet<>();
            for (int j = 0; j < numAllies; j++) {
                if (entryTag.contains("Ally" + j))
                    allies.add(entryTag.getUUID("Ally" + j));
            }
            data.allies.put(owner, allies);
        }
        return data;
    }

    public static SavedData.Factory<AlliesSavedData> factory() {
        return new SavedData.Factory<>(AlliesSavedData::new, AlliesSavedData::load, null);
    }

    @SubscribeEvent
    public static void onSave(LevelEvent.Save event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;
        System.out.println("Sending allies data to all players");
        // Send the respective allies data to all players
        serverLevel.players().forEach(player -> Networking.sendToPlayerClient(new PacketSetAllies(player.getUUID(), getAllies(serverLevel, player.getUUID())), player));
    }
    
}
