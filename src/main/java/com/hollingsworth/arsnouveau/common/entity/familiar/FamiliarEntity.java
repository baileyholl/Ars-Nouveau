package com.hollingsworth.arsnouveau.common.entity.familiar;

import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.api.event.MaxManaCalcEvent;
import com.hollingsworth.arsnouveau.api.familiar.IFamiliar;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.entity.goal.familiar.FamOwnerHurtByTargetGoal;
import com.hollingsworth.arsnouveau.common.entity.goal.familiar.FamOwnerHurtTargetGoal;
import com.hollingsworth.arsnouveau.common.entity.goal.familiar.FamiliarFollowGoal;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.NetworkHooks;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class FamiliarEntity extends CreatureEntity implements IAnimatable, IFamiliar, IDispellable {

    private static final DataParameter<Optional<UUID>> OWNER_UUID = EntityDataManager.defineId(FamiliarEntity.class, DataSerializers.OPTIONAL_UUID);

    public FamiliarEntity(EntityType<? extends CreatureEntity> p_i48575_1_, World p_i48575_2_) {
        super(p_i48575_1_, p_i48575_2_);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void remove() {
        super.remove();
    }

    @Override
    public boolean removeWhenFarAway(double p_213397_1_) {
        return false;
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();
    }

    @SubscribeEvent
    public void maxManaCalc(MaxManaCalcEvent event) {
        if(!isAlive())
            return;
        if(getOwner() != null && getOwner().equals(event.getEntity())){
            event.setMax((int) (event.getMax() -  event.getMax() * getManaReserveModifier()));
        }
    }

    public double getManaReserveModifier(){
        return 0.15;
    }

    @Override
    public void tick() {
        super.tick();
        if(level.getGameTime() % 20 ==0 && !level.isClientSide){
            if(((ServerWorld)level).getEntity(getOwnerID()) == null){
                this.remove();
            }
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if(source == DamageSource.DROWN || source == DamageSource.IN_WALL || source == DamageSource.FLY_INTO_WALL || source == DamageSource.FALL)
            return false;
        if(source.getEntity() == null)
            return false;
        return super.hurt(source, amount);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(3, new FamiliarFollowGoal(this, 2, 6, 4));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
        this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 8.0f));
        this.targetSelector.addGoal(1, new FamOwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new FamOwnerHurtTargetGoal(this));
    }

    public PlayState walkPredicate(AnimationEvent event) {
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "walkController", 1, this::walkPredicate));
    }


    public boolean canTeleport(){
        return getOwner() != null && getOwner().isOnGround();
    }

    public @Nullable LivingEntity getOwner(){
        if(level.isClientSide)
            return null;

        return (LivingEntity) ((ServerWorld)level).getEntity(getOwnerID());
    }

    public AnimationFactory factory = new AnimationFactory(this);

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(OWNER_UUID, Optional.empty());
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT tag) {
        super.addAdditionalSaveData(tag);
        tag.putUUID("ownerID", getOwnerID());
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT tag) {
        super.readAdditionalSaveData(tag);
        setOwnerID(tag.getUUID("ownerID"));
    }

    public UUID getOwnerID() {
        return this.getEntityData().get(OWNER_UUID).get();
    }

    public void setOwnerID(UUID uuid) {
        this.getEntityData().set(OWNER_UUID, Optional.of(uuid));
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public static AttributeModifierMap.MutableAttribute attributes() {
        return MobEntity.createMobAttributes().add(Attributes.MAX_HEALTH, 40d)
                .add(Attributes.MOVEMENT_SPEED, 0.2d).add(Attributes.FLYING_SPEED, Attributes.FLYING_SPEED.getDefaultValue())
                .add(Attributes.FOLLOW_RANGE, 16D);
    }

    @Override
    public boolean canTrample(BlockState state, BlockPos pos, float fallDistance) {
        return false;
    }

    @Override
    protected boolean canRide(Entity p_184228_1_) {
        return false;
    }

    @Override
    public boolean onDispel(@Nullable LivingEntity caster) {
        if(!level.isClientSide && getOwner() != null && getOwner().equals(caster)){
            this.remove();
            ParticleUtil.spawnPoof((ServerWorld) level, blockPosition());
            return true;
        }
        return false;
    }
}