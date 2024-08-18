package com.hollingsworth.arsnouveau.common.entity.statemachine.alakarkinos;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.datagen.BlockTagProvider;
import com.hollingsworth.arsnouveau.common.entity.Alakarkinos;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import org.jetbrains.annotations.Nullable;

public class ConvertBlockState extends CrabState{
    BlockPos target;
    public ConvertBlockState(Alakarkinos alakarkinos, BlockPos target) {
        super(alakarkinos);
        this.target = target;
    }

    @Override
    public @Nullable CrabState tick() {

        alakarkinos.getNavigation().moveTo(target.getX() + 0.5, target.getY() + 0.5, target.getZ(), 1.0);
        if(BlockUtil.distanceFrom(alakarkinos.blockPosition(), target) <= 2){
            var wasGravel = alakarkinos.level.getBlockState(target).is(BlockTagProvider.ALAKARKINOS_GRAVEL);
            alakarkinos.level.setBlock(target, wasGravel ? Blocks.SUSPICIOUS_GRAVEL.defaultBlockState() : Blocks.SUSPICIOUS_SAND.defaultBlockState(), 3);
            if(alakarkinos.level.getBlockEntity(target) instanceof BrushableBlockEntity brushableBlock){
                brushableBlock.lootTable = BuiltInLootTables.DESERT_PYRAMID_ARCHAEOLOGY;
                brushableBlock.lootTableSeed = alakarkinos.getRandom().nextLong();
            }
            alakarkinos.findBlockCooldown = 5 * 60;
            return new DecideCrabActionState(alakarkinos);
        }
        return super.tick();
    }
}
