package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.tile.DrygmyTile;
import com.hollingsworth.arsnouveau.common.entity.goal.UntamedFindItemGoal;
import com.hollingsworth.arsnouveau.common.entity.goal.drygmy.CollectEssenceGoal;
import com.hollingsworth.arsnouveau.common.entity.goal.sylph.FollowMobGoalBackoff;
import com.hollingsworth.arsnouveau.common.entity.goal.sylph.FollowPlayerGoal;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketANEffect;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayerFactory;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class EntityDrygmy extends CreatureEntity implements IAnimatable, ITooltipProvider, IDispellable {

    public static final DataParameter<Boolean> CHANNELING = EntityDataManager.defineId(EntityDrygmy.class, DataSerializers.BOOLEAN);
    public static final DataParameter<Boolean> TAMED = EntityDataManager.defineId(EntityDrygmy.class, DataSerializers.BOOLEAN);
    public static final DataParameter<Boolean> BEING_TAMED = EntityDataManager.defineId(EntityDrygmy.class, DataSerializers.BOOLEAN);
    public static final DataParameter<Boolean> HOLDING_ESSENCE = EntityDataManager.defineId(EntityDrygmy.class, DataSerializers.BOOLEAN);
    public static final DataParameter<Integer> CHANNELING_ENTITY = EntityDataManager.defineId(EntityDrygmy.class, DataSerializers.INT);

    public int channelCooldown;
    private boolean setBehaviors;
    public BlockPos homePos;
    public int tamingTime;
    @Override
    protected int getExperienceReward(PlayerEntity player) {
        return 0;
    }

    public EntityDrygmy(EntityType<? extends CreatureEntity> p_i48575_1_, World p_i48575_2_) {
        super(p_i48575_1_, p_i48575_2_);
        addGoalsAfterConstructor();
    }

    public EntityDrygmy(World world, boolean tamed){
        super(ModEntities.ENTITY_DRYGMY, world);
        setTamed(tamed);
        addGoalsAfterConstructor();
    }

    public @Nullable DrygmyTile getHome(){
        if(homePos == null || !(level.getBlockEntity(homePos) instanceof DrygmyTile))
            return null;
        return (DrygmyTile) level.getBlockEntity(homePos);
    }

    @Override
    public void die(DamageSource source) {
        if(!level.isClientSide && isTamed()){
            ItemStack stack = new ItemStack(ItemsRegistry.DRYGMY_CHARM);
            level.addFreshEntity(new ItemEntity(level, getX(), getY(), getZ(), stack));
        }
        super.die(source);
    }

    @Override
    public void tick() {
        super.tick();
        if(!level.isClientSide && channelCooldown > 0){
            channelCooldown--;
        }

        if(!level.isClientSide && level.getGameTime() % 60 == 0 && isTamed() && homePos != null && !(level.getBlockEntity(homePos) instanceof DrygmyTile)) {
            this.hurt(DamageSource.playerAttack(FakePlayerFactory.getMinecraft((ServerWorld) level)), 99);
            return;
        }

        if(level.isClientSide && isChanneling() && getChannelEntity() != -1){
            Entity entity = level.getEntity(getChannelEntity());
            if(entity == null || entity.removed)
                return;
            Vector3d vec = entity.position;
            level.addParticle(GlowParticleData.createData(new ParticleColor(50, 255, 20)),
                    (float) (vec.x) - Math.sin((ClientInfo.ticksInGame ) / 8D) ,
                    (float) (vec.y) + Math.sin(ClientInfo.ticksInGame/5d)/8D + 0.5  ,
                    (float) (vec.z) - Math.cos((ClientInfo.ticksInGame) / 8D) ,
                    0, 0, 0);
        }
        if (!isTamed() && !this.entityData.get(BEING_TAMED) && level.getGameTime() % 40 == 0 ) {
            for (ItemEntity itementity : this.level.getEntitiesOfClass(ItemEntity.class, this.getBoundingBox().inflate(1))) {
                pickUpItem(itementity);
            }
        }
        if (!isTamed() && this.entityData.get(BEING_TAMED)) {

            tamingTime++;
            if (tamingTime % 20 == 0 && !level.isClientSide())
                Networking.sendToNearby(level, this, new PacketANEffect(PacketANEffect.EffectType.TIMED_HELIX, blockPosition()));

            if (tamingTime > 60 && !level.isClientSide) {
                ItemStack stack = new ItemStack(ItemsRegistry.DRYGMY_SHARD, 1 + level.random.nextInt(2));
                level.addFreshEntity(new ItemEntity(level, getX(), getY() + 0.5, getZ(), stack));
                this.remove(false);
                level.playSound(null, getX(), getY(), getZ(), SoundEvents.ILLUSIONER_MIRROR_MOVE, SoundCategory.NEUTRAL, 1f, 1f);
            }
        }
    }

    public boolean removeWhenFarAway(double p_213397_1_) {
        return false;
    }

    private PlayState animationPredicate(AnimationEvent event) {
        if(isChanneling() || this.entityData.get(BEING_TAMED)){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("channel"));
            return PlayState.CONTINUE;
        }
        if (event.isMoving()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("run"));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    // Cannot add conditional goals in RegisterGoals as it is final and called during the MobEntity super.
    protected void addGoalsAfterConstructor(){
        if(this.level.isClientSide())
            return;

        for(PrioritizedGoal goal : getGoals()){
            this.goalSelector.addGoal(goal.getPriority(), goal.getGoal());
        }
    }

    public List<PrioritizedGoal> getGoals(){
        return this.entityData.get(TAMED) ? getTamedGoals() : getUntamedGoals();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CHANNELING, false);
        this.entityData.define(TAMED, false);
        this.entityData.define(HOLDING_ESSENCE, false);
        this.entityData.define(CHANNELING_ENTITY, -1);
        this.entityData.define(BEING_TAMED, false);
    }

    public boolean holdingEssence(){
        return this.entityData.get(HOLDING_ESSENCE);
    }

    public void setHoldingEssence(boolean holdingEssence){
        this.entityData.set(HOLDING_ESSENCE, holdingEssence);
    }

    public boolean isTamed(){
        return this.entityData.get(TAMED);
    }

    public void setTamed(boolean tamed){
        this.entityData.set(TAMED, tamed);
    }

    public boolean isChanneling(){
        return this.entityData.get(CHANNELING);
    }

    public void setChanneling(boolean channeling){
        this.entityData.set(CHANNELING,channeling);
    }

    public int getChannelEntity(){
        return this.entityData.get(CHANNELING_ENTITY);
    }

    public void setChannelingEntity(int entityID){
        this.entityData.set(CHANNELING_ENTITY, entityID);
    }

    @Override
    protected void registerGoals() { }
    public List<PrioritizedGoal> getTamedGoals(){
        List<PrioritizedGoal> list = new ArrayList<>();
        list.add(new PrioritizedGoal(3, new LookRandomlyGoal(this)));
        list.add(new PrioritizedGoal(2, new CollectEssenceGoal(this)));
        list.add(new PrioritizedGoal(0, new SwimGoal(this)));
        return list;
    }

    public List<PrioritizedGoal> getUntamedGoals(){
        List<PrioritizedGoal> list = new ArrayList<>();
        list.add(new PrioritizedGoal(3, new FollowMobGoalBackoff(this, 1.0D, 3.0F, 7.0F, 0.5f)));
        list.add(new PrioritizedGoal(5, new FollowPlayerGoal(this, 1.0D, 3.0F, 7.0F)));
        list.add(new PrioritizedGoal(2, new LookRandomlyGoal(this)));
        list.add(new PrioritizedGoal(2, new WaterAvoidingRandomWalkingGoal(this, 1.0D)));
        list.add(new PrioritizedGoal(0, new SwimGoal(this)));
        list.add(new PrioritizedGoal(1, new UntamedFindItemGoal(this,
                () -> !this.isTamed() && !this.entityData.get(BEING_TAMED)
                ,(itemEntity -> !itemEntity.hasPickUpDelay() && itemEntity.isAlive() && itemEntity.getItem().getItem() == ItemsRegistry.WILDEN_HORN))));
        return list;
    }
    @Override
    public List<String> getTooltip() {
        return null;
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<EntityDrygmy>(this, "walkController", 20, this::animationPredicate));
        animationData.addAnimationController(new AnimationController<EntityDrygmy>(this, "idleController", 20, this::idlePredicate));
    }

    @Override
    protected void pickUpItem(ItemEntity itemEntity) {
        if(!isTamed() && !entityData.get(BEING_TAMED) && itemEntity.getItem().getItem() == ItemsRegistry.WILDEN_HORN) {
            entityData.set(BEING_TAMED, true);
            itemEntity.getItem().shrink(1);
            this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ITEM_PICKUP, this.getSoundSource(), 1.0F, 1.0F);
        }
    }

    @Override
    public boolean onDispel(@Nullable LivingEntity caster) {
        if (this.removed)
            return false;

        if (!level.isClientSide && isTamed()) {
            ItemStack stack = new ItemStack(ItemsRegistry.DRYGMY_SHARD);
            level.addFreshEntity(new ItemEntity(level, getX(), getY(), getZ(), stack));
            ParticleUtil.spawnPoof((ServerWorld) level, blockPosition());
            this.remove();
        }
        return this.isTamed();
    }

    private PlayState idlePredicate(AnimationEvent event) {
        return PlayState.CONTINUE;
    }

    AnimationFactory factory = new AnimationFactory(this);

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT tag) {
        super.addAdditionalSaveData(tag);
        NBTUtil.storeBlockPos(tag, "home", homePos);
        tag.putBoolean("tamed", this.entityData.get(TAMED));
        tag.putInt("cooldown", channelCooldown);
        tag.putInt("taming", tamingTime);
        tag.putBoolean("beingTamed", this.entityData.get(BEING_TAMED));
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT tag) {
        super.readAdditionalSaveData(tag);
        if(NBTUtil.hasBlockPos(tag, "home"))
            this.homePos = NBTUtil.getBlockPos(tag, "home");
        setTamed(tag.getBoolean("tamed"));
        if(!setBehaviors){
            tryResetGoals();
            setBehaviors = true;
        }
        channelCooldown = tag.getInt("cooldown");
        this.tamingTime = tag.getInt("taming");
        entityData.set(BEING_TAMED, tag.getBoolean("beingTamed"));
    }

    // A workaround for goals not registering correctly for a dynamic variable on reload as read() is called after constructor.
    public void tryResetGoals(){
        this.goalSelector.availableGoals = new LinkedHashSet<>();
        this.addGoalsAfterConstructor();
    }
}
