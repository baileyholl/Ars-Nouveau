package com.hollingsworth.arsnouveau.common.mixin;

import com.hollingsworth.arsnouveau.common.spell.effect.EffectPrestidigitation;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

// 1.21.11: inventoryTick(Level, Entity, int, boolean) → inventoryTick(Level, Entity, @Nullable EquipmentSlot)
@Mixin(ItemStack.class)
public class ItemStackMixin {

    @WrapMethod(method = "inventoryTick")
    public void ars_nouveau$inventoryTick(Level level, Entity entity, @Nullable EquipmentSlot equipmentSlot, Operation<Void> original) {
        EffectPrestidigitation.onInventoryTick(((ItemStack) (Object) this), level, entity, equipmentSlot);
        original.call(level, entity, equipmentSlot);
    }
}
