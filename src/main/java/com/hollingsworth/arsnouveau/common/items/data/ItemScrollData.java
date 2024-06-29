package com.hollingsworth.arsnouveau.common.items.data;

import com.hollingsworth.arsnouveau.api.item.NBTComponent;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ItemScrollData implements NBTComponent<ItemScrollData>, TooltipProvider {

    public static final Codec<ItemScrollData> CODEC = ItemStack.CODEC.listOf().xmap(ItemScrollData::new, (i) -> i.items);
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemScrollData> STREAM_CODEC = StreamCodec.composite(ItemStack.LIST_STREAM_CODEC, (i) -> i.items, ItemScrollData::new);

    private final List<ItemStack> items;

    public ItemScrollData(List<ItemStack> items) {
        this.items = List.copyOf(items);
    }

    public ItemScrollData(){
        this(List.of());
    }

    public boolean containsStack(ItemStack stack) {
        return contains(items, stack);
    }

    public static boolean contains(List<ItemStack> list, ItemStack stack) {
        return list.stream().anyMatch(s -> ItemStack.isSameItem(s, stack));
    }

    public Iterable<ItemStack> getItems() {
        return items;
    }

    @Override
    public Codec<ItemScrollData> getCodec() {
        return CODEC;
    }

    public Mutable mutable() {
        return new Mutable(this);
    }

    @Override
    public void addToTooltip(Item.TooltipContext pContext, Consumer<Component> pTooltipAdder, TooltipFlag pTooltipFlag) {
        for (ItemStack s : items) {
            pTooltipAdder.accept(s.getHoverName());
        }
    }

    public static class Mutable {
        private final List<ItemStack> list;

        public Mutable(ItemScrollData data) {
            this.list = new ArrayList<>(data.items);
        }

        public boolean add(ItemStack stack) {
            return list.add(stack.copy());
        }

        public boolean remove(ItemStack stack) {
            return list.remove(stack.copy());
        }

        public List<ItemStack> getItems() {
            return list;
        }

        public ItemScrollData toImmutable() {
            return new ItemScrollData(list);
        }

        public boolean writeWithFeedback(Player player, ItemStack stackToWrite) {
            if (stackToWrite.isEmpty())
                return false;
            if (ItemScrollData.contains(list, stackToWrite)) {
                PortUtil.sendMessage(player, Component.translatable("ars_nouveau.scribe.item_removed"));
                return remove(stackToWrite);
            }
            if (add(stackToWrite)) {
                PortUtil.sendMessage(player, Component.translatable("ars_nouveau.scribe.item_added"));
                return true;
            }
            return false;
        }
    }
}
