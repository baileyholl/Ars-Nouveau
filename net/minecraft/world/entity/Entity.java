package net.minecraft.world.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.floats.FloatArraySet;
import it.unimi.dsi.fastutil.floats.FloatArrays;
import it.unimi.dsi.fastutil.floats.FloatSet;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2DoubleArrayMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.protocol.game.VecDeltaCodec;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SyncedDataHolder;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ARGB;
import net.minecraft.util.BlockUtil;
import net.minecraft.util.Mth;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.util.debug.DebugEntityBlockIntersection;
import net.minecraft.util.debug.DebugSubscriptions;
import net.minecraft.util.debug.DebugValueSource;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.Nameable;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.HoneyBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntityInLevelCallback;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Team;
import net.minecraft.world.waypoints.WaypointTransmitter;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public abstract class Entity extends net.neoforged.neoforge.attachment.AttachmentHolder implements SyncedDataHolder, DebugValueSource, Nameable, ItemOwner, SlotProvider, EntityAccess, ScoreHolder, DataComponentGetter, net.neoforged.neoforge.common.extensions.IEntityExtension {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String TAG_ID = "id";
    public static final String TAG_UUID = "UUID";
    public static final String TAG_PASSENGERS = "Passengers";
    public static final String TAG_DATA = "data";
    public static final String TAG_POS = "Pos";
    public static final String TAG_MOTION = "Motion";
    public static final String TAG_ROTATION = "Rotation";
    public static final String TAG_PORTAL_COOLDOWN = "PortalCooldown";
    public static final String TAG_NO_GRAVITY = "NoGravity";
    public static final String TAG_AIR = "Air";
    public static final String TAG_ON_GROUND = "OnGround";
    public static final String TAG_FALL_DISTANCE = "fall_distance";
    public static final String TAG_FIRE = "Fire";
    public static final String TAG_SILENT = "Silent";
    public static final String TAG_GLOWING = "Glowing";
    public static final String TAG_INVULNERABLE = "Invulnerable";
    public static final String TAG_CUSTOM_NAME = "CustomName";
    protected static final AtomicInteger ENTITY_COUNTER = new AtomicInteger();
    public static final int CONTENTS_SLOT_INDEX = 0;
    public static final int BOARDING_COOLDOWN = 60;
    public static final int TOTAL_AIR_SUPPLY = 300;
    public static final int MAX_ENTITY_TAG_COUNT = 1024;
    private static final Codec<List<String>> TAG_LIST_CODEC = Codec.STRING.sizeLimitedListOf(1024);
    public static final float DELTA_AFFECTED_BY_BLOCKS_BELOW_0_2 = 0.2F;
    public static final double DELTA_AFFECTED_BY_BLOCKS_BELOW_0_5 = 0.500001;
    public static final double DELTA_AFFECTED_BY_BLOCKS_BELOW_1_0 = 0.999999;
    public static final int BASE_TICKS_REQUIRED_TO_FREEZE = 140;
    public static final int FREEZE_HURT_FREQUENCY = 40;
    public static final int BASE_SAFE_FALL_DISTANCE = 3;
    private static final AABB INITIAL_AABB = new AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    private static final double WATER_FLOW_SCALE = 0.014;
    private static final double LAVA_FAST_FLOW_SCALE = 0.007;
    private static final double LAVA_SLOW_FLOW_SCALE = 0.0023333333333333335;
    private static final int MAX_BLOCK_ITERATIONS_ALONG_TRAVEL_PER_TICK = 16;
    private static final double MAX_MOVEMENT_RESETTING_TRACE_DISTANCE = 8.0;
    private static double viewScale = 1.0;
    @Deprecated // Neo: Use the getter to allow overriding in mods
    private final EntityType<?> type;
    private boolean requiresPrecisePosition;
    private int id = ENTITY_COUNTER.incrementAndGet();
    public boolean blocksBuilding;
    private ImmutableList<Entity> passengers = ImmutableList.of();
    protected int boardingCooldown;
    private @Nullable Entity vehicle;
    public Level level;
    public double xo;
    public double yo;
    public double zo;
    public Vec3 position;
    private BlockPos blockPosition;
    private ChunkPos chunkPosition;
    private Vec3 deltaMovement = Vec3.ZERO;
    public float yRot;
    public float xRot;
    public float yRotO;
    public float xRotO;
    private AABB bb = INITIAL_AABB;
    private boolean onGround;
    public boolean horizontalCollision;
    public boolean verticalCollision;
    public boolean verticalCollisionBelow;
    public boolean minorHorizontalCollision;
    public boolean hurtMarked;
    protected Vec3 stuckSpeedMultiplier = Vec3.ZERO;
    private Entity.@Nullable RemovalReason removalReason;
    public static final float DEFAULT_BB_WIDTH = 0.6F;
    public static final float DEFAULT_BB_HEIGHT = 1.8F;
    public float moveDist;
    public float flyDist;
    public double fallDistance;
    private float nextStep = 1.0F;
    public double xOld;
    public double yOld;
    public double zOld;
    public boolean noPhysics;
    protected final RandomSource random = RandomSource.create();
    public int tickCount;
    private int remainingFireTicks;
    protected boolean wasTouchingWater;
    @Deprecated // Neo: Use forgeFluidTypeHeight instead
    protected Object2DoubleMap<TagKey<Fluid>> fluidHeight = new Object2DoubleArrayMap<>(2);
    protected boolean wasEyeInWater;
    @Deprecated // Neo: Use forgeFluidTypeOnEyes instead
    private final Set<TagKey<Fluid>> fluidOnEyes = new HashSet<>();
    public int invulnerableTime;
    protected boolean firstTick = true;
    protected final SynchedEntityData entityData;
    protected static final EntityDataAccessor<Byte> DATA_SHARED_FLAGS_ID = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.BYTE);
    protected static final int FLAG_ONFIRE = 0;
    private static final int FLAG_SHIFT_KEY_DOWN = 1;
    private static final int FLAG_SPRINTING = 3;
    private static final int FLAG_SWIMMING = 4;
    private static final int FLAG_INVISIBLE = 5;
    protected static final int FLAG_GLOWING = 6;
    protected static final int FLAG_FALL_FLYING = 7;
    private static final EntityDataAccessor<Integer> DATA_AIR_SUPPLY_ID = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Optional<Component>> DATA_CUSTOM_NAME = SynchedEntityData.defineId(
        Entity.class, EntityDataSerializers.OPTIONAL_COMPONENT
    );
    private static final EntityDataAccessor<Boolean> DATA_CUSTOM_NAME_VISIBLE = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_SILENT = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_NO_GRAVITY = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Pose> DATA_POSE = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.POSE);
    private static final EntityDataAccessor<Integer> DATA_TICKS_FROZEN = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.INT);
    private EntityInLevelCallback levelCallback = EntityInLevelCallback.NULL;
    private final VecDeltaCodec packetPositionCodec = new VecDeltaCodec();
    public boolean needsSync;
    public @Nullable PortalProcessor portalProcess;
    private int portalCooldown;
    private boolean invulnerable;
    protected UUID uuid = Mth.createInsecureUUID(this.random);
    protected String stringUUID = this.uuid.toString();
    private boolean hasGlowingTag;
    private final Set<String> tags = Sets.newHashSet();
    private final double[] pistonDeltas = new double[]{0.0, 0.0, 0.0};
    private long pistonDeltasGameTime;
    private EntityDimensions dimensions;
    public float eyeHeight;
    public boolean isInPowderSnow;
    public boolean wasInPowderSnow;
    public Optional<BlockPos> mainSupportingBlockPos = Optional.empty();
    private boolean onGroundNoBlocks = false;
    private float crystalSoundIntensity;
    private int lastCrystalSoundPlayTick;
    private boolean hasVisualFire;
    private Vec3 lastKnownSpeed = Vec3.ZERO;
    private @Nullable Vec3 lastKnownPosition;
    private @Nullable BlockState inBlockState = null;
    public static final int MAX_MOVEMENTS_HANDELED_PER_TICK = 100;
    private final ArrayDeque<Entity.Movement> movementThisTick = new ArrayDeque<>(100);
    private final List<Entity.Movement> finalMovementsThisTick = new ObjectArrayList<>();
    private final LongSet visitedBlocks = new LongOpenHashSet();
    private final InsideBlockEffectApplier.StepBasedCollector insideEffectCollector = new InsideBlockEffectApplier.StepBasedCollector();
    private CustomData customData = CustomData.EMPTY;

    public Entity(EntityType<?> p_19870_, Level p_19871_) {
        this.type = p_19870_;
        this.level = p_19871_;
        this.dimensions = p_19870_.getDimensions();
        this.position = Vec3.ZERO;
        this.blockPosition = BlockPos.ZERO;
        this.chunkPosition = ChunkPos.ZERO;
        SynchedEntityData.Builder synchedentitydata$builder = new SynchedEntityData.Builder(this);
        synchedentitydata$builder.define(DATA_SHARED_FLAGS_ID, (byte)0);
        synchedentitydata$builder.define(DATA_AIR_SUPPLY_ID, this.getMaxAirSupply());
        synchedentitydata$builder.define(DATA_CUSTOM_NAME_VISIBLE, false);
        synchedentitydata$builder.define(DATA_CUSTOM_NAME, Optional.empty());
        synchedentitydata$builder.define(DATA_SILENT, false);
        synchedentitydata$builder.define(DATA_NO_GRAVITY, false);
        synchedentitydata$builder.define(DATA_POSE, Pose.STANDING);
        synchedentitydata$builder.define(DATA_TICKS_FROZEN, 0);
        this.defineSynchedData(synchedentitydata$builder);
        this.entityData = synchedentitydata$builder.build();
        this.setPos(0.0, 0.0, 0.0);
        net.neoforged.neoforge.event.entity.EntityEvent.Size sizeEvent = net.neoforged.neoforge.event.EventHooks.getEntitySizeForge(this, Pose.STANDING, this.dimensions);
        this.dimensions = sizeEvent.getNewSize();
        this.eyeHeight = this.dimensions.eyeHeight();
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.entity.EntityEvent.EntityConstructing(this));
    }

    public boolean isColliding(BlockPos p_20040_, BlockState p_20041_) {
        VoxelShape voxelshape = p_20041_.getCollisionShape(this.level(), p_20040_, CollisionContext.of(this)).move(p_20040_);
        return Shapes.joinIsNotEmpty(voxelshape, Shapes.create(this.getBoundingBox()), BooleanOp.AND);
    }

    public int getTeamColor() {
        Team team = this.getTeam();
        return team != null && team.getColor().getColor() != null ? team.getColor().getColor() : 16777215;
    }

    public boolean isSpectator() {
        return false;
    }

    public boolean canInteractWithLevel() {
        return this.isAlive() && !this.isRemoved() && !this.isSpectator();
    }

    public final void unRide() {
        if (this.isVehicle()) {
            this.ejectPassengers();
        }

        if (this.isPassenger()) {
            this.stopRiding();
        }
    }

    public void syncPacketPositionCodec(double p_217007_, double p_217008_, double p_217009_) {
        this.packetPositionCodec.setBase(new Vec3(p_217007_, p_217008_, p_217009_));
    }

    public VecDeltaCodec getPositionCodec() {
        return this.packetPositionCodec;
    }

    public EntityType<?> getType() {
        return this.type;
    }

    public boolean getRequiresPrecisePosition() {
        return this.requiresPrecisePosition;
    }

    public void setRequiresPrecisePosition(boolean p_416605_) {
        this.requiresPrecisePosition = p_416605_;
    }

    @Override
    public int getId() {
        return this.id;
    }

    public void setId(int p_20235_) {
        this.id = p_20235_;
    }

    public Set<String> getTags() {
        return this.tags;
    }

    public boolean addTag(String p_20050_) {
        return this.tags.size() >= 1024 ? false : this.tags.add(p_20050_);
    }

    public boolean removeTag(String p_20138_) {
        return this.tags.remove(p_20138_);
    }

    public void kill(ServerLevel p_376411_) {
        this.remove(Entity.RemovalReason.KILLED);
        this.gameEvent(GameEvent.ENTITY_DIE);
    }

    public final void discard() {
        this.remove(Entity.RemovalReason.DISCARDED);
    }

    protected abstract void defineSynchedData(SynchedEntityData.Builder p_326003_);

    public SynchedEntityData getEntityData() {
        return this.entityData;
    }

    @Override
    public boolean equals(Object p_20245_) {
        return p_20245_ instanceof Entity ? ((Entity)p_20245_).id == this.id : false;
    }

    @Override
    public int hashCode() {
        return this.id;
    }

    public void remove(Entity.RemovalReason p_146834_) {
        this.setRemoved(p_146834_);
    }

    public void onClientRemoval() {
    }

    public void onRemoval(Entity.RemovalReason p_379764_) {
    }

    public void setPose(Pose p_20125_) {
        this.entityData.set(DATA_POSE, p_20125_);
    }

    public Pose getPose() {
        return this.entityData.get(DATA_POSE);
    }

    public boolean hasPose(Pose p_217004_) {
        return this.getPose() == p_217004_;
    }

    public boolean closerThan(Entity p_19951_, double p_19952_) {
        return this.position().closerThan(p_19951_.position(), p_19952_);
    }

    public boolean closerThan(Entity p_216993_, double p_216994_, double p_216995_) {
        double d0 = p_216993_.getX() - this.getX();
        double d1 = p_216993_.getY() - this.getY();
        double d2 = p_216993_.getZ() - this.getZ();
        return Mth.lengthSquared(d0, d2) < Mth.square(p_216994_) && Mth.square(d1) < Mth.square(p_216995_);
    }

    protected void setRot(float p_19916_, float p_19917_) {
        this.setYRot(p_19916_ % 360.0F);
        this.setXRot(p_19917_ % 360.0F);
    }

    public final void setPos(Vec3 p_146885_) {
        this.setPos(p_146885_.x(), p_146885_.y(), p_146885_.z());
    }

    public void setPos(double p_20210_, double p_20211_, double p_20212_) {
        this.setPosRaw(p_20210_, p_20211_, p_20212_);
        this.setBoundingBox(this.makeBoundingBox());
    }

    protected final AABB makeBoundingBox() {
        return this.makeBoundingBox(this.position);
    }

    protected AABB makeBoundingBox(Vec3 p_388602_) {
        return this.dimensions.makeBoundingBox(p_388602_);
    }

    protected void reapplyPosition() {
        this.lastKnownPosition = null;
        this.setPos(this.position.x, this.position.y, this.position.z);
    }

    public void turn(double p_19885_, double p_19886_) {
        float f = (float)p_19886_ * 0.15F;
        float f1 = (float)p_19885_ * 0.15F;
        this.setXRot(this.getXRot() + f);
        this.setYRot(this.getYRot() + f1);
        this.setXRot(Mth.clamp(this.getXRot(), -90.0F, 90.0F));
        this.xRotO += f;
        this.yRotO += f1;
        this.xRotO = Mth.clamp(this.xRotO, -90.0F, 90.0F);
        if (this.vehicle != null) {
            this.vehicle.onPassengerTurned(this);
        }
    }

    public void updateDataBeforeSync() {
    }

    public void tick() {
        this.baseTick();
    }

    public void baseTick() {
        ProfilerFiller profilerfiller = Profiler.get();
        profilerfiller.push("entityBaseTick");
        this.computeSpeed();
        this.inBlockState = null;
        if (this.isPassenger() && this.getVehicle().isRemoved()) {
            this.stopRiding();
        }

        if (this.boardingCooldown > 0) {
            this.boardingCooldown--;
        }

        this.handlePortal();
        if (this.canSpawnSprintParticle()) {
            this.spawnSprintParticle();
        }

        this.wasInPowderSnow = this.isInPowderSnow;
        this.isInPowderSnow = false;
        this.updateInWaterStateAndDoFluidPushing();
        this.updateFluidOnEyes();
        this.updateSwimming();
        if (this.level() instanceof ServerLevel serverlevel) {
            if (this.remainingFireTicks > 0) {
                if (this.fireImmune()) {
                    this.clearFire();
                } else {
                    if (this.remainingFireTicks % 20 == 0 && !this.isInLava()) {
                        this.hurtServer(serverlevel, this.damageSources().onFire(), 1.0F);
                    }

                    this.setRemainingFireTicks(this.remainingFireTicks - 1);
                }
            }
        } else {
            this.clearFire();
        }

        if (this.isInLava()) {
            this.fallDistance *= this.getFluidFallDistanceModifier(net.neoforged.neoforge.common.NeoForgeMod.LAVA_TYPE.value());
        }

        this.checkBelowWorld();
        if (!this.level().isClientSide()) {
            this.setSharedFlagOnFire(this.remainingFireTicks > 0);
        }

        this.firstTick = false;
        if (this.level() instanceof ServerLevel serverlevel1 && this instanceof Leashable) {
            Leashable.tickLeash(serverlevel1, (Entity & Leashable)this);
        }

        profilerfiller.pop();
    }

    protected void computeSpeed() {
        if (this.lastKnownPosition == null) {
            this.lastKnownPosition = this.position();
        }

        this.lastKnownSpeed = this.position().subtract(this.lastKnownPosition);
        this.lastKnownPosition = this.position();
    }

    public void setSharedFlagOnFire(boolean p_146869_) {
        this.setSharedFlag(0, p_146869_ || this.hasVisualFire);
    }

    public void checkBelowWorld() {
        if (this.getY() < this.level().getMinY() - 64) {
            this.onBelowWorld();
        }
    }

    public void setPortalCooldown() {
        this.portalCooldown = this.getDimensionChangingDelay();
    }

    public void setPortalCooldown(int p_287760_) {
        this.portalCooldown = p_287760_;
    }

    public int getPortalCooldown() {
        return this.portalCooldown;
    }

    public boolean isOnPortalCooldown() {
        return this.portalCooldown > 0;
    }

    protected void processPortalCooldown() {
        if (this.isOnPortalCooldown()) {
            this.portalCooldown--;
        }
    }

    public void lavaIgnite() {
        if (!this.fireImmune()) {
            this.igniteForSeconds(15.0F);
        }
    }

    public void lavaHurt() {
        if (!this.fireImmune()) {
            if (this.level() instanceof ServerLevel serverlevel
                && this.hurtServer(serverlevel, this.damageSources().lava(), 4.0F)
                && this.shouldPlayLavaHurtSound()
                && !this.isSilent()) {
                serverlevel.playSound(
                    null, this.getX(), this.getY(), this.getZ(), SoundEvents.GENERIC_BURN, this.getSoundSource(), 0.4F, 2.0F + this.random.nextFloat() * 0.4F
                );
            }
        }
    }

    protected boolean shouldPlayLavaHurtSound() {
        return true;
    }

    public final void igniteForSeconds(float p_345382_) {
        this.igniteForTicks(Mth.floor(p_345382_ * 20.0F));
    }

    public void igniteForTicks(int p_320711_) {
        if (this.remainingFireTicks < p_320711_) {
            this.setRemainingFireTicks(p_320711_);
        }

        this.clearFreeze();
    }

    public void setRemainingFireTicks(int p_20269_) {
        this.remainingFireTicks = p_20269_;
    }

    public int getRemainingFireTicks() {
        return this.remainingFireTicks;
    }

    public void clearFire() {
        this.setRemainingFireTicks(Math.min(0, this.getRemainingFireTicks()));
    }

    protected void onBelowWorld() {
        this.discard();
    }

    public boolean isFree(double p_20230_, double p_20231_, double p_20232_) {
        return this.isFree(this.getBoundingBox().move(p_20230_, p_20231_, p_20232_));
    }

    private boolean isFree(AABB p_20132_) {
        return this.level().noCollision(this, p_20132_) && !this.level().containsAnyLiquid(p_20132_);
    }

    public void setOnGround(boolean p_20181_) {
        this.onGround = p_20181_;
        this.checkSupportingBlock(p_20181_, null);
    }

    public void setOnGroundWithMovement(boolean p_386472_, Vec3 p_388270_) {
        this.setOnGroundWithMovement(p_386472_, this.horizontalCollision, p_388270_);
    }

    public void setOnGroundWithMovement(boolean p_289661_, boolean p_364321_, Vec3 p_289653_) {
        this.onGround = p_289661_;
        this.horizontalCollision = p_364321_;
        this.checkSupportingBlock(p_289661_, p_289653_);
    }

    public boolean isSupportedBy(BlockPos p_287613_) {
        return this.mainSupportingBlockPos.isPresent() && this.mainSupportingBlockPos.get().equals(p_287613_);
    }

    protected void checkSupportingBlock(boolean p_289694_, @Nullable Vec3 p_289680_) {
        if (p_289694_) {
            AABB aabb = this.getBoundingBox();
            AABB aabb1 = new AABB(aabb.minX, aabb.minY - 1.0E-6, aabb.minZ, aabb.maxX, aabb.minY, aabb.maxZ);
            Optional<BlockPos> optional = this.level.findSupportingBlock(this, aabb1);
            if (optional.isPresent() || this.onGroundNoBlocks) {
                this.mainSupportingBlockPos = optional;
            } else if (p_289680_ != null) {
                AABB aabb2 = aabb1.move(-p_289680_.x, 0.0, -p_289680_.z);
                optional = this.level.findSupportingBlock(this, aabb2);
                this.mainSupportingBlockPos = optional;
            }

            this.onGroundNoBlocks = optional.isEmpty();
        } else {
            this.onGroundNoBlocks = false;
            if (this.mainSupportingBlockPos.isPresent()) {
                this.mainSupportingBlockPos = Optional.empty();
            }
        }
    }

    public boolean onGround() {
        return this.onGround;
    }

    public void move(MoverType p_19973_, Vec3 p_19974_) {
        if (this.noPhysics) {
            this.setPos(this.getX() + p_19974_.x, this.getY() + p_19974_.y, this.getZ() + p_19974_.z);
            this.horizontalCollision = false;
            this.verticalCollision = false;
            this.verticalCollisionBelow = false;
            this.minorHorizontalCollision = false;
        } else {
            if (p_19973_ == MoverType.PISTON) {
                p_19974_ = this.limitPistonMovement(p_19974_);
                if (p_19974_.equals(Vec3.ZERO)) {
                    return;
                }
            }

            ProfilerFiller profilerfiller = Profiler.get();
            profilerfiller.push("move");
            if (this.stuckSpeedMultiplier.lengthSqr() > 1.0E-7) {
                if (p_19973_ != MoverType.PISTON) {
                    p_19974_ = p_19974_.multiply(this.stuckSpeedMultiplier);
                }

                this.stuckSpeedMultiplier = Vec3.ZERO;
                this.setDeltaMovement(Vec3.ZERO);
            }

            p_19974_ = this.maybeBackOffFromEdge(p_19974_, p_19973_);
            Vec3 vec3 = this.collide(p_19974_);
            double d0 = vec3.lengthSqr();
            if (d0 > 1.0E-7 || p_19974_.lengthSqr() - d0 < 1.0E-7) {
                if (this.fallDistance != 0.0 && d0 >= 1.0) {
                    double d1 = Math.min(vec3.length(), 8.0);
                    Vec3 vec32 = this.position().add(vec3.normalize().scale(d1));
                    BlockHitResult blockhitresult = this.level()
                        .clip(new ClipContext(this.position(), vec32, ClipContext.Block.FALLDAMAGE_RESETTING, ClipContext.Fluid.WATER, this));
                    if (blockhitresult.getType() != HitResult.Type.MISS) {
                        this.resetFallDistance();
                    }
                }

                Vec3 vec34 = this.position();
                Vec3 vec31 = vec34.add(vec3);
                this.addMovementThisTick(new Entity.Movement(vec34, vec31, p_19974_));
                this.setPos(vec31);
            }

            profilerfiller.pop();
            profilerfiller.push("rest");
            boolean flag = !Mth.equal(p_19974_.x, vec3.x);
            boolean flag1 = !Mth.equal(p_19974_.z, vec3.z);
            this.horizontalCollision = flag || flag1;
            if (Math.abs(p_19974_.y) > 0.0 || this.isLocalInstanceAuthoritative()) {
                this.verticalCollision = p_19974_.y != vec3.y;
                this.verticalCollisionBelow = this.verticalCollision && p_19974_.y < 0.0;
                this.setOnGroundWithMovement(this.verticalCollisionBelow, this.horizontalCollision, vec3);
            }

            if (this.horizontalCollision) {
                this.minorHorizontalCollision = this.isHorizontalCollisionMinor(vec3);
            } else {
                this.minorHorizontalCollision = false;
            }

            BlockPos blockpos = this.getOnPosLegacy();
            BlockState blockstate = this.level().getBlockState(blockpos);
            if (this.isLocalInstanceAuthoritative()) {
                this.checkFallDamage(vec3.y, this.onGround(), blockstate, blockpos);
            }

            if (this.isRemoved()) {
                profilerfiller.pop();
            } else {
                if (this.horizontalCollision) {
                    Vec3 vec33 = this.getDeltaMovement();
                    this.setDeltaMovement(flag ? 0.0 : vec33.x, vec33.y, flag1 ? 0.0 : vec33.z);
                }

                if (this.canSimulateMovement()) {
                    Block block = blockstate.getBlock();
                    if (p_19974_.y != vec3.y) {
                        block.updateEntityMovementAfterFallOn(this.level(), this);
                    }
                }

                if (!this.level().isClientSide() || this.isLocalInstanceAuthoritative()) {
                    Entity.MovementEmission entity$movementemission = this.getMovementEmission();
                    if (entity$movementemission.emitsAnything() && !this.isPassenger()) {
                        this.applyMovementEmissionAndPlaySound(entity$movementemission, vec3, blockpos, blockstate);
                    }
                }

                float f = this.getBlockSpeedFactor();
                this.setDeltaMovement(this.getDeltaMovement().multiply(f, 1.0, f));
                profilerfiller.pop();
            }
        }
    }

    private void applyMovementEmissionAndPlaySound(Entity.MovementEmission p_365086_, Vec3 p_365141_, BlockPos p_365493_, BlockState p_365295_) {
        float f = 0.6F;
        float f1 = (float)(p_365141_.length() * 0.6F);
        float f2 = (float)(p_365141_.horizontalDistance() * 0.6F);
        BlockPos blockpos = this.getOnPos();
        BlockState blockstate = this.level().getBlockState(blockpos);
        boolean flag = this.isStateClimbable(blockstate);
        this.moveDist += flag ? f1 : f2;
        this.flyDist += f1;
        if (this.moveDist > this.nextStep && !blockstate.isAir()) {
            boolean flag1 = blockpos.equals(p_365493_);
            boolean flag2 = this.vibrationAndSoundEffectsFromBlock(p_365493_, p_365295_, p_365086_.emitsSounds(), flag1, p_365141_);
            if (!flag1) {
                flag2 |= this.vibrationAndSoundEffectsFromBlock(blockpos, blockstate, false, p_365086_.emitsEvents(), p_365141_);
            }

            if (flag2) {
                this.nextStep = this.nextStep();
            } else if (this.isInWater()) {
                this.nextStep = this.nextStep();
                if (p_365086_.emitsSounds()) {
                    this.waterSwimSound();
                }

                if (p_365086_.emitsEvents()) {
                    this.gameEvent(GameEvent.SWIM);
                }
            }
        } else if (blockstate.isAir()) {
            this.processFlappingMovement();
        }
    }

    protected void applyEffectsFromBlocks() {
        this.finalMovementsThisTick.clear();
        this.finalMovementsThisTick.addAll(this.movementThisTick);
        this.movementThisTick.clear();
        if (this.finalMovementsThisTick.isEmpty()) {
            this.finalMovementsThisTick.add(new Entity.Movement(this.oldPosition(), this.position()));
        } else if (this.finalMovementsThisTick.getLast().to.distanceToSqr(this.position()) > 9.9999994E-11F) {
            this.finalMovementsThisTick.add(new Entity.Movement(this.finalMovementsThisTick.getLast().to, this.position()));
        }

        this.applyEffectsFromBlocks(this.finalMovementsThisTick);
    }

    private void addMovementThisTick(Entity.Movement p_428474_) {
        if (this.movementThisTick.size() >= 100) {
            Entity.Movement entity$movement = this.movementThisTick.removeFirst();
            Entity.Movement entity$movement1 = this.movementThisTick.removeFirst();
            Entity.Movement entity$movement2 = new Entity.Movement(entity$movement.from(), entity$movement1.to());
            this.movementThisTick.addFirst(entity$movement2);
        }

        this.movementThisTick.add(p_428474_);
    }

    public void removeLatestMovementRecording() {
        if (!this.movementThisTick.isEmpty()) {
            this.movementThisTick.removeLast();
        }
    }

    protected void clearMovementThisTick() {
        this.movementThisTick.clear();
    }

    public boolean hasMovedHorizontallyRecently() {
        return Math.abs(this.lastKnownSpeed.horizontalDistance()) > 1.0E-5F;
    }

    public void applyEffectsFromBlocks(Vec3 p_372978_, Vec3 p_372858_) {
        this.applyEffectsFromBlocks(List.of(new Entity.Movement(p_372978_, p_372858_)));
    }

    private void applyEffectsFromBlocks(List<Entity.Movement> p_410126_) {
        if (this.isAffectedByBlocks()) {
            if (this.onGround()) {
                BlockPos blockpos = this.getOnPosLegacy();
                BlockState blockstate = this.level().getBlockState(blockpos);
                blockstate.getBlock().stepOn(this.level(), blockpos, blockstate, this);
            }

            boolean flag1 = this.isOnFire();
            boolean flag2 = this.isFreezing();
            int i = this.getRemainingFireTicks();
            this.checkInsideBlocks(p_410126_, this.insideEffectCollector);
            this.insideEffectCollector.applyAndClear(this);
            if (this.isInRain()) {
                this.clearFire();
            }

            if (flag1 && !this.isOnFire() || flag2 && !this.isFreezing()) {
                this.playEntityOnFireExtinguishedSound();
            }

            boolean flag = this.getRemainingFireTicks() > i;
            if (!this.level().isClientSide() && !this.isOnFire() && !flag) {
                this.setRemainingFireTicks(-this.getFireImmuneTicks());
            }
        }
    }

    protected boolean isAffectedByBlocks() {
        return !this.isRemoved() && !this.noPhysics;
    }

    private boolean isStateClimbable(BlockState p_286733_) {
        return p_286733_.is(BlockTags.CLIMBABLE) || p_286733_.is(Blocks.POWDER_SNOW);
    }

    private boolean vibrationAndSoundEffectsFromBlock(BlockPos p_286221_, BlockState p_286549_, boolean p_286708_, boolean p_286543_, Vec3 p_286448_) {
        if (p_286549_.isAir()) {
            return false;
        } else {
            boolean flag = this.isStateClimbable(p_286549_);
            if ((this.onGround() || flag || this.isCrouching() && p_286448_.y == 0.0 || this.isOnRails()) && !this.isSwimming()) {
                if (p_286708_) {
                    this.walkingStepSound(p_286221_, p_286549_);
                }

                if (p_286543_) {
                    this.level().gameEvent(GameEvent.STEP, this.position(), GameEvent.Context.of(this, p_286549_));
                }

                return true;
            } else {
                return false;
            }
        }
    }

    protected boolean isHorizontalCollisionMinor(Vec3 p_196625_) {
        return false;
    }

    protected void playEntityOnFireExtinguishedSound() {
        if (!this.level.isClientSide()) {
            this.level()
                .playSound(
                    null,
                    this.getX(),
                    this.getY(),
                    this.getZ(),
                    SoundEvents.GENERIC_EXTINGUISH_FIRE,
                    this.getSoundSource(),
                    0.7F,
                    1.6F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F
                );
        }
    }

    public void extinguishFire() {
        if (this.isOnFire()) {
            this.playEntityOnFireExtinguishedSound();
        }

        this.clearFire();
    }

    protected void processFlappingMovement() {
        if (this.isFlapping()) {
            this.onFlap();
            if (this.getMovementEmission().emitsEvents()) {
                this.gameEvent(GameEvent.FLAP);
            }
        }
    }

    @Deprecated
    public BlockPos getOnPosLegacy() {
        return this.getOnPos(0.2F);
    }

    public BlockPos getBlockPosBelowThatAffectsMyMovement() {
        return this.getOnPos(0.500001F);
    }

    public BlockPos getOnPos() {
        return this.getOnPos(1.0E-5F);
    }

    protected BlockPos getOnPos(float p_216987_) {
        if (this.mainSupportingBlockPos.isPresent()) {
            BlockPos blockpos = this.mainSupportingBlockPos.get();
            if (!(p_216987_ > 1.0E-5F)) {
                return blockpos;
            } else {
                BlockState blockstate = this.level().getBlockState(blockpos);
                return (!(p_216987_ <= 0.5) || !blockstate.collisionExtendsVertically(this.level(), blockpos, this))
                    ? blockpos.atY(Mth.floor(this.position.y - p_216987_))
                    : blockpos;
            }
        } else {
            int i = Mth.floor(this.position.x);
            int j = Mth.floor(this.position.y - p_216987_);
            int k = Mth.floor(this.position.z);
            return new BlockPos(i, j, k);
        }
    }

    protected float getBlockJumpFactor() {
        float f = this.level().getBlockState(this.blockPosition()).getBlock().getJumpFactor();
        float f1 = this.level().getBlockState(this.getBlockPosBelowThatAffectsMyMovement()).getBlock().getJumpFactor();
        return f == 1.0 ? f1 : f;
    }

    protected float getBlockSpeedFactor() {
        BlockState blockstate = this.level().getBlockState(this.blockPosition());
        float f = blockstate.getBlock().getSpeedFactor();
        if (!blockstate.is(Blocks.WATER) && !blockstate.is(Blocks.BUBBLE_COLUMN)) {
            return f == 1.0 ? this.level().getBlockState(this.getBlockPosBelowThatAffectsMyMovement()).getBlock().getSpeedFactor() : f;
        } else {
            return f;
        }
    }

    protected Vec3 maybeBackOffFromEdge(Vec3 p_20019_, MoverType p_20020_) {
        return p_20019_;
    }

    protected Vec3 limitPistonMovement(Vec3 p_20134_) {
        if (p_20134_.lengthSqr() <= 1.0E-7) {
            return p_20134_;
        } else {
            long i = this.level().getGameTime();
            if (i != this.pistonDeltasGameTime) {
                Arrays.fill(this.pistonDeltas, 0.0);
                this.pistonDeltasGameTime = i;
            }

            if (p_20134_.x != 0.0) {
                double d2 = this.applyPistonMovementRestriction(Direction.Axis.X, p_20134_.x);
                return Math.abs(d2) <= 1.0E-5F ? Vec3.ZERO : new Vec3(d2, 0.0, 0.0);
            } else if (p_20134_.y != 0.0) {
                double d1 = this.applyPistonMovementRestriction(Direction.Axis.Y, p_20134_.y);
                return Math.abs(d1) <= 1.0E-5F ? Vec3.ZERO : new Vec3(0.0, d1, 0.0);
            } else if (p_20134_.z != 0.0) {
                double d0 = this.applyPistonMovementRestriction(Direction.Axis.Z, p_20134_.z);
                return Math.abs(d0) <= 1.0E-5F ? Vec3.ZERO : new Vec3(0.0, 0.0, d0);
            } else {
                return Vec3.ZERO;
            }
        }
    }

    private double applyPistonMovementRestriction(Direction.Axis p_20043_, double p_20044_) {
        int i = p_20043_.ordinal();
        double d0 = Mth.clamp(p_20044_ + this.pistonDeltas[i], -0.51, 0.51);
        p_20044_ = d0 - this.pistonDeltas[i];
        this.pistonDeltas[i] = d0;
        return p_20044_;
    }

    public double getAvailableSpaceBelow(double p_449381_) {
        AABB aabb = this.getBoundingBox();
        AABB aabb1 = aabb.setMinY(aabb.minY - p_449381_).setMaxY(aabb.minY);
        List<VoxelShape> list = collectAllColliders(this, this.level, aabb1);
        return list.isEmpty() ? p_449381_ : -Shapes.collide(Direction.Axis.Y, aabb, list, -p_449381_);
    }

    private Vec3 collide(Vec3 p_20273_) {
        AABB aabb = this.getBoundingBox();
        List<VoxelShape> list = this.level().getEntityCollisions(this, aabb.expandTowards(p_20273_));
        Vec3 vec3 = p_20273_.lengthSqr() == 0.0 ? p_20273_ : collideBoundingBox(this, p_20273_, aabb, this.level(), list);
        boolean flag = p_20273_.x != vec3.x;
        boolean flag1 = p_20273_.y != vec3.y;
        boolean flag2 = p_20273_.z != vec3.z;
        boolean flag3 = flag1 && p_20273_.y < 0.0;
        if (this.maxUpStep() > 0.0F && (flag3 || this.onGround()) && (flag || flag2)) {
            AABB aabb1 = flag3 ? aabb.move(0.0, vec3.y, 0.0) : aabb;
            AABB aabb2 = aabb1.expandTowards(p_20273_.x, this.maxUpStep(), p_20273_.z);
            if (!flag3) {
                aabb2 = aabb2.expandTowards(0.0, -1.0E-5F, 0.0);
            }

            List<VoxelShape> list1 = collectColliders(this, this.level, list, aabb2);
            float f = (float)vec3.y;
            float[] afloat = collectCandidateStepUpHeights(aabb1, list1, this.maxUpStep(), f);

            for (float f1 : afloat) {
                Vec3 vec31 = collideWithShapes(new Vec3(p_20273_.x, f1, p_20273_.z), aabb1, list1);
                if (vec31.horizontalDistanceSqr() > vec3.horizontalDistanceSqr()) {
                    double d0 = aabb.minY - aabb1.minY;
                    return vec31.subtract(0.0, d0, 0.0);
                }
            }
        }

        return vec3;
    }

    private static float[] collectCandidateStepUpHeights(AABB p_345729_, List<VoxelShape> p_345260_, float p_346165_, float p_345968_) {
        FloatSet floatset = new FloatArraySet(4);

        for (VoxelShape voxelshape : p_345260_) {
            for (double d0 : voxelshape.getCoords(Direction.Axis.Y)) {
                float f = (float)(d0 - p_345729_.minY);
                if (!(f < 0.0F) && f != p_345968_) {
                    if (f > p_346165_) {
                        break;
                    }

                    floatset.add(f);
                }
            }
        }

        float[] afloat = floatset.toFloatArray();
        FloatArrays.unstableSort(afloat);
        return afloat;
    }

    public static Vec3 collideBoundingBox(@Nullable Entity p_198895_, Vec3 p_198896_, AABB p_198897_, Level p_198898_, List<VoxelShape> p_198899_) {
        List<VoxelShape> list = collectColliders(p_198895_, p_198898_, p_198899_, p_198897_.expandTowards(p_198896_));
        return collideWithShapes(p_198896_, p_198897_, list);
    }

    public static List<VoxelShape> collectAllColliders(@Nullable Entity p_449211_, Level p_449969_, AABB p_449715_) {
        List<VoxelShape> list = p_449969_.getEntityCollisions(p_449211_, p_449715_);
        return collectColliders(p_449211_, p_449969_, list, p_449715_);
    }

    private static List<VoxelShape> collectColliders(@Nullable Entity p_344804_, Level p_345583_, List<VoxelShape> p_345198_, AABB p_345837_) {
        Builder<VoxelShape> builder = ImmutableList.builderWithExpectedSize(p_345198_.size() + 1);
        if (!p_345198_.isEmpty()) {
            builder.addAll(p_345198_);
        }

        WorldBorder worldborder = p_345583_.getWorldBorder();
        boolean flag = p_344804_ != null && worldborder.isInsideCloseToBorder(p_344804_, p_345837_);
        if (flag) {
            builder.add(worldborder.getCollisionShape());
        }

        builder.addAll(p_345583_.getBlockCollisions(p_344804_, p_345837_));
        return builder.build();
    }

    private static Vec3 collideWithShapes(Vec3 p_198901_, AABB p_198902_, List<VoxelShape> p_198903_) {
        if (p_198903_.isEmpty()) {
            return p_198901_;
        } else {
            Vec3 vec3 = Vec3.ZERO;

            for (Direction.Axis direction$axis : Direction.axisStepOrder(p_198901_)) {
                double d0 = p_198901_.get(direction$axis);
                if (d0 != 0.0) {
                    double d1 = Shapes.collide(direction$axis, p_198902_.move(vec3), p_198903_, d0);
                    vec3 = vec3.with(direction$axis, d1);
                }
            }

            return vec3;
        }
    }

    protected float nextStep() {
        return (int)this.moveDist + 1;
    }

    protected SoundEvent getSwimSound() {
        return SoundEvents.GENERIC_SWIM;
    }

    protected SoundEvent getSwimSplashSound() {
        return SoundEvents.GENERIC_SPLASH;
    }

    protected SoundEvent getSwimHighSpeedSplashSound() {
        return SoundEvents.GENERIC_SPLASH;
    }

    private void checkInsideBlocks(List<Entity.Movement> p_381632_, InsideBlockEffectApplier.StepBasedCollector p_405233_) {
        if (this.isAffectedByBlocks()) {
            LongSet longset = this.visitedBlocks;

            for (Entity.Movement entity$movement : p_381632_) {
                Vec3 vec3 = entity$movement.from;
                Vec3 vec31 = entity$movement.to().subtract(entity$movement.from());
                int i = 16;
                if (entity$movement.axisDependentOriginalMovement().isPresent() && vec31.lengthSqr() > 0.0) {
                    for (Direction.Axis direction$axis : Direction.axisStepOrder(entity$movement.axisDependentOriginalMovement().get())) {
                        double d0 = vec31.get(direction$axis);
                        if (d0 != 0.0) {
                            Vec3 vec32 = vec3.relative(direction$axis.getPositive(), d0);
                            i -= this.checkInsideBlocks(vec3, vec32, p_405233_, longset, i);
                            vec3 = vec32;
                        }
                    }
                } else {
                    i -= this.checkInsideBlocks(entity$movement.from(), entity$movement.to(), p_405233_, longset, 16);
                }

                if (i <= 0) {
                    this.checkInsideBlocks(entity$movement.to(), entity$movement.to(), p_405233_, longset, 1);
                }
            }

            longset.clear();
        }
    }

    private int checkInsideBlocks(Vec3 p_428555_, Vec3 p_428532_, InsideBlockEffectApplier.StepBasedCollector p_428368_, LongSet p_428369_, int p_436707_) {
        AABB aabb = this.makeBoundingBox(p_428532_).deflate(1.0E-5F);
        boolean flag = p_428555_.distanceToSqr(p_428532_) > Mth.square(0.9999900000002526);
        boolean flag1 = this.level instanceof ServerLevel serverlevel
            && serverlevel.getServer().debugSubscribers().hasAnySubscriberFor(DebugSubscriptions.ENTITY_BLOCK_INTERSECTIONS);
        AtomicInteger atomicinteger = new AtomicInteger();
        BlockGetter.forEachBlockIntersectedBetween(
            p_428555_,
            p_428532_,
            aabb,
            (p_451749_, p_451750_) -> {
                if (!this.isAlive()) {
                    return false;
                } else if (p_451750_ >= p_436707_) {
                    return false;
                } else {
                    atomicinteger.set(p_451750_);
                    BlockState blockstate = this.level().getBlockState(p_451749_);
                    if (blockstate.isAir()) {
                        if (flag1) {
                            this.debugBlockIntersection((ServerLevel)this.level(), p_451749_.immutable(), false, false);
                        }

                        return true;
                    } else {
                        VoxelShape voxelshape = blockstate.getEntityInsideCollisionShape(this.level(), p_451749_, this);
                        boolean flag2 = voxelshape == Shapes.block()
                            || this.collidedWithShapeMovingFrom(p_428555_, p_428532_, voxelshape.move(new Vec3(p_451749_)).toAabbs());
                        boolean flag3 = this.collidedWithFluid(blockstate.getFluidState(), p_451749_, p_428555_, p_428532_);
                        if ((flag2 || flag3) && p_428369_.add(p_451749_.asLong())) {
                            if (flag2) {
                                try {
                                    boolean flag4 = flag || aabb.intersects(p_451749_);
                                    p_428368_.advanceStep(p_451750_);
                                    blockstate.entityInside(this.level(), p_451749_, this, p_428368_, flag4);
                                    this.onInsideBlock(blockstate);
                                } catch (Throwable throwable) {
                                    CrashReport crashreport = CrashReport.forThrowable(throwable, "Colliding entity with block");
                                    CrashReportCategory crashreportcategory = crashreport.addCategory("Block being collided with");
                                    CrashReportCategory.populateBlockDetails(crashreportcategory, this.level(), p_451749_, blockstate);
                                    CrashReportCategory crashreportcategory1 = crashreport.addCategory("Entity being checked for collision");
                                    this.fillCrashReportCategory(crashreportcategory1);
                                    throw new ReportedException(crashreport);
                                }
                            }

                            if (flag3) {
                                p_428368_.advanceStep(p_451750_);
                                blockstate.getFluidState().entityInside(this.level(), p_451749_, this, p_428368_);
                            }

                            if (flag1) {
                                this.debugBlockIntersection((ServerLevel)this.level(), p_451749_.immutable(), flag2, flag3);
                            }

                            return true;
                        } else {
                            return true;
                        }
                    }
                }
            }
        );
        return atomicinteger.get() + 1;
    }

    private void debugBlockIntersection(ServerLevel p_449638_, BlockPos p_449782_, boolean p_449501_, boolean p_449292_) {
        DebugEntityBlockIntersection debugentityblockintersection;
        if (p_449292_) {
            debugentityblockintersection = DebugEntityBlockIntersection.IN_FLUID;
        } else if (p_449501_) {
            debugentityblockintersection = DebugEntityBlockIntersection.IN_BLOCK;
        } else {
            debugentityblockintersection = DebugEntityBlockIntersection.IN_AIR;
        }

        p_449638_.debugSynchronizers().sendBlockValue(p_449782_, DebugSubscriptions.ENTITY_BLOCK_INTERSECTIONS, debugentityblockintersection);
    }

    public boolean collidedWithFluid(FluidState p_398005_, BlockPos p_397890_, Vec3 p_397088_, Vec3 p_397336_) {
        AABB aabb = p_398005_.getAABB(this.level(), p_397890_);
        return aabb != null && this.collidedWithShapeMovingFrom(p_397088_, p_397336_, List.of(aabb));
    }

    public boolean collidedWithShapeMovingFrom(Vec3 p_371201_, Vec3 p_372821_, List<AABB> p_397872_) {
        AABB aabb = this.makeBoundingBox(p_371201_);
        Vec3 vec3 = p_372821_.subtract(p_371201_);
        return aabb.collidedAlongVector(vec3, p_397872_);
    }

    protected void onInsideBlock(BlockState p_20005_) {
    }

    public BlockPos adjustSpawnLocation(ServerLevel p_352356_, BlockPos p_352343_) {
        BlockPos blockpos = p_352356_.getRespawnData().pos();
        Vec3 vec3 = blockpos.getCenter();
        int i = p_352356_.getChunkAt(blockpos).getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockpos.getX(), blockpos.getZ()) + 1;
        return BlockPos.containing(vec3.x, i, vec3.z);
    }

    public void gameEvent(Holder<GameEvent> p_316133_, @Nullable Entity p_146854_) {
        this.level().gameEvent(p_146854_, p_316133_, this.position);
    }

    public void gameEvent(Holder<GameEvent> p_316256_) {
        this.gameEvent(p_316256_, this);
    }

    private void walkingStepSound(BlockPos p_281828_, BlockState p_282118_) {
        this.playStepSound(p_281828_, p_282118_);
        if (this.shouldPlayAmethystStepSound(p_282118_)) {
            this.playAmethystStepSound();
        }
    }

    protected void waterSwimSound() {
        Entity entity = Objects.requireNonNullElse(this.getControllingPassenger(), this);
        float f = entity == this ? 0.35F : 0.4F;
        Vec3 vec3 = entity.getDeltaMovement();
        float f1 = Math.min(1.0F, (float)Math.sqrt(vec3.x * vec3.x * 0.2F + vec3.y * vec3.y + vec3.z * vec3.z * 0.2F) * f);
        this.playSwimSound(f1);
    }

    protected BlockPos getPrimaryStepSoundBlockPos(BlockPos p_278049_) {
        BlockPos blockpos = p_278049_.above();
        BlockState blockstate = this.level().getBlockState(blockpos);
        return !blockstate.is(BlockTags.INSIDE_STEP_SOUND_BLOCKS) && !blockstate.is(BlockTags.COMBINATION_STEP_SOUND_BLOCKS) ? p_278049_ : blockpos;
    }

    protected void playCombinationStepSounds(BlockState p_277472_, BlockState p_277630_, BlockPos primaryPos, BlockPos secondaryPos) {
        // Neo: Allow the blockstate full control over step sounds
        p_277472_.playStepSound(this.level, primaryPos, this, .15F, 1F);
        this.playMuffledStepSound(p_277630_, secondaryPos);
    }

    protected void playMuffledStepSound(BlockState p_283110_, BlockPos pos) {
        // Neo: Allow the blockstate full control over step sounds
        p_283110_.playStepSound(this.level, pos, this, .05F, .8F);
    }

    protected void playStepSound(BlockPos p_20135_, BlockState p_20136_) {
        // Neo: Allow the blockstate full control over step sounds
        p_20136_.playStepSound(this.level(), p_20135_, this, .15F, 1F);
    }

    private boolean shouldPlayAmethystStepSound(BlockState p_278069_) {
        return p_278069_.is(BlockTags.CRYSTAL_SOUND_BLOCKS) && this.tickCount >= this.lastCrystalSoundPlayTick + 20;
    }

    private void playAmethystStepSound() {
        this.crystalSoundIntensity = this.crystalSoundIntensity * (float)Math.pow(0.997, this.tickCount - this.lastCrystalSoundPlayTick);
        this.crystalSoundIntensity = Math.min(1.0F, this.crystalSoundIntensity + 0.07F);
        float f = 0.5F + this.crystalSoundIntensity * this.random.nextFloat() * 1.2F;
        float f1 = 0.1F + this.crystalSoundIntensity * 1.2F;
        this.playSound(SoundEvents.AMETHYST_BLOCK_CHIME, f1, f);
        this.lastCrystalSoundPlayTick = this.tickCount;
    }

    protected void playSwimSound(float p_20213_) {
        this.playSound(this.getSwimSound(), p_20213_, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
    }

    protected void onFlap() {
    }

    protected boolean isFlapping() {
        return false;
    }

    public void playSound(SoundEvent p_19938_, float p_19939_, float p_19940_) {
        if (!this.isSilent()) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), p_19938_, this.getSoundSource(), p_19939_, p_19940_);
        }
    }

    public void playSound(SoundEvent p_216991_) {
        if (!this.isSilent()) {
            this.playSound(p_216991_, 1.0F, 1.0F);
        }
    }

    public boolean isSilent() {
        return this.entityData.get(DATA_SILENT);
    }

    public void setSilent(boolean p_20226_) {
        this.entityData.set(DATA_SILENT, p_20226_);
    }

    public boolean isNoGravity() {
        return this.entityData.get(DATA_NO_GRAVITY);
    }

    public void setNoGravity(boolean p_20243_) {
        this.entityData.set(DATA_NO_GRAVITY, p_20243_);
    }

    protected double getDefaultGravity() {
        return 0.0;
    }

    public final double getGravity() {
        return this.isNoGravity() ? 0.0 : this.getDefaultGravity();
    }

    protected void applyGravity() {
        double d0 = this.getGravity();
        if (d0 != 0.0) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -d0, 0.0));
        }
    }

    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.ALL;
    }

    public boolean dampensVibrations() {
        return false;
    }

    public final void doCheckFallDamage(double p_390594_, double p_390598_, double p_390591_, boolean p_390600_) {
        if (!this.touchingUnloadedChunk()) {
            this.checkSupportingBlock(p_390600_, new Vec3(p_390594_, p_390598_, p_390591_));
            BlockPos blockpos = this.getOnPosLegacy();
            BlockState blockstate = this.level().getBlockState(blockpos);
            this.checkFallDamage(p_390598_, p_390600_, blockstate, blockpos);
        }
    }

    protected void checkFallDamage(double p_19911_, boolean p_19912_, BlockState p_19913_, BlockPos p_19914_) {
        if (!this.isInWater() && p_19911_ < 0.0) {
            this.fallDistance -= (float)p_19911_;
        }

        if (p_19912_) {
            if (this.fallDistance > 0.0) {
                p_19913_.getBlock().fallOn(this.level(), p_19913_, p_19914_, this, this.fallDistance);
                this.level()
                    .gameEvent(
                        GameEvent.HIT_GROUND,
                        this.position,
                        GameEvent.Context.of(
                            this, this.mainSupportingBlockPos.<BlockState>map(p_286200_ -> this.level().getBlockState(p_286200_)).orElse(p_19913_)
                        )
                    );
            }

            this.resetFallDistance();
        }
    }

    public boolean fireImmune() {
        return this.getType().fireImmune();
    }

    public boolean causeFallDamage(double p_397956_, float p_146828_, DamageSource p_146830_) {
        if (this.type.is(EntityTypeTags.FALL_DAMAGE_IMMUNE)) {
            return false;
        } else {
            this.propagateFallToPassengers(p_397956_, p_146828_, p_146830_);
            return false;
        }
    }

    protected void propagateFallToPassengers(double p_401264_, float p_401278_, DamageSource p_401066_) {
        if (this.isVehicle()) {
            for (Entity entity : this.getPassengers()) {
                entity.causeFallDamage(p_401264_, p_401278_, p_401066_);
            }
        }
    }

    public boolean isInWater() {
        return this.wasTouchingWater;
    }

    boolean isInRain() {
        BlockPos blockpos = this.blockPosition();
        return this.level().isRainingAt(blockpos)
            || this.level().isRainingAt(BlockPos.containing(blockpos.getX(), this.getBoundingBox().maxY, blockpos.getZ()));
    }

    public boolean isInWaterOrRain() {
        return this.isInWater() || this.isInRain();
    }

    public boolean isInLiquid() {
        return this.isInWater() || this.isInLava();
    }

    public boolean isUnderWater() {
        return this.wasEyeInWater && this.isInWater();
    }

    public boolean isInShallowWater() {
        return this.isInWater() && !this.isUnderWater() || (this.isInFluidType((fluidType, height) -> this.canSwimInFluidType(fluidType)) && !this.canStartSwimming());
    }

    public boolean isInClouds() {
        if (ARGB.alpha(this.level.environmentAttributes().getValue(EnvironmentAttributes.CLOUD_COLOR, this.position())) == 0) {
            return false;
        } else {
            float f = this.level.environmentAttributes().getValue(EnvironmentAttributes.CLOUD_HEIGHT, this.position());
            if (this.getY() + this.getBbHeight() < f) {
                return false;
            } else {
                float f1 = f + 4.0F;
                return this.getY() <= f1;
            }
        }
    }

    public void updateSwimming() {
        if (this.isSwimming()) {
            this.setSwimming(this.isSprinting() && (this.isInWater() || this.isInFluidType((fluidType, height) -> this.canSwimInFluidType(fluidType))) && !this.isPassenger());
        } else {
            this.setSwimming(
                this.isSprinting() && this.canStartSwimming() && !this.isPassenger()
            );
        }
    }

    protected boolean updateInWaterStateAndDoFluidPushing() {
        this.fluidHeight.clear();
        this.forgeFluidTypeHeight.clear();
        this.updateInWaterStateAndDoWaterCurrentPushing();
        if (this.isInFluidType() && !(this.getVehicle() instanceof AbstractBoat)) {
            this.fallDistance *= this.forgeFluidTypeHeight.object2DoubleEntrySet().stream().filter(e -> !e.getKey().isAir() && !e.getKey().isVanilla()).map(e -> this.getFluidFallDistanceModifier(e.getKey())).min(Float::compare).orElse(1F);
            if (this.isInFluidType((fluidType, height) -> !fluidType.isAir() && !fluidType.isVanilla() && this.canFluidExtinguish(fluidType))) this.clearFire();
        }
        return this.isInFluidType();
    }

    @Deprecated // Forge: Use updateInWaterStateAndDoWaterCurrentPushing with bool param instead
    void updateInWaterStateAndDoWaterCurrentPushing() {
        this.updateInWaterStateAndDoWaterCurrentPushing(true);
    }

    void updateInWaterStateAndDoWaterCurrentPushing(boolean performFluidPushing) {
        if (this.getVehicle() instanceof AbstractBoat abstractboat && !abstractboat.isUnderWater()) {
            this.wasTouchingWater = false;
        } else if (this.updateFluidHeightAndDoCanPushEntityFluidPushing(performFluidPushing)) {
            if (!this.wasTouchingWater && !this.firstTick) {
                this.doWaterSplashEffect();
            }

            this.resetFallDistance();
            this.wasTouchingWater = true;
        } else {
            this.wasTouchingWater = false;
        }
    }

    private void updateFluidOnEyes() {
        this.wasEyeInWater = this.isEyeInFluid(FluidTags.WATER);
        this.fluidOnEyes.clear();
        this.forgeFluidTypeOnEyes = net.neoforged.neoforge.common.NeoForgeMod.EMPTY_TYPE.value();
        double d0 = this.getEyeY();
        if (!(
            this.getVehicle() instanceof AbstractBoat abstractboat
                && !abstractboat.isUnderWater()
                && abstractboat.getBoundingBox().maxY >= d0
                && abstractboat.getBoundingBox().minY <= d0
        )) {
            BlockPos blockpos = BlockPos.containing(this.getX(), d0, this.getZ());
            FluidState fluidstate = this.level().getFluidState(blockpos);
            double d1 = blockpos.getY() + fluidstate.getHeight(this.level(), blockpos);
            if (d1 > d0) {
                this.forgeFluidTypeOnEyes = fluidstate.getFluidType();
            }
        }
    }

    protected void doWaterSplashEffect() {
        Entity entity = Objects.requireNonNullElse(this.getControllingPassenger(), this);
        float f = entity == this ? 0.2F : 0.9F;
        Vec3 vec3 = entity.getDeltaMovement();
        float f1 = Math.min(1.0F, (float)Math.sqrt(vec3.x * vec3.x * 0.2F + vec3.y * vec3.y + vec3.z * vec3.z * 0.2F) * f);
        if (f1 < 0.25F) {
            this.playSound(this.getSwimSplashSound(), f1, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
        } else {
            this.playSound(this.getSwimHighSpeedSplashSound(), f1, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
        }

        float f2 = Mth.floor(this.getY());

        for (int i = 0; i < 1.0F + this.dimensions.width() * 20.0F; i++) {
            double d0 = (this.random.nextDouble() * 2.0 - 1.0) * this.dimensions.width();
            double d1 = (this.random.nextDouble() * 2.0 - 1.0) * this.dimensions.width();
            this.level()
                .addParticle(ParticleTypes.BUBBLE, this.getX() + d0, f2 + 1.0F, this.getZ() + d1, vec3.x, vec3.y - this.random.nextDouble() * 0.2F, vec3.z);
        }

        for (int j = 0; j < 1.0F + this.dimensions.width() * 20.0F; j++) {
            double d2 = (this.random.nextDouble() * 2.0 - 1.0) * this.dimensions.width();
            double d3 = (this.random.nextDouble() * 2.0 - 1.0) * this.dimensions.width();
            this.level().addParticle(ParticleTypes.SPLASH, this.getX() + d2, f2 + 1.0F, this.getZ() + d3, vec3.x, vec3.y, vec3.z);
        }

        this.gameEvent(GameEvent.SPLASH);
    }

    @Deprecated
    protected BlockState getBlockStateOnLegacy() {
        return this.level().getBlockState(this.getOnPosLegacy());
    }

    public BlockState getBlockStateOn() {
        return this.level().getBlockState(this.getOnPos());
    }

    public boolean canSpawnSprintParticle() {
        return this.isSprinting() && !this.isInWater() && !this.isSpectator() && !this.isCrouching() && !this.isInLava() && this.isAlive() && !this.isInFluidType();
    }

    protected void spawnSprintParticle() {
        BlockPos blockpos = this.getOnPosLegacy();
        BlockState blockstate = this.level().getBlockState(blockpos);
        if(!blockstate.addRunningEffects(level, blockpos, this))
        if (blockstate.getRenderShape() != RenderShape.INVISIBLE) {
            Vec3 vec3 = this.getDeltaMovement();
            BlockPos blockpos1 = this.blockPosition();
            double d0 = this.getX() + (this.random.nextDouble() - 0.5) * this.dimensions.width();
            double d1 = this.getZ() + (this.random.nextDouble() - 0.5) * this.dimensions.width();
            if (blockpos1.getX() != blockpos.getX()) {
                d0 = Mth.clamp(d0, (double)blockpos.getX(), blockpos.getX() + 1.0);
            }

            if (blockpos1.getZ() != blockpos.getZ()) {
                d1 = Mth.clamp(d1, (double)blockpos.getZ(), blockpos.getZ() + 1.0);
            }

            this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockstate, blockpos), d0, this.getY() + 0.1, d1, vec3.x * -4.0, 1.5, vec3.z * -4.0);
        }
    }

    @Deprecated // Forge: Use isEyeInFluidType instead
    public boolean isEyeInFluid(TagKey<Fluid> p_204030_) {
        if (p_204030_ == FluidTags.WATER) return this.isEyeInFluidType(net.neoforged.neoforge.common.NeoForgeMod.WATER_TYPE.value());
        else if (p_204030_ == FluidTags.LAVA) return this.isEyeInFluidType(net.neoforged.neoforge.common.NeoForgeMod.LAVA_TYPE.value());
        return this.fluidOnEyes.contains(p_204030_);
    }

    public boolean isInLava() {
        return !this.firstTick && this.forgeFluidTypeHeight.getDouble(net.neoforged.neoforge.common.NeoForgeMod.LAVA_TYPE.value()) > 0.0D;
    }

    public void moveRelative(float p_19921_, Vec3 p_19922_) {
        Vec3 vec3 = getInputVector(p_19922_, p_19921_, this.getYRot());
        this.setDeltaMovement(this.getDeltaMovement().add(vec3));
    }

    protected static Vec3 getInputVector(Vec3 p_20016_, float p_20017_, float p_20018_) {
        double d0 = p_20016_.lengthSqr();
        if (d0 < 1.0E-7) {
            return Vec3.ZERO;
        } else {
            Vec3 vec3 = (d0 > 1.0 ? p_20016_.normalize() : p_20016_).scale(p_20017_);
            float f = Mth.sin(p_20018_ * (float) (Math.PI / 180.0));
            float f1 = Mth.cos(p_20018_ * (float) (Math.PI / 180.0));
            return new Vec3(vec3.x * f1 - vec3.z * f, vec3.y, vec3.z * f1 + vec3.x * f);
        }
    }

    @Deprecated
    public float getLightLevelDependentMagicValue() {
        return this.level().hasChunkAt(this.getBlockX(), this.getBlockZ())
            ? this.level().getLightLevelDependentMagicValue(BlockPos.containing(this.getX(), this.getEyeY(), this.getZ()))
            : 0.0F;
    }

    public void absSnapTo(double p_397411_, double p_397695_, double p_397235_, float p_397420_, float p_397906_) {
        this.absSnapTo(p_397411_, p_397695_, p_397235_);
        this.absSnapRotationTo(p_397420_, p_397906_);
    }

    public void absSnapRotationTo(float p_348662_, float p_348500_) {
        this.setYRot(p_348662_ % 360.0F);
        this.setXRot(Mth.clamp(p_348500_, -90.0F, 90.0F) % 360.0F);
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }

    public void absSnapTo(double p_20249_, double p_20250_, double p_20251_) {
        double d0 = Mth.clamp(p_20249_, -3.0E7, 3.0E7);
        double d1 = Mth.clamp(p_20251_, -3.0E7, 3.0E7);
        this.xo = d0;
        this.yo = p_20250_;
        this.zo = d1;
        this.setPos(d0, p_20250_, d1);
    }

    public void snapTo(Vec3 p_397262_) {
        this.snapTo(p_397262_.x, p_397262_.y, p_397262_.z);
    }

    public void snapTo(double p_397080_, double p_398021_, double p_397836_) {
        this.snapTo(p_397080_, p_398021_, p_397836_, this.getYRot(), this.getXRot());
    }

    public void snapTo(BlockPos p_398044_, float p_397246_, float p_397582_) {
        this.snapTo(p_398044_.getBottomCenter(), p_397246_, p_397582_);
    }

    public void snapTo(Vec3 p_397138_, float p_397314_, float p_397145_) {
        this.snapTo(p_397138_.x, p_397138_.y, p_397138_.z, p_397314_, p_397145_);
    }

    public void snapTo(double p_20108_, double p_20109_, double p_20110_, float p_20111_, float p_20112_) {
        this.setPosRaw(p_20108_, p_20109_, p_20110_);
        this.setYRot(p_20111_);
        this.setXRot(p_20112_);
        this.setOldPosAndRot();
        this.reapplyPosition();
    }

    public final void setOldPosAndRot() {
        this.setOldPos();
        this.setOldRot();
    }

    public final void setOldPosAndRot(Vec3 p_371927_, float p_371686_, float p_371638_) {
        this.setOldPos(p_371927_);
        this.setOldRot(p_371686_, p_371638_);
    }

    protected void setOldPos() {
        this.setOldPos(this.position);
    }

    public void setOldRot() {
        this.setOldRot(this.getYRot(), this.getXRot());
    }

    private void setOldPos(Vec3 p_371773_) {
        this.xo = this.xOld = p_371773_.x;
        this.yo = this.yOld = p_371773_.y;
        this.zo = this.zOld = p_371773_.z;
    }

    private void setOldRot(float p_371749_, float p_371505_) {
        this.yRotO = p_371749_;
        this.xRotO = p_371505_;
    }

    public final Vec3 oldPosition() {
        return new Vec3(this.xOld, this.yOld, this.zOld);
    }

    public float distanceTo(Entity p_20271_) {
        float f = (float)(this.getX() - p_20271_.getX());
        float f1 = (float)(this.getY() - p_20271_.getY());
        float f2 = (float)(this.getZ() - p_20271_.getZ());
        return Mth.sqrt(f * f + f1 * f1 + f2 * f2);
    }

    public double distanceToSqr(double p_20276_, double p_20277_, double p_20278_) {
        double d0 = this.getX() - p_20276_;
        double d1 = this.getY() - p_20277_;
        double d2 = this.getZ() - p_20278_;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    public double distanceToSqr(Entity p_20281_) {
        return this.distanceToSqr(p_20281_.position());
    }

    public double distanceToSqr(Vec3 p_20239_) {
        double d0 = this.getX() - p_20239_.x;
        double d1 = this.getY() - p_20239_.y;
        double d2 = this.getZ() - p_20239_.z;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    public void playerTouch(Player p_20081_) {
    }

    public void push(Entity p_20293_) {
        if (!this.isPassengerOfSameVehicle(p_20293_)) {
            if (!p_20293_.noPhysics && !this.noPhysics) {
                double d0 = p_20293_.getX() - this.getX();
                double d1 = p_20293_.getZ() - this.getZ();
                double d2 = Mth.absMax(d0, d1);
                if (d2 >= 0.01F) {
                    d2 = Math.sqrt(d2);
                    d0 /= d2;
                    d1 /= d2;
                    double d3 = 1.0 / d2;
                    if (d3 > 1.0) {
                        d3 = 1.0;
                    }

                    d0 *= d3;
                    d1 *= d3;
                    d0 *= 0.05F;
                    d1 *= 0.05F;
                    if (!this.isVehicle() && this.isPushable()) {
                        this.push(-d0, 0.0, -d1);
                    }

                    if (!p_20293_.isVehicle() && p_20293_.isPushable()) {
                        p_20293_.push(d0, 0.0, d1);
                    }
                }
            }
        }
    }

    public void push(Vec3 p_347665_) {
        if (p_347665_.isFinite()) {
            this.push(p_347665_.x, p_347665_.y, p_347665_.z);
        }
    }

    public void push(double p_20286_, double p_20287_, double p_20288_) {
        if (Double.isFinite(p_20286_) && Double.isFinite(p_20287_) && Double.isFinite(p_20288_)) {
            this.setDeltaMovement(this.getDeltaMovement().add(p_20286_, p_20287_, p_20288_));
            this.needsSync = true;
        }
    }

    protected void markHurt() {
        this.hurtMarked = true;
    }

    @Deprecated
    public final void hurt(DamageSource p_19946_, float p_19947_) {
        if (this.level instanceof ServerLevel serverlevel) {
            this.hurtServer(serverlevel, p_19946_, p_19947_);
        }
    }

    @Deprecated
    public final boolean hurtOrSimulate(DamageSource p_376534_, float p_376514_) {
        return this.level instanceof ServerLevel serverlevel ? this.hurtServer(serverlevel, p_376534_, p_376514_) : this.hurtClient(p_376534_);
    }

    public abstract boolean hurtServer(ServerLevel p_376804_, DamageSource p_376155_, float p_376892_);

    public boolean hurtClient(DamageSource p_376938_) {
        return false;
    }

    public final Vec3 getViewVector(float p_20253_) {
        return this.calculateViewVector(this.getViewXRot(p_20253_), this.getViewYRot(p_20253_));
    }

    public Direction getNearestViewDirection() {
        return Direction.getApproximateNearest(this.getViewVector(1.0F));
    }

    public float getViewXRot(float p_20268_) {
        return this.getXRot(p_20268_);
    }

    public float getViewYRot(float p_20279_) {
        return this.getYRot(p_20279_);
    }

    public float getXRot(float p_363660_) {
        return p_363660_ == 1.0F ? this.getXRot() : Mth.lerp(p_363660_, this.xRotO, this.getXRot());
    }

    public float getYRot(float p_363186_) {
        return p_363186_ == 1.0F ? this.getYRot() : Mth.rotLerp(p_363186_, this.yRotO, this.getYRot());
    }

    public final Vec3 calculateViewVector(float p_20172_, float p_20173_) {
        float f = p_20172_ * (float) (Math.PI / 180.0);
        float f1 = -p_20173_ * (float) (Math.PI / 180.0);
        float f2 = Mth.cos(f1);
        float f3 = Mth.sin(f1);
        float f4 = Mth.cos(f);
        float f5 = Mth.sin(f);
        return new Vec3(f3 * f4, -f5, f2 * f4);
    }

    public final Vec3 getUpVector(float p_20290_) {
        return this.calculateUpVector(this.getViewXRot(p_20290_), this.getViewYRot(p_20290_));
    }

    protected final Vec3 calculateUpVector(float p_20215_, float p_20216_) {
        return this.calculateViewVector(p_20215_ - 90.0F, p_20216_);
    }

    public final Vec3 getEyePosition() {
        return new Vec3(this.getX(), this.getEyeY(), this.getZ());
    }

    public final Vec3 getEyePosition(float p_20300_) {
        double d0 = Mth.lerp((double)p_20300_, this.xo, this.getX());
        double d1 = Mth.lerp((double)p_20300_, this.yo, this.getY()) + this.getEyeHeight();
        double d2 = Mth.lerp((double)p_20300_, this.zo, this.getZ());
        return new Vec3(d0, d1, d2);
    }

    public Vec3 getLightProbePosition(float p_20309_) {
        return this.getEyePosition(p_20309_);
    }

    public final Vec3 getPosition(float p_20319_) {
        double d0 = Mth.lerp((double)p_20319_, this.xo, this.getX());
        double d1 = Mth.lerp((double)p_20319_, this.yo, this.getY());
        double d2 = Mth.lerp((double)p_20319_, this.zo, this.getZ());
        return new Vec3(d0, d1, d2);
    }

    public HitResult pick(double p_19908_, float p_19909_, boolean p_19910_) {
        Vec3 vec3 = this.getEyePosition(p_19909_);
        Vec3 vec31 = this.getViewVector(p_19909_);
        Vec3 vec32 = vec3.add(vec31.x * p_19908_, vec31.y * p_19908_, vec31.z * p_19908_);
        return this.level().clip(new ClipContext(vec3, vec32, ClipContext.Block.OUTLINE, p_19910_ ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE, this));
    }

    public boolean canBeHitByProjectile() {
        return this.isAlive() && this.isPickable();
    }

    public boolean isPickable() {
        return false;
    }

    public boolean isPushable() {
        return false;
    }

    public void awardKillScore(Entity p_19953_, DamageSource p_19955_) {
        if (p_19953_ instanceof ServerPlayer) {
            CriteriaTriggers.ENTITY_KILLED_PLAYER.trigger((ServerPlayer)p_19953_, this, p_19955_);
        }
    }

    public boolean shouldRender(double p_20296_, double p_20297_, double p_20298_) {
        double d0 = this.getX() - p_20296_;
        double d1 = this.getY() - p_20297_;
        double d2 = this.getZ() - p_20298_;
        double d3 = d0 * d0 + d1 * d1 + d2 * d2;
        return this.shouldRenderAtSqrDistance(d3);
    }

    public boolean shouldRenderAtSqrDistance(double p_19883_) {
        double d0 = this.getBoundingBox().getSize();
        if (Double.isNaN(d0)) {
            d0 = 1.0;
        }

        d0 *= 64.0 * viewScale;
        return p_19883_ < d0 * d0;
    }

    public boolean saveAsPassenger(ValueOutput p_421773_) {
        if (this.removalReason != null && !this.removalReason.shouldSave()) {
            return false;
        } else {
            String s = this.getEncodeId();
            if (s == null) {
                return false;
            } else {
                p_421773_.putString("id", s);
                this.saveWithoutId(p_421773_);
                return true;
            }
        }
    }

    public boolean save(ValueOutput p_422525_) {
        return this.isPassenger() ? false : this.saveAsPassenger(p_422525_);
    }

    public void saveWithoutId(ValueOutput p_422475_) {
        try {
            if (this.vehicle != null) {
                p_422475_.store("Pos", Vec3.CODEC, new Vec3(this.vehicle.getX(), this.getY(), this.vehicle.getZ()));
            } else {
                p_422475_.store("Pos", Vec3.CODEC, this.position());
            }

            p_422475_.store("Motion", Vec3.CODEC, this.getDeltaMovement());
            p_422475_.store("Rotation", Vec2.CODEC, new Vec2(this.getYRot(), this.getXRot()));
            p_422475_.putDouble("fall_distance", this.fallDistance);
            p_422475_.putShort("Fire", (short)this.remainingFireTicks);
            p_422475_.putShort("Air", (short)this.getAirSupply());
            p_422475_.putBoolean("OnGround", this.onGround());
            p_422475_.putBoolean("Invulnerable", this.invulnerable);
            p_422475_.putInt("PortalCooldown", this.portalCooldown);
            p_422475_.store("UUID", UUIDUtil.CODEC, this.getUUID());
            p_422475_.storeNullable("CustomName", ComponentSerialization.CODEC, this.getCustomName());
            if (this.isCustomNameVisible()) {
                p_422475_.putBoolean("CustomNameVisible", this.isCustomNameVisible());
            }

            if (this.isSilent()) {
                p_422475_.putBoolean("Silent", this.isSilent());
            }

            if (this.isNoGravity()) {
                p_422475_.putBoolean("NoGravity", this.isNoGravity());
            }

            if (this.hasGlowingTag) {
                p_422475_.putBoolean("Glowing", true);
            }

            int i = this.getTicksFrozen();
            if (i > 0) {
                p_422475_.putInt("TicksFrozen", this.getTicksFrozen());
            }

            if (this.hasVisualFire) {
                p_422475_.putBoolean("HasVisualFire", this.hasVisualFire);
            }

            if (!this.tags.isEmpty()) {
                p_422475_.store("Tags", TAG_LIST_CODEC, List.copyOf(this.tags));
            }

            if (!this.customData.isEmpty()) {
                p_422475_.store("data", CustomData.CODEC, this.customData);
            }

            var attachments = p_422475_.child(ATTACHMENTS_NBT_KEY);
            serializeAttachments(attachments);
            if (attachments.isEmpty()) p_422475_.discard(ATTACHMENTS_NBT_KEY);
            p_422475_.storeNullable("NeoForgeData", net.minecraft.nbt.CompoundTag.CODEC, persistentData);

            this.addAdditionalSaveData(p_422475_);
            if (this.isVehicle()) {
                ValueOutput.ValueOutputList valueoutput$valueoutputlist = p_422475_.childrenList("Passengers");

                for (Entity entity : this.getPassengers()) {
                    ValueOutput valueoutput = valueoutput$valueoutputlist.addChild();
                    if (!entity.saveAsPassenger(valueoutput)) {
                        valueoutput$valueoutputlist.discardLast();
                    }
                }

                if (valueoutput$valueoutputlist.isEmpty()) {
                    p_422475_.discard("Passengers");
                }
            }
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.forThrowable(throwable, "Saving entity NBT");
            CrashReportCategory crashreportcategory = crashreport.addCategory("Entity being saved");
            this.fillCrashReportCategory(crashreportcategory);
            throw new ReportedException(crashreport);
        }
    }

    public void load(ValueInput p_422206_) {
        try {
            Vec3 vec3 = p_422206_.read("Pos", Vec3.CODEC).orElse(Vec3.ZERO);
            Vec3 vec31 = p_422206_.read("Motion", Vec3.CODEC).orElse(Vec3.ZERO);
            Vec2 vec2 = p_422206_.read("Rotation", Vec2.CODEC).orElse(Vec2.ZERO);
            this.setDeltaMovement(Math.abs(vec31.x) > 10.0 ? 0.0 : vec31.x, Math.abs(vec31.y) > 10.0 ? 0.0 : vec31.y, Math.abs(vec31.z) > 10.0 ? 0.0 : vec31.z);
            this.needsSync = true;
            double d0 = 3.0000512E7;
            this.setPosRaw(Mth.clamp(vec3.x, -3.0000512E7, 3.0000512E7), Mth.clamp(vec3.y, -2.0E7, 2.0E7), Mth.clamp(vec3.z, -3.0000512E7, 3.0000512E7));
            this.setYRot(vec2.x);
            this.setXRot(vec2.y);
            this.setOldPosAndRot();
            this.setYHeadRot(this.getYRot());
            this.setYBodyRot(this.getYRot());
            this.fallDistance = p_422206_.getDoubleOr("fall_distance", 0.0);
            this.remainingFireTicks = p_422206_.getShortOr("Fire", (short)0);
            this.setAirSupply(p_422206_.getIntOr("Air", this.getMaxAirSupply()));
            this.onGround = p_422206_.getBooleanOr("OnGround", false);
            this.invulnerable = p_422206_.getBooleanOr("Invulnerable", false);
            this.portalCooldown = p_422206_.getIntOr("PortalCooldown", 0);
            p_422206_.read("UUID", UUIDUtil.CODEC).ifPresent(p_404265_ -> {
                this.uuid = p_404265_;
                this.stringUUID = this.uuid.toString();
            });
            if (!Double.isFinite(this.getX()) || !Double.isFinite(this.getY()) || !Double.isFinite(this.getZ())) {
                throw new IllegalStateException("Entity has invalid position");
            } else if (Double.isFinite(this.getYRot()) && Double.isFinite(this.getXRot())) {
                this.reapplyPosition();
                this.setRot(this.getYRot(), this.getXRot());
                this.setCustomName(p_422206_.read("CustomName", ComponentSerialization.CODEC).orElse(null));
                this.setCustomNameVisible(p_422206_.getBooleanOr("CustomNameVisible", false));
                this.setSilent(p_422206_.getBooleanOr("Silent", false));
                this.setNoGravity(p_422206_.getBooleanOr("NoGravity", false));
                this.setGlowingTag(p_422206_.getBooleanOr("Glowing", false));
                this.setTicksFrozen(p_422206_.getIntOr("TicksFrozen", 0));
                this.hasVisualFire = p_422206_.getBooleanOr("HasVisualFire", false);
                this.customData = p_422206_.read("data", CustomData.CODEC).orElse(CustomData.EMPTY);
                p_422206_.read("NeoForgeData", net.minecraft.nbt.CompoundTag.CODEC).ifPresent(neoData -> this.persistentData = neoData);
                p_422206_.child(ATTACHMENTS_NBT_KEY).ifPresent(attachments -> this.deserializeAttachments(attachments));
                this.tags.clear();
                p_422206_.read("Tags", TAG_LIST_CODEC).ifPresent(this.tags::addAll);
                this.readAdditionalSaveData(p_422206_);
                if (this.repositionEntityAfterLoad()) {
                    this.reapplyPosition();
                }
            } else {
                throw new IllegalStateException("Entity has invalid rotation");
            }
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.forThrowable(throwable, "Loading entity NBT");
            CrashReportCategory crashreportcategory = crashreport.addCategory("Entity being loaded");
            this.fillCrashReportCategory(crashreportcategory);
            throw new ReportedException(crashreport);
        }
    }

    protected boolean repositionEntityAfterLoad() {
        return true;
    }

    public final @Nullable String getEncodeId() {
        EntityType<?> entitytype = this.getType();
        Identifier identifier = EntityType.getKey(entitytype);
        return !entitytype.canSerialize() ? null : identifier.toString();
    }

    protected abstract void readAdditionalSaveData(ValueInput p_422333_);

    protected abstract void addAdditionalSaveData(ValueOutput p_421996_);

    public @Nullable ItemEntity spawnAtLocation(ServerLevel p_376558_, ItemLike p_376369_) {
        return this.spawnAtLocation(p_376558_, new ItemStack(p_376369_), 0.0F);
    }

    public @Nullable ItemEntity spawnAtLocation(ServerLevel p_376705_, ItemStack p_376226_) {
        return this.spawnAtLocation(p_376705_, p_376226_, 0.0F);
    }

    public @Nullable ItemEntity spawnAtLocation(ServerLevel p_426271_, ItemStack p_426002_, Vec3 p_426167_) {
        if (p_426002_.isEmpty()) {
            return null;
        } else {
            ItemEntity itementity = new ItemEntity(p_426271_, this.getX() + p_426167_.x, this.getY() + p_426167_.y, this.getZ() + p_426167_.z, p_426002_);
            itementity.setDefaultPickUpDelay();
            if (captureDrops() != null) captureDrops().add(itementity);
            else
            p_426271_.addFreshEntity(itementity);
            return itementity;
        }
    }

    public @Nullable ItemEntity spawnAtLocation(ServerLevel p_376141_, ItemStack p_376472_, float p_376881_) {
        return this.spawnAtLocation(p_376141_, p_376472_, new Vec3(0.0, p_376881_, 0.0));
    }

    public boolean isAlive() {
        return !this.isRemoved();
    }

    public boolean isInWall() {
        if (this.noPhysics) {
            return false;
        } else {
            float f = this.dimensions.width() * 0.8F;
            AABB aabb = AABB.ofSize(this.getEyePosition(), f, 1.0E-6, f);
            return BlockPos.betweenClosedStream(aabb)
                .anyMatch(
                    p_392961_ -> {
                        BlockState blockstate = this.level().getBlockState(p_392961_);
                        return !blockstate.isAir()
                            && blockstate.isSuffocating(this.level(), p_392961_)
                            && Shapes.joinIsNotEmpty(blockstate.getCollisionShape(this.level(), p_392961_).move(p_392961_), Shapes.create(aabb), BooleanOp.AND);
                    }
                );
        }
    }

    public InteractionResult interact(Player p_19978_, InteractionHand p_19979_) {
        if (!this.level().isClientSide()
            && p_19978_.isSecondaryUseActive()
            && this instanceof Leashable leashable
            && leashable.canBeLeashed()
            && this.isAlive()
            && !(this instanceof LivingEntity livingentity && livingentity.isBaby())) {
            List<Leashable> list = Leashable.leashableInArea(this, p_417766_ -> p_417766_.getLeashHolder() == p_19978_);
            if (!list.isEmpty()) {
                boolean flag = false;

                for (Leashable leashable1 : list) {
                    if (leashable1.canHaveALeashAttachedTo(this)) {
                        leashable1.setLeashedTo(this, true);
                        flag = true;
                    }
                }

                if (flag) {
                    this.level().gameEvent(GameEvent.ENTITY_ACTION, this.blockPosition(), GameEvent.Context.of(p_19978_));
                    this.playSound(SoundEvents.LEAD_TIED);
                    return InteractionResult.SUCCESS_SERVER.withoutItem();
                }
            }
        }

        ItemStack itemstack = p_19978_.getItemInHand(p_19979_);
        if (itemstack.canPerformAction(net.neoforged.neoforge.common.ItemAbilities.SHEARS_HARVEST) && this.shearOffAllLeashConnections(p_19978_)) {
            itemstack.hurtAndBreak(1, p_19978_, p_19979_);
            return InteractionResult.SUCCESS;
        } else if (this instanceof Mob mob
            && itemstack.canPerformAction(net.neoforged.neoforge.common.ItemAbilities.SHEARS_REMOVE_ARMOR)
            && mob.canShearEquipment(p_19978_)
            && !p_19978_.isSecondaryUseActive()
            && this.attemptToShearEquipment(p_19978_, p_19979_, itemstack, mob)) {
            return InteractionResult.SUCCESS;
        } else {
            if (this.isAlive() && this instanceof Leashable leashable2) {
                if (leashable2.getLeashHolder() == p_19978_) {
                    if (!this.level().isClientSide()) {
                        if (p_19978_.hasInfiniteMaterials()) {
                            leashable2.removeLeash();
                        } else {
                            leashable2.dropLeash();
                        }

                        this.gameEvent(GameEvent.ENTITY_INTERACT, p_19978_);
                        this.playSound(SoundEvents.LEAD_UNTIED);
                    }

                    return InteractionResult.SUCCESS.withoutItem();
                }

                ItemStack itemstack1 = p_19978_.getItemInHand(p_19979_);
                if (itemstack1.is(Items.LEAD) && !(leashable2.getLeashHolder() instanceof Player)) {
                    if (this.level().isClientSide()) {
                        return InteractionResult.CONSUME;
                    }

                    if (leashable2.canHaveALeashAttachedTo(p_19978_)) {
                        if (leashable2.isLeashed()) {
                            leashable2.dropLeash();
                        }

                        leashable2.setLeashedTo(p_19978_, true);
                        this.playSound(SoundEvents.LEAD_TIED);
                        itemstack1.shrink(1);
                        return InteractionResult.SUCCESS_SERVER;
                    }
                }
            }

            return InteractionResult.PASS;
        }
    }

    public boolean shearOffAllLeashConnections(@Nullable Player p_418337_) {
        boolean flag = this.dropAllLeashConnections(p_418337_);
        if (flag && this.level() instanceof ServerLevel serverlevel) {
            serverlevel.playSound(null, this.blockPosition(), SoundEvents.SHEARS_SNIP, p_418337_ != null ? p_418337_.getSoundSource() : this.getSoundSource());
        }

        return flag;
    }

    public boolean dropAllLeashConnections(@Nullable Player p_418288_) {
        List<Leashable> list = Leashable.leashableLeashedTo(this);
        boolean flag = !list.isEmpty();
        if (this instanceof Leashable leashable && leashable.isLeashed()) {
            leashable.dropLeash();
            flag = true;
        }

        for (Leashable leashable1 : list) {
            leashable1.dropLeash();
        }

        if (flag) {
            this.gameEvent(GameEvent.SHEAR, p_418288_);
            return true;
        } else {
            return false;
        }
    }

    private boolean attemptToShearEquipment(Player p_426225_, InteractionHand p_425581_, ItemStack p_426326_, Mob p_426179_) {
        for (EquipmentSlot equipmentslot : EquipmentSlot.VALUES) {
            ItemStack itemstack = p_426179_.getItemBySlot(equipmentslot);
            Equippable equippable = itemstack.get(DataComponents.EQUIPPABLE);
            if (equippable != null
                && equippable.canBeSheared()
                && (!EnchantmentHelper.has(itemstack, EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE) || p_426225_.isCreative())) {
                p_426326_.hurtAndBreak(1, p_426225_, p_425581_.asEquipmentSlot());
                Vec3 vec3 = this.dimensions.attachments().getAverage(EntityAttachment.PASSENGER);
                p_426179_.setItemSlotAndDropWhenKilled(equipmentslot, ItemStack.EMPTY);
                this.gameEvent(GameEvent.SHEAR, p_426225_);
                this.playSound(equippable.shearingSound().value());
                if (this.level() instanceof ServerLevel serverlevel) {
                    this.spawnAtLocation(serverlevel, itemstack, vec3);
                    CriteriaTriggers.PLAYER_SHEARED_EQUIPMENT.trigger((ServerPlayer)p_426225_, itemstack, p_426179_);
                }

                return true;
            }
        }

        return false;
    }

    public boolean canCollideWith(Entity p_20303_) {
        return p_20303_.canBeCollidedWith(this) && !this.isPassengerOfSameVehicle(p_20303_);
    }

    public boolean canBeCollidedWith(@Nullable Entity p_423659_) {
        return false;
    }

    public void rideTick() {
        this.setDeltaMovement(Vec3.ZERO);
        // Neo: Permit cancellation of Entity#tick via EntityTickEvent.Pre
        if (!net.neoforged.neoforge.event.EventHooks.fireEntityTickPre(this).isCanceled()) {
            this.tick();
            net.neoforged.neoforge.event.EventHooks.fireEntityTickPost(this);
        }
        if (this.isPassenger()) {
            this.getVehicle().positionRider(this);
        }
    }

    public final void positionRider(Entity p_20312_) {
        if (this.hasPassenger(p_20312_)) {
            this.positionRider(p_20312_, Entity::setPos);
        }
    }

    protected void positionRider(Entity p_19957_, Entity.MoveFunction p_19958_) {
        Vec3 vec3 = this.getPassengerRidingPosition(p_19957_);
        Vec3 vec31 = p_19957_.getVehicleAttachmentPoint(this);
        p_19958_.accept(p_19957_, vec3.x - vec31.x, vec3.y - vec31.y, vec3.z - vec31.z);
    }

    public void onPassengerTurned(Entity p_20320_) {
    }

    public Vec3 getVehicleAttachmentPoint(Entity p_316322_) {
        return this.getAttachments().get(EntityAttachment.VEHICLE, 0, this.yRot);
    }

    public Vec3 getPassengerRidingPosition(Entity p_294938_) {
        return this.position().add(this.getPassengerAttachmentPoint(p_294938_, this.dimensions, 1.0F));
    }

    protected Vec3 getPassengerAttachmentPoint(Entity p_294756_, EntityDimensions p_295396_, float p_296362_) {
        return getDefaultPassengerAttachmentPoint(this, p_294756_, p_295396_.attachments());
    }

    protected static Vec3 getDefaultPassengerAttachmentPoint(Entity p_316557_, Entity p_316799_, EntityAttachments p_316167_) {
        int i = p_316557_.getPassengers().indexOf(p_316799_);
        return p_316167_.getClamped(EntityAttachment.PASSENGER, i, p_316557_.yRot);
    }

    public final boolean startRiding(Entity p_20330_) {
        return this.startRiding(p_20330_, false, true);
    }

    public boolean showVehicleHealth() {
        return this instanceof LivingEntity;
    }

    public boolean startRiding(Entity p_19966_, boolean p_19967_, boolean p_435254_) {
        if (p_19966_ == this.vehicle) {
            return false;
        } else if (!p_19966_.couldAcceptPassenger()) {
            return false;
        } else if (!this.level().isClientSide() && !p_19966_.type.canSerialize()) {
            return false;
        } else {
            for (Entity entity = p_19966_; entity.vehicle != null; entity = entity.vehicle) {
                if (entity.vehicle == this) {
                    return false;
                }
            }

        if (!net.neoforged.neoforge.event.EventHooks.canMountEntity(this, p_19966_, true)) return false;
            if (p_19967_ || this.canRide(p_19966_) && p_19966_.canAddPassenger(this)) {
                if (this.isPassenger()) {
                    this.stopRiding();
                }

                this.setPose(Pose.STANDING);
                this.vehicle = p_19966_;
                this.vehicle.addPassenger(this);
                if (p_435254_) {
                    this.level().gameEvent(this, GameEvent.ENTITY_MOUNT, this.vehicle.position);
                    p_19966_.getIndirectPassengersStream()
                        .filter(p_185984_ -> p_185984_ instanceof ServerPlayer)
                        .forEach(p_466532_ -> CriteriaTriggers.START_RIDING_TRIGGER.trigger((ServerPlayer)p_466532_));
                }

                return true;
            } else {
                return false;
            }
        }
    }

    protected boolean canRide(Entity p_20339_) {
        return !this.isShiftKeyDown() && this.boardingCooldown <= 0;
    }

    public void ejectPassengers() {
        for (int i = this.passengers.size() - 1; i >= 0; i--) {
            this.passengers.get(i).stopRiding();
        }
    }

    public void removeVehicle() {
        if (this.vehicle != null) {
            Entity entity = this.vehicle;
            if (!net.neoforged.neoforge.event.EventHooks.canMountEntity(this, entity, false)) return;
            this.vehicle = null;
            entity.removePassenger(this);
            Entity.RemovalReason entity$removalreason = this.getRemovalReason();
            if (entity$removalreason == null || entity$removalreason.shouldDestroy()) {
                this.level().gameEvent(this, GameEvent.ENTITY_DISMOUNT, entity.position);
            }
        }
    }

    public void stopRiding() {
        this.removeVehicle();
    }

    protected void addPassenger(Entity p_20349_) {
        if (p_20349_.getVehicle() != this) {
            throw new IllegalStateException("Use x.startRiding(y), not y.addPassenger(x)");
        } else {
            if (this.passengers.isEmpty()) {
                this.passengers = ImmutableList.of(p_20349_);
            } else {
                List<Entity> list = Lists.newArrayList(this.passengers);
                if (!this.level().isClientSide() && p_20349_ instanceof Player && !(this.getFirstPassenger() instanceof Player)) {
                    list.add(0, p_20349_);
                } else {
                    list.add(p_20349_);
                }

                this.passengers = ImmutableList.copyOf(list);
            }
        }
    }

    protected void removePassenger(Entity p_20352_) {
        if (p_20352_.getVehicle() == this) {
            throw new IllegalStateException("Use x.stopRiding(y), not y.removePassenger(x)");
        } else {
            if (this.passengers.size() == 1 && this.passengers.get(0) == p_20352_) {
                this.passengers = ImmutableList.of();
            } else {
                this.passengers = this.passengers.stream().filter(p_350857_ -> p_350857_ != p_20352_).collect(ImmutableList.toImmutableList());
            }

            p_20352_.boardingCooldown = 60;
        }
    }

    protected boolean canAddPassenger(Entity p_20354_) {
        return this.passengers.isEmpty();
    }

    /** @deprecated Forge: Use {@link #canBeRiddenUnderFluidType(net.neoforged.neoforge.fluids.FluidType, Entity) rider sensitive version} */
    @Deprecated
    protected boolean couldAcceptPassenger() {
        return true;
    }

    public final boolean isInterpolating() {
        return this.getInterpolation() != null && this.getInterpolation().hasActiveInterpolation();
    }

    public final void moveOrInterpolateTo(Vec3 p_394393_, float p_394448_, float p_393511_) {
        this.moveOrInterpolateTo(Optional.of(p_394393_), Optional.of(p_394448_), Optional.of(p_393511_));
    }

    public final void moveOrInterpolateTo(float p_436643_, float p_436812_) {
        this.moveOrInterpolateTo(Optional.empty(), Optional.of(p_436643_), Optional.of(p_436812_));
    }

    public final void moveOrInterpolateTo(Vec3 p_436632_) {
        this.moveOrInterpolateTo(Optional.of(p_436632_), Optional.empty(), Optional.empty());
    }

    public final void moveOrInterpolateTo(Optional<Vec3> p_436730_, Optional<Float> p_436764_, Optional<Float> p_436810_) {
        InterpolationHandler interpolationhandler = this.getInterpolation();
        if (interpolationhandler != null) {
            interpolationhandler.interpolateTo(
                p_436730_.orElse(interpolationhandler.position()), p_436764_.orElse(interpolationhandler.yRot()), p_436810_.orElse(interpolationhandler.xRot())
            );
        } else {
            p_436730_.ifPresent(this::setPos);
            p_436764_.ifPresent(p_436548_ -> this.setYRot(p_436548_ % 360.0F));
            p_436810_.ifPresent(p_436539_ -> this.setXRot(p_436539_ % 360.0F));
        }
    }

    public @Nullable InterpolationHandler getInterpolation() {
        return null;
    }

    public void lerpHeadTo(float p_19918_, int p_19919_) {
        this.setYHeadRot(p_19918_);
    }

    public float getPickRadius() {
        return 0.0F;
    }

    public Vec3 getLookAngle() {
        return this.calculateViewVector(this.getXRot(), this.getYRot());
    }

    public Vec3 getHeadLookAngle() {
        return this.calculateViewVector(this.getXRot(), this.getYHeadRot());
    }

    public Vec3 getHandHoldingItemAngle(Item p_204035_) {
        if (!(this instanceof Player player)) {
            return Vec3.ZERO;
        } else {
            boolean flag = player.getOffhandItem().is(p_204035_) && !player.getMainHandItem().is(p_204035_);
            HumanoidArm humanoidarm = flag ? player.getMainArm().getOpposite() : player.getMainArm();
            return this.calculateViewVector(0.0F, this.getYRot() + (humanoidarm == HumanoidArm.RIGHT ? 80 : -80)).scale(0.5);
        }
    }

    public Vec2 getRotationVector() {
        return new Vec2(this.getXRot(), this.getYRot());
    }

    public Vec3 getForward() {
        return Vec3.directionFromRotation(this.getRotationVector());
    }

    public void setAsInsidePortal(Portal p_351060_, BlockPos p_350462_) {
        if (this.isOnPortalCooldown()) {
            this.setPortalCooldown();
        } else {
            if (this.portalProcess == null || !this.portalProcess.isSamePortal(p_351060_)) {
                this.portalProcess = new PortalProcessor(p_351060_, p_350462_.immutable());
            } else if (!this.portalProcess.isInsidePortalThisTick()) {
                this.portalProcess.updateEntryPosition(p_350462_.immutable());
                this.portalProcess.setAsInsidePortalThisTick(true);
            }
        }
    }

    protected void handlePortal() {
        if (this.level() instanceof ServerLevel serverlevel) {
            this.processPortalCooldown();
            if (this.portalProcess != null) {
                if (this.portalProcess.processPortalTeleportation(serverlevel, this, this.canUsePortal(false))) {
                    ProfilerFiller profilerfiller = Profiler.get();
                    profilerfiller.push("portal");
                    this.setPortalCooldown();
                    TeleportTransition teleporttransition = this.portalProcess.getPortalDestination(serverlevel, this);
                    if (teleporttransition != null) {
                        ServerLevel serverlevel1 = teleporttransition.newLevel();
                        if (serverlevel.isAllowedToEnterPortal(serverlevel1)
                            && (serverlevel1.dimension() == serverlevel.dimension() || this.canTeleport(serverlevel, serverlevel1))) {
                            this.teleport(teleporttransition);
                        }
                    }

                    profilerfiller.pop();
                } else if (this.portalProcess.hasExpired()) {
                    this.portalProcess = null;
                }
            }
        }
    }

    public int getDimensionChangingDelay() {
        Entity entity = this.getFirstPassenger();
        return entity instanceof ServerPlayer ? entity.getDimensionChangingDelay() : 300;
    }

    public void lerpMotion(Vec3 p_436647_) {
        this.setDeltaMovement(p_436647_);
    }

    public void handleDamageEvent(DamageSource p_270704_) {
    }

    public void handleEntityEvent(byte p_19882_) {
        switch (p_19882_) {
            case 53:
                HoneyBlock.showSlideParticles(this);
        }
    }

    public void animateHurt(float p_265161_) {
    }

    public boolean isOnFire() {
        boolean flag = this.level() != null && this.level().isClientSide();
        return !this.fireImmune() && (this.remainingFireTicks > 0 || flag && this.getSharedFlag(0));
    }

    public boolean isPassenger() {
        return this.getVehicle() != null;
    }

    public boolean isVehicle() {
        return !this.passengers.isEmpty();
    }

    public boolean dismountsUnderwater() {
        return this.getType().is(EntityTypeTags.DISMOUNTS_UNDERWATER);
    }

    public boolean canControlVehicle() {
        return !this.getType().is(EntityTypeTags.NON_CONTROLLING_RIDER);
    }

    public void setShiftKeyDown(boolean p_20261_) {
        this.setSharedFlag(1, p_20261_);
    }

    public boolean isShiftKeyDown() {
        return this.getSharedFlag(1);
    }

    public boolean isSteppingCarefully() {
        return this.isShiftKeyDown();
    }

    public boolean isSuppressingBounce() {
        return this.isShiftKeyDown();
    }

    public boolean isDiscrete() {
        return this.isShiftKeyDown();
    }

    public boolean isDescending() {
        return this.isShiftKeyDown();
    }

    public boolean isCrouching() {
        return this.hasPose(Pose.CROUCHING);
    }

    public boolean isSprinting() {
        return this.getSharedFlag(3);
    }

    public void setSprinting(boolean p_20274_) {
        this.setSharedFlag(3, p_20274_);
    }

    public boolean isSwimming() {
        return this.getSharedFlag(4);
    }

    public boolean isVisuallySwimming() {
        return this.hasPose(Pose.SWIMMING);
    }

    public boolean isVisuallyCrawling() {
        return this.isVisuallySwimming() && !this.isInWater() && !this.isInFluidType((fluidType, height) -> this.canSwimInFluidType(fluidType));
    }

    public void setSwimming(boolean p_20283_) {
        this.setSharedFlag(4, p_20283_);
    }

    public final boolean hasGlowingTag() {
        return this.hasGlowingTag;
    }

    public final void setGlowingTag(boolean p_146916_) {
        this.hasGlowingTag = p_146916_;
        this.setSharedFlag(6, this.isCurrentlyGlowing());
    }

    public boolean isCurrentlyGlowing() {
        return this.level().isClientSide() ? this.getSharedFlag(6) : this.hasGlowingTag;
    }

    public boolean isInvisible() {
        return this.getSharedFlag(5);
    }

    public boolean isInvisibleTo(Player p_20178_) {
        if (p_20178_.isSpectator()) {
            return false;
        } else {
            Team team = this.getTeam();
            return team != null && p_20178_ != null && p_20178_.getTeam() == team && team.canSeeFriendlyInvisibles() ? false : this.isInvisible();
        }
    }

    public boolean isOnRails() {
        return false;
    }

    public void updateDynamicGameEventListener(BiConsumer<DynamicGameEventListener<?>, ServerLevel> p_216996_) {
    }

    public @Nullable PlayerTeam getTeam() {
        return this.level().getScoreboard().getPlayersTeam(this.getScoreboardName());
    }

    public final boolean isAlliedTo(@Nullable Entity p_20355_) {
        return p_20355_ == null ? false : this == p_20355_ || this.considersEntityAsAlly(p_20355_) || p_20355_.considersEntityAsAlly(this);
    }

    protected boolean considersEntityAsAlly(Entity p_364894_) {
        return this.isAlliedTo(p_364894_.getTeam());
    }

    public boolean isAlliedTo(@Nullable Team p_20032_) {
        return this.getTeam() != null ? this.getTeam().isAlliedTo(p_20032_) : false;
    }

    public void setInvisible(boolean p_20304_) {
        this.setSharedFlag(5, p_20304_);
    }

    protected boolean getSharedFlag(int p_20292_) {
        return (this.entityData.get(DATA_SHARED_FLAGS_ID) & 1 << p_20292_) != 0;
    }

    protected void setSharedFlag(int p_20116_, boolean p_20117_) {
        byte b0 = this.entityData.get(DATA_SHARED_FLAGS_ID);
        if (p_20117_) {
            this.entityData.set(DATA_SHARED_FLAGS_ID, (byte)(b0 | 1 << p_20116_));
        } else {
            this.entityData.set(DATA_SHARED_FLAGS_ID, (byte)(b0 & ~(1 << p_20116_)));
        }
    }

    public int getMaxAirSupply() {
        return 300;
    }

    public int getAirSupply() {
        return this.entityData.get(DATA_AIR_SUPPLY_ID);
    }

    public void setAirSupply(int p_20302_) {
        this.entityData.set(DATA_AIR_SUPPLY_ID, p_20302_);
    }

    public void clearFreeze() {
        this.setTicksFrozen(0);
    }

    public int getTicksFrozen() {
        return this.entityData.get(DATA_TICKS_FROZEN);
    }

    public void setTicksFrozen(int p_146918_) {
        this.entityData.set(DATA_TICKS_FROZEN, p_146918_);
    }

    public float getPercentFrozen() {
        int i = this.getTicksRequiredToFreeze();
        return (float)Math.min(this.getTicksFrozen(), i) / i;
    }

    public boolean isFullyFrozen() {
        return this.getTicksFrozen() >= this.getTicksRequiredToFreeze();
    }

    public int getTicksRequiredToFreeze() {
        return 140;
    }

    public void thunderHit(ServerLevel p_19927_, LightningBolt p_19928_) {
        this.setRemainingFireTicks(this.remainingFireTicks + 1);
        if (this.remainingFireTicks == 0) {
            this.igniteForSeconds(8.0F);
        }

        this.hurt(this.damageSources().lightningBolt(), p_19928_.getDamage());
    }

    public void onAboveBubbleColumn(boolean p_399860_, BlockPos p_399894_) {
        handleOnAboveBubbleColumn(this, p_399860_, p_399894_);
    }

    protected static void handleOnAboveBubbleColumn(Entity p_400150_, boolean p_400155_, BlockPos p_399923_) {
        Vec3 vec3 = p_400150_.getDeltaMovement();
        double d0;
        if (p_400155_) {
            d0 = Math.max(-0.9, vec3.y - 0.03);
        } else {
            d0 = Math.min(1.8, vec3.y + 0.1);
        }

        p_400150_.setDeltaMovement(vec3.x, d0, vec3.z);
        sendBubbleColumnParticles(p_400150_.level, p_399923_);
    }

    protected static void sendBubbleColumnParticles(Level p_399612_, BlockPos p_393959_) {
        if (p_399612_ instanceof ServerLevel serverlevel) {
            for (int i = 0; i < 2; i++) {
                serverlevel.sendParticles(
                    ParticleTypes.SPLASH,
                    p_393959_.getX() + p_399612_.random.nextDouble(),
                    p_393959_.getY() + 1,
                    p_393959_.getZ() + p_399612_.random.nextDouble(),
                    1,
                    0.0,
                    0.0,
                    0.0,
                    1.0
                );
                serverlevel.sendParticles(
                    ParticleTypes.BUBBLE,
                    p_393959_.getX() + p_399612_.random.nextDouble(),
                    p_393959_.getY() + 1,
                    p_393959_.getZ() + p_399612_.random.nextDouble(),
                    1,
                    0.0,
                    0.01,
                    0.0,
                    0.2
                );
            }
        }
    }

    public void onInsideBubbleColumn(boolean p_20322_) {
        handleOnInsideBubbleColumn(this, p_20322_);
    }

    protected static void handleOnInsideBubbleColumn(Entity p_399975_, boolean p_399770_) {
        Vec3 vec3 = p_399975_.getDeltaMovement();
        double d0;
        if (p_399770_) {
            d0 = Math.max(-0.3, vec3.y - 0.03);
        } else {
            d0 = Math.min(0.7, vec3.y + 0.06);
        }

        p_399975_.setDeltaMovement(vec3.x, d0, vec3.z);
        p_399975_.resetFallDistance();
    }

    public boolean killedEntity(ServerLevel p_216988_, LivingEntity p_216989_, DamageSource p_432749_) {
        return true;
    }

    public void checkFallDistanceAccumulation() {
        if (this.getDeltaMovement().y() > -0.5 && this.fallDistance > 1.0) {
            this.fallDistance = 1.0;
        }
    }

    public void resetFallDistance() {
        this.fallDistance = 0.0;
    }

    protected void moveTowardsClosestSpace(double p_20315_, double p_20316_, double p_20317_) {
        BlockPos blockpos = BlockPos.containing(p_20315_, p_20316_, p_20317_);
        Vec3 vec3 = new Vec3(p_20315_ - blockpos.getX(), p_20316_ - blockpos.getY(), p_20317_ - blockpos.getZ());
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        Direction direction = Direction.UP;
        double d0 = Double.MAX_VALUE;

        for (Direction direction1 : new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, Direction.UP}) {
            blockpos$mutableblockpos.setWithOffset(blockpos, direction1);
            if (!this.level().getBlockState(blockpos$mutableblockpos).isCollisionShapeFullBlock(this.level(), blockpos$mutableblockpos)) {
                double d1 = vec3.get(direction1.getAxis());
                double d2 = direction1.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1.0 - d1 : d1;
                if (d2 < d0) {
                    d0 = d2;
                    direction = direction1;
                }
            }
        }

        float f = this.random.nextFloat() * 0.2F + 0.1F;
        float f1 = direction.getAxisDirection().getStep();
        Vec3 vec31 = this.getDeltaMovement().scale(0.75);
        if (direction.getAxis() == Direction.Axis.X) {
            this.setDeltaMovement(f1 * f, vec31.y, vec31.z);
        } else if (direction.getAxis() == Direction.Axis.Y) {
            this.setDeltaMovement(vec31.x, f1 * f, vec31.z);
        } else if (direction.getAxis() == Direction.Axis.Z) {
            this.setDeltaMovement(vec31.x, vec31.y, f1 * f);
        }
    }

    public void makeStuckInBlock(BlockState p_20006_, Vec3 p_20007_) {
        this.resetFallDistance();
        this.stuckSpeedMultiplier = p_20007_;
    }

    private static Component removeAction(Component p_20141_) {
        MutableComponent mutablecomponent = p_20141_.plainCopy().setStyle(p_20141_.getStyle().withClickEvent(null));

        for (Component component : p_20141_.getSiblings()) {
            mutablecomponent.append(removeAction(component));
        }

        return mutablecomponent;
    }

    @Override
    public Component getName() {
        Component component = this.getCustomName();
        return component != null ? removeAction(component) : this.getTypeName();
    }

    protected Component getTypeName() {
        return this.getType().getDescription(); // Forge: Use getter to allow overriding by mods
    }

    public boolean is(Entity p_20356_) {
        return this == p_20356_;
    }

    public float getYHeadRot() {
        return 0.0F;
    }

    public void setYHeadRot(float p_20328_) {
    }

    public void setYBodyRot(float p_20338_) {
    }

    public boolean isAttackable() {
        return true;
    }

    public boolean skipAttackInteraction(Entity p_20357_) {
        return false;
    }

    @Override
    public String toString() {
        String s = this.level() == null ? "~NULL~" : this.level().toString();
        return this.removalReason != null
            ? String.format(
                Locale.ROOT,
                "%s['%s'/%d, l='%s', x=%.2f, y=%.2f, z=%.2f, removed=%s]",
                this.getClass().getSimpleName(),
                this.getPlainTextName(),
                this.id,
                s,
                this.getX(),
                this.getY(),
                this.getZ(),
                this.removalReason
            )
            : String.format(
                Locale.ROOT,
                "%s['%s'/%d, l='%s', x=%.2f, y=%.2f, z=%.2f]",
                this.getClass().getSimpleName(),
                this.getPlainTextName(),
                this.id,
                s,
                this.getX(),
                this.getY(),
                this.getZ()
            );
    }

    protected final boolean isInvulnerableToBase(DamageSource p_20122_) {
        boolean isVanillaInvulnerable = this.isRemoved()
            || this.invulnerable && !p_20122_.is(DamageTypeTags.BYPASSES_INVULNERABILITY) && !p_20122_.isCreativePlayer()
            || p_20122_.is(DamageTypeTags.IS_FIRE) && this.fireImmune()
            || p_20122_.is(DamageTypeTags.IS_FALL) && this.getType().is(EntityTypeTags.FALL_DAMAGE_IMMUNE);
        return net.neoforged.neoforge.common.CommonHooks.isEntityInvulnerableTo(this, p_20122_, isVanillaInvulnerable);
    }

    public boolean isInvulnerable() {
        return this.invulnerable;
    }

    public void setInvulnerable(boolean p_20332_) {
        this.invulnerable = p_20332_;
    }

    public void copyPosition(Entity p_20360_) {
        this.snapTo(p_20360_.getX(), p_20360_.getY(), p_20360_.getZ(), p_20360_.getYRot(), p_20360_.getXRot());
    }

    public void restoreFrom(Entity p_20362_) {
        try (ProblemReporter.ScopedCollector problemreporter$scopedcollector = new ProblemReporter.ScopedCollector(this.problemPath(), LOGGER)) {
            TagValueOutput tagvalueoutput = TagValueOutput.createWithContext(problemreporter$scopedcollector, p_20362_.registryAccess());
            p_20362_.saveWithoutId(tagvalueoutput);
            this.load(TagValueInput.create(problemreporter$scopedcollector, this.registryAccess(), tagvalueoutput.buildResult()));
        }

        this.portalCooldown = p_20362_.portalCooldown;
        this.portalProcess = p_20362_.portalProcess;
    }

    public @Nullable Entity teleport(TeleportTransition p_379899_) {
        if (!net.neoforged.neoforge.common.CommonHooks.onTravelToDimension(this, p_379899_.newLevel().dimension())) return null;
        if (this.level() instanceof ServerLevel serverlevel && !this.isRemoved()) {
            ServerLevel serverlevel1 = p_379899_.newLevel();
            boolean flag = serverlevel1.dimension() != serverlevel.dimension();
            if (!p_379899_.asPassenger()) {
                this.stopRiding();
            }

            return flag ? this.teleportCrossDimension(serverlevel, serverlevel1, p_379899_) : this.teleportSameDimension(serverlevel, p_379899_);
        } else {
            return null;
        }
    }

    private Entity teleportSameDimension(ServerLevel p_379931_, TeleportTransition p_380103_) {
        for (Entity entity : this.getPassengers()) {
            entity.teleport(this.calculatePassengerTransition(p_380103_, entity));
        }

        ProfilerFiller profilerfiller = Profiler.get();
        profilerfiller.push("teleportSameDimension");
        this.teleportSetPosition(PositionMoveRotation.of(p_380103_), p_380103_.relatives());
        if (!p_380103_.asPassenger()) {
            this.sendTeleportTransitionToRidingPlayers(p_380103_);
        }

        p_380103_.postTeleportTransition().onTransition(this);
        profilerfiller.pop();
        return this;
    }

    private @Nullable Entity teleportCrossDimension(ServerLevel p_380146_, ServerLevel p_416740_, TeleportTransition p_379377_) {
        List<Entity> list = this.getPassengers();
        List<Entity> list1 = new ArrayList<>(list.size());
        this.ejectPassengers();

        for (Entity entity : list) {
            Entity entity1 = entity.teleport(this.calculatePassengerTransition(p_379377_, entity));
            if (entity1 != null) {
                list1.add(entity1);
            }
        }

        ProfilerFiller profilerfiller = Profiler.get();
        profilerfiller.push("teleportCrossDimension");
        Entity entity3 = this.getType().create(p_416740_, EntitySpawnReason.DIMENSION_TRAVEL);
        if (entity3 == null) {
            profilerfiller.pop();
            return null;
        } else {
            entity3.restoreFrom(this);
            this.removeAfterChangingDimensions();
            entity3.teleportSetPosition(PositionMoveRotation.of(this), PositionMoveRotation.of(p_379377_), p_379377_.relatives());
            p_416740_.addDuringTeleport(entity3);

            for (Entity entity2 : list1) {
                entity2.startRiding(entity3, true, false);
            }

            p_416740_.resetEmptyTime();
            p_379377_.postTeleportTransition().onTransition(entity3);
            this.teleportSpectators(p_379377_, p_380146_);
            profilerfiller.pop();
            return entity3;
        }
    }

    protected void teleportSpectators(TeleportTransition p_415929_, ServerLevel p_416559_) {
        for (ServerPlayer serverplayer : List.copyOf(p_416559_.players())) {
            if (serverplayer.getCamera() == this) {
                serverplayer.teleport(p_415929_);
                serverplayer.setCamera(null);
            }
        }
    }

    private TeleportTransition calculatePassengerTransition(TeleportTransition p_380120_, Entity p_379847_) {
        float f = p_380120_.yRot() + (p_380120_.relatives().contains(Relative.Y_ROT) ? 0.0F : p_379847_.getYRot() - this.getYRot());
        float f1 = p_380120_.xRot() + (p_380120_.relatives().contains(Relative.X_ROT) ? 0.0F : p_379847_.getXRot() - this.getXRot());
        Vec3 vec3 = p_379847_.position().subtract(this.position());
        Vec3 vec31 = p_380120_.position()
            .add(
                p_380120_.relatives().contains(Relative.X) ? 0.0 : vec3.x(),
                p_380120_.relatives().contains(Relative.Y) ? 0.0 : vec3.y(),
                p_380120_.relatives().contains(Relative.Z) ? 0.0 : vec3.z()
            );
        return p_380120_.withPosition(vec31).withRotation(f, f1).transitionAsPassenger();
    }

    private void sendTeleportTransitionToRidingPlayers(TeleportTransition p_379413_) {
        Entity entity = this.getControllingPassenger();

        for (Entity entity1 : this.getIndirectPassengers()) {
            if (entity1 instanceof ServerPlayer serverplayer) {
                if (entity != null && serverplayer.getId() == entity.getId()) {
                    serverplayer.connection
                        .send(ClientboundTeleportEntityPacket.teleport(this.getId(), PositionMoveRotation.of(p_379413_), p_379413_.relatives(), this.onGround));
                } else {
                    serverplayer.connection
                        .send(ClientboundTeleportEntityPacket.teleport(this.getId(), PositionMoveRotation.of(this), Set.of(), this.onGround));
                }
            }
        }
    }

    public void teleportSetPosition(PositionMoveRotation p_376669_, Set<Relative> p_376772_) {
        this.teleportSetPosition(PositionMoveRotation.of(this), p_376669_, p_376772_);
    }

    public void teleportSetPosition(PositionMoveRotation p_451395_, PositionMoveRotation p_451284_, Set<Relative> p_451242_) {
        PositionMoveRotation positionmoverotation = PositionMoveRotation.calculateAbsolute(p_451395_, p_451284_, p_451242_);
        this.setPosRaw(positionmoverotation.position().x, positionmoverotation.position().y, positionmoverotation.position().z);
        this.setYRot(positionmoverotation.yRot());
        this.setYHeadRot(positionmoverotation.yRot());
        this.setXRot(positionmoverotation.xRot());
        this.reapplyPosition();
        this.setOldPosAndRot();
        this.setDeltaMovement(positionmoverotation.deltaMovement());
        this.clearMovementThisTick();
    }

    public void forceSetRotation(float p_379323_, boolean p_436782_, float p_380342_, boolean p_436769_) {
        Set<Relative> set = Relative.rotation(p_436782_, p_436769_);
        PositionMoveRotation positionmoverotation = PositionMoveRotation.of(this);
        PositionMoveRotation positionmoverotation1 = positionmoverotation.withRotation(p_379323_, p_380342_);
        PositionMoveRotation positionmoverotation2 = PositionMoveRotation.calculateAbsolute(positionmoverotation, positionmoverotation1, set);
        this.setYRot(positionmoverotation2.yRot());
        this.setYHeadRot(positionmoverotation2.yRot());
        this.setXRot(positionmoverotation2.xRot());
        this.setOldRot();
    }

    public void placePortalTicket(BlockPos p_352083_) {
        if (this.level() instanceof ServerLevel serverlevel) {
            serverlevel.getChunkSource().addTicketWithRadius(TicketType.PORTAL, new ChunkPos(p_352083_), 3);
        }
    }

    protected void removeAfterChangingDimensions() {
        this.setRemoved(Entity.RemovalReason.CHANGED_DIMENSION);
        if (this instanceof Leashable leashable) {
            leashable.removeLeash();
        }

        if (this instanceof WaypointTransmitter waypointtransmitter && this.level instanceof ServerLevel serverlevel) {
            serverlevel.getWaypointManager().untrackWaypoint(waypointtransmitter);
        }
    }

    public Vec3 getRelativePortalPosition(Direction.Axis p_20045_, BlockUtil.FoundRectangle p_467310_) {
        return PortalShape.getRelativePosition(p_467310_, p_20045_, this.position(), this.getDimensions(this.getPose()));
    }

    public boolean canUsePortal(boolean p_352898_) {
        return (p_352898_ || !this.isPassenger()) && this.isAlive();
    }

    public boolean canTeleport(Level p_352904_, Level p_352909_) {
        if (p_352904_.dimension() == Level.END && p_352909_.dimension() == Level.OVERWORLD) {
            for (Entity entity : this.getPassengers()) {
                if (entity instanceof ServerPlayer serverplayer && !serverplayer.seenCredits) {
                    return false;
                }
            }
        }

        return true;
    }

    public float getBlockExplosionResistance(
        Explosion p_19992_, BlockGetter p_19993_, BlockPos p_19994_, BlockState p_19995_, FluidState p_19996_, float p_19997_
    ) {
        return p_19997_;
    }

    public boolean shouldBlockExplode(Explosion p_19987_, BlockGetter p_19988_, BlockPos p_19989_, BlockState p_19990_, float p_19991_) {
        return true;
    }

    public int getMaxFallDistance() {
        return 3;
    }

    public boolean isIgnoringBlockTriggers() {
        return false;
    }

    public void fillCrashReportCategory(CrashReportCategory p_20051_) {
        p_20051_.setDetail("Entity Type", () -> EntityType.getKey(this.getType()) + " (" + this.getClass().getCanonicalName() + ")");
        p_20051_.setDetail("Entity ID", this.id);
        p_20051_.setDetail("Entity Name", () -> this.getPlainTextName());
        p_20051_.setDetail("Entity's Exact location", String.format(Locale.ROOT, "%.2f, %.2f, %.2f", this.getX(), this.getY(), this.getZ()));
        p_20051_.setDetail(
            "Entity's Block location", CrashReportCategory.formatLocation(this.level(), Mth.floor(this.getX()), Mth.floor(this.getY()), Mth.floor(this.getZ()))
        );
        Vec3 vec3 = this.getDeltaMovement();
        p_20051_.setDetail("Entity's Momentum", String.format(Locale.ROOT, "%.2f, %.2f, %.2f", vec3.x, vec3.y, vec3.z));
        p_20051_.setDetail("Entity's Passengers", () -> this.getPassengers().toString());
        p_20051_.setDetail("Entity's Vehicle", () -> String.valueOf(this.getVehicle()));
    }

    public boolean displayFireAnimation() {
        return this.isOnFire() && !this.isSpectator();
    }

    public void setUUID(UUID p_20085_) {
        this.uuid = p_20085_;
        this.stringUUID = this.uuid.toString();
    }

    @Override
    public UUID getUUID() {
        return this.uuid;
    }

    public String getStringUUID() {
        return this.stringUUID;
    }

    @Override
    public String getScoreboardName() {
        return this.stringUUID;
    }

    @Deprecated // Forge: Use FluidType sensitive version
    public boolean isPushedByFluid() {
        return true;
    }

    public static double getViewScale() {
        return viewScale;
    }

    public static void setViewScale(double p_20104_) {
        viewScale = p_20104_;
    }

    @Override
    public Component getDisplayName() {
        return PlayerTeam.formatNameForTeam(this.getTeam(), this.getName())
            .withStyle(p_185975_ -> p_185975_.withHoverEvent(this.createHoverEvent()).withInsertion(this.getStringUUID()));
    }

    public void setCustomName(@Nullable Component p_20053_) {
        this.entityData.set(DATA_CUSTOM_NAME, Optional.ofNullable(p_20053_));
    }

    @Override
    public @Nullable Component getCustomName() {
        return this.entityData.get(DATA_CUSTOM_NAME).orElse(null);
    }

    @Override
    public boolean hasCustomName() {
        return this.entityData.get(DATA_CUSTOM_NAME).isPresent();
    }

    public void setCustomNameVisible(boolean p_20341_) {
        this.entityData.set(DATA_CUSTOM_NAME_VISIBLE, p_20341_);
    }

    public boolean isCustomNameVisible() {
        return this.entityData.get(DATA_CUSTOM_NAME_VISIBLE);
    }

    public boolean teleportTo(
        ServerLevel p_265257_,
        double p_265407_,
        double p_265727_,
        double p_265410_,
        Set<Relative> p_265083_,
        float p_265573_,
        float p_265094_,
        boolean p_365015_
    ) {
        Entity entity = this.teleport(
            new TeleportTransition(
                p_265257_, new Vec3(p_265407_, p_265727_, p_265410_), Vec3.ZERO, p_265573_, p_265094_, p_265083_, TeleportTransition.DO_NOTHING
            )
        );
        return entity != null;
    }

    public void dismountTo(double p_146825_, double p_146826_, double p_146827_) {
        this.teleportTo(p_146825_, p_146826_, p_146827_);
    }

    public void teleportTo(double p_19887_, double p_19888_, double p_19889_) {
        if (this.level() instanceof ServerLevel) {
            this.snapTo(p_19887_, p_19888_, p_19889_, this.getYRot(), this.getXRot());
            this.teleportPassengers();
        }
    }

    private void teleportPassengers() {
        this.getSelfAndPassengers().forEach(p_185977_ -> {
            for (Entity entity : p_185977_.passengers) {
                p_185977_.positionRider(entity, Entity::snapTo);
            }
        });
    }

    public void teleportRelative(double p_249341_, double p_252229_, double p_252038_) {
        this.teleportTo(this.getX() + p_249341_, this.getY() + p_252229_, this.getZ() + p_252038_);
    }

    public boolean shouldShowName() {
        return this.isCustomNameVisible();
    }

    @Override
    public void onSyncedDataUpdated(List<SynchedEntityData.DataValue<?>> p_270372_) {
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> p_20059_) {
        if (DATA_POSE.equals(p_20059_)) {
            this.refreshDimensions();
        }
    }

    @Deprecated
    protected void fixupDimensions() {
        Pose pose = this.getPose();
        EntityDimensions entitydimensions = this.getDimensions(pose);
        this.dimensions = entitydimensions;
        this.eyeHeight = entitydimensions.eyeHeight();
    }

    public void refreshDimensions() {
        EntityDimensions entitydimensions = this.dimensions;
        Pose pose = this.getPose();
        EntityDimensions entitydimensions1 = this.getDimensions(pose);
        net.neoforged.neoforge.event.entity.EntityEvent.Size sizeEvent = net.neoforged.neoforge.event.EventHooks.getEntitySizeForge(this, pose, entitydimensions, entitydimensions1);
        entitydimensions1 = sizeEvent.getNewSize();
        this.dimensions = entitydimensions1;
        this.eyeHeight = entitydimensions1.eyeHeight();
        this.reapplyPosition();
        boolean flag = entitydimensions1.width() <= 4.0F && entitydimensions1.height() <= 4.0F;
        if (!this.level.isClientSide()
            && !this.firstTick
            && !this.noPhysics
            && flag
            && (entitydimensions1.width() > entitydimensions.width() || entitydimensions1.height() > entitydimensions.height())
            && !(this instanceof Player)) {
            this.fudgePositionAfterSizeChange(entitydimensions);
        }
    }

    public boolean fudgePositionAfterSizeChange(EntityDimensions p_347526_) {
        EntityDimensions entitydimensions = this.getDimensions(this.getPose());
        Vec3 vec3 = this.position().add(0.0, p_347526_.height() / 2.0, 0.0);
        double d0 = Math.max(0.0F, entitydimensions.width() - p_347526_.width()) + 1.0E-6;
        double d1 = Math.max(0.0F, entitydimensions.height() - p_347526_.height()) + 1.0E-6;
        VoxelShape voxelshape = Shapes.create(AABB.ofSize(vec3, d0, d1, d0));
        Optional<Vec3> optional = this.level
            .findFreePosition(this, voxelshape, vec3, entitydimensions.width(), entitydimensions.height(), entitydimensions.width());
        if (optional.isPresent()) {
            this.setPos(optional.get().add(0.0, -entitydimensions.height() / 2.0, 0.0));
            return true;
        } else {
            if (entitydimensions.width() > p_347526_.width() && entitydimensions.height() > p_347526_.height()) {
                VoxelShape voxelshape1 = Shapes.create(AABB.ofSize(vec3, d0, 1.0E-6, d0));
                Optional<Vec3> optional1 = this.level
                    .findFreePosition(this, voxelshape1, vec3, entitydimensions.width(), p_347526_.height(), entitydimensions.width());
                if (optional1.isPresent()) {
                    this.setPos(optional1.get().add(0.0, -p_347526_.height() / 2.0 + 1.0E-6, 0.0));
                    return true;
                }
            }

            return false;
        }
    }

    public Direction getDirection() {
        return Direction.fromYRot(this.getYRot());
    }

    public Direction getMotionDirection() {
        return this.getDirection();
    }

    protected HoverEvent createHoverEvent() {
        return new HoverEvent.ShowEntity(new HoverEvent.EntityTooltipInfo(this.getType(), this.getUUID(), this.getName()));
    }

    public boolean broadcastToPlayer(ServerPlayer p_19937_) {
        return true;
    }

    @Override
    public final AABB getBoundingBox() {
        return this.bb;
    }

    public final void setBoundingBox(AABB p_20012_) {
        this.bb = p_20012_;
    }

    public final float getEyeHeight(Pose p_20237_) {
        return this.getDimensions(p_20237_).eyeHeight();
    }

    public final float getEyeHeight() {
        return this.eyeHeight;
    }

    @Override
    public @Nullable SlotAccess getSlot(int p_146919_) {
        return null;
    }

    public InteractionResult interactAt(Player p_19980_, Vec3 p_19981_, InteractionHand p_19982_) {
        return InteractionResult.PASS;
    }

    public boolean ignoreExplosion(Explosion p_312868_) {
        return false;
    }

    public void startSeenByPlayer(ServerPlayer p_20119_) {
    }

    public void stopSeenByPlayer(ServerPlayer p_20174_) {
    }

    public float rotate(Rotation p_20004_) {
        float f = Mth.wrapDegrees(this.getYRot());

        return switch (p_20004_) {
            case CLOCKWISE_180 -> f + 180.0F;
            case COUNTERCLOCKWISE_90 -> f + 270.0F;
            case CLOCKWISE_90 -> f + 90.0F;
            default -> f;
        };
    }

    public float mirror(Mirror p_20003_) {
        float f = Mth.wrapDegrees(this.getYRot());

        return switch (p_20003_) {
            case FRONT_BACK -> -f;
            case LEFT_RIGHT -> 180.0F - f;
            default -> f;
        };
    }

    public ProjectileDeflection deflection(Projectile p_319824_) {
        return this.getType().is(EntityTypeTags.DEFLECTS_PROJECTILES) ? ProjectileDeflection.REVERSE : ProjectileDeflection.NONE;
    }

    public @Nullable LivingEntity getControllingPassenger() {
        return null;
    }

    public final boolean hasControllingPassenger() {
        return this.getControllingPassenger() != null;
    }

    public final List<Entity> getPassengers() {
        return this.passengers;
    }

    public @Nullable Entity getFirstPassenger() {
        return this.passengers.isEmpty() ? null : this.passengers.get(0);
    }

    public boolean hasPassenger(Entity p_20364_) {
        return this.passengers.contains(p_20364_);
    }

    public boolean hasPassenger(Predicate<Entity> p_146863_) {
        for (Entity entity : this.passengers) {
            if (p_146863_.test(entity)) {
                return true;
            }
        }

        return false;
    }

    private Stream<Entity> getIndirectPassengersStream() {
        return this.passengers.stream().flatMap(Entity::getSelfAndPassengers);
    }

    @Override
    public Stream<Entity> getSelfAndPassengers() {
        return Stream.concat(Stream.of(this), this.getIndirectPassengersStream());
    }

    @Override
    public Stream<Entity> getPassengersAndSelf() {
        return Stream.concat(this.passengers.stream().flatMap(Entity::getPassengersAndSelf), Stream.of(this));
    }

    public Iterable<Entity> getIndirectPassengers() {
        return () -> this.getIndirectPassengersStream().iterator();
    }

    public int countPlayerPassengers() {
        return (int)this.getIndirectPassengersStream().filter(p_185943_ -> p_185943_ instanceof Player).count();
    }

    public boolean hasExactlyOnePlayerPassenger() {
        return this.countPlayerPassengers() == 1;
    }

    public Entity getRootVehicle() {
        Entity entity = this;

        while (entity.isPassenger()) {
            entity = entity.getVehicle();
        }

        return entity;
    }

    public boolean isPassengerOfSameVehicle(Entity p_20366_) {
        return this.getRootVehicle() == p_20366_.getRootVehicle();
    }

    public boolean hasIndirectPassenger(Entity p_20368_) {
        if (!p_20368_.isPassenger()) {
            return false;
        } else {
            Entity entity = p_20368_.getVehicle();
            return entity == this ? true : this.hasIndirectPassenger(entity);
        }
    }

    public final boolean isLocalInstanceAuthoritative() {
        return this.level.isClientSide() ? this.isLocalClientAuthoritative() : !this.isClientAuthoritative();
    }

    protected boolean isLocalClientAuthoritative() {
        LivingEntity livingentity = this.getControllingPassenger();
        return livingentity != null && livingentity.isLocalClientAuthoritative();
    }

    public boolean isClientAuthoritative() {
        LivingEntity livingentity = this.getControllingPassenger();
        return livingentity != null && livingentity.isClientAuthoritative();
    }

    public boolean canSimulateMovement() {
        return this.isLocalInstanceAuthoritative();
    }

    public boolean isEffectiveAi() {
        return this.isLocalInstanceAuthoritative();
    }

    protected static Vec3 getCollisionHorizontalEscapeVector(double p_19904_, double p_19905_, float p_19906_) {
        double d0 = (p_19904_ + p_19905_ + 1.0E-5F) / 2.0;
        float f = -Mth.sin(p_19906_ * (float) (Math.PI / 180.0));
        float f1 = Mth.cos(p_19906_ * (float) (Math.PI / 180.0));
        float f2 = Math.max(Math.abs(f), Math.abs(f1));
        return new Vec3(f * d0 / f2, 0.0, f1 * d0 / f2);
    }

    public Vec3 getDismountLocationForPassenger(LivingEntity p_20123_) {
        return new Vec3(this.getX(), this.getBoundingBox().maxY, this.getZ());
    }

    public @Nullable Entity getVehicle() {
        return this.vehicle;
    }

    public @Nullable Entity getControlledVehicle() {
        return this.vehicle != null && this.vehicle.getControllingPassenger() == this ? this.vehicle : null;
    }

    public PushReaction getPistonPushReaction() {
        return PushReaction.NORMAL;
    }

    public SoundSource getSoundSource() {
        return SoundSource.NEUTRAL;
    }

    protected int getFireImmuneTicks() {
        return 0;
    }

    public CommandSourceStack createCommandSourceStackForNameResolution(ServerLevel p_376557_) {
        return new CommandSourceStack(
            CommandSource.NULL,
            this.position(),
            this.getRotationVector(),
            p_376557_,
            PermissionSet.NO_PERMISSIONS,
            this.getPlainTextName(),
            this.getDisplayName(),
            p_376557_.getServer(),
            this
        );
    }

    public void lookAt(EntityAnchorArgument.Anchor p_20033_, Vec3 p_20034_) {
        Vec3 vec3 = p_20033_.apply(this);
        double d0 = p_20034_.x - vec3.x;
        double d1 = p_20034_.y - vec3.y;
        double d2 = p_20034_.z - vec3.z;
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        this.setXRot(Mth.wrapDegrees((float)(-(Mth.atan2(d1, d3) * 180.0F / (float)Math.PI))));
        this.setYRot(Mth.wrapDegrees((float)(Mth.atan2(d2, d0) * 180.0F / (float)Math.PI) - 90.0F));
        this.setYHeadRot(this.getYRot());
        this.xRotO = this.getXRot();
        this.yRotO = this.getYRot();
    }

    public float getPreciseBodyRotation(float p_352259_) {
        return Mth.lerp(p_352259_, this.yRotO, this.yRot);
    }

    @Deprecated // Forge: Use no parameter version instead, only for vanilla Tags
    public boolean updateFluidHeightAndDoFluidPushing(TagKey<Fluid> p_204032_, double p_204033_) {
        this.updateFluidHeightAndDoFluidPushing(true);
        if(p_204032_ == FluidTags.WATER) return this.isInFluidType(net.neoforged.neoforge.common.NeoForgeMod.WATER_TYPE.value());
        else if (p_204032_ == FluidTags.LAVA) return this.isInFluidType(net.neoforged.neoforge.common.NeoForgeMod.LAVA_TYPE.value());
        else return false;
    }

    public boolean updateFluidHeightAndDoCanPushEntityFluidPushing(boolean performFluidPushing) {
        if (performFluidPushing) {
            this.forgeFluidTypeHeight.clear();
        }
        this.updateFluidHeightAndDoFluidPushing(performFluidPushing);

        if (this.forgeFluidTypeHeight.isEmpty()) {
            return false;
        }

        for (net.neoforged.neoforge.fluids.FluidType fluidType : this.forgeFluidTypeHeight.keySet()) {
            if (fluidType.getIsWaterLike()) {
                return true;
            }
        }

        return false;
    }

    public void updateFluidHeightAndDoFluidPushing(boolean doFluidPushing) {
        if (this.touchingUnloadedChunk()) {
            return;
        } else {
            AABB aabb = this.getBoundingBox().deflate(0.001);
            int i = Mth.floor(aabb.minX);
            int j = Mth.ceil(aabb.maxX);
            int k = Mth.floor(aabb.minY);
            int l = Mth.ceil(aabb.maxY);
            int i1 = Mth.floor(aabb.minZ);
            int j1 = Mth.ceil(aabb.maxZ);
            double d0 = 0.0;
            boolean flag = this.isPushedByFluid();
            boolean flag1 = false;
            Vec3 vec3 = Vec3.ZERO;
            int k1 = 0;
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
            class InterimCalculation {
                double fluidHeight = 0.0D;
                Vec3 flowVector = Vec3.ZERO;
                int blockCount = 0;
            }
            it.unimi.dsi.fastutil.objects.Object2ObjectMap<net.neoforged.neoforge.fluids.FluidType, InterimCalculation> interimCalcs = null;

            for (int l1 = i; l1 < j; l1++) {
                for (int i2 = k; i2 < l; i2++) {
                    for (int j2 = i1; j2 < j1; j2++) {
                        blockpos$mutableblockpos.set(l1, i2, j2);
                        FluidState fluidstate = this.level().getFluidState(blockpos$mutableblockpos);
                        net.neoforged.neoforge.fluids.FluidType fluidType = fluidstate.getFluidType();
                        if (!fluidType.isAir()) {
                            double d1 = i2 + fluidstate.getHeight(this.level(), blockpos$mutableblockpos);
                            if (d1 >= aabb.minY) {
                                flag1 = true;
                                if (interimCalcs == null) {
                                    interimCalcs = new it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap<>();
                                }
                                InterimCalculation interim = interimCalcs.computeIfAbsent(fluidType, t -> new InterimCalculation());
                                interim.fluidHeight = Math.max(d1 - aabb.minY, interim.fluidHeight);
                                if (this.isPushedByFluid(fluidType)) {
                                    Vec3 vec31 = fluidstate.getFlow(this.level(), blockpos$mutableblockpos);
                                    if (interim.fluidHeight < 0.4D) {
                                        vec31 = vec31.scale(interim.fluidHeight);
                                    }

                                    interim.flowVector = interim.flowVector.add(vec31);
                                    interim.blockCount++;
                                }
                            }
                        }
                    }
                }
            }

            if(interimCalcs != null) {
            interimCalcs.forEach((fluidType, interim) -> {
            if (doFluidPushing && interim.flowVector.length() > 0.0D) {
                if (interim.blockCount > 0) {
                    interim.flowVector = interim.flowVector.scale(1.0D / (double)interim.blockCount);
                }

                if (!(this instanceof Player)) {
                    interim.flowVector = interim.flowVector.normalize();
                }

                Vec3 vec32 = this.getDeltaMovement();
                interim.flowVector = interim.flowVector.scale(this.getFluidMotionScale(fluidType));
                double d2 = 0.003;
                if (Math.abs(vec32.x) < 0.003D && Math.abs(vec32.z) < 0.003D && interim.flowVector.length() < 0.0045000000000000005D) {
                    interim.flowVector = interim.flowVector.normalize().scale(0.0045000000000000005D);
                }

                this.setDeltaMovement(this.getDeltaMovement().add(interim.flowVector));
            }

            this.setFluidTypeHeight(fluidType, interim.fluidHeight);
            });
            }
        }
    }

    public boolean touchingUnloadedChunk() {
        AABB aabb = this.getBoundingBox().inflate(1.0);
        int i = Mth.floor(aabb.minX);
        int j = Mth.ceil(aabb.maxX);
        int k = Mth.floor(aabb.minZ);
        int l = Mth.ceil(aabb.maxZ);
        return !this.level().hasChunksAt(i, k, j, l);
    }

    @Deprecated // Forge: Use getFluidTypeHeight instead
    public double getFluidHeight(TagKey<Fluid> p_204037_) {
        if (p_204037_ == FluidTags.WATER) return getFluidTypeHeight(net.neoforged.neoforge.common.NeoForgeMod.WATER_TYPE.value());
        else if (p_204037_ == FluidTags.LAVA) return getFluidTypeHeight(net.neoforged.neoforge.common.NeoForgeMod.LAVA_TYPE.value());
        return this.fluidHeight.getDouble(p_204037_);
    }

    public double getFluidJumpThreshold() {
        return this.getEyeHeight() < 0.4 ? 0.0 : 0.4;
    }

    public final float getBbWidth() {
        return this.dimensions.width();
    }

    public final float getBbHeight() {
        return this.dimensions.height();
    }

    public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity p_352110_) {
        return new ClientboundAddEntityPacket(this, p_352110_);
    }

    public EntityDimensions getDimensions(Pose p_19975_) {
        return this.type.getDimensions();
    }

    public final EntityAttachments getAttachments() {
        return this.dimensions.attachments();
    }

    @Override
    public Vec3 position() {
        return this.position;
    }

    public Vec3 trackingPosition() {
        return this.position();
    }

    @Override
    public BlockPos blockPosition() {
        return this.blockPosition;
    }

    public BlockState getInBlockState() {
        if (this.inBlockState == null) {
            this.inBlockState = this.level().getBlockState(this.blockPosition());
        }

        return this.inBlockState;
    }

    public ChunkPos chunkPosition() {
        return this.chunkPosition;
    }

    public Vec3 getDeltaMovement() {
        return this.deltaMovement;
    }

    public void setDeltaMovement(Vec3 p_20257_) {
        if (p_20257_.isFinite()) {
            this.deltaMovement = p_20257_;
        }
    }

    public void addDeltaMovement(Vec3 p_250128_) {
        if (p_250128_.isFinite()) {
            this.setDeltaMovement(this.getDeltaMovement().add(p_250128_));
        }
    }

    public void setDeltaMovement(double p_20335_, double p_20336_, double p_20337_) {
        this.setDeltaMovement(new Vec3(p_20335_, p_20336_, p_20337_));
    }

    public final int getBlockX() {
        return this.blockPosition.getX();
    }

    public final double getX() {
        return this.position.x;
    }

    public double getX(double p_20166_) {
        return this.position.x + this.getBbWidth() * p_20166_;
    }

    public double getRandomX(double p_20209_) {
        return this.getX((2.0 * this.random.nextDouble() - 1.0) * p_20209_);
    }

    public final int getBlockY() {
        return this.blockPosition.getY();
    }

    public final double getY() {
        return this.position.y;
    }

    public double getY(double p_20228_) {
        return this.position.y + this.getBbHeight() * p_20228_;
    }

    public double getRandomY() {
        return this.getY(this.random.nextDouble());
    }

    public double getEyeY() {
        return this.position.y + this.eyeHeight;
    }

    public final int getBlockZ() {
        return this.blockPosition.getZ();
    }

    public final double getZ() {
        return this.position.z;
    }

    public double getZ(double p_20247_) {
        return this.position.z + this.getBbWidth() * p_20247_;
    }

    public double getRandomZ(double p_20263_) {
        return this.getZ((2.0 * this.random.nextDouble() - 1.0) * p_20263_);
    }

    public final void setPosRaw(double p_20344_, double p_20345_, double p_20346_) {
        if (this.position.x != p_20344_ || this.position.y != p_20345_ || this.position.z != p_20346_) {
            this.position = new Vec3(p_20344_, p_20345_, p_20346_);
            int i = Mth.floor(p_20344_);
            int j = Mth.floor(p_20345_);
            int k = Mth.floor(p_20346_);
            if (i != this.blockPosition.getX() || j != this.blockPosition.getY() || k != this.blockPosition.getZ()) {
                this.blockPosition = new BlockPos(i, j, k);
                this.inBlockState = null;
                if (SectionPos.blockToSectionCoord(i) != this.chunkPosition.x || SectionPos.blockToSectionCoord(k) != this.chunkPosition.z) {
                    this.chunkPosition = new ChunkPos(this.blockPosition);
                }
            }

            this.levelCallback.onMove();
            if (!this.firstTick && this.level instanceof ServerLevel serverlevel && !this.isRemoved()) {
                if (this instanceof WaypointTransmitter waypointtransmitter && waypointtransmitter.isTransmittingWaypoint()) {
                    serverlevel.getWaypointManager().updateWaypoint(waypointtransmitter);
                }

                if (this instanceof ServerPlayer serverplayer && serverplayer.isReceivingWaypoints() && serverplayer.connection != null) {
                    serverlevel.getWaypointManager().updatePlayer(serverplayer);
                }
            }
        }
        // Neo: ensure target chunk is loaded.
        if (this.isAddedToLevel() && !this.level.isClientSide() && !this.isRemoved()) {
            this.level.getChunk((int) Math.floor(p_20344_) >> 4, (int) Math.floor(p_20346_) >> 4);
        }
    }

    public void checkDespawn() {
    }

    public Vec3[] getQuadLeashHolderOffsets() {
        return Leashable.createQuadLeashOffsets(this, 0.0, 0.5, 0.5, 0.0);
    }

    public boolean supportQuadLeashAsHolder() {
        return false;
    }

    public void notifyLeashHolder(Leashable p_418071_) {
    }

    public void notifyLeasheeRemoved(Leashable p_418306_) {
    }

    public Vec3 getRopeHoldPosition(float p_20347_) {
        return this.getPosition(p_20347_).add(0.0, this.eyeHeight * 0.7, 0.0);
    }

    public void recreateFromPacket(ClientboundAddEntityPacket p_146866_) {
        int i = p_146866_.getId();
        double d0 = p_146866_.getX();
        double d1 = p_146866_.getY();
        double d2 = p_146866_.getZ();
        this.syncPacketPositionCodec(d0, d1, d2);
        this.snapTo(d0, d1, d2, p_146866_.getYRot(), p_146866_.getXRot());
        this.setId(i);
        this.setUUID(p_146866_.getUUID());
        this.setDeltaMovement(p_146866_.getMovement());
    }

    public @Nullable ItemStack getPickResult() {
        return null;
    }

    public void setIsInPowderSnow(boolean p_146925_) {
        this.isInPowderSnow = p_146925_;
    }

    public boolean canFreeze() {
        return !this.getType().is(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES);
    }

    public boolean isFreezing() {
        return this.getTicksFrozen() > 0;
    }

    public float getYRot() {
        return this.yRot;
    }

    @Override
    public float getVisualRotationYInDegrees() {
        return this.getYRot();
    }

    public void setYRot(float p_146923_) {
        if (!Float.isFinite(p_146923_)) {
            Util.logAndPauseIfInIde("Invalid entity rotation: " + p_146923_ + ", discarding.");
        } else {
            this.yRot = p_146923_;
        }
    }

    public float getXRot() {
        return this.xRot;
    }

    public void setXRot(float p_146927_) {
        if (!Float.isFinite(p_146927_)) {
            Util.logAndPauseIfInIde("Invalid entity rotation: " + p_146927_ + ", discarding.");
        } else {
            this.xRot = Math.clamp(p_146927_ % 360.0F, -90.0F, 90.0F);
        }
    }

    public boolean canSprint() {
        return false;
    }

    public float maxUpStep() {
        return 0.0F;
    }

    public void onExplosionHit(@Nullable Entity p_326352_) {
    }

    @Override
    public final boolean isRemoved() {
        return this.removalReason != null;
    }

    public Entity.@Nullable RemovalReason getRemovalReason() {
        return this.removalReason;
    }

    @Override
    public void setRemoved(Entity.RemovalReason p_146876_) {
        if (this.removalReason == null) {
            this.removalReason = p_146876_;
        }

        if (this.removalReason.shouldDestroy()) {
            this.stopRiding();
        }

        this.getPassengers().forEach(Entity::stopRiding);
        this.levelCallback.onRemove(p_146876_);
        this.onRemoval(p_146876_);
    }

    protected void unsetRemoved() {
        this.removalReason = null;
    }

    @Override
    public void setLevelCallback(EntityInLevelCallback p_146849_) {
        this.levelCallback = p_146849_;
    }

    @Override
    public boolean shouldBeSaved() {
        if (this.removalReason != null && !this.removalReason.shouldSave()) {
            return false;
        } else {
            return this.isPassenger() ? false : !this.isVehicle() || !this.hasExactlyOnePlayerPassenger();
        }
    }

    @Override
    public boolean isAlwaysTicking() {
        return false;
    }

    public boolean mayInteract(ServerLevel p_376870_, BlockPos p_146844_) {
        return true;
    }

    /**
     * Neo: Short-lived holder of dropped item entities. Used mainly for Neo hooks and event logic.
     * <p>
     * When not null, records all item entities from {@link #spawnAtLocation(ItemStack, float)} and {@link net.minecraft.server.level.ServerPlayer#drop(ItemStack, boolean, boolean)} instead of adding them to the world.
     */
    private java.util.@Nullable Collection<ItemEntity> captureDrops = null;

    @Override
    public java.util.@Nullable Collection<ItemEntity> captureDrops() {
        return captureDrops;
    }

    @Override
    public java.util.@Nullable Collection<ItemEntity> captureDrops(java.util.@Nullable Collection<ItemEntity> value) {
        java.util.Collection<ItemEntity> ret = captureDrops;
        this.captureDrops = value;
        return ret;
    }

    // Neo: Injected ability to store arbitrary nbt onto entities in ways that allow inter-mod compat without compile-time dependencies
    private net.minecraft.nbt.@Nullable CompoundTag persistentData;

    @Override
    public net.minecraft.nbt.CompoundTag getPersistentData() {
        if (persistentData == null)
            persistentData = new net.minecraft.nbt.CompoundTag();
        return persistentData;
    }

    // Neo: Set the default behavior for trampling on Farmland
    @Override
    public boolean canTrample(ServerLevel level, BlockState state, BlockPos pos, double fallDistance) {
        return level.random.nextFloat() < fallDistance - 0.5F
             && this instanceof LivingEntity
             && (this instanceof Player || net.neoforged.neoforge.event.EventHooks.canEntityGrief(level, this))
             && this.getBbWidth() * this.getBbWidth() * this.getBbHeight() > 0.512F;
    }

    /**
     * Neo: Internal use for keeping track of entities that are tracked by a world, to
     * allow guarantees that entity position changes will force a chunk load, avoiding
     * potential issues with entity desyncing and bad chunk data.
     */
    private boolean isAddedToLevel;

    @Override
    public final boolean isAddedToLevel() { return this.isAddedToLevel; }

    @Override
    public void onAddedToLevel() { this.isAddedToLevel = true; }

    @Override
    public void onRemovedFromLevel() { this.isAddedToLevel = false; }

    // Neo: Helper method to stop an entity from being removed if already marked for removal
    @Override
    public void revive() {
        this.unsetRemoved();
    }

    // Neo: New logic for determining entity-fluid interactions. Replaces the vanilla logic that used fluids/fluid tags.
    protected Object2DoubleMap<net.neoforged.neoforge.fluids.FluidType> forgeFluidTypeHeight = new Object2DoubleArrayMap<>(net.neoforged.neoforge.fluids.FluidType.SIZE.get());
    private net.neoforged.neoforge.fluids.FluidType forgeFluidTypeOnEyes = net.neoforged.neoforge.common.NeoForgeMod.EMPTY_TYPE.value();

    protected final void setFluidTypeHeight(net.neoforged.neoforge.fluids.FluidType type, double height) {
        this.forgeFluidTypeHeight.put(type, height);
    }

    @Override
    public final double getFluidTypeHeight(net.neoforged.neoforge.fluids.FluidType type) {
        return this.forgeFluidTypeHeight.getDouble(type);
    }

    @Override
    public final boolean isInFluidType(java.util.function.BiPredicate<net.neoforged.neoforge.fluids.FluidType, Double> predicate, boolean forAllTypes) {
        if (this.forgeFluidTypeHeight.isEmpty()) {
            return false;
        }
        return forAllTypes ? this.forgeFluidTypeHeight.object2DoubleEntrySet().stream().allMatch(e -> predicate.test(e.getKey(), e.getDoubleValue()))
                  : this.forgeFluidTypeHeight.object2DoubleEntrySet().stream().anyMatch(e -> predicate.test(e.getKey(), e.getDoubleValue()));
    }

    @Override
    public final boolean isInFluidType() {
        return this.forgeFluidTypeHeight.size() > 0;
    }

    @Override
    public final net.neoforged.neoforge.fluids.FluidType getEyeInFluidType() {
        return forgeFluidTypeOnEyes;
    }

    @Override
    public net.neoforged.neoforge.fluids.FluidType getMaxHeightFluidType() {
        if (this.forgeFluidTypeHeight.isEmpty()) {
            return net.neoforged.neoforge.common.NeoForgeMod.EMPTY_TYPE.value();
        }
        return this.forgeFluidTypeHeight.object2DoubleEntrySet().stream().max(java.util.Comparator.comparingDouble(Object2DoubleMap.Entry::getDoubleValue)).map(Object2DoubleMap.Entry::getKey).orElseGet(net.neoforged.neoforge.common.NeoForgeMod.EMPTY_TYPE::value);
    }

    // Neo: Hookup Attachment data setting
    @Override
    @Nullable
    public final <T> T setData(net.neoforged.neoforge.attachment.AttachmentType<T> type, T data) {
        // Entities are always saved, no setChanged() call is necessary.
        return super.setData(type, data);
    }

    @Override
    public final void syncData(net.neoforged.neoforge.attachment.AttachmentType<?> type) {
        net.neoforged.neoforge.attachment.AttachmentSync.syncEntityUpdate(this, type);
    }

    // Neo: Hookup Capabilities getters to entities
    @Nullable
    public final <T, C extends @org.jspecify.annotations.Nullable Object> T getCapability(net.neoforged.neoforge.capabilities.EntityCapability<T, C> capability, C context) {
        return capability.getCapability(this, context);
    }

    @Nullable
    public final <T> T getCapability(net.neoforged.neoforge.capabilities.EntityCapability<T, @org.jspecify.annotations.Nullable Void> capability) {
        return capability.getCapability(this, null);
    }

    public boolean isFlyingVehicle() {
        return false;
    }

    @Override
    public Level level() {
        return this.level;
    }

    protected void setLevel(Level p_285201_) {
        this.level = p_285201_;
    }

    public DamageSources damageSources() {
        return this.level().damageSources();
    }

    public RegistryAccess registryAccess() {
        return this.level().registryAccess();
    }

    protected void lerpPositionAndRotationStep(int p_294824_, double p_296038_, double p_294518_, double p_294658_, double p_294681_, double p_295540_) {
        double d0 = 1.0 / p_294824_;
        double d1 = Mth.lerp(d0, this.getX(), p_296038_);
        double d2 = Mth.lerp(d0, this.getY(), p_294518_);
        double d3 = Mth.lerp(d0, this.getZ(), p_294658_);
        float f = (float)Mth.rotLerp(d0, (double)this.getYRot(), p_294681_);
        float f1 = (float)Mth.lerp(d0, (double)this.getXRot(), p_295540_);
        this.setPos(d1, d2, d3);
        this.setRot(f, f1);
    }

    public RandomSource getRandom() {
        return this.random;
    }

    public Vec3 getKnownMovement() {
        return this.getControllingPassenger() instanceof Player player && this.isAlive() ? player.getKnownMovement() : this.getDeltaMovement();
    }

    public Vec3 getKnownSpeed() {
        return this.getControllingPassenger() instanceof Player player && this.isAlive() ? player.getKnownSpeed() : this.lastKnownSpeed;
    }

    public @Nullable ItemStack getWeaponItem() {
        return null;
    }

    public Optional<ResourceKey<LootTable>> getLootTable() {
        return this.type.getDefaultLootTable();
    }

    protected void applyImplicitComponents(DataComponentGetter p_397589_) {
        this.applyImplicitComponentIfPresent(p_397589_, DataComponents.CUSTOM_NAME);
        this.applyImplicitComponentIfPresent(p_397589_, DataComponents.CUSTOM_DATA);
    }

    public final void applyComponentsFromItemStack(ItemStack p_397915_) {
        this.applyImplicitComponents(p_397915_.getComponents());
    }

    @Override
    public <T> @Nullable T get(DataComponentType<? extends T> p_397010_) {
        if (p_397010_ == DataComponents.CUSTOM_NAME) {
            return castComponentValue((DataComponentType<T>)p_397010_, this.getCustomName());
        } else {
            return p_397010_ == DataComponents.CUSTOM_DATA ? castComponentValue((DataComponentType<T>)p_397010_, this.customData) : null;
        }
    }

    @Contract("_,!null->!null;_,_->_")
    protected static <T> @Nullable T castComponentValue(DataComponentType<T> p_397295_, @Nullable Object p_397934_) {
        return (T)p_397934_;
    }

    public <T> void setComponent(DataComponentType<T> p_397096_, T p_397233_) {
        this.applyImplicitComponent(p_397096_, p_397233_);
    }

    protected <T> boolean applyImplicitComponent(DataComponentType<T> p_397325_, T p_397189_) {
        if (p_397325_ == DataComponents.CUSTOM_NAME) {
            this.setCustomName(castComponentValue(DataComponents.CUSTOM_NAME, p_397189_));
            return true;
        } else if (p_397325_ == DataComponents.CUSTOM_DATA) {
            this.customData = castComponentValue(DataComponents.CUSTOM_DATA, p_397189_);
            return true;
        } else {
            return false;
        }
    }

    protected <T> boolean applyImplicitComponentIfPresent(DataComponentGetter p_397023_, DataComponentType<T> p_397949_) {
        T t = p_397023_.get(p_397949_);
        return t != null ? this.applyImplicitComponent(p_397949_, t) : false;
    }

    public ProblemReporter.PathElement problemPath() {
        return new Entity.EntityPathElement(this);
    }

    @Override
    public void registerDebugValues(ServerLevel p_449077_, DebugValueSource.Registration p_449629_) {
    }

    record EntityPathElement(Entity entity) implements ProblemReporter.PathElement {
        @Override
        public String get() {
            return this.entity.toString();
        }
    }

    @FunctionalInterface
    public interface MoveFunction {
        void accept(Entity p_20373_, double p_20374_, double p_20375_, double p_20376_);
    }

    record Movement(Vec3 from, Vec3 to, Optional<Vec3> axisDependentOriginalMovement) {
        public Movement(Vec3 p_449233_, Vec3 p_449886_, Vec3 p_449963_) {
            this(p_449233_, p_449886_, Optional.of(p_449963_));
        }

        public Movement(Vec3 p_449348_, Vec3 p_449385_) {
            this(p_449348_, p_449385_, Optional.empty());
        }
    }

    public static enum MovementEmission {
        NONE(false, false),
        SOUNDS(true, false),
        EVENTS(false, true),
        ALL(true, true);

        final boolean sounds;
        final boolean events;

        private MovementEmission(boolean p_146942_, boolean p_146943_) {
            this.sounds = p_146942_;
            this.events = p_146943_;
        }

        public boolean emitsAnything() {
            return this.events || this.sounds;
        }

        public boolean emitsEvents() {
            return this.events;
        }

        public boolean emitsSounds() {
            return this.sounds;
        }
    }

    public static enum RemovalReason {
        KILLED(true, false),
        DISCARDED(true, false),
        UNLOADED_TO_CHUNK(false, true),
        UNLOADED_WITH_PLAYER(false, false),
        CHANGED_DIMENSION(false, false);

        private final boolean destroy;
        private final boolean save;

        private RemovalReason(boolean p_146963_, boolean p_146964_) {
            this.destroy = p_146963_;
            this.save = p_146964_;
        }

        public boolean shouldDestroy() {
            return this.destroy;
        }

        public boolean shouldSave() {
            return this.save;
        }
    }
}
