package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.RegistryHelper;
import com.hollingsworth.arsnouveau.api.item.IScribeable;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;

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

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if(!stack.hasTag())
            stack.setTag(new CompoundTag());
    }

    public abstract SortPref getSortPref(ItemStack stackToStore, CompoundTag scrollTag, IItemHandler inventory);

    public enum SortPref {
        INVALID,
        LOW,
        HIGH,
        HIGHEST
    }

    public static String ITEM_PREFIX = "item_";

    public List<ItemStack> getItems(ItemStack stack){
        CompoundTag tag = stack.getTag();
        List<ItemStack> stacks = new ArrayList<>();
        if(tag == null)
            return stacks;

        for(String s : tag.getAllKeys()){
            if(s.contains(ITEM_PREFIX)){
                stacks.add(ItemStack.of(tag.getCompound(s)));
            }
        }
        return stacks;
    }

    public static boolean addItem(ItemStack itemToAdd, CompoundTag tag){
        CompoundTag itemTag = new CompoundTag();
        itemToAdd.save(itemTag);
        tag.put(getItemKey(itemToAdd), itemTag);
        return true;
    }

    public static boolean removeItem(ItemStack itemToRemove, CompoundTag tag){
        tag.remove(getItemKey(itemToRemove));
        return true;
    }

    public static boolean containsItem(ItemStack stack, CompoundTag tag){
        return tag != null && tag.contains(getItemKey(stack));
    }

    public static String getItemKey(ItemStack stack){
        return ITEM_PREFIX + RegistryHelper.getRegistryName(stack.getItem()).toString();
    }

    @Override
    public boolean onScribe(Level world, BlockPos pos, Player player, InteractionHand handIn, ItemStack thisStack) {
        return ItemScroll.scribe(world, pos, player, handIn, thisStack);
    }

    public static boolean scribe(Level world, BlockPos pos, Player player, InteractionHand handIn, ItemStack thisStack){
        ItemStack stackToWrite = player.getItemInHand(handIn);
        CompoundTag tag = thisStack.getTag();
        if(stackToWrite == ItemStack.EMPTY || tag == null)
            return false;

        if(containsItem(stackToWrite, tag)) {
            PortUtil.sendMessage(player, Component.translatable("ars_nouveau.scribe.item_removed"));
            return removeItem(stackToWrite, tag);
        }
        PortUtil.sendMessage(player, Component.translatable("ars_nouveau.scribe.item_added"));
        return addItem(stackToWrite, tag);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip2, TooltipFlag flagIn) {
        CompoundTag tag = stack.getTag();
        if(tag == null)
            return;
        List<ItemStack> stacks = new ArrayList<>();
        for(String s : tag.getAllKeys()){
            if(s.contains(ITEM_PREFIX)){
                stacks.add(ItemStack.of(tag.getCompound(s)));
            }
        }
        for(ItemStack s : stacks){
            tooltip2.add(s.getHoverName());
        }
    }
}
