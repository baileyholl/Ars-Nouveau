package com.hollingsworth.arsnouveau.client.renderer.entity;

import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.renderer.base.GeoRenderState;

import java.util.Map;

/**
 * Compile-time satisfier for GeoArmorRenderer<T, R extends HumanoidRenderState & GeoRenderState>.
 * GeckoLib's mixin adds GeoRenderState to HumanoidRenderState at runtime,
 * but the Java compiler doesn't know this — so we create this class.
 *
 * IMPORTANT: same addGeckolibData/getDataMap split as Entity/BlockEntity states — override both.
 */
public class ArsHumanoidRenderState extends HumanoidRenderState implements GeoRenderState {
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
