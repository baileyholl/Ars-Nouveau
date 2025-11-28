package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.tile.PlanariumTile;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class PlanariumModel extends GenericModel<PlanariumTile> {
    public PlanariumModel(boolean dimModel) {
        super(dimModel ? "planarium_dimension" : "planarium");
        this.textLoc = ArsNouveau.prefix("textures/" + textPathRoot + "/planarium.png");
    }


    @Override
    public @Nullable RenderType getRenderType(PlanariumTile animatable, ResourceLocation texture) {
        return RenderType.entityCutout(texture);
    }
}
