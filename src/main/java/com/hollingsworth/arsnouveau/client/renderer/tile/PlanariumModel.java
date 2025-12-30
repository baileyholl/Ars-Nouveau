package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;

public class PlanariumModel extends GenericModel {
    public PlanariumModel(boolean dimModel) {
        super(dimModel ? "planarium_dimension" : "planarium");
        this.textLoc = ArsNouveau.prefix("textures/" + textPathRoot + "/planarium.png");
    }


    @Override
    public @Nullable RenderType getRenderType(GeoAnimatable animatable, ResourceLocation texture) {
        return RenderType.entityCutout(texture);
    }
}
