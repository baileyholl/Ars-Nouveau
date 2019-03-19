package com.hollingsworth.craftedmagic.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ProjectileSpell  extends Entity {
    private final int maxTicksToExist;
    private int ticksExisted;
    public ProjectileSpell(World world){
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

    public ProjectileSpell(World world, EntityLivingBase entityLiving, double projectileSpeed){
        super(world);
        this.noClip = true;
        setSize(0.25F, 0.25F);
        setLocationAndAngles(entityLiving.posX, entityLiving.posY + entityLiving.getEyeHeight(), entityLiving.posZ, entityLiving.rotationYaw, entityLiving.rotationPitch);
        posX -= MathHelper.cos((rotationYaw / 180F) * 3.141593F) * 0.16F;
        posY -= 0.10000000149011612D;
        posZ -= MathHelper.sin((rotationYaw / 180F) * 3.141593F) * 0.16F;
        setPosition(posX, posY, posZ);
        yOffset = 0.0F;
        float f = 0.01F;
        motionX = -MathHelper.sin((rotationYaw / 180F) * 3.141593F) * MathHelper.cos((rotationPitch / 180F) * 3.141593F) * f;
        motionZ = MathHelper.cos((rotationYaw / 180F) * 3.141593F) * MathHelper.cos((rotationPitch / 180F) * 3.141593F) * f;
        motionY = -MathHelper.sin((rotationPitch / 180F) * 3.141593F) * f;
        maxTicksToExist = -1;
        setSpellProjectileHeading(motionX, motionY, motionZ, projectileSpeed, projectileSpeed);
    }
}
