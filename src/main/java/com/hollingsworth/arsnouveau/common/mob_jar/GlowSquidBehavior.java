package com.hollingsworth.arsnouveau.common.mob_jar;

import com.hollingsworth.arsnouveau.api.mob_jar.JarBehavior;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import net.minecraft.world.entity.GlowSquid;
import net.minecraft.world.phys.Vec3;

public class GlowSquidBehavior extends JarBehavior<GlowSquid> {


    @Override
    public Vec3 scaleOffset(MobJarTile pBlockEntity) {
        return SquidBehavior.SCALE;
    }

    @Override
    public Vec3 translate(MobJarTile pBlockEntity) {
        return SquidBehavior.TRANSLATE;
    }

    @Override
    public int lightLevel(MobJarTile pBlockEntity) {
        return 11;
    }
}
