package com.hollingsworth.arsnouveau.client.renderer.entity;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.renderer.base.GeoRenderState;

import java.util.Map;

/**
 * Compile-time satisfier for GeoEntityRenderer<T, R extends EntityRenderState & GeoRenderState>.
 * GeckoLib's EntityRenderStateMixin adds GeoRenderState to EntityRenderState at runtime,
 * but the Java compiler doesn't know this — so we create this class to satisfy the type bounds.
 *
 * IMPORTANT: EntityRenderStateMixin.addGeckolibData writes directly to geckolib$data field
 * (not via getDataMap()), so we must override addGeckolibData to redirect to our own map.
 * Otherwise reads (via getDataMap) and writes (via mixin) go to different maps, causing NPE.
 */
public class ArsEntityRenderState extends EntityRenderState implements GeoRenderState {
    private final it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap<DataTicket<?>, Object> dataMap =
            new it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap<>();

    @Override
    public Map<DataTicket<?>, Object> getDataMap() {
        return dataMap;
    }

    // Override mixin's concrete addGeckolibData so it uses our dataMap (same as getDataMap())
    @Override
    public <D> void addGeckolibData(DataTicket<D> ticket, D data) {
        dataMap.put(ticket, data);
    }

    // Override mixin's concrete hasGeckolibData for consistency
    @Override
    public boolean hasGeckolibData(DataTicket<?> ticket) {
        return dataMap.containsKey(ticket);
    }
}
