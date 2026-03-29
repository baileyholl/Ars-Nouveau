package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemBlockRenderer;
import com.hollingsworth.arsnouveau.common.block.tile.ArcaneCoreTile;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.RenderPassInfo;

// TODO: rotateBlock no longer exists in GeckoLib 5 - block rotation needs reimplementation via adjustRenderPose.
public class ArcaneCoreRenderer extends ArsGeoBlockRenderer<ArcaneCoreTile> {
    private static final Logger LOGGER = LogManager.getLogger(ArcaneCoreRenderer.class);
    private static final DataTicket<Direction> FACING = DataTicket.create("ars_nouveau:arcane_core_facing", Direction.class);
    private static int dbgFrame = 0;

    public static GeoModel model = new GenericModel<>("arcane_core");

    public ArcaneCoreRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
        this(rendererDispatcherIn, model);
    }

    public ArcaneCoreRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn, GeoModel<ArcaneCoreTile> modelProvider) {
        super(rendererDispatcherIn, modelProvider);
    }

    @Override
    public void captureDefaultRenderState(ArcaneCoreTile animatable, Void relatedObject, ArsBlockEntityRenderState renderState, float partialTick) {
        super.captureDefaultRenderState(animatable, relatedObject, renderState, partialTick);
        if (animatable != null) {
            Direction dir = animatable.getBlockState().getValue(BlockStateProperties.FACING);
            renderState.addGeckolibData(FACING, dir);
            if (dbgFrame++ % 60 == 0) {
                LOGGER.info("ArcaneCore captureRenderState: pos={} facing={}", animatable.getBlockPos(), dir);
            }
        }
    }

    @Override
    public void adjustRenderPose(RenderPassInfo<ArsBlockEntityRenderState> renderPassInfo) {
        Direction dir = renderPassInfo.renderState().getOrDefaultGeckolibData(FACING, (Direction) null);
        if (dbgFrame % 60 == 1) {
            LOGGER.info("ArcaneCore adjustRenderPose: facing={}", dir);
        }
        // No rotation applied yet — observing default render to understand model orientation.
    }

    public static GenericItemBlockRenderer getISTER() {
        return new GenericItemBlockRenderer(model);
    }
}
