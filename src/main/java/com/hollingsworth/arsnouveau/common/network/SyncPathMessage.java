package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.client.renderer.world.PathfindingDebugRenderer;
import com.hollingsworth.arsnouveau.common.entity.pathfinding.MNode;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Message to sync some path over to the client.
 */
public class SyncPathMessage
{
    /**
     * Set of visited nodes.
     */
    public Set<MNode> lastDebugNodesVisited = new HashSet<>();

    /**
     * Set of not visited nodes.
     */
    public Set<MNode> lastDebugNodesNotVisited  = new HashSet<>();

    /**
     * Set of chosen nodes for the path.
     */
    public Set<MNode> lastDebugNodesPath  = new HashSet<>();

    /**
     * Default constructor.
     */
    public SyncPathMessage()
    {
        super();
    }

    /**
     * Create a new path message with the filled pathpoints.
     */
    public SyncPathMessage(final Set<MNode> lastDebugNodesVisited, final Set<MNode> lastDebugNodesNotVisited, final Set<MNode>  lastDebugNodesPath)
    {
        super();
        this.lastDebugNodesVisited = lastDebugNodesVisited;
        this.lastDebugNodesNotVisited = lastDebugNodesNotVisited;
        this.lastDebugNodesPath = lastDebugNodesPath;
    }

    public void toBytes(final FriendlyByteBuf buf)
    {
        buf.writeInt(lastDebugNodesVisited.size());
        for (final MNode node : lastDebugNodesVisited)
        {
            node.serializeToBuf(buf);
        }

        buf.writeInt(lastDebugNodesNotVisited.size());
        for (final MNode node : lastDebugNodesNotVisited)
        {
            node.serializeToBuf(buf);
        }

        buf.writeInt(lastDebugNodesPath.size());
        for (final MNode node : lastDebugNodesPath)
        {
            node.serializeToBuf(buf);
        }
    }

    public SyncPathMessage(final FriendlyByteBuf buf)
    {
        int size = buf.readInt();
        for (int i = 0; i < size; i++)
        {
            lastDebugNodesVisited.add(new MNode(buf));
        }

        size = buf.readInt();
        for (int i = 0; i < size; i++)
        {
            lastDebugNodesNotVisited.add(new MNode(buf));
        }

        size = buf.readInt();
        for (int i = 0; i < size; i++)
        {
            lastDebugNodesPath.add(new MNode(buf));
        }
    }

    public static class Handler {
        public static void handle(final SyncPathMessage m, final Supplier<NetworkEvent.Context> ctx) {
            if (ctx.get().getDirection().getReceptionSide().isServer()) {
                ctx.get().setPacketHandled(true);
                return;
            }

            ctx.get().enqueueWork(new Runnable() {
                // Use anon - lambda causes classloading issues
                @Override
                public void run() {
                    PathfindingDebugRenderer.lastDebugNodesVisited = m.lastDebugNodesVisited;
                    PathfindingDebugRenderer.lastDebugNodesNotVisited = m.lastDebugNodesNotVisited;
                    PathfindingDebugRenderer.lastDebugNodesPath = m.lastDebugNodesPath;
                }
            });
            ctx.get().setPacketHandled(true);

        }
    }
}
