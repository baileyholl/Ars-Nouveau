package com.hollingsworth.arsnouveau.client.renderer.entity;


import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class RenderSpell extends EntityRenderer<EntityProjectileSpell> {
    private final ResourceLocation entityTexture; // new ResourceLocation(ExampleMod.MODID, "textures/entity/spell_proj.png");


    public RenderSpell(EntityRendererManager renderManagerIn, ResourceLocation entityTexture)
    {
        super(renderManagerIn);
        this.entityTexture = entityTexture;

    }

    @Override
    public void render(EntityProjectileSpell proj, float entityYaw, float partialTicks, MatrixStack p_225623_4_, IRenderTypeBuffer p_225623_5_, int p_225623_6_) {
//        super.render(p_225623_1_, p_225623_2_, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_);
        if(proj.age < 1 || true)
            return;

        double deltaX = proj.getX() - proj.xOld;
        double deltaY = proj.getY() - proj.yOld;
        double deltaZ = proj.getZ() - proj.zOld;
        deltaX = deltaX + proj.getDeltaMovement().x() * partialTicks%20;
        deltaY = deltaY + proj.getDeltaMovement().y() *partialTicks%20;
        deltaZ = deltaZ + proj.getDeltaMovement().z() *partialTicks%20;
        double dist = Math.ceil(Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) * 10);
        int counter = 0;

        for (double j = 0; j < dist; j++) {
            double coeff = j / dist;

            proj.level.addParticle(GlowParticleData.createData(proj.getParticleColor()), (float) (proj.xOld + deltaX * coeff),
                    (float) (proj.yOld + deltaY * coeff),
                    (float) (proj.zOld  + deltaZ * coeff),
                    0.0125f * ParticleUtil.inRange(-0.5, 0.5), 0.0125f * ParticleUtil.inRange(-0.5, 0.5), 0.0125f * ParticleUtil.inRange(-0.5, 0.5));


         }

//        proj.world.addParticle(GlowParticleData.createData(proj.getParticleColor()), (float) (proj.getPosX()), (float) (proj.getPosY()) ,
//                (proj.getPosZ()),
//                0.0125f * ParticleUtil.inRange(-0.3, 0.3), 0.0125f * ParticleUtil.inRange(-0.3, 0.3), 0.0125f * ParticleUtil.inRange(-0.3, 0.3));
////
//        double nextX = proj.getPosX() + deltaX;
//        double nextY = proj.getPosY() + deltaY;
//        double nextZ = proj.getPosZ() + deltaZ;
//        dist = Math.ceil(Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) * 20);
//        counter = 0;
//
//        for (double j = 0; j < dist; j++) {
//            double coeff = j / dist;
//            counter += proj.world.rand.nextInt(3);
//            if (counter % (Minecraft.getInstance().gameSettings.particles.getId() == 0 ? 1 : 2 * Minecraft.getInstance().gameSettings.particles.getId()) == 0) {
//                proj.world.addParticle(GlowParticleData.createData(proj.getParticleColor()), (float) (nextX + deltaX * coeff), (float) (nextY + deltaY * coeff), (float) (nextZ + deltaZ * coeff),
//                        0.0125f * ParticleUtil.inRange(-0.3, 0.3), 0.0125f * ParticleUtil.inRange(-0.3, 0.3), 0.0125f * ParticleUtil.inRange(-0.3, 0.3));
//            }
//
//        }
    }
//
//    @Override
//    public void doRender(ArrowEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
//        super.doRender(entity, x, y, z, entityYaw, partialTicks);
//        float[] colors = new float[3];
//        colors[0] = .1f;
//        colors[1] = .1f;
//        colors[2] = .1f;
//       // BeaconTileEntityRenderer.renderBeamSegment(x, y, z, partialTicks, 1.0, entity.world.getGameTime(), 0, 1,colors, 1, 2);
////        for(int i =0; i < 10; i++){
////            double d0 = x + entity.world.rand.nextFloat();
////            double d1 = y + 1;
////            double d2 = z + entity.world.rand.nextFloat();
////
////            entity.world.addParticle(ParticleTypes.EXPLOSION_EMITTER, d0, d1, d2, 0.1, .1, 0.1);
////        }
//    }

    @Override
    public ResourceLocation getTextureLocation(EntityProjectileSpell entity) {
        return this.entityTexture;
    }
}