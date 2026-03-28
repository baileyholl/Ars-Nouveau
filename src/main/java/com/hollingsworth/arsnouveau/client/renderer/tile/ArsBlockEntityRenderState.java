package com.hollingsworth.arsnouveau.client.renderer.tile;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.renderer.base.GeoRenderState;

import java.util.Map;

/**
 * Compile-time satisfier for GeoBlockRenderer<T, R extends BlockEntityRenderState & GeoRenderState>.
 * GeckoLib's BlockEntityRenderStateMixin adds GeoRenderState to BlockEntityRenderState at runtime,
 * but the Java compiler doesn't know this - so we create this class to satisfy the bounds.
 *
 * IMPORTANT: BlockEntityRenderStateMixin.addGeckolibData writes to geckolib$data field directly
 * (not via getDataMap()), so we override addGeckolibData/hasGeckolibData to redirect to our map.
 */
public class ArsBlockEntityRenderState extends BlockEntityRenderState implements GeoRenderState {
    private final it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap<DataTicket<?>, Object> dataMap =
            new it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap<>();

    @Override
    public Map<DataTicket<?>, Object> getDataMap() {
        return dataMap;
    }

    @Override
    public <D> void addGeckolibData(DataTicket<D> ticket, D data) {
        dataMap.put(ticket, data);
    }

    @Override
    public boolean hasGeckolibData(DataTicket<?> ticket) {
        return dataMap.containsKey(ticket);
    }
}
