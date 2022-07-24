package com.hollingsworth.arsnouveau.api.item;

import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.util.CasterUtil;
import com.hollingsworth.arsnouveau.client.keybindings.ModKeyBindings;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface ISpellHotkeyListener extends IRadialProvider {

    default void onNextKeyPressed(ItemStack stack, ServerPlayer player) {
        ISpellCaster iSpellCaster = CasterUtil.getCaster(stack);
        iSpellCaster.setNextSlot();
    }

    default void onPreviousKeyPressed(ItemStack stack, ServerPlayer player) {
        ISpellCaster iSpellCaster = CasterUtil.getCaster(stack);
        iSpellCaster.setPreviousSlot();
    }

    default void onQuickCast(ItemStack stack, ServerPlayer player, InteractionHand hand, int slot){
        ISpellCaster iSpellCaster = CasterUtil.getCaster(stack);
        iSpellCaster.castSpell(player.level, player, hand, null, iSpellCaster.getSpell(slot));
    }

    @OnlyIn(Dist.CLIENT)
    default void onOpenBookMenuKeyPressed(ItemStack stack, Player player) {
    }

    @OnlyIn(Dist.CLIENT)
    default void onRadialKeyPressed(ItemStack stack, Player player) {
    }

    @Override
    default int forKey() {
        return ModKeyBindings.OPEN_RADIAL_HUD.getKey().getValue();
    }
}
