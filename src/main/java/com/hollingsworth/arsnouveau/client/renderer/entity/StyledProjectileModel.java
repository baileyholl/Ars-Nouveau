package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.ModelProperty;
import com.hollingsworth.arsnouveau.api.particle.timelines.ProjectileTimeline;
import com.hollingsworth.arsnouveau.api.registry.ParticlePropertyRegistry;
import com.hollingsworth.arsnouveau.api.registry.ParticleTimelineRegistry;
import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class StyledProjectileModel extends GeoModel<EntityProjectileSpell> {
    private static final ResourceLocation TEXTURE = ArsNouveau.prefix( "textures/entity/projectile.png");
    public static final ResourceLocation NORMAL_MODEL = ArsNouveau.prefix( "geo/cube.geo.json");
    public static final ResourceLocation ANIMATIONS = ArsNouveau.prefix( "animations/empty.json");

    @Override
    public ResourceLocation getModelResource(EntityProjectileSpell animatable) {
        ResourceLocation resourceLocation = getModelRes(animatable);
        return ResourceLocation.fromNamespaceAndPath(resourceLocation.getNamespace(), "geo/" + resourceLocation.getPath() + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(EntityProjectileSpell animatable) {
        ResourceLocation resourceLocation = getModelRes(animatable);
        return ResourceLocation.fromNamespaceAndPath(resourceLocation.getNamespace(), "textures/entity/" + resourceLocation.getPath() + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(EntityProjectileSpell animatable) {
        return ANIMATIONS;
    }

    public ResourceLocation getModelRes(EntityProjectileSpell projectileSpell){
        ProjectileTimeline timeline = projectileSpell.resolver().spell.particleTimeline().get(ParticleTimelineRegistry.PROJECTILE_TIMELINE.get());
        ModelProperty modelProperty = timeline.trailEffect.particleOptions().map.get(ParticlePropertyRegistry.MODEL_PROPERTY.get());
        ResourceLocation resourceLocation = modelProperty.selectedResource.resourceLocation();
        return resourceLocation;
    }
}
