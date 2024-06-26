package com.hollingsworth.arsnouveau.common.event;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketReactiveSpell;
import com.hollingsworth.arsnouveau.common.spell.casters.ReactiveCaster;
import com.hollingsworth.arsnouveau.setup.registry.EnchantmentRegistry;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingHurtEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = ArsNouveau.MODID)
public class ReactiveEvents {

    @SubscribeEvent
    public static void livingHitEvent(LivingHurtEvent e) {
        LivingEntity entity = e.getEntity();
        if (entity.getCommandSenderWorld().isClientSide)
            return;

        for (ItemStack s : entity.getArmorSlots()) {
            castSpell(entity, s);
        }
    }

    public static void castSpell(LivingEntity playerIn, ItemStack s) {
        if (s.getEnchantmentLevel(EnchantmentRegistry.REACTIVE_ENCHANTMENT) * .25 >= Math.random() && new ReactiveCaster(s).getSpell().isValid()) {
            ReactiveCaster reactiveCaster = new ReactiveCaster(s);
            reactiveCaster.castSpell(playerIn.getCommandSenderWorld(), playerIn, InteractionHand.MAIN_HAND, null);
        }
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
        Player entity = e.getEntity();

        if (entity == null || entity.getCommandSenderWorld().isClientSide)
            return;
        ItemStack s = e.getEntity().getMainHandItem();
        castSpell(entity, s);
    }


    @SubscribeEvent
    public static void leftClickAir(PlayerInteractEvent.LeftClickEmpty e) {
        if (e.getItemStack().getEnchantmentLevel(EnchantmentRegistry.REACTIVE_ENCHANTMENT) > 0)
            Networking.sendToServer(new PacketReactiveSpell());
    }
}
