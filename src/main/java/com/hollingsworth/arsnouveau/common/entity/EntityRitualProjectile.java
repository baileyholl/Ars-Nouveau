package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleSparkleData;
import com.hollingsworth.arsnouveau.common.block.tile.RitualBrazierTile;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

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
            this.remove(RemovalReason.DISCARDED);
            return;
        }

        xOld = getX();
        yOld = getY();
        zOld = getZ();

        this.setPos(getX(), getY() + Math.sin(level.getGameTime() / 10D) / 10, getZ());
        xo = getX();
        yo = getY();
        zo = getZ();

        if (level.isClientSide) {
            int counter = 0;
            for (double j = 0; j < 3; j++) {

                counter += level.random.nextInt(3);
                if (counter % (Minecraft.getInstance().options.particles().get().getId() == 0 ? 1 : 2 * Minecraft.getInstance().options.particles().get().getId()) == 0) {
                    level.addParticle(ParticleSparkleData.createData(getParticleColor()),
                            (float) (position().x()) + Math.sin(level.getGameTime() / 3D),
                            (float) (position().y()),
                            (float) (position().z()) + Math.cos(level.getGameTime() / 3D),
                            0.0225f * (random.nextFloat()), 0.0225f * (random.nextFloat()), 0.0225f * (random.nextFloat()));
                }
            }

            for (double j = 0; j < 3; j++) {

                counter += level.random.nextInt(3);
                if (counter % (Minecraft.getInstance().options.particles().get().getId() == 0 ? 1 : 2 * Minecraft.getInstance().options.particles().get().getId()) == 0) {
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
    public boolean save(CompoundTag tag) {
        if (tilePos != null)
            tag.put("ritpos", NbtUtils.writeBlockPos(tilePos));
        return super.save(tag);
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        if (compound.contains("ritpos")) {
            tilePos = NbtUtils.readBlockPos(compound, "ritpos").orElse(null);
        }
    }
}
