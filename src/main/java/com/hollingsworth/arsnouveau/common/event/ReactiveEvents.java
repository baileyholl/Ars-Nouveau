package com.hollingsworth.arsnouveau.common.event;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketReactiveSpell;
import com.hollingsworth.arsnouveau.common.util.HolderHelper;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import com.hollingsworth.arsnouveau.setup.registry.EnchantmentRegistry;
import net.minecraft.world.InteractionHand;
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
        if (entity.level().isClientSide())
            return;

        // 1.21.11: getArmorSlots() removed; iterate armor slots manually
        for (net.minecraft.world.entity.EquipmentSlot armorSlot : new net.minecraft.world.entity.EquipmentSlot[]{net.minecraft.world.entity.EquipmentSlot.HEAD, net.minecraft.world.entity.EquipmentSlot.CHEST, net.minecraft.world.entity.EquipmentSlot.LEGS, net.minecraft.world.entity.EquipmentSlot.FEET}) {
            castSpell(entity, entity.getItemBySlot(armorSlot));
        }
    }

    public static void castSpell(LivingEntity playerIn, ItemStack stack) {
        int level = stack.getEnchantmentLevel(playerIn.level.holderOrThrow(EnchantmentRegistry.REACTIVE_ENCHANTMENT));
        var reactiveCaster = stack.get(DataComponentRegistry.REACTIVE_CASTER);
        if (level * .25 >= Math.random() && reactiveCaster != null && reactiveCaster.getSpell().isValid()) {
            reactiveCaster.castSpell(playerIn.level(), playerIn, InteractionHand.MAIN_HAND, null);
        }
    }

    @SubscribeEvent
    public static void leftClickBlock(PlayerInteractEvent.LeftClickBlock e) {
        Player entity = e.getEntity();

        if (entity.level().isClientSide())
            return;
        ItemStack s = e.getItemStack();
        castSpell(entity, s);
    }

    @SubscribeEvent
    public static void playerAttackEntity(AttackEntityEvent e) {
        Player entity = e.getEntity();

        if (entity == null || entity.level().isClientSide())
            return;
        ItemStack s = e.getEntity().getMainHandItem();
        castSpell(entity, s);
    }


    @SubscribeEvent
    public static void leftClickAir(PlayerInteractEvent.LeftClickEmpty e) {
        if (e.getItemStack().getEnchantmentLevel(HolderHelper.unwrap(e.getLevel(), EnchantmentRegistry.REACTIVE_ENCHANTMENT)) > 0)
            Networking.sendToServer(new PacketReactiveSpell());
    }
}
