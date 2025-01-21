package com.hollingsworth.arsnouveau.api.particle;

import net.minecraft.world.level.Level;

public interface IParticleCallback {


    void tick(Level level, double x, double y, double z, double prevX, double prevY, double prevZ);
}
