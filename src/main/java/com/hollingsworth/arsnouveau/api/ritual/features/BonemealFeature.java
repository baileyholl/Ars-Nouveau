package com.hollingsworth.arsnouveau.api.ritual.features;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.ritual.FeaturePlacementRitual;
import com.hollingsworth.arsnouveau.common.block.tile.RitualBrazierTile;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class BonemealFeature implements IPlaceableFeature {

    double distance;
    double chance;

    public BonemealFeature(double distance, double chance) {
        this.distance = distance;
        this.chance = chance;
    }

    @Override
    public double distanceFromOthers() {
        return distance;
    }

    @Override
    public boolean onPlace(Level level, BlockPos pos, FeaturePlacementRitual placementRitual, RitualBrazierTile brazierTile) {
        ItemStack stack = new ItemStack(Items.BONE_MEAL, 64);
        if (level.random.nextFloat() < chance && BoneMealItem.applyBonemeal(stack, level, pos.below(), ANFakePlayer.getPlayer((ServerLevel) level))) {
            if (!level.isClientSide) {
                level.levelEvent(1505, pos.below(), 0); //particles
            }
            return true;
        }
        return false;
    }

    @Override
    public String getFeatureName() {
        return "bonemeal";
    }
}
