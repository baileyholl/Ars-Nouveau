package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleLineData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.tile.IAnimationListener;
import com.hollingsworth.arsnouveau.common.entity.goal.chimera.ChimeraAttackGoal;
import com.hollingsworth.arsnouveau.common.entity.goal.chimera.ChimeraRamGoal;
import com.hollingsworth.arsnouveau.common.entity.goal.chimera.ChimeraSummonGoal;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketAnimEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.BossInfo;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraft.world.server.ServerWorld;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.ArrayList;

public class EntityChimera extends MonsterEntity implements IAnimatable, IAnimationListener {
    private final ServerBossInfo bossEvent = (ServerBossInfo)(new ServerBossInfo(this.getDisplayName(), BossInfo.Color.PURPLE, BossInfo.Overlay.PROGRESS)).setDarkenScreen(true).setCreateWorldFog(true);
    public static final DataParameter<Boolean> HAS_SPIKES = EntityDataManager.defineId(EntityChimera.class, DataSerializers.BOOLEAN);
    public static final DataParameter<Boolean> HAS_HORNS = EntityDataManager.defineId(EntityChimera.class, DataSerializers.BOOLEAN);
    public static final DataParameter<Boolean> HAS_WINGS = EntityDataManager.defineId(EntityChimera.class, DataSerializers.BOOLEAN);
    public static final DataParameter<Integer> PHASE = EntityDataManager.defineId(EntityChimera.class, DataSerializers.INT);
    public static final DataParameter<Boolean> DEFENSIVE_MODE = EntityDataManager.defineId(EntityChimera.class, DataSerializers.BOOLEAN);
    public static final DataParameter<Boolean> PHASE_SWAPPING = EntityDataManager.defineId(EntityChimera.class, DataSerializers.BOOLEAN);

    public int summonCooldown;
    public int diveCooldown;
    public int spikeCooldown;
    public int ramCooldown;

    protected EntityChimera(EntityType<? extends MonsterEntity> p_i48553_1_, World p_i48553_2_) {
        super(p_i48553_1_, p_i48553_2_);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(5, new ChimeraAttackGoal(this, true));
        this.goalSelector.addGoal(3, new ChimeraSummonGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
//        this.goalSelector.addGoal(8, new WaterAvoidingRandomWalkingGoal(this, 1.2d));
        this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
        this.goalSelector.addGoal(3, new ChimeraRamGoal(this));
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<EntityChimera>(this, "walkController", 20, this::groundPredicate));
        animationData.addAnimationController(new AnimationController<EntityChimera>(this, "flyController", 20, this::flyPredicate));
        animationData.addAnimationController(new AnimationController<EntityChimera>(this, "attackController", 1, this::attackPredicate));
    }

    private <E extends Entity> PlayState flyPredicate(AnimationEvent event) {
        return PlayState.STOP;
    }
    private <E extends Entity> PlayState attackPredicate(AnimationEvent event) {
        return PlayState.CONTINUE;
    }

    private<E extends Entity> PlayState groundPredicate(AnimationEvent e){
        if (e.isMoving()) {
            e.getController().setAnimation(new AnimationBuilder().addAnimation("run"));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    @Override
    public void tick() {
        super.tick();
        if(summonCooldown > 0)
            summonCooldown--;
        if(diveCooldown > 0)
            diveCooldown--;
        if(spikeCooldown > 0)
            spikeCooldown--;
        if(ramCooldown > 0)
            ramCooldown--;

        if(!level.isClientSide && getPhaseSwapping()){
            if(this.getTarget() != null)
                this.getLookControl().setLookAt(this.getTarget(), 0.3f, 0.3f);
            if(this.getHealth() < this.getMaxHealth()){
                this.heal((float) (0.3f));
            }else{
                this.setPhaseSwapping(false);
                getRandomUpgrade();
                ParticleUtil.spawnPoof((ServerWorld) level, blockPosition().above());
            }
        }
        int baseAge =  40;
        float scaleAge = (float) ParticleUtil.inRange(0.1, 0.2);
        if(getPhaseSwapping() && level.isClientSide){
            BlockPos pos = blockPosition();
            for(int i =0; i< 10; i++){
                Vector3d particlePos = new Vector3d(pos.getX(), pos.getY() + 1, pos.getZ()).add(0.5, 0.5, 0.5);
                particlePos = particlePos.add(ParticleUtil.pointInSphere().multiply(3.0, 3.0, 3.0));
                level.addParticle(ParticleLineData.createData(ParticleColor.makeRandomColor(255, 255, 255, random),scaleAge, baseAge + level.random.nextInt(20)) ,
                        particlePos.x(), particlePos.y(), particlePos.z(),
                        pos.getX() + 0.5  , pos.getY() +0.5 , pos.getZ()+ 0.5);
            }
        }
    }

    @Override
    public void heal(float p_70691_1_) {
        this.setHealth(this.getHealth() + p_70691_1_);
    }

    public boolean canSwipe(){
        return !getPhaseSwapping();
    }

    public boolean canDive(){
        return diveCooldown <= 0 && hasWings() && !getPhaseSwapping();
    }

    public boolean canSpike(){
        return spikeCooldown <= 0 && hasSpikes() && !getPhaseSwapping();
    }

    public boolean canRam(){
        return ramCooldown <= 0 && hasHorns() && !getPhaseSwapping();
    }

    public boolean canSummon(){
        return getTarget() != null && summonCooldown <= 0;
    }

    public boolean canAttack(){
        return getTarget() != null && this.getHealth() >= 1 && !this.getPhaseSwapping();
    }


    @Override
    public boolean isDeadOrDying() {
        return this.getHealth() <= 0.0 && getPhase() == 3;
    }

    public void getRandomUpgrade(){
        ArrayList<Integer> upgrades = new ArrayList<>();
        setHorns(true);
        if(!this.hasWings())
            upgrades.add(0);
        if(!this.hasSpikes())
            upgrades.add(1);
        if(!this.hasHorns())
            upgrades.add(2);
        if(upgrades.isEmpty())
            return;
        int upgrade = upgrades.get(random.nextInt(upgrades.size()));
        switch(upgrade){
            case 0:
                setWings(true);
                return;
            case 1:
                setSpikes(true);
                return;
            case 2:
                setHorns(true);
                return;
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source == DamageSource.CACTUS || source == DamageSource.SWEET_BERRY_BUSH)
            return false;
        if(this.getPhaseSwapping())
            return false;
        boolean res = super.hurt(source, amount);
        if(!this.level.isClientSide && this.getHealth() <= 0.0 && getPhase() < 3){
            this.setPhaseSwapping(true);
            this.setPhase(this.getPhase() + 1);
            this.setHealth(1.0f);
            Networking.sendToNearby(level, this, new PacketAnimEntity(this.getId(), EntityChimera.Animations.HOWL.ordinal()));
            this.dead = false;
        }

        return res;
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        this.bossEvent.setPercent(this.getHealth() / this.getMaxHealth());
    }

    protected boolean canRide(Entity p_184228_1_) {
        return false;
    }

    public void startSeenByPlayer(ServerPlayerEntity p_184178_1_) {
        super.startSeenByPlayer(p_184178_1_);
        this.bossEvent.addPlayer(p_184178_1_);
    }

    public void stopSeenByPlayer(ServerPlayerEntity p_184203_1_) {
        super.stopSeenByPlayer(p_184203_1_);
        this.bossEvent.removePlayer(p_184203_1_);
    }

    public boolean canChangeDimensions() {
        return false;
    }

    public boolean canBeAffected(EffectInstance p_70687_1_) {
        return super.canBeAffected(p_70687_1_);
    }

    AnimationFactory factory = new AnimationFactory(this);

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HAS_HORNS, false);
        this.entityData.define(HAS_SPIKES, false);
        this.entityData.define(HAS_WINGS, false);
        this.entityData.define(PHASE, 1);
        this.entityData.define(DEFENSIVE_MODE, false);
        this.entityData.define(PHASE_SWAPPING, false);
    }

    public boolean hasHorns(){
        return entityData.get(HAS_HORNS);
    }

    public void setHorns(boolean hasHorns){
        entityData.set(HAS_HORNS, hasHorns);
    }

    public boolean hasSpikes(){
        return entityData.get(HAS_SPIKES);
    }

    public void setSpikes(boolean hasSpikes){
        entityData.set(HAS_SPIKES, hasSpikes);
    }

    public boolean hasWings(){
        return entityData.get(HAS_WINGS);
    }

    public void setWings(boolean hasWings){
        entityData.set(HAS_WINGS, hasWings);
    }

    public boolean isDefensive(){
        return entityData.get(DEFENSIVE_MODE);
    }

    public void setDefensiveMode(boolean defensiveMode){
        entityData.set(DEFENSIVE_MODE, defensiveMode);
    }

    public int getPhase(){
        return entityData.get(PHASE);
    }

    public void setPhase(int phase){
        entityData.set(PHASE, phase);
    }

    public boolean getPhaseSwapping(){
        return entityData.get(PHASE_SWAPPING);
    }

    public void setPhaseSwapping(boolean swapping){
        entityData.set(PHASE_SWAPPING, swapping);
    }

    @Override
    public void load(CompoundNBT tag) {
        super.load(tag);
        setHorns(tag.getBoolean("horns"));
        setSpikes(tag.getBoolean("spikes"));
        setWings(tag.getBoolean("wings"));
        setPhase(tag.getInt("phase"));
        setDefensiveMode(tag.getBoolean("defensive"));
        setPhaseSwapping(tag.getBoolean("swapping"));
        summonCooldown = tag.getInt("summonCooldown");
        diveCooldown = tag.getInt("diveCooldown");
        spikeCooldown = tag.getInt("spikeCooldown");
        ramCooldown = tag.getInt("ramCooldown");
    }

    @Override
    public boolean save(CompoundNBT tag) {
        tag.putBoolean("spikes", hasSpikes());
        tag.putBoolean("horns", hasHorns());
        tag.putBoolean("wings", hasWings());
        tag.putInt("phase", getPhase());
        tag.putBoolean("defensive", isDefensive());
        tag.putInt("summonCooldown", summonCooldown);
        tag.putInt("diveCooldown", diveCooldown);
        tag.putInt("spikeCooldown", spikeCooldown);
        tag.putInt("ramCooldown", ramCooldown);
        tag.putBoolean("swapping", getPhaseSwapping());
        return super.save(tag);
    }

    @Override
    public void startAnimation(int arg) {
        try{
            if(arg == Animations.ATTACK.ordinal()){
                AnimationController controller = this.factory.getOrCreateAnimationData(this.hashCode()).getAnimationControllers().get("attackController");

                if(controller.getCurrentAnimation() != null && (controller.getCurrentAnimation().animationName.equals("claw_swipe"))) {
                    return;
                }
                controller.markNeedsReload();
                controller.setAnimation(new AnimationBuilder().addAnimation("claw_swipe", false).addAnimation("idle"));
            }

            if(arg == Animations.HOWL.ordinal()){
                AnimationController controller = this.factory.getOrCreateAnimationData(this.hashCode()).getAnimationControllers().get("attackController");

                if(controller.getCurrentAnimation() != null && (controller.getCurrentAnimation().animationName.equals("howl"))) {
                    return;
                }
                controller.markNeedsReload();
                controller.setAnimation(new AnimationBuilder().addAnimation("howl", false).addAnimation("idle"));
            }


            if(arg == Animations.CHARGE.ordinal()){
                AnimationController controller = this.factory.getOrCreateAnimationData(this.hashCode()).getAnimationControllers().get("attackController");
                controller.markNeedsReload();
                controller.setAnimation(new AnimationBuilder().addAnimation("ready_charge", false).addAnimation("charge", true));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public enum Animations{
        ATTACK,
        HOWL,
        CHARGE
    }
}
