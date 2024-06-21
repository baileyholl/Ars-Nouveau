package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.setup.registry.RegistryHelper;
import com.hollingsworth.arsnouveau.api.item.IScribeable;
import com.hollingsworth.arsnouveau.api.nbt.ItemstackData;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandler;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class ItemScroll extends ModItem implements IScribeable {

    public ItemScroll() {
        super();
    }

    public ItemScroll(Properties properties) {
        super(properties);
    }

    public abstract SortPref getSortPref(ItemStack stackToStore, ItemStack scrollStack, IItemHandler inventory);

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if(pUsedHand == InteractionHand.MAIN_HAND && !pLevel.isClientSide){
            ItemStack thisStack = pPlayer.getItemInHand(pUsedHand);
            ItemStack otherStack = pPlayer.getItemInHand(InteractionHand.OFF_HAND);
            if(!otherStack.isEmpty()){
                onScribe(pLevel, pPlayer.blockPosition(), pPlayer, InteractionHand.OFF_HAND , thisStack);
                return InteractionResultHolder.success(thisStack);
            }
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }
    // TODO: Move this to API.
    public enum SortPref {
        INVALID,
        LOW,
        HIGH,
        HIGHEST
    }

    @Override
    public boolean onScribe(Level world, BlockPos pos, Player player, InteractionHand handIn, ItemStack thisStack) {
        ItemStack stackToWrite = player.getItemInHand(handIn);
        ItemScrollData scrollData = new ItemScrollData(thisStack);
        return scrollData.writeWithFeedback(player, stackToWrite);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip2, TooltipFlag flagIn) {
        if(!stack.hasTag())
            return;
        ItemScrollData scrollData = new ItemScrollData(stack);
        for (ItemStack s : scrollData.items) {
            tooltip2.add(s.getHoverName());
        }
    }

    public static class ItemScrollData extends ItemstackData {
        private List<ItemStack> items = new ArrayList<>();

        public ItemScrollData(ItemStack stack) {
            super(stack);
            CompoundTag tag = getItemTag(stack);
            if (tag == null || tag.isEmpty())
                return;
            for (String s : tag.getAllKeys()) {
                if (s.contains("item_")) {
                    items.add(ItemStack.of(tag.getCompound(s)));
                }
            }
        }

        public boolean writeWithFeedback(Player player, ItemStack stackToWrite) {
            if (stackToWrite.isEmpty())
                return false;
            if (containsStack(stackToWrite)) {
                PortUtil.sendMessage(player, Component.translatable("ars_nouveau.scribe.item_removed"));
                return remove(stackToWrite);
            }
            if(add(stackToWrite)) {
                PortUtil.sendMessage(player, Component.translatable("ars_nouveau.scribe.item_added"));
                return true;
            }
            return false;
        }

        public boolean containsStack(ItemStack stack){
            return items.stream().anyMatch(s -> ItemStack.isSameItem(s, stack));
        }

        public boolean remove(ItemStack stack){
            boolean didRemove = items.removeIf(s -> ItemStack.isSameItem(s, stack));
            writeItem();
            return didRemove;
        }

        public boolean add(ItemStack stack){
            boolean added = items.add(stack.copy());
            writeItem();
            return added;
        }

        public List<ItemStack> getItems(){
            return items;
        }

        public String getItemKey(ItemStack stack) {
            return "item_" + RegistryHelper.getRegistryName(stack.getItem()).toString();
        }

        @Override
        public void writeToNBT(CompoundTag tag) {
            for (ItemStack s : items) {
                CompoundTag itemTag = new CompoundTag();
                s.save(itemTag);
                tag.put(getItemKey(s), itemTag);
            }
        }

        @Override
        public String getTagString() {
            return "an_scrollData";
        }
    }
}
