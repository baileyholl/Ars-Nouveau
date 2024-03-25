package com.hollingsworth.arsnouveau.api.recipe;

import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.common.block.tile.WixieCauldronTile;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class CraftingManager {
    public ItemStack outputStack;
    // Stacks still needed to craft
    public List<ItemStack> neededItems;
    // Items that are left over after crafting, like buckets
    public List<ItemStack> remainingItems;
    public boolean craftCompleted;

    public CraftingManager() {
        outputStack = ItemStack.EMPTY;
        neededItems = new ArrayList<>();
        remainingItems = new ArrayList<>();
    }

    public CraftingManager(ItemStack outputStack, List<ItemStack> neededItems) {
        this.remainingItems = getContainerItems(neededItems);
        this.outputStack = outputStack;
        this.neededItems = neededItems;
    }

    List<ItemStack> getContainerItems(List<ItemStack> items) {
        List<ItemStack> remaining = new ArrayList<>();
        for(ItemStack item : items) {
            if (item.hasCraftingRemainingItem()) {
                remaining.add(item.getCraftingRemainingItem());
            }
        }

        return remaining;
    }

    public ItemStack getNextItem() {
        return !neededItems.isEmpty() ? neededItems.get(0) : ItemStack.EMPTY;
    }

    public boolean giveItem(Item i) {
        if (canBeCompleted())
            return false;

        ItemStack stackToRemove = ItemStack.EMPTY;
        for (ItemStack stack : neededItems) {
            if (stack.getItem() == i) {
                stackToRemove = stack;
                break;
            }
        }
        return neededItems.remove(stackToRemove);
    }

    public boolean canBeCompleted() {
        return neededItems.isEmpty();
    }

    public boolean isCraftCompleted(){
        return craftCompleted;
    }

    public void completeCraft(WixieCauldronTile tile){
        Level level = tile.getLevel();
        BlockPos worldPosition = tile.getBlockPos();
        assert level != null;

        if(!outputStack.isEmpty()){
            level.addFreshEntity(new ItemEntity(level, worldPosition.getX(), worldPosition.getY() + 1.0, worldPosition.getZ(), outputStack.copy()));
        }
        for (ItemStack i : remainingItems) {
            if(!i.isEmpty()) {
                level.addFreshEntity(new ItemEntity(level, worldPosition.getX(), worldPosition.getY() + 1.0, worldPosition.getZ(), i.copy()));
            }
        }
        tile.hasSource = false;
        tile.onCraftingComplete();
        this.craftCompleted = true;
    }

    public void write(CompoundTag tag) {
        CompoundTag stack = new CompoundTag();
        outputStack.save(stack);
        tag.put("output_stack", stack);
        NBTUtil.writeItems(tag, "progress", neededItems);
        NBTUtil.writeItems(tag, "refund", remainingItems);
        tag.putBoolean("completed", craftCompleted);
    }

    public void read(CompoundTag tag){
        outputStack = ItemStack.of(tag.getCompound("output_stack"));
        neededItems = NBTUtil.readItems(tag, "progress");
        remainingItems = NBTUtil.readItems(tag, "refund");
    }

    public static CraftingManager fromTag(CompoundTag tag){
        CraftingManager craftingManager = tag.getBoolean("isPotion") ? new PotionCraftingManager() : new CraftingManager();
        craftingManager.read(tag);
        return craftingManager;
    }
}
