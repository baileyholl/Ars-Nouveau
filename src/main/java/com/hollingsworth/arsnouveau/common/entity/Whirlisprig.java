package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.client.IVariantColorProvider;
import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.tile.WhirlisprigTile;
import com.hollingsworth.arsnouveau.common.entity.goal.GoBackHomeGoal;
import com.hollingsworth.arsnouveau.common.entity.goal.whirlisprig.BonemealGoal;
import com.hollingsworth.arsnouveau.common.entity.goal.whirlisprig.FollowMobGoalBackoff;
import com.hollingsworth.arsnouveau.common.entity.goal.whirlisprig.FollowPlayerGoal;
import com.hollingsworth.arsnouveau.common.entity.goal.whirlisprig.InspectPlantGoal;
import com.hollingsworth.arsnouveau.common.items.ItemScroll;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketANEffect;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.level.SaplingGrowTreeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
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

public class Whirlisprig extends AbstractFlyingCreature implements IAnimatable, ITooltipProvider, IDispellable, IVariantColorProvider {
    AnimationFactory manager = new AnimationFactory(this);


    public int timeSinceBonemeal = 0;
    public static final EntityDataAccessor<Boolean> TAMED = SynchedEntityData.defineId(Whirlisprig.class, EntityDataSerializers.BOOLEAN);
    /*Strictly used for after a tame event*/
    public int tamingTime = 0;
    public boolean droppingShards; // Strictly used by non-tamed spawns for giving shards
    public static final EntityDataAccessor<Integer> MOOD_SCORE = SynchedEntityData.defineId(Whirlisprig.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<String> COLOR = SynchedEntityData.defineId(Whirlisprig.class, EntityDataSerializers.STRING);
    public List<ItemStack> ignoreItems;
    public int diversityScore;
    public BlockPos flowerPos;
    public int timeSinceGen;
    private boolean setBehaviors;

    private <E extends Entity> PlayState idlePredicate(AnimationEvent event) {
        if (event.isMoving()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("fly"));
        } else {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("idle"));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController(this, "idleController", 20, this::idlePredicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return manager;
    }

    @Override
    public int getExperienceReward() {
        return 0;
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (player.getCommandSenderWorld().isClientSide)
            return super.mobInteract(player, hand);
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() == ItemsRegistry.DENY_ITEM_SCROLL.asItem()) {
            ItemScroll.ItemScrollData scrollData = new ItemScroll.ItemScrollData(stack);
            this.ignoreItems =  new ArrayList<>(scrollData.getItems());
            PortUtil.sendMessage(player, Component.translatable("ars_nouveau.whirlisprig.ignore"));
        }
        return super.mobInteract(player, hand);
    }

    public String getColor() {
        return this.entityData.get(COLOR);
    }

    public void setColor(String color) {
        this.entityData.set(COLOR, color);
    }

    public static String getColorFromStack(ItemStack stack) {
        if (stack.is(Tags.Items.DYES)) {
            if (stack.is(Tags.Items.DYES_GREEN)) {
                return "summer";
            } else if (stack.is(Tags.Items.DYES_ORANGE)) {
                return "autumn";
            } else if (stack.is(Tags.Items.DYES_YELLOW)) {
                return "spring";
            } else if (stack.is(Tags.Items.DYES_WHITE)) {
                return "winter";
            }
        }
        return null;
    }

    @Override
    public InteractionResult interactAt(Player player, Vec3 vec, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND || player.getCommandSenderWorld().isClientSide || !this.entityData.get(TAMED))
            return InteractionResult.PASS;

        ItemStack stack = player.getItemInHand(hand);
        String color = getColorFromStack(stack);
        if (color != null && !getColor().equals(color)) {
            this.entityData.set(COLOR, color);
            stack.shrink(1);
            return InteractionResult.SUCCESS;
        }

        if (stack.isEmpty()) {
            int moodScore = entityData.get(MOOD_SCORE);
            if (moodScore < 250) {
                PortUtil.sendMessage(player, Component.translatable("whirlisprig.unhappy"));
            } else if (moodScore <= 500) {
                PortUtil.sendMessage(player, Component.translatable("whirlisprig.content"));
            } else if (moodScore <= 750) {
                PortUtil.sendMessage(player, Component.translatable("whirlisprig.happy"));
            } else if (moodScore < 1000) {
                PortUtil.sendMessage(player, Component.translatable("whirlisprig.very_happy"));
            } else {
                PortUtil.sendMessage(player, Component.translatable("whirlisprig.extremely_happy"));
            }
            int numDrops = diversityScore / 2;
            if (numDrops <= 5) {
                PortUtil.sendMessage(player, Component.translatable("whirlisprig.okay_diversity"));
            } else if (numDrops <= 10) {
                PortUtil.sendMessage(player, Component.translatable("whirlisprig.diverse_enough"));
            } else if (numDrops <= 20) {
                PortUtil.sendMessage(player, Component.translatable("whirlisprig.very_diverse"));
            } else {
                PortUtil.sendMessage(player, Component.translatable("whirlisprig.extremely_diverse"));
            }
            if (ignoreItems != null && !ignoreItems.isEmpty()) {
                StringBuilder status = new StringBuilder();
                status.append(Component.translatable("ars_nouveau.whirlisprig.ignore_list").getString());
                for (ItemStack i : ignoreItems) {
                    status.append(i.getHoverName().getString()).append(" ");
                }
                PortUtil.sendMessage(player, status.toString());
            }

            return InteractionResult.SUCCESS;
        }
        if (!(stack.getItem() instanceof BlockItem))
            return InteractionResult.PASS;
        BlockState state = ((BlockItem) stack.getItem()).getBlock().defaultBlockState();
        int score = WhirlisprigTile.getScore(state);
        if (score > 0 && getTile() != null && getTile().scoreMap != null && getTile().scoreMap.get(state) != null && getTile().scoreMap.get(state) >= 50) {
            PortUtil.sendMessage(player, Component.translatable("whirlisprig.toomuch"));
            return InteractionResult.SUCCESS;
        }

        if (score == 0) {
            PortUtil.sendMessage(player, Component.translatable("whirlisprig.notinterested"));
        }
        if (score == 1) {
            PortUtil.sendMessage(player, Component.translatable("whirlisprig.likes"));
        }

        if (score == 2) {
            PortUtil.sendMessage(player, Component.translatable("whirlisprig.excited"));
        }
        return InteractionResult.SUCCESS;
    }

    public Whirlisprig(EntityType<? extends AbstractFlyingCreature> type, Level worldIn) {
        super(type, worldIn);
        MinecraftForge.EVENT_BUS.register(this);
        this.moveControl = new FlyingMoveControl(this, 10, true);
        addGoalsAfterConstructor();
    }

    public Whirlisprig(Level world, boolean isTamed, BlockPos pos) {
        super(ModEntities.WHIRLISPRIG_TYPE.get(), world);
        MinecraftForge.EVENT_BUS.register(this);
        this.moveControl = new FlyingMoveControl(this, 10, true);
        this.entityData.set(TAMED, isTamed);
        this.flowerPos = pos;
        addGoalsAfterConstructor();
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide) {
            if (level.getGameTime() % 20 == 0 && this.blockPosition().getY() < this.level.getMinBuildHeight()) {
                this.remove(RemovalReason.DISCARDED);
                return;
            }

            this.timeSinceBonemeal++;
            this.timeSinceGen++;
            if (level.getGameTime() % 20 == 0 && flowerPos != null && isTamed() && getTile() != null) {
                this.entityData.set(MOOD_SCORE, getTile().moodScore);
                this.diversityScore = getTile().diversityScore;
            }
        }

        if (!level.isClientSide && level.getGameTime() % 60 == 0 && isTamed() && flowerPos != null && !(level.getBlockEntity(flowerPos) instanceof WhirlisprigTile)) {
            this.hurt(DamageSource.playerAttack(ANFakePlayer.getPlayer((ServerLevel) level)), 99);
            return;
        }

        if (this.droppingShards) {
            tamingTime++;
            if (tamingTime % 20 == 0 && !level.isClientSide())
                Networking.sendToNearby(level, this, new PacketANEffect(PacketANEffect.EffectType.TIMED_HELIX, blockPosition()));

            if (tamingTime > 60 && !level.isClientSide) {
                ItemStack stack = new ItemStack(ItemsRegistry.WHIRLISPRIG_SHARDS, 1 + level.random.nextInt(1));
                level.addFreshEntity(new ItemEntity(level, getX(), getY() + 0.5, getZ(), stack));
                this.remove(RemovalReason.DISCARDED);
                level.playSound(null, getX(), getY(), getZ(), SoundEvents.ILLUSIONER_MIRROR_MOVE, SoundSource.NEUTRAL, 1f, 1f);
            } else if (tamingTime > 55 && level.isClientSide) {
                for (int i = 0; i < 10; i++) {
                    double d0 = getX();
                    double d1 = getY() + 0.1;
                    double d2 = getZ();
                    level.addParticle(ParticleTypes.END_ROD, d0, d1, d2, (level.random.nextFloat() * 1 - 0.5) / 3, (level.random.nextFloat() * 1 - 0.5) / 3, (level.random.nextFloat() * 1 - 0.5) / 3);
                }
            }
        }
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

    public boolean isTamed() {
        return this.entityData.get(TAMED);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source == DamageSource.CACTUS || source == DamageSource.SWEET_BERRY_BUSH || source == DamageSource.DROWN)
            return false;
        return super.hurt(source, amount);
    }

    @Override
    public void die(DamageSource source) {
        if (!level.isClientSide && isTamed()) {
            ItemStack stack = new ItemStack(ItemsRegistry.WHIRLISPRIG_CHARM);
            level.addFreshEntity(new ItemEntity(level, getX(), getY(), getZ(), stack));
        }
        super.die(source);
    }

    //MOJANG MAKES THIS SO CURSED WHAT THE HECK

    public List<WrappedGoal> getTamedGoals() {
        List<WrappedGoal> list = new ArrayList<>();
        list.add(new WrappedGoal(3, new RandomLookAroundGoal(this)));
        list.add(new WrappedGoal(2, new BonemealGoal(this, () -> this.flowerPos, 10)));
        list.add(new WrappedGoal(2, new InspectPlantGoal(this, () -> this.flowerPos, 15)));
        list.add(new WrappedGoal(1, new GoBackHomeGoal(this, () -> this.flowerPos, 20)));
        list.add(new WrappedGoal(0, new FloatGoal(this)));
        return list;
    }

    public List<WrappedGoal> getUntamedGoals() {
        List<WrappedGoal> list = new ArrayList<>();
        list.add(new WrappedGoal(3, new FollowMobGoalBackoff(this, 1.0D, 3.0F, 7.0F, 0.5f)));
        list.add(new WrappedGoal(5, new FollowPlayerGoal(this, 1.0D, 3.0F, 7.0F)));
        list.add(new WrappedGoal(2, new RandomLookAroundGoal(this)));
        list.add(new WrappedGoal(2, new WaterAvoidingRandomFlyingGoal(this, 1.0D)));
        list.add(new WrappedGoal(1, new BonemealGoal(this)));
        list.add(new WrappedGoal(0, new FloatGoal(this)));
        return list;
    }

    public WhirlisprigTile getTile() {
        if (this.flowerPos == null || !(level.getBlockEntity(flowerPos) instanceof WhirlisprigTile)) {
            return null;
        }
        return (WhirlisprigTile) level.getBlockEntity(flowerPos);
    }

    @SubscribeEvent
    public void treeGrow(SaplingGrowTreeEvent event) {
        if (!this.entityData.get(TAMED) && BlockUtil.distanceFrom(this.blockPosition(), event.getPos()) <= 10) {
            this.droppingShards = true;
        }
    }

    @Override
    protected void registerGoals() { /*Do not use. See above*/}

    @Override
    public void getTooltip(List<Component> tooltip) {
        if (!this.entityData.get(TAMED))
            return;
        int mood = this.entityData.get(MOOD_SCORE);
        String moodStr = Component.translatable("ars_nouveau.whirlisprig.tooltip_unhappy").getString();
        if (mood >= 1000)
            moodStr = Component.translatable("ars_nouveau.whirlisprig.tooltip_extremely_happy").getString();
        else if (mood >= 750)
            moodStr = Component.translatable("ars_nouveau.whirlisprig.tooltip_very_happy").getString();
        else if (mood >= 500)
            moodStr = Component.translatable("ars_nouveau.whirlisprig.tooltip_happy").getString();
        else if (mood >= 250)
            moodStr = Component.translatable("ars_nouveau.whirlisprig.tooltip_content").getString();
        tooltip.add(Component.literal(Component.translatable("ars_nouveau.whirlisprig.tooltip_mood").getString() + moodStr));
    }

    @Override
    public boolean removeWhenFarAway(double p_213397_1_) {
        return false;
    }

    public static AttributeSupplier.Builder attributes() {
        return Mob.createMobAttributes().add(Attributes.FLYING_SPEED, Attributes.FLYING_SPEED.getDefaultValue())
                .add(Attributes.MAX_HEALTH, 6.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D);
    }

    @Override
    protected @NotNull PathNavigation createNavigation(Level world) {
        FlyingPathNavigation flyingpathnavigator = new FlyingPathNavigation(this, world);
        flyingpathnavigator.setCanOpenDoors(false);
        flyingpathnavigator.setCanFloat(true);
        flyingpathnavigator.setCanPassDoors(true);
        return flyingpathnavigator;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("summoner_x"))
            flowerPos = new BlockPos(tag.getInt("summoner_x"), tag.getInt("summoner_y"), tag.getInt("summoner_z"));
        timeSinceBonemeal = tag.getInt("bonemeal");
        this.entityData.set(TAMED, tag.getBoolean("tamed"));
        this.entityData.set(Whirlisprig.MOOD_SCORE, tag.getInt("score"));
        if (!setBehaviors) {
            tryResetGoals();
            setBehaviors = true;
        }
        ignoreItems = NBTUtil.readItems(tag, "ignored_");
        this.entityData.set(COLOR, tag.getString("color"));
        this.timeSinceGen = tag.getInt("genTime");
    }

    // A workaround for goals not registering correctly for a dynamic variable on reload as read() is called after constructor.
    public void tryResetGoals() {
        this.goalSelector.availableGoals = new LinkedHashSet<>();
        this.addGoalsAfterConstructor();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (flowerPos != null) {
            tag.putInt("summoner_x", flowerPos.getX());
            tag.putInt("summoner_y", flowerPos.getY());
            tag.putInt("summoner_z", flowerPos.getZ());
        }
        tag.putInt("bonemeal", timeSinceBonemeal);
        tag.putBoolean("tamed", this.entityData.get(TAMED));
        tag.putInt("score", this.entityData.get(Whirlisprig.MOOD_SCORE));
        tag.putString("color", this.entityData.get(COLOR));
        tag.putInt("genTime", timeSinceGen);
        if (ignoreItems != null && !ignoreItems.isEmpty())
            NBTUtil.writeItems(tag, "ignored_", ignoreItems);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(MOOD_SCORE, 0);
        this.entityData.define(TAMED, false);
        this.entityData.define(COLOR, "summer");
    }

    @Override
    public boolean onDispel(@Nullable LivingEntity caster) {
        if (this.isRemoved())
            return false;

        if (!level.isClientSide && isTamed()) {
            ItemStack stack = new ItemStack(ItemsRegistry.WHIRLISPRIG_CHARM);
            level.addFreshEntity(new ItemEntity(level, getX(), getY(), getZ(), stack));
            ParticleUtil.spawnPoof((ServerLevel) level, blockPosition());
            this.remove(RemovalReason.DISCARDED);
        }
        return this.isTamed();
    }

    @Override
    public ResourceLocation getTexture(LivingEntity entity) {
        return new ResourceLocation(ArsNouveau.MODID, "textures/entity/sylph_" + (getColor().isEmpty() ? "summer" : getColor()) + ".png");
    }
}