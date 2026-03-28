package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.ModelProperty;
import com.hollingsworth.arsnouveau.api.particle.timelines.ProjectileTimeline;
import com.hollingsworth.arsnouveau.api.registry.ParticlePropertyRegistry;
import com.hollingsworth.arsnouveau.api.registry.ParticleTimelineRegistry;
import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

/**
 * GeckoLib 5 model for EntityProjectileSpell.
 *
 * Model and texture are resolved per-entity from the spell's ModelProperty (particle timeline).
 * addAdditionalStateData stores model+texture Identifiers via DataTickets so getModelResource/
 * getTextureResource (which only receive GeoRenderState) can return the correct values.
 *
 * Models:    ars_nouveau:cube  → geo/cube.geo.json   (CUBE_BODY)
 *            ars_nouveau:spike → geo/spike.geo.json  (SPIKE_BODY)
 * Textures:  CUBE_BODY → textures/particle/projectile_{age/5 % 4}.png (animated)
 *            SPIKE_BODY → textures/entity/spike.png
 */
public class StyledProjectileModel extends GeoModel<EntityProjectileSpell> {

    // Fallback when entity has no ModelProperty configured
    public static final Identifier FALLBACK_MODEL   = ArsNouveau.prefix("cube");
    public static final Identifier FALLBACK_TEXTURE = ArsNouveau.prefix("textures/particle/projectile_0.png");
    public static final Identifier ANIMATIONS       = ArsNouveau.prefix("empty");

    // Per-entity model/texture passed from addAdditionalStateData to the get*Resource methods
    public static final DataTicket<Identifier> MODEL_TICKET   = DataTicket.create("ars_nouveau_proj_model",   Identifier.class);
    public static final DataTicket<Identifier> TEXTURE_TICKET = DataTicket.create("ars_nouveau_proj_texture", Identifier.class);

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        Identifier model = renderState.getGeckolibData(MODEL_TICKET);
        return model != null ? model : FALLBACK_MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        Identifier texture = renderState.getGeckolibData(TEXTURE_TICKET);
        return texture != null ? texture : FALLBACK_TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(EntityProjectileSpell animatable) {
        return ANIMATIONS;
    }

    /**
     * Called by GeckoLib during extractRenderState.
     * Reads the entity's ModelProperty from its spell particle timeline and stores
     * model + texture identifiers in the render state via DataTickets.
     */
    @Override
    public void addAdditionalStateData(EntityProjectileSpell animatable, @Nullable Object relatedObject, GeoRenderState renderState) {
        try {
            ProjectileTimeline timeline = animatable.resolver().spell.particleTimeline()
                    .get(ParticleTimelineRegistry.PROJECTILE_TIMELINE.get());
            ModelProperty modelProp = timeline.trailEffect.motion().propertyMap
                    .get(ParticlePropertyRegistry.MODEL_PROPERTY.get());
            if (modelProp == null || modelProp.selectedResource == ModelProperty.NONE) return;

            renderState.addGeckolibData(MODEL_TICKET,   modelProp.selectedResource.resourceLocation());
            renderState.addGeckolibData(TEXTURE_TICKET, modelProp.selectedResource.getTexture().apply(animatable));
        } catch (Exception ignored) {
            // Resolver not yet set on entity (e.g. during spawn) — fallbacks in get*Resource handle it
        }
    }
}
