package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.items.DominionWand;
import com.hollingsworth.arsnouveau.common.items.data.DominionWandData;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PacketUpdateDominionWand extends AbstractPacket {
    public static final Type<PacketUpdateDominionWand> TYPE = new Type<>(ArsNouveau.prefix("set_wand_mode"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketUpdateDominionWand> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            s -> s.slot,
            PacketUpdateDominionWand::new
    );

    public int slot;

    public PacketUpdateDominionWand(int slot) {
        this.slot = slot;
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {

        ItemStack stack = player.getMainHandItem().getItem() instanceof DominionWand ? player.getMainHandItem() : player.getOffhandItem();

        stack.set(DataComponentRegistry.DOMINION_WAND, switch (slot) {
            default -> stack.getOrDefault(DataComponentRegistry.DOMINION_WAND, new DominionWandData())
                    .setFace(null)
                    .storeEntity(DominionWandData.NULL_ENTITY)
                    .storePos(null);
            case 1 ->
                    stack.getOrDefault(DataComponentRegistry.DOMINION_WAND, new DominionWandData()).toggleStrictMode(false).toggleRemoveMode(false);
            case 2 ->
                    stack.getOrDefault(DataComponentRegistry.DOMINION_WAND, new DominionWandData()).toggleStrictMode(true).toggleRemoveMode(false);
            case 3 ->
                    stack.getOrDefault(DataComponentRegistry.DOMINION_WAND, new DominionWandData()).toggleRemoveMode(true).toggleStrictMode(false);
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
