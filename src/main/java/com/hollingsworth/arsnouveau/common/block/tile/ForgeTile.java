package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;

public class ForgeTile extends TileEntity implements ITickableTileEntity {
    public ForgeTile() {
        super(BlockRegistry.FORGE_TILE_TYPE);
    }

    @Override
    public void tick() {
        if(world.isRemote)
            return;
        BlockState state = world.getBlockState(pos.north());
        Optional<FurnaceRecipe> optional = world.getRecipeManager().getRecipe(IRecipeType.SMELTING, new Inventory(new ItemStack(state.getBlock().asItem(), 1)),
                world);
        BlockPos pos = getPos();
        if (optional.isPresent()) {
            ItemStack itemstack = optional.get().getRecipeOutput();
            if (!itemstack.isEmpty()) {
                ItemStack itemstack1 = itemstack.copy();
                itemstack1.setCount(2); //Forge: Support smelting returning multiple

                world.addEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), itemstack1));
            }
        }
    }
}
