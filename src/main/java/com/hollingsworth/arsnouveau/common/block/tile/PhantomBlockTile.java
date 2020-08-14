package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.common.block.BlockRegistry;
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
        if(!world.isRemote){
            age++;
            //15 seconds
            if(age > 20 * 15){
                world.destroyBlock(this.getPos(), false);
                world.removeTileEntity(this.getPos());
            }
        }
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        this.age = compound.getInt("age");
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.put("age", IntNBT.valueOf(age));
        return super.write(compound);
    }
}
