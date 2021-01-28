package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.IEnchantingRecipe;
import com.hollingsworth.arsnouveau.api.util.ManaUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class EnchantingApparatusTile extends AnimatedTile implements IInventory {
    public ItemStack catalystItem;
    public ItemEntity entity;
    public long frames = 0;

    private int craftingLength = 100;
    public boolean isCrafting;

    public EnchantingApparatusTile() {
        super(BlockRegistry.ENCHANTING_APP_TILE);
        counter = 1;
    }


    @Override
    public void tick() {
        if(world.isRemote)
            return;

        if(isCrafting){
            if(this.getRecipe(catalystItem) == null)
                this.isCrafting = false;
            counter += 1;
        }

        if(counter > craftingLength) {
            counter = 1;

            if (this.isCrafting) {
                IEnchantingRecipe recipe = this.getRecipe(catalystItem);
                List<ItemStack> pedestalItems = getPedestalItems();
                if (recipe != null) {
                    pedestalItems.forEach(i -> i = null);
                    this.catalystItem = recipe.getResult(pedestalItems, this.catalystItem, this);
                    clearItems();
                    ParticleUtil.spawnPoof((ServerWorld) world, pos);
                }

                this.isCrafting = false;
            }
            updateBlock();
        }
    }


    public void clearItems(){
        BlockPos.getAllInBox(this.getPos().add(5, -3, 5), this.getPos().add(-5, 3, -5)).forEach(blockPos -> {
            if (world.getTileEntity(blockPos) instanceof ArcanePedestalTile && ((ArcanePedestalTile) world.getTileEntity(blockPos)).stack != null) {
                ArcanePedestalTile tile = ((ArcanePedestalTile) world.getTileEntity(blockPos));
                tile.stack = tile.stack == null ? ItemStack.EMPTY : tile.stack.getContainerItem();
                BlockState state = world.getBlockState(blockPos);
                world.notifyBlockUpdate(blockPos, state, state, 3);
            }
        });
    }

    // Used for rendering on the client
    public List<BlockPos> pedestalList(){
        ArrayList<BlockPos> posList = new ArrayList<>();
        BlockPos.getAllInBox(this.getPos().add(5, -3, 5), this.getPos().add(-5, 3, -5)).forEach(blockPos -> {
            if(world.getTileEntity(blockPos) instanceof ArcanePedestalTile && ((ArcanePedestalTile) world.getTileEntity(blockPos)).stack != null &&  !((ArcanePedestalTile) world.getTileEntity(blockPos)).stack.isEmpty()) {
                posList.add(blockPos.toImmutable());
            }
        });
        return posList;
    }

    public List<ItemStack> getPedestalItems(){
        ArrayList<ItemStack> pedestalItems = new ArrayList<>();
        BlockPos.getAllInBox(this.getPos().add(5, -3, 5), this.getPos().add(-5, 3, -5)).forEach(blockPos -> {
            if(world.getTileEntity(blockPos) instanceof ArcanePedestalTile && ((ArcanePedestalTile) world.getTileEntity(blockPos)).stack != null && !((ArcanePedestalTile) world.getTileEntity(blockPos)).stack.isEmpty()) {
                pedestalItems.add(((ArcanePedestalTile) world.getTileEntity(blockPos)).stack);
            }
        });
        return pedestalItems;
    }

    public IEnchantingRecipe getRecipe(ItemStack catalyst){
        List<ItemStack> pedestalItems = getPedestalItems();
        return ArsNouveauAPI.getInstance().getEnchantingApparatusRecipes(world).stream().filter(r-> r.isMatch(pedestalItems, catalyst, this)).findFirst().orElse(null);
    }

    public boolean attemptCraft(ItemStack catalyst){
        if(isCrafting)
            return false;
        IEnchantingRecipe recipe = this.getRecipe(catalyst);
        if(recipe != null) {
            if(recipe.consumesMana()){
                if(!ManaUtil.hasManaNearby(pos, world, 10, recipe.manaCost()))
                    return false;
                ManaUtil.takeManaNearbyWithParticles(pos, world, 10, recipe.manaCost());
            }
            this.isCrafting = true;
            updateBlock();
            return true;
        }
        return false;
    }

    public void updateBlock(){
        if(counter == 0)
            counter = 1;
        BlockState state = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, state, state, 2);
    }

    @Override
    public void read(BlockState state, CompoundNBT compound) {
        catalystItem = ItemStack.read((CompoundNBT)compound.get("itemStack"));
        isCrafting = compound.getBoolean("is_crafting");
        counter = compound.getInt("counter");
        super.read(state, compound);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        if(catalystItem != null) {
            CompoundNBT reagentTag = new CompoundNBT();
            catalystItem.write(reagentTag);
            compound.put("itemStack", reagentTag);
        }
        compound.putBoolean("is_crafting", isCrafting);
        compound.putInt("counter", counter);

        return super.write(compound);
    }
    @Override
    @Nullable
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.pos, 3, this.getUpdateTag());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT tag = new CompoundNBT();
        tag.putInt("counter", this.counter);
        tag.putBoolean("is_crafting", this.isCrafting);
        return this.write(tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        super.onDataPacket(net, pkt);
        handleUpdateTag(world.getBlockState(pos), pkt.getNbtCompound());
    }

    @Override
    public int getSizeInventory() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {

    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity player) {
        return false;
    }

    @Override
    public void clear() {

    }
}
