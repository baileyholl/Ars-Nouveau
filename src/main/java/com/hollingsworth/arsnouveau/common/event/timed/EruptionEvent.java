package com.hollingsworth.arsnouveau.common.event.timed;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.event.ITimedEvent;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public class EruptionEvent implements ITimedEvent {

    int delay;
    BlockPos origin;
    Level world;
    int start;
    int particleDelay;

    public EruptionEvent(Level world, BlockPos origin, int delay, int particleDelay) {
        this.world = world;
        this.origin = origin;
        this.delay = delay;
        start = delay;
        this.particleDelay = particleDelay;
    }

    @Override
    public void tick(boolean serverSide) {
        delay--;
        if (!serverSide && start - delay >= particleDelay) {
            if (world.random.nextInt(5) == 0) {
//                for(int i = 0 ; i < 10; i ++) {
//                    (world).addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, origin.getX() + 0.5, origin.getY() +  ParticleUtil.inRange(-0.05, 0.05), origin.getZ() + 0.5,
//                            ParticleUtil.inRange(-0.05, 0.05), 0.2, ParticleUtil.inRange(-0.05, 0.05));
//                }
                int intensity = 50;
                ParticleColor centerColor = new ParticleColor(255, 50, 50);
                double xzOffset = 0.25;
                BlockPos pos = origin;
                for (int i = 0; i < intensity; i++) {
                    world.addParticle(
                            GlowParticleData.createData(centerColor),
                            pos.getX() + 0.5 + ParticleUtil.inRange(-xzOffset / 2, xzOffset / 2), pos.getY() + 1 + ParticleUtil.inRange(-0.05, 0.2), pos.getZ() + 0.5 + ParticleUtil.inRange(-xzOffset / 2, xzOffset / 2),
                            0, ParticleUtil.inRange(0.0, 0.05f), 0);
                }
                for (int i = 0; i < intensity; i++) {
                    world.addParticle(
                            GlowParticleData.createData(centerColor),
                            pos.getX() + 0.5 + ParticleUtil.inRange(-xzOffset, xzOffset), pos.getY() + 1 + ParticleUtil.inRange(0, 0.7), pos.getZ() + 0.5 + ParticleUtil.inRange(-xzOffset, xzOffset),
                            0, ParticleUtil.inRange(0.0, 0.05f), 0);
                }
            }

        }
        if (serverSide && delay <= 0) {
            world.explode(null, origin.getX(), origin.getY(), origin.getZ(), 5.0f, Level.ExplosionInteraction.NONE);
        }

    }

    @Override
    public boolean isExpired() {
        return delay <= 0;
    }

    @Override
    public CompoundTag serialize(CompoundTag tag) {
        ITimedEvent.super.serialize(tag);
        NBTUtil.storeBlockPos(tag, "pos", origin);
        tag.putInt("delay", delay);
        tag.putInt("particleDelay", particleDelay);
        return tag;
    }

    public static EruptionEvent get(CompoundTag tag) {
        return new EruptionEvent(ArsNouveau.proxy.getClientWorld(), NBTUtil.getBlockPos(tag, "pos"), tag.getInt("delay"), tag.getInt("particleDelay"));
    }

    public static final String ID = "eruption";

    @Override
    public String getID() {
        return ID;
    }
}
