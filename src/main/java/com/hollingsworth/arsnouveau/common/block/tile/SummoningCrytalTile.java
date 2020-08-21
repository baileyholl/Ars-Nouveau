package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.common.block.BlockRegistry;
import com.hollingsworth.arsnouveau.common.entity.EntityWelp;
import net.minecraft.block.material.Material;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class SummoningCrytalTile extends AbstractManaTile {

    ArrayList<UUID> entityList = new ArrayList<>();
    int numEntities = 0;

    int tier;
    int taskIndex;

    public SummoningCrytalTile() {
        super(BlockRegistry.SUMMONING_CRYSTAL_TILE);
        tier = 1;
    }

    @Override
    public int getTransferRate() {
        return 0;
    }

    public void summon(){
        if(!world.isRemote){
            EntityWelp kobold = new EntityWelp(world, pos);
            kobold.setPosition(this.pos.getX(), this.pos.getY() + 1, this.pos.getZ());
            world.addEntity(kobold);
            numEntities +=1;
            entityList.add(kobold.getUniqueID());
        }
    }

    public @Nullable BlockPos getNextTaskLoc(){
        if(taskIndex + 1 > getTargets().size()){
            taskIndex = 0;
        }
        if(world == null || world.isRemote)
            return null;
        BlockPos pos = getTargets().get(taskIndex++);
        for(int i = 1; i < 4; i++) {
            if (world.getBlockState(pos.up(i)).getMaterial() != Material.AIR){
                pos = pos.up(i);
                break;
            }
        }

        return world.getBlockState(pos.up()).getMaterial() == Material.AIR ? pos : null;
    }


    public ArrayList<BlockPos> getTargets(){
        ArrayList<BlockPos> positions = new ArrayList<>();
        if(tier == 1){
            positions.add(getPos().north().down());
            positions.add(getPos().south().down());
            positions.add(getPos().east().down());
            positions.add(getPos().west().down());
            return positions;
        }
        return  new ArrayList<>();
    }

    public int getRange(){
        return 1;
    }

    public void cleanupKobolds(){
        List<UUID> list = world.getEntitiesWithinAABB(EntityWelp.class, new AxisAlignedBB(pos).grow(10)).stream().map(f -> f.getUniqueID()).collect(Collectors.toList());
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

        for(EntityWelp kobold : world.getEntitiesWithinAABB(EntityWelp.class, new AxisAlignedBB(pos).grow(10))){
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
