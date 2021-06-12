package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class EntityWardProjectile extends EntityProjectileSpell{
    public Entity wardedEntity;
    int ticksLeft;
    private static final DataParameter<Optional<UUID>> OWNER_UUID = EntityDataManager.defineId(EntityWardProjectile.class, DataSerializers.OPTIONAL_UUID);
    public static final DataParameter<Integer> OFFSET = EntityDataManager.defineId(EntityWardProjectile.class, DataSerializers.INT);


    public EntityWardProjectile(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public EntityWardProjectile(World worldIn, LivingEntity shooter) {
        super(worldIn, shooter);
    }

    public EntityWardProjectile(EntityType<EntityWardProjectile> entityWardProjectileEntityType, World world) {
        super(entityWardProjectileEntityType, world);
    }


    public void setOffset(int offset){
        entityData.set(OFFSET, offset);
    }

    public int getOffset(){
        return entityData.get(OFFSET) * 15;
    }
    @Override
    public void tick() {
        Entity owner = level.getPlayerByUUID(getOwnerID());
//        this.remove();
        if(!level.isClientSide && owner == null) {
            this.remove();
            return;
        }

        if(owner == null)
            return;

        this.setPos(owner.getX(), owner.getY(), owner.getZ());

        if(level.isClientSide) {

            level.addParticle(GlowParticleData.createData(ParticleUtil.defaultParticleColor()),
                    (float) (getX()) - Math.sin((ClientInfo.ticksInGame + getOffset()) / 8D) ,
                    (float) (getY()) + 1  ,
                    (float) (getZ()) - Math.cos((ClientInfo.ticksInGame + getOffset()) / 8D) ,
                    0, 0, 0);
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(OWNER_UUID, Optional.of(Util.NIL_UUID));
        this.entityData.define(OFFSET, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("left", ticksLeft);
        tag.putInt("offset", getOffset());
        writeOwner(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT tag) {
        super.readAdditionalSaveData(tag);
        this.ticksLeft = tag.getInt("left");
        setOffset(tag.getInt("offset"));
        if(getOwnerID() != null)
            setOwnerID(tag.getUUID("owner"));
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.ENTITY_WARD;
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public EntityWardProjectile(FMLPlayMessages.SpawnEntity packet, World world) {
        super(ModEntities.ENTITY_WARD, world);
    }

    void writeOwner(CompoundNBT tag){
        if(getOwnerID() != null)
            tag.putUUID("owner", getOwnerID());
    }
    public int getTicksLeft() {
        return ticksLeft;
    }

    public void setTicksLeft(int ticks) {
        this.ticksLeft = ticks;
    }

    @Nullable
    public UUID getOwnerID() {
        return !this.getEntityData().get(OWNER_UUID).isPresent() ? this.getUUID() : this.getEntityData().get(OWNER_UUID).get();
    }


    public void setOwnerID(UUID uuid) {
        this.getEntityData().set(OWNER_UUID, Optional.ofNullable(uuid));
    }
}
