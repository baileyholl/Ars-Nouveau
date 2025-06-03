package com.hollingsworth.arsnouveau.api.config;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

import javax.annotation.Nullable;

/**
 * Interface for game objects that can have their own configuration.
 * Implement this on blocks, items, or rituals to enable individual configuration.
 */
public interface IConfigurable {

    /**
     * Called during mod initialization to build the configuration for this object.
     * @param builder The configuration builder to add configuration options to
     */
    void buildConfig(ModConfigSpec.Builder builder);

    /**
     * Gets the registry name of this object.
     * This is used to create a unique configuration category.
     * @return The registry name of the object
     */
    ResourceLocation getRegistryName();

    /**
     * Gets the subfolder path for this configurable's configuration file.
     * This will be inserted between the namespace and the filename.
     * @return The subfolder path, or null for no subfolder
     */
    @Nullable
    default String getSubFolder() {
        return null;
    }

    /**
     * Gets the configuration file path for this object.
     * @return A path in the format "namespace[/subfolder]/path.toml"
     */
    default String getConfigPath() {
        String namespace = getRegistryName().getNamespace();
        String path = getRegistryName().getPath();
        String subfolder = getSubFolder();

        if (subfolder == null || subfolder.isEmpty()) {
            return String.format("%s/%s.toml", namespace, path);
        }
        return String.format("%s/%s/%s.toml", namespace, subfolder, path);
    }

    /**
     * Gets the ModConfigSpec for this configurable object.
     *
     * @return The ModConfigSpec instance, or null if not built yet
     */
    @Nullable ModConfigSpec getConfigSpec();

    /**
     * Sets the ModConfigSpec for this configurable object.
     * @param config The ModConfigSpec instance to set
     */
    void setConfigSpec(@Nullable ModConfigSpec config);

    static void register(IConfigurable configurable) {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        configurable.buildConfig(builder);
        ModConfigSpec spec = builder.build();
        if (spec.isEmpty()) return;
        configurable.setConfigSpec(spec);
        ModLoadingContext.get().getActiveContainer().registerConfig(ModConfig.Type.SERVER, spec, configurable.getConfigPath());
    }
}
