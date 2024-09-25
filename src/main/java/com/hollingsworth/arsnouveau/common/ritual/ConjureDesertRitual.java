package com.hollingsworth.arsnouveau.common.ritual;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ritual.ConjureBiomeRitual;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ConjureDesertRitual extends ConjureBiomeRitual {
    public ConjureDesertRitual() {
        super(Biomes.DESERT);
    }

    @Override
    public void onStart(@Nullable Player player) {
        super.onStart(player);
        if(getConsumedItems().stream().anyMatch(i -> i.is(ItemTags.TERRACOTTA))){
            biome = Biomes.BADLANDS;
        }
    }

    @Override
    public BlockState stateForPos(BlockPos placePos) {
        boolean isBadlands = getConsumedItems().stream().anyMatch(i -> i.is(ItemTags.TERRACOTTA));
        if(isBadlands){
            int depth = getPos().getY() - placePos.getY();
            if(depth == 1){
                return Blocks.RED_SAND.defaultBlockState();
            }else if(depth == 2){
                return Blocks.ORANGE_TERRACOTTA.defaultBlockState();
            }else if(depth == 3 || depth == 4){
                return Blocks.RED_TERRACOTTA.defaultBlockState();
            }
            return Blocks.TERRACOTTA.defaultBlockState();
        }
        return placePos.getY() == getPos().getY() - 1 ? Blocks.SAND.defaultBlockState() : Blocks.SANDSTONE.defaultBlockState();
    }

    @Override
    public String getLangName() {
        return "Conjure Island: Desert";
    }

    @Override
    public String getLangDescription() {
        return "Creates an island of sand and sandstone in a circle around the ritual, converting the area to Desert. The island will generate with a radius of 7 blocks. Augmenting the ritual with Source Gems will increase the radius by 1 for each gem. Source must be provided nearby as blocks are generated. Augmenting with Terracotta will create Badlands instead.";
    }

    @Override
    public boolean canConsumeItem(ItemStack stack) {
        boolean isBadlands = getConsumedItems().stream().anyMatch(i -> i.is(ItemTags.TERRACOTTA));
        return super.canConsumeItem(stack) || (!isBadlands && stack.is(ItemTags.TERRACOTTA));
    }

    @Override
    public ResourceLocation getRegistryName() {
        return ArsNouveau.prefix( RitualLib.DESERT);
    }

    @Override
    public void read(HolderLookup.Provider provider,  CompoundTag tag) {
        super.read(provider, tag);
        boolean isBadlands = getConsumedItems().stream().anyMatch(i -> i.is(ItemTags.TERRACOTTA));
        if(isBadlands){
            biome = Biomes.BADLANDS;
        }
    }
}
