package com.hollingsworth.arsnouveau.common.light;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.level.Level;

/**
 * Represents a dynamic light source.
 * This is a straight implementation from <a href="https://github.com/LambdAurora/LambDynamicLights">LambDynamicLights</a>, a super awesome Fabric mod!
 *
 * @author LambdAurora
 * @version 1.3.3
 * @since 1.0.0
 */
public interface LambDynamicLight {
    /**
     * Returns the dynamic light source X coordinate.
     *
     * @return the X coordinate
     */
    double ars_nouveau$getDynamicLightX();

    /**
     * Returns the dynamic light source Y coordinate.
     *
     * @return the Y coordinate
     */
    double ars_nouveau$getDynamicLightY();

    /**
     * Returns the dynamic light source Z coordinate.
     *
     * @return the Z coordinate
     */
    double ars_nouveau$getDynamicLightZ();

    /**
     * Returns the dynamic light source world.
     *
     * @return the world instance
     */
    Level ars_nouveau$getDynamicLightWorld();

    /**
     * Returns whether the dynamic light is enabled or not.
     *
     * @return {@code true} if the dynamic light is enabled, else {@code false}
     */
    default boolean ars_nouveau$isDynamicLightEnabled() {
        return LightManager.containsLightSource(this);
    }

    void ars_nouveau$resetDynamicLight();

    default void ars_nouveau$setDynamicLightEnabled(boolean enabled) {
        this.ars_nouveau$resetDynamicLight();
        if (enabled)
            LightManager.addLightSource(this);
        else
            LightManager.removeLightSource(this);
    }

    /**
     * Returns the luminance of the light source.
     * The maximum is 15, below 1 values are ignored.
     *
     * @return the luminance of the light source
     */
    int ars_nouveau$getLuminance();

    /**
     * Executed at each tick.
     */
    void ars_nouveau$dynamicLightTick();

    /**
     * Returns whether this dynamic light source should update.
     *
     * @return {@code true} if this dynamic light source should update, else {@code false}
     */
    boolean ars_nouveau$shouldUpdateDynamicLight();

    boolean ars_nouveau$updateDynamicLight(LevelRenderer renderer);

    void ars_nouveau$scheduleTrackedChunksRebuild(LevelRenderer renderer);
}
