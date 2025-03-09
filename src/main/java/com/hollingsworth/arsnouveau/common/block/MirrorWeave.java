package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.api.event.InvalidateMirrorweaveRender;
import com.hollingsworth.arsnouveau.common.block.tile.MirrorWeaveTile;
import com.hollingsworth.arsnouveau.common.datagen.BlockTagProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockAndTintGetter;
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
        if(level.isClientSide || tile == null || tile.mimicState == null || tile.nextState == null || tile.nextState.equals(tile.mimicState)){
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
        EventQueue.getServerInstance().addEvent(new InvalidateMirrorweaveRender(pPos, pLevel));
    }

    @Override
    public VoxelShape getInteractionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        if(pLevel.getBlockEntity(pPos) instanceof MirrorWeaveTile tile && !tile.mimicState.is(BlockTagProvider.FALSE_OCCLUSION)){
            return tile.mimicState.getInteractionShape(pLevel, pPos);
        }
        return super.getInteractionShape(pState, pLevel, pPos);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        if(pLevel.getBlockEntity(pPos) instanceof MirrorWeaveTile tile && !tile.mimicState.is(BlockTagProvider.FALSE_OCCLUSION)){
            return tile.mimicState.getShape(pLevel, pPos);
        }
        return super.getShape(pState, pLevel, pPos, pContext);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        if(pLevel.getBlockEntity(pPos) instanceof MirrorWeaveTile tile && !tile.mimicState.is(BlockTagProvider.FALSE_OCCLUSION)){
            return tile.mimicState.getCollisionShape(pLevel, pPos, pContext);
        }
        return super.getCollisionShape(pState, pLevel, pPos, pContext);
    }

    @Override
    public boolean hidesNeighborFace(BlockGetter level, BlockPos pos, BlockState state, BlockState neighborState, Direction dir) {
        if(level.getBlockEntity(pos) instanceof MirrorWeaveTile tile && !tile.mimicState.is(BlockTagProvider.FALSE_OCCLUSION)){
            return tile.mimicState.hidesNeighborFace(level, pos, neighborState, dir);
        }
        return super.hidesNeighborFace(level, pos, state, neighborState, dir);
    }

    @Override
    public boolean supportsExternalFaceHiding(BlockState state) {
        return super.supportsExternalFaceHiding(state);
    }

    @Override
    public BlockState getAppearance(BlockState state, BlockAndTintGetter level, BlockPos pos, Direction side, @Nullable BlockState queryState, @Nullable BlockPos queryPos) {
        if(level.getBlockEntity(pos) instanceof MirrorWeaveTile tile && !tile.mimicState.is(BlockTagProvider.FALSE_OCCLUSION)){
            return tile.mimicState;
        }

        return super.getAppearance(state, level, pos, side, queryState, queryPos);
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        if(level.getBlockEntity(pos) instanceof MirrorWeaveTile tile && !tile.mimicState.is(BlockTagProvider.FALSE_OCCLUSION)){
            return tile.mimicState.propagatesSkylightDown(level, pos);
        }
        return super.propagatesSkylightDown(state, level, pos);
    }

    @Override
    public boolean hasDynamicShape() {
        return true;
    }

    @Override
    public boolean isCollisionShapeFullBlock(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        if(pLevel.getBlockEntity(pPos) instanceof MirrorWeaveTile tile && !tile.mimicState.is(BlockTagProvider.FALSE_OCCLUSION)){
            return tile.mimicState.isCollisionShapeFullBlock(pLevel, pPos);
        }
        return super.isCollisionShapeFullBlock(pState, pLevel, pPos);
    }

    @Override
    protected boolean isOcclusionShapeFullBlock(BlockState state, BlockGetter level, BlockPos pos) {
        if(level.getBlockEntity(pos) instanceof MirrorWeaveTile tile && !tile.mimicState.is(BlockTagProvider.FALSE_OCCLUSION)){
            return Block.isShapeFullBlock(tile.mimicState.getOcclusionShape(level, pos));
        }
        return super.isOcclusionShapeFullBlock(state, level, pos);
    }


    @Override
    protected VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        if(level.getBlockEntity(pos) instanceof MirrorWeaveTile tile  && !tile.mimicState.is(BlockTagProvider.FALSE_OCCLUSION)){
            return tile.mimicState.getOcclusionShape(level, pos);
        }
        return super.getOcclusionShape(state, level, pos);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
        if(!level.isClientSide && level.getBlockEntity(pos) instanceof MirrorWeaveTile tile){
            tile.renderInvalid = true;
            tile.updateBlock();
        }
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        if(level.getBlockEntity(pos) instanceof MirrorWeaveTile tile && !tile.mimicState.is(BlockTagProvider.FALSE_OCCLUSION)){
            return tile.mimicState.getShadeBrightness(level, pos);
        }
        return super.getShadeBrightness(state, level, pos);
    }

    @Override
    protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if(level.getBlockEntity(pos) instanceof MirrorWeaveTile tile && !tile.mimicState.is(BlockTagProvider.FALSE_OCCLUSION)){
            return tile.mimicState.getVisualShape(level, pos, context);
        }
        return super.getVisualShape(state, level, pos, context);
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
