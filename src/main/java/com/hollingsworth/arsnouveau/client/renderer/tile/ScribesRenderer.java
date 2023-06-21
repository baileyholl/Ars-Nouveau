package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemBlockRenderer;
import com.hollingsworth.arsnouveau.common.block.ScribesBlock;
import com.hollingsworth.arsnouveau.common.block.ThreePartBlock;
import com.hollingsworth.arsnouveau.common.block.tile.ScribesTile;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import software.bernie.geckolib.model.GeoModel;

import javax.annotation.Nullable;
import java.util.List;

public class ScribesRenderer extends ArsGeoBlockRenderer<ScribesTile> {
    public static GeoModel model = new GenericModel<>("scribes_table");

    public ScribesRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
        super(rendererDispatcherIn, model);
    }

    @Override
    public void renderEarly(ScribesTile tile, PoseStack matrixStack, float ticks, MultiBufferSource iRenderTypeBuffer, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float partialTicks) {
        try {
            if (tile.getLevel().getBlockState(tile.getBlockPos()).getBlock() != BlockRegistry.SCRIBES_BLOCK)
                return;
            if (tile.getLevel().getBlockState(tile.getBlockPos()).getValue(ScribesBlock.PART) != ThreePartBlock.HEAD)
                return;
            if (tile.getStack() == null) {
                return;
            }
            double x = tile.getBlockPos().getX();
            double y = tile.getBlockPos().getY();
            double z = tile.getBlockPos().getZ();
            renderPressedItem(tile, tile.crafting ? tile.craftingTicks < 40 ? tile.recipe.output.getItem().getDefaultInstance() : ItemsRegistry.BLANK_GLYPH.get().getDefaultInstance() : tile.getStack(), matrixStack, iRenderTypeBuffer, packedLightIn, packedOverlayIn, ticks + partialTicks);
        } catch (Throwable t) {
            t.printStackTrace();
            // Mercy for HORRIBLE RENDER CHANGING MODS
        }
    }


    @Override
    public void render(ScribesTile tile, float partialTicks, PoseStack stack, MultiBufferSource bufferIn, int packedLightIn) {
        try {
            if (tile.getLevel().getBlockState(tile.getBlockPos()).getBlock() != BlockRegistry.SCRIBES_BLOCK)
                return;
            if (tile.getLevel().getBlockState(tile.getBlockPos()).getValue(ScribesBlock.PART) != ThreePartBlock.HEAD)
                return;
            Direction direction = tile.getLevel().getBlockState(tile.getBlockPos()).getValue(ScribesBlock.FACING);
            stack.pushPose();

            if (direction == Direction.NORTH) {
                stack.mulPose(Axis.YP.rotationDegrees(-90));
                stack.translate(1, 0, -1);
            }

            if (direction == Direction.SOUTH) {
                stack.mulPose(Axis.YP.rotationDegrees(270));
                stack.translate(-1, 0, -1);
            }

            if (direction == Direction.WEST) {
                stack.mulPose(Axis.YP.rotationDegrees(270));

                stack.translate(0, 0, -2);
            }

            if (direction == Direction.EAST) {
                stack.mulPose(Axis.YP.rotationDegrees(-90));
                stack.translate(0, 0, 0);

            }

            super.render(tile, partialTicks, stack, bufferIn, packedLightIn);
            stack.popPose();
        } catch (Throwable t) {
            t.printStackTrace();
            // why must people change the rendering order of tesrs
        }
    }

    public void renderPressedItem(ScribesTile tile, ItemStack itemToRender, PoseStack matrixStack, MultiBufferSource iRenderTypeBuffer, int packedLight, int packedOverlay, float partialTicks) {
        Direction direction = tile.getLevel().getBlockState(tile.getBlockPos()).getValue(ScribesBlock.FACING);

        matrixStack.pushPose();
        matrixStack.translate(0, 1.D, 0);
        BlockState state = tile.getLevel().getBlockState(tile.getBlockPos());
        if (!(state.getBlock() instanceof ScribesBlock))
            return;
        float y = state.getValue(ScribesBlock.FACING).getClockWise().toYRot();
        matrixStack.mulPose(Axis.YP.rotationDegrees(-y + 90f));
        matrixStack.mulPose(Axis.XP.rotationDegrees(90f));
        matrixStack.mulPose(Axis.ZP.rotationDegrees(180F));

        if (direction == Direction.WEST) {
            matrixStack.mulPose(Axis.ZP.rotationDegrees(90f));
        }
        if (direction == Direction.EAST) {
            matrixStack.mulPose(Axis.ZP.rotationDegrees(-90f));
        }
        if (direction == Direction.SOUTH) {
            matrixStack.mulPose(Axis.ZP.rotationDegrees(180));
        }
        matrixStack.translate(-0.7, 0, 0);
        matrixStack.scale(0.6f, 0.6f, 0.6f);

        Minecraft.getInstance().getItemRenderer().renderStatic(itemToRender, ItemDisplayContext.FIXED, packedLight, packedOverlay, matrixStack, iRenderTypeBuffer, (int) tile.getBlockPos().asLong());
        matrixStack.popPose();
        if (tile.recipe != null && !tile.crafting) {
            List<Ingredient> inputs = tile.getRemainingRequired();
            float ticks = (partialTicks + (float) ClientInfo.ticksInGame);
            float angleBetweenEach = 360.0f / inputs.size();
            // How far away from the center should they be.
            Vec3 distanceVec = new Vec3(1, -0.5, 1);
            for (int i = 0; i < inputs.size(); i++) {
                Ingredient ingredient = inputs.get(i);
                ItemStack stack = ingredient.getItems()[(ClientInfo.ticksInGame / 20) % ingredient.getItems().length];
                matrixStack.pushPose();
                matrixStack.translate(-0.5, 2.0, 0);
                matrixStack.scale(0.25f, 0.25f, 0.25f);
                // This spaces them out from each other
                matrixStack.mulPose(Axis.YP.rotationDegrees(ticks + (i * angleBetweenEach)));
                // Controls the distance from the center, also makes them float up and down
                matrixStack.translate(distanceVec.x(), distanceVec.y() + ((i % 2 == 0 ? -i : i) * Mth.sin(ticks / 60) * 0.0625), distanceVec.z());
                // This rotates the individual stacks, with every 2nd stack rotating a different direction
                matrixStack.mulPose((i % 2 == 0 ? Axis.ZP : Axis.XP).rotationDegrees(ticks));
                RenderSystem.setShaderColor(1, 1, 1, 0.5f);
                Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, packedLight, packedOverlay, matrixStack, iRenderTypeBuffer, (int) tile.getBlockPos().asLong());
                matrixStack.popPose();
            }
        }
    }

    public static GenericItemBlockRenderer getISTER() {
        return new GenericItemBlockRenderer(model);
    }

    @Override
    public RenderType getRenderType(ScribesTile animatable, float partialTicks, PoseStack stack, @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        return RenderType.entityTranslucent(textureLocation);
    }



}
