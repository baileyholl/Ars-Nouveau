package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleSparkleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.entity.Whirlisprig;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

import javax.annotation.Nullable;
import java.util.Random;

public class SylphRenderer extends GeoEntityRenderer {

    public SylphRenderer(EntityRendererProvider.Context manager) {
        super(manager, new SylphModel());
    }

    @Override
    public void render(LivingEntity entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        if(Minecraft.getInstance().isPaused())
            return;
        Level world = entityIn.getCommandSenderWorld();
        Random rand = ParticleUtil.r;
        Vec3 particlePos = entityIn.position();

        IBone sylph = ((SylphModel) getGeoModelProvider()).getBone("sylph");
        IBone propellers = ((SylphModel) getGeoModelProvider()).getBone("propellers");

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

    @Override
    public ResourceLocation getTextureLocation(LivingEntity entity) {
        if(entity instanceof Whirlisprig){
            return new ResourceLocation(ArsNouveau.MODID, "textures/entity/sylph_" + (((Whirlisprig) entity).getColor().isEmpty() ? "summer" : ((Whirlisprig) entity).getColor())+ ".png");
        }
        return new ResourceLocation(ArsNouveau.MODID, "textures/entity/sylph_summer.png");
    }

    @Override
    public RenderType getRenderType(Object animatable, float partialTicks, PoseStack stack, @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        return RenderType.entityCutoutNoCull(textureLocation);
    }
}
