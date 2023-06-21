package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.WildenChimera;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationState;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

import javax.annotation.Nullable;

public class WildenChimeraModel extends GeoModel<WildenChimera> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(ArsNouveau.MODID, "textures/entity/wilden_chimera.png");
    public static final ResourceLocation NORMAL_MODEL = new ResourceLocation(ArsNouveau.MODID, "geo/wilden_chimera.geo.json");
    public static final ResourceLocation ANIMATIONS = new ResourceLocation(ArsNouveau.MODID, "animations/wilden_chimera_animations.json");



    @Override
    public void setCustomAnimations(WildenChimera entity, int uniqueID, @Nullable AnimationState customPredicate) {
        super.setCustomAnimations(entity, uniqueID, customPredicate);

        IBone head = this.getAnimationProcessor().getBone("head");
        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
        if(!entity.isFlying()){
            head.setRotationY(extraData.netHeadYaw * 0.012453292F);
            head.setRotationX(extraData.headPitch * 0.037453292F);
        }

        boolean useBigWings = entity.hasWings() && (entity.isFlying() || (entity.hasWings() && entity.isRamPrep()));

        this.getBone("wings_folded").setHidden(!entity.hasWings() || useBigWings);

        this.getBone("wings_extended_right").setHidden(!useBigWings);
        this.getBone("wings_extended_left").setHidden(!useBigWings);
        this.getBone("wings_extended_right2").setHidden(!useBigWings);
        this.getBone("wings_extended_left2").setHidden(!useBigWings);

        this.getBone("spikes_extended").setHidden(!entity.isDefensive() || !entity.hasSpikes());
        this.getBone("spikes_retracted").setHidden(!entity.hasSpikes() || entity.isDefensive());
        this.getBone("fins").setHidden(!entity.hasSpikes());
        this.getBone("horns").setHidden(!entity.hasHorns());
    }

    @Override
    public ResourceLocation getModelResource(WildenChimera entity) {
        return NORMAL_MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(WildenChimera entity) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(WildenChimera animatable) {
        return ANIMATIONS;
    }
}
