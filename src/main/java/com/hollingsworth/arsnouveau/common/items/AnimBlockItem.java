package com.hollingsworth.arsnouveau.common.items;

import net.minecraft.world.level.block.Block;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import net.minecraft.world.item.Item.Properties;

public class AnimBlockItem extends ModBlockItem implements IAnimatable {
    AnimationFactory manager = new AnimationFactory(this);

    public AnimBlockItem(Block blockIn, Properties builder) {
        super(blockIn, builder);
    }

    @Override
    public void registerControllers(AnimationData animationData) {

    }

    @Override
    public AnimationFactory getFactory() {
        return manager;
    }
}
