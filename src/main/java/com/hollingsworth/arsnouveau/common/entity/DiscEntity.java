package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class DiscEntity extends Entity implements PlayerRideableJumping, GeoEntity {

    public float prevBoardRot = 0;
    private boolean rocking;
    private float rockingIntensity;
    private float rockingAngle;
    private float prevRockingAngle;
    private int extinguishTimer = 0;

    private int jumpFor = 0;
    private int lSteps;
    private double lx;
    private double ly;
    private double lz;
    private double lyr;
    private double lxr;
    private double lxd;
    private double lyd;
    private double lzd;

    private int rideForTicks = 0;

    private float boardForwards = 0.0F;
    private int removeIn;
    private Player returnToPlayer = null;

    int jumpWindow = 0;

    public DiscEntity(EntityType<?> p_i48580_1_, Level p_i48580_2_) {
        super(p_i48580_1_, p_i48580_2_);
        this.blocksBuilding = true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    public DiscEntity(Level worldIn, double x, double y, double z) {
        this(ModEntities.DISC_ENTITY.get(), worldIn);
        this.setPos(x, y, z);
        this.setDeltaMovement(Vec3.ZERO);
        this.xo = x;
        this.yo = y;
        this.zo = z;
    }

    public static boolean canVehicleCollide(Entity p_242378_0_, Entity entity) {
        return (entity.canBeCollidedWith() || entity.isPushable()) && !p_242378_0_.isPassengerOfSameVehicle(entity);
    }

    public boolean shouldRiderSit() {
        return false;
    }

    public boolean canCollideWith(Entity entity) {
        return canVehicleCollide(this, entity);
    }


    public void push(Entity entityIn) {
        if (entityIn instanceof DiscEntity) {
            if (entityIn.getBoundingBox().minY < this.getBoundingBox().maxY) {
                super.push(entityIn);
            }
        } else if (entityIn.getBoundingBox().minY <= this.getBoundingBox().minY) {
            super.push(entityIn);
        }

    }

    public boolean isRemoveLogic() {
        return this.isRemoved();
    }

    public boolean canBeCollidedWith() {
        return !this.isRemoveLogic();
    }

    public boolean isPushable() {
        return !this.isRemoveLogic();
    }

    public boolean isPickable() {
        return !this.isRemoveLogic();
    }

    public boolean shouldBeSaved() {
        return !this.isRemoveLogic();
    }

    public boolean isAttackable() {
        return !this.isRemoveLogic();
    }

    public void tick() {
        super.tick();
        if (jumpFor > 0) {
            jumpFor--;
        }
        if (jumpWindow > 0) {
            jumpWindow--;
        }
        if (extinguishTimer > 0) {
            extinguishTimer--;
        }
        Entity controller = getControllingPlayer();
        if (this.level().isClientSide) {
            if (this.lSteps > 0) {
                double d5 = this.getX() + (this.lx - this.getX()) / (double) this.lSteps;
                double d6 = this.getY() + (this.ly - this.getY()) / (double) this.lSteps;
                double d7 = this.getZ() + (this.lz - this.getZ()) / (double) this.lSteps;
                this.setYRot(Mth.wrapDegrees((float) this.lyr));
                this.setXRot(this.getXRot() + (float) (this.lxr - (double) this.getXRot()) / (float) this.lSteps);
                --this.lSteps;
                this.setPos(d5, d6, d7);
                this.setRot(this.getYRot(), this.getXRot());
            } else {
                this.reapplyPosition();
                this.setRot(this.getYRot(), this.getXRot());
            }
        } else {
            this.checkInsideBlocks();
            float slowdown = 0.98F;
            tickMovement();
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().multiply(slowdown, slowdown, slowdown));
            float f2 = (float) -((float) this.getDeltaMovement().y * 0.5F * (double) Mth.RAD_TO_DEG);
            this.setXRot(Mth.approachDegrees(this.getXRot(), f2, 5));

            if (controller instanceof Player player) {
                returnToPlayer = player;
                rideForTicks++;
                if (player.getRemainingFireTicks() > 0 && extinguishTimer == 0) {
                    player.clearFire();
                }
                this.setYRot(Mth.approachDegrees(this.getYRot(), player.getYRot(), 6));
                Vec3 deltaMovement = this.getDeltaMovement();
                if (deltaMovement.y > -0.5D) {
                    this.fallDistance = 1.0F;
                }

                float slow = player.zza < 0 ? 0 : player.zza * 0.115F;

                boardForwards = slow;

                if (player.isShiftKeyDown() || !this.isAlive()) {
                    this.ejectPassengers();
                }
                if (player.isInWall()) {
                    this.ejectPassengers();
                    this.hurt(damageSources().generic(), 100);
                }
            } else {
                rideForTicks = 0;
            }
        }
    }

    private void tickMovement() {
        this.hasImpulse = true;
        float moveForwards = Math.min(boardForwards, 1.0F);
        float yRot = this.getYRot();
        Vec3 prev = this.getDeltaMovement();
        float gravity = isOnRideableSurface() ? 0F : (isInLava() || isInWater() ? 1.0F : -1f);
        float f1 = -Mth.sin(yRot * ((float) Math.PI / 180F));
        float f2 = Mth.cos(yRot * ((float) Math.PI / 180F));
        Vec3 moveVec = new Vec3(f1, 0, f2).scale(moveForwards);
        Vec3 vec31 = prev.scale(0.975F).add(moveVec);
        float jumpGravity = gravity;
        if (jumpFor > 0) {
            float jumpRunsOutIn = jumpFor < 5 ? jumpFor / 5F : 1F;
            jumpGravity += jumpRunsOutIn + jumpRunsOutIn * 1F;
        }
        this.setDeltaMovement(vec31.x, jumpGravity, vec31.z);
    }

    private boolean isOnRideableSurface() {
//        return true;
        BlockPos ourPos = BlockPos.containing(this.getX(), this.getY() + 0.4F, this.getZ());
        BlockPos underPos = this.getOnPos();
        return this.onGround() || !level.getBlockState(underPos).getFluidState().isEmpty();
    }

    @Override
    public float maxUpStep() {
        return 2.0f;
    }

    @Override
    public void lerpTo(double x, double y, double z, float yr, float xr, int steps) {
        this.lx = x;
        this.ly = y;
        this.lz = z;
        this.lyr = yr;
        this.lxr = xr;
        this.lSteps = steps;
        this.setDeltaMovement(this.lxd, this.lyd, this.lzd);
    }


    @Override
    public void lerpMotion(double lerpX, double lerpY, double lerpZ) {
        this.lxd = lerpX;
        this.lyd = lerpY;
        this.lzd = lerpZ;
        this.setDeltaMovement(this.lxd, this.lyd, this.lzd);
    }

    public double getEyeY() {
        return this.getY() + 0.3F;
    }

    @Nullable
    public LivingEntity getControllingPassenger() {
        return getControllingPlayer();
    }

    @Nullable
    public boolean isControlledByLocalInstance() {
        return false;
    }

    @Nullable
    public Player getControllingPlayer() {
        for (Entity passenger : this.getPassengers()) {
            if (passenger instanceof Player) {
                return (Player) passenger;
            }
        }
        return null;
    }

    @Override
    protected void addPassenger(Entity passenger) {
        super.addPassenger(passenger);
        if (this.isControlledByLocalInstance() && this.lSteps > 0) {
            this.lSteps = 0;
            this.absMoveTo(this.lx, this.ly, this.lz, (float) this.lyr, (float) this.lxr);
        }
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (player.isSecondaryUseActive()) {
            return InteractionResult.PASS;
        } else {
            if (!this.level().isClientSide) {
                return player.startRiding(this) ? InteractionResult.CONSUME : InteractionResult.PASS;
            } else {
                return InteractionResult.SUCCESS;
            }
        }
    }


    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return MovementEmission.EVENTS;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
    }

    @Override
    public void onPlayerJump(int i) {

    }

    @Override
    public boolean canJump() {
        if (isOnRideableSurface()) {
            jumpWindow = 20;
        }
        return jumpWindow > 0;
    }

    @Override
    public void handleStartJump(int i) {
        this.hasImpulse = true;
        if (canJump()) {
            float f = 0.075F;
            jumpFor = 5 + (int) (i * f);
        }
    }

    public Vec3 getDismountLocationForPassenger(LivingEntity entity) {
        return new Vec3(this.getX(), this.getY() + 2F, this.getZ());
    }

    @Override
    public void handleStopJump() {

    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}