package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.common.block.tile.ScribesTile;
import com.hollingsworth.arsnouveau.common.crafting.recipes.GlyphRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.network.NetworkEvent;
import java.util.function.Supplier;

public class PacketSetScribeRecipe {
    BlockPos scribePos;
    ResourceLocation recipeID;

    public PacketSetScribeRecipe(FriendlyByteBuf buf) {
        this.scribePos = buf.readBlockPos();
        this.recipeID = buf.readResourceLocation();
    }

    //Encoder
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(scribePos);
        buf.writeResourceLocation(recipeID);
    }

    public PacketSetScribeRecipe(BlockPos scribesPos, ResourceLocation resourceLocation) {
        this.scribePos = scribesPos;
        this.recipeID = resourceLocation;
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null)
                return;
            if (player.level.getBlockEntity(scribePos) instanceof ScribesTile scribesTile) {
                Recipe recipe = player.level.getRecipeManager().byKey(recipeID).orElse(null);
                if (recipe instanceof GlyphRecipe glyphRecipe) {
                    scribesTile.setRecipe(glyphRecipe, player);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
