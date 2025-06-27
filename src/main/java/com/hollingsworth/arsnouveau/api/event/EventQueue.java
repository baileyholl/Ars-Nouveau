package com.hollingsworth.arsnouveau.api.event;

import com.hollingsworth.arsnouveau.ArsNouveau;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.server.ServerTickRateManager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * For queuing deferred or over-time tasks. Tick refers to the Server or Client Tick event.
 */
@SuppressWarnings("ForLoopReplaceableByForEach")
@EventBusSubscriber(modid = ArsNouveau.MODID)
public class EventQueue {
    @NotNull List<ITimedEvent> events = new ObjectArrayList<>();

    public void tick(@Nullable ServerTickEvent.Post e) {
        if (events.isEmpty()) {
            return;
        }

        List<ITimedEvent> stale = new ObjectArrayList<>();
        // Enhanced-for or iterator will cause a concurrent modification.
        int size = events.size();
        for (int i = 0; i < size; i++) {
            ITimedEvent event = events.get(i);
            if (event.isExpired()) {
                stale.add(event);
            } else {
                if (e == null)
                    event.tick(false);
                else
                    event.tick(e);
            }
        }
        this.events.removeAll(stale);
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
        for (ITimedEvent event : events) {
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
        if (!Minecraft.getInstance().isPaused()) {
            EventQueue.getClientQueue().tick(null);
        }
    }
}
