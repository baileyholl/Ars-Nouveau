package com.hollingsworth.arsnouveau.common.spell.augment;

import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.world.World;

import java.util.Optional;

public class AugmentSmelt extends AbstractAugment {
    public AugmentSmelt() {
        super(ModConfig.AugmentSmeltID, "Smelt");
    }

    public ItemStack smelt(World world, ItemStack stack, int fortune){
        Optional<FurnaceRecipe> optional = world.getRecipeManager().getRecipe(IRecipeType.SMELTING, new Inventory(stack),
                world);
        if (optional.isPresent()) {
            ItemStack itemstack = optional.get().getRecipeOutput();
            if (!itemstack.isEmpty()) {
                ItemStack itemstack1 = itemstack.copy();
                itemstack1.setCount(1 + world.rand.nextInt(fortune));
                return itemstack1;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public int getManaCost() {
        return 100;
    }
}
