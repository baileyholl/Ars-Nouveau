package net.minecraft.world.level.block;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.IdMapper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class Block extends BlockBehaviour implements ItemLike, net.neoforged.neoforge.common.extensions.IBlockExtension {
    public static final MapCodec<Block> CODEC = simpleCodec(Block::new);
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Holder.Reference<Block> builtInRegistryHolder = BuiltInRegistries.BLOCK.createIntrusiveHolder(this);
    public static final IdMapper<BlockState> BLOCK_STATE_REGISTRY = net.neoforged.neoforge.registries.GameData.getBlockStateIDMap();
    private static final LoadingCache<VoxelShape, Boolean> SHAPE_FULL_BLOCK_CACHE = CacheBuilder.newBuilder()
        .maximumSize(512L)
        .weakKeys()
        .build(new CacheLoader<VoxelShape, Boolean>() {
            public Boolean load(VoxelShape p_49972_) {
                return !Shapes.joinIsNotEmpty(Shapes.block(), p_49972_, BooleanOp.NOT_SAME);
            }
        });
    public static final int UPDATE_NEIGHBORS = 1;
    public static final int UPDATE_CLIENTS = 2;
    public static final int UPDATE_INVISIBLE = 4;
    public static final int UPDATE_IMMEDIATE = 8;
    public static final int UPDATE_KNOWN_SHAPE = 16;
    public static final int UPDATE_SUPPRESS_DROPS = 32;
    public static final int UPDATE_MOVE_BY_PISTON = 64;
    public static final int UPDATE_SKIP_SHAPE_UPDATE_ON_WIRE = 128;
    public static final int UPDATE_SKIP_BLOCK_ENTITY_SIDEEFFECTS = 256;
    public static final int UPDATE_SKIP_ON_PLACE = 512;
    @Block.UpdateFlags
    public static final int UPDATE_NONE = 260;
    @Block.UpdateFlags
    public static final int UPDATE_ALL = 3;
    @Block.UpdateFlags
    public static final int UPDATE_ALL_IMMEDIATE = 11;
    @Block.UpdateFlags
    public static final int UPDATE_SKIP_ALL_SIDEEFFECTS = 816;
    public static final float INDESTRUCTIBLE = -1.0F;
    public static final float INSTANT = 0.0F;
    public static final int UPDATE_LIMIT = 512;
    protected final StateDefinition<Block, BlockState> stateDefinition;
    private BlockState defaultBlockState;
    private @Nullable Item item;
    private static final int CACHE_SIZE = 256;
    public static final ThreadLocal<Object2ByteLinkedOpenHashMap<Block.ShapePairKey>> OCCLUSION_CACHE = ThreadLocal.withInitial(() -> {
        Object2ByteLinkedOpenHashMap<Block.ShapePairKey> object2bytelinkedopenhashmap = new Object2ByteLinkedOpenHashMap<Block.ShapePairKey>(256, 0.25F) {
            @Override
            protected void rehash(int p_49979_) {
            }
        };
        object2bytelinkedopenhashmap.defaultReturnValue((byte)127);
        return object2bytelinkedopenhashmap;
    });

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    public static int getId(@Nullable BlockState p_49957_) {
        if (p_49957_ == null) {
            return 0;
        } else {
            int i = BLOCK_STATE_REGISTRY.getId(p_49957_);
            return i == -1 ? 0 : i;
        }
    }

    public static BlockState stateById(int p_49804_) {
        BlockState blockstate = BLOCK_STATE_REGISTRY.byId(p_49804_);
        return blockstate == null ? Blocks.AIR.defaultBlockState() : blockstate;
    }

    public static Block byItem(@Nullable Item p_49815_) {
        return p_49815_ instanceof BlockItem ? ((BlockItem)p_49815_).getBlock() : Blocks.AIR;
    }

    public static BlockState pushEntitiesUp(BlockState p_49898_, BlockState p_49899_, LevelAccessor p_238252_, BlockPos p_49901_) {
        VoxelShape voxelshape = Shapes.joinUnoptimized(
                p_49898_.getCollisionShape(p_238252_, p_49901_), p_49899_.getCollisionShape(p_238252_, p_49901_), BooleanOp.ONLY_SECOND
            )
            .move(p_49901_);
        if (voxelshape.isEmpty()) {
            return p_49899_;
        } else {
            for (Entity entity : p_238252_.getEntities(null, voxelshape.bounds())) {
                double d0 = Shapes.collide(Direction.Axis.Y, entity.getBoundingBox().move(0.0, 1.0, 0.0), List.of(voxelshape), -1.0);
                entity.teleportRelative(0.0, 1.0 + d0, 0.0);
            }

            return p_49899_;
        }
    }

    public static VoxelShape box(double p_49797_, double p_49798_, double p_49799_, double p_49800_, double p_49801_, double p_49802_) {
        return Shapes.box(p_49797_ / 16.0, p_49798_ / 16.0, p_49799_ / 16.0, p_49800_ / 16.0, p_49801_ / 16.0, p_49802_ / 16.0);
    }

    public static VoxelShape[] boxes(int p_393774_, IntFunction<VoxelShape> p_394099_) {
        return IntStream.rangeClosed(0, p_393774_).mapToObj(p_394099_).toArray(VoxelShape[]::new);
    }

    public static VoxelShape cube(double p_394458_) {
        return cube(p_394458_, p_394458_, p_394458_);
    }

    public static VoxelShape cube(double p_393493_, double p_394533_, double p_394623_) {
        double d0 = p_394533_ / 2.0;
        return column(p_393493_, p_394623_, 8.0 - d0, 8.0 + d0);
    }

    public static VoxelShape column(double p_393922_, double p_394403_, double p_393991_) {
        return column(p_393922_, p_393922_, p_394403_, p_393991_);
    }

    public static VoxelShape column(double p_393678_, double p_394077_, double p_394409_, double p_394538_) {
        double d0 = p_393678_ / 2.0;
        double d1 = p_394077_ / 2.0;
        return box(8.0 - d0, p_394409_, 8.0 - d1, 8.0 + d0, p_394538_, 8.0 + d1);
    }

    public static VoxelShape boxZ(double p_393726_, double p_394571_, double p_393953_) {
        return boxZ(p_393726_, p_393726_, p_394571_, p_393953_);
    }

    public static VoxelShape boxZ(double p_393965_, double p_393708_, double p_393930_, double p_394112_) {
        double d0 = p_393708_ / 2.0;
        return boxZ(p_393965_, 8.0 - d0, 8.0 + d0, p_393930_, p_394112_);
    }

    public static VoxelShape boxZ(double p_394601_, double p_394192_, double p_393712_, double p_394326_, double p_394559_) {
        double d0 = p_394601_ / 2.0;
        return box(8.0 - d0, p_394192_, p_394326_, 8.0 + d0, p_393712_, p_394559_);
    }

    public static BlockState updateFromNeighbourShapes(BlockState p_49932_, LevelAccessor p_49933_, BlockPos p_49934_) {
        BlockState blockstate = p_49932_;
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for (Direction direction : UPDATE_SHAPE_ORDER) {
            blockpos$mutableblockpos.setWithOffset(p_49934_, direction);
            blockstate = blockstate.updateShape(
                p_49933_, p_49933_, p_49934_, direction, blockpos$mutableblockpos, p_49933_.getBlockState(blockpos$mutableblockpos), p_49933_.getRandom()
            );
        }

        return blockstate;
    }

    public static void updateOrDestroy(BlockState p_49903_, BlockState p_49904_, LevelAccessor p_49905_, BlockPos p_49906_, @Block.UpdateFlags int p_49907_) {
        updateOrDestroy(p_49903_, p_49904_, p_49905_, p_49906_, p_49907_, 512);
    }

    public static void updateOrDestroy(
        BlockState p_49909_, BlockState p_49910_, LevelAccessor p_49911_, BlockPos p_49912_, @Block.UpdateFlags int p_49913_, int p_49914_
    ) {
        if (p_49910_ != p_49909_) {
            if (p_49910_.isAir()) {
                if (!p_49911_.isClientSide()) {
                    p_49911_.destroyBlock(p_49912_, (p_49913_ & 32) == 0, null, p_49914_);
                }
            } else {
                p_49911_.setBlock(p_49912_, p_49910_, p_49913_ & -33, p_49914_);
            }
        }
    }

    public Block(BlockBehaviour.Properties p_49795_) {
        super(p_49795_);
        StateDefinition.Builder<Block, BlockState> builder = new StateDefinition.Builder<>(this);
        this.createBlockStateDefinition(builder);
        this.stateDefinition = builder.create(Block::defaultBlockState, BlockState::new);
        this.registerDefaultState(this.stateDefinition.any());
        if (SharedConstants.IS_RUNNING_IN_IDE && false) {
            String s = this.getClass().getSimpleName();
            if (!s.endsWith("Block")) {
                LOGGER.error("Block classes should end with Block and {} doesn't.", s);
            }
        }
    }

    public static boolean isExceptionForConnection(BlockState p_152464_) {
        return p_152464_.getBlock() instanceof LeavesBlock
            || p_152464_.is(Blocks.BARRIER)
            || p_152464_.is(Blocks.CARVED_PUMPKIN)
            || p_152464_.is(Blocks.JACK_O_LANTERN)
            || p_152464_.is(Blocks.MELON)
            || p_152464_.is(Blocks.PUMPKIN)
            || p_152464_.is(BlockTags.SHULKER_BOXES);
    }

    protected static boolean dropFromBlockInteractLootTable(
        ServerLevel p_434351_,
        ResourceKey<LootTable> p_433048_,
        BlockState p_433540_,
        @Nullable BlockEntity p_434845_,
        @Nullable ItemStack p_432854_,
        @Nullable Entity p_435907_,
        BiConsumer<ServerLevel, ItemStack> p_433624_
    ) {
        return dropFromLootTable(
            p_434351_,
            p_433048_,
            p_432610_ -> p_432610_.withParameter(LootContextParams.BLOCK_STATE, p_433540_)
                .withOptionalParameter(LootContextParams.BLOCK_ENTITY, p_434845_)
                .withOptionalParameter(LootContextParams.INTERACTING_ENTITY, p_435907_)
                .withOptionalParameter(LootContextParams.TOOL, p_432854_)
                .create(LootContextParamSets.BLOCK_INTERACT),
            p_433624_
        );
    }

    protected static boolean dropFromLootTable(
        ServerLevel p_435579_,
        ResourceKey<LootTable> p_433074_,
        Function<LootParams.Builder, LootParams> p_433532_,
        BiConsumer<ServerLevel, ItemStack> p_435844_
    ) {
        LootTable loottable = p_435579_.getServer().reloadableRegistries().getLootTable(p_433074_);
        LootParams lootparams = p_433532_.apply(new LootParams.Builder(p_435579_));
        List<ItemStack> list = loottable.getRandomItems(lootparams);
        if (!list.isEmpty()) {
            list.forEach(p_432605_ -> p_435844_.accept(p_435579_, p_432605_));
            return true;
        } else {
            return false;
        }
    }

    /**
     * @deprecated Neo: use overload with level context instead
     */
    @Deprecated
    public static boolean shouldRenderFace(BlockState p_152445_, BlockState p_361252_, Direction p_152448_) {
        return shouldRenderFace(net.minecraft.world.level.EmptyBlockGetter.INSTANCE, BlockPos.ZERO, p_152445_, p_361252_, p_152448_);
    }

    public static boolean shouldRenderFace(BlockGetter level, BlockPos pos, BlockState p_152445_, BlockState p_361252_, Direction p_152448_) {
        VoxelShape voxelshape = p_361252_.getFaceOcclusionShape(p_152448_.getOpposite());
        if (voxelshape == Shapes.block()) {
            return false;
        } else if (p_152445_.skipRendering(p_361252_, p_152448_)) {
            return false;
        } else if (p_361252_.hidesNeighborFace(level, pos.relative(p_152448_), p_152445_, p_152448_.getOpposite()) && p_152445_.supportsExternalFaceHiding()) {
            return false;
        } else if (voxelshape == Shapes.empty()) {
            return true;
        } else {
            VoxelShape voxelshape1 = p_152445_.getFaceOcclusionShape(p_152448_);
            if (voxelshape1 == Shapes.empty()) {
                return true;
            } else {
                Block.ShapePairKey block$shapepairkey = new Block.ShapePairKey(voxelshape1, voxelshape);
                Object2ByteLinkedOpenHashMap<Block.ShapePairKey> object2bytelinkedopenhashmap = OCCLUSION_CACHE.get();
                byte b0 = object2bytelinkedopenhashmap.getAndMoveToFirst(block$shapepairkey);
                if (b0 != 127) {
                    return b0 != 0;
                } else {
                    boolean flag = Shapes.joinIsNotEmpty(voxelshape1, voxelshape, BooleanOp.ONLY_FIRST);
                    if (object2bytelinkedopenhashmap.size() == 256) {
                        object2bytelinkedopenhashmap.removeLastByte();
                    }

                    object2bytelinkedopenhashmap.putAndMoveToFirst(block$shapepairkey, (byte)(flag ? 1 : 0));
                    return flag;
                }
            }
        }
    }

    public static boolean canSupportRigidBlock(BlockGetter p_49937_, BlockPos p_49938_) {
        return p_49937_.getBlockState(p_49938_).isFaceSturdy(p_49937_, p_49938_, Direction.UP, SupportType.RIGID);
    }

    public static boolean canSupportCenter(LevelReader p_49864_, BlockPos p_49865_, Direction p_49866_) {
        BlockState blockstate = p_49864_.getBlockState(p_49865_);
        return p_49866_ == Direction.DOWN && blockstate.is(BlockTags.UNSTABLE_BOTTOM_CENTER)
            ? false
            : blockstate.isFaceSturdy(p_49864_, p_49865_, p_49866_, SupportType.CENTER);
    }

    public static boolean isFaceFull(VoxelShape p_49919_, Direction p_49920_) {
        VoxelShape voxelshape = p_49919_.getFaceShape(p_49920_);
        return isShapeFullBlock(voxelshape);
    }

    public static boolean isShapeFullBlock(VoxelShape p_49917_) {
        return SHAPE_FULL_BLOCK_CACHE.getUnchecked(p_49917_);
    }

    public void animateTick(BlockState p_220827_, Level p_220828_, BlockPos p_220829_, RandomSource p_220830_) {
    }

    public void destroy(LevelAccessor p_49860_, BlockPos p_49861_, BlockState p_49862_) {
    }

    public static List<ItemStack> getDrops(BlockState p_49870_, ServerLevel p_49871_, BlockPos p_49872_, @Nullable BlockEntity p_49873_) {
        LootParams.Builder lootparams$builder = new LootParams.Builder(p_49871_)
            .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(p_49872_))
            .withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
            .withOptionalParameter(LootContextParams.BLOCK_ENTITY, p_49873_);
        return p_49870_.getDrops(lootparams$builder);
    }

    public static List<ItemStack> getDrops(
        BlockState p_49875_, ServerLevel p_49876_, BlockPos p_49877_, @Nullable BlockEntity p_49878_, @Nullable Entity p_49879_, ItemStack p_49880_
    ) {
        LootParams.Builder lootparams$builder = new LootParams.Builder(p_49876_)
            .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(p_49877_))
            .withParameter(LootContextParams.TOOL, p_49880_)
            .withOptionalParameter(LootContextParams.THIS_ENTITY, p_49879_)
            .withOptionalParameter(LootContextParams.BLOCK_ENTITY, p_49878_);
        return p_49875_.getDrops(lootparams$builder);
    }

    public static void dropResources(BlockState p_49951_, Level p_49952_, BlockPos p_49953_) {
        if (p_49952_ instanceof ServerLevel) {
            beginCapturingDrops();
            getDrops(p_49951_, (ServerLevel)p_49952_, p_49953_, null).forEach(p_152406_ -> popResource(p_49952_, p_49953_, p_152406_));
            List<ItemEntity> captured = stopCapturingDrops();
            net.neoforged.neoforge.common.CommonHooks.handleBlockDrops((ServerLevel) p_49952_, p_49953_, p_49951_, null, captured, null, ItemStack.EMPTY);
        }
    }

    public static void dropResources(BlockState p_49893_, LevelAccessor p_49894_, BlockPos p_49895_, @Nullable BlockEntity p_49896_) {
        if (p_49894_ instanceof ServerLevel) {
            beginCapturingDrops();
            getDrops(p_49893_, (ServerLevel)p_49894_, p_49895_, p_49896_).forEach(p_49859_ -> popResource((ServerLevel)p_49894_, p_49895_, p_49859_));
            List<ItemEntity> captured = stopCapturingDrops();
            net.neoforged.neoforge.common.CommonHooks.handleBlockDrops((ServerLevel) p_49894_, p_49895_, p_49893_, p_49896_, captured, null, ItemStack.EMPTY);
        }
    }

    public static void dropResources(
            BlockState p_49882_, Level p_49883_, BlockPos p_49884_, @Nullable BlockEntity p_49885_, @Nullable Entity p_49886_, ItemStack p_49887_
    ) {
        if (p_49883_ instanceof ServerLevel) {
            beginCapturingDrops();
            getDrops(p_49882_, (ServerLevel)p_49883_, p_49884_, p_49885_, p_49886_, p_49887_).forEach(p_49944_ -> popResource(p_49883_, p_49884_, p_49944_));
            List<ItemEntity> captured = stopCapturingDrops();
            net.neoforged.neoforge.common.CommonHooks.handleBlockDrops((ServerLevel) p_49883_, p_49884_, p_49882_, p_49885_, captured, p_49886_, p_49887_);
        }
    }

    public static void popResource(Level p_49841_, BlockPos p_49842_, ItemStack p_49843_) {
        double d0 = EntityType.ITEM.getHeight() / 2.0;
        double d1 = p_49842_.getX() + 0.5 + Mth.nextDouble(p_49841_.random, -0.25, 0.25);
        double d2 = p_49842_.getY() + 0.5 + Mth.nextDouble(p_49841_.random, -0.25, 0.25) - d0;
        double d3 = p_49842_.getZ() + 0.5 + Mth.nextDouble(p_49841_.random, -0.25, 0.25);
        popResource(p_49841_, () -> new ItemEntity(p_49841_, d1, d2, d3, p_49843_), p_49843_);
    }

    public static void popResourceFromFace(Level p_152436_, BlockPos p_152437_, Direction p_152438_, ItemStack p_152439_) {
        int i = p_152438_.getStepX();
        int j = p_152438_.getStepY();
        int k = p_152438_.getStepZ();
        double d0 = EntityType.ITEM.getWidth() / 2.0;
        double d1 = EntityType.ITEM.getHeight() / 2.0;
        double d2 = p_152437_.getX() + 0.5 + (i == 0 ? Mth.nextDouble(p_152436_.random, -0.25, 0.25) : i * (0.5 + d0));
        double d3 = p_152437_.getY() + 0.5 + (j == 0 ? Mth.nextDouble(p_152436_.random, -0.25, 0.25) : j * (0.5 + d1)) - d1;
        double d4 = p_152437_.getZ() + 0.5 + (k == 0 ? Mth.nextDouble(p_152436_.random, -0.25, 0.25) : k * (0.5 + d0));
        double d5 = i == 0 ? Mth.nextDouble(p_152436_.random, -0.1, 0.1) : i * 0.1;
        double d6 = j == 0 ? Mth.nextDouble(p_152436_.random, 0.0, 0.1) : j * 0.1 + 0.1;
        double d7 = k == 0 ? Mth.nextDouble(p_152436_.random, -0.1, 0.1) : k * 0.1;
        popResource(p_152436_, () -> new ItemEntity(p_152436_, d2, d3, d4, p_152439_, d5, d6, d7), p_152439_);
    }

    private static void popResource(Level p_152441_, Supplier<ItemEntity> p_152442_, ItemStack p_152443_) {
        if (p_152441_ instanceof ServerLevel serverLevel && !p_152443_.isEmpty() && serverLevel.getGameRules().get(GameRules.BLOCK_DROPS) && !p_152441_.restoringBlockSnapshots) {
            ItemEntity itementity = p_152442_.get();
            itementity.setDefaultPickUpDelay();
            // Neo: Add drops to the captured list if capturing is enabled.
            if (capturedDrops != null) {
                capturedDrops.add(itementity);
            }
            else {
                p_152441_.addFreshEntity(itementity);
            }
        }
    }

    public void popExperience(ServerLevel p_49806_, BlockPos p_49807_, int p_49808_) {
        if (p_49806_.getGameRules().get(GameRules.BLOCK_DROPS) && !p_49806_.restoringBlockSnapshots) {
            ExperienceOrb.award(p_49806_, Vec3.atCenterOf(p_49807_), p_49808_);
        }
    }

    @Deprecated //Forge: Use more sensitive version
    public float getExplosionResistance() {
        return this.explosionResistance;
    }

    public void wasExploded(ServerLevel p_361333_, BlockPos p_49845_, Explosion p_49846_) {
    }

    public void stepOn(Level p_152431_, BlockPos p_152432_, BlockState p_152433_, Entity p_152434_) {
    }

    public @Nullable BlockState getStateForPlacement(BlockPlaceContext p_49820_) {
        return this.defaultBlockState();
    }

    public void playerDestroy(Level p_49827_, Player p_49828_, BlockPos p_49829_, BlockState p_49830_, @Nullable BlockEntity p_49831_, ItemStack p_49832_) {
        p_49828_.awardStat(Stats.BLOCK_MINED.get(this));
        p_49828_.causeFoodExhaustion(0.005F);
        dropResources(p_49830_, p_49827_, p_49829_, p_49831_, p_49828_, p_49832_);
    }

    public void setPlacedBy(Level p_49847_, BlockPos p_49848_, BlockState p_49849_, @Nullable LivingEntity p_49850_, ItemStack p_49851_) {
    }

    public boolean isPossibleToRespawnInThis(BlockState p_279289_) {
        return !p_279289_.isSolid() && !p_279289_.liquid();
    }

    public MutableComponent getName() {
        return Component.translatable(this.getDescriptionId());
    }

    public void fallOn(Level p_152426_, BlockState p_152427_, BlockPos p_152428_, Entity p_152429_, double p_397222_) {
        p_152429_.causeFallDamage(p_397222_, 1.0F, p_152429_.damageSources().fall());
    }

    public void updateEntityMovementAfterFallOn(BlockGetter p_49821_, Entity p_49822_) {
        p_49822_.setDeltaMovement(p_49822_.getDeltaMovement().multiply(1.0, 0.0, 1.0));
    }

    public float getFriction() {
        return this.friction;
    }

    public float getSpeedFactor() {
        return this.speedFactor;
    }

    public float getJumpFactor() {
        return this.jumpFactor;
    }

    protected void spawnDestroyParticles(Level p_152422_, Player p_152423_, BlockPos p_152424_, BlockState p_152425_) {
        p_152422_.levelEvent(p_152423_, 2001, p_152424_, getId(p_152425_));
    }

    public BlockState playerWillDestroy(Level p_49852_, BlockPos p_49853_, BlockState p_49854_, Player p_49855_) {
        this.spawnDestroyParticles(p_49852_, p_49855_, p_49853_, p_49854_);
        if (p_49854_.is(BlockTags.GUARDED_BY_PIGLINS) && p_49852_ instanceof ServerLevel serverlevel) {
            PiglinAi.angerNearbyPiglins(serverlevel, p_49855_, false);
        }

        p_49852_.gameEvent(GameEvent.BLOCK_DESTROY, p_49853_, GameEvent.Context.of(p_49855_, p_49854_));
        return p_49854_;
    }

    public void handlePrecipitation(BlockState p_152450_, Level p_152451_, BlockPos p_152452_, Biome.Precipitation p_152453_) {
    }

    @Deprecated //Forge: Use more sensitive version
    public boolean dropFromExplosion(Explosion p_49826_) {
        return true;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49915_) {
    }

    public StateDefinition<Block, BlockState> getStateDefinition() {
        return this.stateDefinition;
    }

    protected final void registerDefaultState(BlockState p_49960_) {
        this.defaultBlockState = p_49960_;
    }

    public final BlockState defaultBlockState() {
        return this.defaultBlockState;
    }

    public final BlockState withPropertiesOf(BlockState p_152466_) {
        BlockState blockstate = this.defaultBlockState();

        for (Property<?> property : p_152466_.getBlock().getStateDefinition().getProperties()) {
            if (blockstate.hasProperty(property)) {
                blockstate = copyProperty(p_152466_, blockstate, property);
            }
        }

        return blockstate;
    }

    private static <T extends Comparable<T>> BlockState copyProperty(BlockState p_152455_, BlockState p_152456_, Property<T> p_152457_) {
        return p_152456_.setValue(p_152457_, p_152455_.getValue(p_152457_));
    }

    @Override
    public Item asItem() {
        if (this.item == null) {
            this.item = Item.byBlock(this);
        }

        return this.item;
    }

    public boolean hasDynamicShape() {
        return this.dynamicShape;
    }

    @Override
    public String toString() {
        return "Block{" + BuiltInRegistries.BLOCK.wrapAsHolder(this).getRegisteredName() + "}";
    }

    @Override
    protected Block asBlock() {
        return this;
    }

    protected Function<BlockState, VoxelShape> getShapeForEachState(Function<BlockState, VoxelShape> p_394452_) {
        return this.stateDefinition.getPossibleStates().stream().collect(ImmutableMap.toImmutableMap(Function.identity(), p_394452_))::get;
    }

    protected Function<BlockState, VoxelShape> getShapeForEachState(Function<BlockState, VoxelShape> p_152459_, Property<?>... p_394264_) {
        Map<? extends Property<?>, Object> map = Arrays.stream(p_394264_)
            .collect(Collectors.toMap(p_393321_ -> p_393321_, p_393320_ -> p_393320_.getPossibleValues().getFirst()));
        ImmutableMap<BlockState, VoxelShape> immutablemap = this.stateDefinition
            .getPossibleStates()
            .stream()
            .filter(p_393327_ -> map.entrySet().stream().allMatch(p_393329_ -> p_393327_.getValue((Property<?>)p_393329_.getKey()) == p_393329_.getValue()))
            .collect(ImmutableMap.toImmutableMap(Function.identity(), p_152459_));
        return p_393324_ -> {
            for (Entry<? extends Property<?>, Object> entry : map.entrySet()) {
                p_393324_ = setValueHelper(p_393324_, (Property<?>)entry.getKey(), entry.getValue());
            }

            return immutablemap.get(p_393324_);
        };
    }

    private static <S extends StateHolder<?, S>, T extends Comparable<T>> S setValueHelper(S p_394311_, Property<T> p_394352_, Object p_394525_) {
        return p_394311_.setValue(p_394352_, (T)p_394525_);
    }

    /**
     * Neo: Short-lived holder of dropped item entities. Used mainly for Neo hooks and event logic.
     * <p>
     * When not null, records all item entities from {@link #popResource(Level, Supplier, ItemStack)} instead of adding them to the world.
     */
    @Nullable
    private static List<ItemEntity> capturedDrops = null;

    /**
     * Initializes {@link #capturedDrops}, starting the drop capture process.
     * <p>
     * Must only be called on the server thread.
     */
    private static void beginCapturingDrops() {
        capturedDrops = new java.util.ArrayList<>();
    }

    /**
     * Ends the drop capture process by setting {@link #capturedDrops} to null and returning the old list.
     * <p>
     * Must only be called on the server thread.
     */
    private static List<ItemEntity> stopCapturingDrops() {
        List<ItemEntity> drops = capturedDrops;
        capturedDrops = null;
        return drops;
    }

    /** @deprecated */
    @Deprecated
    public Holder.Reference<Block> builtInRegistryHolder() {
        return this.builtInRegistryHolder;
    }

    protected void tryDropExperience(ServerLevel p_220823_, BlockPos p_220824_, ItemStack p_220825_, IntProvider p_220826_) {
        int i = EnchantmentHelper.processBlockExperience(p_220823_, p_220825_, p_220826_.sample(p_220823_.getRandom()));
        if (i > 0) {
            this.popExperience(p_220823_, p_220824_, i);
        }
    }

    record ShapePairKey(VoxelShape first, VoxelShape second) {
        @Override
        public boolean equals(Object p_364876_) {
            return p_364876_ instanceof Block.ShapePairKey block$shapepairkey
                && this.first == block$shapepairkey.first
                && this.second == block$shapepairkey.second;
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(this.first) * 31 + System.identityHashCode(this.second);
        }
    }

    @Retention(RetentionPolicy.CLASS)
    @Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.METHOD, ElementType.TYPE_USE})
    @org.intellij.lang.annotations.MagicConstant(flags = {UPDATE_NEIGHBORS, UPDATE_CLIENTS, UPDATE_INVISIBLE, UPDATE_IMMEDIATE, UPDATE_KNOWN_SHAPE, UPDATE_SUPPRESS_DROPS, UPDATE_MOVE_BY_PISTON, UPDATE_SKIP_SHAPE_UPDATE_ON_WIRE, UPDATE_SKIP_BLOCK_ENTITY_SIDEEFFECTS, UPDATE_SKIP_ON_PLACE})
    public @interface UpdateFlags {
    }
}
