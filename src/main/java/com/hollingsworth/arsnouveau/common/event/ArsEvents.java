package com.hollingsworth.arsnouveau.common.event;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.event.*;
import com.hollingsworth.arsnouveau.api.perk.PerkAttributes;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.PlayerCaster;
import com.hollingsworth.arsnouveau.api.util.ManaUtil;
import com.hollingsworth.arsnouveau.common.block.tile.GhostWeaveTile;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectInvisibility;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ArsNouveau.MODID)
public class ArsEvents {

    @SubscribeEvent
    public static void costCalc(SpellCostCalcEvent e){
        if(e.context.getCasterTool().isEmpty()){
            return;
        }
        if(e.context.getCaster() instanceof PlayerCaster livingCaster && e.context.getCasterTool().is(ItemsRegistry.CASTER_TOME.get())){
            int maxMana = ManaUtil.getMaxMana(livingCaster.player);
            if (e.currentCost > maxMana) {
                e.currentCost = maxMana;
            } else {
                e.currentCost /= 2;
            }
        }
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
