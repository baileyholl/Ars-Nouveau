package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.SkyBlockTile;
import com.hollingsworth.arsnouveau.common.light.ISkyLightSource;
import com.hollingsworth.arsnouveau.common.light.SkyLightOverrider;
import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SkyWeave extends MirrorWeave implements ITickableBlock, ISkyLightSource {
    public static final BooleanProperty SHOW_FACADE = BooleanProperty.create("show_facade");

    public SkyWeave(Properties properties) {
        super(properties);
    }

    public SkyWeave() {
        super();
    }

    {
        registerDefaultState(defaultBlockState().setValue(SHOW_FACADE, false));
    }

    @Override
    protected VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof SkyBlockTile sbt && sbt.showFacade()) {
            return sbt.mimicState.canOcclude() ? sbt.mimicState.getOcclusionShape(level, pos) : Shapes.empty();
        }
        return super.getOcclusionShape(state, level, pos);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new SkyBlockTile(p_153215_, p_153216_);
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pLevel.isClientSide || pHand != InteractionHand.MAIN_HAND) {
            return ItemInteractionResult.SUCCESS;
        }

        if (pLevel.getBlockEntity(pPos) instanceof SkyBlockTile tile) {
            if (!tile.showFacade() && !pPlayer.hasEffect(ModPotions.MAGIC_FIND_EFFECT)) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
        }
        return super.useItemOn(stack, pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    @Override
    public boolean emitsDirectSkyLight(BlockState state, BlockGetter level, BlockPos pos) {
        return !state.getValue(SHOW_FACADE);
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        if (!state.getValue(SHOW_FACADE)) {
            return 0;
        }
        return super.getLightBlock(state, level, pos);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(SHOW_FACADE);
    }
}
