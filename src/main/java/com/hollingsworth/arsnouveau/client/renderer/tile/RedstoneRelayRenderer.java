package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemBlockRenderer;
import com.hollingsworth.arsnouveau.common.block.tile.RedstoneRelayTile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.RedStoneWireBlock;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoBone;

import java.util.ArrayList;
import java.util.List;

public class RedstoneRelayRenderer extends ArsGeoBlockRenderer<RedstoneRelayTile>{
    public static GenericModel model = new GenericModel<>("redstone_relay");

    public RedstoneRelayRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
        super(rendererDispatcherIn, model);
    }

    @Override
    public void renderRecursively(GeoBone bone, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.renderRecursively(bone, poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        ArrayList<String> strings = new ArrayList<>(List.of(new String[]{
                "framework_input",
                "bone",
                "bone2",
                "bone3",
                "bone4"
        }));
        if (strings.contains(bone.getName())) {
            //NOTE: if the bone have a parent, the recursion will get here with the neutral color, making the color getter useless
            super.renderRecursively(bone, poseStack, buffer, packedLight, packedOverlay, Color.WHITE.getRed() / 255f, Color.WHITE.getGreen() / 255f, Color.WHITE.getBlue() / 255f, Color.WHITE.getAlpha() / 255f);
        } else {
            super.renderRecursively(bone, poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        }
    }

    @Override
    public Color getRenderColor(RedstoneRelayTile animatable, float partialTick, PoseStack poseStack, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, int packedLight) {
        return Color.ofOpaque(RedStoneWireBlock.getColorForPower(animatable.getOutputPower()));
    }

    public static GenericItemBlockRenderer getISTER() {
        return new GenericItemBlockRenderer(model);
    }
}
