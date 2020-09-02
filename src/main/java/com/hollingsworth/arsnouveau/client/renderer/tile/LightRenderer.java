package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.tile.LightTile;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class LightRenderer extends TileEntityRenderer<LightTile> {
    public LightRenderer(TileEntityRendererDispatcher p_i226006_1_) {
        super(p_i226006_1_);
    }

    @Override
    public void render(LightTile lightTile, float v, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int i, int i1) {
        World world = lightTile.getWorld();
        BlockPos pos = lightTile.getPos();
//        if(world.getGameTime()%20 == 0 && world.rand.nextInt( 6) == 0){
//            world.addParticle(
//                    ParticleGlow.createData(new ParticleColor(255, 0,0)),
//                    pos.getX() +0.5, pos.getY() +0.5, pos.getZ() +0.5,
//                    ParticleUtil.inRange(-0.1, 0.1),  ParticleUtil.inRange(-0.1, 0.1),   ParticleUtil.inRange(-0.1, 0.1));
//        }
        float bounce = (Minecraft.getInstance().world.getGameTime()/5.0f) % 360;
        bounce = (float) (Math.cos(bounce) + Math.sin(bounce));
        Random rand = world.rand;
        if(world.getGameTime() % 1 == 0){
//            world.addParticle(
//                    ParticleGlow.createData(new ParticleColor(238,238,  88)),
//                    pos.getX() +0.5 +ParticleUtil.inRange(-0.02, 0.2) , pos.getY() +0.5 +ParticleUtil.inRange(-0.05, 0.05), pos.getZ() +0.5 +ParticleUtil.inRange(-0.2, 0.2),
//                    0,0,0);
            world.addParticle(
                    GlowParticleData.createData(new ParticleColor(rand.nextInt(255),rand.nextInt(255),rand.nextInt(255))),
                    pos.getX() +0.5 + ParticleUtil.inRange(-0.05, 0.05)  , pos.getY() +0.5  + ParticleUtil.inRange(-0.05, 0.05) , pos.getZ() +0.5 + ParticleUtil.inRange(-0.05, 0.05),
                    0,0,0);
        }


//        if(world.getGameTime() % 10 ==0){
//            world.addParticle(
//                    ParticleGlow.createData(new ParticleColor(238,238,30)),
//                    pos.getX() +0.5 + ParticleUtil.inRange(-0.2, 0.2)  , pos.getY() +0.5 + ParticleUtil.inRange(-0.2, 0.2), pos.getZ() +0.5 + ParticleUtil.inRange(-0.2, 0.2),
//                    0,0,0);
//        }
    }
}
