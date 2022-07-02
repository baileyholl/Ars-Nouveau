package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class ArcaneCoreTile extends ModdedTile implements IAnimatable {

    public ArcaneCoreTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.ARCANE_CORE_TILE, pos, state);
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "controller", 1, this::spin));
    }

    public PlayState spin(AnimationEvent e) {
        e.getController().setAnimation(new AnimationBuilder().addAnimation("gem_spin", true));
        return PlayState.CONTINUE;
    }

    AnimationFactory factory = new AnimationFactory(this);

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
}
