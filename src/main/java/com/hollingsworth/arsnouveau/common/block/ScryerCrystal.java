package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.ScryerCrystalTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class ScryerCrystal extends TickableModBlock{
    public static final DirectionProperty FACING = DirectionProperty.create("facing");
    public static final BooleanProperty BEING_VIEWED = BooleanProperty.create("being_viewed");
    public ScryerCrystal(Properties properties, String registry) {
        super(properties, registry);
    }

    public ScryerCrystal(String registryName) {
        super(registryName);
    }

    public ScryerCrystal(Properties properties) {
        super(properties);
        this.registerDefaultState((this.stateDefinition.any().setValue(FACING, Direction.NORTH)).setValue(BEING_VIEWED, false));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, BEING_VIEWED);
    }

    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return Shapes.empty();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ScryerCrystalTile(pPos, pState);
    }
}
