package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.api.registry.ParticleTimelineRegistry;
import com.hollingsworth.arsnouveau.api.registry.SpellCasterRegistry;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.renderer.ANGeoModel;
import com.hollingsworth.arsnouveau.common.items.SpellCrossbow;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class SpellCrossbowRenderer extends GeoItemRenderer<SpellCrossbow> {
    public SpellCrossbowRenderer() {
        super(new ANGeoModel<>("spell_crossbow", "textures/item/spell_crossbow.png", "wand_animation"));
    }

    @Override
    public int getRenderColor(SpellCrossbow animatable, GeoItemRenderer.RenderData renderData, float partialTick) {
        ParticleColor color = ParticleColor.defaultParticleColor();
        var caster = SpellCasterRegistry.from(renderData.itemStack());
        if (caster != null) {
            var timeline = caster.getSpell().particleTimeline().get(ParticleTimelineRegistry.PROJECTILE_TIMELINE.get());
            color = timeline.trailEffect.particleOptions().colorProp().color();
        }
        return ARGB.colorFromFloat(0.75f, color.getRed(), color.getGreen(), color.getBlue());
    }

    @Override
    public RenderType getRenderType(GeoRenderState renderState, Identifier texture) {
        return RenderTypes.entityTranslucent(texture);
    }
}
