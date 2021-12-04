package com.hollingsworth.arsnouveau.client.renderer.tile;


import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleLineData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.tile.CrystallizerTile;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class CrystallizerRenderer implements BlockEntityRenderer<CrystallizerTile> {

    public CrystallizerRenderer(BlockEntityRendererProvider.Context manager) {

    }

    @Override
    public void render(CrystallizerTile crystallizerTile, float f, PoseStack ms, MultiBufferSource buffers, int light, int overlay) {
        Level world = crystallizerTile.getLevel();
        BlockPos pos  = crystallizerTile.getBlockPos();

        double x = crystallizerTile.getBlockPos().getX();
        double y = crystallizerTile.getBlockPos().getY();
        double z = crystallizerTile.getBlockPos().getZ();
        ms.pushPose();
        ms.translate(0.5, -0.5, 0.5);
      //  model.render(ms, buffer, light, overlay, 1, 1, 1, 1, 1);
        boolean draining = crystallizerTile.draining;
        int baseAge = draining ? 20 : 40;
        int randBound = draining ? 3 : 6;
        int numParticles = draining ? 2 : 1;
        float scaleAge = draining ?(float) ParticleUtil.inRange(0.1, 0.2) : (float) ParticleUtil.inRange(0.05, 0.15);
        if(world.random.nextInt( randBound)  == 0 && !Minecraft.getInstance().isPaused()){
            for(int i =0; i< numParticles; i++){
                Vec3 particlePos = new Vec3(pos.getX(), pos.getY(), pos.getZ()).add(0.5, 0.5, 0.5);
                particlePos = particlePos.add(ParticleUtil.pointInSphere());
                world.addParticle(ParticleLineData.createData(new ParticleColor(255,25,180) ,scaleAge, baseAge+world.random.nextInt(20)) ,
                        particlePos.x(), particlePos.y(), particlePos.z(),
                        pos.getX() + 0.5  , pos.getY() +0.5 , pos.getZ()+ 0.5);
            }
        }

        if(crystallizerTile.stack == null)
            return;

        if (crystallizerTile.entity == null || !ItemStack.matches(crystallizerTile.entity.getItem(), crystallizerTile.stack)) {
            crystallizerTile.entity = new ItemEntity(crystallizerTile.getLevel(), x ,y, z, crystallizerTile.stack);
        }
        crystallizerTile.entity.setPos(x,y+1,z);
        ItemEntity entityItem = crystallizerTile.entity;
        ms.popPose();
        ms.pushPose();
        ms.scale(0.5f, 0.5f, 0.5f);
        ms.translate(1D, 1f, 1D);
        Minecraft.getInstance().getItemRenderer().renderStatic(entityItem.getItem(), ItemTransforms.TransformType.FIXED, 15728880, overlay, ms, buffers, (int) crystallizerTile.getBlockPos().asLong());
        ms.popPose();
    }
}
