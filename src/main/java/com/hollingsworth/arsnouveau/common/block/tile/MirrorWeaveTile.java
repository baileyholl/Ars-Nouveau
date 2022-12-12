package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class MirrorWeaveTile extends ModdedTile implements IAnimatable {
    public BlockState mimicState = BlockRegistry.MIRROR_WEAVE.defaultBlockState();

    public MirrorWeaveTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.MIRROR_WEAVE_TILE, pos, state);
    }

    @Override
    public void registerControllers(AnimationData data) {

    }
    AnimationFactory factory = new AnimationFactory(this);
    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("mimic_state",  NbtUtils.writeBlockState(mimicState));
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        if(pTag.contains("mimic_state"))
            mimicState = NbtUtils.readBlockState(pTag.getCompound("mimic_state"));
    }
}
