package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemBlockRenderer;
import com.hollingsworth.arsnouveau.common.block.tile.RedstoneRelayTile;
import com.hollingsworth.arsnouveau.common.items.AnimBlockItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.RedStoneWireBlock;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.util.Color;

import java.util.ArrayList;
import java.util.List;

public class RedstoneRelayRenderer extends ArsGeoBlockRenderer<RedstoneRelayTile>{

    public RedstoneRelayRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
        super(rendererDispatcherIn, new GenericModel<>("redstone_relay"));
    }

    @Override
    public void renderRecursively(PoseStack poseStack, RedstoneRelayTile animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        ArrayList<String> strings = new ArrayList<>(List.of(new String[]{
                "framework_input",
                "bone",
                "bone2",
                "bone3",
                "bone4"
        }));
        if (strings.contains(bone.getName())) {
            //NOTE: if the bone have a parent, the recursion will get here with the neutral color, making the color getter useless
            super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, Color.WHITE.getRed() / 255f, Color.WHITE.getGreen() / 255f, Color.WHITE.getBlue() / 255f, Color.WHITE.getAlpha() / 255f);
        } else {
            super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        }
    }

    @Override
    public void actuallyRender(PoseStack poseStack, RedstoneRelayTile animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public Color getRenderColor(RedstoneRelayTile animatable, float partialTick, int packedLight) {
        return new Color(0xFF000000 | RedStoneWireBlock.getColorForPower(Math.max(1, animatable.getOutputPower())));
    }

    public static GenericItemBlockRenderer getISTER() {
        return new GenericItemBlockRenderer(new GenericModel<>("redstone_relay")){

            @Override
            public void renderRecursively(PoseStack poseStack, AnimBlockItem animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
                ArrayList<String> strings = new ArrayList<>(List.of(new String[]{
                        "framework_input",
                        "bone",
                        "bone2",
                        "bone3",
                        "bone4"
                }));
                if (strings.contains(bone.getName())) {
                    //NOTE: if the bone have a parent, the recursion will get here with the neutral color, making the color getter useless
                    super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, Color.WHITE.getRed() / 255f, Color.WHITE.getGreen() / 255f, Color.WHITE.getBlue() / 255f, Color.WHITE.getAlpha() / 255f);
                } else {
                    super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
                }
            }

            @Override
            public Color getRenderColor(AnimBlockItem animatable, float partialTick, int packedLight) {
                return Color.ofOpaque(RedStoneWireBlock.getColorForPower(1));
            }
        };
    }
}
