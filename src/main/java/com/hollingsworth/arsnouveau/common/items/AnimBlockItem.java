package com.hollingsworth.arsnouveau.common.items;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import net.minecraft.item.Item.Properties;

public class AnimBlockItem extends BlockItem implements IAnimatable {
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
