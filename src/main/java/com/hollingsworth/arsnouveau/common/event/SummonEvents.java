package com.hollingsworth.arsnouveau.common.event;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.event.SummonEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ArsNouveau.MODID)
public class SummonEvents {

    @SubscribeEvent
    public static void summonedEvent(SummonEvent event){ }

    @SubscribeEvent
    public static void summonDeathEvent(SummonEvent.Death event){ }
}
