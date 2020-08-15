package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.EnchantingApparatusRecipe;
import com.hollingsworth.arsnouveau.common.block.BlockRegistry;
import com.hollingsworth.arsnouveau.common.block.EnchantingApparatusBlock;
import com.hollingsworth.arsnouveau.common.block.GlyphPressBlock;
import com.hollingsworth.arsnouveau.common.block.ManaCondenserBlock;
import com.sun.jna.platform.win32.WinDef;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.ArrayList;

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
        if(world.isRemote)
            return;
        if(isCrafting){
            if(this.getRecipe() == null)
                this.isCrafting = false;
            counter += 1;

        }
        if(counter > 47) {
            counter = 1;
            if(this.isCrafting){
                EnchantingApparatusRecipe recipe = this.getRecipe();
                if(recipe != null){
                    recipe.pedestalItems.forEach(i -> i = null);
                    this.catalystItem = recipe.result;
                    BlockPos.getAllInBox(this.getPos().add(5, -3, 5), this.getPos().add(-5, 3, -5)).forEach(blockPos -> {
                        if(world.getTileEntity(blockPos) instanceof ArcanePedestalTile && ((ArcanePedestalTile) world.getTileEntity(blockPos)).stack != null) {
                          ((ArcanePedestalTile) world.getTileEntity(blockPos)).stack = null;
                            BlockState state = world.getBlockState(blockPos);
                            world.notifyBlockUpdate(blockPos, state, state, 3);
                        }
                    });
                }
                this.isCrafting = false;
            }
        }
        updateBlock();
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
        BlockState state = world.getBlockState(pos);
        world.setBlockState(pos, world.getBlockState(pos).with(EnchantingApparatusBlock.stage, counter),3);
        world.notifyBlockUpdate(pos, state, state, 2);
    }

    @Override
    public void read(CompoundNBT compound) {
        catalystItem = ItemStack.read((CompoundNBT)compound.get("itemStack"));
        super.read(compound);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        if(catalystItem != null) {
            CompoundNBT reagentTag = new CompoundNBT();
            catalystItem.write(reagentTag);
            compound.put("itemStack", reagentTag);
        }

        return super.write(compound);
    }
    @Override
    @Nullable
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.pos, 3, this.getUpdateTag());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        super.onDataPacket(net, pkt);
        handleUpdateTag(pkt.getNbtCompound());
    }
}
