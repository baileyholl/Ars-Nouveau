package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.mana.AbstractManaTile;
import com.hollingsworth.arsnouveau.api.mana.IManaTile;
import com.hollingsworth.arsnouveau.api.mana.SourcelinkEventQueue;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.ManaUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.BlockPos;
import net.minecraftforge.eventbus.api.Event;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SourcelinkTile extends AbstractManaTile implements IAnimatable, ITickable {

    int progress;
    public boolean isDisabled = false;
    public boolean registered = false;

    public SourcelinkTile(BlockEntityType<?> sourceLinkTile, BlockPos pos, BlockState state) {
        super(sourceLinkTile, pos, state);
    }

    @Override
    public int getTransferRate() {
        return 1000;
    }

    @Override
    public int getMaxMana() {
        return 1000;
    }

    @Override
    public void tick() {
        if(level.isClientSide)
            return;
        if(level.getGameTime() % 120 == 0 && usesEventQueue()){
            SourcelinkEventQueue.addPosition(level, this.worldPosition);
            registered = true;
        }

        if(level.getGameTime() % 100 == 0 && getCurrentMana() > 0){
            BlockPos jarPos = ManaUtil.canGiveManaClosest(worldPosition, level, 5);
            if(jarPos != null){
                transferMana(this, (IManaTile) level.getBlockEntity(jarPos));
                ParticleUtil.spawnFollowProjectile(level, this.worldPosition, jarPos);
            }
        }
    }

    public List<ArcanePedestalTile> getSurroundingPedestals(){
        List<ArcanePedestalTile> inventories = new ArrayList<>();
        for(BlockPos p : BlockPos.betweenClosed(getBlockPos().below().east().north(), getBlockPos().above().west().south())){
            if(level.getBlockEntity(p) instanceof ArcanePedestalTile){
                inventories.add((ArcanePedestalTile) level.getBlockEntity(p));
            }
        }
        return inventories;
    }

    public void getManaEvent(BlockPos sourcePos, int total){
        this.addMana(total);
        ParticleUtil.spawnFollowProjectile(level, sourcePos, this.worldPosition);
    }

    public boolean eventInRange(BlockPos sourcePos, @Nullable Event event){
        return BlockUtil.distanceFrom(this.worldPosition, sourcePos) <= 15;
    }

    public boolean usesEventQueue(){
        return false;
    }

    public void doRandomAction(){}

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "rotate_controller", 0, this::idlePredicate));
    }
    AnimationFactory factory = new AnimationFactory(this);
    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        progress = tag.getInt("progress");
        isDisabled = tag.getBoolean("disabled");
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        tag.putInt("progress", progress);
        tag.putBoolean("disabled", isDisabled);
    }

    private <E extends BlockEntity & IAnimatable > PlayState idlePredicate(AnimationEvent<E> event) {
        if(this.isDisabled)
            return PlayState.STOP;
        event.getController().setAnimation(new AnimationBuilder().addAnimation("rotation", true));
        return PlayState.CONTINUE;
    }
}
