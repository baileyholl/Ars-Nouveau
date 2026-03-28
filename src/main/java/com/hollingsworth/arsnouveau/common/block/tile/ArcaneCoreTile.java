package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.state.AnimationTest;
import software.bernie.geckolib.animation.object.PlayState;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;


public class ArcaneCoreTile extends ModdedTile implements GeoBlockEntity {

    public ArcaneCoreTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.ARCANE_CORE_TILE, pos, state);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<ArcaneCoreTile>("controller", 1, this::spin));
    }

    public PlayState spin(AnimationTest<ArcaneCoreTile> e) {
        e.controller().setAnimation(RawAnimation.begin().thenPlay("gem_spin"));
        return PlayState.CONTINUE;
    }

    AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }
}
