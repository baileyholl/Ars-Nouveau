package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.EnchantingApparatusRecipe;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.IEnchantingRecipe;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.common.block.EnchantingApparatusBlock;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class EnchantingApparatusTile extends AnimatedTile {
    public ItemStack catalystItem;
    public ItemEntity entity;
    public long frames = 0;

    private int craftingLength = 100;
    public boolean isCrafting;
    public long timeStartedCrafting;

    public EnchantingApparatusTile() {
        super(BlockRegistry.ENCHANTING_APP_TILE);
        counter = 1;
    }


    @Override
    public void tick() {

        if(isCrafting && !world.isRemote){
            if(this.getRecipe() == null)
                this.isCrafting = false;
            counter += 1;

        }

        if(counter > craftingLength && !world.isRemote) {
            counter = 1;

            if (this.isCrafting) {
                IEnchantingRecipe recipe = this.getRecipe();
                List<ItemStack> pedestalItems = getPedestalItems();
                if (recipe != null) {
                    pedestalItems.forEach(i -> i = null);
                    this.catalystItem = recipe.getResult(pedestalItems, this.catalystItem, this);
                    clearItems();
                }

                this.isCrafting = false;
            }
            updateBlock();
        }else if(world.isRemote && counter >= craftingLength - 1){
            spawnPoofParticles();
        }
    }

    public void spawnPoofParticles(){
        for(int i =0; i < 10; i++){
            double d0 = getPos().getX() +0.5;
            double d1 = getPos().getY() +1.2;
            double d2 = getPos().getZ() +.5 ;
            world.addParticle(ParticleTypes.END_ROD, d0, d1, d2, (world.rand.nextFloat() * 1 - 0.5)/3, (world.rand.nextFloat() * 1 - 0.5)/3, (world.rand.nextFloat() * 1 - 0.5)/3);
        }
    }

    public void clearItems(){
        BlockPos.getAllInBox(this.getPos().add(5, -3, 5), this.getPos().add(-5, 3, -5)).forEach(blockPos -> {
            if (world.getTileEntity(blockPos) instanceof ArcanePedestalTile && ((ArcanePedestalTile) world.getTileEntity(blockPos)).stack != null) {
                ArcanePedestalTile tile = ((ArcanePedestalTile) world.getTileEntity(blockPos));
                tile.stack = tile.stack.getItem() == ItemsRegistry.bucketOfMana ? new ItemStack(Items.BUCKET) : ItemStack.EMPTY;
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

    public IEnchantingRecipe getRecipe(){
        List<ItemStack> pedestalItems = getPedestalItems();
        return ArsNouveauAPI.getInstance().getEnchantingApparatusRecipes().stream().filter(r-> r.isMatch(pedestalItems, catalystItem, this)).findFirst().orElse(null);
    }

    public void attemptCraft(){
        if(this.catalystItem == null || isCrafting)
            return;
        if(this.getRecipe() != null)
            this.isCrafting = true;

        updateBlock();
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
}
