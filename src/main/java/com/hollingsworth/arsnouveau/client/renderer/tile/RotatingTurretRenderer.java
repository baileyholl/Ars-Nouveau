package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.common.block.tile.RotatingTurretTile;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

@SuppressWarnings("rawtypes")
public class RotatingTurretRenderer extends GeoBlockRenderer<RotatingTurretTile> {
    public static AnimatedGeoModel model = new GenericModel("basic_spell_turret") {
        @Override
        public void setCustomAnimations(Object animatable, int instanceId, AnimationEvent event) {
            if (animatable instanceof RotatingTurretTile tile) {
                IBone master = this.getAnimationProcessor().getBone("spell_turret");
                master.setRotationY((tile.getRotationX() + 90) * Mth.DEG_TO_RAD);
                master.setRotationX(tile.getRotationY() * Mth.DEG_TO_RAD);
            }
        }
    };

    public RotatingTurretRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
        super(rendererDispatcherIn, model);
    }

    //Disable geckolib automatic rotation based on blockstate
    @Override
    protected void rotateBlock(Direction facing, PoseStack poseStack) {
    }
}
