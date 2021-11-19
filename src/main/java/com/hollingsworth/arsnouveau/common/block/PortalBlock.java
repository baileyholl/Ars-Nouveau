package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.api.util.FlatPortalAreaHelper;
import com.hollingsworth.arsnouveau.common.block.tile.PortalTile;
import com.hollingsworth.arsnouveau.common.datagen.Recipes;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Random;

public class PortalBlock extends ModBlock{
    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 14.0D, 12.0D, 14.0D);
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    public PortalBlock() {
        super(Block.Properties.of(Material.PORTAL).noCollission().strength(-1.0F, 3600000.0F).noDrops(),LibBlockNames.PORTAL);
        this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.X));
    }

    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        for(int i = 0; i < 4; ++i) {
            double d0 = (double)pos.getX() + (double)rand.nextFloat();
            double d1 = (double)pos.getY() + (double)rand.nextFloat();
            double d2 = (double)pos.getZ() + (double)rand.nextFloat();
            double d3 = ((double)rand.nextFloat() - 0.5D) * 0.5D;
            double d4 = ((double)rand.nextFloat() - 0.5D) * 0.5D;
            double d5 = ((double)rand.nextFloat() - 0.5D) * 0.5D;
            int j = rand.nextInt(2) * 2 - 1;
            if (worldIn.getBlockState(pos.west()).getBlock() != this && worldIn.getBlockState(pos.east()).getBlock() != this) {
                d0 = (double)pos.getX() + 0.5D + 0.25D * (double)j;
                d3 = rand.nextFloat() * 2.0F * (float)j;
            } else {
                d2 = (double)pos.getZ() + 0.5D + 0.25D * (double)j;
                d5 = rand.nextFloat() * 2.0F * (float)j;
            }

            worldIn.addParticle(ParticleTypes.PORTAL, d0, d1, d2, d3, d4, d5);
        }

    }

    @Override
    public void onProjectileHit(World worldIn, BlockState state, BlockRayTraceResult hit, ProjectileEntity projectile) {
        if(worldIn.getBlockEntity(hit.getBlockPos()) instanceof PortalTile){
            ((PortalTile) worldIn.getBlockEntity(hit.getBlockPos())).warp(projectile);
        }
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public void entityInside(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
        if(worldIn.getBlockEntity(pos) instanceof PortalTile && !(entityIn instanceof PlayerEntity)){
            ((PortalTile) worldIn.getBlockEntity(pos)).warp(entityIn);
            entityIn.fallDistance = 0;
        }
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new PortalTile();
    }


    public boolean trySpawnPortal(IWorld worldIn, BlockPos pos, BlockPos warpPos, String dimID, Vector2f rotation, String displayName) {
        Size portalblock$size = this.isPortal(worldIn, pos);
        if (portalblock$size != null) {
            portalblock$size.placePortalBlocks(warpPos, dimID, rotation, displayName);
            return true;
        } else {
            return false;
        }
    }

    public boolean trySpawnHoriztonalPortal(World worldIn, BlockPos pos, BlockPos warpPos, String dimID, Vector2f rotation, String displayName){
        FlatPortalAreaHelper helper = new FlatPortalAreaHelper().init(worldIn, pos, null, (bs) -> bs.is(Recipes.DECORATIVE_AN));
        if(helper.isValidFrame()){
            BlockPos.betweenClosed(helper.lowerCorner, helper.lowerCorner.relative(Direction.Axis.X, helper.xSize - 1).relative(Direction.Axis.Z, helper.zSize - 1)).forEach((blockPos) -> {
                worldIn.setBlock(blockPos, BlockRegistry.PORTAL_BLOCK.defaultBlockState().setValue(PortalBlock.AXIS, Direction.Axis.X), 18);
                if(worldIn.getBlockEntity(blockPos) instanceof PortalTile){
                    PortalTile tile = (PortalTile) worldIn.getBlockEntity(blockPos);
                    tile.warpPos = warpPos;
                    tile.dimID = dimID;
                    tile.rotationVec = rotation;
                    tile.displayName = displayName;
                    tile.isHorizontal = true;
                    tile.update();
                }
            });
            return true;
        }
        return false;
    }

    public BlockState rotate(BlockState state, Rotation rot) {
        switch(rot) {
            case COUNTERCLOCKWISE_90:
            case CLOCKWISE_90:
                switch(state.getValue(AXIS)) {
                    case Z:
                        return state.setValue(AXIS, Direction.Axis.X);
                    case X:
                        return state.setValue(AXIS, Direction.Axis.Z);
                    default:
                        return state;
                }
            default:
                return state;
        }
    }

    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    /**
     * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
     * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
     * returns its solidified counterpart.
     * Note that this method should ideally consider only the specific face passed in.
     */
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        Direction.Axis direction$axis = facing.getAxis();
        Direction.Axis direction$axis1 = stateIn.getValue(AXIS);
        boolean flag = direction$axis1 != direction$axis && direction$axis.isHorizontal();
        if(worldIn.getBlockEntity(currentPos) instanceof PortalTile){
            if(((PortalTile) worldIn.getBlockEntity(currentPos)).isHorizontal){
                FlatPortalAreaHelper frameTester = new FlatPortalAreaHelper();
                frameTester.init((World) worldIn, currentPos, null, (bs) -> bs.is(Recipes.DECORATIVE_AN));
                if(!frameTester.isValidFrame()){
                    return Blocks.AIR.defaultBlockState();
                }
                return  super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
            }
        }
        return !flag && facingState.getBlock() != this && !(new Size(worldIn, currentPos, direction$axis1)).isComplete() ? Blocks.AIR.defaultBlockState() : super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
    }

    @Nullable
    public Size isPortal(IWorld worldIn, BlockPos pos) {
        Size portalblock$size = new Size(worldIn, pos, Direction.Axis.X);
        if (portalblock$size.isValid() && portalblock$size.portalBlockCount == 0) {
            return portalblock$size;
        } else {
            Size portalblock$size1 = new Size(worldIn, pos, Direction.Axis.Z);
            return portalblock$size1.isValid() && portalblock$size1.portalBlockCount == 0 ? portalblock$size1 : null;
        }
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockItemUseContext useContext) {
        return false;
    }

    public static class Size {
        private final IWorld world;
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
        public Size(IWorld worldIn, BlockPos pos, Direction.Axis axisIn) {
            this.world = worldIn;
            this.axis = axisIn;
            if (axisIn == Direction.Axis.X) {
                this.leftDir = Direction.EAST;
                this.rightDir = Direction.WEST;
            } else {
                this.leftDir = Direction.NORTH;
                this.rightDir = Direction.SOUTH;
            }

            for(BlockPos blockpos = pos; pos.getY() > blockpos.getY() - 21 && pos.getY() > 0 && this.canReplace(worldIn.getBlockState(pos.below())); pos = pos.below()) {
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
            for(i = 0; i < 22; ++i) {
                BlockPos blockpos = pos.relative(directionIn, i);
                if (!this.canReplace(this.world.getBlockState(blockpos)) || !isPortalFrame(this.world, blockpos.below())) {
                    break;
                }
            }

            BlockPos framePos = pos.relative(directionIn, i);
            return isPortalFrame(this.world, framePos) ? i : 0;
        }

        public boolean isPortalFrame(IWorld world, BlockPos pos){
            return world.getBlockState(pos).getBlock().is(Recipes.DECORATIVE_AN);
        }

        public int getHeight() {
            return this.height;
        }

        public int getWidth() {
            return this.width;
        }

        protected int calculatePortalHeight() {
            label56:
            for(this.height = 0; this.height < 21; ++this.height) {
                for(int i = 0; i < this.width; ++i) {
                    BlockPos blockpos = this.bottomLeft.relative(this.rightDir, i).above(this.height);
                    BlockState blockstate = this.world.getBlockState(blockpos);
                    if (!this.canReplace(blockstate)) {
                        break label56;
                    }

                    Block block = blockstate.getBlock();
                    if (block == BlockRegistry.PORTAL_BLOCK) {
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

            for(int j = 0; j < this.width; ++j) {
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
            return pos.isAir() || block == Blocks.FIRE || block == BlockRegistry.PORTAL_BLOCK;
        }

        public boolean isValid() {
            return this.bottomLeft != null && this.width >= 1 && this.width <= 21 && this.height >= 1 && this.height <= 21;
        }

        public void placePortalBlocks(BlockPos warpPos, String dimId, Vector2f rotation, String displayName) {
            for(int i = 0; i < this.width; ++i) {
                BlockPos blockpos = this.bottomLeft.relative(this.rightDir, i);

                for(int j = 0; j < this.height; ++j) {
                    this.world.setBlock(blockpos.above(j), BlockRegistry.PORTAL_BLOCK.defaultBlockState().setValue(PortalBlock.AXIS, this.axis), 18);
                    if(this.world.getBlockEntity(blockpos.above(j)) instanceof PortalTile){
                        PortalTile tile = (PortalTile) this.world.getBlockEntity(blockpos.above(j));
                        tile.warpPos = warpPos;
                        tile.dimID = dimId;
                        tile.rotationVec = rotation;
                        tile.displayName = displayName;
                        tile.update();
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
