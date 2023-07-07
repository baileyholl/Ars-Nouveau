package com.hollingsworth.arsnouveau.client.renderer.item;

import net.minecraft.world.item.Item;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class FixedGeoItemRenderer<T extends Item & GeoItem> extends GeoItemRenderer<T> {
    public FixedGeoItemRenderer(GeoModel modelProvider) {
        super(modelProvider);
        this.useAlternateGuiLighting();
    }
}