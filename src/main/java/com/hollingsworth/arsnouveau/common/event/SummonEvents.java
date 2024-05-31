package com.hollingsworth.arsnouveau.common.event;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.event.SummonEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;

@EventBusSubscriber(modid = ArsNouveau.MODID)
public class SummonEvents {

    @SubscribeEvent
    public static void summonedEvent(SummonEvent event) {
    }

    @SubscribeEvent
    public static void summonDeathEvent(SummonEvent.Death event) {
    }
}
