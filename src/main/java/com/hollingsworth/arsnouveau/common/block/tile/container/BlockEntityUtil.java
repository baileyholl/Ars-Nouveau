/*
 * MIT License
 *
 * Copyright 2020 klikli-dev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package com.hollingsworth.arsnouveau.common.block.tile.container;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.server.ServerLifecycleHooks;

public class BlockEntityUtil {
    //region Static Methods

    public static boolean isLoaded(Level level, GlobalBlockPos pos){
        if (pos == null)
            return false;

        if (level.dimension() == pos.getDimensionKey()) {
            return level.isLoaded(pos.getPos());
        }
        if (level.isClientSide) //can only access other dimensions on the server.
            return false;

        Level dimensionWorld = ServerLifecycleHooks.getCurrentServer().getLevel(pos.getDimensionKey());
        if (dimensionWorld != null)
            return dimensionWorld.isLoaded(pos.getPos());

        return false;
    }

    /**
     * Gets the block entity from the given global position.
     *
     * @param level the level. Returns null if the dimension is unloaded.
     * @param pos   the global position.
     * @return the block entity or null.
     */
    public static BlockEntity get(Level level, GlobalBlockPos pos) {
        if (pos == null)
            return null;

        if (level.dimension() == pos.getDimensionKey()) {
            return getWorldTileEntityUnchecked(level, pos.getPos());
        }
        if (level.isClientSide) //can only access other dimensions on the server.
            return null;

        Level dimensionWorld = ServerLifecycleHooks.getCurrentServer().getLevel(pos.getDimensionKey());
        if (dimensionWorld != null)
            return getWorldTileEntityUnchecked(dimensionWorld, pos.getPos());

        return null;
    }

    static BlockEntity getWorldTileEntityUnchecked(Level level, BlockPos pos) {
        if (!level.isLoaded(pos)) {
            return null;
        } else {
            return level.getChunkAt(pos).getBlockEntity(pos, LevelChunk.EntityCreationType.IMMEDIATE);
        }
    }


    /**
     * Updates the block entity at the given position (mark dirty & send updates)
     *
     * @param level the level to update
     * @param pos   the position to update
     */
    public static void updateTile(Level level, BlockPos pos) {
        if (level == null || level.isClientSide || !level.isLoaded(pos)) {
            return;
        }

        BlockState state = level.getBlockState(pos);
        level.sendBlockUpdated(pos, state, state, 2);
        level.blockEntityChanged(pos);
    }

    /**
     * Checks all faces of a block entity for the given capability.
     *
     * @param blockEntity the block entity to check.
     * @param capability  the capability to check for.
     * @return true if the capability is found on any face.
     */
    public static boolean hasCapabilityOnAnySide(BlockEntity blockEntity, Capability<?> capability) {
        for (Direction face : Direction.values()) {
            if (blockEntity.getCapability(capability, face).isPresent())
                return true;
        }
        return false;
    }

    /**
     * Creates the item entity with nbt from the block entity. Default pickup delay is set.
     *
     * @param itemStack   the stack to drop.
     * @param blockEntity the block entity to get nbt from.
     * @return the item entity.
     */
    public static ItemEntity getDroppedItemWithNbt(ItemStack itemStack, BlockEntity blockEntity) {
        CompoundTag CompoundTag = blockEntity.saveWithoutMetadata();
        if (!CompoundTag.isEmpty()) {
            itemStack.addTagElement("BlockEntityTag", CompoundTag);
        }
        ItemEntity itementity =
                new ItemEntity(blockEntity.getLevel(), blockEntity.getBlockPos().getX(), blockEntity.getBlockPos().getY(),
                        blockEntity.getBlockPos().getZ(), itemStack);
        itementity.setDefaultPickUpDelay();
        return itementity;
    }

    /**
     * Handles the common use case of dropping self with block entity nbt on block change during replace.
     *
     * @param block    the current block.
     * @param state    the old state.
     * @param level    the level
     * @param pos      the position.
     * @param newState the new state
     */
    public static void onBlockChangeDropWithNbt(Block block, BlockState state, Level level, BlockPos pos,
                                                BlockState newState) {
        if (state.getBlock() != newState.getBlock()) {
            if (!level.isClientSide) {
                BlockEntity blockEntity = level.getBlockEntity(pos);
                if (blockEntity != null) {
                    level.addFreshEntity(BlockEntityUtil.getDroppedItemWithNbt(new ItemStack(block), blockEntity));
                }
            }
            level.updateNeighbourForOutputSignal(pos, block);
        }
    }

    /**
     * Handles the common use case of giving self as item with block entity nbt.
     *
     * @param block the current block.
     * @param level the level
     * @param pos   the position.
     */
    public static ItemStack getItemWithNbt(Block block, BlockGetter level, BlockPos pos) {
        ItemStack itemStack = new ItemStack(block);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        CompoundTag CompoundTag = blockEntity.serializeNBT();
        if (!CompoundTag.isEmpty()) {
            itemStack.addTagElement("BlockEntityTag", CompoundTag);
        }

        return itemStack;
    }
    //endregion Static Methods
}
