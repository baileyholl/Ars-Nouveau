package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleSparkleData;
import com.hollingsworth.arsnouveau.common.block.tile.RitualBrazierTile;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class EntityRitualProjectile extends ColoredProjectile {

    public BlockPos tilePos;

    public EntityRitualProjectile(Level worldIn, double x, double y, double z) {
        super(ModEntities.ENTITY_RITUAL.get(), worldIn, x, y, z);
    }

    public EntityRitualProjectile(Level worldIn, BlockPos pos) {
        super(ModEntities.ENTITY_RITUAL.get(), worldIn, pos.getX(), pos.getY(), pos.getZ());
    }

    public EntityRitualProjectile(EntityType<EntityRitualProjectile> entityAOEProjectileEntityType, Level world) {
        super(entityAOEProjectileEntityType, world);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level.isClientSide() && (tilePos == null || !(level.getBlockEntity(tilePos) instanceof RitualBrazierTile tile) || tile.ritual == null)) {
            this.remove(Entity.RemovalReason.DISCARDED);
            return;
        }

        xOld = getX();
        yOld = getY();
        zOld = getZ();

        this.setPos(getX(), getY() + Math.sin(level.getGameTime() / 10D) / 10, getZ());
        xo = getX();
        yo = getY();
        zo = getZ();

        if (level.isClientSide()) {
            // particles().get().getId() removed in 1.21.11 - use ordinal() instead
            int particleId = Minecraft.getInstance().options.particles().get().ordinal();
            int counter = 0;
            for (double j = 0; j < 3; j++) {

                counter += level.random.nextInt(3);
                if (counter % (particleId == 0 ? 1 : 2 * particleId) == 0) {
                    level.addParticle(ParticleSparkleData.createData(getParticleColor()),
                            (float) (position().x()) + Math.sin(level.getGameTime() / 3D),
                            (float) (position().y()),
                            (float) (position().z()) + Math.cos(level.getGameTime() / 3D),
                            0.0225f * (random.nextFloat()), 0.0225f * (random.nextFloat()), 0.0225f * (random.nextFloat()));
                }
            }

            for (double j = 0; j < 3; j++) {

                counter += level.random.nextInt(3);
                if (counter % (particleId == 0 ? 1 : 2 * particleId) == 0) {
                    level.addParticle(ParticleSparkleData.createData(new ParticleColor(2, 0, 144)),
                            (float) (position().x()) - Math.sin(level.getGameTime() / 3D),
                            (float) (position().y()),
                            (float) (position().z()) - Math.cos(level.getGameTime() / 3D),
                            0.0225f * (random.nextFloat()), 0.0225f * (random.nextFloat()), 0.0225f * (random.nextFloat()));
                }
            }
        }
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.ENTITY_RITUAL.get();
    }

    @Override
    public void addAdditionalSaveData(ValueOutput tag) {
        super.addAdditionalSaveData(tag);
        if (tilePos != null) {
            tag.putInt("ritpos_x", tilePos.getX());
            tag.putInt("ritpos_y", tilePos.getY());
            tag.putInt("ritpos_z", tilePos.getZ());
        }
    }

    @Override
    public void readAdditionalSaveData(ValueInput tag) {
        super.readAdditionalSaveData(tag);
        if (tag.keySet().contains("ritpos_x")) {
            tilePos = new BlockPos(tag.getIntOr("ritpos_x", 0), tag.getIntOr("ritpos_y", 0), tag.getIntOr("ritpos_z", 0));
        }
    }
}
