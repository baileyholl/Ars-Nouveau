package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class ArmorTile extends ModdedTile implements IAnimatable, ITickable {
    public ItemEntity entity;
    public ItemStack armorStack = ItemStack.EMPTY;
    public ArmorTile(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    public ArmorTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.ARMOR_TILE, pos, state);

    }

    @Override
    public void registerControllers(AnimationData data) {

    }

    public ItemStack getArmorStack(){
        return armorStack;
    }

    public AnimationFactory factory = new AnimationFactory(this);

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
}
