package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.block.tile.RitualBrazierTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public class RitualBrazierBlock extends TickableModBlock {
    public RitualBrazierBlock() {
        super(defaultProperties().noOcclusion().lightLevel((b) -> b.getValue(LIT) ? 15 : 0));
    }

    public static final Property<Boolean> LIT = BooleanProperty.create("lit");

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (!(worldIn.getBlockEntity(pos) instanceof RitualBrazierTile tile) || handIn != InteractionHand.MAIN_HAND)
            return super.use(state, worldIn, pos, player, handIn, hit);
        ItemStack heldStack = player.getMainHandItem();
        if (heldStack.isEmpty() && tile.ritual != null && !tile.isRitualDone()) {
            tile.startRitual();
        }
        if(!heldStack.isEmpty()){
            tile.tryBurnStack(heldStack);
        }
        return super.use(state, worldIn, pos, player, handIn, hit);
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState p_149656_1_) {
        return PushReaction.BLOCK;
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, world, pos, blockIn, fromPos, isMoving);
        if (!world.isClientSide() && world.getBlockEntity(pos) instanceof RitualBrazierTile tile) {
            tile.isOff = world.hasNeighborSignal(pos);
            if (world.hasNeighborSignal(pos) && tile.ritual != null && tile.canRitualStart()) {
                tile.startRitual();
            }
            BlockUtil.safelyUpdateState(world, pos);
        }
    }

    @Override
    public void playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player) {
        super.playerWillDestroy(worldIn, pos, state, player);
        if (worldIn.getBlockEntity(pos) instanceof RitualBrazierTile tile) {
            if (tile.ritual != null && !tile.ritual.isRunning() && !tile.ritual.isDone()) {
                worldIn.addFreshEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(ArsNouveauAPI.getInstance().getRitualItemMap().get(tile.ritual.getRegistryName()))));
            }

        }
    }


    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        if (entity != null) {
            world.setBlock(pos, state.setValue(LIT, false), 2);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RitualBrazierTile(pos, state);
    }
}
