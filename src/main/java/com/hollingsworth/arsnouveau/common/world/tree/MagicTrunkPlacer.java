package com.hollingsworth.arsnouveau.common.world.tree;

import com.google.common.collect.Lists;
import com.hollingsworth.arsnouveau.setup.ModSetup;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

import java.util.List;
import java.util.function.BiConsumer;

public class MagicTrunkPlacer extends TrunkPlacer {
    public MagicTrunkPlacer(int baseHeight, int height_rand_a, int height_rand_b) {
        super(baseHeight, height_rand_a, height_rand_b);
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return ModSetup.MAGIC_TRUNK_PLACER.get();
    }

    public static final Codec<MagicTrunkPlacer> CODEC = RecordCodecBuilder.create((p_70090_) -> {
        return trunkPlacerParts(p_70090_).apply(p_70090_, MagicTrunkPlacer::new);
    });

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader world, BiConsumer<BlockPos, BlockState> consumer, RandomSource rand, int foliageHeight, BlockPos pos, TreeConfiguration baseTreeFeatureConfig) {
        List<FoliagePlacer.FoliageAttachment> list = Lists.newArrayList();
        BlockPos blockpos = pos.below();
        setDirtAt(world, consumer, rand, blockpos, baseTreeFeatureConfig);
        setDirtAt(world, consumer, rand, blockpos.east(), baseTreeFeatureConfig);
        setDirtAt(world, consumer, rand, blockpos.south(), baseTreeFeatureConfig);
        setDirtAt(world, consumer, rand, blockpos.south().east(), baseTreeFeatureConfig);
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        int yOffset = y + foliageHeight - 1;

        int numBranches = 0;
        int lastBranch = 0;
        boolean northB = rand.nextFloat() >= 0.5;
        boolean southB = rand.nextFloat() >= 0.5;
        boolean eastB = rand.nextFloat() >= 0.5;
        boolean westB = rand.nextFloat() >= 0.5;

        for(int i = 0; i < foliageHeight; ++i) {
            int j2 = y + i;
            BlockPos blockpos1 = new BlockPos(x, j2, z);
            if (TreeFeature.isAirOrLeaves(world, blockpos1)) {
                placeLog(world, consumer, rand, blockpos1, baseTreeFeatureConfig);
                placeLog(world, consumer,rand, blockpos1.east(),  baseTreeFeatureConfig);
                placeLog(world,consumer, rand, blockpos1.south(),  baseTreeFeatureConfig);
                placeLog(world,consumer, rand, blockpos1.east().south(),  baseTreeFeatureConfig);
            }

            if(i < 1){
                addRoots(world, rand, pos.west().above(i),  consumer, baseTreeFeatureConfig);
                addRoots(world, rand, pos.south().south().above(i),consumer, baseTreeFeatureConfig);
                addRoots(world, rand, pos.south().west().above(i), consumer,baseTreeFeatureConfig);
                addRoots(world, rand, pos.south().south().east().above(i),consumer, baseTreeFeatureConfig);
                addRoots(world, rand, pos.east().east().above(i), consumer,baseTreeFeatureConfig);
                addRoots(world, rand, pos.east().east().south().above(i),consumer, baseTreeFeatureConfig);
                addRoots(world, rand, pos.east().north().above(i), consumer,baseTreeFeatureConfig);
                addRoots(world, rand, pos.north().above(i), consumer,baseTreeFeatureConfig);
            }

            if(i > 1 && i > lastBranch){
                if(northB){
                    addBranch(world, pos, i, Direction.NORTH,rand, baseTreeFeatureConfig, consumer);
                    lastBranch = i;
                    numBranches++;
                    northB = false;
                }else if(southB){
                    addBranch(world, pos.relative(Direction.SOUTH), i, Direction.SOUTH,rand, baseTreeFeatureConfig, consumer);
                    lastBranch = i;
                    numBranches++;
                    southB = false;
                }else if(eastB){
                    addBranch(world, pos.relative(Direction.EAST).south(), i, Direction.EAST, rand,baseTreeFeatureConfig, consumer);
                    lastBranch = i;
                    numBranches++;
                    eastB = false;
                }else if(westB){
                    addBranch(world, pos, i, Direction.WEST, rand,baseTreeFeatureConfig, consumer);
                    lastBranch = i;
                    numBranches++;
                    westB = false;
                }else if(numBranches == 0){
                    addBranch(world, pos, i, Direction.NORTH, rand, baseTreeFeatureConfig, consumer);
                    lastBranch = i;
                    numBranches++;

                    addBranch(world, pos, i, Direction.SOUTH, rand, baseTreeFeatureConfig, consumer);
                    numBranches++;

                }

            }

            if(i == foliageHeight - 2){
                float leafChance = .1f;

                //Bell top
                addLineLeaves(world, pos.north(4).above(i), Direction.NORTH, 6,rand, baseTreeFeatureConfig, leafChance, consumer);
                addLineLeaves(world, pos.north(4).above(i + 1), Direction.NORTH, 6,rand, baseTreeFeatureConfig, leafChance, consumer);

                addLineLeaves(world, pos.north(3).above(i - 1), Direction.NORTH, 6,rand, baseTreeFeatureConfig, leafChance, consumer);

                addLineLeaves(world, pos.north(3).above(i), Direction.NORTH, 6,rand, baseTreeFeatureConfig, consumer);
                addLineLeaves(world, pos.north(3).above(i + 1), Direction.NORTH, 6,rand, baseTreeFeatureConfig, consumer);
                addLineLeaves(world, pos.north(2).above(i + 1), Direction.NORTH, 6,rand, baseTreeFeatureConfig, consumer);
                addLineLeaves(world, pos.north(1).above(i + 1), Direction.NORTH, 6,rand, baseTreeFeatureConfig, consumer);

                addLineLeaves(world, pos.north(2).above(i + 2), Direction.NORTH, 4,rand, baseTreeFeatureConfig, consumer);

                addLineLeaves(world, pos.east(5).above(i), Direction.EAST, 6, rand,baseTreeFeatureConfig, leafChance, consumer);
                addLineLeaves(world, pos.east(5).above(i + 1), Direction.EAST, 6,rand, baseTreeFeatureConfig, leafChance, consumer);
                addLineLeaves(world, pos.east(4).above(i - 1), Direction.EAST, 6, rand,baseTreeFeatureConfig, leafChance, consumer);

                addLineLeaves(world, pos.east(4).above(i), Direction.EAST, 6, rand,baseTreeFeatureConfig, consumer);
                addLineLeaves(world, pos.east(4).above(i + 1), Direction.EAST, 6,rand, baseTreeFeatureConfig, consumer);
                addLineLeaves(world, pos.east(3).above(i + 1), Direction.EAST, 6,rand, baseTreeFeatureConfig, consumer);
                addLineLeaves(world, pos.east(2).above(i + 1), Direction.EAST, 6,rand, baseTreeFeatureConfig, consumer);

                addLineLeaves(world, pos.east(3).above(i + 2), Direction.EAST, 4,rand, baseTreeFeatureConfig, consumer);
                addLineLeaves(world, pos.east(2).above(i + 2), Direction.EAST, 4,rand, baseTreeFeatureConfig, consumer);
                addLineLeaves(world, pos.east(1).above(i + 2), Direction.EAST, 4, rand,baseTreeFeatureConfig, consumer);
                addLineLeaves(world, pos.east(0).above(i + 2), Direction.EAST, 4,rand, baseTreeFeatureConfig, consumer);
                addLineLeaves(world, pos.east(-1).above(i + 2), Direction.EAST, 4,rand, baseTreeFeatureConfig, consumer);
                addLineLeaves(world, pos.east(-2).above(i + 2), Direction.EAST, 4,rand, baseTreeFeatureConfig, consumer);

                addLineLeaves(world, pos.west(4).south().above(i), Direction.WEST, 6,rand, baseTreeFeatureConfig, leafChance, consumer);
                addLineLeaves(world, pos.west(4).south().above(i + 1), Direction.WEST, 6,rand, baseTreeFeatureConfig, leafChance, consumer);

                addLineLeaves(world, pos.west(3).south().above(i - 1), Direction.WEST, 6,rand, baseTreeFeatureConfig, leafChance, consumer);
                addLineLeaves(world, pos.west(3).south().above(i), Direction.WEST, 6,rand, baseTreeFeatureConfig, consumer);
                addLineLeaves(world, pos.west(3).south().above(i + 1), Direction.WEST, 6,rand, baseTreeFeatureConfig, consumer);

                addLineLeaves(world, pos.west(2).south().above(i + 1), Direction.WEST, 6,rand, baseTreeFeatureConfig, consumer);
                addLineLeaves(world, pos.west(1).south().above(i + 1), Direction.WEST, 6,rand, baseTreeFeatureConfig, consumer);

                addLineLeaves(world, pos.west(2).south().above(i + 2), Direction.WEST, 4,rand, baseTreeFeatureConfig, consumer);

                // layers 1-2
                addLineLeaves(world, pos.south(4).east().above(i), Direction.SOUTH, 6,rand, baseTreeFeatureConfig, consumer);
                addLineLeaves(world, pos.south(4).east().above(i + 1), Direction.SOUTH, 6,rand, baseTreeFeatureConfig, consumer);

                addLineLeaves(world, pos.south(5).east().above(i), Direction.SOUTH, 6,rand, baseTreeFeatureConfig, leafChance, consumer);
                addLineLeaves(world, pos.south(5).east().above(i + 1), Direction.SOUTH, 6,rand, baseTreeFeatureConfig, leafChance, consumer);


                addLineLeaves(world, pos.south(3).east().above(i + 1), Direction.SOUTH, 6,rand, baseTreeFeatureConfig, consumer);
                addLineLeaves(world, pos.south(2).east().above(i + 1), Direction.SOUTH, 6,rand, baseTreeFeatureConfig, consumer);
                // layer 3
                addLineLeaves(world, pos.south(3).east().above(i + 2), Direction.SOUTH, 4,rand, baseTreeFeatureConfig, consumer);


                addLineLeaves(world, pos.east(2).above(i + 3), Direction.EAST, 4,rand, baseTreeFeatureConfig, consumer);
                addLineLeaves(world, pos.east(1).above(i + 3), Direction.EAST, 4,rand, baseTreeFeatureConfig, consumer);
                addLineLeaves(world, pos.east(0).above(i + 3), Direction.EAST, 4,rand, baseTreeFeatureConfig, consumer);
                addLineLeaves(world, pos.east(-1).above(i + 3), Direction.EAST, 4,rand, baseTreeFeatureConfig, consumer);



                addLineLeaves(world, pos.east(2).above(i + 4), Direction.EAST, 4,rand, baseTreeFeatureConfig, leafChance, consumer);
                addLineLeaves(world, pos.east(1).above(i + 4), Direction.EAST, 4,rand, baseTreeFeatureConfig, leafChance, consumer);
                addLineLeaves(world, pos.east(0).above(i + 4), Direction.EAST, 4,rand, baseTreeFeatureConfig, leafChance, consumer);
                addLineLeaves(world, pos.east(-1).above(i + 4), Direction.EAST, 4,rand, baseTreeFeatureConfig, leafChance, consumer);


            }
        }

        list.add(new FoliagePlacer.FoliageAttachment(new BlockPos(x, yOffset, z), 0, true));
        return list;
    }

    public void addBranch(LevelSimulatedReader world, BlockPos pos, int height, Direction d, RandomSource random, TreeConfiguration baseTreeFeatureConfig, BiConsumer<BlockPos, BlockState> consumer) {
        pos = pos.above(height);
        addLog(world, pos.relative(d), random, baseTreeFeatureConfig, consumer);
        addLog(world, pos.relative(d).above(1), random, baseTreeFeatureConfig, consumer);
        addLog(world, pos.relative(d).above(2), random, baseTreeFeatureConfig, consumer);
        addLog(world, pos.relative(d, 2).above(2), random, baseTreeFeatureConfig, consumer);
        addLog(world, pos.relative(d, 3).above(2), random, baseTreeFeatureConfig, consumer);
        addLog(world, pos.relative(d, 3).above(1), random, baseTreeFeatureConfig, consumer);


        addLineLeaves(world, pos.relative(d).above(1), d, 3, random, baseTreeFeatureConfig, consumer);
        addLineLeaves(world, pos.relative(d).above(2), d, 3,random, baseTreeFeatureConfig,consumer);
        addLineLeaves(world, pos.relative(d).above(3), d, 3, random,baseTreeFeatureConfig,consumer);

        for(int j =1; j < 4; j++){
            addLineLeaves(world, pos.relative(d, j).above(3), d, 3, random,baseTreeFeatureConfig,consumer);
            addLineLeaves(world, pos.relative(d, j).above(2), d, 3,random, baseTreeFeatureConfig,consumer);

            addLineLeaves(world, pos.relative(d, j).above(4), d, 3, random,baseTreeFeatureConfig, .1f,consumer);
           // addLineLeaves(world, pos.offset(d, j).up(2), d, 3,random, baseTreeFeatureConfig);

        }
//        for(int i = 2; i < 5; i++){
//            for(int j = 1; j <= 2; j++){
//                addHollowLine(world, pos.offset(d, i).up(j), d, 2,random, baseTreeFeatureConfig);
//            }
//
//        }
        for(int i = 0; i < 2; i++){
            addHollowLine(world, pos.relative(d, 2 + i).above(1), d, 2,random, baseTreeFeatureConfig,consumer);
            addHollowLine(world, pos.relative(d, 2 + i).above(2), d, 2,random, baseTreeFeatureConfig,consumer);
            addHollowLine(world, pos.relative(d, 2 + i).above(1), d, 3,random, baseTreeFeatureConfig, 0.1f,consumer);
            addHollowLine(world, pos.relative(d, 2 + i).above(2), d, 3,random, baseTreeFeatureConfig, 0.1f,consumer);
        }
//        addHollowLine(world, pos.offset(d, 2).up(2), d, 2,random, baseTreeFeatureConfig);
//        addHollowLine(world, pos.offset(d, 2).up(1), d, 2,random, baseTreeFeatureConfig);
//
//        addHollowLine(world, pos.offset(d, 3).up(2), d, 2,random, baseTreeFeatureConfig);
//        addHollowLine(world, pos.offset(d, 3).up(1), d, 2,random, baseTreeFeatureConfig);
//

        addLineLeaves(world, pos.relative(d, 4).above(1), d, 3,random, baseTreeFeatureConfig,consumer);
        addLineLeaves(world, pos.relative(d, 4).above(2), d, 3,random, baseTreeFeatureConfig,consumer);


        addLineLeaves(world, pos.relative(d, 5).above(1), d, 3,random, baseTreeFeatureConfig, 0.1f,consumer);
        addLineLeaves(world, pos.relative(d, 5).above(2), d, 3,random, baseTreeFeatureConfig, 0.1f,consumer);

    }

    public boolean addLog(LevelSimulatedReader world, BlockPos pos, RandomSource random, TreeConfiguration baseTreeFeatureConfig, BiConsumer<BlockPos, BlockState> consumer) {
        return addBlock(world, pos, baseTreeFeatureConfig.trunkProvider.getState(random, pos), consumer);
    }

    public boolean addBlock(LevelSimulatedReader world, BlockPos pos, BlockState state, BiConsumer<BlockPos, BlockState> consumer) {
        if (TreeFeature.validTreePos(world, pos)) {
            setBlock(world, pos, state, consumer);
            return true;
        } else {
            return false;
        }
    }

    public void addHollowLine(LevelSimulatedReader world, BlockPos pos, Direction d, int length, RandomSource rand, TreeConfiguration baseTreeFeatureConfig, BiConsumer<BlockPos, BlockState> consumer) {
        addHollowLine(world, pos, d, length, rand, baseTreeFeatureConfig, 1.0f, consumer);
    }

    public void addHollowLine(LevelSimulatedReader world, BlockPos pos, Direction d, int length, RandomSource rand, TreeConfiguration baseTreeFeatureConfig, float chance, BiConsumer<BlockPos, BlockState> consumer) {
        Direction left = d.getClockWise();
        Direction right = left.getOpposite();

        if (rand.nextFloat() <= chance && TreeFeature.validTreePos(world, pos.relative(left, length))) {
            setBlock(world, pos.relative(left, length), baseTreeFeatureConfig.foliageProvider.getState(rand, pos.relative(left, length)), consumer);
        }
        if (rand.nextFloat() <= chance && TreeFeature.validTreePos(world, pos.relative(right, length))) {
            setBlock(world, pos.relative(right, length), baseTreeFeatureConfig.foliageProvider.getState(rand, pos.relative(right, length)), consumer);
        }
    }

    public void addLineLeaves(LevelSimulatedReader world, BlockPos pos, Direction d, int length, RandomSource rand, TreeConfiguration baseTreeFeatureConfig, BiConsumer<BlockPos, BlockState> consumer) {
        if (length % 2 == 0)
            addLineLeavesEven(world, pos, d, length, rand, baseTreeFeatureConfig, 1.0f, consumer);
        else
            addLineLeavesOdd(world, pos, d, length, rand, baseTreeFeatureConfig, 1.0f, consumer);
    }

    public void addLineLeaves(LevelSimulatedReader world, BlockPos pos, Direction d, int length, RandomSource rand, TreeConfiguration baseTreeFeatureConfig, float chance, BiConsumer<BlockPos, BlockState> consumer) {
        if (length % 2 == 0)
            addLineLeavesEven(world, pos, d, length, rand, baseTreeFeatureConfig, chance, consumer);
        else
            addLineLeavesOdd(world, pos, d, length, rand, baseTreeFeatureConfig, chance, consumer);
    }

    public void addLineLeavesEven(LevelSimulatedReader world, BlockPos pos, Direction d, int length, RandomSource rand, TreeConfiguration baseTreeFeatureConfig, float chance, BiConsumer<BlockPos, BlockState> consumer) {
        Direction left = d.getClockWise();
        Direction right = left.getOpposite();

        for (int i = 0; i < length; i++) {
            if (rand.nextFloat() <= chance && TreeFeature.validTreePos(world, pos.relative(left, i - length / 3))) {
                setBlock(world, pos.relative(left, i - length / 3), baseTreeFeatureConfig.foliageProvider.getState(rand, pos.relative(left, i - length / 3)), consumer);
            }
        }
    }

    public void addLineLeavesOdd(LevelSimulatedReader world, BlockPos pos, Direction d, int length, RandomSource rand, TreeConfiguration baseTreeFeatureConfig, float chance, BiConsumer<BlockPos, BlockState> consumer) {
        Direction left = d.getClockWise();
        Direction right = left.getOpposite();
        length += 2;
        for (int i = 0; i < (length - 1) / 2; i++) {
            if (rand.nextFloat() <= chance && TreeFeature.validTreePos(world, pos.relative(left, i))) {
                setBlock(world, pos.relative(left, i), baseTreeFeatureConfig.foliageProvider.getState(rand, pos.relative(left, i)), consumer);
            }

            if (rand.nextFloat() <= chance && TreeFeature.validTreePos(world, pos.relative(right, i))) {
                setBlock(world, pos.relative(right, i), baseTreeFeatureConfig.foliageProvider.getState(rand, pos.relative(right, i)), consumer);
            }
        }
    }


    public boolean addRoots(LevelSimulatedReader world, RandomSource rand, BlockPos pos, BiConsumer<BlockPos, BlockState> consumer, TreeConfiguration baseTreeFeatureConfig) {
        BlockState state = baseTreeFeatureConfig.trunkProvider.getState(rand, pos);
        if (rand.nextDouble() < 0.75 && TreeFeature.validTreePos(world, pos)) {
            setBlock(world, pos.immutable(), state, consumer);

            return true;
        } else {
            return false;
        }
    }

    public void setBlock(LevelSimulatedReader world, BlockPos pos, BlockState state, BiConsumer<BlockPos, BlockState> consumer){
        consumer.accept(pos, state);
    }
}
