package com.hollingsworth.arsnouveau.common.event;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.event.ManaRegenCalcEvent;
import com.hollingsworth.arsnouveau.api.event.SpellDamageEvent;
import com.hollingsworth.arsnouveau.api.perk.PerkAttributes;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ArsNouveau.MODID)
public class ArsEvents {

    @SubscribeEvent
    public static void regenCalc(ManaRegenCalcEvent e) {
        if (e.getEntity() != null && e.getEntity().hasEffect(ModPotions.HEX_EFFECT.get())) {
            e.setRegen(e.getRegen() / 2.0);
        }
    }

    @SubscribeEvent
    public static void spellCalc(SpellDamageEvent e) {
        if(e.caster == null)
            return;
        if (e.caster.hasEffect(ModPotions.SPELL_DAMAGE_EFFECT.get())) {
            e.damage += 1.5f * (e.caster.getEffect(ModPotions.SPELL_DAMAGE_EFFECT.get()).getAmplifier() + 1);
        }
        e.damage += e.caster.getAttributeValue(PerkAttributes.SPELL_DAMAGE_BONUS.get());
    }
}
