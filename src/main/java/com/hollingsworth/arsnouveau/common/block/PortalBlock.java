package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.api.util.FlatPortalAreaHelper;
import com.hollingsworth.arsnouveau.common.block.tile.PortalTile;
import com.hollingsworth.arsnouveau.common.datagen.BlockTagProvider;
import com.hollingsworth.arsnouveau.common.items.DominionWand;
import com.hollingsworth.arsnouveau.common.items.data.WarpScrollData;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

import static com.hollingsworth.arsnouveau.setup.config.ServerConfig.ENABLE_WARP_PORTALS;

public class PortalBlock extends TickableModBlock {
    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 14.0D, 12.0D, 14.0D);


    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;
    public static final BooleanProperty ALTERNATE = BooleanProperty.create("alternate");

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    public PortalBlock() {
        super(BlockBehaviour.Properties.of().pushReaction(PushReaction.BLOCK).noCollission().strength(-1.0F, 3600000.0F).noLootTable());
        this.registerDefaultState(this.defaultBlockState().setValue(AXIS, Direction.Axis.X).setValue(ALTERNATE, false));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, RandomSource rand) {
        for (int i = 0; i < 4; ++i) {
            double d0 = (double) pos.getX() + (double) rand.nextFloat();
            double d1 = (double) pos.getY() + (double) rand.nextFloat();
            double d2 = (double) pos.getZ() + (double) rand.nextFloat();
            double d3 = (rand.nextFloat() - 0.5D) * 0.5D;
            double d4 = (rand.nextFloat() - 0.5D) * 0.5D;
            double d5 = (rand.nextFloat() - 0.5D) * 0.5D;
            int j = rand.nextInt(2) * 2 - 1;
            if (worldIn.getBlockState(pos.west()).getBlock() != this && worldIn.getBlockState(pos.east()).getBlock() != this) {
                d0 = pos.getX() + 0.5D + 0.25D * j;
                d3 = rand.nextFloat() * 2.0F * j;
            } else {
                d2 = pos.getZ() + 0.5D + 0.25D * j;
                d5 = rand.nextFloat() * 2.0F * j;
            }

            worldIn.addParticle(ParticleTypes.PORTAL, d0, d1, d2, d3, d4, d5);
        }
    }

    public void setType(Level pLevel, BlockPos pPos,boolean alternate){
        if(pLevel.getBlockState(pPos).getValue(ALTERNATE) == alternate)
            return;
        pLevel.setBlockAndUpdate(pPos, pLevel.getBlockState(pPos).setValue(ALTERNATE, alternate));
        for (BlockPos pos : BlockPos.betweenClosed(pPos.offset(-1, -1, -1), pPos.offset(1, 1, 1))) {
            if (pLevel.getBlockState(pos).getBlock() instanceof PortalBlock portalBlock && pLevel.getBlockState(pos).getValue(ALTERNATE) != alternate) {
                setType(pLevel, pos, alternate);
            }
        }
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if(pLevel.isClientSide || pHand != InteractionHand.MAIN_HAND)
            return ItemInteractionResult.SUCCESS;
        if(pPlayer.getItemInHand(pHand).getItem() instanceof DominionWand){
            if(pLevel.getBlockEntity(pPos) instanceof PortalTile){
                boolean nextVal = !pLevel.getBlockState(pPos).getValue(ALTERNATE);
                setType(pLevel, pPos, nextVal);
                return ItemInteractionResult.CONSUME;
            }
        }
        return super.useItemOn(stack, pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    @Override
    public void onProjectileHit(Level worldIn, BlockState state, BlockHitResult hit, Projectile projectile) {
        if (worldIn.getBlockEntity(hit.getBlockPos()) instanceof PortalTile tile) {
            tile.warp(projectile);
        }
    }


    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new PortalTile(p_153215_, p_153216_);
    }

    @Override
    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {
        if (worldIn.getBlockEntity(pos) instanceof PortalTile tile) {
            if(entityIn instanceof Player player) {
                tile.entityQueue.add(player);
            }else{
                tile.warp(entityIn);
                entityIn.fallDistance = 0;
            }
        }
    }

    public boolean trySpawnPortal(Level worldIn, BlockPos stackPos, WarpScrollData data, String displayName) {
        if (!ENABLE_WARP_PORTALS.get()) return false;
        return trySpawnVerticalPortal(worldIn, stackPos, data, displayName) || trySpawnHorizontalPortal(worldIn, stackPos, data, displayName);
    }


    public boolean trySpawnVerticalPortal(LevelAccessor worldIn, BlockPos stackPos, WarpScrollData data, String displayName) {
        Size portalblock$size = this.isPortal(worldIn, stackPos);
        if (portalblock$size != null) {
            portalblock$size.placePortalBlocks(data, displayName);
            return true;
        } else {
            return false;
        }
    }

    public boolean trySpawnHorizontalPortal(Level worldIn, BlockPos stackPos, WarpScrollData data, String displayName) {
        FlatPortalAreaHelper helper = new FlatPortalAreaHelper().init(worldIn, stackPos, null, (bs) -> bs.is(BlockTagProvider.DECORATIVE_AN));
        if (helper.isValidFrame()) {
            BlockPos.betweenClosed(helper.lowerCorner, helper.lowerCorner.relative(Direction.Axis.X, helper.xSize - 1).relative(Direction.Axis.Z, helper.zSize - 1)).forEach((blockPos) -> {
                worldIn.setBlock(blockPos, BlockRegistry.PORTAL_BLOCK.defaultBlockState().setValue(PortalBlock.AXIS, Direction.Axis.Y), 18);
                if (worldIn.getBlockEntity(blockPos) instanceof PortalTile tile) {
                    tile.warpPos = data.pos();
                    tile.dimID = data.dimension();
                    tile.rotationVec = data.rotation();
                    tile.displayName = displayName;
                    tile.isHorizontal = true;
                    tile.updateBlock();
                }
            });
            return true;
        }
        return false;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return switch (rot) {
            case COUNTERCLOCKWISE_90, CLOCKWISE_90 -> switch (state.getValue(AXIS)) {
                case Z -> state.setValue(AXIS, Direction.Axis.X);
                case X -> state.setValue(AXIS, Direction.Axis.Z);
                default -> state;
            };
            default -> state;
        };
    }

    /**
     * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
     * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
     * returns its solidified counterpart.
     * Note that this method should ideally consider only the specific face passed in.
     */
    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
        Direction.Axis direction$axis = facing.getAxis();
        Direction.Axis direction$axis1 = stateIn.getValue(AXIS);
        boolean flag = direction$axis1 != direction$axis && direction$axis.isHorizontal();
        if (worldIn.getBlockEntity(currentPos) instanceof PortalTile portal && portal.isHorizontal) {
            FlatPortalAreaHelper frameTester = new FlatPortalAreaHelper();
            frameTester.init((Level) worldIn, currentPos, null, (bs) -> bs.is(BlockTagProvider.DECORATIVE_AN));
            if (!frameTester.isValidFrame()) {
                return Blocks.AIR.defaultBlockState();
            }
            return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
        }
        return !flag && facingState.getBlock() != this && !(new Size(worldIn, currentPos, direction$axis1)).isComplete() ? Blocks.AIR.defaultBlockState() : super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS).add(ALTERNATE);
    }

    @Nullable
    public Size isPortal(LevelAccessor worldIn, BlockPos pos) {
        Size portalblock$size = new Size(worldIn, pos, Direction.Axis.X);
        if (portalblock$size.isValid() && portalblock$size.portalBlockCount == 0) {
            return portalblock$size;
        } else {
            Size portalblock$size1 = new Size(worldIn, pos, Direction.Axis.Z);
            return portalblock$size1.isValid() && portalblock$size1.portalBlockCount == 0 ? portalblock$size1 : null;
        }
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext useContext) {
        return false;
    }

    public static class Size {
        private final LevelAccessor world;
        private final Direction.Axis axis;
        private final Direction rightDir;
        private final Direction leftDir;
        private int portalBlockCount;
        @Nullable
        private BlockPos bottomLeft;
        private int height;
        private int width;
        private BlockPos warpPos;
        private int dimId;

        public Size(LevelAccessor worldIn, BlockPos pos, Direction.Axis axisIn) {
            this.world = worldIn;
            this.axis = axisIn;
            if (axisIn == Direction.Axis.X) {
                this.leftDir = Direction.EAST;
                this.rightDir = Direction.WEST;
            } else {
                this.leftDir = Direction.NORTH;
                this.rightDir = Direction.SOUTH;
            }

            // Some cursed decompiled mojang code
            BlockPos blockpos = pos;
            while (pos.getY() > blockpos.getY() - 21 && pos.getY() > 0 && this.canReplace(worldIn.getBlockState(pos.below()))) {
                pos = pos.below();
            }

            int i = this.getDistanceUntilEdge(pos, this.leftDir) - 1;
            if (i >= 0) {
                this.bottomLeft = pos.relative(this.leftDir, i);
                this.width = this.getDistanceUntilEdge(this.bottomLeft, this.rightDir);
                if (this.width < 1 || this.width > 21) {
                    this.bottomLeft = null;
                    this.width = 0;
                }
            }

            if (this.bottomLeft != null) {
                this.height = this.calculatePortalHeight();
            }

        }

        protected int getDistanceUntilEdge(BlockPos pos, Direction directionIn) {
            int i;
            for (i = 0; i < 22; ++i) {
                BlockPos blockpos = pos.relative(directionIn, i);
                if (!this.canReplace(this.world.getBlockState(blockpos)) || !isPortalFrame(this.world, blockpos.below())) {
                    break;
                }
            }

            BlockPos framePos = pos.relative(directionIn, i);
            return isPortalFrame(this.world, framePos) ? i : 0;
        }

        public boolean isPortalFrame(LevelAccessor world, BlockPos pos) {
            return world.getBlockState(pos).is(BlockTagProvider.DECORATIVE_AN);
        }

        public int getHeight() {
            return this.height;
        }

        public int getWidth() {
            return this.width;
        }

        protected int calculatePortalHeight() {
            label56:
            for (this.height = 0; this.height < 21; ++this.height) {
                for (int i = 0; i < this.width; ++i) {
                    BlockPos blockpos = this.bottomLeft.relative(this.rightDir, i).above(this.height);
                    BlockState blockstate = this.world.getBlockState(blockpos);
                    if (!this.canReplace(blockstate)) {
                        break label56;
                    }

                    Block block = blockstate.getBlock();
                    if (block instanceof PortalBlock) {
                        ++this.portalBlockCount;
                    }

                    if (i == 0) {
                        BlockPos framePos = blockpos.relative(this.leftDir);
                        if (!isPortalFrame(this.world, framePos)) {
                            break label56;
                        }
                    } else if (i == this.width - 1) {
                        BlockPos framePos = blockpos.relative(this.rightDir);
                        if (!isPortalFrame(this.world, framePos)) {
                            break label56;
                        }
                    }
                }
            }

            for (int j = 0; j < this.width; ++j) {
                BlockPos framePos = this.bottomLeft.relative(this.rightDir, j).above(this.height);
                if (!isPortalFrame(this.world, framePos)) {
                    this.height = 0;
                    break;
                }
            }

            if (this.height <= 21 && this.height >= 1) {
                return this.height;
            } else {
                this.bottomLeft = null;
                this.width = 0;
                this.height = 0;
                return 0;
            }
        }

        protected boolean canReplace(BlockState pos) {
            Block block = pos.getBlock();
            return pos.isAir() || block == Blocks.FIRE || block instanceof PortalBlock;
        }

        public boolean isValid() {
            return this.bottomLeft != null && this.width >= 1 && this.width <= 21 && this.height >= 1 && this.height <= 21;
        }

        public void placePortalBlocks(WarpScrollData data, String displayName) {
            for (int i = 0; i < this.width; ++i) {
                BlockPos blockpos = this.bottomLeft.relative(this.rightDir, i);

                for (int j = 0; j < this.height; ++j) {
                    this.world.setBlock(blockpos.above(j), BlockRegistry.PORTAL_BLOCK.defaultBlockState().setValue(PortalBlock.AXIS, this.axis), 18);
                    if (this.world.getBlockEntity(blockpos.above(j)) instanceof PortalTile tile) {
                        tile.setFromScroll(data);
                        tile.displayName = displayName;
                        tile.updateBlock();
                    }
                }
            }

        }

        private boolean rightSize() {
            return this.portalBlockCount >= this.width * this.height;
        }

        public boolean isComplete() {
            return this.isValid() && this.rightSize();
        }
    }
}
