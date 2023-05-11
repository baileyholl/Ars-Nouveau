package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.EntityChimera;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

import javax.annotation.Nullable;

public class WildenChimeraModel extends AnimatedGeoModel<EntityChimera> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(ArsNouveau.MODID, "textures/entity/wilden_chimera.png");
    public static final ResourceLocation NORMAL_MODEL = new ResourceLocation(ArsNouveau.MODID, "geo/wilden_chimera.geo.json");
    public static final ResourceLocation DEFENSIVE_MODEL = new ResourceLocation(ArsNouveau.MODID, "geo/wilden_chimera_defense.geo.json");
    public static final ResourceLocation ANIMATIONS = new ResourceLocation(ArsNouveau.MODID, "animations/wilden_chimera_animations.json");



    @Override
    public void setCustomAnimations(EntityChimera entity, int uniqueID, @Nullable AnimationEvent customPredicate) {
        super.setCustomAnimations(entity, uniqueID, customPredicate);

        IBone head = this.getAnimationProcessor().getBone("head");
        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
        head.setRotationX(extraData.headPitch * 0.037453292F + 0.45f);
        head.setRotationY(extraData.netHeadYaw * 0.012453292F);
        this.getBone("wings").setHidden(!entity.hasWings());
        this.getBone("spikes_extended").setHidden(!entity.isDefensive() || !entity.hasSpikes());
        this.getBone("spikes_retracted").setHidden(!entity.hasSpikes() || entity.isDefensive());
        this.getBone("fins").setHidden(!entity.hasSpikes());
        this.getBone("horns").setHidden(!entity.hasHorns());

    }

    @Override
    public ResourceLocation getModelResource(EntityChimera entity) {
        return entity.isDefensive() ? DEFENSIVE_MODEL : NORMAL_MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityChimera entity) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityChimera animatable) {
        return ANIMATIONS;
    }
}
