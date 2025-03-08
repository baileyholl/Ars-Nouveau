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
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class Networking {

    public static void register(final RegisterPayloadHandlersEvent event) {
        // Sets the current network version
        final PayloadRegistrar reg = event.registrar("1");
        reg.playToClient(PacketOpenSpellBook.TYPE, PacketOpenSpellBook.CODEC,  Networking::handle);
        reg.playToClient(ChangeBiomePacket.TYPE, ChangeBiomePacket.CODEC, Networking::handle);
        reg.playToServer(PacketSetLauncher.TYPE, PacketSetLauncher.CODEC, Networking::handle);
        reg.playToServer(ClientToServerStoragePacket.TYPE, ClientToServerStoragePacket.CODEC, Networking::handle);
        reg.playBidirectional(SetTerminalSettingsPacket.TYPE, SetTerminalSettingsPacket.CODEC, new DirectionalPayloadHandler<>((msg, ctx) -> ClientMessageHandler.handleClient(msg, ctx), (msg, ctx) -> msg.onServerReceived(ctx.player().getServer(), (ServerPlayer) ctx.player())));
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
        reg.playToClient(PacketInitDocs.TYPE, PacketInitDocs.CODEC, Networking::handle);
        reg.playToClient(PacketExportDocs.TYPE, PacketExportDocs.CODEC, Networking::handle);
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
        reg.playToServer(PacketSummonDog.TYPE, PacketSummonDog.CODEC, Networking::handle);
        reg.playToClient(PacketSyncLitEntities.TYPE, PacketSyncLitEntities.CODEC, Networking::handle);
        reg.playToClient(PacketSyncPlayerCap.TYPE, PacketSyncPlayerCap.CODEC, Networking::handle);
        reg.playToClient(PacketSyncTag.TYPE , PacketSyncTag.CODEC, Networking::handle);
        reg.playToClient(PacketTimedEvent.TYPE, PacketTimedEvent.CODEC, Networking::handle);
        reg.playToServer(PacketToggleFamiliar.TYPE, PacketToggleFamiliar.CODEC, Networking::handle);
        reg.playToClient(PacketToggleLight.TYPE, PacketToggleLight.CODEC, Networking::handle);
        reg.playToServer(PacketUnsummonDog.TYPE, PacketUnsummonDog.CODEC, Networking::handle);
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
        reg.playToClient(PacketUpdateGlowColor.TYPE, PacketUpdateGlowColor.CODEC, Networking::handle);
        reg.playToServer(PacketUpdateDominionWand.TYPE, PacketUpdateDominionWand.CODEC, Networking::handle);
        reg.playToServer(PacketCastSpell.TYPE, PacketCastSpell.CODEC, Networking::handle);

    }

    public static <T extends AbstractPacket> void handle(T message, IPayloadContext ctx) {
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
            PacketDistributor.sendToPlayersTrackingChunk(ws, new ChunkPos(pos), toSend);
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
