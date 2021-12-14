package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemRenderer;
import com.hollingsworth.arsnouveau.common.block.tile.BasicSpellTurretTile;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ReducerTurretRenderer extends BasicTurretRenderer{
    public static AnimatedGeoModel model = new GenericModel("spell_turret");

    public ReducerTurretRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
        super(rendererDispatcherIn, model);
    }

    public ReducerTurretRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn, AnimatedGeoModel<BasicSpellTurretTile> modelProvider) {
        super(rendererDispatcherIn, modelProvider);
    }

    public static GenericItemRenderer getISTER(){
        return new GenericItemRenderer(model);
    }
}
