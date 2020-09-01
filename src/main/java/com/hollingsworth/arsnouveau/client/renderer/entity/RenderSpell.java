package com.hollingsworth.arsnouveau.client.renderer.entity;


import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.TippedArrowRenderer;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.util.ResourceLocation;

public class RenderSpell extends TippedArrowRenderer {
    private final ResourceLocation entityTexture; // new ResourceLocation(ExampleMod.MODID, "textures/entity/spell_proj.png");


    public RenderSpell(EntityRendererManager renderManagerIn, ResourceLocation entityTexture)
    {
        super(renderManagerIn);
        this.entityTexture = entityTexture;

    }

    @Override
    public void render(ArrowEntity p_225623_1_, float p_225623_2_, float p_225623_3_, MatrixStack p_225623_4_, IRenderTypeBuffer p_225623_5_, int p_225623_6_) {
//        super.render(p_225623_1_, p_225623_2_, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_);
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
    public ResourceLocation getEntityTexture(ArrowEntity entity) {
        return this.entityTexture;
    }
}