package com.hollingsworth.arsnouveau.client.renderer.entity.familiar;

import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleSparkleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.client.renderer.entity.WhirlisprigModel;
import com.hollingsworth.arsnouveau.common.entity.familiar.FamiliarWhirlisprig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib3.core.processor.IBone;

import java.util.Random;

public class FamiliarWhirlisprigRenderer<T extends FamiliarWhirlisprig> extends GenericFamiliarRenderer<T>{

    public FamiliarWhirlisprigRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new WhirlisprigModel<>());
    }

    @Override
    public void render(T entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        if(Minecraft.getInstance().isPaused())
            return;
        Level world = entityIn.getCommandSenderWorld();
        Random rand = ParticleUtil.r;
        Vec3 particlePos = entityIn.position();

        IBone sylph = ((WhirlisprigModel<T>) getGeoModelProvider()).getBone("sylph");
        IBone propellers = ((WhirlisprigModel<T>) getGeoModelProvider()).getBone("propellers");

        float offsetY = sylph.getPositionY() / 9f;
        float roteAngle = propellers.getRotationY() / 4;

        if (rand.nextInt(5) == 0) {
            for (int i = 0; i < 5; i++) {
                world.addParticle(ParticleSparkleData.createData(new ParticleColor(52, 255, 36), 0.05f, 60),
                        particlePos.x() + Math.cos(roteAngle) / 2, particlePos.y() + 0.5 + offsetY, particlePos.z() + Math.sin(roteAngle) / 2,
                        0, 0, 0);
            }

        }
    }

}
