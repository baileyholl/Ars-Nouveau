package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.api.registry.BuddingConversionRegistry;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.api.util.SummonUtil;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.compat.PatchouliHandler;
import com.hollingsworth.arsnouveau.common.crafting.recipes.BuddingConversionRecipe;
import com.hollingsworth.arsnouveau.common.entity.goal.GoBackHomeGoal;
import com.hollingsworth.arsnouveau.common.entity.goal.amethyst_golem.*;
import com.hollingsworth.arsnouveau.common.entity.pathfinding.MinecoloniesAdvancedPathNavigate;
import com.hollingsworth.arsnouveau.common.entity.pathfinding.PathingStuckHandler;
import com.hollingsworth.arsnouveau.common.items.data.ICharmSerializable;
import com.hollingsworth.arsnouveau.common.items.data.PersistentFamiliarData;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.hollingsworth.arsnouveau.common.datagen.BlockTagProvider.BUDDING_BLOCKS;

public class AmethystGolem extends PathfinderMob implements GeoEntity, IDispellable, ITooltipProvider, IWandable, ICharmSerializable {
    public static final EntityDataAccessor<Optional<BlockPos>> HOME = SynchedEntityData.defineId(AmethystGolem.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    public static final EntityDataAccessor<Boolean> IMBUEING = SynchedEntityData.defineId(AmethystGolem.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> STOMPING = SynchedEntityData.defineId(AmethystGolem.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<BlockPos> IMBUE_POS = SynchedEntityData.defineId(AmethystGolem.class, EntityDataSerializers.BLOCK_POS);
    public final List<BuddingConversionRecipe> recipes = BuddingConversionRegistry.getRecipes();

    public int growCooldown;
    public int convertCooldown;
    public int pickupCooldown;
    public int harvestCooldown;
    public List<BlockPos> buddingBlocks = new ArrayList<>();
    public List<BlockPos> amethystBlocks = new ArrayList<>();
    public int scanCooldown;
    public MinecoloniesAdvancedPathNavigate pathNavigate;
    public PathNavigation minecraftPathNav;
    public AmethystGolemGoalState goalState;

    @Override
    public void fromCharmData(PersistentFamiliarData data) {
        this.setCustomName(data.name());
    }

    public enum AmethystGolemGoalState {
        NONE,
        CONVERT,
        GROW,
        HARVEST,
        PICKUP,
        DEPOSIT
    }

    public AmethystGolem(EntityType<? extends PathfinderMob> p_21683_, Level p_21684_) {
        super(p_21683_, p_21684_);
    }

    @Override
    public MinecoloniesAdvancedPathNavigate getNavigation() {
        if (this.pathNavigate == null) {
            this.pathNavigate = new MinecoloniesAdvancedPathNavigate(this, this.level);
            this.minecraftPathNav = this.navigation;
            this.navigation = pathNavigate;
            this.pathNavigate.setCanFloat(true);
            this.pathNavigate.setSwimSpeedFactor(2.0);
            this.pathNavigate.getPathingOptions().setEnterDoors(true);
            this.pathNavigate.getPathingOptions().setCanOpenDoors(true);
            this.pathNavigate.setStuckHandler(PathingStuckHandler.createStuckHandler());
        }
        return pathNavigate;
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader level) {
        if (this.getHome() == null) {
            return super.getWalkTargetValue(pos, level);
        }

        double distance = pos.getCenter().distanceToSqr(this.getHome().getCenter());

        // Encourage amethyst golem to stay within 10 blocks of its home.
        // See 1/max(100, x^2) on a graphing calculator.
        return (float) (1.0D / Math.max(100, distance));
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new GoBackHomeGoal(this, this::getHome, 10, () -> true));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(3, new ConvertBuddingGoal(this, () -> convertCooldown <= 0 && getHome() != null && getMainHandItem().isEmpty()));
        this.goalSelector.addGoal(4, new GrowClusterGoal(this, () -> growCooldown <= 0 && getHome() != null && getMainHandItem().isEmpty()));
        this.goalSelector.addGoal(5, new HarvestClusterGoal(this, () -> harvestCooldown <= 0 && getHome() != null && !isImbueing() && getMainHandItem().isEmpty()));
        this.goalSelector.addGoal(2, new PickupAmethystGoal(this, () -> getHome() != null && pickupCooldown <= 0));
        this.goalSelector.addGoal(2, new DepositAmethystGoal(this, () -> getHome() != null && !getMainHandItem().isEmpty()));
    }

    @Override
    public void tick() {
        super.tick();
        SummonUtil.healOverTime(this);
        if (harvestCooldown > 0)
            harvestCooldown--;
        if (growCooldown > 0)
            growCooldown--;
        if (convertCooldown > 0)
            convertCooldown--;
        if (scanCooldown > 0) {
            scanCooldown--;
        }
        if (pickupCooldown > 0)
            pickupCooldown--;
        if (!level.isClientSide && scanCooldown == 0 && getHome() != null) {
            scanCooldown = 20 * 60 * 3;
            scanBlocks();
        }

        if (level.isClientSide && isImbueing() && getImbuePos() != null) {
            Vec3 vec = new Vec3(getImbuePos().getX() + 0.5, getImbuePos().getY(), getImbuePos().getZ() + 0.5);
            level.addParticle(GlowParticleData.createData(new ParticleColor(255, 50, 150)),
                    (float) (vec.x) - Math.sin((ClientInfo.ticksInGame) / 8D),
                    (float) (vec.y) + Math.sin(ClientInfo.ticksInGame / 5d) / 8D + 0.5,
                    (float) (vec.z) - Math.cos((ClientInfo.ticksInGame) / 8D),
                    0, 0, 0);
        }
    }

    public void setHeldStack(ItemStack stack) {
        this.setItemSlot(EquipmentSlot.MAINHAND, stack);
    }

    public boolean isStomping() {
        return this.entityData.get(STOMPING);
    }

    public void setStomping(boolean imbueing) {
        this.entityData.set(STOMPING, imbueing);
    }

    public boolean isImbueing() {
        return this.entityData.get(IMBUEING);
    }

    public void setImbueing(boolean imbueing) {
        this.entityData.set(IMBUEING, imbueing);
    }

    public BlockPos getImbuePos() {
        return this.entityData.get(IMBUE_POS);
    }

    public void setImbuePos(BlockPos pos) {
        this.entityData.set(IMBUE_POS, pos);
    }

    public void scanBlocks() {
        BlockPos pos = getHome().immutable();
        amethystBlocks = new ArrayList<>();
        buddingBlocks = new ArrayList<>();
        for (BlockPos b : BlockPos.betweenClosed(pos.below(3).south(5).east(5), pos.above(10).north(5).west(5))) {
            BlockState bs = level.getBlockState(b);
            if (bs.isAir())
                continue;

            for (BuddingConversionRecipe recipe : recipes) {
                if (recipe.matches(bs)) {
                    amethystBlocks.add(b.immutable());
                    break;
                }
            }

            if (bs.is(BUDDING_BLOCKS)) {
                buddingBlocks.add(b.immutable());
            }
        }
    }

    @Override
    protected void playStepSound(BlockPos pPos, BlockState pBlock) {
        SoundEvent soundtype = SoundEvents.AMETHYST_CLUSTER_STEP;
        this.playSound(soundtype, (float) (Math.random() * 0.45F), (float) (Math.random() * 1.0f));
    }

    @Override
    public void onFinishedConnectionFirst(@javax.annotation.Nullable BlockPos storedPos, @javax.annotation.Nullable LivingEntity storedEntity, Player playerEntity) {
        if (storedPos != null) {
            setHome(storedPos);
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.home_set"));
        }
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        if (getHome() != null) {
            tooltip.add(Component.translatable("ars_nouveau.gathering_at", getHome().toShortString()));
        }
    }

    @Override
    public void die(DamageSource source) {
        if (!level.isClientSide) {
            ItemStack stack = new ItemStack(ItemsRegistry.AMETHYST_GOLEM_CHARM.get());
            stack.set(DataComponentRegistry.PERSISTENT_FAMILIAR_DATA, createCharmData());
            level.addFreshEntity(new ItemEntity(level, getX(), getY(), getZ(), stack));
            if (this.getMainHandItem() != null)
                level.addFreshEntity(new ItemEntity(level, getX(), getY(), getZ(), this.getMainHandItem()));
        }
        super.die(source);
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        return SummonUtil.canSummonTakeDamage(pSource) && super.hurt(pSource, pAmount);
    }

    @Override
    public boolean onDispel(@Nullable LivingEntity caster) {
        if (this.isRemoved())
            return false;

        if (!level.isClientSide) {
            ItemStack stack = new ItemStack(ItemsRegistry.AMETHYST_GOLEM_CHARM.get());
            stack.set(DataComponentRegistry.PERSISTENT_FAMILIAR_DATA, createCharmData());
            level.addFreshEntity(new ItemEntity(level, getX(), getY(), getZ(), stack.copy()));
            stack = getMainHandItem();
            level.addFreshEntity(new ItemEntity(level, getX(), getY(), getZ(), stack));
            ParticleUtil.spawnPoof((ServerLevel) level, blockPosition());
            this.remove(RemovalReason.DISCARDED);
        }
        return true;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        NBTUtil.storeBlockPos(tag, "home", getHome());
        tag.putInt("grow", growCooldown);
        tag.putInt("convert", convertCooldown);
        tag.putInt("harvest", harvestCooldown);
        tag.putInt("pickup", pickupCooldown);

        if (getMainHandItem() != null && !getMainHandItem().isEmpty()) {
            Tag itemTag = getMainHandItem().save(level.registryAccess());
            tag.put("held", itemTag);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (NBTUtil.hasBlockPos(tag, "home")) {
            setHome(NBTUtil.getBlockPos(tag, "home"));
        }
        this.growCooldown = tag.getInt("grow");
        this.convertCooldown = tag.getInt("convert");
        this.harvestCooldown = tag.getInt("harvest");
        this.pickupCooldown = tag.getInt("pickup");

        setHeldStack(ItemStack.parseOptional(level.registryAccess(), tag.getCompound("held")));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "run_controller", 1, e ->{
            AnimationController controller = e.getController();
            if (isStomping()) {
                controller.setAnimation(RawAnimation.begin().thenPlay("harvest2"));
                return PlayState.CONTINUE;
            }

            if (isImbueing() || (level.isClientSide && PatchouliHandler.isPatchouliWorld())) {
                controller.setAnimation(RawAnimation.begin().thenPlay("tending_master"));
                return PlayState.CONTINUE;
            }
            if (e.isMoving()) {
                String anim = getMainHandItem().isEmpty() ? "run" : "run_carry";
                controller.setAnimation(RawAnimation.begin().thenPlay(anim));
                return PlayState.CONTINUE;
            }

            if (!getMainHandItem().isEmpty()) {
                controller.setAnimation(RawAnimation.begin().thenPlay("carry_idle"));
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));
        data.add(new AnimationController<>(this, "attack_controller", 5, e ->{
            return PlayState.CONTINUE;
        }));
    }



    public void setHome(BlockPos home) {
        this.entityData.set(HOME, Optional.of(home));
    }

    public @Nullable BlockPos getHome() {
        return this.entityData.get(HOME).orElse(null);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(HOME, Optional.empty());
        pBuilder.define(IMBUEING, false);
        pBuilder.define(IMBUE_POS, BlockPos.ZERO);
        pBuilder.define(STOMPING, false);
    }

    @Override
    public boolean removeWhenFarAway(double p_213397_1_) {
        return false;
    }

    @Override
    protected int getBaseExperienceReward() {
        return 0;
    }

    AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    public static AttributeSupplier.Builder attributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2d);
    }

}
