package com.hollingsworth.arsnouveau.common.potions;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;

public class ShockedEffect extends MobEffect {

    public ShockedEffect() {
        super(MobEffectCategory.HARMFUL, 2039587);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(ServerLevel serverLevel, LivingEntity entity, int amp) {
        int multiplier = 0;
        // 1.21.11: getArmorSlots() removed; iterate armor EquipmentSlots
        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            ItemStack i = entity.getItemBySlot(slot);
            EnergyHandler energyStorage = Capabilities.Energy.ITEM.getCapability(i, ItemAccess.forStack(i));
            if (energyStorage != null) {
                multiplier++;
            }
        }

        ItemStack mainHand = entity.getMainHandItem();
        EnergyHandler energyStorage = Capabilities.Energy.ITEM.getCapability(mainHand, ItemAccess.forStack(mainHand));
        if (energyStorage != null)
            multiplier++;
        ItemStack offHand = entity.getOffhandItem();
        energyStorage = Capabilities.Energy.ITEM.getCapability(offHand, ItemAccess.forStack(offHand));
        if (energyStorage != null)
            multiplier++;
        if (multiplier > 0) {
            entity.hurt(serverLevel.damageSources().lightningBolt(), 20 * multiplier * (amp + 1));
        }
        return true;
    }
}
