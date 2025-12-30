package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.potion.IPotionProvider;
import com.hollingsworth.arsnouveau.api.registry.PotionProviderRegistry;
import com.hollingsworth.arsnouveau.common.items.PotionFlask;
import com.hollingsworth.arsnouveau.common.util.PotionUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionContents;

public class PacketConsumePotion extends AbstractPacket {
    public static final Type<PacketConsumePotion> TYPE = new Type<>(ArsNouveau.prefix("consume_potion"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketConsumePotion> CODEC = StreamCodec.ofMember(PacketConsumePotion::toBytes, PacketConsumePotion::new);

    int inventorySlot;

    public PacketConsumePotion(RegistryFriendlyByteBuf buf) {
        this.inventorySlot = buf.readInt();
    }

    //Encoder
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeInt(inventorySlot);
    }

    public PacketConsumePotion(int inventorySlot) {
        this.inventorySlot = inventorySlot;
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        if (player == null)
            return;

        if (inventorySlot >= player.inventory.getContainerSize())
            return;
        ItemStack stack = player.inventory.getItem(inventorySlot);
        if (stack.getItem() instanceof PotionItem) {
            PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
            if (contents == null) {
                return;
            }
            PotionUtil.applyContents(contents, player, player, player);
            if (!player.hasInfiniteMaterials()) {
                stack.shrink(1);
            }
            player.inventory.add(new ItemStack(Items.GLASS_BOTTLE));
            player.level.playSound(null, player.blockPosition(), SoundEvents.GENERIC_DRINK, SoundSource.PLAYERS, 0.5f, player.level.random.nextFloat() * 0.1F + 0.9F);
        } else if (stack.getItem() instanceof PotionFlask) {
            IPotionProvider data = PotionProviderRegistry.from(stack);
            if (data == null || data.getPotionData(stack) == PotionContents.EMPTY || data.usesRemaining(stack) <= 0)
                return;

            stack.getItem().finishUsingItem(stack, minecraftServer.getLevel(player.level().dimension()), player);
            player.level.playSound(null, player.blockPosition(), SoundEvents.GENERIC_DRINK, SoundSource.PLAYERS, 0.5f, player.level.random.nextFloat() * 0.1F + 0.9F);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
