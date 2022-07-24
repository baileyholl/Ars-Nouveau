package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.block.tile.MageBlockTile;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.CrashReportCategory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class EnchantedFallingBlock extends ColoredProjectile {
    public static final EntityDataAccessor<Boolean> SHOULD_COLOR = SynchedEntityData.defineId(EnchantedFallingBlock.class, EntityDataSerializers.BOOLEAN);

    private static final Logger LOGGER = LogUtils.getLogger();
    public BlockState blockState = Blocks.SAND.defaultBlockState();
    public int time;
    public boolean dropItem = true;
    public boolean cancelDrop;
    private boolean hurtEntities;
    private int fallDamageMax = 40;
    private float fallDamagePerDistance;
    public int knockback = 2;
    @Nullable
    public CompoundTag blockData;
    public SpellContext context;
    public float baseDamage;

    protected static final EntityDataAccessor<BlockPos> DATA_START_POS = SynchedEntityData.defineId(EnchantedFallingBlock.class, EntityDataSerializers.BLOCK_POS);
    private IntOpenHashSet piercingIgnoreEntityIds = new IntOpenHashSet(5);

    public EnchantedFallingBlock(EntityType<? extends ColoredProjectile> p_31950_, Level p_31951_) {
        super(p_31950_, p_31951_);
    }

    public EnchantedFallingBlock(Level world, double v, double y, double v1, BlockState blockState) {
        this(ModEntities.ENCHANTED_FALLING_BLOCK.get(), world);
        this.blockState = blockState;
        this.blocksBuilding = true;
        this.setPos(v, y, v1);
        this.setDeltaMovement(Vec3.ZERO);
        this.xo = v;
        this.yo = y;
        this.zo = v1;
        this.setStartPos(this.blockPosition());
    }

    public EnchantedFallingBlock(Level world, BlockPos pos, BlockState blockState) {
        this(world, pos.getX(), pos.getY(), pos.getZ(), blockState);
    }

    public static @Nullable EnchantedFallingBlock fall(Level level, BlockPos pos, LivingEntity owner, SpellContext context, SpellResolver resolver, SpellStats spellStats) {
        if((level.getBlockEntity(pos) != null &&
                !(level.getBlockEntity(pos) instanceof MageBlockTile))){
            return null;
        }
        if(!BlockUtil.canBlockBeHarvested(spellStats, level, pos) || !BlockUtil.destroyRespectsClaim(owner, level, pos)) {
            return null;
        }
        BlockState blockState = level.getBlockState(pos);
        EnchantedFallingBlock fallingblockentity = new EnchantedFallingBlock(level, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, blockState.hasProperty(BlockStateProperties.WATERLOGGED) ? blockState.setValue(BlockStateProperties.WATERLOGGED, Boolean.FALSE) : blockState);
        level.addFreshEntity(fallingblockentity);
        fallingblockentity.setOwner(owner);
        fallingblockentity.context = context;
        fallingblockentity.baseDamage = (float) (9.0f + spellStats.getDamageModifier());
        if (level.getBlockEntity(pos) instanceof MageBlockTile tile) {
            fallingblockentity.setColor(tile.color);
            fallingblockentity.getEntityData().set(SHOULD_COLOR, true);
        }
        if (resolver.hasFocus(new ItemStack(ItemsRegistry.SHAPERS_FOCUS.get()))) {
            fallingblockentity.hurtEntities = true;
        }

        level.setBlock(pos, blockState.getFluidState().createLegacyBlock(), 3);
        return fallingblockentity;
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.ENCHANTED_FALLING_BLOCK.get();
    }

    @Override
    public boolean canCollideWith(Entity pEntity) {
        return super.canCollideWith(pEntity) && !(pEntity instanceof FallingBlockEntity) && !(pEntity instanceof EnchantedFallingBlock) && pEntity != getOwner();
    }

    @Override
    protected boolean canHitEntity(Entity p_37250_) {
        return super.canHitEntity(p_37250_) && p_37250_ != getOwner() && !this.piercingIgnoreEntityIds.contains(p_37250_.getId());
    }

    @Override
    public void tick() {
        super.tick();
        if (this.blockState.isAir()) {
            this.discard();
            return;
        }
        Block block = this.blockState.getBlock();
        ++this.time;
        if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.04D, 0.0D));
        }
        EntityHitResult hitEntity = findHitEntity(this.position, this.position.add(this.getDeltaMovement()));
        if (hitEntity != null) {
            onHitEntity(hitEntity);
        }
        this.move(MoverType.SELF, this.getDeltaMovement());
        if (!this.level.isClientSide) {
            BlockPos blockpos = this.blockPosition();
            boolean isConcrete = this.blockState.getBlock() instanceof ConcretePowderBlock;
            boolean isConcreteInWater = isConcrete && this.level.getFluidState(blockpos).is(FluidTags.WATER);
            double d0 = this.getDeltaMovement().lengthSqr();
            if (isConcrete && d0 > 1.0D) { // if we are concrete powder
                BlockHitResult blockhitresult = this.level.clip(new ClipContext(new Vec3(this.xo, this.yo, this.zo), this.position(), ClipContext.Block.COLLIDER, ClipContext.Fluid.SOURCE_ONLY, this));
                if (blockhitresult.getType() != HitResult.Type.MISS && this.level.getFluidState(blockhitresult.getBlockPos()).is(FluidTags.WATER)) {
                    blockpos = blockhitresult.getBlockPos();
                    isConcreteInWater = true;
                }
            }

            if (!this.onGround && !isConcreteInWater) {
                if (!this.level.isClientSide && (this.time > 100 && (blockpos.getY() <= this.level.getMinBuildHeight() || blockpos.getY() > this.level.getMaxBuildHeight()) || this.time > 600)) {
                    if (this.dropItem && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                        this.spawnAtLocation(block);
                    }

                    this.discard();
                }
            } else { // on ground
                BlockState blockstate = this.level.getBlockState(blockpos);
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.7D, -0.5D, 0.7D));
                if (!blockstate.is(Blocks.MOVING_PISTON)) {
                    if (!this.cancelDrop) {
                        boolean flag2 = blockstate.canBeReplaced(new DirectionalPlaceContext(this.level, blockpos, Direction.DOWN, ItemStack.EMPTY, Direction.UP));
                        boolean flag3 = FallingBlock.isFree(this.level.getBlockState(blockpos.below())) && (!isConcrete || !isConcreteInWater);
                        boolean flag4 = this.blockState.canSurvive(this.level, blockpos) && !flag3;
                        if (flag2 && flag4) {
                            if (this.blockState.hasProperty(BlockStateProperties.WATERLOGGED) && this.level.getFluidState(blockpos).getType() == Fluids.WATER) {
                                this.blockState = this.blockState.setValue(BlockStateProperties.WATERLOGGED, Boolean.TRUE);
                            }

                            if (this.level.setBlock(blockpos, this.blockState, 3)) {
                                ((ServerLevel) this.level).getChunkSource().chunkMap.broadcast(this, new ClientboundBlockUpdatePacket(blockpos, this.level.getBlockState(blockpos)));
                                this.discard();
                                if (block instanceof Fallable fallable) {
                                    fallable.onLand(this.level, blockpos, this.blockState, blockstate, new FallingBlockEntity(level, this.getX(), this.getY(), this.getZ(), this.blockState));
                                }

                                if (this.blockData != null && this.blockState.hasBlockEntity()) {
                                    BlockEntity blockentity = this.level.getBlockEntity(blockpos);
                                    if (blockentity != null) {
                                        CompoundTag compoundtag = blockentity.saveWithoutMetadata();

                                        for (String s : this.blockData.getAllKeys()) {
                                            compoundtag.put(s, this.blockData.get(s).copy());
                                        }

                                        try {
                                            blockentity.load(compoundtag);
                                        } catch (Exception exception) {
                                        }

                                        blockentity.setChanged();
                                    }
                                }
                            } else if (this.dropItem && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                                this.discard();
                                this.callOnBrokenAfterFall(block, blockpos);
                                this.spawnAtLocation(block);
                            }
                        } else {
                            this.discard();
                            if (this.dropItem && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                                this.callOnBrokenAfterFall(block, blockpos);
                                this.spawnAtLocation(block);
                            }
                        }
                    } else {
                        this.discard();
                        this.callOnBrokenAfterFall(block, blockpos);
                    }
                }
            }
        }
        this.setDeltaMovement(this.getDeltaMovement().scale(0.98D));
    }

    public float getStateDamageBonus() {
        float destroySpeed = 1.0f;
        try {
            destroySpeed = this.blockState.getDestroySpeed(level, blockPosition());
        } catch (Exception e) {
            // Passing unexpected values here, catch any errors
        }

        return destroySpeed;
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        if (!hurtEntities)
            return;
        super.onHitEntity(pResult);
        Entity entity = pResult.getEntity();
        float f = (float) this.getDeltaMovement().length();
        int i = Mth.ceil(Mth.clamp((Math.min(f, 2.5) * this.baseDamage) + getStateDamageBonus(), 0.0D, 2.147483647E9D));
        this.piercingIgnoreEntityIds.add(entity.getId());

        Entity owner = this.getOwner();
        DamageSource damagesource;
        if (owner == null) {
            damagesource = new IndirectEntityDamageSource("an_enchantedBlock", this, owner);
        } else {
            damagesource = new IndirectEntityDamageSource("an_enchantedBlock", this, owner);
            if (owner instanceof LivingEntity livingOwner) {
                livingOwner.setLastHurtMob(entity);
            }
        }

        boolean isEnderman = entity.getType() == EntityType.ENDERMAN;
        int k = entity.getRemainingFireTicks();
        if (this.isOnFire() && !isEnderman) {
            entity.setSecondsOnFire(5);
        }

        if (entity.hurt(damagesource, (float) i)) {
            if (isEnderman) {
                return;
            }

            if (entity instanceof LivingEntity livingentity) {

                if (this.knockback > 0) {
                    Vec3 vec3 = this.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D).normalize().scale((double) this.knockback * 0.6D);
                    if (vec3.lengthSqr() > 0.0D) {
                        livingentity.push(vec3.x, 0.1D, vec3.z);
                    }
                }

                if (!this.level.isClientSide && owner instanceof LivingEntity) {
                    EnchantmentHelper.doPostHurtEffects(livingentity, owner);
                    EnchantmentHelper.doPostDamageEffects((LivingEntity) owner, livingentity);
                }

                this.doPostHurtEffects(livingentity);
            }

            this.playSound(this.blockState.getSoundType().getBreakSound(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));

        } else {
            entity.setRemainingFireTicks(k);
        }
    }

    private void doPostHurtEffects(LivingEntity livingentity) {
    }

    public void fillCrashReportCategory(CrashReportCategory pCategory) {
        super.fillCrashReportCategory(pCategory);
        pCategory.setDetail("Immitating BlockState", this.blockState.toString());
    }

    public BlockState getBlockState() {
        return this.blockState;
    }

    public boolean onlyOpCanSetNbt() {
        return true;
    }

    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this, Block.getId(this.getBlockState()));
    }

    public void recreateFromPacket(ClientboundAddEntityPacket pPacket) {
        super.recreateFromPacket(pPacket);
        this.blockState = Block.stateById(pPacket.getData());
        this.blocksBuilding = true;
        double d0 = pPacket.getX();
        double d1 = pPacket.getY();
        double d2 = pPacket.getZ();
        this.setPos(d0, d1, d2);
        this.setStartPos(this.blockPosition());
    }

    public void callOnBrokenAfterFall(Block p_149651_, BlockPos p_149652_) {
        if (p_149651_ instanceof Fallable) {
            ((Fallable) p_149651_).onBrokenAfterFall(this.level, p_149652_, new FallingBlockEntity(level, this.getX(), this.getY(), this.getZ(), this.blockState));
        }

    }

    public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
        if (!this.hurtEntities) {
            return false;
        } else {
            int i = Mth.ceil(pFallDistance - 1.0F);
            if (i < 0) {
                return false;
            } else {
                Predicate<Entity> predicate;
                DamageSource damagesource;
                if (this.blockState.getBlock() instanceof Fallable fallable) {
                    predicate = fallable.getHurtsEntitySelector();
                    damagesource = fallable.getFallDamageSource();
                } else {
                    predicate = EntitySelector.NO_SPECTATORS;
                    damagesource = DamageSource.FALLING_BLOCK;
                }

                float f = (float) Math.min(Mth.floor((float) i * this.fallDamagePerDistance), this.fallDamageMax);
                this.level.getEntities(this, this.getBoundingBox(), predicate).forEach(p_149649_ -> p_149649_.hurt(damagesource, f));
                boolean flag = this.blockState.is(BlockTags.ANVIL);
                if (flag && f > 0.0F && this.random.nextFloat() < 0.05F + (float) i * 0.05F) {
                    BlockState blockstate = AnvilBlock.damage(this.blockState);
                    if (blockstate == null) {
                        this.cancelDrop = true;
                    } else {
                        this.blockState = blockstate;
                    }
                }

                return false;
            }
        }
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.put("BlockState", NbtUtils.writeBlockState(this.blockState));
        pCompound.putInt("Time", this.time);
        pCompound.putBoolean("DropItem", this.dropItem);
        pCompound.putBoolean("HurtEntities", this.hurtEntities);
        pCompound.putFloat("FallHurtAmount", this.fallDamagePerDistance);
        pCompound.putInt("FallHurtMax", this.fallDamageMax);
        if (this.blockData != null) {
            pCompound.put("TileEntityData", this.blockData);
        }
        pCompound.putBoolean("shouldColor", entityData.get(SHOULD_COLOR));
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        entityData.set(SHOULD_COLOR, compound.getBoolean("shouldColor"));
    }


    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.blockState = NbtUtils.readBlockState(pCompound.getCompound("BlockState"));
        this.time = pCompound.getInt("Time");
        if (pCompound.contains("HurtEntities", 99)) {
            this.hurtEntities = pCompound.getBoolean("HurtEntities");
            this.fallDamagePerDistance = pCompound.getFloat("FallHurtAmount");
            this.fallDamageMax = pCompound.getInt("FallHurtMax");
        } else if (this.blockState.is(BlockTags.ANVIL)) {
            this.hurtEntities = true;
        }

        if (pCompound.contains("DropItem", 99)) {
            this.dropItem = pCompound.getBoolean("DropItem");
        }

        if (pCompound.contains("TileEntityData", 10)) {
            this.blockData = pCompound.getCompound("TileEntityData");
        }

        if (this.blockState.isAir()) {
            this.blockState = Blocks.SAND.defaultBlockState();
        }

    }

    public void setHurtsEntities(float p_149657_, int p_149658_) {
        this.hurtEntities = true;
        this.fallDamagePerDistance = p_149657_;
        this.fallDamageMax = p_149658_;
    }

    /**
     * Returns true if it's possible to attack this entity with an item.
     */
    public boolean isAttackable() {
        return false;
    }

    public void setStartPos(BlockPos pOrigin) {
        this.entityData.set(DATA_START_POS, pOrigin);
    }

    public BlockPos getStartPos() {
        return this.entityData.get(DATA_START_POS);
    }

    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.NONE;
    }

    public void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_START_POS, BlockPos.ZERO);
        this.entityData.define(SHOULD_COLOR, false);
    }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    public boolean isPickable() {
        return !this.isRemoved();
    }


}
