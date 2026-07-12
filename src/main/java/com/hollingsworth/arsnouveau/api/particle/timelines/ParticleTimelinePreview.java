package com.hollingsworth.arsnouveau.api.particle.timelines;

import net.minecraft.world.level.Level;

public interface ParticleTimelinePreview {

    boolean tick(Level level);

    default Scene scene() {
        return new Scene(15f, 3, 2);
    }

    record Scene(float scale, int grassRadiusX, int grassRadiusZ) {
    }
}
