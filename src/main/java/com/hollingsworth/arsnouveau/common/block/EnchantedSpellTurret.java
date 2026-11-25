package com.hollingsworth.arsnouveau.common.block;

import com.google.common.collect.Maps;
import com.hollingsworth.arsnouveau.common.block.tile.EnchantedTurretTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class EnchantedSpellTurret extends BasicSpellTurret {

    public EnchantedSpellTurret() {
        super(defaultProperties().noOcclusion());
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new EnchantedTurretTile(pos, state);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPES.get(state.getValue(FACING));
    }

    static final Map<Direction, VoxelShape> SHAPES = Maps.newEnumMap(Map.of(
            Direction.NORTH, Block.box(4.5, 4.5, 3.5, 11.5, 11.5, 10.5),
            Direction.SOUTH, Block.box(4.5, 4.5, 5.5, 11.5, 11.5, 12.5),
            Direction.WEST, Block.box(3.5, 4.5, 4.5, 10.5, 11.5, 11.5),
            Direction.EAST, Block.box(5.5, 4.5, 4.5, 12.5, 11.5, 11.5),
            Direction.UP, Block.box(4.5, 5.5, 4.5, 11.5, 12.5, 11.5),
            Direction.DOWN, Block.box(4.5, 3.5, 4.5, 11.5, 10.5, 11.5)));

}
