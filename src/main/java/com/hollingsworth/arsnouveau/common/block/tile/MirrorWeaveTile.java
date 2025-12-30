package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.api.spell.ILightable;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.common.block.MirrorWeave;
import com.hollingsworth.arsnouveau.common.datagen.BlockTagProvider;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import static net.minecraft.world.level.block.Block.OCCLUSION_CACHE;

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
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(tag, pRegistries);
        tag.put("mimic_state", NbtUtils.writeBlockState(mimicState));
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        renderInvalid = true;
        if (pTag.contains("mimic_state")) {
            HolderGetter<Block> holdergetter = this.level != null ? this.level.holderLookup(Registries.BLOCK) : BuiltInRegistries.BLOCK.asLookup();
            mimicState = NbtUtils.readBlockState(holdergetter, pTag.getCompound("mimic_state"));
        } else {
            mimicState = getDefaultBlockState();
        }
    }

    // Copy of Block.shouldRenderFace with getStateForCulling replacing the first state param
    public boolean shouldRenderFace(BlockState blockstate, Level level, BlockPos offset, Direction face, BlockPos pos) {
        BlockState state = getStateForCulling();
        if (state.skipRendering(blockstate, face)) {
            return false;
        } else if (blockstate.hidesNeighborFace(level, pos, state, face.getOpposite()) && state.supportsExternalFaceHiding()) {
            return false;
        } else if (blockstate.canOcclude()) {
            Block.BlockStatePairKey block$blockstatepairkey = new Block.BlockStatePairKey(state, blockstate, face);
            Object2ByteLinkedOpenHashMap<Block.BlockStatePairKey> object2bytelinkedopenhashmap = OCCLUSION_CACHE.get();
            byte b0 = object2bytelinkedopenhashmap.getAndMoveToFirst(block$blockstatepairkey);
            if (b0 != 127) {
                return b0 != 0;
            } else {
                VoxelShape voxelshape = state.getFaceOcclusionShape(level, offset, face);
                if (voxelshape.isEmpty()) {
                    return true;
                } else {
                    VoxelShape voxelshape1 = blockstate.getFaceOcclusionShape(level, pos, face.getOpposite());
                    boolean flag = Shapes.joinIsNotEmpty(voxelshape, voxelshape1, BooleanOp.ONLY_FIRST);
                    if (object2bytelinkedopenhashmap.size() == 2048) {
                        object2bytelinkedopenhashmap.removeLastByte();
                    }

                    object2bytelinkedopenhashmap.putAndMoveToFirst(block$blockstatepairkey, (byte) (flag ? 1 : 0));
                    return flag;
                }
            }
        } else {
            return true;
        }
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
