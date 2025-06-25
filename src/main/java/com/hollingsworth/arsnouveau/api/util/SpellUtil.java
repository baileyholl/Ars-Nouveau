package com.hollingsworth.arsnouveau.api.util;

import com.hollingsworth.arsnouveau.api.event.SpellCastEvent;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.FakePlayer;

import java.util.*;
import java.util.function.Predicate;

public class SpellUtil {

    public static SpellCastEvent postEvent(SpellCastEvent e) {
        return NeoForge.EVENT_BUS.post(e);
    }

    public static List<BlockPos> calcAOEBlocks(LivingEntity caster, BlockPos origin, BlockHitResult mop, int aoeBonus) {
        return calcAOEBlocks(caster, origin, mop, 1 + aoeBonus, 1 + aoeBonus, 1, -1);
    }

    public static List<BlockPos> calcAOEBlocks(LivingEntity caster, BlockPos origin, BlockHitResult mop, double aoeBonus) {
        return calcAOEBlocks(caster, origin, mop, (int) (1 + Math.floor(aoeBonus)), (int) (1 + Math.ceil(aoeBonus)), 1, -1);
    }

    public static List<BlockPos> calcAOEBlocks(LivingEntity caster, BlockPos origin, BlockHitResult mop, SpellStats stats) {
        double aoeBonus = stats.getAoeMultiplier();
        int pierceBonus = stats.getBuffCount(AugmentPierce.INSTANCE);
        return calcAOEBlocks(caster, origin, mop, aoeBonus, pierceBonus);
    }

    public static boolean isCorrectHarvestLevel(int strength, BlockState state) {
        Tier tier = switch (strength) {
            case 2:
                yield Tiers.STONE;
            case 3:
                yield Tiers.IRON;
            case 4:
                yield Tiers.DIAMOND;
            case 5:
                yield Tiers.NETHERITE;
            default:
                yield Tiers.WOOD;
        };
        if (strength > 5)
            tier = Tiers.NETHERITE;
        return !BuiltInRegistries.BLOCK.getOrCreateTag(tier.getIncorrectBlocksForDrops()).contains(state.getBlockHolder());
    }


    public static List<BlockPos> calcAOEBlocks(LivingEntity caster, BlockPos origin, BlockHitResult mop, int aoeBonus, int pierceBonus) {
        return calcAOEBlocks(caster, origin, mop, 1 + aoeBonus, 1 + aoeBonus, 1 + pierceBonus, -1);
    }

    public static List<BlockPos> calcAOEBlocks(LivingEntity caster, BlockPos origin, BlockHitResult mop, double aoeBonus, int pierceBonus) {
        return calcAOEBlocks(caster, origin, mop, (int) (1 + Math.floor(aoeBonus)), (int) (1 + Math.ceil(aoeBonus)), 1 + pierceBonus, -1);
    }

    public static List<BlockPos> calcAOEBlocks(LivingEntity caster, BlockPos origin, BlockHitResult mop, int width, int height, int depth, int distance) {
        Vec3i hitVec = caster.getDirection().getNormal();
        if (caster instanceof FakePlayer) { // Do I know why I need this? No. But I do, or else spell turrets break on the wrong plane.
            mop = new BlockHitResult(mop.getLocation(), mop.getDirection(), mop.getBlockPos(), false);
        }

        return calcAOEBlocks(hitVec, origin, mop, width, height, depth, distance);
    }

    public static List<BlockPos> calcAOEBlocks(Vec3 hitVec, BlockPos origin, BlockHitResult mop, int width, int height, int depth, int distance) {
        return calcAOEBlocks(Direction.getNearest(hitVec.x, hitVec.y, hitVec.z).getOpposite().getNormal(), origin, mop, width, height, depth, distance);
    }

    // https://github.com/SlimeKnights/TinkersConstruct/blob/1.12/src/main/java/slimeknights/tconstruct/library/utils/ToolHelper.java
    public static List<BlockPos> calcAOEBlocks(Vec3i facingVec, BlockPos origin, BlockHitResult mop, int width, int height, int depth, int distance) {
        // we know the block and we know which side of the block we're hitting. time to calculate the depth along the different axes
        int x, y, z;
        BlockPos start = origin;
        switch (mop.isInside() ? Direction.DOWN : mop.getDirection()) {
            case DOWN, UP -> {
                // x y depends on the angle we look?
                x = facingVec.getX() * height + facingVec.getZ() * width;
                y = mop.getDirection().getAxisDirection().getStep() * -depth;
                z = facingVec.getX() * width + facingVec.getZ() * height;
                start = start.offset(-x / 2, 0, -z / 2);
                if (x % 2 == 0) {
                    if (x > 0 && mop.getLocation().x - mop.getBlockPos().getX() > 0.5d) {
                        start = start.offset(1, 0, 0);
                    } else if (x < 0 && mop.getLocation().x - mop.getBlockPos().getX() < 0.5d) {
                        start = start.offset(-1, 0, 0);
                    }
                }
                if (z % 2 == 0) {
                    if (z > 0 && mop.getLocation().z - mop.getBlockPos().getZ() > 0.5d) {
                        start = start.offset(0, 0, 1);
                    } else if (z < 0 && mop.getLocation().z - mop.getBlockPos().getZ() < 0.5d) {
                        start = start.offset(0, 0, -1);
                    }
                }
            }
            case NORTH, SOUTH -> {
                x = width;
                y = height;
                z = mop.getDirection().getAxisDirection().getStep() * -depth;
                start = start.offset(-x / 2, -y / 2, 0);
                if (x % 2 == 0 && mop.getLocation().x - mop.getBlockPos().getX() > 0.5d) {
                    start = start.offset(1, 0, 0);
                }
                if (y % 2 == 0 && mop.getLocation().y - mop.getBlockPos().getY() > 0.5d) {
                    start = start.offset(0, 1, 0);
                }
            }
            case WEST, EAST -> {
                x = mop.getDirection().getAxisDirection().getStep() * -depth;
                y = height;
                z = width;
                start = start.offset(-0, -y / 2, -z / 2);
                if (y % 2 == 0 && mop.getLocation().y - mop.getBlockPos().getY() > 0.5d) {
                    start = start.offset(0, 1, 0);
                }
                if (z % 2 == 0 && mop.getLocation().z - mop.getBlockPos().getZ() > 0.5d) {
                    start = start.offset(0, 0, 1);
                }
            }
            default -> x = y = z = 0;
        }

        ArrayList<BlockPos> builder = new ArrayList<>();
        for (int xp = start.getX(); xp != start.getX() + x; xp += x / Mth.abs(x)) {
            for (int yp = start.getY(); yp != start.getY() + y; yp += y / Mth.abs(y)) {
                for (int zp = start.getZ(); zp != start.getZ() + z; zp += z / Mth.abs(z)) {
                    // don't add the origin block
                    if (xp == origin.getX() && yp == origin.getY() && zp == origin.getZ()) {
                        continue;
                    }
                    if (distance > 0 && Mth.abs(xp - origin.getX()) + Mth.abs(yp - origin.getY()) + Mth.abs(
                            zp - origin.getZ()) > distance) {
                        continue;
                    }
                    BlockPos pos = new BlockPos(xp, yp, zp);
                    builder.add(pos);
                }
            }
        }
        builder.add(origin);

        return builder;
    }

    public static Set<BlockPos> DFSBlockstates(Level world, BlockPos start, int maxBlocks, Predicate<BlockState> isMatch) {
        return DFSBlockstates(world, Collections.singleton(start), maxBlocks, isMatch);
    }

    private static Set<BlockPos> DFSBlockstates(Level world, Collection<BlockPos> start, int maxBlocks, Predicate<BlockState> isMatch) {
        LinkedList<BlockPos> searchQueue = new LinkedList<>(start);
        HashSet<BlockPos> searched = new HashSet<>(start);
        HashSet<BlockPos> found = new HashSet<>();

        while (!searchQueue.isEmpty() && found.size() < maxBlocks) {
            BlockPos current = searchQueue.removeFirst();
            BlockState state = world.getBlockState(current);
            if (isMatch.test(state)) {
                found.add(current);
                BlockPos.betweenClosedStream(current.offset(1, 1, 1), current.offset(-1, -1, -1)).forEach(neighborMutable -> {
                    if (searched.contains(neighborMutable)) return;
                    BlockPos neighbor = neighborMutable.immutable();
                    searched.add(neighbor);
                    searchQueue.add(neighbor);
                });
            }
        }
        return found;
    }

    public static HitResult rayTrace(Entity entity, double length, float lookOffset, boolean hitLiquids) {
        HitResult result = entity.pick(length, lookOffset, hitLiquids);
        EntityHitResult entityLookedAt = MathUtil.getLookedAtEntity(entity, 25);
        return entityLookedAt == null ? result : entityLookedAt;
    }
}
