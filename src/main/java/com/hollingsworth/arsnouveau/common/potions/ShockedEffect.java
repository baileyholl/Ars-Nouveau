package com.hollingsworth.arsnouveau.common.potions;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class ShockedEffect extends MobEffect {

    public ShockedEffect() {
        super(MobEffectCategory.HARMFUL, 2039587);
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

        IEnergyStorage energyStorage = entity.getMainHandItem().getCapability(CapabilityEnergy.ENERGY).orElse(null);
        if(energyStorage != null)
            multiplier++;
        energyStorage = entity.getOffhandItem().getCapability(CapabilityEnergy.ENERGY).orElse(null);
        if(energyStorage != null)
            multiplier++;
        if(multiplier > 0){
            int numTicks = 0;
            if(entity instanceof Player){
                CompoundTag tag = entity.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
            }
            entity.hurt(DamageSource.LIGHTNING_BOLT, 20 * multiplier * (amp + 1));

        }
    }
}
