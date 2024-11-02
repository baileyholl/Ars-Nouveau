package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.Goolem;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.GeoModel;

public class GoolemModel extends GeoModel<Goolem> {
    public static ResourceLocation TEXTURE = ArsNouveau.prefix("textures/entity/lily.png");
    public static ResourceLocation MODEL = ArsNouveau.prefix("geo/lily.geo.json");
    public static ResourceLocation ANIMATION = ArsNouveau.prefix("animations/lily_animations.json");
    public GoolemModel() {
        super();
    }


    @Override
    public @Nullable RenderType getRenderType(Goolem animatable, ResourceLocation texture) {
        return super.getRenderType(animatable, texture);
    }

    @Override
    public ResourceLocation getModelResource(Goolem animatable) {
        return ArsNouveau.prefix("geo/goolem_prototype.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Goolem whirlisprig) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(Goolem whirlisprig) {
        return ANIMATION;
    }
}
