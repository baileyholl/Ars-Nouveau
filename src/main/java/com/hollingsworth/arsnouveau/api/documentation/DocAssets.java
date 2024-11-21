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

    public record BlitInfo(ResourceLocation location, int u, int v, int width, int height) {
        public BlitInfo(ResourceLocation location, int width, int height){
            this(location, 0, 0, width, height);
        }
    }
}
