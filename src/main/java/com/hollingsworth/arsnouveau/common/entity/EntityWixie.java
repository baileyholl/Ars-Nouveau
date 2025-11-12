package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.entity.IDecoratable;
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
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
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
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class EntityWixie extends AbstractFlyingCreature implements GeoEntity, IAnimationListener, IDispellable, ICharmSerializable, IDecoratable {
    AnimatableInstanceCache manager = GeckoLibUtil.createInstanceCache(this);
    public static final EntityDataAccessor<String> COLOR = SynchedEntityData.defineId(EntityWixie.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<ItemStack> COSMETIC = SynchedEntityData.defineId(EntityWixie.class, EntityDataSerializers.ITEM_STACK);

    public BlockPos cauldronPos;
    public int inventoryBackoff;

    private <P extends GeoAnimatable> PlayState idlePredicate(AnimationState<P> event) {
        if (getNavigation().isInProgress())
            return PlayState.STOP;
        event.getController().setAnimation(RawAnimation.begin().thenPlay("idle"));
        return PlayState.CONTINUE;
    }

    private <P extends GeoAnimatable> PlayState castPredicate(AnimationState<P> event) {
        return PlayState.CONTINUE;
    }

    private <P extends GeoAnimatable> PlayState summonPredicate(AnimationState<P> event) {
        return PlayState.CONTINUE;
    }

    @Override
    public int getBaseExperienceReward() {
        return 0;
    }

    @Override
    public boolean hurt(@NotNull DamageSource pSource, float pAmount) {
        return SummonUtil.canSummonTakeDamage(pSource) && super.hurt(pSource, pAmount);
    }

    AnimationController<?> summonController;
    AnimationController<?> castController;

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar animatableManager) {
        animatableManager.add(new AnimationController<>(this, "idleController", 20, this::idlePredicate));
        castController = new AnimationController<>(this, "castController", 1, this::castPredicate);
        summonController = new AnimationController<>(this, "summonController", 1, this::summonPredicate);
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
    protected @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
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
        if (!level.isClientSide && (cauldronPos == null || !(level.getBlockEntity(cauldronPos) instanceof WixieCauldronTile)))
            this.hurt(level.damageSources().playerAttack(ANFakePlayer.getPlayer((ServerLevel) level)), 99);
        if (!level.isClientSide && inventoryBackoff > 0) {
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
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(COLOR, "blue");
        pBuilder.define(COSMETIC, ItemStack.EMPTY);
    }

    @Override
    public boolean removeWhenFarAway(double p_213397_1_) {
        return false;
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level world) {
        FlyingPathNavigation flyingpathnavigator = new FlyingPathNavigation(this, world);
        flyingpathnavigator.setCanOpenDoors(false);
        flyingpathnavigator.setCanFloat(true);
        flyingpathnavigator.setCanPassDoors(true);
        return flyingpathnavigator;
    }

    public static AttributeSupplier.Builder attributes() {
        return Mob.createMobAttributes().add(Attributes.FLYING_SPEED, Attributes.FLYING_SPEED.value().getDefaultValue())
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("summoner_x"))
            cauldronPos = new BlockPos(tag.getInt("summoner_x"), tag.getInt("summoner_y"), tag.getInt("summoner_z"));
        if (tag.contains("color"))
            this.entityData.set(COLOR, tag.getString("color"));
        this.entityData.set(COSMETIC, ItemStack.parseOptional(this.level.registryAccess(), tag.getCompound("cosmetic")));
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (cauldronPos != null) {
            tag.putInt("summoner_x", cauldronPos.getX());
            tag.putInt("summoner_y", cauldronPos.getY());
            tag.putInt("summoner_z", cauldronPos.getZ());
        }
        if (this.entityData.get(COLOR) != null) {
            tag.putString("color", this.entityData.get(COLOR));
        }
        if (!this.entityData.get(COSMETIC).isEmpty()) {
            Tag cosmeticTag = this.entityData.get(COSMETIC).save(level.registryAccess());
            tag.put("cosmetic", cosmeticTag);
        }
    }

    @Override
    public void startAnimation(int arg) {
        if (arg == Animations.CAST.ordinal() && castController != null) {
            castController.forceAnimationReset();
            castController.setAnimation(RawAnimation.begin().thenPlay("cast"));
        } else if (arg == Animations.SUMMON_ITEM.ordinal() && summonController != null) {
            summonController.forceAnimationReset();
            summonController.setAnimation(RawAnimation.begin().thenPlay("summon_item"));
        }
    }

    @Override
    protected void dropCustomDeathLoot(@NotNull ServerLevel level, @NotNull DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
        if (!level.isClientSide) {
            ItemStack stack = new ItemStack(ItemsRegistry.WIXIE_CHARM);
            stack.set(DataComponentRegistry.PERSISTENT_FAMILIAR_DATA, createCharmData());
            level.addFreshEntity(new ItemEntity(level, getX(), getY(), getZ(), stack));
        }
    }

    @Override
    public boolean onDispel(@Nullable LivingEntity caster) {
        if (this.isRemoved())
            return false;

        if (!level.isClientSide) {
            ItemStack stack = new ItemStack(ItemsRegistry.WIXIE_CHARM);
            stack.set(DataComponentRegistry.PERSISTENT_FAMILIAR_DATA, this.createCharmData());
            level.addFreshEntity(new ItemEntity(level, getX(), getY(), getZ(), stack.copy()));
            ParticleUtil.spawnPoof((ServerLevel) level, blockPosition());
            this.remove(RemovalReason.DISCARDED);
        }
        return true;
    }

    public static Map<String, ResourceLocation> TEXTURES = new HashMap<>();

    @Override
    public @NotNull ItemStack getCosmeticItem() {
        return this.entityData.get(COSMETIC);
    }

    @Override
    public void setCosmeticItem(ItemStack cosmeticItem) {
        this.entityData.set(COSMETIC, cosmeticItem);
    }

    public ResourceLocation getTexture() {
        String color = getColor().toLowerCase();
        if (color.isEmpty())
            color = "blue";
        String finalColor = color;
        return TEXTURES.computeIfAbsent(color, (k) -> ArsNouveau.prefix("textures/entity/wixie_" + finalColor + ".png"));
    }

    @Override
    public void fromCharmData(PersistentFamiliarData data) {
        this.entityData.set(COLOR, data.color());
        this.entityData.set(COSMETIC, data.cosmetic());
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
