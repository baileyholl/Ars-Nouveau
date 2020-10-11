package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.spell.IPickupResponder;
import com.hollingsworth.arsnouveau.common.entity.goal.*;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.ai.goal.FollowMobGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import software.bernie.geckolib.animation.builder.AnimationBuilder;
import software.bernie.geckolib.animation.controller.EntityAnimationController;
import software.bernie.geckolib.entity.IAnimatedEntity;
import software.bernie.geckolib.event.AnimationTestEvent;
import software.bernie.geckolib.manager.EntityAnimationManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EntitySylph extends AbstractFlyingCreature implements IPickupResponder, IAnimatedEntity, ITooltipProvider {
    EntityAnimationManager manager = new EntityAnimationManager();

    EntityAnimationController<EntitySylph> idleController = new EntityAnimationController<>(this, "idleController", 20, this::idlePredicate);

    public int timeSinceBonemeal = 0;
    public static final DataParameter<Boolean> TAMED = EntityDataManager.createKey(EntitySylph.class, DataSerializers.BOOLEAN);
    public static final DataParameter<Integer> MOOD_SCORE = EntityDataManager.createKey(EntitySylph.class, DataSerializers.VARINT);

    public int timeSinceGather = 0;
    public int timeUntilEvaluation = 0;
    public int diversityScore;
    public Map<Block, Integer> genTable;
    public BlockPos crystalPos;
    private boolean setBehaviors;
    private <E extends Entity> boolean idlePredicate(AnimationTestEvent<E> event) {
        manager.setAnimationSpeed(1.0f);
        idleController.setAnimation(new AnimationBuilder().addAnimation("idle"));
        return true;
    }

    protected EntitySylph(EntityType<? extends AbstractFlyingCreature> type, World worldIn) {
        super(type, worldIn);
        this.moveController =  new FlyingMovementController(this, 10, true);
        setupAnimations();
        addGoalsAfterConstructor();
    }

    public EntitySylph(World world, boolean isTamed, BlockPos pos) {
        super(ModEntities.ENTITY_SYLPH_TYPE, world);
        this.moveController =  new FlyingMovementController(this, 10, true);
        this.dataManager.set(TAMED, isTamed);
        this.crystalPos = pos;
        setupAnimations();
        addGoalsAfterConstructor();
    }

    public void setupAnimations(){
        manager.addAnimationController(idleController);
    }

    @Override
    public void tick() {
        super.tick();
        if(!this.world.isRemote){
            if(Boolean.TRUE.equals(this.dataManager.get(TAMED))){
                this.timeUntilEvaluation--;
            }
            this.timeSinceBonemeal++;
        }
    }

    // Cannot add conditional goals in RegisterGoals as it is final and called during the MobEntity super.
    protected void addGoalsAfterConstructor(){
        if(this.world.isRemote())
            return;

        for(PrioritizedGoal goal : getGoals()){
            this.goalSelector.addGoal(goal.getPriority(), goal.getGoal());
        }
    }

    public List<PrioritizedGoal> getGoals(){
        return this.dataManager.get(TAMED) ? getTamedGoals() : getUntamedGoals();
    }

    //MOJANG MAKES THIS SO CURSED WHAT THE HECK

    public List<PrioritizedGoal> getTamedGoals(){
        List<PrioritizedGoal> list = new ArrayList<>();
        list.add(new PrioritizedGoal(3, new LookRandomlyGoal(this)));
        list.add(new PrioritizedGoal(2, new BonemealGoal(this, () -> this.crystalPos, 10)));
        list.add(new PrioritizedGoal(1, new EvaluateGroveGoal(this, 60)));
        list.add(new PrioritizedGoal(2, new InspectPlantGoal(this, () -> this.crystalPos,15)));
        list.add(new PrioritizedGoal(1, new GoBackHomeGoal(this, () -> this.crystalPos,20)));
        return list;
    }

    public List<PrioritizedGoal> getUntamedGoals(){
        List<PrioritizedGoal> list = new ArrayList<>();
        list.add(new PrioritizedGoal(3, new FollowMobGoal(this, 1.0D, 3.0F, 7.0F)));
        list.add(new PrioritizedGoal(5, new FollowPlayerGoal(this, 1.0D, 3.0F, 7.0F)));
        list.add(new PrioritizedGoal(2, new LookRandomlyGoal(this)));
        list.add(new PrioritizedGoal(2, new WaterAvoidingRandomFlyingGoal(this, 1.0D)));
        list.add(new PrioritizedGoal(1, new BonemealGoal(this)));
        return list;
    }

    @Override
    protected void registerGoals() { /*Do not use. See above*/}

    @Override
    public List<String> getTooltip() {
        List<String> tooltip = new ArrayList<>();
        if(!this.dataManager.get(TAMED))
            return tooltip;
        tooltip.add("Mood: " + this.dataManager.get(MOOD_SCORE));
        return tooltip;
    }


    @Override
    public ItemStack onPickup(ItemStack stack) {
        return null;
    }

    @Override
    public EntityAnimationManager getAnimationManager() {
        return manager;
    }

    @Override
    public boolean canDespawn(double p_213397_1_) {
        return false;
    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttributes().registerAttribute(SharedMonsterAttributes.FLYING_SPEED);
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(6.0D);
        this.getAttribute(SharedMonsterAttributes.FLYING_SPEED).setBaseValue((double)0.4F);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double)0.2F);
    }

    @Override
    protected PathNavigator createNavigator(World world) {
        FlyingPathNavigator flyingpathnavigator = new FlyingPathNavigator(this, world);
        flyingpathnavigator.setCanOpenDoors(false);
        flyingpathnavigator.setCanSwim(true);
        flyingpathnavigator.setCanEnterDoors(true);
        return flyingpathnavigator;
    }

    @Override
    public void readAdditional(CompoundNBT tag) {
        super.readAdditional(tag);
        if(tag.contains("summoner_x"))
            crystalPos = new BlockPos(tag.getInt("summoner_x"), tag.getInt("summoner_y"), tag.getInt("summoner_z"));
        timeSinceBonemeal = tag.getInt("bonemeal");
        timeSinceGather = tag.getInt("gather");
        timeUntilEvaluation = tag.getInt("eval");
        this.dataManager.set(TAMED, tag.getBoolean("tamed"));
        this.dataManager.set(EntitySylph.MOOD_SCORE, tag.getInt("score"));
        if(!setBehaviors){
            tryResetGoals();
            setBehaviors = true;
        }

    }
    // A workaround for goals not registering correctly for a dynamic variable on reload as read() is called after constructor.
    public void tryResetGoals(){
        List<PrioritizedGoal> goals = getGoals();
        for(PrioritizedGoal g : goals){
            this.goalSelector.removeGoal(g.getGoal());
        }
        this.addGoalsAfterConstructor();
    }

    @Override
    public void writeAdditional(CompoundNBT tag) {
        super.writeAdditional(tag);
        if(crystalPos != null){
            tag.putInt("summoner_x", crystalPos.getX());
            tag.putInt("summoner_y", crystalPos.getY());
            tag.putInt("summoner_z", crystalPos.getZ());
        }
        tag.putInt("eval", timeUntilEvaluation);
        tag.putInt("bonemeal", timeSinceBonemeal);
        tag.putInt("gather", timeSinceGather);
        tag.putBoolean("tamed", this.dataManager.get(TAMED));
        tag.putInt("score", this.dataManager.get(EntitySylph.MOOD_SCORE));
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(MOOD_SCORE, 0);
        this.dataManager.register(TAMED, false);
    }
}