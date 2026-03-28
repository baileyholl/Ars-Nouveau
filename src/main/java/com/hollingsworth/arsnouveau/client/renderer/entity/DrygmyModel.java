package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.renderer.ANDataTickets;
import com.hollingsworth.arsnouveau.common.entity.EntityDrygmy;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

// GeckoLib 5: getTextureResource reads DRYGMY_COLOR from render state (set by renderer in addRenderData)
public class DrygmyModel<T extends LivingEntity & GeoAnimatable> extends GeoModel<T> {

    public static final Identifier NORMAL_MODEL = ArsNouveau.prefix("drygmy");
    public static final Identifier ANIMATIONS = ArsNouveau.prefix("drygmy_animations");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return NORMAL_MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        String color = renderState.getGeckolibData(ANDataTickets.DRYGMY_COLOR);
        if (color == null || color.isEmpty()) color = "brown";
        return EntityDrygmy.TEXTURES.computeIfAbsent(color, c -> ArsNouveau.prefix("textures/entity/drygmy_" + c + ".png"));
    }

    @Override
    public Identifier getAnimationResource(T drygmy) {
        return ANIMATIONS;
    }
}
