package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.common.entity.goal.GoBackHomeGoal;
import com.hollingsworth.arsnouveau.common.entity.goal.amethyst_golem.ConvertBuddingGoal;
import com.hollingsworth.arsnouveau.common.entity.goal.amethyst_golem.GrowClusterGoal;
import com.hollingsworth.arsnouveau.common.entity.goal.amethyst_golem.HarvestClusterGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.List;
import java.util.Optional;

public class AmethystGolem  extends PathfinderMob implements IAnimatable, IDispellable, ITooltipProvider, IWandable {
    public static final EntityDataAccessor<Optional<BlockPos>> HOME = SynchedEntityData.defineId(WealdWalker.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    int growCooldown;
    int convertCooldown;
    int harvestCooldown;

    protected AmethystGolem(EntityType<? extends PathfinderMob> p_21683_, Level p_21684_) {
        super(p_21683_, p_21684_);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(3, new GoBackHomeGoal(this, this::getHome, 10, () -> true));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(2, new ConvertBuddingGoal(this, () -> convertCooldown <= 0 && getHome() != null));
        this.goalSelector.addGoal(2, new GrowClusterGoal(this, () -> growCooldown <= 0 && getHome() != null));
        this.goalSelector.addGoal(2, new HarvestClusterGoal(this, () -> harvestCooldown <= 0 && getHome() != null));
    }

    @Override
    public void tick() {
        super.tick();
        if(harvestCooldown > 0)
            harvestCooldown--;
        if(growCooldown > 0)
            growCooldown--;
        if(convertCooldown > 0)
            convertCooldown--;
    }

    @Override
    public void getTooltip(List<Component> tooltip) {

    }

    @Override
    public boolean onDispel(@Nullable LivingEntity caster) {
        return false;
    }


    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        NBTUtil.storeBlockPos(tag, "home", getHome());
        tag.putInt("grow", growCooldown);
        tag.putInt("convert", convertCooldown);
        tag.putInt("harvest",harvestCooldown);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if(NBTUtil.hasBlockPos(tag, "home")){
            setHome(NBTUtil.getBlockPos(tag, "home"));
        }
        this.growCooldown = tag.getInt("grow");
        this.convertCooldown = tag.getInt("convert");
        this.harvestCooldown = tag.getInt("harvest");
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this,"run_controller", 1.0f, this::runController));
        data.addAnimationController(new AnimationController(this,"attack_controller", 5f, this::attackController));
    }
    private PlayState attackController(AnimationEvent animationEvent) {
        return PlayState.CONTINUE;
    }

    private PlayState runController(AnimationEvent animationEvent) {
        if(animationEvent.getController().getCurrentAnimation() != null && !(animationEvent.getController().getCurrentAnimation().animationName.equals("run"))) {
            return PlayState.STOP;
        }
        if(animationEvent.isMoving()){
            animationEvent.getController().setAnimation(new AnimationBuilder().addAnimation("run"));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    public void setHome(BlockPos home){
        this.entityData.set(HOME, Optional.of(home));
    }

    public @Nullable BlockPos getHome(){
        return this.entityData.get(HOME).orElse(null);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HOME, Optional.empty());
    }

    @Override
    public boolean removeWhenFarAway(double p_213397_1_) {
        return false;
    }

    @Override
    protected int getExperienceReward(Player p_70693_1_) {
        return 0;
    }

    AnimationFactory factory = new AnimationFactory(this);

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    public static AttributeSupplier.Builder attributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 6.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2d);
    }

}
