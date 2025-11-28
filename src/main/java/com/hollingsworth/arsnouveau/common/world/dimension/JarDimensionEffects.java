package com.hollingsworth.arsnouveau.common.world.dimension;

import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.world.phys.Vec3;

public class JarDimensionEffects extends DimensionSpecialEffects {

    public JarDimensionEffects() {
        this(Float.NaN, false, SkyType.NONE, true, false);
    }

    public JarDimensionEffects(float cloudLevel, boolean hasGround, SkyType skyType, boolean forceBrightLightmap, boolean constantAmbientLight) {
        super(cloudLevel, hasGround, skyType, forceBrightLightmap, constantAmbientLight);
    }

    @Override
    public Vec3 getBrightnessDependentFogColor(Vec3 vec3, float v) {
        return vec3.scale((double) 0.15F);
    }

    @Override
    public boolean isFoggyAt(int i, int i1) {
        return false;
    }
}
