package com.hollingsworth.arsnouveau.api;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.OptionalInt;
import java.util.UUID;

// https://github.com/Creators-of-Create/Create/blob/mc1.15/dev/src/main/java/com/simibubi/create/content/contraptions/components/deployer/DeployerFakePlayer.java#L57
public class ANFakePlayer extends FakePlayer {

    public static Player getOrFakePlayer(ServerLevel level, @Nullable LivingEntity player) {
        return player instanceof Player player1 ? player1 : getPlayer(level);
    }

    public static FakePlayer getPlayer(ServerLevel level, @Nullable UUID uuid) {
        return uuid != null ? FakePlayerFactory.get(level, new GameProfile(uuid, "")) : ANFakePlayer.getPlayer(level);
    }

    private static final Connection NETWORK_MANAGER = new Connection(PacketFlow.CLIENTBOUND);


    public static final GameProfile PROFILE =
            new GameProfile(UUID.fromString("7400926d-1007-4e53-880f-b43e67f2bf29"), "Ars_Nouveau");

    private ANFakePlayer(ServerLevel world) {
        super(world, PROFILE);
        connection = new FakePlayNetHandler(world.getServer(), this, CommonListenerCookie.createInitial(PROFILE, false));
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

    private static class FakePlayNetHandler extends ServerGamePacketListenerImpl {
        public FakePlayNetHandler(MinecraftServer server, ServerPlayer playerIn, CommonListenerCookie pCookie) {
            super(server, NETWORK_MANAGER, playerIn, pCookie);
        }

        @Override
        public void send(Packet<?> packetIn) {
        }

        @Override
        public void send(Packet<?> p_243227_, @Nullable PacketSendListener p_243273_) {
        }
    }
}
