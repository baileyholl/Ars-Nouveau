package com.hollingsworth.arsnouveau.client.events;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.documentation.DocDataLoader;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;


@EventBusSubscriber(modid = ArsNouveau.MODID)
public class ClientPlayerEvent {
    @SubscribeEvent
    public static void playerLogout(ClientPlayerNetworkEvent.LoggingOut e) {
        DocDataLoader.writeBookmarks();
    }
}
