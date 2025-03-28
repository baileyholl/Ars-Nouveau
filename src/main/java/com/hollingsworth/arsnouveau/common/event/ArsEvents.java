package com.hollingsworth.arsnouveau.common.event;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.event.SpellCastEvent;
import com.hollingsworth.arsnouveau.api.event.SpellCostCalcEvent;
import com.hollingsworth.arsnouveau.api.event.SpellDamageEvent;
import com.hollingsworth.arsnouveau.api.event.SpellResolveEvent;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.PlayerCaster;
import com.hollingsworth.arsnouveau.api.util.ManaUtil;
import com.hollingsworth.arsnouveau.common.block.tile.GhostWeaveTile;
import com.hollingsworth.arsnouveau.common.block.tile.SpellSensorTile;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectInvisibility;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = ArsNouveau.MODID)
public class ArsEvents {

    @SubscribeEvent
    public static void costCalc(SpellCostCalcEvent e) {
        if (e.context.getCasterTool().isEmpty()) {
            return;
        }
        if (e.context.getCaster() instanceof PlayerCaster livingCaster && e.context.getCasterTool().is(ItemsRegistry.CASTER_TOME.get())) {
            int maxMana = ManaUtil.getMaxMana(livingCaster.player);
            if (e.currentCost > maxMana) {
                e.currentCost = maxMana;
            } else {
                e.currentCost /= 2;
            }
        }
    }

    @SubscribeEvent
    public static void castEvent(SpellCastEvent castEvent) {
        SpellSensorTile.onSpellCast(castEvent);
    }

    @SubscribeEvent
    public static void spellResolve(SpellResolveEvent.Post e) {
        SpellSensorTile.onSpellResolve(e);
        if (e.spell.unsafeList().contains(EffectInvisibility.INSTANCE) && e.rayTraceResult instanceof BlockHitResult blockHitResult) {
            if (e.world.getBlockEntity(blockHitResult.getBlockPos()) instanceof GhostWeaveTile ghostWeaveTile) {
                ghostWeaveTile.setVisibility(true);
            }
        }
    }

    @SubscribeEvent
    public static void preSpellDamage(SpellDamageEvent.Pre e) {
        if (e.damageSource.is(DamageTypeTags.IS_FIRE) && e.caster.hasEffect(ModPotions.IMMOLATE_EFFECT)) {
            e.damage += 2 * (e.caster.getEffect(ModPotions.IMMOLATE_EFFECT).getAmplifier() + 1);
        }
    }
}
