package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class SpellBookModel extends TransformAnimatedModel<SpellBook> {
    public static final Identifier OPEN = ArsNouveau.prefix("spellbook_open");
    public static final Identifier CLOSED = ArsNouveau.prefix("spellbook_closed");

    public Identifier modelLoc;

    public SpellBookModel(Identifier modelLocation) {
        this.modelLoc = modelLocation;
    }

    @Override
    public Identifier getModelResource(@Nullable ItemDisplayContext transformType) {
        if (transformType == ItemDisplayContext.GUI || transformType == ItemDisplayContext.FIXED) {
            return CLOSED;
        }
        return modelLoc;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return ArsNouveau.prefix("textures/item/spellbook_purple.png");
    }

    @Override
    public Identifier getAnimationResource(SpellBook animatable) {
        return ArsNouveau.prefix("empty");
    }

    // GeckoLib 5: getRenderType moved to GeoRenderer, not GeoModel
}
