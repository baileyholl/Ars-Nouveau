package com.hollingsworth.arsnouveau.common.entity.statemachine.alakarkinos;

import com.hollingsworth.arsnouveau.api.registry.AlakarkinosConversionRegistry;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.crafting.recipes.AlakarkinosRecipe;
import com.hollingsworth.arsnouveau.common.entity.Alakarkinos;
import com.hollingsworth.arsnouveau.common.entity.EntityFlyingItem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ConvertBlockState extends CrabState {
    BlockPos target;
    int waitTicks;
    boolean didBubbles;

    boolean spawnedFlyingItem;
    AlakarkinosRecipe recipe;
    public ConvertBlockState(Alakarkinos alakarkinos, BlockPos target) {
        super(alakarkinos);
        this.target = target;
    }

    @Override
    public void onEnd() {
        super.onEnd();
        alakarkinos.lookAt = null;
    }

    @Override
    public @Nullable CrabState tick() {
        super.tick();
        if (waitTicks > 0) {
            waitTicks--;
            return null;
        }

        if (BlockUtil.distanceFrom(alakarkinos.blockPosition(), target) > 2 && ticksRunning < 200) {
            alakarkinos.getNavigation().moveTo(target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5, 2, 1.0);
            return null;
        }
        if (!didBubbles) {

            alakarkinos.getNavigation().stop();
            alakarkinos.lookAt = Vec3.atCenterOf(target);
            didBubbles = true;
            waitTicks = 60;
            alakarkinos.setBlowingBubbles(true);
            alakarkinos.getEntityData().set(Alakarkinos.BLOWING_AT, Optional.of(target));
            return null;
        }
        var hatPos = alakarkinos.hatPos;
        if(hatPos == null){
            return new DecideCrabActionState(alakarkinos);
        }
        if (!spawnedFlyingItem) {
            Block lootBlock = alakarkinos.level.getBlockState(target).getBlock();
            var res = AlakarkinosConversionRegistry.getConversionResult(lootBlock, alakarkinos.level.random);
            if(res == null){
                return new DecideCrabActionState(alakarkinos);
            }
            this.recipe = res;
            spawnedFlyingItem = true;
            alakarkinos.setBlowingBubbles(false);
            EntityFlyingItem.spawn(alakarkinos.getHome(), (ServerLevel) alakarkinos.level, target, hatPos.above())
                    .setStack(alakarkinos.level.getBlockState(target).getBlock().asItem().getDefaultInstance())
                    .getEntityData().set(EntityFlyingItem.IS_BUBBLE, true);
            alakarkinos.level.setBlockAndUpdate(target, Blocks.AIR.defaultBlockState());
            waitTicks = 60;
            alakarkinos.setNeedSource(true);
            return null;
        }
        return new SpawnLootState(alakarkinos, recipe);
    }
}
