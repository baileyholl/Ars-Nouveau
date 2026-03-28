package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemBlockRenderer;
import com.hollingsworth.arsnouveau.common.block.tile.ScryersOculusTile;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.state.AnimationTest;
import software.bernie.geckolib.cache.model.GeoBone;


public class ScryerOculusRenderer extends ArsGeoBlockRenderer<ScryersOculusTile> {

    public ScryerOculusRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn, ScryersEyeModel model) {
        super(rendererDispatcherIn, model);
    }

    // GeckoLib 5: actuallyRender/renderRecursively removed. Bone animation now goes in GeoModel.setCustomAnimations.
    // The eye bone rotation was previously done per-render using tile data.
    // TODO: Port bone animation to ScryersEyeModel.setCustomAnimations using DataTickets to pass tile state.
    // For now, bone animation is not applied - the eye will be static.

    public static GenericItemBlockRenderer getISTER() {
        return new GenericItemBlockRenderer(new ScryersEyeModel());
    }
}
