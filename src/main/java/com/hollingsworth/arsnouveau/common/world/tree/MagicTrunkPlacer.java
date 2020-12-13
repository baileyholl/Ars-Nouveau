package com.hollingsworth.arsnouveau.common.world.tree;

import com.google.common.collect.Lists;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.foliageplacer.FoliagePlacer;
import net.minecraft.world.gen.trunkplacer.AbstractTrunkPlacer;
import net.minecraft.world.gen.trunkplacer.TrunkPlacerType;

import java.util.List;
import java.util.Random;
import java.util.Set;

public class MagicTrunkPlacer extends AbstractTrunkPlacer {
    public MagicTrunkPlacer(int baseHeight, int height_rand_a, int height_rand_b) {
        super(baseHeight, height_rand_a, height_rand_b);
    }

    @Override
    protected TrunkPlacerType<?> func_230381_a_() {
        return TrunkPlacerType.DARK_OAK_TRUNK_PLACER;
    }

    @Override
    public List<FoliagePlacer.Foliage> func_230382_a_(IWorldGenerationReader world, Random rand, int foliageHeight, BlockPos pos, Set<BlockPos> posSet,
                                                      MutableBoundingBox boundingBox, BaseTreeFeatureConfig baseTreeFeatureConfig) {
        List<FoliagePlacer.Foliage> list = Lists.newArrayList();
        BlockPos blockpos = pos.down();
        func_236909_a_(world, blockpos);
        func_236909_a_(world, blockpos.east());
        func_236909_a_(world, blockpos.south());
        func_236909_a_(world, blockpos.south().east());
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        int xOffset = x;
        int zOffset = z;
        int yOffset = y + foliageHeight - 1;

        int numBranches = 0;
        int lastBranch = 0;
        boolean northB = rand.nextFloat() >= 0.5;
        boolean southB = rand.nextFloat() >= 0.5;
        boolean eastB = rand.nextFloat() >= 0.5;
        boolean westB = rand.nextFloat() >= 0.5;

        for(int i = 0; i < foliageHeight; ++i) {
            int j2 = y + i;
            BlockPos blockpos1 = new BlockPos(xOffset, j2, zOffset);
            if (TreeFeature.isAirOrLeavesAt(world, blockpos1)) {
                func_236911_a_(world, rand, blockpos1, posSet, boundingBox, baseTreeFeatureConfig);
                func_236911_a_(world, rand, blockpos1.east(), posSet, boundingBox, baseTreeFeatureConfig);
                func_236911_a_(world, rand, blockpos1.south(), posSet, boundingBox, baseTreeFeatureConfig);
                func_236911_a_(world, rand, blockpos1.east().south(), posSet, boundingBox, baseTreeFeatureConfig);
            }

            if(i < 1){
                addRoots(world, rand, pos.west().up(i), posSet, boundingBox, Blocks.DARK_OAK_LOG.getDefaultState());
                addRoots(world, rand, pos.south().south().up(i), posSet, boundingBox, Blocks.DARK_OAK_LOG.getDefaultState());
                addRoots(world, rand, pos.south().west().up(i), posSet, boundingBox, Blocks.DARK_OAK_LOG.getDefaultState());
                addRoots(world, rand, pos.south().south().east().up(i), posSet, boundingBox, Blocks.DARK_OAK_LOG.getDefaultState());
                addRoots(world, rand, pos.east().east().up(i), posSet, boundingBox, Blocks.DARK_OAK_LOG.getDefaultState());
                addRoots(world, rand, pos.east().east().south().up(i), posSet, boundingBox, Blocks.DARK_OAK_LOG.getDefaultState());
                addRoots(world, rand, pos.east().north().up(i), posSet, boundingBox, Blocks.DARK_OAK_LOG.getDefaultState());
                addRoots(world, rand, pos.north().up(i), posSet, boundingBox, Blocks.DARK_OAK_LOG.getDefaultState());
                addRoots(world, rand, pos.west().north(), posSet, boundingBox, Blocks.DARK_OAK_LOG.getDefaultState());
                addRoots(world, rand, pos.east().east().north(), posSet, boundingBox, Blocks.DARK_OAK_LOG.getDefaultState());
                addRoots(world, rand, pos.south().south().west(), posSet, boundingBox, Blocks.DARK_OAK_LOG.getDefaultState());
                addRoots(world, rand, pos.south().south().east().east(), posSet, boundingBox, Blocks.DARK_OAK_LOG.getDefaultState());

                //addRoots(world, rand, pos.north().west(), posSet, boundingBox, Blocks.DARK_OAK_LOG.getDefaultState());
            }

            if(i > 1 && i > lastBranch){
                if(northB){
                    addBranch(world, pos, posSet, boundingBox, i, Direction.NORTH);
                    lastBranch = i;
                    numBranches++;
                    northB = false;
                }else if(southB){
                    addBranch(world, pos.offset(Direction.SOUTH), posSet, boundingBox, i, Direction.SOUTH);
                    lastBranch = i;
                    numBranches++;
                    southB = false;
                }else if(eastB){
                    addBranch(world, pos.offset(Direction.EAST).south(), posSet, boundingBox, i, Direction.EAST);
                    lastBranch = i;
                    numBranches++;
                    eastB = false;
                }else if(westB){
                    addBranch(world, pos, posSet, boundingBox, i, Direction.WEST);
                    lastBranch = i;
                    numBranches++;
                    westB = false;
                }else if(numBranches == 0){
                    addBranch(world, pos, posSet, boundingBox, i, Direction.NORTH);
                    lastBranch = i;
                    numBranches++;
                    northB = false;
                }

            }
            // Add branch
//            if(i == 4)
//                addBranch(world, pos.offset(Direction.EAST).south(), posSet, boundingBox, 4, Direction.EAST);
//
//            if(i == 3)
//                addBranch(world, pos.offset(Direction.SOUTH), posSet, boundingBox, 3, Direction.SOUTH);
//
//            if(i == 2)
//                addBranch(world, pos, posSet, boundingBox, 2, Direction.NORTH);
//
//            if(i == 4)
//                addBranch(world, pos, posSet, boundingBox, 4, Direction.WEST);


            if(i == foliageHeight - 2){
                //Bell top
                addLineLeaves(world, pos.north(3).up(i), posSet, boundingBox, Direction.NORTH, 6);
                addLineLeaves(world, pos.north(3).up(i + 1), posSet, boundingBox, Direction.NORTH, 6);
                addLineLeaves(world, pos.north(2).up(i + 1), posSet, boundingBox, Direction.NORTH, 6);
                addLineLeaves(world, pos.north(1).up(i + 1), posSet, boundingBox, Direction.NORTH, 6);

                addLineLeaves(world, pos.north(2).up(i + 2), posSet, boundingBox, Direction.NORTH, 4);

                addLineLeaves(world, pos.east(4).up(i), posSet, boundingBox, Direction.EAST, 6);
                addLineLeaves(world, pos.east(4).up(i + 1), posSet, boundingBox, Direction.EAST, 6);
                addLineLeaves(world, pos.east(3).up(i + 1), posSet, boundingBox, Direction.EAST, 6);
                addLineLeaves(world, pos.east(2).up(i + 1), posSet, boundingBox, Direction.EAST, 6);

                addLineLeaves(world, pos.east(3).up(i + 2), posSet, boundingBox, Direction.EAST, 4);
                addLineLeaves(world, pos.east(2).up(i + 2), posSet, boundingBox, Direction.EAST, 4);
                addLineLeaves(world, pos.east(1).up(i + 2), posSet, boundingBox, Direction.EAST, 4);
                addLineLeaves(world, pos.east(0).up(i + 2), posSet, boundingBox, Direction.EAST, 4);
                addLineLeaves(world, pos.east(-1).up(i + 2), posSet, boundingBox, Direction.EAST, 4);
                addLineLeaves(world, pos.east(-2).up(i + 2), posSet, boundingBox, Direction.EAST, 4);



                addLineLeaves(world, pos.east(2).up(i + 3), posSet, boundingBox, Direction.EAST, 4);
                addLineLeaves(world, pos.east(1).up(i + 3), posSet, boundingBox, Direction.EAST, 4);
                addLineLeaves(world, pos.east(0).up(i + 3), posSet, boundingBox, Direction.EAST, 4);
                addLineLeaves(world, pos.east(-1).up(i + 3), posSet, boundingBox, Direction.EAST, 4);

                addLineLeaves(world, pos.west(3).south().up(i), posSet, boundingBox, Direction.WEST, 6);
                addLineLeaves(world, pos.west(3).south().up(i + 1), posSet, boundingBox, Direction.WEST, 6);
                addLineLeaves(world, pos.west(2).south().up(i + 1), posSet, boundingBox, Direction.WEST, 6);
                addLineLeaves(world, pos.west(1).south().up(i + 1), posSet, boundingBox, Direction.WEST, 6);

                addLineLeaves(world, pos.west(2).south().up(i + 2), posSet, boundingBox, Direction.WEST, 4);

                // layers 1-2
                addLineLeaves(world, pos.south(4).east().up(i), posSet, boundingBox, Direction.SOUTH, 6);
                addLineLeaves(world, pos.south(4).east().up(i + 1), posSet, boundingBox, Direction.SOUTH, 6);
                addLineLeaves(world, pos.south(3).east().up(i + 1), posSet, boundingBox, Direction.SOUTH, 6);
                addLineLeaves(world, pos.south(2).east().up(i + 1), posSet, boundingBox, Direction.SOUTH, 6);
                // layer 3
                addLineLeaves(world, pos.south(3).east().up(i + 2), posSet, boundingBox, Direction.SOUTH, 4);

            }
        }

        list.add(new FoliagePlacer.Foliage(new BlockPos(xOffset, yOffset, zOffset), 0, true));
        return list;
    }


    public void addBranch(IWorldGenerationReader world,BlockPos pos, Set<BlockPos> posSet, MutableBoundingBox boundingBox,int height, Direction d){

        addBlock(world, pos.offset(d).up(height), posSet, boundingBox, Blocks.DARK_OAK_LOG.getDefaultState());
        addBlock(world, pos.offset(d).up(height + 1), posSet, boundingBox, Blocks.DARK_OAK_LOG.getDefaultState());
        addBlock(world, pos.offset(d).up(height + 2), posSet, boundingBox, Blocks.DARK_OAK_LOG.getDefaultState());
        addBlock(world, pos.offset(d, 2).up(height + 2), posSet, boundingBox, Blocks.DARK_OAK_LOG.getDefaultState());
        addBlock(world, pos.offset(d, 3).up(height + 2), posSet, boundingBox, Blocks.DARK_OAK_LOG.getDefaultState());
        addBlock(world, pos.offset(d, 3).up(height + 1), posSet, boundingBox, Blocks.DARK_OAK_LOG.getDefaultState());


        addLineLeaves(world, pos.offset(d).up(height+ 1), posSet, boundingBox, d, 3);
        addLineLeaves(world, pos.offset(d).up(height+ 2), posSet, boundingBox, d, 3);
        addLineLeaves(world, pos.offset(d).up(height+ 3), posSet, boundingBox, d, 3);

        for(int j =1; j < 4; j++){
            addLineLeaves(world, pos.offset(d, j).up(height+ 3), posSet, boundingBox, d, 3);
            addLineLeaves(world, pos.offset(d, j).up(height+ 2), posSet, boundingBox, d, 3);

        }
        addHollowLine(world, pos.offset(d, 2).up(height+2), posSet, boundingBox, d, 2);
        addHollowLine(world, pos.offset(d, 2).up(height+1), posSet, boundingBox, d, 2);
        addHollowLine(world, pos.offset(d, 3).up(height+2), posSet, boundingBox, d, 2);
        addHollowLine(world, pos.offset(d, 3).up(height+1), posSet, boundingBox, d, 2);

        addLineLeaves(world, pos.offset(d, 4).up(height+1), posSet, boundingBox, d, 3);
        addLineLeaves(world, pos.offset(d, 4).up(height+2), posSet, boundingBox, d, 3);

    }

    public boolean addBlock(IWorldGenerationReader world,BlockPos pos, Set<BlockPos> posSet, MutableBoundingBox mutableBoundingBox, BlockState state){
        if(TreeFeature.isReplaceableAt(world, pos)) {
            func_236913_a_(world, pos, state, mutableBoundingBox);
            posSet.add(pos.toImmutable());
            return true;
        } else {
            return false;
        }
    }

    public void addHollowLine(IWorldGenerationReader world, BlockPos pos, Set<BlockPos> posSet, MutableBoundingBox mutableBoundingBox, Direction d, int length){
        Direction left = d.rotateY();
        Direction right = left.getOpposite();

        if (TreeFeature.isReplaceableAt(world, pos.offset(left, length))) {
            func_236913_a_(world,  pos.offset(left, length), Blocks.OAK_LEAVES.getDefaultState(), mutableBoundingBox);
            posSet.add( pos.offset(left, length).toImmutable());

        }
        if (TreeFeature.isReplaceableAt(world, pos.offset(right, length))) {
            func_236913_a_(world,  pos.offset(right, length), Blocks.OAK_LEAVES.getDefaultState(), mutableBoundingBox);
            posSet.add( pos.offset(right, length).toImmutable());

        }
    }

    public void addLineLeaves(IWorldGenerationReader world, BlockPos pos, Set<BlockPos> posSet, MutableBoundingBox mutableBoundingBox, Direction d, int length){
        if(length % 2 == 0)
            addLineLeavesEven(world, pos, posSet, mutableBoundingBox, d, length);
        else
            addLineLeavesOdd(world, pos, posSet, mutableBoundingBox, d, length);
    }

    public void addLineLeavesEven(IWorldGenerationReader world, BlockPos pos, Set<BlockPos> posSet, MutableBoundingBox mutableBoundingBox, Direction d, int length){
        Direction left = d.rotateY();
        Direction right = left.getOpposite();

        for(int i = 0; i < length; i++){
            if (TreeFeature.isReplaceableAt(world, pos.offset(left, i - length/3))) {
                func_236913_a_(world,  pos.offset(left, i- length/3), Blocks.OAK_LEAVES.getDefaultState(), mutableBoundingBox);
                posSet.add( pos.offset(left, i- length/3).toImmutable());

            }
//            if (TreeFeature.isReplaceableAt(world, pos.offset(right, i +1))) {
//                func_236913_a_(world,  pos.offset(right, i), Blocks.OAK_LEAVES.getDefaultState(), mutableBoundingBox);
//                posSet.add( pos.offset(right, i).toImmutable());
//
//            }
        }
    }

    public void addLineLeavesOdd(IWorldGenerationReader world, BlockPos pos, Set<BlockPos> posSet, MutableBoundingBox mutableBoundingBox, Direction d, int length){
        Direction left = d.rotateY();
        Direction right = left.getOpposite();
        length += 2;
        for(int i = 0; i < (length - 1) / 2; i++){
            if (TreeFeature.isReplaceableAt(world, pos.offset(left, i))) {
                func_236913_a_(world,  pos.offset(left, i), Blocks.OAK_LEAVES.getDefaultState(), mutableBoundingBox);
                posSet.add( pos.offset(left, i).toImmutable());

            }
            if (TreeFeature.isReplaceableAt(world, pos.offset(right, i))) {
                func_236913_a_(world,  pos.offset(right, i), Blocks.OAK_LEAVES.getDefaultState(), mutableBoundingBox);
                posSet.add( pos.offset(right, i).toImmutable());

            }
        }
    }

    public void addLeftRightLeaves(IWorldGenerationReader world, BlockPos pos, Set<BlockPos> posSet, MutableBoundingBox mutableBoundingBox, Direction d){
        BlockPos left = pos.offset(d.rotateY());
        if (TreeFeature.isReplaceableAt(world, left)) {
            func_236913_a_(world, left, Blocks.OAK_LEAVES.getDefaultState(), mutableBoundingBox);
            posSet.add(left.toImmutable());

        }
        BlockPos right = pos.offset(d.rotateY().getOpposite());

        if (TreeFeature.isReplaceableAt(world, right)) {
            func_236913_a_(world, right, Blocks.OAK_LEAVES.getDefaultState(), mutableBoundingBox);
            posSet.add(right.toImmutable());

        }
    }


    public boolean addRoots(IWorldGenerationReader world, Random rand, BlockPos pos, Set<BlockPos> posSet, MutableBoundingBox mutableBoundingBox, BlockState state) {
        if (rand.nextDouble() < 0.75 && TreeFeature.isReplaceableAt(world, pos)) {
            func_236913_a_(world, pos, state, mutableBoundingBox);
            posSet.add(pos.toImmutable());
            return true;
        } else {
            return false;
        }
    }
}
