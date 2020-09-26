package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleLineData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.entity.EntityCarbuncle;
import com.hollingsworth.arsnouveau.common.entity.EntityEarthElemental;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EarthElementalRenderer extends MobRenderer<EntityEarthElemental, EarthElementalModel> {
    private static final ResourceLocation WILD_TEXTURE = new ResourceLocation(ArsNouveau.MODID, "textures/entity/earth_elemental.png");

    public EarthElementalRenderer(EntityRendererManager manager) {
        super(manager, new EarthElementalModel(), 0.2f);
    }

    @Override
    public void render(EntityEarthElemental entityElemental, float p_225623_2_, float p_225623_3_, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int p_225623_6_) {
        super.render(entityElemental, p_225623_2_, p_225623_3_, matrixStack, iRenderTypeBuffer, p_225623_6_);
        World world = entityElemental.world;
        Vec3d particlePos = entityElemental.getPositionVec();
        if(world.rand.nextInt(20) == 0){
            for(int i =0; i< 2; i++){
                world.addParticle(ParticleTypes.FLAME,
                        particlePos.getX(), particlePos.getY()+0.5, particlePos.getZ(),
                        ParticleUtil.inRange(-0.05, 0.05), ParticleUtil.inRange(-0.05, 0.05), ParticleUtil.inRange(-0.05, 0.05));
                world.addParticle(ParticleTypes.SMOKE,
                        particlePos.getX(), particlePos.getY()+0.5, particlePos.getZ(),
                        ParticleUtil.inRange(-0.05, 0.05), ParticleUtil.inRange(-0.05, 0.05), ParticleUtil.inRange(-0.05, 0.05));
            }
        }
    }

    @Override
    public ResourceLocation getEntityTexture(EntityEarthElemental entity) {
        return WILD_TEXTURE;
    }

}
