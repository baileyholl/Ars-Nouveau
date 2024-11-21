package com.hollingsworth.arsnouveau.api.documentation;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.resources.ResourceLocation;

public class DocAssets {
    public static final BlitInfo HEADER_WITH_ITEM = new BlitInfo(ArsNouveau.prefix("textures/gui/documentation/doc_detail_chapter_header.png"), 118, 22);
    public static final BlitInfo UNDERLINE = new BlitInfo(ArsNouveau.prefix("textures/gui/documentation/doc_detail_underline.png"), 118, 6);
    public static final BlitInfo ITEM_FRAME = new BlitInfo(ArsNouveau.prefix("textures/gui/documentation/doc_detail_frame_item.png"), 22, 22);
    public static final BlitInfo PEDESTAL_FRAME = new BlitInfo(ArsNouveau.prefix("textures/gui/documentation/doc_detail_frame_pedestal.png"), 22, 22);
    public static final BlitInfo RING = new BlitInfo(ArsNouveau.prefix("textures/gui/documentation/doc_detail_ring.png"), 64, 64);

    public static final BlitInfo GLYPH_DETAILS = new BlitInfo(ArsNouveau.prefix("textures/gui/documentation/doc_detail_frame_glyph_attributes.png"), 118, 14);
    public static final BlitInfo FIRE_ICON = new BlitInfo(ArsNouveau.prefix("textures/gui/documentation/doc_icon_fire.png"), 10, 10);
    public static final BlitInfo WATER_ICON = new BlitInfo(ArsNouveau.prefix("textures/gui/documentation/doc_icon_water.png"), 10, 10);
    public static final BlitInfo EARTH_ICON = new BlitInfo(ArsNouveau.prefix("textures/gui/documentation/doc_icon_earth.png"), 10, 10);
    public static final BlitInfo AIR_ICON = new BlitInfo(ArsNouveau.prefix("textures/gui/documentation/doc_icon_air.png"), 10, 10);
    public static final BlitInfo CONJURATION_ICON = new BlitInfo(ArsNouveau.prefix("textures/gui/documentation/doc_icon_conjuration.png"), 10, 10);
    public static final BlitInfo ALCHEMANCY_ICON = new BlitInfo(ArsNouveau.prefix("textures/gui/documentation/doc_icon_alchemancy_glyph.png"), 10, 10);
    public static final BlitInfo MANIPULATION_ICON = new BlitInfo(ArsNouveau.prefix("textures/gui/documentation/doc_icon_manipulation.png"), 10, 10);
    public static final BlitInfo FORM_ICON = new BlitInfo(ArsNouveau.prefix("textures/gui/documentation/doc_icon_form.png"), 8, 10);
    public static final BlitInfo EFFECT_ICON = new BlitInfo(ArsNouveau.prefix("textures/gui/documentation/doc_icon_effect.png"), 8, 10);
    public static final BlitInfo AUGMENT_ICON = new BlitInfo(ArsNouveau.prefix("textures/gui/documentation/doc_icon_augment.png"), 8, 10);
    public static final BlitInfo AUGMENT_UNAVAILABLE_ICON = new BlitInfo(ArsNouveau.prefix("textures/gui/documentation/doc_icon_augment_unavailable.png"), 8, 10);
    public static final BlitInfo NA_ICON = new BlitInfo(ArsNouveau.prefix("textures/gui/documentation/doc_icon_not_applicable.png"), 10, 10);

    // tier one two and three
    public static final BlitInfo TIER_ONE = new BlitInfo(ArsNouveau.prefix("textures/gui/documentation/doc_icon_tier1.png"), 7, 10);
    public static final BlitInfo TIER_TWO = new BlitInfo(ArsNouveau.prefix("textures/gui/documentation/doc_icon_tier2.png"), 7, 10);
    public static final BlitInfo TIER_THREE = new BlitInfo(ArsNouveau.prefix("textures/gui/documentation/doc_icon_tier3.png"), 7, 10);


    public record BlitInfo(ResourceLocation location, int u, int v, int width, int height) {
        public BlitInfo(ResourceLocation location, int width, int height){
            this(location, 0, 0, width, height);
        }
    }
}
