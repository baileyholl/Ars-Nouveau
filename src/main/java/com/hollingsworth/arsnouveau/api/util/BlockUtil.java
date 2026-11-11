package com.hollingsworth.arsnouveau.api.util;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GameMasterBlock;
import net.minecraft.world.level.block.ObserverBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.UsernameCache;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BlockUtil {

    // Use level sensitive version
    @Deprecated(forRemoval = true)
    public static double distanceFrom(BlockPos start, BlockPos end) {
        if (start == null || end == null)
            return 0;
        return Math.sqrt(Math.pow(start.getX() - end.getX(), 2) + Math.pow(start.getY() - end.getY(), 2) + Math.pow(start.getZ() - end.getZ(), 2));
    }

    public static double distanceFrom(Level level, BlockPos start, BlockPos end) {
        return distanceFrom(start, end);
    }

    // Use level sensitive version
    @Deprecated(forRemoval = true)
    public static double distanceFromCenter(BlockPos start, BlockPos end) {
        if (start == null || end == null)
            return 0;
        return distanceFrom(new Vec3(start.getX() + 0.5, start.getY() + 0.5, start.getZ() + 0.5), new Vec3(end.getX() + 0.5, end.getY() + 0.5, end.getZ() + 0.5));
    }

    public static double distanceFromCenter(Level level, BlockPos start, BlockPos end) {
        if (start == null || end == null)
            return 0;
        return distanceFrom(level, new Vec3(start.getX() + 0.5, start.getY() + 0.5, start.getZ() + 0.5), new Vec3(end.getX() + 0.5, end.getY() + 0.5, end.getZ() + 0.5));
    }

    // Use level sensitive version
    @Deprecated(forRemoval = true)
    public static double distanceFrom(Vec3 start, BlockPos end) {
        if (start == null || end == null)
            return 0;
        return Math.sqrt(Math.pow(start.x - end.getX(), 2) + Math.pow(start.y - end.getY(), 2) + Math.pow(start.z - end.getZ(), 2));
    }

    public static double distanceFrom(Level level, Vec3 start, BlockPos end) {
        return distanceFrom(start, end);
    }

    // Use level sensitive version
    @Deprecated(forRemoval = true)
    public static double distanceFrom(Vec3 start, Vec3 end) {
        return Math.sqrt(Math.pow(start.x - end.x, 2) + Math.pow(start.y - end.y, 2) + Math.pow(start.z - end.z, 2));
    }

    public static double distanceFrom(Level level, Vec3 start, Vec3 end) {
        return distanceFrom(start, end);
    }

    public static boolean destroyBlockSafely(Level world, BlockPos pos, boolean dropBlock, LivingEntity caster) {
        if (!(world instanceof ServerLevel serverLevel))
            return false;
        Player playerEntity = ANFakePlayer.getOrFakePlayer(serverLevel, caster);
        if (ANEventBus.post(new BlockEvent.BreakEvent(world, pos, world.getBlockState(pos), playerEntity)))
            return false;
        world.getBlockState(pos).getBlock().playerWillDestroy(world, pos, world.getBlockState(pos), playerEntity);
        return world.destroyBlock(pos, dropBlock);
    }

    public static boolean destroyRespectsClaim(LivingEntity caster, Level world, BlockPos pos) {
        if (!(world instanceof ServerLevel serverLevel))
            return false;

        Player playerEntity = ANFakePlayer.getOrFakePlayer(serverLevel, caster);
        return !ANEventBus.post(new BlockEvent.BreakEvent(world, pos, world.getBlockState(pos), playerEntity));
    }

    public static boolean canUUIDBreak(@Nullable UUID uuid, Level world, BlockPos pos) {
        if (!(world instanceof ServerLevel serverLevel)) {
            return false;
        }

        Player playerEntity = ANFakePlayer.getPlayer(serverLevel, uuid);
        return !ANEventBus.post(new BlockEvent.BreakEvent(world, pos, world.getBlockState(pos), playerEntity));
    }

    public static void safelyUpdateState(Level world, BlockPos pos, BlockState state) {
        if (!world.isOutsideBuildHeight(pos))
            world.sendBlockUpdated(pos, state, state, 3);
    }

    public static void safelyUpdateState(Level world, BlockPos pos) {
        safelyUpdateState(world, pos, world.getBlockState(pos));
    }

    @Deprecated
    public static boolean destroyBlockSafelyWithoutSound(Level world, BlockPos pos, boolean dropBlock) {
        return destroyBlockWithoutSound(world, pos, dropBlock, null);
    }

    public static boolean destroyBlockSafelyWithoutSound(Level world, BlockPos pos, boolean dropBlock, LivingEntity caster) {
        if (!(world instanceof ServerLevel serverLevel))
            return false;

        Player playerEntity = ANFakePlayer.getOrFakePlayer(serverLevel, caster);
        if (ANEventBus.post(new BlockEvent.BreakEvent(world, pos, world.getBlockState(pos), playerEntity)))
            return false;

        return destroyBlockWithoutSound(world, pos, dropBlock, playerEntity);
    }

    @Deprecated
    private static boolean destroyBlockWithoutSound(Level world, BlockPos pos, boolean dropBlock) {
        return destroyBlockWithoutSound(world, pos, dropBlock, null);
    }

    private static boolean destroyBlockWithoutSound(Level world, BlockPos pos, boolean isMoving, LivingEntity entityIn) {
        BlockState blockstate = world.getBlockState(pos);
        if (blockstate.isAir()) {
            return false;
        } else {
            FluidState ifluidstate = world.getFluidState(pos);
            if (isMoving) {
                BlockEntity tileentity = blockstate.hasBlockEntity() ? world.getBlockEntity(pos) : null;
                Block.dropResources(blockstate, world, pos, tileentity, entityIn, ItemStack.EMPTY);
            }

            return world.setBlock(pos, ifluidstate.createLegacyBlock(), 3);
        }
    }

    public static List<IItemHandler> getAdjacentInventories(Level world, BlockPos pos) {
        if (world == null || pos == null) return new ArrayList<>();
        ArrayList<IItemHandler> iInventories = new ArrayList<>();
        for (Direction d : Direction.values()) {
            var cap = world.getCapability(Capabilities.ItemHandler.BLOCK, pos.relative(d), null);
            if (cap != null) {
                iInventories.add(cap);
            }
        }

        return iInventories;
    }

    public static ItemStack insertItemAdjacent(Level world, BlockPos pos, ItemStack stack) {
        for (IItemHandler i : BlockUtil.getAdjacentInventories(world, pos)) {
            if (stack == ItemStack.EMPTY || stack == null)
                break;
            stack = ItemHandlerHelper.insertItemStacked(i, stack, false);
        }
        return stack;
    }

    public static boolean canBlockBeHarvested(SpellStats stats, Level world, BlockPos pos) {
        return world.getBlockState(pos).getDestroySpeed(world, pos) >= 0 && SpellUtil.isCorrectHarvestLevel(getBaseHarvestLevel(stats), world.getBlockState(pos));
    }

    public static int getBaseHarvestLevel(SpellStats stats) {
        return (int) (3 + stats.getAmpMultiplier());
    }

    public static void updateObservers(Level level, BlockPos pos) {
        for (Direction d : Direction.values()) {
            BlockPos adjacentPos = pos.relative(d);
            if (level.getBlockState(adjacentPos).getBlock() instanceof ObserverBlock) {
                BlockState observer = level.getBlockState(adjacentPos);
                if (adjacentPos.relative(observer.getValue(ObserverBlock.FACING)).equals(pos)) { // Make sure the observer is facing us.
                    level.scheduleTick(pos.relative(d), level.getBlockState(pos.relative(d)).getBlock(), 2);
                }
            }
        }
    }

    /**
     * Vanilla Copy:  <br>
     * Attempts to harvest a block as if the player with the given uuid
     * harvested it while holding the passed item.
     *
     * @param world    The world the block is in.
     * @param pos      The position of the block.
     * @param mainhand The main hand item that the player is supposibly holding.
     * @param source   The UUID of the breaking player.
     * @return If the block was successfully broken.
     */
    public static boolean breakExtraBlock(ServerLevel level, BlockPos pos, ItemStack mainhand, @Nullable UUID source, boolean bypassTool) {
        BlockState state = level.getBlockState(pos);
        FakePlayer player = ANFakePlayer.getPlayer(level);
        if (source != null) {
            var username = UsernameCache.getLastKnownUsername(source);
            if (username != null) {
                player = FakePlayerFactory.get(level, new GameProfile(source, username));
                Player realPlayer = level.getPlayerByUUID(source);
                if (realPlayer != null) {
                    // Move the fakeplayer to the position of the real player, if one is known
                    player.setPos(realPlayer.position());
                }
            }
        }

        player.getInventory().items.set(player.getInventory().selected, mainhand);

        if (!bypassTool && (state.getDestroySpeed(level, pos) < 0 || !state.canHarvestBlock(level, pos, player))) {
            return false;
        }

        GameType type = player.getAbilities().instabuild ? GameType.CREATIVE : GameType.SURVIVAL;
        BlockEvent.BreakEvent exp = CommonHooks.fireBlockBreak(level, type, player, pos, state);
        if (exp.isCanceled()) {
            return false;
        } else {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            Block block = state.getBlock();
            if (block instanceof GameMasterBlock && !player.canUseGameMasterBlocks()) {
                level.sendBlockUpdated(pos, state, state, 3);
                return false;
            } else if (player.blockActionRestricted(level, pos, type)) {
                return false;
            } else {
                BlockState newState = block.playerWillDestroy(level, pos, state, player);
                if (player.getAbilities().instabuild) {
                    removeBlock(level, player, pos, newState, false);
                    return true;
                } else {
                    ItemStack tool = player.getMainHandItem();
                    ItemStack toolCopy = tool.copy();
                    boolean canHarvest = bypassTool || newState.canHarvestBlock(level, pos, player);
                    tool.mineBlock(level, newState, pos, player);
                    boolean removed = removeBlock(level, player, pos, newState, canHarvest);

                    if (canHarvest && removed) {
                        block.playerDestroy(level, player, pos, newState, blockEntity, toolCopy);
                    }

                    if (tool.isEmpty() && !toolCopy.isEmpty()) {
                        EventHooks.onPlayerDestroyItem(player, toolCopy, InteractionHand.MAIN_HAND);
                    }

                    return true;
                }
            }
        }

    }

    /**
     * Vanilla Copy: {@link ServerPlayerGameMode#removeBlock(BlockPos, BlockState, boolean)}
     *
     * @param level      The world
     * @param player     The removing player
     * @param pos        The block location
     * @param canHarvest If the player can actually harvest this block.
     * @return If the block was actually removed.
     */
    public static boolean removeBlock(ServerLevel level, ServerPlayer player, BlockPos pos, BlockState state, boolean canHarvest) {
        boolean removed = state.onDestroyedByPlayer(level, pos, player, canHarvest, level.getFluidState(pos));
        if (removed) {
            state.getBlock().destroy(level, pos, state);
        }
        return removed;
    }

    private BlockUtil() {
    }

}
