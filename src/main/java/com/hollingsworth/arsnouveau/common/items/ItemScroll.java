package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.item.IScribeable;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item.Properties;

public class ItemScroll extends ModItem implements IScribeable {
    public ItemScroll(String reg) {
        super(reg);
    }
    public ItemScroll(Properties properties, String reg) {
        super(properties, reg);
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if(!stack.hasTag())
            stack.setTag(new CompoundNBT());
    }

    public static String ITEM_PREFIX = "item_";

    public List<ItemStack> getItems(ItemStack stack){
        CompoundNBT tag = stack.getTag();
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

    public boolean addItem(ItemStack itemToAdd, CompoundNBT tag){
        CompoundNBT itemTag = new CompoundNBT();
        itemToAdd.save(itemTag);
        tag.put(getItemKey(itemToAdd), itemTag);
        return true;
    }

    public boolean removeItem(ItemStack itemToRemove, CompoundNBT tag){
        tag.remove(getItemKey(itemToRemove));
        return true;
    }

    public static boolean containsItem(ItemStack stack, CompoundNBT tag){
        return tag != null && tag.contains(getItemKey(stack));
    }

    public static String getItemKey(ItemStack stack){
        return ITEM_PREFIX + stack.getItem().getRegistryName().toString();
    }

    @Override
    public boolean onScribe(World world, BlockPos pos, PlayerEntity player, Hand handIn, ItemStack thisStack) {
        ItemScroll itemScroll = (ItemScroll) thisStack.getItem();
        ItemStack stackToWrite = player.getItemInHand(handIn);
        CompoundNBT tag = thisStack.getTag();
        if(stackToWrite == ItemStack.EMPTY || tag == null)
            return false;

        if(itemScroll.containsItem(stackToWrite, tag)) {
            PortUtil.sendMessage(player, new TranslationTextComponent("ars_nouveau.scribe.item_removed"));
            return removeItem(stackToWrite, tag);
        }
        PortUtil.sendMessage(player, new TranslationTextComponent("ars_nouveau.scribe.item_added"));
        return itemScroll.addItem(stackToWrite, tag);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip2, ITooltipFlag flagIn) {
        CompoundNBT tag = stack.getTag();
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
