package com.hollingsworth.arsnouveau.common.items;

import net.minecraft.world.level.block.Block;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;


public class AnimBlockItem extends ModBlockItem implements GeoItem {
    AnimatableInstanceCache manager = GeckoLibUtil.createInstanceCache(this);

    public AnimBlockItem(Block blockIn, Properties builder) {
        super(blockIn, builder);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar animatableManager) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return manager;
    }
}
