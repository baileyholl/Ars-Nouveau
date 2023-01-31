package com.hollingsworth.arsnouveau.api.recipe;

import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.common.block.tile.WixieCauldronTile;
import com.hollingsworth.arsnouveau.common.util.PotionUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CraftingProgress {
    public ItemStack outputStack;
    public List<ItemStack> neededItems;
    public List<ItemStack> remainingItems;
    private Potion potionNeeded;
    public Potion potionOut;
    public boolean isPotionCrafting;
    private boolean hasObtainedPotion;

    public CraftingProgress() {
        outputStack = ItemStack.EMPTY;
        neededItems = new ArrayList<>();
        remainingItems = new ArrayList<>();
    }

    public CraftingProgress(Potion potionNeeded, List<ItemStack> itemsNeeded, Potion potionOut) {
        this.setPotionNeeded(potionNeeded);
        this.potionOut = potionOut;
        neededItems = itemsNeeded;
        remainingItems = itemsNeeded;
        isPotionCrafting = true;
        setHasObtainedPotion(false);
        outputStack = ItemStack.EMPTY;
    }

    public CraftingProgress(ItemStack outputStack, List<ItemStack> neededItems, @Nullable Recipe recipe) {
        CraftingContainer inventory = new CraftingContainer(new AbstractContainerMenu(null, -1) {
            @Override
            public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
                return ItemStack.EMPTY;
            }

            public boolean stillValid(Player playerIn) {
                return false;
            }
        }, 3, 3);
        for (int i = 0; i < neededItems.size(); i++) {
            inventory.setItem(i, neededItems.get(i).copy());
        }
        this.remainingItems = recipe == null ? new ArrayList<>() : recipe.getRemainingItems(inventory);
        this.outputStack = outputStack;
        this.neededItems = neededItems;
    }

    public ItemStack getNextItem() {
        return !neededItems.isEmpty() ? neededItems.get(0) : ItemStack.EMPTY;
    }

    public boolean giveItem(Item i) {
        if (isDone())
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

    public boolean isDone() {
        return !isPotionCrafting ? neededItems.isEmpty() : hasObtainedPotion() && neededItems.isEmpty();
    }

    public boolean isPotionCrafting() {
        return isPotionCrafting || (potionOut != Potions.EMPTY && potionOut != null);
    }

    public void dropCompletedItems(WixieCauldronTile tile){
        Level level = tile.getLevel();
        BlockPos worldPosition = tile.getBlockPos();

        if(!outputStack.isEmpty()){
            level.addFreshEntity(new ItemEntity(level, worldPosition.getX(), worldPosition.getY() + 1.0, worldPosition.getZ(), outputStack.copy()));
        }
        for (ItemStack i : remainingItems) {
            if(!i.isEmpty()) {
                level.addFreshEntity(new ItemEntity(level, worldPosition.getX(), worldPosition.getY() + 1.0, worldPosition.getZ(), i.copy()));
            }
        }

    }

    public void write(CompoundTag tag) {
        CompoundTag stack = new CompoundTag();
        outputStack.save(stack);
        tag.put("output_stack", stack);
        NBTUtil.writeItems(tag, "progress", neededItems);
        NBTUtil.writeItems(tag, "refund", remainingItems);
        CompoundTag outputTag = new CompoundTag();
        PotionUtil.addPotionToTag(potionOut, outputTag);
        tag.put("potionout", outputTag);

        CompoundTag neededTag = new CompoundTag();
        PotionUtil.addPotionToTag(getPotionNeeded(), neededTag);
        tag.put("potionNeeded", neededTag);
        tag.putBoolean("gotPotion", hasObtainedPotion());
        tag.putBoolean("isPotionCraft", isPotionCrafting);
    }

    public static CraftingProgress read(CompoundTag tag) {
        CraftingProgress progress = new CraftingProgress();
        progress.outputStack = ItemStack.of(tag.getCompound("output_stack"));
        progress.neededItems = NBTUtil.readItems(tag, "progress");
        progress.remainingItems = NBTUtil.readItems(tag, "refund");
        progress.potionOut = PotionUtils.getPotion(tag.getCompound("potionout"));
        progress.setPotionNeeded(PotionUtils.getPotion(tag.getCompound("potionNeeded")));
        progress.setHasObtainedPotion(tag.getBoolean("gotPotion"));
        progress.isPotionCrafting = tag.getBoolean("isPotionCraft");
        return progress;
    }

    public Potion getPotionNeeded() {
        return potionNeeded;
    }

    public void setPotionNeeded(Potion potionNeeded) {
        this.potionNeeded = potionNeeded;
    }

    public boolean hasObtainedPotion() {
        return hasObtainedPotion || potionNeeded == Potions.WATER || potionNeeded == Potions.EMPTY;
    }

    public void setHasObtainedPotion(boolean hasObtainedPotion) {
        this.hasObtainedPotion = hasObtainedPotion;
    }
}
