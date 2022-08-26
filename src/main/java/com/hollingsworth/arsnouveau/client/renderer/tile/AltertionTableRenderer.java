package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemBlockRenderer;
import com.hollingsworth.arsnouveau.common.block.AlterationTable;
import com.hollingsworth.arsnouveau.common.block.tile.AlterationTile;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.BedPart;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

import javax.annotation.Nullable;

public class AltertionTableRenderer extends GeoBlockRenderer<AlterationTile> {

    public AltertionTableRenderer(BlockEntityRendererProvider.Context p_i226006_1_) {
        super(p_i226006_1_, new GenericModel<>("alteration_table").withEmptyAnim());
    }

    @Override
    public void renderEarly(AlterationTile tile, PoseStack matrixStack, float ticks, MultiBufferSource iRenderTypeBuffer, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float partialTicks) {
        try {
            if (tile.getLevel().getBlockState(tile.getBlockPos()).getBlock() != BlockRegistry.ALTERATION_APPARATUS)
                return;
            if (tile.getLevel().getBlockState(tile.getBlockPos()).getValue(AlterationTable.PART) != BedPart.HEAD)
                return;
            if (tile.getStack() == null) {
                return;
            }
            double x = tile.getBlockPos().getX();
            double y = tile.getBlockPos().getY();
            double z = tile.getBlockPos().getZ();
//            renderPressedItem(tile, tile.crafting ? tile.craftingTicks < 40 ? tile.recipe.output.getItem() : ItemsRegistry.BLANK_GLYPH.get() : tile.getStack().getItem(), matrixStack, iRenderTypeBuffer, packedLightIn, packedOverlayIn, ticks + partialTicks);
        } catch (Throwable t) {
            t.printStackTrace();
            // Mercy for HORRIBLE RENDER CHANGING MODS
        }
    }


    @Override
    public void render(AlterationTile tile, float partialTicks, PoseStack stack, MultiBufferSource bufferIn, int packedLightIn) {
        try {
            if (tile.getLevel().getBlockState(tile.getBlockPos()).getBlock() != BlockRegistry.ALTERATION_APPARATUS)
                return;
            if (tile.getLevel().getBlockState(tile.getBlockPos()).getValue(AlterationTable.PART) != BedPart.HEAD)
                return;
            Direction direction = tile.getLevel().getBlockState(tile.getBlockPos()).getValue(AlterationTable.FACING);
            stack.pushPose();

            if (direction == Direction.NORTH) {
                stack.mulPose(Vector3f.YP.rotationDegrees(-90));
                stack.translate(1, 0, -1);
            }

            if (direction == Direction.SOUTH) {
                stack.mulPose(Vector3f.YP.rotationDegrees(270));
                stack.translate(-1, 0, -1);
            }

            if (direction == Direction.WEST) {
                stack.mulPose(Vector3f.YP.rotationDegrees(270));

                stack.translate(0, 0, -2);
            }

            if (direction == Direction.EAST) {
                stack.mulPose(Vector3f.YP.rotationDegrees(-90));
                stack.translate(0, 0, 0);

            }

            super.render(tile, partialTicks, stack, bufferIn, packedLightIn);
            stack.popPose();
        } catch (Throwable t) {
            t.printStackTrace();
            // why must people change the rendering order of tesrs
        }
    }

//    public void renderPressedItem(AlterationTile tile, Item itemToRender, PoseStack matrixStack, MultiBufferSource iRenderTypeBuffer, int packedLight, int packedOverlay, float partialTicks) {
//        Direction direction = tile.getLevel().getBlockState(tile.getBlockPos()).getValue(ScribesBlock.FACING);
//
//        matrixStack.pushPose();
//        matrixStack.translate(0, 1.D, 0);
//        BlockState state = tile.getLevel().getBlockState(tile.getBlockPos());
//        if (!(state.getBlock() instanceof ScribesBlock))
//            return;
//        float y = state.getValue(ScribesBlock.FACING).getClockWise().toYRot();
//        matrixStack.mulPose(Vector3f.YP.rotationDegrees(-y + 90f));
//        matrixStack.mulPose(Vector3f.XP.rotationDegrees(90f));
//        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180F));
//
//        if (direction == Direction.WEST) {
//            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(90f));
//        }
//        if (direction == Direction.EAST) {
//            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(-90f));
//        }
//        if (direction == Direction.SOUTH) {
//            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180));
//        }
//        matrixStack.translate(-0.7, 0, 0);
//        matrixStack.scale(0.6f, 0.6f, 0.6f);
//
//        Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(itemToRender), ItemTransforms.TransformType.FIXED, packedLight, packedOverlay, matrixStack, iRenderTypeBuffer, (int) tile.getBlockPos().asLong());
//        matrixStack.popPose();
//        if (tile.recipe != null && !tile.crafting) {
//            List<Ingredient> inputs = tile.getRemainingRequired();
//            float ticks = (partialTicks + (float) ClientInfo.ticksInGame);
//            float angleBetweenEach = 360.0f / inputs.size();
//            // How far away from the center should they be.
//            Vec3 distanceVec = new Vec3(1, -0.5, 1);
//            for (int i = 0; i < inputs.size(); i++) {
//                Ingredient ingredient = inputs.get(i);
//                ItemStack stack = ingredient.getItems()[(ClientInfo.ticksInGame / 20) % ingredient.getItems().length];
//                matrixStack.pushPose();
//                matrixStack.translate(-0.5, 2.0, 0);
//                matrixStack.scale(0.25f, 0.25f, 0.25f);
//                // This spaces them out from each other
//                matrixStack.mulPose(Vector3f.YP.rotationDegrees(ticks + (i * angleBetweenEach)));
//                // Controls the distance from the center, also makes them float up and down
//                matrixStack.translate(distanceVec.x(), distanceVec.y() + ((i % 2 == 0 ? -i : i) * Mth.sin(ticks / 60) * 0.0625), distanceVec.z());
//                // This rotates the individual stacks, with every 2nd stack rotating a different direction
//                matrixStack.mulPose((i % 2 == 0 ? Vector3f.ZP : Vector3f.XP).rotationDegrees(ticks));
//                RenderSystem.setShaderColor(1, 1, 1, 0.5f);
//                Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.FIXED, packedLight, packedOverlay, matrixStack, iRenderTypeBuffer, (int) tile.getBlockPos().asLong());
//                matrixStack.popPose();
//            }
//        }
//    }

    public static GenericItemBlockRenderer getISTER() {
        return new GenericItemBlockRenderer(new GenericModel<>("alteration_table").withEmptyAnim());
    }

    @Override
    public RenderType getRenderType(AlterationTile animatable, float partialTicks, PoseStack stack, @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        return RenderType.entityTranslucent(textureLocation);
    }

    @Override
    public boolean shouldRenderOffScreen(BlockEntity p_188185_1_) {
        return false;
    }
}