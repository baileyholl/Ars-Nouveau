package com.hollingsworth.arsnouveau.api.item;

import com.hollingsworth.arsnouveau.api.registry.SpellCasterRegistry;
import com.hollingsworth.arsnouveau.api.spell.AbstractCaster;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public interface ISpellHotkeyListener {

    default void onNextKeyPressed(ItemStack stack, ServerPlayer player) {
        AbstractCaster<?> iSpellCaster = SpellCasterRegistry.from(stack);
        iSpellCaster.setNextSlot().saveToStack(stack);
    }

    default void onPreviousKeyPressed(ItemStack stack, ServerPlayer player) {
        AbstractCaster<?> iSpellCaster = SpellCasterRegistry.from(stack);
        iSpellCaster.setPreviousSlot().saveToStack(stack);
    }

    default void onQuickCast(ItemStack stack, ServerPlayer player, InteractionHand hand, int slot){
        AbstractCaster<?> iSpellCaster = SpellCasterRegistry.from(stack);
        iSpellCaster.castSpell(player.level, player, hand, null, iSpellCaster.getSpell(slot));
    }
    //TODO: 1.20 Remove this and have onQuickCast return a boolean
    @Deprecated(forRemoval = true)
    default boolean canQuickCast(){
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    default void onOpenBookMenuKeyPressed(ItemStack stack, Player player) {
    }
}
