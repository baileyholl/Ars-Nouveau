package com.hollingsworth.arsnouveau.common.entity.statemachine.alakarkinos;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.entity.Alakarkinos;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketAnimEntity;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

public class PlaceHatState extends CrabState{
    BlockPos placeHatPos;
    int placeTries;
    boolean didHatAnimate;

    int waitTicks;
    boolean placedHat;
    BlockPos convertStatePos;

    public PlaceHatState(Alakarkinos alakarkinos, BlockPos placeHatPos, BlockPos convertStatePos) {
        super(alakarkinos);
        this.placeHatPos = placeHatPos;
        this.convertStatePos = convertStatePos;
    }

    public static BlockPos findHatPos(Alakarkinos alakarkinos){

        for(BlockPos b : BlockPos.withinManhattan(alakarkinos.getHome(), 3, 1, 3)) {
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
            placeHatPos = findHatPos(alakarkinos);
            return null;
        }
        if(!didHatAnimate){
            alakarkinos.getNavigation().moveTo(placeHatPos.getX() + 0.5, placeHatPos.getY() + 0.5, placeHatPos.getZ(), 1.0);
            if(BlockUtil.distanceFrom(alakarkinos.blockPosition(), placeHatPos) <= 2){
                didHatAnimate = true;
                alakarkinos.getNavigation().stop();
                waitTicks = 20;
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

        return new ConvertBlockState(alakarkinos, this.convertStatePos);
    }
}
