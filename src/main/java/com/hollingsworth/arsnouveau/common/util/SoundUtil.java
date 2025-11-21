package com.hollingsworth.arsnouveau.common.util;

import com.hollingsworth.arsnouveau.api.event.SpellResolveEvent;
import com.hollingsworth.arsnouveau.common.network.PacketBatchedSounds;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SoundUtil {
    private static final Object2ObjectOpenHashMap<UUID, List<ClientboundSoundPacket>> QUEUE = new Object2ObjectOpenHashMap<>();

    @SubscribeEvent
    public static void processQueue(ServerTickEvent.Post event) {
        processQueue(event.getServer().getPlayerList());
    }

    public static void processQueue(PlayerList players) {
        if (QUEUE.isEmpty()) {
            return;
        }

        var iter = Object2ObjectMaps.fastIterator(QUEUE);
        while (iter.hasNext()) {
            var entry = iter.next();
            var player = players.getPlayer(entry.getKey());
            var sounds = entry.getValue();
            if (player != null && !sounds.isEmpty()) {
                player.connection.send(new PacketBatchedSounds(sounds));
            }

            iter.remove();
        }
    }

    public static void playSound(@NotNull Level level, @Nullable Entity entity, BlockPos pos, SoundEvent sound, SoundSource category, float volume, float pitch) {
        playSound(level, entity instanceof Player player ? player : null, pos, sound, category, volume, pitch);
    }

    /**
     * Plays a sound. On the server, the sound is broadcast to all nearby <em>except</em> the given player. On the client, the sound only plays if the given player is the client player. Thus, this method is intended to be called from code running on both sides. The client plays it locally and the server plays it for everyone else.
     */
    public static void playSound(@NotNull Level level, @Nullable Player player, BlockPos pos, SoundEvent sound, SoundSource category, float volume, float pitch) {
        playSound(
                level, player, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, sound, category, volume, pitch
        );
    }

    public static void playSeededSound(
            @NotNull Level level,
            @Nullable Player player,
            double x,
            double y,
            double z,
            SoundEvent sound,
            SoundSource category,
            float volume,
            float pitch,
            long seed
    ) {
        playSeededSound(
                level, player, x, y, z, BuiltInRegistries.SOUND_EVENT.wrapAsHolder(sound), category, volume, pitch, seed
        );
    }

    public static void playSound(@NotNull Level level, @Nullable Player player, double x, double y, double z, SoundEvent sound, SoundSource category) {
        playSound(level, player, x, y, z, sound, category, 1.0F, 1.0F);
    }

    public static void playSound(
            @NotNull Level level, @Nullable Player player, double x, double y, double z, SoundEvent sound, SoundSource category, float volume, float pitch
    ) {
        playSeededSound(level, player, x, y, z, sound, category, volume, pitch, level.random.nextLong());
    }

    public static void playSound(
            @NotNull Level level,
            @Nullable Player player,
            double x,
            double y,
            double z,
            Holder<SoundEvent> sound,
            SoundSource category,
            float volume,
            float pitch
    ) {
        playSeededSound(level, player, x, y, z, sound, category, volume, pitch, level.random.nextLong());
    }

    public static void playSound(@NotNull Level level, @Nullable Player player, Entity entity, SoundEvent event, SoundSource category, float volume, float pitch) {
        playSeededSound(
                level, player, entity, BuiltInRegistries.SOUND_EVENT.wrapAsHolder(event), category, volume, pitch, level.random.nextLong()
        );
    }

    public static void playSeededSound(
            @NotNull Level level,
            @Nullable Player player,
            double x,
            double y,
            double z,
            Holder<SoundEvent> sound,
            SoundSource category,
            float volume,
            float pitch,
            long seed
    ) {
        net.neoforged.neoforge.event.PlayLevelSoundEvent.AtPosition event = net.neoforged.neoforge.event.EventHooks.onPlaySoundAtPosition(level, x, y, z, sound, category, volume, pitch);
        if (event.isCanceled() || event.getSound() == null) return;
        sound = event.getSound();
        category = event.getSource();
        volume = event.getNewVolume();
        pitch = event.getNewPitch();

        if (level instanceof ServerLevel serverLevel) {
            var packet = new ClientboundSoundPacket(sound, category, x, y, z, volume, pitch, seed);
            var range = sound.value().getRange(volume);
            queueInRange(player, serverLevel.players(), x, y, z, range, packet);
        } else {
            level.playSeededSound(player, x, y, z, sound, category, volume, pitch, seed);
        }
    }

    public static void playSeededSound(
            @NotNull Level level, @Nullable Player player, Entity entity, Holder<SoundEvent> sound, SoundSource category, float volume, float pitch, long seed
    ) {
        net.neoforged.neoforge.event.PlayLevelSoundEvent.AtEntity event = net.neoforged.neoforge.event.EventHooks.onPlaySoundAtEntity(entity, sound, category, volume, pitch);
        if (event.isCanceled() || event.getSound() == null) return;
        sound = event.getSound();
        category = event.getSource();
        volume = event.getNewVolume();
        pitch = event.getNewPitch();
        if (level instanceof ServerLevel serverLevel) {
            var x = entity.getX();
            var y = entity.getY();
            var z = entity.getZ();
            var packet = new ClientboundSoundPacket(sound, category, x, y, z, volume, pitch, seed);
            var range = sound.value().getRange(volume);
            queueInRange(player, serverLevel.players(), x, y, z, range, packet);
        } else {
            level.playSeededSound(player, entity, sound, category, volume, pitch, seed);
        }
    }

    public static void queueInRange(@Nullable Player except, List<ServerPlayer> players, double x, double y, double z, double range, ClientboundSoundPacket packet) {
        var rangeSqr = range * range;

        for (var p : players) {
            if (p == except || p.distanceToSqr(x, y, z) >= rangeSqr) {
                continue;
            }

            QUEUE.compute(p.getUUID(), (a, b) -> {
                List<ClientboundSoundPacket> list = b == null ? new ArrayList<>() : b;
                list.add(packet);
                return list;
            });
        }
    }
}
