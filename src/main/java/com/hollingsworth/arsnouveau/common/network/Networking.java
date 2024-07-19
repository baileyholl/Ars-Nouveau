package com.hollingsworth.arsnouveau.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class Networking {

    private static int ID = 0;

    public static int nextID() {
        return ID++;
    }

    public static void register(final RegisterPayloadHandlersEvent event) {
        // Sets the current network version
        final PayloadRegistrar reg = event.registrar("1");
        reg.playToClient(PacketOpenSpellBook.TYPE, PacketOpenSpellBook.CODEC,  Networking::handle);
        reg.playToClient(ChangeBiomePacket.TYPE, ChangeBiomePacket.CODEC, Networking::handle);
        reg.playToServer(PacketSetLauncher.TYPE, PacketSetLauncher.CODEC, Networking::handle);
        reg.playToServer(ClientToServerStoragePacket.TYPE, ClientToServerStoragePacket.CODEC, Networking::handle);
        reg.playToClient(HighlightAreaPacket.TYPE, HighlightAreaPacket.CODEC, Networking::handle);
        reg.playToClient(NotEnoughManaPacket.TYPE, NotEnoughManaPacket.CODEC, Networking::handle);
        reg.playToClient(PacketAddFadingLight.TYPE, PacketAddFadingLight.CODEC, Networking::handle);
        reg.playToClient(PacketANEffect.TYPE, PacketANEffect.CODEC, Networking::handle);
        reg.playToClient(PacketClientDelayEffect.TYPE, PacketClientDelayEffect.CODEC, Networking::handle);
        reg.playToClient(PacketClientRewindEffect.TYPE, PacketClientRewindEffect.CODEC, Networking::handle);
        reg.playToServer(PacketConsumePotion.TYPE, PacketConsumePotion.CODEC, Networking::handle);
        reg.playToServer(PacketDismountCamera.TYPE, PacketDismountCamera.CODEC, Networking::handle);
        reg.playToServer(PacketDispelFamiliars.TYPE, PacketDispelFamiliars.CODEC, Networking::handle);
        reg.playToClient(PacketAnimEntity.TYPE, PacketAnimEntity.CODEC, Networking::handle);
        reg.playToClient(PacketGetPersistentData.TYPE, PacketGetPersistentData.CODEC, Networking::handle);
        reg.playToServer(PacketHotkeyPressed.TYPE, PacketHotkeyPressed.CODEC, Networking::handle);
        reg.playToClient(PacketJoinedServer.TYPE, PacketJoinedServer.CODEC, Networking::handle);
        reg.playToServer(PacketGenericClientMessage.TYPE, PacketGenericClientMessage.CODEC, Networking::handle);
        reg.playToServer(PacketMountCamera.TYPE, PacketMountCamera.CODEC, Networking::handle);
        reg.playToClient(PacketNoSpamChatMessage.TYPE, PacketNoSpamChatMessage.CODEC, Networking::handle);
        reg.playToClient(PacketOneShotAnimation.TYPE, PacketOneShotAnimation.CODEC, Networking::handle);
        reg.playToClient(PacketOpenGlyphCraft.TYPE, PacketOpenGlyphCraft.CODEC, Networking::handle);
        reg.playToServer(PacketQuickCast.TYPE, PacketQuickCast.CODEC, Networking::handle);
        reg.playToServer(PacketReactiveSpell.TYPE, PacketReactiveSpell.CODEC, Networking::handle);
        reg.playToServer(PacketSetCasterSlot.TYPE, PacketSetCasterSlot.CODEC, Networking::handle);
        reg.playToClient(PacketSetCameraView.TYPE, PacketSetCameraView.CODEC, Networking::handle);
        reg.playToServer(PacketSetScribeRecipe.TYPE, PacketSetScribeRecipe.CODEC, Networking::handle);
        reg.playToServer(PacketSetSound.TYPE, PacketSetSound.CODEC, Networking::handle);
        reg.playToServer(PacketSummonFamiliar.TYPE, PacketSummonFamiliar.CODEC, Networking::handle);
        reg.playToServer(PacketSummonLily.TYPE, PacketSummonLily.CODEC, Networking::handle);
        reg.playToClient(PacketSyncLitEntities.TYPE, PacketSyncLitEntities.CODEC, Networking::handle);
        reg.playToClient(PacketSyncPlayerCap.TYPE, PacketSyncPlayerCap.CODEC, Networking::handle);
        reg.playToClient(PacketSyncTag.TYPE , PacketSyncTag.CODEC, Networking::handle);
        reg.playToClient(PacketTimedEvent.TYPE, PacketTimedEvent.CODEC, Networking::handle);
        reg.playToServer(PacketToggleFamiliar.TYPE, PacketToggleFamiliar.CODEC, Networking::handle);
        reg.playToClient(PacketToggleLight.TYPE, PacketToggleLight.CODEC, Networking::handle);
        reg.playToServer(PacketUnsummonLily.TYPE, PacketUnsummonLily.CODEC, Networking::handle);
        reg.playToClient(PacketUpdateBookGUI.TYPE, PacketUpdateBookGUI.CODEC, Networking::handle);
        reg.playToServer(PacketUpdateCaster.TYPE, PacketUpdateCaster.CODEC, Networking::handle);
        reg.playToClient(PacketUpdateFlight.TYPE, PacketUpdateFlight.CODEC, Networking::handle);
        reg.playToClient(PacketUpdateMana.TYPE, PacketUpdateMana.CODEC, Networking::handle);
        reg.playToServer(PacketUpdateSpellColorAll.TYPE, PacketUpdateSpellColorAll.CODEC, Networking::handle);
        reg.playToServer(PacketUpdateSpellColors.TYPE, PacketUpdateSpellColors.CODEC, Networking::handle);
        reg.playToServer(PacketUpdateSpellSoundAll.TYPE, PacketUpdateSpellSoundAll.CODEC, Networking::handle);
        reg.playToClient(PacketWarpPosition.TYPE, PacketWarpPosition.CODEC, Networking::handle);
        reg.playToClient(PotionSyncPacket.TYPE, PotionSyncPacket.CODEC, Networking::handle);
        reg.playToClient(ServerToClientStoragePacket.TYPE, ServerToClientStoragePacket.CODEC, Networking::handle);
        reg.playToClient(UpdateStorageItemsPacket.TYPE, UpdateStorageItemsPacket.CODEC, Networking::handle);
    }

    private static <T extends AbstractPacket> void handle(T message, IPayloadContext ctx) {
        if (ctx.flow().getReceptionSide() == LogicalSide.SERVER) {
            handleServer(message, ctx);
        } else {
            //separate class to avoid loading client code on server.
            //Using OnlyIn on a method in this class would work too, but is discouraged
            ClientMessageHandler.handleClient(message, ctx);
        }
    }

    private static <T extends AbstractPacket> void handleServer(T message, IPayloadContext ctx) {
        MinecraftServer server = ctx.player().getServer();
        message.onServerReceived(server, (ServerPlayer) ctx.player());
    }

    private static class ClientMessageHandler {

        public static <T extends AbstractPacket> void handleClient(T message, IPayloadContext ctx) {
            Minecraft minecraft = Minecraft.getInstance();
            message.onClientReceived(minecraft, minecraft.player);
        }
    }

    public static void sendToNearbyClient(Level world, BlockPos pos, CustomPacketPayload toSend) {
        if (world instanceof ServerLevel ws) {
            ws.getChunkSource().chunkMap.getPlayers(new ChunkPos(pos), false).stream()
                    .filter(p -> p.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) < 64 * 64)
                    .forEach(p -> Networking.sendToPlayerClient(toSend, p));
        }
    }

    public static void sendToNearbyClient(Level world, Entity e, CustomPacketPayload toSend) {
        sendToNearbyClient(world, e.blockPosition(), toSend);
    }

    public static void sendToPlayerClient(CustomPacketPayload msg, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, msg);
    }

    public static void sendToServer(CustomPacketPayload msg) {
        PacketDistributor.sendToServer(msg);
    }
}
