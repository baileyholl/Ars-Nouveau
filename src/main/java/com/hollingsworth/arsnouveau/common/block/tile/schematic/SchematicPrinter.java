package com.hollingsworth.arsnouveau.common.block.tile.schematic;

import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.common.util.Log;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BambooSaplingBlock;
import net.minecraft.world.level.block.BaseCoralPlantTypeBlock;
import net.minecraft.world.level.block.BasePressurePlateBlock;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.BaseTorchBlock;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.BigDripleafBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.GrindstoneBlock;
import net.minecraft.world.level.block.GrowingPlantBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class SchematicPrinter {

    public enum PrintStage {
        BLOCKS, DEFERRED_BLOCKS, ENTITIES
    }

    private static final List<IntegerProperty> ZEROED_PROPERTIES = List.of(
            BlockStateProperties.AGE_1, BlockStateProperties.AGE_2, BlockStateProperties.AGE_3, BlockStateProperties.AGE_4,
            BlockStateProperties.AGE_5, BlockStateProperties.AGE_7, BlockStateProperties.AGE_15, BlockStateProperties.AGE_25,
            BlockStateProperties.LEVEL_HONEY, BlockStateProperties.HATCH, BlockStateProperties.STAGE, BlockStateProperties.LEVEL_COMPOSTER);

    private boolean schematicLoaded;
    private boolean isErrored;
    private SchematicLevel blockReader;
    private BlockPos schematicAnchor;

    private BlockPos currentPos;
    private int printingEntityIndex = -1;
    private PrintStage printStage = PrintStage.BLOCKS;
    private final Deque<BlockPos> deferredBlocks = new ArrayDeque<>();

    public void fromTag(CompoundTag compound, boolean clientPacket) {
        currentPos = NBTUtil.getNullablePos(compound, "CurrentPos");
        if (clientPacket) {
            schematicAnchor = NBTUtil.getNullablePos(compound, "Anchor");
            schematicLoaded = schematicAnchor != null;
        }
        printingEntityIndex = compound.getInt("EntityProgress");
        if (compound.contains("PrintStage"))
            printStage = PrintStage.valueOf(compound.getString("PrintStage"));
        deferredBlocks.clear();
        for (long packed : compound.getLongArray("DeferredBlocks")) {
            deferredBlocks.add(BlockPos.of(packed));
        }
    }

    public void write(CompoundTag compound) {
        NBTUtil.storeNullableBlockPos(compound, "CurrentPos", currentPos);
        NBTUtil.storeNullableBlockPos(compound, "Anchor", schematicAnchor);
        compound.putInt("EntityProgress", printingEntityIndex);
        compound.putString("PrintStage", printStage.name());
        compound.putLongArray("DeferredBlocks", deferredBlocks.stream().mapToLong(BlockPos::asLong).toArray());
    }

    public void loadSchematic(StructureTemplate template, BlockPos anchor, StructurePlaceSettings settings, Level originalWorld) {
        schematicAnchor = anchor;
        isErrored = false;
        blockReader = new SchematicLevel(anchor, originalWorld);
        try {
            template.placeInWorld(blockReader, anchor, anchor, settings, blockReader.getRandom(), Block.UPDATE_CLIENTS);
        } catch (Exception e) {
            Log.getLogger().error("Failed to load schematic for printing", e);
            schematicLoaded = true;
            isErrored = true;
            return;
        }
        BlockPos extraBounds = StructureTemplate.calculateRelativePosition(settings, new BlockPos(template.getSize()).offset(-1, -1, -1));
        BoundingBox bounds = blockReader.getBounds().encapsulate(extraBounds);
        printingEntityIndex = -1;
        printStage = PrintStage.BLOCKS;
        deferredBlocks.clear();
        currentPos = new BlockPos(bounds.minX() - 1, bounds.minY(), bounds.minZ());
        schematicLoaded = true;
    }

    public void resetSchematic() {
        schematicLoaded = false;
        schematicAnchor = null;
        isErrored = false;
        currentPos = null;
        blockReader = null;
        printingEntityIndex = -1;
        printStage = PrintStage.BLOCKS;
        deferredBlocks.clear();
    }

    public boolean isLoaded() {
        return schematicLoaded;
    }

    public boolean isErrored() {
        return isErrored;
    }

    @Nullable
    public BlockPos getCurrentTarget() {
        if (!schematicLoaded || isErrored || currentPos == null)
            return null;
        return schematicAnchor.offset(currentPos);
    }

    public PrintStage getPrintStage() {
        return printStage;
    }

    @Nullable
    public BlockPos getAnchor() {
        return schematicAnchor;
    }

    public boolean isWorldEmpty() {
        return blockReader.getAllPositions().isEmpty();
    }

    @FunctionalInterface
    public interface BlockTargetHandler {
        void handle(BlockPos target, BlockState blockState, @Nullable BlockEntity blockEntity);
    }

    @FunctionalInterface
    public interface EntityTargetHandler {
        void handle(BlockPos target, Entity entity);
    }

    public void handleCurrentTarget(BlockTargetHandler blockHandler, EntityTargetHandler entityHandler) {
        BlockPos target = getCurrentTarget();
        if (printStage == PrintStage.ENTITIES) {
            entityHandler.handle(target, blockReader.getEntityList().get(printingEntityIndex));
        } else {
            blockHandler.handle(target, setZeroAge(blockReader.getBlockState(target)), blockReader.getBlockEntity(target));
        }
    }

    @FunctionalInterface
    public interface PlacementPredicate {
        boolean shouldPlace(BlockPos target, BlockState blockState, @Nullable BlockEntity blockEntity,
                            BlockState toReplace, @Nullable BlockState toReplaceOther, boolean isNormalCube);
    }

    @Nullable
    public static BlockPos getOtherHalf(BlockState state, BlockPos pos) {
        if (state.hasProperty(BlockStateProperties.BED_PART) && state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)
                && state.getValue(BlockStateProperties.BED_PART) == BedPart.FOOT)
            return pos.relative(state.getValue(BlockStateProperties.HORIZONTAL_FACING));
        if (state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)
                && state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER)
            return pos.above();
        return null;
    }

    public boolean isCurrentTargetLoaded(Level world) {
        BlockPos target = getCurrentTarget();
        if (target == null || !world.isLoaded(target))
            return false;
        if (printStage == PrintStage.ENTITIES)
            return true;
        BlockPos otherPos = getOtherHalf(blockReader.getBlockState(target), target);
        return otherPos == null || world.isLoaded(otherPos);
    }

    public boolean shouldPlaceCurrent(Level world, PlacementPredicate predicate) {
        if (world == null)
            return false;
        if (printStage == PrintStage.ENTITIES) {
            Entity entity = blockReader.getEntityList().get(printingEntityIndex);
            return world.getEntities(entity.getType(), new AABB(entity.blockPosition()), e -> e.position().distanceToSqr(entity.position()) < 1.0E-4).isEmpty();
        }
        return shouldPlaceBlock(world, predicate, getCurrentTarget());
    }

    public boolean shouldPlaceBlock(Level world, PlacementPredicate predicate, BlockPos pos) {
        BlockState state = setZeroAge(blockReader.getBlockState(pos));
        BlockPos otherPos = getOtherHalf(state, pos);
        if (!world.isLoaded(pos) || !world.getWorldBorder().isWithinBounds(pos) || (otherPos != null && !world.isLoaded(otherPos)))
            return false;

        BlockState toReplace = world.getBlockState(pos);
        BlockState toReplaceOther = otherPos == null ? null : world.getBlockState(otherPos);
        if (toReplace == state)
            return false;
        if (toReplace.getDestroySpeed(world, pos) == -1
                || (toReplaceOther != null && toReplaceOther.getDestroySpeed(world, otherPos) == -1))
            return false;

        BlockEntity blockEntity = blockReader.getBlockEntity(pos);
        return predicate.shouldPlace(pos, state, blockEntity, toReplace, toReplaceOther, state.isRedstoneConductor(blockReader, pos));
    }

    public ItemRequirement getCurrentRequirement() {
        if (printStage == PrintStage.ENTITIES)
            return ItemRequirement.of(blockReader.getEntityList().get(printingEntityIndex));
        BlockPos target = getCurrentTarget();
        return ItemRequirement.of(setZeroAge(blockReader.getBlockState(target)), blockReader.getBlockEntity(target));
    }

    public boolean advanceCurrentPos() {
        List<Entity> entities = blockReader.getEntityList();
        do {
            if (printStage == PrintStage.BLOCKS) {
                while (tryAdvanceCurrentPos()) {
                    deferredBlocks.add(currentPos);
                }
            }

            if (printStage == PrintStage.DEFERRED_BLOCKS) {
                if (deferredBlocks.isEmpty()) {
                    printStage = PrintStage.ENTITIES;
                } else {
                    currentPos = deferredBlocks.poll();
                }
            }

            if (printStage == PrintStage.ENTITIES) {
                if (printingEntityIndex + 1 >= entities.size())
                    return false;
                printingEntityIndex++;
                currentPos = entities.get(printingEntityIndex).blockPosition().subtract(schematicAnchor);
            }
        } while (!blockReader.getBounds().isInside(currentPos));
        return true;
    }

    public boolean tryAdvanceCurrentPos() {
        BoundingBox bounds = blockReader.getBounds();
        int x = currentPos.getX() + 1;
        int y = currentPos.getY();
        int z = currentPos.getZ();
        if (x > bounds.maxX()) {
            x = bounds.minX();
            z++;
        }
        if (z > bounds.maxZ()) {
            z = bounds.minZ();
            y++;
        }
        if (y > bounds.maxY()) {
            printStage = PrintStage.DEFERRED_BLOCKS;
            return false;
        }
        currentPos = new BlockPos(x, y, z);
        return shouldDeferBlock(blockReader.getBlockState(getCurrentTarget()));
    }

    public static boolean shouldDeferBlock(BlockState state) {
        Block block = state.getBlock();
        return state.hasProperty(BlockStateProperties.HANGING)
                || state.is(BlockTags.BANNERS)
                || state.is(BlockTags.BEDS)
                || state.is(BlockTags.DOORS)
                || block instanceof LadderBlock
                || block instanceof BaseTorchBlock
                || block instanceof SignBlock
                || block instanceof BasePressurePlateBlock
                || (block instanceof FaceAttachedHorizontalDirectionalBlock && !(block instanceof GrindstoneBlock))
                || block instanceof BaseRailBlock
                || block instanceof DiodeBlock
                || block instanceof RedStoneWireBlock
                || block instanceof CarpetBlock
                || block instanceof BellBlock
                || block instanceof CocoaBlock
                || block instanceof FlowerPotBlock
                || block instanceof BambooSaplingBlock
                || block instanceof BigDripleafBlock
                || block instanceof VineBlock
                || block instanceof GrowingPlantBlock
                || block instanceof CakeBlock
                || block instanceof BaseCoralPlantTypeBlock;
    }

    public static BlockState setZeroAge(BlockState state) {
        if (state.is(BlockTags.CAULDRONS))
            return Blocks.CAULDRON.defaultBlockState();
        for (IntegerProperty property : ZEROED_PROPERTIES) {
            if (state.hasProperty(property))
                state = state.setValue(property, 0);
        }
        if (state.hasProperty(BlockStateProperties.EXTENDED))
            state = state.setValue(BlockStateProperties.EXTENDED, false);
        return state;
    }

    public void sendBlockUpdates(Level level) {
        BoundingBox bounds = blockReader.getBounds();
        BlockPos.betweenClosedStream(bounds.inflatedBy(1))
                .filter(pos -> !bounds.isInside(pos))
                .map(pos -> pos.offset(schematicAnchor))
                .filter(pos -> level.isLoaded(pos) && level.getFluidState(pos).is(Fluids.WATER))
                .forEach(pos -> level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level)));
    }
}
