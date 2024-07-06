package com.hollingsworth.arsnouveau.common.event;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = ArsNouveau.MODID)
public class ReactiveEvents {

    @SubscribeEvent
    public static void livingHitEvent(LivingDamageEvent.Post e) {
        LivingEntity entity = e.getEntity();
        if (entity.getCommandSenderWorld().isClientSide)
            return;

        for (ItemStack s : entity.getArmorSlots()) {
            castSpell(entity, s);
        }
    }

    public static void castSpell(LivingEntity playerIn, ItemStack stack) {
        //todo: reenable reactive
//        int level = stack.getEnchantmentLevel(playerIn.level.holderOrThrow(EnchantmentRegistry.REACTIVE_ENCHANTMENT));
//        var reactiveCaster = stack.get(DataComponentRegistry.REACTIVE_CASTER);
//        if (level * .25 >= Math.random() && reactiveCaster != null && reactiveCaster.getSpell().isValid()) {
//            reactiveCaster.castSpell(playerIn.getCommandSenderWorld(), playerIn, InteractionHand.MAIN_HAND, null);
//        }
    }

    @SubscribeEvent
    public static void leftClickBlock(PlayerInteractEvent.LeftClickBlock e) {
        Player entity = e.getEntity();

        if (entity.getCommandSenderWorld().isClientSide)
            return;
        ItemStack s = e.getItemStack();
        castSpell(entity, s);
    }

    @SubscribeEvent
    public static void playerAttackEntity(AttackEntityEvent e) {
        //todo: reenable reactive
//        Player entity = e.getEntity();
//
//        if (entity == null || entity.getCommandSenderWorld().isClientSide)
//            return;
//        ItemStack s = e.getEntity().getMainHandItem();
//        castSpell(entity, s);
    }


    @SubscribeEvent
    public static void leftClickAir(PlayerInteractEvent.LeftClickEmpty e) {
        //todo: reenable reactive
    }
//        if (e.getItemStack().getEnchantmentLevel(HolderHelper.unwrap(e.getLevel(), EnchantmentRegistry.REACTIVE_ENCHANTMENT)) > 0)
//            Networking.sendToServer(new PacketReactiveSpell());
//    }
}
