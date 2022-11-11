package com.hollingsworth.arsnouveau.common.items;

import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class AnimModItem extends ModItem implements IAnimatable {

    public AnimModItem(Properties properties) {
        super(properties);
    }

    public AnimModItem() {
        super();
    }

    @Override
    public void registerControllers(AnimationData data) {
    }

    AnimationFactory factory = GeckoLibUtil.createFactory(this);

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
}
