package com.hollingsworth.arsnouveau.api.item;

import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.util.CasterUtil;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.PacketDistributor;
import software.bernie.geckolib3.network.GeckoLibNetwork;
import software.bernie.geckolib3.util.GeckoLibUtil;

public interface ISpellHotkeyListener {

    default void onNextKeyPressed(ItemStack stack, ServerPlayer player) {
        ISpellCaster iSpellCaster = CasterUtil.getCaster(stack);
        iSpellCaster.setNextSlot();
        if (stack.getItem() instanceof SpellBook book && player.level instanceof ServerLevel level) {

            final int id = GeckoLibUtil.guaranteeIDForStack(stack, level);
            final PacketDistributor.PacketTarget target = PacketDistributor.TRACKING_ENTITY_AND_SELF
                    .with(() -> player);
            GeckoLibNetwork.syncAnimation(target, book, id, 1);

        }
    }

    default void onPreviousKeyPressed(ItemStack stack, ServerPlayer player) {
        ISpellCaster iSpellCaster = CasterUtil.getCaster(stack);
        iSpellCaster.setPreviousSlot();
        if (stack.getItem() instanceof SpellBook book && player.level instanceof ServerLevel level) {

            final int id = GeckoLibUtil.guaranteeIDForStack(stack, level);
            final PacketDistributor.PacketTarget target = PacketDistributor.TRACKING_ENTITY_AND_SELF
                    .with(() -> player);
            GeckoLibNetwork.syncAnimation(target, book, id, 0);

        }
    }

    default void onQuickCast(ItemStack stack, ServerPlayer player, InteractionHand hand, int slot) {
        ISpellCaster iSpellCaster = CasterUtil.getCaster(stack);
        iSpellCaster.castSpell(player.level, player, hand, null, iSpellCaster.getSpell(slot));
    }

    //TODO: 1.20 Remove this and have onQuickCast return a boolean
    @Deprecated(forRemoval = true)
    default boolean canQuickCast() {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    default void onOpenBookMenuKeyPressed(ItemStack stack, Player player) {
    }
}
