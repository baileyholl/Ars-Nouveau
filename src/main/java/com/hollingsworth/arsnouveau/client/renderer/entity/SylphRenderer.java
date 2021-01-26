package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleSparkleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.entity.EntitySylph;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.exception.GeoModelException;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

import java.util.Random;

public class SylphRenderer extends GeoEntityRenderer<EntitySylph> {
    private static final ResourceLocation WILD_TEXTURE = new ResourceLocation(ArsNouveau.MODID, "textures/entity/sylph.png");

    private static SylphModel model = new SylphModel();

    public SylphRenderer(EntityRendererManager manager) {
        super(manager, new SylphModel());
    }


    @Override
    public void render(EntitySylph entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        try {
            super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
            World world = entityIn.getEntityWorld();
            Random rand = ParticleUtil.r;
            Vector3d particlePos = entityIn.getPositionVec();

            IBone sylph = ((SylphModel) getGeoModelProvider()).getBone("sylph");
            IBone propellers = ((SylphModel) getGeoModelProvider()).getBone("propellers");

            float offsetY = sylph.getPositionY() / 9f;
            float roteAngle = propellers.getRotationY() / 4;

            if (rand.nextInt(5) == 0) {
                for (int i = 0; i < 5; i++) {
                    world.addParticle(ParticleSparkleData.createData(new ParticleColor(52, 255, 36), 0.05f, 60),
                            particlePos.getX() + Math.cos(roteAngle) / 2, particlePos.getY() + 0.5 + offsetY, particlePos.getZ() + Math.sin(roteAngle) / 2,
                            0, 0, 0);
                }

            }
        }catch (GeoModelException e){
            System.out.println("Missing model detected, restart client.");
            e.printStackTrace();
        }

    }

    @Override
    public ResourceLocation getEntityTexture(EntitySylph entity) {
        return WILD_TEXTURE;
    }
}
