package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.mana.AbstractManaTile;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class VolcanicTile extends AbstractManaTile implements IAnimatable {
    public VolcanicTile() {
        super(BlockRegistry.VOLCANIC_TILE);
    }
    AnimationFactory manager = new AnimationFactory(this);

    int progress;


    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController(this, "rotate_controller", 0, this::idlePredicate));
        animationData.addAnimationController(new AnimationController(this, "hover_controller", 0, this::hover));
        animationData.addAnimationController(new AnimationController(this, "gem_controller", 0, this::gem));
    }

    @Override
    public AnimationFactory getFactory() {
        return manager;
    }

    @Override
    public int getTransferRate() {
        return 1000;
    }

    @Override
    public void tick() {
        if(world.isRemote)
            return;
        if(world.getGameTime() % 20 == 0){
            int numSource = (int) BlockPos.getAllInBox(this.getPos().down().add(1, 0, 1), this.getPos().down().add(-1, 0, -1))
                    .filter(b -> world.getFluidState(b).getFluid() instanceof LavaFluid).map(b -> world.getFluidState(b))
                    .filter(FluidState::isSource).count();
            this.addMana(numSource);
        }
    }

    public void doRandomAction(){

    }

    @Override
    public int getMaxMana() {
        return 1000;
    }

    @Override
    public void read(BlockState state, CompoundNBT tag) {
        super.read(state, tag);
        progress = tag.getInt("progress");
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        tag.putInt("progress", progress);
        return super.write(tag);
    }

    private <E extends TileEntity  & IAnimatable > PlayState idlePredicate(AnimationEvent<E> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("core_rotation", true));
        return PlayState.CONTINUE;
    }
    private <E extends TileEntity  & IAnimatable > PlayState hover(AnimationEvent<E> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("gem_hover", true));
        return PlayState.CONTINUE;
    }
    private <E extends TileEntity  & IAnimatable > PlayState gem(AnimationEvent<E> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("gem_rotation", true));
        return PlayState.CONTINUE;
    }

}
