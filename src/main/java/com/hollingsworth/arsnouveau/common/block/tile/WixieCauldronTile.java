package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.WixieCauldron;
import com.hollingsworth.arsnouveau.common.entity.EntityFlyingItem;
import com.hollingsworth.arsnouveau.common.entity.EntityFollowProjectile;
import com.hollingsworth.arsnouveau.common.entity.EntityWixie;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WixieCauldronTile extends TileEntity implements ITickableTileEntity {

    List<ItemStack> itemsRequired; // Items still required for crafting

    public List<BlockPos> inventories;
    public Item craftingItem;
    int itemCounter;

    boolean converted;
    boolean finishItem;
    public WixieCauldronTile() {
        super(BlockRegistry.WIXIE_CAULDRON_TYPE);
    }

    @Override
    public void tick() {
        if(world.isRemote)
            return;
        if (!converted) {
            convertedEffect();
            return;
        }
        if (world.getGameTime() % 100 == 0) {
            updateInventories(); // Update the inventories available to use
        }

    }



    public boolean giveItem(ItemStack stack) {

        boolean result = itemsRequired.remove(stack);

        ItemStack stackToRemove = null;
        for(ItemStack itemStack : itemsRequired){
            if( itemStack.getItem() == stack.getItem()){
                stackToRemove = itemStack;
                break;
            }
        }
        if(stackToRemove != null)
            itemsRequired.remove(stackToRemove);

        if(itemsRequired.isEmpty())
            finishItem = true;
        return result;
    }


    public void setRecipes(ItemStack stack){

    }
    public ItemStack getNextRequiredItem() {
//        if (itemsRequired == null || itemsRequired.isEmpty())
//            return ItemStack.EMPTY;
//        if (recipeItemCounter >= itemsRequired.size())
//            recipeItemCounter = 0;
//        ItemStack stack = itemsRequired.get(recipeItemCounter);
//        recipeItemCounter++;
        return ItemStack.EMPTY;
    }

    public void updateInventories() {
        inventories = new ArrayList<>();
        for (BlockPos bPos : BlockPos.getAllInBoxMutable(pos.north(6).east(6).down(2), pos.south(6).west(6).up(2))) {
            if (world.getTileEntity(bPos) instanceof IInventory)
                inventories.add(bPos.toImmutable());
        }
    }

    public void spawnFlyingItem(BlockPos from, ItemStack stack) {
        EntityFlyingItem flyingItem = new EntityFlyingItem(world, from.up(), pos);
        flyingItem.getDataManager().set(EntityFlyingItem.HELD_ITEM, stack.copy());
        world.addEntity(flyingItem);
    }


    public void convertedEffect() {

        itemCounter++;
        if (itemCounter >= 120 && !world.isRemote) {
            converted = true;
            world.setBlockState(pos, world.getBlockState(pos).with(WixieCauldron.FILLED, false).with(WixieCauldron.CONVERTED, true));
            EntityWixie wixie = new EntityWixie(world, true, pos);
            wixie.setPosition(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
            world.addEntity(wixie);
            ParticleUtil.spawnPoof((ServerWorld) world, pos.up());
            itemCounter = 0;
            return;
        }
        if (itemCounter % 10 == 0 && !world.isRemote) {
            Random r = world.rand;
            int min = -2;
            int max = 2;
            EntityFollowProjectile proj1 = new EntityFollowProjectile(world, pos.add(r.nextInt(max - min) + min, 3, r.nextInt(max - min) + min), pos, r.nextInt(255), r.nextInt(255), r.nextInt(255));
            world.addEntity(proj1);
        }
    }



    @Override
    public void read(BlockState state, CompoundNBT compound) {
        super.read(state, compound);

        itemsRequired = NBTUtil.readItems(compound, "required_");
        this.converted = compound.getBoolean("converted");
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        if (itemsRequired != null)
            NBTUtil.writeItems(compound, "required_", itemsRequired);
        compound.putBoolean("converted", converted);
        return super.write(compound);
    }
}
