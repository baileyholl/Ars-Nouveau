package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.util.ManaUtil;
import com.hollingsworth.arsnouveau.common.block.BlockRegistry;
import com.hollingsworth.arsnouveau.common.block.ManaBlock;
import com.hollingsworth.arsnouveau.common.block.SummoningCrystal;
import com.hollingsworth.arsnouveau.common.entity.EntityWhelp;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.HopperTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;

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

    public void summon(EntityWhelp whelp){
        if(!world.isRemote){
            numEntities +=1;
            entityList.add(whelp.getUniqueID());
        }
    }

    public void changeTier(PlayerEntity entity){
        if(tier == 1){
            tier = 2;
            entity.sendMessage(new StringTextComponent("Set area to 5 x 5"));
        }else if(tier == 2){
            tier = 3;
            entity.sendMessage(new StringTextComponent("Set area to 9 x 9"));
        }else if(tier == 3){
            tier = 1;
            entity.sendMessage(new StringTextComponent("Set area to adjacent blocks only."));
        }
    }

    public ArrayList<IInventory> inventories(){
        if(world == null)return new ArrayList<>();
        ArrayList<IInventory> iInventories = new ArrayList<>();
        for(Direction d : Direction.values()){
            IInventory iInventory =  HopperTileEntity.getInventoryAtPosition(world, pos.offset(d));
            if(iInventory != null)
                iInventories.add(iInventory);
        }

        return iInventories;
    }

    public ItemStack insertItem(ItemStack stack){

        for(IInventory i : inventories()){
            if(stack == ItemStack.EMPTY || stack == null)
                break;
            stack = HopperTileEntity.putStackInInventoryAllSlots(null, i, stack, null);
        }
        return stack;
    }

    public @Nullable BlockPos getNextTaskLoc(){
        List<BlockPos> posList = getTargets();
        if(posList == null || posList.size() == 0) {
            return null;
        }
        if(taskIndex + 1 > posList.size()){
            taskIndex = 0;
        }
        if(world == null || world.isRemote)
            return null;
        BlockPos pos = posList.get(taskIndex);
        taskIndex += 1;
        if (!(world.getBlockState(pos).getBlock() instanceof LogBlock) && !(world.getBlockState(pos).getBlock() instanceof LeavesBlock)) {
            for(int i = 1; i < 4; i++) {

                if (world.getBlockState(pos.up(i)).getMaterial() != Material.AIR){
                    pos = pos.up(i);
                    break;
                }
            }
        }

        Block block = world.getBlockState(pos).getBlock();
        if(block instanceof SummoningCrystal || block instanceof ContainerBlock || block instanceof ManaBlock || block instanceof IInventory)
            return null;
        return world.getBlockState(pos.up()).getMaterial() == Material.AIR ? pos : null;
    }

    public boolean enoughMana(ArrayList<AbstractSpellPart> spellParts){
        final boolean[] enough = {false};
        int manaCost = ManaUtil.getRecipeCost(spellParts) / 4;
        BlockPos.getAllInBox(this.getPos().add(7, -3, 7), this.getPos().add(-7, 3, -7)).forEach(blockPos -> {
            if(!enough[0] && world.getTileEntity(blockPos) instanceof ManaJarTile && ((ManaJarTile) world.getTileEntity(blockPos)).getCurrentMana() >= manaCost ) {
                enough[0] = true;
            }
        });
        return enough[0];
    }

    public boolean removeMana(ArrayList<AbstractSpellPart> spellParts){
        final boolean[] enough = {false};
        int manaCost = ManaUtil.getRecipeCost(spellParts) / 4;
        BlockPos.getAllInBox(this.getPos().add(5, -3, 5), this.getPos().add(-5, 3, -5)).forEach(blockPos -> {
            if(!enough[0] && world.getTileEntity(blockPos) instanceof ManaJarTile && ((ManaJarTile) world.getTileEntity(blockPos)).getCurrentMana() >= manaCost ) {
                ((ManaJarTile) world.getTileEntity(blockPos)).removeMana(manaCost);
                enough[0] = true;
            }
        });
        return enough[0];
    }

    public List<BlockPos> getTargets(){
        List<BlockPos> positions = new ArrayList<>();
        if(tier == 1){
            positions.add(getPos().north().down());
            positions.add(getPos().south().down());
            positions.add(getPos().east().down());
            positions.add(getPos().west().down());
        }
        if(tier == 2){
            BlockPos.getAllInBox(getPos().north(2).east(2).down(1), getPos().south(2).west(2).down()).forEach(t -> positions.add(new BlockPos(t)));
        }
        if(tier == 3){
            BlockPos.getAllInBox(getPos().north(4).east(4).down(1), getPos().south(4).west(4).down()).forEach(t -> positions.add(new BlockPos(t)));
        }
        return positions;
    }

    public int getRange(){
        return 1;
    }

    public void cleanupKobolds(){
        List<UUID> list = world.getEntitiesWithinAABB(EntityWhelp.class, new AxisAlignedBB(pos).grow(10)).stream().map(f -> f.getUniqueID()).collect(Collectors.toList());
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
        taskIndex = tag.getInt("task_index");
        tier = tag.getInt("tier");
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        tag.putInt("entities", numEntities);
        for (int i = 0; i < entityList.size(); i++) {
            tag.putUniqueId("entity" + i, entityList.get(i));
        }
        tag.putInt("task_index", taskIndex);
        tag.putInt("tier", tier);
        return super.write(tag);
    }
}
