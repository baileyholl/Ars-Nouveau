package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.common.block.tile.EnchantingApparatusTile;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.world.gen.PerlinNoiseGenerator;
import org.apache.logging.log4j.core.appender.rolling.action.IfAll;

import java.util.stream.IntStream;

public class EnchantingApparatusRenderer extends TileEntityRenderer<EnchantingApparatusTile> {
    public final EnchantingApparatusModel model;
    public final ResourceLocation texture = new ResourceLocation(ArsNouveau.MODID + ":textures/entity/enchanting_apparatus.png");

    public EnchantingApparatusRenderer(TileEntityRendererDispatcher p_i226006_1_) {
        super(p_i226006_1_);
        model = new EnchantingApparatusModel();
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
        int levels = 2;
//        PerlinNoiseGenerator noiseGenerator = new PerlinNoiseGenerator(new SharedSeedRandom("NOISE_GRASS".hashCode()), 0, 1);
//        System.out.println(noiseGenerator.noiseAt(tileEntityIn.getPos().getX() / 8, tileEntityIn.getPos().getY()/8 + ClientInfo.ticksInGame, false));
//        double offset = noiseGenerator.noiseAt(tileEntityIn.getPos().getX(), tileEntityIn.getPos().getY() + (ClientInfo.ticksInGame + v)/40 , false) / 10;
        double sinOffset = Math.pow(Math.cos((ClientInfo.ticksInGame + v)  /10)/4, 2);
        matrixStack.translate(0.5D, -0.5f +sinOffset, 0.5D);
        if(tileEntityIn.isCrafting){
            float angle = ((ClientInfo.ticksInGame + v)/10.0f) % 360;
            model.frame_all.rotateAngleY = angle;
            model.frame_bot.rotateAngleX = angle;
            model.frame_top.rotateAngleZ = angle;
        }
        model.render(matrixStack, buffer, lightIn, overlayIn, 1, 1, 1, 1);
        matrixStack.pop();


        ItemEntity entityItem = tileEntityIn.entity;
        matrixStack.push();
        RenderSystem.enableLighting();
        matrixStack.translate(0.5D, 0.5f +sinOffset, 0.5D);
        matrixStack.scale(0.35f, 0.35f, 0.35F);

        Minecraft.getInstance().getItemRenderer().renderItem(entityItem.getItem(), ItemCameraTransforms.TransformType.FIXED, 15728880, overlayIn, matrixStack, iRenderTypeBuffer);
        matrixStack.pop();

    }
}
