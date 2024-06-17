package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemBlockRenderer;
import com.hollingsworth.arsnouveau.common.block.BasicSpellTurret;
import com.hollingsworth.arsnouveau.common.block.tile.BasicSpellTurretTile;
import com.hollingsworth.arsnouveau.common.block.tile.RotatingTurretTile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

import java.util.Optional;

public class BasicTurretRenderer extends ArsGeoBlockRenderer<BasicSpellTurretTile> {
    public static GeoModel model = new GenericModel("basic_spell_turret") {
        @Override
        public void setCustomAnimations(GeoAnimatable animatable, long instanceId, AnimationState animationState) {
            super.setCustomAnimations(animatable, instanceId, animationState);
            Optional<GeoBone> master = this.getBone("spell_turret");
            master.get().setRotX(0);
            master.get().setRotY(0);
            if (animatable instanceof RotatingTurretTile tile) {
                master.get().setRotY((tile.getRotationX() + 90) * Mth.DEG_TO_RAD);
                master.get().setRotX(tile.getRotationY() * Mth.DEG_TO_RAD);
            }
        }
    };
    public BasicTurretRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
        this(rendererDispatcherIn, model);
    }

    public BasicTurretRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn, GeoModel<BasicSpellTurretTile> modelProvider) {
        super(rendererDispatcherIn, modelProvider);
    }

    @Override
    public void actuallyRender(PoseStack poseStack, BasicSpellTurretTile animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        poseStack.pushPose();
        Direction direction = animatable.getBlockState().getValue(BasicSpellTurret.FACING);
        if (direction == Direction.UP) {
            poseStack.translate(0, 0.5, -0.5);
        } else if (direction == Direction.DOWN) {
            poseStack.translate(0, 0.5, 0.5);
        }
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        poseStack.popPose();
    }

    public static GenericItemBlockRenderer getISTER() {
        return new GenericItemBlockRenderer(model);
    }
}
