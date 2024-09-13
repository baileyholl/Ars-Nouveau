package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.compat.PatchouliHandler;
import com.hollingsworth.arsnouveau.common.entity.debug.IDebugger;
import com.hollingsworth.arsnouveau.common.entity.debug.IDebuggerProvider;
import com.hollingsworth.arsnouveau.common.entity.statemachine.SimpleStateMachine;
import com.hollingsworth.arsnouveau.common.entity.statemachine.alakarkinos.DecideCrabActionState;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
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
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.jetbrains.annotations.NotNull;
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

public class Alakarkinos extends PathfinderMob implements GeoEntity, IDispellable, ITooltipProvider, IWandable, IDebuggerProvider{

    public boolean tamed;
    public static final EntityDataAccessor<Optional<BlockPos>> HOME = SynchedEntityData.defineId(Alakarkinos.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    public static final EntityDataAccessor<Boolean> HAS_HAT = SynchedEntityData.defineId(Alakarkinos.class, EntityDataSerializers.BOOLEAN);

    public int findBlockCooldown;

    public boolean partyCrab = false;
    public BlockPos jukeboxPos = null;
    public BlockPos hatPos = null;

    public SimpleStateMachine stateMachine = new SimpleStateMachine(new DecideCrabActionState(this));

    public Alakarkinos(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        reloadGoals();
        this.tamed = true;
    }

    @Override
    public void setRecordPlayingNearby(BlockPos pos, boolean hasSound) {
        super.setRecordPlayingNearby(pos, hasSound);
        this.partyCrab = hasSound;
        this.jukeboxPos = pos;
    }

    @Override
    public void tick() {
        super.tick();
        stateMachine.tick();
        if(findBlockCooldown > 0){
            findBlockCooldown--;
        }
    }

    // Cannot add conditional goals in RegisterGoals as it is final and called during the MobEntity super.
    protected void reloadGoals() {
        if (this.level.isClientSide())
            return;
        this.goalSelector.availableGoals.clear();
        for (WrappedGoal goal : getGoals()) {
            this.goalSelector.addGoal(goal.getPriority(), goal.getGoal());
        }
    }

    public List<WrappedGoal> getGoals() {
        List<WrappedGoal> list = new ArrayList<>();
        list.add(new WrappedGoal(0, new FloatGoal(this)));
        if (!tamed) {
            list.add(new WrappedGoal(4, new LookAtPlayerGoal(this, Player.class, 3.0F, 0.02F)));
            list.add(new WrappedGoal(4, new LookAtPlayerGoal(this, Mob.class, 8.0F)));
            list.add(new WrappedGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.2D)));
            list.add(new WrappedGoal(0, new FloatGoal(this)));
        }
        return list;
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.ALAKARKINOS_TYPE.get();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(HOME, Optional.empty());
        pBuilder.define(HAS_HAT, true);
    }

    public @Nullable BlockPos getHome(){
        return this.entityData.get(HOME).orElse(null);
    }

    @Override
    public void getTooltip(List<Component> tooltip) {

    }

    @Override
    public void onFinishedConnectionLast(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity) {
        if(playerEntity.level.getCapability(Capabilities.ItemHandler.BLOCK, storedPos, null) != null){
            this.entityData.set(HOME, Optional.ofNullable(storedPos));
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.alakarkinos.set_home"));
        }
    }

    @Override
    public boolean onDispel(@NotNull LivingEntity caster) {
        return false;
    }

    @Override
    public IDebugger getDebugger() {
        return null;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "walkController", 1, (event) -> {
            if (event.isMoving() || (level.isClientSide && PatchouliHandler.isPatchouliWorld())) {
                event.getController().setAnimation(RawAnimation.begin().thenPlay("run"));
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));

        controllers.add(new AnimationController<>(this, "idleController", 1, (event) -> {
            if (!event.isMoving()) {
                event.getController().setAnimation(RawAnimation.begin().thenPlay("idle"));
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));

        controllers.add(new AnimationController<>(this, "danceController", 1, (event) -> {
            if ((this.partyCrab && this.jukeboxPos != null && BlockUtil.distanceFrom(position, jukeboxPos) <= 8)) {
                event.getController().setAnimation(RawAnimation.begin().thenPlay("dance"));
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));
    }


    AnimatableInstanceCache manager = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return manager;
    }

    @Override
    public boolean save(CompoundTag pCompound) {
        if(this.entityData.get(HOME).isPresent()){
            pCompound.putLong("home", this.entityData.get(HOME).get().asLong());
        }
        pCompound.putInt("findBlockCooldown", findBlockCooldown);
        if(this.hatPos != null){
            pCompound.putLong("hatPos", this.hatPos.asLong());
        }
        return super.save(pCompound);
    }

    @Override
    public void load(CompoundTag pCompound) {
        super.load(pCompound);
        if(pCompound.contains("home")){
            this.entityData.set(HOME, Optional.of(BlockPos.of(pCompound.getLong("home"))));
        }
        findBlockCooldown = pCompound.getInt("findBlockCooldown");
        if(pCompound.contains("hatPos")){
            this.hatPos = BlockPos.of(pCompound.getLong("hatPos"));
        }
    }
}
