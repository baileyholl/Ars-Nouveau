package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.entity.ISummon;
import com.hollingsworth.arsnouveau.common.items.SummonerBell;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PacketUpdateSummonerChime extends AbstractPacket {
    public static final Type<PacketUpdateSummonerChime> TYPE = new Type<>(ArsNouveau.prefix("set_chime_mode"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketUpdateSummonerChime> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            s -> s.mode,
            PacketUpdateSummonerChime::new
    );

    public int mode;

    public PacketUpdateSummonerChime(int slot) {
        this.mode = slot;
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {

        ItemStack stack = player.getMainHandItem().getItem() instanceof SummonerBell ? player.getMainHandItem() : player.getOffhandItem();
        stack.set(DataComponentRegistry.SUMMON_BEHAVIOR, ISummon.SummonBehavior.fromId(mode));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
