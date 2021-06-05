package com.hollingsworth.arsnouveau.common.loot;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.ObjectHolder;

import static com.hollingsworth.arsnouveau.ArsNouveau.MODID;

@Mod.EventBusSubscriber(modid = ArsNouveau.MODID)
public class LootProviderEvent {


    @ObjectHolder(ArsNouveau.MODID)
    @Mod.EventBusSubscriber(modid = ArsNouveau.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class LootRegistry{
        @SubscribeEvent
        public static void runData(GatherDataEvent event)
        {
            event.getGenerator().addProvider(new ParchmentLootGenerator(event.getGenerator(), MODID));
        }
    }



    public static void registerLootData()
    {

    }
}
