package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.common.block.tile.EnchantingApparatusTile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;
import software.bernie.geckolib3.util.RenderUtils;

public class EnchantingApparatusRenderer extends GeoBlockRenderer<EnchantingApparatusTile> {

    public EnchantingApparatusRenderer(BlockEntityRendererProvider.Context p_i226006_1_) {
        super(p_i226006_1_, new GenericModel<>("enchanting_apparatus"));
    }
    MultiBufferSource buffer;
    EnchantingApparatusTile tile;
    ResourceLocation text;
    @Override
    public void renderRecursively(GeoBone bone, PoseStack stack, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if(bone.getName().equals("frame_all") && tile.catalystItem != null){

            double x = tile.getBlockPos().getX();
            double y = tile.getBlockPos().getY();
            double z = tile.getBlockPos().getZ();
            if (tile.entity == null || !ItemStack.matches(tile.entity.getItem(), tile.catalystItem)) {
                tile.entity = new ItemEntity(tile.getLevel(), x, y, z, tile.catalystItem);
            }
            stack.pushPose();
            RenderUtils.translate(bone, stack);
          //  RenderUtils.rotate(bone, stack);
           // RenderUtils.moveToPivot(bone,stack);
            stack.translate(0, +0.4, 0);
            stack.scale(0.75f, 0.75f, 0.75f);
            ItemStack itemstack = tile.entity.getItem();
            Minecraft.getInstance().getItemRenderer().renderStatic(itemstack, ItemTransforms.TransformType.GROUND, packedLightIn, OverlayTexture.NO_OVERLAY, stack, this.buffer, (int) tile.getBlockPos().asLong());
            stack.popPose();
            bufferIn = buffer.getBuffer(RenderType.entityCutoutNoCull(text));
        }
        super.renderRecursively(bone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }

    @Override
    public void renderEarly(EnchantingApparatusTile animatable, PoseStack stackIn, float ticks, MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float partialTicks) {
        this.tile = animatable;
        this.buffer = renderTypeBuffer;
        this.text = this.getTextureLocation(animatable);
        super.renderEarly(animatable, stackIn, ticks, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, partialTicks);
    }

    @Override
    public void render(BlockEntity tile, float v, PoseStack matrixStack, MultiBufferSource iRenderTypeBuffer, int lightIn, int overlayIn) {
        super.render(tile, v, matrixStack, iRenderTypeBuffer, lightIn, overlayIn);
        EnchantingApparatusTile tileEntityIn = (EnchantingApparatusTile) tile;
        this.tile = tileEntityIn;


//        if(tileEntityIn.isCrafting){
//            Level world = tileEntityIn.getLevel();
//            BlockPos pos  = tileEntityIn.getBlockPos().offset(0, 0.5, 0);
//            Random rand = world.getRandom();
//            for(int i =0; i< 1; i++){
//                Vec3 particlePos = new Vec3(pos.getX(), pos.getY(), pos.getZ()).add(0.5, 0, 0.5);
//                particlePos = particlePos.add(ParticleUtil.pointInSphere());
//                world.addParticle(ParticleLineData.createData(new ParticleColor(rand.nextInt(255),rand.nextInt(255),rand.nextInt(255))),
//                        particlePos.x(), particlePos.y(), particlePos.z(),
//                        pos.getX()  +0.5, pos.getY() + 1  , pos.getZ() +0.5);
//            }
//
//
//            tileEntityIn.pedestalList().forEach((b) ->{
//                BlockPos pos2 = b.immutable();
//                for(int i = 0; i < 1; i++){
//                    tileEntityIn.getLevel().addParticle(
//                            GlowParticleData.createData(new ParticleColor(rand.nextInt(255),rand.nextInt(255),rand.nextInt(255))),
//                            pos2.getX() +0.5 + ParticleUtil.inRange(-0.2, 0.2)  , pos2.getY() +1.5  + ParticleUtil.inRange(-0.3, 0.3) , pos2.getZ() +0.5 + ParticleUtil.inRange(-0.2, 0.2),
//                            0,0,0);
//                }
//            });
//        }


    }
}
