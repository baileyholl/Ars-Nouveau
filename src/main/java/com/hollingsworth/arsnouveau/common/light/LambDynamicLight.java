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
    double getDynamicLightX();

    /**
     * Returns the dynamic light source Y coordinate.
     *
     * @return the Y coordinate
     */
    double getDynamicLightY();

    /**
     * Returns the dynamic light source Z coordinate.
     *
     * @return the Z coordinate
     */
    double getDynamicLightZ();

    /**
     * Returns the dynamic light source world.
     *
     * @return the world instance
     */
    Level getDynamicLightWorld();

    /**
     * Returns whether the dynamic light is enabled or not.
     *
     * @return {@code true} if the dynamic light is enabled, else {@code false}
     */
    default boolean isDynamicLightEnabled() {
        return LightManager.containsLightSource(this);
    }

    void resetDynamicLight();

    default void setDynamicLightEnabled(boolean enabled) {
        this.resetDynamicLight();
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
    int getLuminance();

    /**
     * Executed at each tick.
     */
    void dynamicLightTick();

    /**
     * Returns whether this dynamic light source should update.
     *
     * @return {@code true} if this dynamic light source should update, else {@code false}
     */
    boolean ars_nouveau$shouldUpdateDynamicLight();

    boolean ars_nouveau$updateDynamicLight(LevelRenderer renderer);

    void ars_nouveau$scheduleTrackedChunksRebuild(LevelRenderer renderer);
}
