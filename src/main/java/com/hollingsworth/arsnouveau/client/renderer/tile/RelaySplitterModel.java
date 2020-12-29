package com.hollingsworth.arsnouveau.client.renderer.tile;// Made with Blockbench 3.6.6
// Exported for Minecraft version 1.15
// Paste this class into your mod and generate all required imports


import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.tile.ArcaneRelaySplitterTile;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class RelaySplitterModel extends AnimatedGeoModel<ArcaneRelaySplitterTile> {
	@Override
	public ResourceLocation getModelLocation(ArcaneRelaySplitterTile volcanicTile) {
		return new ResourceLocation(ArsNouveau.MODID , "geo/mana_splitter.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(ArcaneRelaySplitterTile volcanicTile) {
		return new ResourceLocation(ArsNouveau.MODID, "textures/blocks/mana_splitter.png");
	}

	@Override
	public ResourceLocation getAnimationFileLocation(ArcaneRelaySplitterTile volcanicTile) {
		return new ResourceLocation(ArsNouveau.MODID , "animations/mana_splitter_animation.json");
	}
}