package com.hollingsworth.arsnouveau.common.event;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.event.EffectResolveEvent;
import com.hollingsworth.arsnouveau.api.event.SpellDamageEvent;
import com.hollingsworth.arsnouveau.api.perk.IEffectResolvePerk;
import com.hollingsworth.arsnouveau.api.perk.IPerk;
import com.hollingsworth.arsnouveau.api.perk.IPerkHolder;
import com.hollingsworth.arsnouveau.api.perk.PerkInstance;
import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import com.hollingsworth.arsnouveau.common.perk.TotemPerk;
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
        if (!event.getEntity().level.isClientSide) {
            if (event.getSlot().getType() != EquipmentSlot.Type.ARMOR)
                return;
            List<PerkInstance> perkInstances = PerkUtil.getPerksFromItem(event.getFrom());
            List<PerkInstance> toInstances = PerkUtil.getPerksFromItem(event.getTo());
            if (perkInstances.equals(toInstances))
                return;

            List<IPerk> playerPerks = new ArrayList<>(PerkUtil.getPerksFromLiving(event.getEntity()).stream().map(PerkInstance::getPerk).toList());
            List<IPerk> itemPerks = PerkUtil.getPerksFromItem(event.getTo()).stream().map(PerkInstance::getPerk).toList();
            // This event is called after the item is equipped, and the player contains the perks already from the item that was equipped.
            // Remove them, so we can detect actual duplicate.
            for (IPerk perk : itemPerks) {
                playerPerks.remove(perk);
            }

            for (IPerk equippedPerks : playerPerks) {
                if (itemPerks.contains(equippedPerks)) {
                    PortUtil.sendMessageNoSpam(event.getEntity(), Component.translatable("ars_nouveau.perks.duplicated"));
                    return;
                }
            }
        }
    }

    @SubscribeEvent
    public static void preEffectResolve(final EffectResolveEvent.Pre event){
        PerkUtil.getPerksFromLiving(event.shooter).forEach(perkInstance -> {
            IPerk perk = perkInstance.getPerk();
            if (perk instanceof IEffectResolvePerk) {
                ((IEffectResolvePerk) perk).onPreResolve(event, perkInstance);
            }
        });
    }

    @SubscribeEvent
    public static void postEffectResolve(final EffectResolveEvent.Post event){
        PerkUtil.getPerksFromLiving(event.shooter).forEach(perkInstance -> {
            IPerk perk = perkInstance.getPerk();
            if (perk instanceof IEffectResolvePerk) {
                ((IEffectResolvePerk) perk).onPostResolve(event, perkInstance);
            }
        });
    }

    @SubscribeEvent
    public static void preSpellDamageEvent(final SpellDamageEvent.Pre event) {
        PerkUtil.getPerksFromLiving(event.caster).forEach(perkInstance -> {
            IPerk perk = perkInstance.getPerk();
            if (perk instanceof IEffectResolvePerk) {
                ((IEffectResolvePerk) perk).onPreSpellDamageEvent(event, perkInstance);
            }
        });
    }

    @SubscribeEvent
    public static void postSpellDamageEvent(final SpellDamageEvent.Post event) {
        PerkUtil.getPerksFromLiving(event.caster).forEach(perkInstance -> {
            IPerk perk = perkInstance.getPerk();
            if (perk instanceof IEffectResolvePerk) {
                ((IEffectResolvePerk) perk).onPostSpellDamageEvent(event, perkInstance);
            }
        });
    }

    @SubscribeEvent
    public static void totemEvent(final LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        IPerkHolder<ItemStack> holder = PerkUtil.getHolderForPerk(TotemPerk.INSTANCE, entity);
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
        PortUtil.sendMessage(entity, Component.translatable("ars_nouveau.totem_perk.trigger"));
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void sleepEvent(final SleepFinishedTimeEvent event) {
        for (Player p : event.getLevel().players()) {
            IPerkHolder<ItemStack> holder = PerkUtil.getHolderForPerk(TotemPerk.INSTANCE, (LivingEntity) p);
            if (holder == null)
                continue;
            TotemPerk.Data perkData = new TotemPerk.Data(holder);
            perkData.setActive(true);
            PortUtil.sendMessage(p, Component.translatable("ars_nouveau.totem_perk.active"));
        }
    }
}
