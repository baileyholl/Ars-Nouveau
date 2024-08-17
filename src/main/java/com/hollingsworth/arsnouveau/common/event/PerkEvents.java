package com.hollingsworth.arsnouveau.common.event;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.event.EffectResolveEvent;
import com.hollingsworth.arsnouveau.api.event.SpellCastEvent;
import com.hollingsworth.arsnouveau.api.event.SpellDamageEvent;
import com.hollingsworth.arsnouveau.api.event.SpellResolveEvent;
import com.hollingsworth.arsnouveau.api.perk.IEffectResolvePerk;
import com.hollingsworth.arsnouveau.api.perk.IPerk;
import com.hollingsworth.arsnouveau.api.perk.PerkInstance;
import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import com.hollingsworth.arsnouveau.common.perk.TotemPerk;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.level.SleepFinishedTimeEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = ArsNouveau.MODID)
public class PerkEvents {

    @SubscribeEvent
    public static void equipmentChangedEvent(final LivingEquipmentChangeEvent event) {
        if (!event.getEntity().level.isClientSide) {
            if (event.getSlot().getType() != EquipmentSlot.Type.HUMANOID_ARMOR)
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
    public static void onSpellCast(SpellCastEvent spellCastEvent){
        PerkUtil.getPerksFromLiving(spellCastEvent.getEntity()).forEach(perkInstance -> {
            IPerk perk = perkInstance.getPerk();
            if (perk instanceof IEffectResolvePerk) {
                ((IEffectResolvePerk) perk).onSpellCast(spellCastEvent, perkInstance);
            }
        });
    }

    @SubscribeEvent
    public static void preSpellResolve(final SpellResolveEvent.Pre event){
        PerkUtil.getPerksFromLiving(event.shooter).forEach(perkInstance -> {
            IPerk perk = perkInstance.getPerk();
            if (perk instanceof IEffectResolvePerk) {
                ((IEffectResolvePerk) perk).onSpellPreResolve(event, perkInstance);
            }
        });
    }

    @SubscribeEvent
    public static void postSpellResolve(final SpellResolveEvent.Post event){
        PerkUtil.getPerksFromLiving(event.shooter).forEach(perkInstance -> {
            IPerk perk = perkInstance.getPerk();
            if (perk instanceof IEffectResolvePerk) {
                ((IEffectResolvePerk) perk).onSpellPostResolve(event, perkInstance);
            }
        });
    }

    @SubscribeEvent
    public static void preEffectResolve(final EffectResolveEvent.Pre event){
        PerkUtil.getPerksFromLiving(event.shooter).forEach(perkInstance -> {
            IPerk perk = perkInstance.getPerk();
            if (perk instanceof IEffectResolvePerk) {
                ((IEffectResolvePerk) perk).onEffectPreResolve(event, perkInstance);
            }
        });
    }

    @SubscribeEvent
    public static void postEffectResolve(final EffectResolveEvent.Post event){
        PerkUtil.getPerksFromLiving(event.shooter).forEach(perkInstance -> {
            IPerk perk = perkInstance.getPerk();
            if (perk instanceof IEffectResolvePerk) {
                ((IEffectResolvePerk) perk).onEffectPostResolve(event, perkInstance);
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
        if(!(entity instanceof Player player))
            return;
        var holder = PerkUtil.getHolderForPerk(TotemPerk.INSTANCE, entity);
        if (holder == null)
            return;
        CompoundTag tag = holder.getB().getTagForPerk(TotemPerk.INSTANCE);
        if (tag == null || !tag.getBoolean("isActive")) {
            return;
        }
        entity.setHealth(1.0F);
        entity.removeAllEffects();
        entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
        entity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
        entity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
        entity.level.broadcastEntityEvent(entity, (byte) 35);
        tag.putBoolean("isActive", false);
        holder.getA().set(DataComponentRegistry.ARMOR_PERKS, holder.getB().setTagForPerk(TotemPerk.INSTANCE, tag));
        PortUtil.sendMessage(entity, Component.translatable("ars_nouveau.totem_perk.trigger"));
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void sleepEvent(final SleepFinishedTimeEvent event) {
        for (Player p : event.getLevel().players()) {
            var holder = PerkUtil.getHolderForPerk(TotemPerk.INSTANCE, p);
            if (holder == null)
                continue;
            CompoundTag tag = holder.getB().getTagForPerk(TotemPerk.INSTANCE);
            if(tag != null){
                tag.putBoolean("isActive", true);
                holder.getA().set(DataComponentRegistry.ARMOR_PERKS, holder.getB().setTagForPerk(TotemPerk.INSTANCE, tag));
            }
            PortUtil.sendMessage(p, Component.translatable("ars_nouveau.totem_perk.active"));
        }
    }
}
