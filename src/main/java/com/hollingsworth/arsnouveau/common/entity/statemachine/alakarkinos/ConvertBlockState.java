package com.hollingsworth.arsnouveau.common.entity.statemachine.alakarkinos;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.datagen.BlockTagProvider;
import com.hollingsworth.arsnouveau.common.entity.Alakarkinos;
import com.hollingsworth.arsnouveau.common.mixin.BrushableBlockEntityAccessor;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import org.jetbrains.annotations.Nullable;

public class ConvertBlockState extends CrabState{
    BlockPos target;
    BlockPos placeHatPos = null;
    int placeTries;
    boolean placedHat;
    public ConvertBlockState(Alakarkinos alakarkinos, BlockPos target) {
        super(alakarkinos);
        this.target = target;
    }

    @Override
    public void onStart() {
        super.onStart();
        placeHatPos = findHatPos();
        placeTries++;
    }

    public BlockPos findHatPos(){

        for(BlockPos b : BlockPos.withinManhattan(target, 3, 1, 3)) {
            if(alakarkinos.level.getBlockState(b).canBeReplaced()){
                return b.immutable();
            }
        }
        return null;
    }

    @Override
    public @Nullable CrabState tick() {
        if(placeHatPos == null){
            if(placeTries > 4){
                return new DecideCrabActionState(alakarkinos);
            }
            placeHatPos = findHatPos();
            return null;
        }
        if(!placedHat){
            alakarkinos.getNavigation().moveTo(placeHatPos.getX() + 0.5, placeHatPos.getY() + 0.5, placeHatPos.getZ(), 1.0);
            if(BlockUtil.distanceFrom(alakarkinos.blockPosition(), placeHatPos) <= 2){
                if(alakarkinos.level.getBlockState(placeHatPos).canBeReplaced()) {
                    alakarkinos.level.setBlock(placeHatPos, BlockRegistry.CRAB_HAT.defaultBlockState(), 3);
                    placedHat = true;
                }else{
                    // Became invalid
                    placeHatPos = null;
                }
            }
            return null;
        }

        if(BlockUtil.distanceFrom(alakarkinos.blockPosition(), target) <= 2){
            var wasGravel = alakarkinos.level.getBlockState(target).is(BlockTagProvider.ALAKARKINOS_GRAVEL);
            alakarkinos.level.setBlock(target, wasGravel ? Blocks.SUSPICIOUS_GRAVEL.defaultBlockState() : Blocks.SUSPICIOUS_SAND.defaultBlockState(), 3);
            if(alakarkinos.level.getBlockEntity(target) instanceof BrushableBlockEntityAccessor brushableBlock){
                brushableBlock.setLootTable(BuiltInLootTables.DESERT_PYRAMID_ARCHAEOLOGY);
                brushableBlock.setLootTableSeed(alakarkinos.getRandom().nextLong());
            }
            alakarkinos.findBlockCooldown = 5 * 60;
            return new DecideCrabActionState(alakarkinos);
        }
        return super.tick();
    }
}
