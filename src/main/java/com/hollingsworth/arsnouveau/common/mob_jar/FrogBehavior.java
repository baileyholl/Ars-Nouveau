package com.hollingsworth.arsnouveau.common.mob_jar;

import com.hollingsworth.arsnouveau.api.mob_jar.JarBehavior;
import com.hollingsworth.arsnouveau.api.nbt.AbstractData;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class FrogBehavior extends JarBehavior<Frog> {

    @Override
    public void tick(MobJarTile tile) {
        if (tile.getLevel().isClientSide || isPowered(tile)) {
            return;
        }
        ExtraData data = new ExtraData(tile.getExtraDataTag());
        if (data.isEating) {
            if (data.tickCounter >= 8) {
                Entity target = tile.getLevel().getEntity(data.entityId);
                if (target instanceof LivingEntity livingEntity) {
                    Frog frog = entityFromJar(tile);
                    livingEntity.playSound(SoundEvents.FROG_EAT, 2.0F, 1.0f);
                    if (livingEntity.isAlive()) {
                        frog.doHurtTarget(livingEntity);
                        if (!livingEntity.isAlive()) {
                            livingEntity.remove(Entity.RemovalReason.KILLED);
                        }
                    }
                }
                data.isEating = false;
            }
            data.tickCounter++;
            writeData(tile, data);
            return;
        }
        if (tile.getLevel().getRandom().nextInt(60) == 0) {
            Frog frog = entityFromJar(tile);
            List<LivingEntity> livingEntities = frog.level.getEntitiesOfClass(LivingEntity.class, new AABB(tile.getBlockPos()).inflate(2), Frog::canEat);
            if (livingEntities.isEmpty())
                return;
            LivingEntity entity = livingEntities.get(tile.getLevel().getRandom().nextInt(livingEntities.size()));
            entity.level.playSound(null, entity, SoundEvents.FROG_TONGUE, SoundSource.NEUTRAL, 2.0F, 1.0F);
            entity.setDeltaMovement(entity.position().vectorTo(frog.position()).normalize().scale(0.75D));
            entity.hasImpulse = true;
            data.position = entity.position();
            data.tickCounter = 0;
            data.isEating = true;
            data.entityId = entity.getId();
            writeData(tile, data);
        }
    }

    public void writeData(MobJarTile tile, ExtraData data) {
        CompoundTag tag = new CompoundTag();
        data.writeToNBT(tag);
        tile.setExtraDataTag(tag);
    }

    public static class ExtraData extends AbstractData {
        public Vec3 position;
        public int tickCounter;
        public boolean isEating;
        public int entityId;

        public ExtraData(CompoundTag tag) {
            super(tag);
            position = NBTUtil.getVec(tag, "position");
            tickCounter = tag.getInt("tickCounter");
            isEating = tag.getBoolean("isEating");
            entityId = tag.getInt("entityId");
        }

        @Override
        public void writeToNBT(CompoundTag tag) {
            NBTUtil.storeVec(tag, "position", position);
            tag.putInt("tickCounter", tickCounter);
            tag.putBoolean("isEating", isEating);
            tag.putInt("entityId", entityId);

        }
    }
}
