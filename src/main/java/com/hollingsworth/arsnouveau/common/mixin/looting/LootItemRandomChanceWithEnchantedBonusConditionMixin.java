package com.hollingsworth.arsnouveau.common.mixin.looting;

import com.hollingsworth.arsnouveau.api.perk.PerkAttributes;
import com.hollingsworth.arsnouveau.api.util.DamageUtil;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithEnchantedBonusCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LootItemRandomChanceWithEnchantedBonusCondition.class)
public class LootItemRandomChanceWithEnchantedBonusConditionMixin {

    @WrapOperation(
            method = "test(Lnet/minecraft/world/level/storage/loot/LootContext;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getEnchantmentLevel(Lnet/minecraft/core/Holder;Lnet/minecraft/world/entity/LivingEntity;)I"
            )
    )
    private int ars_nouveau$adjustLooting(Holder<Enchantment> enchantment, LivingEntity attacker, Operation<Integer> original, @Local(argsOnly = true) LootContext context) {
        int add = (int) attacker.getAttributeValue(PerkAttributes.DRYGMY);
        int spellLuck = context.getParam(LootContextParams.DAMAGE_SOURCE) instanceof DamageUtil.SpellDamageSource spellDamageSource ? spellDamageSource.getLuckLevel() : 0;

        // TODO: add FamilarEntity check
        return Math.max(spellLuck, original.call(enchantment, attacker)) + add;
    }

}