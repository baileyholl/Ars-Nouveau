package com.hollingsworth.arsnouveau.api.util;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.UsernameCache;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import static java.lang.Math.abs;

public class BlockUtil {

    public static BlockPos toPos(Vec3 vec) {
        return BlockPos.containing(vec.x, vec.y, vec.z);
    }

    public static boolean isTreeBlock(BlockState block) {
        return block.is(BlockTags.LEAVES) || block.is(BlockTags.LOGS);
    }

    public static boolean containsStateInRadius(Level world, BlockPos start, int radius, Class clazz) {
        for (double x = start.getX() - radius; x <= start.getX() + radius; x++) {
            for (double y = start.getY() - radius; y <= start.getY() + radius; y++) {
                for (double z = start.getZ() - radius; z <= start.getZ() + radius; z++) {
                    BlockPos pos = BlockPos.containing(x, y, z);
                    if (!pos.equals(start) && world.getBlockState(pos).getBlock().getClass().equals(clazz)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static double distanceFrom(BlockPos start, BlockPos end) {
        if (start == null || end == null)
            return 0;
        return Math.sqrt(Math.pow(start.getX() - end.getX(), 2) + Math.pow(start.getY() - end.getY(), 2) + Math.pow(start.getZ() - end.getZ(), 2));
    }

    public static double distanceFromCenter(BlockPos start, BlockPos end) {
        if (start == null || end == null)
            return 0;
        return distanceFrom(new Vec3(start.getX() + 0.5, start.getY() + 0.5, start.getZ() + 0.5), new Vec3(end.getX() + 0.5, end.getY() + 0.5, end.getZ() + 0.5));
    }

    public static double distanceFrom(Vec3 start, BlockPos end) {
        if (start == null || end == null)
            return 0;
        return Math.sqrt(Math.pow(start.x - end.getX(), 2) + Math.pow(start.y - end.getY(), 2) + Math.pow(start.z - end.getZ(), 2));
    }

    public static double distanceFrom(Vec3 start, Vec3 end) {
        return Math.sqrt(Math.pow(start.x - end.x, 2) + Math.pow(start.y - end.y, 2) + Math.pow(start.z - end.z, 2));
    }

    public static boolean destroyBlockSafely(Level world, BlockPos pos, boolean dropBlock, LivingEntity caster) {
        if (!(world instanceof ServerLevel))
            return false;
        Player playerEntity = caster instanceof Player ? (Player) caster : ANFakePlayer.getPlayer((ServerLevel) world);
        if (ANEventBus.post(new BlockEvent.BreakEvent(world, pos, world.getBlockState(pos), playerEntity)))
            return false;
        world.getBlockState(pos).getBlock().playerWillDestroy(world, pos, world.getBlockState(pos), playerEntity);
        return world.destroyBlock(pos, dropBlock);

    }

    public static boolean destroyRespectsClaim(Entity caster, Level world, BlockPos pos) {
        Player playerEntity = caster instanceof Player ? (Player) caster : ANFakePlayer.getPlayer((ServerLevel) world);
        return !ANEventBus.post(new BlockEvent.BreakEvent(world, pos, world.getBlockState(pos), playerEntity));
    }

    public static void safelyUpdateState(Level world, BlockPos pos, BlockState state) {
        if (!world.isOutsideBuildHeight(pos))
            world.sendBlockUpdated(pos, state, state, 3);
    }

    public static void safelyUpdateState(Level world, BlockPos pos) {
        safelyUpdateState(world, pos, world.getBlockState(pos));
    }

    public static boolean destroyBlockSafelyWithoutSound(Level world, BlockPos pos, boolean dropBlock) {
        return destroyBlockWithoutSound(world, pos, dropBlock, null);
    }

    public static boolean destroyBlockSafelyWithoutSound(Level world, BlockPos pos, boolean dropBlock, @Nullable LivingEntity caster) {
        if (!(world instanceof ServerLevel))
            return false;

        Player playerEntity = caster instanceof Player ? (Player) caster : ANFakePlayer.getPlayer((ServerLevel) world);
        if (ANEventBus.post(new BlockEvent.BreakEvent(world, pos, world.getBlockState(pos), playerEntity)))
            return false;

        return destroyBlockWithoutSound(world, pos, dropBlock);
    }

    private static boolean destroyBlockWithoutSound(Level world, BlockPos pos, boolean dropBlock) {
        return destroyBlockWithoutSound(world, pos, dropBlock, null);
    }

    private static boolean destroyBlockWithoutSound(Level world, BlockPos pos, boolean isMoving, @Nullable Entity entityIn) {
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
            if(cap != null){
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

    public static ItemStack getItemAdjacent(Level world, BlockPos pos, Predicate<ItemStack> matchPredicate) {
        ItemStack stack = ItemStack.EMPTY;
        for (IItemHandler inv : BlockUtil.getAdjacentInventories(world, pos)) {
            for (int i = 0; i < inv.getSlots(); ++i) {
                if (matchPredicate.test(inv.getStackInSlot(i)))
                    return inv.getStackInSlot(i);
            }
        }
        return stack;
    }

    public static boolean canBlockBeHarvested(SpellStats stats, Level world, BlockPos pos){
        return world.getBlockState(pos).getDestroySpeed(world, pos) >= 0 && SpellUtil.isCorrectHarvestLevel(getBaseHarvestLevel(stats), world.getBlockState(pos));
    }

    public static int getBaseHarvestLevel(SpellStats stats){
        return (int) (3 + stats.getAmpMultiplier());
    }

    public static List<BlockPos> getLine(int x0, int y0, int x1, int y1, float wd) {
        List<BlockPos> vects = new ArrayList<>();
        int dx = abs(x1 - x0), sx = x0 < x1 ? 1 : -1;
        int dy = abs(y1 - y0), sy = y0 < y1 ? 1 : -1;
        int err = dx - dy, e2, x2, y2;                          /* error value e_xy */
        float ed = dx + dy == 0 ? 1 : Mth.sqrt((float) dx * dx + (float) dy * dy);

        for (wd = (wd + 1) / 2; ; ) {                                   /* pixel loop */
            vects.add(new BlockPos(x0, 0, y0));
            e2 = err;
            x2 = x0;
            if (2 * e2 >= -dx) {                                           /* x step */
                for (e2 += dy, y2 = y0; e2 < ed * wd && (y1 != y2 || dx > dy); e2 += dx) {
                    vects.add(new BlockPos(x0, 0, y2 += sy));
                }
                if (x0 == x1) break;
                e2 = err;
                err -= dy;
                x0 += sx;
            }
            if (2 * e2 <= dy) {                                            /* y step */
                for (e2 = dx - e2; e2 < ed * wd && (x1 != x2 || dx < dy); e2 += dy) {
                    vects.add(new BlockPos(x2 += sx, 0, y0));
                }
                if (y0 == y1) break;
                err += dx;
                y0 += sy;
            }
        }
        return vects;
    }

    /**
     * Find the closest block near the points.
     *
     * @param world   the world.
     * @param point   the point where to search.
     * @param radiusX x search distance.
     * @param radiusY y search distance.
     * @param radiusZ z search distance.
     * @param height  check if blocks above the found block are air or block.
     * @return the coordinates of the found block.
     */
    @Nullable
    public static BlockPos scanForBlockNearPoint(final Level world, final BlockPos point, final int radiusX, final int radiusY, final int radiusZ, final int height) {
        @Nullable BlockPos closestCoords = null;
        double minDistance = Double.MAX_VALUE;

        for (int j = point.getY(); j <= point.getY() + radiusY; j++) {
            for (int i = point.getX() - radiusX; i <= point.getX() + radiusX; i++) {
                for (int k = point.getZ() - radiusZ; k <= point.getZ() + radiusZ; k++) {
                    if (wontSuffocate(world, i, j, k, height)) {
                        BlockPos tempCoords = new BlockPos(i, j, k);

                        if (world.getBlockState(tempCoords.below()).isSolid() || world.getBlockState(tempCoords.below(2)).isSolid()) {
                            final double distance = getDistanceSquared(tempCoords, point);
                            if (closestCoords == null || distance < minDistance) {
                                closestCoords = tempCoords;
                                minDistance = distance;
                            }
                        }
                    }
                }
            }
        }
        return closestCoords;
    }

    /**
     * Checks if the blocks above that point are all non-motion blocking
     *
     * @param world  the world we check on.
     * @param x      the x coordinate.
     * @param y      the y coordinate.
     * @param z      the z coordinate.
     * @param height the number of blocks above to check.
     * @return true if no blocks block motion
     */
    private static boolean wontSuffocate(Level world, final int x, final int y, final int z, final int height) {
        for (int dy = 0; dy < height; dy++) {
            final BlockState state = world.getBlockState(new BlockPos(x, y + dy, z));
            if (state.blocksMotion()) {
                return false;
            }
        }
        return true;
    }


    /**
     * Squared distance between two BlockPos.
     *
     * @param block1 position one.
     * @param block2 position two.
     * @return squared distance.
     */
    public static long getDistanceSquared(BlockPos block1, BlockPos block2) {
        final long xDiff = (long) block1.getX() - block2.getX();
        final long yDiff = (long) block1.getY() - block2.getY();
        final long zDiff = (long) block1.getZ() - block2.getZ();

        final long result = xDiff * xDiff + yDiff * yDiff + zDiff * zDiff;
        if (result < 0) {
            throw new IllegalStateException("max-sqrt is to high! Failure to catch overflow with "
                    + xDiff + " | " + yDiff + " | " + zDiff);
        }
        return result;
    }

    public static void updateObservers(Level level, BlockPos pos ){
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
     * @param world The world the block is in.
     * @param pos The position of the block.
     * @param mainhand The main hand item that the player is supposibly holding.
     * @param source The UUID of the breaking player.
     * @return If the block was successfully broken.
     */
    public static boolean breakExtraBlock(ServerLevel world, BlockPos pos, ItemStack mainhand, @Nullable UUID source, boolean bypassToolCheck) {
        BlockState blockstate = world.getBlockState(pos);
        FakePlayer player;
        if (source != null) {
            player = FakePlayerFactory.get(world, new GameProfile(source, UsernameCache.getLastKnownUsername(source)));
            Player realPlayer = world.getPlayerByUUID(source);
            if (realPlayer != null) player.setPos(realPlayer.position());
        } else player = FakePlayerFactory.getMinecraft(world);
        player.getInventory().items.set(player.getInventory().selected, mainhand);
        //player.setPos(pos.getX(), pos.getY(), pos.getZ());

        if (blockstate.getDestroySpeed(world, pos) < 0 || (!blockstate.canHarvestBlock(world, pos, player) && !bypassToolCheck)){
            return false;
        }

        GameType type = player.getAbilities().instabuild ? GameType.CREATIVE : GameType.SURVIVAL;
        boolean canceled = net.neoforged.neoforge.common.CommonHooks.fireBlockBreak(world, type, player, pos, blockstate).isCanceled();
        if (canceled) {
            return false;
        }
        BlockEntity tileentity = world.getBlockEntity(pos);
        Block block = blockstate.getBlock();
        if ((block instanceof CommandBlock || block instanceof StructureBlock || block instanceof JigsawBlock) && !player.canUseGameMasterBlocks()) {
            world.sendBlockUpdated(pos, blockstate, blockstate, 3);
            return false;
        } else if (player.blockActionRestricted(world, pos, type)) {
            return false;
        } else {
            if (player.getAbilities().instabuild) {
                removeBlock(world, player, pos, false);
                return true;
            } else {
                ItemStack copyMain = mainhand.copy();
                boolean canHarvest = blockstate.canHarvestBlock(world, pos, player) || bypassToolCheck;
                mainhand.mineBlock(world, blockstate, pos, player);
                if (mainhand.isEmpty() && !copyMain.isEmpty()){
                    net.neoforged.neoforge.event.EventHooks.onPlayerDestroyItem(player, copyMain, InteractionHand.MAIN_HAND);
                }
                boolean removed = removeBlock(world, player, pos, canHarvest);

                if (removed && canHarvest) {
                    block.playerDestroy(world, player, pos, blockstate, tileentity, copyMain);
                }

                return true;
            }
        }

    }

    /**
     * Vanilla Copy:
     * @param world The world
     * @param player The removing player
     * @param pos The block location
     * @param canHarvest If the player can actually harvest this block.
     * @return If the block was actually removed.
     */
    public static boolean removeBlock(ServerLevel world, ServerPlayer player, BlockPos pos, boolean canHarvest) {
        BlockState state = world.getBlockState(pos);
        boolean removed = state.onDestroyedByPlayer(world, pos, player, canHarvest, world.getFluidState(pos));
        if (removed) state.getBlock().destroy(world, pos, state);
        return removed;
    }

    private BlockUtil() {
    }

}
