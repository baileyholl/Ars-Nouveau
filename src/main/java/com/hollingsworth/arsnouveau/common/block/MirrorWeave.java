package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.MirrorWeaveTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class MirrorWeave extends ModBlock implements EntityBlock {
    public static final Property<Integer> LIGHT_LEVEL = IntegerProperty.create("level", 0, 15);

    public MirrorWeave(BlockBehaviour.Properties properties) {
        super(properties.lightLevel((b) -> b.getValue(LIGHT_LEVEL)));
    }

    public MirrorWeave(){
        this(Block.Properties.of().strength(0.1F).sound(SoundType.WOOL).noOcclusion());
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pLevel.isClientSide || pHand != InteractionHand.MAIN_HAND) {
            return ItemInteractionResult.SUCCESS;
        }

        MirrorWeaveTile tile = (MirrorWeaveTile) pLevel.getBlockEntity(pPos);
        if(tile != null){
            if(stack.getItem() instanceof BlockItem blockItem && !(blockItem.getBlock() instanceof EntityBlock)){
                if(tile.mimicState.is(blockItem.getBlock())){
                    return super.useItemOn(stack, pState, pLevel, pPos, pPlayer, pHand, pHit);
                }
                tile.nextState = blockItem.getBlock().getStateForPlacement(new BlockPlaceContext(pLevel, pPlayer, pHand, stack, pHit));
                this.setMimicState(pLevel, pPos, !pPlayer.isShiftKeyDown());
                return ItemInteractionResult.SUCCESS;
            }
        }

        return super.useItemOn(stack,pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    public void setMimicState(Level level, BlockPos pos, boolean updateNeighbors) {
        MirrorWeaveTile tile = (MirrorWeaveTile) level.getBlockEntity(pos);
        if(tile == null || tile.mimicState == null || tile.nextState == null || tile.nextState.equals(tile.mimicState)){
            return;
        }
        BlockState previousState = tile.mimicState;
        tile.mimicState = tile.nextState;
        level.setBlockAndUpdate(pos, tile.getBlockState().setValue(LIGHT_LEVEL, tile.mimicState.getLightEmission(level, pos)));
        tile.updateBlock();
        int ticks = 1;
        if(!updateNeighbors)
            return;
        for(Direction d : Direction.values()){
            BlockPos offset = pos.relative(d);
            if(level.getBlockEntity(offset) instanceof MirrorWeaveTile neighbor){
                if(neighbor.mimicState == previousState) {
                    neighbor.nextState = tile.mimicState;
                    level.scheduleTick(offset, neighbor.getBlockState().getBlock(), ticks++);
                }
            }
        }
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        MirrorWeaveTile tile = (MirrorWeaveTile) pLevel.getBlockEntity(pPos);
        if(tile == null)
            return;
        this.setMimicState(pLevel, pPos, true);
    }

    @Override
    public VoxelShape getInteractionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        if(pLevel.getBlockEntity(pPos) instanceof MirrorWeaveTile tile  && tile.mimicState.getBlock() != this){
            return tile.mimicState.getInteractionShape(pLevel, pPos);
        }
        return super.getInteractionShape(pState, pLevel, pPos);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        if(pLevel.getBlockEntity(pPos) instanceof MirrorWeaveTile tile  && tile.mimicState.getBlock() != this){
            return tile.mimicState.getShape(pLevel, pPos);
        }
        return super.getShape(pState, pLevel, pPos, pContext);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        if(pLevel.getBlockEntity(pPos) instanceof MirrorWeaveTile tile && tile.mimicState.getBlock() != this){
            return tile.mimicState.getCollisionShape(pLevel, pPos, pContext);
        }
        return super.getCollisionShape(pState, pLevel, pPos, pContext);
    }

    @Override
    public boolean isCollisionShapeFullBlock(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        if(pLevel.getBlockEntity(pPos) instanceof MirrorWeaveTile tile  && tile.mimicState.getBlock() != this){
            return tile.mimicState != null && tile.mimicState.isCollisionShapeFullBlock(pLevel, pPos);
        }
        return super.isCollisionShapeFullBlock(pState, pLevel, pPos);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new MirrorWeaveTile(pPos, pState);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIGHT_LEVEL);
    }
}
