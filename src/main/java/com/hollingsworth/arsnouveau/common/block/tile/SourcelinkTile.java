package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.mana.AbstractManaTile;
import com.hollingsworth.arsnouveau.api.mana.IManaTile;
import com.hollingsworth.arsnouveau.api.mana.SourcelinkEventQueue;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.ManaUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.eventbus.api.Event;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;

public class SourcelinkTile extends AbstractManaTile implements IAnimatable {

    int progress;
    public boolean isDisabled = false;
    public boolean registered = false;

    public SourcelinkTile(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
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
        if(!registered && usesEventQueue()){
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
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);
        progress = tag.getInt("progress");
        isDisabled = tag.getBoolean("disabled");
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        tag.putInt("progress", progress);
        tag.putBoolean("disabled", isDisabled);
        return super.save(tag);
    }

    private <E extends TileEntity & IAnimatable > PlayState idlePredicate(AnimationEvent<E> event) {
        if(this.isDisabled)
            return PlayState.STOP;
        event.getController().setAnimation(new AnimationBuilder().addAnimation("rotation", true));
        return PlayState.CONTINUE;
    }
}
