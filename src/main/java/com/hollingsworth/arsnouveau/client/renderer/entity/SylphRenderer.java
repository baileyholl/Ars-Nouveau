package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleSparkleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.entity.EntitySylph;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.Random;

public class SylphRenderer extends MobRenderer<EntitySylph, SylphModel> {
    private static final ResourceLocation WILD_TEXTURE = new ResourceLocation(ArsNouveau.MODID, "textures/entity/sylph.png");

    private static SylphModel model = new SylphModel();
    public SylphRenderer(EntityRendererManager manager) {
        super(manager, new SylphModel(), 0.2f);
    }

    public SylphRenderer(EntityRendererManager renderManagerIn, SylphModel entityModelIn, float shadowSizeIn) {
        super(renderManagerIn, entityModelIn, shadowSizeIn);
    }

    @Override
    public void render(EntitySylph entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        World world = entityIn.getEntityWorld();
        Random rand = ParticleUtil.r;
        Vector3d particlePos = entityIn.getPositionVec();


        float offsetY = this.getEntityModel().sylph.positionOffsetY/9f;
        float roteAngle = this.getEntityModel().propellers.rotateAngleY / 4;

        if(rand.nextInt(5) == 0){
            for(int i =0; i < 5; i++){
                world.addParticle(ParticleSparkleData.createData(new ParticleColor(52,255,36), 0.05f, 60),
                        particlePos.getX()  + Math.cos(roteAngle)/2 , particlePos.getY() +0.5+ offsetY , particlePos.getZ()  + Math.sin(roteAngle)/2,
                        0, 0,0);
            }

        }

    }

    @Override
    public ResourceLocation getEntityTexture(EntitySylph entity) {
        return WILD_TEXTURE;
    }
}
