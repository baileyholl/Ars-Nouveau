package com.hollingsworth.arsnouveau.common.mob_jar;

import com.hollingsworth.arsnouveau.api.mob_jar.JarBehavior;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.phys.Vec3;

public class GhastBehavior extends JarBehavior<Ghast> {

    @Override
    public Vec3 scaleOffset(MobJarTile pBlockEntity) {
        return new Vec3(0.7,0.7,0.7);
    }

    @Override
    public Vec3 translate(MobJarTile pBlockEntity) {
        return new Vec3(0, 0.3, 0);
    }
}
