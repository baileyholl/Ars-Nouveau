package com.hollingsworth.arsnouveau.common.items.data;

import com.hollingsworth.arsnouveau.common.items.PotionFlask;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;

public class PotionLauncherData {
    private PotionData lastDataForRender;
    private int lastSlot;
    public int amountLeft;

    public PotionLauncherData(ItemStack stack) {
        super(stack);
        CompoundTag tag = getItemTag(stack);
        if(tag == null)
            return;
        lastDataForRender = PotionData.fromTag(tag.getCompound("lastDataForRender"));
        lastSlot = tag.getInt("lastSlot");
        amountLeft = tag.getInt("amountLeft");
    }

    public PotionData getPotionDataFromSlot(Player player){
        if(lastSlot < 0 || lastSlot >= player.inventory.getContainerSize())
            return new PotionData();
        ItemStack stack = player.inventory.getItem(lastSlot);
        return new PotionData(stack);
    }

    public PotionData expendPotion(Player player){
        if(lastSlot >= player.inventory.getContainerSize())
            return new PotionData();
        ItemStack item = player.inventory.getItem(lastSlot);
        if(item.getItem() instanceof PotionFlask){
            PotionFlask.FlaskData flaskData = new PotionFlask.FlaskData(item);
            if(flaskData.getCount() <= 0 || flaskData.getPotion().isEmpty())
                return new PotionData();
            PotionData data = flaskData.getPotion().clone();
            flaskData.setCount(flaskData.getCount() - 1);
            setAmountLeft(flaskData.getCount());
            return data;
        }else if(item.getItem() instanceof PotionItem){
            PotionData data = new PotionData(item).clone();
            if(data.isEmpty())
                return new PotionData();
            item.shrink(1);
            player.inventory.add(new ItemStack(Items.GLASS_BOTTLE));
            setAmountLeft(0);
            return data;
        }
        return new PotionData();
    }

    public void setAmountLeft(int amount){
        amountLeft = amount;
        writeItem();
    }

    public void setLastSlot(int lastSlot) {
        this.lastSlot = lastSlot;
        writeItem();
    }

    public void setLastDataForRender(PotionData lastDataForRender) {
        this.lastDataForRender = lastDataForRender;
        writeItem();
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        tag.putInt("lastSlot", lastSlot);
        tag.put("lastDataForRender", lastDataForRender.toTag());
        tag.putInt("amountLeft", amountLeft);
    }

    public PotionData getLastDataForRender() {
        return lastDataForRender;
    }

    @Override
    public String getTagString() {
        return "potion_launcher";
    }
}
