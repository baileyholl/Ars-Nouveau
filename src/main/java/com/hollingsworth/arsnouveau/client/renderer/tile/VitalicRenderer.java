package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemRenderer;
import com.hollingsworth.arsnouveau.common.block.tile.VitalicSourcelinkTile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

import java.util.Random;

public class VitalicRenderer extends GeoBlockRenderer<VitalicSourcelinkTile> {
    public static SourcelinkModel model =  new SourcelinkModel("vitalic");

    public VitalicRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
        super(rendererDispatcherIn, model);
    }


    @Override
    public void renderLate(VitalicSourcelinkTile animatable, PoseStack stackIn, float ticks, MultiBufferSource renderTypeBuffer, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float partialTicks) {
        Level world = animatable.getLevel();
        BlockPos pos = animatable.getBlockPos();
        Random rand = world.random;
        if(Minecraft.getInstance().isPaused())
            return;
        for(int i = 0; i < 1; i++){
            world.addParticle(
                    GlowParticleData.createData(new ParticleColor(
                            rand.nextInt(255),
                            rand.nextInt(50),
                            rand.nextInt(255)
                    )),
                    pos.getX() +0.5  , pos.getY() +0.3  + ParticleUtil.inRange(-0.1, 0.35) , pos.getZ() +0.5 ,
                    0,0,0);
        }
        int time = (int) (ClientInfo.ticksInGame + ticks);

//        for(int i =0; i < 1; i++){
//            world.addParticle(ParticleSparkleData.createData(new ParticleColor(255,52,36), 0.05f, 60),
//                    pos.getX()  +Math.cos(time)/2 +0.5 , pos.getY() +1.0 , pos.getZ() + Math.sin(time)/2 +0.5,
//                    ParticleUtil.inRange(-0.01, 0.01),  ParticleUtil.inRange(-0.01, 0.01), ParticleUtil.inRange(-0.01, 0.01));
//        }
    }
    public static GenericItemRenderer getISTER(){
        return new GenericItemRenderer(model);
    }
}
