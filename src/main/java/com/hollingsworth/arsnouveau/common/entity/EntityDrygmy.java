package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.client.IVariantColorProvider;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.api.util.SummonUtil;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.tile.DrygmyTile;
import com.hollingsworth.arsnouveau.common.compat.PatchouliHandler;
import com.hollingsworth.arsnouveau.common.datagen.ItemTagProvider;
import com.hollingsworth.arsnouveau.common.entity.goal.UntamedFindItemGoal;
import com.hollingsworth.arsnouveau.common.entity.goal.drygmy.CollectEssenceGoal;
import com.hollingsworth.arsnouveau.common.entity.goal.whirlisprig.FollowMobGoalBackoff;
import com.hollingsworth.arsnouveau.common.entity.goal.whirlisprig.FollowPlayerGoal;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketANEffect;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

public class EntityDrygmy extends MagicalBuddyMob implements ITooltipProvider, IVariantColorProvider<EntityDrygmy> {

    public static final EntityDataAccessor<Boolean> CHANNELING = SynchedEntityData.defineId(EntityDrygmy.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> TAMED = SynchedEntityData.defineId(EntityDrygmy.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> BEING_TAMED = SynchedEntityData.defineId(EntityDrygmy.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> HOLDING_ESSENCE = SynchedEntityData.defineId(EntityDrygmy.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> CHANNELING_ENTITY = SynchedEntityData.defineId(EntityDrygmy.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<String> COLOR = SynchedEntityData.defineId(EntityDrygmy.class, EntityDataSerializers.STRING);

    public int channelCooldown;
    private boolean setBehaviors;
    public BlockPos homePos;
    public static String[] COLORS = {"brown", "cyan", "orange"};

    @Override
    public int getExperienceReward() {
        return 0;
    }

    public EntityDrygmy(EntityType<? extends PathfinderMob> p_i48575_1_, Level p_i48575_2_) {
        super(p_i48575_1_, p_i48575_2_);
        addGoalsAfterConstructor();
    }

    public EntityDrygmy(Level world, boolean tamed) {
        super(ModEntities.ENTITY_DRYGMY.get(), world);
        setTamed(tamed);
        addGoalsAfterConstructor();
    }

    public @Nullable DrygmyTile getHome() {
        if (homePos == null || !(level.getBlockEntity(homePos) instanceof DrygmyTile))
            return null;
        return (DrygmyTile) level.getBlockEntity(homePos);
    }

    @Override
    public void die(DamageSource source) {
        if (!level.isClientSide && isTamed()) {
            ItemStack stack = new ItemStack(ItemsRegistry.DRYGMY_CHARM);
            level.addFreshEntity(new ItemEntity(level, getX(), getY(), getZ(), stack));
        }
        super.die(source);
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (level.isClientSide || hand != InteractionHand.MAIN_HAND)
            return InteractionResult.SUCCESS;
        ItemStack stack = player.getItemInHand(hand);

        if (player.getMainHandItem().is(Tags.Items.DYES)) {
            DyeColor color = DyeColor.getColor(stack);
            if (color == null || this.entityData.get(COLOR).equals(color.getName()) || !Arrays.asList(COLORS).contains(color.getName()))
                return InteractionResult.SUCCESS;
            this.entityData.set(COLOR, color.getName());
            player.getMainHandItem().shrink(1);
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public void tick() {
        super.tick();
        SummonUtil.healOverTime(this);
        if (!level.isClientSide && channelCooldown > 0) {
            channelCooldown--;
        }

        if (!level.isClientSide && level.getGameTime() % 60 == 0 && isTamed() && homePos != null && !(level.getBlockEntity(homePos) instanceof DrygmyTile)) {
            this.hurt(level.damageSources().playerAttack(ANFakePlayer.getPlayer((ServerLevel) level)), 99);
            return;
        }

        if (level.isClientSide && isChanneling() && getChannelEntity() != -1) {
            Entity entity = level.getEntity(getChannelEntity());
            if (entity == null || entity.isRemoved())
                return;
            Vec3 vec = entity.position;
            level.addParticle(GlowParticleData.createData(new ParticleColor(50, 255, 20)),
                    (float) (vec.x) - Math.sin((ClientInfo.ticksInGame) / 8D),
                    (float) (vec.y) + Math.sin(ClientInfo.ticksInGame / 5d) / 8D + 0.5,
                    (float) (vec.z) - Math.cos((ClientInfo.ticksInGame) / 8D),
                    0, 0, 0);
        }
        if (!isTamed() && !this.entityData.get(BEING_TAMED) && level.getGameTime() % 40 == 0) {
            for (ItemEntity itementity : this.level.getEntitiesOfClass(ItemEntity.class, this.getBoundingBox().inflate(1))) {
                pickUpItem(itementity);
            }
        }
        if (!isTamed() && this.entityData.get(BEING_TAMED)) {

            tamingTime++;
            if (tamingTime % 20 == 0 && !level.isClientSide())
                Networking.sendToNearby(level, this, new PacketANEffect(PacketANEffect.EffectType.TIMED_HELIX, blockPosition(), ParticleColor.ORANGE));

            if (tamingTime > 60 && !level.isClientSide) {
                ItemStack stack = new ItemStack(ItemsRegistry.DRYGMY_SHARD, 1 + level.random.nextInt(2));
                level.addFreshEntity(new ItemEntity(level, getX(), getY() + 0.5, getZ(), stack));
                this.remove(RemovalReason.DISCARDED);
                level.playSound(null, getX(), getY(), getZ(), SoundEvents.ILLUSIONER_MIRROR_MOVE, SoundSource.NEUTRAL, 1f, 1f);
            }
        }
    }

    public boolean removeWhenFarAway(double p_213397_1_) {
        return false;
    }

    private PlayState animationPredicate(AnimationState<?> event) {
        if (isChanneling() || this.entityData.get(BEING_TAMED) || (level.isClientSide && PatchouliHandler.isPatchouliWorld())) {
            event.getController().setAnimation(RawAnimation.begin().thenPlay("channel"));
            return PlayState.CONTINUE;
        }
        if (event.isMoving()) {
            event.getController().setAnimation(RawAnimation.begin().thenPlay("run"));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    // Cannot add conditional goals in RegisterGoals as it is final and called during the MobEntity super.
    protected void addGoalsAfterConstructor() {
        if (this.level.isClientSide())
            return;

        for (WrappedGoal goal : getGoals()) {
            this.goalSelector.addGoal(goal.getPriority(), goal.getGoal());
        }
    }

    public List<WrappedGoal> getGoals() {
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
        this.entityData.define(COLOR, "brown");
    }

    public boolean holdingEssence() {
        return this.entityData.get(HOLDING_ESSENCE);
    }

    public void setHoldingEssence(boolean holdingEssence) {
        this.entityData.set(HOLDING_ESSENCE, holdingEssence);
    }

    public boolean isTamed() {
        return this.entityData.get(TAMED);
    }

    public void setTamed(boolean tamed) {
        this.entityData.set(TAMED, tamed);
    }

    public boolean isChanneling() {
        return this.entityData.get(CHANNELING);
    }

    public void setChanneling(boolean channeling) {
        this.entityData.set(CHANNELING, channeling);
    }

    public int getChannelEntity() {
        return this.entityData.get(CHANNELING_ENTITY);
    }

    public void setChannelingEntity(int entityID) {
        this.entityData.set(CHANNELING_ENTITY, entityID);
    }

    @Override
    protected void registerGoals() {
    }

    public List<WrappedGoal> getTamedGoals() {
        List<WrappedGoal> list = new ArrayList<>();
        list.add(new WrappedGoal(3, new RandomLookAroundGoal(this)));
        list.add(new WrappedGoal(2, new CollectEssenceGoal(this)));
        list.add(new WrappedGoal(0, new FloatGoal(this)));
        return list;
    }

    public List<WrappedGoal> getUntamedGoals() {
        List<WrappedGoal> list = new ArrayList<>();
        list.add(new WrappedGoal(3, new FollowMobGoalBackoff(this, 1.0D, 3.0F, 7.0F, 0.5f)));
        list.add(new WrappedGoal(5, new FollowPlayerGoal(this, 1.0D, 3.0F, 7.0F)));
        list.add(new WrappedGoal(2, new RandomLookAroundGoal(this)));
        list.add(new WrappedGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0D)));
        list.add(new WrappedGoal(0, new FloatGoal(this)));
        list.add(new WrappedGoal(1, new UntamedFindItemGoal(this,
                () -> !this.isTamed() && !this.entityData.get(BEING_TAMED)
                , (itemEntity -> !itemEntity.hasPickUpDelay() && itemEntity.isAlive() && itemEntity.getItem().is(ItemTagProvider.WILDEN_DROP_TAG)))));
        return list;
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar animatableManager) {
        animatableManager.add(new AnimationController<>(this, "walkController", 20, this::animationPredicate));
        animatableManager.add(new AnimationController<>(this, "idleController", 20, this::idlePredicate));
    }

    @Override
    protected void pickUpItem(ItemEntity itemEntity) {
        if (!isTamed() && !entityData.get(BEING_TAMED) && itemEntity.getItem().is(ItemTagProvider.WILDEN_DROP_TAG)) {
            entityData.set(BEING_TAMED, true);
            itemEntity.getItem().shrink(1);
            this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ITEM_PICKUP, this.getSoundSource(), 1.0F, 1.0F);
        }
    }

    @Override
    public boolean onDispel(@Nullable LivingEntity caster) {
        if (this.isRemoved())
            return false;

        if (!level.isClientSide && isTamed()) {
            ItemStack stack = new ItemStack(ItemsRegistry.DRYGMY_CHARM);
            level.addFreshEntity(new ItemEntity(level, getX(), getY(), getZ(), stack));
            ParticleUtil.spawnPoof((ServerLevel) level, blockPosition());
            this.remove(RemovalReason.DISCARDED);
        }
        return this.isTamed();
    }

    private PlayState idlePredicate(AnimationState<?> event) {
        return PlayState.CONTINUE;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        NBTUtil.storeBlockPos(tag, "home", homePos);
        tag.putBoolean("tamed", this.entityData.get(TAMED));
        tag.putInt("cooldown", channelCooldown);
        tag.putInt("taming", tamingTime);
        tag.putBoolean("beingTamed", this.entityData.get(BEING_TAMED));
        tag.putString("color", this.entityData.get(COLOR));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (NBTUtil.hasBlockPos(tag, "home"))
            this.homePos = NBTUtil.getBlockPos(tag, "home");
        setTamed(tag.getBoolean("tamed"));
        if (!setBehaviors) {
            tryResetGoals();
            setBehaviors = true;
        }
        channelCooldown = tag.getInt("cooldown");
        this.tamingTime = tag.getInt("taming");
        entityData.set(BEING_TAMED, tag.getBoolean("beingTamed"));
        if (tag.contains("color"))
            this.entityData.set(COLOR, tag.getString("color"));
    }

    // A workaround for goals not registering correctly for a dynamic variable on reload as read() is called after constructor.
    public void tryResetGoals() {
        this.goalSelector.availableGoals = new LinkedHashSet<>();
        this.addGoalsAfterConstructor();
    }

    @Override
    public ResourceLocation getTexture(EntityDrygmy entity) {
        String color = getColor(entity).toLowerCase();
        if (color.isEmpty())
            color = "brown";
        return new ResourceLocation(ArsNouveau.MODID, "textures/entity/drygmy_" + color + ".png");
    }

    @Override
    public String getColor(EntityDrygmy entity) {
        return getEntityData().get(COLOR);
    }

    @Override
    public void setColor(String color, EntityDrygmy entity) {
        getEntityData().set(COLOR, color);
    }
}
