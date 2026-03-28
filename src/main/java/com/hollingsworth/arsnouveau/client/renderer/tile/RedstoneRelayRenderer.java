package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemBlockRenderer;
import com.hollingsworth.arsnouveau.common.block.tile.RedstoneRelayTile;
import com.hollingsworth.arsnouveau.common.items.AnimBlockItem;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.RedStoneWireBlock;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class RedstoneRelayRenderer extends ArsGeoBlockRenderer<RedstoneRelayTile> {

    public RedstoneRelayRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
        super(rendererDispatcherIn, new GenericModel<>("redstone_relay"));
    }

    @Override
    public int getRenderColor(RedstoneRelayTile animatable, Void renderState, float partialTick) {
        return 0xFF000000 | RedStoneWireBlock.getColorForPower(Math.max(1, animatable.getOutputPower()));
    }

    public static GenericItemBlockRenderer getISTER() {
        return new GenericItemBlockRenderer(new GenericModel<>("redstone_relay")) {
            // GeckoLib 5: GeoItemRenderer uses O=GeoItemRenderer.RenderData, not Void
            @Override
            public int getRenderColor(AnimBlockItem animatable, GeoItemRenderer.RenderData renderState, float partialTick) {
                return 0xFF000000 | RedStoneWireBlock.getColorForPower(1);
            }
        };
    }
}
