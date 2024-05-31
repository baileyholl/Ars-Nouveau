package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.client.renderer.world.PathfindingDebugRenderer;
import com.hollingsworth.arsnouveau.common.entity.pathfinding.ModNode;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.NetworkEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Message to sync the reached positions over to the client for rendering.
 */
public class SyncPathReachedMessage
{
    /**
     * Set of reached positions.
     */
    public Set<BlockPos> reached = new HashSet<>();

    /**
     * Default constructor.
     */
    public SyncPathReachedMessage()
    {
        super();
    }

    /**
     * Create the message to send a set of positions over to the client side.
     *
     */
    public SyncPathReachedMessage(final Set<BlockPos> reached)
    {
        super();
        this.reached = reached;
    }


    public void toBytes(final FriendlyByteBuf buf)
    {
        buf.writeInt(reached.size());
        for (final BlockPos node : reached)
        {
            buf.writeBlockPos(node);
        }
    }


    public SyncPathReachedMessage(final FriendlyByteBuf buf)
    {
        int size = buf.readInt();
        for (int i = 0; i < size; i++)
        {
            reached.add(buf.readBlockPos());
        }
    }

    public static class Handler {
        public static void handle(final SyncPathReachedMessage m, final Supplier<NetworkEvent.Context> ctx) {
            if (ctx.get().getDirection().getReceptionSide().isServer()) {
                ctx.get().setPacketHandled(true);
                return;
            }

            ctx.get().enqueueWork(new Runnable() {
                // Use anon - lambda causes classloading issues
                @Override
                public void run() {
                    for (final ModNode node : PathfindingDebugRenderer.lastDebugNodesPath)
                    {
                        if (m.reached.contains(node.pos))
                        {
                            node.setReachedByWorker(true);
                        }
                    }
                }
            });
            ctx.get().setPacketHandled(true);

        }
    }
}
