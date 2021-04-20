package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;

public class PhantomBlockTile extends TileEntity implements ITickableTileEntity {

    int age;
    public PhantomBlockTile() {
        super(BlockRegistry.PHANTOM_TILE);
    }

    @Override
    public void tick() {
        if(!level.isClientSide){
            age++;
            //15 seconds
            if(age > 20 * 15){
                level.destroyBlock(this.getBlockPos(), false);
                level.removeBlockEntity(this.getBlockPos());
            }
        }
    }

    @Override
    public void load(BlockState state, CompoundNBT compound) {
        super.load(state,compound);
        this.age = compound.getInt("age");
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        compound.put("age", IntNBT.valueOf(age));
        return super.save(compound);
    }
}
