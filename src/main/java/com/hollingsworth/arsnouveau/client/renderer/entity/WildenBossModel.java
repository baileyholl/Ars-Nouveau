package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.EntityChimera;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

import javax.annotation.Nullable;

public class WildenBossModel extends AnimatedGeoModel<EntityChimera> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(ArsNouveau.MODID, "textures/entity/chimera.png");
    private static final ResourceLocation DEFENSIVE_TEXTURE = new ResourceLocation(ArsNouveau.MODID, "textures/entity/chimera_defense.png");

    public static final ResourceLocation NORMAL_MODEL = new ResourceLocation(ArsNouveau.MODID , "geo/wilden_chimera.geo.json");
    public static final ResourceLocation DEFENSIVE_MODEL = new ResourceLocation(ArsNouveau.MODID , "geo/wilden_chimera_defense.geo.json");


    @Override
    public void setLivingAnimations(EntityChimera entity, Integer uniqueID, @Nullable AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
        if(entity.isFlying())
            return;
        IBone head = this.getAnimationProcessor().getBone("head");
        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
        head.setRotationX(extraData.headPitch * 0.017453292F + 0.45f);
        head.setRotationY(extraData.netHeadYaw * 0.012453292F );
        this.getBone("wings").setHidden(!entity.hasWings());
        this.getBone("spikes_lower_body").setHidden(!entity.hasSpikes());
        this.getBone("spikes_upper_body").setHidden(!entity.hasSpikes());
        this.getBone("spikes_right_forearm").setHidden(!entity.hasSpikes());
        this.getBone("spikes_left_upper_arm").setHidden(!entity.hasSpikes());
        this.getBone("spikes_left_forearm").setHidden(!entity.hasSpikes());
        this.getBone("spikes_right_thigh").setHidden(!entity.hasSpikes());
        this.getBone("spikes_left_thigh").setHidden(!entity.hasSpikes());
        this.getBone("spikes_right_upper_arm").setHidden(!entity.hasSpikes());
        this.getBone("horns").setHidden(!entity.hasHorns());

    }

    @Override
    public ResourceLocation getModelLocation(EntityChimera entity) {
        return entity.isDefensive() ? DEFENSIVE_MODEL : NORMAL_MODEL;
    }

    @Override
    public ResourceLocation getTextureLocation(EntityChimera entity) {
        return entity.isDefensive() ? DEFENSIVE_TEXTURE : TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(EntityChimera animatable) {
        return new ResourceLocation(ArsNouveau.MODID, "animations/wilden_chimera_animations.geo.json");
    }
}
