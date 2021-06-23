package com.hollingsworth.arsnouveau.common.potions;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.util.DamageSource;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class ShockedEffect extends Effect {

    public ShockedEffect() {
        super(EffectType.HARMFUL, 2039587);
        setRegistryName(ArsNouveau.MODID, "shocked");
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amp) {
        int j =  25 >> amp;
        if (j > 0) {
            return duration % j == 0;
        } else {
            return true;
        }
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amp) {
        int multiplier = 0;
        for(ItemStack i : entity.getArmorSlots()){
            IEnergyStorage energyStorage = i.getCapability(CapabilityEnergy.ENERGY).orElse(null);
            if(energyStorage != null){
                multiplier++;
            }
        }

        if(entity instanceof LivingEntity){
            IEnergyStorage energyStorage = ((LivingEntity) entity).getMainHandItem().getCapability(CapabilityEnergy.ENERGY).orElse(null);
            if(energyStorage != null)
                multiplier++;
            energyStorage = ((LivingEntity) entity).getOffhandItem().getCapability(CapabilityEnergy.ENERGY).orElse(null);
            if(energyStorage != null)
                multiplier++;
        }
        if(multiplier > 0){
            int numTicks = 0;
            if(entity instanceof PlayerEntity){
                CompoundNBT tag = entity.getPersistentData().getCompound(PlayerEntity.PERSISTED_NBT_TAG);
            }
            entity.hurt(DamageSource.LIGHTNING_BOLT, 20 * multiplier * (amp + 1));

        }
    }
}
