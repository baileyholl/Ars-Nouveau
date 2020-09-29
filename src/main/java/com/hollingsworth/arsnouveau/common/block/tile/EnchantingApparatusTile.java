package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.EnchantingApparatusRecipe;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.common.block.EnchantingApparatusBlock;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.PressurePlateBlock;
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

        if(counter > 47) {
            if(!world.isRemote) {
                counter = 1;
                if (this.isCrafting) {
                    EnchantingApparatusRecipe recipe = this.getRecipe();
                    if (recipe != null) {
                        recipe.pedestalItems.forEach(i -> i = null);
                        this.catalystItem = recipe.result;
                        BlockPos.getAllInBox(this.getPos().add(5, -3, 5), this.getPos().add(-5, 3, -5)).forEach(blockPos -> {
                            if (world.getTileEntity(blockPos) instanceof ArcanePedestalTile && ((ArcanePedestalTile) world.getTileEntity(blockPos)).stack != null) {
                                if (((ArcanePedestalTile) world.getTileEntity(blockPos)).stack.getItem() == ItemsRegistry.bucketOfMana) {
                                    ((ArcanePedestalTile) world.getTileEntity(blockPos)).stack = new ItemStack(Items.BUCKET);
                                } else {
                                    ((ArcanePedestalTile) world.getTileEntity(blockPos)).stack = null;
                                }

                                BlockState state = world.getBlockState(blockPos);
                                world.notifyBlockUpdate(blockPos, state, state, 3);
                            }
                        });
                    }

                    this.isCrafting = false;
                }
            }
        }else if(world.isRemote && counter >= 46){
            if(world.isRemote){
                for(int i =0; i < 10; i++){
                    double d0 = getPos().getX() +0.5; //+ world.rand.nextFloat();
                    double d1 = getPos().getY() +1.2;//+ world.rand.nextFloat() ;
                    double d2 = getPos().getZ() +.5 ; //+ world.rand.nextFloat();
                    world.addParticle(ParticleTypes.END_ROD, d0, d1, d2, (world.rand.nextFloat() * 1 - 0.5)/3, (world.rand.nextFloat() * 1 - 0.5)/3, (world.rand.nextFloat() * 1 - 0.5)/3);
                }
            }
        }
        if(!world.isRemote)
            updateBlock();
    }
    // Used for rendering on the client
    public List<BlockPos> pedestalList(){
        ArrayList<BlockPos> posList = new ArrayList<>();
        BlockPos.getAllInBox(this.getPos().add(5, -3, 5), this.getPos().add(-5, 3, -5)).forEach(blockPos -> {
            if(world.getTileEntity(blockPos) instanceof ArcanePedestalTile && ((ArcanePedestalTile) world.getTileEntity(blockPos)).stack != null) {
                posList.add(blockPos.toImmutable());
            }
        });
        return posList;
    }

    public EnchantingApparatusRecipe getRecipe(){
        ArrayList<ItemStack> pedestalItems = new ArrayList<>();
        BlockPos.getAllInBox(this.getPos().add(5, -3, 5), this.getPos().add(-5, 3, -5)).forEach(blockPos -> {
            if(world.getTileEntity(blockPos) instanceof ArcanePedestalTile && ((ArcanePedestalTile) world.getTileEntity(blockPos)).stack != null) {
                pedestalItems.add(((ArcanePedestalTile) world.getTileEntity(blockPos)).stack);
            }
        });

        EnchantingApparatusRecipe resultRecipe  =  ArsNouveauAPI.getInstance().getEnchantingApparatusRecipes().stream().filter(r-> r.isResultOf(catalystItem, pedestalItems) != null).findFirst().orElse(null);
        return resultRecipe;

    }



    public void attemptCraft(){
        if(this.catalystItem == null || isCrafting)
            return;
        if(this.getRecipe() != null)
            this.isCrafting = true;
//        this.catalystItem = resultRecipe.result;
        updateBlock();
    }

    public void updateBlock(){
        if(counter == 0)
            counter = 1;
        BlockState state = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, state, state, 2);
    }

    @Override
    public void read(CompoundNBT compound) {
        catalystItem = ItemStack.read((CompoundNBT)compound.get("itemStack"));
        isCrafting = compound.getBoolean("is_crafting");
        counter = compound.getInt("counter");
        super.read(compound);
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
        handleUpdateTag(pkt.getNbtCompound());
    }
}
