package com.hollingsworth.arsnouveau.common.event;

import com.hollingsworth.arsnouveau.api.entity.ISummon;
import com.hollingsworth.arsnouveau.api.event.EntityPreRemovalEvent;
import com.hollingsworth.arsnouveau.api.event.MaxManaCalcEvent;
import com.hollingsworth.arsnouveau.api.event.SummonEvent;
import com.hollingsworth.arsnouveau.api.perk.PerkAttributes;
import com.hollingsworth.arsnouveau.setup.config.ServerConfig;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber
public class SummonManager {

    // Does it need to be concurrent? Probably not...
    public static Map<UUID, List<ISummon>> summonedEntities = new ConcurrentHashMap<>();

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void summonManaReserve(MaxManaCalcEvent event) {
        if (event.getMax() <= 0 || event.getReserve() >= 1) return;
        if (getSummonedEntitiesOrDefault(event.getEntity()).isEmpty()) return;
        float reserve = 0;
        for (ISummon summon : summonedEntities.get(event.getEntity().getUUID())) {
            if (summon instanceof LivingEntity living && !living.isAlive()) continue;
            // reserve is in raw mana,
            reserve += summon.getManaReserve();
        }
        //needs to become a percentage based on max and capped at 1
        var maxMana = event.getMax();
        reserve = reserve / maxMana;
        event.setReserve(Math.min(1.0F, event.getReserve() + reserve));
    }

    @SubscribeEvent
    public static void registerSummon(SummonEvent.Pre event) {
        LivingEntity owner = event.summon.getOwnerAlt();
        ISummon summon = event.summon;
        if (!(owner instanceof Player)) return;
        var manaCap = CapabilityRegistry.getMana(owner);
        if (manaCap == null) return;

        List<ISummon> list = getSummonedEntitiesOrDefault(owner);
        // Check if the summon limit is active and enforce it
        if (ServerConfig.SUMMON_COUNT_LIMIT_MAX.get() >= 0 && ServerConfig.SUMMON_COUNT_LIMIT_BASE.get() >= 0) {
            int limit = ServerConfig.SUMMON_COUNT_LIMIT_BASE.get() + (int) owner.getAttributeValue(PerkAttributes.SUMMON_CAPACITY);
            //Hard cap on number of summons per caster
            limit = Math.min(limit, ServerConfig.SUMMON_COUNT_LIMIT_MAX.get());
            if (list.size() >= limit) {
                event.setCanceled(true);
                return;
            }
        }
        float partialReserve = 0;
        for (ISummon s : list) {
            if (s instanceof LivingEntity living && !living.isAlive()) continue;
            partialReserve += s.getManaReserve();
        }
        float estimatedBaseMax = manaCap.getMaxMana() / (1 - manaCap.getReserve());
        float limit = estimatedBaseMax * 0.99F;
        if (partialReserve + summon.getManaReserve() >= limit) {
            //Cannot summon more, would exceed 100% mana reserve
            event.setCanceled(true);
            return;
        }
        list.add(summon);
        summonedEntities.put(owner.getUUID(), list);
    }

    public static List<ISummon> getSummonedEntitiesOrDefault(LivingEntity owner) {
        return summonedEntities.getOrDefault(owner.getUUID(), new ArrayList<>());
    }

    @SubscribeEvent
    public static void unregisterSummonDeath(SummonEvent.Death event) {
        unregisterSummon(event.summon, event.summon.getOwnerAlt());
    }

    @SubscribeEvent
    public static void unregisterSummonDiscarded(EntityPreRemovalEvent event) {
        if (!(event.getEntity() instanceof ISummon summon) || event.getLevel().isClientSide()) return;
        unregisterSummon(summon, summon.getOwnerAlt());
    }

    public static void unregisterSummon(ISummon summon, LivingEntity owner) {
        if (owner == null) return;
        List<ISummon> list = getSummonedEntitiesOrDefault(owner);
        if (list.isEmpty()) return;
        list.remove(summon);
        summonedEntities.put(owner.getUUID(), list);
    }

    @SubscribeEvent
    public static void onOwnerTeleport(EntityTeleportEvent event) {
        if (!(event.getEntity() instanceof Player owner)) return;
        teleportSummons(owner, event);
    }

    public static void teleportSummons(LivingEntity owner, EntityTeleportEvent event) {
        List<ISummon> list = getSummonedEntitiesOrDefault(owner);
        if (list.isEmpty()) return;
        for (ISummon summon : list) {
            if (summon instanceof LivingEntity living && living.isAlive()) {
                living.teleportTo(event.getTargetX(), event.getTargetY(), event.getTargetZ());
            }
        }
    }
}
