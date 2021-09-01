package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemRenderer;
import com.hollingsworth.arsnouveau.common.block.tile.BasicSpellTurretTile;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class TimerTurretRenderer extends BasicTurretRenderer{
    public static AnimatedGeoModel model = new GenericModel("spell_turret_timer");

    public TimerTurretRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn, model);
    }

    public TimerTurretRenderer(TileEntityRendererDispatcher rendererDispatcherIn, AnimatedGeoModel<BasicSpellTurretTile> modelProvider) {
        super(rendererDispatcherIn, modelProvider);
    }

    public static GenericItemRenderer getISTER(){
        return new GenericItemRenderer(model);
    }
}
