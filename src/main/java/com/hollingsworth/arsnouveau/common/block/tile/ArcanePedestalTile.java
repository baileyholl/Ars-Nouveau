package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class ArcanePedestalTile extends SingleItemTile implements Container, IAnimatable {
    public float frames;
    public boolean hasSignal;

    public ArcanePedestalTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.ARCANE_PEDESTAL_TILE, pos, state);
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public void registerControllers(AnimationData data) {}

    AnimationFactory factory = GeckoLibUtil.createFactory(this);

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        hasSignal = tag.getBoolean("hasSignal");
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        this.hasSignal = compound.getBoolean("hasSignal");
    }
}
