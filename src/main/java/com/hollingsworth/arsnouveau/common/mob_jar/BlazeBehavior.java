package com.hollingsworth.arsnouveau.common.mob_jar;

import com.hollingsworth.arsnouveau.api.mob_jar.JarBehavior;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import net.minecraft.world.entity.monster.Blaze;

public class BlazeBehavior extends JarBehavior<Blaze> {

    @Override
    public int lightLevel(MobJarTile pBlockEntity) {
        return 15;
    }
}
