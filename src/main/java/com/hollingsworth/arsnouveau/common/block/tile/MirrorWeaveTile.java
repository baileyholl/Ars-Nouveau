package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.spell.ILightable;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.common.block.MirrorWeave;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class MirrorWeaveTile extends ModdedTile implements IAnimatable, ILightable {
    public BlockState mimicState;
    public BlockState nextState = BlockRegistry.MIRROR_WEAVE.defaultBlockState();

    public MirrorWeaveTile(BlockPos pos, BlockState state) {
        this(BlockRegistry.MIRROR_WEAVE_TILE, pos, state);
    }

    public MirrorWeaveTile(BlockEntityType type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.mimicState = getDefaultBlockState();
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
        tag.put("mimic_state",  NbtUtils.writeBlockState(mimicState));
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        if(pTag.contains("mimic_state")) {
            mimicState = NbtUtils.readBlockState(pTag.getCompound("mimic_state"));
        }else{
            mimicState = getDefaultBlockState();
        }
    }

    public BlockState getDefaultBlockState(){
        return BlockRegistry.MIRROR_WEAVE.defaultBlockState();
    }

    @Override
    public void onLight(HitResult rayTraceResult, Level world, LivingEntity shooter, SpellStats stats, SpellContext spellContext) {
        if (rayTraceResult instanceof BlockHitResult) {
            BlockState state = world.getBlockState(((BlockHitResult) rayTraceResult).getBlockPos());
            world.setBlock(getBlockPos(), state.setValue(MirrorWeave.LIGHT_LEVEL, Math.min(Math.max(0, 15 - stats.getBuffCount(AugmentDampen.INSTANCE)), 15)), 3);
            world.sendBlockUpdated(((BlockHitResult) rayTraceResult).getBlockPos(), state,
                    state.setValue(MirrorWeave.LIGHT_LEVEL, Math.min(Math.max(0, 15 - stats.getBuffCount(AugmentDampen.INSTANCE)), 15)), 3);
        }
        updateBlock();
    }
}
