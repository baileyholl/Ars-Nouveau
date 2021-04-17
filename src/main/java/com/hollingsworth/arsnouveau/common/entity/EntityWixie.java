package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.tile.IAnimationListener;
import com.hollingsworth.arsnouveau.common.block.tile.WixieCauldronTile;
import com.hollingsworth.arsnouveau.common.entity.goal.wixie.CompleteCraftingGoal;
import com.hollingsworth.arsnouveau.common.entity.goal.wixie.FindNextItemGoal;
import com.hollingsworth.arsnouveau.common.entity.goal.wixie.FindPotionGoal;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
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
import java.util.List;

public class EntityWixie extends AbstractFlyingCreature implements IAnimatable, IAnimationListener, IDispellable {
    AnimationFactory manager = new AnimationFactory(this);

    public static final DataParameter<Boolean> TAMED = EntityDataManager.defineId(EntityWixie.class, DataSerializers.BOOLEAN);

    public BlockPos cauldronPos;
    public int inventoryBackoff;

    private <P extends IAnimatable> PlayState idlePredicate(AnimationEvent<P> event) {
        if(getNavigation().isInProgress())
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
    protected int getExperienceReward(PlayerEntity player) {
        return 0;
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<EntityWixie>(this, "idleController", 20, this::idlePredicate));
        animationData.addAnimationController(new AnimationController<EntityWixie>(this, "castController", 1, this::castPredicate));
        animationData.addAnimationController(new AnimationController<EntityWixie>(this, "summonController", 1, this::summonPredicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return manager;
    }

    protected EntityWixie(EntityType<? extends AbstractFlyingCreature> type, World worldIn) {
        super(type, worldIn);
        MinecraftForge.EVENT_BUS.register(this);
        this.moveControl =  new FlyingMovementController(this, 10, true);
        addGoalsAfterConstructor();
    }

    public EntityWixie(World world, boolean isTamed, BlockPos pos) {
        super(ModEntities.ENTITY_WIXIE_TYPE, world);
        MinecraftForge.EVENT_BUS.register(this);
        this.cauldronPos = pos;
        this.moveControl =  new FlyingMovementController(this, 10, true);
        this.entityData.set(TAMED, isTamed);
        addGoalsAfterConstructor();
    }

    @Override
    public void tick() {
        super.tick();
        if(!level.isClientSide && (cauldronPos == null || !(level.getBlockEntity(cauldronPos) instanceof WixieCauldronTile)))
            this.hurt(DamageSource.playerAttack(FakePlayerFactory.getMinecraft((ServerWorld)level)), 99);
        if(!level.isClientSide && inventoryBackoff > 0){
            inventoryBackoff--;
        }

    }

    //MOJANG MAKES THIS SO CURSED WHAT THE HECK
    public List<PrioritizedGoal> getTamedGoals(){
        List<PrioritizedGoal> list = new ArrayList<>();
        list.add(new PrioritizedGoal(3, new LookRandomlyGoal(this)));
        list.add(new PrioritizedGoal(2, new FindNextItemGoal(this)));
        list.add(new PrioritizedGoal(2, new FindPotionGoal(this)));
        list.add(new PrioritizedGoal(1, new CompleteCraftingGoal(this)));
        return list;
    }

    public List<PrioritizedGoal> getUntamedGoals(){
        List<PrioritizedGoal> list = new ArrayList<>();
        list.add(new PrioritizedGoal(3, new LookRandomlyGoal(this)));
        list.add(new PrioritizedGoal(2, new FindNextItemGoal(this)));
        list.add(new PrioritizedGoal(2, new FindPotionGoal(this)));
        list.add(new PrioritizedGoal(1, new CompleteCraftingGoal(this)));
        return list;
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
        this.entityData.define(TAMED, false);
    }

    @Override
    public boolean removeWhenFarAway(double p_213397_1_) {
        return false;
    }

    @Override
    protected PathNavigator createNavigation(World world) {
        FlyingPathNavigator flyingpathnavigator = new FlyingPathNavigator(this, world);
        flyingpathnavigator.setCanOpenDoors(false);
        flyingpathnavigator.setCanFloat(true);
        flyingpathnavigator.setCanPassDoors(true);
        return flyingpathnavigator;
    }
    public static AttributeModifierMap.MutableAttribute attributes() {
        return MobEntity.createMobAttributes().add(Attributes.FLYING_SPEED, Attributes.FLYING_SPEED.getDefaultValue())
                .add(Attributes.MAX_HEALTH, 6.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT tag) {
        super.readAdditionalSaveData(tag);
        if(tag.contains("summoner_x"))
            cauldronPos = new BlockPos(tag.getInt("summoner_x"), tag.getInt("summoner_y"), tag.getInt("summoner_z"));

        this.entityData.set(TAMED, tag.getBoolean("tamed"));
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT tag) {
        super.addAdditionalSaveData(tag);
        if(cauldronPos != null){
            tag.putInt("summoner_x", cauldronPos.getX());
            tag.putInt("summoner_y", cauldronPos.getY());
            tag.putInt("summoner_z", cauldronPos.getZ());
        }
        tag.putBoolean("tamed", this.entityData.get(TAMED));
    }

    @Override
    public void startAnimation(int arg) {
        if(arg == Animations.CAST.ordinal()){
            AnimationController controller = this.manager.getOrCreateAnimationData(this.hashCode()).getAnimationControllers().get("castController");
            controller.markNeedsReload();
            controller.setAnimation(new AnimationBuilder().addAnimation("cast", false));
        }else if(arg == Animations.SUMMON_ITEM.ordinal()){
            AnimationController controller = this.manager.getOrCreateAnimationData(this.hashCode()).getAnimationControllers().get("summonController");
            controller.markNeedsReload();
            controller.setAnimation(new AnimationBuilder().addAnimation("summon_item", false));
        }
    }
    @Override
    public void die(DamageSource source) {
        if(!level.isClientSide ){
            ItemStack stack = new ItemStack(ItemsRegistry.WIXIE_CHARM);
            level.addFreshEntity(new ItemEntity(level, getX(), getY(), getZ(), stack));

        }
        super.die(source);
    }
    @Override
    public boolean onDispel(@Nullable LivingEntity caster) {
        if(this.removed)
            return false;

        if(!level.isClientSide ){
            ItemStack stack = new ItemStack(ItemsRegistry.WIXIE_CHARM);
            level.addFreshEntity(new ItemEntity(level, getX(), getY(), getZ(), stack.copy()));
            ParticleUtil.spawnPoof((ServerWorld)level, blockPosition());
            this.remove();
        }
        return true;
    }

    public enum Animations{
        CAST,
        SUMMON_ITEM
    }
}
