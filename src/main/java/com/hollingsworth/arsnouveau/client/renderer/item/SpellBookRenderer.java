package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.common.items.SpellBook;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

// GeckoLib 5: default getRenderType is entityCutoutNoCull; entityTranslucent caused rendering artifacts
public class SpellBookRenderer extends GeoItemRenderer<SpellBook> {
    public GeoModel<SpellBook> closedModel;

    public SpellBookRenderer() {
        super(new SpellBookModel(SpellBookModel.OPEN));
        this.closedModel = new SpellBookModel(SpellBookModel.CLOSED);
    }
}
