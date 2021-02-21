package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class SummonHorse extends HorseEntity {
    public int ticksLeft;
    public SummonHorse(EntityType<? extends HorseEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    protected boolean canMate() {
        return false;
    }

    @Override
    public ActionResultType func_230254_b_(PlayerEntity p_230254_1_, Hand p_230254_2_) {
        return super.func_230254_b_(p_230254_1_, p_230254_2_);
    }

    @Override
    public void tick() {
        super.tick();
        if(!world.isRemote){
            ticksLeft--;
            if(ticksLeft <= 0) {
                ParticleUtil.spawnPoof((ServerWorld) world, getPosition());
                this.remove();
            }
        }
    }

    @Override
    public boolean canPickUpItem(ItemStack itemstackIn) {
        return false;
    }

    @Override
    protected void dropInventory() { }

    @Override
    protected int getExperiencePoints(PlayerEntity player) {
        return 0;
    }

    public Inventory getHorseInventory(){
        return this.horseChest;
    }

    @Override
    public void openGUI(PlayerEntity playerEntity) { }

    @Override
    public boolean canMateWith(AnimalEntity otherAnimal) {
        return false;
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.ticksLeft = compound.getInt("left");
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("left", ticksLeft);
    }
}
