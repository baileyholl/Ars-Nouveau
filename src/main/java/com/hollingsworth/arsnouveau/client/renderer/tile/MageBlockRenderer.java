package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemBlockRenderer;
import com.hollingsworth.arsnouveau.client.renderer.entity.ArsEntityRenderState;
import com.hollingsworth.arsnouveau.common.entity.EnchantedMageblock;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

// GeckoLib 5.4.2: GeoEntityRenderer requires R extends EntityRenderState & GeoRenderState
// ArsEntityRenderState satisfies this at compile time; GeckoLib's mixin does it at runtime.
public class MageBlockRenderer extends GeoEntityRenderer<EnchantedMageblock, ArsEntityRenderState> {

    public static GenericModel model = new GenericModel("mage_block");

    public MageBlockRenderer(EntityRendererProvider.Context rendererDispatcherIn) {
        super(rendererDispatcherIn, model);
    }

    // GeckoLib 5: getRenderColor returns ARGB int, context is Void
    @Override
    public int getRenderColor(EnchantedMageblock animatable, Void context, float partialTick) {
        ParticleColor particleColor = animatable.getColor();
        int r = (int)(particleColor.getRed() * 255);
        int g = (int)(particleColor.getGreen() * 255);
        int b = (int)(particleColor.getBlue() * 255);
        return (0xFF << 24) | (r << 16) | (g << 8) | b;
    }

    // GeckoLib 5: createRenderState(T, Void) is the correct override (no-arg is final)
    @Override
    public ArsEntityRenderState createRenderState(EnchantedMageblock animatable, Void context) {
        return new ArsEntityRenderState();
    }

    public static GenericItemBlockRenderer getISTER() {
        return new GenericItemBlockRenderer(model);
    }
}
