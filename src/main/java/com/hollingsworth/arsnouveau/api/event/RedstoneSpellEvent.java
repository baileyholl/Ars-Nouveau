package com.hollingsworth.arsnouveau.api.event;

import com.hollingsworth.arsnouveau.common.spell.effect.EffectRedstone;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class RedstoneSpellEvent implements ITimedEvent {

    private int duration;
    private final BlockHitResult result;
    private final Level world;


    public RedstoneSpellEvent(int delay, BlockHitResult rayTraceResult, Level world) {
        this.duration = delay;
        this.result = rayTraceResult;
        this.world = world;
    }

    @Override
    public void tick(boolean serverSide) {
        if (!serverSide) return;
        duration--;
        if (duration <= 0 && world.isLoaded(result.getBlockPos())) {
            EffectRedstone.signalMap.get(world.dimension().location().toString()).remove(result.getBlockPos());
            BlockPos neighborPos = result.getBlockPos().relative(result.getDirection());
            world.updateNeighborsAt(neighborPos, world.getBlockState(neighborPos).getBlock());
        }
    }

    /**
     * If this event should be removed from the queue
     */
    @Override
    public boolean isExpired() {
        return duration <= 0 || world == null;
    }

    @SubscribeEvent
    public static void onServerShutdown(ServerStoppedEvent event) {
        event.getServer().getAllLevels().forEach(dim -> EffectRedstone.signalMap.get(dim.dimension().location().toString()).clear());
    }

}
