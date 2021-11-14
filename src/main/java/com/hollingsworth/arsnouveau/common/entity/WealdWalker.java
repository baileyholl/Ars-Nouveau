package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.api.spell.EntitySpellResolver;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.tile.IAnimationListener;
import com.hollingsworth.arsnouveau.common.entity.goal.GoBackHomeGoal;
import com.hollingsworth.arsnouveau.common.entity.goal.wealdwalker.CastSpellGoal;
import com.hollingsworth.arsnouveau.common.entity.goal.wealdwalker.SmashGoal;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
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
import java.util.Optional;

public class WealdWalker extends AgeableEntity implements IAnimatable, IAnimationListener, IRangedAttackMob, IWandable, ITooltipProvider {

    public static final DataParameter<Boolean> SMASHING = EntityDataManager.defineId(WealdWalker.class, DataSerializers.BOOLEAN);
    public static final DataParameter<Boolean> CASTING = EntityDataManager.defineId(WealdWalker.class, DataSerializers.BOOLEAN);
    public static final DataParameter<Boolean> BABY = EntityDataManager.defineId(WealdWalker.class, DataSerializers.BOOLEAN);
    public static final DataParameter<Optional<BlockPos>> HOME = EntityDataManager.defineId(WealdWalker.class, DataSerializers.OPTIONAL_BLOCK_POS);
    public int smashCooldown;
    public int castCooldown;
    public Spell spell = Spell.EMPTY;
    public ParticleColor color = ParticleUtil.defaultParticleColor();

    public WealdWalker(EntityType<? extends AgeableEntity> type, World world) {
        super(type, world);
    }

    @Override
    public void tick() {
        super.tick();

        if(smashCooldown > 0)
            smashCooldown--;
        if(castCooldown > 0)
            castCooldown--;
        if(!level.isClientSide && level.getGameTime() % 20 == 0 && !this.isDeadOrDying()){
            this.heal(1.0f);
        }
    }
    public ActionResultType mobInteract(PlayerEntity p_230254_1_, Hand p_230254_2_) {
        ItemStack itemstack = p_230254_1_.getItemInHand(p_230254_2_);
        if (itemstack.getItem() instanceof BoneMealItem && isBaby()) {
            int i = this.getAge();

            if (this.isBaby()) {
                this.usePlayerItem(p_230254_1_, itemstack);
                this.ageUp((int)((float)(-i / 20) * 0.1F), true);
                return ActionResultType.sidedSuccess(this.level.isClientSide);
            }

            if (this.level.isClientSide) {
                return ActionResultType.CONSUME;
            }
        }

        return super.mobInteract(p_230254_1_, p_230254_2_);
    }

    protected void usePlayerItem(PlayerEntity p_175505_1_, ItemStack p_175505_2_) {
        if (!p_175505_1_.abilities.instabuild) {
            p_175505_2_.shrink(1);
        }
    }

    @Override
    public void onFinishedConnectionFirst(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, PlayerEntity playerEntity) {
        if(storedPos != null){
            setHome(storedPos);
            PortUtil.sendMessage(playerEntity, new TranslationTextComponent("ars_nouveau.weald_walker.setpos"));
        }
    }

    @Override
    public EntitySize getDimensions(Pose p_213305_1_) {
        return isBaby() ? new EntitySize(1,1,true) : super.getDimensions(p_213305_1_);
    }

    @Nullable
    @Override
    public AgeableEntity getBreedOffspring(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
        return null;
    }

    @Override
    public void die(DamageSource source) {
        if(!isBaby() && !level.isClientSide){

            setBaby(true);
            refreshDimensions();
            this.setHealth(60);
            ParticleUtil.spawnPoof((ServerWorld) level, blockPosition().above());
            if(source.getEntity() != null && source.getEntity() instanceof MobEntity)
                ((MobEntity) source.getEntity()).setTarget(null);
            return;
        }
        super.die(source);
    }

    @Override
    public void setBaby(boolean baby) {
        super.setBaby(baby);
        this.entityData.set(BABY, baby);
    }

    @Override
    public void setAge(int age) {
        this.age = age;
        if(this.age >= 0 && !level.isClientSide){
            this.ageBoundaryReached();
        }
    }

    public void setHome(BlockPos home){
        this.entityData.set(HOME, Optional.of(home));
    }

    public @Nullable BlockPos getHome(){
        return this.entityData.get(HOME).orElse(null);
    }

    @Override
    protected void ageBoundaryReached() {
        super.ageBoundaryReached();
        if(!level.isClientSide)
            this.entityData.set(BABY, false);
    }

    @Override
    public boolean isBaby() {
        return this.entityData.get(BABY);
    }
    public void onSyncedDataUpdated(DataParameter<?> p_184206_1_) {
        if (BABY.equals(p_184206_1_)) {
            this.refreshDimensions();
        }

        super.onSyncedDataUpdated(p_184206_1_);
    }
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new SwimGoal(this));
        this.goalSelector.addGoal(2, new GoBackHomeGoal(this, this::getHome, 10, () -> this.getTarget() == null || this.isBaby()));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, MonsterEntity.class, false));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
        this.goalSelector.addGoal(2, new SmashGoal(this, true,() ->smashCooldown <= 0 && !this.entityData.get(BABY), Animations.SMASH.ordinal(), 25, 5));
        this.goalSelector.addGoal(2, new CastSpellGoal(this, 1.2d, 20,15f, () -> castCooldown <= 0 && !this.entityData.get(BABY), Animations.CAST.ordinal(), 20));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SMASHING, false);
        this.entityData.define(CASTING, false);
        this.entityData.define(BABY, false);
        this.entityData.define(HOME, Optional.empty());
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("isBaby", entityData.get(BABY));
        NBTUtil.storeBlockPos(tag, "home", getHome());
        tag.putInt("smash", smashCooldown);
        tag.putInt("cast", castCooldown);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT tag) {
        super.readAdditionalSaveData(tag);
        entityData.set(BABY, tag.getBoolean("isBaby"));
        if(NBTUtil.hasBlockPos(tag, "home")){
            setHome(NBTUtil.getBlockPos(tag, "home"));
        }
        this.smashCooldown = tag.getInt("smash");
        this.castCooldown = tag.getInt("cast");
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
        if(entityData.get(SMASHING) || entityData.get(CASTING))
            return PlayState.STOP;
        if(animationEvent.getController().getCurrentAnimation() != null && !(animationEvent.getController().getCurrentAnimation().animationName.equals("run_master"))) {
            return PlayState.STOP;
        }
        if(animationEvent.isMoving()){
            animationEvent.getController().setAnimation(new AnimationBuilder().addAnimation("run_master"));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    AnimationFactory factory = new AnimationFactory(this);
    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    @Override
    public void startAnimation(int arg) {
        try{
            if(arg == Animations.SMASH.ordinal()){
                AnimationController controller = this.factory.getOrCreateAnimationData(this.hashCode()).getAnimationControllers().get("attack_controller");

                if(controller.getCurrentAnimation() != null && (controller.getCurrentAnimation().animationName.equals("smash"))) {
                    return;
                }
                controller.markNeedsReload();
                controller.setAnimation(new AnimationBuilder().addAnimation("smash", false).addAnimation("idle", false));
            }

            if(arg == Animations.CAST.ordinal()){
                AnimationController controller = this.factory.getOrCreateAnimationData(this.hashCode()).getAnimationControllers().get("attack_controller");
                if(controller.getCurrentAnimation() != null && controller.getCurrentAnimation().animationName.equals("cast")) {
                    return;
                }
                controller.markNeedsReload();
                controller.setAnimation(new AnimationBuilder().addAnimation("cast", false).addAnimation("idle", false));
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static AttributeModifierMap.MutableAttribute attributes() {
        return MobEntity.createMobAttributes().add(Attributes.MAX_HEALTH, 60d)
                .add(Attributes.MOVEMENT_SPEED, 0.2d)
                .add(Attributes.FOLLOW_RANGE, 16D)
                .add(Attributes.ATTACK_DAMAGE, 10.5d);
    }

    @Override
    public void checkDespawn() {
        super.checkDespawn();
    }

    @Override
    public boolean removeWhenFarAway(double p_213397_1_) {
        return false;
    }

    @Override
    protected int getExperienceReward(PlayerEntity p_70693_1_) {
        return 0;
    }

    @Override
    public void performRangedAttack(LivingEntity p_82196_1_, float p_82196_2_) {
        EntitySpellResolver resolver = new EntitySpellResolver(new SpellContext(spell, this).withColors(color.toWrapper()));
        EntityProjectileSpell projectileSpell = new EntityProjectileSpell(level, resolver);
        projectileSpell.setColor(color.toWrapper());
        projectileSpell.shoot(this, this.xRot, this.yRot, 0.0F, 1.0f, 0.8f);
        level.addFreshEntity(projectileSpell);
        this.castCooldown = 40;
    }

    @Override
    public List<String> getTooltip() {
        List<String> tips = new ArrayList<>();
        if(getHome() != null){
            String home = getHome().getX() + ", " + getHome().getY() + ", " + getHome().getZ();
            tips.add(new TranslationTextComponent("ars_nouveau.weald_walker.home",home).getString());
        }else{
            tips.add(new TranslationTextComponent("ars_nouveau.weald_walker.home",new TranslationTextComponent("ars_nouveau.nothing").getString()).getString());
        }
        return tips;
    }

    public enum Animations{
        CAST,
        SMASH
    }
}
