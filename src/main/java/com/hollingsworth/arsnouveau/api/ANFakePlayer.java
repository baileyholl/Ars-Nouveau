package com.hollingsworth.arsnouveau.api;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.OptionalInt;
import java.util.UUID;

// ANFakePlayer - simplified to use NeoForge FakePlayer base which already handles
// the connection setup via FakePlayerNetHandler + FakeConnection internally.
public class ANFakePlayer extends FakePlayer {

    public static Player getOrFakePlayer(ServerLevel level, @Nullable LivingEntity player) {
        return player instanceof Player player1 ? player1 : getPlayer(level);
    }

    public static FakePlayer getPlayer(ServerLevel level, @Nullable UUID uuid) {
        return uuid != null ? FakePlayerFactory.get(level, new GameProfile(uuid, "")) : ANFakePlayer.getPlayer(level);
    }

    public static final GameProfile PROFILE =
            new GameProfile(UUID.fromString("7400926d-1007-4e53-880f-b43e67f2bf29"), "Ars_Nouveau");

    private ANFakePlayer(ServerLevel world) {
        super(world, PROFILE);
    }

    @Override
    public double entityInteractionRange() {
        return 4.5;
    }

    private static WeakReference<ANFakePlayer> FAKE_PLAYER = null;

    public static ANFakePlayer getPlayer(ServerLevel world) {
        ANFakePlayer ret = FAKE_PLAYER != null ? FAKE_PLAYER.get() : null;
        if (ret == null) {
            ret = new ANFakePlayer(world);
            FAKE_PLAYER = new WeakReference<>(ret);
        }
        FAKE_PLAYER.get().setLevel(world);
        return FAKE_PLAYER.get();
    }


    @Override
    public OptionalInt openMenu(MenuProvider container) {
        return OptionalInt.empty();
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("AN_Fake_Player");
    }
}
