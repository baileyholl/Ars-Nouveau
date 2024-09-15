package com.hollingsworth.arsnouveau.common.entity.statemachine.alakarkinos;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.datagen.BlockTagProvider;
import com.hollingsworth.arsnouveau.common.entity.Alakarkinos;
import com.hollingsworth.arsnouveau.common.entity.EntityFlyingItem;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketAnimEntity;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ConvertBlockState extends CrabState{
    BlockPos target;
    BlockPos placeHatPos = null;
    int placeTries;
    boolean placedHat;

    int waitTicks;
    boolean didBubbles;
    boolean didHatAnimate;

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
        if(!didHatAnimate){
            alakarkinos.getNavigation().moveTo(placeHatPos.getX() + 0.5, placeHatPos.getY() + 0.5, placeHatPos.getZ(), 1.0);
            if(BlockUtil.distanceFrom(alakarkinos.blockPosition(), placeHatPos) <= 2){
                didHatAnimate = true;
                alakarkinos.getNavigation().stop();
                waitTicks = 25;
                Networking.sendToNearbyClient(alakarkinos.level, alakarkinos, new PacketAnimEntity(alakarkinos.getId(), 0));
            }
            return null;
        }

        if(waitTicks > 0){
            waitTicks--;
            return null;
        }

        if(!placedHat){
            if(alakarkinos.level.getBlockState(placeHatPos).canBeReplaced()) {
                alakarkinos.level.setBlock(placeHatPos, BlockRegistry.CRAB_HAT.defaultBlockState(), 3);
                placedHat = true;
                alakarkinos.hatPos = placeHatPos.immutable();
                alakarkinos.getEntityData().set(Alakarkinos.HAS_HAT, Boolean.FALSE);
            }else{
                placeHatPos = null;
                return null;
            }
        }

        if(BlockUtil.distanceFrom(alakarkinos.blockPosition(), target) <= 3){
            alakarkinos.getNavigation().stop();
            alakarkinos.getLookControl().setLookAt(target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5);
            if(!didBubbles){
                didBubbles = true;
                waitTicks = 60;
                alakarkinos.setBlowingBubbles(true);
                alakarkinos.getEntityData().set(Alakarkinos.BLOWING_AT, Optional.of(target));
                return null;
            }
            alakarkinos.setBlowingBubbles(false);
            alakarkinos.level.setBlockAndUpdate(target, Blocks.AIR.defaultBlockState());
            EntityFlyingItem flyingItem = new EntityFlyingItem(alakarkinos.level, target, placeHatPos.above());
            flyingItem.getEntityData().set(EntityFlyingItem.IS_BUBBLE, true);
            alakarkinos.level.addFreshEntity(flyingItem);
            var wasGravel = alakarkinos.level.getBlockState(target).is(BlockTagProvider.ALAKARKINOS_GRAVEL);
            flyingItem.setStack(alakarkinos.level.getBlockState(target).getBlock().asItem().getDefaultInstance());
//            alakarkinos.level.setBlock(target, wasGravel ? Blocks.SUSPICIOUS_GRAVEL.defaultBlockState() : Blocks.SUSPICIOUS_SAND.defaultBlockState(), 3);
//            if(alakarkinos.level.getBlockEntity(target) instanceof BrushableBlockEntityAccessor brushableBlock){
//                brushableBlock.setLootTable(BuiltInLootTables.DESERT_PYRAMID_ARCHAEOLOGY);
//                brushableBlock.setLootTableSeed(alakarkinos.getRandom().nextLong());
//            }
            alakarkinos.findBlockCooldown = 60;
            return new DecideCrabActionState(alakarkinos);
        }else{
            alakarkinos.getNavigation().moveTo(target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5, 1.0);
        }
        return super.tick();
    }
}
