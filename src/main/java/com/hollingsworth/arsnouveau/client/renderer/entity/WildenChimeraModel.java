package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.WildenChimera;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.animatable.model.CoreGeoBone;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import javax.annotation.Nullable;

public class WildenChimeraModel extends GeoModel<WildenChimera> {

    private static final ResourceLocation TEXTURE = ArsNouveau.prefix( "textures/entity/wilden_chimera.png");
    public static final ResourceLocation NORMAL_MODEL = ArsNouveau.prefix( "geo/wilden_chimera.geo.json");
    public static final ResourceLocation ANIMATIONS = ArsNouveau.prefix( "animations/wilden_chimera_animations.json");



    @Override
    public void setCustomAnimations(WildenChimera entity, long uniqueID, @Nullable AnimationState customPredicate) {
        super.setCustomAnimations(entity, uniqueID, customPredicate);

        CoreGeoBone head = this.getAnimationProcessor().getBone("head");
        EntityModelData extraData = (EntityModelData) customPredicate.getExtraData().get(DataTickets.ENTITY_MODEL_DATA);
        if(!entity.isFlying()){
            head.setRotY(extraData.netHeadYaw() * 0.012453292F);
            head.setRotX(extraData.headPitch() * 0.037453292F);
        }

        boolean useBigWings = entity.hasWings() && (entity.isFlying() || (entity.hasWings() && entity.isRamPrep()));

        this.getBone("wings_folded").get().setHidden(!entity.hasWings() || useBigWings);

        this.getBone("wings_extended_right").get().setHidden(!useBigWings);
        this.getBone("wings_extended_left").get().setHidden(!useBigWings);
        this.getBone("wings_extended_right2").get().setHidden(!useBigWings);
        this.getBone("wings_extended_left2").get().setHidden(!useBigWings);

        this.getBone("spikes_extended").get().setHidden(!entity.isDefensive() || !entity.hasSpikes());
        this.getBone("spikes_retracted").get().setHidden(!entity.hasSpikes() || entity.isDefensive());
        this.getBone("fins").get().setHidden(!entity.hasSpikes());
        this.getBone("horns").get().setHidden(!entity.hasHorns());
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
