package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.client.renderer.tile.GenericModel;
import com.hollingsworth.arsnouveau.common.items.MobJarItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

// 1.21.11: renderByItem and BlockEntityRenderDispatcher.renderItem() removed.
// TODO: Port mob-jar entity rendering to new GeckoLib 5 / 1.21.11 API.
// The new API requires using submit() pipeline, not the old direct-render path.
public class MobJarItemRenderer extends GeoItemRenderer<MobJarItem> {
    public MobJarItemRenderer() {
        super(new GenericModel<>("mob_jar"));
    }
}
