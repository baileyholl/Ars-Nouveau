package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.renderer.ANDataTickets;
import com.hollingsworth.arsnouveau.client.renderer.tile.GenericModel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

/**
 * GenericModel variant that selects texture based on DYE_COLOR from the render state.
 * Falls back to the base texture (name.png) if no color is set.
 * Colored variants use the pattern: name_colorname.png
 *
 * Color is stored in addAdditionalStateData (not addRenderData) to avoid the Java bridge
 * ClassCastException: GeoArmorRenderer passes the PLAYER's render state (e.g. AvatarRenderState)
 * through a blind cast, so typed addRenderData overrides fail at runtime.
 * addAdditionalStateData takes GeoRenderState directly — no cast, no crash.
 */
public class DyeableGeoModel<T extends GeoAnimatable> extends GenericModel<T> {

    public DyeableGeoModel(String name, String textPath) {
        super(name, textPath);
    }

    @Override
    public void addAdditionalStateData(T animatable, @Nullable Object relatedObject, GeoRenderState renderState) {
        if (relatedObject instanceof GeoArmorRenderer.RenderData renderData) {
            DyeColor color = renderData.itemStack().getOrDefault(DataComponents.BASE_COLOR, DyeColor.PURPLE);
            renderState.addGeckolibData(ANDataTickets.DYE_COLOR, color.getName());
        }
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        String color = renderState.getGeckolibData(ANDataTickets.DYE_COLOR);
        if (color != null && !color.isEmpty()) {
            return ArsNouveau.prefix("textures/" + textPathRoot + "/" + name + "_" + color + ".png");
        }
        return super.getTextureResource(renderState);
    }
}
