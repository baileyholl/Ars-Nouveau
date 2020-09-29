package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.tile.ArcaneCoreTile;
import com.hollingsworth.arsnouveau.common.block.tile.ArcanePedestalTile;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class ArcaneCoreRenderer extends TileEntityRenderer<ArcaneCoreTile> {
    public static final ResourceLocation texture = new ResourceLocation(ArsNouveau.MODID + ":textures/entity/arcane_core.png");
    public static final ArcaneCoreModel model = new ArcaneCoreModel();

    public ArcaneCoreRenderer(TileEntityRendererDispatcher p_i226006_1_) {
        super(p_i226006_1_);
    }

    @Override
    public void render(ArcaneCoreTile tileEntityIn, float partialTicks, MatrixStack ms, IRenderTypeBuffer buffers, int light, int overlay) {
        ms.push();
        ms.translate(0.5, -0.5, 0.5);
        IVertexBuilder buffer = buffers.getBuffer(model.getRenderType(texture));
        model.render(ms, buffer, light, overlay, 1, 1, 1, 1);
        ms.pop();
        World world = tileEntityIn.getWorld();
        Random rand = world.rand;
        BlockPos pos = tileEntityIn.getPos();
        ParticleColor color = new ParticleColor(50 +rand.nextInt(175),50+ rand.nextInt(175), 50+rand.nextInt(175));
        ParticleColor randColor = new ParticleColor(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
        for(int i = 0; i < 2; i++) {
            world.addParticle(
                    GlowParticleData.createData(randColor),
                    pos.getX() + 0.5 + ParticleUtil.inRange(-0.3, 0.3), pos.getY() + 0.5 + ParticleUtil.inRange(-0.3, 0.3), pos.getZ() + 0.5 + ParticleUtil.inRange(-0.3, 0.3),
                    0, 0, 0);
        }
    }

    public static class ISRender extends ItemStackTileEntityRenderer {

        public ISRender(){ }

        @Override
        public void render(ItemStack p_228364_1_, MatrixStack ms, IRenderTypeBuffer buffers, int light, int overlay) {
            ms.push();
            ms.scale(0.75f, 0.75f, 0.75f);
            ms.translate(0.75, -0.40, 0.6);
            IVertexBuilder buffer = buffers.getBuffer(model.getRenderType(texture));
            model.render(ms, buffer, light, overlay, 1, 1, 1, 1);
            ms.pop();
        }
    }

}
