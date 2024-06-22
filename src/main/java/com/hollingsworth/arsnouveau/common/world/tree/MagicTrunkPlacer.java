package com.hollingsworth.arsnouveau.common.world.tree;

import com.google.common.collect.Lists;
import com.hollingsworth.arsnouveau.common.block.ArchfruitPod;
import com.hollingsworth.arsnouveau.common.block.SconceBlock;
import com.hollingsworth.arsnouveau.setup.ModSetup;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

import java.util.List;
import java.util.function.BiConsumer;

public class MagicTrunkPlacer extends TrunkPlacer {
    boolean isWorldGen;
    ResourceLocation podID;
    public MagicTrunkPlacer(int baseHeight, int height_rand_a, int height_rand_b) {
        super(baseHeight, height_rand_a, height_rand_b);
    }

    public MagicTrunkPlacer(int baseHeight, int height_rand_a, int height_rand_b, boolean isWorldGen, String podName) {
        this(baseHeight, height_rand_a, height_rand_b);
        this.isWorldGen = isWorldGen;
        this.podID = new ResourceLocation(podName);
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return ModSetup.MAGIC_TRUNK_PLACER.get();
    }

    public static final Codec<MagicTrunkPlacer> CODEC = RecordCodecBuilder.create(builder ->
            builder.group(Codec.intRange(0, 32).fieldOf("base_height").forGetter(placer -> placer.baseHeight),
                            Codec.intRange(0, 24).fieldOf("height_rand_a").forGetter(placer -> placer.heightRandA),
                            Codec.intRange(0, 24).fieldOf("height_rand_b").forGetter(placer -> placer.heightRandB),
                            Codec.BOOL.fieldOf("isWorldGen").forGetter(placer -> placer.isWorldGen),
                            Codec.STRING.fieldOf("podID").forGetter(placer -> placer.podID.toString()))
                    .apply(builder, MagicTrunkPlacer::new));

    protected static void setDirtAt(LevelSimulatedReader pLevel, BiConsumer<BlockPos, BlockState> pBlockSetter, RandomSource pRandom, BlockPos pPos, TreeConfiguration pConfig, boolean isTreeOrigin) {
        if (isTreeOrigin || pLevel.isStateAtPosition(pPos, (state) -> state.is(BlockTags.DIRT) || state.is(Blocks.FARMLAND))) {
            TrunkPlacer.setDirtAt(pLevel, pBlockSetter, pRandom, pPos, pConfig);
        }
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader world, BiConsumer<BlockPos, BlockState> consumer, RandomSource rand, int foliageHeight, BlockPos pos, TreeConfiguration baseTreeFeatureConfig) {
        List<FoliagePlacer.FoliageAttachment> list = Lists.newArrayList();
        BlockPos blockpos = pos.below();
        setDirtAt(world, consumer, rand, blockpos, baseTreeFeatureConfig, true);
        setDirtAt(world, consumer, rand, blockpos.east(), baseTreeFeatureConfig, false);
        setDirtAt(world, consumer, rand, blockpos.south(), baseTreeFeatureConfig, false);
        setDirtAt(world, consumer, rand, blockpos.south().east(), baseTreeFeatureConfig, false);
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        int yOffset = y + foliageHeight - 1;

        int numBranches = 0;
        int lastBranch = 0;
        double branchChance = 0.5;
        boolean northB = rand.nextFloat() >= branchChance;
        boolean southB = rand.nextFloat() >= branchChance;
        boolean eastB = rand.nextFloat() >= branchChance;
        boolean westB = rand.nextFloat() >= branchChance;
        for (int i = 0; i < foliageHeight; ++i) {
            int j2 = y + i;
            BlockPos blockpos1 = new BlockPos(x, j2, z);
            if (TreeFeature.isAirOrLeaves(world, blockpos1)) {
                placeLog(world, consumer, rand, blockpos1, baseTreeFeatureConfig);
                placeLog(world, consumer, rand, blockpos1.east(), baseTreeFeatureConfig);
                placeLog(world, consumer, rand, blockpos1.south(), baseTreeFeatureConfig);
                placeLog(world, consumer, rand, blockpos1.east().south(), baseTreeFeatureConfig);
            }

            if (i == 0) {
                BlockPos abovePos = pos.above(i);
                addRoots(world, rand, abovePos.west(), consumer, baseTreeFeatureConfig, new Direction[]{Direction.NORTH, Direction.WEST});
                addRoots(world, rand, abovePos.south(2), consumer, baseTreeFeatureConfig, new Direction[]{Direction.SOUTH, Direction.WEST});
                addRoots(world, rand, abovePos.south().west(), consumer, baseTreeFeatureConfig, new Direction[]{Direction.WEST});
                addRoots(world, rand, abovePos.south(2).east(), consumer, baseTreeFeatureConfig, new Direction[]{Direction.EAST, Direction.SOUTH});
                addRoots(world, rand, abovePos.east(2), consumer, baseTreeFeatureConfig, new Direction[]{Direction.EAST, Direction.NORTH});
                addRoots(world, rand, abovePos.east(2).south(), consumer, baseTreeFeatureConfig, new Direction[]{Direction.EAST});
                addRoots(world, rand, abovePos.east().north(), consumer, baseTreeFeatureConfig, new Direction[]{Direction.NORTH});
                addRoots(world, rand, abovePos.north(), consumer, baseTreeFeatureConfig, new Direction[]{Direction.NORTH, Direction.EAST});
            }

            if (i > 1 && i > lastBranch) {
                if (northB) {
                    addBranch(world, pos, i, Direction.NORTH, rand, baseTreeFeatureConfig, consumer);
                    lastBranch = i;
                    numBranches++;
                    northB = false;
                } else if (southB) {
                    addBranch(world, pos.relative(Direction.SOUTH), i, Direction.SOUTH, rand, baseTreeFeatureConfig, consumer);
                    lastBranch = i;
                    numBranches++;
                    southB = false;
                } else if (eastB) {
                    addBranch(world, pos.relative(Direction.EAST).south(), i, Direction.EAST, rand, baseTreeFeatureConfig, consumer);
                    lastBranch = i;
                    numBranches++;
                    eastB = false;
                } else if (westB) {
                    addBranch(world, pos, i, Direction.WEST, rand, baseTreeFeatureConfig, consumer);
                    lastBranch = i;
                    numBranches++;
                    westB = false;
                } else if (numBranches == 0) {
                    addBranch(world, pos, i, Direction.NORTH, rand, baseTreeFeatureConfig, consumer);
                    lastBranch = i;
                    numBranches++;

                    addBranch(world, pos, i, Direction.SOUTH, rand, baseTreeFeatureConfig, consumer);
                    numBranches++;

                }

            }
            // Leaf tops
            if (i == foliageHeight - 2) {
                float leafChance = .1f;

                //Bell top
                addLineLeaves(world, pos.north(4).above(i), Direction.NORTH, 6, rand, baseTreeFeatureConfig, leafChance, consumer);
                addLineLeaves(world, pos.north(4).above(i + 1), Direction.NORTH, 6, rand, baseTreeFeatureConfig, leafChance, consumer);

                addLineLeaves(world, pos.north(3).above(i - 1), Direction.NORTH, 6, rand, baseTreeFeatureConfig, leafChance, consumer);

                addLineLeaves(world, pos.north(3).above(i), Direction.NORTH, 6, rand, baseTreeFeatureConfig, consumer);
                addLineLeaves(world, pos.north(3).above(i + 1), Direction.NORTH, 6, rand, baseTreeFeatureConfig, consumer);
                addLineLeaves(world, pos.north(2).above(i + 1), Direction.NORTH, 6, rand, baseTreeFeatureConfig, consumer);
                addLineLeaves(world, pos.north(1).above(i + 1), Direction.NORTH, 6, rand, baseTreeFeatureConfig, consumer);

                addLineLeaves(world, pos.north(2).above(i + 2), Direction.NORTH, 4, rand, baseTreeFeatureConfig, consumer);

                addLineLeaves(world, pos.east(5).above(i), Direction.EAST, 6, rand, baseTreeFeatureConfig, leafChance, consumer);
                addLineLeaves(world, pos.east(5).above(i + 1), Direction.EAST, 6, rand, baseTreeFeatureConfig, leafChance, consumer);
                addLineLeaves(world, pos.east(4).above(i - 1), Direction.EAST, 6, rand, baseTreeFeatureConfig, leafChance, consumer);

                addLineLeaves(world, pos.east(4).above(i), Direction.EAST, 6, rand, baseTreeFeatureConfig, consumer);
                addLineLeaves(world, pos.east(4).above(i + 1), Direction.EAST, 6, rand, baseTreeFeatureConfig, consumer);
                addLineLeaves(world, pos.east(3).above(i + 1), Direction.EAST, 6, rand, baseTreeFeatureConfig, consumer);
                addLineLeaves(world, pos.east(2).above(i + 1), Direction.EAST, 6, rand, baseTreeFeatureConfig, consumer);

                addLineLeaves(world, pos.east(3).above(i + 2), Direction.EAST, 4, rand, baseTreeFeatureConfig, consumer);
                addLineLeaves(world, pos.east(2).above(i + 2), Direction.EAST, 4, rand, baseTreeFeatureConfig, consumer);
                addLineLeaves(world, pos.east(1).above(i + 2), Direction.EAST, 4, rand, baseTreeFeatureConfig, consumer);
                addLineLeaves(world, pos.east(0).above(i + 2), Direction.EAST, 4, rand, baseTreeFeatureConfig, consumer);
                addLineLeaves(world, pos.east(-1).above(i + 2), Direction.EAST, 4, rand, baseTreeFeatureConfig, consumer);
                addLineLeaves(world, pos.east(-2).above(i + 2), Direction.EAST, 4, rand, baseTreeFeatureConfig, consumer);

                addLineLeaves(world, pos.west(4).south().above(i), Direction.WEST, 6, rand, baseTreeFeatureConfig, leafChance, consumer);
                addLineLeaves(world, pos.west(4).south().above(i + 1), Direction.WEST, 6, rand, baseTreeFeatureConfig, leafChance, consumer);

                addLineLeaves(world, pos.west(3).south().above(i - 1), Direction.WEST, 6, rand, baseTreeFeatureConfig, leafChance, consumer);
                addLineLeaves(world, pos.west(3).south().above(i), Direction.WEST, 6, rand, baseTreeFeatureConfig, consumer);
                addLineLeaves(world, pos.west(3).south().above(i + 1), Direction.WEST, 6, rand, baseTreeFeatureConfig, consumer);

                addLineLeaves(world, pos.west(2).south().above(i + 1), Direction.WEST, 6, rand, baseTreeFeatureConfig, consumer);
                addLineLeaves(world, pos.west(1).south().above(i + 1), Direction.WEST, 6, rand, baseTreeFeatureConfig, consumer);

                addLineLeaves(world, pos.west(2).south().above(i + 2), Direction.WEST, 4, rand, baseTreeFeatureConfig, consumer);

                // layers 1-2
                addLineLeaves(world, pos.south(4).east().above(i), Direction.SOUTH, 6, rand, baseTreeFeatureConfig, consumer);
                addLineLeaves(world, pos.south(4).east().above(i + 1), Direction.SOUTH, 6, rand, baseTreeFeatureConfig, consumer);

                addLineLeaves(world, pos.south(5).east().above(i), Direction.SOUTH, 6, rand, baseTreeFeatureConfig, leafChance, consumer);
                addLineLeaves(world, pos.south(5).east().above(i + 1), Direction.SOUTH, 6, rand, baseTreeFeatureConfig, leafChance, consumer);


                addLineLeaves(world, pos.south(3).east().above(i + 1), Direction.SOUTH, 6, rand, baseTreeFeatureConfig, consumer);
                addLineLeaves(world, pos.south(2).east().above(i + 1), Direction.SOUTH, 6, rand, baseTreeFeatureConfig, consumer);
                // layer 3
                addLineLeaves(world, pos.south(3).east().above(i + 2), Direction.SOUTH, 4, rand, baseTreeFeatureConfig, consumer);


                addLineLeaves(world, pos.east(2).above(i + 3), Direction.EAST, 4, rand, baseTreeFeatureConfig, consumer);
                addLineLeaves(world, pos.east(1).above(i + 3), Direction.EAST, 4, rand, baseTreeFeatureConfig, consumer);
                addLineLeaves(world, pos.east(0).above(i + 3), Direction.EAST, 4, rand, baseTreeFeatureConfig, consumer);
                addLineLeaves(world, pos.east(-1).above(i + 3), Direction.EAST, 4, rand, baseTreeFeatureConfig, consumer);

                addLineLeaves(world, pos.east(2).above(i + 4), Direction.EAST, 4, rand, baseTreeFeatureConfig, leafChance, consumer);
                addLineLeaves(world, pos.east(1).above(i + 4), Direction.EAST, 4, rand, baseTreeFeatureConfig, leafChance, consumer);
                addLineLeaves(world, pos.east(0).above(i + 4), Direction.EAST, 4, rand, baseTreeFeatureConfig, leafChance, consumer);
                addLineLeaves(world, pos.east(-1).above(i + 4), Direction.EAST, 4, rand, baseTreeFeatureConfig, leafChance, consumer);


            }

            // pod decoration
            if(isWorldGen && i >= 2 && i < foliageHeight - 2){
                addPod(world,blockpos1,rand,consumer, new Direction[]{Direction.NORTH, Direction.WEST} );
                addPod(world,blockpos1.east(),rand,consumer, new Direction[]{Direction.NORTH, Direction.EAST} );
                addPod(world,blockpos1.south(),rand,consumer, new Direction[]{Direction.SOUTH, Direction.WEST} );
                addPod(world,blockpos1.south().east(),rand,consumer, new Direction[]{Direction.SOUTH, Direction.EAST} );
            }
        }

        list.add(new FoliagePlacer.FoliageAttachment(new BlockPos(x, yOffset, z), 0, true));
        return list;
    }

    public void addPod(LevelSimulatedReader world, BlockPos pos, RandomSource random, BiConsumer<BlockPos, BlockState> consumer, Direction[] validDirs){
        if(random.nextDouble() <= 0.07) {
            Direction d = validDirs[random.nextInt(validDirs.length)];
            int age = random.nextIntBetweenInclusive(0, 2);
            if(world.isStateAtPosition(pos.relative(d), BlockBehaviour.BlockStateBase::isAir)) {
                setBlock(world, pos.relative(d).immutable(), getPodState()
                        .setValue(HorizontalDirectionalBlock.FACING, d.getOpposite())
                        .setValue(SconceBlock.LIGHT_LEVEL, 6 + age)
                        .setValue(ArchfruitPod.AGE, age), consumer);
            }
        }
    }

    public BlockState getPodState(){
        return ForgeRegistries.BLOCKS.getValue(podID).defaultBlockState();
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
        addLineLeaves(world, pos.relative(d).above(2), d, 3, random, baseTreeFeatureConfig, consumer);
        addLineLeaves(world, pos.relative(d).above(3), d, 3, random, baseTreeFeatureConfig, consumer);

        for (int j = 1; j < 4; j++) {
            addLineLeaves(world, pos.relative(d, j).above(3), d, 3, random, baseTreeFeatureConfig, consumer);
            addLineLeaves(world, pos.relative(d, j).above(2), d, 3, random, baseTreeFeatureConfig, consumer);
            addLineLeaves(world, pos.relative(d, j).above(4), d, 3, random, baseTreeFeatureConfig, .1f, consumer);
        }

        for (int i = 0; i < 2; i++) {
            addHollowLine(world, pos.relative(d, 2 + i).above(1), d, 2, random, baseTreeFeatureConfig, consumer);
            addHollowLine(world, pos.relative(d, 2 + i).above(2), d, 2, random, baseTreeFeatureConfig, consumer);
            addHollowLine(world, pos.relative(d, 2 + i).above(1), d, 3, random, baseTreeFeatureConfig, 0.1f, consumer);
            addHollowLine(world, pos.relative(d, 2 + i).above(2), d, 3, random, baseTreeFeatureConfig, 0.1f, consumer);
        }

        addLineLeaves(world, pos.relative(d, 4).above(1), d, 3, random, baseTreeFeatureConfig, consumer);
        addLineLeaves(world, pos.relative(d, 4).above(2), d, 3, random, baseTreeFeatureConfig, consumer);


        addLineLeaves(world, pos.relative(d, 5).above(1), d, 3, random, baseTreeFeatureConfig, 0.1f, consumer);
        addLineLeaves(world, pos.relative(d, 5).above(2), d, 3, random, baseTreeFeatureConfig, 0.1f, consumer);

    }

    public boolean addLog(LevelSimulatedReader world, BlockPos pos, RandomSource random, TreeConfiguration baseTreeFeatureConfig, BiConsumer<BlockPos, BlockState> consumer) {
        return addBlock(world, pos, baseTreeFeatureConfig.trunkProvider.getState(random, pos), consumer);
    }

    public boolean addBlock(LevelSimulatedReader world, BlockPos pos, BlockState state, BiConsumer<BlockPos, BlockState> consumer) {
        if (TreeFeature.validTreePos(world, pos)) {
            setBlock(world, pos, state, consumer);
            return true;
        }
        return false;
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


    public boolean addRoots(LevelSimulatedReader world, RandomSource rand, BlockPos pos, BiConsumer<BlockPos, BlockState> consumer, TreeConfiguration baseTreeFeatureConfig, Direction[] extendedDirs) {
        BlockState state = baseTreeFeatureConfig.trunkProvider.getState(rand, pos);
        if (rand.nextDouble() < 0.75 && TreeFeature.validTreePos(world, pos)) {
            setBlock(world, pos.immutable(), state, consumer);
            if (isWorldGen) {
                for (int i = 0; i < 2; i++) {
                    if (TreeFeature.validTreePos(world, pos.below())) {
                        pos = pos.below();
                        setBlock(world, pos.immutable(), state, consumer);
                    } else {
                        break;
                    }
                }
                for (Direction d : extendedDirs) {
                    placeRotatedRoot(world, rand, pos.below().relative(d), consumer, baseTreeFeatureConfig, d);
                }
                return true;
            }
        }
        return false;
    }

    public boolean placeRotatedRoot(LevelSimulatedReader world, RandomSource rand, BlockPos pos, BiConsumer<BlockPos, BlockState> consumer, TreeConfiguration baseTreeFeatureConfig, Direction direction) {
        BlockState state = baseTreeFeatureConfig.trunkProvider.getState(rand, pos);
        if (state.hasProperty(RotatedPillarBlock.AXIS)) {
            state = state.setValue(RotatedPillarBlock.AXIS, direction.getAxis());
        }
        if (rand.nextDouble() < 0.6 && validForExtendedRoot(world, pos)) {
            setBlock(world, pos.immutable(), state, consumer);
            int count = 0;
            while (rand.nextDouble() < 0.8 - count * 0.3) {
                count++;
                if (rand.nextDouble() < 0.7) {
                    direction = rand.nextDouble() < 0.5 ? direction.getClockWise() : direction.getCounterClockWise();
                    state = state.setValue(RotatedPillarBlock.AXIS, direction.getAxis());
                }
                pos = pos.relative(direction);
                if (TreeFeature.validTreePos(world, pos.below())) {
                    pos = pos.below();
                }
                if (validForExtendedRoot(world, pos)) {
                    setBlock(world, pos.immutable(), state, consumer);
                } else {
                    break;
                }
            }
            return true;
        }
        return false;
    }

    public boolean validForExtendedRoot(LevelSimulatedReader world, BlockPos pos) {
        return TreeFeature.validTreePos(world, pos) || world.isStateAtPosition(pos, (b) -> b.getBlock() == Blocks.DIRT || b.getBlock() == Blocks.GRASS_BLOCK);
    }

    public void setBlock(LevelSimulatedReader world, BlockPos pos, BlockState state, BiConsumer<BlockPos, BlockState> consumer) {
        consumer.accept(pos, state);
    }
}
