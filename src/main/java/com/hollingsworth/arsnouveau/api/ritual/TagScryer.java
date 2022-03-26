package com.hollingsworth.arsnouveau.api.ritual;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class TagScryer implements IScryer {
    public static final TagScryer INSTANCE = new TagScryer();
    ResourceLocation tagID;
    Tag<Block> blockTag;

    public TagScryer() {}

    public TagScryer(ResourceLocation tagID) {
        this.tagID = tagID;
        this.blockTag = BlockTags.getAllTags().getTag(tagID);
    }

    public TagScryer(Tag.Named<Block> blockTag) {
        this.blockTag = blockTag;
        this.tagID = blockTag.getName();
    }

    @Override
    public boolean shouldRevealBlock(BlockState state, BlockPos p, Player player) {
        return blockTag != null && state.is(blockTag);
    }

    @Override
    public IScryer fromTag(CompoundTag tag) {
        TagScryer scryer = new TagScryer();
        if(tag.contains("blockTag")){
            Tag<Block> tag1 = BlockTags.getAllTags().getTag(new ResourceLocation(tag.getString("blockTag")));
            scryer.blockTag = tag1;
        }
        return scryer;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        if(tagID != null){
            tag.putString("blockTag", tagID.toString());
        }
        return IScryer.super.toTag(tag);
    }

    @Override
    public String getID() {
        return "an_tag_scryer";
    }
}
