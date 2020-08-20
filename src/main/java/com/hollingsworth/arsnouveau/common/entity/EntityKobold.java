package com.hollingsworth.arsnouveau.common.entity;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class EntityKobold extends FlyingEntity {

    BlockPos crystalPos;
    protected EntityKobold(EntityType<? extends FlyingEntity> p_i48568_1_, World p_i48568_2_) {
        super(p_i48568_1_, p_i48568_2_);
        this.moveController = new MoveHelperController(this);

    }

    public EntityKobold(World p_i50190_2_) {
        super(ModEntities.ENTITY_KOBOLD_TYPE, p_i50190_2_);
        this.moveController = new MoveHelperController(this);
    }

    public EntityKobold(World world, BlockPos crystalPos){
        this(world);
        this.crystalPos = crystalPos;
    }

    @Override
    protected PathNavigator createNavigator(World world) {
        FlyingPathNavigator flyingpathnavigator = new FlyingPathNavigator(this, world);
        flyingpathnavigator.setCanOpenDoors(false);
        flyingpathnavigator.setCanSwim(true);
        flyingpathnavigator.setCanEnterDoors(true);
        return flyingpathnavigator;
    }

    protected void registerGoals() {

//        this.goalSelector.addGoal(1, new PanicGoal(this, 1.25D));
//        this.goalSelector.addGoal(1, new LevitateGoal());
        this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
    }

    protected void updateAITasks() {
        EntityKobold kobold = EntityKobold.this;
        boolean flyUp = false;
        boolean stopMoving = true;
        int blocksBelow = 0;
        while(!kobold.world.getBlockState(kobold.getPosition().down(blocksBelow)).isSolid() && blocksBelow < 5){
            blocksBelow++;
        }
        stopMoving = blocksBelow == 2;
        flyUp = blocksBelow <= 2 ;
        if(stopMoving)
            return;
       if(flyUp && !world.getBlockState(EntityKobold.this.getPosition().up()).isSolid()){
           Vec3d lvt_2_1_ = this.getMotion();
           this.setMotion(this.getMotion().add(0.0D, (0.30000001192092896D - lvt_2_1_.y) * 0.30000001192092896D, 0.0D));
           this.isAirBorne = true;
       }else{
           if(!world.getBlockState(EntityKobold.this.getPosition().down()).isSolid()){
               Vec3d lvt_2_1_ = this.getMotion();
               this.setMotion(this.getMotion().add(0.0D, (-0.15 - lvt_2_1_.y) * 0.30000001192092896D, 0.0D));
               this.isAirBorne = true;
           }

       }


        super.updateAITasks();
    }
    @Override
    public EntityType<?> getType() {
        return ModEntities.ENTITY_KOBOLD_TYPE;
    }


    @Override
    public void writeAdditional(CompoundNBT tag) {
        super.writeAdditional(tag);
        if(crystalPos != null){
            tag.putInt("summoner_x", crystalPos.getX());
            tag.putInt("summoner_y", crystalPos.getY());
            tag.putInt("summoner_z", crystalPos.getZ());

        }
    }

    @Override
    public void readAdditional(CompoundNBT tag) {
        super.readAdditional(tag);
        if(tag.contains("summoner_x"))
            crystalPos = new BlockPos(tag.getInt("summoner_x"), tag.getInt("summoner_y"), tag.getInt("summoner_z"));
    }

    @Override
    public ILivingEntityData onInitialSpawn(IWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
        return super.onInitialSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
    }

    static class MoveHelperController extends MovementController {
        private final EntityKobold parentEntity;
        private int courseChangeCooldown;

        public MoveHelperController(EntityKobold p_i45838_1_) {
            super(p_i45838_1_);
            this.parentEntity = p_i45838_1_;
        }

        public void tick() {
            if (this.action == Action.MOVE_TO) {
                if (this.courseChangeCooldown-- <= 0) {
                    this.courseChangeCooldown += this.parentEntity.getRNG().nextInt(5) + 2;
                    Vec3d lvt_1_1_ = new Vec3d(this.posX - this.parentEntity.getPosX(), this.posY - this.parentEntity.getPosY(), this.posZ - this.parentEntity.getPosZ());
                    double lvt_2_1_ = lvt_1_1_.length();
                    lvt_1_1_ = lvt_1_1_.normalize();
                    if (this.func_220673_a(lvt_1_1_, MathHelper.ceil(lvt_2_1_))) {
                        this.parentEntity.setMotion(this.parentEntity.getMotion().add(lvt_1_1_.scale(0.1D)));
                    } else {
                        this.action = Action.WAIT;
                    }
                }

            }
        }

        private boolean func_220673_a(Vec3d p_220673_1_, int p_220673_2_) {
            AxisAlignedBB lvt_3_1_ = this.parentEntity.getBoundingBox();

            for(int lvt_4_1_ = 1; lvt_4_1_ < p_220673_2_; ++lvt_4_1_) {
                lvt_3_1_ = lvt_3_1_.offset(p_220673_1_);
                if (!this.parentEntity.world.hasNoCollisions(this.parentEntity, lvt_3_1_)) {
                    return false;
                }
            }

            return true;
        }
    }
    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void livingTick() {
      super.livingTick();

    }


    class LevitateGoal extends Goal {
        LevitateGoal() {
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean shouldExecute() {
            EntityKobold kobold = EntityKobold.this;
            kobold.world.getBlockState(kobold.getPosition());
            int yCorrection = 0;
            int blocksBelow = 0;
            while(!kobold.world.getBlockState(kobold.getPosition().down(blocksBelow)).isSolid() && blocksBelow < 4){
                blocksBelow++;
            }
            System.out.println(blocksBelow);
            return EntityKobold.this.navigator.noPath() && blocksBelow != 3;
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean shouldContinueExecuting() {
            EntityKobold kobold = EntityKobold.this;
            return kobold.navigator.func_226337_n_();
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void startExecuting() {

            BlockPos loc = getFloatingLoc();
            EntityKobold.this.navigator.setPath(EntityKobold.this.navigator.getPathToPos(loc, 1), 1.0D);
            EntityKobold.this.moveController.setMoveTo(loc.getX(), loc.getY(), loc.getZ(), 0.1);
            System.out.println(EntityKobold.this.navigator.getPathToPos(loc, 1).getTarget());

        }
        @Nullable
        private BlockPos getFloatingLoc() {
            Vec3d vec3d = EntityKobold.this.getLook(0.0F);
            boolean flyUp = false;
            for(int i =0; i < 3; i++){
                if(world.getBlockState(EntityKobold.this.getPosition().down(i)).isSolid()){ // Too close to the ground
                    flyUp = true;
                    continue;
                }
            }
            return flyUp ? EntityKobold.this.getPosition().up() : EntityKobold.this.getPosition().down();
        }

        @Override
        public void tick() {
            super.tick();

//            if (--this.timeToRecalcPath <= 0) {
//                this.timeToRecalcPath = 10;
//
//                if (!this.navigator.tryMoveToEntityLiving(this.summon.getSummoner(), this.followSpeed)) {
//
//                    if (!(this.summon.getSelfEntity().getDistanceSq(this.summon.getSummoner()) < 144.0D)) {
//                        int i = MathHelper.floor(this.summon.getSummoner().getPosX()) - 2;
//                        int j = MathHelper.floor(this.summon.getSummoner().getPosZ()) - 2;
//                        int k = MathHelper.floor(this.summon.getSummoner().getBoundingBox().minY);
//
//                        for(int l = 0; l <= 4; ++l) {
//                            for(int i1 = 0; i1 <= 4; ++i1) {
//                                if ((l < 1 || i1 < 1 || l > 3 || i1 > 3) && this.canTeleportToBlock(new BlockPos(i + l, k - 1, j + i1))) {
//                                    this.summon.getSelfEntity().setLocationAndAngles((double)((float)(i + l) + 0.5F), (double)k, (double)((float)(j + i1) + 0.5F), this.summon.getSelfEntity().rotationYaw, this.summon.getSelfEntity().rotationPitch);
//                                    this.navigator.clearPath();
//                                    return;
//                                }
//                            }
//                        }
//                    }
//                }
//            }
        }
    }
}
