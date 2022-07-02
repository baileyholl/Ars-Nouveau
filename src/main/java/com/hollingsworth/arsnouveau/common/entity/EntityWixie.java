package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.client.IVariantTextureProvider;
import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.tile.IAnimationListener;
import com.hollingsworth.arsnouveau.common.block.tile.WixieCauldronTile;
import com.hollingsworth.arsnouveau.common.entity.goal.wixie.CompleteCraftingGoal;
import com.hollingsworth.arsnouveau.common.entity.goal.wixie.FindNextItemGoal;
import com.hollingsworth.arsnouveau.common.entity.goal.wixie.FindPotionGoal;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.Tags;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntityWixie extends AbstractFlyingCreature implements IAnimatable, IAnimationListener, IDispellable, IVariantTextureProvider {
    AnimationFactory manager = new AnimationFactory(this);

    public static final EntityDataAccessor<Boolean> TAMED = SynchedEntityData.defineId(EntityWixie.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> COLOR = SynchedEntityData.defineId(EntityWixie.class, EntityDataSerializers.STRING);

    public BlockPos cauldronPos;
    public int inventoryBackoff;

    private <P extends IAnimatable> PlayState idlePredicate(AnimationEvent<P> event) {
        if (getNavigation().isInProgress())
            return PlayState.STOP;
        event.getController().setAnimation(new AnimationBuilder().addAnimation("idle"));
        return PlayState.CONTINUE;
    }

    private <P extends IAnimatable> PlayState castPredicate(AnimationEvent<P> event) {
        return PlayState.CONTINUE;
    }

    private <P extends IAnimatable> PlayState summonPredicate(AnimationEvent<P> event) {
        return PlayState.CONTINUE;
    }

    @Override
    public int getExperienceReward() {
        return 0;
    }

    AnimationController<?> summonController;
    AnimationController<?> castController;

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<>(this, "idleController", 20, this::idlePredicate));
        castController = new AnimationController<>(this, "castController", 1, this::castPredicate);
        summonController = new AnimationController<>(this, "summonController", 1, this::summonPredicate);
        animationData.addAnimationController(castController);
        animationData.addAnimationController(summonController);
    }

    @Override
    public AnimationFactory getFactory() {
        return manager;
    }

    protected EntityWixie(EntityType<? extends AbstractFlyingCreature> type, Level worldIn) {
        super(type, worldIn);
        this.moveControl = new FlyingMoveControl(this, 10, true);
        addGoalsAfterConstructor();
    }

    public EntityWixie(Level world, boolean isTamed, BlockPos pos) {
        super(ModEntities.ENTITY_WIXIE_TYPE.get(), world);
        this.cauldronPos = pos;
        this.moveControl = new FlyingMoveControl(this, 10, true);
        this.entityData.set(TAMED, isTamed);
        addGoalsAfterConstructor();
    }

    public static String[] COLORS = {"white", "green", "blue", "black", "red"};

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
        if (!level.isClientSide && (cauldronPos == null || !(level.getBlockEntity(cauldronPos) instanceof WixieCauldronTile)))
            this.hurt(DamageSource.playerAttack(ANFakePlayer.getPlayer((ServerLevel) level)), 99);
        if (!level.isClientSide && inventoryBackoff > 0) {
            inventoryBackoff--;
        }

    }

    //MOJANG MAKES THIS SO CURSED WHAT THE HECK
    public List<WrappedGoal> getTamedGoals() {
        List<WrappedGoal> list = new ArrayList<>();
        list.add(new WrappedGoal(3, new RandomLookAroundGoal(this)));
        list.add(new WrappedGoal(2, new FindNextItemGoal(this)));
        list.add(new WrappedGoal(2, new FindPotionGoal(this)));
        list.add(new WrappedGoal(1, new CompleteCraftingGoal(this)));
        return list;
    }

    public List<WrappedGoal> getUntamedGoals() {
        List<WrappedGoal> list = new ArrayList<>();
        list.add(new WrappedGoal(3, new RandomLookAroundGoal(this)));
        list.add(new WrappedGoal(2, new FindNextItemGoal(this)));
        list.add(new WrappedGoal(2, new FindPotionGoal(this)));
        list.add(new WrappedGoal(1, new CompleteCraftingGoal(this)));
        return list;
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
        this.entityData.define(TAMED, false);
        this.entityData.define(COLOR, "blue");
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
        flyingpathnavigator.setCanPassDoors(true);
        return flyingpathnavigator;
    }

    public static AttributeSupplier.Builder attributes() {
        return Mob.createMobAttributes().add(Attributes.FLYING_SPEED, Attributes.FLYING_SPEED.getDefaultValue())
                .add(Attributes.MAX_HEALTH, 6.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("summoner_x"))
            cauldronPos = new BlockPos(tag.getInt("summoner_x"), tag.getInt("summoner_y"), tag.getInt("summoner_z"));

        this.entityData.set(TAMED, tag.getBoolean("tamed"));
        if (tag.contains("color"))
            this.entityData.set(COLOR, tag.getString("color"));

    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (cauldronPos != null) {
            tag.putInt("summoner_x", cauldronPos.getX());
            tag.putInt("summoner_y", cauldronPos.getY());
            tag.putInt("summoner_z", cauldronPos.getZ());
        }
        tag.putBoolean("tamed", this.entityData.get(TAMED));
        tag.putString("color", this.entityData.get(COLOR));
    }

    @Override
    public void startAnimation(int arg) {
        if (arg == Animations.CAST.ordinal() && castController != null) {
            castController.markNeedsReload();
            castController.setAnimation(new AnimationBuilder().addAnimation("cast", false));
        } else if (arg == Animations.SUMMON_ITEM.ordinal() && summonController != null) {
            summonController.markNeedsReload();
            summonController.setAnimation(new AnimationBuilder().addAnimation("summon_item", false));
        }
    }

    @Override
    public void die(DamageSource source) {
        if (!level.isClientSide) {
            ItemStack stack = new ItemStack(ItemsRegistry.WIXIE_CHARM);
            level.addFreshEntity(new ItemEntity(level, getX(), getY(), getZ(), stack));

        }
        super.die(source);
    }

    @Override
    public boolean onDispel(@Nullable LivingEntity caster) {
        if (this.isRemoved())
            return false;

        if (!level.isClientSide) {
            ItemStack stack = new ItemStack(ItemsRegistry.WIXIE_CHARM);
            level.addFreshEntity(new ItemEntity(level, getX(), getY(), getZ(), stack.copy()));
            ParticleUtil.spawnPoof((ServerLevel) level, blockPosition());
            this.remove(RemovalReason.DISCARDED);
        }
        return true;
    }

    @Override
    public ResourceLocation getTexture(LivingEntity entity) {
        String color = getEntityData().get(EntityWixie.COLOR).toLowerCase();
        if (color.isEmpty())
            color = "blue";
        return new ResourceLocation(ArsNouveau.MODID, "textures/entity/wixie_" + color + ".png");
    }

    public enum Animations {
        CAST,
        SUMMON_ITEM
    }
}
