package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.items.SchematicItem;
import com.hollingsworth.arsnouveau.common.items.data.SchematicData;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

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
        BoundingBox bounds = BoundingBox.fromCorners(first, second);
        if (data != null && SchematicItem.saveSchematic(player, bounds, data.uuid())) {
            stack.set(DataComponentRegistry.SCHEMATIC, new SchematicData(data.uuid(), player.getUUID(), Optional.of(new Vec3i(bounds.getXSpan(), bounds.getYSpan(), bounds.getZSpan()))));
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
