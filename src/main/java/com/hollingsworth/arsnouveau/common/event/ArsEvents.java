package com.hollingsworth.arsnouveau.common.event;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.event.ManaRegenCalcEvent;
import com.hollingsworth.arsnouveau.api.event.MaxManaCalcEvent;
import com.hollingsworth.arsnouveau.api.event.SpellModifierEvent;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ArsNouveau.MODID)
public class ArsEvents {

    @SubscribeEvent
    public static void maxCalc(MaxManaCalcEvent e) {
    }

    @SubscribeEvent
    public static void regenCalc(ManaRegenCalcEvent e) {
        if (e.getEntity() != null && e.getEntity().hasEffect(ModPotions.HEX_EFFECT.get())) {
            e.setRegen(e.getRegen() / 2.0);
        }
    }

    @SubscribeEvent
    public static void spellCalc(SpellModifierEvent e) {
        if (e.caster != null && e.caster.hasEffect(ModPotions.SPELL_DAMAGE_EFFECT.get())) {
            e.builder.addDamageModifier(1.5f * (e.caster.getEffect(ModPotions.SPELL_DAMAGE_EFFECT.get()).getAmplifier() + 1));
        }
    }
}
