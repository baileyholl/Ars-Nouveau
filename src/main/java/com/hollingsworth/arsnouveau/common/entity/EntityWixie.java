package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.tile.IAnimationListener;
import com.hollingsworth.arsnouveau.common.block.tile.WixieCauldronTile;
import com.hollingsworth.arsnouveau.common.entity.goal.wixie.CompleteCraftingGoal;
import com.hollingsworth.arsnouveau.common.entity.goal.wixie.FindNextItemGoal;
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

    public static final DataParameter<Boolean> TAMED = EntityDataManager.createKey(EntityWixie.class, DataSerializers.BOOLEAN);

    public BlockPos cauldronPos;
    public int inventoryBackoff;

    private <P extends IAnimatable> PlayState idlePredicate(AnimationEvent<P> event) {
        if(getNavigator().hasPath())
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
    protected int getExperiencePoints(PlayerEntity player) {
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
        this.moveController =  new FlyingMovementController(this, 10, true);
        addGoalsAfterConstructor();
    }

    public EntityWixie(World world, boolean isTamed, BlockPos pos) {
        super(ModEntities.ENTITY_WIXIE_TYPE, world);
        MinecraftForge.EVENT_BUS.register(this);
        this.cauldronPos = pos;
        this.moveController =  new FlyingMovementController(this, 10, true);
        this.dataManager.set(TAMED, isTamed);
        addGoalsAfterConstructor();
    }

    @Override
    public void tick() {
        super.tick();
        if(!world.isRemote && (cauldronPos == null || !(world.getTileEntity(cauldronPos) instanceof WixieCauldronTile)))
            this.attackEntityFrom(DamageSource.causePlayerDamage(FakePlayerFactory.getMinecraft((ServerWorld)world)), 99);
        if(!world.isRemote && inventoryBackoff > 0){
            inventoryBackoff--;
        }

    }

    //MOJANG MAKES THIS SO CURSED WHAT THE HECK
    public List<PrioritizedGoal> getTamedGoals(){
        List<PrioritizedGoal> list = new ArrayList<>();
        list.add(new PrioritizedGoal(3, new LookRandomlyGoal(this)));
        list.add(new PrioritizedGoal(2, new FindNextItemGoal(this)));
        list.add(new PrioritizedGoal(1, new CompleteCraftingGoal(this)));
        return list;
    }

    public List<PrioritizedGoal> getUntamedGoals(){
        List<PrioritizedGoal> list = new ArrayList<>();
        list.add(new PrioritizedGoal(3, new LookRandomlyGoal(this)));
        list.add(new PrioritizedGoal(2, new FindNextItemGoal(this)));
        list.add(new PrioritizedGoal(1, new CompleteCraftingGoal(this)));
        return list;
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

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(TAMED, false);
    }

    @Override
    public boolean canDespawn(double p_213397_1_) {
        return false;
    }

    @Override
    protected PathNavigator createNavigator(World world) {
        FlyingPathNavigator flyingpathnavigator = new FlyingPathNavigator(this, world);
        flyingpathnavigator.setCanOpenDoors(false);
        flyingpathnavigator.setCanSwim(true);
        flyingpathnavigator.setCanEnterDoors(true);
        return flyingpathnavigator;
    }
    public static AttributeModifierMap.MutableAttribute attributes() {
        return MobEntity.func_233666_p_().createMutableAttribute(Attributes.FLYING_SPEED, Attributes.FLYING_SPEED.getDefaultValue())
                .createMutableAttribute(Attributes.MAX_HEALTH, 6.0D)
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.2D);
    }

    @Override
    public void readAdditional(CompoundNBT tag) {
        super.readAdditional(tag);
        if(tag.contains("summoner_x"))
            cauldronPos = new BlockPos(tag.getInt("summoner_x"), tag.getInt("summoner_y"), tag.getInt("summoner_z"));

        this.dataManager.set(TAMED, tag.getBoolean("tamed"));
    }

    @Override
    public void writeAdditional(CompoundNBT tag) {
        super.writeAdditional(tag);
        if(cauldronPos != null){
            tag.putInt("summoner_x", cauldronPos.getX());
            tag.putInt("summoner_y", cauldronPos.getY());
            tag.putInt("summoner_z", cauldronPos.getZ());
        }
        tag.putBoolean("tamed", this.dataManager.get(TAMED));
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
    public void onDeath(DamageSource source) {
        if(!world.isRemote ){
            ItemStack stack = new ItemStack(ItemsRegistry.WIXIE_CHARM);
            world.addEntity(new ItemEntity(world, getPosX(), getPosY(), getPosZ(), stack));

        }
        super.onDeath(source);
    }
    @Override
    public boolean onDispel(@Nullable LivingEntity caster) {
        if(this.removed)
            return false;

        if(!world.isRemote ){
            ItemStack stack = new ItemStack(ItemsRegistry.WIXIE_CHARM);
            world.addEntity(new ItemEntity(world, getPosX(), getPosY(), getPosZ(), stack.copy()));
            ParticleUtil.spawnPoof((ServerWorld)world, getPosition());
            this.remove();
        }
        return true;
    }

    public enum Animations{
        CAST,
        SUMMON_ITEM
    }
}
