package com.hollingsworth.arsnouveau.api.event;

import com.hollingsworth.arsnouveau.ArsNouveau;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.ServerTickRateManager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.ListIterator;

/**
 * For queuing deferred or over-time tasks. Tick refers to the Server or Client Tick event.
 */
@EventBusSubscriber(modid = ArsNouveau.MODID)
public class EventQueue {
    @NotNull List<ITimedEvent> events = new ObjectArrayList<>();

    public void tick(@Nullable ServerTickEvent.Post e) {
        if (events.isEmpty()) {
            return;
        }

        int length = events.size();
        int index = 0;
        ListIterator<ITimedEvent> iter = events.listIterator();
        while (index++ < length && iter.hasNext()) {
            ITimedEvent event = iter.next();
            if (event.isExpired()) {
                iter.remove();
                continue;
            }

            if (e == null) {
                event.tick(false);
            } else {
                event.tick(e);
            }
        }
    }

    public void addEvent(ITimedEvent event) {
        events.add(event);
    }

    public static EventQueue getServerInstance() {
        if (serverQueue == null)
            serverQueue = new EventQueue();
        return serverQueue;
    }

    public static EventQueue getClientQueue() {
        if (clientQueue == null)
            clientQueue = new EventQueue();
        return clientQueue;
    }


    // Tear down on world unload
    public void clear() {
        for(ITimedEvent event : events){
            event.onServerStopping();
        }
        this.events = new ObjectArrayList<>();
    }

    // Split these because our integrated servers are CURSED and both tick.
    private static EventQueue serverQueue;
    private static EventQueue clientQueue;

    private EventQueue() {
        events = new ObjectArrayList<>();
    }

    private static boolean tickStepping = false;

    @SubscribeEvent
    public static void serverTick(ServerTickEvent.Post e) {
        ServerTickRateManager trm = e.getServer().tickRateManager();

        if (trm.isFrozen() && !tickStepping) {
            return;
        }

        EventQueue.getServerInstance().tick(e);
    }

    @SubscribeEvent
    public static void serverTickPre(ServerTickEvent.Pre e) {
        tickStepping = e.getServer().tickRateManager().isSteppingForward();
    }

    @SubscribeEvent
    public static void clientTickEvent(ClientTickEvent.Post e) {
        EventQueue.getClientQueue().tick(null);
    }
}
