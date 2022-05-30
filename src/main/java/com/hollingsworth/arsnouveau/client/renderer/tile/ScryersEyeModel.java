package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.common.block.tile.ScryersOculusTile;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ScryersEyeModel extends AnimatedGeoModel<ScryersOculusTile> {
    public static ResourceLocation SQUINTING = new ResourceLocation(ArsNouveau.MODID, "textures/blocks/scryers_eye_squinting.png");
    public static ResourceLocation ALERT = new ResourceLocation(ArsNouveau.MODID, "textures/blocks/scryers_eye_alert.png");
    public static ResourceLocation IDLE = new ResourceLocation(ArsNouveau.MODID, "textures/blocks/scryers_eye_idle.png");
    public static ResourceLocation SLEEPING = new ResourceLocation(ArsNouveau.MODID, "textures/blocks/scryers_eye_sleeping.png");
    public static final ResourceLocation anim = new ResourceLocation(ArsNouveau.MODID , "animations/scryers_eye_animations.json");
    public static final ResourceLocation model = new ResourceLocation(ArsNouveau.MODID , "geo/scryers_eye.geo.json");
    @Override
    public ResourceLocation getModelLocation(ScryersOculusTile object) {
        return model;
    }

    @Override
    public ResourceLocation getTextureLocation(ScryersOculusTile object) {
        return object != null && object.playerNear ? SQUINTING : IDLE;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(ScryersOculusTile animatable) {
        return anim;
    }


    @Override
    public void setLivingAnimations(ScryersOculusTile pBlockEntity, Integer uniqueID) {
        super.setLivingAnimations(pBlockEntity, uniqueID);
        IBone eye = this.getAnimationProcessor().getBone("eye");
        if(eye == null)
            return;
        // Taken from enchantment table
        float f1;
        for(f1 = pBlockEntity.rot - pBlockEntity.oRot; f1 >= (float)Math.PI; f1 -= ((float)Math.PI * 2F)) {
        }

        while(f1 < -(float)Math.PI) {
            f1 += ((float)Math.PI * 2F);
        }
        float f2 = pBlockEntity.oRot + f1 * ClientInfo.partialTicks - 4.7f;
        eye.setRotationY(-f2);
        eye.setPositionY((Mth.sin((float) ((ClientInfo.ticksInGame + ClientInfo.partialTicks)/ 10.0f)))/2f);
    }
}
