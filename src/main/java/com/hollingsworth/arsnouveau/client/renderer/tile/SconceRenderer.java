package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.common.block.tile.SconceTile;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;

public class SconceRenderer extends TileEntityRenderer<SconceTile> {
    public SconceRenderer(TileEntityRendererDispatcher p_i226006_1_) {
        super(p_i226006_1_);
    }

    @Override
    public void render(SconceTile tile, float v, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int i, int i1) {
//        World world = tile.getLevel();
//        BlockPos pos = tile.getBlockPos();
//        Random rand = world.random;
//        System.out.println("hi");
//        double xzOffset = 0.15;
//        BlockState state = tile.getLevel().getBlockState(tile.getBlockPos());
//        if(!(state.getBlock() instanceof SconceBlock))
//            return;
//        float y = ((Direction)state.getValue(ScribesBlock.FACING)).getClockWise().toYRot();
//        if((Direction)state.getValue(ScribesBlock.FACING) == Direction.NORTH){
//            double centerX = pos.getX() + 0.5 + ParticleUtil.inRange(-xzOffset/4, xzOffset/4);
//            double centerZ = pos.getZ() + 0.8+ ParticleUtil.inRange(-xzOffset/4, xzOffset/4);
//            double centerY = pos.getY() + 1 + ParticleUtil.inRange(-0.05, 0.2);
//            ParticleColor color = new ParticleColor(tile.red, tile.green, tile.blue);
//            world.addParticle(
//                    GlowParticleData.createData(color),
//                    centerX  , pos.getY() + 0.8 + ParticleUtil.inRange(-0.00, 0.1) , centerZ,
//                    0, ParticleUtil.inRange(0.0, 0.03f),0);
//
//
////            world.addParticle(
////                    GlowParticleData.createData(color),
////                    centerX  , pos.getY() +0.75 + ParticleUtil.inRange(0, 0.3) , centerZ,
////                    0,ParticleUtil.inRange(0.0, 0.05f),0);;
//
//        }
    }
}