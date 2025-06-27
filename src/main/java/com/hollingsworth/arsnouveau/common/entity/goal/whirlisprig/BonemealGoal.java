package com.hollingsworth.arsnouveau.common.entity.goal.whirlisprig;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.entity.Whirlisprig;
import com.hollingsworth.arsnouveau.common.entity.goal.DistanceRestrictedGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class BonemealGoal extends DistanceRestrictedGoal {
    private int timeGrowing;
    BlockPos growPos;
    Whirlisprig sylph;
    public final Predicate<BlockState> IS_GRASS = BlockStatePredicate.forBlock(Blocks.GRASS_BLOCK);

    public BonemealGoal(Whirlisprig sylph) {
        super(sylph::blockPosition, 0);
        this.sylph = sylph;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
    }

    public BonemealGoal(Whirlisprig sylph, Supplier<BlockPos> from, int distanceFrom) {
        super(from, distanceFrom);
        this.sylph = sylph;
    }

    @Override
    public void stop() {
        this.timeGrowing = 0;
        this.growPos = null;
    }

    @Override
    public void tick() {

        if (this.growPos == null) {
            return;
        }
        if (BlockUtil.distanceFrom(sylph.blockPosition(), this.growPos) > 1.2) {
            sylph.getNavigation().moveTo(this.growPos.getX(), this.growPos.getY(), this.growPos.getZ(), 1.2);
        } else {
            ServerLevel world = (ServerLevel) sylph.level;
            world.sendParticles(ParticleTypes.COMPOSTER, this.growPos.getX() + 0.5, this.growPos.getY() + 1.1, this.growPos.getZ() + 0.5, 1, ParticleUtil.inRange(-0.2, 0.2), 0, ParticleUtil.inRange(-0.2, 0.2), 0.01);
            this.timeGrowing--;
            if (this.timeGrowing <= 0) {
                sylph.timeSinceBonemeal = 0;
                ItemStack stack = new ItemStack(Items.BONE_MEAL);
                BoneMealItem.applyBonemeal(stack, world, growPos, ANFakePlayer.getPlayer(world));
            }
        }
    }

    @Override
    public boolean canContinueToUse() {
        return this.timeGrowing > 0 && growPos != null && sylph.timeSinceBonemeal >= (60 * 20 * 8) && isInRange(growPos);
    }

    @Override
    public boolean canUse() {
        return sylph.level.random.nextInt(5) == 0 && sylph.timeSinceBonemeal >= (60 * 20 * 8) && isInRange(sylph.blockPosition());
    }

    @Override
    public void start() {
        Level world = sylph.level;
        int range = 4;
        if (this.IS_GRASS.test(world.getBlockState(sylph.blockPosition().below())) && world.getBlockState(sylph.blockPosition()).isAir()) {
            this.growPos = sylph.blockPosition().below();

        } else {
            List<BlockPos> list = new ArrayList<>();
            BlockPos.betweenClosedStream(sylph.blockPosition().offset(range, range, range), sylph.blockPosition().offset(-range, -range, -range)).forEach(bp -> {
                bp = bp.immutable();
                if (IS_GRASS.test(world.getBlockState(bp)) && world.getBlockState(bp.above()).isAir())
                    list.add(bp);
            });
            Collections.shuffle(list);
            if (!list.isEmpty())
                this.growPos = list.get(0);
        }
        this.timeGrowing = 60;
    }
}
