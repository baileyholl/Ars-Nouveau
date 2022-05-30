package com.hollingsworth.arsnouveau.common.items;

import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class AnimModItem extends ModItem implements IAnimatable {
    public AnimModItem(Properties properties) {
        super(properties);
    }

    public AnimModItem(Properties properties, String registryName) {
        super(properties, registryName);
    }

    public AnimModItem(String registryName) {
        super(registryName);
    }

    @Override
    public void registerControllers(AnimationData data) {
    }

    AnimationFactory factory = new AnimationFactory(this);
    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
}
