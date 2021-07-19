package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.entity.ISummon;
import com.hollingsworth.arsnouveau.api.spell.EntitySpellResolver;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleLineData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.tile.IAnimationListener;
import com.hollingsworth.arsnouveau.common.entity.goal.chimera.*;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketAnimEntity;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectDelay;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectKnockback;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectLaunch;
import com.hollingsworth.arsnouveau.common.spell.method.MethodTouch;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
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

import javax.annotation.Nullable;
import java.util.ArrayList;

public class EntityChimera extends MonsterEntity implements IAnimatable, IAnimationListener {
    private final ServerBossInfo bossEvent = (ServerBossInfo)(new ServerBossInfo(this.getDisplayName(), BossInfo.Color.PURPLE, BossInfo.Overlay.PROGRESS)).setDarkenScreen(true).setCreateWorldFog(true);
    public static final DataParameter<Boolean> HAS_SPIKES = EntityDataManager.defineId(EntityChimera.class, DataSerializers.BOOLEAN);
    public static final DataParameter<Boolean> HAS_HORNS = EntityDataManager.defineId(EntityChimera.class, DataSerializers.BOOLEAN);
    public static final DataParameter<Boolean> HAS_WINGS = EntityDataManager.defineId(EntityChimera.class, DataSerializers.BOOLEAN);
    public static final DataParameter<Integer> PHASE = EntityDataManager.defineId(EntityChimera.class, DataSerializers.INT);
    public static final DataParameter<Boolean> DEFENSIVE_MODE = EntityDataManager.defineId(EntityChimera.class, DataSerializers.BOOLEAN);
    public static final DataParameter<Boolean> PHASE_SWAPPING = EntityDataManager.defineId(EntityChimera.class, DataSerializers.BOOLEAN);
    public static final DataParameter<Boolean> IS_FLYING = EntityDataManager.defineId(EntityChimera.class, DataSerializers.BOOLEAN);
    public int summonCooldown;
    public int diveCooldown;
    public int spikeCooldown;
    public int ramCooldown;
    public boolean diving;

    public FlyingPathNavigator flyingNavigator;

    protected EntityChimera(EntityType<? extends MonsterEntity> p_i48553_1_, World p_i48553_2_) {
        super(p_i48553_1_, p_i48553_2_);
        moveControl = new ChimeraMoveController(this, 10, true);
        maxUpStep = 2.0f;
        initFlyingNavigator();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(5, new ChimeraAttackGoal(this, true));
        this.goalSelector.addGoal(3, new ChimeraSummonGoal(this));
        this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomWalkingGoal(this, 1.2d));
        this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
        this.goalSelector.addGoal(3, new ChimeraRamGoal(this));
        this.goalSelector.addGoal(3, new ChimeraDiveGoal(this));
        this.goalSelector.addGoal(3, new ChimeraSpikeGoal(this));
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<EntityChimera>(this, "walkController", 20, this::groundPredicate));
        animationData.addAnimationController(new AnimationController<EntityChimera>(this, "attackController", 1, this::attackPredicate));
    }


    private <E extends Entity> PlayState attackPredicate(AnimationEvent event) {
        return PlayState.CONTINUE;
    }

    private<E extends Entity> PlayState groundPredicate(AnimationEvent e){
        if (isDefensive()) {
            e.getController().setAnimation(new AnimationBuilder().addAnimation("crouch"));
            return PlayState.CONTINUE;
        }

        if (e.isMoving() && !isFlying()) {
            e.getController().setAnimation(new AnimationBuilder().addAnimation("run"));
            return PlayState.CONTINUE;
        }

        return PlayState.STOP;
    }

    @Override
    public void tick() {
        super.tick();

        if(!isFlying())
            setNoGravity(false);

        if(this.isFlying()) {
            this.navigation.stop();
            flyingNavigator.tick();
        }
        if(this.isFlying() && !this.level.isClientSide){
            if(this.goalSelector.getRunningGoals().noneMatch(g -> g.getGoal() instanceof ChimeraDiveGoal))
                setFlying(false);
        }

        if(summonCooldown > 0)
            summonCooldown--;
        if(diveCooldown > 0)
            diveCooldown--;
        if(spikeCooldown > 0)
            spikeCooldown--;
        if(ramCooldown > 0)
            ramCooldown--;

        if(!level.isClientSide && getPhaseSwapping()){
//            if(this.getTarget() != null)
//                this.getLookControl().setLookAt(this.getTarget(), 30f, 30f);
            if(this.getHealth() < this.getMaxHealth()){
                this.heal((float) (0.3f));
            }else{
                this.setPhaseSwapping(false);
                for(LivingEntity e : level.getEntitiesOfClass(PlayerEntity.class, new AxisAlignedBB(this.blockPosition()).inflate(5))){

                    EntitySpellResolver resolver = new EntitySpellResolver(new SpellContext(
                            new Spell.Builder().add(MethodTouch.INSTANCE)
                                    .add(EffectLaunch.INSTANCE).add(AugmentAmplify.INSTANCE, 2).add(EffectDelay.INSTANCE).add(EffectKnockback.INSTANCE).add(AugmentAmplify.INSTANCE, 2).build()
                    , this));
                    resolver.onCastOnEntity(ItemStack.EMPTY, this, e, Hand.MAIN_HAND);
                }
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

    public boolean canDive(){
        return diveCooldown <= 0 && hasWings() && !getPhaseSwapping() && !isFlying() && this.onGround && !isDefensive();
    }

    public boolean canSpike(){
        return spikeCooldown <= 0 && hasSpikes() && !getPhaseSwapping() && !isFlying() && this.onGround;
    }

    public boolean canRam(){
        return ramCooldown <= 0 && hasHorns() && !getPhaseSwapping() && !isFlying() && !isDefensive();
    }

    public boolean canSummon(){
        return getTarget() != null && summonCooldown <= 0 && !isFlying() && !getPhaseSwapping();
    }

    public boolean canAttack(){
        return getTarget() != null && this.getHealth() >= 1 && !this.getPhaseSwapping() && !isFlying() && !isDefensive();
    }


    @Override
    public boolean isDeadOrDying() {
        return this.getHealth() <= 0.0 && getPhase() == 3;
    }

    public void getRandomUpgrade(){
        ArrayList<Integer> upgrades = new ArrayList<>();
        setWings(true);
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
        if (source == DamageSource.CACTUS || source == DamageSource.SWEET_BERRY_BUSH || source == DamageSource.IN_WALL || source == DamageSource.LAVA)
            return false;
        if(this.getPhaseSwapping())
            return false;

        Entity entity = source.getEntity();
        if(entity instanceof LivingEntity && !entity.equals(this)){
            if(isDefensive() && !source.msgId.equals("thorns")){
                if(!source.isBypassArmor()){
                    entity.hurt(DamageSource.thorns(this), 6.0f);
                }
            }

            LivingEntity entity1 = (LivingEntity) entity;
            // Omit our summoned sources that might aggro or accidentally hurt us
            if(entity1 instanceof WildenStalker || entity1 instanceof WildenGuardian || entity instanceof WildenHunter
                    || (entity instanceof ISummon && ((ISummon) entity).getOwnerID() != null && ((ISummon) entity).getOwnerID().equals(this.getUUID()))
            || (entity1 instanceof SummonWolf && ((SummonWolf) entity1).isWildenSummon))
                return false;
        }

        if(isDefensive())
            return false;

        boolean res = super.hurt(source, amount);
        if(!this.level.isClientSide && this.getHealth() <= 0.0 && getPhase() < 3){
            this.setPhaseSwapping(true);
            this.setPhase(this.getPhase() + 1);
            this.setHealth(1.0f);
            Networking.sendToNearby(level, this, new PacketAnimEntity(this.getId(), EntityChimera.Animations.HOWL.ordinal()));
            this.setFlying(false);
            this.setDefensiveMode(false);
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

    @Override
    protected void jumpFromGround() {
//        super.jumpFromGround();
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
        this.entityData.define(IS_FLYING, false);
    }
    public boolean isFlying(){
        return entityData.get(IS_FLYING);
    }

    public void setFlying(boolean flying){
        entityData.set(IS_FLYING, flying);
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

            if(arg == Animations.FLYING.ordinal()){
                AnimationController controller = this.factory.getOrCreateAnimationData(this.hashCode()).getAnimationControllers().get("attackController");
                controller.markNeedsReload();
                controller.setAnimation(new AnimationBuilder().addAnimation("flying", true));
            }
            if(arg == Animations.DIVE_BOMB.ordinal()){
                AnimationController controller = this.factory.getOrCreateAnimationData(this.hashCode()).getAnimationControllers().get("attackController");
                controller.markNeedsReload();
                controller.setAnimation(new AnimationBuilder().addAnimation("divebomb", true));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void checkFallDamage(double p_184231_1_, boolean p_184231_3_, BlockState p_184231_4_, BlockPos p_184231_5_) {
        super.checkFallDamage(p_184231_1_, p_184231_3_, p_184231_4_, p_184231_5_);
        this.fallDistance = 0;
    }

    public enum Animations{
        ATTACK,
        HOWL,
        CHARGE,
        FLYING,
        DIVE_BOMB
    }

    @Override
    public PathNavigator getNavigation() {
        return this.isFlying() ? flyingNavigator : super.getNavigation();
    }

    public void initFlyingNavigator(){
        FlyingPathNavigator flyingpathnavigator = new FlyingPathNavigator(this, level);
        flyingpathnavigator.setCanOpenDoors(true);
        flyingpathnavigator.setCanFloat(false);
        flyingpathnavigator.setCanPassDoors(true);
        this.flyingNavigator = flyingpathnavigator;
    }

    public Vector3d orbitOffset = Vector3d.ZERO;

    public static class ChimeraMoveController extends MovementController{

        private final int maxTurn;
        private final boolean hoversInPlace;
        private float diveFactor = 1.0f;



        public ChimeraMoveController(EntityChimera p_i225710_1_, int maxTurn, boolean hoversInPlace) {
            super(p_i225710_1_);
            this.maxTurn = maxTurn;
            this.hoversInPlace = hoversInPlace;
        }

        @Override
        public void tick() {
            EntityChimera chimera = (EntityChimera) this.mob;
            if(chimera.isFlying()) {
                if (chimera.diving) {
                    diveTick();
                } else {
                    flyTick();
                }
            }else{
                super.tick();
            }
        }
        // Copy from FlyingMovementController
        public void flyTick(){
            if (this.operation == MovementController.Action.MOVE_TO) {
                this.operation = MovementController.Action.WAIT;
                this.mob.setNoGravity(true);
                double d0 = this.wantedX - this.mob.getX();
                double d1 = this.wantedY - this.mob.getY();
                double d2 = this.wantedZ - this.mob.getZ();
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if (d3 < (double)2.5000003E-7F) {
                    this.mob.setYya(0.0F);
                    this.mob.setZza(0.0F);
                    return;
                }

                float f = (float)(MathHelper.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
                this.mob.yRot = this.rotlerp(this.mob.yRot, f, 90.0F);
                float f1;
                if (this.mob.isOnGround()) {
                    f1 = (float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED));
                } else {
                    f1 = (float)(this.speedModifier * this.mob.getAttributeValue(Attributes.FLYING_SPEED));
                }

                this.mob.setSpeed(f1);
                double d4 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
                float f2 = (float)(-(MathHelper.atan2(d1, d4) * (double)(180F / (float)Math.PI)));
                this.mob.xRot = this.rotlerp(this.mob.xRot, f2, (float)this.maxTurn);
                this.mob.setYya(d1 > 0.0D ? f1 : -f1);
            } else {
                if (!this.hoversInPlace) {
                    this.mob.setNoGravity(false);
                }

                this.mob.setYya(0.0F);
                this.mob.setZza(0.0F);
            }

        }

        public void diveTick(){
//            this.speedFactor = 0.4f;
            EntityChimera mob = (EntityChimera) this.mob;

            double posX = mob.getX();
            double posY = mob.getY();
            double posZ = mob.getZ();
            double motionX = mob.getDeltaMovement().x;
            double motionY = mob.getDeltaMovement().y;
            double motionZ = mob.getDeltaMovement().z;
            BlockPos dest = new BlockPos(mob.orbitOffset);


          //  mob.getLookControl().setLookAt(dest.getX(), dest.getY(), dest.getZ());
            double speedMod = 1.3;
            if (dest.getX() != 0 || dest.getY() != 0 || dest.getZ() != 0){
                double targetX = dest.getX()+0.5;
                double targetY = dest.getY()+0.5;
                double targetZ = dest.getZ()+0.5;
                Vector3d targetVector = new Vector3d(targetX-posX,targetY-posY,targetZ-posZ);
                double length = targetVector.length();
                targetVector = targetVector.scale(0.3/length);
                double weight  = 0;
                if (length <= 3){
                    weight = 0.9*((3.0-length)/3.0);
                }

                motionX = (0.9-weight)*motionX+(speedMod + weight)*targetVector.x;
                motionY = (0.9-weight)*motionY+(speedMod + weight)*targetVector.y;
                motionZ = (0.9-weight)*motionZ+(speedMod + weight)*targetVector.z;
            }
            mob.setDeltaMovement(motionX, motionY, motionZ);
            faceBlock(new BlockPos(mob.orbitOffset), mob);
        }
    }

    public static void faceBlock(@Nullable final BlockPos block, final LivingEntity citizen)
    {
        if (block == null)
        {
            return;
        }

        final double xDifference = block.getX() - citizen.blockPosition().getX();
        final double zDifference = block.getZ() - citizen.blockPosition().getZ();
        final double yDifference = block.getY() - (citizen.blockPosition().getY() + citizen.getEyeHeight());

        final double squareDifference = Math.sqrt(xDifference * xDifference + zDifference * zDifference);
        final double intendedRotationYaw = (Math.atan2(zDifference, xDifference) * 180.0D / Math.PI) - 90.0;
        final double intendedRotationPitch = -(Math.atan2(yDifference, squareDifference) * 180.0D / Math.PI);
//        citizen.setOwnRotation((float) updateRotation(citizen.getRotationYaw(), intendedRotationYaw, ROTATION_MOVEMENT),
//                (float) updateRotation(citizen.getRotationPitch(), intendedRotationPitch, ROTATION_MOVEMENT));


        citizen.yRot = (float) (updateRotation(citizen.yRot, intendedRotationYaw, 360.0) % 360.0F);
        citizen.xRot = (float) (updateRotation(citizen.xRot, intendedRotationPitch, 360.0) % 360.0F);
        final double goToX = xDifference;
        final double goToZ = zDifference;

        //Have to move the entity minimally into the direction to render his new rotation.

    }
    public static double updateRotation(final double currentRotation, final double intendedRotation, final double maxIncrement)
    {
        double wrappedAngle = MathHelper.wrapDegrees(intendedRotation - currentRotation);

        if (wrappedAngle > maxIncrement)
        {
            wrappedAngle = maxIncrement;
        }

        if (wrappedAngle < -maxIncrement)
        {
            wrappedAngle = -maxIncrement;
        }

        return currentRotation + wrappedAngle;
    }
    protected static float getXRotD(BlockPos wantedPos, LivingEntity mob) {
        double d0 = wantedPos.getX() - mob.getX();
        double d1 = wantedPos.getY() - mob.getEyeY();
        double d2 = wantedPos.getZ() - mob.getZ();
        double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
        return (float)(-(MathHelper.atan2(d1, d3) * (double)(180F / (float)Math.PI)));
    }
    protected static float rotateTowards(float min, float p_220675_2_, float p_220675_3_) {
        float f = MathHelper.degreesDifference(min, p_220675_2_);
        float f1 = MathHelper.clamp(f, -p_220675_3_, p_220675_3_);
        return min + f1;
    }


    public static AttributeModifierMap.MutableAttribute getModdedAttributes(){
        return MobEntity.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 25D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.6F)
                .add(Attributes.ATTACK_KNOCKBACK, 1.0D)
                .add(Attributes.ATTACK_DAMAGE, 4.5D)
                .add(Attributes.ARMOR, 2.0D)
                .add(Attributes.FOLLOW_RANGE, 100D)
                .add(Attributes.FLYING_SPEED,0.4f );
    }
}
