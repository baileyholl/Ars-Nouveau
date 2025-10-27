package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.item.ICosmeticItem;
import com.hollingsworth.arsnouveau.common.entity.familiar.FamiliarWixie;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import javax.annotation.Nullable;

public class WixieModel<T extends LivingEntity & GeoAnimatable> extends GeoModel<T> {

    private static final ResourceLocation WILD_TEXTURE = ArsNouveau.prefix("textures/entity/wixie.png");

    @Override
    public void setCustomAnimations(T entity, long uniqueID, @Nullable AnimationState<T> customPredicate) {
        super.setCustomAnimations(entity, uniqueID, customPredicate);
        GeoBone head = this.getAnimationProcessor().getBone("head");
        EntityModelData extraData = (EntityModelData) customPredicate.getExtraData().get(DataTickets.ENTITY_MODEL_DATA);
        head.setRotX(extraData.headPitch() * 0.010453292F);
        head.setRotY(extraData.netHeadYaw() * 0.015453292F);
        if (entity instanceof FamiliarWixie w) {
            // hides the hat bone if the cosmetic item is a hat
            this.getAnimationProcessor().getBone("hat")
                    .setHidden(w.getCosmeticItem().getItem() instanceof ICosmeticItem cosmetic && cosmetic.getBone(w).equals("hat"));
        }
    }

    @Override
    public ResourceLocation getModelResource(T entityWixie) {
        return ArsNouveau.prefix("geo/wixie.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(T entityWixie) {
        return WILD_TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(T entityWixie) {
        return ArsNouveau.prefix("animations/wixie_animations.json");
    }
}
