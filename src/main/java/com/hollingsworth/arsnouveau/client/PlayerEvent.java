package com.hollingsworth.arsnouveau.client;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.common.block.ScribesBlock;
import com.hollingsworth.arsnouveau.common.enchantment.EnchantmentRegistry;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.hollingsworth.arsnouveau.common.spell.casters.ReactiveCaster;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ArsNouveau.MODID)
public class PlayerEvent {

    @SubscribeEvent
    public static void onBlock(final PlayerInteractEvent.RightClickBlock event) {
        Player entity = event.getPlayer();
        if(!event.getWorld().isClientSide || event.getHand() != InteractionHand.MAIN_HAND || event.getWorld().getBlockState(event.getPos()).getBlock() instanceof ScribesBlock)
            return;
        if(entity.getItemInHand(event.getHand()).getItem() instanceof SpellBook){
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onTooltip(final ItemTooltipEvent event){
        ItemStack stack = event.getItemStack();
        int level = EnchantmentHelper.getItemEnchantmentLevel(EnchantmentRegistry.REACTIVE_ENCHANTMENT, stack);
        if(level > 0 && new ReactiveCaster(stack).getSpell().isValid()){
            Spell spell = new ReactiveCaster(stack).getSpell();
            event.getToolTip().add(Component.literal(spell.getDisplayString()));
        }
    }

    @SubscribeEvent
    public static void onItem(final PlayerInteractEvent.RightClickItem event) {
        Player entity = event.getPlayer();
        if(!event.getWorld().isClientSide || event.getHand() != InteractionHand.MAIN_HAND)
            return;
        if(entity.getItemInHand(event.getHand()).getItem() instanceof SpellBook){
            event.setCanceled(true);
        }
    }
}
