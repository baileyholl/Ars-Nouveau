package com.hollingsworth.arsnouveau.api.ritual.features;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.ritual.FeaturePlacementRitual;
import com.hollingsworth.arsnouveau.common.block.tile.RitualBrazierTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.List;

public class CocoaFeature implements IPlaceableFeature {
    double distance, chance;
    List<BlockState> cocoaStates = new ArrayList<>();

    public CocoaFeature(double distance, double chance) {
        this.distance = distance;
        this.chance = chance;
        BlockState state = Blocks.COCOA.defaultBlockState();
        BlockState north = state.setValue(net.minecraft.world.level.block.CocoaBlock.FACING, Direction.NORTH);
        BlockState south = state.setValue(net.minecraft.world.level.block.CocoaBlock.FACING, Direction.SOUTH);
        BlockState east = state.setValue(net.minecraft.world.level.block.CocoaBlock.FACING, Direction.EAST);
        BlockState west = state.setValue(net.minecraft.world.level.block.CocoaBlock.FACING, Direction.WEST);
        cocoaStates.add(north);
        cocoaStates.add(south);
        cocoaStates.add(east);
        cocoaStates.add(west);
    }

    @Override
    public double distanceFromOthers() {
        return distance;
    }

    @Override
    public boolean onPlace(Level level, BlockPos pos, FeaturePlacementRitual placementRitual, RitualBrazierTile brazierTile) {

        for (BlockState state : cocoaStates) {
            if (level.random.nextFloat() < chance && state.canSurvive(level, pos)) {
                if (state.getBlock().asItem() instanceof BlockItem blockItem) {
                    blockItem.place(new BlockPlaceContext(level, ANFakePlayer.getPlayer((ServerLevel) level), InteractionHand.MAIN_HAND, new ItemStack(blockItem), new BlockHitResult(new Vec3(pos.getX(), pos.getY(), pos.getZ()), Direction.DOWN, pos, false)));
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public String getFeatureName() {
        return "cocoa";
    }

    @Override
    public Pair<BlockPos, BlockPos> getCustomOffsets() {
        return new Pair<>(BlockPos.ZERO, new BlockPos(0, 10, 0));
    }
}
