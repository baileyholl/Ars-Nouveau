package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;
import software.bernie.geckolib3.core.easing.EasingManager;
import software.bernie.geckolib3.core.easing.EasingType;


public class EntityFlyingItem extends EntityFollowProjectile {

    public int age;
    //    int age;
    int maxAge;

    public static final DataParameter<ItemStack> HELD_ITEM = EntityDataManager.defineId(EntityFlyingItem.class, DataSerializers.ITEM_STACK);
    public static final DataParameter<Float> OFFSET = EntityDataManager.defineId(EntityFlyingItem.class, DataSerializers.FLOAT);
    public static final DataParameter<Boolean> DIDOFFSET = EntityDataManager.defineId(EntityFlyingItem.class, DataSerializers.BOOLEAN);

    public EntityFlyingItem(World worldIn, Vector3d from, Vector3d to) {
        this(ModEntities.ENTITY_FLYING_ITEM, worldIn);
        this.entityData.set(EntityFollowProjectile.to, new BlockPos(to));
        this.entityData.set(EntityFollowProjectile.from, new BlockPos(from));
//        this.age = 0;
        this.maxAge = (int) Math.floor(from.subtract(to).length() * 5);
        setPos(from.x + 0.5, from.y, from.z+ 0.5);
        this.entityData.set(RED, 255);
        this.entityData.set(GREEN, 25);
        this.entityData.set(BLUE, 180);
    }
    public EntityFlyingItem(World worldIn, BlockPos from, BlockPos to) {
        this(worldIn, new Vector3d(from.getX(), from.getY(), from.getZ()), new Vector3d(to.getX(), to.getY(), to.getZ()));
    }
    public EntityFlyingItem(World worldIn, BlockPos from, BlockPos to, int r, int g, int b) {
        this(worldIn, new Vector3d(from.getX(), from.getY(), from.getZ()), new Vector3d(to.getX(), to.getY(), to.getZ()));
        this.entityData.set(RED, r);
        this.entityData.set(GREEN, g);
        this.entityData.set(BLUE, b);
    }

    public EntityFlyingItem(EntityType<EntityFlyingItem> entityAOEProjectileEntityType, World world) {
        super(entityAOEProjectileEntityType, world);
    }

    /**
     * This is the actual function that smoothly interpolates (lerp) between keyframes
     *
     * @param startValue The animation's start value
     * @param endValue   The animation's end value
     * @return The interpolated value
     */
    public static float lerp(double percentCompleted, double startValue, double endValue, EasingType type)
    {
        if(percentCompleted >= 1)
        {
            return (float) endValue;
        }
        percentCompleted = EasingManager.ease(percentCompleted, type, null);
        // current tick / position should be between 0 and 1 and represent the percentage of the lerping that has completed
        return (float) lerpInternal(percentCompleted, startValue,
                endValue);
    }

    public static double lerpInternal(double pct, double start, double end) {
        return start + pct * (end - start);
    }

    /**
     * Calculates a value between 0 and 1, given the precondition that value
     * is between min and max. 0 means value = max, and 1 means value = min.
     */
    public double normalize(double value, double min, double max) {
        return 1 - ((value - min) / (max - min));
    }
    boolean wentUp;

    @Override
    public void tick() {

        this.age++;


        if(age > 400)
            this.remove();


        Vector3d vec3d2 = this.getDeltaMovement();
        BlockPos start = entityData.get(from);
        BlockPos end = entityData.get(to);
        if(BlockUtil.distanceFrom(this.blockPosition(), end) < 1 || this.age > 1000 || BlockUtil.distanceFrom(this.blockPosition(), end) > 14){
            this.remove();
            if(level.isClientSide && entityData.get(SPAWN_TOUCH))
                ParticleUtil.spawnTouch((ClientWorld) level, end);
            return;
        }
        double posX = getX();
        double posY = getY();
        double posZ = getZ();



        double time = 1 - normalize((double)age, 0.0, 80);

        EasingType type = EasingType.NONE;

        double startY = start.getY();
        double endY = end.getY() + getDistanceAdjustment(start, end);// BlockUtil.distanceFrom(start, end)*3;
        double lerpX = lerp(time, (double)start.getX() +0.5, (double)end.getX()+0.5, type);
        double lerpY = lerp(time, lerp(time, startY, endY, type), lerp(time,endY, startY, type), type);
        double lerpZ = lerp(time,(double)start.getZ()+0.5, (double)end.getZ()+0.5, type);

        BlockPos adjustedPos = new BlockPos(posX, end.getY(), posZ);
        if(BlockUtil.distanceFrom(adjustedPos, end) <= 0.5){
            posY  = getY() - 0.05;
          //  this.setPosition(lerpX, posY - 0.05, lerpZ);
            this.setPos(lerpX, posY, lerpZ);
        }else{
            this.setPos(lerpX, lerpY, lerpZ);
        }

        if(level.isClientSide && this.age > 1) {
            double deltaX = getX() - xOld;
            double deltaY = getY() - yOld;
            double deltaZ = getZ() - zOld;
            double dist = Math.ceil(Math.sqrt(deltaX*deltaX+deltaY*deltaY+deltaZ*deltaZ) * 20);
            int counter = 0;

            for (double i = 0; i < dist; i ++){
                double coeff = i/dist;
                counter += level.random.nextInt(3);
                if (counter % (Minecraft.getInstance().options.particles.getId() == 0 ? 1 : 2 * Minecraft.getInstance().options.particles.getId()) == 0) {
                    level.addParticle(GlowParticleData.createData(
                            new ParticleColor(this.entityData.get(RED),this.entityData.get(GREEN),this.entityData.get(BLUE))),
                            (float) (xo + deltaX * coeff), (float) (yo + deltaY * coeff), (float) (zo + deltaZ * coeff), 0.0125f * (random.nextFloat() - 0.5f), 0.0125f * (random.nextFloat() - 0.5f), 0.0125f * (random.nextFloat() - 0.5f));
                }
            }

        }
    }

    public EntityFlyingItem withNoTouch(){
        this.entityData.set(SPAWN_TOUCH, false);
        return this;
    }

    public void setDistanceAdjust(float offset){
        this.entityData.set(OFFSET, offset);
        this.entityData.set(DIDOFFSET, true);

    }

    private double getDistanceAdjustment(BlockPos start, BlockPos end) {
        if(this.entityData.get(DIDOFFSET))
            return this.entityData.get(OFFSET);

        double distance = BlockUtil.distanceFrom(start, end);

        if(distance <= 1.5)
            return 2.5;



        return 3;
    }

    public Vector3d getLerped(){
        BlockPos start = entityData.get(from);
        BlockPos end = entityData.get(to);
        double startY = start.getY();
        double endY = end.getY() +4.0;
        double time = 1 - normalize((double)age, 0.0, 100);

        double yOffset = -3.0;
        EasingType type = EasingType.NONE;
        double lerpX = lerp(time, (double)start.getX() +0.5, (double)end.getX()+0.5, type);
        double lerpY = lerp(time, lerp(time, startY, endY, type), lerp(time,endY, startY, type), type);
        double lerpZ = lerp(time,(double)start.getZ()+0.5, (double)end.getZ()+0.5, type);
        return new Vector3d(lerpX, lerpY, lerpZ);
    }
    @Override
    public void load(CompoundNBT compound) {
        super.load(compound);
        if(compound.contains("item")){
            this.entityData.set(HELD_ITEM, ItemStack.of(compound.getCompound("item")));
        }
        this.age = compound.getInt("age");
        this.entityData.set(DIDOFFSET,compound.getBoolean("didoffset"));
        this.entityData.set(OFFSET,compound.getFloat("offset") );
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        if(getStack() != null){
            CompoundNBT tag = new CompoundNBT();
            getStack().save(tag);
            compound.put("item", tag);
        }
        compound.putInt("age", age);
        compound.putBoolean("didoffset", this.entityData.get(DIDOFFSET));
        compound.putFloat("offset", this.entityData.get(OFFSET));
    }

    public ItemStack getStack(){
        return this.entityData.get(HELD_ITEM);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HELD_ITEM, ItemStack.EMPTY);
        this.entityData.define(OFFSET, 0.0f);
        this.entityData.define(DIDOFFSET, false);
    }

    @Override
    public boolean defaultsBurst() {
        return true;
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.ENTITY_FLYING_ITEM;
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public EntityFlyingItem(FMLPlayMessages.SpawnEntity packet, World world){
        super(ModEntities.ENTITY_FLYING_ITEM, world);
    }
}
