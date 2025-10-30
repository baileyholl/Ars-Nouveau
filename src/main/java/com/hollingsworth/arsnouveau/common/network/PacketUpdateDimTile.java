package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.tile.PlanariumTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class PacketUpdateDimTile extends AbstractPacket {
    public static final Type<PacketUpdateDimTile> TYPE = new Type<>(ArsNouveau.prefix("update_dim_block"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketUpdateDimTile> CODEC = StreamCodec.ofMember(PacketUpdateDimTile::toBytes, PacketUpdateDimTile::new);

    public BlockPos pos;
    public StructureTemplate structureTemplate;

    public PacketUpdateDimTile(BlockPos pos, StructureTemplate structureTemplate) {
        this.pos = pos;
        this.structureTemplate = structureTemplate;
    }

    public PacketUpdateDimTile(RegistryFriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.structureTemplate = new StructureTemplate();
        this.structureTemplate.load(BuiltInRegistries.BLOCK.asLookup(), (CompoundTag) buf.readNbt(NbtAccounter.unlimitedHeap()));
    }

    @Override
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeNbt(structureTemplate.save(new CompoundTag()));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        ClientLevel world = minecraft.level;
        if (world.getBlockEntity(pos) instanceof PlanariumTile planariumTile) {
            planariumTile.setTemplateClientSide(structureTemplate);
        }
    }
}
