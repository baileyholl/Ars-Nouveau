package com.hollingsworth.arsnouveau.common.mixin;

import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(DamageSource.class)
public class DamageSourceMixin {
    @Shadow @Final @Nullable private Entity causingEntity;

    @Inject(method = "getLocalizedDeathMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z", shift = At.Shift.BEFORE), cancellable = true)
    private void spellDeathMessage(LivingEntity pLivingEntity, CallbackInfoReturnable<Component> cir, @Local(ordinal = 0) String attack) {
        Entity killer = this.causingEntity;
        if (!(killer instanceof LivingEntity livingKiller)) return;

        ItemStack mainHand = livingKiller.getMainHandItem();
        if (!(mainHand.getItem() instanceof SpellBook spellBook)) return;

        Spell spell = spellBook.getSpellCaster(mainHand).getSpell();
        if (spell.name().isBlank()) return;

        MutableComponent spellName = ComponentUtils.wrapInSquareBrackets(Component.empty().append(spell.name()).withStyle(Style.EMPTY.withColor(spell.color().getColor()))
                        .withStyle(ChatFormatting.ITALIC))
                .withStyle(mainHand.getRarity().getStyleModifier())
                .withStyle((comp) -> comp.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackInfo(mainHand))));

        cir.setReturnValue(Component.translatable(attack + ".item", pLivingEntity.getDisplayName(), livingKiller.getDisplayName(), spellName));
    }
}
