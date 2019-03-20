package com.hollingsworth.craftedmagic.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class EntityProjectileSpell extends Entity {

    private final int maxTicksToExist;
    private int ticksExisted;

    public EntityProjectileSpell(World world){
        super(world);
        ticksExisted = 0;
        maxTicksToExist = -1;
        this.noClip = true;
    }

    @Override
    protected void entityInit() {
        
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {

    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {

    }

    @Override
    public void onUpdate() {
        try {
            if (ticksExisted > 200)
                this.setDead();
            RayTraceResult mop = world.rayTraceBlocks(new Vec3d(posX, posY, posZ),new Vec3d(posX + motionX, posY + motionY, posZ + motionZ));
            if (mop != null && mop.typeOfHit.equals(RayTraceResult.Type.BLOCK)) {
                if (world.getBlockState(mop.getBlockPos()).getBlock().getDefaultState().isSideSolid(world, mop.getBlockPos(), mop.sideHit)) {
                    world.getBlockState(mop.getBlockPos()).getBlock().onEntityCollidedWithBlock(world, mop.getBlockPos(), world.getBlockState(mop.getBlockPos()), this);

//                    SpellUtils.applyStageToGround(getSpell(), getShooter(), world, mop.getBlockPos(), mop.sideHit, posX, posY, posZ, true);
//                    SpellUtils.applyStackStage(getSpell(), getShooter(), null, mop.hitVec.xCoord + motionX, mop.hitVec.yCoord + motionY, mop.hitVec.zCoord + motionZ, mop.sideHit, world, false, true, 0);
//                    if (this.getPierces() == 1 || !SpellUtils.modifierIsPresent(SpellModifiers.PIERCING, this.getSpell()))
//                        this.setDead();
//                    else
//                        this.currentPierces++;
                }
            } else {
                List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox().grow(motionX, motionY, motionZ).expand(0.25D, 0.25D, 0.25D));
                int effSize = list.size();
                for (Entity entity : list) {
                    if (entity instanceof EntityLivingBase) {
                        if (entity.equals(getShooter())) {
                            effSize--;
                            continue;
                        }
//
//                        SpellUtils.applyStageToEntity(getSpell(), getShooter(), world, entity, true);
//                        SpellUtils.applyStackStage(getSpell(), getShooter(), (EntityLivingBase) entity, entity.posX, entity.posY, entity.posZ, null, world, false, true, 0);
                        break;
                    } else {
                        effSize--;
                    }
                }
                if (effSize != 0) {
//                    if (this.getPierces() == 1 || !SpellUtils.modifierIsPresent(SpellModifiers.PIERCING, this.getSpell()))
//                        this.setDead();
//                    else
//                        this.currentPierces++;
                }
            }
            //motionY += this.getDataManager().get(DW_GRAVITY);
            setPosition(posX + motionX, posY + motionY, posZ + motionZ);
        } catch (NullPointerException e) {
            this.setDead();
        }
    }

    private Entity getShooter() {
        return null;
    }
}
