package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.common.block.SpellTurret;
import com.hollingsworth.arsnouveau.common.block.tile.SpellTurretTile;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class SpellTurretRenderer extends TileEntityRenderer<SpellTurretTile> {
    public static final ResourceLocation texture = new ResourceLocation(ArsNouveau.MODID + ":textures/blocks/spell_turret.png");
    public final SpellTurretModel model = new SpellTurretModel();

    public SpellTurretRenderer(TileEntityRendererDispatcher manager) {
        super(manager);
    }

    @Override
    public void render(SpellTurretTile tileEntityIn, float partialTicks, MatrixStack ms, IRenderTypeBuffer buffers, int light, int overlay) {
        ms.pushPose();
        Direction direction = tileEntityIn.getBlockState().getValue(SpellTurret.FACING);
        if(direction == Direction.UP){
            ms.translate(0.5F, 0.5F, -0.5F);
            ms.mulPose(Vector3f.XP.rotationDegrees(90));
        }else if(direction == Direction.DOWN){
            ms.translate(0.5F, 0.5F, 1.5F);
            ms.mulPose(Vector3f.XP.rotationDegrees(-90));
        }else if(direction == Direction.SOUTH ){
            ms.translate(0.5F, -0.5F, 0.5F);
            ms.mulPose(Vector3f.YP.rotationDegrees(180));
        }else if(direction == Direction.NORTH){
            ms.translate(0.5F, -0.5F, 0.5F);
            ms.mulPose(Vector3f.YP.rotationDegrees(0));
        }
        else{
            ms.translate(0.5F, -0.5F, 0.5F);
            ms.mulPose(Vector3f.YP.rotationDegrees(direction.toYRot()));
        }
        double sinOffset = Math.pow(Math.cos((ClientInfo.ticksInGame + partialTicks)  /10)/4, 2);
        if(direction == Direction.UP || direction == Direction.DOWN){
            ms.translate(  0, 0, sinOffset);
        }else{
            ms.translate(  0, sinOffset, 0);
        }
        IVertexBuilder buffer = buffers.getBuffer(model.renderType(texture));
        model.back.zRot = ((ClientInfo.ticksInGame +partialTicks) /10.0f) % 360;
        model.renderToBuffer(ms, buffer, light, overlay, 1, 1, 1, 1);
        ms.popPose();
    }

    public static class ISRender extends ItemStackTileEntityRenderer {
        public final SpellTurretModel model = new SpellTurretModel();

        @Override
        public void renderByItem(ItemStack itemStack, ItemCameraTransforms.TransformType p_239207_2_, MatrixStack ms, IRenderTypeBuffer buffers, int light, int overlay) {
            ms.pushPose();
            ms.translate(0.75, -0.78, 0.2);
            ms.mulPose(Vector3f.YP.rotation(90));

            IVertexBuilder buffer = buffers.getBuffer(model.renderType(texture));
            float outerAngle = ((ClientInfo.ticksInGame) /10.0f) % 360;

            model.back.zRot = outerAngle;
            model.renderToBuffer(ms, buffer, light, overlay, 1, 1, 1, 1);
            ms.popPose();
        }
    }

}
