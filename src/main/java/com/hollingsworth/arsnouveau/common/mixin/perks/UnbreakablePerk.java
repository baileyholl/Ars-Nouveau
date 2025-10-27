package com.hollingsworth.arsnouveau.common.mixin.perks;

import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry.UNBREAKING;

@Mixin(ItemStack.class)
public abstract class UnbreakablePerk implements DataComponentHolder {

    @Inject(method = "isDamageableItem", at = @At("HEAD"), cancellable = true)
    void ars_nouveau$unbreakablePerk(CallbackInfoReturnable<Boolean> cir) {
        if (this.getOrDefault(UNBREAKING, false))
            cir.setReturnValue(false);
    }

}
