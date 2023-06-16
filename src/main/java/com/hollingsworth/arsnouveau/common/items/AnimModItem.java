package com.hollingsworth.arsnouveau.common.items;

import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib3.core.GeoAnimatable;
import software.bernie.geckolib3.core.manager.AnimatableInstanceCache;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class AnimModItem extends ModItem implements GeoAnimatable {

    public AnimModItem(Properties properties) {
        super(properties);
    }

    public AnimModItem() {
        super();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
    }

    AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }
}
