package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.common.lib.EntityTags;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

public class Cinder extends Projectile {

    public Cinder(EntityType<? extends Cinder> type, Level worldIn) {
        super(type, worldIn);
    }

    public Cinder(EntityType<? extends Cinder> type, Level worldIn, double x, double y, double z) {
        this(type, worldIn);
        setPos(x, y, z);
    }

    public Cinder(EntityType<? extends Cinder> type, Level worldIn, LivingEntity shooter) {
        this(type, worldIn);
        setOwner(shooter);
    }


    public Cinder(Level worldIn, double x, double y, double z) {
        this(ModEntities.CINDER.get(), worldIn);
        setPos(x, y, z);
    }
    @Override
    public void tick() {
        super.tick();
        if(level.isClientSide){
            level.addParticle(ParticleTypes.FLAME, getX(), getY(), getZ(), 0, 0, 0);
        }
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        return super.canHitEntity(entity) || entity.getType().is(EntityTags.SPELL_CAN_HIT);
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.CINDER.get();
    }

    public Cinder(PlayMessages.SpawnEntity packet, Level world) {
        super(ModEntities.CINDER.get(), world);
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

}
