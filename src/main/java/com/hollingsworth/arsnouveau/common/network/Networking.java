package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.NetworkDirection;
import net.neoforged.neoforge.network.NetworkRegistry;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.simple.SimpleChannel;
import java.util.Optional;

public class Networking {
    public static SimpleChannel INSTANCE;

    private static int ID = 0;

    public static int nextID() {
        return ID++;
    }

    public static void registerMessages() {
        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(ArsNouveau.MODID, "network"), () -> "1.0", s -> true, s -> true);

        INSTANCE.registerMessage(nextID(),
                PacketOpenSpellBook.class,
                PacketOpenSpellBook::toBytes,
                PacketOpenSpellBook::new,
                PacketOpenSpellBook::handle);
        INSTANCE.registerMessage(nextID(),
                PacketUpdateCaster.class,
                PacketUpdateCaster::toBytes,
                PacketUpdateCaster::new,
                PacketUpdateCaster::handle);

        INSTANCE.registerMessage(nextID(),
                PacketUpdateBookGUI.class,
                PacketUpdateBookGUI::toBytes,
                PacketUpdateBookGUI::new,
                PacketUpdateBookGUI::handle);
        INSTANCE.registerMessage(nextID(),
                PacketUpdateMana.class,
                PacketUpdateMana::toBytes,
                PacketUpdateMana::new,
                PacketUpdateMana::handle);
        INSTANCE.registerMessage(nextID(),
                PacketSetBookMode.class,
                PacketSetBookMode::toBytes,
                PacketSetBookMode::new,
                PacketSetBookMode::handle);

        INSTANCE.registerMessage(nextID(),
                PacketANEffect.class,
                PacketANEffect::encode,
                PacketANEffect::decode,
                PacketANEffect.Handler::handle);

        INSTANCE.registerMessage(nextID(),
                PacketReactiveSpell.class,
                PacketReactiveSpell::toBytes,
                PacketReactiveSpell::new,
                PacketReactiveSpell::handle);
        INSTANCE.registerMessage(nextID(),
                PacketWarpPosition.class,
                PacketWarpPosition::encode,
                PacketWarpPosition::decode,
                PacketWarpPosition.Handler::handle);
        INSTANCE.registerMessage(nextID(),
                PacketUpdateSpellColors.class,
                PacketUpdateSpellColors::toBytes,
                PacketUpdateSpellColors::new,
                PacketUpdateSpellColors::handle);
        INSTANCE.registerMessage(nextID(),
                PacketOneShotAnimation.class,
                PacketOneShotAnimation::encode,
                PacketOneShotAnimation::decode,
                PacketOneShotAnimation.Handler::handle);

        INSTANCE.registerMessage(nextID(),
                PacketAnimEntity.class,
                PacketAnimEntity::encode,
                PacketAnimEntity::decode,
                PacketAnimEntity.Handler::handle);


        INSTANCE.registerMessage(nextID(),
                PacketGetPersistentData.class,
                PacketGetPersistentData::toBytes,
                PacketGetPersistentData::new,
                PacketGetPersistentData::handle);

        INSTANCE.registerMessage(nextID(),
                PacketNoSpamChatMessage.class,
                PacketNoSpamChatMessage::toBytes,
                PacketNoSpamChatMessage::new,
                PacketNoSpamChatMessage::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));

        INSTANCE.registerMessage(nextID(),
                PacketUpdateFlight.class,
                PacketUpdateFlight::toBytes,
                PacketUpdateFlight::new,
                PacketUpdateFlight::handle);
        INSTANCE.registerMessage(nextID(),
                PacketClientDelayEffect.class,
                PacketClientDelayEffect::toBytes,
                PacketClientDelayEffect::new,
                PacketClientDelayEffect::handle);

        INSTANCE.registerMessage(nextID(),
                PacketTimedEvent.class,
                PacketTimedEvent::toBytes,
                PacketTimedEvent::new,
                PacketTimedEvent::handle);
        INSTANCE.registerMessage(nextID(),
                PacketSummonFamiliar.class,
                PacketSummonFamiliar::toBytes,
                PacketSummonFamiliar::new,
                PacketSummonFamiliar::handle);
        INSTANCE.registerMessage(nextID(),
                PacketSyncPlayerCap.class,
                PacketSyncPlayerCap::toBytes,
                PacketSyncPlayerCap::new,
                PacketSyncPlayerCap::handle);

        INSTANCE.registerMessage(nextID(),
                PacketTogglePathing.class,
                PacketTogglePathing::toBytes,
                PacketTogglePathing::new,
                PacketTogglePathing::handle);

        INSTANCE.registerMessage(nextID(),
                PacketHotkeyPressed.class,
                PacketHotkeyPressed::toBytes,
                PacketHotkeyPressed::new,
                PacketHotkeyPressed::handle);

        INSTANCE.registerMessage(nextID(),
                PacketOpenGlyphCraft.class,
                PacketOpenGlyphCraft::toBytes,
                PacketOpenGlyphCraft::new,
                PacketOpenGlyphCraft::handle);

        INSTANCE.registerMessage(nextID(),
                PacketSetScribeRecipe.class,
                PacketSetScribeRecipe::toBytes,
                PacketSetScribeRecipe::new,
                PacketSetScribeRecipe::handle);
        INSTANCE.registerMessage(nextID(),
                PacketToggleLight.class,
                PacketToggleLight::toBytes,
                PacketToggleLight::new,
                PacketToggleLight::handle);
        INSTANCE.registerMessage(nextID(),
                PacketAddFadingLight.class,
                PacketAddFadingLight::encode,
                PacketAddFadingLight::decode,
                PacketAddFadingLight.Handler::handle);

        INSTANCE.registerMessage(nextID(),
                PacketSetSound.class,
                PacketSetSound::toBytes,
                PacketSetSound::new,
                PacketSetSound::handle);
        INSTANCE.registerMessage(nextID(),
                PacketMountCamera.class,
                PacketMountCamera::encode,
                PacketMountCamera::decode,
                PacketMountCamera::onMessage);
        INSTANCE.registerMessage(nextID(),
                PacketDismountCamera.class,
                PacketDismountCamera::encode,
                PacketDismountCamera::decode,
                PacketDismountCamera::onMessage);
        INSTANCE.registerMessage(nextID(),
                PacketSetCameraView.class,
                PacketSetCameraView::encode,
                PacketSetCameraView::decode,
                PacketSetCameraView::onMessage);
        INSTANCE.registerMessage(nextID(),
                PacketSyncLitEntities.class,
                PacketSyncLitEntities::toBytes,
                PacketSyncLitEntities::new,
                PacketSyncLitEntities::handle);
        INSTANCE.registerMessage(nextID(),
                PacketSyncTag.class,
                PacketSyncTag::encode,
                PacketSyncTag::decode,
                PacketSyncTag.Handler::handle);
        INSTANCE.registerMessage(nextID(),
                PacketQuickCast.class,
                PacketQuickCast::toBytes,
                PacketQuickCast::new,
                PacketQuickCast::handle);
        INSTANCE.registerMessage(nextID(),
                PacketConsumePotion.class,
                PacketConsumePotion::toBytes,
                PacketConsumePotion::new,
                PacketConsumePotion::handle);
        INSTANCE.registerMessage(nextID(),
                PacketSetLauncher.class,
                PacketSetLauncher::toBytes,
                PacketSetLauncher::new,
                PacketSetLauncher::handle);
        INSTANCE.registerMessage(nextID(), ChangeBiomePacket.class, ChangeBiomePacket::encode, ChangeBiomePacket::new, ChangeBiomePacket.Handler::onMessage);
        INSTANCE.registerMessage(nextID(), ServerToClientStoragePacket.class, ServerToClientStoragePacket::toBytes, ServerToClientStoragePacket::new, ServerToClientStoragePacket.Handler::onMessage);
        INSTANCE.registerMessage(nextID(), ClientToServerStoragePacket.class, ClientToServerStoragePacket::toBytes, ClientToServerStoragePacket::new, ClientToServerStoragePacket.Handler::onMessage);
        INSTANCE.registerMessage(nextID(), HighlightAreaPacket.class, HighlightAreaPacket::encode, HighlightAreaPacket::decode, HighlightAreaPacket.Handler::handle);
        INSTANCE.registerMessage(nextID(), PacketToggleFamiliar.class, PacketToggleFamiliar::toBytes, PacketToggleFamiliar::new, PacketToggleFamiliar::handle);
        INSTANCE.registerMessage(nextID(), PacketDispelFamiliars.class, PacketDispelFamiliars::toBytes, PacketDispelFamiliars::new, PacketDispelFamiliars::handle);
        INSTANCE.registerMessage(nextID(), PacketGenericClientMessage.class, PacketGenericClientMessage::toBytes, PacketGenericClientMessage::new, PacketGenericClientMessage::handle);
        INSTANCE.registerMessage(nextID(), NotEnoughManaPacket.class, NotEnoughManaPacket::encode, NotEnoughManaPacket::decode, NotEnoughManaPacket::handle);
        INSTANCE.registerMessage(nextID(), PacketSummonLily.class, PacketSummonLily::toBytes, PacketSummonLily::new, PacketSummonLily::handle);
        INSTANCE.registerMessage(nextID(), PacketJoinedServer.class, PacketJoinedServer::toBytes, PacketJoinedServer::new, PacketJoinedServer.Handler::handle);
        INSTANCE.registerMessage(nextID(), PacketUnsummonLily.class, PacketUnsummonLily::toBytes, PacketUnsummonLily::new, PacketUnsummonLily::handle);
        INSTANCE.registerMessage(nextID(), SyncPathMessage.class, SyncPathMessage::toBytes, SyncPathMessage::new, SyncPathMessage.Handler::handle);
        INSTANCE.registerMessage(nextID(), SyncPathReachedMessage.class, SyncPathReachedMessage::toBytes, SyncPathReachedMessage::new, SyncPathReachedMessage.Handler::handle);
        INSTANCE.registerMessage(nextID(), PacketUpdateSpellColorAll.class, PacketUpdateSpellColorAll::toBytes, PacketUpdateSpellColorAll::new, PacketUpdateSpellColorAll::handle);
        INSTANCE.registerMessage(nextID(), PacketUpdateSpellSoundAll.class, PacketUpdateSpellSoundAll::toBytes, PacketUpdateSpellSoundAll::new, PacketUpdateSpellSoundAll::handle);
        INSTANCE.registerMessage(nextID(), PotionSyncPacket.class, PotionSyncPacket::toBytes, PotionSyncPacket::new, PotionSyncPacket::handle);
        INSTANCE.registerMessage(nextID(), PacketClientRewindEffect.class, PacketClientRewindEffect::toBytes, PacketClientRewindEffect::new, PacketClientRewindEffect::handle);
    }

    public static void sendToNearby(Level world, BlockPos pos, Object toSend) {
        if (world instanceof ServerLevel ws) {
            ws.getChunkSource().chunkMap.getPlayers(new ChunkPos(pos), false).stream()
                    .filter(p -> p.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) < 64 * 64)
                    .forEach(p -> INSTANCE.send(PacketDistributor.PLAYER.with(() -> p), toSend));
        }
    }

    public static void sendToNearby(Level world, Entity e, Object toSend) {
        sendToNearby(world, e.blockPosition(), toSend);
    }

    public static void sendToPlayerClient(Object msg, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), msg);
    }

    public static void sendToServer(Object msg) {
        INSTANCE.sendToServer(msg);
    }
}
