package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.tile.ArcaneCoreTile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Random;

public class ArcaneCoreRenderer extends BlockEntityRenderer<ArcaneCoreTile> {
    public static final ResourceLocation texture = new ResourceLocation(ArsNouveau.MODID + ":textures/entity/arcane_core.png");
    public static final ArcaneCoreModel model = new ArcaneCoreModel();

    public ArcaneCoreRenderer(BlockEntityRenderDispatcher p_i226006_1_) {
        super(p_i226006_1_);
    }

    @Override
    public void render(ArcaneCoreTile tileEntityIn, float partialTicks, PoseStack ms, MultiBufferSource buffers, int light, int overlay) {
        ms.pushPose();
        ms.translate(0.5, -0.5, 0.5);
        VertexConsumer buffer = buffers.getBuffer(model.renderType(texture));
        model.renderToBuffer(ms, buffer, light, overlay, 1, 1, 1, 1);
        ms.popPose();
        Level world = tileEntityIn.getLevel();
        Random rand = world.random;
        BlockPos pos = tileEntityIn.getBlockPos();
        ParticleColor color = new ParticleColor(50 +rand.nextInt(175),50+ rand.nextInt(175), 50+rand.nextInt(175));
        ParticleColor randColor = new ParticleColor(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
        if(Minecraft.getInstance().isPaused())
            return;

        for(int i = 0; i < 2; i++) {
            world.addParticle(
                    GlowParticleData.createData(randColor),
                    pos.getX() + 0.5 + ParticleUtil.inRange(-0.3, 0.3), pos.getY() + 0.5 + ParticleUtil.inRange(-0.3, 0.3), pos.getZ() + 0.5 + ParticleUtil.inRange(-0.3, 0.3),
                    0, 0, 0);
        }
    }

    public static class ISRender extends BlockEntityWithoutLevelRenderer {

        public ISRender(){ }

        @Override
        public void renderByItem(ItemStack stack, ItemTransforms.TransformType p_239207_2_, PoseStack ms, MultiBufferSource buffers, int light, int overlay) {
            ms.pushPose();
            ms.scale(0.75f, 0.75f, 0.75f);
            ms.translate(0.75, -0.40, 0.6);
            VertexConsumer buffer = buffers.getBuffer(model.renderType(texture));
            model.renderToBuffer(ms, buffer, light, overlay, 1, 1, 1, 1);
            ms.popPose();
        }
    }

}
