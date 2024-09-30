package com.hollingsworth.arsnouveau.common.mixin.looting;

import com.hollingsworth.arsnouveau.api.util.DamageUtil;
import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import com.hollingsworth.arsnouveau.common.perk.LootingPerk;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnchantedCountIncreaseFunction.class)
public class EnchantedCountIncreaseFunctionMixin {

    @WrapOperation(
            method = "run",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getEnchantmentLevel(Lnet/minecraft/core/Holder;Lnet/minecraft/world/entity/LivingEntity;)I"
            )
    )
    private int ars_nouveau$lootingPerk(Holder<Enchantment> enchantment, LivingEntity attacker, Operation<Integer> original, @Local(argsOnly = true) LootContext context) {
        int add = PerkUtil.countForPerk(LootingPerk.INSTANCE, attacker);
        int spellLuck = context.getParamOrNull(LootContextParams.DAMAGE_SOURCE) instanceof DamageUtil.SpellDamageSource spellDamageSource ? spellDamageSource.getLuckLevel() : 0;
        // TODO: add FamilarEntity check
        return Math.max(spellLuck, original.call(enchantment, attacker)) + add;
    }
}
