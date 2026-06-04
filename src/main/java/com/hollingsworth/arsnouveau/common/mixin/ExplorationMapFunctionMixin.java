package com.hollingsworth.arsnouveau.common.mixin;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.ExplorationMapFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ExplorationMapFunction.class)
public abstract class ExplorationMapFunctionMixin {
    @WrapWithCondition(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/MapItem;renderBiomePreviewMap(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;)V"))
    private boolean preventCallOnFakePlayer(ServerLevel serverLevel, ItemStack stack, @Local(argsOnly = true) LootContext context) {
        return !(context.getParamOrNull(LootContextParams.THIS_ENTITY) instanceof ANFakePlayer);
    }
}
