package com.hollingsworth.arsnouveau.common.event;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.event.ManaRegenCalcEvent;
import com.hollingsworth.arsnouveau.api.event.MaxManaCalcEvent;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ArsNouveau.MODID)
public class ArsEvents {

    @SubscribeEvent
    public static void maxCalc(MaxManaCalcEvent e){ }

    @SubscribeEvent
    public static void regenCalc(ManaRegenCalcEvent e){
        if(e.getEntityLiving() != null && e.getEntityLiving().getEffect(ModPotions.HEX_EFFECT) != null){
            e.setRegen(e.getRegen()/2.0);
        }

    }
}
