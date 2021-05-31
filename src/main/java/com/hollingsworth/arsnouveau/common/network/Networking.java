package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.jline.utils.Log;

import java.util.Optional;

public class Networking {
    public static SimpleChannel INSTANCE;

    private static int ID = 0;
    public static int nextID(){return ID++;}
    public static void registerMessages(){
        System.out.println("Registering packets!!");
        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(ArsNouveau.MODID, "network"), () -> "1.0", s->true, s->true);

        INSTANCE.registerMessage(nextID(),
                PacketOpenSpellBook.class,
                PacketOpenSpellBook::toBytes,
                PacketOpenSpellBook::new,
                PacketOpenSpellBook::handle);
        INSTANCE.registerMessage(nextID(),
                PacketUpdateSpellbook.class,
                PacketUpdateSpellbook::toBytes,
                PacketUpdateSpellbook::new,
                PacketUpdateSpellbook::handle);

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
                PacketBeam.class,
                PacketBeam::encode,
                PacketBeam::decode,
                PacketBeam.Handler::handle);
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
                PacketOpenRitualBook.class,
                PacketOpenRitualBook::toBytes,
                PacketOpenRitualBook::new,
                PacketOpenRitualBook::handle);

        INSTANCE.registerMessage(nextID(),
                PacketSetRitual.class,
                PacketSetRitual::toBytes,
                PacketSetRitual::new,
                PacketSetRitual::handle);

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
    }

    public static void sendToNearby(World world, BlockPos pos, Object toSend){
        if (world instanceof ServerWorld) {
            ServerWorld ws = (ServerWorld) world;
            ws.getChunkSource().chunkMap.getPlayers(new ChunkPos(pos), false)
                    .filter(p -> p.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) < 64 * 64)
                    .forEach(p -> INSTANCE.send(PacketDistributor.PLAYER.with(() -> p), toSend));
        }
    }

    public static void sendToNearby(World world, Entity e, Object toSend) {
        sendToNearby(world, e.blockPosition(), toSend);
    }

    public static void sendToPlayer(Object msg, PlayerEntity player) {
        if (EffectiveSide.get() == LogicalSide.SERVER) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
            INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), msg);
        }
    }
}
