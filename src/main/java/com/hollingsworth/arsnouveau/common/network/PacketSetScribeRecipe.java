package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.tile.ScribesTile;
import com.hollingsworth.arsnouveau.common.crafting.recipes.GlyphRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.Recipe;

public class PacketSetScribeRecipe extends AbstractPacket{
    public static final Type<PacketSetScribeRecipe> TYPE = new Type<>(ArsNouveau.prefix("set_scribe_recipe"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketSetScribeRecipe> CODEC = StreamCodec.ofMember(PacketSetScribeRecipe::toBytes, PacketSetScribeRecipe::new);
    BlockPos scribePos;
    ResourceLocation recipeID;

    public PacketSetScribeRecipe(RegistryFriendlyByteBuf buf) {
        this.scribePos = buf.readBlockPos();
        this.recipeID = buf.readResourceLocation();
    }

    //Encoder
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeBlockPos(scribePos);
        buf.writeResourceLocation(recipeID);
    }

    public PacketSetScribeRecipe(BlockPos scribesPos, ResourceLocation resourceLocation) {
        this.scribePos = scribesPos;
        this.recipeID = resourceLocation;
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        if (player.level.getBlockEntity(scribePos) instanceof ScribesTile scribesTile) {
            Recipe recipe = player.level.getRecipeManager().byKey(recipeID).orElse(null);
            if (recipe instanceof GlyphRecipe glyphRecipe) {
                scribesTile.setRecipe(glyphRecipe, player);
            }
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
