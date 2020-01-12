package com.hollingsworth.craftedmagic.entity;

import com.hollingsworth.craftedmagic.ExampleMod;
import com.hollingsworth.craftedmagic.spell.SpellResolver;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityProjectileSpell extends ThrowableEntity {

    private int age;
    private SpellResolver spellResolver;
    public int xpColor;

    public EntityProjectileSpell(final EntityType<? extends EntityProjectileSpell> entityType, World worldIn){
        super(entityType,worldIn);
        this.age = 0;
        this.spellResolver = null;
    }
    public EntityProjectileSpell(EntityType<? extends ThrowableEntity> type, World worldIn, SpellResolver spellResolver) {
        super(type, worldIn);
        this.spellResolver = spellResolver;
        age = 0;
    }


    public EntityProjectileSpell(EntityType<? extends ThrowableEntity> type, World worldIn, SpellResolver spellResolver,  double x, double y, double z) {
        super(type, x, y, z, worldIn);
        this.spellResolver = spellResolver;
        age = 0;
    }


    public EntityProjectileSpell(EntityType<? extends ThrowableEntity> type, World world, LivingEntity thrower, SpellResolver spellResolver) {
        super(type,  thrower, world);
        this.spellResolver = spellResolver;
        age = 0;
    }

    /**
     * Sets throwable heading based on an entity that's throwing it
     */
    public void shoot(Entity entityThrower, float rotationPitchIn, float rotationYawIn, float pitchOffset, float velocity, float inaccuracy)
    {
        float f = -MathHelper.sin(rotationYawIn * ((float)Math.PI / 180F)) * MathHelper.cos(rotationPitchIn * ((float)Math.PI / 180F));
        float f1 = -MathHelper.sin((rotationPitchIn + pitchOffset) * ((float)Math.PI / 180F));
        float f2 = MathHelper.cos(rotationYawIn * ((float)Math.PI / 180F)) * MathHelper.cos(rotationPitchIn * ((float)Math.PI / 180F));
        this.shoot((double)f, (double)f1, (double)f2, velocity, inaccuracy);
        Vec3d vec3d = entityThrower.getMotion();
        this.setMotion(this.getMotion().add(vec3d.x, entityThrower.onGround ? 0.0D : vec3d.y, vec3d.z));
    }

    /**
     * Similar to setArrowHeading, it's point the throwable entity to a x, y, z direction.
     */
    public void shoot(double x, double y, double z, float velocity, float inaccuracy)
    {
        Vec3d vec3d = (new Vec3d(x, y, z)).normalize().add(this.rand.nextGaussian() * (double)0.0075F * (double)inaccuracy, this.rand.nextGaussian() * (double)0.0075F * (double)inaccuracy, this.rand.nextGaussian() * (double)0.0075F * (double)inaccuracy).scale((double)velocity);
        this.setMotion(vec3d);
        float f = MathHelper.sqrt(func_213296_b(vec3d));
        this.rotationYaw = (float)(MathHelper.atan2(vec3d.x, vec3d.z) * (double)(180F / (float)Math.PI));
        this.rotationPitch = (float)(MathHelper.atan2(vec3d.y, (double)f) * (double)(180F / (float)Math.PI));
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

            this.remove();

        }

    }


    /**
     * Checks if the entity is in range to render.
     */
    @OnlyIn(Dist.CLIENT)
    public boolean isInRangeToRenderDist(double distance)
    {
        double d0 = this.getBoundingBox().getAverageEdgeLength() * 4.0D;

        if (Double.isNaN(d0))
        {
            d0 = 4.0D;
        }

        d0 = d0 * 64.0D;
        return distance < d0 * d0;
    }


    private boolean damageable(LivingEntity entity) {
        boolean out = entity.isInvulnerable() || entity.world.isRemote || (entity.getHealth() <= 0.0);
        if(entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity)entity;
            out = out || player.isCreative();
        }
        return !out;
    }


    @Override
    public float getGravityVelocity() {
        return 0.0f;
    }


    @Override
    public void tick() {
        age++;
        ++this.xpColor;
        if ((age >= 200) && !world.isRemote) {
            this.remove();
        }
        super.tick();
    }

    @OnlyIn(Dist.CLIENT)
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
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        age = nbt.getInt("Age");
    }

    @Override
    public CompoundNBT serializeNBT() {
        super.serializeNBT();
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt("Age", age);
        return nbt;
    }


    @OnlyIn(Dist.CLIENT)
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
    protected void registerData() {

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
