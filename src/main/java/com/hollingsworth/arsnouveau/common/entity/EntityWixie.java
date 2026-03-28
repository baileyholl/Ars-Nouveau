package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.api.util.SummonUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.tile.IAnimationListener;
import com.hollingsworth.arsnouveau.common.block.tile.WixieCauldronTile;
import com.hollingsworth.arsnouveau.common.entity.goal.wixie.CompleteCraftingGoal;
import com.hollingsworth.arsnouveau.common.entity.goal.wixie.FindNextItemGoal;
import com.hollingsworth.arsnouveau.common.entity.goal.wixie.FindPotionGoal;
import com.hollingsworth.arsnouveau.common.items.data.ICharmSerializable;
import com.hollingsworth.arsnouveau.common.items.data.PersistentFamiliarData;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.Tags;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.state.AnimationTest;
import software.bernie.geckolib.animation.object.PlayState;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class EntityWixie extends AbstractFlyingCreature implements GeoEntity, IAnimationListener, IDispellable, ICharmSerializable {
    AnimatableInstanceCache manager = GeckoLibUtil.createInstanceCache(this);
    public static final EntityDataAccessor<String> COLOR = SynchedEntityData.defineId(EntityWixie.class, EntityDataSerializers.STRING);

    public BlockPos cauldronPos;
    public int inventoryBackoff;

    private PlayState idlePredicate(AnimationTest<EntityWixie> event) {
        if (getNavigation().isInProgress())
            return PlayState.STOP;
        event.controller().setAnimation(RawAnimation.begin().thenPlay("idle"));
        return PlayState.CONTINUE;
    }

    private PlayState castPredicate(AnimationTest<EntityWixie> event) {
        return PlayState.CONTINUE;
    }

    private PlayState summonPredicate(AnimationTest<EntityWixie> event) {
        return PlayState.CONTINUE;
    }

    @Override
    public int getBaseExperienceReward(ServerLevel level) {
        return 0;
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource pSource, float pAmount) {
        return SummonUtil.canSummonTakeDamage(pSource) && super.hurtServer(level, pSource, pAmount);
    }

    AnimationController<EntityWixie> summonController;
    AnimationController<EntityWixie> castController;

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar animatableManager) {
        animatableManager.add(new AnimationController<EntityWixie>("idleController", 20, this::idlePredicate));
        castController = new AnimationController<EntityWixie>("castController", 1, this::castPredicate);
        summonController = new AnimationController<EntityWixie>("summonController", 1, this::summonPredicate);
        animatableManager.add(castController);
        animatableManager.add(summonController);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return manager;
    }

    public EntityWixie(EntityType<? extends AbstractFlyingCreature> type, Level worldIn) {
        super(type, worldIn);
        this.moveControl = new FlyingMoveControl(this, 10, true);
    }

    public EntityWixie(Level world, BlockPos pos) {
        this(ModEntities.ENTITY_WIXIE_TYPE.get(), world);
        this.cauldronPos = pos;
    }

    public static String[] COLORS = {"white", "green", "blue", "black", "red"};

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (level.isClientSide() || hand != InteractionHand.MAIN_HAND)
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
        if (!level.isClientSide() && (cauldronPos == null || !(level.getBlockEntity(cauldronPos) instanceof WixieCauldronTile)))
            this.hurt(level.damageSources().playerAttack(ANFakePlayer.getPlayer((ServerLevel) level)), 99);
        if (!level.isClientSide() && inventoryBackoff > 0) {
            inventoryBackoff--;
        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        goalSelector.addGoal(2, new FindNextItemGoal(this));
        goalSelector.addGoal(2, new FindPotionGoal(this));
        goalSelector.addGoal(1, new CompleteCraftingGoal(this));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(COLOR, "blue");
    }

    @Override
    public boolean removeWhenFarAway(double p_213397_1_) {
        return false;
    }

    @Override
    protected PathNavigation createNavigation(Level world) {
        FlyingPathNavigation flyingpathnavigator = new FlyingPathNavigation(this, world);
        flyingpathnavigator.setCanOpenDoors(false);
        flyingpathnavigator.setCanFloat(true);
        // setCanPassDoors removed in 1.21.11
        return flyingpathnavigator;
    }

    public static AttributeSupplier.Builder attributes() {
        return Mob.createMobAttributes().add(Attributes.FLYING_SPEED, Attributes.FLYING_SPEED.value().getDefaultValue())
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D);
    }

    @Override
    public void readAdditionalSaveData(ValueInput tag) {
        super.readAdditionalSaveData(tag);
        if (tag.keySet().contains("summoner_x"))
            cauldronPos = new BlockPos(tag.getIntOr("summoner_x", 0), tag.getIntOr("summoner_y", 0), tag.getIntOr("summoner_z", 0));
        if (tag.keySet().contains("color"))
            this.entityData.set(COLOR, tag.getStringOr("color", ""));

    }

    @Override
    public void addAdditionalSaveData(ValueOutput tag) {
        super.addAdditionalSaveData(tag);
        if (cauldronPos != null) {
            tag.putInt("summoner_x", cauldronPos.getX());
            tag.putInt("summoner_y", cauldronPos.getY());
            tag.putInt("summoner_z", cauldronPos.getZ());
        }
        if (this.entityData.get(COLOR) != null) {
            tag.putString("color", this.entityData.get(COLOR));
        }
    }

    @Override
    public void startAnimation(int arg) {
        if (arg == Animations.CAST.ordinal() && castController != null) {
            castController.reset();
            castController.setAnimation(RawAnimation.begin().thenPlay("cast"));
        } else if (arg == Animations.SUMMON_ITEM.ordinal() && summonController != null) {
            summonController.reset();
            summonController.setAnimation(RawAnimation.begin().thenPlay("summon_item"));
        }
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
        if (!level.isClientSide()) {
            ItemStack stack = new ItemStack(ItemsRegistry.WIXIE_CHARM);
            stack.set(DataComponentRegistry.PERSISTENT_FAMILIAR_DATA, createCharmData());
            level.addFreshEntity(new ItemEntity(level, getX(), getY(), getZ(), stack));
        }
    }

    @Override
    public boolean onDispel(@Nullable LivingEntity caster) {
        if (this.isRemoved())
            return false;

        if (!level.isClientSide()) {
            ItemStack stack = new ItemStack(ItemsRegistry.WIXIE_CHARM);
            stack.set(DataComponentRegistry.PERSISTENT_FAMILIAR_DATA, this.createCharmData());
            level.addFreshEntity(new ItemEntity(level, getX(), getY(), getZ(), stack.copy()));
            ParticleUtil.spawnPoof((ServerLevel) level, blockPosition());
            this.remove(net.minecraft.world.entity.Entity.RemovalReason.DISCARDED);
        }
        return true;
    }

    public static Map<String, Identifier> TEXTURES = new HashMap<>();

    public Identifier getTexture() {
        String color = getColor().toLowerCase();
        if (color.isEmpty())
            color = "blue";
        String finalColor = color;
        return TEXTURES.computeIfAbsent(color, (k) -> ArsNouveau.prefix("textures/entity/wixie_" + finalColor + ".png"));
    }

    @Override
    public void fromCharmData(PersistentFamiliarData data) {
        this.entityData.set(COLOR, data.color());
        setCustomName(data.name());
    }

    @Override
    public String getColor() {
        return this.getEntityData().get(COLOR);
    }

    public enum Animations {
        CAST,
        SUMMON_ITEM
    }
}
