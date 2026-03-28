package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemBlockRenderer;
import com.hollingsworth.arsnouveau.common.block.ScribesBlock;
import com.hollingsworth.arsnouveau.common.block.ThreePartBlock;
import com.hollingsworth.arsnouveau.common.block.tile.ScribesTile;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.RenderPassInfo;

import java.util.List;

// GeckoLib 5.4.2 migration:
// - actuallyRender() REMOVED - direction-based rotation ported to adjustRenderPose(RenderPassInfo)
// - renderFinal() REMOVED - item rendering needs to be ported to preRenderPass or a separate hook
// - getRenderType signature changed: now (S renderState, Identifier texture) not (T, Identifier, MultiBufferSource, float)
// TODO: Port renderFinal item/recipe rendering to preRenderPass(RenderPassInfo, SubmitNodeCollector)
//   using collector.submitItem() or collector.submitModel().
public class ScribesRenderer extends ArsGeoBlockRenderer<ScribesTile> {
    public static GeoModel model = new GenericModel<>("scribes_table");

    public ScribesRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
        super(rendererDispatcherIn, model);
    }

    @Override
    public void adjustRenderPose(RenderPassInfo<ArsBlockEntityRenderState> renderPassInfo) {
        super.adjustRenderPose(renderPassInfo);
        BlockState state = renderPassInfo.renderState().blockState;
        if (state.getBlock() != BlockRegistry.SCRIBES_BLOCK.get()) return;
        if (state.getValue(ScribesBlock.PART) != ThreePartBlock.HEAD) return;
        Direction direction = state.getValue(ScribesBlock.FACING);
        PoseStack stack = renderPassInfo.poseStack();
        if (direction == Direction.NORTH) {
            stack.mulPose(Axis.YP.rotationDegrees(-90));
            stack.translate(1, 0, 0);
        } else if (direction == Direction.SOUTH) {
            stack.mulPose(Axis.YP.rotationDegrees(270));
            stack.translate(-1, 0, 0);
        } else if (direction == Direction.WEST) {
            stack.mulPose(Axis.YP.rotationDegrees(270));
            stack.translate(0, 0, -1);
        } else if (direction == Direction.EAST) {
            stack.mulPose(Axis.YP.rotationDegrees(-90));
            stack.translate(0, 0, 1);
        }
    }

    @Override
    public RenderType getRenderType(ArsBlockEntityRenderState renderState, Identifier texture) {
        return RenderTypes.entityCutoutNoCull(texture);
    }

    // TODO: Port renderFinal item + recipe rendering to preRenderPass or a per-bone hook.
    // ItemRenderer.renderStatic() was removed in 1.21.11; use ItemStackRenderState.submit() instead.
    // The tile entity (ScribesTile) needs to be captured in a custom BlockEntityRenderState subclass
    // during extractRenderState, then used in preRenderPass to submit item geometry via collector.submitItem().
    // Ingredient.getItems() was also removed in 1.21.11.
    public void renderPressedItem(ScribesTile tile, ItemStack itemToRender, PoseStack matrixStack, net.minecraft.client.renderer.MultiBufferSource iRenderTypeBuffer, int packedLight, int packedOverlay, float partialTicks) {
        // TODO: Re-implement using ItemStackRenderState + SubmitNodeCollector API introduced in 1.21.11
    }

    public static GenericItemBlockRenderer getISTER() {
        return new GenericItemBlockRenderer(model);
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }

    @Override
    public AABB getRenderBoundingBox(ScribesTile blockEntity) {
        return AABB.INFINITE;
    }
}
