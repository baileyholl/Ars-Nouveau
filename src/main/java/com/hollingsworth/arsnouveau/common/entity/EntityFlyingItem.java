package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;
import software.bernie.geckolib.core.easing.EasingManager;
import software.bernie.geckolib.core.easing.EasingType;


public class EntityFlyingItem extends EntityFollowProjectile {

    public int age;
    //    int age;
    int maxAge;

    public static final DataParameter<ItemStack> HELD_ITEM = EntityDataManager.createKey(EntityFlyingItem.class, DataSerializers.ITEMSTACK);

    public EntityFlyingItem(World worldIn, Vec3d from, Vec3d to) {
        this(ModEntities.ENTITY_FLYING_ITEM, worldIn);
        this.dataManager.set(EntityFollowProjectile.to, new BlockPos(to));
        this.dataManager.set(EntityFollowProjectile.from, new BlockPos(from));
//        this.age = 0;
        this.maxAge = (int) Math.floor(from.subtract(to).length() * 5);
        setPosition(from.x + 0.5, from.y, from.z+ 0.5);
        this.dataManager.set(RED, 255);
        this.dataManager.set(GREEN, 25);
        this.dataManager.set(BLUE, 180);
    }
    public EntityFlyingItem(World worldIn, BlockPos from, BlockPos to) {
        this(worldIn, new Vec3d(from.getX(), from.getY(), from.getZ()), new Vec3d(to.getX(), to.getY(), to.getZ()));
    }
    public EntityFlyingItem(World worldIn, BlockPos from, BlockPos to, int r, int g, int b) {
        this(worldIn, new Vec3d(from.getX(), from.getY(), from.getZ()), new Vec3d(to.getX(), to.getY(), to.getZ()));
        this.dataManager.set(RED, r);
        this.dataManager.set(GREEN, g);
        this.dataManager.set(BLUE, b);
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


        Vec3d vec3d2 = this.getMotion();
        BlockPos start = dataManager.get(from);
        BlockPos end = dataManager.get(to);
        if(BlockUtil.distanceFrom(this.getPosition(), end.up()) < 1 || this.age > 1000 || BlockUtil.distanceFrom(this.getPosition(), end) > 14){
            this.remove();
            return;
        }
        double posX = getPosX();
        double posY = getPosY();
        double posZ = getPosZ();



        double time = 1 - normalize((double)age, 0.0, 80);

        double yOffset = -3.0;
        EasingType type = EasingType.NONE;

        double startY = start.getY();
        double endY = end.getY() + BlockUtil.distanceFrom(start, end)*2;
        double lerpX = lerp(time, (double)start.getX() +0.5, (double)end.getX()+0.5, type);
        double lerpY = lerp(time, lerp(time, startY, endY, type), lerp(time,endY, startY, type), type);
        double lerpZ = lerp(time,(double)start.getZ()+0.5, (double)end.getZ()+0.5, type);

        BlockPos adjustedPos = new BlockPos(posX, end.getY(), posZ);
        if(BlockUtil.distanceFrom(adjustedPos, end) <= 1.5){
            posY  = getPosY() - 0.05;
          //  this.setPosition(lerpX, posY - 0.05, lerpZ);
            this.setPosition(lerpX, posY, lerpZ);
        }else{
            this.setPosition(lerpX, lerpY, lerpZ);
        }

        if(world.isRemote && this.age > 1) {
            double deltaX = getPosX() - lastTickPosX;
            double deltaY = getPosY() - lastTickPosY;
            double deltaZ = getPosZ() - lastTickPosZ;
            double dist = Math.ceil(Math.sqrt(deltaX*deltaX+deltaY*deltaY+deltaZ*deltaZ) * 20);
            int counter = 0;

            for (double i = 0; i < dist; i ++){
                double coeff = i/dist;
                counter += world.rand.nextInt(3);
                if (counter % (Minecraft.getInstance().gameSettings.particles.getId() == 0 ? 1 : 2 * Minecraft.getInstance().gameSettings.particles.getId()) == 0) {
                    world.addParticle(GlowParticleData.createData(
                            new ParticleColor(this.dataManager.get(RED),this.dataManager.get(GREEN),this.dataManager.get(BLUE))),
                            (float) (prevPosX + deltaX * coeff), (float) (prevPosY + deltaY * coeff), (float) (prevPosZ + deltaZ * coeff), 0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f));
                }
            }

        }
    }

    public Vec3d getLerped(){
        BlockPos start = dataManager.get(from);
        BlockPos end = dataManager.get(to);
        double startY = start.getY();
        double endY = end.getY() +4.0;
        double time = 1 - normalize((double)age, 0.0, 100);

        double yOffset = -3.0;
        EasingType type = EasingType.NONE;
        double lerpX = lerp(time, (double)start.getX() +0.5, (double)end.getX()+0.5, type);
        double lerpY = lerp(time, lerp(time, startY, endY, type), lerp(time,endY, startY, type), type);
        double lerpZ = lerp(time,(double)start.getZ()+0.5, (double)end.getZ()+0.5, type);
        return new Vec3d(lerpX, lerpY, lerpZ);
    }
    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        if(compound.contains("item")){
            this.dataManager.set(HELD_ITEM, ItemStack.read(compound.getCompound("item")));
        }
        this.age = compound.getInt("age");
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        if(getStack() != null){
            CompoundNBT tag = new CompoundNBT();
            getStack().write(tag);
            compound.put("item", tag);
        }
        compound.putInt("age", age);
    }

    public ItemStack getStack(){
        return this.dataManager.get(HELD_ITEM);
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(HELD_ITEM, ItemStack.EMPTY);
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.ENTITY_FLYING_ITEM;
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public EntityFlyingItem(FMLPlayMessages.SpawnEntity packet, World world){
        super(ModEntities.ENTITY_FLYING_ITEM, world);
    }
}
