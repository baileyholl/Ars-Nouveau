package com.hollingsworth.craftedmagic.entity;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.hollingsworth.craftedmagic.ExampleMod;
import com.hollingsworth.craftedmagic.api.Position;
import com.hollingsworth.craftedmagic.spell.SpellResolver;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.util.*;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class EntityProjectileSpell extends EntityThrowable {

    private int age;
    private SpellResolver spellResolver;
    public int xpColor;
    public EntityProjectileSpell(World worldIn){
        super(worldIn);
        this.age = 0;
        this.spellResolver = null;
    }
    public EntityProjectileSpell(World worldIn, SpellResolver spellResolver) {
        super(worldIn);
        this.spellResolver = spellResolver;
        age = 0;
    }


    public EntityProjectileSpell(World worldIn, SpellResolver spellResolver,  double x, double y, double z) {
        super(worldIn, x, y, z);
        this.spellResolver = spellResolver;
        age = 0;
    }


    public EntityProjectileSpell(World world, EntityLivingBase thrower, SpellResolver spellResolver) {
        super(world, thrower);
        this.spellResolver = spellResolver;
        age = 0;
    }

    /**
     * Sets throwable heading based on an entity that's throwing it
     */
    public void shoot(Entity entityThrower, float rotationPitchIn, float rotationYawIn, float pitchOffset, float velocity, float inaccuracy)
    {
        float f = -MathHelper.sin(rotationYawIn * 0.017453292F) * MathHelper.cos(rotationPitchIn * 0.017453292F);
        float f1 = -MathHelper.sin((rotationPitchIn + pitchOffset) * 0.017453292F);
        float f2 = MathHelper.cos(rotationYawIn * 0.017453292F) * MathHelper.cos(rotationPitchIn * 0.017453292F);
        this.shoot((double)f, (double)f1, (double)f2, velocity, inaccuracy);
        this.motionX += entityThrower.motionX;
        this.motionZ += entityThrower.motionZ;

        if (!entityThrower.onGround)
        {
            this.motionY += entityThrower.motionY;
        }
    }

    /**
     * Similar to setArrowHeading, it's point the throwable entity to a x, y, z direction.
     */
    public void shoot(double x, double y, double z, float velocity, float inaccuracy)
    {
        float f = MathHelper.sqrt(x * x + y * y + z * z);
        x = x / (double)f;
        y = y / (double)f;
        z = z / (double)f;
        x = x + this.rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
        y = y + this.rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
        z = z + this.rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
        x = x * (double)velocity;
        y = y * (double)velocity;
        z = z * (double)velocity;
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;
        float f1 = MathHelper.sqrt(x * x + z * z);
        this.rotationYaw = (float)(MathHelper.atan2(x, z) * (180D / Math.PI));
        this.rotationPitch = (float)(MathHelper.atan2(y, (double)f1) * (180D / Math.PI));
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;

    }


    @Override
    protected void onImpact(RayTraceResult result) {
        if (!world.isRemote) {
            SoundEvent event = new SoundEvent(new ResourceLocation(ExampleMod.MODID, "resolve_spell"));
            world.playSound(null, this.posX, this.posY, this.posZ,
                    event, SoundCategory.BLOCKS,
                    4.0F, (1.0F + (this.world.rand.nextFloat()
                            - world.rand.nextFloat()) * 0.2F) * 0.7F);

            if(this.spellResolver != null && result != null)
                this.spellResolver.onResolveEffect(this.world, this.getThrower(), result);

            this.setDead();
        }

    }


    /**
     * Checks if the entity is in range to render.
     */
    @SideOnly(Side.CLIENT)
    public boolean isInRangeToRenderDist(double distance)
    {
        double d0 = this.getEntityBoundingBox().getAverageEdgeLength() * 4.0D;

        if (Double.isNaN(d0))
        {
            d0 = 4.0D;
        }

        d0 = d0 * 64.0D;
        return distance < d0 * d0;
    }


    private boolean damageable(EntityLivingBase entity) {
        boolean out = entity.getIsInvulnerable() || entity.world.isRemote || (entity.getHealth() <= 0.0);
        if(entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)entity;
            out = out || player.isCreative();
        }
        return !out;
    }


    @Override
    public float getGravityVelocity() {
        return 0.0f;
    }


    @Override
    public void onUpdate() {
        age++;
        ++this.xpColor;
        if ((age >= 200) && !world.isRemote) {
            this.setDead();
        }
        super.onUpdate();
    }

    @SideOnly(Side.CLIENT)
    public int getTextureByXP() {
        System.out.println(age);
        if (this.age >= 12)
        {
            return 10;
        }
        else if (this.age >= 11)
        {
            return 9;
        }
        else if (this.age >= 10)
        {
            return 8;
        }
        else if (this.age >= 9)
        {
            return 7;
        }
        else if (this.age >= 8)
        {
            return 6;
        }
        else if (this.age >= 7)
        {
            return 5;
        }
        else if (this.age >= 6)
        {
            return 4;
        }
        else if (this.age >= 5)
        {
            return 3;
        }
        else if (this.age >= 4)
        {
            return 2;
        }
        else
        {
            return this.age >= 3 ? 1 : 0;
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("Age", age);
    }


    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        age = compound.getInteger("Age");

    }


    @SideOnly(Side.CLIENT)
    public int getBrightnessForRender()
    {
        float f = 0.5F;
        f = MathHelper.clamp(f, 0.0F, 1.0F);
        int i = super.getBrightnessForRender();
        int j = i & 255;
        int k = i >> 16 & 255;
        j = j + (int)(f * 15.0F * 16.0F);

        if (j > 240)
        {
            j = 240;
        }

        return j | k << 16;
    }


    @Override
    public boolean hasNoGravity() {
        return true;
    }


    @Override
    public boolean isInWater() {
        return false;
    }
}
