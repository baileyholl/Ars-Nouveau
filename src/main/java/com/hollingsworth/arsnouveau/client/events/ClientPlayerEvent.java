package com.hollingsworth.arsnouveau.client.events;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.ScribesBlock;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;


@EventBusSubscriber(modid = ArsNouveau.MODID)
public class ClientPlayerEvent {

    @SubscribeEvent
    public static void onBlock(final PlayerInteractEvent.RightClickBlock event) {
        Player entity = event.getEntity();
        if (!event.getLevel().isClientSide || event.getHand() != InteractionHand.MAIN_HAND || event.getLevel().getBlockState(event.getPos()).getBlock() instanceof ScribesBlock)
            return;
        if (entity.getItemInHand(event.getHand()).getItem() instanceof SpellBook) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onItem(final PlayerInteractEvent.RightClickItem event) {
        Player entity = event.getEntity();
        if (!event.getLevel().isClientSide || event.getHand() != InteractionHand.MAIN_HAND)
            return;
        if (entity.getItemInHand(event.getHand()).getItem() instanceof SpellBook) {
            event.setCanceled(true);
        }
    }
}
