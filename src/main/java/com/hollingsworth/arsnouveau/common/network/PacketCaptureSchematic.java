package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.items.SchematicItem;
import com.hollingsworth.arsnouveau.common.items.data.SchematicData;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.jetbrains.annotations.NotNull;

public class PacketCaptureSchematic extends AbstractPacket {
    public static final Type<PacketCaptureSchematic> TYPE = new Type<>(ArsNouveau.prefix("capture_schematic"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketCaptureSchematic> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            p -> p.first,
            BlockPos.STREAM_CODEC,
            p -> p.second,
            PacketCaptureSchematic::new
    );

    public BlockPos first;
    public BlockPos second;

    public PacketCaptureSchematic(BlockPos first, BlockPos second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        ItemStack stack = SchematicItem.getHeldSchematic(player);
        SchematicData data = stack.get(DataComponentRegistry.SCHEMATIC);
        if (data != null && SchematicItem.saveSchematic(player, BoundingBox.fromCorners(first, second), data.uuid())) {
            stack.set(DataComponentRegistry.SCHEMATIC, new SchematicData(data.uuid(), player.getUUID()));
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
