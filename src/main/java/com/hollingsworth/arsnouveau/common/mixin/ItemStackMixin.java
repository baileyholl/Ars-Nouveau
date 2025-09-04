package com.hollingsworth.arsnouveau.common.mixin;

import com.hollingsworth.arsnouveau.common.spell.effect.EffectPrestidigitation;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @WrapMethod(method = "inventoryTick")
    public void ars_nouveau$inventoryTick(Level level, Entity entity, int inventorySlot, boolean isCurrentItem, Operation<Void> original) {
        EffectPrestidigitation.onInventoryTick(((ItemStack) (Object) this), level, entity, inventorySlot, isCurrentItem);
        original.call(level, entity, inventorySlot, isCurrentItem);
    }
}
