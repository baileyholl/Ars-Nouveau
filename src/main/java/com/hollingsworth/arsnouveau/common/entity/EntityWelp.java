package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.block.tile.SummoningCrytalTile;
import com.hollingsworth.arsnouveau.common.spell.EntitySpellResolver;
import com.hollingsworth.arsnouveau.common.spell.SpellResolver;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectBlink;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectGrow;
import com.hollingsworth.arsnouveau.common.spell.method.MethodTouch;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.Direction;
import net.minecraft.util.math.*;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;

public class EntityWelp extends FlyingEntity {

    BlockPos crystalPos;
    int ticksSinceLastSpell;


    protected EntityWelp(EntityType<? extends FlyingEntity> p_i48568_1_, World p_i48568_2_) {
        super(p_i48568_1_, p_i48568_2_);
        this.moveController =  new FlyingMovementController(this, 10, true);

    }

    public EntityWelp(World p_i50190_2_) {
        super(ModEntities.ENTITY_KOBOLD_TYPE, p_i50190_2_);
        this.moveController = new FlyingMovementController(this, 10, true);
    }

    public EntityWelp(World world, BlockPos crystalPos){
        this(world);
        this.crystalPos = crystalPos;
    }

    @Override
    public void tick() {
        super.tick();
        ticksSinceLastSpell += 1;
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
        this.goalSelector.addGoal(6, new PerformTaskGoal(this));
        this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
    }

    public boolean canPerformAnotherTask(){
        return  ticksSinceLastSpell > 100;
    }

    public BlockPos getTaskLoc(){
        if(world.getTileEntity(crystalPos) instanceof SummoningCrytalTile){
            return ((SummoningCrytalTile) world.getTileEntity(crystalPos)).getNextTaskLoc();
        }
        return null;
    }

    public void castSpell(BlockPos target){
        System.out.println("Casting");
        if(world.isRemote)
            return;
        if(world instanceof ServerWorld){


            double d0 = target.getX() +0.5; //+ world.rand.nextFloat();
            double d1 = target.getY() + 1;//+ world.rand.nextFloat() ;
            double d2 = target.getZ() +0.5; //+ world.rand.nextFloat();

            ((ServerWorld)world).spawnParticle(ParticleTypes.ENCHANTED_HIT, d0, d1, d2,rand.nextInt(4), 0,0.3,0, 0.1);

        }
        this.ticksSinceLastSpell = 0;
        EntitySpellResolver resolver = new EntitySpellResolver(new AbstractSpellPart[]{
           new MethodTouch(), new EffectGrow()
        });
        resolver.onCastOnBlock(new BlockRayTraceResult(new Vec3d(target.getX(), target.getY(), target.getZ()), Direction.UP,target, false ), this);
    }

    public boolean enoughManaForTask(){
        return true;
    }

    protected void updateAITasks() {
//        EntityKobold kobold = EntityKobold.this;
//        boolean flyUp = false;
//        boolean stopMoving = true;
//        int blocksBelow = 0;
//        while(!kobold.world.getBlockState(kobold.getPosition().down(blocksBelow)).isSolid() && blocksBelow < 5){
//            blocksBelow++;
//        }
//        stopMoving = blocksBelow == 2;
//        flyUp = blocksBelow <= 2 ;
//        if(stopMoving)
//            return;
//       if(flyUp && !world.getBlockState(EntityKobold.this.getPosition().up()).isSolid()){
//           Vec3d lvt_2_1_ = this.getMotion();
//           this.setMotion(this.getMotion().add(0.0D, (0.30000001192092896D - lvt_2_1_.y) * 0.30000001192092896D, 0.0D));
//           this.isAirBorne = true;
//       }else{
//           if(!world.getBlockState(EntityKobold.this.getPosition().down()).isSolid()){
//               Vec3d lvt_2_1_ = this.getMotion();
//               this.setMotion(this.getMotion().add(0.0D, (-0.15 - lvt_2_1_.y) * 0.30000001192092896D, 0.0D));
//               this.isAirBorne = true;
//           }
//
//       }


        super.updateAITasks();
    }


    public static class PerformTaskGoal extends Goal {
        EntityWelp kobold;
        BlockPos taskLoc;
        int timePerformingTask;
        public PerformTaskGoal(EntityWelp kobold){
            this.kobold = kobold;
            this.setMutexFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public void startExecuting() {
            super.startExecuting();
            System.out.println("Executing");
            this.kobold.getNavigator().clearPath();
            taskLoc = this.kobold.getTaskLoc();
            timePerformingTask = 0;
            this.kobold.navigator.setPath(this.kobold.navigator.getPathToPos(taskLoc, 1), 1.0f);
        }

        @Override
        public void tick() {
            super.tick();
            timePerformingTask++;
            if(BlockUtil.distanceFrom(kobold.getPosition(), taskLoc) <= 1){
                kobold.castSpell(taskLoc);
                kobold.navigator.clearPath();
            }else{
                this.kobold.navigator.setPath(this.kobold.navigator.getPathToPos(taskLoc.up(), 0), 1f);
            }
        }

        @Override
        public boolean shouldContinueExecuting() {
            System.out.println("Executing shoul");

            return kobold.ticksSinceLastSpell > 100 && this.taskLoc != null || timePerformingTask > 300;
        }

        @Override
        public boolean shouldExecute() {
            return kobold.canPerformAnotherTask() && kobold.enoughManaForTask();
        }
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
        tag.putInt("last_spell", ticksSinceLastSpell);
    }

    @Override
    public void readAdditional(CompoundNBT tag) {
        super.readAdditional(tag);
        if(tag.contains("summoner_x"))
            crystalPos = new BlockPos(tag.getInt("summoner_x"), tag.getInt("summoner_y"), tag.getInt("summoner_z"));

        ticksSinceLastSpell = tag.getInt("last_spell");
    }

    @Override
    public ILivingEntityData onInitialSpawn(IWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
        return super.onInitialSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void livingTick() {
        super.livingTick();
    }
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttributes().registerAttribute(SharedMonsterAttributes.FLYING_SPEED);
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(6.0D);
        this.getAttribute(SharedMonsterAttributes.FLYING_SPEED).setBaseValue((double)0.4F);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double)0.2F);
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
            EntityWelp kobold = EntityWelp.this;
            kobold.world.getBlockState(kobold.getPosition());
            int yCorrection = 0;
            int blocksBelow = 0;
            while(!kobold.world.getBlockState(kobold.getPosition().down(blocksBelow)).isSolid() && blocksBelow < 4){
                blocksBelow++;
            }
            System.out.println(blocksBelow);
            return EntityWelp.this.navigator.noPath() && blocksBelow != 3;
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean shouldContinueExecuting() {
            EntityWelp kobold = EntityWelp.this;
            return kobold.navigator.func_226337_n_();
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void startExecuting() {

            BlockPos loc = getFloatingLoc();
            EntityWelp.this.navigator.setPath(EntityWelp.this.navigator.getPathToPos(loc, 1), 1.0D);
            EntityWelp.this.moveController.setMoveTo(loc.getX(), loc.getY(), loc.getZ(), 0.1);
            System.out.println(EntityWelp.this.navigator.getPathToPos(loc, 1).getTarget());

        }
        @Nullable
        private BlockPos getFloatingLoc() {
            Vec3d vec3d = EntityWelp.this.getLook(0.0F);
            boolean flyUp = false;
            for(int i =0; i < 3; i++){
                if(world.getBlockState(EntityWelp.this.getPosition().down(i)).isSolid()){ // Too close to the ground
                    flyUp = true;
                    continue;
                }
            }
            return flyUp ? EntityWelp.this.getPosition().up() : EntityWelp.this.getPosition().down();
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
