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
    protected TrunkPlacerType<?> type() {
        return TrunkPlacerType.DARK_OAK_TRUNK_PLACER;
    }

    @Override
    public List<FoliagePlacer.Foliage> placeTrunk(IWorldGenerationReader world, Random rand, int foliageHeight, BlockPos pos, Set<BlockPos> posSet,
                                                      MutableBoundingBox boundingBox, BaseTreeFeatureConfig baseTreeFeatureConfig) {
        List<FoliagePlacer.Foliage> list = Lists.newArrayList();
        BlockPos blockpos = pos.below();
        setDirtAt(world, blockpos);
        setDirtAt(world, blockpos.east());
        setDirtAt(world, blockpos.south());
        setDirtAt(world, blockpos.south().east());
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
            if (TreeFeature.isAirOrLeaves(world, blockpos1)) {
                placeLog(world, rand, blockpos1, posSet, boundingBox, baseTreeFeatureConfig);
                placeLog(world, rand, blockpos1.east(), posSet, boundingBox, baseTreeFeatureConfig);
                placeLog(world, rand, blockpos1.south(), posSet, boundingBox, baseTreeFeatureConfig);
                placeLog(world, rand, blockpos1.east().south(), posSet, boundingBox, baseTreeFeatureConfig);
            }

            if(i < 1){
                addRoots(world, rand, pos.west().above(i), posSet, boundingBox, baseTreeFeatureConfig);
                addRoots(world, rand, pos.south().south().above(i), posSet, boundingBox,baseTreeFeatureConfig);
                addRoots(world, rand, pos.south().west().above(i), posSet, boundingBox, baseTreeFeatureConfig);
                addRoots(world, rand, pos.south().south().east().above(i), posSet, boundingBox, baseTreeFeatureConfig);
                addRoots(world, rand, pos.east().east().above(i), posSet, boundingBox, baseTreeFeatureConfig);
                addRoots(world, rand, pos.east().east().south().above(i), posSet, boundingBox, baseTreeFeatureConfig);
                addRoots(world, rand, pos.east().north().above(i), posSet, boundingBox, baseTreeFeatureConfig);
                addRoots(world, rand, pos.north().above(i), posSet, boundingBox, baseTreeFeatureConfig);
            }

            if(i > 1 && i > lastBranch){
                if(northB){
                    addBranch(world, pos, posSet, boundingBox, i, Direction.NORTH,rand, baseTreeFeatureConfig);
                    lastBranch = i;
                    numBranches++;
                    northB = false;
                }else if(southB){
                    addBranch(world, pos.relative(Direction.SOUTH), posSet, boundingBox, i, Direction.SOUTH,rand, baseTreeFeatureConfig);
                    lastBranch = i;
                    numBranches++;
                    southB = false;
                }else if(eastB){
                    addBranch(world, pos.relative(Direction.EAST).south(), posSet, boundingBox, i, Direction.EAST, rand,baseTreeFeatureConfig);
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

                    addBranch(world, pos, posSet, boundingBox, i, Direction.SOUTH, rand, baseTreeFeatureConfig);
                    numBranches++;

                }

            }

            if(i == foliageHeight - 2){
                float leafChance = .1f;
                //Bell top
                addLineLeaves(world, pos.north(4).above(i), posSet, boundingBox, Direction.NORTH, 6,rand, baseTreeFeatureConfig, leafChance);
                addLineLeaves(world, pos.north(4).above(i + 1), posSet, boundingBox, Direction.NORTH, 6,rand, baseTreeFeatureConfig, leafChance);

                addLineLeaves(world, pos.north(3).above(i - 1), posSet, boundingBox, Direction.NORTH, 6,rand, baseTreeFeatureConfig, leafChance);

                addLineLeaves(world, pos.north(3).above(i), posSet, boundingBox, Direction.NORTH, 6,rand, baseTreeFeatureConfig);
                addLineLeaves(world, pos.north(3).above(i + 1), posSet, boundingBox, Direction.NORTH, 6,rand, baseTreeFeatureConfig);
                addLineLeaves(world, pos.north(2).above(i + 1), posSet, boundingBox, Direction.NORTH, 6,rand, baseTreeFeatureConfig);
                addLineLeaves(world, pos.north(1).above(i + 1), posSet, boundingBox, Direction.NORTH, 6,rand, baseTreeFeatureConfig);

                addLineLeaves(world, pos.north(2).above(i + 2), posSet, boundingBox, Direction.NORTH, 4,rand, baseTreeFeatureConfig);

                addLineLeaves(world, pos.east(5).above(i), posSet, boundingBox, Direction.EAST, 6, rand,baseTreeFeatureConfig, leafChance);
                addLineLeaves(world, pos.east(5).above(i + 1), posSet, boundingBox, Direction.EAST, 6,rand, baseTreeFeatureConfig, leafChance);
                addLineLeaves(world, pos.east(4).above(i - 1), posSet, boundingBox, Direction.EAST, 6, rand,baseTreeFeatureConfig, leafChance);

                addLineLeaves(world, pos.east(4).above(i), posSet, boundingBox, Direction.EAST, 6, rand,baseTreeFeatureConfig);
                addLineLeaves(world, pos.east(4).above(i + 1), posSet, boundingBox, Direction.EAST, 6,rand, baseTreeFeatureConfig);
                addLineLeaves(world, pos.east(3).above(i + 1), posSet, boundingBox, Direction.EAST, 6,rand, baseTreeFeatureConfig);
                addLineLeaves(world, pos.east(2).above(i + 1), posSet, boundingBox, Direction.EAST, 6,rand, baseTreeFeatureConfig);

                addLineLeaves(world, pos.east(3).above(i + 2), posSet, boundingBox, Direction.EAST, 4,rand, baseTreeFeatureConfig);
                addLineLeaves(world, pos.east(2).above(i + 2), posSet, boundingBox, Direction.EAST, 4,rand, baseTreeFeatureConfig);
                addLineLeaves(world, pos.east(1).above(i + 2), posSet, boundingBox, Direction.EAST, 4, rand,baseTreeFeatureConfig);
                addLineLeaves(world, pos.east(0).above(i + 2), posSet, boundingBox, Direction.EAST, 4,rand, baseTreeFeatureConfig);
                addLineLeaves(world, pos.east(-1).above(i + 2), posSet, boundingBox, Direction.EAST, 4,rand, baseTreeFeatureConfig);
                addLineLeaves(world, pos.east(-2).above(i + 2), posSet, boundingBox, Direction.EAST, 4,rand, baseTreeFeatureConfig);

                addLineLeaves(world, pos.west(4).south().above(i), posSet, boundingBox, Direction.WEST, 6,rand, baseTreeFeatureConfig, leafChance);
                addLineLeaves(world, pos.west(4).south().above(i + 1), posSet, boundingBox, Direction.WEST, 6,rand, baseTreeFeatureConfig, leafChance);

                addLineLeaves(world, pos.west(3).south().above(i - 1), posSet, boundingBox, Direction.WEST, 6,rand, baseTreeFeatureConfig, leafChance);
                addLineLeaves(world, pos.west(3).south().above(i), posSet, boundingBox, Direction.WEST, 6,rand, baseTreeFeatureConfig);
                addLineLeaves(world, pos.west(3).south().above(i + 1), posSet, boundingBox, Direction.WEST, 6,rand, baseTreeFeatureConfig);

                addLineLeaves(world, pos.west(2).south().above(i + 1), posSet, boundingBox, Direction.WEST, 6,rand, baseTreeFeatureConfig);
                addLineLeaves(world, pos.west(1).south().above(i + 1), posSet, boundingBox, Direction.WEST, 6,rand, baseTreeFeatureConfig);

                addLineLeaves(world, pos.west(2).south().above(i + 2), posSet, boundingBox, Direction.WEST, 4,rand, baseTreeFeatureConfig);

                // layers 1-2
                addLineLeaves(world, pos.south(4).east().above(i), posSet, boundingBox, Direction.SOUTH, 6,rand, baseTreeFeatureConfig);
                addLineLeaves(world, pos.south(4).east().above(i + 1), posSet, boundingBox, Direction.SOUTH, 6,rand, baseTreeFeatureConfig);

                addLineLeaves(world, pos.south(5).east().above(i), posSet, boundingBox, Direction.SOUTH, 6,rand, baseTreeFeatureConfig, leafChance);
                addLineLeaves(world, pos.south(5).east().above(i + 1), posSet, boundingBox, Direction.SOUTH, 6,rand, baseTreeFeatureConfig, leafChance);


                addLineLeaves(world, pos.south(3).east().above(i + 1), posSet, boundingBox, Direction.SOUTH, 6,rand, baseTreeFeatureConfig);
                addLineLeaves(world, pos.south(2).east().above(i + 1), posSet, boundingBox, Direction.SOUTH, 6,rand, baseTreeFeatureConfig);
                // layer 3
                addLineLeaves(world, pos.south(3).east().above(i + 2), posSet, boundingBox, Direction.SOUTH, 4,rand, baseTreeFeatureConfig);


                addLineLeaves(world, pos.east(2).above(i + 3), posSet, boundingBox, Direction.EAST, 4,rand, baseTreeFeatureConfig);
                addLineLeaves(world, pos.east(1).above(i + 3), posSet, boundingBox, Direction.EAST, 4,rand, baseTreeFeatureConfig);
                addLineLeaves(world, pos.east(0).above(i + 3), posSet, boundingBox, Direction.EAST, 4,rand, baseTreeFeatureConfig);
                addLineLeaves(world, pos.east(-1).above(i + 3), posSet, boundingBox, Direction.EAST, 4,rand, baseTreeFeatureConfig);



                addLineLeaves(world, pos.east(2).above(i + 4), posSet, boundingBox, Direction.EAST, 4,rand, baseTreeFeatureConfig, leafChance);
                addLineLeaves(world, pos.east(1).above(i + 4), posSet, boundingBox, Direction.EAST, 4,rand, baseTreeFeatureConfig, leafChance);
                addLineLeaves(world, pos.east(0).above(i + 4), posSet, boundingBox, Direction.EAST, 4,rand, baseTreeFeatureConfig, leafChance);
                addLineLeaves(world, pos.east(-1).above(i + 4), posSet, boundingBox, Direction.EAST, 4,rand, baseTreeFeatureConfig, leafChance);


            }
        }

        list.add(new FoliagePlacer.Foliage(new BlockPos(xOffset, yOffset, zOffset), 0, true));
        return list;
    }


    public void addBranch(IWorldGenerationReader world,BlockPos pos, Set<BlockPos> posSet, MutableBoundingBox boundingBox,int height, Direction d, Random random, BaseTreeFeatureConfig baseTreeFeatureConfig){
        pos = pos.above(height);
        addLog(world, pos.relative(d), posSet, boundingBox, random, baseTreeFeatureConfig);
        addLog(world, pos.relative(d).above(1), posSet, boundingBox, random, baseTreeFeatureConfig);
        addLog(world, pos.relative(d).above(2), posSet, boundingBox, random, baseTreeFeatureConfig);
        addLog(world, pos.relative(d, 2).above(2), posSet, boundingBox,random, baseTreeFeatureConfig );
        addLog(world, pos.relative(d, 3).above(2), posSet, boundingBox, random, baseTreeFeatureConfig);
        addLog(world, pos.relative(d, 3).above(1), posSet, boundingBox, random, baseTreeFeatureConfig);


        addLineLeaves(world, pos.relative(d).above(1), posSet, boundingBox, d, 3,random, baseTreeFeatureConfig);
        addLineLeaves(world, pos.relative(d).above(2), posSet, boundingBox, d, 3,random, baseTreeFeatureConfig);
        addLineLeaves(world, pos.relative(d).above(3), posSet, boundingBox, d, 3, random,baseTreeFeatureConfig);

        for(int j =1; j < 4; j++){
            addLineLeaves(world, pos.relative(d, j).above(3), posSet, boundingBox, d, 3, random,baseTreeFeatureConfig);
            addLineLeaves(world, pos.relative(d, j).above(2), posSet, boundingBox, d, 3,random, baseTreeFeatureConfig);

            addLineLeaves(world, pos.relative(d, j).above(4), posSet, boundingBox, d, 3, random,baseTreeFeatureConfig, .1f);
           // addLineLeaves(world, pos.offset(d, j).up(2), posSet, boundingBox, d, 3,random, baseTreeFeatureConfig);

        }
//        for(int i = 2; i < 5; i++){
//            for(int j = 1; j <= 2; j++){
//                addHollowLine(world, pos.offset(d, i).up(j), posSet, boundingBox, d, 2,random, baseTreeFeatureConfig);
//            }
//
//        }
        for(int i = 0; i < 2; i++){
            addHollowLine(world, pos.relative(d, 2 + i).above(1), posSet, boundingBox, d, 2,random, baseTreeFeatureConfig);
            addHollowLine(world, pos.relative(d, 2 + i).above(2), posSet, boundingBox, d, 2,random, baseTreeFeatureConfig);
            addHollowLine(world, pos.relative(d, 2 + i).above(1), posSet, boundingBox, d, 3,random, baseTreeFeatureConfig, 0.1f);
            addHollowLine(world, pos.relative(d, 2 + i).above(2), posSet, boundingBox, d, 3,random, baseTreeFeatureConfig, 0.1f);
        }
//        addHollowLine(world, pos.offset(d, 2).up(2), posSet, boundingBox, d, 2,random, baseTreeFeatureConfig);
//        addHollowLine(world, pos.offset(d, 2).up(1), posSet, boundingBox, d, 2,random, baseTreeFeatureConfig);
//
//        addHollowLine(world, pos.offset(d, 3).up(2), posSet, boundingBox, d, 2,random, baseTreeFeatureConfig);
//        addHollowLine(world, pos.offset(d, 3).up(1), posSet, boundingBox, d, 2,random, baseTreeFeatureConfig);
//

        addLineLeaves(world, pos.relative(d, 4).above(1), posSet, boundingBox, d, 3,random, baseTreeFeatureConfig);
        addLineLeaves(world, pos.relative(d, 4).above(2), posSet, boundingBox, d, 3,random, baseTreeFeatureConfig);


        addLineLeaves(world, pos.relative(d, 5).above(1), posSet, boundingBox, d, 3,random, baseTreeFeatureConfig, 0.1f);
        addLineLeaves(world, pos.relative(d, 5).above(2), posSet, boundingBox, d, 3,random, baseTreeFeatureConfig, 0.1f);

    }

    public boolean addLog(IWorldGenerationReader world,BlockPos pos, Set<BlockPos> posSet, MutableBoundingBox mutableBoundingBox, Random random, BaseTreeFeatureConfig baseTreeFeatureConfig){
        return addBlock(world, pos, posSet, mutableBoundingBox, baseTreeFeatureConfig.trunkProvider.getState(random, pos));
    }

    public boolean addBlock(IWorldGenerationReader world,BlockPos pos, Set<BlockPos> posSet, MutableBoundingBox mutableBoundingBox, BlockState state){
        if(TreeFeature.validTreePos(world, pos)) {
            setBlock(world, pos, state, mutableBoundingBox);
            posSet.add(pos.immutable());
            return true;
        } else {
            return false;
        }
    }
    public void addHollowLine(IWorldGenerationReader world, BlockPos pos, Set<BlockPos> posSet, MutableBoundingBox mutableBoundingBox, Direction d, int length,Random rand, BaseTreeFeatureConfig baseTreeFeatureConfig){
        addHollowLine(world, pos, posSet, mutableBoundingBox, d, length, rand, baseTreeFeatureConfig, 1.0f);
    }

    public void addHollowLine(IWorldGenerationReader world, BlockPos pos, Set<BlockPos> posSet, MutableBoundingBox mutableBoundingBox, Direction d, int length,Random rand, BaseTreeFeatureConfig baseTreeFeatureConfig, float chance){
        Direction left = d.getClockWise();
        Direction right = left.getOpposite();

        if (rand.nextFloat() <= chance && TreeFeature.validTreePos(world, pos.relative(left, length))) {
            setBlock(world,  pos.relative(left, length), baseTreeFeatureConfig.leavesProvider.getState(rand, pos.relative(left, length)), mutableBoundingBox);
            posSet.add( pos.relative(left, length).immutable());

        }
        if (rand.nextFloat() <= chance && TreeFeature.validTreePos(world, pos.relative(right, length))) {
            setBlock(world,  pos.relative(right, length), baseTreeFeatureConfig.leavesProvider.getState(rand, pos.relative(right, length)), mutableBoundingBox);
            posSet.add( pos.relative(right, length).immutable());

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
        Direction left = d.getClockWise();
        Direction right = left.getOpposite();

        for(int i = 0; i < length; i++){
            if (rand.nextFloat() <= chance && TreeFeature.validTreePos(world, pos.relative(left, i - length/3))) {
                setBlock(world,  pos.relative(left, i- length/3), baseTreeFeatureConfig.leavesProvider.getState(rand, pos.relative(left, i- length/3)), mutableBoundingBox);
                posSet.add( pos.relative(left, i- length/3).immutable());

            }
        }
    }

    public void addLineLeavesOdd(IWorldGenerationReader world, BlockPos pos, Set<BlockPos> posSet, MutableBoundingBox mutableBoundingBox, Direction d, int length,Random rand, BaseTreeFeatureConfig baseTreeFeatureConfig, float chance){
        Direction left = d.getClockWise();
        Direction right = left.getOpposite();
        length += 2;
        for(int i = 0; i < (length - 1) / 2; i++){
            if (rand.nextFloat() <= chance && TreeFeature.validTreePos(world, pos.relative(left, i))) {
                setBlock(world,  pos.relative(left, i), baseTreeFeatureConfig.leavesProvider.getState(rand, pos.relative(left, i)), mutableBoundingBox);
                posSet.add( pos.relative(left, i).immutable());
            }

            if (rand.nextFloat() <= chance && TreeFeature.validTreePos(world, pos.relative(right, i))) {
                setBlock(world,  pos.relative(right, i), baseTreeFeatureConfig.leavesProvider.getState(rand, pos.relative(right, i)), mutableBoundingBox);
                posSet.add( pos.relative(right, i).immutable());
            }
        }
    }


    public boolean addRoots(IWorldGenerationReader world, Random rand, BlockPos pos, Set<BlockPos> posSet, MutableBoundingBox mutableBoundingBox, BaseTreeFeatureConfig baseTreeFeatureConfig) {
        BlockState state = baseTreeFeatureConfig.trunkProvider.getState(rand,pos);
        if (rand.nextDouble() < 0.75 && TreeFeature.validTreePos(world, pos)) {
            setBlock(world, pos, state, mutableBoundingBox);
            posSet.add(pos.immutable());
            return true;
        } else {
            return false;
        }
    }
}
