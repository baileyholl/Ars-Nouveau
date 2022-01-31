package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.compat.PatchouliHandler;
import com.hollingsworth.arsnouveau.common.entity.goal.GoBackHomeGoal;
import com.hollingsworth.arsnouveau.common.entity.goal.amethyst_golem.*;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AmethystGolem  extends PathfinderMob implements IAnimatable, IDispellable, ITooltipProvider, IWandable {
    public static final EntityDataAccessor<Optional<BlockPos>> HOME = SynchedEntityData.defineId(AmethystGolem.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    public static final EntityDataAccessor<Boolean> IMBUEING = SynchedEntityData.defineId(AmethystGolem.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> STOMPING = SynchedEntityData.defineId(AmethystGolem.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<BlockPos> IMBUE_POS = SynchedEntityData.defineId(AmethystGolem.class, EntityDataSerializers.BLOCK_POS);

    public int growCooldown;
    public int convertCooldown;
    public int pickupCooldown;
    public int harvestCooldown;
    public List<BlockPos> buddingBlocks = new ArrayList<>();
    public List<BlockPos> amethystBlocks = new ArrayList<>();
    int scanCooldown;

    public AmethystGolem(EntityType<? extends PathfinderMob> p_21683_, Level p_21684_) {
        super(p_21683_, p_21684_);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new GoBackHomeGoal(this, this::getHome, 10, () -> true));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(3, new ConvertBuddingGoal(this, () -> convertCooldown <= 0 && getHome() != null && getHeldStack().isEmpty()));
        this.goalSelector.addGoal(4, new GrowClusterGoal(this, () -> growCooldown <= 0 && getHome() != null && getHeldStack().isEmpty()));
        this.goalSelector.addGoal(5, new HarvestClusterGoal(this, () -> harvestCooldown <= 0 && getHome() != null && !isImbueing() && getHeldStack().isEmpty()));
        this.goalSelector.addGoal(2, new PickupAmethystGoal(this,() -> getHome() != null && pickupCooldown <= 0));
        this.goalSelector.addGoal(2, new DepositAmethystGoal(this,() -> getHome() != null && !getHeldStack().isEmpty()));
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
        if(scanCooldown > 0){
            scanCooldown--;
        }
        if(pickupCooldown > 0)
            pickupCooldown--;
        if(!level.isClientSide && scanCooldown == 0 && getHome() != null){
            scanCooldown = 20 * 60 * 3;
            scanBlocks();
        }

        if(level.isClientSide && isImbueing() && getImbuePos() != null){
            Vec3 vec = new Vec3(getImbuePos().getX() + 0.5, getImbuePos().getY(), getImbuePos().getZ() + 0.5);
            level.addParticle(GlowParticleData.createData(new ParticleColor(255, 50, 150)),
                    (float) (vec.x) - Math.sin((ClientInfo.ticksInGame ) / 8D) ,
                    (float) (vec.y) + Math.sin(ClientInfo.ticksInGame/5d)/8D + 0.5  ,
                    (float) (vec.z) - Math.cos((ClientInfo.ticksInGame) / 8D) ,
                    0, 0, 0);
        }
    }

    public void setHeldStack(ItemStack stack) {
        this.setItemSlot(EquipmentSlot.MAINHAND, stack);
    }

    public ItemStack getHeldStack() {
        return this.getMainHandItem();
    }

    public boolean isStomping(){
        return this.entityData.get(STOMPING);
    }

    public void setStomping(boolean imbueing){
        this.entityData.set(STOMPING,imbueing);
    }

    public boolean isImbueing(){
        return this.entityData.get(IMBUEING);
    }

    public void setImbueing(boolean imbueing){
        this.entityData.set(IMBUEING,imbueing);
    }

    public BlockPos getImbuePos(){
        return this.entityData.get(IMBUE_POS);
    }

    public void setImbuePos(BlockPos pos){
        this.entityData.set(IMBUE_POS,pos);
    }

    public void scanBlocks(){
        BlockPos pos = getHome().immutable();
        amethystBlocks = new ArrayList<>();
        buddingBlocks = new ArrayList<>();
        for(BlockPos b : BlockPos.betweenClosed(pos.below(3).south(5).east(5), pos.above(10).north(5).west(5))){
            if(level.getBlockState(b).isAir())
                continue;
            if(level.getBlockState(b).getBlock() == Blocks.AMETHYST_BLOCK){
                amethystBlocks.add(b.immutable());
            }
            if(level.getBlockState(b).getBlock() == Blocks.BUDDING_AMETHYST){
                buddingBlocks.add(b.immutable());

            }
        }
    }

    @Override
    public void onFinishedConnectionFirst(@javax.annotation.Nullable BlockPos storedPos, @javax.annotation.Nullable LivingEntity storedEntity, Player playerEntity) {
        if(storedPos != null){
            setHome(storedPos);
            PortUtil.sendMessage(playerEntity, new TranslatableComponent("ars_nouveau.home_set"));
        }
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        if(getHome() != null){
            tooltip.add(new TranslatableComponent("ars_nouveau.gathering_at", getHome().toShortString()));
        }
    }

    @Override
    public void die(DamageSource source) {
        if (!level.isClientSide) {
            ItemStack stack = new ItemStack(ItemsRegistry.AMETHYST_GOLEM_CHARM);
            level.addFreshEntity(new ItemEntity(level, getX(), getY(), getZ(), stack));
            if (this.getHeldStack() != null)
                level.addFreshEntity(new ItemEntity(level, getX(), getY(), getZ(), this.getHeldStack()));
        }
        super.die(source);
    }

    @Override
    public boolean onDispel(@Nullable LivingEntity caster) {
        if (this.isRemoved())
            return false;

        if (!level.isClientSide) {
            ItemStack stack = new ItemStack(ItemsRegistry.AMETHYST_GOLEM_CHARM);
            level.addFreshEntity(new ItemEntity(level, getX(), getY(), getZ(), stack.copy()));
            stack = getHeldStack();
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
        tag.putInt("harvest",harvestCooldown);
        tag.putInt("pickup", pickupCooldown);

        if (getHeldStack() != null) {
            CompoundTag itemTag = new CompoundTag();
            getHeldStack().save(itemTag);
            tag.put("held", itemTag);
        }
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
        this.pickupCooldown = tag.getInt("pickup");

        if (tag.contains("held"))
            setHeldStack(ItemStack.of((CompoundTag) tag.get("held")));
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
        if(isStomping()){
            animationEvent.getController().setAnimation(new AnimationBuilder().addAnimation("harvest2"));
            return PlayState.CONTINUE;
        }

        if(isImbueing() || (level.isClientSide && PatchouliHandler.isPatchouliWorld())){
            animationEvent.getController().setAnimation(new AnimationBuilder().addAnimation("tending_master"));
            return PlayState.CONTINUE;
        }
        if(animationEvent.isMoving()){
            String anim = getHeldStack().isEmpty() ? "run" : "run_carry";
            animationEvent.getController().setAnimation(new AnimationBuilder().addAnimation(anim));
            return PlayState.CONTINUE;
        }

        if(!getHeldStack().isEmpty()){
            animationEvent.getController().setAnimation(new AnimationBuilder().addAnimation("carry_idle"));
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
        this.entityData.define(IMBUEING, false);
        this.entityData.define(IMBUE_POS, BlockPos.ZERO);
        this.entityData.define(STOMPING, false);
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
