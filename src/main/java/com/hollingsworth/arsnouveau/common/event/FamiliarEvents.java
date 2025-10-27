package com.hollingsworth.arsnouveau.common.event;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.event.*;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.entity.familiar.*;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@EventBusSubscriber(modid = ArsNouveau.MODID)
public class FamiliarEvents {

    public static List<FamiliarEntity> getFamiliars(Predicate<FamiliarEntity> predicate) {
        List<FamiliarEntity> stale = new ArrayList<>();
        List<FamiliarEntity> matching = new ArrayList<>();
        for (FamiliarEntity familiarEntity : FamiliarEntity.FAMILIAR_SET) {
            if (familiarEntity.isRemoved() || familiarEntity.terminatedFamiliar || familiarEntity.getOwner() == null) {
                stale.add(familiarEntity);
            } else if (predicate.test(familiarEntity)) {
                matching.add(familiarEntity);
            }
        }
        stale.forEach(FamiliarEntity.FAMILIAR_SET::remove);
        return matching;
    }

    @SubscribeEvent
    public static void castEvent(SpellCastEvent event) {
        for (FamiliarEntity entity : getFamiliars((f) -> f instanceof ISpellCastListener)) {
            if (entity instanceof ISpellCastListener) {
                ((ISpellCastListener) entity).onCast(event);
            }
        }
    }

    @SubscribeEvent
    public static void calcEvent(SpellCostCalcEvent event) {
        for (FamiliarEntity entity : getFamiliars((f) -> f instanceof ISpellCastListener)) {
            if (entity instanceof ISpellCastListener) {
                ((ISpellCastListener) entity).onCostCalc(event);
            }
        }
    }

    @SubscribeEvent
    public static void summonEvent(FamiliarSummonEvent event) {
        for (FamiliarEntity entity : getFamiliars((f) -> true)) {
            if (entity.getOwner() != null && entity.getOwner().equals(event.owner)) {
                entity.onFamiliarSpawned(event);
            }
        }

    }

    @SubscribeEvent
    public static void maxManaCalc(MaxManaCalcEvent event) {
        for (FamiliarEntity entity : getFamiliars(familiarEntity -> true)) {
            if (!entity.isAlive())
                return;
            if (entity.getOwner() != null && entity.getOwner().equals(event.getEntity())) {
                event.setReserve((float) (event.getReserve() + entity.getManaReserveModifier()));
                return;
            }
        }
    }

    @SubscribeEvent
    public static void spellResolveEvent(SpellModifierEvent event) {
        for (FamiliarEntity entity : getFamiliars((familiarEntity -> familiarEntity instanceof ISpellCastListener))) {
            if (entity instanceof ISpellCastListener) {
                ((ISpellCastListener) entity).onModifier(event);
            }
        }
    }

    @SubscribeEvent
    public static void modifierEvent(SpellModifierEvent event) {
        for (FamiliarEntity entity : getFamiliars((familiarEntity -> familiarEntity instanceof ISpellCastListener))) {
            if (entity instanceof ISpellCastListener) {
                ((ISpellCastListener) entity).onModifier(event);
            }
        }
    }

    // TODO: restore drygmy fortune event
//    @SubscribeEvent
//    public static void fortuneEvent(LootingLevelEvent event) {
//        for (FamiliarEntity entity : getFamiliars((familiarEntity -> familiarEntity instanceof FamiliarDrygmy))) {
//            if (entity instanceof FamiliarDrygmy) {
//                ((FamiliarDrygmy) entity).onLootingEvent(event);
//            }
//        }
//    }

    @SubscribeEvent
    public static void eatEvent(LivingEntityUseItemEvent.Finish event) {
        for (FamiliarEntity entity : getFamiliars((familiarEntity -> familiarEntity instanceof FamiliarWhirlisprig))) {
            if (entity instanceof FamiliarWhirlisprig) {
                ((FamiliarWhirlisprig) entity).eatEvent(event);
            }
        }
    }

    @SubscribeEvent
    public static void potionEvent(MobEffectEvent.Added event) {
        for (FamiliarEntity entity : getFamiliars((familiarEntity -> familiarEntity instanceof FamiliarWixie))) {
            if (entity instanceof FamiliarWixie) {
                ((FamiliarWixie) entity).potionEvent(event);
            }
        }
    }


    @SubscribeEvent
    public static void knockbackEvent(LivingKnockBackEvent event) {
        List<FamiliarEntity> golems = getFamiliars((familiarEntity -> familiarEntity instanceof FamiliarAmethystGolem golem && golem.getOwner() != null && golem.getOwner().equals(event.getEntity())));
        if (!golems.isEmpty()) {
            event.setStrength(event.getStrength() * 0.5f);
        }
    }

    @SubscribeEvent
    public static void livingHurtEvent(LivingDamageEvent.Post event) {
        if (!event.getSource().is(DamageTypeTags.BYPASSES_ARMOR) && event.getEntity() instanceof Player player) {
            List<FamiliarEntity> golems = getFamiliars((familiarEntity -> familiarEntity instanceof FamiliarAmethystGolem golem && golem.getOwner() != null && golem.getOwner().equals(event.getEntity())));
            if (!golems.isEmpty()) {
                Entity entity = event.getSource().getEntity();
                if (entity instanceof LivingEntity livingTarget && BlockUtil.distanceFrom(player.blockPosition(), entity.blockPosition()) < 3) {
                    livingTarget.knockback(0.5f, player.getX() - entity.getX(), player.getZ() - entity.getZ());
                }
            }
        }
    }
}
