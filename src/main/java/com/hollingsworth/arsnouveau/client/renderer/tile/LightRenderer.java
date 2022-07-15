package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.tile.LightTile;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

public class LightRenderer implements BlockEntityRenderer<LightTile> {

    public LightRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(LightTile lightTile, float v, PoseStack matrixStack, MultiBufferSource iRenderTypeBuffer, int i, int i1) {
        Level world = lightTile.getLevel();
        BlockPos pos = lightTile.getBlockPos();
        RandomSource rand = world.random;
        if (Minecraft.getInstance().isPaused())
            return;

        world.addParticle(
                GlowParticleData.createData(lightTile.color.nextColor(rand), 0.25f, 0.9f, 36),
                pos.getX() + 0.5 + ParticleUtil.inRange(-0.1, 0.1), pos.getY() + 0.5 + ParticleUtil.inRange(-0.1, 0.1), pos.getZ() + 0.5 + ParticleUtil.inRange(-0.1, 0.1),
                0, 0, 0);

    }
}
