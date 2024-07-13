package com.hollingsworth.arsnouveau.api.scrying;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;


public class TagScryer implements IScryer {
    public static final TagScryer INSTANCE = new TagScryer();
    ResourceLocation tagID;
    TagKey<Block> blockTag;

    public TagScryer() {
    }

    public TagScryer(TagKey<Block> blockTag) {
        this.blockTag = blockTag;
        this.tagID = blockTag.location();
    }

    @Override
    public boolean shouldRevealBlock(BlockState state, BlockPos p, Player player) {
        return blockTag != null && state.is(blockTag);
    }

    @Override
    public IScryer fromTag(CompoundTag tag) {
        TagScryer scryer = new TagScryer();
        if (tag.contains("blockTag")) {
            var tagKey = TagKey.create(Registries.BLOCK, ResourceLocation.tryParse(tag.getString("blockTag")));
            var blocks = BuiltInRegistries.BLOCK.getTag(tagKey);
            if(blocks.isEmpty()){
                return scryer;
            }
            scryer.blockTag = tagKey;
        }
        return scryer;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        if (tagID != null) {
            tag.putString("blockTag", tagID.toString());
        }
        return IScryer.super.toTag(tag);
    }

    @Override
    public ResourceLocation getRegistryName() {
        return ArsNouveau.prefix( "tag_scryer");
    }
}
