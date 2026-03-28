package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.IntangibleAirTile;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.world.entity.LivingEntity;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.world.level.BlockGetter;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.world.level.LevelAccessor;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.world.level.block.Block;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.world.level.block.LiquidBlockContainer;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.world.level.block.RenderShape;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.world.level.block.entity.BlockEntity;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.world.level.block.state.BlockState;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.world.level.material.Fluid;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.world.level.material.FluidState;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.world.level.material.MapColor;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.world.level.material.PushReaction;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.world.phys.shapes.CollisionContext;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.world.phys.shapes.Shapes;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class IntangibleAirBlock extends TickableModBlock implements LiquidBlockContainer {

    public IntangibleAirBlock() {
        super(BlockRegistry.newBlockProperties().noCollision().noLootTable().pushReaction(PushReaction.BLOCK).mapColor(MapColor.NONE));
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos pos) {
        return Shapes.block();
    }


    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return Shapes.empty();
    }

    @Override
    protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected VoxelShape getOcclusionShape(BlockState state) {
        return Shapes.empty();
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new IntangibleAirTile(pos, state);
    }

    @Override
    protected boolean isAir(BlockState state) {
        return false;
    }

    @Override
    public boolean canPlaceLiquid(@Nullable LivingEntity pPlayer, BlockGetter pLevel, BlockPos pPos, BlockState pState, Fluid pFluid) {
        return false;
    }

    @Override
    public boolean placeLiquid(LevelAccessor worldIn, BlockPos pos, BlockState state, FluidState fluidStateIn) {
        return false;
    }
}
