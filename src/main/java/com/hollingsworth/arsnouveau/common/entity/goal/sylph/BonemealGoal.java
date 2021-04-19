package com.hollingsworth.arsnouveau.common.entity.goal.sylph;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.entity.EntitySylph;
import com.hollingsworth.arsnouveau.common.entity.goal.DistanceRestrictedGoal;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.block.pattern.BlockStateMatcher;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class BonemealGoal extends DistanceRestrictedGoal {
    private int timeGrowing;
    BlockPos growPos;
    EntitySylph sylph;
    public final Predicate<BlockState> IS_GRASS = BlockStateMatcher.forBlock(Blocks.GRASS_BLOCK);

    public BonemealGoal(EntitySylph sylph){
        super(()->sylph.blockPosition(), 0);
        this.sylph = sylph;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
    }

    public BonemealGoal(EntitySylph sylph, Supplier<BlockPos> from, int distanceFrom){
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

        if(this.growPos == null) {
            return;
        }
        if(BlockUtil.distanceFrom(sylph.blockPosition(), this.growPos) > 1.2){
            sylph.getNavigation().moveTo(this.growPos.getX(), this.growPos.getY(), this.growPos.getZ(), 1.2);
        }else{
            ServerWorld world = (ServerWorld) sylph.level;
            world.sendParticles(ParticleTypes.COMPOSTER, this.growPos.getX() +0.5, this.growPos.getY()+1.1, this.growPos.getZ()+0.5, 1, ParticleUtil.inRange(-0.2, 0.2),0,ParticleUtil.inRange(-0.2, 0.2),0.01);
            this.timeGrowing--;
            if(this.timeGrowing <= 0){
                sylph.timeSinceBonemeal = 0;
                ItemStack stack = new ItemStack(Items.BONE_MEAL);
                BoneMealItem.applyBonemeal(stack, world, growPos, FakePlayerFactory.getMinecraft(world));
            }
        }
    }

    @Override
    public boolean canContinueToUse() {
        return this.timeGrowing > 0 && growPos != null && sylph.timeSinceBonemeal >= (60 * 20 * 5) && isInRange(growPos);
    }

    @Override
    public boolean canUse() {
        return  sylph.level.random.nextInt(5) == 0 && sylph.timeSinceBonemeal >= (60 * 20 * 5) && isInRange(sylph.blockPosition());
    }

    @Override
    public void start() {
        World world = sylph.level;
        int range = 4;
        if(this.IS_GRASS.test(world.getBlockState(sylph.blockPosition().below())) && world.getBlockState(sylph.blockPosition()).getMaterial() == Material.AIR){
            this.growPos = sylph.blockPosition().below();

        }else{
            List<BlockPos> list = new ArrayList<>();
            BlockPos.betweenClosedStream(sylph.blockPosition().offset(range, range, range), sylph.blockPosition().offset(-range, -range, -range)).forEach(bp ->{
                bp = bp.immutable();
                if(IS_GRASS.test(world.getBlockState(bp)) && world.getBlockState(bp.above()).getMaterial() == Material.AIR)
                    list.add(bp);
            });
            Collections.shuffle(list);
            if(!list.isEmpty())
                this.growPos = list.get(0);
        }
        this.timeGrowing = 60;
    }
}
