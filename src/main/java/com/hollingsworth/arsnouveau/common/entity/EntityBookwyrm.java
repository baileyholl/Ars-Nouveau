package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.client.IVariantColorProvider;
import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.api.familiar.PersistentFamiliarData;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.api.util.SummonUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.tile.StorageLecternTile;
import com.hollingsworth.arsnouveau.common.entity.goal.bookwyrm.RandomStorageVisitGoal;
import com.hollingsworth.arsnouveau.common.entity.goal.bookwyrm.TransferGoal;
import com.hollingsworth.arsnouveau.common.entity.goal.bookwyrm.TransferTask;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntityBookwyrm extends FlyingMob implements IDispellable, ITooltipProvider, IWandable, IAnimatable, IVariantColorProvider<EntityBookwyrm> {

    public static final EntityDataAccessor<ItemStack> HELD_ITEM = SynchedEntityData.defineId(EntityBookwyrm.class, EntityDataSerializers.ITEM_STACK);
    public static final EntityDataAccessor<String> COLOR = SynchedEntityData.defineId(EntityBookwyrm.class, EntityDataSerializers.STRING);

    public BlockPos lecternPos;
    public int backoffTicks;

    protected EntityBookwyrm(EntityType<? extends FlyingMob> p_i48568_1_, Level p_i48568_2_) {
        super(p_i48568_1_, p_i48568_2_);
        this.moveControl = new FlyingMoveControl(this, 10, true);
    }

    public EntityBookwyrm(Level p_i50190_2_) {
        super(ModEntities.ENTITY_BOOKWYRM_TYPE.get(), p_i50190_2_);
        this.moveControl = new FlyingMoveControl(this, 10, true);
    }

    public EntityBookwyrm(Level world, BlockPos lecternPos) {
        this(world);
        this.lecternPos = lecternPos;
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND || player.getCommandSenderWorld().isClientSide)
            return InteractionResult.SUCCESS;

        ItemStack stack = player.getItemInHand(hand);

        if (player.getMainHandItem().is(Tags.Items.DYES)) {
            DyeColor color = DyeColor.getColor(stack);
            if (color == null || this.entityData.get(COLOR).equals(color.getName()) || !Arrays.asList(COLORS).contains(color.getName()))
                return InteractionResult.SUCCESS;
            setColor(color.getName(), this);
            player.getMainHandItem().shrink(1);
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public void tick() {
        super.tick();
        if (level == null || this.dead || lecternPos == null)
            return;
        if (!level.isClientSide) {
            if (backoffTicks >= 0)
                backoffTicks--;
        }

        if (level.getGameTime() % 20 == 0) {
            if (!(level.getBlockEntity(lecternPos) instanceof StorageLecternTile)) {
                if (!level.isClientSide) {
                    this.hurt(DamageSource.playerAttack(ANFakePlayer.getPlayer((ServerLevel) level)), 99);
                }
            }
        }
    }

    @Override
    public boolean canCollideWith(Entity pEntity) {
        if(pEntity instanceof Player)
            return false;
        return super.canCollideWith(pEntity);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void pushEntities() {}

    @Override
    public boolean hurt(@NotNull DamageSource source, float p_70097_2_) {
        if (!SummonUtil.canSummonTakeDamage(source))
            return false;
        return super.hurt(source, p_70097_2_);
    }

    @Override
    protected PathNavigation createNavigation(Level world) {
        FlyingPathNavigation flyingpathnavigator = new FlyingPathNavigation(this, world);
        flyingpathnavigator.setCanOpenDoors(false);
        flyingpathnavigator.setCanFloat(true);
        flyingpathnavigator.setCanPassDoors(true);
        return flyingpathnavigator;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new TransferGoal(this));
        this.goalSelector.addGoal(4, new RandomStorageVisitGoal(this, () ->{
            StorageLecternTile tile = getTile();
            if(tile == null){
                return null;
            }
            List<BlockPos> targets = new ArrayList<>(tile.connectedInventories);
            targets.add(tile.getBlockPos());
            return targets.get(level.random.nextInt(targets.size())).above();
        }));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    public TransferTask getTransferTask() {
        StorageLecternTile tile = getTile();
        if(tile != null){
            return tile.getTransferTask();
        }
        return null;
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
    }

    @Override
    public boolean onDispel(@Nullable LivingEntity caster) {
        if (this.isRemoved())
            return false;

        if (!level.isClientSide) {
            level.addFreshEntity(new ItemEntity(level, getX(), getY(), getZ(), toCharm()));
            ParticleUtil.spawnPoof((ServerLevel) level, blockPosition());
            this.remove(RemovalReason.DISCARDED);
        }
        return true;
    }

    @Override
    public void remove(RemovalReason pReason) {
        super.remove(pReason);
        StorageLecternTile tile = getTile();
        if(tile != null){
            tile.removeBookwyrm(this);
        }
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.ENTITY_BOOKWYRM_TYPE.get();
    }

    public ItemStack toCharm(){
        ItemStack stack = new ItemStack(ItemsRegistry.BOOKWYRM_CHARM.get());
        PersistentFamiliarData<EntityBookwyrm> data = new PersistentFamiliarData<>(new CompoundTag());
        data.color = getColor(this);
        data.name = getCustomName();
        stack.setTag(data.toTag(this, new CompoundTag()));
        return stack;
    }

    public void readCharm(ItemStack stack){
        if(stack.hasTag()) {
            PersistentFamiliarData<EntityBookwyrm> data = new PersistentFamiliarData<>(stack.getOrCreateTag());
            setColor(data.color, this);
            setCustomName(data.name);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (lecternPos != null) {
            tag.putLong("lectern", lecternPos.asLong());
        }

        if (!getHeldStack().isEmpty()) {
            CompoundTag itemTag = new CompoundTag();
            getHeldStack().save(itemTag);
            tag.put("held", itemTag);
        }
        tag.putInt("backoff", backoffTicks);
        tag.putString("color", this.entityData.get(COLOR));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("lectern")){
            lecternPos = BlockPos.of(tag.getLong("lectern"));
        }
        if (tag.contains("held"))
            setHeldStack(ItemStack.of((CompoundTag) tag.get("held")));

        this.backoffTicks = tag.getInt("backoff");
        if (tag.contains("color"))
            this.entityData.set(COLOR, tag.getString("color"));

    }


    public @Nullable StorageLecternTile getTile() {
        return lecternPos == null || !(level.getBlockEntity(lecternPos) instanceof StorageLecternTile) ? null : (StorageLecternTile) level.getBlockEntity(lecternPos);
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "walkController", 1, this::idle));
    }

    public PlayState idle(AnimationEvent<?> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("fly"));
        return PlayState.CONTINUE;
    }

    AnimationFactory factory = GeckoLibUtil.createFactory(this);

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    @Override
    public int getExperienceReward() {
        return 0;
    }

    @Override
    public boolean removeWhenFarAway(double p_213397_1_) {
        return false;
    }

    public @NotNull ItemStack getHeldStack() {
        return this.entityData.get(HELD_ITEM);
    }

    public void setHeldStack(ItemStack stack) {
        this.entityData.set(HELD_ITEM, stack);
    }

    @Override
    public void die(DamageSource source) {
        if (!level.isClientSide) {
            level.addFreshEntity(new ItemEntity(level, getX(), getY(), getZ(), toCharm()));
        }

        super.die(source);
    }

    public static AttributeSupplier.Builder attributes() {
        return Mob.createMobAttributes().add(Attributes.FLYING_SPEED, Attributes.FLYING_SPEED.getDefaultValue())
                .add(Attributes.MAX_HEALTH, 6.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HELD_ITEM, ItemStack.EMPTY);
        this.entityData.define(COLOR, "blue");
    }

    public static String[] COLORS = {"purple", "green", "blue", "black", "red", "white"};

    @Override
    public ResourceLocation getTexture(EntityBookwyrm entity) {
        String color = getColor(entity).toLowerCase();
        if (color.isEmpty())
            color = "blue";
        return new ResourceLocation(ArsNouveau.MODID, "textures/entity/book_wyrm_" + color + ".png");
    }

    @Override
    public String getColor(EntityBookwyrm entityBookwyrm) {
        return getEntityData().get(EntityBookwyrm.COLOR);
    }

    @Override
    public void setColor(String color, EntityBookwyrm entityBookwyrm) {
        getEntityData().set(EntityBookwyrm.COLOR, color);
    }
}
