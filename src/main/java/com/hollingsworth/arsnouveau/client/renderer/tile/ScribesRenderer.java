package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemRenderer;
import com.hollingsworth.arsnouveau.common.block.ScribesBlock;
import com.hollingsworth.arsnouveau.common.block.tile.ScribesTile;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.phys.Vec2;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

import javax.annotation.Nullable;

public class ScribesRenderer extends GeoBlockRenderer<ScribesTile> {
    public static AnimatedGeoModel model = new GenericModel("scribes_table");

    public ScribesRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
        super(rendererDispatcherIn, model);
    }

    @Override
    public void renderEarly(ScribesTile tile, PoseStack matrixStack, float ticks, MultiBufferSource iRenderTypeBuffer, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float partialTicks) {
        try{
            if(tile.getLevel().getBlockState(tile.getBlockPos()).getBlock() != BlockRegistry.SCRIBES_BLOCK)
                return;
            if(tile.getLevel().getBlockState(tile.getBlockPos()).getValue(ScribesBlock.PART) != BedPart.HEAD)
                return;
            if (tile.stack == null) {
                return;
            }
            double x = tile.getBlockPos().getX();
            double y = tile.getBlockPos().getY();
            double z = tile.getBlockPos().getZ();
            if (tile.entity == null || !ItemStack.matches(tile.entity.getItem(), tile.stack)) {
                tile.entity = new ItemEntity(tile.getLevel(), x, y, z, tile.stack);
            }

            ItemEntity entityItem = tile.entity;
            renderPressedItem(tile, entityItem.getItem().getItem(), matrixStack, iRenderTypeBuffer, packedLightIn, packedOverlayIn, ticks + partialTicks);
        }catch (Throwable t){
            t.printStackTrace();
            // Mercy for HORRIBLE RENDER CHANGING MODS
        }
    }


    @Override
    public void render(ScribesTile tile, float partialTicks, PoseStack stack, MultiBufferSource bufferIn, int packedLightIn) {
        try{
            if(tile.getLevel().getBlockState(tile.getBlockPos()).getBlock() != BlockRegistry.SCRIBES_BLOCK)
                return;
            if(tile.getLevel().getBlockState(tile.getBlockPos()).getValue(ScribesBlock.PART) != BedPart.HEAD)
                return;
            Direction direction = tile.getLevel().getBlockState(tile.getBlockPos()).getValue(ScribesBlock.FACING);
            stack.pushPose();

            if(direction == Direction.NORTH){
                stack.mulPose(Vector3f.YP.rotationDegrees(-90));
                stack.translate(1, 0, -1);
            }

            if(direction == Direction.SOUTH){
                stack.mulPose(Vector3f.YP.rotationDegrees(90));
                stack.translate(-1, 0, 0);
            }

            if(direction == Direction.WEST){
                stack.mulPose(Vector3f.YP.rotationDegrees(90));
                stack.translate(-1, 0, 0);

            }

            if(direction == Direction.EAST){
                stack.mulPose(Vector3f.YP.rotationDegrees(-90));
                stack.translate(0, 0, 0);

            }

            super.render(tile, partialTicks, stack, bufferIn, packedLightIn);
            stack.popPose();
        }catch (Throwable t){
            t.printStackTrace();
            // why must people change the rendering order of tesrs
        }
    }
    public void renderPressedItem(ScribesTile tile, Item itemToRender, PoseStack matrixStack, MultiBufferSource iRenderTypeBuffer, int packedLight, int packedOverlay, float partialTicks){
        Direction direction = tile.getLevel().getBlockState(tile.getBlockPos()).getValue(ScribesBlock.FACING);

        matrixStack.pushPose();
        matrixStack.translate(0, 1.D, 0);
        BlockState state = tile.getLevel().getBlockState(tile.getBlockPos());
        if(!(state.getBlock() instanceof ScribesBlock))
            return;
        float y = state.getValue(ScribesBlock.FACING).getClockWise().toYRot();
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(-y + 90f));
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(90f));
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180F));

        if(direction == Direction.WEST){
            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(90f));
        }
        if(direction == Direction.EAST){
            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(-90f));
        }
        if(direction == Direction.SOUTH){
            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180));
        }
        matrixStack.translate(-0.8, 0, 0);
        matrixStack.scale(0.6f, 0.6f, 0.6f);

        Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(itemToRender), ItemTransforms.TransformType.FIXED, packedLight, packedOverlay, matrixStack, iRenderTypeBuffer, (int) tile.getBlockPos().asLong());
        matrixStack.popPose();
        if(tile.recipe != null){
            float ticks = (partialTicks + (float) ClientInfo.ticksInGame);
            float angleBetweenEach = 360.0f / tile.recipe.inputs.size();
            float currentDegree = (partialTicks + (float) ClientInfo.ticksInGame);
            Vec2 vec2 = new Vec2(1, 0);
            Vec2 center = new Vec2(1,2);
            int counter = 0;
            for(Ingredient i : tile.recipe.inputs){
                matrixStack.pushPose();
                matrixStack.translate(0, 2.5, 0);
                matrixStack.scale(0.6f, 0.6f, 0.6f);
                counter++;
                vec2 = rotatePointAbout(vec2, center, angleBetweenEach);
                matrixStack.translate(vec2.x, 0, vec2.y);
                matrixStack.mulPose(Vector3f.YP.rotationDegrees(90 + ticks));
                Minecraft.getInstance().getItemRenderer().renderStatic(i.getItems()[0], ItemTransforms.TransformType.FIXED, packedLight, packedOverlay, matrixStack, iRenderTypeBuffer, (int) tile.getBlockPos().asLong());
                currentDegree += angleBetweenEach;
                matrixStack.popPose();
            }
        }
    }

    private void renderIngredientAtAngle(PoseStack ms, float angle) {

        double x = 1;
        double y = 1;
        angle -= 90;
        int radius = 32;
        double xPos = x + Math.cos(angle * Math.PI / 180D) * radius + 32;
        double yPos = y + Math.sin(angle * Math.PI / 180D) * radius + 32;

        ms.translate(xPos - (int) xPos, 0, yPos - (int) yPos);

    }

    public static Vec2 rotatePointAbout(Vec2 in, Vec2 about, double degrees) {
        double rad = degrees * Math.PI / 180.0;
        double newX = Math.cos(rad) * (in.x - about.x) - Math.sin(rad) * (in.y - about.y) + about.x;
        double newY = Math.sin(rad) * (in.x - about.x) + Math.cos(rad) * (in.y - about.y) + about.y;
        return new Vec2((float) newX, (float) newY);
    }

    public static GenericItemRenderer getISTER(){
        return new GenericItemRenderer(model);
    }

    @Override
    public RenderType getRenderType(ScribesTile animatable, float partialTicks, PoseStack stack, @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        return RenderType.entityTranslucent(textureLocation);
    }

    @Override
    public boolean shouldRenderOffScreen(BlockEntity p_188185_1_) {
        return false;
    }


}
