package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.common.block.tile.PlanariumTile;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class PlanariumModel extends GenericModel<PlanariumTile> {
    public PlanariumModel() {
        super("planarium");
    }


    @Override
    public @Nullable RenderType getRenderType(PlanariumTile animatable, ResourceLocation texture) {
        return RenderType.entityCutout(texture);
    }
}
