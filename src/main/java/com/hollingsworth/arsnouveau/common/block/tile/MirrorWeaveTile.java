package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.api.spell.ILightable;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.common.block.MirrorWeave;
import com.hollingsworth.arsnouveau.common.datagen.BlockTagProvider;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class MirrorWeaveTile extends ModdedTile implements GeoBlockEntity, ILightable, IDispellable {
    public BlockState mimicState;
    public BlockState nextState = BlockRegistry.MIRROR_WEAVE.defaultBlockState();
    public boolean renderInvalid = true;
    public long lastUpdateTick = 0;
    protected boolean[] renderDirections = new boolean[6];
    public boolean disableRender = false;

    public MirrorWeaveTile(BlockEntityType type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.mimicState = getDefaultBlockState();
    }

    public MirrorWeaveTile(BlockPos pos, BlockState state) {
        this(BlockRegistry.MIRROR_WEAVE_TILE.get(), pos, state);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
    }

    AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    @Override
    protected void saveAdditional(ValueOutput tag) {
        super.saveAdditional(tag);
        tag.store("mimic_state", BlockState.CODEC, mimicState);
    }

    @Override
    protected void loadAdditional(ValueInput pTag) {
        super.loadAdditional(pTag);
        renderInvalid = true;
        mimicState = pTag.read("mimic_state", BlockState.CODEC).orElseGet(this::getDefaultBlockState);
    }

    // Delegates to Block.shouldRenderFace, substituting getStateForCulling() for our actual block state.
    // This lets the mimic state drive face-culling decisions while preserving vanilla cache behaviour.
    public boolean shouldRenderFace(BlockState blockstate, Level level, BlockPos offset, Direction face, BlockPos pos) {
        BlockState state = getStateForCulling();
        if (state.skipRendering(blockstate, face)) {
            return false;
        } else if (blockstate.hidesNeighborFace(level, pos, state, face.getOpposite()) && state.supportsExternalFaceHiding()) {
            return false;
        }
        // Delegate occlusion check to vanilla which has access to the package-private OCCLUSION_CACHE key type.
        return Block.shouldRenderFace(state, blockstate, face);
    }

    public BlockState getDefaultBlockState() {
        return BlockRegistry.MIRROR_WEAVE.defaultBlockState();
    }

    /**
     * Which state should be used for culling renderable sides
     */
    public BlockState getStateForCulling() {
        if (this.mimicState.is(BlockTagProvider.FALSE_OCCLUSION)) {
            return Blocks.COBBLESTONE.defaultBlockState();
        }
        return mimicState;
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

    public void setRenderDirection(Direction direction, boolean render) {
        renderDirections[direction.ordinal()] = render;
    }

    public boolean shouldRenderDirection(Direction direction) {
        return renderDirections[direction.ordinal()];
    }

    @Override
    public void onLoad() {
        super.onLoad();
    }

    @Override
    public boolean onDispel(@NotNull LivingEntity caster) {
        if (this.mimicState.getBlock() == this.getDefaultBlockState().getBlock()) {
            return false;
        }
        if (level.getBlockState(worldPosition).getBlock() instanceof MirrorWeave mirrorWeave) {
            this.nextState = this.getDefaultBlockState();
            mirrorWeave.setMimicState(level, worldPosition, true);
        }
        return true;
    }
}
