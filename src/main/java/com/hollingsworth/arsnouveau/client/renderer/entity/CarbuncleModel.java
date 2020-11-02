package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.EntityCarbuncle;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib.core.event.predicate.AnimationEvent;
import software.bernie.geckolib.core.processor.IBone;
import software.bernie.geckolib.model.AnimatedGeoModel;

import javax.annotation.Nullable;

public class CarbuncleModel extends AnimatedGeoModel<EntityCarbuncle> {

    private static final ResourceLocation WILD_TEXTURE = new ResourceLocation(ArsNouveau.MODID, "textures/entity/carbuncle_wild_orange.png");
    private static final ResourceLocation TAMED_TEXTURE = new ResourceLocation(ArsNouveau.MODID, "textures/entity/carbuncle_orange.png");

    @Override
    public void setLivingAnimations(EntityCarbuncle entity, Integer uniqueID, @Nullable AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
      //  IBone head = this.getAnimationProcessor().getModelRendererList();
//        for(Object bone : this.getAnimationProcessor().getModelRendererList()){
//            System.out.println(((IBone)bone).getName());
//        }
        IBone head = this.getAnimationProcessor().getBone("head");
        if(entity != null && head != null){
       //     entity.setHeadRotation(entity.rotationYaw * 0.017453292F, (int) (entity.rotationPitch * 0.017453292F));
         //   System.out.println(entity.ro);
          //  System.out.println(entity.rotationYaw);
//            head.setRotationX(Vector3f.XP.rotation(entity.rotationPitch * 0.017453292F).getX());
//            head.setRotationY(Vector3f.YP.rotation((entity.rotationYawHead * 0.017453292F)).getY());
        }
    }

    @Override
    public ResourceLocation getModelLocation(EntityCarbuncle carbuncle) {
        return new ResourceLocation(ArsNouveau.MODID , "geo/carbuncle.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(EntityCarbuncle carbuncle) {
        return carbuncle.isTamed() ? TAMED_TEXTURE : WILD_TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(EntityCarbuncle carbuncle) {
        return new ResourceLocation(ArsNouveau.MODID , "animations/carbuncle_animations.json");
    }


}
