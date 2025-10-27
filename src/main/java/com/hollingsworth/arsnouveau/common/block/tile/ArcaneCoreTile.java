package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;


public class ArcaneCoreTile extends ModdedTile implements GeoBlockEntity {

    public ArcaneCoreTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.ARCANE_CORE_TILE, pos, state);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "controller", 1, this::spin));
    }

    public PlayState spin(AnimationState<?> e) {
        e.getController().setAnimation(RawAnimation.begin().thenPlay("gem_spin"));
        return PlayState.CONTINUE;
    }

    AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }
}
