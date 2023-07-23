package com.hollingsworth.arsnouveau.common.event;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.event.*;
import com.hollingsworth.arsnouveau.api.perk.PerkAttributes;
import com.hollingsworth.arsnouveau.common.block.tile.GhostWeaveTile;
import com.hollingsworth.arsnouveau.common.block.tile.SpellSensorTile;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectInvisibility;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ArsNouveau.MODID)
public class ArsEvents {

    @SubscribeEvent
    public static void castEvent(SpellCastEvent castEvent) {
        SpellSensorTile.onSpellCast(castEvent);
    }

    @SubscribeEvent
    public static void regenCalc(ManaRegenCalcEvent e) {
        if (e.getEntity() != null && e.getEntity().hasEffect(ModPotions.HEX_EFFECT.get())) {
            e.setRegen(e.getRegen() / 2.0);
        }
    }

    @SubscribeEvent
    public static void spellCalc(SpellDamageEvent.Pre e) {
        if (e.caster == null)
            return;
        if (e.caster.hasEffect(ModPotions.SPELL_DAMAGE_EFFECT.get())) {
            e.damage += 1.5f * (e.caster.getEffect(ModPotions.SPELL_DAMAGE_EFFECT.get()).getAmplifier() + 1);
        }
        if (e.caster.getAttributes().hasAttribute(PerkAttributes.SPELL_DAMAGE_BONUS.get())) {
            e.damage += e.caster.getAttributeValue(PerkAttributes.SPELL_DAMAGE_BONUS.get());
        }
    }

    @SubscribeEvent
    public static void spellResolve(SpellResolveEvent.Post e) {
        if(e.spell.recipe.contains(EffectInvisibility.INSTANCE) && e.rayTraceResult instanceof BlockHitResult blockHitResult){
            if(e.world.getBlockEntity(blockHitResult.getBlockPos()) instanceof GhostWeaveTile ghostWeaveTile){
                ghostWeaveTile.setVisibility(true);
            }
        }
    }

    @SubscribeEvent
    public static void dispelEvent(DispelEvent e) {
        if(e.rayTraceResult instanceof BlockHitResult blockHitResult && e.world.getBlockEntity(blockHitResult.getBlockPos()) instanceof GhostWeaveTile ghostWeaveTile){
            ghostWeaveTile.setVisibility(false);
        }
    }

}
