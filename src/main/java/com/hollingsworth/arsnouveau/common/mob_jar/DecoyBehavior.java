package com.hollingsworth.arsnouveau.common.mob_jar;

import com.hollingsworth.arsnouveau.api.mob_jar.JarBehavior;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import com.hollingsworth.arsnouveau.common.entity.EntityDummy;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class DecoyBehavior extends JarBehavior<EntityDummy> {
    @Override
    public void tick(MobJarTile tile) {
        super.tick(tile);

        if (!isPowered(tile)) return;

        Level level = tile.getLevel();
        if (level == null) return;
        if (level.isClientSide) return;

        BlockPos pos = tile.getBlockPos();
        EntityDummy dummy = entityFromJar(tile);
        for (Mob entity : level.getEntitiesOfClass(Mob.class, new AABB(pos).inflate(10))) {
            Vec3 vec3d = new Vec3(pos.getX() - entity.getX(), pos.getY() - entity.getY(), pos.getZ() - entity.getZ());
            if (vec3d.length() < 1) continue;
            entity.setDeltaMovement(entity.getDeltaMovement().add(vec3d.normalize()).scale(0.2F));
            entity.hurtMarked = true;
        }
    }
}
