package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

// GeckoLib 5: getModelResource/getTextureResource now take GeoRenderState
public class AmethystGolemModel<T extends LivingEntity & GeoEntity> extends GeoModel<T> {

    private static final Identifier WILD_TEXTURE = ArsNouveau.prefix("textures/entity/amethyst_golem.png");
    public static final Identifier NORMAL_MODEL = ArsNouveau.prefix("amethyst_golem");
    public static final Identifier ANIMATIONS = ArsNouveau.prefix("amethyst_golem_animations");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return NORMAL_MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return WILD_TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(T drygmy) {
        return ANIMATIONS;
    }

}
