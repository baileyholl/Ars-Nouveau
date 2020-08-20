package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.common.block.BlockRegistry;
import com.hollingsworth.arsnouveau.common.entity.EntityKobold;
import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class SummoningCrytalTile extends AbstractManaTile {

    ArrayList<UUID> entityList = new ArrayList<>();
    int numEntities = 0;
    public SummoningCrytalTile() {
        super(BlockRegistry.SUMMONING_CRYSTAL_TILE);
    }

    @Override
    public int getTransferRate() {
        return 0;
    }

    public void summon(){
        if(!world.isRemote){
            EntityKobold kobold = new EntityKobold(world, pos);
            kobold.setPosition(this.pos.getX(), this.pos.getY() + 1, this.pos.getZ());
            world.addEntity(kobold);
            numEntities +=1;
            entityList.add(kobold.getUniqueID());
        }
    }


    public void cleanupKobolds(){
        List<UUID> list = world.getEntitiesWithinAABB(EntityKobold.class, new AxisAlignedBB(pos).grow(10)).stream().map(f -> f.getUniqueID()).collect(Collectors.toList());
        ArrayList<UUID> removed = new ArrayList<>();
        for(UUID uuid : this.entityList) {
            if (!list.contains(uuid)) {
                removed.add(uuid);
            }
        }
        for(UUID uuid : removed){
            this.entityList.remove(uuid);
            this.numEntities--;
        }
    }

    @Override
    public void tick() {
        if(world.getGameTime() % 20 != 0  || world.isRemote)
            return;

        for(EntityKobold kobold : world.getEntitiesWithinAABB(EntityKobold.class, new AxisAlignedBB(pos).grow(10))){
            System.out.println(kobold.getUniqueID());
        }
        System.out.println(this.numEntities);
        System.out.println(this.entityList);
        cleanupKobolds();

    }

    @Override
    public void read(CompoundNBT tag) {
        super.read(tag);
        this.numEntities = tag.getInt("entities");
        int count = 0;
        while(tag.hasUniqueId("entity" + count)){
            entityList.add(tag.getUniqueId("entity" + count));
            count++;
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        tag.putInt("entities", numEntities);
        for (int i = 0; i < entityList.size(); i++) {
            tag.putUniqueId("entity" + i, entityList.get(i));
        }
        return super.write(tag);
    }
}
