package com.hollingsworth.arsnouveau.common.event;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.event.SpellDamageEvent;
import com.hollingsworth.arsnouveau.api.perk.IPerk;
import com.hollingsworth.arsnouveau.api.perk.IPerkHolder;
import com.hollingsworth.arsnouveau.api.perk.PerkInstance;
import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import com.hollingsworth.arsnouveau.common.perk.TotemPerk;
import com.hollingsworth.arsnouveau.common.perk.VampiricPerk;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.level.SleepFinishedTimeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = ArsNouveau.MODID)
public class PerkEvents {

    @SubscribeEvent
    public static void equipmentChangedEvent(final LivingEquipmentChangeEvent event) {
        if (!event.getEntity().level.isClientSide && event.getEntity() instanceof Player player) {
            if (event.getSlot().getType() != EquipmentSlot.Type.ARMOR)
                return;
            List<PerkInstance> perkInstances = PerkUtil.getPerksFromItem(event.getFrom());
            List<PerkInstance> toInstances = PerkUtil.getPerksFromItem(event.getTo());
            if (perkInstances.equals(toInstances))
                return;

            List<IPerk> playerPerks = new ArrayList<>(PerkUtil.getPerksFromPlayer(player).stream().map(PerkInstance::getPerk).toList());
            List<IPerk> itemPerks = PerkUtil.getPerksFromItem(event.getTo()).stream().map(PerkInstance::getPerk).toList();
            // This event is called after the item is equipped, and the player contains the perks already from the item that was equipped.
            // Remove them, so we can detect actual duplicate.
            for (IPerk perk : itemPerks) {
                playerPerks.remove(perk);
            }

            for (IPerk equippedPerks : playerPerks) {
                if (itemPerks.contains(equippedPerks)) {
                    PortUtil.sendMessageNoSpam(player, Component.translatable("ars_nouveau.perks.duplicated"));
                    return;
                }
            }
        }
    }

    @SubscribeEvent
    public static void spellDamageEvent(final SpellDamageEvent.Post event) {
        if (event.caster instanceof Player player && !event.isCanceled()) {
            int vampLevel = PerkUtil.countForPerk(VampiricPerk.INSTANCE, player);
            if (vampLevel > 0) {
                float healAmount = event.damage * (0.2f * vampLevel);
                player.heal(healAmount);
            }
        }
    }

    @SubscribeEvent
    public static void totemEvent(final LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof Player player) {
            IPerkHolder<ItemStack> holder = PerkUtil.getHolderForPerk(TotemPerk.INSTANCE, player);
            if (holder == null)
                return;
            TotemPerk.Data perkData = new TotemPerk.Data(holder);
            if (!perkData.isActive())
                return;
            entity.setHealth(1.0F);
            entity.removeAllEffects();
            entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
            entity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
            entity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
            entity.level.broadcastEntityEvent(entity, (byte) 35);
            perkData.setActive(false);
            PortUtil.sendMessage(player, Component.translatable("ars_nouveau.totem_perk.trigger"));
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void sleepEvent(final SleepFinishedTimeEvent event) {
        for (Player p : event.getLevel().players()) {
            IPerkHolder<ItemStack> holder = PerkUtil.getHolderForPerk(TotemPerk.INSTANCE, p);
            if (holder == null)
                continue;
            TotemPerk.Data perkData = new TotemPerk.Data(holder);
            perkData.setActive(true);
            PortUtil.sendMessage(p, Component.translatable("ars_nouveau.totem_perk.active"));
        }
    }
}
