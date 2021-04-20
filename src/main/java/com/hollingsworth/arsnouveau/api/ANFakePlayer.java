package com.hollingsworth.arsnouveau.api;

import com.mojang.authlib.GameProfile;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.network.IPacket;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketDirection;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;

import java.util.OptionalInt;
import java.util.UUID;

// https://github.com/Creators-of-Create/Create/blob/mc1.15/dev/src/main/java/com/simibubi/create/content/contraptions/components/deployer/DeployerFakePlayer.java#L57
public class ANFakePlayer extends FakePlayer {

    private static final NetworkManager NETWORK_MANAGER = new NetworkManager(PacketDirection.CLIENTBOUND);
    public static final GameProfile PROFILE =
            new GameProfile(UUID.fromString("7400926d-1007-4e53-880f-b43e67f2bf29"), "Ars_Nouveau");

    public ANFakePlayer(ServerWorld world) {
        super(world, PROFILE);
        connection = new FakePlayNetHandler(world.getServer(), this);
    }

    @Override
    public OptionalInt openMenu(INamedContainerProvider container) {
        return OptionalInt.empty();
    }

    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent("AN_Fake_Player");
    }
    private static class FakePlayNetHandler extends ServerPlayNetHandler {
        public FakePlayNetHandler(MinecraftServer server, ServerPlayerEntity playerIn) {
            super(server, NETWORK_MANAGER, playerIn);
        }

        @Override
        public void send(IPacket<?> packetIn) {}

        @Override
        public void send(IPacket<?> packetIn, GenericFutureListener<? extends Future<? super Void>> futureListeners) { }
    }
}
