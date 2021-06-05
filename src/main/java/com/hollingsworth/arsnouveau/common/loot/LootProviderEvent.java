package com.hollingsworth.arsnouveau.common.loot;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static com.hollingsworth.arsnouveau.ArsNouveau.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public class LootProviderEvent {

    public static final DeferredRegister<GlobalLootModifierSerializer<?>> GLM = DeferredRegister.create(ForgeRegistries.LOOT_MODIFIER_SERIALIZERS, ArsNouveau.MODID);

    @SubscribeEvent
    public static void runData(GatherDataEvent event)
    {
        event.getGenerator().addProvider(new ParchmentLootGenerator(event.getGenerator(), MODID));
    }
}
