package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleLineData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.tile.EnchantingApparatusTile;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.Random;

public class EnchantingApparatusRenderer extends TileEntityRenderer<EnchantingApparatusTile> {
    public static final EnchantingApparatusModel model = new EnchantingApparatusModel();
    public static final ResourceLocation texture = new ResourceLocation(ArsNouveau.MODID + ":textures/blocks/enchanting_apparatus.png");

    public EnchantingApparatusRenderer(TileEntityRendererDispatcher p_i226006_1_) {
        super(p_i226006_1_);
    }

    @Override
    public void render(EnchantingApparatusTile tileEntityIn, float v, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int lightIn, int overlayIn) {
        double x = tileEntityIn.getPos().getX();
        double y = tileEntityIn.getPos().getY();
        double z = tileEntityIn.getPos().getZ();
        if(tileEntityIn.catalystItem == null)
            return;

        if (tileEntityIn.entity == null || !ItemStack.areItemStacksEqual(tileEntityIn.entity.getItem(), tileEntityIn.catalystItem)) {
            tileEntityIn.entity = new ItemEntity(tileEntityIn.getWorld(), x, y, z, tileEntityIn.catalystItem);
        }
        matrixStack.push();
        IVertexBuilder buffer = iRenderTypeBuffer.getBuffer(model.getRenderType(texture));
        double sinOffset = Math.pow(Math.cos((ClientInfo.ticksInGame + v)  /10)/4, 2);
        matrixStack.translate(0.5D,  0.5 + sinOffset, 0.5D);
        float angle = ((ClientInfo.ticksInGame + v)/5.0f) % 360;
        if(tileEntityIn.isCrafting){
            World world = tileEntityIn.getWorld();
            BlockPos pos  = tileEntityIn.getPos().add(0, 0.5, 0);
            Random rand = world.getRandom();
            for(int i =0; i< 5; i++){
                Vector3d particlePos = new Vector3d(pos.getX(), pos.getY(), pos.getZ()).add(0.5, 0, 0.5);
                particlePos = particlePos.add(ParticleUtil.pointInSphere());
                world.addParticle(ParticleLineData.createData(new ParticleColor(rand.nextInt(255),rand.nextInt(255),rand.nextInt(255))),
                        particlePos.getX(), particlePos.getY(), particlePos.getZ(),
                        pos.getX()  +0.5, pos.getY() + 1  , pos.getZ() +0.5);
            }

            model.frame_all.rotateAngleX = angle;
            model.frame_bot.rotateAngleY = angle;
            model.frame_top.rotateAngleY = -angle;

            tileEntityIn.pedestalList().forEach((b) ->{
                BlockPos pos2 = b.toImmutable();
                for(int i = 0; i < 1; i++){
                    tileEntityIn.getWorld().addParticle(
                            GlowParticleData.createData(new ParticleColor(rand.nextInt(255),rand.nextInt(255),rand.nextInt(255))),
                            pos2.getX() +0.5 + ParticleUtil.inRange(-0.2, 0.2)  , pos2.getY() +1.5  + ParticleUtil.inRange(-0.3, 0.3) , pos2.getZ() +0.5 + ParticleUtil.inRange(-0.2, 0.2),
                            0,0,0);
                }
            });
        }else{
            model.frame_all.rotateAngleX = 0;
            model.frame_bot.rotateAngleY = 0;
            model.frame_top.rotateAngleY = 0;
        }
        model.render(matrixStack, buffer, lightIn, overlayIn, 1, 1, 1, 1);
        matrixStack.pop();


        ItemEntity entityItem = tileEntityIn.entity;
        matrixStack.push();
        matrixStack.translate(0.5D, 0.55f +sinOffset, 0.5D);
        matrixStack.scale(0.35f, 0.35f, 0.35F);
        Minecraft.getInstance().getItemRenderer().renderItem(entityItem.getItem(), ItemCameraTransforms.TransformType.FIXED, 15728880, overlayIn, matrixStack, iRenderTypeBuffer);
        matrixStack.pop();

    }

    public static class ISRender extends ItemStackTileEntityRenderer {

        public ISRender(){ }


        @Override
        public void func_239207_a_(ItemStack p_228364_1_,ItemCameraTransforms.TransformType p_239207_2_,MatrixStack ms, IRenderTypeBuffer buffers, int light, int overlay) {
            ms.push();
            ms.translate(0.75, 0.25, 0.2);
            IVertexBuilder buffer = buffers.getBuffer(model.getRenderType(texture));
            model.render(ms, buffer, light, overlay, 1, 1, 1, 1);
            ms.pop();
        }
    }
}
