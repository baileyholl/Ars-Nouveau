package com.hollingsworth.arsnouveau.common.world.tree;

import com.google.common.collect.Lists;
import net.minecraft.block.BlockState;
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
                addRoots(world, rand, pos.west().up(i), posSet, boundingBox, baseTreeFeatureConfig);
                addRoots(world, rand, pos.south().south().up(i), posSet, boundingBox,baseTreeFeatureConfig);
                addRoots(world, rand, pos.south().west().up(i), posSet, boundingBox, baseTreeFeatureConfig);
                addRoots(world, rand, pos.south().south().east().up(i), posSet, boundingBox, baseTreeFeatureConfig);
                addRoots(world, rand, pos.east().east().up(i), posSet, boundingBox, baseTreeFeatureConfig);
                addRoots(world, rand, pos.east().east().south().up(i), posSet, boundingBox, baseTreeFeatureConfig);
                addRoots(world, rand, pos.east().north().up(i), posSet, boundingBox, baseTreeFeatureConfig);
                addRoots(world, rand, pos.north().up(i), posSet, boundingBox, baseTreeFeatureConfig);
                addRoots(world, rand, pos.west().north(), posSet, boundingBox, baseTreeFeatureConfig);
                addRoots(world, rand, pos.east().east().north(), posSet, boundingBox, baseTreeFeatureConfig);
                addRoots(world, rand, pos.south().south().west(), posSet, boundingBox, baseTreeFeatureConfig);
                addRoots(world, rand, pos.south().south().east().east(), posSet, boundingBox, baseTreeFeatureConfig);

                //addRoots(world, rand, pos.north().west(), posSet, boundingBox, Blocks.DARK_OAK_LOG.getDefaultState());
            }

            if(i > 1 && i > lastBranch){
                if(northB){
                    addBranch(world, pos, posSet, boundingBox, i, Direction.NORTH,rand, baseTreeFeatureConfig);
                    lastBranch = i;
                    numBranches++;
                    northB = false;
                }else if(southB){
                    addBranch(world, pos.offset(Direction.SOUTH), posSet, boundingBox, i, Direction.SOUTH,rand, baseTreeFeatureConfig);
                    lastBranch = i;
                    numBranches++;
                    southB = false;
                }else if(eastB){
                    addBranch(world, pos.offset(Direction.EAST).south(), posSet, boundingBox, i, Direction.EAST, rand,baseTreeFeatureConfig);
                    lastBranch = i;
                    numBranches++;
                    eastB = false;
                }else if(westB){
                    addBranch(world, pos, posSet, boundingBox, i, Direction.WEST, rand,baseTreeFeatureConfig);
                    lastBranch = i;
                    numBranches++;
                    westB = false;
                }else if(numBranches == 0){
                    addBranch(world, pos, posSet, boundingBox, i, Direction.NORTH, rand, baseTreeFeatureConfig);
                    lastBranch = i;
                    numBranches++;
                    northB = false;
                }

            }

            if(i == foliageHeight - 2){
                float leafChance = .1f;
                //Bell top
                addLineLeaves(world, pos.north(4).up(i), posSet, boundingBox, Direction.NORTH, 6,rand, baseTreeFeatureConfig, leafChance);
                addLineLeaves(world, pos.north(4).up(i + 1), posSet, boundingBox, Direction.NORTH, 6,rand, baseTreeFeatureConfig, leafChance);

                addLineLeaves(world, pos.north(3).up(i - 1), posSet, boundingBox, Direction.NORTH, 6,rand, baseTreeFeatureConfig, leafChance);

                addLineLeaves(world, pos.north(3).up(i), posSet, boundingBox, Direction.NORTH, 6,rand, baseTreeFeatureConfig);
                addLineLeaves(world, pos.north(3).up(i + 1), posSet, boundingBox, Direction.NORTH, 6,rand, baseTreeFeatureConfig);
                addLineLeaves(world, pos.north(2).up(i + 1), posSet, boundingBox, Direction.NORTH, 6,rand, baseTreeFeatureConfig);
                addLineLeaves(world, pos.north(1).up(i + 1), posSet, boundingBox, Direction.NORTH, 6,rand, baseTreeFeatureConfig);

                addLineLeaves(world, pos.north(2).up(i + 2), posSet, boundingBox, Direction.NORTH, 4,rand, baseTreeFeatureConfig);

                addLineLeaves(world, pos.east(5).up(i), posSet, boundingBox, Direction.EAST, 6, rand,baseTreeFeatureConfig, leafChance);
                addLineLeaves(world, pos.east(5).up(i + 1), posSet, boundingBox, Direction.EAST, 6,rand, baseTreeFeatureConfig, leafChance);
                addLineLeaves(world, pos.east(4).up(i - 1), posSet, boundingBox, Direction.EAST, 6, rand,baseTreeFeatureConfig, leafChance);

                addLineLeaves(world, pos.east(4).up(i), posSet, boundingBox, Direction.EAST, 6, rand,baseTreeFeatureConfig);
                addLineLeaves(world, pos.east(4).up(i + 1), posSet, boundingBox, Direction.EAST, 6,rand, baseTreeFeatureConfig);
                addLineLeaves(world, pos.east(3).up(i + 1), posSet, boundingBox, Direction.EAST, 6,rand, baseTreeFeatureConfig);
                addLineLeaves(world, pos.east(2).up(i + 1), posSet, boundingBox, Direction.EAST, 6,rand, baseTreeFeatureConfig);

                addLineLeaves(world, pos.east(3).up(i + 2), posSet, boundingBox, Direction.EAST, 4,rand, baseTreeFeatureConfig);
                addLineLeaves(world, pos.east(2).up(i + 2), posSet, boundingBox, Direction.EAST, 4,rand, baseTreeFeatureConfig);
                addLineLeaves(world, pos.east(1).up(i + 2), posSet, boundingBox, Direction.EAST, 4, rand,baseTreeFeatureConfig);
                addLineLeaves(world, pos.east(0).up(i + 2), posSet, boundingBox, Direction.EAST, 4,rand, baseTreeFeatureConfig);
                addLineLeaves(world, pos.east(-1).up(i + 2), posSet, boundingBox, Direction.EAST, 4,rand, baseTreeFeatureConfig);
                addLineLeaves(world, pos.east(-2).up(i + 2), posSet, boundingBox, Direction.EAST, 4,rand, baseTreeFeatureConfig);

                addLineLeaves(world, pos.west(4).south().up(i), posSet, boundingBox, Direction.WEST, 6,rand, baseTreeFeatureConfig, leafChance);
                addLineLeaves(world, pos.west(4).south().up(i + 1), posSet, boundingBox, Direction.WEST, 6,rand, baseTreeFeatureConfig, leafChance);

                addLineLeaves(world, pos.west(3).south().up(i - 1), posSet, boundingBox, Direction.WEST, 6,rand, baseTreeFeatureConfig, leafChance);
                addLineLeaves(world, pos.west(3).south().up(i), posSet, boundingBox, Direction.WEST, 6,rand, baseTreeFeatureConfig);
                addLineLeaves(world, pos.west(3).south().up(i + 1), posSet, boundingBox, Direction.WEST, 6,rand, baseTreeFeatureConfig);

                addLineLeaves(world, pos.west(2).south().up(i + 1), posSet, boundingBox, Direction.WEST, 6,rand, baseTreeFeatureConfig);
                addLineLeaves(world, pos.west(1).south().up(i + 1), posSet, boundingBox, Direction.WEST, 6,rand, baseTreeFeatureConfig);

                addLineLeaves(world, pos.west(2).south().up(i + 2), posSet, boundingBox, Direction.WEST, 4,rand, baseTreeFeatureConfig);

                // layers 1-2
                addLineLeaves(world, pos.south(4).east().up(i), posSet, boundingBox, Direction.SOUTH, 6,rand, baseTreeFeatureConfig);
                addLineLeaves(world, pos.south(4).east().up(i + 1), posSet, boundingBox, Direction.SOUTH, 6,rand, baseTreeFeatureConfig);

                addLineLeaves(world, pos.south(5).east().up(i), posSet, boundingBox, Direction.SOUTH, 6,rand, baseTreeFeatureConfig, leafChance);
                addLineLeaves(world, pos.south(5).east().up(i + 1), posSet, boundingBox, Direction.SOUTH, 6,rand, baseTreeFeatureConfig, leafChance);


                addLineLeaves(world, pos.south(3).east().up(i + 1), posSet, boundingBox, Direction.SOUTH, 6,rand, baseTreeFeatureConfig);
                addLineLeaves(world, pos.south(2).east().up(i + 1), posSet, boundingBox, Direction.SOUTH, 6,rand, baseTreeFeatureConfig);
                // layer 3
                addLineLeaves(world, pos.south(3).east().up(i + 2), posSet, boundingBox, Direction.SOUTH, 4,rand, baseTreeFeatureConfig);


                addLineLeaves(world, pos.east(2).up(i + 3), posSet, boundingBox, Direction.EAST, 4,rand, baseTreeFeatureConfig);
                addLineLeaves(world, pos.east(1).up(i + 3), posSet, boundingBox, Direction.EAST, 4,rand, baseTreeFeatureConfig);
                addLineLeaves(world, pos.east(0).up(i + 3), posSet, boundingBox, Direction.EAST, 4,rand, baseTreeFeatureConfig);
                addLineLeaves(world, pos.east(-1).up(i + 3), posSet, boundingBox, Direction.EAST, 4,rand, baseTreeFeatureConfig);



                addLineLeaves(world, pos.east(2).up(i + 4), posSet, boundingBox, Direction.EAST, 4,rand, baseTreeFeatureConfig, leafChance);
                addLineLeaves(world, pos.east(1).up(i + 4), posSet, boundingBox, Direction.EAST, 4,rand, baseTreeFeatureConfig, leafChance);
                addLineLeaves(world, pos.east(0).up(i + 4), posSet, boundingBox, Direction.EAST, 4,rand, baseTreeFeatureConfig, leafChance);
                addLineLeaves(world, pos.east(-1).up(i + 4), posSet, boundingBox, Direction.EAST, 4,rand, baseTreeFeatureConfig, leafChance);


            }
        }

        list.add(new FoliagePlacer.Foliage(new BlockPos(xOffset, yOffset, zOffset), 0, true));
        return list;
    }


    public void addBranch(IWorldGenerationReader world,BlockPos pos, Set<BlockPos> posSet, MutableBoundingBox boundingBox,int height, Direction d, Random random, BaseTreeFeatureConfig baseTreeFeatureConfig){
        pos = pos.up(height);
        addLog(world, pos.offset(d), posSet, boundingBox, random, baseTreeFeatureConfig);
        addLog(world, pos.offset(d).up(1), posSet, boundingBox, random, baseTreeFeatureConfig);
        addLog(world, pos.offset(d).up(2), posSet, boundingBox, random, baseTreeFeatureConfig);
        addLog(world, pos.offset(d, 2).up(2), posSet, boundingBox,random, baseTreeFeatureConfig );
        addLog(world, pos.offset(d, 3).up(2), posSet, boundingBox, random, baseTreeFeatureConfig);
        addLog(world, pos.offset(d, 3).up(1), posSet, boundingBox, random, baseTreeFeatureConfig);


        addLineLeaves(world, pos.offset(d).up(1), posSet, boundingBox, d, 3,random, baseTreeFeatureConfig);
        addLineLeaves(world, pos.offset(d).up(2), posSet, boundingBox, d, 3,random, baseTreeFeatureConfig);
        addLineLeaves(world, pos.offset(d).up(3), posSet, boundingBox, d, 3, random,baseTreeFeatureConfig);

        for(int j =1; j < 4; j++){
            addLineLeaves(world, pos.offset(d, j).up(3), posSet, boundingBox, d, 3, random,baseTreeFeatureConfig);
            addLineLeaves(world, pos.offset(d, j).up(2), posSet, boundingBox, d, 3,random, baseTreeFeatureConfig);

            addLineLeaves(world, pos.offset(d, j).up(4), posSet, boundingBox, d, 3, random,baseTreeFeatureConfig, .1f);
           // addLineLeaves(world, pos.offset(d, j).up(2), posSet, boundingBox, d, 3,random, baseTreeFeatureConfig);

        }
//        for(int i = 2; i < 5; i++){
//            for(int j = 1; j <= 2; j++){
//                addHollowLine(world, pos.offset(d, i).up(j), posSet, boundingBox, d, 2,random, baseTreeFeatureConfig);
//            }
//
//        }
        for(int i = 0; i < 2; i++){
            addHollowLine(world, pos.offset(d, 2 + i).up(1), posSet, boundingBox, d, 2,random, baseTreeFeatureConfig);
            addHollowLine(world, pos.offset(d, 2 + i).up(2), posSet, boundingBox, d, 2,random, baseTreeFeatureConfig);
            addHollowLine(world, pos.offset(d, 2 + i).up(1), posSet, boundingBox, d, 3,random, baseTreeFeatureConfig, 0.1f);
            addHollowLine(world, pos.offset(d, 2 + i).up(2), posSet, boundingBox, d, 3,random, baseTreeFeatureConfig, 0.1f);
        }
//        addHollowLine(world, pos.offset(d, 2).up(2), posSet, boundingBox, d, 2,random, baseTreeFeatureConfig);
//        addHollowLine(world, pos.offset(d, 2).up(1), posSet, boundingBox, d, 2,random, baseTreeFeatureConfig);
//
//        addHollowLine(world, pos.offset(d, 3).up(2), posSet, boundingBox, d, 2,random, baseTreeFeatureConfig);
//        addHollowLine(world, pos.offset(d, 3).up(1), posSet, boundingBox, d, 2,random, baseTreeFeatureConfig);
//

        addLineLeaves(world, pos.offset(d, 4).up(1), posSet, boundingBox, d, 3,random, baseTreeFeatureConfig);
        addLineLeaves(world, pos.offset(d, 4).up(2), posSet, boundingBox, d, 3,random, baseTreeFeatureConfig);


        addLineLeaves(world, pos.offset(d, 5).up(1), posSet, boundingBox, d, 3,random, baseTreeFeatureConfig, 0.1f);
        addLineLeaves(world, pos.offset(d, 5).up(2), posSet, boundingBox, d, 3,random, baseTreeFeatureConfig, 0.1f);

    }

    public boolean addLog(IWorldGenerationReader world,BlockPos pos, Set<BlockPos> posSet, MutableBoundingBox mutableBoundingBox, Random random, BaseTreeFeatureConfig baseTreeFeatureConfig){
        return addBlock(world, pos, posSet, mutableBoundingBox, baseTreeFeatureConfig.trunkProvider.getBlockState(random, pos));
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
    public void addHollowLine(IWorldGenerationReader world, BlockPos pos, Set<BlockPos> posSet, MutableBoundingBox mutableBoundingBox, Direction d, int length,Random rand, BaseTreeFeatureConfig baseTreeFeatureConfig){
        addHollowLine(world, pos, posSet, mutableBoundingBox, d, length, rand, baseTreeFeatureConfig, 1.0f);
    }

    public void addHollowLine(IWorldGenerationReader world, BlockPos pos, Set<BlockPos> posSet, MutableBoundingBox mutableBoundingBox, Direction d, int length,Random rand, BaseTreeFeatureConfig baseTreeFeatureConfig, float chance){
        Direction left = d.rotateY();
        Direction right = left.getOpposite();

        if (rand.nextFloat() <= chance && TreeFeature.isReplaceableAt(world, pos.offset(left, length))) {
            func_236913_a_(world,  pos.offset(left, length), baseTreeFeatureConfig.leavesProvider.getBlockState(rand, pos.offset(left, length)), mutableBoundingBox);
            posSet.add( pos.offset(left, length).toImmutable());

        }
        if (rand.nextFloat() <= chance && TreeFeature.isReplaceableAt(world, pos.offset(right, length))) {
            func_236913_a_(world,  pos.offset(right, length), baseTreeFeatureConfig.leavesProvider.getBlockState(rand, pos.offset(right, length)), mutableBoundingBox);
            posSet.add( pos.offset(right, length).toImmutable());

        }
    }
    public void addLineLeaves(IWorldGenerationReader world, BlockPos pos, Set<BlockPos> posSet, MutableBoundingBox mutableBoundingBox, Direction d, int length,Random rand, BaseTreeFeatureConfig baseTreeFeatureConfig){
        if(length % 2 == 0)
            addLineLeavesEven(world, pos, posSet, mutableBoundingBox, d, length, rand, baseTreeFeatureConfig, 1.0f);
        else
            addLineLeavesOdd(world, pos, posSet, mutableBoundingBox, d, length, rand,baseTreeFeatureConfig, 1.0f);
    }

    public void addLineLeaves(IWorldGenerationReader world, BlockPos pos, Set<BlockPos> posSet, MutableBoundingBox mutableBoundingBox, Direction d, int length,Random rand, BaseTreeFeatureConfig baseTreeFeatureConfig, float chance){
        if(length % 2 == 0)
            addLineLeavesEven(world, pos, posSet, mutableBoundingBox, d, length, rand, baseTreeFeatureConfig, chance);
        else
            addLineLeavesOdd(world, pos, posSet, mutableBoundingBox, d, length, rand,baseTreeFeatureConfig, chance);
    }

    public void addLineLeavesEven(IWorldGenerationReader world, BlockPos pos, Set<BlockPos> posSet, MutableBoundingBox mutableBoundingBox, Direction d, int length, Random rand, BaseTreeFeatureConfig baseTreeFeatureConfig, float chance){
        Direction left = d.rotateY();
        Direction right = left.getOpposite();

        for(int i = 0; i < length; i++){
            if (rand.nextFloat() <= chance && TreeFeature.isReplaceableAt(world, pos.offset(left, i - length/3))) {
                func_236913_a_(world,  pos.offset(left, i- length/3), baseTreeFeatureConfig.leavesProvider.getBlockState(rand, pos.offset(left, i- length/3)), mutableBoundingBox);
                posSet.add( pos.offset(left, i- length/3).toImmutable());

            }
        }
    }

    public void addLineLeavesOdd(IWorldGenerationReader world, BlockPos pos, Set<BlockPos> posSet, MutableBoundingBox mutableBoundingBox, Direction d, int length,Random rand, BaseTreeFeatureConfig baseTreeFeatureConfig, float chance){
        Direction left = d.rotateY();
        Direction right = left.getOpposite();
        length += 2;
        for(int i = 0; i < (length - 1) / 2; i++){
            if (rand.nextFloat() <= chance && TreeFeature.isReplaceableAt(world, pos.offset(left, i))) {
                func_236913_a_(world,  pos.offset(left, i), baseTreeFeatureConfig.leavesProvider.getBlockState(rand, pos.offset(left, i)), mutableBoundingBox);
                posSet.add( pos.offset(left, i).toImmutable());
            }

            if (rand.nextFloat() <= chance && TreeFeature.isReplaceableAt(world, pos.offset(right, i))) {
                func_236913_a_(world,  pos.offset(right, i), baseTreeFeatureConfig.leavesProvider.getBlockState(rand, pos.offset(right, i)), mutableBoundingBox);
                posSet.add( pos.offset(right, i).toImmutable());
            }
        }
    }


    public boolean addRoots(IWorldGenerationReader world, Random rand, BlockPos pos, Set<BlockPos> posSet, MutableBoundingBox mutableBoundingBox, BaseTreeFeatureConfig baseTreeFeatureConfig) {
        BlockState state = baseTreeFeatureConfig.trunkProvider.getBlockState(rand,pos);
        if (rand.nextDouble() < 0.75 && TreeFeature.isReplaceableAt(world, pos)) {
            func_236913_a_(world, pos, state, mutableBoundingBox);
            posSet.add(pos.toImmutable());
            return true;
        } else {
            return false;
        }
    }
}
