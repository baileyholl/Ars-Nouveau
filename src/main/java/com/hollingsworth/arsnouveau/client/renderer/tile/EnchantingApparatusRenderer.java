package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleLineData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.tile.EnchantingApparatusTile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import java.util.Random;

public class EnchantingApparatusRenderer extends BlockEntityRenderer<EnchantingApparatusTile> {
    public static final EnchantingApparatusModel model = new EnchantingApparatusModel();
    public static final ResourceLocation texture = new ResourceLocation(ArsNouveau.MODID + ":textures/blocks/enchanting_apparatus.png");

    public EnchantingApparatusRenderer(BlockEntityRenderDispatcher p_i226006_1_) {
        super(p_i226006_1_);
    }

    @Override
    public void render(EnchantingApparatusTile tileEntityIn, float v, PoseStack matrixStack, MultiBufferSource iRenderTypeBuffer, int lightIn, int overlayIn) {
        double x = tileEntityIn.getBlockPos().getX();
        double y = tileEntityIn.getBlockPos().getY();
        double z = tileEntityIn.getBlockPos().getZ();
        if(tileEntityIn.catalystItem == null)
            return;

        if (tileEntityIn.entity == null || !ItemStack.matches(tileEntityIn.entity.getItem(), tileEntityIn.catalystItem)) {
            tileEntityIn.entity = new ItemEntity(tileEntityIn.getLevel(), x, y, z, tileEntityIn.catalystItem);
        }
        matrixStack.pushPose();
        VertexConsumer buffer = iRenderTypeBuffer.getBuffer(model.renderType(texture));
        double sinOffset = Math.pow(Math.cos((ClientInfo.ticksInGame + v)  /10)/4, 2);
        matrixStack.translate(0.5D,  0.5 + sinOffset, 0.5D);
        float angle = ((ClientInfo.ticksInGame + v)/5.0f) % 360;
        if(tileEntityIn.isCrafting){
            Level world = tileEntityIn.getLevel();
            BlockPos pos  = tileEntityIn.getBlockPos().offset(0, 0.5, 0);
            Random rand = world.getRandom();
            for(int i =0; i< 5; i++){
                Vec3 particlePos = new Vec3(pos.getX(), pos.getY(), pos.getZ()).add(0.5, 0, 0.5);
                particlePos = particlePos.add(ParticleUtil.pointInSphere());
                world.addParticle(ParticleLineData.createData(new ParticleColor(rand.nextInt(255),rand.nextInt(255),rand.nextInt(255))),
                        particlePos.x(), particlePos.y(), particlePos.z(),
                        pos.getX()  +0.5, pos.getY() + 1  , pos.getZ() +0.5);
            }

            model.frame_all.xRot = angle;
            model.frame_bot.yRot = angle;
            model.frame_top.yRot = -angle;

            tileEntityIn.pedestalList().forEach((b) ->{
                BlockPos pos2 = b.immutable();
                for(int i = 0; i < 1; i++){
                    tileEntityIn.getLevel().addParticle(
                            GlowParticleData.createData(new ParticleColor(rand.nextInt(255),rand.nextInt(255),rand.nextInt(255))),
                            pos2.getX() +0.5 + ParticleUtil.inRange(-0.2, 0.2)  , pos2.getY() +1.5  + ParticleUtil.inRange(-0.3, 0.3) , pos2.getZ() +0.5 + ParticleUtil.inRange(-0.2, 0.2),
                            0,0,0);
                }
            });
        }else{
            model.frame_all.xRot = 0;
            model.frame_bot.yRot = 0;
            model.frame_top.yRot = 0;
        }
        model.renderToBuffer(matrixStack, buffer, lightIn, overlayIn, 1, 1, 1, 1);
        matrixStack.popPose();


        ItemEntity entityItem = tileEntityIn.entity;
        matrixStack.pushPose();
        matrixStack.translate(0.5D, 0.55f +sinOffset, 0.5D);
        matrixStack.scale(0.35f, 0.35f, 0.35F);
        Minecraft.getInstance().getItemRenderer().renderStatic(entityItem.getItem(), ItemTransforms.TransformType.FIXED, 15728880, overlayIn, matrixStack, iRenderTypeBuffer);
        matrixStack.popPose();

    }

    public static class ISRender extends BlockEntityWithoutLevelRenderer {

        public ISRender(){ }


        @Override
        public void renderByItem(ItemStack p_228364_1_,ItemTransforms.TransformType p_239207_2_,PoseStack ms, MultiBufferSource buffers, int light, int overlay) {
            ms.pushPose();
            ms.translate(0.75, 0.25, 0.2);
            VertexConsumer buffer = buffers.getBuffer(model.renderType(texture));
            model.renderToBuffer(ms, buffer, light, overlay, 1, 1, 1, 1);
            ms.popPose();
        }
    }
}
