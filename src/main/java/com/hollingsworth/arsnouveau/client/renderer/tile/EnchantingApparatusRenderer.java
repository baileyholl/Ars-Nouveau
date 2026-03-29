package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.common.block.tile.EnchantingApparatusTile;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.RenderPassInfo;

public class EnchantingApparatusRenderer extends ArsGeoBlockRenderer<EnchantingApparatusTile> {

    private static final Logger LOGGER = LogManager.getLogger(EnchantingApparatusRenderer.class);
    private static final DataTicket<Direction> FACING = DataTicket.create("ars_nouveau:apparatus_facing", Direction.class);
    private static int dbgFrame = 0;

    public EnchantingApparatusRenderer(BlockEntityRendererProvider.Context p_i226006_1_) {
        super(p_i226006_1_, new GenericModel<>("enchanting_apparatus"));
    }

    @Override
    public void captureDefaultRenderState(EnchantingApparatusTile animatable, Void relatedObject, ArsBlockEntityRenderState renderState, float partialTick) {
        super.captureDefaultRenderState(animatable, relatedObject, renderState, partialTick);
        if (animatable != null) {
            Direction dir = animatable.getBlockState().getValue(BlockStateProperties.FACING);
            renderState.addGeckolibData(FACING, dir);
            if (dbgFrame++ % 60 == 0) {
                LOGGER.info("EnchantingApparatus captureRenderState: pos={} facing={}", animatable.getBlockPos(), dir);
            }
        }
    }

    @Override
    public void adjustRenderPose(RenderPassInfo<ArsBlockEntityRenderState> renderPassInfo) {
        Direction dir = renderPassInfo.renderState().getOrDefaultGeckolibData(FACING, (Direction) null);
        if (dbgFrame % 60 == 1) {
            LOGGER.info("EnchantingApparatus adjustRenderPose: facing={}", dir);
        }
        // No rotation applied yet — observing default render to understand model orientation.
    }
}
